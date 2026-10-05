package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

import org.bukkit.entity.Player;

/**
 * D211 / skill-kit S1 (DESIGN-ember-skill-kit-2026-10-06.md §3): 烬斩 shape runes + unlock gates for 烬突.
 * Numbers are the S0-passed values (DS15 / L07 / L09); no permanent damage multipliers.
 * <p>Shape preference lives in {@code p1_slash_shape@all} (0 = fan, 1 = line, 2 = ring). Signature shape mods
 * ({@code skill_line}/{@code skill_ring}/{@code skill_plus}/{@code skill_charge}) override the chosen rune.
 */
public final class EmberSkillKit {
    public static final String C_SHAPE = "p1_slash_shape";
    public static final String UNLOCK_DASH = "q02";
    public static final String UNLOCK_SHAPE = "q04";

    /** S0-passed 烬突 (DS15): 4 blocks, ≤3 targets, 1.5B each, bosses ×0.5. */
    public static final double DASH_DISTANCE = 4.0;
    public static final int DASH_MAX_TARGETS = 3;
    public static final double DASH_BOSS_MULT = 0.5;
    /** Hit radius around the dash path (blocks). */
    public static final double DASH_HIT_RADIUS = 1.25;

    public static final int SHAPE_FAN = 0;
    public static final int SHAPE_LINE = 1;
    public static final int SHAPE_RING = 2;

    public static final class Shape {
        public final int id;           // fan / line / ring (requested; may differ from effective when sig overrides)
        public final double range;
        public final double arc;
        public final double line;      // >0 → narrow line (L07)
        public final int maxTargets;
        public final boolean sigOverride;
        public final String label;

        Shape(int id, double range, double arc, double line, int maxTargets, boolean sigOverride, String label) {
            this.id = id; this.range = range; this.arc = arc; this.line = line;
            this.maxTargets = maxTargets; this.sigOverride = sigOverride; this.label = label;
        }
    }

    private EmberSkillKit() {}

    public static boolean dashUnlocked(PlayerData d, EmberRunService runs) {
        return d != null && runs != null && runs.firstClearedKey(d, UNLOCK_DASH);
    }

    public static boolean shapeUnlocked(PlayerData d, EmberRunService runs) {
        return d != null && runs != null && runs.firstClearedKey(d, UNLOCK_SHAPE);
    }

    /** 0/1/2; absent or unknown → fan. Locked players always read as fan. */
    public static int shapeId(PlayerData d, EmberRunService runs) {
        if (!shapeUnlocked(d, runs)) return SHAPE_FAN;
        int v = d.periodCount(C_SHAPE, "all");
        if (v == SHAPE_LINE || v == SHAPE_RING) return v;
        return SHAPE_FAN;
    }

    public static String shapeName(int id) {
        if (id == SHAPE_LINE) return "直线穿刺";
        if (id == SHAPE_RING) return "原地环斩";
        return "扇形";
    }

    public static String shapeKey(int id) {
        if (id == SHAPE_LINE) return "line";
        if (id == SHAPE_RING) return "ring";
        return "fan";
    }

    public static int parseShape(String raw) {
        if (raw == null) return -1;
        String s = raw.trim().toLowerCase(java.util.Locale.ROOT);
        if ("fan".equals(s) || "扇形".equals(s) || "0".equals(s)) return SHAPE_FAN;
        if ("line".equals(s) || "直线".equals(s) || "穿刺".equals(s) || "1".equals(s)) return SHAPE_LINE;
        if ("ring".equals(s) || "环".equals(s) || "环斩".equals(s) || "2".equals(s)) return SHAPE_RING;
        return -1;
    }

    /**
     * Set the rune. Caller must have already checked unlock + safe zone. Puts {@code p1_slash_shape@all} to the id
     * (fan clears the key). Returns true when the stored value changed.
     */
    public static boolean setShape(PlayerData d, int id) {
        if (d == null) return false;
        if (id != SHAPE_FAN && id != SHAPE_LINE && id != SHAPE_RING) return false;
        int cur = d.periodCount(C_SHAPE, "all");
        if (cur == id || (id == SHAPE_FAN && cur == 0)) {
            if (id == SHAPE_FAN && cur != 0) d.addPeriodCount(C_SHAPE, "all", -cur);
            return false;
        }
        d.addPeriodCount(C_SHAPE, "all", id - cur);
        return true;
    }

    /**
     * Resolve the effective 烬斩 shape: signature line/ring/charge/plus overrides the rune (DESIGN §3).
     * Defaults match ember-v1.yml skill.* when mode is null.
     */
    public static Shape resolve(Player player, PlayerData d, EmberRunService runs, EmberMode mode, EmberGrowth.Mods gmods) {
        double baseRange = mode == null ? 3.5 : mode.d("skill.radius", 3.5);
        double baseArc = mode == null ? 100.0 : mode.d("skill.arc_degrees", 100.0);
        int baseMax = Math.max(1, mode == null ? 5 : mode.i("skill.max_targets", 5));
        EmberGrowth.Mods m = gmods == null ? EmberGrowth.Mods.NONE : gmods;
        boolean variant = m.get("skill_var") > 0;
        double sigLine = variant ? m.get("skill_line") : 0;
        double sigRing = variant ? m.get("skill_ring") : 0;
        boolean sigPlus = variant && m.get("skill_plus") > 0;
        boolean sigCharge = variant && m.get("skill_charge") > 0;
        boolean sigShape = sigLine > 0 || sigRing > 0 || sigPlus || sigCharge;
        if (sigShape) {
            double range = baseRange, arc = baseArc, line = 0;
            int max = baseMax;
            if (sigPlus) arc = 360.0;
            if (sigRing > 0) { arc = 360.0; range = sigRing; }
            if (m.get("skill_cap") > 0) max = Math.max(1, Math.min(max, (int) Math.round(m.get("skill_cap"))));
            if (sigLine > 0) { line = sigLine; range = Math.max(range, sigLine); }
            String label = sigLine > 0 ? "签名·直线" : (sigRing > 0 || sigPlus || sigCharge ? "签名·环斩" : "签名");
            return new Shape(shapeId(d, runs), range, arc, line, max, true, label);
        }
        int id = shapeId(d, runs);
        if (id == SHAPE_LINE) {
            // = L07
            return new Shape(id, Math.max(baseRange, 5.0), baseArc, 5.0, 3, false, shapeName(id));
        }
        if (id == SHAPE_RING) {
            // = L09
            return new Shape(id, 3.0, 360.0, 0, 3, false, shapeName(id));
        }
        return new Shape(SHAPE_FAN, baseRange, baseArc, 0, baseMax, false, shapeName(SHAPE_FAN));
    }

    /** True when the player is inside a P1 dungeon instance (shape swap forbidden — X1). Hub / AFK are OK. */
    public static boolean inDungeon(Player p, EmberRunService runs) {
        return p != null && runs != null && runs.isRunWorld(p.getWorld());
    }
}
