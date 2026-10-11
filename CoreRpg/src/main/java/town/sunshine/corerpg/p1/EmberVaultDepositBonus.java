package town.sunshine.corerpg.p1;

import java.util.Map;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D10689: first non-empty vault depositAll of the day → +{@link #BONUS} of the first deposited mat.
 * Zero vault cap table. Not SoulDust/life densify (warehouse deposit).
 */
public final class EmberVaultDepositBonus {

    public static final String C_USED = "p1_vault_deposit_bonus";
    public static final int BONUS = 1;

    private EmberVaultDepositBonus() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeGrant(Player p, PlayerData d, EmberVault vault, Map<String, Long> moved) {
        if (p == null || d == null || vault == null || moved == null || moved.isEmpty() || !ready(d)) return false;
        String id = null;
        for (String k : moved.keySet()) { id = k; break; }
        if (id == null) return false;
        if (!vault.credit(p, id, BONUS)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a首存加赠 · 材料 +§f" + BONUS + "§a（今日一次）");
            p.sendActionBar("§a首存加赠 · +" + BONUS);
        } catch (Throwable ignored) { }
        return true;
    }
}
