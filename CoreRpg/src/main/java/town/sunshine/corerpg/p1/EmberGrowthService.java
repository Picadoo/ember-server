package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D141–D143 horizontal growth runtime (P2 draft §5za). Owns {@code ember-v1-growth.yml}, the per-player picks
 * (PlayerData counters, period "all"), the 天赋专精 menu / commands / placeholders, and the combat hooks the P1
 * pipeline asks for (outgoing / incoming multipliers, the after-dodge window, potion share, 烬核 share weight, set
 * tuning). Every hook returns the neutral value when the player has nothing picked, outside P1 or with no config.
 */
public final class EmberGrowthService implements Listener {

    static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    public static final String MENU = "ember_p1_spec";
    static final String C_LEARN = "p4_spec_learn_";   // + node id, period all: 1 = learned (coin paid once)
    static final String C_ROW = "p4_spec_row";        // + row, period all: 1-based index of the active node in that row
    static final String C_RESETS = "p4_spec_resets";  // period all: respecs used (first one free)
    public static final String C_CHAL = "p4_chal_";   // + map key, period all: challenge first clear (D141 point source)

    private static volatile EmberGrowthService instance;
    public static EmberGrowthService get() { return instance; }

    private final CoreRpgPlugin plugin;
    private final EmberRunService runs;
    private EmberGrowth.Talents talents;
    private EmberGrowth.Honors honors;
    private EmberAffix.Rules reroll;
    private final Map<UUID, Object[]> honorCache = new ConcurrentHashMap<UUID, Object[]>(); // uuid → {until ms, List<String>}
    static final String C_HONOR_SEEN = "p4_honor_";     // + id, period all: the one-time "勋记解锁" notice was sent
    static final String HONOR_MENU = "ember_p1_honor";
    /** + honor id, period all: admin test grant (Part A 10-04); counts as the condition being met */
    static final String C_HONOR_TEST = "p4_honortest_";
    private static final Map<UUID, String> HFROM = new ConcurrentHashMap<UUID, String>();
    private long epoch;
    private final Map<UUID, Object[]> cache = new ConcurrentHashMap<UUID, Object[]>(); // uuid → {key, Mods}
    private final Map<UUID, Long> dodgeUntil = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> dodgeHealCd = new ConcurrentHashMap<UUID, Long>();
    /** Back button (same idea as D136): the page the talent menu was opened from */
    private static final Map<UUID, String> FROM = new ConcurrentHashMap<UUID, String>();
    static final Map<String, String> FROM_MENUS = new LinkedHashMap<String, String>();
    static {
        FROM_MENUS.put("gear", "ember_p1_gear");
        FROM_MENUS.put("hub", "ember_hub");
    }

    public EmberGrowthService(CoreRpgPlugin plugin, EmberRunService runs) {
        this.plugin = plugin;
        this.runs = runs;
        instance = this;
        load();
    }

