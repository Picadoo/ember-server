package town.sunshine.corerpg.p1;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * D172 (forge review X1 / X4 / X5 / X15): the pure parts of the durable payment path — request ids, the owed refund
 * lines of a price (materials, coins, forge marks), the "reroll paid, roll owed" counter and the seeded roll.
 * No Bukkit here so it is unit-tested.
 */
public final class EmberPayRules {
    private EmberPayRules() {}

    /**
     * PlayerData counters, period "all", + item uid: paid reroll sequence n / roll owed (n*2 + lock). D208: legacy only —
     * the sequence is {@code EmberItemData.rerollN} on the item and the roll commits in the settling transaction; a
     * {@code p4_rro_} left by an older version is still recovered at join.
     */
    public static final String C_RRN = "p4_rrn_", C_RRO = "p4_rro_";
    /** forge marks counter (EmberRunService.C_MARK) — the "mark" delivery kind may only touch these */
    public static final String MARK_COUNTER = "p1_mark_t";
    private static final Pattern MARK_ITEM = Pattern.compile("t[1-3]");

    public static String undoRid(String uid, int rev) { return "undo:" + uid + ":" + rev; }

    public static String rerollRid(String uid, int n) { return "afx:" + uid + ":" + n; }

    /**
     * D208: request id of one paid reroll attempt: {@code afx:<uid>:<n>:<attempt>} — n = the item's committed sequence + 1,
     * the attempt tag keeps a refunded attempt's id from ever being reused (its hold / paid marker stay its own).
     */
    public static String rerollRid(String uid, int n, long now) { return rerollRid(uid, n) + ":" + Long.toString(now, 36); }

    /** D208: request id of one imprint attempt (imp:&lt;uid&gt;:&lt;rev&gt;:&lt;attempt&gt;) */
    public static String imprintRid(String uid, int rev, long now) { return "imp:" + uid + ":" + rev + ":" + Long.toString(now, 36); }

    /** one redemption = one fresh id (a second click is a second redemption; the busy guard stops double clicks) */
    public static String redeemRid(UUID owner, long now, int rnd) {
        return "redeem:" + owner.toString().substring(0, 8) + ":" + Long.toString(now, 36) + ":" + Integer.toString(Math.abs(rnd) % 46656, 36);
    }

    /** D430: random forge request id (≤64) */
    public static String forgeRollRid(UUID owner, long now, int rnd) {
        return "froll:" + owner.toString().substring(0, 8) + ":" + Long.toString(now, 36) + ":" + Integer.toString(Math.abs(rnd) % 46656, 36);
    }

    public static boolean markItem(String item) { return item != null && MARK_ITEM.matcher(item).matches(); }

    public static String markCounter(String item) { return MARK_COUNTER + item.substring(1); }

    /** D208: a {@code sigmark} delivery may only touch the insignia counter of a signature map */
    public static boolean sigItem(String map) { return map != null && EmberSignature.hasMap(map); }

    /** the refund lines of a price: one per material, coins, marks (same order as they are taken) */
    public static List<EmberItemStore.Owed> owed(Map<String, Integer> mats, int coins, int markTier, int marks, String note) {
        return owed(mats, coins, markTier, marks, null, 0, note);
    }

    /** D208: + boss insignia of one map (delivery kind {@code sigmark}, item = map key) — the imprint price */
    public static List<EmberItemStore.Owed> owed(Map<String, Integer> mats, int coins, int markTier, int marks, String sigMap, int sigMarks, String note) {
        List<EmberItemStore.Owed> l = new ArrayList<EmberItemStore.Owed>();
        if (mats != null) for (Map.Entry<String, Integer> m : mats.entrySet()) if (m.getValue() != null && m.getValue() > 0)
            l.add(EmberItemStore.Owed.mat(m.getKey(), m.getValue(), note));
        if (coins > 0) l.add(new EmberItemStore.Owed("coin", "coin", coins, note));
        if (marks > 0 && markTier >= 1 && markTier <= 3) l.add(new EmberItemStore.Owed("mark", "t" + markTier, marks, note));
        if (sigMarks > 0 && sigItem(sigMap)) l.add(new EmberItemStore.Owed("sigmark", sigMap, sigMarks, note));
        return l;
    }

    // ------------------------------------------------------------------ reroll: paid → committed → rolled, exactly once

    public static int owedValue(int n, boolean lock) { return n * 2 + (lock ? 1 : 0); }
    public static int owedSeq(int v) { return v / 2; }
    public static boolean owedLock(int v) { return (v & 1) == 1; }

    public enum Recovery { APPLY, WAIT, CLEAR }

    /**
     * What to do with a "roll owed" found at join: the settling transaction committed → roll now (same seed, same
     * result as the one that may have been shown); its refund hold still open in this server run → wait; neither
     * (refunded, or the payment never reached the save) → drop the owed roll.
     */
    public static Recovery recovery(String state) {
        if ("committed".equals(state)) return Recovery.APPLY;
        if ("hold".equals(state) || state == null) return Recovery.WAIT; // null = could not read → try again later
        return Recovery.CLEAR;
    }

    /** seed of a paid roll: server salt + player + request id → the same roll however often it is (re)applied */
    public static long seed(long salt, String owner, String rid) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Long.toString(salt).getBytes(StandardCharsets.UTF_8));
            md.update((byte) '|');
            md.update(String.valueOf(owner).getBytes(StandardCharsets.UTF_8));
            md.update((byte) '|');
            md.update(String.valueOf(rid).getBytes(StandardCharsets.UTF_8));
            byte[] h = md.digest();
            long v = 0;
            for (int i = 0; i < 8; i++) v = (v << 8) | (h[i] & 0xff);
            return v;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
