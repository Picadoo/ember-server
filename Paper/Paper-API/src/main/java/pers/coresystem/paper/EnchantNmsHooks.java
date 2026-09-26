package pers.coresystem.paper;

import org.bukkit.inventory.ItemStack;

/**
 * Runtime hooks for custom enchanting-table offer generation on Paper 1.12.2.
 * Paper has no NeigeItems dependency — CoreEnchant registers an {@link OfferProvider}
 * and optional {@link CatalystValidator} for the secondary (lapis) slot.
 */
public final class EnchantNmsHooks {

    /** One of the three enchanting-table button offers. */
    public static final class Offer {
        public final int enchantmentId;
        public final int enchantmentLevel;
        public final int levelCost;
        public final int lapisCost;

        public Offer(int enchantmentId, int enchantmentLevel, int levelCost, int lapisCost) {
            this.enchantmentId = enchantmentId;
            this.enchantmentLevel = enchantmentLevel;
            this.levelCost = levelCost > 0 ? levelCost : 1;
            this.lapisCost = lapisCost > 0 ? lapisCost : 1;
        }

        @Override
        public String toString() {
            return "Offer{id=" + enchantmentId + ",lvl=" + enchantmentLevel
                    + ",cost=" + levelCost + ",lapis=" + lapisCost + "}";
        }
    }

    /**
     * Plugin-supplied offer builder. Return a length-3 array (null entries = empty slot),
     * or {@code null} to fall back to vanilla generation.
     */
    public interface OfferProvider {
        /**
         * @param item         item in the enchant slot (Bukkit mirror)
         * @param bookshelves  counted bookshelves around the table
         * @param seed         container enchant seed
         * @return 3 offers, or null for vanilla
         */
        Offer[] buildOffers(ItemStack item, int bookshelves, int seed);

        /** When override is on, whether this item may be enchanted at all. */
        boolean canEnchantItem(ItemStack item);

        /** Lapis/catalyst cost for slot index 0..2 when taking a custom offer (fallback: slot+1). */
        int lapisCostForSlot(int slot);
    }

    /**
     * Validates the secondary (lapis) slot ItemStack. CoreEnchant registers an
     * implementation that checks NeigeItems IDs — Paper never imports NI.
     */
    public interface CatalystValidator {
        boolean isValid(ItemStack stack);
    }

    private static volatile OfferProvider provider;
    private static volatile CatalystValidator catalystValidator;
    private static volatile boolean overrideVanilla = false;
    private static volatile boolean rejectVanillaLapis = false;
    private static volatile String catalystNiId = "";
    private static volatile int registeredTables = 0;

    private EnchantNmsHooks() {}

    public static void setProvider(OfferProvider offerProvider) {
        provider = offerProvider;
    }

    public static OfferProvider getProvider() {
        return provider;
    }

    public static void setCatalystValidator(CatalystValidator validator) {
        catalystValidator = validator;
    }

    public static CatalystValidator getCatalystValidator() {
        return catalystValidator;
    }

    /** When true, NMS skips EnchantmentManager offer rolls and uses the provider. */
    public static void setOverrideVanilla(boolean override) {
        overrideVanilla = override;
    }

    public static boolean isOverrideVanilla() {
        return overrideVanilla;
    }

    public static void setRejectVanillaLapis(boolean reject) {
        rejectVanillaLapis = reject;
    }

    public static boolean isRejectVanillaLapis() {
        return rejectVanillaLapis;
    }

    public static void setCatalystNiId(String id) {
        catalystNiId = id == null ? "" : id;
    }

    public static String getCatalystNiId() {
        return catalystNiId;
    }

    public static void setRegisteredTableCount(int count) {
        registeredTables = count;
    }

    public static int getRegisteredTableCount() {
        return registeredTables;
    }

    public static boolean isActive() {
        return overrideVanilla || provider != null || catalystValidator != null;
    }

    /**
     * When true, ContainerEnchantTable uses {@link #isValidCatalyst} instead of
     * the vanilla blue-dye (lapis) check for the secondary slot.
     */
    public static boolean hasCatalystOverride() {
        return catalystValidator != null;
    }

    public static boolean canEnchant(ItemStack bukkitItem) {
        OfferProvider p = provider;
        if (p == null) {
            return true;
        }
        try {
            return p.canEnchantItem(bukkitItem);
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * Validate secondary-slot catalyst. Only meaningful when {@link #hasCatalystOverride()}.
     */
    public static boolean isValidCatalyst(ItemStack bukkitItem) {
        CatalystValidator v = catalystValidator;
        if (v == null) {
            return false;
        }
        if (bukkitItem == null) {
            return false;
        }
        try {
            return v.isValid(bukkitItem);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * @return custom offers, or null to use vanilla
     */
    public static Offer[] resolveOffers(ItemStack bukkitItem, int bookshelves, int seed) {
        if (!overrideVanilla) {
            return null;
        }
        OfferProvider p = provider;
        if (p == null || bukkitItem == null) {
            return null;
        }
        try {
            if (!p.canEnchantItem(bukkitItem)) {
                return new Offer[3]; // all null → no offers
            }
            Offer[] offers = p.buildOffers(bukkitItem, bookshelves, seed);
            if (offers == null) {
                return new Offer[3];
            }
            if (offers.length != 3) {
                Offer[] padded = new Offer[3];
                for (int i = 0; i < Math.min(3, offers.length); i++) {
                    padded[i] = offers[i];
                }
                return padded;
            }
            return offers;
        } catch (Throwable t) {
            return new Offer[3];
        }
    }

    public static int resolveLapisCost(int slot) {
        OfferProvider p = provider;
        if (p != null && overrideVanilla) {
            try {
                int c = p.lapisCostForSlot(slot);
                if (c > 0) {
                    return c;
                }
            } catch (Throwable ignored) {
            }
        }
        return slot + 1;
    }
}
