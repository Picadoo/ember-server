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
    private final Map<UUID, Object[]> honorCache = new ConcurrentHashMap<UUID, Object[]>(); // uuid → {until ms, List<String>}
    static final String C_HONOR_SEEN = "p4_honor_";     // + id, period all: the one-time "勋记解锁" notice was sent
    static final String HONOR_MENU = "ember_p1_honor";
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
            honorCache.clear();
            epoch++;
            cache.clear();
            plugin.getLogger().info("[P1 growth] " + EmberGrowth.FILE + ": talents " + (talents == null ? "off" : talents.nodes.size() + " nodes / " + talents.points.size() + " points")
                    + ", honors " + (honors == null ? "off" : honors.list.size()));
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
        if (p == null || talents == null || !EmberMode.active()) return EmberGrowth.Mods.NONE;
        PlayerData d = data(p.getUniqueId());
        if (d == null) return EmberGrowth.Mods.NONE;
        String set = runs.loadouts().get(p).activeSet;
        List<String> hon = honorsEarned(p.getUniqueId(), d);
        String key = epoch + "|" + set + "|" + hon;
        Object[] c = cache.get(p.getUniqueId());
        if (c != null && key.equals(c[0])) return (EmberGrowth.Mods) c[1];
        List<Map<String, Double>> parts = new ArrayList<Map<String, Double>>(EmberGrowth.talentParts(talents, picks(d), set));
        if (honors != null && !hon.isEmpty()) parts.add(EmberGrowth.honorParts(honors, hon));
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
        cache.remove(u); dodgeUntil.remove(u); dodgeHealCd.remove(u);
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
