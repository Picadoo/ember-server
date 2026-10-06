package town.sunshine.corerpg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * D241 / Job A: {@code %ember_daily_left%} / {@code %ember_weekly_left%} — the "今日剩余 X/3" / "本周剩余 X/1" counts
 * from {@code docs/design/DESIGN-dungeon-daily-weekly.md} §3.4 / §7, wired to the counters that replaced the old
 * 3-per-day / 1-per-week tickets in S0 (design-ember-stamina-dnf-daily §A.1–A.2). Display only, read-only, no new
 * mechanic:
 * <ul>
 *   <li>{@code daily_left} = daily-dungeon entries the current stamina still pays for today:
 *       {@code stamina / costOf("daily")} (90 / 30 = 3 at a fresh reset, the design's "≈ 3 次/日"). Bank stamina is
 *       not counted — {@link StaminaService#consumeForEnter} only spends current stamina.</li>
 *   <li>{@code weekly_left} = this week's free weekly-dungeon entry still unused: {@code weekly_grant_credit} for the
 *       weekly dungeon (1 per week, Monday 00:00 Asia/Shanghai; same value as {@code %corerpg_stamina_credit_weekly%}).
 *       Further weekly entries cost stamina like any other.</li>
 * </ul>
 * Bukkit-free so the numbers are unit-tested.
 */
final class EmberPapiCounts {

    private EmberPapiCounts() {}

    static final Set<String> KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("daily_left", "weekly_left")));

    /** null when StaminaService / data is absent or the key is not a count key (PAPI then shows the literal). */
    static String resolve(StaminaService st, PlayerData d, String key) {
        if (st == null || d == null || key == null) return null;
        if ("daily_left".equals(key)) return dailyLeftText(st.getStamina(d), st.costOf("daily"));
        if ("weekly_left".equals(key)) {
            st.ensure(d); // rolls the weekly credit over on a new week, exactly like %corerpg_stamina_credit_weekly%
            return String.valueOf(Math.max(0, d.getWeeklyGrantCreditWeekly()));
        }
        return null;
    }

    /** entries current stamina pays for; a zero cost means daily entries are not stamina-limited ("∞"). */
    static String dailyLeftText(int stamina, int cost) {
        if (cost <= 0) return "∞";
        return String.valueOf(Math.max(0, stamina) / cost);
    }
}
