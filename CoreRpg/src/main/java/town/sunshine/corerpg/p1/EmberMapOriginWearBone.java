package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D11921: first time worn blade/charm origin matches active map of the day → +{@link #BONUS} bone.
 * Zero drop table. Not MapFarmHitBone densify (wear, not settle hit).
 */
public final class EmberMapOriginWearBone {

    public static final String C_USED = "p1_map_origin_wear_bone";
    public static final int BONUS = 1;

    private EmberMapOriginWearBone() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeGift(Player p, PlayerData d, EmberLoadout lo, String mapKey) {
        if (p == null || d == null || lo == null || !ready(d)) return false;
        if (!EmberMapResonance.active(lo, mapKey)) return false;
        boolean ok = false;
        try {
            EmberVault v = EmberVault.get();
            if (v != null) ok = v.credit(p, EmberUpgradeRules.MAT_BONE, BONUS);
            if (!ok) {
                JavaPlugin pl = (JavaPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                    town.sunshine.corerpg.CoreRpgPlugin cp = (town.sunshine.corerpg.CoreRpgPlugin) pl;
                    if (cp.getNiBridge() != null)
                        ok = cp.getNiBridge().giveNiItem(p, EmberUpgradeRules.MAT_BONE, BONUS);
                }
            }
        } catch (Throwable ignored) { }
        if (!ok) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a图源穿戴加赠 · 骨尘 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a图源穿戴 · 骨尘+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
