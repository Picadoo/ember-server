package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/** D138 厚甲 shield — STAT: max HP × {@code hp} at promotion, healed to full. */
public final class AffixShield implements AffixBehavior {

    public static final AffixShield INSTANCE = new AffixShield();

    private AffixShield() { }

    @Override public String id() { return "shield"; }
    @Override public AffixFamily family() { return AffixFamily.STAT; }
    @Override public String fx() { return "END_ROD"; } // pre-D241 fell through to the default glow
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.blazeEvery; } // no live tick
    @Override public String how(EmberRunMaps.Variety v) { return "生命 ×" + AffixCycle.fmt(v.shieldHp); }

    /** promoted base max HP */
    public static double maxHp(double base, EmberRunMaps.Variety v) { return base * v.shieldHp; }
}
