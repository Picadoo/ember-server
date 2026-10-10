package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D458: one-glance reroll pity progress text. Display only — does not change pity math.
 */
public final class EmberRerollPity {

    private EmberRerollPity() {}

    /**
     * Short glance line for menus.
     * @param pity current counter (tries below cap)
     * @param need rules.pity threshold (forced when pity &gt;= need)
     */
    public static String glance(int pity, int need) {
        if (need <= 0) return "§8保底未配置";
        int p = Math.max(0, pity);
        if (p >= need) return String.format(Locale.ROOT, "§a保底 %d/%d · 这次必出上限档", need, need);
        return String.format(Locale.ROOT, "§7保底 §f%d§7/%d", p, need);
    }

    /** Hub/gear combined half-line fragments stay slot-local; this is blade|charm empty. */
    public static String emptySlot(String slotLabel) {
        return "§8" + slotLabel + "—";
    }
}
