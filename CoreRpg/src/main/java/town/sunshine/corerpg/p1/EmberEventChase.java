package town.sunshine.corerpg.p1;

import java.util.function.Predicate;

/**
 * TIP-CE D13310 · 房间事件「冲一下」冲图钉死（只读 · 零经济）。
 * featured 已首通 → featured；否则已通最高 q01…q07；皆无 → 空。
 * soft_ready 只读 {@link EmberEventSoftPity#get}≥{@link EmberEventSoftPity#NEED}。
 * chase_ok：chase 非空→{@code 1} 否则 {@code 0}（TrMenu 单条件灰，TIP-CE FAIL 修）。
 * 纯逻辑，无 Bukkit；禁改 SoftPity.onSettle / NEED / event_rate / Pack。
 */
public final class EmberEventChase {

    /** Mainline ordinary maps eligible as chase fallback (ascending → highest wins). */
    public static final String[] MAIN_KEYS = {"q01", "q02", "q03", "q04", "q05", "q06", "q07"};

    private EmberEventChase() {}

    /**
     * Resolve chase map key.
     * @param featured this week's featured key (may be null/blank)
     * @param cleared predicate: map key → first-clear fact
     * @return featured if cleared; else highest cleared among {@link #MAIN_KEYS}; else ""
     */
    public static String chaseKey(String featured, Predicate<String> cleared) {
        if (cleared == null) return "";
        if (featured != null && !featured.isEmpty() && cleared.test(featured)) return featured;
        String best = "";
        for (String k : MAIN_KEYS) {
            if (k != null && cleared.test(k)) best = k;
        }
        return best;
    }

    /** Chase non-empty → {@code "1"}, else {@code "0"} (TrMenu single check; TIP-CE FAIL 修). */
    public static String chaseOk(String chase) {
        return chase != null && !chase.isEmpty() ? "1" : "0";
    }

    /** SoftPity dry ≥ NEED → {@code "1"}, else {@code "0"} (read-only; does not touch NEED). */
    public static String softReady(int dry) {
        return dry >= EmberEventSoftPity.NEED ? "1" : "0";
    }

    /** Same gate as {@link #softReady(int)} as boolean. */
    public static boolean isSoftReady(int dry) {
        return dry >= EmberEventSoftPity.NEED;
    }
}
