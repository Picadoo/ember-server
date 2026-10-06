package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D171 再生 regen — CHANNEL: every {@code every} s (a player within 8) a {@code interrupt_window} s channel; damage
 * taken during it ≥ {@code interrupt_hp} × max HP interrupts, otherwise it heals {@code heal} × max HP (capped at full).
 */
public final class AffixRegen implements AffixBehavior {

    public static final AffixRegen INSTANCE = new AffixRegen();
    public static final double ENGAGE = 8;

    private AffixRegen() { }

    @Override public String id() { return "regen"; }
    @Override public AffixFamily family() { return AffixFamily.CHANNEL; }
    @Override public String fx() { return "HEART"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.regenEvery; }
    @Override public String how(EmberRunMaps.Variety v) { return "发光读条时猛打可打断回血"; }

    /** end of a channel started at {@code now} */
    public static long windowEnd(long now, EmberRunMaps.Variety v) { return AffixCycle.after(now, v.regenInterruptWindow); }

    /** the party hit hard enough during the window */
    public static boolean interrupted(double hurt, double maxHp, EmberRunMaps.Variety v) {
        return !(hurt < maxHp * v.regenInterruptHp);
    }

    /** HP restored by an uninterrupted channel (≤ missing HP; ≤ 0 → nothing) */
    public static double heal(double hp, double maxHp, EmberRunMaps.Variety v) {
        return Math.min(maxHp - hp, maxHp * v.regenHeal);
    }

    /** damage taken counts only while the elite is channelling */
    public static boolean counts(long windowEnd, double dmg) { return windowEnd > 0 && dmg > 0; }
}
