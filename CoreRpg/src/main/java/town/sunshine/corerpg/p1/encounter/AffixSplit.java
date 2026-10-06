package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/** D138 分裂 split — DEATH_SPAWN: on death, {@code count} melee copies at {@code hp} × their base max HP (never below 1). */
public final class AffixSplit implements AffixBehavior {

    public static final AffixSplit INSTANCE = new AffixSplit();

    private AffixSplit() { }

    @Override public String id() { return "split"; }
    @Override public AffixFamily family() { return AffixFamily.DEATH_SPAWN; }
    @Override public String fx() { return "SPELL_WITCH"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.blazeEvery; } // no live tick (idle glow only)
    @Override public String how(EmberRunMaps.Variety v) { return "死后分裂成 " + v.splitCount + " 个小怪（门要等它们也倒下）"; }

    /** number of adds spawned (0 = none) */
    public static int count(EmberRunMaps.Variety v) { return v.splitCount; }

    /** an add's max HP from its role base max HP */
    public static double addMaxHp(double base, EmberRunMaps.Variety v) { return Math.max(1.0, base * v.splitHp); }
}
