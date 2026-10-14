package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CX D13329 · 短征日差软目标极薄有奖（一日一次骨尘×{@link #BONUS}）。
 * <p>触发：今日有奖不同本 n≥{@link EmberShortDayDivChase#SOFT_TARGET} 且今日未领。
 * period {@link #C_CLAIM} · day。零改 SOFT_TARGET / chase/enter / 日帽 / S / targets / weekly_goals。
 */
public final class EmberShortDayDivSoftClaim {

    public static final String C_CLAIM = "p1_short_div_soft_claim";
    /** Bone dust ×2 once/day when soft target met. */
    public static final int BONUS = 2;

    private EmberShortDayDivSoftClaim() {}

    public static boolean claimedToday(PlayerData d) {
        return d != null && d.periodCount(C_CLAIM, "day") > 0;
    }

    /** n≥SOFT_TARGET and not yet claimed today. */
    public static boolean claimable(int n, boolean claimed) {
        return n >= EmberShortDayDivChase.SOFT_TARGET && !claimed;
    }

    public static boolean claimable(PlayerData d, int n) {
        return d != null && claimable(n, claimedToday(d));
    }

    /** TrMenu bit: claimable → {@code "1"}, else {@code "0"}. */
    public static String claimOk(int n, boolean claimed) {
        return claimable(n, claimed) ? "1" : "0";
    }

    public static String claimedBit(boolean claimed) {
        return claimed ? "1" : "0";
    }

    /**
     * Honest line with progress {@code n/3}:
     * 可领 · 骨尘×2 · n/3 | 已领 · n/3 | 未满 · n/3
     */
    public static String claimLine(int n, boolean claimed) {
        int nn = Math.max(0, n);
        String prog = EmberShortDayDivChase.progress(nn);
        if (claimed) return "已领 · " + prog;
        if (claimable(nn, false)) return "可领 · 骨尘×" + BONUS + " · " + prog;
        return "未满 · " + prog;
    }

    /**
     * claimable → period++ → Vault/NI 骨尘×{@link #BONUS}.
     * @return true when granted
     */
    public static boolean tryClaim(Player p, PlayerData d, int n) {
        if (p == null || d == null || !claimable(d, n)) return false;
        d.addPeriodCount(C_CLAIM, "day", 1);
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
        if (ok) {
            try {
                p.sendMessage(EmberRunService.P + "§a日差软奖 · 骨尘 +§f" + BONUS + "§a（今日一次 · "
                        + EmberShortDayDivChase.progress(n) + "）");
                p.sendActionBar("§a日差软奖 · 骨尘+" + BONUS);
            } catch (Throwable ignored) { }
        }
        return ok;
    }

    /** Reject reason for menu/command (honest; no bare-cmd teaching). */
    public static String rejectHint(int n, boolean claimed) {
        int nn = Math.max(0, n);
        if (claimed) return "今日日差软奖已领（" + EmberShortDayDivChase.progress(nn) + "）。";
        if (nn < EmberShortDayDivChase.SOFT_TARGET)
            return "未满软目标（" + EmberShortDayDivChase.progress(nn) + "）· 有奖不同本满 "
                    + EmberShortDayDivChase.SOFT_TARGET + " 后再领。";
        return "暂时无法领取。";
    }
}
