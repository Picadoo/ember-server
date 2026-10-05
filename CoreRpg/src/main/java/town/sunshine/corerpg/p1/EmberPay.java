package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * D172: the one durable payment path for P1 costs paid BEFORE a DB transaction (forge D162, dismantle undo, mark
 * redemption, affix reroll). Order, per request id:
 * <ol>
 *   <li>refund hold rows ({@code cr_p1_delivery status=hold, request refund:<rid>}) — written first, all or none;</li>
 *   <li>the price taken (materials from backpack / warehouse, coins, marks) + {@code p1paid_<rid>} + the caller's hook
 *   in ONE PlayerData save (player row + warehouse = one MySQL transaction), .dat saved right after;</li>
 *   <li>{@code go}: the caller's transaction, which voids the hold in its own COMMIT.</li>
 * </ol>
 * Pay fails → nothing taken, holds void. Transaction fails → {@link #release} → refund once (EmberDelivery). Crash
 * between 2 and 3 → reconcile at join: committed → void, else refund (only when {@code p1paid_} reached the save).
 * Without MySQL the old in-memory path is kept (take, then go).
 */
public final class EmberPay {

    /** extra PlayerData changes that must reach the same save as the deduction (e.g. "this reroll is owed") */
    public interface Hook { void apply(PlayerData d); void revert(PlayerData d); }

    public static final class Price {
        public final Cost cost;
        public final int markTier, marks;
        /** D208: boss insignia of one map (EmberSignature.C_MARK + map) — the imprint price, refundable like forge marks */
        public final String sigMap;
        public final int sigMarks;
        /** D218 (ARCH S2-4): the EmberEconomy sink this price is spent on (C03–C07, C11, C12); null = not a sink (undo) */
        public final String sink;
        public Price(Cost cost, int markTier, int marks) { this(cost, markTier, marks, null, 0); }
        public Price(Cost cost, int markTier, int marks, String sigMap, int sigMarks) { this(cost, markTier, marks, sigMap, sigMarks, null); }
        private Price(Cost cost, int markTier, int marks, String sigMap, int sigMarks, String sink) {
            this.cost = cost == null ? Cost.NONE : cost; this.markTier = markTier; this.marks = Math.max(0, marks);
            this.sigMarks = sigMap == null ? 0 : Math.max(0, sigMarks); this.sigMap = this.sigMarks > 0 ? sigMap : null;
            this.sink = sink;
        }
        /** the same price tagged with a REG sink id (amounts unchanged) */
        public Price at(String sinkId) { return new Price(cost, markTier, marks, sigMap, sigMarks, sinkId); }
        public static Price of(Cost c) { return new Price(c, 0, 0); }
        public static Price marks(int tier, int n) { return new Price(Cost.NONE, tier, n); }
        /** D208: coins + boss insignia (烬炉烙印) */
        public static Price insignia(int coins, String map, int n) { return new Price(new Cost(0, 0, 0, 0, coins), 0, 0, map, n); }
        public Price plus(Price o) {
            Cost a = cost, b = o.cost;
            int tier = marks > 0 ? markTier : o.markTier;
            if (marks > 0 && o.marks > 0 && markTier != o.markTier) throw new IllegalArgumentException("mixed mark tiers");
            if (sigMarks > 0 && o.sigMarks > 0 && !sigMap.equals(o.sigMap)) throw new IllegalArgumentException("mixed insignia maps");
            String sk = sink == null ? o.sink : (o.sink == null || sink.equals(o.sink) ? sink : null); // mixed sinks → untagged
            return new Price(new Cost(a.shards + b.shards, a.cores + b.cores, a.blanks + b.blanks, a.bone + b.bone, a.coins + b.coins), tier, marks + o.marks,
                    sigMarks > 0 ? sigMap : o.sigMap, sigMarks + o.sigMarks, sk);
        }
        /**
         * D218: null when every part of this price is taken by its sink row (or the price is untagged), else why not —
         * checked before anything is taken so a registry mismatch never half-charges.
         */
        public String sinkRefusal() {
            if (sink == null) return null;
            for (Map.Entry<String, Integer> m : cost.materials().entrySet())
                if (m.getValue() > 0 && !EmberEconomy.spendMat(sink, m.getKey(), m.getValue())) return sink + " does not take " + m.getKey();
            if (cost.coins > 0 && !EmberEconomy.takes(sink, EmberEconomy.Account.COIN)) return sink + " does not take coin";
            if (marks > 0 && !EmberEconomy.takes(sink, EmberEconomy.Account.MARK)) return sink + " does not take marks";
            if (sigMarks > 0 && !EmberEconomy.takes(sink, EmberEconomy.Account.INSIGNIA)) return sink + " does not take insignia";
            return null;
        }
        public List<EmberItemStore.Owed> owed(String note) { return EmberPayRules.owed(cost.materials(), cost.coins, markTier, marks, sigMap, sigMarks, note); }
        public boolean free() { return owed("").isEmpty(); }
        public String json() {
            String c = cost.json();
            if (marks > 0) c = c.substring(0, c.length() - 1) + ",\"mark_t" + markTier + "\":" + marks + "}";
            if (sigMarks > 0) c = c.substring(0, c.length() - 1) + ",\"sigmark_" + sigMap + "\":" + sigMarks + "}";
            return c;
        }
        public String label() {
            String c = cost.label();
            if (marks > 0) c = ("免费".equals(c) ? "" : c + " ") + "T" + markTier + " 印记×" + marks;
            if (sigMarks > 0) c = ("免费".equals(c) ? "" : c + " ") + sigMap.toUpperCase(java.util.Locale.ROOT) + " 首领徽记×" + sigMarks;
            return c;
        }
    }

    private static volatile EmberPay instance;
    public static EmberPay get() { return instance; }

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;

    EmberPay(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
        instance = this;
    }

    private EmberItemStore store() { return loadouts.store(); }
    private NiBridge ni() { return plugin.getNiBridge(); }
    private PlayerData data(UUID id) { try { return plugin.getDataStore().get(id); } catch (RuntimeException e) { return null; } }
    public boolean durable() { return store().usable(); }

    public List<String> lacking(Player p, Price c) {
        List<String> out = new ArrayList<String>();
        for (Map.Entry<String, Integer> m : c.cost.materials().entrySet()) {
            int have = ni().countInInventory(p, m.getKey());
            if (have < m.getValue()) out.add(ni().displayName(m.getKey()) + " " + have + "/" + m.getValue());
        }
        PlayerData pd = data(p.getUniqueId());
        int coins = pd == null ? 0 : pd.getCoin();
        if (c.cost.coins > 0 && coins < c.cost.coins) out.add("余烬币 " + coins + "/" + c.cost.coins);
        if (c.marks > 0) {
            int have = pd == null ? 0 : pd.periodCount(EmberPayRules.MARK_COUNTER + c.markTier, "all");
            if (have < c.marks) out.add("T" + c.markTier + " 印记 " + have + "/" + c.marks);
        }
        if (c.sigMarks > 0) {
            int have = pd == null ? 0 : pd.periodCount(EmberSignature.C_MARK + c.sigMap, "all");
            if (have < c.sigMarks) out.add(c.sigMap.toUpperCase(java.util.Locale.ROOT) + " 首领徽记 " + have + "/" + c.sigMarks);
        }
        return out;
    }

    /** checks everything, then takes all of it; a partial failure gives back what was taken. null = taken. */
    String take(Player p, Price c) {
        List<String> lack = lacking(p, c);
        if (!lack.isEmpty()) return "不够（没有扣除，什么都没变）: " + String.join("，", lack);
        String refused = c.sinkRefusal();
        if (refused != null) { plugin.getLogger().warning("[P1 pay] registry refused " + refused); return "账目登记不符（" + c.sink + "），没有扣除"; }
        Map<String, Integer> taken = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> m : c.cost.materials().entrySet()) {
            int got = ni().consume(p, m.getKey(), m.getValue());
            taken.put(m.getKey(), got);
            if (got < m.getValue()) { giveBack(p, taken, 0, 0, 0); return "扣除材料失败，已退回"; }
        }
        PlayerData pd = data(p.getUniqueId());
        if (c.cost.coins > 0 && (pd == null || !spendCoin(pd, c))) { giveBack(p, taken, 0, 0, 0); return "扣除余烬币失败，已退回"; }
        if (c.marks > 0 && (pd == null || !spendMarks(pd, c))) { giveBack(p, taken, c.cost.coins, 0, 0); return "扣除印记失败，已退回"; }
        if (c.sigMarks > 0 && (pd == null || !spendInsignia(pd, c))) {
            giveBack(p, taken, c.cost.coins, 0, 0);
            if (c.marks > 0 && pd != null) pd.addPeriodCount(EmberPayRules.MARK_COUNTER + c.markTier, "all", c.marks);
            return "扣除首领徽记失败，已退回";
        }
        return null;
    }

    /** D218: tagged prices spend through EmberEconomy; an untagged price (dismantle undo) keeps the direct path. */
    static boolean spendCoin(PlayerData pd, Price c) {
        return c.sink != null ? EmberEconomy.spendCoin(pd, c.sink, c.cost.coins) : pd.takeCoin(c.cost.coins);
    }

    static boolean spendMarks(PlayerData pd, Price c) {
        if (c.sink != null) return EmberEconomy.spendMark(pd, c.sink, c.markTier, c.marks);
        String k = EmberPayRules.MARK_COUNTER + c.markTier;
        if (pd.periodCount(k, "all") < c.marks) return false;
        pd.addPeriodCount(k, "all", -c.marks);
        return true;
    }

    static boolean spendInsignia(PlayerData pd, Price c) {
        if (c.sink != null) return EmberEconomy.spendInsignia(pd, c.sink, c.sigMap, c.sigMarks);
        String k = EmberSignature.C_MARK + c.sigMap;
        if (pd.periodCount(k, "all") < c.sigMarks) return false;
        pd.addPeriodCount(k, "all", -c.sigMarks);
        return true;
    }

    void giveBack(Player p, Price c) {
        giveBack(p, c.cost.materials(), c.cost.coins, c.markTier, c.marks);
        PlayerData pd = data(p.getUniqueId());
        if (pd != null && c.sigMarks > 0) pd.addPeriodCount(EmberSignature.C_MARK + c.sigMap, "all", c.sigMarks);
    }

    private void giveBack(Player p, Map<String, Integer> mats, int coins, int markTier, int marks) {
        for (Map.Entry<String, Integer> m : mats.entrySet()) if (m.getValue() > 0) ni().giveNiItem(p, m.getKey(), m.getValue());
        PlayerData pd = data(p.getUniqueId());
        if (pd == null) return;
        if (coins > 0) pd.addCoin(coins);
        if (marks > 0) pd.addPeriodCount(EmberPayRules.MARK_COUNTER + markTier, "all", marks);
    }

    /** single request: see {@link #payAll} */
    public void pay(Player p, String rid, Price price, String refundNote, Hook hook, Runnable go, Consumer<String> fail) {
        LinkedHashMap<String, Price> m = new LinkedHashMap<String, Price>();
        m.put(rid, price);
        payAll(p, m, refundNote, hook, go, fail);
    }

    /**
     * Several requests paid at once (batch undo): holds for all in one DB transaction, the total taken in one save, then
     * {@code go} (which commits each request on its own; a request that does not commit is {@link #release}d).
     * {@code fail} gets the reason when nothing was taken (the caller decides how to tell the player).
     */
    public void payAll(final Player p, final LinkedHashMap<String, Price> parts, final String refundNote, final Hook hook,
                       final Runnable go, final Consumer<String> fail) {
        Price total = Price.of(Cost.NONE);
        for (Price x : parts.values()) total = total.plus(x);
        final Price all = total;
        List<String> lack = lacking(p, all);
        if (!lack.isEmpty()) { fail.accept("不够（没有扣除，什么都没变）: " + String.join("，", lack)); return; }
        String guard = EmberAssetGuard.hold(p);
        if (guard != null) { fail.accept(guard); return; }
        final UUID id = p.getUniqueId();
        final LinkedHashMap<String, List<EmberItemStore.Owed>> owed = new LinkedHashMap<String, List<EmberItemStore.Owed>>();
        for (Map.Entry<String, Price> e : parts.entrySet()) { List<EmberItemStore.Owed> l = e.getValue().owed(refundNote); if (!l.isEmpty()) owed.put(e.getKey(), l); }
        if (!store().usable() || owed.isEmpty()) { // YAML storage / free: the old in-memory path
            String err = take(p, all);
            if (err != null) { fail.accept(err); return; }
            PlayerData pd = data(id);
            if (hook != null && pd != null) hook.apply(pd);
            go.run();
            return;
        }
        store().insertHoldsAll(id, owed, ok -> {
            if (!p.isOnline()) { voidAll(id, owed); fail.accept("已离线，没有扣除"); return; }
            if (!ok) { fail.accept("这次请求正在处理或数据库暂时写不进去，没有扣除，稍后再试"); return; }
            String err = take(p, all);
            if (err != null) { voidAll(id, owed); fail.accept(err); return; }
            PlayerData pd = data(id);
            if (pd != null) for (Map.Entry<String, List<EmberItemStore.Owed>> e : owed.entrySet()) {
                String mk = EmberDelivery.paidMarker(e.getKey()); // one count per refund line, each refunded line takes one
                pd.addPeriodCount(mk, "1", e.getValue().size() - pd.periodCount(mk, "1"));
            }
            if (hook != null && pd != null) hook.apply(pd);
            boolean saved = pd != null && plugin.getDataStore().save(id, pd);
            EmberVault.savePlayerFile(p);
            if (!saved) { // the deduction is not durable: undo it in memory, drop the holds, do nothing
                if (pd != null) {
                    for (String rid : owed.keySet()) { String mk = EmberDelivery.paidMarker(rid); pd.addPeriodCount(mk, "1", -pd.periodCount(mk, "1")); }
                    if (hook != null) hook.revert(pd);
                }
                giveBack(p, all);
                voidAll(id, owed);
                fail.accept("存档写入失败，已退回，没有执行");
                return;
            }
            if (EmberFaults.fire(id, EmberFaults.AFTER_PAY)) {
                plugin.getLogger().warning("[P1 pay] TEST FAULT after_pay: " + owed.keySet() + " paid + saved, commit starts in 10 s");
                Bukkit.getScheduler().runTaskLater(plugin, go, 200L);
                return;
            }
            go.run();
        });
    }

    private void voidAll(UUID id, Map<String, ?> rids) { for (String rid : rids.keySet()) store().voidHolds(id, rid); }

    /** the request did not commit → its held refund becomes a pending delivery (refunded once, then the player is kicked) */
    public void release(final UUID id, String rid) {
        if (!store().usable()) return;
        store().releaseHolds(id, rid, () -> {
            Player q = Bukkit.getPlayer(id);
            EmberGearLib gl = EmberGearLib.get();
            if (q != null && gl != null) gl.delivery().kick(q);
        });
    }

    /** the request committed: its paid marker is no longer needed (the hold is void) */
    public void settled(UUID id, String rid) {
        PlayerData pd = data(id);
        if (pd == null) return;
        String mk = EmberDelivery.paidMarker(rid);
        if (pd.periodCount(mk, "1") > 0) pd.addPeriodCount(mk, "1", -pd.periodCount(mk, "1"));
    }

    /** YAML path (no MySQL): give the price back in memory */
    public void refundInMemory(Player p, Price c) { if (p != null && p.isOnline()) giveBack(p, c); }
}
