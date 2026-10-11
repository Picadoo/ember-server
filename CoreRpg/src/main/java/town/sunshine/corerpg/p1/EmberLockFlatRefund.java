package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D11937: locked reroll that ends flat (new tier == old tier) → +{@link #BONUS} shard once/day.
 * Zero lock_shard / pity table. Not LockTierFloor / RerollLockShardSoft densify (flat refund).
 */
public final class EmberLockFlatRefund {

    public static final String C_USED = "p1_lock_flat_refund";
    public static final int BONUS = 1;

    private EmberLockFlatRefund() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeRefund(Player p, PlayerData d, boolean lock, int oldEnc, int newEnc, int quality,
                                      EmberAffix.Rules rules) {
        if (!lock || p == null || d == null || rules == null || !ready(d)) return false;
        int cap = rules.cap(quality);
        int oldT = Math.min(EmberAffix.decodeTier(oldEnc), cap);
        int newT = Math.min(EmberAffix.decodeTier(newEnc), cap);
        if (oldT <= 0 || newT != oldT) return false;
        boolean ok = false;
        try {
            EmberVault v = EmberVault.get();
            if (v != null) ok = v.credit(p, EmberUpgradeRules.MAT_SHARD, BONUS);
            if (!ok) {
                JavaPlugin pl = (JavaPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                    town.sunshine.corerpg.CoreRpgPlugin cp = (town.sunshine.corerpg.CoreRpgPlugin) pl;
                    if (cp.getNiBridge() != null)
                        ok = cp.getNiBridge().giveNiItem(p, EmberUpgradeRules.MAT_SHARD, BONUS);
                }
            }
        } catch (Throwable ignored) { }
        if (!ok) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a锁洗平档退碎 · 碎片 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a锁洗平档 · 退碎+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
