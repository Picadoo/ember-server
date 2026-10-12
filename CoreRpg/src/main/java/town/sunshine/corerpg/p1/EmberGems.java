package town.sunshine.corerpg.p1;

/**
 * Gem Stage2 (budget PASS 收紧2): sock codes → capped flat A/D + controlled life-steal / move-speed.
 * Sharp ATK only counts when the blade is T2+ (p1sim G1_t2only ✅). Absent sockets → zeros.
 */
public final class EmberGems {

    public static final int CODE_EMPTY = 0;
    public static final int CODE_SHARP = 1;
    public static final int CODE_STEADY = 2;
    public static final int CODE_DRAIN = 3;
    public static final int CODE_GALE = 4;

    public static final double SHARP_ATK = 0.1;
    public static final double STEADY_DEF = 0.1;
    public static final double DRAIN_LS = 0.01;
    public static final double GALE_MS = 0.02;

    public static final double CAP_FLAT_A = 0.2;
    public static final double CAP_FLAT_D = 0.2;
    public static final double CAP_LIFE_STEAL = 0.05;
    public static final double CAP_MOVE_SPEED = 0.08;

    /** Sharp combat floor: blade tier must be ≥ this (T2+). */
    public static final int SHARP_MIN_TIER = 2;

    private EmberGems() {}

    public static double flatA(EmberItemData blade, EmberItemData charm) {
        if (blade == null || blade.tier < SHARP_MIN_TIER) return 0.0;
        return Math.min(CAP_FLAT_A, countCode(blade, charm, CODE_SHARP) * SHARP_ATK);
    }

    public static double flatD(EmberItemData blade, EmberItemData charm) {
        return Math.min(CAP_FLAT_D, countCode(blade, charm, CODE_STEADY) * STEADY_DEF);
    }

    public static double lifeStealPct(EmberItemData blade, EmberItemData charm) {
        return Math.min(CAP_LIFE_STEAL, countCode(blade, charm, CODE_DRAIN) * DRAIN_LS);
    }

    public static double moveSpeedPct(EmberItemData blade, EmberItemData charm) {
        return Math.min(CAP_MOVE_SPEED, countCode(blade, charm, CODE_GALE) * GALE_MS);
    }

    static int countCode(EmberItemData blade, EmberItemData charm, int code) {
        return countOn(blade, code) + countOn(charm, code);
    }

    static int countOn(EmberItemData it, int code) {
        if (it == null || code <= 0) return 0;
        int n = 0;
        int max = it.sockMax();
        for (int i = 0; i < max; i++) {
            if (it.sockAt(i) == code) n++;
        }
        return n;
    }

    /**
     * TIP-BJ: ≤1-line short effect from live constants (display only; zero flat raise).
     * Stone names: 锋利/稳固/汲取/疾风. Sharp notes blade T{SHARP_MIN_TIER}+.
     */
    public static String shortEffect(int code) {
        switch (code) {
            case CODE_SHARP:
                return "锋利 · 攻击+" + fmtFlat(SHARP_ATK) + "（刃 T" + SHARP_MIN_TIER + "+ 才计）";
            case CODE_STEADY:
                return "稳固 · 防御+" + fmtFlat(STEADY_DEF);
            case CODE_DRAIN:
                return "汲取 · 吸血+" + fmtPct(DRAIN_LS);
            case CODE_GALE:
                return "疾风 · 移速+" + fmtPct(GALE_MS);
            default:
                return null;
        }
    }

    static String fmtFlat(double v) {
        return java.math.BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
    }

    static String fmtPct(double v) {
        return java.math.BigDecimal.valueOf(v * 100.0).stripTrailingZeros().toPlainString() + "%";
    }
}
