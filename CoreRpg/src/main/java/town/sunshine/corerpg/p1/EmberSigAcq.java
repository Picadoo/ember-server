package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D456: signature acquisition clarity — settle miss line + menu half-line.
 * Zero change to {@link EmberSignature#STAMP_RATE} / imprint price / ALTS.
 */
public final class EmberSigAcq {

    private EmberSigAcq() {}

    public static int ratePct() {
        return (int) Math.round(EmberSignature.STAMP_RATE * 100.0);
    }

    /**
     * Settle half-line when this delivery paid a repeat-clear insignia but no signature stamp.
     * @param mapKey e.g. q01 (may be null → generic)
     */
    public static String missLine(String mapKey) {
        String map = mapKey == null || mapKey.isEmpty() ? "" : mapKey.toUpperCase(Locale.ROOT) + " ";
        return String.format(Locale.ROOT,
                "§7本局未出%s签名件（约%d%%）· 徽记已入账 · 烙印可保底拿",
                map, ratePct());
    }

    /** Adventure / sig-page acquisition half-line (static honesty). */
    public static String menuLine() {
        return String.format(Locale.ROOT,
                "§8重打：每局徽记 + 约%d%% 签名；未出仍拿徽记，烙印可保底",
                ratePct());
    }

    /** True when this ledger key is the repeat-clear insignia grant (not first-clear fc_sigmark). */
    public static boolean isRepeatMarkKey(String ledgerKey) {
        return "sig_mark".equals(ledgerKey);
    }
}