    public void load() {
        File f = new File(plugin.getDataFolder(), EmberGrowth.FILE);
        if (!f.isFile()) plugin.saveResource(EmberGrowth.FILE, false);
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            Object o = new Yaml().load(r);
            Map<?, ?> root = o instanceof Map ? (Map<?, ?>) o : null;
            talents = EmberGrowth.parseTalents(root);
            honors = EmberGrowth.parseHonors(root);
            reroll = EmberAffix.parse(root);
            honorCache.clear();
            epoch++;
            cache.clear();
            plugin.getLogger().info("[P1 growth] " + EmberGrowth.FILE + ": talents " + (talents == null ? "off" : talents.nodes.size() + " nodes / " + talents.points.size() + " points")
                    + ", honors " + (honors == null ? "off" : honors.list.size()) + ", reroll " + (reroll == null ? "off" : reroll.defs.size() + " affixes"));
        } catch (Exception e) {
            talents = null;
            plugin.getLogger().warning("[P1 growth] cannot read " + EmberGrowth.FILE + ": " + e);
        }
    }

    public EmberGrowth.Talents talents() { return talents; }

    // ------------------------------------------------------------------ state

    private PlayerData data(UUID u) { return runs.dataOf(u); }

    public Map<Integer, String> picks(PlayerData d) {
        Map<Integer, String> m = new LinkedHashMap<Integer, String>();
        if (talents == null) return m;
        if (talents == null || d == null) return m;
        for (EmberGrowth.Row r : talents.rows) {
            int i = d.periodCount(C_ROW + r.row, "all");
            List<EmberGrowth.Node> in = talents.inRow(r.row);
            m.put(r.row, i >= 1 && i <= in.size() ? in.get(i - 1).id : null);
        }
        return m;
    }

    public boolean learned(PlayerData d, String id) { return d != null && d.periodCount(C_LEARN + id, "all") > 0; }

    /** one point source reached? */
    public boolean sourceDone(PlayerData d, EmberGrowth.PointSrc ps) {
        if (d == null || ps == null) return false;
        if ("first_clear".equals(ps.kind)) return runs.progressFlag(d, ps.arg);
        if ("challenge_any".equals(ps.kind)) {
            for (String k : d.getCounters().keySet()) if (k.startsWith(C_CHAL) && k.endsWith("@all")) return true;
            return false;
        }
        if ("raid_any".equals(ps.kind)) {
            for (String r : new String[]{"r01", "r02", "r03"}) if (EmberCosmetics.raidClears(d, r) > 0) return true;
            return false;
        }
        if ("abyss_floor".equals(ps.kind)) {
            try { return runs.abyssBest(d) >= Integer.parseInt(ps.arg.trim()); } catch (RuntimeException e) { return false; }
        }
        return false;
    }

    public int pointsEarned(PlayerData d) {
        if (talents == null) return 0;
        int n = 0;
        for (EmberGrowth.PointSrc ps : talents.points) if (sourceDone(d, ps)) n++;
        return n;
    }

    /** Combined growth modifiers of this player for the loadout's active set (cached per epoch + set). */
    public EmberGrowth.Mods mods(Player p) {
        if (p == null || !EmberMode.active() || (talents == null && honors == null && reroll == null)) return EmberGrowth.Mods.NONE;
        PlayerData d = data(p.getUniqueId());
        if (d == null) return EmberGrowth.Mods.NONE;
        EmberLoadout lo = runs.loadouts().get(p);
        String set = lo.activeSet;
        List<String> hon = honorsEarned(p.getUniqueId(), d);
        int[][] af = affixItems(d, lo);
        List<EmberSignature.Def> sg = signatures(d, lo); // D174
        String key = epoch + "|" + set + "|" + hon + "|" + afKey(af) + "|" + sigKey(sg);
        Object[] c = cache.get(p.getUniqueId());
        if (c != null && key.equals(c[0])) return (EmberGrowth.Mods) c[1];
        List<Map<String, Double>> parts = new ArrayList<Map<String, Double>>(EmberGrowth.talentParts(talents, picks(d), set));
        if (honors != null && !hon.isEmpty()) parts.add(EmberGrowth.honorParts(honors, hon));
        if (reroll != null) { Map<String, Double> ap = EmberAffix.parts(reroll, af); if (!ap.isEmpty()) parts.add(ap); } // D143
        for (EmberSignature.Def sd : sg) parts.add(sd.mods); // D174 签名传奇 (≤ 2, each its own part so the ADD keys add)
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(parts);
        cache.put(p.getUniqueId(), new Object[]{key, m});
        return m;
    }

    // ------------------------------------------------------------------ combat hooks

    /**
     * Target-class piece of {@link #outMult}: boss / split add / affixed elite body / other mob.
     * D163 / B01: {@code dmg_affix_body} applies only to the affixed elite body (not split adds), so talent
     * 「拆分」{@code dmg_split: 1.50, dmg_affix_body: 0.80} is net +50% on clones and −20% on the body.
     */
    static double classMult(EmberGrowth.Mods m, String cls) { return classMult(m, cls, null); }

    /**
     * D164 破甲: {@code affix} = the elite type of the target (blazing / split / shield; split adds count as "split"),
     * so {@code dmg_affix_<type>} multiplies the body and — for split — its clones too (p1sim builddiv P7).
     */
    static double classMult(EmberGrowth.Mods m, String cls, String affix) {
        if ("boss".equals(cls)) return m.get("dmg_boss");
        double r;
        if ("split".equals(cls)) r = m.get("dmg_affix") * m.get("dmg_split"); // clones: 猎缀/破缀 × 裂身纹
        else if ("affix".equals(cls)) r = m.get("dmg_affix") * m.get("dmg_affix_body"); // body: × body-only key
        else return m.get("dmg_mob");
        String kind = "split".equals(cls) ? "split" : affix;
        if (kind != null) r *= m.get("dmg_affix_" + kind);
        return r;
    }

    /** Outgoing multiplier on a run mob (boss / affixed elite / split add / other mob) + the after-dodge window + set events. */
    public double outMult(Player p, Entity target, EmberSetEngine.Kind kind) {
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return 1.0;
        String cls = runs.mobClass(target);
        if (cls == null) return 1.0; // only inside P1 runs
        double r = classMult(m, cls, "affix".equals(cls) ? runs.mobAffix(target) : null);
        Long until = dodgeUntil.get(p.getUniqueId());
        if (until != null && System.currentTimeMillis() < until) r *= m.get("dodge_dmg");
        if (kind == EmberSetEngine.Kind.BURN || kind == EmberSetEngine.Kind.EXPLOSION) r *= m.get("set_dmg");
        return r;
    }

    /** Incoming multiplier by hit class: tele (boss telegraph) / boss (boss melee) / mob / affix / share */
    public double takenMult(Player p, String cls) {
        if (cls == null) return 1.0;
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return 1.0;
        double r = m.get("taken_all");
        if (m.has("abyss_taken") && runs.inAbyss(p)) r *= m.get("abyss_taken"); // D142 深渊老手 (abyss only)
        if ("tele".equals(cls)) r *= m.get("taken_tele");
        else if ("boss".equals(cls)) r *= m.get("taken_boss");
        else if ("affix".equals(cls)) r *= m.get("taken_mob") * m.get("taken_affix");
        else if ("mob".equals(cls)) r *= m.get("taken_mob");
        return r; // share: the weight / share_taken are applied where the circle lands (EmberRunDirector)
    }

    /** A boss telegraph landed and missed this player (in range, outside the shape). */
    public void onDodge(Player p) {
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return;
        long now = System.currentTimeMillis();
        double secs = m.get("dodge_secs");
        if (secs > 0 && m.get("dodge_dmg") != 1.0) {
            dodgeUntil.put(p.getUniqueId(), now + (long) (secs * 1000));
            p.sendActionBar(ChatColor.GOLD + "躲开了！" + String.format(Locale.ROOT, "%.0f", secs) + " 秒内伤害 +" + pct(m.get("dodge_dmg") - 1));
        }
        int db = (int) Math.round(m.get("dodge_burst"));
        if (db > 0 && plugin.getEmberSets() != null && plugin.getEmberSets().primeBurst(p, db))
            p.sendActionBar(ChatColor.GOLD + "借势！烬爆计数 +" + db);
        double heal = m.get("dodge_heal");
        if (heal > 0) {
            Long cd = dodgeHealCd.get(p.getUniqueId());
            if (cd == null || now >= cd) {
                dodgeHealCd.put(p.getUniqueId(), now + (long) (m.get("dodge_icd") * 1000));
                EmberHeal.heal(p, heal * EmberHeal.maxHp(p), "D141 踏步回气 " + pct(heal));
            }
        }
    }

    /** A boss telegraph hit this player (反震: the 烬爆 counter moves forward). */
    public void onTeleHit(Player p) {
        EmberGrowth.Mods m = mods(p);
        int hb = (int) Math.round(m.get("hit_burst"));
        if (hb > 0 && plugin.getEmberSets() != null && plugin.getEmberSets().primeBurst(p, hb))
            p.sendActionBar(ChatColor.GOLD + "反震！烬爆计数 +" + hb);
    }

    public double potionMult(Player p) { return mods(p).get("potion"); }
    public double shareWeight(Player p) { return Math.max(0.1, mods(p).get("share_w")); }
    public double shareTaken(Player p) { return mods(p).get("share_taken"); }

    /** Set tuning for the player's active set: {burstEveryDelta, sustainEveryDelta, burnMult, burstMult, sustainMult, burnSpread} */
    public EmberSetEngine.Tune setTune(Player p) {
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return EmberSetEngine.Tune.NONE;
        return new EmberSetEngine.Tune((int) Math.round(m.get("burst_every")), (int) Math.round(m.get("sustain_every")),
                m.get("burn_mult"), m.get("burst_mult"), m.get("sustain_mult"), (int) Math.round(m.get("burn_ticks")), m.get("burn_spread"), m.get("spread_icd"));
    }

    static String pct(double x) { return String.format(Locale.ROOT, "%.0f%%", x * 100); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        cache.remove(u); dodgeUntil.remove(u); dodgeHealCd.remove(u); pendingRoll.remove(u); rerollBusy.remove(u);
        SPICK.remove(u); SFROM.remove(u); // D174 stage 1.5
    }

    // ------------------------------------------------------------------ commands

    /** /corerpg p1 spec [from <gear|hub> | back | pick <id> [confirm] | reset [confirm] | list] */
    public boolean command(Player p, String[] args) {
        if (talents == null) { p.sendMessage(P + "天赋专精未配置（" + EmberGrowth.FILE + "）"); return true; }
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好，稍后再试"); return true; }
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        boolean confirm = args.length >= 5 && "confirm".equalsIgnoreCase(args[4]) || args.length >= 4 && "confirm".equalsIgnoreCase(args[args.length - 1]);
        if (op.isEmpty()) { FROM.remove(p.getUniqueId()); openMenu(p, MENU); return true; }
        if ("from".equals(op)) {
            String from = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
            if (FROM_MENUS.containsKey(from)) FROM.put(p.getUniqueId(), from); else FROM.remove(p.getUniqueId());
            openMenu(p, MENU);
            return true;
        }
        if ("back".equals(op)) {
            String menu = FROM_MENUS.get(FROM.get(p.getUniqueId()));
            if (menu != null) openMenu(p, menu);
            return true;
        }
        if ("list".equals(op)) { list(p, d); return true; }
        if ("pick".equals(op) && args.length >= 4) return pick(p, d, args[3], confirm);
        if ("reset".equals(op)) return reset(p, d, confirm);
        p.sendMessage(P + "用法：/corerpg p1 spec（打开天赋页）");
        return true;
    }

    private boolean pick(Player p, PlayerData d, String id, boolean confirm) {
        if (gate(p)) return true;
        Map<Integer, String> picks = picks(d);
        String why = EmberGrowth.canPick(talents, picks, id, pointsEarned(d));
        EmberGrowth.Node n = talents.node(id);
        if (why != null) { p.sendMessage(P + "§c" + why); return true; }
        EmberGrowth.Row r = talents.row(n.row);
        boolean learn = !learned(d, n.id);
        int coin = learn ? r.coin : 0;
        if (learn && !confirm) {
            if (d.getCoin() < coin) { p.sendMessage(P + "§c学会「" + n.name + "」要 " + coin + " 余烬币，现有 " + d.getCoin()); return true; }
            p.sendMessage(P + "§6学会并点上「" + n.name + "」§7：占 " + r.points + " 专精点 + 一次性 " + coin + " 余烬币（以后重置再点不收币）");
            p.sendMessage(P + "§a得：" + n.good + " §7· §c失：" + n.bad);
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认学会 · " + coin + " 币]", "/corerpg p1 spec pick " + n.id + " confirm", "扣 " + coin + " 余烬币，点上「" + n.name + "」", "GREEN"},
                    new String[]{"[回天赋页]", "/corerpg p1 spec from " + fromKey(p), "不学，回去看看", "GRAY"});
            return true;
        }
        if (coin > 0 && !d.takeCoin(coin)) { p.sendMessage(P + "§c余烬币不够（要 " + coin + "）"); return true; }
        if (learn) d.addPeriodCount(C_LEARN + n.id, "all", 1);
        int idx = talents.inRow(n.row).indexOf(n) + 1;
        d.addPeriodCount(C_ROW + n.row, "all", idx - d.periodCount(C_ROW + n.row, "all"));
        epoch++;
        runs.flushData(p.getUniqueId());
        plugin.getLogger().info("[P1 growth] " + p.getName() + " picked " + n.id + (coin > 0 ? " (learned, " + coin + " coin)" : ""));
        String gated = n.activeWith(runs.loadouts().get(p).activeSet) ? "" : " §e（要穿" + EmberItemData.familyName(n.set) + "套才生效）";
        p.sendMessage(P + "§a已点上「" + n.name + "」§7（第 " + n.row + " 排 · " + r.name + "）" + gated);
        openMenu(p, MENU);
        return true;
    }

    private boolean reset(Player p, PlayerData d, boolean confirm) {
        if (gate(p)) return true;
        Map<Integer, String> picks = picks(d);
        if (EmberGrowth.pointsUsed(talents, picks) == 0) { p.sendMessage(P + "还没点任何天赋，不用重置"); return true; }
        int used = d.periodCount(C_RESETS, "all");
        int cost = EmberGrowth.respecCost(talents, used);
        if (!confirm) {
            p.sendMessage(P + "§6重置天赋§7：三排全部清空、专精点全退；学会过的天赋以后再点不收币。"
                    + (cost == 0 ? "§a这次免费（第一次）" : "§7这次 §f" + cost + " §7余烬币（第一次之后每次都收）"));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认重置" + (cost == 0 ? " · 免费" : " · " + cost + " 币") + "]", "/corerpg p1 spec reset confirm", "清空三排、退回专精点", "RED"},
                    new String[]{"[回天赋页]", "/corerpg p1 spec from " + fromKey(p), "不重置", "GRAY"});
            return true;
        }
        if (cost > 0 && d.getCoin() < cost) { p.sendMessage(P + "§c重置要 " + cost + " 余烬币，现有 " + d.getCoin()); return true; }
        if (cost > 0 && !d.takeCoin(cost)) { p.sendMessage(P + "§c扣币失败"); return true; }
        for (EmberGrowth.Row r : talents.rows) d.addPeriodCount(C_ROW + r.row, "all", -d.periodCount(C_ROW + r.row, "all"));
        d.addPeriodCount(C_RESETS, "all", 1);
        epoch++;
        runs.flushData(p.getUniqueId());
        plugin.getLogger().info("[P1 growth] " + p.getName() + " respec #" + (used + 1) + " cost " + cost);
        p.sendMessage(P + "§a天赋已重置§7（专精点全退" + (cost > 0 ? "，扣 " + cost + " 币" : "，这次免费") + "）");
        openMenu(p, MENU);
        return true;
    }

    /** picks only in town (same rule as the forge): no mid-fight swaps */
    private boolean gate(Player p) {
        if (EmberMode.isP1World(p.getWorld()) || runs.isRunWorld(p.getWorld())) {
            p.sendMessage(P + "§c请回城后再点天赋（副本里不能改）");
            return true;
        }
        return false;
    }

    private String fromKey(Player p) { String f = FROM.get(p.getUniqueId()); return f == null ? "none" : f; }

    private void list(Player p, PlayerData d) {
        Map<Integer, String> picks = picks(d);
        p.sendMessage(P + "§6天赋专精 §7· " + pointsLine(d));
        for (EmberGrowth.Row r : talents.rows) {
            String cur = picks.get(r.row);
            p.sendMessage(P + "第 " + r.row + " 排「" + r.name + "」：" + (cur == null ? "§8空" : "§a" + talents.node(cur).name));
        }
    }

    void openMenu(Player p, String menu) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName());
        });
    }

    // ------------------------------------------------------------------ honors (D142)

    public EmberGrowth.Honors honors() { return honors; }

    /** one honor reached? (all conditions are existing one-time achievements) */
    public boolean honorDone(PlayerData d, EmberGrowth.Honor h) {
        if (d == null || h == null) return false;
        if (d.periodCount(C_HONOR_TEST + h.id, "all") > 0) return true; // admin test grant
        int n;
        try { n = h.arg == null || h.arg.trim().isEmpty() ? 0 : Integer.parseInt(h.arg.trim()); } catch (NumberFormatException e) { n = 0; }
        if ("abyss_floor".equals(h.kind)) return runs.abyssBest(d) >= n;
        if ("challenge_count".equals(h.kind)) {
            int c = 0;
            for (String k : d.getCounters().keySet()) if (k.startsWith(C_CHAL) && k.endsWith("@all")) c++;
            return c >= n;
        }
        if ("raid_count".equals(h.kind)) {
            int c = 0;
            for (String r : new String[]{"r01", "r02", "r03"}) if (EmberCosmetics.raidClears(d, r) > 0) c++;
            return c >= n;
        }
        if ("codex_full".equals(h.kind)) return EmberCodex.count(d) >= EmberCodex.size();
        return false;
    }

    /** earned honor ids (cached 5 s per player; the menu / settlement call {@link #refreshHonors}) */
    public List<String> honorsEarned(UUID u, PlayerData d) {
        if (honors == null || d == null) return java.util.Collections.emptyList();
        Object[] c = honorCache.get(u);
        long now = System.currentTimeMillis();
        if (c != null && (Long) c[0] > now) {
            @SuppressWarnings("unchecked") List<String> l = (List<String>) c[1];
            return l;
        }
        List<String> l = new ArrayList<String>();
        for (EmberGrowth.Honor h : honors.list) if (honorDone(d, h)) l.add(h.id);
        honorCache.put(u, new Object[]{now + 5000L, l});
        return l;
    }

    /** after a settlement / on join: recompute and send the one-time unlock notice for new honors */
    public void refreshHonors(Player p) {
        if (honors == null || p == null) return;
        PlayerData d = data(p.getUniqueId());
        if (d == null) return;
        honorCache.remove(p.getUniqueId());
        for (String id : honorsEarned(p.getUniqueId(), d)) {
            if (d.periodCount(C_HONOR_SEEN + id, "all") > 0) continue;
            d.addPeriodCount(C_HONOR_SEEN + id, "all", 1);
            EmberGrowth.Honor h = honors.byId(id);
            p.sendMessage(P + "§6获得余烬勋记「" + h.name + "」§7— " + h.desc + " §8（账号永久，/corerpg p1 honor 查看）");
            plugin.getLogger().info("[P1 growth] " + p.getName() + " honor " + id);
        }
    }

    /** settlement coin bonus for these coins (0 when no honor) */
    public int honorCoin(Player p, int coins) {
        if (p == null || coins <= 0) return 0;
        double m = mods(p).get("coin");
        return m <= 1.0 ? 0 : (int) Math.round(coins * (m - 1.0));
    }

    public int honorShard(Player p) { return p == null ? 0 : (int) Math.round(mods(p).get("shard_bonus")); }

    /** abyss segment fee after 深渊行者 */
    public int abyssFee(Player p, int fee) {
        if (p == null || fee <= 0) return fee;
        return (int) Math.round(fee * mods(p).get("abyss_fee"));
    }

    public boolean honorCommand(Player p, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if ("from".equals(op)) {
            String from = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
            if (HONOR_FROM.containsKey(from)) HFROM.put(p.getUniqueId(), from); else HFROM.remove(p.getUniqueId());
        } else if ("back".equals(op)) {
            String menu = HONOR_FROM.get(HFROM.get(p.getUniqueId()));
            if (menu != null) openMenu(p, menu);
            return true;
        } else if ("list".equals(op)) {
            PlayerData d = data(p.getUniqueId());
            if (honors == null || d == null) { p.sendMessage(P + "余烬勋记未配置"); return true; }
            List<String> e = honorsEarned(p.getUniqueId(), d);
            p.sendMessage(P + "§6余烬勋记 §7" + e.size() + "/" + honors.list.size());
            for (EmberGrowth.Honor h : honors.list) p.sendMessage(P + (e.contains(h.id) ? "§a✔ " : "§8✘ ") + h.name + " §7— " + h.desc);
            return true;
        } else {
            HFROM.remove(p.getUniqueId());
        }
        refreshHonors(p);
        openMenu(p, HONOR_MENU);
        return true;
    }

    /**
     * Admin: /corerpg p1 honor test <id|kind|kind:arg|all> [玩家] — grant the condition (then the normal unlock notice fires);
     * honor test clear [玩家] — drop every test grant and the "seen" marks so the notice can fire again; honor test show [玩家].
     */
    public boolean honorTest(org.bukkit.command.CommandSender s, String[] args) {
        if (honors == null) { s.sendMessage(P + "余烬勋记未配置"); return true; }
        String w = args.length >= 4 ? args[3] : "show";
        Player t = args.length >= 5 ? Bukkit.getPlayerExact(args[4]) : (s instanceof Player ? (Player) s : null);
        if (t == null) { s.sendMessage(P + "/corerpg p1 honor test <勋记id|条件|all|clear|show> [在线玩家]"); return true; }
        PlayerData d = data(t.getUniqueId());
        if (d == null) { s.sendMessage(P + "数据还没加载好"); return true; }
        if ("clear".equalsIgnoreCase(w)) {
            for (EmberGrowth.Honor h : honors.list) {
                d.addPeriodCount(C_HONOR_TEST + h.id, "all", -d.periodCount(C_HONOR_TEST + h.id, "all"));
                d.addPeriodCount(C_HONOR_SEEN + h.id, "all", -d.periodCount(C_HONOR_SEEN + h.id, "all"));
            }
        } else if (!"show".equalsIgnoreCase(w)) {
            List<String> ids = EmberGrowth.honorMatch(honors, w);
            if (ids.isEmpty()) { s.sendMessage(P + "没有这个勋记 / 条件：" + w + "（id 或 abyss_floor:5 / challenge_count:3 / raid_count:1 / codex_full / all）"); return true; }
            for (String id : ids) d.addPeriodCount(C_HONOR_TEST + id, "all", 1 - d.periodCount(C_HONOR_TEST + id, "all"));
        }
        epoch++;
        refreshHonors(t); // sends the one-time unlock notice for newly met honors
        runs.flushData(t.getUniqueId());
        List<String> e = honorsEarned(t.getUniqueId(), d);
        Map<String, Double> tot = EmberGrowth.honorParts(honors, e);
        s.sendMessage(P + t.getName() + " 勋记 " + e.size() + "/" + honors.list.size() + " " + e + " · 合计（封顶后）："
                + (tot.isEmpty() ? "无" : EmberGrowth.describeCn(tot)) + " · raw " + tot);
        plugin.getLogger().info("[P1 growth] admin " + s.getName() + " honor test " + w + " " + t.getName() + " → " + e + " " + tot);
        return true;
    }

    static final Map<String, String> HONOR_FROM = new LinkedHashMap<String, String>();
    static {
        HONOR_FROM.put("gear", "ember_p1_gear");
        HONOR_FROM.put("hub", "ember_hub");
        HONOR_FROM.put("spec", "ember_p1_spec");
    }

    /** key after "honor_" */
    public String honorPapi(Player p, PlayerData d, String key) {
        if (honors == null || d == null) return "";
        List<String> e = honorsEarned(p.getUniqueId(), d);
        if ("n".equals(key)) return "§7已得 §f" + e.size() + "§7/" + honors.list.size();
        if ("back".equals(key)) {
            String f = HFROM.get(p.getUniqueId());
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "spec".equals(f) ? "§7返回天赋页" : "§7关闭";
        }
        if ("total".equals(key)) {
            Map<String, Double> m = EmberGrowth.honorParts(honors, e);
            return m.isEmpty() ? "§8还没有勋记效果" : "§a" + EmberGrowth.describeCn(m);
        }
        if (key.startsWith("name_") || key.startsWith("st_") || key.startsWith("desc_")) {
            int i;
            try { i = Integer.parseInt(key.substring(key.indexOf('_') + 1)); } catch (NumberFormatException ex) { return ""; }
            if (i < 1 || i > honors.list.size()) return "";
            EmberGrowth.Honor h = honors.list.get(i - 1);
            boolean on = e.contains(h.id);
            if (key.startsWith("name_")) return (on ? "§a✔ " : "§8✘ ") + h.name;
            if (key.startsWith("desc_")) return (on ? "§f" : "§7") + h.desc;
            return on ? "§a已获得（账号永久）" : "§7条件：" + condText(h);
        }
        return "";
    }

    static String condText(EmberGrowth.Honor h) {
        if ("abyss_floor".equals(h.kind)) return "深渊·余烬层最高到第 " + h.arg + " 层";
        if ("challenge_count".equals(h.kind)) return "首通任意 " + h.arg + " 张挑战版";
        if ("raid_count".equals(h.kind)) return "3".equals(h.arg) ? "首通全部三个团本" : "首通任意 " + h.arg + " 个团本";
        if ("codex_full".equals(h.kind)) return "图录全部点亮";
        return h.kind;
    }

    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) refreshHonors(p); }, 100L);
        // D172: a reroll paid before a disconnect / crash — after EmberDelivery has reconciled the refund holds
        Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) recoverRolls(p, 0); }, 120L);
    }

    // ------------------------------------------------------------------ placeholders (%corerpg_p1_spec_*%)

    String pointsLine(PlayerData d) {
        int earned = pointsEarned(d), used = EmberGrowth.pointsUsed(talents, picks(d));
        return "专精点 §f" + earned + "§7/" + talents.maxPoints() + " · 已用 §f" + used + " §7· 空 §f" + (earned - used);
    }

    /** key after "spec_" */
    public String papi(Player p, PlayerData d, String key) {
        if (talents == null || d == null) return "";
        if ("pts".equals(key)) return "§7" + pointsLine(d);
        if (key.startsWith("src_")) {
            int i;
            try { i = Integer.parseInt(key.substring(4)); } catch (NumberFormatException e) { return ""; }
            if (i < 1 || i > talents.points.size()) return "";
            EmberGrowth.PointSrc ps = talents.points.get(i - 1);
            return sourceDone(d, ps) ? "§a✔ " + ps.name + " §7+1" : "§8✘ " + ps.name;
        }
        if ("reset".equals(key)) {
            int cost = EmberGrowth.respecCost(talents, d.periodCount(C_RESETS, "all"));
            return cost == 0 ? "§a下次重置免费（第一次）" : "§7下次重置 §f" + cost + " §7余烬币";
        }
        if ("back".equals(key)) {
            String f = FROM.get(p.getUniqueId());
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "§7关闭";
        }
        if (key.startsWith("row_")) {
            int r;
            try { r = Integer.parseInt(key.substring(4)); } catch (NumberFormatException e) { return ""; }
            EmberGrowth.Row row = talents.row(r);
            return row == null ? "" : "§6第 " + r + " 排 · " + row.name + " §7（" + row.points + " 点 · 学会 " + row.coin + " 币 · " + row.theme + "）";
        }
        if (key.startsWith("st_")) return status(p, d, key.substring(3));
        if (key.startsWith("good_")) { EmberGrowth.Node n = talents.node(key.substring(5)); return n == null ? "" : "§a得 · " + n.good; }
        if (key.startsWith("bad_")) { EmberGrowth.Node n = talents.node(key.substring(4)); return n == null ? "" : "§c失 · " + n.bad; }
        if (key.startsWith("fit_")) { EmberGrowth.Node n = talents.node(key.substring(4)); return n == null || n.fit.isEmpty() ? "" : "§e" + n.fit; } // D147
        if (key.startsWith("name_")) {
            EmberGrowth.Node n = talents.node(key.substring(5));
            if (n == null) return "";
            boolean on = n.id.equals(picks(d).get(n.row));
            return (on ? "§a✔ " : "§6") + n.name + " §8（" + EmberItemData.familyName(n.family) + "向" + (n.set != null && !n.set.isEmpty() ? " · 只在" + EmberItemData.familyName(n.set) + "成套时" : "") + "）";
        }
        return "";
    }

    String status(Player p, PlayerData d, String id) {
        EmberGrowth.Node n = talents.node(id);
        if (n == null) return "";
        Map<Integer, String> picks = picks(d);
        EmberGrowth.Row r = talents.row(n.row);
        if (n.id.equals(picks.get(n.row))) {
            boolean on = n.activeWith(runs.loadouts().get(p).activeSet);
            return on ? "§a✔ 已点上（生效中）" : "§e✔ 已点上 · 现在没穿" + EmberItemData.familyName(n.set) + "套，不生效";
        }
        String why = EmberGrowth.canPick(talents, picks, id, pointsEarned(d));
        if (why != null) {
            if (picks.get(n.row) != null) return "§8这排已点「" + talents.node(picks.get(n.row)).name + "」· 想换先重置";
            return "§8" + why;
        }
        return learned(d, n.id) ? "§e➥ 点击点上 §7（" + r.points + " 点，已学会不收币）" : "§e➥ 点击学会并点上 §7（" + r.points + " 点 + " + r.coin + " 币）";
    }

    // ------------------------------------------------------------------ affix reroll (D143)

    static final String C_AF = "p4_af_";    // + item uid, period all: affix code * 10 + tier (0 = empty)
    static final String C_AFP = "p4_afp_";  // + item uid, period all: tries in a row below the quality cap (pity)
    public static final String REROLL_MENU = "ember_p1_reroll";
    private static final Map<UUID, String> RFROM = new ConcurrentHashMap<UUID, String>();
    /** uuid → {item uid, slot, encoded candidate, old encoded} — the roll waiting for 保留新 / 保留旧 (not chosen = old) */
    private final Map<UUID, Object[]> pendingRoll = new ConcurrentHashMap<UUID, Object[]>();
    private final java.util.Set<UUID> rerollBusy = java.util.Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    static final Map<String, String> REROLL_FROM = new LinkedHashMap<String, String>();
    static {
        REROLL_FROM.put("gear", "ember_p1_gear");
        REROLL_FROM.put("hub", "ember_hub");
    }

    public EmberAffix.Rules reroll() { return reroll; }

    public int affixOf(PlayerData d, String uid) { return d == null || uid == null ? 0 : d.periodCount(C_AF + uid, "all"); }

    private int[][] affixItems(PlayerData d, EmberLoadout lo) {
        if (reroll == null || lo == null) return new int[0][];
        return new int[][]{lo.blade == null ? null : new int[]{affixOf(d, lo.blade.uid), lo.blade.quality},
                lo.charm == null ? null : new int[]{affixOf(d, lo.charm.uid), lo.charm.quality}};
    }

    // ------------------------------------------------------------------ D174 签名传奇

    /** signature code on this uid (0 = none) */
    public int sigOf(PlayerData d, String uid) { return d == null || uid == null ? 0 : d.periodCount(EmberSignature.C_SIG + uid, "all"); }

    /** D174: an affix or a signature = invested (never a reroll duplicate, skipped by bulk dismantle) */
    public boolean invested(PlayerData d, String uid) { return affixOf(d, uid) > 0 || sigOf(d, uid) > 0; }

    EmberSignature.Worn worn(PlayerData d, EmberItemData it) {
        return it == null ? null : new EmberSignature.Worn(EmberSignature.byCode(sigOf(d, it.uid)), it.slot, it.family);
    }

    /** D174 stage 1.5: the player switched this slot's signature off in the menu (period all, 1 = off) */
    static final String C_SIGOFF = "p1_sigoff_";
    /** D174 stage 1.5: this signature was ever stamped / imprinted for this player (period all; codex 「获得过」) */
    static final String C_SIGSEEN = "p1_sigseen_";

    public boolean sigOff(PlayerData d, String slot) { return d != null && d.periodCount(C_SIGOFF + slot, "all") > 0; }

    /** worn piece as the rules see it: a slot switched off counts as "no signature" (so it never blocks the other slot) */
    private EmberSignature.Worn wornOn(PlayerData d, EmberItemData it) {
        if (it == null) return null;
        if (sigOff(d, it.slot)) return new EmberSignature.Worn(null, it.slot, it.family);
        return worn(d, it);
    }

    /** codex: has this player ever had {@code sd} (seen counter, or any item uid carrying it on the save) */
    public boolean sigSeen(PlayerData d, EmberSignature.Def sd) {
        if (d == null || sd == null) return false;
        if (d.periodCount(C_SIGSEEN + sd.id, "all") > 0) return true;
        for (Map.Entry<String, Integer> e : d.getCounters().entrySet()) {
            String k = e.getKey();
            if (k.startsWith(EmberSignature.C_SIG) && k.endsWith("@all") && e.getValue() != null && e.getValue() == sd.code) return true;
        }
        return false;
    }

    /** mark seen (called by the settlement stamp and by imprint; the caller saves) */
    public static void markSeen(PlayerData d, EmberSignature.Def sd) {
        if (d != null && sd != null && d.periodCount(C_SIGSEEN + sd.id, "all") <= 0) d.addPeriodCount(C_SIGSEEN + sd.id, "all", 1);
    }

    /** the active signatures of this loadout (≤ 2; see EmberSignature.active) */
    public List<EmberSignature.Def> signatures(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null) return Collections.<EmberSignature.Def>emptyList();
        EmberSignature.Worn b = wornOn(d, lo.blade), c = wornOn(d, lo.charm); // stage 1.5: menu toggle
        if ((b == null || b.def == null) && (c == null || c.def == null)) return Collections.<EmberSignature.Def>emptyList();
        return EmberSignature.active(b, c, lo.activeSet, picks(d).values(), runs.progressFlag(d, EmberSignature.DUAL_UNLOCK));
    }

    private String mapLabel(String key) {
        EmberRunMaps.MapDef md = runs.maps() == null ? null : runs.maps().byKey(key);
        return key.toUpperCase(Locale.ROOT) + (md == null ? "" : " " + md.name);
    }

    private String wornLine(PlayerData d, EmberLoadout lo, String slot) {
        EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
        if (it == null) return "§7" + EmberItemData.slotName(slot) + "：§8没有";
        EmberSignature.Worn w = worn(d, it);
        if (w.def == null) return "§7" + EmberItemData.slotName(slot) + "：§f" + it.shortLabel() + " §8（无签名）";
        String why = sigWhy(d, lo, slot);
        return "§7" + EmberItemData.slotName(slot) + "：§f" + it.shortLabel() + " §6" + w.def.name + (why == null ? " §a生效" : " §c不生效：" + why);
    }

    /** why the worn {@code slot} signature is off (null = on; "无" = no signature) */
    String sigWhy(PlayerData d, EmberLoadout lo, String slot) {
        EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
        if (it == null) return "无";
        EmberSignature.Worn w = worn(d, it);
        if (w.def == null) return "无";
        if (sigOff(d, slot)) return "你在签名页关掉了";
        return "blade".equals(slot) ? EmberSignature.offReason(w, null, lo.activeSet, picks(d).values(), true)
                : EmberSignature.offReason(w, wornOn(d, lo.blade), lo.activeSet, picks(d).values(), runs.progressFlag(d, EmberSignature.DUAL_UNLOCK));
    }

    /**
     * D174 /corerpg p1 sig — 签名图鉴 + 徽记 + 生效情况 | imprint &lt;Lxx&gt; [confirm tok:…] 烬炉烙印 |
     * test &lt;marks map n | stamp blade|charm Lxx | clear&gt; [player] (corerpg.admin, smoke tests)
     */
    public boolean sigCommand(org.bukkit.command.CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if ("test".equals(op)) return sigTest(s, args);
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好，稍后再试"); return true; }
        EmberLoadout lo = runs.loadouts().get(p);
        if ("imprint".equals(op) || "烙印".equals(op)) return imprint(p, d, lo, args);
        // ---- stage 1.5 menu ops (players never need to type these: every one is a menu button)
        if (op.isEmpty() || "menu".equals(op) || "from".equals(op)) { // stage 1.5: bare /corerpg p1 sig opens the page; "list" = chat codex
            String from = "from".equals(op) && args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
            if (SIG_FROM.containsKey(from)) SFROM.put(p.getUniqueId(), from);
            else if (!"menu".equals(op)) SFROM.remove(p.getUniqueId()); // "menu" = back from the imprint page: keep the opener
            openMenu(p, SIG_MENU);
            return true;
        }
        if ("back".equals(op)) {
            String menu = SIG_FROM.get(SFROM.get(p.getUniqueId()));
            if (menu != null) openMenu(p, menu);
            return true;
        }
        if ("toggle".equals(op)) return sigToggle(p, d, lo, args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "");
        if ("pick".equals(op)) return sigPick(p, d, args.length >= 4 ? args[3] : null);
        if ("target".equals(op)) {
            String[] pk = SPICK.get(p.getUniqueId());
            if (pk == null) { openMenu(p, SIG_MENU); return true; }
            SPICK.put(p.getUniqueId(), new String[]{pk[0], args.length >= 4 && "held".equalsIgnoreCase(args[3]) ? "held" : "worn"});
            openMenu(p, SIG_IMP_MENU);
            return true;
        }
        if ("ui".equals(op)) return sigUiConfirm(p, d, lo, args.length >= 5 && "overwrite".equalsIgnoreCase(args[4]));
        if (!"list".equals(op)) { p.sendMessage(P + "用法：/corerpg p1 sig（打开签名页）· list（聊天栏图鉴）"); return true; }
        boolean dual = runs.progressFlag(d, EmberSignature.DUAL_UNLOCK), imp = runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK);
        p.sendMessage(P + "§6签名传奇§7（每张主线图首领自己的效果；同时最多 " + (dual ? 2 : 1) + " 条"
                + (dual ? "" : "，首通 " + EmberSignature.DUAL_UNLOCK.toUpperCase(Locale.ROOT) + " 后 2 条") + "；同类不叠）");
        p.sendMessage(P + wornLine(d, lo, "blade"));
        p.sendMessage(P + wornLine(d, lo, "charm"));
        for (String mk : EmberSignature.maps()) {
            boolean cl = runs.progressFlag(d, mk);
            p.sendMessage(P + "§e" + mapLabel(mk) + " §7· 首领徽记 §f" + d.periodCount(EmberSignature.C_MARK + mk, "all")
                    + (cl ? "" : " §8（未首通：首通后重打才掉签名和徽记）"));
            for (EmberSignature.Def sd : EmberSignature.forMap(mk)) {
                String line = P + "  §6" + sd.name + " §7" + sd.kindText() + "：§f" + sd.good + " §7/ 代价：" + sd.bad
                        + (!sd.excl.isEmpty() && picks(d).values().contains(sd.excl) ? " §c（与已选天赋同类，不生效）" : "");
                if (cl && imp) town.sunshine.corerpg.ConfirmTokens.sendClick(p, line + " ", "[烙印]", "/corerpg p1 sig imprint " + sd.id,
                        "把「" + sd.name + "」烙到正在用的" + EmberItemData.slotName(sd.slot) + "上\n" + EmberSignature.IMPRINT_MARKS + " 枚 "
                                + mk.toUpperCase(Locale.ROOT) + " 首领徽记 + " + EmberSignature.IMPRINT_COIN_PER_TIER + "×阶级 余烬币；覆盖原来的签名");
                else p.sendMessage(line);
            }
        }
        p.sendMessage(P + "§7来源：已首通的图普通重打每局 +1 徽记，基础掉落 " + Math.round(EmberSignature.STAMP_RATE * 100) + "% 是这张图的签名件；首通给 "
                + EmberSignature.FC_MARKS + " 枚。" + (imp ? "" : "§8烬炉烙印在首通 " + EmberSignature.IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放。"));
        return true;
    }

    private boolean imprint(Player p, PlayerData d, EmberLoadout lo, String[] args) {
        EmberSignature.Def sd = EmberSignature.byId(args.length >= 4 ? args[3] : null);
        if (sd == null) { p.sendMessage(P + "用法：/corerpg p1 sig imprint L01（先 /corerpg p1 sig 看图鉴）"); return true; }
        String hold = EmberAssetGuard.hold(p);
        if (hold != null) { p.sendMessage(P + ChatColor.RED + hold); return true; }
        if (inRun(p)) { p.sendMessage(P + ChatColor.RED + "请回城后烙印"); return true; }
        EmberItemData t = "blade".equals(sd.slot) ? lo.blade : lo.charm;
        int marks = d.periodCount(EmberSignature.C_MARK + sd.map, "all");
        int cur = t == null ? 0 : sigOf(d, t.uid);
        String why = EmberSignature.imprintCheck(sd, t, runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK), runs.progressFlag(d, sd.map), marks, d.getCoin(), cur);
        if (why != null) { p.sendMessage(P + ChatColor.RED + why); return true; }
        int coin = EmberSignature.imprintCoin(t.tier);
        String fp = t.uid + "|" + sd.id + "|" + cur;
        boolean go = "confirm".equalsIgnoreCase(args.length >= 5 ? args[4] : "");
        if (go && cur > 0) { // overwriting a signature destroys it → the one-shot token from the preview (like 分解)
            String tok = args.length >= 6 && args[5].startsWith("tok:") ? args[5].substring(4) : "";
            String bad = town.sunshine.corerpg.ConfirmTokens.consume(p, "p1sig", tok, fp);
            if (bad != null) { p.sendMessage(P + ChatColor.RED + bad); go = false; }
        }
        EmberSignature.Def old = EmberSignature.byCode(cur);
        if (!go) {
            p.sendMessage(P + "烬炉烙印：把 §6" + sd.name + " §7烙到 §f" + t.shortLabel() + " §7· 花 " + EmberSignature.IMPRINT_MARKS + " 枚 "
                    + sd.map.toUpperCase(Locale.ROOT) + " 首领徽记（有 " + marks + "）+ " + coin + " 余烬币");
            if (old != null) p.sendMessage(P + ChatColor.RED + "⚠ 这件现在的签名「" + old.name + "」会被覆盖，不能取回");
            String tk = town.sunshine.corerpg.ConfirmTokens.issue(p, "p1sig", fp);
            town.sunshine.corerpg.ConfirmTokens.sendClick(p, P + "确认：", "[确认烙印]", "/corerpg p1 sig imprint " + sd.id + " confirm tok:" + tk,
                    sd.name + "：" + sd.good + "\n代价：" + sd.bad);
            return true;
        }
        doImprint(p, d, lo, sd, t, cur, coin, "chat");
        return true;
    }

    private boolean inRun(Player p) { return EmberMode.isP1World(p.getWorld()) || runs.isRunWorld(p.getWorld()); }

    /** one PlayerData mutation (insignia + coins + the signature + seen), then one checked save — never half applied */
    private boolean doImprint(Player p, PlayerData d, EmberLoadout lo, EmberSignature.Def sd, EmberItemData t, int cur, int coin, String via) {
        if (!d.takeCoin(coin)) { p.sendMessage(P + ChatColor.RED + "余烬币不够"); return false; }
        EmberSignature.Def old = EmberSignature.byCode(cur);
        d.addPeriodCount(EmberSignature.C_MARK + sd.map, "all", -EmberSignature.IMPRINT_MARKS);
        d.addPeriodCount(EmberSignature.C_SIG + t.uid, "all", sd.code - cur);
        markSeen(d, sd);
        boolean saved = plugin.getDataStore().flushMutationChecked(p.getUniqueId());
        plugin.getLogger().info("[P1 sig] " + p.getName() + " imprint " + t.uid + " " + (old == null ? "-" : old.id) + " → " + sd.id
                + " (-" + EmberSignature.IMPRINT_MARKS + " " + sd.map + " marks, -" + coin + " coin, via " + via + ", saved=" + saved + ")");
        p.sendMessage(P + "§a烙印完成：§f" + t.shortLabel() + " §7→ §6" + sd.name + "§7（剩 " + d.periodCount(EmberSignature.C_MARK + sd.map, "all") + " 枚徽记）"
                + (saved ? "" : " §e（存档稍后重试）"));
        boolean worn = "blade".equals(sd.slot) ? lo.blade != null && lo.blade.uid.equals(t.uid) : lo.charm != null && lo.charm.uid.equals(t.uid);
        if (!worn) p.sendMessage(P + "§e注意：这件现在没在用（选定 / 手持后才生效）");
        else {
            String off = sigWhy(d, lo, sd.slot);
            if (off != null) p.sendMessage(P + "§e注意：现在不生效——" + off);
        }
        return true;
    }

    // ------------------------------------------------------------------ D174 stage 1.5: TrMenu pages (no commands for players)

    public static final String SIG_MENU = "ember_p1_sig", SIG_IMP_MENU = "ember_p1_sig_imprint";
    static final Map<String, String> SIG_FROM = new LinkedHashMap<String, String>();
    static {
        SIG_FROM.put("gear", "ember_p1_gear");
        SIG_FROM.put("hub", "ember_hub");
    }
    private static final Map<UUID, String> SFROM = new ConcurrentHashMap<UUID, String>();
    /** uuid → {signature id, "worn" | "held"} — the imprint page's current choice */
    private static final Map<UUID, String[]> SPICK = new ConcurrentHashMap<UUID, String[]>();

    private boolean sigToggle(Player p, PlayerData d, EmberLoadout lo, String slot) {
        if (!"blade".equals(slot) && !"charm".equals(slot)) { openMenu(p, SIG_MENU); return true; }
        if (inRun(p)) { p.sendMessage(P + ChatColor.RED + "请回城后再开关签名（副本里不能改）"); return true; }
        boolean off = sigOff(d, slot);
        d.addPeriodCount(C_SIGOFF + slot, "all", off ? -1 : 1);
        runs.flushData(p.getUniqueId());
        plugin.getLogger().info("[P1 sig] " + p.getName() + " toggle " + slot + " → " + (off ? "on" : "off"));
        p.sendMessage(P + EmberItemData.slotName(slot) + "上的签名" + (off ? "§a已打开" : "§e已关闭§7（随时可以再打开；不影响掉落和烙印）"));
        openMenu(p, SIG_MENU);
        return true;
    }

    private boolean sigPick(Player p, PlayerData d, String id) {
        EmberSignature.Def sd = EmberSignature.byId(id);
        if (sd == null) { openMenu(p, SIG_MENU); return true; }
        if (!runs.progressFlag(d, sd.map)) {
            p.sendMessage(P + "§7" + sd.name + "：先首通 " + mapLabel(sd.map) + " 才解锁（重打掉落 / 烙印）");
            return true;
        }
        if (!runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK)) {
            p.sendMessage(P + "§7烬炉烙印在首通 " + mapLabel(EmberSignature.IMPRINT_UNLOCK) + " 后开放；现在只能靠重打 " + mapLabel(sd.map) + " 掉签名件");
            return true;
        }
        SPICK.put(p.getUniqueId(), new String[]{sd.id, "worn"});
        openMenu(p, SIG_IMP_MENU);
        return true;
    }

    /** the main-hand P1 piece if it is a trusted item of this player (or null) */
    private EmberItemData heldPiece(Player p) {
        org.bukkit.inventory.ItemStack it = p.getInventory().getItemInMainHand();
        if (it == null || !runs.loadouts().items().hasData(it)) return null;
        EmberItems.Read r = runs.loadouts().items().read(it);
        if (r == null || !r.ok()) return null;
        return runs.loadouts().trust(p, r.data) == null ? r.data : null;
    }

    /** the imprint page target: the worn piece of the signature's slot, or the held piece */
    private EmberItemData pickTarget(Player p, EmberLoadout lo, EmberSignature.Def sd, String which) {
        if ("held".equals(which)) {
            EmberItemData h = heldPiece(p);
            return h != null && sd.slot.equals(h.slot) ? h : null;
        }
        return "blade".equals(sd.slot) ? lo.blade : lo.charm;
    }

    private boolean sigUiConfirm(Player p, PlayerData d, EmberLoadout lo, boolean overwrite) {
        String[] pk = SPICK.get(p.getUniqueId());
        EmberSignature.Def sd = pk == null ? null : EmberSignature.byId(pk[0]);
        if (sd == null) { openMenu(p, SIG_MENU); return true; }
        String hold = EmberAssetGuard.hold(p);
        if (hold != null) { p.sendMessage(P + ChatColor.RED + hold); return true; }
        if (inRun(p)) { p.sendMessage(P + ChatColor.RED + "请回城后烙印"); return true; }
        EmberItemData t = pickTarget(p, lo, sd, pk[1]);
        int cur = t == null ? 0 : sigOf(d, t.uid);
        String why = EmberSignature.imprintCheck(sd, t, runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK), runs.progressFlag(d, sd.map),
                d.periodCount(EmberSignature.C_MARK + sd.map, "all"), d.getCoin(), cur);
        if (why != null) { p.sendMessage(P + ChatColor.RED + why); openMenu(p, SIG_IMP_MENU); return true; }
        if (cur > 0 && !overwrite) { // the page says Shift+点击 for an overwrite; a plain click never destroys a signature
            p.sendMessage(P + ChatColor.RED + "这件已有签名「" + EmberSignature.byCode(cur).name + "」，覆盖请 Shift+点击确认");
            openMenu(p, SIG_IMP_MENU);
            return true;
        }
        if (doImprint(p, d, lo, sd, t, cur, EmberSignature.imprintCoin(t.tier), "menu")) SPICK.remove(p.getUniqueId());
        openMenu(p, SIG_MENU);
        return true;
    }

    private static int idx(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    /** %corerpg_p1_sig_*% (key after "sig_") — the 签名传奇 page and the 烬炉烙印 page */
    public String sigPapi(Player p, PlayerData d, String key) {
        if (p == null || d == null) return "";
        EmberLoadout lo = runs.loadouts().get(p);
        boolean dual = runs.progressFlag(d, EmberSignature.DUAL_UNLOCK), imp = runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK);
        if ("head".equals(key)) {
            int un = 0;
            for (EmberSignature.Def sd : EmberSignature.DEFS) if (runs.progressFlag(d, sd.map)) un++;
            return "§7图鉴已解锁 §f" + un + "§7/" + EmberSignature.DEFS.size() + " §7· 同时生效最多 §f" + (dual ? 2 : 1) + " §7条"
                    + (dual ? "" : " §8（首通 " + EmberSignature.DUAL_UNLOCK.toUpperCase(Locale.ROOT) + " 后 2 条）");
        }
        if ("rules".equals(key)) return imp ? "§7烬炉烙印：§a已开放 §7（点一条签名开始）" : "§7烬炉烙印：§8首通 " + EmberSignature.IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放";
        if ("marks".equals(key)) {
            StringBuilder sb = new StringBuilder("§7首领徽记：");
            for (String mk : EmberSignature.maps()) {
                sb.append("§e").append(mk.toUpperCase(Locale.ROOT)).append(" §f")
                        .append(runs.progressFlag(d, mk) ? String.valueOf(d.periodCount(EmberSignature.C_MARK + mk, "all")) : "§8—").append("  ");
            }
            return sb.toString().trim();
        }
        if ("back".equals(key)) {
            String f = SFROM.get(p.getUniqueId());
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "§7关闭";
        }
        if ("blade".equals(key) || "charm".equals(key)) return wornLine(d, lo, key);
        if (key.startsWith("mn_") || key.startsWith("m_")) { // per map: mn_q01 = name line, m_q01 = first clear + insignia
            String mk = key.substring(key.indexOf('_') + 1).toLowerCase(Locale.ROOT);
            if (!EmberSignature.hasMap(mk)) return "";
            List<EmberSignature.Def> l = EmberSignature.forMap(mk);
            if (key.startsWith("mn_")) return "§6" + mapLabel(mk) + " §7· " + l.get(0).boss;
            if (!runs.progressFlag(d, mk)) return "§8还没首通：首通后重打掉它的签名件 + 首领徽记（首通给 " + EmberSignature.FC_MARKS + " 枚）";
            return "§7首领徽记 §f" + d.periodCount(EmberSignature.C_MARK + mk, "all") + " §7枚 · 普通重打每局 +" + EmberSignature.CLEAR_MARKS
                    + "，基础掉落 " + Math.round(EmberSignature.STAMP_RATE * 100) + "% 是签名件";
        }
        if ("blade_t".equals(key) || "charm_t".equals(key)) {
            String slot = key.substring(0, 5);
            EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
            if (sigOff(d, slot)) return "§e➥ 点击重新打开这条签名";
            if (it == null || sigOf(d, it.uid) <= 0) return "§8这件没有签名（在下面点一条签名可以烙印）";
            return "§e➥ 点击关闭这条签名 §8（有代价时可以先关掉；随时再开）";
        }
        // per signature by index (1 = DEFS[0]): n_ name · k_ kind/map/boss · g_ good · b_ bad · s_ status · a_ action
        int u = key.indexOf('_');
        if (u == 1 && "nkgbsa".indexOf(key.charAt(0)) >= 0) {
            int i = idx(key.substring(2));
            if (i < 1 || i > EmberSignature.DEFS.size()) return "";
            EmberSignature.Def sd = EmberSignature.DEFS.get(i - 1);
            boolean cl = runs.progressFlag(d, sd.map);
            switch (key.charAt(0)) {
                case 'n': return (cl ? "§6" : "§8") + sd.name + (cl ? "" : " §8（未解锁）");
                case 'k': return "§7" + sd.kindText() + " · " + mapLabel(sd.map) + " · " + sd.boss + (sd.anyFamily() ? "" : " §8（只在" + EmberItemData.familyName(sd.family) + "成套时生效）");
                case 'g': return "§a得：§f" + sd.good;
                case 'b': return "§c代价：§7" + sd.bad;
                case 's': return sigStatus(d, lo, sd, cl);
                default:
                    if (!cl) return "§8首通 " + mapLabel(sd.map) + " 后解锁：重打掉签名件和徽记";
                    if (!imp) return "§8重打 " + sd.map.toUpperCase(Locale.ROOT) + " 有机会掉 · 烙印在首通 " + EmberSignature.IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放";
                    return "§e➥ 点击：烙印到自己的" + EmberItemData.slotName(sd.slot) + "上 §7（" + EmberSignature.IMPRINT_MARKS + " 枚徽记 + "
                            + EmberSignature.IMPRINT_COIN_PER_TIER + "×阶级 币）";
            }
        }
        if (key.startsWith("p_")) return impPapi(p, d, lo, key.substring(2));
        return "";
    }

    private String sigStatus(PlayerData d, EmberLoadout lo, EmberSignature.Def sd, boolean cleared) {
        String ex = !sd.excl.isEmpty() && picks(d).values().contains(sd.excl) ? " §c· 与已选天赋同类，不生效" : "";
        for (String slot : new String[]{"blade", "charm"}) {
            EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
            if (it != null && sigOf(d, it.uid) == sd.code) {
                String why = sigWhy(d, lo, slot);
                return why == null ? "§a▶ 穿着 · 生效中" : "§e▶ 穿着 · 不生效：" + why;
            }
        }
        if (sigSeen(d, sd)) return "§b✔ 获得过" + ex;
        return (cleared ? "§7✘ 还没获得" : "§8✘ 未解锁") + ex;
    }

    private String impPapi(Player p, PlayerData d, EmberLoadout lo, String k) {
        String[] pk = SPICK.get(p.getUniqueId());
        EmberSignature.Def sd = pk == null ? null : EmberSignature.byId(pk[0]);
        if (sd == null) return "nok".equals(k) ? "1" : "§8先在签名页点一条签名";
        String which = pk[1];
        EmberItemData t = pickTarget(p, lo, sd, which);
        int cur = t == null ? 0 : sigOf(d, t.uid), marks = d.periodCount(EmberSignature.C_MARK + sd.map, "all");
        String why = EmberSignature.imprintCheck(sd, t, runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK), runs.progressFlag(d, sd.map), marks, d.getCoin(), cur);
        switch (k) {
            case "name": return "§6" + sd.name + " §7（" + sd.kindText() + " · " + mapLabel(sd.map) + "）";
            case "good": return "§a得：§f" + sd.good;
            case "bad": return "§c代价：§7" + sd.bad;
            case "worn": {
                EmberItemData w = "blade".equals(sd.slot) ? lo.blade : lo.charm;
                String lab = w == null ? "§8没有正在用的" + EmberItemData.slotName(sd.slot) : "§f" + w.shortLabel() + sigNow(d, w);
                return ("worn".equals(which) ? "§a▶ 已选 · " : "§7") + lab;
            }
            case "held": {
                EmberItemData h = heldPiece(p);
                String lab = h == null ? "§8手里没有自己的 P1 件" : !sd.slot.equals(h.slot) ? "§8手里是" + EmberItemData.slotName(h.slot) + "，这条只能烙在" + EmberItemData.slotName(sd.slot) + "上"
                        : "§f" + h.shortLabel() + sigNow(d, h);
                return ("held".equals(which) ? "§a▶ 已选 · " : "§7") + lab;
            }
            case "cost": {
                int coin = t == null ? EmberSignature.imprintCoin(1) : EmberSignature.imprintCoin(t.tier);
                return "§7花费：§f" + EmberSignature.IMPRINT_MARKS + " §7枚 " + sd.map.toUpperCase(Locale.ROOT) + " 首领徽记（有 " + (marks >= EmberSignature.IMPRINT_MARKS ? "§a" : "§c") + marks
                        + "§7）+ §f" + coin + " §7余烬币（有 " + (d.getCoin() >= coin ? "§a" : "§c") + d.getCoin() + "§7）" + (t == null ? " §8（按 T1 算）" : "");
            }
            case "warn": {
                EmberSignature.Def old = EmberSignature.byCode(cur);
                return old == null ? "§7这件现在没有签名" : "§c⚠ 会覆盖这件现在的签名「" + old.name + "」，不能取回";
            }
            case "check": return why == null ? "§a条件都满足" : "§c" + why;
            case "go":
                if (why != null) return "§8不能烙印：" + why;
                return cur > 0 ? "§c➥ Shift+点击：确认覆盖并烙印" : "§e➥ 点击：确认烙印";
            case "nok": return why == null ? "0" : "1";
            default: return "";
        }
    }

    private String sigNow(PlayerData d, EmberItemData it) {
        EmberSignature.Def c = EmberSignature.byCode(sigOf(d, it.uid));
        return c == null ? " §8（无签名）" : " §7· 现在：§6" + c.name;
    }

    private boolean sigTest(org.bukkit.command.CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        String w = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
        String usage = P + "/corerpg p1 sig test marks <q01..> <n> [玩家] | stamp <blade|charm> <Lxx> [玩家] | show [玩家] | clear [玩家]";
        int pi = "marks".equals(w) ? 6 : "stamp".equals(w) ? 6 : 4;
        Player t = args.length > pi ? Bukkit.getPlayerExact(args[pi]) : (s instanceof Player ? (Player) s : null);
        if (t == null) { s.sendMessage(usage); return true; }
        PlayerData d = data(t.getUniqueId());
        if (d == null) { s.sendMessage(P + "数据还没加载好"); return true; }
        EmberLoadout lo = runs.loadouts().get(t);
        if ("marks".equals(w) && args.length >= 6) {
            int n;
            try { n = Integer.parseInt(args[5]); } catch (NumberFormatException e) { s.sendMessage(usage); return true; }
            d.addPeriodCount(EmberSignature.C_MARK + args[4].toLowerCase(Locale.ROOT), "all", n);
        } else if ("stamp".equals(w) && args.length >= 6) {
            EmberItemData it = "charm".equalsIgnoreCase(args[4]) ? lo.charm : lo.blade;
            EmberSignature.Def sd = EmberSignature.byId(args[5]);
            if (it == null || sd == null || !EmberSignature.fits(sd, it.slot, it.family)) { s.sendMessage(P + "没有这件 / 签名不合这件"); return true; }
            d.addPeriodCount(EmberSignature.C_SIG + it.uid, "all", sd.code - sigOf(d, it.uid));
        } else if ("show".equals(w)) {
            // read only
        } else if ("clear".equals(w)) {
            for (String mk : EmberSignature.maps()) {
                d.addPeriodCount(EmberSignature.C_MARK + mk, "all", -d.periodCount(EmberSignature.C_MARK + mk, "all"));
            }
            for (EmberItemData it : new EmberItemData[]{lo.blade, lo.charm}) if (it != null) d.addPeriodCount(EmberSignature.C_SIG + it.uid, "all", -sigOf(d, it.uid));
            for (String sl : new String[]{"blade", "charm"}) d.addPeriodCount(C_SIGOFF + sl, "all", -d.periodCount(C_SIGOFF + sl, "all"));
            for (EmberSignature.Def sd : EmberSignature.DEFS) d.addPeriodCount(C_SIGSEEN + sd.id, "all", -d.periodCount(C_SIGSEEN + sd.id, "all"));
        } else { s.sendMessage(usage); return true; }
        runs.flushData(t.getUniqueId());
        s.sendMessage(P + t.getName() + " · " + wornLine(d, lo, "blade") + " §7| " + wornLine(d, lo, "charm"));
        StringBuilder sb = new StringBuilder();
        for (String mk : EmberSignature.maps()) sb.append(mk).append('=').append(d.periodCount(EmberSignature.C_MARK + mk, "all")).append(' ');
        StringBuilder seen = new StringBuilder();
        for (EmberSignature.Def sd : EmberSignature.DEFS) if (sigSeen(d, sd)) seen.append(sd.id).append(' ');
        s.sendMessage(P + "徽记 " + sb + "· off blade=" + sigOff(d, "blade") + " charm=" + sigOff(d, "charm") + " · seen " + seen + "· active "
                + sigKey(signatures(d, lo)) + " · mods " + mods(t));
        return true;
    }

    private static String sigKey(List<EmberSignature.Def> sg) {
        StringBuilder sb = new StringBuilder();
        for (EmberSignature.Def x : sg) sb.append(x.code).append(',');
        return sb.toString();
    }

    private static String afKey(int[][] af) {
        StringBuilder sb = new StringBuilder();
        for (int[] x : af) sb.append(x == null ? "-" : x[0] + ":" + x[1]).append(',');
        return sb.toString();
    }

    /** "余烬 2 档：套装事件（燃烧 / 烬爆）伤害 +2%" (tier clamped to the quality cap) */
    String affixText(int enc, int quality) {
        EmberAffix.Def def = EmberAffix.decodeDef(reroll, enc);
        if (def == null) return "§8空";
        int t = Math.min(EmberAffix.decodeTier(enc), reroll.cap(quality));
        return "§b" + def.name + " " + t + " 档§7：" + def.text(t);
    }

    private EmberItemData target(Player p, String slot) {
        EmberLoadout lo = runs.loadouts().get(p);
        return "charm".equals(slot) ? lo.charm : lo.blade;
    }

    /** inventory index of a usable duplicate for {@code t}, or -1 (the equipped pieces are never taken) */
    private int findDup(Player p, PlayerData d, EmberItemData t) {
        EmberLoadout lo = runs.loadouts().get(p);
        org.bukkit.inventory.ItemStack[] all = p.getInventory().getContents();
        int held = p.getInventory().getHeldItemSlot();
        for (int i = 0; i < Math.min(36, all.length); i++) {
            if (i == held || all[i] == null || !runs.loadouts().items().hasData(all[i])) continue;
            EmberItems.Read r = runs.loadouts().items().read(all[i]);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (lo.blade != null && x.uid.equals(lo.blade.uid) || lo.charm != null && x.uid.equals(lo.charm.uid)) continue;
            if (EmberAffix.duplicateOk(t, x, invested(d, x.uid)) == null) return i; // D174: signature = invested
        }
        return -1;
    }

    /** D159: first eligible gear-library duplicate (unlocked / unfavourited); null when none or lib not ready. */
    private EmberStorageRules.Entry findLibDup(Player p, PlayerData d, EmberItemData t) {
        EmberGearLib lib = EmberGearLib.get();
        if (lib == null) return null;
        return lib.findDupForReroll(p, t, uid -> invested(d, uid)); // D174: signature = invested
    }

    int rerollCoin(Player p, EmberItemData t) {
        return (int) Math.round(reroll.coinFor(t.tier) * mods(p).get("reroll_coin"));
    }

    /** /corerpg p1 reroll [from gear|hub] | back | blade|charm dup|shard [lock] [confirm] | keep new|old */
    public boolean rerollCommand(Player p, String[] args) {
        if (reroll == null) { p.sendMessage(P + "词条洗练未配置（" + EmberGrowth.FILE + "）"); return true; }
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好，稍后再试"); return true; }
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        boolean confirm = "confirm".equalsIgnoreCase(args[args.length - 1]);
        if (op.isEmpty()) { RFROM.remove(p.getUniqueId()); openMenu(p, REROLL_MENU); return true; }
        if ("from".equals(op)) {
            String from = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
            if (REROLL_FROM.containsKey(from)) RFROM.put(p.getUniqueId(), from); else RFROM.remove(p.getUniqueId());
            openMenu(p, REROLL_MENU);
            return true;
        }
        if ("back".equals(op)) {
            String menu = REROLL_FROM.get(RFROM.get(p.getUniqueId()));
            if (menu != null) openMenu(p, menu);
            return true;
        }
        if ("keep".equals(op)) return keep(p, d, args.length >= 4 && "new".equalsIgnoreCase(args[3]));
        if (("blade".equals(op) || "charm".equals(op)) && args.length >= 4) {
            String how = args[3].toLowerCase(Locale.ROOT);
            if (!"dup".equals(how) && !"shard".equals(how)) { p.sendMessage(P + "用法：/corerpg p1 reroll（打开洗练页）"); return true; }
            boolean lock = false;
            for (int i = 4; i < args.length; i++) if ("lock".equalsIgnoreCase(args[i])) lock = true;
            return reroll(p, d, op, "dup".equals(how), lock, confirm);
        }
        p.sendMessage(P + "用法：/corerpg p1 reroll（打开洗练页）");
        return true;
    }

    private boolean reroll(Player p, PlayerData d, String slot, boolean dup, boolean lock, boolean confirm) {
        EmberItemData t = target(p, slot);
        String name = "charm".equals(slot) ? "护符" : "刃";
        if (t == null) { p.sendMessage(P + "§c" + ("charm".equals(slot) ? "还没有选定的护符" : "主手没有余烬刃") + "（洗练的是正在用的那件）"); return true; }
        String why = EmberAffix.eligible(t);
        if (why != null) { p.sendMessage(P + "§c" + why); return true; }
        int coin = rerollCoin(p, t), shard = reroll.shardFor(t.tier);
        int di = dup ? findDup(p, d, t) : -1;
        EmberStorageRules.Entry libDup = (dup && di < 0) ? findLibDup(p, d, t) : null;
        boolean haveDup = di >= 0 || libDup != null;
        int cap = reroll.cap(t.quality), pity = d.periodCount(C_AFP + t.uid, "all");
        // D148 lock: keep the current affix, roll only the tier, for lock_shard extra shards
        EmberAffix.Def cur = EmberAffix.decodeDef(reroll, affixOf(d, t.uid));
        int lockShard = reroll.lockShardFor(t.tier);
        if (lock) {
            if (lockShard <= 0) { p.sendMessage(P + "§c锁定词条没有开放"); return true; }
            if (cur == null) { p.sendMessage(P + "§c词条槽还是空的，没有可锁定的词条（先普通洗一次）"); return true; }
            if (!cur.rollable) { p.sendMessage(P + "§c" + cur.name + " 已移出洗练池，不能锁定再洗（已有的照常生效；普通洗会换成别的词条）"); return true; }
            if (Math.min(EmberAffix.decodeTier(affixOf(d, t.uid)), cap) >= cap) { p.sendMessage(P + "§c" + cur.name + " 已经是这件成色的上限档（" + cap + " 档），锁定再洗没有意义"); return true; }
        }
        int needShard = (dup ? 0 : shard) + (lock ? lockShard : 0);
        String mode = (dup ? "dup" : "shard") + (lock ? " lock" : "");
        if (!confirm) {
            p.sendMessage(P + "§6词条洗练 · " + t.shortLabel());
            p.sendMessage(P + "现在：" + affixText(affixOf(d, t.uid), t.quality) + " §7· 成色" + EmberItemData.qualityName(t.quality) + "最高 §f" + cap + " §7档 · 保底 §f" + pity + "/" + reroll.pity
                    + (pity >= reroll.pity ? " §a（这次必出 " + cap + " 档）" : ""));
            p.sendMessage(P + "档位概率：" + oddsText(cap));
            p.sendMessage(P + (lock ? "§a锁定词条：§f" + cur.name + " §7不变，只重抽档位" : "词条：本部位 " + reroll.pool(slot).size() + " 个里随机（保底只保档位，不保词条）"));
            String dupWhere = !dup ? "" : di >= 0 ? " §a（背包）" : libDup != null ? " §a（装备库）"
                    : " §c（背包或装备库里没有：要掉落来的、没强化 / 成色 / 精工 / 词条、未锁定/收藏的 T" + t.tier + name + "）§7";
            String pay = (dup ? "一件同部位同阶的重复件" + dupWhere
                    : shard + " 余烬碎片") + (lock ? " + 锁定 " + lockShard + " 碎片" : "") + " + " + coin + " 余烬币";
            p.sendMessage(P + "花费：" + pay);
            if (dup && !haveDup) { p.sendMessage(P + "§7没有重复件时可以改用碎片：" + (shard + (lock ? lockShard : 0)) + " 碎片 + " + coin + " 币"); return true; }
            List<String[]> btn = new ArrayList<String[]>();
            btn.add(new String[]{"[确认洗练]", "/corerpg p1 reroll " + slot + " " + mode + " confirm", "扣上面的花费，" + (lock ? "锁定「" + cur.name + "」重抽档位" : "抽一次" + name + "的词条"), "GREEN"});
            if (!lock && cur != null && cur.rollable && lockShard > 0 && Math.min(EmberAffix.decodeTier(affixOf(d, t.uid)), cap) < cap)
                btn.add(new String[]{"[锁定" + cur.name + "再洗]", "/corerpg p1 reroll " + slot + " " + (dup ? "dup" : "shard") + " lock", "保留「" + cur.name + "」只重抽档位，另加 " + lockShard + " 碎片", "AQUA"});
            btn.add(new String[]{"[回洗练页]", "/corerpg p1 reroll from " + rfromKey(p), "不洗，回去看看", "GRAY"});
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, btn.toArray(new String[0][]));
            return true;
        }
        if (gate(p)) return true;
        final UUID id = p.getUniqueId();
        if (d.periodCount(EmberPayRules.C_RRO + t.uid, "all") > 0) { recoverRolls(p, 0); p.sendMessage(P + "§c这件上一次洗练已付款、结果还在处理（几秒后自动出结果）"); return true; }
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c洗练暂不可用（服务未就绪）"); return true; }
        if (!rerollBusy.add(id)) { p.sendMessage(P + "§c上一次洗练还在处理"); return true; }
        if (dup && !haveDup) { rerollBusy.remove(id); p.sendMessage(P + "§c背包或装备库里没有可用的重复件"); return true; }
        final EmberItemData dupData = !dup ? null : di >= 0 ? runs.loadouts().items().read(p.getInventory().getItem(di)).data : libDup.d;
        final int dupIndex = di;
        final String lockPaid = lock ? " + 锁定 " + lockShard + " 碎片" : "";
        final String paid = coin + " 币 + " + (dup ? "重复件" : shard + " 碎片") + lockPaid;
        // D172 (forge review X15): the durable payment path. Refund hold → coins / shards taken + "roll owed" (n, lock) in ONE
        // save → the settling transaction (ledger row, or the duplicate's retire) → only THEN the roll, seeded by the request
        // id, written in the save that clears "roll owed". Disconnect / kill at any step: refund once, or the same roll
        // once at the next join — never a free roll, never a paid roll lost.
        final int n = d.periodCount(EmberPayRules.C_RRN + t.uid, "all") + 1;
        final String rid = EmberPayRules.rerollRid(t.uid, n);
        final boolean lockF = lock;
        final EmberPay.Price price = EmberPay.Price.of(new EmberUpgradeRules.Cost(needShard, 0, 0, 0, coin));
        EmberPay.Hook hook = new EmberPay.Hook() {
            @Override public void apply(PlayerData x) {
                x.addPeriodCount(EmberPayRules.C_RRN + t.uid, "all", n - x.periodCount(EmberPayRules.C_RRN + t.uid, "all"));
                x.addPeriodCount(EmberPayRules.C_RRO + t.uid, "all", EmberPayRules.owedValue(n, lockF) - x.periodCount(EmberPayRules.C_RRO + t.uid, "all"));
                pendingRoll.remove(id); // an older 保留新 / 保留旧 choice is void once a new roll is paid
            }
            @Override public void revert(PlayerData x) {
                x.addPeriodCount(EmberPayRules.C_RRN + t.uid, "all", (n - 1) - x.periodCount(EmberPayRules.C_RRN + t.uid, "all"));
                x.addPeriodCount(EmberPayRules.C_RRO + t.uid, "all", -x.periodCount(EmberPayRules.C_RRO + t.uid, "all"));
            }
        };
        pay.pay(p, rid, price, "洗练没完成，退回", hook, () -> settleReroll(id, t, rid, dup, dupIndex, dupData, price, paid),
                err -> { rerollBusy.remove(id); if (p.isOnline()) p.sendMessage(P + "§c没有洗练：" + err); });
        return true;
    }

    /** D172: the transaction that makes the payment final; the roll follows it (or the refund, if it did not commit) */
    private void settleReroll(final UUID id, final EmberItemData t, final String rid, boolean dup, int dupIndex, EmberItemData dupData,
                              EmberPay.Price price, final String paid) {
        final EmberPay pay = EmberPay.get();
        final java.util.function.Consumer<Boolean> after = committed -> {
            rerollBusy.remove(id);
            if (committed) {
                pay.settled(id, rid);
                Player q = Bukkit.getPlayer(id);
                if (q != null) applyOwed(q, t.uid, t.quality, t.slot, rid, paid, false); // offline: rolled at the next join
                return;
            }
            pay.release(id, rid); // not committed → the coins / shards come back once
            Player q = Bukkit.getPlayer(id);
            if (q != null) q.sendMessage(P + "§c洗练没完成（结果没抽），" + paid.replace(" + 重复件", "") + " 会退回");
            recoverRolls(id, 0); // after the release (same DB queue): txn missing → the owed roll is dropped
        };
        Player p = Bukkit.getPlayer(id);
        if (!pay.durable()) { // YAML storage: the old in-memory path (dup consumption is MySQL-only anyway)
            if (dup && p != null) consumeDup(p, dupIndex, dupData, t, null, ok -> { if (!ok) pay.refundInMemory(Bukkit.getPlayer(id), price); rerollBusy.remove(id);
                Player q = Bukkit.getPlayer(id); PlayerData x = data(id);
                if (ok && q != null) applyOwed(q, t.uid, t.quality, t.slot, rid, paid, false);
                else if (x != null) x.addPeriodCount(EmberPayRules.C_RRO + t.uid, "all", -x.periodCount(EmberPayRules.C_RRO + t.uid, "all")); });
            else { rerollBusy.remove(id); if (p != null) applyOwed(p, t.uid, t.quality, t.slot, rid, paid, false); }
            return;
        }
        if (!dup) {
            runs.loadouts().store().commitPlain(rid, "reroll", id, t.uid, price.json(), "洗练 " + t.shortLabel() + "（" + paid + "）",
                    res -> after.accept(res.status == EmberItemStore.TxnStatus.OK || res.status == EmberItemStore.TxnStatus.REPLAY));
            return;
        }
        if (p == null) { after.accept(false); return; }
        consumeDup(p, dupIndex, dupData, t, rid, after);
    }

    private void consumeDup(Player p, int dupIndex, EmberItemData dupData, EmberItemData t, String rid, java.util.function.Consumer<Boolean> cb) {
        if (dupIndex >= 0) plugin.getEmberForge().consumeForReroll(p, dupIndex, dupData, "洗练 " + t.shortLabel() + " 用掉重复件 " + dupData.shortLabel(), rid, cb);
        else EmberGearLib.get().consumeForReroll(p, dupData.uid, "洗练 " + t.shortLabel() + " 用掉装备库重复件 " + dupData.shortLabel(), rid, cb);
    }

    private String oddsText(int cap) {
        double[] o = EmberAffix.tierOdds(reroll, cap);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o.length; i++) sb.append(i == 0 ? "" : " · ").append(i + 1).append(" 档 ").append(String.format(Locale.ROOT, "%.0f%%", o[i] * 100));
        return sb.append(" §8（成色上限截断后归一）").toString();
    }

    private static volatile Long salt;

    /** D172: server secret for the reroll seed (plugins/CoreRpg/p1-runs/reroll-salt, runtime, gitignored) */
    private long salt() {
        Long v = salt;
        if (v != null) return v;
        synchronized (EmberGrowthService.class) {
            if (salt != null) return salt;
            File f = new File(new File(plugin.getDataFolder(), "p1-runs"), "reroll-salt");
            long x;
            try {
                if (f.isFile()) x = Long.parseLong(new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8).trim());
                else {
                    x = new java.security.SecureRandom().nextLong();
                    if (!f.getParentFile().isDirectory()) f.getParentFile().mkdirs();
                    java.nio.file.Files.write(f.toPath(), Long.toString(x).getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[P1 growth] reroll salt " + f + ": " + e + " — using an in-memory salt (results stay exactly-once, not replay-stable over a restart)");
                x = new java.security.SecureRandom().nextLong();
            }
            salt = x;
            return x;
        }
    }

    /**
     * D172: rolls a PAID reroll exactly once. Only when "roll owed" still names this request; the seed comes from the
     * request id, so a roll computed (and maybe shown) but not saved before a crash comes out the same when re-applied.
     * Pity + affix (or the 保留新/旧 candidate) and the cleared "roll owed" go into one save.
     */
    private void applyOwed(Player p, String uid, int quality, String slot, String rid, String paid, boolean recovered) {
        UUID id = p.getUniqueId();
        PlayerData d = data(id);
        if (d == null || reroll == null) return;
        int v = d.periodCount(EmberPayRules.C_RRO + uid, "all");
        if (v <= 0 || !rid.equals(EmberPayRules.rerollRid(uid, EmberPayRules.owedSeq(v)))) return; // applied already
        String lockId = null;
        if (EmberPayRules.owedLock(v)) { EmberAffix.Def cur = EmberAffix.decodeDef(reroll, affixOf(d, uid)); lockId = cur == null ? null : cur.id; }
        int pity = d.periodCount(C_AFP + uid, "all");
        EmberAffix.Roll r = EmberAffix.roll(reroll, slot, quality, pity, new java.util.Random(EmberPayRules.seed(salt(), id.toString(), rid)), lockId);
        d.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -v);
        if (r == null) { plugin.getDataStore().save(id, d); p.sendMessage(P + "§c这个部位没有词条池"); return; }
        d.addPeriodCount(C_AFP + uid, "all", r.pityAfter - pity);
        int enc = EmberAffix.encode(reroll.def(r.id), r.tier), old = affixOf(d, uid);
        if (old == 0) { d.addPeriodCount(C_AF + uid, "all", enc); epoch++; }
        boolean saved = plugin.getDataStore().save(id, d);
        plugin.getLogger().info("[P1 growth] " + p.getName() + " reroll " + uid + " " + rid + " " + paid + " → " + r.id + " t" + r.tier + (lockId != null ? " (lock)" : "")
                + (r.forced ? " (pity)" : "") + " pity " + pity + "→" + r.pityAfter + (recovered ? " (recovered at join)" : "") + (saved ? "" : " (save failed, retried)"));
        if (recovered) p.sendMessage(P + "§e上次洗练（掉线 / 重启前已付款）的结果：");
        p.sendMessage(P + "§6洗练结果：" + affixText(enc, quality) + (r.forced ? " §a（保底）" : "") + " §7· 保底 " + r.pityAfter + "/" + reroll.pity);
        if (old == 0) {
            pendingRoll.remove(id);
            p.sendMessage(P + "§a词条槽原来是空的，直接装上了");
        } else {
            pendingRoll.put(id, new Object[]{uid, slot, enc, old, quality});
            p.sendMessage(P + "原来：" + affixText(old, quality));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[保留新]", "/corerpg p1 reroll keep new", "换成：" + affixText(enc, quality).replaceAll("§.", ""), "GREEN"},
                    new String[]{"[保留旧]", "/corerpg p1 reroll keep old", "不换（不选也等于保留旧，花费不退）", "GRAY"});
        }
        if (!recovered) openMenu(p, REROLL_MENU);
    }

    /** D172: settle every "roll owed" of a player (join, or after a refunded attempt); retried while a hold is in flight */
    void recoverRolls(final UUID id, final int attempt) {
        Player p = Bukkit.getPlayer(id);
        if (p != null) recoverRolls(p, attempt);
    }

    private void recoverRolls(final Player p, final int attempt) {
        final UUID id = p.getUniqueId();
        PlayerData d = data(id);
        if (d == null || !p.isOnline()) return;
        final EmberItemStore store = runs.loadouts().store();
        for (Map.Entry<String, Integer> e : new ArrayList<Map.Entry<String, Integer>>(d.getCounters().entrySet())) {
            String k = e.getKey();
            if (!k.startsWith(EmberPayRules.C_RRO) || !k.endsWith("@all") || e.getValue() == null || e.getValue() <= 0) continue;
            final String uid = k.substring(EmberPayRules.C_RRO.length(), k.length() - 4);
            final int v = e.getValue();
            final String rid = EmberPayRules.rerollRid(uid, EmberPayRules.owedSeq(v));
            if (!store.usable()) continue;
            store.txnState(id, rid, st -> {
                Player q = Bukkit.getPlayer(id);
                PlayerData x = data(id);
                if (q == null || x == null || x.periodCount(EmberPayRules.C_RRO + uid, "all") != v) return;
                switch (EmberPayRules.recovery(st)) {
                    case APPLY:
                        store.lookupFull(uid, row -> {
                            Player q2 = Bukkit.getPlayer(id);
                            PlayerData x2 = data(id);
                            if (q2 == null || x2 == null) return;
                            if (row == null) { // the piece is gone: nothing to roll on
                                x2.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -x2.periodCount(EmberPayRules.C_RRO + uid, "all"));
                                plugin.getDataStore().save(id, x2);
                                plugin.getLogger().warning("[P1 growth] " + q2.getName() + " reroll " + rid + " owed on a missing item, dropped");
                                return;
                            }
                            EmberPay pay = EmberPay.get();
                            if (pay != null) pay.settled(id, rid);
                            applyOwed(q2, uid, row.data.quality, row.data.slot, rid, "已付款", true);
                        });
                        break;
                    case WAIT:
                        if (attempt < 30) Bukkit.getScheduler().runTaskLater(plugin, () -> recoverRolls(id, attempt + 1), 200L);
                        break;
                    default: // refunded (or the payment never reached the save): no roll
                        x.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -v);
                        plugin.getDataStore().save(id, x);
                        plugin.getLogger().info("[P1 growth] " + q.getName() + " reroll " + rid + " not committed → owed roll dropped (cost refunded once)");
                }
            });
        }
    }

    private boolean keep(Player p, PlayerData d, boolean takeNew) {
        Object[] pr = pendingRoll.remove(p.getUniqueId());
        if (pr == null) { p.sendMessage(P + "没有待选的洗练结果"); return true; }
        String uid = (String) pr[0];
        int enc = (Integer) pr[2], old = (Integer) pr[3], q = (Integer) pr[4];
        if (takeNew) {
            if (affixOf(d, uid) != old) { p.sendMessage(P + "§c这件的词条已经变了，结果作废"); return true; }
            d.addPeriodCount(C_AF + uid, "all", enc - old);
            epoch++;
            runs.flushData(p.getUniqueId());
            plugin.getLogger().info("[P1 growth] " + p.getName() + " reroll keep new " + uid + " " + old + "→" + enc);
            p.sendMessage(P + "§a已换成：" + affixText(enc, q));
        } else p.sendMessage(P + "§7保留原来的：" + affixText(old, q));
        openMenu(p, REROLL_MENU);
        return true;
    }

    private String rfromKey(Player p) { String f = RFROM.get(p.getUniqueId()); return f == null ? "none" : f; }

    /** key after "reroll_" */
    public String rerollPapi(Player p, PlayerData d, String key) {
        if (reroll == null || d == null) return "";
        if ("back".equals(key)) {
            String f = RFROM.get(p.getUniqueId());
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "§7关闭";
        }
        if ("pending".equals(key)) {
            Object[] pr = pendingRoll.get(p.getUniqueId());
            return pr == null ? "§8没有待选的结果" : "§e新：" + affixText((Integer) pr[2], (Integer) pr[4]) + " §7· 旧：" + affixText((Integer) pr[3], (Integer) pr[4]);
        }
        if ("odds".equals(key)) return "§7" + oddsText(4);
        if (key.startsWith("pool_")) { // pool_blade_1
            String[] a = key.split("_");
            if (a.length < 3) return "";
            List<EmberAffix.Def> pool = reroll.pool(a[1]);
            int i;
            try { i = Integer.parseInt(a[2]); } catch (NumberFormatException e) { return ""; }
            List<EmberAffix.Def> gone = reroll.retired(a[1]);
            if (i < 1 || i > pool.size() + gone.size()) return "";
            if (i > pool.size()) { // D165: retired affixes listed after the pool
                EmberAffix.Def df = gone.get(i - pool.size() - 1);
                return "§8· " + df.name + "：已移出洗练池（洗不出来；已有的照常生效）";
            }
            EmberAffix.Def df = pool.get(i - 1);
            return "§7· §b" + df.name + "§7：" + df.desc + " " + String.join(" / ", pctList(df)) + (df.note.isEmpty() ? "" : " §8（" + df.note + "）");
        }
        String slot = key.startsWith("charm_") ? "charm" : key.startsWith("blade_") ? "blade" : null;
        if (slot == null) return "";
        String k = key.substring(slot.length() + 1);
        EmberItemData t = target(p, slot);
        if (t == null) return "item".equals(k) ? ("charm".equals(slot) ? "§8还没有选定的护符" : "§8主手没有余烬刃（拿在主手再打开）") : "";
        if ("item".equals(k)) return "§f" + t.shortLabel();
        if (EmberAffix.eligible(t) != null) return "cur".equals(k) ? "§8" + EmberAffix.eligible(t) : "";
        if ("cur".equals(k)) return "§7词条：" + affixText(affixOf(d, t.uid), t.quality);
        if ("cap".equals(k)) {
            int pity = d.periodCount(C_AFP + t.uid, "all");
            return "§7成色" + EmberItemData.qualityName(t.quality) + " · 最高 §f" + reroll.cap(t.quality) + " §7档 · 保底 §f" + pity + "/" + reroll.pity;
        }
        if ("dup".equals(k)) {
            if (findDup(p, d, t) >= 0) return "§a背包里有重复件 §7+ " + rerollCoin(p, t) + " 币";
            if (findLibDup(p, d, t) != null) return "§a装备库里有重复件 §7+ " + rerollCoin(p, t) + " 币";
            return "§8背包或装备库里没有重复件（同部位 T" + t.tier + "、掉落来的、没投入、未锁定/收藏）";
        }
        if ("shard".equals(k)) return "§7" + reroll.shardFor(t.tier) + " 碎片 + " + rerollCoin(p, t) + " 币";
        if ("lock".equals(k)) { // D148
            EmberAffix.Def cur = EmberAffix.decodeDef(reroll, affixOf(d, t.uid));
            int ls = reroll.lockShardFor(t.tier);
            if (ls <= 0) return "";
            if (cur == null) return "§8锁定词条：词条槽还是空的";
            if (!cur.rollable) return "§8" + cur.name + " 已移出洗练池，不能锁定（普通洗会换成别的词条）";
            if (Math.min(EmberAffix.decodeTier(affixOf(d, t.uid)), reroll.cap(t.quality)) >= reroll.cap(t.quality)) return "§a" + cur.name + " 已是上限档";
            return "§bShift+点击：锁定「" + cur.name + "」只重抽档位（另加 " + ls + " 碎片）";
        }
        return "";
    }

    private static List<String> pctList(EmberAffix.Def df) {
        List<String> l = new ArrayList<String>();
        for (double v : df.values) l.add(EmberGrowth.signedPct(v));
        return l;
    }

    // ------------------------------------------------------------------ admin

    public Map<String, Object> debug(Player p) {
        Map<String, Object> m = new HashMap<String, Object>();
        PlayerData d = data(p.getUniqueId());
        m.put("points", pointsEarned(d));
        m.put("picks", picks(d));
        m.put("mods", mods(p).view());
        return m;
    }
}
