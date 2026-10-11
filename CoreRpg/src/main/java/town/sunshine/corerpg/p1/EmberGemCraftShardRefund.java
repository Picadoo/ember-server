package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

/**
 * D8385: first successful gem craft of the day → refund {@link #BONUS} shard.
 * Zero gem craft cost table. Not bone-gift densify.
 */
public final class EmberGemCraftShardRefund {

    public static final String C_USED = "p1_gem_craft_shard_rf";
    public static final int BONUS = 1;

    private EmberGemCraftShardRefund() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeRefund(Player p, PlayerData d, NiBridge ni) {
        if (p == null || d == null || ni == null || !ready(d)) return false;
        if (!ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, BONUS)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a合成退碎 · 碎片 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a合成退碎 · +" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
