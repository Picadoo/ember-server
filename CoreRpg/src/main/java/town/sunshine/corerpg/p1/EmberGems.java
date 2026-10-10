package town.sunshine.corerpg.p1;

/**
 * Gem Stage2: map sock codes on equipped blade/charm into capped flat A/D and controlled
 * life-steal / move-speed percents. Pure; no Bukkit. Absent sockets → zeros (bit-identical loadout).
 * Caps: flatA ≤ 0.5, flatD ≤ 0.5 (tightened after +1 FAIL), lifeSteal ≤ 0.05, moveSpeed ≤ 0.08.
 */
public final class EmberGems {

    public static final int CODE_EMPTY = 0;
    public static final int CODE_SHARP = 1;
    public static final int CODE_STEADY = 2;
    public static final int CODE_DRAIN = 3;
    public static final int CODE_GALE = 4;

    public static final double SHARP_ATK = 0.25;
    public static final double STEADY_DEF = 0.25;
    public static final double DRAIN_LS = 0.01;
    public static final double GALE_MS = 0.02;

    public static final double CAP_FLAT_A = 0.5;
    public static final double CAP_FLAT_D = 0.5;
    public static final double CAP_LIFE_STEAL = 0.05;
    public static final double CAP_MOVE_SPEED = 0.08;

    private EmberGems() {}

    public static double flatA(EmberItemData blade, EmberItemData charm) {
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
        int n = 0;
        n += countOn(blade, code);
        n += countOn(charm, code);
        return n;
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
}
