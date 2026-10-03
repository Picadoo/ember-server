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
        String key = epoch + "|" + set + "|" + hon + "|" + afKey(af);
        Object[] c = cache.get(p.getUniqueId());
        if (c != null && key.equals(c[0])) return (EmberGrowth.Mods) c[1];
        List<Map<String, Double>> parts = new ArrayList<Map<String, Double>>(EmberGrowth.talentParts(talents, picks(d), set));
        if (honors != null && !hon.isEmpty()) parts.add(EmberGrowth.honorParts(honors, hon));
        if (reroll != null) { Map<String, Double> ap = EmberAffix.parts(reroll, af); if (!ap.isEmpty()) parts.add(ap); } // D143
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(parts);
        cache.put(p.getUniqueId(), new Object[]{key, m});
        return m;
    }

    // ------------------------------------------------------------------ combat hooks

    /** Outgoing multiplier on a run mob (boss / affixed elite / split add / other mob) + the after-dodge window + set events. */
    public double outMult(Player p, Entity target, EmberSetEngine.Kind kind) {
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return 1.0;
        String cls = runs.mobClass(target);
        if (cls == null) return 1.0; // only inside P1 runs
        double r;
        if ("boss".equals(cls)) r = m.get("dmg_boss");
        else if ("split".equals(cls)) r = m.get("dmg_affix") * m.get("dmg_split");
        else if ("affix".equals(cls)) r = m.get("dmg_affix");
        else r = m.get("dmg_mob");
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
            if (EmberAffix.duplicateOk(t, x, affixOf(d, x.uid) > 0) == null) return i;
        }
        return -1;
    }

    int rerollCoin(Player p, EmberItemData t) {
        return (int) Math.round(reroll.coinFor(t.tier) * mods(p).get("reroll_coin"));
    }

    /** /corerpg p1 reroll [from gear|hub] | back | blade|charm dup|shard [confirm] | keep new|old */
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
            return reroll(p, d, op, "dup".equals(how), confirm);
        }
        p.sendMessage(P + "用法：/corerpg p1 reroll（打开洗练页）");
        return true;
    }

    private boolean reroll(Player p, PlayerData d, String slot, boolean dup, boolean confirm) {
        EmberItemData t = target(p, slot);
        String name = "charm".equals(slot) ? "护符" : "刃";
        if (t == null) { p.sendMessage(P + "§c" + ("charm".equals(slot) ? "还没有选定的护符" : "主手没有余烬刃") + "（洗练的是正在用的那件）"); return true; }
        String why = EmberAffix.eligible(t);
        if (why != null) { p.sendMessage(P + "§c" + why); return true; }
        int coin = rerollCoin(p, t), shard = reroll.shardFor(t.tier);
        int di = dup ? findDup(p, d, t) : -1;
        int cap = reroll.cap(t.quality), pity = d.periodCount(C_AFP + t.uid, "all");
        if (!confirm) {
            p.sendMessage(P + "§6词条洗练 · " + t.shortLabel());
            p.sendMessage(P + "现在：" + affixText(affixOf(d, t.uid), t.quality) + " §7· 成色" + EmberItemData.qualityName(t.quality) + "最高 §f" + cap + " §7档 · 保底 §f" + pity + "/" + reroll.pity
                    + (pity >= reroll.pity ? " §a（这次必出 " + cap + " 档）" : ""));
            p.sendMessage(P + "档位概率：" + oddsText(cap));
            String pay = (dup ? "一件同部位同阶的重复件" + (di < 0 ? " §c（背包里没有：要掉落来的、没强化 / 成色 / 精工 / 词条、没在用的 T" + t.tier + name + "）§7" : "")
                    : shard + " 余烬碎片") + " + " + coin + " 余烬币";
            p.sendMessage(P + "花费：" + pay);
            if (dup && di < 0) { p.sendMessage(P + "§7没有重复件时可以改用碎片：" + shard + " 碎片 + " + coin + " 币"); return true; }
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认洗练]", "/corerpg p1 reroll " + slot + " " + (dup ? "dup" : "shard") + " confirm", "扣上面的花费，抽一次" + name + "的词条", "GREEN"},
                    new String[]{"[回洗练页]", "/corerpg p1 reroll from " + rfromKey(p), "不洗，回去看看", "GRAY"});
            return true;
        }
        if (gate(p)) return true;
        if (!rerollBusy.add(p.getUniqueId())) { p.sendMessage(P + "§c上一次洗练还在处理"); return true; }
        if (d.getCoin() < coin) { rerollBusy.remove(p.getUniqueId()); p.sendMessage(P + "§c余烬币不够：要 " + coin + "，现有 " + d.getCoin()); return true; }
        town.sunshine.corerpg.NiBridge ni = plugin.getNiBridge();
        if (!dup && ni.countInInventory(p, EmberUpgradeRules.MAT_SHARD) < shard) {
            rerollBusy.remove(p.getUniqueId());
            p.sendMessage(P + "§c碎片不够：要 " + shard + "，背包里 " + ni.countInInventory(p, EmberUpgradeRules.MAT_SHARD));
            return true;
        }
        if (dup && di < 0) { rerollBusy.remove(p.getUniqueId()); p.sendMessage(P + "§c背包里没有可用的重复件"); return true; }
        if (!d.takeCoin(coin)) { rerollBusy.remove(p.getUniqueId()); p.sendMessage(P + "§c扣币失败"); return true; }
        if (!dup) {
            int got = ni.consume(p, EmberUpgradeRules.MAT_SHARD, shard);
            if (got < shard) {
                if (got > 0) ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, got);
                d.addCoin(coin);
                rerollBusy.remove(p.getUniqueId());
                p.sendMessage(P + "§c扣碎片失败，已退回");
                return true;
            }
            roll(p, d, t, slot, coin + " 币 + " + shard + " 碎片");
            rerollBusy.remove(p.getUniqueId());
            return true;
        }
        EmberItems.Read dr = runs.loadouts().items().read(p.getInventory().getItem(di));
        final EmberItemData dupData = dr.data;
        plugin.getEmberForge().consumeForReroll(p, di, dupData, "洗练 " + t.shortLabel() + " 用掉重复件 " + dupData.shortLabel(), ok -> {
            rerollBusy.remove(p.getUniqueId());
            if (!ok) { d.addCoin(coin); if (p.isOnline()) p.sendMessage(P + "§c重复件没扣成，" + coin + " 币已退回"); return; }
            if (p.isOnline()) roll(p, d, t, slot, coin + " 币 + 重复件");
        });
        return true;
    }

    private String oddsText(int cap) {
        double[] o = EmberAffix.tierOdds(reroll, cap);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o.length; i++) sb.append(i == 0 ? "" : " · ").append(i + 1).append(" 档 ").append(String.format(Locale.ROOT, "%.0f%%", o[i] * 100));
        return sb.append(" §8（成色上限截断后归一）").toString();
    }

    private void roll(Player p, PlayerData d, EmberItemData t, String slot, String paid) {
        int pity = d.periodCount(C_AFP + t.uid, "all");
        EmberAffix.Roll r = EmberAffix.roll(reroll, slot, t.quality, pity, java.util.concurrent.ThreadLocalRandom.current());
        if (r == null) { p.sendMessage(P + "§c这个部位没有词条池"); return; }
        d.addPeriodCount(C_AFP + t.uid, "all", r.pityAfter - pity);
        int enc = EmberAffix.encode(reroll.def(r.id), r.tier), old = affixOf(d, t.uid);
        plugin.getLogger().info("[P1 growth] " + p.getName() + " reroll " + t.uid + " " + paid + " → " + r.id + " t" + r.tier + (r.forced ? " (pity)" : "") + " pity " + pity + "→" + r.pityAfter);
        p.sendMessage(P + "§6洗练结果：" + affixText(enc, t.quality) + (r.forced ? " §a（保底）" : "") + " §7· 保底 " + r.pityAfter + "/" + reroll.pity);
        if (old == 0) {
            d.addPeriodCount(C_AF + t.uid, "all", enc);
            pendingRoll.remove(p.getUniqueId());
            epoch++;
            p.sendMessage(P + "§a词条槽原来是空的，直接装上了");
        } else {
            pendingRoll.put(p.getUniqueId(), new Object[]{t.uid, slot, enc, old, t.quality});
            p.sendMessage(P + "原来：" + affixText(old, t.quality));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[保留新]", "/corerpg p1 reroll keep new", "换成：" + affixText(enc, t.quality).replaceAll("§.", ""), "GREEN"},
                    new String[]{"[保留旧]", "/corerpg p1 reroll keep old", "不换（不选也等于保留旧，花费不退）", "GRAY"});
        }
        runs.flushData(p.getUniqueId());
        openMenu(p, REROLL_MENU);
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
            if (i < 1 || i > pool.size()) return "";
            EmberAffix.Def df = pool.get(i - 1);
            return "§7· §b" + df.name + "§7：" + df.desc + " " + String.join(" / ", pctList(df));
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
        if ("dup".equals(k)) return findDup(p, d, t) >= 0 ? "§a背包里有重复件 §7+ " + rerollCoin(p, t) + " 币" : "§8背包里没有重复件（同部位 T" + t.tier + "、掉落来的、没投入）";
        if ("shard".equals(k)) return "§7" + reroll.shardFor(t.tier) + " 碎片 + " + rerollCoin(p, t) + " 币";
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
