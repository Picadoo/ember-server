package town.sunshine.corerpg.p1.encounter;

/**
 * D239 / ARCH S3-10: RoomObjective adapter — room clear / variety-event goals as a data-driven view,
 * plus the Bukkit-free room-event helpers that used to live on {@code EmberRunDirector}
 * (D179 hold/relay/beacon, D191 breach/chain/unscathed).
 * <p>Director keeps thin one-line delegates so existing unit tests stay green.
 * Live room ticks / FX / tellRun still run in Director; this class owns predicates + family labels.
 * No number changes (bv58).
 */
public final class EmberRoomObjective implements RoomObjective {

    private final String id;
    private final String family;
    private boolean complete;

    private EmberRoomObjective(String id, String family) {
        this.id = id == null ? "" : id;
        this.family = family == null ? "clear" : family;
        this.complete = false;
    }

    /** factory from a variety event kind (or {@code "clear"} for a plain room clear). */
    public static EmberRoomObjective of(String kind) {
        return new EmberRoomObjective(kind == null ? "clear" : kind, familyOf(kind));
    }

    /**
     * Map a variety event kind → {@link RoomObjective#family()} short family.
     * Unknown / null → {@code clear}.
     */
    public static String familyOf(String kind) {
        if (kind == null || kind.isEmpty() || "clear".equals(kind)) return "clear";
        if ("timed".equals(kind)) return "clear"; // timed clear = clear with a soft clock
        if ("hold".equals(kind)) return "hold";
        if ("escort".equals(kind)) return "escort";
        if ("chain".equals(kind)) return "chain";
        if ("breach".equals(kind) || "crystal".equals(kind) || "beacon".equals(kind)
                || "relay".equals(kind) || "unscathed".equals(kind)) return "event";
        return "event";
    }

    @Override public String id() { return id; }

    @Override public String family() { return family; }

    @Override public boolean complete() { return complete; }

    /** mark win condition met (Director sets this when the room/event succeeds). */
    public void markComplete() { this.complete = true; }

    /** reset (room reopen / test). */
    public void reset() { this.complete = false; }

    // ------------------------------------------------------------------ D179 hold / beacon / relay

    /** D179: hold only accumulates when a grounded player is inside the circle. */
    public static boolean holdCounts(boolean inRadius, boolean onGround) {
        return inRadius && onGround;
    }

    /** D179: relay advances only when the next ordered index is pressed. */
    public static int relayAdvance(int nextIndex, int pressedIndex, int count) {
        if (pressedIndex != nextIndex || nextIndex < 0 || nextIndex >= count) return nextIndex;
        return nextIndex + 1;
    }

    /** D179: beacon is never Extra.TREASURE and never blocks room-clear (tracked outside mobs). */
    public static boolean beaconIsTreasureExtra() { return false; }
    public static boolean beaconBlocksRoomClear() { return false; }

    // ------------------------------------------------------------------ D191 breach / chain / unscathed

    /** D191: radius after {@code dt} seconds of shrinking (never below 0). */
    public static double breachStep(double r, double dt, double shrink) {
        return Math.max(0.0, r - Math.max(0.0, dt) * shrink);
    }

    /** D191: a kill inside the rift pushes it back out, capped at {@code max}. */
    public static double breachGrow(double r, double grow, double max) {
        return Math.min(max, r + grow);
    }

    /** D191: the rift has closed (event failed). */
    public static boolean breachCollapsed(double r, double min) { return r <= min; }

    /** D191: horizontal distance check for a kill inside the rift. */
    public static boolean breachInside(double dx, double dz, double r) {
        return dx * dx + dz * dz <= r * r;
    }

    /** D191: streak after a kill at {@code nowMs} (first kill / gap exceeded → 1). */
    public static int chainNext(int streak, long lastMs, long nowMs, double gapSecs) {
        if (streak <= 0 || lastMs <= 0 || nowMs - lastMs > (long) (gapSecs * 1000)) return 1;
        return streak + 1;
    }

    /** D191: kills needed = min(config need, mobs spawned in the room), at least 1. */
    public static int chainNeedFor(int need, int spawned) {
        return Math.max(1, Math.min(need, spawned));
    }

    /** D191: hit budget of the party (base + per extra member). */
    public static int unscathedBudget(int hits, int perMember, int party) {
        return Math.max(0, hits) + Math.max(0, perMember) * Math.max(0, party - 1);
    }

    /** D191: still within budget. */
    public static boolean unscathedOk(int taken, int budget) {
        return budget >= 0 && taken <= budget;
    }
}
