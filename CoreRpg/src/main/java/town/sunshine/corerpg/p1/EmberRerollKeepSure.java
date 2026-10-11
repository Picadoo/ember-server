package town.sunshine.corerpg.p1;

/**
 * D5985: after paid reroll, auto-keep candidate when strictly better tier (or pity-forced at/above old).
 * Zero pity / pool / shard cost change. Not tip twin / not ConfirmTokens densify on worse rolls.
 */
public final class EmberRerollKeepSure {

    private EmberRerollKeepSure() {}

    /**
     * @param oldEnc previous encoded affix (0 = empty — caller installs directly)
     * @param newEnc candidate encoded
     * @param quality piece quality (cap)
     * @param forced pity-forced roll
     * @param rules growth reroll table (cap)
     */
    public static boolean autoKeepNew(int oldEnc, int newEnc, int quality, boolean forced,
                                      EmberAffix.Rules rules) {
        if (rules == null || newEnc == 0) return false;
        if (oldEnc == 0) return true; // empty slot already auto-installs; keep helper honest
        int cap = rules.cap(quality);
        int oldT = Math.min(EmberAffix.decodeTier(oldEnc), cap);
        int newT = Math.min(EmberAffix.decodeTier(newEnc), cap);
        if (newT > oldT) return true;
        if (forced && newT >= oldT) return true; // pity at cap / not worse
        return false;
    }
}
