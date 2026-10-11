package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

/**
 * D8705: first successful gem insert of the day → +{@link #BONUS} shard.
 * Zero gem craft cost. Not GemCraftShardRefund densify (insert, not craft).
 */
public final class EmberGemInsertShard {

    public static final String C_USED = "p1_gem_insert_shard";
    public static final int BONUS = 1;

    private EmberGemInsertShard() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeGift(Player p, PlayerData d, NiBridge ni) {
        if (p == null || d == null || ni == null || !ready(d)) return false;
        if (!ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, BONUS)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a镶嵌加赠 · 碎片 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a镶嵌加赠 · 碎+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
