package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * D690: signature stamp miss dry-streak visibility.
 * Zero STAMP_RATE / imprint price change — display only (not a PAPI twin).
 */
public final class EmberSigStampDry {

    /** Period all: consecutive repeat-clear stamp misses (cleared on stamp hit). */
    public static final String C_DRY = "p1_sig_stamp_dry";

    /** Expected clears at STAMP_RATE for half-line (ceil 1/rate). */
    public static final int EXPECT_CLEARS = (int) Math.ceil(1.0 / EmberSignature.STAMP_RATE);

    private EmberSigStampDry() {}

    public static int get(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_DRY, "all"));
    }

    public static void onMiss(PlayerData d) {
        if (d == null) return;
        d.addPeriodCount(C_DRY, "all", 1);
    }

    public static void onHit(PlayerData d) {
        if (d == null) return;
        int cur = d.periodCount(C_DRY, "all");
        if (cur != 0) d.addPeriodCount(C_DRY, "all", -cur);
    }

    /** Append after missLine — empty when dry=0. */
    public static String missAppend(PlayerData d) {
        int n = get(d);
        if (n <= 0) return "";
        return String.format(Locale.ROOT,
                " §8· 签名连空 §f%d§8 局（约%d%% · 期望~%d局 · 烙印可保底）",
                n, EmberSigAcq.ratePct(), EXPECT_CLEARS);
    }

    public static String glance(PlayerData d) {
        int n = get(d);
        if (n <= 0) return "§8签名掉落：未连空 · 约" + EmberSigAcq.ratePct() + "%";
        return "§e签名连空 §f" + n + " §7局 · 约" + EmberSigAcq.ratePct() + "% · 烙印保底";
    }
}
