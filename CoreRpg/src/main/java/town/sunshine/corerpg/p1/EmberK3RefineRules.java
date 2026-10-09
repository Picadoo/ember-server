package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D341 K3 · same-slot armor refine (DESIGN-ember-six-slot-k3-same-slot-refine §2). Pure rules: no Bukkit, no IO.
 * <p>craft' = max(target, material); material destroyed + audit line; zero cost; never touches quality / family /
 * drop tier / enhance / affix / signature. Separate from K0 forge ({@link EmberUpgradeRules#ARMOR_REFUSE} stays).
 * Switch {@link EmberSixSlot#k3RefineEnabled()} defaults off — off means refuse everything (same as live today).
 */
public final class EmberK3RefineRules {

    private EmberK3RefineRules() {}

    public static final String REFUSE_OFF = "护甲熔炼尚未开放";
    public static final String REFUSE_SLOT = "同部位才能熔炼";
    public static final String REFUSE_NO_GAIN = "不会提高精工";
    public static final String REFUSE_WORN_MAT = "材料不能是正穿着的";
    public static final String REFUSE_NOT_ARMOR = "仅护甲可熔炼";
    public static final String REFUSE_UNBOUND = "只能熔炼绑定的护甲";
    public static final String REFUSE_VERSION = "护甲数据版本过旧";
    public static final String REFUSE_SELF = "不能用自身作材料";

    /**
     * Outcome of a K3 plan. {@code error != null} → nothing changes, material not destroyed.
     * On success: {@code targetAfter} has craft = max; {@code destroyMaterial} is true; {@code cost} is {@link EmberUpgradeRules.Cost#NONE}.
     */
    public static final class Plan {
        public final String error;
        public final EmberUpgradeRules.Cost cost;
        public final EmberItemData targetAfter;
        public final boolean destroyMaterial;
        public final String note;
        /** one-line audit payload (uid / craft before→after / operator placeholder filled by caller) */
        public final String audit;

        Plan(String error, EmberUpgradeRules.Cost cost, EmberItemData targetAfter, boolean destroyMaterial,
             String note, String audit) {
            this.error = error;
            this.cost = cost == null ? EmberUpgradeRules.Cost.NONE : cost;
            this.targetAfter = targetAfter;
            this.destroyMaterial = destroyMaterial;
            this.note = note;
            this.audit = audit;
        }

        static Plan fail(String why) {
            return new Plan(why, EmberUpgradeRules.Cost.NONE, null, false, null, null);
        }

        public boolean ok() { return error == null; }
    }

    /**
     * @param target keep piece (may be worn)
     * @param material consume piece (must not be worn — pass {@code materialWorn=true} to refuse)
     * @param materialWorn true when material is currently equipped in an armor slot
     */
    public static Plan plan(EmberItemData target, EmberItemData material, boolean materialWorn) {
        if (!EmberSixSlot.k3RefineEnabled()) return Plan.fail(REFUSE_OFF);
        String tb = basicArmor(target);
        if (tb != null) return Plan.fail(tb);
        String mb = basicArmor(material);
        if (mb != null) return Plan.fail(mb);
        if (target.uid.equals(material.uid)) return Plan.fail(REFUSE_SELF);
        if (!target.slot.equals(material.slot)) return Plan.fail(REFUSE_SLOT);
        if (materialWorn) return Plan.fail(REFUSE_WORN_MAT);
        int next = Math.max(target.craft, material.craft);
        if (next <= target.craft) return Plan.fail(REFUSE_NO_GAIN);
        EmberItemData after = EmberUpgradeRules.copy(target, target.tier, target.quality, next, target.enhance, target.pity, target.bound);
        String v = after.validate();
        if (v != null) return Plan.fail("熔炼后数据无效: " + v);
        String note = String.format(Locale.ROOT, "熔炼完成 · 精工 %s → %s", craftWord(target.craft), craftWord(after.craft));
        String audit = String.format(Locale.ROOT,
                "k3_refine target=%s craft=%d→%d material=%s craft=%d slot=%s fam=%s/%s tier=%d/%d q=%d/%d",
                target.uid, target.craft, after.craft, material.uid, material.craft,
                target.slot, target.family, material.family, target.tier, material.tier, target.quality, material.quality);
        return new Plan(null, EmberUpgradeRules.Cost.NONE, after, true, note, audit);
    }

    /** preview-only alias (same as {@link #plan}) — commit happens in a later Bukkit service. */
    public static Plan preview(EmberItemData target, EmberItemData material, boolean materialWorn) {
        return plan(target, material, materialWorn);
    }

    private static String basicArmor(EmberItemData d) {
        if (d == null) return "不是 P1 装备";
        String v = d.validate();
        if (v != null) return "物品数据无效: " + v;
        if (!d.isArmor()) return REFUSE_NOT_ARMOR;
        if (!d.bound) return REFUSE_UNBOUND;
        if (d.version < EmberItemData.DATA_VERSION) return REFUSE_VERSION;
        return null;
    }

    static String craftWord(int c) {
        switch (c) {
            case 0: return "零";
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            default: return String.valueOf(c);
        }
    }
}
