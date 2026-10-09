package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * D391 短征 sx01（DESIGN-ember-short-dungeon-p1-reachable）：Bukkit-free 日帽 / 首通 / S40 结算包。
 * <ul>
 *   <li>有奖通关（日 ≤ {@link #DAILY_CAP}）：币 80 + 碎片 4 + 骨尘 3</li>
 *   <li>生涯首通另加：币 200 + 碎片 8 + 胚料 1</li>
 *   <li>日帽后（第 4+ 次）：仍可进本（体力照扣），结算包为空</li>
 * </ul>
 * 金额与 {@link EmberEconomy} S40 / {@code ember-v1-economy.yml} 金样对齐；p1sim 模型另号。
 */
public final class EmberShortRules {

    public static final String KEY = "sx01";
    public static final String CLAIM = "p1_sx01_day";
    public static final int DAILY_CAP = 3;
    public static final int COST = 30;
    public static final String REQUIRES = "q01";

    public static final String G_CLEAR_COIN = "sx_clear_coin";
    public static final String G_CLEAR_SHARD = "sx_clear_shard";
    public static final String G_CLEAR_BONE = "sx_clear_bone";
    public static final String G_FC_COIN = "sx_fc_coin";
    public static final String G_FC_SHARD = "sx_fc_shard";
    public static final String G_FC_BLANK = "sx_fc_blank";
    public static final String G_PRACTICE = "sx_practice";

    private EmberShortRules() {}

    /** True when this settle still pays the clear package (day count before bump &lt; cap). */
    public static boolean paysReward(int rewardedToday, int dailyCap) {
        return rewardedToday < Math.max(0, dailyCap);
    }

    /**
     * Build settlement grants for one clear.
     * @param rewardedToday already-paid rewarded clears today (before this settle)
     * @param firstClearPaid whether the career first-clear package was already paid
     */
    public static List<EmberRunRules.Grant> settleGrants(int rewardedToday, int dailyCap, boolean firstClearPaid) {
        List<EmberRunRules.Grant> out = new ArrayList<EmberRunRules.Grant>();
        if (!paysReward(rewardedToday, dailyCap)) {
            out.add(new EmberRunRules.Grant(G_PRACTICE, EmberRunRules.Kind.COIN, null, 0, null));
            return Collections.unmodifiableList(out);
        }
        int coin = EmberEconomy.amount("S40", "clear.coin");
        int shard = EmberEconomy.amount("S40", "clear.shard");
        int bone = EmberEconomy.amount("S40", "clear.bone");
        if (coin > 0) out.add(new EmberRunRules.Grant(G_CLEAR_COIN, EmberRunRules.Kind.COIN, null, coin, null));
        if (shard > 0) out.add(new EmberRunRules.Grant(G_CLEAR_SHARD, EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, shard, null));
        if (bone > 0) out.add(new EmberRunRules.Grant(G_CLEAR_BONE, EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BONE, bone, null));
        if (!firstClearPaid) {
            int fcCoin = EmberEconomy.amount("S40", "fc.coin");
            int fcShard = EmberEconomy.amount("S40", "fc.shard");
            int fcBlank = EmberEconomy.amount("S40", "fc.blank");
            if (fcCoin > 0) out.add(new EmberRunRules.Grant(G_FC_COIN, EmberRunRules.Kind.COIN, null, fcCoin, null));
            if (fcShard > 0) out.add(new EmberRunRules.Grant(G_FC_SHARD, EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, fcShard, null));
            if (fcBlank > 0) out.add(new EmberRunRules.Grant(G_FC_BLANK, EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BLANK, fcBlank, null));
        }
        return Collections.unmodifiableList(out);
    }

    /** Bukkit-free entry gate wording. Null = may enter. */
    public static String entryProblemText(String playerName, boolean q01Done) {
        if (!q01Done) return playerName + " 未解锁短征（需本人首通 " + REQUIRES.toUpperCase(Locale.ROOT) + "）";
        return null;
    }

    public static String rewardLine() {
        return "有奖通关：币 " + EmberEconomy.amount("S40", "clear.coin")
                + " + 碎片 " + EmberEconomy.amount("S40", "clear.shard")
                + " + 骨尘 " + EmberEconomy.amount("S40", "clear.bone")
                + " · 日有奖帽 " + DAILY_CAP
                + " · 首通另加币 " + EmberEconomy.amount("S40", "fc.coin")
                + " + 碎片 " + EmberEconomy.amount("S40", "fc.shard")
                + " + 胚料 " + EmberEconomy.amount("S40", "fc.blank");
    }

    public static String dayLine(int rewardedToday, int dailyCap) {
        int cap = Math.max(0, dailyCap);
        int n = Math.max(0, rewardedToday);
        if (n < cap) return "今日有奖 " + n + "/" + cap;
        return "今日有奖已满 " + n + "/" + cap + "（再通关无结算包，仍耗体力）";
    }
}
