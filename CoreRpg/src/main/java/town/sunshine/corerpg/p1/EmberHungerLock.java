package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * D550: hunger lock — while P1 is on and {@code hunger_lock: true}, food level never drops.
 * Cancels {@link FoodLevelChangeEvent} decreases; join / world change / respawn re-saturate.
 * Multiverse {@code hunger: false} is belt-and-suspenders. Bite/bread paths left intact.
 * Zero combat tables / AFK / sx / forge / enter cue.
 */
public final class EmberHungerLock implements Listener {

    public static boolean enabled() {
        if (!EmberMode.active()) return false;
        EmberMode m = EmberMode.get();
        return m != null && m.b("hunger_lock", true);
    }

    public static void saturate(Player p) {
        if (p == null || !p.isOnline()) return;
        try {
            p.setFoodLevel(20);
            p.setSaturation(20f);
            p.setExhaustion(0f);
        } catch (Throwable ignored) {}
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFood(FoodLevelChangeEvent e) {
        if (!enabled()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        int old = p.getFoodLevel();
        int neu = e.getFoodLevel();
        if (neu < old) {
            e.setCancelled(true);
            // keep bar full even if something forced a partial write
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CoreRpg"), () -> {
                if (p.isOnline() && enabled()) saturate(p);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!enabled()) return;
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CoreRpg"), () -> {
            if (p.isOnline() && enabled()) saturate(p);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent e) {
        if (!enabled()) return;
        saturate(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        if (!enabled()) return;
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CoreRpg"), () -> {
            if (p.isOnline() && enabled()) saturate(p);
        });
    }
}
