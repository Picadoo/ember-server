package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D4961/D5025: brand craft/pin preview-do when mats ready; craft→path auto-pin.
 * Zero craftShards / pin price / MAX_CHARGES. Not forge enhance sure twin / K3.
 */
public final class EmberBrandSure {

    /** Latch: one auto-pin after craft per path id */
    public static final String C_AUTO_PIN = "p1_brand_auto_pin_";

    private EmberBrandSure() {}

    public static boolean craftReady(int haveShards, int need) {
        return need > 0 && haveShards >= need;
    }

    public static boolean pinReady(int haveBrand, int needBrand, int haveCoin, int needCoin) {
        return needBrand > 0 && haveBrand >= needBrand && haveCoin >= needCoin;
    }

    /**
     * D5025: after craft, if brandpath set and pin mats ready → one auto pin.
     * @return true when auto pin started
     */
    public static boolean maybeAutoPinAfterCraft(Player p, PlayerData d, EmberGrowthService g) {
        if (p == null || d == null || g == null) return false;
        int path = EmberBrandPath.get(d);
        if (!EmberBrandPath.valid(path)) return false;
        String key = C_AUTO_PIN + path;
        if (d.periodCount(key, "all") > 0) return false;
        String slot = EmberBrandPath.pinSlot(path);
        String affix = EmberBrandPath.pinAffixId(path);
        if (slot == null || affix == null) return false;
        d.addPeriodCount(key, "all", 1);
        boolean ok = g.autoBrandPin(p, slot, affix);
        if (!ok) {
            d.addPeriodCount(key, "all", -1);
            return false;
        }
        return true;
    }
}
