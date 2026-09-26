package pers.coresystem.paper;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Runtime hooks for NI-only totem-of-undying resurrection on Paper 1.12.2.
 * Paper never imports NeigeItems — CoreCombat registers a {@link TotemValidator}.
 */
public final class TotemNmsHooks {

    /**
     * Validates a hand ItemStack as a usable totem for the given entity
     * (may enforce NI id whitelist and cooldowns).
     */
    public interface TotemValidator {
        boolean isValid(LivingEntity entity, ItemStack stack);
    }

    private static volatile TotemValidator validator;
    private static volatile boolean overrideVanilla = false;
    private static volatile int cooldownSeconds = 0;
    private static volatile int allowedCount = 0;

    private TotemNmsHooks() {}

    public static void setValidator(TotemValidator v) {
        validator = v;
    }

    public static TotemValidator getValidator() {
        return validator;
    }

    public static void setOverrideVanilla(boolean override) {
        overrideVanilla = override;
    }

    public static boolean isOverrideVanilla() {
        return overrideVanilla;
    }

    public static void setCooldownSeconds(int seconds) {
        cooldownSeconds = seconds < 0 ? 0 : seconds;
    }

    public static int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public static void setAllowedCount(int count) {
        allowedCount = count;
    }

    public static int getAllowedCount() {
        return allowedCount;
    }

    public static boolean hasOverride() {
        return overrideVanilla && validator != null;
    }

    public static boolean isActive() {
        return hasOverride() || validator != null;
    }

    /**
     * When override is on, only stacks accepted by the validator resurrect.
     * When override is off, returns false so NMS falls back to vanilla Items.cY check.
     */
    public static boolean isValidTotem(LivingEntity entity, ItemStack bukkitStack) {
        if (!overrideVanilla) {
            return false;
        }
        TotemValidator v = validator;
        if (v == null || entity == null || bukkitStack == null) {
            return false;
        }
        try {
            return v.isValid(entity, bukkitStack);
        } catch (Throwable t) {
            return false;
        }
    }
}
