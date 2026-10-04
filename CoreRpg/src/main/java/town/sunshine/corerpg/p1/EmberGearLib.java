package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.QuestService;
import town.sunshine.corerpg.p1.EmberItemStore.LibRow;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberItemStore.TxnStatus;
import town.sunshine.corerpg.p1.EmberStorageRules.Entry;
import town.sunshine.corerpg.p1.EmberStorageRules.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1.62 装备库 (gear library) + the 仓库 page (P1 material warehouse GUI), deposit-all, soft undo of a dismantle and the
 * admin item history. A stored piece is its own cr_p1_item row in state {@code stored} (rev + 1 through the normal
 * item transaction, ledger kinds stash / unstash / glibdis / undo); it exists nowhere as an ItemStack, so it cannot be
 * used, duplicated or lost while stored. Lock / favourite flags live in cr_p1_gearlib. Withdrawing re-creates the
 * signed stack at the new rev. Needs MySQL (the guard keeps P1 from running without it).
 *
 * <p>Balance: storage only. Drop tables, yields and stats are untouched; gear moves in and out only in town (same gate
 * as the forge), materials may be put away anywhere and are spent straight from the warehouse.</p>
 */
public final class EmberGearLib implements Listener {

    private static final String P = ChatColor.DARK_AQUA + "[装备库] " + ChatColor.GRAY;
    private static final String PV = ChatColor.DARK_AQUA + "[仓库] " + ChatColor.GRAY;
    static final String C_MODE = "p5_glib_mode"; // period all: 1 = always stash new drops, 2 = off (absent = while nearly full)
    static final int PER_PAGE = 45;

    private static volatile EmberGearLib instance;
    public static EmberGearLib get() { return instance; }

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final Map<UUID, List<Entry>> cache = new ConcurrentHashMap<UUID, List<Entry>>();
    private final Map<UUID, Filter> filters = new HashMap<UUID, Filter>();
    private final Set<String> busy = new HashSet<String>();
    private final Map<String, Material> iconType = new HashMap<String, Material>();

    public EmberGearLib(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
        instance = this;
    }

    private EmberItemStore store() { return loadouts.store(); }
    private EmberVault vault() { return EmberVault.get(); }

    public boolean usable() {
        EmberMode m = EmberMode.get();
        return EmberMode.active() && store().usable() && (m == null || m.b("storage.gearlib.enabled", true));
    }

    private int cfg(String k, int def) { EmberMode m = EmberMode.get(); return m == null ? def : m.i(k, def); }
    public int minFree() { return cfg("storage.gearlib.auto_stash_below_free", 4); }
    public int undoMinutes() { return cfg("storage.gearlib.undo_minutes", 10); }

    private PlayerData data(UUID id) { try { return plugin.getDataStore().get(id); } catch (RuntimeException e) { return null; } }

    public int mode(Player p) {
        PlayerData d = data(p.getUniqueId());
        int v = d == null ? 0 : d.periodCount(C_MODE, "all");
        return v == EmberStorageRules.STASH_ALWAYS || v == EmberStorageRules.STASH_OFF ? v : EmberStorageRules.STASH_LOW;
    }

