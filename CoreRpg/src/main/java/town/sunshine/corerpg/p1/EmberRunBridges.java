package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/** Soft reflection bridges to MythicMobs (spawn) and DungeonPlus (team members) — no compile dependency. */
final class EmberRunBridges {

    private EmberRunBridges() { }

    // ------------------------------------------------------------------ MythicMobs 4.x

    private static Object mmApi;
    private static Method mmSpawn;
    private static boolean mmTried;

    /** MythicMobs.inst().getAPIHelper().spawnMythicMob(id, loc); null when MM is missing or the id is unknown. */
    static Entity spawnMythic(String id, Location loc, Logger log) {
        try {
            if (!mmTried) {
                mmTried = true;
                Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
                if (mm != null) {
                    Class<?> c = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs", true, mm.getClass().getClassLoader());
                    Object inst = c.getMethod("inst").invoke(null);
                    mmApi = inst.getClass().getMethod("getAPIHelper").invoke(inst);
                    mmSpawn = mmApi.getClass().getMethod("spawnMythicMob", String.class, Location.class);
                }
            }
            if (mmApi == null) return null;
            Object e = mmSpawn.invoke(mmApi, id, loc);
            return e instanceof Entity ? (Entity) e : null;
        } catch (Throwable t) {
            Throwable c = t.getCause() != null ? t.getCause() : t;
            log.warning("[P1 run] MythicMobs spawn " + id + " failed: " + c);
            return null;
        }
    }

    // ------------------------------------------------------------------ DungeonPlus teams (same reflection as GuildService)

    static Object dpTeam(Player p) {
        try {
            Class<?> c = Class.forName("org.serverct.ersha.dungeon.DungeonPlus");
            Object tm = c.getField("teamManager").get(null);
            if (tm == null) return null;
            return tm.getClass().getMethod("getTeam", Player.class).invoke(tm, p);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Leader first, then the other members; a player without a DP team is a party of one. */
    static List<UUID> teamMembers(Player p) {
        List<UUID> out = new ArrayList<UUID>();
        Object team = dpTeam(p);
        if (team != null) {
            try {
                Object l = team.getClass().getField("leader").get(team);
                if (l instanceof UUID) out.add((UUID) l);
                Object o = team.getClass().getField("players").get(team);
                if (o instanceof List) for (Object x : (List<?>) o) if (x instanceof UUID && !out.contains(x)) out.add((UUID) x);
            } catch (Throwable ignored) {
            }
        }
        if (!out.contains(p.getUniqueId())) out.add(0, p.getUniqueId());
        return out;
    }

    static boolean teamLeader(Player p) {
        Object team = dpTeam(p);
        if (team == null) return true;
        try {
            Object l = team.getClass().getField("leader").get(team);
            return !(l instanceof UUID) || l.equals(p.getUniqueId());
        } catch (Throwable t) {
            return true;
        }
    }
}
