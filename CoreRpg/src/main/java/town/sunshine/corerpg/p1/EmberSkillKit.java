package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

import org.bukkit.entity.Player;

/**
 * D211 / skill-kit S1 (DESIGN-ember-skill-kit-2026-10-06.md §3): 烬斩 shape runes + unlock gates for 烬突.
 * D214 / skill-kit S2: 身法·火痕步 (焚烬 2pc + Q05) — replaces 踏步 with landing ignite ×1 at set burn rate.
 * Numbers are the S0-passed values (DS15 / L07 / L09 / F14c0n1); no permanent damage multipliers.
 * <p>Shape preference lives in {@code p1_slash_shape@all} (0 = fan, 1 = line, 2 = ring). Signature shape mods
 * ({@code skill_line}/{@code skill_ring}/{@code skill_plus}/{@code skill_charge}) override the chosen rune.
 * <p>D219 / RESEARCH-ember-backstep: 身法方向 preference in {@code p1_step_dir@all} (0 = 前冲, 1 = 后撤).
 * Available from Q01 for all sets; not a set-bound variant. Shares the 14s 踏步/火痕步 CD.
 * <p>D301 / DESIGN-ember-guard-skill-pivot: 守招·余烬招架 (G2_parry_B2) — Q03 unlock, bare Q, independent CD,
 * flat ×0.45B on successful parry of a boss tele hit. No shared dash tax / slash charge / uniform red.
 * <p>D434 / DESIGN-ember-set-step-symmetry: 爆闪步(烬爆2pc) / 承护步(承烬2pc) — Q05 + set; share 14s CD;
 * slow I 1.5s / resist I 2s at land (or takeoff if back). ≠守招; no crit/lifesteal/set count.
 */
public final class EmberSkillKit {
    public static final String C_SHAPE = "p1_slash_shape";
    /** D219 身法方向: 0 = forward (前冲), 1 = back (后撤). */
    public static final String C_STEP_DIR = "p1_step_dir";
    public static final String UNLOCK_DASH = "q02";
    public static final String UNLOCK_SHAPE = "q04";
    /** S2 火痕步 unlock (焚烬 auto-replace). Direction switch is Q01 / always. */
    public static final String UNLOCK_STEP = "q05";
    /** D301 守招·余烬招架 unlock (T0b G2_parry_B2 reachable from Q03). */
    public static final String UNLOCK_PARRY = "q03";

    /** S0-passed 烬突 (DS15): 4 blocks, ≤3 targets, 1.5B each, bosses ×0.5. */
    public static final double DASH_DISTANCE = 4.0;
    public static final int DASH_MAX_TARGETS = 3;
    public static final double DASH_BOSS_MULT = 0.5;
    /** Hit radius around the dash path (blocks). */
    public static final double DASH_HIT_RADIUS = 1.25;
    /** D436 S0 ✅ 烬突套装身份 */
    public static final double DASH_IGNITE_SCALE = 0.12;
    public static final int DASH_BURST_SLOW_TICKS = 30; // 1.5s Slow I
    public static final double DASH_HEAL_PCT = 0.005;
    /** D438 S0 ✅/🟡 烬斩套装落点（焚点燃空廊→仅展示） */
    public static final int SLASH_BURST_SLOW_TICKS = 20; // 1.0s Slow I
    public static final double SLASH_HEAL_PCT = 0.002;

    /** S0-passed 火痕步 (F14c0n1): replace 踏步, ignite 1 at landing, burn mult = set coef ×1.0. */
    public static final String HUOHEN_FAMILY = "scorch";
    /** D434 爆闪步 family (烬爆 2pc). */
    public static final String BAOSHAN_FAMILY = "burst";
    /** D434 承护步 family (承烬 2pc). */
    public static final String CHENGHU_FAMILY = "sustain";
    public static final int STEP_IGNITE_N = 1;
    public static final double STEP_BURN_MULT = 1.0;
    /** Search radius around landing/takeoff for the single ignite target (blocks). */
    public static final double STEP_IGNITE_RADIUS = 3.0;
    /** D434 爆闪步: Slow I duration ticks (1.5s). */
    public static final int STEP_SLOW_TICKS = 30;
    /** D434 承护步: Damage Resistance I ticks (2s). */
    public static final int STEP_RESIST_TICKS = 40; // D434 S0 ❌ — unused live
    /** D435 S0 🟡 C_heal005 */
    public static final double STEP_HEAL_PCT = 0.005;
    public static final int STEP_VARIANT_PLAIN = 0;
    public static final int STEP_VARIANT_HUOHEN = 1;
    public static final int STEP_VARIANT_BAOSHAN = 2;
    public static final int STEP_VARIANT_CHENGHU = 3;
    /** D219 后撤步 distance (forward 踏步 stays skills.yml 5.0). */
    public static final double BACKSTEP_DISTANCE = 4.0;

    /** D301 T0b G2_parry_B2: independent CD seconds. */
    public static final int PARRY_CD_SECONDS = 28;
    public static final long PARRY_CD_MS = PARRY_CD_SECONDS * 1000L;
    /** Parry window after boss tele land (ms); mid of design 0.35–0.55s. */
    public static final long PARRY_WINDOW_MS = 450L;
    /** Flat retaliate multiplier × blade B on successful parry (tele still lands). */
    public static final double PARRY_FLAT_MULT = 0.45;
    public static final String DISPLAY_PARRY = "余烬招架";

    public static final int DIR_FORWARD = 0;
    public static final int DIR_BACK = 1;

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

    public static boolean stepVariantUnlocked(PlayerData d, EmberRunService runs) {
        return d != null && runs != null && runs.firstClearedKey(d, UNLOCK_STEP);
    }

