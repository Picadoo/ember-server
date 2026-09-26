package pers.coresystem.paper;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime registry for custom furnace cook times and result amounts.
 * Paper itself has no NeigeItems dependency — plugins register a {@link RuleResolver}
 * (or keyed rules) that NMS {@code TileEntityFurnace} consults each cook start / finish.
 */
public final class FurnaceNmsHooks {

    public static final int DEFAULT_COOK_TIME = 200;
    public static final int DEFAULT_RESULT_AMOUNT = 1;

    /** Immutable cook rule. */
    public static final class Rule {
        public final int cookTimeTicks;
        public final int resultAmount;

        public Rule(int cookTimeTicks, int resultAmount) {
            this.cookTimeTicks = cookTimeTicks > 0 ? cookTimeTicks : DEFAULT_COOK_TIME;
            this.resultAmount = resultAmount > 0 ? resultAmount : DEFAULT_RESULT_AMOUNT;
        }

        @Override
        public String toString() {
            return "Rule{cook=" + cookTimeTicks + ",amount=" + resultAmount + "}";
        }
    }

    /**
     * Plugin-supplied matcher. Typically inspects NI id via ItemManager and returns
     * the rule for that input, or {@code null} for vanilla default.
     */
    public interface RuleResolver {
        Rule resolve(ItemStack bukkitInput);
    }

    private static final ConcurrentHashMap<String, Rule> RULES = new ConcurrentHashMap<String, Rule>();
    private static volatile RuleResolver resolver;
    private static volatile boolean activeLogged;

    private FurnaceNmsHooks() {}

    public static void setResolver(RuleResolver ruleResolver) {
        resolver = ruleResolver;
    }

    public static RuleResolver getResolver() {
        return resolver;
    }

    /** Register or replace a rule keyed by plugin-chosen id (usually NI input id). */
    public static void setRule(String ruleId, int cookTimeTicks, int resultAmount) {
        if (ruleId == null || ruleId.isEmpty()) {
            return;
        }
        RULES.put(ruleId, new Rule(cookTimeTicks, resultAmount));
    }

    public static Rule getRule(String ruleId) {
        return ruleId == null ? null : RULES.get(ruleId);
    }

    public static void clearRules() {
        RULES.clear();
    }

    public static Map<String, Rule> snapshotRules() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<String, Rule>(RULES));
    }

    public static int ruleCount() {
        return RULES.size();
    }

    /** Called from NMS — never throws. */
    public static int resolveCookTime(ItemStack bukkitInput) {
        Rule rule = resolve(bukkitInput);
        return rule != null ? rule.cookTimeTicks : DEFAULT_COOK_TIME;
    }

    /** Called from NMS — never throws. */
    public static int resolveResultAmount(ItemStack bukkitInput) {
        Rule rule = resolve(bukkitInput);
        return rule != null ? rule.resultAmount : DEFAULT_RESULT_AMOUNT;
    }

    public static Rule resolve(ItemStack bukkitInput) {
        if (bukkitInput == null) {
            return null;
        }
        RuleResolver r = resolver;
        if (r != null) {
            try {
                return r.resolve(bukkitInput);
            } catch (Throwable t) {
                // Keep furnace ticking even if plugin misbehaves
                return null;
            }
        }
        return null;
    }

    public static boolean isActive() {
        return resolver != null || !RULES.isEmpty();
    }

    public static void markBootLogged() {
        activeLogged = true;
    }

    public static boolean wasBootLogged() {
        return activeLogged;
    }
}
