package town.sunshine.corerpg.p1;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * TIP-CG D13312 · 短征选页「本周短征可冲」冲图钉死（只读 · 零经济）。
 * 已解锁 sx 中优先 day_left&gt;0；同档稳定序 {@link EmberShortRules#SHORT_KEYS}；
 * 可选对齐 sx_today（若已解锁且 day_left&gt;0 则优先；全满练习回落时若已解锁亦优先）。
 * chase_ok：chase 非空→{@code 1} 否则 {@code 0}（TrMenu 单条件灰，吸取 TIP-CE 教训）。
 * done：进度≥target→{@code 1}（只读；禁改 targets / addGoal）。
 * 纯逻辑，无 Bukkit；禁改 addGoal / weekly_goals.targets / 徽 / 日帽 / SoftPity。
 */
public final class EmberGoalShortChase {

    private EmberGoalShortChase() {}

    /**
     * Resolve chase map key among unlocked shorts.
     * @param todayKey optional sx_today (may be null/blank); preferred when it qualifies
     * @param unlocked predicate: map key → unlocked
     * @param dayLeft remaining rewarded clears today for that map
     * @return unlocked key with day_left&gt;0 (prefer today); else any unlocked (practice); else ""
     */
    public static String chaseKey(String todayKey, Predicate<String> unlocked, ToIntFunction<String> dayLeft) {
        if (unlocked == null || dayLeft == null) return "";
        // Pass 1: unlocked + day_left > 0 — prefer sx_today if it qualifies
        if (todayKey != null && !todayKey.isEmpty()
                && unlocked.test(todayKey) && dayLeft.applyAsInt(todayKey) > 0) {
            return todayKey;
        }
        for (String k : EmberShortRules.SHORT_KEYS) {
            if (k != null && unlocked.test(k) && dayLeft.applyAsInt(k) > 0) return k;
        }
        // Pass 2: all day-full → practice fallback among unlocked — prefer sx_today if unlocked
        if (todayKey != null && !todayKey.isEmpty() && unlocked.test(todayKey)) {
            return todayKey;
        }
        for (String k : EmberShortRules.SHORT_KEYS) {
            if (k != null && unlocked.test(k)) return k;
        }
        return "";
    }

    /** Chase non-empty → {@code "1"}, else {@code "0"} (TrMenu single check). */
    public static String chaseOk(String chase) {
        return chase != null && !chase.isEmpty() ? "1" : "0";
    }

    /**
     * Weekly short goal done bit: progress ≥ target → {@code "1"}, else {@code "0"}.
     * target≤0 → {@code "0"} (goal absent / not configured).
     */
    public static String done(int progress, int target) {
        if (target <= 0) return "0";
        return progress >= target ? "1" : "0";
    }
}
