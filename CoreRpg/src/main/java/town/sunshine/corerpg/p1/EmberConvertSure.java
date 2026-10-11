package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D5857: weekly convert preview→do when enhance already 0 and mats ready.
 * Zero weekly cap / price / quality-cap change. Not forge sure densify.
 */
public final class EmberConvertSure {
    private EmberConvertSure() {}

    public static boolean eligible(EmberItemData t) {
        return t != null && t.enhance <= 0;
    }

    public static boolean matsOk(Player p, PlayerData d, EmberItemData t, CoreRpgPlugin plugin) {
        if (p == null || d == null || t == null || plugin == null) return false;
        int blanks = EmberConvertRules.blanks(t.tier);
        int coins = EmberConvertRules.coins(t.tier);
        if (d.getCoin() < coins) return false;
        int haveBlank = 0;
        try {
            if (plugin.getNiBridge() != null)
                haveBlank = plugin.getNiBridge().countInInventory(p, EmberUpgradeRules.MAT_BLANK);
        } catch (Throwable ignored) { }
        return haveBlank >= blanks;
    }
}
