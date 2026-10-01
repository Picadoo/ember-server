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

    private EmberLoadout(EmberItemData blade, EmberItemData charm, int level, String activeSet, int awakening,
                         double b, double h0, double h, double d, double m) {
        this.blade = blade; this.charm = charm; this.level = level; this.activeSet = activeSet;
        this.awakening = awakening; this.b = b; this.h0 = h0; this.h = h; this.d = d; this.m = m;
    }

    public static EmberLoadout compute(EmberTables t, EmberItemData blade, EmberItemData charm, int level) {
        if (blade != null && !blade.isBlade()) blade = null;
        if (charm != null && !charm.isCharm()) charm = null;
        if (blade != null && charm != null && blade.uid.equals(charm.uid)) charm = null; // §8.2 same uid cannot fill two slots
        String set = setFamily(blade, charm);
        int awk = "none".equals(set) ? 0 : awakening(blade, charm);
        double b = blade == null ? EmberFormula.baseAttack(t, -1, 0, 0, 0, level)
                : EmberFormula.baseAttack(t, blade.tier, blade.quality, blade.craft, blade.enhance, level);
        double h0 = charm == null ? EmberFormula.baseHp(t, -1, 0, 0, 0, level)
                : EmberFormula.baseHp(t, charm.tier, charm.quality, charm.craft, charm.enhance, level);
        double h = EmberFormula.maxHp(t, h0, "sustain".equals(set));
        double d = EmberFormula.defense(t, charm == null ? -1 : charm.tier);
        double m = EmberFormula.mitigation(t, d);
        return new EmberLoadout(blade, charm, level, set, awk, b, h0, h, d, m);
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

    public double ehp() { return EmberFormula.ehp(h, m); }

    public String setLabel() {
        if ("none".equals(activeSet)) return "未成套";
        return EmberItemData.familyName(activeSet) + " 觉醒" + (awakening == 3 ? "III" : awakening == 2 ? "II" : "I");
    }
}
