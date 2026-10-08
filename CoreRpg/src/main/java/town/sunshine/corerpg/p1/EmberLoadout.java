package town.sunshine.corerpg.p1;

/**
 * EquippedLoadout (策划书 §4.1, source table §6.3): the main-hand blade plus the explicitly selected charm,
 * and the single character-level {@code active_set}. Pure value object: the Bukkit side resolves and
 * verifies the two items, this class only applies the §4.3 set rule and the §7.2 formulas.
 */
public final class EmberLoadout {

    /** may be null (no valid P1 blade in the main hand) */
    public final EmberItemData blade;
    /** may be null (no valid selected charm in the inventory) */
    public final EmberItemData charm;
    public final int level;
    /** "none" or the family of a valid two-piece set */
    public final String activeSet;
    /** 0 = no set, 1..3 = 觉醒 I..III */
    public final int awakening;
    public final double b, h0, h, d, m;
    /** D139 festival charm slot (own slot, never part of a set): flat H0 / D added, capped at a standard T3 charm */
    public final double festHp, festDef;
    /**
     * D318 六槽: the four counted armor pieces (head, chest, legs, boots; an entry is null = empty slot = q0 f0), or null
     * for the 2-slot loadout (switch off — every value below is then the pre-D318 one, bit for bit).
     */
    private final EmberItemData[] armor;

    private EmberLoadout(EmberItemData blade, EmberItemData charm, int level, String activeSet, int awakening,
                         double b, double h0, double h, double d, double m, double festHp, double festDef) {
        this(blade, charm, level, activeSet, awakening, b, h0, h, d, m, festHp, festDef, null);
    }

    private EmberLoadout(EmberItemData blade, EmberItemData charm, int level, String activeSet, int awakening,
                         double b, double h0, double h, double d, double m, double festHp, double festDef, EmberItemData[] armor) {
        this.blade = blade; this.charm = charm; this.level = level; this.activeSet = activeSet;
        this.awakening = awakening; this.b = b; this.h0 = h0; this.h = h; this.d = d; this.m = m;
        this.festHp = festHp; this.festDef = festDef; this.armor = armor;
    }

    /** D318: true when this loadout was computed with the six-slot formula (switch on) */
    public boolean sixSlot() { return armor != null; }

    /** D318: counted armor piece of slot i (0 head … 3 boots); null = empty or 2-slot loadout */
    public EmberItemData armor(int i) { return armor == null || i < 0 || i > 3 ? null : armor[i]; }

    /** D318: copy of the counted armor (null for the 2-slot loadout) */
    public EmberItemData[] armorCopy() { return armor == null ? null : armor.clone(); }

    /**
     * D318 六槽 T1: the 2-slot loadout plus four armor pieces (spec §3.1, F 共鸣). {@code armor == null} → exactly
     * {@link #compute(EmberTables, EmberItemData, EmberItemData, int, double, double)} (the switch-off path, T1-1).
     * Otherwise H0 = {@link EmberFormula#baseHpSix} with each counted piece's own quality / craft (a slot is counted only
     * when it holds armor of that slot whose uid is not the blade, the charm or another slot); B and D never change.
     */
    public static EmberLoadout compute(EmberTables t, EmberItemData blade, EmberItemData charm, int level, double festHp, double festDef,
                                       EmberItemData[] armor) {
        if (armor == null) return compute(t, blade, charm, level, festHp, festDef);
        EmberLoadout two = compute(t, blade, charm, level, festHp, festDef);
        EmberItemData[] a = new EmberItemData[4];
        java.util.Set<String> seen = new java.util.HashSet<String>();
        for (int i = 0; i < 4 && i < armor.length; i++) {
            EmberItemData x = armor[i];
            if (x == null || !x.isArmor() || EmberItemData.armorIndex(x.slot) != i) continue;
            if ((two.blade != null && x.uid.equals(two.blade.uid)) || (two.charm != null && x.uid.equals(two.charm.uid))) continue;
            if (!seen.add(x.uid)) continue;
            a[i] = x;
        }
        int[] aq = new int[4], af = new int[4];
        for (int i = 0; i < 4; i++) { aq[i] = a[i] == null ? 0 : a[i].quality; af[i] = a[i] == null ? 0 : a[i].craft; }
        EmberItemData c = two.charm;
        double h0 = c == null ? EmberFormula.baseHpSix(t, -1, 0, 0, 0, level, EmberSixSlot.armorWeights(), aq, af)
                : EmberFormula.baseHpSix(t, c.tier, c.quality, c.craft, c.enhance, level, EmberSixSlot.armorWeights(), aq, af);
        h0 += two.festHp;
        double h = EmberFormula.maxHp(t, h0, "sustain".equals(two.activeSet));
        return new EmberLoadout(two.blade, c, level, two.activeSet, two.awakening, two.b, h0, h, two.d, two.m, two.festHp, two.festDef, a);
    }

    public static EmberLoadout compute(EmberTables t, EmberItemData blade, EmberItemData charm, int level) {
        return compute(t, blade, charm, level, 0, 0);
    }

    /** D139: the festival charm's H / D cap — never above a standard T3 charm (h 165 / D 14 in the book tables) */
    public static double festCapHp(EmberTables t) { return t.charmH(3); }
    public static double festCapDef(EmberTables t) { return t.charmD(3); }

