package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D6305: G1 continuous empowerment — worn blade/charm origin.map matches current map
 * → slash geometry widen (zero skill_mult / dmg mult). Not sure-do / Path / BagBetter.
 */
public final class EmberMapResonance {

    public static final String C_ENTER = "p1_map_res_enter_";

    private EmberMapResonance() {}

    /** Normalize q01c / q01a → q01 for match. */
    public static String baseMap(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return "";
        String k = mapKey.toLowerCase(Locale.ROOT);
        if (k.length() >= 3 && k.charAt(0) == 'q' && Character.isDigit(k.charAt(1)) && Character.isDigit(k.charAt(2)))
            return k.substring(0, 3);
        return k;
    }

    public static boolean matches(EmberItemData.Origin origin, String mapKey) {
        if (origin == null || !origin.present()) return false;
        String om = baseMap(origin.map);
        String mk = baseMap(mapKey);
        return !om.isEmpty() && !mk.isEmpty() && om.equals(mk);
    }

    public static boolean active(EmberLoadout lo, String mapKey) {
        if (lo == null || mapKey == null || mapKey.isEmpty()) return false;
        if (lo.blade != null && matches(lo.blade.origin, mapKey)) return true;
        if (lo.charm != null && matches(lo.charm.origin, mapKey)) return true;
        return false;
    }

    public static boolean active(Player p, EmberRunService runs, String mapKey) {
        if (p == null || runs == null) return false;
        EmberLoadout lo = runs.loadouts() == null ? null : runs.loadouts().get(p);
        return active(lo, mapKey == null ? runs.activeMapKey(p) : mapKey);
    }

    /** Rising-edge enter cue (once per map key). */
    public static void maybeEnterCue(Player p, PlayerData d, String mapKey) {
        if (p == null || d == null || mapKey == null) return;
        String base = baseMap(mapKey);
        if (base.isEmpty()) return;
        EmberLoadout lo = null;
        try {
            org.bukkit.plugin.Plugin raw = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (raw instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                EmberLoadoutService ls = ((town.sunshine.corerpg.CoreRpgPlugin) raw).getEmberLoadouts();
                lo = ls == null ? null : ls.get(p);
            }
        } catch (Throwable ignored) { }
        if (!active(lo, mapKey)) return;
        String key = C_ENTER + base;
        if (d.periodCount(key, "all") > 0) return;
        d.addPeriodCount(key, "all", 1);
        boolean day = false;
        try { day = EmberMapResonanceDayBoost.maybeLatch(d); } catch (Throwable ignored) { } // D11857
        try { EmberMapOriginWearBone.maybeGift(p, d, lo, mapKey); } catch (Throwable ignored) { } // D11921
        p.sendMessage(EmberRunService.P + "§a图源共鸣 · §f" + base.toUpperCase(Locale.ROOT)
                + " §7· 本图掉落件加宽烬斩（零伤乘）"
                + (day ? " §a· 今日共鸣加宽已开" : ""));
        try {
            p.sendActionBar(day ? ("§a图源共鸣·日加宽 · §f" + base.toUpperCase(Locale.ROOT))
                    : ("§a图源共鸣 · §f" + base.toUpperCase(Locale.ROOT)));
        } catch (Throwable ignored) { }
    }
}
