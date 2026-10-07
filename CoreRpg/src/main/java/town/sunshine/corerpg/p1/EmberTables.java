package town.sunshine.corerpg.p1;

import java.util.Arrays;
import java.util.List;

/**
 * ember-v1.0-P1 numeric tables (策划书 §5.1 成色, §5.2 精工, §6.1 强化, §7.1 阶级基准, §7.2 公式参数).
 * Immutable; pure Java so it can be unit-tested offline. {@link #defaults()} is the book's values;
 * ember-v1.yml may override them, but only via {@link #of} which validates shape and monotonicity.
 */
public final class EmberTables {

    public static final int MAX_TIER = 3;
    public static final int MAX_QUALITY = 3;
    public static final int MAX_CRAFT = 3;
    public static final int MAX_ENHANCE = 10;

    /** §7.1 武器基准 A by tier T0..T3. */
    private final double[] weaponA;
    /** §7.1 护符额外生命 h by tier. */
    private final double[] charmH;
    /** §7.1 护符防御 D by tier. */
    private final double[] charmD;
    /** §5.1 成色增量 q: 标准/精良/卓越/极品. */
    private final double[] quality;
    /** §5.2 精工增量 f: 0/2/4/6%. */
    private final double[] craft;
    /** §6.1 累计基准增量 e for +0..+10. */
    private final double[] enhance;

    public final double critRate;
    public final double critMult;
    public final double critMinCharge;
    public final double defK;
    public final double defFloor;
    public final double sustainHpMult;
    public final double levelAtkPer;
    public final double levelHpPer;
    public final int levelBase;
    public final int levelCap;
    public final double skillMult;

    private EmberTables(double[] a, double[] h, double[] d, double[] q, double[] f, double[] e,
                        double critRate, double critMult, double critMinCharge, double defK, double defFloor,
                        double sustainHpMult, double levelAtkPer, double levelHpPer, int levelBase, int levelCap,
                        double skillMult) {
        this.weaponA = a; this.charmH = h; this.charmD = d;
        this.quality = q; this.craft = f; this.enhance = e;
        this.critRate = critRate; this.critMult = critMult; this.critMinCharge = critMinCharge;
        this.defK = defK; this.defFloor = defFloor; this.sustainHpMult = sustainHpMult;
        this.levelAtkPer = levelAtkPer; this.levelHpPer = levelHpPer;
        this.levelBase = levelBase; this.levelCap = levelCap; this.skillMult = skillMult;
    }

    private static final EmberTables DEFAULTS = new EmberTables(
            new double[]{12, 24, 42, 70},
            new double[]{20, 50, 95, 165},
            new double[]{2, 6, 10, 14},
            new double[]{0.00, 0.04, 0.08, 0.12},
            new double[]{0.00, 0.02, 0.04, 0.06},
            new double[]{0.00, 0.04, 0.08, 0.13, 0.18, 0.24, 0.30, 0.37, 0.44, 0.52, 0.60},
            0.10, 1.5, 0.9, 40.0, 0.5, 1.00, 0.2, 1.0, 10, 60, 1.5);

    public static EmberTables defaults() { return DEFAULTS; }

    /**
     * Builds a validated table set. Any list may be null to keep the default. Throws
     * IllegalArgumentException on a wrong length, a negative value or a non-monotonic row,
     * so a bad config can never silently produce a different curve.
     */
    public static EmberTables of(List<? extends Number> a, List<? extends Number> h, List<? extends Number> d,
                                 List<? extends Number> q, List<? extends Number> f, List<? extends Number> e,
                                 double critRate, double critMult, double defK, double defFloor, double sustainHpMult) {
        EmberTables x = DEFAULTS;
        double[] aa = row("tier.weapon_a", a, x.weaponA, MAX_TIER + 1);
        double[] hh = row("tier.charm_h", h, x.charmH, MAX_TIER + 1);
        double[] dd = row("tier.charm_d", d, x.charmD, MAX_TIER + 1);
        double[] qq = row("quality", q, x.quality, MAX_QUALITY + 1);
        double[] ff = row("craft", f, x.craft, MAX_CRAFT + 1);
        double[] ee = row("enhance", e, x.enhance, MAX_ENHANCE + 1);
        if (!(critRate >= 0 && critRate <= 1)) throw new IllegalArgumentException("crit.rate out of [0,1]: " + critRate);
        if (!(critMult >= 1 && critMult <= 5)) throw new IllegalArgumentException("crit.mult out of [1,5]: " + critMult);
        if (!(defK > 0)) throw new IllegalArgumentException("defense.k must be > 0: " + defK);
        if (!(defFloor > 0 && defFloor <= 1)) throw new IllegalArgumentException("defense.floor out of (0,1]: " + defFloor);
        if (!(sustainHpMult >= 1 && sustainHpMult <= 2)) throw new IllegalArgumentException("sustain_hp_mult out of [1,2]: " + sustainHpMult);
        return new EmberTables(aa, hh, dd, qq, ff, ee, critRate, critMult, x.critMinCharge, defK, defFloor,
                sustainHpMult, x.levelAtkPer, x.levelHpPer, x.levelBase, x.levelCap, x.skillMult);
    }

    private static double[] row(String name, List<? extends Number> in, double[] def, int len) {
        if (in == null || in.isEmpty()) return def.clone();
        if (in.size() != len) throw new IllegalArgumentException(name + " needs " + len + " values, got " + in.size());
        double[] out = new double[len];
        for (int i = 0; i < len; i++) {
            Number n = in.get(i);
            if (n == null) throw new IllegalArgumentException(name + "[" + i + "] is null");
            double v = n.doubleValue();
            if (!(v >= 0) || Double.isInfinite(v)) throw new IllegalArgumentException(name + "[" + i + "] invalid: " + v);
            if (i > 0 && v < out[i - 1]) throw new IllegalArgumentException(name + " must be non-decreasing at " + i);
            out[i] = v;
        }
        return out;
    }

    public double weaponA(int tier) { return weaponA[clampTier(tier)]; }
    public double charmH(int tier) { return charmH[clampTier(tier)]; }
    public double charmD(int tier) { return charmD[clampTier(tier)]; }
    public double quality(int q) { return quality[clamp(q, 0, MAX_QUALITY)]; }
    public double craft(int f) { return craft[clamp(f, 0, MAX_CRAFT)]; }
    public double enhance(int e) { return enhance[clamp(e, 0, MAX_ENHANCE)]; }

    static int clampTier(int t) { return clamp(t, 0, MAX_TIER); }
    static int clamp(int v, int lo, int hi) { return v < lo ? lo : (v > hi ? hi : v); }

    @Override public String toString() {
        return "A=" + Arrays.toString(weaponA) + " h=" + Arrays.toString(charmH) + " D=" + Arrays.toString(charmD)
                + " q=" + Arrays.toString(quality) + " f=" + Arrays.toString(craft) + " e=" + Arrays.toString(enhance)
                + " crit=" + critRate + "x" + critMult + " def=max(" + defFloor + "," + defK + "/(" + defK + "+D))";
    }
}
