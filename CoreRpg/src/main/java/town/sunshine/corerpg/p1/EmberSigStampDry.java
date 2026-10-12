package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * D690: signature stamp miss dry-streak visibility.
 * Zero STAMP_RATE / imprint price change — display only.
 * TIP-BW: also exposed as PAPI {@code sig_stamp_hope} via {@code EmberGrowthService#sigPapi}.
 */
public final class EmberSigStampDry {

    /** Period all: consecutive repeat-clear stamp misses (cleared on stamp hit). */
    public static final String C_DRY = "p1_sig_stamp_dry";

    /** Expected clears at STAMP_RATE for half-line (ceil 1/rate). Golden mirror. */
    public static final int EXPECT_CLEARS = (int) Math.ceil(1.0 / EmberSignature.STAMP_RATE);

    /** D1054: live rate from economy SoT. */
    public static int expectClears() {
        return (int) Math.ceil(1.0 / EmberSignature.stampRate());
    }

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
        if (EmberSigStampPity.ready(d))
            return String.format(Locale.ROOT,
                    " §a· 签名软保底已满（连空%d · 下次必出盖章候选）", n);
        return String.format(Locale.ROOT,
                " §8· 签名连空 §f%d§8/%d（约%d%% · 烙印可保底）",
                n, EmberSigStampPity.need(), EmberSigAcq.ratePct());
    }

    public static String glance(PlayerData d) {
        int n = get(d);
        if (n <= 0) return "§8签名掉落：未连空 · 约" + EmberSigAcq.ratePct() + "%";
        if (EmberSigStampPity.ready(d))
            return "§a签名软保底已满 · 下次重打必出盖章候选 · 连空" + n;
        return "§e签名连空 §f" + n + "/" + EmberSigStampPity.need() + " §7· 约" + EmberSigAcq.ratePct() + "% · 烙印保底";
    }
}
