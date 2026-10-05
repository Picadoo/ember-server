package town.sunshine.corerpg.p1.encounter;

/**
 * D236: D106 raid revive-point triggers as a named primitive.
 * <p>Director / RunService used to hard-code the Chinese {@code why} string at each call site;
 * this enum is the single source. {@link EmberRaidService#reviveFallen} still owns the revive
 * behaviour (50 % HP next to a living teammate); {@code onBossPhase} remains the thin hook.
 * <p>Room / boss-spawn points fire from RunService lifecycle; half-HP / adds / last-phase fire
 * from Director {@code bossTick}.
 */
public enum RevivePoint {
    /** new combat room opened */
    ROOM_OPEN("新房间开打"),
    /** boss entity spawned in the arena */
    BOSS_SPAWN("首领现身"),
    /** boss crossed half HP (phase tell / below-gated pressure) */
    HALF_HP("首领进入半血"),
    /** boss adds phase armed (yml {@code adds.at_hp}) */
    ADDS_PHASE("首领半血转阶段"),
    /** D118 last-phase extra revive at {@code raid_revive.last_phase_hp} */
    LAST_PHASE("最后阶段额外复活");

    /** exact why string passed to {@code reviveFallen} / player tell (behaviour-preserving) */
    public final String why;

    RevivePoint(String why) { this.why = why; }

    /** lookup by why string (null if unknown — keeps forward-compat for ad-hoc reasons) */
    public static RevivePoint byWhy(String why) {
        if (why == null) return null;
        for (RevivePoint p : values()) if (p.why.equals(why)) return p;
        return null;
    }
}
