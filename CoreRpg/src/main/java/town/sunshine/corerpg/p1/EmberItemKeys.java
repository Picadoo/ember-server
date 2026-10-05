package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D208 (ARCH S1-4 · REG-counter-registry §4-4): the four item keys that used to hang on the owner's PlayerData —
 * affix {@code p4_af_<uid>}, reroll pity {@code p4_afp_<uid>}, signature {@code p1_sig_<uid>}, paid reroll sequence
 * {@code p4_rrn_<uid>} — now live on the item ({@link EmberItemData} version 2: {@code ember_v1} NBT, {@code cr_p1_item}
 * columns, HMAC). One rule for every reader and writer:
 * <ul>
 *   <li>a <b>v2</b> item is authoritative: its fields are the value, legacy counters for its uid are ignored (a stale
 *   counter can never resurrect an old pity or signature);</li>
 *   <li>a <b>v1</b> item carries none of them: the value is the legacy counter (test bots made before 1.65.42);</li>
 *   <li>every durable write of a v1 item (forge, reroll, imprint, keep) {@link #fold}s the counters into the new v2
 *   data, and {@link #clearLegacy} drops the counters once that transaction committed.</li>
 * </ul>
 * The pending-reroll key {@code p4_rro_} is gone from the write path: a paid reroll's result commits in the settling
 * item transaction itself (the cr_p1_txn row is the state); old {@code p4_rro_} counters are still recovered at join.
 * Pure (PlayerData only), unit-tested.
 */
public final class EmberItemKeys {
    private EmberItemKeys() {}

    private static int legacy(PlayerData d, String prefix, String uid) {
        return d == null || uid == null ? 0 : Math.max(0, d.periodCount(prefix + uid, "all"));
    }

    public static int affix(PlayerData d, EmberItemData it) {
        if (it == null) return 0;
        return it.itemKeys() ? it.affix : legacy(d, EmberGrowthService.C_AF, it.uid);
    }

    public static int afPity(PlayerData d, EmberItemData it) {
        if (it == null) return 0;
        return it.itemKeys() ? it.afPity : legacy(d, EmberGrowthService.C_AFP, it.uid);
    }

    public static int sig(PlayerData d, EmberItemData it) {
        if (it == null) return 0;
        return it.itemKeys() ? it.sigCode : legacy(d, EmberSignature.C_SIG, it.uid);
    }

    public static int rerollN(PlayerData d, EmberItemData it) {
        if (it == null) return 0;
        return it.itemKeys() ? it.rerollN : legacy(d, EmberPayRules.C_RRN, it.uid);
    }

    /** v1 + its legacy counters → the same piece as v2 (rev unchanged); a v2 item is returned as is */
    public static EmberItemData fold(PlayerData d, EmberItemData it) {
        if (it == null || it.itemKeys()) return it;
        return it.withItemKeys(legacy(d, EmberGrowthService.C_AF, it.uid), legacy(d, EmberGrowthService.C_AFP, it.uid),
                legacy(d, EmberSignature.C_SIG, it.uid), legacy(d, EmberPayRules.C_RRN, it.uid));
    }

    /** any legacy item-key counter left for this uid (p4_rro_ excluded: that is a pending roll, see recoverRolls) */
    public static boolean hasLegacy(PlayerData d, String uid) {
        if (d == null || uid == null) return false;
        for (String k : keys()) if (d.periodCount(k + uid, "all") != 0) return true;
        return false;
    }

    /** drops the legacy counters of {@code uid} (after the v2 fold committed); true when something changed */
    public static boolean clearLegacy(PlayerData d, String uid) {
        if (d == null || uid == null) return false;
        boolean ch = false;
        int sc = legacy(d, EmberSignature.C_SIG, uid); // the codex 「获得过」 scan reads the sig counters: keep it as a seen mark
        if (sc > 0) EmberGrowthService.markSeen(d, EmberSignature.byCode(sc));
        for (String k : keys()) {
            int v = d.periodCount(k + uid, "all");
            if (v != 0) { d.addPeriodCount(k + uid, "all", -v); ch = true; }
        }
        return ch;
    }

    static String[] keys() { return new String[]{EmberGrowthService.C_AF, EmberGrowthService.C_AFP, EmberSignature.C_SIG, EmberPayRules.C_RRN}; }
}
