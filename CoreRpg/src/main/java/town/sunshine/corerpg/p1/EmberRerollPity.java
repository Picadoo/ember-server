package town.sunshine.corerpg.p1;

/**
 * D458: one-glance reroll pity progress text. Display only — does not change pity math.
 * D601: {@link #glance} still delegates to HopeMeter for existing PAPI.
 * D1248: {@link #remainLine} / {@link #attemptsUntilCap} — deterministic remain (non-hope wording).
 * D12764–D12765: ternary remain/attempts honor day-first {@link EmberRerollPityNudge#ready}.
 */
public final class EmberRerollPity {

    private EmberRerollPity() {}

    /**
     * Short glance line for menus (legacy hope style for existing PAPI).
     * @param pity current counter (tries below cap)
     * @param need rules.pity threshold (forced when pity &gt;= need)
     */
    public static String glance(int pity, int need) {
        return EmberHopeMeter.glance(pity, need, EmberHopeMeter.READY_REROLL);
    }

    /** Hub/gear combined half-line fragments stay slot-local; this is blade|charm empty. */
    public static String emptySlot(String slotLabel) {
        return EmberHopeMeter.emptySlot(slotLabel);
    }

    /**
     * D1248: attempts including current until forced cap-tier (pity &gt;= need forces).
     * When already forced, returns 1 (this try). Binary = no nudge.
     */
    public static int attemptsUntilCap(int pity, int need) {
        return attemptsUntilCap(pity, need, false);
    }

    /**
     * D12764: when day-first pity nudge is still ready and more than one attempt remains,
     * report one fewer (this try will also add {@link EmberRerollPityNudge#BONUS}).
     */
    public static int attemptsUntilCap(int pity, int need, boolean nudgeReady) {
        if (need <= 0) return 0;
        int p = Math.max(0, pity);
        if (p >= need) return 1;
        int incl = need - p;
        if (nudgeReady && incl > 1) return incl - 1;
        return incl;
    }

    /**
     * D1248: chat/ActionBar remain — no hope wording. Binary = no nudge.
     * forced → {@code 这次必出上限档}; else {@code 第pity/need · 含本次≤N次必出上限档}.
     */
    public static String remainLine(int pity, int need) {
        return remainLine(pity, need, false);
    }

    /**
     * D12765: remain with optional day-first nudge honesty —
     * {@code 第3/5 · 含本次≤1次必出上限档 · 今日加码待用}.
     */
    public static String remainLine(int pity, int need, boolean nudgeReady) {
        if (need <= 0) return "保底未配置";
        int p = Math.max(0, pity);
        if (p >= need) return "这次必出上限档";
        int incl = attemptsUntilCap(p, need, nudgeReady);
        String base = "第" + p + "/" + need + " · 含本次≤" + incl + "次必出上限档";
        if (nudgeReady && p < need) return base + " · 今日加码待用";
        return base;
    }

    /** After a non-cap roll: remain for next try (pityAfter already applied). */
    public static String afterRemainLine(int pityAfter, int need) {
        if (need <= 0) return "";
        int p = Math.max(0, pityAfter);
        if (p >= need) return "下次洗练必出上限档";
        int incl = attemptsUntilCap(p, need);
        return "含下次≤" + incl + "次必出上限档";
    }
}
