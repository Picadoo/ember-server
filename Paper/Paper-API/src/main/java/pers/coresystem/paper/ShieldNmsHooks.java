package pers.coresystem.paper;

import org.bukkit.inventory.ItemStack;

/**
 * Runtime hooks for NI-only shield blocking on Paper 1.12.2.
 * Paper never imports NeigeItems — CoreCombat registers a {@link ShieldValidator}.
 */
public final class ShieldNmsHooks {

    public interface ShieldValidator {
        boolean canBlock(ItemStack stack);
    }

    private static volatile ShieldValidator validator;
    private static volatile boolean overrideVanilla = false;
    private static volatile float durabilityLossMultiplier = 1.0f;
    private static volatile int allowedCount = 0;

    private ShieldNmsHooks() {}

    public static void setValidator(ShieldValidator v) {
        validator = v;
    }

    public static ShieldValidator getValidator() {
        return validator;
    }

    public static void setOverrideVanilla(boolean override) {
        overrideVanilla = override;
    }

    public static boolean isOverrideVanilla() {
        return overrideVanilla;
    }

    public static void setDurabilityLossMultiplier(float mult) {
        durabilityLossMultiplier = mult > 0.0f ? mult : 1.0f;
    }

    public static float getDurabilityLossMultiplier() {
        return durabilityLossMultiplier;
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
     * When override is on, only stacks accepted by the validator may block.
     * When override is off, always returns true (vanilla behaviour).
     */
    public static boolean canBlock(ItemStack bukkitStack) {
        if (!overrideVanilla) {
            return true;
        }
        ShieldValidator v = validator;
        if (v == null) {
            return false;
        }
        if (bukkitStack == null) {
            return false;
        }
        try {
            return v.canBlock(bukkitStack);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Called from NMS damageShield — never throws. */
    public static int scaleDurabilityDamage(int baseDamage) {
        if (baseDamage <= 0) {
            return baseDamage;
        }
        float m = durabilityLossMultiplier;
        if (m == 1.0f) {
            return baseDamage;
        }
        int scaled = Math.round(baseDamage * m);
        return scaled < 1 ? 1 : scaled;
    }
}
