package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.List;

/**
 * D239 / ARCH S3-10: BossMove adapter — data view over {@link EmberRunMaps.Skill} plus the
 * Bukkit-free cast-schedule helpers that used to live on {@code EmberRunDirector}
 * ({@code dueSkill} / {@code gatedNext} / {@code nextDue} / {@code hasBelowPressure}).
 * <p>Director keeps thin one-line delegates so {@code EmberRunRulesTest} / shape tests stay green.
 * No FX, no Bukkit, no number changes (bv58).
 */
public final class EmberBossMove implements BossMove {

    private final EmberRunMaps.Skill skill;

    private EmberBossMove(EmberRunMaps.Skill skill) {
        this.skill = skill;
    }

    /** wrap a Skill row (null → null). */
    public static EmberBossMove of(EmberRunMaps.Skill skill) {
        return skill == null ? null : new EmberBossMove(skill);
    }

    @Override public String name() { return skill.name; }

    @Override public String shape() { return skill.type; }

    @Override public double every() { return skill.every; }

    @Override public double warn() { return skill.warn; }

    @Override public double below() { return skill.below; }

    @Override public boolean has(CounterplayKind kind) {
        if (kind == null || skill == null) return false;
        switch (kind) {
            case WALL:  return EmberCounterplay.hasWall(skill);
            case WHIFF: return EmberCounterplay.hasWhiff(skill);
            case BREAK: return EmberCounterplay.hasBreak(skill);
            default: return false;
        }
    }

    @Override public EmberRunMaps.Skill skill() { return skill; }

    // ------------------------------------------------------------------ schedule (was Director)

    /**
     * §10.I: the first-listed skill wins when several are due in the same tick; the other waits until the current
     * action and its recovery are over (it stays due, it is not skipped). @return index or −1
     */
    public static int dueSkill(long[] nextAt, long now) {
        for (int i = 0; i < nextAt.length; i++) if (now >= nextAt[i]) return i;
        return -1;
    }

    /** P2-6: like dueSkill, but a skill with below[i] fires only while the boss HP ratio is under it. */
    public static int dueSkill(long[] nextAt, long now, double[] below, double ratio) {
        for (int i = 0; i < nextAt.length; i++) if (now >= nextAt[i] && (below == null || ratio < below[i])) return i;
        return -1;
    }

    /** D173 / P2-6: true if any top-level or follow skill is phase-gated (below ≤ 1.0). */
    public static boolean hasBelowPressure(EmberRunMaps.Boss b) {
        if (b == null || b.skills == null) return false;
        for (EmberRunMaps.Skill sk : b.skills) {
            if (sk.below <= 1.0) return true;
            if (sk.follow != null && sk.follow.below <= 1.0) return true;
        }
        return false;
    }

    /** D194: next due time after a cast; {@code firstGated} (first cast once an HP gate opened) → a full cooldown from now. */
    public static long gatedNext(long at, long every, long now, boolean firstGated) {
        if (every <= 0) return Long.MAX_VALUE / 4;
        return firstGated ? now + every : nextDue(at, every, now);
    }

    /** Keeps the schedule anchored at the fight start: next slot strictly after {@code now}. */
    public static long nextDue(long at, long every, long now) {
        if (every <= 0) return Long.MAX_VALUE / 4;
        long n = at;
        while (n <= now) n += every;
        return n;
    }

    /** copy below[] from a skill list (Director caches; tests / callers may rebuild). */
    public static double[] belowOf(List<EmberRunMaps.Skill> skills) {
        if (skills == null) return new double[0];
        double[] out = new double[skills.size()];
        for (int i = 0; i < out.length; i++) out[i] = skills.get(i).below;
        return out;
    }

    /** true when this move is phase-gated (fires only under an HP ratio). */
    public boolean gated() { return skill != null && skill.below <= 1.0; }
}
