package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D236 / ARCH S3 first adapter: counterplay window registry (wall / whiff / break).
 * <p>Bukkit-free pure predicates and timing — behaviour-preserving extract from
 * {@code EmberRunDirector} (D188 / D192 / D193). Director still draws / FX / tellRun;
 * call sites that used Director statics now go through here (Director keeps thin
 * one-line delegates so older tests keep compiling).
 * <p>Skill field caps stay in {@code EmberRunMaps.Skill} parse (bv58 unchanged).
 */
public final class EmberCounterplay {

    private EmberCounterplay() { }

    /** D188: wall stun seconds cap (Skill parse also clamps to this) */
    public static final double WALL_STUN_MAX = 3.0;
    /** D192: whiff stun seconds cap */
    public static final double WHIFF_STUN_MAX = 2.0;
    /** D193: break_hp fraction of boss max HP cap */
    public static final double BREAK_HP_MAX = 0.5;
    /** D193: break stun seconds cap */
    public static final double BREAK_STUN_MAX = 2.0;
    /** D193: default break stun when break_hp set but break_stun omitted (Skill parse) */
    public static final double BREAK_STUN_DEFAULT = 1.0;

    /** half of the boss footprint used by charge crash / clear-run probes (Director BOSS_HALF_WIDTH) */
    public static final double BOSS_HALF_WIDTH = 0.4;
    /** charge shorter than this is not worth a telegraph */
    public static final double CHARGE_MIN = 2.0;

    /** probe: is (x,z) a wall / blocked cell? */
    public interface GroundTest { boolean ok(double x, double z); }

    // ------------------------------------------------------------------ arm (warn-start)

    /**
     * D192: arm a whiff window only when the skill carries whiff_stun and someone stands inside
     * the telegraph at warn start (standing far away is not a dodge).
     */
    public static boolean armWhiff(double whiffStun, boolean someoneInside) {
        return whiffStun > 0 && someoneInside;
    }

    /**
     * D193: damage the party must deal during the channel to break it.
     * {@code 0} = not armed. Floor 1.0 HP so tiny bosses still need a real hit.
     */
    public static double armBreakNeed(double maxHp, double breakHp) {
        if (breakHp <= 0) return 0;
        return Math.max(1.0, maxHp * breakHp);
    }

    /**
     * D188: a charge that will hit a real wall (clipped strip + wall probe) arms pendingCrash
     * only when the skill carries wall_stun.
     */
    public static boolean armWallCrash(double wallStun, boolean crashesIntoWall) {
        return wallStun > 0 && crashesIntoWall;
    }

    // ------------------------------------------------------------------ resolve (cast land / channel)

    /**
     * D192 落空破绽: armed at warn start and the hit landed on nobody → the boss staggers.
     * {@code done} null or whiffStun ≤ 0 → false.
     */
    public static boolean whiffs(boolean armed, int landed, EmberRunMaps.Skill done) {
        return armed && landed == 0 && done != null && done.whiffStun > 0;
    }

    /** overload without Skill (tests / callers that only have the stun seconds) */
    public static boolean whiffs(boolean armed, int landed, double whiffStun) {
        return armed && landed == 0 && whiffStun > 0;
    }

    /** D193 破招: channel armed (need &gt; 0) and party damage since warn start reached the need. */
    public static boolean broken(double need, double done) {
        return need > 0 && done >= need;
    }

    /**
     * D188: the step right after the clipped strip end hits a wall.
     * A strip that ran its full length, or stopped at a ledge / area edge, is no crash.
     * Identical to the former {@code EmberRunDirector.crashGrid}.
     */
    public static boolean crashGrid(GroundTest wall, double ox, double oz, double dx, double dz,
                                    double run, double max, double half) {
        if (run >= max - 1e-9) return false;
        double k = run + 0.25, px = -dz, pz = dx;
        double cx = ox + dx * k, cz = oz + dz * k;
        return wall.ok(cx, cz) || wall.ok(cx + px * half, cz + pz * half) || wall.ok(cx - px * half, cz - pz * half)
                || wall.ok(cx + dx * half, cz + dz * half);
    }

    /**
     * B2.165 clear-run along (dx,dz): every 0.25 step centre + box edges standable.
     * Identical to the former {@code EmberRunDirector.clearRunGrid}.
     */
    public static double clearRunGrid(GroundTest g, double ox, double oz, double dx, double dz,
                                      double max, double half) {
        double best = 0, px = -dz, pz = dx;
        for (double k = 0.25; k <= max + 1e-9; k += 0.25) {
            double cx = ox + dx * k, cz = oz + dz * k;
            if (!g.ok(cx, cz) || !g.ok(cx + px * half, cz + pz * half) || !g.ok(cx - px * half, cz - pz * half)
                    || !g.ok(cx + dx * half, cz + dz * half)) break;
            best = k;
        }
        return best;
    }

    // ------------------------------------------------------------------ timing / apply helpers (no Bukkit)

    /** stun duration in milliseconds from skill seconds (wall / whiff / break). */
    public static long stunMs(double seconds) {
        if (seconds <= 0) return 0L;
        return (long) (seconds * 1000);
    }

    /** potion amplifier ticks = seconds × 20 + 4 (Director wall/whiff/break slow padding). */
    public static int stunPotionTicks(double seconds) {
        if (seconds <= 0) return 0;
        return (int) (seconds * 20) + 4;
    }

    /**
     * After a counterplay stun starts: new recoverUntil / followStart lower bounds.
     * Returns {@code [stunUntil, recoverUntil, followStart]} (followStart unchanged when follow inactive
     * — pass the current followStart; caller decides whether a follow is queued).
     */
    public static long[] applyStunBounds(long now, long stunMs, long recoverUntil, long followStart) {
        long stunUntil = now + stunMs;
        return new long[] {
                stunUntil,
                Math.max(recoverUntil, stunUntil),
                Math.max(followStart, stunUntil)
        };
    }

    /** which kinds are active on this skill (post-parse fields). */
    public static boolean hasWall(EmberRunMaps.Skill sk) { return sk != null && sk.wallStun > 0; }
    public static boolean hasWhiff(EmberRunMaps.Skill sk) { return sk != null && sk.whiffStun > 0; }
    public static boolean hasBreak(EmberRunMaps.Skill sk) { return sk != null && sk.breakHp > 0; }

    /**
     * Bukkit-free warn-line suffixes (Director concatenates after the shape hint).
     * Empty string when that window is not active for this cast.
     */
    public static String wallHint(double wallStun, String fmtSeconds) {
        if (wallStun <= 0) return "";
        return " §a· 让它撞上墙会晕 " + fmtSeconds + " 秒";
    }

    public static String whiffHint(double whiffStun, boolean armed, String fmtSeconds) {
        if (whiffStun <= 0 || !armed) return "";
        return " §a· 全员躲开它会踉跄 " + fmtSeconds + " 秒";
    }

    public static String breakHint(double breakNeed, double breakStun, String fmtStun) {
        if (breakNeed <= 0) return "";
        return " §a· 蓄力期间全队打掉它 " + Math.round(breakNeed) + " 点血可打断（踉跄 " + fmtStun + " 秒）§7· 打不动就跑出圈";
    }

    /** log / chat kind label for unit tests */
    public static String kindOf(EmberRunMaps.Skill sk) {
        if (hasWall(sk)) return CounterplayKind.WALL.name();
        if (hasWhiff(sk)) return CounterplayKind.WHIFF.name();
        if (hasBreak(sk)) return CounterplayKind.BREAK.name();
        return "NONE";
    }
}
