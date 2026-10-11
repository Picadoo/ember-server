package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D11729: first successful 勋阶日礼 of the day → +{@link #BONUS} 空白纸.
 * Zero cost table. Not vip coin table densify (win gift).
 */
public final class EmberVipDailyWinBlank {

    public static final String C_USED = "p1_vip_daily_win_blank";
    public static final int BONUS = 1;

    private EmberVipDailyWinBlank() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeGift(Player p, PlayerData d) {
        if (p == null || d == null || !ready(d)) return false;
        boolean ok = false;
        try {
            EmberVault v = EmberVault.get();
            if (v != null) ok = v.credit(p, EmberUpgradeRules.MAT_BLANK, BONUS);
            if (!ok) {
                JavaPlugin pl = (JavaPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                    town.sunshine.corerpg.CoreRpgPlugin cp = (town.sunshine.corerpg.CoreRpgPlugin) pl;
                    if (cp.getNiBridge() != null)
                        ok = cp.getNiBridge().giveNiItem(p, EmberUpgradeRules.MAT_BLANK, BONUS);
                }
            }
        } catch (Throwable ignored) { }
        if (!ok) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a勋阶日礼加赠 · 空白纸 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a勋阶日礼加赠 · 空白纸+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
