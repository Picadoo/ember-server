package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * D391–D417 短征（DESIGN short-dungeon）：Bukkit-free 日帽 / 首通 / 结算包。
 * <ul>
 *   <li>sx01 → S40：有奖 币80+碎片4+骨尘3；首通另加 200/8/胚料1</li>
 *   <li>sx02 → S41：有奖同 S40 量级；首通略薄 180/6/胚料1</li>
 *   <li>sx03 → S42：有奖同 S40/S41 量级；首通略薄 160/8/胚料1</li>
 *   <li>sx04 → S43：有奖同量级；首通略薄 150/6/胚料1</li>
 *   <li>sx05 → S44：有奖同量级；首通略薄 140/6/胚料1</li>
 *   <li>sx06 → S45：有奖同量级；首通略薄 130/6/胚料1</li>
 *   <li>sx07 → S46：有奖同量级；首通略薄 120/6/胚料1</li>
 *   <li>sx08 → S47：有奖同量级；首通略薄 110/6/胚料1</li>
 *   <li>sx09 → S48：有奖同量级；首通略薄 100/6/胚料1</li>
 *   <li>sx10 → S49：有奖同量级；首通略薄 90/6/胚料1</li>
 *   <li>sx11 → S50：有奖同量级；首通略薄 80/6/胚料1</li>
 *   <li>日帽后（第 4+ 次）：仍可进本（体力照扣），结算包为空</li>
 * </ul>
 * 金额与 {@link EmberEconomy} / {@code ember-v1-economy.yml} 金样对齐；p1sim 模型另号。
 */
public final class EmberShortRules {

    public static final String KEY = "sx01";
    public static final String CLAIM = "p1_sx01_day";
    public static final int DAILY_CAP = 3;
    public static final int COST = 30;
    public static final String REQUIRES = "q01";

    /** Legacy sx01 grant keys (REG S40). */
    public static final String G_CLEAR_COIN = "sx_clear_coin";
    public static final String G_CLEAR_SHARD = "sx_clear_shard";
    public static final String G_CLEAR_BONE = "sx_clear_bone";
    public static final String G_FC_COIN = "sx_fc_coin";
    public static final String G_FC_SHARD = "sx_fc_shard";
    public static final String G_FC_BLANK = "sx_fc_blank";
    public static final String G_PRACTICE = "sx_practice";

    private EmberShortRules() {}

