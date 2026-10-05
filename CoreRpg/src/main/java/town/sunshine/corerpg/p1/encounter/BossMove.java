package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D236 stub: one telegraphed boss (or elite-twist) move as a data-driven view.
 * <p>Live cast / telegraph / damage still runs inside {@code EmberRunDirector}; this interface
 * documents the seam for a later adapter that would wrap {@link EmberRunMaps.Skill} and expose
 * shape / counterplay / phase gate without Director knowing yml keys.
 * <p>Not wired in D236 — do not implement production callers yet.
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
