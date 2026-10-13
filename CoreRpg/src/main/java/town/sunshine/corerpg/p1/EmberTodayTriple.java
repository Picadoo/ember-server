package town.sunshine.corerpg.p1;

/**
 * TIP-CH D13313 · 冒险「今日三冲」智路由钉死（只读 · 零经济）。
 * 优先序：① short-today（今日推已解锁且今日仍有有奖）→ ② short-goal（周目标未满且 chase_ok）
 * → ③ event-chase（chase_ok）→ ④ 空。
 * kind：{@code today|week|event|empty}；ok：非空→{@code 1}（TrMenu 单条件灰，吸取 TIP-CE 教训）。
 * 纯逻辑，无 Bukkit；禁抬日帽/short:5/徽/event_rate/NEED；复用既有三别名门禁谓词由调用方给。
 */
public final class EmberTodayTriple {

    public static final String KIND_TODAY = "today";
    public static final String KIND_WEEK = "week";
    public static final String KIND_EVENT = "event";
    public static final String KIND_EMPTY = "empty";

    public static final String ALIAS_TODAY = "short-today";
    public static final String ALIAS_WEEK = "short-goal";
    public static final String ALIAS_EVENT = "event-chase";

    /** Immutable pick: kind + resolved map key (empty when kind=empty). */
    public static final class Pick {
        public final String kind;
        public final String mapKey;
        public final String alias;

        Pick(String kind, String mapKey, String alias) {
            this.kind = kind;
            this.mapKey = mapKey == null ? "" : mapKey;
            this.alias = alias == null ? "" : alias;
        }

        public boolean present() {
            return mapKey != null && !mapKey.isEmpty();
        }
    }

    private static final Pick EMPTY = new Pick(KIND_EMPTY, "", "");

    private EmberTodayTriple() {}

    /**
     * Resolve today-triple chase.
     * @param todayKey sx_today (may be null/blank)
     * @param todayUnlocked ShortPath/progressFlag unlock for todayKey
     * @param todayDayLeft remaining rewarded clears today for todayKey
     * @param weekDone goal_short_done (progress≥target)
     * @param weekChase goal_short_chase map key (empty if none)
     * @param eventChase event_chase map key (empty if none)
     */
    public static Pick pick(String todayKey, boolean todayUnlocked, int todayDayLeft,
                            boolean weekDone, String weekChase, String eventChase) {
        if (todayKey != null && !todayKey.isEmpty() && todayUnlocked && todayDayLeft > 0) {
            return new Pick(KIND_TODAY, todayKey, ALIAS_TODAY);
        }
        if (!weekDone && weekChase != null && !weekChase.isEmpty()) {
            return new Pick(KIND_WEEK, weekChase, ALIAS_WEEK);
        }
        if (eventChase != null && !eventChase.isEmpty()) {
            return new Pick(KIND_EVENT, eventChase, ALIAS_EVENT);
        }
        return EMPTY;
    }

    /** Non-empty chase → {@code "1"}, else {@code "0"} (TrMenu single check). */
    public static String ok(Pick pick) {
        return pick != null && pick.present() ? "1" : "0";
    }

    /** Convenience: same as {@link #ok(Pick)} from map key. */
    public static String ok(String mapKey) {
        return mapKey != null && !mapKey.isEmpty() ? "1" : "0";
    }
}
