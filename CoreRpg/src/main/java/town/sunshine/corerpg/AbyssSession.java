package town.sunshine.corerpg;

/**
 * In-memory abyss run state. B2.140: kept across a quit (quitAt) for a grace window; an unsettled floor is also
 * mirrored into PlayerData (abyss_run_floor@all) so a relog after the window or a restart still pays it out.
 */
public final class AbyssSession {
    private int floor;
    private boolean settled;
    /** epoch ms of the quit while unsettled; 0 while online */
    long quitAt;

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

    /** New run (DP start script calls progress … 0). */
    public void reset() {
        floor = 0;
        settled = false;
        quitAt = 0;
    }
}
