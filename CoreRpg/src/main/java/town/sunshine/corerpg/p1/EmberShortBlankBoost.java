package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import town.sunshine.corerpg.PlayerData;

/**
 * D7681: short-expedition rewarded settle → +{@link #BONUS} blank once per uid@day.
 * Zero short reward table. Not BulkBlank/DupRefund densify.
 */
public final class EmberShortBlankBoost {

    public static final String C_USED = "p1_short_blank_boost";
    public static final int BONUS = 1;

    private EmberShortBlankBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }

    /** Grant blank if ready; @return true when given. */
    public static boolean maybeGrant(Player p, PlayerData d) {
        if (p == null || d == null || !consume(d)) return false;
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
        if (ok) {
            p.sendMessage(EmberRunService.P + "§a短征加赠 · 胚料 +§f" + BONUS + "§a（本日一次）");
            try { p.sendActionBar("§a短征加赠 · 胚+" + BONUS); } catch (Throwable ignored) { }
        }
        return ok;
    }
}
