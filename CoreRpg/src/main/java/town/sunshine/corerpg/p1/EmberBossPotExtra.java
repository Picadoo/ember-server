package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D10993: when boss pot gift fires, once/day grant {@link #EXTRA} more pot.
 * Zero potion price. Not BossPotGift densify (extra bottle on same trigger).
 */
public final class EmberBossPotExtra {

    public static final String C_USED = "p1_boss_pot_extra";
    public static final int EXTRA = 1;

    private EmberBossPotExtra() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeExtra(Player p, PlayerData d, CoreRpgPlugin plugin) {
        if (p == null || d == null || plugin == null || !ready(d)) return false;
        try {
            EmberSupplyService sup = plugin.getEmberSupplies();
            if (sup == null) return false;
            int got = sup.give(p, EXTRA, "boss_gift_extra");
            if (got <= 0) return false;
            d.addPeriodCount(C_USED, "day", 1);
            p.sendMessage(EmberRunService.P + "§a救急加赠 · 回复药 +§f" + EXTRA + "§a（今日一次）");
            try { p.sendActionBar("§a救急加赠 · +" + EXTRA); } catch (Throwable ignored) { }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
