package town.sunshine.corerpg.p1;

/**
 * G02 set parameters, 策划书 §4.2–4.4 (fixed by the book; not exposed in config so a typo cannot change the
 * trigger structure). Index 1..3 = awakening I..III; index 0 unused.
 */
public final class EmberSetRules {

    private EmberSetRules() {}

    /** 焚烬 per-second coefficient × snapshot B, 4 ticks */
    public static final double[] BURN_COEF = {0, 0.26, 0.34, 0.42};
    /** 烬爆 per-target coefficient × B */
    public static final double[] BURST_COEF = {0, 0.65, 0.80, 0.95};
    public static final double[] BURST_RADIUS = {0, 3.0, 3.0, 3.5};
    /** 炽愈 heal as a share of the effective max HP at trigger time */
    public static final double[] SUSTAIN_PCT = {0, 0.025, 0.0325, 0.04};

    public static final int SCORCH_EVERY = 3;
    public static final int BURST_EVERY = 5;
    public static final int SUSTAIN_EVERY = 5;
    public static final long BURST_ICD_MS = 3000L;
    public static final long SUSTAIN_ICD_MS = 6000L;
    public static final int BURN_TICKS = 4;
    public static final long BURN_INTERVAL_MS = 1000L;
    public static final int BURN_MAX_TARGETS = 5;
    public static final int BURST_MAX_TARGETS = 5;
    public static final long COMBAT_TIMEOUT_MS = 8000L;
    public static final double MIN_CHARGE = 0.9;
    /** how many counted root ids each player remembers for de-duplication */
    public static final int ROOT_MEMORY = 128;

    static int awk(int a) { return a < 1 ? 1 : (a > 3 ? 3 : a); }
    public static double burnCoef(int awakening) { return BURN_COEF[awk(awakening)]; }
    public static double burstCoef(int awakening) { return BURST_COEF[awk(awakening)]; }
    public static double burstRadius(int awakening) { return BURST_RADIUS[awk(awakening)]; }
    public static double sustainPct(int awakening) { return SUSTAIN_PCT[awk(awakening)]; }
}
