package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.PlayerData;

/** D12065: lock land on cap from cap−1 → +1 shard once/day. Not LockFlatRefund densify. */
public final class EmberLockCapRefund {
    public static final String C_USED = "p1_lock_cap_refund";
    public static final int BONUS = 1;
    private EmberLockCapRefund() {}
    public static boolean usedToday(PlayerData d) { return d != null && d.periodCount(C_USED, "day") > 0; }
    public static boolean ready(PlayerData d) { return d != null && !usedToday(d); }
    public static boolean maybeRefund(Player p, PlayerData d, boolean lock, int oldEnc, int newEnc, int quality, EmberAffix.Rules rules) {
        if (!lock || p == null || d == null || rules == null || !ready(d)) return false;
        int cap = rules.cap(quality);
        if (cap <= 1) return false;
        int oldT = Math.min(EmberAffix.decodeTier(oldEnc), cap);
        int newT = Math.min(EmberAffix.decodeTier(newEnc), cap);
        if (oldT != cap - 1 || newT != cap) return false;
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
            p.sendMessage(EmberRunService.P + "§a锁洗达上限退碎 · 碎片 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a锁洗上限 · 退碎+" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
