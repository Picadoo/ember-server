package town.sunshine.corerpg.p1;

/**
 * D458: one-glance reroll pity progress text. Display only — does not change pity math.
 * D601: delegates to {@link EmberHopeMeter} so enhance + reroll share the same meter style.
 */
public final class EmberRerollPity {

    private EmberRerollPity() {}

    /**
     * Short glance line for menus.
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
}
