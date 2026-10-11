package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D11537: first successful 竞技胜 of the day → +{@link #BONUS} 空白纸.
 * Zero cost table. Not arena coin densify (win gift).
 */
public final class EmberArenaWinBlank {

    public static final String C_USED = "p1_arena_win_blank";
    public static final int BONUS = 1;

    private EmberArenaWinBlank() {}

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
            p.sendMessage(EmberRunService.P + "§a竞技胜加赠 · 空白纸 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a竞技胜加赠 · 空白纸+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
