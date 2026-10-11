package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D7425: boss spawn + 0 heal pots → gift 1 bound pot once per uid@day.
 * Zero potion price table. Not StockPath densify.
 */
public final class EmberBossPotGift {

    public static final String C_USED = "p1_boss_pot_gift";

    private EmberBossPotGift() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return true when gift given. */
    public static boolean maybeGift(Player p, PlayerData d, CoreRpgPlugin plugin) {
        if (p == null || d == null || plugin == null || !ready(d)) return false;
        if (!EmberMode.isP1(p)) return false;
        try {
            EmberSupplyService sup = plugin.getEmberSupplies();
            if (sup == null) return false;
            if (sup.countHealPotions(p) > 0) return false;
            int got = sup.give(p, 1, "boss_gift");
            if (got <= 0) return false;
            d.addPeriodCount(C_USED, "day", 1);
            p.sendMessage(EmberRunService.P + "§a首领救急药 · 回复药 ×1（空瓶对峙 · 本日一次）");
            try { p.sendActionBar("§a救急药 · ×1"); } catch (Throwable ignored) { }
            try { EmberBossPotExtra.maybeExtra(p, d, plugin); } catch (Throwable ignored) { } // D10993
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
