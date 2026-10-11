package town.sunshine.corerpg.p1;

/**
 * D3409: open-passage link tempo feel (zero skill_mult).
 * Not Door PORTAL twin; not Boss telegraph LAVA; not Gate Speed/Resist.
 * Bukkit-free helpers for tests.
 */
public final class EmberLinkTempo {

    /** Distance² within which ASK gets denser entry pulse. */
    public static final double ASK_NEAR_DIST_SQ = 16.0; // 4 blocks

    private EmberLinkTempo() {}

    public static boolean showCue(int linkPath) {
        return !EmberLinkPath.shouldMute(linkPath);
    }

    public static String actionBar(String label) {
        if (label == null || label.isEmpty()) return "§b通道 · §f可跃迁";
        return "§b通道 · §f" + label;
    }

    public static String landActionBar(String label) {
        if (label == null || label.isEmpty()) return "§a跃迁落地";
        return "§a跃迁落地 · §f" + label;
    }

    /** Base particle count for entry column; ASK near bump applied by caller. */
    public static int pulseCount(int linkPath) {
        if (EmberLinkPath.shouldMute(linkPath)) return 4; // quiet but still visible
        if (linkPath == EmberLinkPath.ASK) return 10;
        if (linkPath == EmberLinkPath.AUTO) return 8;
        return 8;
    }

    public static int pulseCountNear(int linkPath, boolean nearEntry) {
        int n = pulseCount(linkPath);
        if (nearEntry && linkPath == EmberLinkPath.ASK) return n + 6;
        return n;
    }

    public static boolean nearEntry(double distSq) {
        return distSq >= 0 && distSq <= ASK_NEAR_DIST_SQ;
    }
}
