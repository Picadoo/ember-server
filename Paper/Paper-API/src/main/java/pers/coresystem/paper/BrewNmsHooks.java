package pers.coresystem.paper;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime registry for custom brewing stand times, ingredient→result recipes,
 * and raised stack limits (bottle / ingredient / result NI items).
 * Paper holds only callbacks/maps — CoreBrew supplies NI-aware matching.
 * <p>
 * Max stack ceiling defaults to {@value #DEFAULT_MAX_STACK} because 1.12 NBT
 * {@code Count} is a signed byte and the vanilla client/protocol often glitches past that.
 */
public final class BrewNmsHooks {

    public static final int DEFAULT_BREW_TIME = 400;
    public static final int DEFAULT_RESULT_AMOUNT = 1;
    /** Honest 1.12 ceiling (NBT Count is a signed byte). */
    public static final int DEFAULT_MAX_STACK = 127;
    public static final int ABSOLUTE_MAX_STACK = 127;

    public static final class BrewRule {
        public final int brewTimeTicks;
        public final int resultAmount;
        /** Optional opaque result key (e.g. NI id); actual ItemStack comes from {@link BrewResolver}. */
        public final String resultKey;

        public BrewRule(int brewTimeTicks, int resultAmount, String resultKey) {
            this.brewTimeTicks = brewTimeTicks > 0 ? brewTimeTicks : DEFAULT_BREW_TIME;
            this.resultAmount = resultAmount > 0 ? resultAmount : DEFAULT_RESULT_AMOUNT;
            this.resultKey = resultKey;
        }

        @Override
        public String toString() {
            return "BrewRule{time=" + brewTimeTicks + ",amount=" + resultAmount + ",result=" + resultKey + "}";
        }
    }

    /**
     * Match result for one bottle slot given the current ingredient.
     */
    public static final class BrewMatch {
        public final ItemStack result;
        public final int brewTimeTicks;
        public final int resultAmount;

        public BrewMatch(ItemStack result, int brewTimeTicks, int resultAmount) {
            this.result = result;
            this.brewTimeTicks = brewTimeTicks > 0 ? brewTimeTicks : DEFAULT_BREW_TIME;
            this.resultAmount = resultAmount > 0 ? resultAmount : DEFAULT_RESULT_AMOUNT;
        }
    }

    public interface BrewResolver {
        /** Whether this stack may sit in the ingredient slot. */
        boolean isCustomIngredient(ItemStack ingredient);

        /** Whether this stack may sit in a bottle slot (beyond vanilla potions). */
        boolean isCustomBottle(ItemStack bottle);

        /**
         * Whether this stack is a custom brew result (for raised max stack).
         * Default implementors that only care about inputs may return false.
         */
        boolean isCustomResult(ItemStack result);

        /**
         * Resolve a custom brew for ingredient + one bottle.
         * @return match with result stack, or null if no custom recipe
         */
        BrewMatch resolve(ItemStack ingredient, ItemStack bottle);
    }

    private static final ConcurrentHashMap<String, BrewRule> RULES = new ConcurrentHashMap<String, BrewRule>();
    private static volatile BrewResolver resolver;
    private static volatile boolean vanillaEnabled = true;

    private static volatile int defaultMaxStack = DEFAULT_MAX_STACK;
    private static volatile boolean stackLimitsEnabled = false;
    private static volatile boolean applyToIngredients = true;
    private static volatile boolean applyToBottles = true;
    private static volatile boolean applyToResults = true;

    private BrewNmsHooks() {}

    public static void setResolver(BrewResolver brewResolver) {
        resolver = brewResolver;
    }

    public static BrewResolver getResolver() {
        return resolver;
    }

    /**
     * When false, TileEntityBrewingStand ignores PotionBrewer vanilla matches
     * and only runs custom recipes from the resolver.
     */
    public static void setVanillaEnabled(boolean enabled) {
        vanillaEnabled = enabled;
    }

    public static boolean isVanillaEnabled() {
        return vanillaEnabled;
    }

    // ---- stack limits ----

    /**
     * Configure raised stack limits. Clamps to {@link #ABSOLUTE_MAX_STACK} (1.12 Count byte).
     * Enables slot/item overrides consulted by ContainerBrewingStand / ItemStack.
     */
    public static void configureStack(int maxStack, boolean ingredients, boolean bottles, boolean results) {
        setDefaultMaxStack(maxStack);
        applyToIngredients = ingredients;
        applyToBottles = bottles;
        applyToResults = results;
        stackLimitsEnabled = true;
    }

    public static void setDefaultMaxStack(int size) {
        if (size < 1) {
            size = 1;
        }
        if (size > ABSOLUTE_MAX_STACK) {
            size = ABSOLUTE_MAX_STACK;
        }
        defaultMaxStack = size;
    }

    public static int getDefaultMaxStack() {
        return defaultMaxStack;
    }

    public static void setStackLimitsEnabled(boolean enabled) {
        stackLimitsEnabled = enabled;
    }

    public static boolean isStackLimitsEnabled() {
        return stackLimitsEnabled;
    }

    public static boolean isApplyToIngredients() {
        return applyToIngredients;
    }

    public static boolean isApplyToBottles() {
        return applyToBottles;
    }

    public static boolean isApplyToResults() {
        return applyToResults;
    }

    /**
     * Slot ceiling for bottle / ingredient slots when stack limits are enabled.
     * @return configured max, or {@code -1} if limits are off (caller keeps vanilla slot max)
     */
    public static int getSlotMaxStackSize() {
        return stackLimitsEnabled ? defaultMaxStack : -1;
    }

    /**
     * Per-item max stack for custom brew NI items (bottles / ingredients / results).
     * @return configured max, or {@code -1} if this stack should use vanilla Item max
     */
    public static int getMaxStackSize(ItemStack bukkit) {
        if (!stackLimitsEnabled || bukkit == null) {
            return -1;
        }
        BrewResolver r = resolver;
        if (r == null) {
            return -1;
        }
        try {
            if (applyToBottles && r.isCustomBottle(bukkit)) {
                return defaultMaxStack;
            }
            if (applyToIngredients && r.isCustomIngredient(bukkit)) {
                return defaultMaxStack;
            }
            if (applyToResults && r.isCustomResult(bukkit)) {
                return defaultMaxStack;
            }
        } catch (Throwable t) {
            return -1;
        }
        return -1;
    }

    public static void setRule(String ruleId, int brewTimeTicks, int resultAmount, String resultKey) {
        if (ruleId == null || ruleId.isEmpty()) {
            return;
        }
        RULES.put(ruleId, new BrewRule(brewTimeTicks, resultAmount, resultKey));
    }

    public static BrewRule getRule(String ruleId) {
        return ruleId == null ? null : RULES.get(ruleId);
    }

    public static void clearRules() {
        RULES.clear();
    }

    public static Map<String, BrewRule> snapshotRules() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<String, BrewRule>(RULES));
    }

    public static int ruleCount() {
        return RULES.size();
    }

    public static boolean isAllowedIngredient(ItemStack bukkit) {
        BrewResolver r = resolver;
        if (r == null || bukkit == null) {
            return false;
        }
        try {
            return r.isCustomIngredient(bukkit);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isAllowedBottle(ItemStack bukkit) {
        BrewResolver r = resolver;
        if (r == null || bukkit == null) {
            return false;
        }
        try {
            return r.isCustomBottle(bukkit);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isAllowedResult(ItemStack bukkit) {
        BrewResolver r = resolver;
        if (r == null || bukkit == null) {
            return false;
        }
        try {
            return r.isCustomResult(bukkit);
        } catch (Throwable t) {
            return false;
        }
    }

    public static BrewMatch resolveMatch(ItemStack ingredient, ItemStack bottle) {
        BrewResolver r = resolver;
        if (r == null || ingredient == null || bottle == null) {
            return null;
        }
        try {
            return r.resolve(ingredient, bottle);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Brew duration for the current stand contents (ingredient + any bottle that matches).
     */
    public static int resolveBrewTime(ItemStack ingredient, ItemStack[] bottles) {
        if (ingredient == null) {
            return DEFAULT_BREW_TIME;
        }
        if (bottles != null) {
            for (ItemStack bottle : bottles) {
                if (bottle == null) {
                    continue;
                }
                BrewMatch match = resolveMatch(ingredient, bottle);
                if (match != null) {
                    return match.brewTimeTicks;
                }
            }
        }
        return DEFAULT_BREW_TIME;
    }

    public static boolean hasCustomMatch(ItemStack ingredient, ItemStack[] bottles) {
        if (ingredient == null || bottles == null) {
            return false;
        }
        for (ItemStack bottle : bottles) {
            if (bottle == null) {
                continue;
            }
            if (resolveMatch(ingredient, bottle) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActive() {
        return resolver != null || !RULES.isEmpty() || !vanillaEnabled || stackLimitsEnabled;
    }
}
