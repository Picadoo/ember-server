package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D171 凝霜 frost — AURA: every {@code tick} s, players within {@code radius} get Slowness {@code amplifier} for one tick
 * (+0.5 s); stepping out removes only our own short slow (never a longer slow from elsewhere).
 */
public final class AffixFrost implements AffixBehavior {

    public static final AffixFrost INSTANCE = new AffixFrost();

    private AffixFrost() { }

    @Override public String id() { return "frost"; }
    @Override public AffixFamily family() { return AffixFamily.AURA; }
    @Override public String fx() { return "SNOW_SHOVEL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.frostTick; }
    @Override public String how(EmberRunMaps.Variety v) { return "别站在它身边的霜圈里（出圈即解除）"; }

    public static long nextTick(long now, EmberRunMaps.Variety v) { return AffixCycle.after(now, v.frostTick); }

    public static boolean inAura(double dist2, EmberRunMaps.Variety v) { return dist2 <= v.frostRadius * v.frostRadius; }

    /** Slowness length in ticks for one aura pulse */
    public static int slowTicks(EmberRunMaps.Variety v) { return (int) (v.frostTick * 20) + 10; }

    /** is this Slowness our own aura pulse (safe to remove when the player is outside)? */
    public static boolean ownSlow(int amplifier, int durationTicks, EmberRunMaps.Variety v) {
        return amplifier == v.frostAmplifier && durationTicks <= (int) (v.frostTick * 20) + 15;
    }
}
