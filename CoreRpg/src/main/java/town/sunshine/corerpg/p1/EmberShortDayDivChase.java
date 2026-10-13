package town.sunshine.corerpg.p1;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * TIP-CU D13326 · 短征选页「日差本可冲」冲图钉死（只读 · 零经济）。
 * <p>进度 n：今日 {@code rewardedToday≥1} 的已解锁短征本数（distinct）。
 * 软目标 {@link #SOFT_TARGET}=3 <b>仅展示</b>；禁写入 weekly_goals / 禁发额外币料。
 * <p>chase：已解锁 ∧ dayLeft&gt;0 ∧ rewardedToday==0；稳定序 {@link EmberShortRules#SHORT_KEYS}；
 * 若 todayKey 合格则优先；今日推本已有奖（rewardedToday≥1）自然跳过。
 * <p>chase_ok：chase 非空→{@code 1}；done：n≥3 或无合格 chase→{@code 1}。
 * 纯逻辑，无 Bukkit；禁改日帽 / S40–S58 / weekly_goals.targets.short / Cast / Aid。
 */
public final class EmberShortDayDivChase {

    /** Soft UI target only — zero reward, zero weekly_goals write. */
    public static final int SOFT_TARGET = 3;

    private EmberShortDayDivChase() {}

    /**
     * Distinct unlocked shorts with at least one rewarded clear today.
     * @param unlocked map key → unlocked ({@link EmberShortPath#unlocked} in live)
     * @param rewardedToday map key → rewarded clears today
     */
    public static int distinctRewarded(Predicate<String> unlocked, ToIntFunction<String> rewardedToday) {
        if (unlocked == null || rewardedToday == null) return 0;
        int n = 0;
        for (String k : EmberShortRules.SHORT_KEYS) {
            if (k != null && unlocked.test(k) && rewardedToday.applyAsInt(k) >= 1) n++;
        }
        return n;
    }

    /**
     * Resolve chase map key: unlocked ∧ dayLeft&gt;0 ∧ rewardedToday==0.
     * Prefer todayKey when it qualifies; else stable SHORT_KEYS order.
     * @param todayKey optional sx_today (may be null/blank)
     */
    public static String chaseKey(String todayKey, Predicate<String> unlocked,
                                  ToIntFunction<String> dayLeft, ToIntFunction<String> rewardedToday) {
        if (unlocked == null || dayLeft == null || rewardedToday == null) return "";
        if (todayKey != null && !todayKey.isEmpty() && qualifies(todayKey, unlocked, dayLeft, rewardedToday)) {
            return todayKey;
        }
        for (String k : EmberShortRules.SHORT_KEYS) {
            if (k != null && qualifies(k, unlocked, dayLeft, rewardedToday)) return k;
        }
        return "";
    }

    private static boolean qualifies(String k, Predicate<String> unlocked,
                                     ToIntFunction<String> dayLeft, ToIntFunction<String> rewardedToday) {
        return unlocked.test(k) && dayLeft.applyAsInt(k) > 0 && rewardedToday.applyAsInt(k) == 0;
    }

    /** Chase non-empty → {@code "1"}, else {@code "0"} (TrMenu single check). */
    public static String chaseOk(String chase) {
        return chase != null && !chase.isEmpty() ? "1" : "0";
    }

    /**
     * Done bit: n≥{@link #SOFT_TARGET} <b>or</b> no qualifying chase → {@code "1"}, else {@code "0"}.
     * Read-only display; zero economy.
     */
    public static String done(int n, String chase) {
        if (n >= SOFT_TARGET) return "1";
        if (chase == null || chase.isEmpty()) return "1";
        return "0";
    }

    /** Progress line: {@code 今日有奖不同本 n/3}. */
    public static String line(int n) {
        return "今日有奖不同本 " + Math.max(0, n) + "/" + SOFT_TARGET;
    }

    /** Compact progress {@code n/3} (bare {@code short_div} optional). */
    public static String progress(int n) {
        return Math.max(0, n) + "/" + SOFT_TARGET;
    }
}
