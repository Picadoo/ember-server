package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D6305: G1 continuous empowerment — worn blade/charm origin.map matches current map
 * → slash geometry widen (zero skill_mult / dmg mult). Not sure-do / Path / BagBetter.
 * D12426–D12430 TIP-R: mid-run wear rising-edge cue (zero widen / BONUS change).
 */
public final class EmberMapResonance {

    public static final String C_ENTER = "p1_map_res_enter_";

    /** D12430: {@code map_res.wear_cue}; default true when key / section absent. */
    private static volatile boolean wearCue = true;

    /** Per-player last {@link #active} snapshot (cleared on leave). */
    private static final ConcurrentHashMap<UUID, Boolean> wearWasActive = new ConcurrentHashMap<UUID, Boolean>();
    /** Per-player runId that already received a wear cue (once per run). */
    private static final ConcurrentHashMap<UUID, String> wearCuedRunId = new ConcurrentHashMap<UUID, String>();

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
        // Seed wear snapshot so enter-already-active does not twin with wear cue this run
        try {
            UUID id = p.getUniqueId();
            wearWasActive.put(id, Boolean.TRUE);
            EmberRunService runs = null;
            org.bukkit.plugin.Plugin raw = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (raw instanceof town.sunshine.corerpg.CoreRpgPlugin)
                runs = ((town.sunshine.corerpg.CoreRpgPlugin) raw).getEmberRuns();
            EmberRunSession s = runs == null ? null : runs.openSessionOf(id);
            if (s != null && s.runId != null) wearCuedRunId.put(id, s.runId);
        } catch (Throwable ignored) { }
    }

    // ------------------------------------------------------------------ TIP-R wear cue (D12426–D12430)

    /**
     * Pure: rising edge false→true, not yet latched this run, flag on.
     * D12426 / D12431.
     */
    public static boolean shouldWearCue(boolean wasActive, boolean nowActive, boolean runLatched, boolean enabled) {
        return !wasActive && nowActive && !runLatched && enabled;
    }

    /** Pure: snapshot always becomes {@code now}. */
    public static boolean nextSnapshot(boolean was, boolean now) {
        return now;
    }

    /** D12430 TIP-R hot flag; default true. */
    public static boolean wearCueEnabled() {
        return wearCue;
    }

    /** Apply from ember-v1.yml root (or null); absent {@code map_res.wear_cue} → true. */
    public static void loadWearCue(org.bukkit.configuration.ConfigurationSection root) {
        if (root == null) {
            wearCue = true;
            return;
        }
        org.bukkit.configuration.ConfigurationSection sec = root.getConfigurationSection("map_res");
        if (sec == null) {
            wearCue = true;
            return;
        }
        wearCue = sec.getBoolean("wear_cue", true);
    }

    /** Test hook; {@code null} restores default true. */
    public static void setWearCueEnabledForTest(Boolean enabled) {
        wearCue = enabled == null || enabled.booleanValue();
    }

    /** Drop wear snapshot + run latch (leave map / no active run). D12427. */
    public static void clearWearState(UUID id) {
        if (id == null) return;
        wearWasActive.remove(id);
        wearCuedRunId.remove(id);
    }

    /** Convenience overload — resolves loadout from plugin cache. */
    public static void maybeWearCue(Player p, PlayerData d, String mapKey) {
        EmberLoadout lo = null;
        try {
            org.bukkit.plugin.Plugin raw = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (raw instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                EmberLoadoutService ls = ((town.sunshine.corerpg.CoreRpgPlugin) raw).getEmberLoadouts();
                lo = ls == null ? null : ls.get(p);
            }
        } catch (Throwable ignored) { }
        maybeWearCue(p, d, mapKey, lo);
    }

    /**
     * D12426 TIP-R: in-run loadout refresh — {@code active} false→true rising edge → mid-run wear cue.
     * Once per runId; leave clears snapshot. Does not touch C_ENTER or widen tables.
     */
    public static void maybeWearCue(Player p, PlayerData d, String mapKey, EmberLoadout lo) {
        if (p == null) return;
        UUID id = p.getUniqueId();
        EmberRunService runs = null;
        try {
            org.bukkit.plugin.Plugin raw = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (raw instanceof town.sunshine.corerpg.CoreRpgPlugin)
                runs = ((town.sunshine.corerpg.CoreRpgPlugin) raw).getEmberRuns();
        } catch (Throwable ignored) { }
        EmberRunSession s = runs == null ? null : runs.openSessionOf(id);
        if (s == null || s.runId == null || s.runId.isEmpty()) {
            clearWearState(id);
            return;
        }
        String mk = mapKey;
        if (mk == null || mk.isEmpty()) {
            try { mk = runs.activeMapKey(p); } catch (Throwable ignored) { }
            if (mk == null || mk.isEmpty()) mk = s.mapKey;
        }
        if (mk == null || mk.isEmpty()) {
            clearWearState(id);
            return;
        }
        if (lo == null && runs.loadouts() != null) {
            try { lo = runs.loadouts().get(p); } catch (Throwable ignored) { }
        }
        boolean nowActive = active(lo, mk);
        Boolean prev = wearWasActive.get(id);
        if (prev == null) {
            // First observe this tracking window: seed only (enter-already-active must not twin)
            wearWasActive.put(id, Boolean.valueOf(nowActive));
            return;
        }
        boolean was = prev.booleanValue();
        String latchedId = wearCuedRunId.get(id);
        boolean runLatched = latchedId != null && latchedId.equals(s.runId);
        boolean fire = shouldWearCue(was, nowActive, runLatched, wearCueEnabled());
        wearWasActive.put(id, Boolean.valueOf(nextSnapshot(was, nowActive)));
        if (!fire) return;
        wearCuedRunId.put(id, s.runId);
        String base = baseMap(mk);
        if (base.isEmpty()) base = mk;
        boolean day = false;
        try { day = EmberMapResonanceDayBoost.activeToday(d); } catch (Throwable ignored) { }
        try {
            p.sendMessage(EmberRunService.P + "§a图源共鸣亮了 · §f" + base.toUpperCase(Locale.ROOT)
                    + " §7· 本图掉落件加宽烬斩（零伤乘）"
                    + (day ? " §a· 今日共鸣加宽已开" : ""));
        } catch (Throwable ignored) { }
        try {
            p.sendActionBar(day ? ("§a共鸣亮了·日加宽 · §f" + base.toUpperCase(Locale.ROOT))
                    : ("§a图源共鸣亮了 · §f" + base.toUpperCase(Locale.ROOT)));
        } catch (Throwable ignored) { }
        // D12429: reuse WearBone once/day; BONUS unchanged
        try {
            if (d != null && EmberMapOriginWearBone.ready(d))
                EmberMapOriginWearBone.maybeGift(p, d, lo, mk);
        } catch (Throwable ignored) { }
    }
}
