package pers.coresystem.paper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Runtime hooks for custom fishing loot and wait-tick ranges on Paper 1.12.2.
 * Paper has no NeigeItems dependency — CoreFish registers a {@link LootProvider}.
 */
public final class FishNmsHooks {

    /**
     * Plugin-supplied loot roller. Return a Bukkit ItemStack to replace vanilla
     * fishing loot, or {@code null} to keep vanilla loot tables.
     */
    public interface LootProvider {
        ItemStack rollLoot(Player player, float luck);
    }

    private static volatile LootProvider lootProvider;
    private static volatile boolean overrideLoot = false;
    private static volatile int minWaitTicks = -1;
    private static volatile int maxWaitTicks = -1;
    private static volatile int tableEntryCount = 0;

    private FishNmsHooks() {}

    public static void setLootProvider(LootProvider provider) {
        lootProvider = provider;
    }

    public static LootProvider getLootProvider() {
        return lootProvider;
    }

    public static void setOverrideLoot(boolean override) {
        overrideLoot = override;
    }

    public static boolean isOverrideLoot() {
        return overrideLoot;
    }

    /**
     * When &gt;= 0, EntityFishingHook uses this instead of paperConfig.fishingMinTicks.
     * Pass -1 to fall back to Paper config.
     */
    public static void setWaitTicks(int min, int max) {
        minWaitTicks = min;
        maxWaitTicks = max;
    }

    public static int getMinWaitTicks() {
        return minWaitTicks;
    }

    public static int getMaxWaitTicks() {
        return maxWaitTicks;
    }

    public static boolean hasWaitOverride() {
        return minWaitTicks >= 0 && maxWaitTicks >= minWaitTicks;
    }

    public static void setTableEntryCount(int count) {
        tableEntryCount = count;
    }

    public static int getTableEntryCount() {
        return tableEntryCount;
    }

    public static boolean isActive() {
        return overrideLoot || lootProvider != null || hasWaitOverride();
    }

    /**
     * Called from NMS EntityFishingHook — never throws.
     * @return custom loot stack, or null to use vanilla
     */
    public static ItemStack resolveLoot(Player player, float luck) {
        if (!overrideLoot) {
            return null;
        }
        LootProvider p = lootProvider;
        if (p == null) {
            return null;
        }
        try {
            return p.rollLoot(player, luck);
        } catch (Throwable t) {
            return null;
        }
    }
}