    private void setMode(Player p, int m) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) return;
        d.addPeriodCount(C_MODE, "all", m - d.periodCount(C_MODE, "all"));
        plugin.getDataStore().flushMutation(p.getUniqueId());
    }

    /** same rule as the forge: gear moves only outside P1 combat / instance worlds */
    String gate(Player p) {
        if (EmberMode.isP1World(p.getWorld())) return "请回城后操作（副本里不能存取装备）";
        QuestService q = plugin.getQuestService();
        if (q != null && q.isInstanceWorld(p.getWorld())) return "请回城后操作（副本里不能存取装备）";
        return null;
    }

    private Set<String> equipped(Player p) {
        Set<String> s = new HashSet<String>();
        EmberPlayerState st = loadouts.state(p.getUniqueId());
        if (st.mainhandUid != null) s.add(st.mainhandUid);
        if (st.charmUid != null) s.add(st.charmUid);
        EmberLoadout l = loadouts.get(p);
        if (l != null && l.blade != null) s.add(l.blade.uid);
        if (l != null && l.charm != null) s.add(l.charm.uid);
        return s;
    }

    private List<Entry> entries(UUID id) {
        List<Entry> l = cache.get(id);
        if (l == null) { l = Collections.synchronizedList(new ArrayList<Entry>()); cache.put(id, l); }
        return l;
    }

    private Entry find(UUID id, String uid) {
        for (Entry e : new ArrayList<Entry>(entries(id))) if (e.d.uid.equals(uid)) return e;
        return null;
    }

    private void replace(UUID id, String uid, Entry with) {
        List<Entry> l = entries(id);
        synchronized (l) {
            for (int i = 0; i < l.size(); i++) if (l.get(i).d.uid.equals(uid)) { if (with == null) l.remove(i); else l.set(i, with); return; }
            if (with != null) l.add(with);
        }
    }

    /** loads the owner's stored rows, then runs {@code then} on the main thread */
    public void reload(final Player p, final Runnable then) {
        final UUID id = p.getUniqueId();
        store().loadByState(id, "stored", 0L, rows -> {
            List<Entry> l = Collections.synchronizedList(new ArrayList<Entry>());
            for (LibRow r : rows) l.add(new Entry(r.data, r.locked, r.fav, r.storedAt));
            cache.put(id, l);
            for (LibRow r : rows) loadouts.rememberRow(r.data.uid, id, r.data.rev, "stored");
            if (then != null) then.run();
        });
    }

    public int count(UUID id) { List<Entry> l = cache.get(id); return l == null ? 0 : l.size(); }

    // ================================================================== auto-stash of new gear (EmberRunService.giveItem)

    /**
     * A freshly granted piece goes straight into the library (no ItemStack is created) when
     * {@link EmberStorageRules#autoStash} says so. Returns true when stored.
     */
    public boolean autoStash(Player p, EmberItemData d, int freeSlots, boolean upgradeCandidate, boolean hasActive) {
        if (!EmberStorageRules.autoStash(usable(), mode(p), freeSlots, minFree(), upgradeCandidate, hasActive)) return false;
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        store().upsertItem(d, id, "stored");
        store().logCreate(d, id, "stored", d.source + " → 装备库（自动）");
        store().saveLibFlags(d.uid, id, false, false, now);
        loadouts.rememberRow(d.uid, id, d.rev, "stored");
        if (cache.containsKey(id)) replace(id, d.uid, new Entry(d, false, false, now));
        PlayerData pd = data(id);
        if (pd != null && EmberCodex.register(pd, d) && d.tier > 0)
            p.sendMessage("§6[图录] §f登记 T" + d.tier + " " + EmberItemData.familyName(d.family) + EmberItemData.slotName(d.slot)
                    + " §7（" + EmberCodex.count(pd) + "/" + EmberCodex.ENTRIES.size() + "）");
        plugin.getLogger().info("[P1 gearlib] auto-stash " + p.getName() + " " + d.uid.substring(0, 8) + " " + d.shortLabel());
        return true;
    }

    // ================================================================== stash / withdraw

    /** One inventory slot → library (town only). {@code cb} gets the label when stored, null when not. */
    private void stashSlot(final Player p, final int index, final java.util.function.Consumer<String> cb) {
        ItemStack st = p.getInventory().getItem(index);
        if (st == null || !loadouts.items().hasData(st)) { cb.accept(null); return; }
        EmberItems.Read r = loadouts.items().read(st);
        if (r == null || !r.ok() || r.data == null) { cb.accept(null); return; }
        final EmberItemData d = r.data;
        if (loadouts.trust(p, d) != null || busy.contains(d.uid) || equipped(p).contains(d.uid)) { cb.accept(null); return; }
        final ItemStack original = st.clone();
        p.getInventory().setItem(index, null); // out of reach while the transaction runs
        busy.add(d.uid);
        final UUID id = p.getUniqueId();
        store().commitTxn("stash:" + d.uid + ":" + d.rev, "stash", id, Arrays.asList(new TxnItem(d, null, "stored", "active")),
                null, "存入装备库 " + d.shortLabel(), res -> {
                    busy.remove(d.uid);
                    Player q = Bukkit.getPlayer(id);
                    if (res.status == TxnStatus.OK) {
                        long now = System.currentTimeMillis();
                        loadouts.rememberRow(d.uid, id, d.rev + 1, "stored");
                        store().saveLibFlags(d.uid, id, false, false, now);
                        replace(id, d.uid, new Entry(d.withRev(d.rev + 1), false, false, now));
                        cb.accept(d.shortLabel());
                    } else {
                        if (q != null) giveBack(q, original);
                        else plugin.getLogger().warning("[P1 gearlib] stash failed and " + id + " left: " + d.uid + " (" + res.detail + ") — row unchanged, stack lost with logout");
                        cb.accept(null);
                    }
                });
    }

    private static void giveBack(Player p, ItemStack st) {
        Map<Integer, ItemStack> left = p.getInventory().addItem(st);
        for (ItemStack s : left.values()) p.getWorld().dropItem(p.getLocation(), s);
        p.updateInventory();
    }

    public void withdraw(final Player p, final String uid) {
        String g = gate(p);
        if (g != null) { p.sendMessage(P + ChatColor.RED + g); return; }
        final UUID id = p.getUniqueId();
        final Entry e = find(id, uid);
        if (e == null) { p.sendMessage(P + ChatColor.RED + "装备库里没有这件（可能刚取出）"); return; }
        if (busy.contains(uid)) { p.sendMessage(P + "这件正在处理中"); return; }
        if (p.getInventory().firstEmpty() < 0) { p.sendMessage(P + ChatColor.RED + "背包满了，空出一格再取"); return; }
        final EmberItemData after = e.d.withRev(e.d.rev + 1);
        final ItemStack fresh = loadouts.items().create(after);
        if (fresh == null) { p.sendMessage(P + ChatColor.RED + "物品生成失败（NI 模板或签名密钥不可用），没有取出"); return; }
        busy.add(uid);
        store().commitTxn("unstash:" + uid + ":" + e.d.rev, "unstash", id, Arrays.asList(new TxnItem(e.d, null, "active", "stored")),
                null, "从装备库取出 " + e.d.shortLabel(), res -> {
                    busy.remove(uid);
                    if (res.status != TxnStatus.OK) {
                        Player q = Bukkit.getPlayer(id);
                        if (q != null) q.sendMessage(P + ChatColor.RED + "没有取出（" + res.status + ": " + res.detail + "），装备还在库里");
                        if (res.status == TxnStatus.CONFLICT) { Player qq = Bukkit.getPlayer(id); if (qq != null) reload(qq, null); }
                        return;
                    }
                    loadouts.rememberRow(uid, id, after.rev, "active");
                    replace(id, uid, null);
                    Player q = Bukkit.getPlayer(id);
                    if (q == null) { plugin.getLogger().warning("[P1 gearlib] " + id + " left during withdraw of " + uid + "; row active, no stack — /corerpg p1 audit restore"); return; }
                    giveBack(q, fresh);
                    loadouts.refresh(q);
                    q.sendMessage(P + ChatColor.GREEN + "已取出 " + after.shortLabel());
                    q.playSound(q.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.6f, 1.0f);
                    if (q.getOpenInventory().getTopInventory().getHolder() instanceof Holder) openLib(q, ((Holder) q.getOpenInventory().getTopInventory().getHolder()).opener, ((Holder) q.getOpenInventory().getTopInventory().getHolder()).page);
                });
    }

    /**
     * 一键存入: whitelisted materials of the whole backpack + hotbar → warehouse (anywhere); P1 gear in the backpack rows
     * (not the hotbar, not the blade in use, not the selected charm) → library (town only).
     */
    public void depositAll(final Player p, final Runnable done) {
        Map<String, Long> mats = vault() == null ? new HashMap<String, Long>() : vault().depositAll(p);
        final List<String> parts = new ArrayList<String>();
        for (Map.Entry<String, Long> m : mats.entrySet()) parts.add(plugin.getNiBridge().displayName(m.getKey()) + " ×" + m.getValue());
        List<Integer> gear = new ArrayList<Integer>();
        String g = gate(p);
        if (g == null && usable()) {
            Set<String> eq = equipped(p);
            for (int i = 9; i < 36; i++) {
                ItemStack x = p.getInventory().getItem(i);
                if (x == null || !loadouts.items().hasData(x)) continue;
                EmberItems.Read r = loadouts.items().read(x);
                if (r != null && r.ok() && r.data != null && !eq.contains(r.data.uid)) gear.add(i);
            }
        }
        final String gateMsg = g;
        if (gear.isEmpty()) {
            report(p, parts, 0, gateMsg);
            if (done != null) done.run();
            return;
        }
        final int[] left = {gear.size()}, ok = {0};
        for (int i : gear) stashSlot(p, i, label -> {
            if (label != null) ok[0]++;
            if (--left[0] == 0) {
                Player q = Bukkit.getPlayer(p.getUniqueId());
                if (q != null) { report(q, parts, ok[0], null); loadouts.refresh(q); }
                if (done != null) done.run();
            }
        });
    }

    private void report(Player p, List<String> mats, int gear, String gateMsg) {
        if (mats.isEmpty() && gear == 0) p.sendMessage(PV + "背包里没有可存的材料或装备" + (gateMsg != null ? "（装备：" + gateMsg + "）" : "")
                + "。快捷栏、手上的刃和已选的护符不会被存走。");
        else p.sendMessage(PV + ChatColor.GREEN + "一键存入：" + (mats.isEmpty() ? "" : "材料 " + String.join("、", mats))
                + (gear > 0 ? (mats.isEmpty() ? "" : "；") + "装备 " + gear + " 件 → 装备库" : "")
                + (gateMsg != null ? ChatColor.GRAY + "（装备没存：" + gateMsg + "）" : ""));
    }

    // ================================================================== flags + bulk dismantle

    private void toggle(Player p, String uid, boolean lock) {
        UUID id = p.getUniqueId();
        Entry e = find(id, uid);
        if (e == null) return;
        Entry n = lock ? new Entry(e.d, !e.locked, e.fav, e.storedAt) : new Entry(e.d, e.locked, !e.fav, e.storedAt);
        replace(id, uid, n);
        store().saveLibFlags(uid, id, n.locked, n.fav, 0L);
    }


    /** uids in {@code view} that currently carry a 词条 (D143); empty when growth service / player data missing */
    private Set<String> affixUids(Player p, List<Entry> view) {
        Set<String> out = new HashSet<String>();
        EmberGrowthService gs = EmberGrowthService.get();
        if (gs == null || p == null || view == null) return out;
        PlayerData pd;
        try { pd = plugin.getDataStore().get(p.getUniqueId()); } catch (RuntimeException e) { return out; }
        if (pd == null) return out;
        for (Entry e : view) {
            try { if (gs.affixOf(pd, e.d.uid) > 0) out.add(e.d.uid); } catch (RuntimeException ignored) {}
        }
        return out;
    }

    private Filter filter(UUID id) { Filter f = filters.get(id); if (f == null) { f = new Filter(); filters.put(id, f); } return f; }

    private boolean bulk(final Player p, boolean go, String tok) {
        String g = gate(p);
        if (g != null) { p.sendMessage(P + ChatColor.RED + g); return true; }
        final UUID id = p.getUniqueId();
        Filter f = filter(id);
        List<Entry> view = EmberStorageRules.view(new ArrayList<Entry>(entries(id)), f);
        final List<Entry> take = EmberStorageRules.bulkDismantle(view, equipped(p), affixUids(p, view));
        int skipped = view.size() - take.size();
        StringBuilder fp = new StringBuilder();
        for (Entry e : take) fp.append(e.d.uid).append(':').append(e.d.rev).append(',');
        String fps = Integer.toHexString(fp.toString().hashCode());
        if (take.isEmpty()) { p.sendMessage(P + "当前筛选（" + f.label() + "）里没有可分解的件（锁定、收藏、有投入、非掉落件、T0 都不分解）"); return true; }
        if (go) {
            String bad = ConfirmTokens.consume(p, "glibbulk", tok, fps);
            if (bad != null) { p.sendMessage(P + ChatColor.RED + bad + "（列表有变化时请重新点批量分解）"); go = false; }
        }
        if (!go) {
            p.sendMessage(P + "批量分解（筛选：" + f.label() + "）：" + ChatColor.WHITE + take.size() + ChatColor.GRAY + " 件 → 胚料 ×"
                    + EmberStorageRules.blanksOf(take) + (skipped > 0 ? "；另有 " + skipped + " 件锁定 / 收藏 / 有投入(强化·精工·成色卓越+/词条) / 不可分解，跳过" : ""));
            int shown = 0;
            for (Entry e : take) { if (shown++ >= 8) { p.sendMessage(P + "  … 共 " + take.size() + " 件"); break; } p.sendMessage(P + "  · " + e.d.shortLabel()); }
            p.sendMessage(P + ChatColor.GRAY + "有强化 / 精工 / 成色卓越及以上 / 词条的件默认不批量分解（单件分解仍可）。");
            p.sendMessage(P + ChatColor.YELLOW + "分解后 " + undoMinutes() + " 分钟内可在装备库「撤销分解」找回（要退回胚料）。");
            String t = ConfirmTokens.issue(p, "glibbulk", fps);
            ConfirmTokens.sendClick(p, P + "确认无误再点：", "[确认批量分解]", "/corerpg p1 gearlib bulk confirm tok:" + t,
                    "分解上面列出的 " + take.size() + " 件\n锁定 / 收藏 / 装备中 / 有投入的不会分解");
            return true;
        }
        final int[] left = {take.size()}, okN = {0}, blanks = {0};
        for (final Entry e : take) {
            if (busy.contains(e.d.uid)) { if (--left[0] == 0) bulkDone(id, okN[0], blanks[0]); continue; }
            busy.add(e.d.uid);
            final int y = EmberUpgradeRules.dismantleYield(e.d);
            store().commitTxn("glibdis:" + e.d.uid + ":" + e.d.rev, "glibdis", id, Arrays.asList(new TxnItem(e.d, null, "dismantled", "stored")),
                    null, "装备库分解 " + e.d.shortLabel() + " → 胚料×" + y, res -> {
                        busy.remove(e.d.uid);
                        if (res.status == TxnStatus.OK) {
                            okN[0]++; blanks[0] += y;
                            loadouts.rememberRow(e.d.uid, id, e.d.rev + 1, "dismantled");
                            replace(id, e.d.uid, null);
                        }
                        if (--left[0] == 0) bulkDone(id, okN[0], blanks[0]);
                    });
        }
        return true;
    }

    private void bulkDone(UUID id, int n, int blanks) {
        Player q = Bukkit.getPlayer(id);
        if (q == null) { plugin.getLogger().warning("[P1 gearlib] bulk dismantle " + id + " left: " + n + " pieces, blanks " + blanks + " not given"); return; }
        if (blanks > 0) { if (vault() != null) vault().give(q, EmberUpgradeRules.MAT_BLANK, blanks); else plugin.getNiBridge().giveNiItem(q, EmberUpgradeRules.MAT_BLANK, blanks); }
        plugin.getLogger().info("[P1 gearlib] bulk dismantle " + q.getName() + " " + n + " pieces → blanks " + blanks);
        q.sendMessage(P + ChatColor.GREEN + "已分解 " + n + " 件 → 胚料 ×" + blanks + ChatColor.GRAY + "（" + undoMinutes() + " 分钟内可撤销）");
        q.playSound(q.getLocation(), Sound.BLOCK_ANVIL_USE, 0.6f, 1.2f);
    }

    // ================================================================== soft undo of a dismantle

    public boolean undo(final Player p, String[] args) {
        final int mins = undoMinutes();
        if (mins <= 0) { p.sendMessage(P + "撤销分解已关闭"); return true; }
        if (!store().usable()) { p.sendMessage(P + ChatColor.RED + "需要 MySQL"); return true; }
        final UUID id = p.getUniqueId();
        final String want = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : null;
        final long now = System.currentTimeMillis();
        store().recentDismantles(id, now - mins * 60000L, rows -> {
            if (!p.isOnline()) return;
            if (want == null) {
                if (rows.isEmpty()) { p.sendMessage(P + "最近 " + mins + " 分钟没有可撤销的分解（洗练吃掉的重复件不能撤销）"); return; }
                p.sendMessage(P + "最近 " + mins + " 分钟分解的装备（撤销要退回当时给的胚料，物品回到装备库）：");
                for (LibRow r : rows)
                    ConfirmTokens.sendButtons(p, P + "· " + r.data.shortLabel() + " §8(剩 " + EmberStorageRules.left(r.updatedAt, now, mins) + ") ",
                            new String[]{"[撤销]", "/corerpg p1 undo " + r.data.uid, "退回胚料 ×" + EmberUpgradeRules.dismantleYield(r.data) + "，这件回到装备库", "GREEN"});
                return;
            }
            LibRow hit = null;
            for (LibRow r : rows) if (r.data.uid.equals(want)) hit = r;
            if (hit == null) { p.sendMessage(P + ChatColor.RED + "这件不在可撤销的列表里（超过 " + mins + " 分钟、已撤销，或不是分解）"); return; }
            String g = gate(p);
            if (g != null) { p.sendMessage(P + ChatColor.RED + g); return; }
            final EmberItemData d = hit.data;
            if (busy.contains(d.uid)) return;
            final int y = EmberUpgradeRules.dismantleYield(d);
            int have = plugin.getNiBridge().countInInventory(p, EmberUpgradeRules.MAT_BLANK);
            if (have < y) { p.sendMessage(P + ChatColor.RED + "撤销要退回胚料 ×" + y + "，背包 + 仓库只有 " + have); return; }
            if (y > 0 && plugin.getNiBridge().consume(p, EmberUpgradeRules.MAT_BLANK, y) < y) { p.sendMessage(P + ChatColor.RED + "扣胚料失败"); return; }
            busy.add(d.uid);
            store().commitTxn("undo:" + d.uid + ":" + d.rev, "undo", id, Arrays.asList(new TxnItem(d, null, "stored", "dismantled")),
                    "{\"mat_ember_v1_blank\":" + y + "}", "撤销分解 " + d.shortLabel() + "（退回胚料×" + y + "，回到装备库）", res -> {
                        busy.remove(d.uid);
                        Player q = Bukkit.getPlayer(id);
                        if (res.status == TxnStatus.OK) {
                            long t = System.currentTimeMillis();
                            loadouts.rememberRow(d.uid, id, d.rev + 1, "stored");
                            store().saveLibFlags(d.uid, id, false, false, t);
                            replace(id, d.uid, new Entry(d.withRev(d.rev + 1), false, false, t));
                            plugin.getLogger().info("[P1 gearlib] undo dismantle " + id + " " + d.uid.substring(0, 8) + " blanks back " + y);
                            if (q != null) q.sendMessage(P + ChatColor.GREEN + "已撤销：" + d.shortLabel() + " 回到装备库（主菜单 仓库 → 装备库）");
                        } else {
                            if (q != null) { if (vault() != null) vault().give(q, EmberUpgradeRules.MAT_BLANK, y); else plugin.getNiBridge().giveNiItem(q, EmberUpgradeRules.MAT_BLANK, y); }
                            if (q != null) q.sendMessage(P + ChatColor.RED + "没有撤销（" + res.status + ": " + res.detail + "），胚料已退回");
                        }
                    });
        });
        return true;
    }

    // ================================================================== admin item history

    public boolean itemlog(final CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (!store().usable()) { s.sendMessage(P + "需要 MySQL"); return true; }
        if (args.length < 3) { s.sendMessage(P + "/corerpg p1 itemlog <玩家> [条数] · /corerpg p1 itemlog uid <前缀≥6> [条数]"); return true; }
        boolean byUid = "uid".equalsIgnoreCase(args[2]);
        int n = 20;
        String last = args[args.length - 1];
        if (args.length >= (byUid ? 5 : 4)) try { n = Integer.parseInt(last); } catch (NumberFormatException ignored) {}
        final String prefix;
        final UUID owner;
        if (byUid) {
            if (args.length < 4 || !args[3].toLowerCase(Locale.ROOT).matches("[0-9a-f]{6,32}")) { s.sendMessage(P + "uid 前缀至少 6 位十六进制"); return true; }
            prefix = args[3].toLowerCase(Locale.ROOT); owner = null;
        } else {
            Player on = Bukkit.getPlayerExact(args[2]);
            @SuppressWarnings("deprecation") org.bukkit.OfflinePlayer op = on != null ? on : Bukkit.getOfflinePlayer(args[2]);
            if (op == null || op.getUniqueId() == null) { s.sendMessage(P + "找不到玩家 " + args[2]); return true; }
            owner = op.getUniqueId(); prefix = null;
        }
        final String who = byUid ? "uid " + prefix : args[2];
        store().history(owner, prefix, n, rows -> {
            s.sendMessage(P + "物品记录 " + who + "：" + rows.size() + " 条（新的在前；create 创建 · enhance/upgrade/refine/quality/swap 锻造 · dismantle 分解 · reroll 洗练吃掉 · stash/unstash 装备库 · glibdis 库内分解 · undo 撤销分解）");
            SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT);
            f.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            for (EmberItemStore.TxnRow r : rows)
                s.sendMessage(ChatColor.DARK_GRAY + "  " + f.format(new Date(r.at)) + " CST " + ChatColor.YELLOW + r.kind + ChatColor.GRAY + " "
                        + r.uidA.substring(0, 8) + (r.uidB == null ? "" : "+" + r.uidB.substring(0, 8))
                        + (byUid ? " owner " + r.owner.substring(0, 8) : "") + ChatColor.WHITE + " " + (r.note == null ? "" : r.note)
                        + (r.cost == null || "{}".equals(r.cost) ? "" : ChatColor.DARK_GRAY + " " + r.cost));
        });
        return true;
    }

    // ================================================================== commands

    /** /corerpg p1 vault | gearlib | stash | undo | itemlog */
    public boolean cmd(CommandSender s, String sub, String[] args) {
        if ("itemlog".equals(sub)) return itemlog(s, args);
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        if (!EmberMode.active()) { p.sendMessage(P + "P1 模式未开启"); return true; }
        String a = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        String opener = args.length >= 4 && "from".equals(a) ? args[3] : null;
        if ("undo".equals(sub)) return undo(p, args);
        if ("stash".equals(sub) || "存入".equals(sub)) { depositAll(p, null); return true; }
        if ("vault".equals(sub) || "仓库".equals(sub)) {
            if ("auto".equals(a)) { boolean on = vault() != null && vault().toggleAuto(p); p.sendMessage(PV + "材料自动入库：" + (on ? "§a开" : "§c关")); return true; }
            if ("take".equals(a) && args.length >= 5) {
                long n; try { n = Long.parseLong(args[4]); } catch (NumberFormatException e) { n = 64; }
                takeMat(p, args[3], n);
                return true;
            }
            openVault(p, opener);
            return true;
        }
        if ("gearlib".equals(sub) || "装备库".equals(sub)) {
            if (!usable()) { p.sendMessage(P + ChatColor.RED + "装备库需要 MySQL 存储"); return true; }
            if ("take".equals(a) && args.length >= 4) { withdraw(p, args[3].toLowerCase(Locale.ROOT)); return true; }
            if ("bulk".equals(a)) {
                boolean go = false; String tok = null;
                for (int i = 3; i < args.length; i++) { if ("confirm".equalsIgnoreCase(args[i])) go = true; else if (args[i].startsWith("tok:")) tok = args[i].substring(4); }
                return bulk(p, go, tok);
            }
            if ("mode".equals(a)) { setMode(p, EmberStorageRules.nextStashMode(mode(p))); p.sendMessage(P + "新装备自动入库：" + EmberStorageRules.stashModeName(mode(p))); return true; }
            final String op = opener;
            reload(p, () -> openLib(p, op, 0));
            return true;
        }
        return false;
    }

    private void takeMat(Player p, String niId, long n) {
        if (vault() == null) return;
        long got = vault().withdraw(p, niId, n);
        if (got < 0) p.sendMessage(PV + ChatColor.RED + "背包满了");
        else if (got == 0) p.sendMessage(PV + "仓库里没有 " + plugin.getNiBridge().displayName(niId));
        else p.sendMessage(PV + ChatColor.GREEN + "取出 " + plugin.getNiBridge().displayName(niId) + " ×" + got + ChatColor.GRAY + "（库存 " + vault().amount(p.getUniqueId(), niId) + "）");
    }

    // ================================================================== GUI (chest pages; every click is cancelled)

    static final class Holder implements InventoryHolder {
        final String kind; final String opener; final int page;
        final Map<Integer, String> slotKey = new HashMap<Integer, String>();
        Inventory inv;
        Holder(String kind, String opener, int page) { this.kind = kind; this.opener = opener; this.page = page; }
        @Override public Inventory getInventory() { return inv; }
    }

    private static ItemStack icon(Material m, int data, String name, String... lore) {
        ItemStack it = new ItemStack(m, 1, (short) data);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(new ArrayList<String>(Arrays.asList(lore)));
        it.setItemMeta(meta);
        return it;
    }

    private static String menuOf(String opener) {
        if (opener == null) return null;
        if ("hub".equalsIgnoreCase(opener)) return "ember_hub";
        if ("gear".equalsIgnoreCase(opener)) return "ember_p1_gear";
        return null;
    }

    public void openVault(Player p, String opener) {
        EmberVault v = vault();
        Holder h = new Holder("vault", opener, 0);
        Inventory inv = Bukkit.createInventory(h, 54, "§3余烬 · 仓库");
        h.inv = inv;
        int slot = 0;
        if (v != null) {
            for (Map.Entry<String, Long> e : v.all(p.getUniqueId()).entrySet()) {
                if (slot >= 36) break;
                ItemStack proto = plugin.getNiBridge().createNiItem(e.getKey());
                Material m = proto == null ? Material.PAPER : proto.getType();
                int dv = proto == null ? 0 : proto.getDurability();
                inv.setItem(slot, icon(m, dv, "§f" + plugin.getNiBridge().displayName(e.getKey()),
                        "§7库存 §f" + e.getValue(), "", "§7锻造、洗练、兑换直接从仓库扣，不用取出",
                        "§e左键 §f取 64 §8· §eShift+左键 §f取一整背包 §8· §e右键 §f取 1"));
                h.slotKey.put(slot, "mat:" + e.getKey());
                slot++;
            }
        }
        if (slot == 0) inv.setItem(13, icon(Material.BARRIER, 0, "§7仓库是空的", "§7碎片、骨尘、核心碎片、胚料、活动币",
                "§7捡到或结算时自动进这里（自动入库开着时）", "§7也可以点下面「一键存入」"));
        boolean auto = v != null && v.autoOn(p);
        int n = count(p.getUniqueId());
        inv.setItem(45, icon(Material.ARROW, 0, "§7返回", menuOf(opener) == null ? "§8关闭" : "§8回到打开它的那一页"));
        inv.setItem(47, icon(Material.HOPPER, 0, "§f材料自动入库：" + (auto ? "§a开" : "§c关"),
                "§7开着时，捡起或结算得到的碎片 / 骨尘 / 核心碎片 / 胚料 / 活动币", "§7直接进仓库（动作栏有提示），背包不占格", "", "§e点击切换"));
        inv.setItem(48, icon(Material.CHEST, 0, "§a一键存入", "§7材料：背包 + 快捷栏里的全部存进仓库", "§7装备：背包里（不含快捷栏、手上的刃、已选的护符）",
                "§7存进装备库（只能在城里）", "", "§e点击存入"));
        inv.setItem(49, icon(Material.DIAMOND_SWORD, 0, "§b装备库 §7（" + n + " 件）", "§7存放余烬刃 / 护符：没有格数上限",
                "§7分页、筛选、排序、锁定、收藏、批量分解", "", "§e点击打开"));
        inv.setItem(50, icon(Material.ENDER_CHEST, 0, "§5末影箱", "§7普通物品仍放末影箱", "", "§e点击打开"));
        int mode = mode(p);
        inv.setItem(51, icon(Material.EMERALD, 0, "§f新装备自动入库：§e" + EmberStorageRules.stashModeName(mode),
                "§7背包快满时：空格少于 " + minFree() + " 格才把掉落的新装备存进装备库", "§7总是：比现在用的强的照样进背包，其余都进库",
                "§7关闭：都进背包（背包满了就暂存，点「补领」）", "", "§e点击切换"));
        inv.setItem(53, icon(Material.BOOK, 0, "§7说明", "§7仓库：每种材料一格，数量近无限，不用解锁",
                "§7装备库：装备存在数据库里，库里的不能用也不会丢", "§7分解后 " + undoMinutes() + " 分钟内可在装备库撤销"));
        p.openInventory(inv);
    }

    private ItemStack gearIcon(Player p, Entry e) {
        Material m = iconType.get(e.d.ni);
        if (m == null) {
            ItemStack proto = plugin.getNiBridge().createNiItem(e.d.ni);
            m = proto == null ? (e.d.isBlade() ? Material.IRON_SWORD : Material.GOLD_NUGGET) : proto.getType();
            iconType.put(e.d.ni, m);
        }
        List<String> lore = new ArrayList<String>();
        PlayerData pd = data(p.getUniqueId());
        int lv = pd == null ? 10 : pd.getEmberLevel();
        for (String l : EmberCompare.card(EmberMode.tables(), e.d, lv)) lore.add("§7" + l);
        EmberGrowthService gs = EmberGrowthService.get();
        if (gs != null && e.d.tier > 0) try { lore.add("§7词条：" + gs.affixText(gs.affixOf(pd, e.d.uid), e.d.quality)); } catch (RuntimeException ignored) {}
        lore.add("§8来源 " + EmberCompare.sourceName(e.d.source) + " · uid " + e.d.uid.substring(0, 8));
        lore.add("");
        lore.add("§e左键 §f取出到背包" + (gate(p) != null ? " §c(回城后)" : ""));
        lore.add("§e右键 §f" + (e.locked ? "解锁" : "锁定") + " §8· §eShift+右键 §f" + (e.fav ? "取消收藏" : "收藏"));
        String name = (e.fav ? "§e★ " : "") + (e.locked ? "§c[锁] " : "") + "§f" + e.d.shortLabel();
        ItemStack it = icon(m, 0, name, lore.toArray(new String[0]));
        return it;
    }

    public void openLib(Player p, String opener, int page) {
        UUID id = p.getUniqueId();
        Filter f = filter(id);
        List<Entry> view = EmberStorageRules.view(new ArrayList<Entry>(entries(id)), f);
        int pages = EmberStorageRules.pages(view.size(), PER_PAGE);
        int pg = Math.max(0, Math.min(page, pages - 1));
        Holder h = new Holder("lib", opener, pg);
        Inventory inv = Bukkit.createInventory(h, 54, "§3余烬 · 装备库 §8" + (pg + 1) + "/" + pages);
        h.inv = inv;
        List<Entry> shown = EmberStorageRules.page(view, pg, PER_PAGE);
        for (int i = 0; i < shown.size(); i++) { inv.setItem(i, gearIcon(p, shown.get(i))); h.slotKey.put(i, "gear:" + shown.get(i).d.uid); }
        if (view.isEmpty()) inv.setItem(22, icon(Material.BARRIER, 0, "§7" + (entries(id).isEmpty() ? "装备库是空的" : "当前筛选没有装备"),
                "§7仓库页「一键存入」把背包里的装备存进来", "§7筛选：" + f.label()));
        List<Entry> bulk = EmberStorageRules.bulkDismantle(view, equipped(p), affixUids(p, view));
        inv.setItem(45, icon(Material.ARROW, 0, "§7返回仓库", "§8仓库页的返回键回到打开它的那一页"));
        inv.setItem(46, icon(Material.PAPER, 0, "§f上一页", "§7第 " + (pg + 1) + " / " + pages + " 页"));
        inv.setItem(47, icon(Material.DIAMOND, 0, "§f成色：§e" + (f.quality < 0 ? "全部" : EmberItemData.qualityName(f.quality)), "§e点击切换"));
        inv.setItem(48, icon(Material.BLAZE_POWDER, 0, "§f套装族：§e" + (f.family == null ? "全部" : EmberItemData.familyName(f.family)), "§e点击切换"));
        inv.setItem(49, icon(Material.IRON_SWORD, 0, "§f部位：§e" + (f.slot == null ? "全部" : EmberItemData.slotName(f.slot)), "§e点击切换"));
        inv.setItem(50, icon(Material.EXP_BOTTLE, 0, "§f阶：§e" + (f.tier < 0 ? "全部" : "T" + f.tier), "§e点击切换"));
        inv.setItem(51, icon(Material.HOPPER, 0, "§f排序：§e" + Filter.SORTS[f.sort], "§e点击切换", "§8" + view.size() + " 件符合筛选，共 " + entries(id).size() + " 件"));
        inv.setItem(52, icon(Material.ANVIL, 0, "§c批量分解（当前筛选）", "§7可分解 §f" + bulk.size() + " §7件 → 胚料 ×" + EmberStorageRules.blanksOf(bulk),
                "§7锁定 / 收藏 / 装备中 / 有投入(强化·精工·成色卓越+/词条) / 非掉落 / T0 跳过", "§7点了先在聊天里列出清单，再点确认", "§e右键：撤销最近的分解（" + undoMinutes() + " 分钟内）"));
        inv.setItem(53, icon(Material.PAPER, 0, "§f下一页", "§7第 " + (pg + 1) + " / " + pages + " 页"));
        p.openInventory(inv);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Holder) || !(e.getWhoClicked() instanceof Player)) return;
        e.setCancelled(true); // read-only pages: nothing moves in or out by clicking
        final Player p = (Player) e.getWhoClicked();
        final Holder h = (Holder) e.getInventory().getHolder();
        if (e.getRawSlot() < 0 || e.getRawSlot() >= 54) return;
        final int raw = e.getRawSlot();
        final ClickType ct = e.getClick();
        Bukkit.getScheduler().runTask(plugin, () -> click(p, h, raw, ct));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Holder) e.setCancelled(true);
    }

    private void click(Player p, Holder h, int slot, ClickType ct) {
        String key = h.slotKey.get(slot);
        if ("vault".equals(h.kind)) {
            if (key != null && key.startsWith("mat:")) {
                long n = ct.isShiftClick() ? Long.MAX_VALUE : ct.isRightClick() ? 1 : 64;
                takeMat(p, key.substring(4), n);
                openVault(p, h.opener);
                return;
            }
            switch (slot) {
                case 45: back(p, menuOf(h.opener)); return;
                case 47: if (vault() != null) vault().toggleAuto(p); openVault(p, h.opener); return;
                case 48: depositAll(p, () -> { Player q = Bukkit.getPlayer(p.getUniqueId()); if (q != null) openVault(q, h.opener); }); return;
                case 49:
                    if (!usable()) { p.sendMessage(P + ChatColor.RED + "装备库需要 MySQL 存储"); return; }
                    reload(p, () -> openLib(p, h.opener, 0)); return;
                case 50: p.closeInventory(); Bukkit.dispatchCommand(p, "corerpg enderchest"); return;
                case 51: setMode(p, EmberStorageRules.nextStashMode(mode(p))); openVault(p, h.opener); return;
                default: return;
            }
        }
        // lib
        Filter f = filter(p.getUniqueId());
        if (key != null && key.startsWith("gear:")) {
            String uid = key.substring(5);
            if (ct == ClickType.SHIFT_RIGHT) toggle(p, uid, false);
            else if (ct.isRightClick()) toggle(p, uid, true);
            else if (ct.isLeftClick()) { withdraw(p, uid); return; }
            openLib(p, h.opener, h.page);
            return;
        }
        switch (slot) {
            case 45: openVault(p, h.opener); return;
            case 46: openLib(p, h.opener, h.page - 1); return;
            case 53: openLib(p, h.opener, h.page + 1); return;
            case 47: f.cycleQuality(); break;
            case 48: f.cycleFamily(); break;
            case 49: f.cycleSlot(); break;
            case 50: f.cycleTier(); break;
            case 51: f.cycleSort(); break;
            case 52:
                p.closeInventory();
                if (ct.isRightClick()) undo(p, new String[]{"p1", "undo"}); else bulk(p, false, null);
                return;
            default: return;
        }
        openLib(p, h.opener, 0);
    }

    private void back(Player p, String menu) {
        p.closeInventory();
        if (menu != null) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
        filters.remove(e.getPlayer().getUniqueId());
    }
}
