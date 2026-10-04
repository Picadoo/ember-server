package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * D174 签名传奇 (pure rules, no Bukkit) — docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md §2–§3.
 * <p>
 * A signature = an ordinary P1 blade / charm plus ONE behaviour effect from the map boss; the effect is a set of the
 * existing growth modifier keys ({@link EmberGrowth.Mods}) and is stored on the owner's save per item uid
 * ({@link #C_SIG} + uid = code, like the D143 affix). The numbers mirror {@code tools/p1sim/mainline.py SIGS}
 * (checked there: 42 cells alone / in every legal pair / on top of a full talent build).
 * <ul>
 *   <li>at most 2 active: the active blade's + the active charm's; before the own {@link #DUAL_UNLOCK} first clear only 1</li>
 *   <li>same tag → only the blade's; same tag as a picked talent node ({@link Def#excl}) → off</li>
 *   <li>family-bound ones only while that set is active (their keys are set events anyway)</li>
 * </ul>
 * Sources: a repeat NORMAL clear of the map → 1 {@link #C_MARK} insignia + {@link #STAMP_RATE} that the base item is a
 * signature (if its slot / family fit one); the map's first clear → {@link #FC_MARKS} insignia once ({@link #C_FC});
 * 烬炉烙印 (after the own {@link #IMPRINT_UNLOCK} first clear) = {@link #IMPRINT_MARKS} insignia of that map + coins.
 */
public final class EmberSignature {

    public static final String C_SIG = "p1_sig_";        // + item uid, period all: Def.code (0 = none)
    public static final String C_MARK = "p1_sigmark_";   // + map key, period all: 首领徽记 count
    public static final String C_FC = "p1_sigfc_";       // + map key, period all: 1 = first-clear insignia paid (once per map, not per content version)
    public static final double STAMP_RATE = 0.12;
    public static final int CLEAR_MARKS = 1, FC_MARKS = 3, IMPRINT_MARKS = 5, IMPRINT_COIN_PER_TIER = 300;
    public static final String IMPRINT_UNLOCK = "q02", DUAL_UNLOCK = "q03";

    public static final class Def {
        public final int code;
        public final String id, map, slot, family, tag, excl, name, boss, good, bad;
        public final Map<String, Double> mods;
        Def(int code, String id, String map, String slot, String family, String tag, String excl, String name, String boss,
            String good, String bad, Map<String, Double> mods) {
            this.code = code; this.id = id; this.map = map; this.slot = slot; this.family = family; this.tag = tag;
            this.excl = excl; this.name = name; this.boss = boss; this.good = good; this.bad = bad;
            this.mods = Collections.unmodifiableMap(mods);
        }
        public boolean anyFamily() { return "any".equals(family); }
        /** "焚烬刃" / "通用护符" */
        public String kindText() { return (anyFamily() ? "通用" : EmberItemData.familyName(family)) + ("blade".equals(slot) ? "刃" : "护符"); }
    }

    private static Map<String, Double> m(Object... kv) {
        Map<String, Double> out = new LinkedHashMap<String, Double>();
        for (int i = 0; i + 1 < kv.length; i += 2) out.put((String) kv[i], ((Number) kv[i + 1]).doubleValue());
        return out;
    }

    /** stage 1 = Q01–Q03 (live Java keys only), stage 2a = Q04–Q06 (new hooks), stage 3 = Q07 (D184). Codes are stored in saves: never reuse or renumber. */
    public static final List<Def> DEFS = Collections.unmodifiableList(java.util.Arrays.asList(
            new Def(1, "L01", "q01", "blade", "scorch", "burn", "t3a", "残门焚斧", "残门蛮兵",
                    "燃烧中的敌人倒下时，把最多 2 秒燃烧传给 4 格内最近的敌人（10 秒一次）", "焚烬燃烧每跳伤害 ×0.95",
                    m("burn_spread", 2, "spread_icd", 10, "burn_mult", 0.95)),
            new Def(2, "L02", "q01", "charm", "any", "dodgeheal", "t1c", "门楼余烬", "残门蛮兵",
                    "躲开首领预警招回 1% 最大生命（15 秒一次）", "被首领预警招打中多受 4%",
                    m("dodge_heal", 0.01, "dodge_icd", 15, "taken_tele", 1.04)),
            new Def(3, "L03", "q02", "charm", "burst", "hitburst", "t3b", "炉心护符", "守炉蛮兵",
                    "被首领预警招打中时烬爆计数 +1", "被首领预警招打中多受 3%",
                    m("hit_burst", 1, "taken_tele", 1.03)),
            new Def(4, "L04", "q02", "blade", "burst", "dodgeburst", "", "守炉重锤", "守炉蛮兵",
                    "躲开首领预警招时烬爆计数 +1", "对首领伤害 ×0.985", // D183 burst rework: was 被打中 +3% + ×0.98 (pairs −3～−4 pp at low dodge)
                    m("dodge_burst", 1, "dmg_boss", 0.985)),
            new Def(5, "L05", "q03", "blade", "sustain", "mend", "", "残誓长戟", "残誓守卫",
                    "炽愈回复少打 1 下就触发（更勤，每次回复量不变）", "无",
                    m("sustain_every", -1)),
            new Def(6, "L06", "q03", "charm", "any", "supply", "", "守誓残灯", "残誓守卫",
                    "回复药回复量 ×1.05", "受到首领伤害 ×1.04",
                    m("potion", 1.05, "taken_boss", 1.04)),
            // ---- stage 2a · act 2 (T2 maps): 烬斩 variants (SkillService) / 低血回涌 (EmberSetEngine.everyAt) — p1sim mainline.py SIGS
            new Def(7, "L07", "q04", "blade", "any", "shape", "", "潮闸长杆", "潮闸重卫",
                    "烬斩改成直线穿刺：正前方 5 格一条线（宽 1 格）", "最多打 3 个（原来前方扇形 3.5 格、最多 5 个）",
                    m("skill_var", 1, "skill_cap", 3, "skill_line", 5)),
            new Def(8, "L08", "q04", "charm", "scorch", "burn", "", "潮蚀护符", "潮闸重卫",
                    "烬斩点燃命中的第 1 个敌人（焚烬燃烧，和套装点燃同一规则）", "焚烬燃烧每跳伤害 ×0.92",
                    m("skill_var", 1, "skill_ignite", 1, "skill_ignite_n", 1, "skill_burn", 1.0, "burn_mult", 0.92)),
            new Def(9, "L09", "q05", "blade", "any", "shape", "", "断塔双斧", "断塔斧卫",
                    "烬斩改成环斩：身边一圈（不用对准，背后的也打）", "半径 3 格（原来 3.5）、最多打 3 个（原来 5 个）",
                    m("skill_var", 1, "skill_cap", 3, "skill_ring", 3.0)),
            new Def(10, "L10", "q05", "charm", "burst", "dodgeburst", "t3b", "回廊护符", "断塔斧卫",
                    "躲开或被首领预警招打中，烬爆计数都 +1", "烬爆伤害 ×0.93", // D184: ×0.9 → ×0.93 (p2econ 无轮换 W30 +0.75 → +0.20)
                    m("hit_burst", 1, "dodge_burst", 1, "burst_mult", 0.93)),
            new Def(11, "L11", "q06", "blade", "any", "guard", "", "霜封长刀", "霜封统领",
                    "烬斩每命中 1 个敌人得 0.5% 最大生命的护盾（最多 1%，5 秒，不叠加）", "烬斩每下伤害 ×0.9，受到首领伤害 ×1.05",
                    m("skill_var", 1, "skill_shield", 0.005, "skill_shield_max", 0.01, "skill_shield_secs", 5, "skill_mult", 0.9, "taken_boss", 1.05)),
            new Def(12, "L12", "q06", "charm", "sustain", "heal", "", "统领护符", "霜封统领",
                    "生命低于 40% 时，炽愈每 3 下就触发（平时 5 下）", "炽愈每次回复量 ×0.99",
                    m("sustain_low", 0.4, "sustain_low_every", -2, "sustain_mult", 0.99)),
            // ---- stage 3 · Q07 (T3): 烬斩 蓄力 (SkillService skill_charge, D184) / 护盾 (giveSkillShield) / 燃烧多 1 跳 (BurnBook burn_ticks)
            new Def(13, "L13", "q07", "blade", "any", "shape", "", "炉锁巨锤", "炉锁巨卫",
                    "烬斩改成蓄力环斩：按下 0.5 秒后落下，打身边一圈（不用对准，背后的也打）", "蓄力的 0.5 秒里普攻无效（冷却从按下时算）",
                    m("skill_var", 1, "skill_charge", 0.5, "skill_plus", 1)),
            new Def(14, "L14", "q07", "charm", "any", "guard", "", "炉芯护符", "炉锁巨卫",
                    "烬斩每命中 1 个敌人得 0.4% 最大生命的护盾（最多 0.8%，4 秒，不叠加）", "回复药回复量 ×0.95",
                    m("skill_var", 1, "skill_shield", 0.004, "skill_shield_max", 0.008, "skill_shield_secs", 4, "potion", 0.95)), // draft 2% / 6% was +37 pp
            new Def(15, "L15", "q07", "charm", "scorch", "burn2", "", "锈轨余火", "炉锁巨卫",
                    "焚烬燃烧多烧 1 跳（换目标、打小怪群时更划算）", "焚烬燃烧每跳伤害 ×0.97",
                    m("burn_ticks", 1, "burn_mult", 0.97)))); // draft ×0.8: −2.8 pp mean (the extra tick never lands on a boss kept burning)

    // ------------------------------------------------------------------ stage 3 · Q07 签名调律 (D184, design §11)

    /** + Def.id, period all: 1 = the 调律 version is the one in effect (0 = original) */
    public static final String C_ALT = "p1_sigalt_";
    /** + Def.id, period all: 1 = the 调律 version is unlocked (permanent, paid once) */
    public static final String C_ALTU = "p1_sigaltu_";
    public static final String ALT_UNLOCK = "q07";
    public static final int ALT_MARKS = 10;

    /** the 调律 version of a signature: same benefit, another cost (full key set, mirrors tools/p1sim/mainline.py ALTS) */
    public static final class Alt {
        public final String bad;
        public final Map<String, Double> mods;
        Alt(String bad, Map<String, Double> mods) { this.bad = bad; this.mods = Collections.unmodifiableMap(mods); }
    }

    /** L03 no passing alternate (§11.3), L11 held back (D184: L11b W30 P2-1 −0.55 wk, outside ±0.5), L04 just reworked (D183), L05 / L07 / L09 have no cost, L13–L15 new this stage → none */
    public static final Map<String, Alt> ALTS;
    static {
        Map<String, Alt> a = new LinkedHashMap<String, Alt>();
        a.put("L01", new Alt("对首领伤害 ×0.98", m("burn_spread", 2, "spread_icd", 10, "dmg_boss", 0.98)));
        a.put("L02", new Alt("回复药回复量 ×0.95", m("dodge_heal", 0.01, "dodge_icd", 15, "potion", 0.95)));
        a.put("L06", new Alt("被首领预警招打中多受 10%", m("potion", 1.05, "taken_tele", 1.10)));
        a.put("L08", new Alt("对首领伤害 ×0.98", m("skill_var", 1, "skill_ignite", 1, "skill_ignite_n", 1, "skill_burn", 1.0, "dmg_boss", 0.98)));
        a.put("L10", new Alt("被首领预警招打中多受 3%", m("hit_burst", 1, "dodge_burst", 1, "taken_tele", 1.03)));
        a.put("L12", new Alt("受到首领伤害 ×1.02", m("sustain_low", 0.4, "sustain_low_every", -2, "taken_boss", 1.02)));
        ALTS = Collections.unmodifiableMap(a);
    }

    public static Alt alt(Def d) { return d == null ? null : ALTS.get(d.id); }

    /** the key set in effect: the 调律 one only if it exists and {@code useAlt} */
    public static Map<String, Double> modsOf(Def d, boolean useAlt) {
        Alt a = useAlt ? alt(d) : null;
        return a != null ? a.mods : d.mods;
    }

    /** the cost text in effect */
    public static String badOf(Def d, boolean useAlt) {
        Alt a = useAlt ? alt(d) : null;
        return a != null ? a.bad : d.bad;
    }

    /** 调律 precheck (unlock or switch); null = ok. Switching only in town is the caller's gate (same as 烙印). */
    public static String attuneCheck(Def d, boolean q07, boolean mapCleared, boolean unlocked, int marks) {
        if (d == null) return "没有这条签名";
        if (alt(d) == null) return d.name + " 没有调律版";
        if (!q07) return "首通 " + ALT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放签名调律";
        if (!mapCleared) return "先首通 " + d.map.toUpperCase(Locale.ROOT) + " 才能调律它的签名";
        if (!unlocked && marks < ALT_MARKS) return d.map.toUpperCase(Locale.ROOT) + " 首领徽记不够（" + marks + "/" + ALT_MARKS + "）";
        return null;
    }

    public static Def byCode(int code) { for (Def d : DEFS) if (d.code == code) return d; return null; }

    public static Def byId(String id) {
        if (id != null) for (Def d : DEFS) if (d.id.equalsIgnoreCase(id)) return d;
        return null;
    }

    public static List<Def> forMap(String map) {
        List<Def> l = new ArrayList<Def>();
        for (Def d : DEFS) if (d.map.equals(map)) l.add(d);
        return l;
    }

    public static boolean hasMap(String map) { return map != null && !forMap(map).isEmpty(); }

    /** the map keys that have signatures, in story order */
    public static List<String> maps() {
        List<String> l = new ArrayList<String>();
        for (Def d : DEFS) if (!l.contains(d.map)) l.add(d.map);
        return l;
    }

    /** can {@code d} sit on a piece of this slot / family? (family-bound ones only on their own family) */
    public static boolean fits(Def d, String slot, String family) {
        return d != null && d.slot.equals(slot) && (d.anyFamily() || d.family.equals(family));
    }

    /** the signatures a base item of (slot, family) dropped on {@code map} can carry */
    public static List<Def> stampable(String map, String slot, String family) {
        List<Def> l = new ArrayList<Def>();
        for (Def d : forMap(map)) if (fits(d, slot, family)) l.add(d);
        return l;
    }

    /** settlement stamp: {@code u} in [0,1) from the run seed; below {@link #STAMP_RATE} one of the fitting ones (uniform) */
    public static Def rollStamp(String map, String slot, String family, double u) {
        List<Def> l = stampable(map, slot, family);
        if (l.isEmpty() || !(u >= 0.0) || u >= STAMP_RATE) return null;
        int i = (int) Math.floor(u / STAMP_RATE * l.size());
        return l.get(Math.max(0, Math.min(l.size() - 1, i)));
    }

    /** one worn piece: its signature (or null) + the piece's slot / family */
    public static final class Worn {
        public final Def def;
        public final String slot, family;
        public Worn(Def def, String slot, String family) { this.def = def; this.slot = slot; this.family = family; }
    }

    /** why a worn signature is off (null = on); same order as {@link #active} */
    public static String offReason(Worn w, Worn other, String activeSet, Collection<String> talentPicks, boolean dual) {
        if (w == null || w.def == null) return "无";
        Def d = w.def;
        if (!fits(d, w.slot, w.family)) return "部位 / 族不对";
        if (!d.anyFamily() && !d.family.equals(activeSet)) return "需要" + EmberItemData.familyName(d.family) + "成套";
        if (!d.excl.isEmpty() && talentPicks != null && talentPicks.contains(d.excl)) return "与已选天赋同类，不叠加";
        if ("charm".equals(w.slot) && other != null && other.def != null && offReason(other, null, activeSet, talentPicks, true) == null) {
            if (!dual) return "首通 " + DUAL_UNLOCK.toUpperCase(Locale.ROOT) + " 后才能同时生效 2 条";
            if (other.def.tag.equals(d.tag)) return "与刃上的签名同类，不叠加";
        }
        return null;
    }

    /** the active signatures (≤ 2): blade first, then the charm unless dual is locked or the tag repeats */
    public static List<Def> active(Worn blade, Worn charm, String activeSet, Collection<String> talentPicks, boolean dual) {
        List<Def> out = new ArrayList<Def>(2);
        if (blade != null && blade.def != null && offReason(blade, null, activeSet, talentPicks, dual) == null) out.add(blade.def);
        if (charm != null && charm.def != null && offReason(charm, blade, activeSet, talentPicks, dual) == null) out.add(charm.def);
        return out;
    }

    public static int imprintCoin(int tier) { return IMPRINT_COIN_PER_TIER * Math.max(1, tier); }

    /** 烬炉烙印 precheck; null = ok */
    public static String imprintCheck(Def d, EmberItemData target, boolean imprintUnlocked, boolean mapCleared, int marks, int coin, int current) {
        if (d == null) return "没有这条签名";
        if (!imprintUnlocked) return "首通 " + IMPRINT_UNLOCK.toUpperCase(Locale.ROOT) + " 后开放烬炉烙印";
        if (!mapCleared) return "先首通 " + d.map.toUpperCase(Locale.ROOT) + " 才能烙印它的签名";
        if (target == null) return "没有正在用的" + ("blade".equals(d.slot) ? "刃" : "护符");
        if (target.tier < 1) return "T0 起步件不能烙印";
        if (!fits(d, target.slot, target.family)) return d.name + " 只能烙在" + d.kindText() + "上";
        if (current == d.code) return "这件已经是 " + d.name;
        if (marks < IMPRINT_MARKS) return d.map.toUpperCase(Locale.ROOT) + " 首领徽记不够（" + marks + "/" + IMPRINT_MARKS + "）";
        if (coin < imprintCoin(target.tier)) return "余烬币不够（需 " + imprintCoin(target.tier) + "）";
        return null;
    }

    private EmberSignature() {}
}
