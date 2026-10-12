package town.sunshine.corerpg.p1.encounter;

/**
 * D12360–D12375 TIP-N · Boss telegraph R-thin (warn bar hold + pre-half cue).
 * Bukkit-free pure helpers — Director owns flash / latch state.
 */
public final class EmberBossFeel {

    private EmberBossFeel() { }

    /** D12362: refresh interval for castStartBar hold (0.25s). */
    public static final long WARN_BAR_HOLD_MS = 250L;

    /** D12364: HP ratio at which the once-per-boss pre-half cue may fire. */
    public static final double PRE_HALF_RATIO = 0.55;

    /**
     * D12362 / D12366: whether to re-send castStartBar now.
     * {@code lastHoldAt <= 0} allows an immediate refresh (caller usually seeds at startWarn).
     */
    public static boolean shouldRefreshWarnBar(long now, long lastHoldAt, long intervalMs,
                                               boolean holdEnabled, boolean warnActive) {
        if (!holdEnabled || !warnActive) return false;
        if (lastHoldAt <= 0L) return true;
        long gap = intervalMs <= 0L ? 0L : intervalMs;
        return now - lastHoldAt >= gap;
    }

    /**
     * D12364 / D12366: true only the first time ratio ≤ {@link #PRE_HALF_RATIO} while enabled.
     */
    public static boolean shouldPreHalfCue(double ratio, boolean alreadyTold, boolean enabled) {
        if (!enabled || alreadyTold) return false;
        return ratio <= PRE_HALF_RATIO;
    }

    /**
     * D12364: ActionBar/subtitle line — {@code §e即将半血} plus short phase root
     * (same family as {@code EmberRunDirector.storyHalfHpCue}).
     */
    public static String preHalfCueLine(String mapKey) {
        String root = preHalfShortRoot(mapKey);
        if (root == null || root.isEmpty()) return "§e即将半血";
        return "§e即将半血 · " + root;
    }

    /**
     * Short half-phase word matching storyHalfHpCue roots (no color codes).
     */
    public static String preHalfShortRoot(String mapKey) {
        if (mapKey == null) return "招式变强";
        String k = mapKey.toLowerCase();
        if ("q02".equals(k) || "q04".equals(k) || "q05".equals(k) || "q06".equals(k) || "q07".equals(k)) {
            return "破招";
        }
        if ("q03".equals(k)) return "增援+破招";
        return "招式变强"; // q01 + default / raid-unknown
    }
}
