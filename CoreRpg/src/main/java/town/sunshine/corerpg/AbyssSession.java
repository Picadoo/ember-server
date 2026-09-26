package town.sunshine.corerpg;

/**
 * In-memory abyss run state (not persisted to MySQL/YAML).
 * Cleared on PlayerQuitEvent.
 */
public final class AbyssSession {
    private int floor;
    private boolean settled;

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = Math.max(0, floor);
    }

    /** Raise floor to at least {@code floor} (max of current and arg). */
    public void raiseFloor(int floor) {
        if (floor > this.floor) this.floor = floor;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }
}
