package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * D3753: map-colored loot land feel from origin.map (zero drop rate / table / skill_mult).
 */
public final class EmberLootMapFeel {

    private EmberLootMapFeel() {}

    public static Particle particleForMap(String map) {
        String m = map == null ? "" : map.toLowerCase();
        if (m.startsWith("q01")) return Particle.CLOUD;
        if (m.startsWith("q02")) return Particle.CRIT;
        if (m.startsWith("q03")) return Particle.CRIT_MAGIC;
        if (m.startsWith("q04")) return Particle.FLAME;
        if (m.startsWith("q05")) return Particle.PORTAL;
        if (m.startsWith("q06")) return Particle.HEART;
        if (m.startsWith("q07")) return Particle.END_ROD;
        if (m.startsWith("r0") || m.startsWith("raid")) return Particle.SPELL_WITCH;
        if ("forge".equals(m)) return Particle.LAVA;
        return Particle.VILLAGER_HAPPY;
    }

    public static int count() {
        return 14;
    }

    public static Sound landSound(String map) {
        String m = map == null ? "" : map.toLowerCase();
        if (m.startsWith("q07")) return Sound.ENTITY_PLAYER_LEVELUP;
        if ("forge".equals(m)) return Sound.BLOCK_ANVIL_USE;
        return Sound.ENTITY_ITEM_PICKUP;
    }

    public static float pitch(String map) {
        String m = map == null ? "" : map.toLowerCase();
        if (m.startsWith("q01")) return 0.9f;
        if (m.startsWith("q02")) return 1.05f;
        if (m.startsWith("q03")) return 1.15f;
        if (m.startsWith("q04")) return 1.0f;
        if (m.startsWith("q05")) return 1.25f;
        if (m.startsWith("q06")) return 1.35f;
        if (m.startsWith("q07")) return 0.8f;
        return 1.2f;
    }

    public static void pulse(Player player, EmberItemData data) {
        if (player == null || player.getWorld() == null) return;
        String map = data != null && data.origin != null ? data.origin.map : "";
        try {
            Location at = player.getLocation().clone().add(0, 0.6, 0);
            player.getWorld().spawnParticle(particleForMap(map), at, count(), 0.35, 0.25, 0.35, 0.02);
            player.getWorld().playSound(player.getLocation(), landSound(map), 0.4f, pitch(map));
        } catch (Throwable ignored) { }
    }
}
