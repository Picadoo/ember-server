package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D3801: map-origin stash filter pin — non-Path real behavior (G1 / D245).
 * Q01 enter OR firstClear unlocks; later Q0x enters pin GearLib filter.map.
 * Zero drop / skill_mult / assist Path / FX densify.
 */
public final class EmberMapStashPin {

    public static final String C_ENTER = "p1_map_stash_pin_enter";

    private EmberMapStashPin() {}

    public static boolean unlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    /** @return true once when latch set */
    public static boolean markEnter(PlayerData d) {
        if (d == null || d.periodCount(C_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_ENTER, "all", 1);
        return true;
    }

    /** Normalize to q01..q07 / null. */
    public static String pinKey(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return null;
        String m = mapKey.toLowerCase(java.util.Locale.ROOT);
        if (m.startsWith("q01")) return "q01";
        if (m.startsWith("q02")) return "q02";
        if (m.startsWith("q03")) return "q03";
        if (m.startsWith("q04")) return "q04";
        if (m.startsWith("q05")) return "q05";
        if (m.startsWith("q06")) return "q06";
        if (m.startsWith("q07")) return "q07";
        return null;
    }

    /**
     * After unlock: pin GearLib filter to this map's origin. Returns true when filter changed.
     */
    public static boolean pinOnEnter(Player p, PlayerData d, EmberRunService runs, String mapKey) {
        if (p == null || !unlocked(d, runs)) return false;
        String pin = pinKey(mapKey);
        if (pin == null) return false;
        EmberGearLib lib = EmberGearLib.get();
        if (lib == null || !lib.usable()) return false;
        if (!lib.pinMapFilter(p, pin)) return false;
        try { EmberMapStashPinBone.maybeGift(p, d); } catch (Throwable ignored) { } // D12161
        return true;
    }
}
