package pers.coresystem.paper;

/**
 * Lightweight status registry for CoreCraft wipe/register.
 * Actual recipe wipe uses Bukkit API; this only exposes stats for /corecraft check.
 */
public final class CraftNmsHooks {

    private static volatile boolean wipeEnabled = false;
    private static volatile int removedCount = 0;
    private static volatile int keptWhitelist = 0;
    private static volatile int registeredCustom = 0;

    private CraftNmsHooks() {}

    public static void setStats(boolean wipeEnabledFlag, int removed, int kept, int registered) {
        wipeEnabled = wipeEnabledFlag;
        removedCount = removed;
        keptWhitelist = kept;
        registeredCustom = registered;
    }

    public static boolean isWipeEnabled() {
        return wipeEnabled;
    }

    public static int getRemovedCount() {
        return removedCount;
    }

    public static int getKeptWhitelist() {
        return keptWhitelist;
    }

    public static int getRegisteredCustom() {
        return registeredCustom;
    }

    public static boolean isActive() {
        return wipeEnabled || registeredCustom > 0;
    }
}
