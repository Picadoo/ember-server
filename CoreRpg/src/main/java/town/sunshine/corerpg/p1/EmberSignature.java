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

    /** stage 1 = Q01–Q03 (live Java keys only). Codes are stored in saves: never reuse or renumber. */
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
                    "躲开首领预警招时烬爆计数 +1", "被首领预警招打中多受 3%，对首领伤害 ×0.98",
                    m("dodge_burst", 1, "taken_tele", 1.03, "dmg_boss", 0.98)),
            new Def(5, "L05", "q03", "blade", "sustain", "mend", "", "残誓长戟", "残誓守卫",
                    "炽愈回复少打 1 下就触发（更勤，每次回复量不变）", "无",
                    m("sustain_every", -1)),
            new Def(6, "L06", "q03", "charm", "any", "supply", "", "守誓残灯", "残誓守卫",
                    "回复药回复量 ×1.05", "受到首领伤害 ×1.04",
                    m("potion", 1.05, "taken_boss", 1.04))));

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