    /**
     * D139: festHp / festDef come from the worn festival charm (0 when none); clamped to [0, T3 charm] and added to
     * H0 / D before the §7.2 max-HP and mitigation formulas, so they go through the same pipeline as any charm stat.
     */
    public static EmberLoadout compute(EmberTables t, EmberItemData blade, EmberItemData charm, int level, double festHp, double festDef) {
        festHp = Math.max(0, Math.min(festCapHp(t), festHp));
        festDef = Math.max(0, Math.min(festCapDef(t), festDef));
        if (blade != null && !blade.isBlade()) blade = null;
        if (charm != null && !charm.isCharm()) charm = null;
        if (blade != null && charm != null && blade.uid.equals(charm.uid)) charm = null; // §8.2 same uid cannot fill two slots
        String set = setFamily(blade, charm);
        int awk = "none".equals(set) ? 0 : awakening(blade, charm);
        double b = blade == null ? EmberFormula.baseAttack(t, -1, 0, 0, 0, level)
                : EmberFormula.baseAttack(t, blade.tier, blade.quality, blade.craft, blade.enhance, level);
        double h0 = charm == null ? EmberFormula.baseHp(t, -1, 0, 0, 0, level)
                : EmberFormula.baseHp(t, charm.tier, charm.quality, charm.craft, charm.enhance, level);
        h0 += festHp;
        double h = EmberFormula.maxHp(t, h0, "sustain".equals(set));
        double d = EmberFormula.defense(t, charm == null ? -1 : charm.tier) + festDef;
        double m = EmberFormula.mitigation(t, d);
        return new EmberLoadout(blade, charm, level, set, awk, b, h0, h, d, m, festHp, festDef);
    }

    /** §4.1: both pieces present, same family (not none), both at least T1. */
    static String setFamily(EmberItemData blade, EmberItemData charm) {
        if (blade == null || charm == null) return "none";
        if (!blade.family.equals(charm.family) || "none".equals(blade.family)) return "none";
        if (blade.tier < 1 || charm.tier < 1) return "none";
        return blade.family;
    }

    /** §4.3: decided by the lower tier and the lower enhance of the two; tiers never stack. */
    static int awakening(EmberItemData blade, EmberItemData charm) {
        int tier = Math.min(blade.tier, charm.tier);
        int enh = Math.min(blade.enhance, charm.enhance);
        if (tier >= 3 && enh >= 9) return 3;
        if (tier >= 2 && enh >= 6) return 2;
        return tier >= 1 ? 1 : 0;
    }

    /**
     * §19.1 "下一次突破缺哪项": what the equipped pair still lacks for the next awakening (§4.3: I = same family both
     * ≥ T1; II = both ≥ T2 and both ≥ +6; III = both ≥ T3 and both ≥ +9). Pure text, no rule of its own.
     */
    public String nextAwakeningHint() {
        if (blade == null) return "觉醒I 缺：主手拿一把余烬刃";
        if (charm == null) return "觉醒I 缺：选定护符（/corerpg p1 charm）";
        if (!blade.family.equals(charm.family) || "none".equals(blade.family))
            return "觉醒I 缺：刃与护符同族（现为 " + EmberItemData.familyName(blade.family) + " / " + EmberItemData.familyName(charm.family) + "）";
        if (awakening >= 3) return "已达 觉醒III（最高）";
        int next = awakening + 1;
        if (next == 1) return "觉醒I 缺：两件均 ≥ T1";
        int needTier = next, needEnh = next == 2 ? 6 : 9;
        StringBuilder sb = new StringBuilder("下一档 觉醒").append(next == 2 ? "II" : "III").append(" 缺：");
        int n = 0;
        n += lack(sb, n, "刃", blade, needTier, needEnh);
        n += lack(sb, n, "护符", charm, needTier, needEnh);
        return sb.toString();
    }

    private static int lack(StringBuilder sb, int before, String name, EmberItemData it, int needTier, int needEnh) {
        StringBuilder part = new StringBuilder();
        if (it.tier < needTier) part.append("T").append(it.tier).append("→T").append(needTier);
        if (it.enhance < needEnh) part.append(part.length() > 0 ? "、" : "").append("+").append(it.enhance).append("→+").append(needEnh);
        if (part.length() == 0) return 0;
        sb.append(before > 0 ? "；" : "").append(name).append(' ').append(part);
        return 1;
    }

    /**
     * Batch 3 "set completion" (D13): how far the equipped pair is from the P1 end state, the same-family T3 two-piece
     * at +9 (觉醒III). Counts only the two set slots (main-hand blade + selected charm) of the blade's family; pieces
     * of another family count 0 (they do not form the set, §4.1). Pure text.
     */
    public String setProgress() {
        String fam = blade == null ? null : blade.family;
        if (blade == null) return "成套进度：主手没有有效的余烬刃";
        if ("none".equals(fam)) return "成套进度：T0 起步刃无族；拿到 T1 刃和同族 T1 护符后开始算";
        int same = 1, t3 = blade.tier >= 3 ? 1 : 0, e9 = blade.tier >= 3 && blade.enhance >= 9 ? 1 : 0;
        if (charm != null && fam.equals(charm.family)) {
            same++;
            if (charm.tier >= 3) t3++;
            if (charm.tier >= 3 && charm.enhance >= 9) e9++;
        }
        return "成套进度（" + EmberItemData.familyName(fam) + "）：同族 " + same + "/2 · T3 " + t3 + "/2 · T3+9 " + e9 + "/2"
                + (e9 == 2 ? " §a· P1 套装完成" : "");
    }

    public double ehp() { return EmberFormula.ehp(h, m); }

    public String setLabel() {
        if ("none".equals(activeSet)) return "未成套";
        return EmberItemData.familyName(activeSet) + " 觉醒" + (awakening == 3 ? "III" : awakening == 2 ? "II" : "I");
    }
}
