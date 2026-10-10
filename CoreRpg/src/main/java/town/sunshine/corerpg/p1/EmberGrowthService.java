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
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.p1.encounter.EmberSigAttunePreview;

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
        String key = epoch + "|" + set + "|" + hon + "|" + afKey(af) + "|" + sigKey(d, sg);
        Object[] c = cache.get(p.getUniqueId());
        if (c != null && key.equals(c[0])) return (EmberGrowth.Mods) c[1];
        List<Map<String, Double>> parts = new ArrayList<Map<String, Double>>(EmberGrowth.talentParts(talents, picks(d), set));
        if (honors != null && !hon.isEmpty()) parts.add(EmberGrowth.honorParts(honors, hon));
        if (reroll != null) { Map<String, Double> ap = EmberAffix.parts(reroll, af); if (!ap.isEmpty()) parts.add(ap); } // D143
        for (EmberSignature.Def sd : sg) parts.add(EmberSignature.modsOf(sd, sigAlt(d, sd))); // D174 签名传奇 (≤ 2, each its own part so the ADD keys add); stage 3 调律 version
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

    /**
     * D455: if an active worn signature owns {@code modKey}, ActionBar-confirm with its name.
     * Yields to set proc HUD via {@link EmberSetService#flashSkillConfirm}. Zero power.
     */
    public boolean flashSigOwned(Player p, String modKey, String verb) {
        if (p == null || modKey == null) return false;
        PlayerData d = data(p.getUniqueId());
        EmberLoadout lo = runs.loadouts() == null ? null : runs.loadouts().get(p);
        EmberSignature.Def sd = EmberSigFeel.owner(signatures(d, lo), modKey);
        if (sd == null) return false;
        long now = System.currentTimeMillis();
        if (!EmberSigFeel.allow(p.getUniqueId(), modKey + ":" + verb, now)) return false;
        String msg = EmberSigFeel.procLine(sd, verb);
        EmberSetService sets = plugin.getEmberSets();
        if (sets != null) return sets.flashSkillConfirm(p, msg);
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        return true;
    }

    /** D455: one-shot run-start identity line (chat) when player has active signatures. */
    public void announceSigFeel(Player p) {
        if (p == null) return;
        PlayerData d = data(p.getUniqueId());
        EmberLoadout lo = runs.loadouts() == null ? null : runs.loadouts().get(p);
        String line = EmberSigFeel.runLine(signatures(d, lo));
        if (line == null || line.isEmpty()) return;
        p.sendMessage(P + line);
        EmberSetService sets = plugin.getEmberSets();
        if (sets != null) sets.flashSkillConfirm(p, line);
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
        if (db > 0 && plugin.getEmberSets() != null && plugin.getEmberSets().primeBurst(p, db)) {
            if (!flashSigOwned(p, "dodge_burst", "借势 · 烬爆 +" + db))
                p.sendActionBar(ChatColor.GOLD + "借势！烬爆计数 +" + db);
        }
        double heal = m.get("dodge_heal");
        if (heal > 0) {
            Long cd = dodgeHealCd.get(p.getUniqueId());
            if (cd == null || now >= cd) {
                dodgeHealCd.put(p.getUniqueId(), now + (long) (m.get("dodge_icd") * 1000));
                EmberHeal.heal(p, heal * EmberHeal.maxHp(p), "D141 踏步回气 " + pct(heal));
                if (!flashSigOwned(p, "dodge_heal", "躲开回气")) {
                    // talent-only path keeps a thin ActionBar (was silent)
                    try { p.sendActionBar(ChatColor.GREEN + "躲开回气 +" + pct(heal)); } catch (Throwable ignored) { }
                }
            }
        }
    }

    /** A boss telegraph hit this player (反震: the 烬爆 counter moves forward). */
    public void onTeleHit(Player p) {
        EmberGrowth.Mods m = mods(p);
        int hb = (int) Math.round(m.get("hit_burst"));
        if (hb > 0 && plugin.getEmberSets() != null && plugin.getEmberSets().primeBurst(p, hb)) {
            if (!flashSigOwned(p, "hit_burst", "反震 · 烬爆 +" + hb))
                p.sendActionBar(ChatColor.GOLD + "反震！烬爆计数 +" + hb);
        }
    }

    public double potionMult(Player p) { return mods(p).get("potion"); }
    public double shareWeight(Player p) { return Math.max(0.1, mods(p).get("share_w")); }
    public double shareTaken(Player p) { return mods(p).get("share_taken"); }

    /** Set tuning for the player's active set: {burstEveryDelta, sustainEveryDelta, burnMult, burstMult, sustainMult, burnSpread} */
    public EmberSetEngine.Tune setTune(Player p) {
        EmberGrowth.Mods m = mods(p);
        if (m.isEmpty()) return EmberSetEngine.Tune.NONE;
        return new EmberSetEngine.Tune((int) Math.round(m.get("burst_every")), (int) Math.round(m.get("sustain_every")),
                m.get("burn_mult"), m.get("burst_mult"), m.get("sustain_mult"), (int) Math.round(m.get("burn_ticks")), m.get("burn_spread"), m.get("spread_icd"),
                m.get("sustain_low"), (int) Math.round(m.get("sustain_low_every"))); // D174 L12
    }

    // ------------------------------------------------------------------ D174 stage 2a: 烬斩 shield (L11) — absorbs P1 enemy damage

    /** uuid → {shield HP, until ms} */
    private final Map<UUID, double[]> shield = new ConcurrentHashMap<UUID, double[]>();

    /**
     * 烬斩 hit {@code n} targets: shield = skill_shield × H per target, capped at skill_shield_max × H, for skill_shield_secs;
     * does not stack (keeps the larger of the still-active one and the new one; p1sim skill_variant).
     */
    public double giveSkillShield(Player p, EmberGrowth.Mods m, int n) {
        double per = m.get("skill_shield");
        if (p == null || per <= 0 || n <= 0) return 0;
        double h = EmberHeal.maxHp(p), cap = (m.get("skill_shield_max") > 0 ? m.get("skill_shield_max") : per) * h;
        double secs = m.get("skill_shield_secs") > 0 ? m.get("skill_shield_secs") : 5.0;
        long now = System.currentTimeMillis();
        double[] cur = shield.get(p.getUniqueId());
        double keep = cur != null && now < cur[1] ? cur[0] : 0;
        double got = Math.max(keep, Math.min(n * per * h, cap));
        shield.put(p.getUniqueId(), new double[]{got, now + (long) (secs * 1000)});
        String shVerb = "护盾 " + String.format(Locale.ROOT, "%.1f", got) + "（" + String.format(Locale.ROOT, "%.0f", secs) + " 秒）";
        if (!flashSigOwned(p, "skill_shield", shVerb))
            p.sendActionBar(ChatColor.AQUA + "霜封护盾 " + String.format(Locale.ROOT, "%.1f", got) + "（" + String.format(Locale.ROOT, "%.0f", secs) + " 秒）");
        return got;
    }

    /** P1 enemy damage after the multipliers → what is left after the shield (the shield shrinks) */
    public double absorbShield(Player p, double dmg) {
        if (p == null || dmg <= 0) return dmg;
        double[] cur = shield.get(p.getUniqueId());
        if (cur == null) return dmg;
        if (System.currentTimeMillis() >= cur[1] || cur[0] <= 0) { shield.remove(p.getUniqueId()); return dmg; }
        double used = Math.min(cur[0], dmg);
        cur[0] -= used;
        if (cur[0] <= 1e-9) shield.remove(p.getUniqueId());
        return dmg - used;
    }

    public void clearShield(UUID u) { if (u != null) shield.remove(u); }

    static String pct(double x) { return String.format(Locale.ROOT, "%.0f%%", x * 100); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        cache.remove(u); dodgeUntil.remove(u); dodgeHealCd.remove(u); pendingRoll.remove(u); rerollBusy.remove(u);
        SPICK.remove(u); SFROM.remove(u); shield.remove(u); // D174 stage 1.5 / 2a
        pendingEnter.remove(u); // D297 W1c
        EmberSigFeel.clear(u); // D455
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
        if (coin > 0 && !EmberEconomy.spendCoin(d, "C09", coin)) { p.sendMessage(P + "§c余烬币不够（要 " + coin + "）"); return true; } // D218 REG C09
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
        if (cost > 0 && !EmberEconomy.spendCoin(d, "C10", cost)) { p.sendMessage(P + "§c扣币失败"); return true; } // D218 REG C10
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

    // D208 (ARCH S1-4): legacy only — the affix and its pity are EmberItemData.affix / afPity on v2 items (EmberItemKeys)
    static final String C_AF = "p4_af_";    // + item uid, period all: affix code * 10 + tier (0 = empty)
    static final String C_AFP = "p4_afp_";  // + item uid, period all: tries in a row below the quality cap (pity)
    /** D429: forge charges used (+ item uid, period all); left = 13 - used */
    static final String C_AFC = "p4_afc_";
    public static final String REROLL_MENU = "ember_p1_reroll";
    private static final Map<UUID, String> RFROM = new ConcurrentHashMap<UUID, String>();
    /** uuid → {item uid, slot, encoded candidate, old encoded} — the roll waiting for 保留新 / 保留旧 (not chosen = old) */
    private final Map<UUID, Object[]> pendingRoll = new ConcurrentHashMap<UUID, Object[]>();
    private final java.util.Set<UUID> rerollBusy = java.util.Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    static final Map<String, String> REROLL_FROM = new LinkedHashMap<String, String>();
    static {
        REROLL_FROM.put("gear", "ember_p1_gear");
        REROLL_FROM.put("hub", "ember_hub");
        REROLL_FROM.put("forge", "ember_p1_forge"); // D451 workshop pillar
    }

    public EmberAffix.Rules reroll() { return reroll; }

    /** D208: the piece's affix — on the item (v2) or its legacy counter (v1) */
    public int affixOf(PlayerData d, EmberItemData it) { return EmberItemKeys.affix(d, it); }

    /** D208: the piece's reroll pity — on the item (v2) or its legacy counter (v1) */
    public int afPityOf(PlayerData d, EmberItemData it) { return EmberItemKeys.afPity(d, it); }

    private int[][] affixItems(PlayerData d, EmberLoadout lo) {
        if (reroll == null || lo == null) return new int[0][];
        return new int[][]{lo.blade == null ? null : new int[]{affixOf(d, lo.blade), lo.blade.quality},
                lo.charm == null ? null : new int[]{affixOf(d, lo.charm), lo.charm.quality}};
    }

    // ------------------------------------------------------------------ D174 签名传奇

    /** signature code of this piece (0 = none) — D208: on the item (v2) or its legacy counter (v1) */
    public int sigOf(PlayerData d, EmberItemData it) { return EmberItemKeys.sig(d, it); }

    /** D174: an affix or a signature = invested (never a reroll duplicate, skipped by bulk dismantle) */
    public boolean invested(PlayerData d, EmberItemData it) { return affixOf(d, it) > 0 || sigOf(d, it) > 0; }

    // ------------------------------------------------------------------ D208: item-key writes (one item transaction each)

    /**
     * D208: commits item changes (signed stacks pre-built, one cr_p1_item transaction, owner online or not). On OK: row
     * cache, stack replaced by uid when the owner is online (otherwise the join resync rewrites it from the DB row),
     * legacy counters of every v1 piece folded here dropped, loadout refreshed. YAML storage: NBT only, applied inline.
     */
    void commitOnItem(final UUID id, final String kind, final String rid, final List<EmberItemStore.TxnItem> items,
                      final String costJson, final String note, final java.util.function.Consumer<Boolean> cb) {
        final Map<String, org.bukkit.inventory.ItemStack> built = new HashMap<String, org.bukkit.inventory.ItemStack>();
        for (EmberItemStore.TxnItem t : items) {
            if (t.after == null) continue;
            org.bukkit.inventory.ItemStack st = runs.loadouts().items().create(t.after);
            if (st == null) { plugin.getLogger().warning("[P1 growth] " + kind + " " + rid + ": stack for " + t.after.uid + " not buildable"); cb.accept(false); return; }
            built.put(t.after.uid, st);
        }
        final EmberItemStore store = runs.loadouts().store();
        java.util.function.Consumer<EmberItemStore.TxnResult> fin = res -> {
            boolean ok = res.status == EmberItemStore.TxnStatus.OK || res.status == EmberItemStore.TxnStatus.REPLAY;
            if (ok) itemsCommitted(id, items, built);
            else plugin.getLogger().info("[P1 growth] " + kind + " " + rid + " not committed: " + res.status + " " + res.detail);
            cb.accept(ok);
        };
        if (store.usable()) store.commitTxn(rid, kind, id, items, costJson, note, fin);
        else fin.accept(new EmberItemStore.TxnResult(EmberItemStore.TxnStatus.OK, null));
    }

    /** D208: bookkeeping after an item transaction committed (also used for the gear-library duplicate path) */
    void itemsCommitted(UUID id, List<EmberItemStore.TxnItem> items, Map<String, org.bukkit.inventory.ItemStack> built) {
        Player q = Bukkit.getPlayer(id);
        PlayerData x = data(id);
        boolean ch = false;
        for (EmberItemStore.TxnItem t : items) {
            if (t.after == null) continue;
            runs.loadouts().rememberRow(t.after.uid, id, t.after.rev, t.expectState);
            if (!t.before.itemKeys()) ch |= EmberItemKeys.clearLegacy(x, t.before.uid);
            org.bukkit.inventory.ItemStack st = built == null ? null : built.get(t.after.uid);
            if (q != null && st != null && "active".equals(t.expectState)) plugin.getEmberForge().replaceStack(q, t.after.uid, st);
        }
        if (ch) plugin.getDataStore().flushMutation(id);
        epoch++;
        if (q != null) runs.loadouts().refresh(q);
    }

    /** D208: the v2 data of {@code t} after a write (legacy counters folded in, rev + 1) */
    static EmberItemData next(PlayerData d, EmberItemData t, int affix, int afPity, int sig, int rerollN) {
        return EmberItemKeys.fold(d, t).withItemKeys(affix, afPity, sig, rerollN).withRev(t.rev + 1);
    }

    EmberSignature.Worn worn(PlayerData d, EmberItemData it) {
        return it == null ? null : new EmberSignature.Worn(EmberSignature.byCode(sigOf(d, it)), it.slot, it.family);
    }

    /** D174 stage 1.5: the player switched this slot's signature off in the menu (period all, 1 = off) */
    static final String C_SIGOFF = "p1_sigoff_";
    /** D174 stage 1.5: this signature was ever stamped / imprinted for this player (period all; codex 「获得过」) */
    static final String C_SIGSEEN = "p1_sigseen_";
    /** D297 W1c: week key already showed attune confirm (period = ISO week) */
    static final String C_ATTUNE_PROMPT = "p1_attuneprompt";
    public static final String SIG_RUN_CONFIRM = "ember_p1_sig_runconfirm";
    /** pending enter after confirm: {mapKey, "1"|"0" challenge} */
    private final Map<UUID, String[]> pendingEnter = new ConcurrentHashMap<UUID, String[]>();

    public boolean sigOff(PlayerData d, String slot) { return d != null && d.periodCount(C_SIGOFF + slot, "all") > 0; }

    /** D174 stage 3 签名调律: the 调律 version of {@code sd} is unlocked (paid once) */
    public boolean altUnlocked(PlayerData d, EmberSignature.Def sd) {
        return d != null && sd != null && EmberSignature.alt(sd) != null && d.periodCount(EmberSignature.C_ALTU + sd.id, "all") > 0;
    }

    /** D174 stage 3 签名调律: the 调律 version is the one in effect (unlocked + picked) */
    public boolean sigAlt(PlayerData d, EmberSignature.Def sd) {
        return altUnlocked(d, sd) && d.periodCount(EmberSignature.C_ALT + sd.id, "all") > 0;
    }

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
        String ed = "";
        if (why == null && EmberSignature.alt(w.def) != null && altUnlocked(d, w.def))
            ed = sigAlt(d, w.def) ? " §d·调律" : " §a·原版";
        return "§7" + EmberItemData.slotName(slot) + "：§f" + it.shortLabel() + " §6" + w.def.name
                + (why == null ? " §a生效" : " §c不生效：" + why) + ed;
    }

    /** D297 W1b: other-edition cost for the worn slot (empty if none / locked). */
    private String wornOtherCost(PlayerData d, EmberLoadout lo, String slot) {
        EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
        if (it == null) return "";
        EmberSignature.Worn w = worn(d, it);
        if (w == null || w.def == null || EmberSignature.alt(w.def) == null) return "";
        if (!altUnlocked(d, w.def)) return "§8调律版未解锁";
        return altOther(d, w.def);
    }

    /** D297: active worn signatures that participate in combat (same as signatures()). */
    private EmberSigAttunePreview.Slot previewSlot(PlayerData d, EmberLoadout lo, String slot) {
        EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
        if (it == null || sigOff(d, slot)) return null;
        EmberSignature.Worn w = worn(d, it);
        if (w == null || w.def == null) return null;
        if (sigWhy(d, lo, slot) != null) return null; // not active
        boolean alt = sigAlt(d, w.def);
        String other = EmberSignature.alt(w.def) == null ? null
                : (alt ? w.def.bad : EmberSignature.alt(w.def).bad);
        return new EmberSigAttunePreview.Slot(EmberItemData.slotName(slot),
                alt ? "调律" : "原版", EmberSignature.badOf(w.def, alt), other);
    }

    /** D297 W1a: empty unless Q07 open and at least one active signature. */
    String runPreviewLine(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null || !runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) return "";
        return EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(previewSlot(d, lo, "blade"), previewSlot(d, lo, "charm")));
    }

    String runPreviewCost(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null || !runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) return "";
        return EmberSigAttunePreview.costLine(EmberSigAttunePreview.listOf(previewSlot(d, lo, "blade"), previewSlot(d, lo, "charm")));
    }

    /** true if at least one worn active sig has unlocked attune. */
    boolean anyUnlockedAttuneWorn(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null || !runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) return false;
        for (String slot : new String[]{"blade", "charm"}) {
            EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
            if (it == null || sigOff(d, slot)) continue;
            EmberSignature.Worn w = worn(d, it);
            if (w == null || w.def == null || sigWhy(d, lo, slot) != null) continue;
            if (altUnlocked(d, w.def)) return true;
        }
        return false;
    }

    boolean needAttunePrompt(PlayerData d, EmberLoadout lo) {
        if (!anyUnlockedAttuneWorn(d, lo)) return false;
        return d.periodCount(C_ATTUNE_PROMPT, DailyService.weekId()) <= 0;
    }

    void markAttunePromptShown(PlayerData d) {
        if (d == null) return;
        String wk = DailyService.weekId();
        if (d.periodCount(C_ATTUNE_PROMPT, wk) <= 0) d.addPeriodCount(C_ATTUNE_PROMPT, wk, 1);
    }

    /**
     * D297 W1c: if first weekly confirm needed, stash enter and open panel; return true = caller must not start run.
     */
    public boolean maybeAttunePrompt(Player leader, String mapKey, boolean challenge) {
        if (leader == null || mapKey == null) return false;
        String mk = mapKey.toLowerCase(Locale.ROOT);
        if (!mk.matches("q0[1-7]")) return false; // D297: 日刷/挑战 (q01–q07) only; not raid/abyss
        PlayerData d = plugin.getDataStore().get(leader.getUniqueId());
        EmberLoadout lo = runs.loadouts().get(leader);
        if (!needAttunePrompt(d, lo)) return false;
        pendingEnter.put(leader.getUniqueId(), new String[]{mk, challenge ? "1" : "0"});
        markAttunePromptShown(d); // 同周不重复：弹出即记
        plugin.getDataStore().flushMutation(leader.getUniqueId());
        openMenu(leader, SIG_RUN_CONFIRM);
        return true;
    }

    /** D297: confirm panel actions — go | attune | cancel */
    boolean sigRunConfirm(Player p, PlayerData d, String action) {
        String[] pend = pendingEnter.get(p.getUniqueId());
        if ("attune".equals(action) || "调律".equals(action)) {
            pendingEnter.remove(p.getUniqueId());
            markAttunePromptShown(d);
            plugin.getDataStore().flushMutation(p.getUniqueId());
            openMenu(p, SIG_ALT_MENU);
            return true;
        }
        if ("cancel".equals(action) || "取消".equals(action)) {
            pendingEnter.remove(p.getUniqueId());
            p.sendMessage(P + "§7已取消进本确认");
            return true;
        }
        // go / 就这样
        if (pend == null) {
            p.sendMessage(P + "§7没有待确认的进本（从冒险/挑战再点一次）");
            return true;
        }
        pendingEnter.remove(p.getUniqueId());
        markAttunePromptShown(d);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        boolean ch = "1".equals(pend[1]);
        runs.tryEnterAfterAttuneConfirm(p, pend[0], ch);
        return true;
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
        if ("chase".equals(op) || "追烙".equals(op)) { // D464 imprint-chase path
            if (args.length < 4) {
                p.sendMessage(P + EmberSigChase.glance(d));
                EmberSigChase.offerPick(p, d, runs);
                return true;
            }
            String raw = args[3];
            if ("clear".equalsIgnoreCase(raw) || "none".equalsIgnoreCase(raw) || "off".equalsIgnoreCase(raw) || "取消".equals(raw)) {
                EmberSigChase.clear(d);
                plugin.getDataStore().flushMutation(p.getUniqueId());
                p.sendMessage(P + "已取消追烙目标");
                return true;
            }
            if ("offer".equalsIgnoreCase(raw) || "nudge".equalsIgnoreCase(raw)) {
                EmberSigChase.forceOffer(p);
                return true;
            }
            EmberSignature.Def def = EmberSigChase.parse(raw);
            if (def == null) {
                p.sendMessage(P + "用法：/corerpg p1 sig chase <L01…L15|clear>");
                return true;
            }
            boolean ch = EmberSigChase.set(d, def);
            plugin.getDataStore().flushMutation(p.getUniqueId());
            p.sendMessage(P + "§a追烙 → §f" + EmberSigChase.label(def) + "§7（" + EmberSigChase.tip(def) + "）"
                    + (ch ? "" : " §8· 已是该目标"));
            p.sendMessage(P + EmberSigChase.glance(d));
            return true;
        }
        if ("toggle".equals(op)) return sigToggle(p, d, lo, args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "");
        if ("runconfirm".equals(op) || "进本确认".equals(op)) { // D297 W1c
            return sigRunConfirm(p, d, args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "go");
        }
        if ("attune".equals(op) || "调律".equals(op)) { // stage 3: bare = the 调律 page; <Lxx> = switch; <Lxx> unlock = pay 10 insignia
            if (args.length < 4) { openMenu(p, SIG_ALT_MENU); return true; }
            return sigAttune(p, d, args[3], args.length >= 5 && "unlock".equalsIgnoreCase(args[4]));
        }
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
                String line = P + "  §6" + sd.name + " §7" + sd.kindText() + "：§f" + sd.good + " §7/ 代价：" + EmberSignature.badOf(sd, sigAlt(d, sd))
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
        int cur = t == null ? 0 : sigOf(d, t);
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

    private final java.util.Set<String> impBusy = java.util.Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    /**
     * D208 (ARCH S1-4): the signature is written on the ITEM (v2 NBT + cr_p1_item.sig_code, HMAC) through the durable
     * payment path: refund hold → insignia + coins taken in one save → the item transaction (which voids the hold) →
     * insignia / coins come back exactly once when it does not commit (EmberDelivery kinds sigmark / coin).
     * Returns true when the payment started.
     */
    private boolean doImprint(Player p, PlayerData d, EmberLoadout lo, EmberSignature.Def sd, EmberItemData t, int cur, int coin, String via) {
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + ChatColor.RED + "烙印暂不可用（服务未就绪）"); return false; }
        if (!t.itemKeys() && d.isLoadFailed()) { p.sendMessage(P + ChatColor.RED + "数据没加载完整，稍后再试"); return false; }
        if (!impBusy.add(t.uid)) { p.sendMessage(P + ChatColor.RED + "这件的烙印还在处理"); return false; }
        final UUID id = p.getUniqueId();
        final EmberSignature.Def old = EmberSignature.byCode(cur);
        final EmberItemData after = next(d, t, affixOf(d, t), afPityOf(d, t), sd.code, EmberItemKeys.rerollN(d, t));
        final String rid = EmberPayRules.imprintRid(t.uid, t.rev, System.currentTimeMillis());
        final EmberPay.Price price = EmberPay.Price.insignia(coin, sd.map, EmberSignature.IMPRINT_MARKS).at(EmberEconomy.sinkForForge("imprint")); // D218 C12
        final String note = "烙印 " + t.shortLabel() + " " + (old == null ? "-" : old.id) + " → " + sd.id;
        pay.pay(p, rid, price, "烙印没完成，退回", null, () -> commitOnItem(id, "imprint", rid,
                java.util.Collections.singletonList(new EmberItemStore.TxnItem(t, after, null)), price.json(), note, ok -> {
                    impBusy.remove(t.uid);
                    Player q = Bukkit.getPlayer(id);
                    if (!ok) {
                        pay.release(id, rid);
                        if (q != null) q.sendMessage(P + ChatColor.RED + "烙印没完成（签名没变），" + price.label() + " 会退回");
                        return;
                    }
                    pay.settled(id, rid);
                    PlayerData x = data(id);
                    markSeen(x, sd);
                    boolean saved = plugin.getDataStore().flushMutationChecked(id);
                    plugin.getLogger().info("[P1 sig] " + (q == null ? id.toString() : q.getName()) + " imprint " + t.uid + " " + (old == null ? "-" : old.id) + " → " + sd.id
                            + " (-" + EmberSignature.IMPRINT_MARKS + " " + sd.map + " marks, -" + coin + " coin, via " + via + ", item rev " + after.rev + ", " + rid + ", saved=" + saved + ")");
                    if (q == null || x == null) return;
                    q.sendMessage(P + "§a烙印完成：§f" + t.shortLabel() + " §7→ §6" + sd.name + "§7（剩 " + x.periodCount(EmberSignature.C_MARK + sd.map, "all") + " 枚徽记）");
                    EmberLoadout l2 = runs.loadouts().get(q);
                    boolean worn = "blade".equals(sd.slot) ? l2.blade != null && l2.blade.uid.equals(t.uid) : l2.charm != null && l2.charm.uid.equals(t.uid);
                    if (!worn) q.sendMessage(P + "§e注意：这件现在没在用（选定 / 手持后才生效）");
                    else {
                        String off = sigWhy(x, l2, sd.slot);
                        if (off != null) q.sendMessage(P + "§e注意：现在不生效——" + off);
                    }
                }), err -> { impBusy.remove(t.uid); if (p.isOnline()) p.sendMessage(P + ChatColor.RED + "没有烙印：" + err); });
        return true;
    }

    // ------------------------------------------------------------------ D174 stage 1.5: TrMenu pages (no commands for players)

    public static final String SIG_MENU = "ember_p1_sig", SIG_IMP_MENU = "ember_p1_sig_imprint", SIG_ALT_MENU = "ember_p1_sig_attune";
    static final Map<String, String> SIG_FROM = new LinkedHashMap<String, String>();
    static {
        SIG_FROM.put("gear", "ember_p1_gear");
        SIG_FROM.put("hub", "ember_hub");
        SIG_FROM.put("skill", "ember_skill_kit"); // D290: 技能组 ↔ 签名深链
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

    /**
     * D174 stage 3 签名调律 (Q07): unlock (once, {@link EmberSignature#ALT_MARKS} insignia of the signature's map, one checked
     * save) or switch the version in effect. Town only (same gate as 烙印 / 开关); the next run uses the new version.
     */
    private boolean sigAttune(Player p, PlayerData d, String id, boolean unlock) {
        EmberSignature.Def sd = EmberSignature.byId(id);
        if (sd == null || EmberSignature.alt(sd) == null) { openMenu(p, SIG_ALT_MENU); return true; }
        if (inRun(p)) { p.sendMessage(P + ChatColor.RED + "请回城后再调律（副本里不能改）"); return true; }
        boolean un = altUnlocked(d, sd);
        int marks = d.periodCount(EmberSignature.C_MARK + sd.map, "all");
        String why = EmberSignature.attuneCheck(sd, runs.progressFlag(d, EmberSignature.ALT_UNLOCK), runs.progressFlag(d, sd.map), un, marks);
        if (why != null) { p.sendMessage(P + ChatColor.RED + why); return true; }
        if (!un) {
            if (!unlock) {
                p.sendMessage(P + "§d签名调律 §7· " + sd.name + "：调律版代价「" + EmberSignature.alt(sd).bad + "」（原版「" + sd.bad + "」，好处不变）");
                p.sendMessage(P + "§e解锁要 " + EmberSignature.ALT_MARKS + " 枚 " + sd.map.toUpperCase(Locale.ROOT) + " 首领徽记（有 " + marks + "）· 在调律页 Shift+点击解锁，之后永久、随时切换");
                return true;
            }
            String hold = EmberAssetGuard.hold(p);
            if (hold != null) { p.sendMessage(P + ChatColor.RED + hold); return true; }
            if (!EmberEconomy.spendInsignia(d, "C13", sd.map, EmberSignature.ALT_MARKS)) { p.sendMessage(P + ChatColor.RED + "扣除首领徽记失败"); return true; } // D218 REG C13
            d.addPeriodCount(EmberSignature.C_ALTU + sd.id, "all", 1);
            d.addPeriodCount(EmberSignature.C_ALT + sd.id, "all", 1 - d.periodCount(EmberSignature.C_ALT + sd.id, "all"));
            boolean saved = plugin.getDataStore().flushMutationChecked(p.getUniqueId());
            plugin.getLogger().info("[P1 sig] " + p.getName() + " attune unlock " + sd.id + " (-" + EmberSignature.ALT_MARKS + " " + sd.map + " marks, saved=" + saved + ")");
            p.sendMessage(P + "§a调律版已解锁：§6" + sd.name + " §7现在用调律版（代价：" + EmberSignature.alt(sd).bad + "）· 剩 "
                    + d.periodCount(EmberSignature.C_MARK + sd.map, "all") + " 枚徽记" + (saved ? "" : " §e（存档稍后重试）"));
        } else {
            boolean nowAlt = !sigAlt(d, sd);
            d.addPeriodCount(EmberSignature.C_ALT + sd.id, "all", (nowAlt ? 1 : 0) - d.periodCount(EmberSignature.C_ALT + sd.id, "all"));
            runs.flushData(p.getUniqueId());
            plugin.getLogger().info("[P1 sig] " + p.getName() + " attune " + sd.id + " → " + (nowAlt ? "alt" : "original"));
            p.sendMessage(P + "§6" + sd.name + " §7现在用" + (nowAlt ? "§d调律版" : "§a原版") + "§7（代价：" + EmberSignature.badOf(sd, nowAlt) + "）· 下一局起生效");
        }
        openMenu(p, SIG_ALT_MENU);
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
        int cur = t == null ? 0 : sigOf(d, t);
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
        if ("alt_head".equals(key)) { // stage 3 签名调律 page header
            if (!runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) return "§8签名调律：首通 " + mapLabel(EmberSignature.ALT_UNLOCK) + " 后开放";
            int un = 0;
            for (String id : EmberSignature.ALTS.keySet()) if (altUnlocked(d, EmberSignature.byId(id))) un++;
            return "§7签名调律：§a已开放 §7· 已解锁调律版 §f" + un + "§7/" + EmberSignature.ALTS.size();
        }
        if ("rules".equals(key)) return imp ? "§7烬炉烙印：§a已开放 §7（点一条签名开始）" : "§7烬炉烙印：§8首通 " + EmberSignature.IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放";
        if ("acq".equals(key) || "acq_line".equals(key)) return EmberSigAcq.menuLine(); // D456
        if ("chase".equals(key) || "chase_line".equals(key)) return EmberSigChase.glance(d); // D464
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
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "skill".equals(f) ? "§7返回技能组" : "§7关闭";
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
            if (it == null || sigOf(d, it) <= 0) return "§8这件没有签名（在下面点一条签名可以烙印）";
            return "§e➥ 点击关闭这条签名 §8（有代价时可以先关掉；随时再开）";
        }
        // per signature by index (1 = DEFS[0]): n_ name · k_ kind/map/boss · g_ good · b_ bad · s_ status · a_ action
        int u = key.indexOf('_');
        if (u == 1 && "nkgbsaot".indexOf(key.charAt(0)) >= 0) {
            int i = idx(key.substring(2));
            if (i < 1 || i > EmberSignature.DEFS.size()) return "";
            EmberSignature.Def sd = EmberSignature.DEFS.get(i - 1);
            boolean cl = runs.progressFlag(d, sd.map);
            switch (key.charAt(0)) {
                case 'n': return (cl ? "§6" : "§8") + sd.name + (cl ? "" : " §8（未解锁）");
                case 'k': return "§7" + sd.kindText() + " · " + mapLabel(sd.map) + " · " + sd.boss + (sd.anyFamily() ? "" : " §8（只在" + EmberItemData.familyName(sd.family) + "成套时生效）");
                case 'g': return "§a得：§f" + sd.good;
                case 'b': return "§c代价：§7" + EmberSignature.badOf(sd, sigAlt(d, sd)) + (sigAlt(d, sd) ? " §d（调律版）" : "");
                case 'o': return altOther(d, sd);
                case 't': return altAction(d, sd, cl);
                case 's': return sigStatus(d, lo, sd, cl);
                default:
                    if (!cl) return "§8首通 " + mapLabel(sd.map) + " 后解锁：重打掉签名件和徽记";
                    if (!imp) return "§8重打 " + sd.map.toUpperCase(Locale.ROOT) + " 有机会掉 · 烙印在首通 " + EmberSignature.IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放";
                    return "§e➥ 点击：烙印到自己的" + EmberItemData.slotName(sd.slot) + "上 §7（" + EmberSignature.IMPRINT_MARKS + " 枚徽记 + "
                            + EmberSignature.IMPRINT_COIN_PER_TIER + "×阶级 币）";
            }
        }
        if ("run".equals(key)) return runPreviewLine(d, lo);
        if ("run_cost".equals(key)) return runPreviewCost(d, lo);
        if ("blade_o".equals(key)) return wornOtherCost(d, lo, "blade");
        if ("charm_o".equals(key)) return wornOtherCost(d, lo, "charm");
        if (key.startsWith("p_")) return impPapi(p, d, lo, key.substring(2));
        return "";
    }

    /** stage 3: the other version's cost line */
    private String altOther(PlayerData d, EmberSignature.Def sd) {
        EmberSignature.Alt a = EmberSignature.alt(sd);
        if (a == null) return "§8没有调律版";
        return sigAlt(d, sd) ? "§7原版代价：" + sd.bad : "§d调律版代价：§7" + a.bad;
    }

    /** stage 3: the 调律 page's state / click line */
    private String altAction(PlayerData d, EmberSignature.Def sd, boolean cleared) {
        if (EmberSignature.alt(sd) == null) return "§8这件没有调律版";
        if (!runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) return "§8首通 " + mapLabel(EmberSignature.ALT_UNLOCK) + " 后开放签名调律";
        if (!cleared) return "§8先首通 " + mapLabel(sd.map);
        if (!altUnlocked(d, sd)) {
            int mk = d.periodCount(EmberSignature.C_MARK + sd.map, "all");
            return (mk >= EmberSignature.ALT_MARKS ? "§e➥ Shift+点击解锁调律版" : "§7解锁调律版") + " §7（" + EmberSignature.ALT_MARKS + " 枚 "
                    + sd.map.toUpperCase(Locale.ROOT) + " 徽记，有 " + mk + "）";
        }
        return sigAlt(d, sd) ? "§d▶ 调律版生效中 §e➥ 点击换回原版" : "§a▶ 原版生效中 §e➥ 点击换成调律版";
    }

    private String sigStatus(PlayerData d, EmberLoadout lo, EmberSignature.Def sd, boolean cleared) {
        String ex = !sd.excl.isEmpty() && picks(d).values().contains(sd.excl) ? " §c· 与已选天赋同类，不生效" : "";
        for (String slot : new String[]{"blade", "charm"}) {
            EmberItemData it = "blade".equals(slot) ? lo.blade : lo.charm;
            if (it != null && sigOf(d, it) == sd.code) {
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
        int cur = t == null ? 0 : sigOf(d, t), marks = d.periodCount(EmberSignature.C_MARK + sd.map, "all");
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
        EmberSignature.Def c = EmberSignature.byCode(sigOf(d, it));
        return c == null ? " §8（无签名）" : " §7· 现在：§6" + c.name;
    }

    private boolean sigTest(org.bukkit.command.CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        String w = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "";
        String usage = P + "/corerpg p1 sig test marks <q01..> <n> [玩家] | stamp <blade|charm> <Lxx> [玩家] | alt <Lxx> <0|1> [玩家] | show [玩家] | clear [玩家]";
        int pi = "marks".equals(w) ? 6 : "stamp".equals(w) ? 6 : "alt".equals(w) ? 6 : 4;
        Player t = args.length > pi ? Bukkit.getPlayerExact(args[pi]) : (s instanceof Player ? (Player) s : null);
        if (t == null) { s.sendMessage(usage); return true; }
        PlayerData d = data(t.getUniqueId());
        if (d == null) { s.sendMessage(P + "数据还没加载好"); return true; }
        EmberLoadout lo = runs.loadouts().get(t);
        if ("marks".equals(w) && args.length >= 6) {
            int n;
            try { n = Integer.parseInt(args[5]); } catch (NumberFormatException e) { s.sendMessage(usage); return true; }
            d.addPeriodCount(EmberSignature.C_MARK + args[4].toLowerCase(Locale.ROOT), "all", n); // econ-ok: admin test hook
        } else if ("stamp".equals(w) && args.length >= 6) {
            EmberItemData it = "charm".equalsIgnoreCase(args[4]) ? lo.charm : lo.blade;
            EmberSignature.Def sd = EmberSignature.byId(args[5]);
            if (it == null || sd == null || !EmberSignature.fits(sd, it.slot, it.family)) { s.sendMessage(P + "没有这件 / 签名不合这件"); return true; }
            sigTestWrite(s, t, d, it, sd.code); // D208: on the item (async item transaction)
        } else if ("alt".equals(w) && args.length >= 6) { // stage 3: unlock + pick the 调律 version without insignia (smoke)
            EmberSignature.Def sd = EmberSignature.byId(args[4]);
            if (EmberSignature.alt(sd) == null) { s.sendMessage(P + "没有这条签名的调律版"); return true; }
            boolean on = "1".equals(args[5]);
            d.addPeriodCount(EmberSignature.C_ALTU + sd.id, "all", 1 - d.periodCount(EmberSignature.C_ALTU + sd.id, "all"));
            d.addPeriodCount(EmberSignature.C_ALT + sd.id, "all", (on ? 1 : 0) - d.periodCount(EmberSignature.C_ALT + sd.id, "all"));
        } else if ("show".equals(w)) {
            // read only
        } else if ("clear".equals(w)) {
            for (String mk : EmberSignature.maps()) {
                d.addPeriodCount(EmberSignature.C_MARK + mk, "all", -d.periodCount(EmberSignature.C_MARK + mk, "all")); // econ-ok: admin test hook
            }
            for (EmberItemData it : new EmberItemData[]{lo.blade, lo.charm}) if (it != null && sigOf(d, it) > 0) sigTestWrite(s, t, d, it, 0); // D208
            for (String sl : new String[]{"blade", "charm"}) d.addPeriodCount(C_SIGOFF + sl, "all", -d.periodCount(C_SIGOFF + sl, "all"));
            for (EmberSignature.Def sd : EmberSignature.DEFS) d.addPeriodCount(C_SIGSEEN + sd.id, "all", -d.periodCount(C_SIGSEEN + sd.id, "all"));
            for (String id : EmberSignature.ALTS.keySet()) {
                d.addPeriodCount(EmberSignature.C_ALT + id, "all", -d.periodCount(EmberSignature.C_ALT + id, "all"));
                d.addPeriodCount(EmberSignature.C_ALTU + id, "all", -d.periodCount(EmberSignature.C_ALTU + id, "all"));
            }
        } else { s.sendMessage(usage); return true; }
        runs.flushData(t.getUniqueId());
        s.sendMessage(P + t.getName() + " · " + wornLine(d, lo, "blade") + " §7| " + wornLine(d, lo, "charm"));
        StringBuilder sb = new StringBuilder();
        for (String mk : EmberSignature.maps()) sb.append(mk).append('=').append(d.periodCount(EmberSignature.C_MARK + mk, "all")).append(' ');
        StringBuilder seen = new StringBuilder();
        for (EmberSignature.Def sd : EmberSignature.DEFS) if (sigSeen(d, sd)) seen.append(sd.id).append(' ');
        s.sendMessage(P + "徽记 " + sb + "· off blade=" + sigOff(d, "blade") + " charm=" + sigOff(d, "charm") + " · seen " + seen + "· active "
                + sigKey(d, signatures(d, lo)) + " · 调律 " + altKey(d) + " · mods " + mods(t));
        return true;
    }

    /** D208 admin smoke: set / clear the signature of a worn piece through an item transaction */
    private void sigTestWrite(final org.bukkit.command.CommandSender s, Player t, PlayerData d, EmberItemData it, final int code) {
        final EmberItemData after = next(d, it, affixOf(d, it), afPityOf(d, it), code, EmberItemKeys.rerollN(d, it));
        commitOnItem(t.getUniqueId(), "sigtest", "sigtest:" + it.uid + ":" + it.rev, java.util.Collections.singletonList(new EmberItemStore.TxnItem(it, after, null)),
                null, "管理员测试签名 " + code, ok -> s.sendMessage(P + "签名测试写入 " + it.uid.substring(0, 8) + " = " + code + (ok ? " §a已提交（物品 rev " + after.rev + "）" : " §c未提交")));
    }

    private String sigKey(PlayerData d, List<EmberSignature.Def> sg) {
        StringBuilder sb = new StringBuilder();
        for (EmberSignature.Def x : sg) sb.append(x.code).append(sigAlt(d, x) ? "b" : "").append(',');
        return sb.toString();
    }

    /** "L01:b L02:u" — unlocked 调律 versions (b = in effect, u = unlocked, original in effect) */
    private String altKey(PlayerData d) {
        StringBuilder sb = new StringBuilder();
        for (String id : EmberSignature.ALTS.keySet())
            if (d.periodCount(EmberSignature.C_ALTU + id, "all") > 0) sb.append(id).append(':').append(d.periodCount(EmberSignature.C_ALT + id, "all") > 0 ? 'b' : 'u').append(' ');
        return sb.length() == 0 ? "- " : sb.toString();
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
            if (EmberAffix.duplicateOk(t, x, invested(d, x)) == null) return i; // D174: signature = invested
        }
        return -1;
    }

    /** D159: first eligible gear-library duplicate (unlocked / unfavourited); null when none or lib not ready. */
    private EmberStorageRules.Entry findLibDup(Player p, PlayerData d, EmberItemData t) {
        EmberGearLib lib = EmberGearLib.get();
        if (lib == null) return null;
        return lib.findDupForReroll(p, t, x -> invested(d, x)); // D174: signature = invested (D208: item keys on the row)
    }

    int rerollCoin(Player p, EmberItemData t) {
        return (int) Math.round(reroll.coinFor(t.tier) * mods(p).get("reroll_coin"));
    }

    /** /corerpg p1 reroll [from gear|hub|forge] | back | blade|charm dup|shard [lock] [confirm] | keep new|old */
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
        int cap = reroll.cap(t.quality), pity = afPityOf(d, t);
        // D148 lock: keep the current affix, roll only the tier, for lock_shard extra shards
        EmberAffix.Def cur = EmberAffix.decodeDef(reroll, affixOf(d, t));
        int lockShard = reroll.lockShardFor(t.tier);
        if (lock) {
            if (lockShard <= 0) { p.sendMessage(P + "§c锁定词条没有开放"); return true; }
            if (cur == null) { p.sendMessage(P + "§c词条槽还是空的，没有可锁定的词条（先普通洗一次）"); return true; }
            if (!cur.rollable) { p.sendMessage(P + "§c" + cur.name + " 已移出洗练池，不能锁定再洗（已有的照常生效；普通洗会换成别的词条）"); return true; }
            if (Math.min(EmberAffix.decodeTier(affixOf(d, t)), cap) >= cap) { p.sendMessage(P + "§c" + cur.name + " 已经是这件成色的上限档（" + cap + " 档），锁定再洗没有意义"); return true; }
        }
        int needShard = (dup ? 0 : shard) + (lock ? lockShard : 0);
        String mode = (dup ? "dup" : "shard") + (lock ? " lock" : "");
        if (!confirm) {
            p.sendMessage(P + "§6词条洗练 · " + t.shortLabel());
            p.sendMessage(P + "现在：" + affixText(affixOf(d, t), t.quality) + " §7· 成色" + EmberItemData.qualityName(t.quality) + "最高 §f" + cap + " §7档 · 保底 §f" + pity + "/" + reroll.pity
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
            if (!lock && cur != null && cur.rollable && lockShard > 0 && Math.min(EmberAffix.decodeTier(affixOf(d, t)), cap) < cap)
                btn.add(new String[]{"[锁定" + cur.name + "再洗]", "/corerpg p1 reroll " + slot + " " + (dup ? "dup" : "shard") + " lock", "保留「" + cur.name + "」只重抽档位，另加 " + lockShard + " 碎片", "AQUA"});
            btn.add(new String[]{"[回洗练页]", "/corerpg p1 reroll from " + rfromKey(p), "不洗，回去看看", "GRAY"});
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, btn.toArray(new String[0][]));
            return true;
        }
        if (gate(p)) return true;
        if (EmberBrandRules.chargesLeft(afcUsed(d, t)) <= 0) {
            p.sendMessage(P + "§c锻造次数已用完，只能换底子（洗练与烙纹定向共用）");
            return true;
        }
        final UUID id = p.getUniqueId();
        if (d.periodCount(EmberPayRules.C_RRO + t.uid, "all") > 0) { recoverRolls(p, 0); p.sendMessage(P + "§c这件上一次洗练已付款、结果还在处理（几秒后自动出结果）"); return true; } // pre-1.65.42 owed roll
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c洗练暂不可用（服务未就绪）"); return true; }
        if (!t.itemKeys() && d.isLoadFailed()) { p.sendMessage(P + "§c数据没加载完整，稍后再试"); return true; }
        if (!rerollBusy.add(id)) { p.sendMessage(P + "§c上一次洗练还在处理"); return true; }
        if (dup && !haveDup) { rerollBusy.remove(id); p.sendMessage(P + "§c背包或装备库里没有可用的重复件"); return true; }
        final EmberItemData dupData = !dup ? null : di >= 0 ? runs.loadouts().items().read(p.getInventory().getItem(di)).data : libDup.d;
        final int dupIndex = di;
        final String lockPaid = lock ? " + 锁定 " + lockShard + " 碎片" : "";
        final String paid = coin + " 币 + " + (dup ? "重复件" : shard + " 碎片") + lockPaid;
        // D208 (ARCH S1-4 · REG §4-4): the durable payment path of D172, with the roll INSIDE the settling transaction.
        // Refund hold → coins / shards taken in one save → ONE item transaction (the target's new affix / pity / reroll
        // sequence on cr_p1_item + the ledger row + the void of the hold, plus the duplicate's retire when one is eaten)
        // → stack rewritten. The roll is seeded by the request id and computed before the commit; nothing is "owed"
        // afterwards (no p4_rro_), so a disconnect / kill at any step means: refunded once, or rolled once on the item.
        final int n = EmberItemKeys.rerollN(d, t) + 1;
        final String rid = EmberPayRules.rerollRid(t.uid, n, System.currentTimeMillis());
        final RollPlan plan = plan(d, t, id, rid, lock, n);
        if (plan == null) { rerollBusy.remove(id); p.sendMessage(P + "§c这个部位没有词条池"); return true; }
        pendingRoll.remove(id); // an older 保留新 / 保留旧 choice is void once a new roll is paid
        final EmberPay.Price price = EmberPay.Price.of(new EmberUpgradeRules.Cost(needShard, 0, 0, 0, coin)).at(EmberEconomy.sinkForForge("reroll")); // D218 C11
        pay.pay(p, rid, price, "洗练没完成，退回", null, () -> settleReroll(id, t, rid, dup, dupIndex, dupData, price, paid, plan),
                err -> { rerollBusy.remove(id); if (p.isOnline()) p.sendMessage(P + "§c没有洗练：" + err); });
        return true;
    }

    /** D208: one paid roll, computed before its transaction: the target's data after it, the roll, new / old affix */
    static final class RollPlan {
        final EmberItemData after; final EmberAffix.Roll roll; final int enc, old, pity; final boolean lock;
        RollPlan(EmberItemData after, EmberAffix.Roll roll, int enc, int old, int pity, boolean lock) { this.after = after; this.roll = roll; this.enc = enc; this.old = old; this.pity = pity; this.lock = lock; }
    }

    /**
     * D208: the roll of request {@code rid} on piece {@code t} (legacy counters folded in): seeded by the request id, so
     * the same request always gives the same roll. An empty slot takes the roll; otherwise the old affix stays and the
     * candidate waits for 保留新 / 保留旧. Pity and the reroll sequence n move in the same item write. null = no pool.
     */
    private RollPlan plan(PlayerData d, EmberItemData t, UUID id, String rid, boolean lock, int n) {
        int old = affixOf(d, t), pity = afPityOf(d, t);
        String lockId = null;
        if (lock) { EmberAffix.Def cur = EmberAffix.decodeDef(reroll, old); lockId = cur == null ? null : cur.id; }
        EmberAffix.Roll r = EmberAffix.roll(reroll, t.slot, t.quality, pity, new java.util.Random(EmberPayRules.seed(salt(), id.toString(), rid)), lockId);
        if (r == null) return null;
        int enc = EmberAffix.encode(reroll.def(r.id), r.tier);
        return new RollPlan(next(d, t, old == 0 ? enc : old, r.pityAfter, sigOf(d, t), n), r, enc, old, pity, lockId != null);
    }

    /** D172 → D208: the item transaction that makes the payment final AND carries the roll (or the refund, if it did not commit) */
    private void settleReroll(final UUID id, final EmberItemData t, final String rid, boolean dup, int dupIndex, EmberItemData dupData,
                              final EmberPay.Price price, final String paid, final RollPlan plan) {
        final EmberPay pay = EmberPay.get();
        final EmberItemStore.TxnItem target = new EmberItemStore.TxnItem(t, plan.after, null);
        final String note = "洗练 " + t.shortLabel() + "（" + paid + "）→ " + plan.roll.id + " t" + plan.roll.tier;
        final java.util.function.Consumer<Boolean> after = committed -> {
            rerollBusy.remove(id);
            if (committed) {
                pay.settled(id, rid);
                rolled(id, t, rid, plan, paid, false);
                return;
            }
            if (pay.durable()) pay.release(id, rid); // not committed → the coins / shards come back once
            else pay.refundInMemory(Bukkit.getPlayer(id), price);
            Player q = Bukkit.getPlayer(id);
            if (q != null) q.sendMessage(P + "§c洗练没完成（词条没变），" + paid.replace(" + 重复件", "") + " 会退回");
        };
        Player p = Bukkit.getPlayer(id);
        if (!dup) { // works while the owner is offline (after_pay + disconnect): the join resync rewrites the stack
            commitOnItem(id, "reroll", rid, java.util.Collections.singletonList(target), price.json(), note, after);
            return;
        }
        if (p == null) { after.accept(false); return; }
        if (dupIndex >= 0) { // inventory duplicate: the forge commit retires it and rewrites the target stack in one go
            plugin.getEmberForge().consumeForReroll(p, dupIndex, dupData, note + " 用掉重复件 " + dupData.shortLabel(), rid, target, price.json(), after);
            return;
        }
        final org.bukkit.inventory.ItemStack st = runs.loadouts().items().create(plan.after);
        if (st == null) { after.accept(false); return; }
        EmberGearLib.get().consumeForReroll(p, dupData.uid, note + " 用掉装备库重复件 " + dupData.shortLabel(), rid, target, price.json(), ok -> {
            if (ok) {
                Map<String, org.bukkit.inventory.ItemStack> b = new HashMap<String, org.bukkit.inventory.ItemStack>();
                b.put(plan.after.uid, st);
                itemsCommitted(id, java.util.Collections.singletonList(target), b);
            }
            after.accept(ok);
        });
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
     * D208: a paid roll committed on the item — log it (also when the owner is already offline) and show it: an empty slot
     * took it; otherwise the candidate waits for 保留新 / 保留旧 (not choosing = keep the old one, cost not refunded).
     */
    private void rolled(UUID id, EmberItemData t, String rid, RollPlan pl, String paid, boolean recovered) {
        Player p = Bukkit.getPlayer(id);
        EmberAffix.Roll r = pl.roll;
        plugin.getLogger().info("[P1 growth] " + (p == null ? id.toString() : p.getName()) + " reroll " + t.uid + " " + rid + " " + paid + " → " + r.id + " t" + r.tier
                + (pl.lock ? " (lock)" : "") + (r.forced ? " (pity)" : "") + " pity " + pl.pity + "→" + r.pityAfter + " n=" + pl.after.rerollN + " rev=" + pl.after.rev
                + (pl.old == 0 ? " installed" : " candidate") + (recovered ? " (recovered at join)" : "") + (p == null ? " (owner offline, on the item)" : ""));
        if (!recovered) afcBump(id, data(id), t); // D429: 洗练与定向共用锻造次数
        if (p == null) return;
        int quality = t.quality;
        if (recovered) p.sendMessage(P + "§e上次洗练（掉线 / 重启前已付款）的结果：");
        p.sendMessage(P + "§6洗练结果：" + affixText(pl.enc, quality) + (r.forced ? " §a（保底）" : "") + " §7· 保底 " + r.pityAfter + "/" + reroll.pity);
        if (pl.old == 0) {
            pendingRoll.remove(id);
            p.sendMessage(P + "§a词条槽原来是空的，直接装上了");
        } else {
            pendingRoll.put(id, new Object[]{t.uid, t.slot, pl.enc, pl.old, quality});
            p.sendMessage(P + "原来：" + affixText(pl.old, quality));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[保留新]", "/corerpg p1 reroll keep new", "换成：" + affixText(pl.enc, quality).replaceAll("§.", ""), "GREEN"},
                    new String[]{"[保留旧]", "/corerpg p1 reroll keep old", "不换（不选也等于保留旧，花费不退）", "GRAY"});
        }
        if (!recovered) openMenu(p, REROLL_MENU);
    }

    /** D172: settle every pre-1.65.42 "roll owed" counter of a player (join, or after a refunded attempt) */
    void recoverRolls(final UUID id, final int attempt) {
        Player p = Bukkit.getPlayer(id);
        if (p != null) recoverRolls(p, attempt);
    }

    /**
     * D172 → D208: legacy recovery only. 1.65.42 never writes {@code p4_rro_} (the roll commits with the payment); a
     * counter left by an older version is settled once: its request committed → the roll (same seed) is written on the
     * item in an item transaction and the counter dropped; hold still open → retry; neither → dropped (refunded).
     */
    private void recoverRolls(final Player p, final int attempt) {
        final UUID id = p.getUniqueId();
        PlayerData d = data(id);
        if (d == null || !p.isOnline() || reroll == null) return;
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
                            PlayerData x2 = data(id);
                            if (Bukkit.getPlayer(id) == null || x2 == null || x2.periodCount(EmberPayRules.C_RRO + uid, "all") != v) return;
                            if (row == null || !id.toString().equals(row.owner) || !("active".equals(row.state) || "stored".equals(row.state))) {
                                x2.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -v);
                                plugin.getDataStore().save(id, x2);
                                plugin.getLogger().warning("[P1 growth] " + id + " reroll " + rid + " owed on a missing item, dropped");
                                return;
                            }
                            final RollPlan pl = plan(x2, row.data, id, rid, EmberPayRules.owedLock(v), EmberPayRules.owedSeq(v));
                            if (pl == null) { x2.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -v); plugin.getDataStore().save(id, x2); return; }
                            commitOnItem(id, "reroll", "afxrec:" + uid + ":" + EmberPayRules.owedSeq(v), java.util.Collections.singletonList(
                                    new EmberItemStore.TxnItem(row.data, pl.after, null, row.state)), null, "洗练（升级前已付款）→ " + pl.roll.id + " t" + pl.roll.tier, ok -> {
                                PlayerData x3 = data(id);
                                if (!ok || x3 == null) return; // retried at the next join
                                x3.addPeriodCount(EmberPayRules.C_RRO + uid, "all", -x3.periodCount(EmberPayRules.C_RRO + uid, "all"));
                                plugin.getDataStore().save(id, x3);
                                EmberPay pay = EmberPay.get();
                                if (pay != null) pay.settled(id, rid);
                                rolled(id, row.data, rid, pl, "已付款", true);
                            });
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
        final String uid = (String) pr[0], slot = (String) pr[1];
        final int enc = (Integer) pr[2], old = (Integer) pr[3], q = (Integer) pr[4];
        if (!takeNew) { p.sendMessage(P + "§7保留原来的：" + affixText(old, q)); openMenu(p, REROLL_MENU); return true; }
        EmberItemData cur = target(p, slot);
        if (cur == null || !cur.uid.equals(uid) || affixOf(d, cur) != old) { p.sendMessage(P + "§c这件的词条已经变了（或这件已不在用），结果作废"); return true; }
        // D208: 保留新 = one item transaction on the piece (affix on cr_p1_item + NBT); nothing is paid here
        final UUID id = p.getUniqueId();
        final EmberItemData after = next(d, cur, enc, afPityOf(d, cur), sigOf(d, cur), EmberItemKeys.rerollN(d, cur));
        commitOnItem(id, "afxkeep", "afxkeep:" + uid + ":" + cur.rev, java.util.Collections.singletonList(new EmberItemStore.TxnItem(cur, after, null)),
                null, "洗练保留新 " + cur.shortLabel() + " " + old + "→" + enc, ok -> {
                    Player x = Bukkit.getPlayer(id);
                    plugin.getLogger().info("[P1 growth] " + (x == null ? id.toString() : x.getName()) + " reroll keep new " + uid + " " + old + "→" + enc + (ok ? " rev " + after.rev : " NOT committed"));
                    if (x == null) return;
                    if (ok) x.sendMessage(P + "§a已换成：" + affixText(enc, q));
                    else x.sendMessage(P + "§c没换成（物品刚被改过或数据库暂时写不进去），原来的词条不变");
                    openMenu(x, REROLL_MENU);
                });
        return true;
    }

    private String rfromKey(Player p) { String f = RFROM.get(p.getUniqueId()); return f == null ? "none" : f; }

    /** key after "reroll_" */
    public String rerollPapi(Player p, PlayerData d, String key) {
        if (reroll == null || d == null) return "";
        if ("back".equals(key)) {
            String f = RFROM.get(p.getUniqueId());
            return "gear".equals(f) ? "§7返回装备页" : "hub".equals(f) ? "§7返回主菜单" : "forge".equals(f) ? "§7返回工坊" : "§7关闭";
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
        if (t == null) {
            if ("item".equals(k)) return "charm".equals(slot) ? "§8还没有选定的护符" : "§8主手没有余烬刃（拿在主手再打开）";
            if ("pity".equals(k)) return EmberRerollPity.emptySlot("charm".equals(slot) ? "护符" : "刃"); // D458
            return "";
        }
        if ("item".equals(k)) return "§f" + t.shortLabel();
        if ("pity".equals(k)) { // D458 one-glance before eligible gate; math unchanged
            if (EmberAffix.eligible(t) != null) return EmberRerollPity.emptySlot("不可洗");
            return EmberRerollPity.glance(afPityOf(d, t), reroll.pity);
        }
        if (EmberAffix.eligible(t) != null) return "cur".equals(k) ? "§8" + EmberAffix.eligible(t) : "";
        if ("cur".equals(k)) return "§7词条：" + affixText(affixOf(d, t), t.quality);
        if ("cap".equals(k)) {
            int pity = afPityOf(d, t);
            return "§7成色" + EmberItemData.qualityName(t.quality) + " · 最高 §f" + reroll.cap(t.quality) + " §7档 · 保底 §f" + pity + "/" + reroll.pity;
        }
        if ("dup".equals(k)) {
            if (findDup(p, d, t) >= 0) return "§a背包里有重复件 §7+ " + rerollCoin(p, t) + " 币";
            if (findLibDup(p, d, t) != null) return "§a装备库里有重复件 §7+ " + rerollCoin(p, t) + " 币";
            return "§8背包或装备库里没有重复件（同部位 T" + t.tier + "、掉落来的、没投入、未锁定/收藏）";
        }
        if ("shard".equals(k)) return "§7" + reroll.shardFor(t.tier) + " 碎片 + " + rerollCoin(p, t) + " 币";
        if ("lock".equals(k)) { // D148
            EmberAffix.Def cur = EmberAffix.decodeDef(reroll, affixOf(d, t));
            int ls = reroll.lockShardFor(t.tier);
            if (ls <= 0) return "";
            if (cur == null) return "§8锁定词条：词条槽还是空的";
            if (!cur.rollable) return "§8" + cur.name + " 已移出洗练池，不能锁定（普通洗会换成别的词条）";
            if (Math.min(EmberAffix.decodeTier(affixOf(d, t)), reroll.cap(t.quality)) >= reroll.cap(t.quality)) return "§a" + cur.name + " 已是上限档";
            return "§bShift+点击：锁定「" + cur.name + "」只重抽档位（另加 " + ls + " 碎片）";
        }
        return "";
    }

    private static List<String> pctList(EmberAffix.Def df) {
        List<String> l = new ArrayList<String>();
        for (double v : df.values) l.add(EmberGrowth.signedPct(v));
        return l;
    }


    // ------------------------------------------------------------------ D429 brand pin / craft

    public int afcUsed(PlayerData d, EmberItemData it) {
        if (d == null || it == null) return 0;
        return d.periodCount(C_AFC + it.uid, "all");
    }

    void afcBump(UUID id, PlayerData d, EmberItemData it) {
        if (d == null || it == null) return;
        d.addPeriodCount(C_AFC + it.uid, "all", 1);
        runs.flushData(id);
    }

    /** /corerpg p1 brand | craft [confirm] | pin <blade|charm> <affixId> [confirm] | menu */
    public boolean brandCommand(Player p, String[] args) {
        if (reroll == null) { p.sendMessage(P + "词条表未加载，烙纹不可用"); return true; }
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好，稍后再试"); return true; }
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (op.isEmpty() || "menu".equals(op)) { openMenu(p, "ember_p1_brand"); return true; }
        if ("craft".equals(op) || "合成".equals(op)) return brandCraft(p, d, args.length >= 4 && "confirm".equalsIgnoreCase(args[args.length - 1]));
        if ("pin".equals(op) || "定向".equals(op)) {
            if (args.length < 5) {
                p.sendMessage(P + "用法：/corerpg p1 brand pin <blade|charm> <词条id> [confirm]");
                return true;
            }
            boolean confirm = "confirm".equalsIgnoreCase(args[args.length - 1]);
            return brandPin(p, d, args[3].toLowerCase(Locale.ROOT), args[4], confirm);
        }
        p.sendMessage(P + "用法：/corerpg p1 brand craft|pin|menu");
        return true;
    }

    private boolean brandCraft(Player p, PlayerData d, boolean confirm) {
        int need = EmberBrandRules.CRAFT_SHARDS;
        if (!confirm) {
            p.sendMessage(P + "§6烙纹合成：§f" + need + " 余烬碎片 §7→ §f1 余烬烙纹");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认合成]", "/corerpg p1 brand craft confirm", "扣 " + need + " 碎片，得 1 烙纹", "GREEN"},
                    new String[]{"[打开烙纹页]", "/corerpg p1 brand menu", "回菜单", "GRAY"});
            return true;
        }
        if (gate(p)) return true;
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c支付服务未就绪"); return true; }
        final UUID id = p.getUniqueId();
        final String rid = "brandcraft:" + id + ":" + System.currentTimeMillis();
        final EmberPay.Price price = EmberPay.Price.of(new EmberUpgradeRules.Cost(need, 0, 0, 0, 0)).at("C20");
        pay.pay(p, rid, price, "烙纹合成没完成，退回碎片", null, () -> {
            boolean ok = plugin.getNiBridge().giveNiItem(p, EmberBrandRules.MAT_BRAND, 1);
            if (!ok) {
                pay.release(id, rid);
                if (p.isOnline()) p.sendMessage(P + "§c烙纹发放失败，碎片已退回");
                return;
            }
            pay.settled(id, rid);
            if (p.isOnline()) p.sendMessage(P + "§a已合成 §f余烬烙纹 ×1 §7（花费 " + need + " 碎片）");
            openMenu(p, "ember_p1_brand");
        }, err -> { if (p.isOnline()) p.sendMessage(P + "§c没有合成：" + err); });
        return true;
    }

    private boolean brandPin(Player p, PlayerData d, String slot, String affixId, boolean confirm) {
        if (!"blade".equals(slot) && !"charm".equals(slot)) {
            p.sendMessage(P + "部位只能是 blade 或 charm");
            return true;
        }
        EmberItemData t = target(p, slot);
        if (t == null) {
            p.sendMessage(P + "§c" + ("charm".equals(slot) ? "还没有选定的护符" : "主手没有余烬刃"));
            return true;
        }
        String whyElig = EmberAffix.eligible(t);
        if (whyElig != null) { p.sendMessage(P + "§c" + whyElig); return true; }
        int used = afcUsed(d, t);
        String refuse = EmberBrandRules.pinRefusal(t.tier, used, affixId, reroll, t.slot);
        if (refuse != null) { p.sendMessage(P + "§c" + refuse); return true; }
        int brands = EmberBrandRules.pinBrands(t.tier);
        int coins = EmberBrandRules.pinCoins(t.tier);
        EmberAffix.Def want = reroll.def(affixId);
        int left = EmberBrandRules.chargesLeft(used);
        if (!confirm) {
            p.sendMessage(P + "§6烙纹定向 · " + t.shortLabel());
            p.sendMessage(P + "现在：" + affixText(affixOf(d, t), t.quality) + " §7· 锻造次数剩余 §f" + left + "/" + EmberBrandRules.MAX_CHARGES);
            p.sendMessage(P + "写入类型：§b" + want.name + " §7（档位仍随机，成色上限截断）");
            p.sendMessage(P + "花费：§f" + brands + " 烙纹 + " + coins + " 余烬币");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认定向]", "/corerpg p1 brand pin " + slot + " " + affixId + " confirm", "扣烙纹与币，写入「" + want.name + "」", "GREEN"},
                    new String[]{"[回烙纹页]", "/corerpg p1 brand menu", "不扣", "GRAY"});
            return true;
        }
        if (gate(p)) return true;
        town.sunshine.corerpg.NiBridge ni = plugin.getNiBridge();
        if (ni.countInInventory(p, EmberBrandRules.MAT_BRAND) < brands) {
            p.sendMessage(P + "§c烙纹不足（需要 " + brands + "，可先合成）");
            return true;
        }
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c支付服务未就绪"); return true; }
        final UUID id = p.getUniqueId();
        final int n = EmberItemKeys.rerollN(d, t) + 1;
        final String rid = "brandpin:" + t.uid + ":" + n + ":" + System.currentTimeMillis();
        final EmberPay.Price price = EmberPay.Price.of(new EmberUpgradeRules.Cost(0, 0, 0, 0, coins)).at("C21");
        if (!ni.consumeExact(p, EmberBrandRules.MAT_BRAND, brands)) {
            p.sendMessage(P + "§c烙纹扣除失败");
            return true;
        }
        pay.pay(p, rid, price, "烙纹定向没完成，退回币", null, () -> {
            EmberAffix.Roll r = EmberAffix.roll(reroll, t.slot, t.quality, afPityOf(d, t),
                    new java.util.Random(EmberPayRules.seed(salt(), id.toString(), rid)), affixId);
            if (r == null) {
                ni.giveNiItem(p, EmberBrandRules.MAT_BRAND, brands);
                pay.release(id, rid);
                if (p.isOnline()) p.sendMessage(P + "§c词条池异常，已退回");
                return;
            }
            int enc = EmberAffix.encode(reroll.def(r.id), r.tier);
            final EmberItemData after = next(d, t, enc, r.pityAfter, sigOf(d, t), n);
            commitOnItem(id, "brandpin", rid, java.util.Collections.singletonList(new EmberItemStore.TxnItem(t, after, null)),
                    price.json(), "烙纹定向 " + t.shortLabel() + " → " + r.id + " t" + r.tier, ok -> {
                        if (!ok) {
                            ni.giveNiItem(p, EmberBrandRules.MAT_BRAND, brands);
                            pay.release(id, rid);
                            if (p.isOnline()) p.sendMessage(P + "§c定向未写入，材料与币退回");
                            return;
                        }
                        pay.settled(id, rid);
                        PlayerData nd = data(id);
                        afcBump(id, nd, after);
                        Player q = Bukkit.getPlayer(id);
                        if (q != null) {
                            q.sendMessage(P + "§a定向完成：" + affixText(enc, t.quality)
                                    + " §7· 锻造次数剩余 §f" + EmberBrandRules.chargesLeft(afcUsed(nd, after))
                                    + "/" + EmberBrandRules.MAX_CHARGES);
                            openMenu(q, "ember_p1_brand");
                        }
                    });
        }, err -> {
            ni.giveNiItem(p, EmberBrandRules.MAT_BRAND, brands);
            if (p.isOnline()) p.sendMessage(P + "§c没有定向：" + err + "（烙纹已退回）");
        });
        return true;
    }



    // ------------------------------------------------------------------ D431 weekly convert

    /** /corerpg p1 convert <fam> [confirm] — held blade/charm → other family; weekly cap 1 */
    public boolean convertCommand(Player p, String[] args) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好，稍后再试"); return true; }
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (op.isEmpty() || "menu".equals(op)) { openMenu(p, "ember_p1_convert"); return true; }
        if ("scorch".equals(op) || "burst".equals(op) || "sustain".equals(op)) {
            boolean confirm = args.length >= 4 && "confirm".equalsIgnoreCase(args[args.length - 1]);
            return convertHeld(p, d, op, confirm);
        }
        p.sendMessage(P + "用法：/corerpg p1 convert <scorch|burst|sustain> [confirm]（手持刃/护符）");
        return true;
    }

    private EmberItemData heldGear(Player p) {
        org.bukkit.inventory.ItemStack st = p.getInventory().getItemInMainHand();
        if (st == null || !runs.loadouts().items().hasData(st)) return null;
        EmberItems.Read r = runs.loadouts().items().read(st);
        if (r == null || !r.ok() || r.data == null) return null;
        if (!"blade".equals(r.data.slot) && !"charm".equals(r.data.slot)) return null;
        return r.data;
    }

    private boolean convertHeld(Player p, PlayerData d, String toFam, boolean confirm) {
        EmberItemData t = heldGear(p);
        String week = town.sunshine.corerpg.DailyService.weekId();
        int used = d.periodCount(EmberConvertRules.C_CONV, week);
        String flag = EmberRunRules.directedForgeFlag(t == null ? 1 : t.tier);
        boolean gate = t == null || t.tier <= 1 || (flag == null) || runs.progressFlag(d, flag);
        String refuse = EmberConvertRules.refusal(t, toFam, used, gate);
        if (refuse != null) { p.sendMessage(P + "§c" + refuse); return true; }
        int blanks = EmberConvertRules.blanks(t.tier);
        int coins = EmberConvertRules.coins(t.tier);
        int qAfter = EmberConvertRules.qualityAfter(t.quality);
        if (!confirm) {
            p.sendMessage(P + "§6每周转化 · " + t.shortLabel());
            p.sendMessage(P + "目标族：§f" + EmberItemData.familyName(toFam)
                    + " §7· 本周剩余 §f" + (EmberConvertRules.WEEKLY_CAP - used) + "/" + EmberConvertRules.WEEKLY_CAP);
            p.sendMessage(P + "成色：§f" + EmberItemData.qualityName(t.quality) + " §7→ §f" + EmberItemData.qualityName(qAfter)
                    + (t.quality > EmberConvertRules.QUALITY_CAP ? " §c（极品截到卓越）" : "")
                    + " §7· 强化 §f+" + t.enhance + " §7→ §f+0");
            p.sendMessage(P + "保留：精工/词条/保底/锻造次数 · uid 不变");
            p.sendMessage(P + "花费：§f" + blanks + " 胚料 + " + coins + " 余烬币");
            if (t.enhance > 0) p.sendMessage(P + "§e提示：可先用工坊「互换」挪走强化再转化");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认转化]", "/corerpg p1 convert " + toFam + " confirm", "扣胚料与币，改族", "GREEN"},
                    new String[]{"[回转化页]", "/corerpg p1 convert menu", "不扣", "GRAY"});
            return true;
        }
        if (gate(p)) return true;
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c支付服务未就绪"); return true; }
        final UUID id = p.getUniqueId();
        final String rid = "fconv:" + t.uid.substring(0, Math.min(8, t.uid.length())) + ":"
                + Long.toString(System.currentTimeMillis(), 36);
        final EmberItemData planned = EmberConvertRules.plan(t, toFam).withRev(t.rev + 1);
        final EmberPay.Price price = EmberPay.Price.of(EmberConvertRules.cost(t.tier)).at("C23");
        final EmberItemData before = t;
        final int usedNow = used;
        pay.pay(p, rid, price, "转化没完成，退回", null, () -> {
            commitOnItem(id, "forge_conv", rid, java.util.Collections.singletonList(new EmberItemStore.TxnItem(before, planned, null)),
                    price.json(), "每周转化 " + before.shortLabel() + " → " + EmberItemData.familyName(toFam), ok -> {
                        if (!ok) {
                            pay.release(id, rid);
                            Player q = Bukkit.getPlayer(id);
                            if (q != null) q.sendMessage(P + "§c转化未写入，材料退回");
                            return;
                        }
                        pay.settled(id, rid);
                        PlayerData nd = data(id);
                        if (nd != null) {
                            nd.addPeriodCount(EmberConvertRules.C_CONV, week, 1);
                            runs.flushData(id);
                        }
                        Player q = Bukkit.getPlayer(id);
                        if (q != null) {
                            q.sendMessage(P + "§a转化完成：§f" + planned.shortLabel()
                                    + " §7· 强化+0 · 本周已用 §f" + (usedNow + 1) + "/" + EmberConvertRules.WEEKLY_CAP);
                            openMenu(q, "ember_p1_convert");
                        }
                    });
        }, err -> { if (p.isOnline()) p.sendMessage(P + "§c没有转化：" + err); });
        return true;
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
