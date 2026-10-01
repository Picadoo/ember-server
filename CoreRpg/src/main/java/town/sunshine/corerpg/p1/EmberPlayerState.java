package town.sunshine.corerpg.p1;

/** Per-player P1 state persisted in {@code cr_p1_loadout} (in memory when MySQL is not active). */
public final class EmberPlayerState {
    /** explicitly selected charm item_uid (null = none) */
    public volatile String charmUid;
    /** last resolved main-hand uid / set, informational snapshot for ops & audits */
    public volatile String mainhandUid;
    public volatile String activeSet = "none";
    public volatile int awakening;
    /** D03 shared heal-potion cooldown, epoch ms; survives reconnect (and restart with MySQL) */
    public volatile long healCdUntil;
    /** A12 烬斩 cooldown, epoch ms; not cleared on quit */
    public volatile long skillCdUntil;
    /** D11: HP when the player left while inside a P1 world (NaN = none) */
    public volatile double lastHp = Double.NaN;
    public volatile String lastWorld;
    public volatile boolean loaded;
}
