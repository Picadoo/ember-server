package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D236 stub / D239 adapter seam: one telegraphed boss (or elite-twist) move as a data-driven view.
 * <p>Live cast / telegraph / damage still runs inside {@code EmberRunDirector}; the live adapter
 * {@link EmberBossMove} wraps {@link EmberRunMaps.Skill} and owns the Bukkit-free schedule helpers
 * ({@code dueSkill} / {@code gatedNext} / {@code nextDue} / {@code hasBelowPressure}).
 */
public interface BossMove {

    /** skill id / display name */
    String name();

    /** telegraph shape: line / cone / circle / charge */
    String shape();

    /** seconds between casts */
    double every();

    /** warn wind-up seconds */
    double warn();

    /** HP-ratio gate (fires only while boss ratio &lt; below); {@code > 1} = ungated */
    double below();

    /** counterplay windows carried by this move (may be empty) */
    boolean has(CounterplayKind kind);

    /** underlying Skill row (temporary bridge until the adapter owns fields) */
    EmberRunMaps.Skill skill();
}
