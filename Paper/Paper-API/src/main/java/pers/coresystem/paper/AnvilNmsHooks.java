package pers.coresystem.paper;

import org.bukkit.inventory.ItemStack;

/**
 * Runtime hooks for custom anvil costs and combine rules on Paper 1.12.2.
 * CoreAnvil registers a {@link AnvilPolicy}; NMS ContainerAnvil consults it.
 */
public final class AnvilNmsHooks {

    public interface AnvilPolicy {
        /** Extra / replacement rename level cost (vanilla is 1). */
        int renameCost();

        /** Soft cap on levelCost (vanilla blocks at &gt;= 40). */
        int maxLevelCost();

        /**
         * Prior-work penalty applied to outgoing repairCost.
         * Vanilla: {@code next = max(left,right)*2+1}. Return the next penalty value.
         */
        int nextRepairPenalty(int maxIncomingRepairCost);

        /** When true, block enchanted-book / item-item enchant merges. */
        boolean blockEnchantCombines();

        /** When true, only material-repair and rename are allowed (no enchant merge). */
        boolean materialRepairOnly();

        /**
         * Whether the right-slot stack is an allowed repair ingredient for left.
         * Return true to allow, false to reject, or null to use vanilla material check only.
         */
        Boolean allowRepairIngredient(ItemStack left, ItemStack right);

        /** Multiply or replace the computed level cost before cap. */
        int adjustLevelCost(int computedCost, boolean renameOnly, boolean materialRepair);
    }

    private static volatile AnvilPolicy policy;
    private static volatile boolean active = false;

    // Demo defaults mirrored for NMS when no policy yet
    private static volatile int defaultRenameCost = 5;
    private static volatile int defaultMaxLevelCost = 30;
    private static volatile double defaultPriorWorkFactor = 2.0;
    private static volatile boolean defaultBlockEnchantCombines = true;
    private static volatile boolean defaultMaterialRepairOnly = true;

    private AnvilNmsHooks() {}

    public static void setPolicy(AnvilPolicy anvilPolicy) {
        policy = anvilPolicy;
        active = anvilPolicy != null;
    }

    public static AnvilPolicy getPolicy() {
        return policy;
    }

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active || policy != null;
    }

    public static void setDefaults(int renameCost, int maxLevelCost, double priorWorkFactor,
                                   boolean blockEnchantCombines, boolean materialRepairOnly) {
        defaultRenameCost = renameCost > 0 ? renameCost : 1;
        defaultMaxLevelCost = maxLevelCost > 0 ? maxLevelCost : 39;
        defaultPriorWorkFactor = priorWorkFactor > 0 ? priorWorkFactor : 2.0;
        defaultBlockEnchantCombines = blockEnchantCombines;
        defaultMaterialRepairOnly = materialRepairOnly;
    }

    public static int renameCost() {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return Math.max(1, p.renameCost());
            } catch (Throwable t) {
                return defaultRenameCost;
            }
        }
        return defaultRenameCost;
    }

    public static int maxLevelCost() {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return Math.max(1, p.maxLevelCost());
            } catch (Throwable t) {
                return defaultMaxLevelCost;
            }
        }
        return defaultMaxLevelCost;
    }

    public static boolean blockEnchantCombines() {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return p.blockEnchantCombines();
            } catch (Throwable t) {
                return defaultBlockEnchantCombines;
            }
        }
        return defaultBlockEnchantCombines;
    }

    public static boolean materialRepairOnly() {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return p.materialRepairOnly();
            } catch (Throwable t) {
                return defaultMaterialRepairOnly;
            }
        }
        return defaultMaterialRepairOnly;
    }

    public static int nextRepairPenalty(int maxIncoming) {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return Math.max(0, p.nextRepairPenalty(maxIncoming));
            } catch (Throwable t) {
                // fall through
            }
        }
        // Configurable factor: vanilla is *2+1
        return (int) Math.floor(maxIncoming * defaultPriorWorkFactor) + 1;
    }

    public static Boolean allowRepairIngredient(ItemStack left, ItemStack right) {
        AnvilPolicy p = policy;
        if (p == null) {
            return null;
        }
        try {
            return p.allowRepairIngredient(left, right);
        } catch (Throwable t) {
            return null;
        }
    }

    public static int adjustLevelCost(int computed, boolean renameOnly, boolean materialRepair) {
        AnvilPolicy p = policy;
        if (p != null) {
            try {
                return Math.max(0, p.adjustLevelCost(computed, renameOnly, materialRepair));
            } catch (Throwable t) {
                // fall through
            }
        }
        return computed;
    }

    public static double getDefaultPriorWorkFactor() {
        return defaultPriorWorkFactor;
    }

    public static int getDefaultRenameCost() {
        return defaultRenameCost;
    }

    public static int getDefaultMaxLevelCost() {
        return defaultMaxLevelCost;
    }
}
