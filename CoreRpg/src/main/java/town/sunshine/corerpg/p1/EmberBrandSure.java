package town.sunshine.corerpg.p1;

/**
 * D4961: brand craft/pin preview-do when mats ready.
 * Zero craftShards / pin price / MAX_CHARGES. Not forge enhance sure twin / K3.
 */
public final class EmberBrandSure {
    private EmberBrandSure() {}

    public static boolean craftReady(int haveShards, int need) {
        return need > 0 && haveShards >= need;
    }

    public static boolean pinReady(int haveBrand, int needBrand, int haveCoin, int needCoin) {
        return needBrand > 0 && haveBrand >= needBrand && haveCoin >= needCoin;
    }
}