    /** D301: 余烬招架 after first-clear Q03. */
    public static boolean parryUnlocked(PlayerData d, EmberRunService runs) {
        return d != null && runs != null && runs.firstClearedKey(d, UNLOCK_PARRY);
    }

    /** Flat retaliate amount for a successful parry (0 if B ≤ 0). */
    public static double parryFlat(double bladeB) {
        return PARRY_FLAT_MULT * Math.max(0.0, bladeB);
    }

    /** True while {@code nowMs} is still inside an opened parry window ending at {@code windowUntilMs}. */
    public static boolean parryWindowOpen(long nowMs, long windowUntilMs) {
        return windowUntilMs > 0 && nowMs <= windowUntilMs;
    }

    /**
     * 火痕步 is active when Q05 is first-cleared and the equipped pair forms the 焚烬 (scorch) two-piece set.
     * Otherwise sneak+Q stays plain 踏步. No stored preference (auto-replaces).
     */
    public static boolean huohenActive(PlayerData d, EmberRunService runs, String activeSetFamily) {
        return stepVariantUnlocked(d, runs) && HUOHEN_FAMILY.equals(activeSetFamily);
    }

    /** D434: 爆闪步 when Q05 + 烬爆 two-piece. */
    public static boolean baoshanActive(PlayerData d, EmberRunService runs, String activeSetFamily) {
        return stepVariantUnlocked(d, runs) && BAOSHAN_FAMILY.equals(activeSetFamily);
    }

    /** D434: 承护步 when Q05 + 承烬 two-piece. */
    public static boolean chenghuActive(PlayerData d, EmberRunService runs, String activeSetFamily) {
        return stepVariantUnlocked(d, runs) && CHENGHU_FAMILY.equals(activeSetFamily);
    }

    /**
     * D434: which set step replaces plain 踏步 (mutually exclusive by activeSet).
     * {@link #STEP_VARIANT_PLAIN}=0 · HUOHEN=1 · BAOSHAN=2 · CHENGHU=3.
     */
    public static int stepSetVariant(PlayerData d, EmberRunService runs, String activeSetFamily) {
        if (!stepVariantUnlocked(d, runs) || activeSetFamily == null) return STEP_VARIANT_PLAIN;
        if (HUOHEN_FAMILY.equals(activeSetFamily)) return STEP_VARIANT_HUOHEN;
        if (BAOSHAN_FAMILY.equals(activeSetFamily)) return STEP_VARIANT_BAOSHAN;
        if (CHENGHU_FAMILY.equals(activeSetFamily)) return STEP_VARIANT_CHENGHU;
        return STEP_VARIANT_PLAIN;
    }

    public static String stepDisplayName(boolean huohen) {
        return stepDisplayName(huohen ? STEP_VARIANT_HUOHEN : STEP_VARIANT_PLAIN, false);
    }

    /** Compat: huohen boolean → variant 0/1. */
    public static String stepDisplayName(boolean huohen, boolean backward) {
        return stepDisplayName(huohen ? STEP_VARIANT_HUOHEN : STEP_VARIANT_PLAIN, backward);
    }

    /** 踏步 / 后撤 / 火痕 / 爆闪 / 承护 (+后撤变体). */
    public static String stepDisplayName(int variant, boolean backward) {
        if (variant == STEP_VARIANT_HUOHEN) return backward ? "火痕·后撤" : "火痕步";
        if (variant == STEP_VARIANT_BAOSHAN) return backward ? "爆闪·后撤" : "爆闪步";
        if (variant == STEP_VARIANT_CHENGHU) return backward ? "承护·后撤" : "承护步";
        return backward ? "后撤步" : "踏步";
    }

    /** 0 = forward, 1 = back; absent → forward. Always available (Q01). */
    public static int stepDirId(PlayerData d) {
        if (d == null) return DIR_FORWARD;
        int v = d.periodCount(C_STEP_DIR, "all");
        return v == DIR_BACK ? DIR_BACK : DIR_FORWARD;
    }

    public static boolean stepBackward(PlayerData d) {
        return stepDirId(d) == DIR_BACK;
    }

    public static String stepDirName(int id) {
        return id == DIR_BACK ? "后撤" : "前冲";
    }

    public static String stepDirKey(int id) {
        return id == DIR_BACK ? "back" : "forward";
    }

    public static int parseStepDir(String raw) {
        if (raw == null) return -1;
        String s = raw.trim().toLowerCase(java.util.Locale.ROOT);
        if ("forward".equals(s) || "front".equals(s) || "前冲".equals(s) || "前".equals(s) || "0".equals(s)) return DIR_FORWARD;
        if ("back".equals(s) || "backward".equals(s) || "backstep".equals(s)
                || "后撤".equals(s) || "后".equals(s) || "后撤步".equals(s) || "1".equals(s)) return DIR_BACK;
        return -1;
    }

    /**
     * Persist 身法方向. Caller checks safe zone. Puts {@code p1_step_dir@all} (forward clears the key).
     * Returns true when the stored value changed.
     */
    public static boolean setStepDir(PlayerData d, int id) {
        if (d == null) return false;
        if (id != DIR_FORWARD && id != DIR_BACK) return false;
        int cur = d.periodCount(C_STEP_DIR, "all");
        if (cur == id || (id == DIR_FORWARD && cur == 0)) {
            if (id == DIR_FORWARD && cur != 0) d.addPeriodCount(C_STEP_DIR, "all", -cur);
            return false;
        }
        d.addPeriodCount(C_STEP_DIR, "all", id - cur);
        return true;
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
