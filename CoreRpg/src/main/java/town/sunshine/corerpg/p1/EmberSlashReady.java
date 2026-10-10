package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D479: rising-edge ActionBar when shared 烬斩/烬突 charge CD ends (mirror D476 parry ready).
 * Zero cooldown-seconds / AFK change.
 */
public final class EmberSlashReady {

    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    private static final ConcurrentHashMap<UUID, Integer> readyTask = new ConcurrentHashMap<UUID, Integer>();

    private EmberSlashReady() {}

    public static String readyActionBar() {
        return "§a充能 · §f烬斩 §a就绪";
    }

    public static String readyChat() {
        return "烬斩充能就绪";
    }

    /** Schedule ready cue for the shared skill charge ending at {@code untilMs}. */
    public static void arm(Player player, long untilMs) {
        schedule(player, untilMs, false);
    }

    /**
     * Admin smoke: set shared skillCdUntil then schedule (force = skip P1 gate).
     */
    public static void adminArmSeconds(Player player, int seconds) {
        if (player == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        EmberLoadoutService ls = pl.getEmberLoadouts();
        if (ls == null) return;
        int sec = Math.max(0, Math.min(60, seconds));
        long until = System.currentTimeMillis() + sec * 1000L;
        EmberPlayerState st = ls.state(player.getUniqueId());
        st.skillCdUntil = until;
        ls.saveState(player);
        schedule(player, until, true);
    }

    public static void cancel(UUID id) {
        if (id == null) return;
        Integer tid = readyTask.remove(id);
        if (tid != null) {
            try { Bukkit.getScheduler().cancelTask(tid.intValue()); } catch (Throwable ignored) { }
        }
    }

    private static void schedule(final Player player, final long untilMs, final boolean force) {
        if (player == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        final UUID id = player.getUniqueId();
        cancel(id);
        long delayMs = Math.max(0L, untilMs - System.currentTimeMillis());
        long delayTicks = Math.max(1L, (delayMs + 49L) / 50L);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
            @Override public void run() {
                readyTask.remove(id);
                Player q = Bukkit.getPlayer(id);
                if (q == null || !q.isOnline()) return;
                EmberLoadoutService ls = pl.getEmberLoadouts();
                if (ls == null) return;
                EmberPlayerState st = ls.state(id);
                // skip if charge was re-armed past this schedule (early-tick safe)
                if (st != null && st.skillCdUntil > untilMs) return;
                if (!force && !EmberMode.isP1(q)) return;
                try { q.sendActionBar(readyActionBar()); } catch (Throwable ignored) { }
                q.sendMessage(P + ChatColor.GREEN + readyChat());
                try {
                    q.playSound(q.getLocation(), Sound.BLOCK_NOTE_PLING, 0.35f, 1.8f);
                } catch (Throwable ignored) { }
            }
        }, delayTicks);
        readyTask.put(id, Integer.valueOf(task.getTaskId()));
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
