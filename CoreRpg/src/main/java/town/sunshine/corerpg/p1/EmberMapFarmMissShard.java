package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/** D12129: map-family settle with no farm-hit → +1 shard once/day. Not MapFarmHitBone densify. */
public final class EmberMapFarmMissShard {
    public static final String C_USED = "p1_map_farm_miss_shard";
    public static final int BONUS = 1;
    private EmberMapFarmMissShard() {}
    public static boolean usedToday(PlayerData d) { return d != null && d.periodCount(C_USED, "day") > 0; }
    public static boolean ready(PlayerData d) { return d != null && !usedToday(d); }
    public static boolean maybeGift(Player p, PlayerData d) {
        if (p == null || d == null || !ready(d)) return false;
        boolean ok = false;
        try {
            EmberVault v = EmberVault.get();
            if (v != null) ok = v.credit(p, EmberUpgradeRules.MAT_SHARD, BONUS);
            if (!ok) {
                JavaPlugin pl = (JavaPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                    town.sunshine.corerpg.CoreRpgPlugin cp = (town.sunshine.corerpg.CoreRpgPlugin) pl;
                    if (cp.getNiBridge() != null) ok = cp.getNiBridge().giveNiItem(p, EmberUpgradeRules.MAT_SHARD, BONUS);
                }
            }
        } catch (Throwable ignored) { }
        if (!ok) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a图装未中加赠 · 碎片 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§e图装未中 · 碎片+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