    /** Economy source id for a short map key (sx01→S40 … sx11→S50). */
    public static String economyId(String mapKey) {
        if (mapKey != null && mapKey.equalsIgnoreCase("sx11")) return "S50";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx10")) return "S49";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx09")) return "S48";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx08")) return "S47";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx07")) return "S46";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx06")) return "S45";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx05")) return "S44";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx04")) return "S43";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx03")) return "S42";
        if (mapKey != null && mapKey.equalsIgnoreCase("sx02")) return "S41";
        return "S40";
    }

    /**
     * Ledger grant key prefix. sx01 keeps legacy {@code sx_} for REG S40 compat;
     * later shorts use {@code <key>_} (e.g. sx02_clear_coin → S41).
     */
    public static String grantPrefix(String mapKey) {
        if (mapKey != null && !mapKey.isEmpty() && !"sx01".equalsIgnoreCase(mapKey)) {
            return mapKey.toLowerCase(Locale.ROOT) + "_";
        }
        return "sx_";
    }

    public static String gClearCoin(String mapKey) { return grantPrefix(mapKey) + "clear_coin"; }
    public static String gClearShard(String mapKey) { return grantPrefix(mapKey) + "clear_shard"; }
    public static String gClearBone(String mapKey) { return grantPrefix(mapKey) + "clear_bone"; }
    public static String gFcCoin(String mapKey) { return grantPrefix(mapKey) + "fc_coin"; }
    public static String gFcShard(String mapKey) { return grantPrefix(mapKey) + "fc_shard"; }
    public static String gFcBlank(String mapKey) { return grantPrefix(mapKey) + "fc_blank"; }
    public static String gPractice(String mapKey) { return grantPrefix(mapKey) + "practice"; }

    /** True when this settle still pays the clear package (day count before bump &lt; cap). */
    public static boolean paysReward(int rewardedToday, int dailyCap) {
        return rewardedToday < Math.max(0, dailyCap);
    }

    /** sx01-compat overload (tests / callers without map key). */
    public static List<EmberRunRules.Grant> settleGrants(int rewardedToday, int dailyCap, boolean firstClearPaid) {
        return settleGrants(KEY, rewardedToday, dailyCap, firstClearPaid);
    }

    /**
     * Build settlement grants for one clear.
     * @param mapKey short map key (sx01 / sx02 / sx03 / sx04 / sx05 / sx06 / sx07 / sx08 / sx09 / sx10 / sx11 / …)
     * @param rewardedToday already-paid rewarded clears today (before this settle)
     * @param firstClearPaid whether the career first-clear package was already paid
     */
    public static List<EmberRunRules.Grant> settleGrants(String mapKey, int rewardedToday, int dailyCap, boolean firstClearPaid) {
        String key = mapKey == null || mapKey.isEmpty() ? KEY : mapKey.toLowerCase(Locale.ROOT);
        String econ = economyId(key);
        List<EmberRunRules.Grant> out = new ArrayList<EmberRunRules.Grant>();
        if (!paysReward(rewardedToday, dailyCap)) {
            out.add(new EmberRunRules.Grant(gPractice(key), EmberRunRules.Kind.COIN, null, 0, null));
            return Collections.unmodifiableList(out);
        }
        int coin = EmberEconomy.amount(econ, "clear.coin");
        int shard = EmberEconomy.amount(econ, "clear.shard");
        int bone = EmberEconomy.amount(econ, "clear.bone");
        if (coin > 0) out.add(new EmberRunRules.Grant(gClearCoin(key), EmberRunRules.Kind.COIN, null, coin, null));
        if (shard > 0) out.add(new EmberRunRules.Grant(gClearShard(key), EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, shard, null));
        if (bone > 0) out.add(new EmberRunRules.Grant(gClearBone(key), EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BONE, bone, null));
        if (!firstClearPaid) {
            int fcCoin = EmberEconomy.amount(econ, "fc.coin");
            int fcShard = EmberEconomy.amount(econ, "fc.shard");
            int fcBlank = EmberEconomy.amount(econ, "fc.blank");
            if (fcCoin > 0) out.add(new EmberRunRules.Grant(gFcCoin(key), EmberRunRules.Kind.COIN, null, fcCoin, null));
            if (fcShard > 0) out.add(new EmberRunRules.Grant(gFcShard(key), EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, fcShard, null));
            if (fcBlank > 0) out.add(new EmberRunRules.Grant(gFcBlank(key), EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BLANK, fcBlank, null));
        }
        return Collections.unmodifiableList(out);
    }

    /** Bukkit-free entry gate wording. Null = may enter. */
    public static String entryProblemText(String playerName, boolean q01Done) {
        if (!q01Done) return playerName + " 未解锁短征（需本人首通 " + REQUIRES.toUpperCase(Locale.ROOT) + "）";
        return null;
    }

    public static String rewardLine() {
        return rewardLine(KEY);
    }

    public static String rewardLine(String mapKey) {
        String key = mapKey == null || mapKey.isEmpty() ? KEY : mapKey.toLowerCase(Locale.ROOT);
        String econ = economyId(key);
        return "有奖通关：币 " + EmberEconomy.amount(econ, "clear.coin")
                + " + 碎片 " + EmberEconomy.amount(econ, "clear.shard")
                + " + 骨尘 " + EmberEconomy.amount(econ, "clear.bone")
                + " · 日有奖帽 " + DAILY_CAP
                + " · 首通另加币 " + EmberEconomy.amount(econ, "fc.coin")
                + " + 碎片 " + EmberEconomy.amount(econ, "fc.shard")
                + " + 胚料 " + EmberEconomy.amount(econ, "fc.blank");
    }

    public static String dayLine(int rewardedToday, int dailyCap) {
        int cap = Math.max(0, dailyCap);
        int n = Math.max(0, rewardedToday);
        if (n < cap) return "今日有奖 " + n + "/" + cap;
        return "今日有奖已满 " + n + "/" + cap + "（再通关无结算包，仍耗体力）";
    }

    /** D395: remaining rewarded clears today (clamp ≥0). */
    public static int dayLeft(int rewardedToday, int dailyCap) {
        return Math.max(0, Math.max(0, dailyCap) - Math.max(0, rewardedToday));
    }

    /** Canonical short expedition keys (sx01–sx11). D406/D407/D410/D412/D414/D417 fc aggregate scan order. */
    public static final String[] SHORT_KEYS = {"sx01", "sx02", "sx03", "sx04", "sx05", "sx06", "sx07", "sx08", "sx09", "sx10", "sx11"};

    /** D406/D417 %corerpg_p1_sx_fc_left%: unpaid first-clear count clamped to 0..11. */
    public static int fcLeft(int unpaidCount) {
        int n = Math.max(0, unpaidCount);
        return n > SHORT_KEYS.length ? SHORT_KEYS.length : n;
    }

    /** D406/D417 optional %corerpg_p1_sx_fc_pending_line%. */
    public static String fcPendingLine(int unpaidCount) {
        int n = fcLeft(unpaidCount);
        if (n <= 0) return "十一本首通已齐";
        return "还有 " + n + " 本生涯首通未领";
    }

    /**
     * D409 W1a: second settle tell after a rewarded clear — vault→forge chase.
     * Color codes may vary at call site; semantic must stay.
     */
    public static String spendChaseLine() {
        return "§7材料已进仓 · 工坊可花（强化/精工/成色）";
    }

    /**
     * D409 W1a optional: when this map's rewarded-after hits dailyCap, append day-full half-clause.
     * Empty when still under cap (or cap≤0).
     */
    public static String spendChaseDayFullHalf(int rewardedAfter, int dailyCap) {
        int cap = Math.max(0, dailyCap);
        if (cap <= 0) return "";
        if (Math.max(0, rewardedAfter) < cap) return "";
        return " §8· 本本今日有奖已满";
    }

    /** D409 W1a: full second-line tell (chase + optional day-full). */
    public static String spendChaseTell(int rewardedAfter, int dailyCap) {
        return spendChaseLine() + spendChaseDayFullHalf(rewardedAfter, dailyCap);
    }

    /** Claim counter id for a short map key ({@code p1_sx01_day} …). */
    public static String claimKey(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return CLAIM;
        return "p1_" + mapKey.toLowerCase(Locale.ROOT) + "_day";
    }
}
