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
    /** D2318: set-shape sync Slow on parry success (zero flat change). */
    public static final int PARRY_SYNC_SLOW_TICKS = 24;
    /** D2318: sustain micro-heal on parry when synced. */
    public static final double PARRY_SYNC_HEAL_PCT = 0.005 * 1.25;

    public static final String DISPLAY_PARRY = "余烬招架";

    /** D607 shape-fork S0: set soft-preferred 烬斩 shape (geometry only · zero skill_mult). */
    public static final String C_SHAPE_MANUAL = "p1_shape_manual"; // 1 = player picked; soft prefer skips
    /** D2278: entered Q04 at least once (shape unlock without clear-gate). */
    public static final String C_SHAPE_ENTER = "p1_shape_enter";
    /** D2290: entered Q02 → 烬突 without clear-gate. */
    public static final String C_DASH_ENTER = "p1_dash_enter";
    /** D2290: entered Q03 → 招架 without clear-gate. */
    public static final String C_PARRY_ENTER = "p1_parry_enter";
    /** D2290: entered Q05 → 身法变体 without clear-gate. */
    public static final String C_STEP_ENTER = "p1_step_enter";
    /** D2293: entered Q07 → 调律 without clear-gate. */
    public static final String C_ATTUNE_ENTER = "p1_attune_enter";
    /** D5217: entered Q02 → 烬炉烙印 without clear-gate. */
    public static final String C_IMPRINT_ENTER = "p1_imprint_enter";
    /** D5217: entered Q03 → 双签名 without clear-gate. */
    public static final String C_DUAL_ENTER = "p1_dual_enter";
    /** D5281: entered Q01 → 宝石路径 without clear-gate. */
    public static final String C_GEM_ENTER = "p1_gem_enter";
    /** D2330: entered Q06 → pledge/trial without clear-gate. */
    public static final String C_PLEDGE_ENTER = "p1_pledge_enter";
    /** D2378: entered Q01 → stride path without clear-gate. */
    public static final String C_STRIDE_ENTER = "p1_stride_enter";
    /** D2502: entered Q02 → room path without clear-gate. */
    public static final String C_ROOM_ENTER = "p1_room_enter";
    /** D2516: entered Q01 → press path without clear-gate. */
    public static final String C_PRESS_ENTER = "p1_press_enter";
    /** D2562: entered Q01 → cut path without clear-gate. */
    public static final String C_CUT_ENTER = "p1_cut_enter";
    /** D2594: entered Q01 → rally path without clear-gate. */
    public static final String C_RALLY_ENTER = "p1_rally_enter";
    /** D2610: entered Q01 → gate path without clear-gate. */
    public static final String C_GATE_ENTER = "p1_gate_enter";
    /** D2674: entered Q01 → call path without clear-gate. */
    public static final String C_CALL_ENTER = "p1_call_enter";
    /** D2685: entered Q01 → pack path without clear-gate. */
    public static final String C_PACK_ENTER = "p1_pack_enter";
    /** D2722: entered Q01 → link path without clear-gate. */
    public static final String C_LINK_ENTER = "p1_link_enter";
    /** D2737: entered Q01 → flee path without clear-gate. */
    public static final String C_FLEE_ENTER = "p1_flee_enter";
    /** D2770: entered Q01 → chest path without clear-gate. */
    public static final String C_CHEST_ENTER = "p1_chest_enter";
    /** D2785: entered Q01 → hunt path without clear-gate. */
    public static final String C_HUNT_ENTER = "p1_hunt_enter";
    /** D2818: entered Q01 → stash path without clear-gate. */
    public static final String C_STASH_ENTER = "p1_stash_enter";
    /** D2866: entered Q01 → extra path without clear-gate. */
    public static final String C_EXTRA_ENTER = "p1_extra_enter";
    /** D2914: entered Q01 → prep path without clear-gate. */
    public static final String C_PREP_ENTER = "p1_prep_enter";
    /** D2915: entered Q01 → bar path without clear-gate. */
    public static final String C_BAR_ENTER = "p1_bar_enter";
    /** D2925: entered Q02 → spice path without clear-gate. */
    public static final String C_SPICE_ENTER = "p1_spice_enter";
    /** D3041: entered Q02 → counter path without clear-gate. */
    public static final String C_COUNTER_ENTER = "p1_counter_enter";

    public static int preferredShape(String family) {
        if ("burst".equals(family)) return SHAPE_LINE;
        if ("sustain".equals(family)) return SHAPE_RING;
        if ("scorch".equals(family)) return SHAPE_FAN;
        return SHAPE_FAN;
    }

    public static String preferredShapeLabel(String family) {
        return shapeName(preferredShape(family));
    }


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
        public final double lineWidth; // D642: perpendicular half-width for line (blocks); 0 = unused
        public final int maxTargets;
        public final boolean sigOverride;
        public final String label;

        Shape(int id, double range, double arc, double line, int maxTargets, boolean sigOverride, String label) {
            this(id, range, arc, line, line > 0 ? 1.0 : 0.0, maxTargets, sigOverride, label);
        }

        Shape(int id, double range, double arc, double line, double lineWidth, int maxTargets, boolean sigOverride, String label) {
            this.id = id; this.range = range; this.arc = arc; this.line = line;
            this.lineWidth = lineWidth;
            this.maxTargets = maxTargets; this.sigOverride = sigOverride; this.label = label;
        }
    }

    private EmberSkillKit() {}

    public static boolean dashUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_DASH_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, UNLOCK_DASH);
    }

    public static boolean shapeUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_SHAPE_ENTER, "all") > 0) return true; // D2278 first-enter
        return runs != null && runs.firstClearedKey(d, UNLOCK_SHAPE);
    }

    /** Rising-edge: mark Q04 entered → shape unlocked without clear. @return true if newly set */
    public static boolean markShapeEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_SHAPE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_SHAPE_ENTER, "all", 1);
        return true;
    }

    public static boolean markDashEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_DASH_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_DASH_ENTER, "all", 1);
        return true;
    }

    public static boolean markParryEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_PARRY_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_PARRY_ENTER, "all", 1);
        return true;
    }

    public static boolean markStepEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_STEP_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_STEP_ENTER, "all", 1);
        return true;
    }

    public static boolean markAttuneEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_ATTUNE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_ATTUNE_ENTER, "all", 1);
        return true;
    }

    /** D5217: Q02 first-enter → imprint feature (no clear-gate). */
    public static boolean markImprintEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_IMPRINT_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_IMPRINT_ENTER, "all", 1);
        return true;
    }

    /** D5217: Q03 first-enter → dual signature (no clear-gate). */
    public static boolean markDualEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_DUAL_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_DUAL_ENTER, "all", 1);
        return true;
    }

    /** D5281: Q01 first-enter → gem path (no clear-gate). */
    public static boolean markGemEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_GEM_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_GEM_ENTER, "all", 1);
        return true;
    }



    public static boolean markPledgeEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_PLEDGE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_PLEDGE_ENTER, "all", 1);
        return true;
    }

    public static boolean markStrideEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_STRIDE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_STRIDE_ENTER, "all", 1);
        return true;
    }

    public static boolean markRoomEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_ROOM_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_ROOM_ENTER, "all", 1);
        return true;
    }

    /** D2502: Q02 clear OR first-enter unlocks room-order path. */
    public static boolean roomPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_ROOM_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q02");
    }

    public static boolean markPressEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_PRESS_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_PRESS_ENTER, "all", 1);
        return true;
    }

    /** D2516: Q01 clear OR first-enter unlocks press path. */
    public static boolean pressPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_PRESS_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markCutEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_CUT_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_CUT_ENTER, "all", 1);
        return true;
    }

    public static boolean cutPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_CUT_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markRallyEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_RALLY_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_RALLY_ENTER, "all", 1);
        return true;
    }

    public static boolean rallyPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_RALLY_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markGateEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_GATE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_GATE_ENTER, "all", 1);
        return true;
    }

    public static boolean gatePathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_GATE_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markCallEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_CALL_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_CALL_ENTER, "all", 1);
        return true;
    }

    public static boolean callPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_CALL_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markPackEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_PACK_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_PACK_ENTER, "all", 1);
        return true;
    }

    public static boolean packPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_PACK_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    /** TIP-FL D13395 · SightPath thin-page grey gate (首通 Q01; matches applyAndReply). */
    public static boolean sightPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        return runs != null && runs.progressFlag(d, "q01");
    }

    public static boolean markLinkEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_LINK_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_LINK_ENTER, "all", 1);
        return true;
    }

    public static boolean linkPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_LINK_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markFleeEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_FLEE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_FLEE_ENTER, "all", 1);
        return true;
    }

    public static boolean fleePathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_FLEE_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markChestEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_CHEST_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_CHEST_ENTER, "all", 1);
        return true;
    }

    public static boolean chestPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_CHEST_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markHuntEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_HUNT_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_HUNT_ENTER, "all", 1);
        return true;
    }

    public static boolean huntPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_HUNT_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markStashEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_STASH_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_STASH_ENTER, "all", 1);
        return true;
    }

    public static boolean stashPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_STASH_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markExtraEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_EXTRA_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_EXTRA_ENTER, "all", 1);
        return true;
    }

    public static boolean extraPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_EXTRA_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markPrepEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_PREP_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_PREP_ENTER, "all", 1);
        return true;
    }

    public static boolean prepPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_PREP_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markBarEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_BAR_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_BAR_ENTER, "all", 1);
        return true;
    }

    public static boolean barPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_BAR_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    public static boolean markSpiceEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_SPICE_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_SPICE_ENTER, "all", 1);
        return true;
    }

    /** D2925: Q02 clear OR first-enter unlocks spice (imprint OR enter). */
    public static boolean spicePathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_SPICE_ENTER, "all") > 0) return true;
        if (runs != null && runs.firstClearedKey(d, "q02")) return true;
        return runs != null && imprintFeatureUnlocked(d, runs);
    }

    public static boolean markCounterEnter(PlayerData d) {
        if (d == null) return false;
        if (d.periodCount(C_COUNTER_ENTER, "all") > 0) return false;
        d.addPeriodCount(C_COUNTER_ENTER, "all", 1);
        return true;
    }

    public static boolean counterPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_COUNTER_ENTER, "all") > 0) return true;
        if (runs != null && runs.firstClearedKey(d, "q02")) return true;
        return runs != null && imprintFeatureUnlocked(d, runs);
    }

    /** D2378: Q01 clear OR first-enter unlocks stride path (still needs Q05 step). */
    public static boolean strideMapUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_STRIDE_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    /** D2330: Q06 clear OR first-enter unlocks pledge/trial paths. */
    public static boolean pledgeUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_PLEDGE_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, EmberPledgeService.UNLOCK);
    }

    /** D2293: Q07 clear OR first-enter unlocks attune. */
    public static boolean attuneUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_ATTUNE_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, EmberSignature.ALT_UNLOCK);
    }

    /** D5217: Q02 clear OR first-enter unlocks 烬炉烙印. */
    public static boolean imprintFeatureUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_IMPRINT_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, EmberSignature.IMPRINT_UNLOCK);
    }

    /** D5217: Q03 clear OR first-enter unlocks dual signature. */
    public static boolean dualUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_DUAL_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, EmberSignature.DUAL_UNLOCK);
    }

    /** D5281: Q01 clear OR first-enter unlocks gem path. */
    public static boolean gemPathUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_GEM_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, "q01");
    }

    /**
     * D5281: directed forge T2 ← shape (Q04 enter/clear); T3 ← attune (Q07 enter/clear).
     * T1 always open. Zero mark prices.
     */
    public static boolean directedForgeOpen(PlayerData d, EmberRunService runs, int tier) {
        if (tier <= 1) return true;
        if (tier == 2) return shapeUnlocked(d, runs);
        if (tier == 3) return attuneUnlocked(d, runs);
        return false;
    }

    /** D5345: Q07 clear OR first-enter — abyss / season goals / late hub (challenge already uses attune). */
    public static boolean endgameUnlocked(PlayerData d, EmberRunService runs) {
        return attuneUnlocked(d, runs);
    }

    /**
     * D5409: soft access for raid/short (and similar) by requires key.
     * q01 → gem enter/clear · q07 → endgame · else first-clear only.
     * Does not change weekly/daily caps or settle tables.
     */
    public static boolean mapAccessUnlocked(PlayerData d, EmberRunService runs, String requires) {
        if (d == null || requires == null || requires.isEmpty()) return false;
        String r = requires.toLowerCase(java.util.Locale.ROOT);
        if ("q01".equals(r)) return gemPathUnlocked(d, runs);
        if ("q07".equals(r)) return endgameUnlocked(d, runs);
        return runs != null && runs.firstClearedKey(d, r);
    }

    /** D2292: step land slow ticks (synced with set-shape). */
    public static int stepLandSlowTicks(boolean setShapeSync) {
        return stepLandSlowTicks(setShapeSync, false);
    }

    /** D2474: +2 ticks when posture aligned. */
    public static int stepLandSlowTicks(boolean setShapeSync, boolean postureAligned) {
        return STEP_SLOW_TICKS + (setShapeSync ? 4 : 0) + (postureAligned ? 2 : 0);
    }

    /** D2292: chenghu land heal fraction. */
    public static double stepLandHealPct(boolean setShapeSync) {
        return stepLandHealPct(setShapeSync, false);
    }

    /** D2474: ×1.1 when posture aligned. */
    public static double stepLandHealPct(boolean setShapeSync, boolean postureAligned) {
        return stepLandHealPct(setShapeSync, postureAligned, null);
    }

    /** D3177: Q06 kit identity — step land heal ×1.08 (pledge endure; not Shape-native). */
    public static double stepLandHealPct(boolean setShapeSync, boolean postureAligned, String mapKey) {
        double v = setShapeSync ? 0.005 * 1.25 : 0.005;
        if (postureAligned) v *= 1.1;
        if (mapKey != null && "q06".equalsIgnoreCase(mapKey)) v *= 1.08;
        return v;
    }

    /** D2475: step travel distance; posture-aligned footwork stretch. */
    public static double stepTravelDistance(double forwardDist, boolean back, boolean postureAligned, int postureId) {
        return stepTravelDistance(forwardDist, back, postureAligned, postureId, null);
    }

    /** D3100: Q05 kit identity — step +0.35 in Q05 (zero skill_mult). */
    public static double stepTravelDistance(double forwardDist, boolean back, boolean postureAligned, int postureId, String mapKey) {
        return stepTravelDistance(forwardDist, back, postureAligned, postureId, mapKey, false);
    }

    /** D5153: set×shape sync + posture → +0.2 travel (zero skill_mult). */
    public static double stepTravelDistance(double forwardDist, boolean back, boolean postureAligned, int postureId,
                                            String mapKey, boolean setShapeSync) {
        double base = back ? BACKSTEP_DISTANCE : forwardDist;
        if (postureAligned && postureId > 0) {
            if (back && postureId == EmberPosturePath.GUARD) base += 0.5;
            else if (!back && (postureId == EmberPosturePath.STRIKE || postureId == EmberPosturePath.SWEEP))
                base += 0.5;
            if (setShapeSync) base += 0.2; // D5153 geom feel
        }
        if (mapKey != null && "q05".equalsIgnoreCase(mapKey)) base += 0.35;
        return base;
    }

    /** D2305: dash land slow (synced). */
    public static int dashLandSlowTicks(boolean setShapeSync) {
        return dashLandSlowTicks(setShapeSync, false);
    }

    /** D2461: +2 ticks when posture aligned. */
    public static int dashLandSlowTicks(boolean setShapeSync, boolean postureAligned) {
        return DASH_BURST_SLOW_TICKS + (setShapeSync ? 4 : 0) + (postureAligned ? 2 : 0);
    }

    /** D2305: dash land heal fraction. */
    public static double dashLandHealPct(boolean setShapeSync) {
        return dashLandHealPct(setShapeSync, false);
    }

    /** D2461: ×1.1 when posture aligned. */
    public static double dashLandHealPct(boolean setShapeSync, boolean postureAligned) {
        double v = setShapeSync ? DASH_HEAL_PCT * 1.25 : DASH_HEAL_PCT;
        return postureAligned ? v * 1.1 : v;
    }

    /** D2306: signature skill_shield duration secs (synced · zero skill_mult). */
    public static double sigShieldSecs(double baseSecs, boolean setShapeSync) {
        if (baseSecs <= 0) baseSecs = 5.0;
        return setShapeSync ? baseSecs * 1.25 : baseSecs;
    }

    /** D2342: dash path hit radius when set-shape synced. */
    public static double dashHitRadius(boolean setShapeSync) {
        return dashHitRadius(setShapeSync, false);
    }

    /** D2618: preferred variety room + set×shape → +0.15 dash radius. */
    public static double dashHitRadius(boolean setShapeSync, boolean roomShape) {
        return dashHitRadius(setShapeSync, roomShape, SHAPE_FAN);
    }

    /** D3009: RING surround dash +0.10 (shape-native; not Shape×Room twin). */
    public static double dashHitRadius(boolean setShapeSync, boolean roomShape, int shapeId) {
        return dashHitRadius(setShapeSync, roomShape, shapeId, null);
    }

    /** D3097: Q02 kit identity — dash radius +0.08 in Q02 (not Shape-native twin). */
    public static double dashHitRadius(boolean setShapeSync, boolean roomShape, int shapeId, String mapKey) {
        return dashHitRadius(setShapeSync, roomShape, shapeId, mapKey, false);
    }

    /** D5153: posture aligned → dash hit +0.12 (zero skill_mult). */
    public static double dashHitRadius(boolean setShapeSync, boolean roomShape, int shapeId, String mapKey,
                                       boolean postureAligned) {
        return dashHitRadius(setShapeSync, roomShape, shapeId, mapKey, postureAligned, false);
    }

    /** D6369: map resonance → dash hit +0.10 (zero skill_mult). */
    public static double dashHitRadius(boolean setShapeSync, boolean roomShape, int shapeId, String mapKey,
                                       boolean postureAligned, boolean mapResonance) {
        double r = setShapeSync ? DASH_HIT_RADIUS + 0.20 : DASH_HIT_RADIUS;
        if (setShapeSync && roomShape) r += 0.15;
        if (shapeId == SHAPE_RING) r += 0.10;
        if (mapKey != null && "q02".equalsIgnoreCase(mapKey)) r += 0.08;
        if (postureAligned) r += 0.12;
        if (mapResonance) r += 0.10;
        return r;
    }

    /** D5153: step ignite radius when posture aligned. */
    public static double stepIgniteRadius(boolean postureAligned) {
        return STEP_IGNITE_RADIUS + (postureAligned ? 0.35 : 0);
    }

    /** D2343: parry window ms when set-shape synced. */
    public static long parryWindowMs(boolean setShapeSync) {
        return parryWindowMs(setShapeSync, false);
    }

    /** D2490: GUARD posture aligned adds +50ms (stacks sync · zero flat). */
    public static long parryWindowMs(boolean setShapeSync, boolean guardPosture) {
        return parryWindowMs(setShapeSync, guardPosture, null);
    }

    /** D3098: Q03 kit identity — parry window +30ms in Q03 (zero skill_mult). */
    public static long parryWindowMs(boolean setShapeSync, boolean guardPosture, String mapKey) {
        return parryWindowMs(setShapeSync, guardPosture, mapKey, false);
    }

    /**
     * D5153: postureAligned (non-GUARD) +25ms; GUARD keeps +50 via guardPosture.
     * Zero skill_mult / flat damage.
     */
    public static long parryWindowMs(boolean setShapeSync, boolean guardPosture, String mapKey,
                                     boolean postureAligned) {
        long ms = setShapeSync ? PARRY_WINDOW_MS + 50L : PARRY_WINDOW_MS;
        if (guardPosture) ms += 50L;
        else if (postureAligned) ms += 25L;
        if (mapKey != null && "q03".equalsIgnoreCase(mapKey)) ms += 30L;
        return ms;
    }

    /** D2366: signature skill_charge wind-up secs (synced · faster cast · zero skill_mult). */
    public static double sigChargeSecs(double baseSecs, boolean setShapeSync) {
        return sigChargeSecs(baseSecs, setShapeSync, null);
    }

    /** D3178: Q07 kit identity — signature charge ×0.92 (attune feel; zero skill_mult). */
    public static double sigChargeSecs(double baseSecs, boolean setShapeSync, String mapKey) {
        if (baseSecs <= 0) return baseSecs;
        double v = setShapeSync ? baseSecs * 0.8 : baseSecs;
        if (mapKey != null && "q07".equalsIgnoreCase(mapKey)) v *= 0.92;
        return v;
    }

    /** D2367: signature skill_shield_max multiplier when synced. */
    public static double sigShieldMaxMult(boolean setShapeSync) {
        return setShapeSync ? 1.25 : 1.0;
    }

    /** D3217: charge-land Slow ticks (synced longer · zero skill_mult). */
    public static final int CHARGE_LAND_SLOW_TICKS = 16;
    public static int chargeLandSlowTicks(boolean setShapeSync) {
        return CHARGE_LAND_SLOW_TICKS + (setShapeSync ? 4 : 0);
    }

    /** D3241: shield-land Resist ticks (synced longer · not path assist Resist twin). */
    public static final int SHIELD_LAND_RESIST_TICKS = 10;
    public static int shieldLandResistTicks(boolean setShapeSync) {
        return SHIELD_LAND_RESIST_TICKS + (setShapeSync ? 6 : 0);
    }

    /** D2380: dodge_heal ICD secs when synced. */
    public static double sigDodgeIcdSecs(double baseSecs, boolean setShapeSync) {
        if (baseSecs <= 0) return baseSecs;
        return setShapeSync ? baseSecs * 0.85 : baseSecs;
    }

    /** D2379: burn_spread ICD secs when synced. */
    public static double sigSpreadIcdSecs(double baseSecs, boolean setShapeSync) {
        if (baseSecs <= 0) return baseSecs;
        return setShapeSync ? baseSecs * 0.85 : baseSecs;
    }

    /** D2379: burn_spread search radius. */
    public static double sigBurnSpreadRadius(boolean setShapeSync) {
        return setShapeSync ? 4.5 : 4.0;
    }

    /** D2381: skill_ignite_n when synced (+1 target · zero skill_mult). */
    public static int sigIgniteN(int baseN, boolean setShapeSync) {
        if (baseN <= 0) return baseN;
        return setShapeSync ? baseN + 1 : baseN;
    }

    /** D2382: sustain_low HP fraction when synced. */
    public static double sigSustainLow(double baseFrac, boolean setShapeSync) {
        if (baseFrac <= 0) return baseFrac;
        return setShapeSync ? Math.min(0.55, baseFrac + 0.05) : baseFrac;
    }

    /** D2394: potion mult when signature already ≠1 and synced. */
    public static double sigPotionMult(double base, boolean setShapeSync) {
        if (base == 1.0) return base;
        return setShapeSync ? base * 1.05 : base;
    }

    /** D2395: sustain_every delta when signature already shortens and synced. */
    public static int sigSustainEvery(int baseDelta, boolean setShapeSync) {
        if (!setShapeSync || baseDelta >= 0) return baseDelta;
        return Math.max(-2, baseDelta - 1);
    }

    /** D2406: extra burn ticks when signature already adds ticks and synced. */
    public static int sigBurnTicksExtra(int baseExtra, boolean setShapeSync) {
        if (!setShapeSync || baseExtra <= 0) return baseExtra;
        return baseExtra + 1;
    }

    /** D2407: hit/dodge burst prime count when signature already primes and synced. */
    public static int sigBurstPrime(int baseN, boolean setShapeSync) {
        if (!setShapeSync || baseN <= 0) return baseN;
        return baseN + 1;
    }

    public static boolean stepVariantUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_STEP_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, UNLOCK_STEP);
    }

    /** D301: 余烬招架 after first-clear Q03. */
    public static boolean parryUnlocked(PlayerData d, EmberRunService runs) {
        if (d == null) return false;
        if (d.periodCount(C_PARRY_ENTER, "all") > 0) return true;
        return runs != null && runs.firstClearedKey(d, UNLOCK_PARRY);
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

    /** D607: soft-set preferred shape once while unlocked and player has not manually picked. */
    public static boolean maybeSoftPreferShape(PlayerData d, EmberRunService runs, String family) {
        if (d == null || !shapeUnlocked(d, runs)) return false;
        if (d.periodCount(C_SHAPE_MANUAL, "all") > 0) return false;
        if (family == null || "none".equals(family)) return false;
        int want = preferredShape(family);
        if (shapeId(d, runs) == want) return false;
        boolean changed = setShape(d, want);
        if (changed) EmberShapePath.mirrorSoftPrefer(d, want); // D645 path glance matches geometry
        return changed;
    }

    public static void markShapeManual(PlayerData d) {
        if (d == null) return;
        if (d.periodCount(C_SHAPE_MANUAL, "all") <= 0) d.addPeriodCount(C_SHAPE_MANUAL, "all", 1);
    }

    /**
     * Resolve the effective 烬斩 shape: signature line/ring/charge/plus overrides the rune (DESIGN §3).
     * Defaults match ember-v1.yml skill.* when mode is null.
     */
    public static Shape resolve(Player player, PlayerData d, EmberRunService runs, EmberMode mode, EmberGrowth.Mods gmods) {
        String activeSet = null;
        if (player != null) {
            try {
                org.bukkit.plugin.Plugin raw = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (raw instanceof town.sunshine.corerpg.CoreRpgPlugin) {
                    EmberLoadoutService ls = ((town.sunshine.corerpg.CoreRpgPlugin) raw).getEmberLoadouts();
                    EmberLoadout lo = ls == null ? null : ls.get(player);
                    if (lo != null) activeSet = lo.activeSet;
                }
            } catch (Throwable ignored) { }
        }
        return resolve(player, d, runs, mode, gmods, activeSet);
    }

    /**
     * D2252/D2418: when 2pc preferred shape matches rune (or signature implied shape), widen geometry (zero skill_mult).
     */
    public static Shape resolve(Player player, PlayerData d, EmberRunService runs, EmberMode mode,
                                EmberGrowth.Mods gmods, String activeSetFamily) {
        double baseRange = mode == null ? 3.5 : mode.d("skill.radius", 3.5);
        double baseArc = mode == null ? 100.0 : mode.d("skill.arc_degrees", 100.0);
        int baseMax = Math.max(1, mode == null ? 5 : mode.i("skill.max_targets", 5));
        EmberGrowth.Mods m = gmods == null ? EmberGrowth.Mods.NONE : gmods;
        boolean roomShape = player != null && runs != null && runs.inPreferredVarietyRoom(player);
        String mapKey = player != null && runs != null ? runs.activeMapKey(player) : null;
        boolean variant = m.get("skill_var") > 0;
        double sigLine = variant ? m.get("skill_line") : 0;
        double sigRing = variant ? m.get("skill_ring") : 0;
        boolean sigPlus = variant && m.get("skill_plus") > 0;
        boolean sigCharge = variant && m.get("skill_charge") > 0;
        boolean sigShape = sigLine > 0 || sigRing > 0 || sigPlus || sigCharge;
        if (sigShape) {
            double range = baseRange, arc = baseArc, line = 0;
            double lineW = 1.0;
            int max = baseMax;
            if (sigPlus) arc = 360.0;
            if (sigRing > 0) { arc = 360.0; range = sigRing; }
            if (m.get("skill_cap") > 0) max = Math.max(1, Math.min(max, (int) Math.round(m.get("skill_cap"))));
            if (sigLine > 0) { line = sigLine; range = Math.max(range, sigLine); }
            // D2418: leftover hole — signature override also eats set×shape geometry (zero skill_mult)
            int implied = sigLine > 0 ? SHAPE_LINE : SHAPE_RING;
            boolean sync = setShapeSynced(activeSetFamily, implied);
            if (sync) {
                if (sigLine > 0) { lineW = 1.2; line = line + 0.5; max = Math.min(max + 1, 5); }
                else { range = Math.min(range + 0.4, 4.5); max = Math.min(max + 1, 5); }
            }
            if (sync && roomShape) {
                if (sigLine > 0) { line = line + 0.25; }
                else { range = Math.min(range + 0.25, 4.7); }
            }
            boolean posture = EmberPosturePath.aligned(d, runs); // D5089
            boolean reso = EmberMapResonance.active(player, runs, mapKey); // D6305
            if (posture) {
                if (sigLine > 0) { line = line + 0.2; lineW = Math.min(lineW + 0.08, 1.35); }
                else { range = Math.min(range + 0.15, 4.85); }
            }
            if (reso) {
                if (sigLine > 0) { line = line + 0.15; lineW = Math.min(lineW + 0.05, 1.4); }
                else { range = Math.min(range + 0.12, 4.95); }
            }
            String label = sigLine > 0 ? "签名·直线" : (sigRing > 0 || sigPlus || sigCharge ? "签名·环斩" : "签名");
            if (sync) label = label + "·形同";
            if (sync && roomShape) label = label + "·优房";
            if (posture) label = label + "·姿齐";
            if (reso) label = label + "·图源";
            return new Shape(shapeId(d, runs), range, arc, line, lineW, max, true, label);
        }
        int id = shapeId(d, runs);
        boolean posture = EmberPosturePath.aligned(d, runs); // D5089
        boolean reso = EmberMapResonance.active(player, runs, mapKey); // D6305
        boolean dayBoost = false;
        try { dayBoost = EmberMapResonanceDayBoost.boost(d, reso); } catch (Throwable ignored) { } // D11857
        return geometryFor(id, baseRange, baseArc, baseMax, setShapeSynced(activeSetFamily, id), roomShape, mapKey, posture, reso, dayBoost);
    }

    /** Pure: 2pc family prefers this rune shape. */
    public static boolean setShapeSynced(String activeSetFamily, int shapeId) {
        if (activeSetFamily == null || activeSetFamily.isEmpty() || "none".equals(activeSetFamily)) return false;
        if (!EmberRunRules.validFamily(activeSetFamily)) return false;
        return preferredShape(activeSetFamily) == shapeId;
    }

    /**
     * D642 + D2252 geometry for a known rune id (tests unlock-bypass).
     * Zero skill_mult.
     */

    /** D2266: burst land slow ticks (base or +4 when set-shape synced). */
    public static int slashLandSlowTicks(boolean setShapeSync) {
        return slashLandSlowTicks(setShapeSync, false, false);
    }

    public static int slashLandSlowTicks(boolean setShapeSync, boolean postureAligned) {
        return slashLandSlowTicks(setShapeSync, postureAligned, false);
    }

    /** D2643: +2t when preferred-room × set×shape (stacks; not posture twin). */
    public static int slashLandSlowTicks(boolean setShapeSync, boolean postureAligned, boolean roomShape) {
        return slashLandSlowTicks(setShapeSync, postureAligned, roomShape, SHAPE_FAN);
    }

    /** D2977: LINE pierce linger +2t (shape-native; not Shape×Room twin). */
    public static int slashLandSlowTicks(boolean setShapeSync, boolean postureAligned, boolean roomShape, int shapeId) {
        int t = SLASH_BURST_SLOW_TICKS + (setShapeSync ? 4 : 0) + (postureAligned ? 2 : 0)
                + (setShapeSync && roomShape ? 2 : 0);
        if (shapeId == SHAPE_LINE) t += 2;
        return t;
    }

    /** D2266: sustain land heal pct (×1.25 when synced). */
    public static double slashLandHealPct(boolean setShapeSync) {
        return slashLandHealPct(setShapeSync, false, false);
    }

    public static double slashLandHealPct(boolean setShapeSync, boolean postureAligned) {
        return slashLandHealPct(setShapeSync, postureAligned, false);
    }

    /** D2658: ×1.05 when preferred-room × set×shape (on top of sync/posture). */
    public static double slashLandHealPct(boolean setShapeSync, boolean postureAligned, boolean roomShape) {
        return slashLandHealPct(setShapeSync, postureAligned, roomShape, SHAPE_FAN);
    }

    /** D2978: RING surround sustain ×1.04 (shape-native; not Shape×Room twin). */
    public static double slashLandHealPct(boolean setShapeSync, boolean postureAligned, boolean roomShape, int shapeId) {
        double v = setShapeSync ? SLASH_HEAL_PCT * 1.25 : SLASH_HEAL_PCT;
        if (postureAligned) v *= 1.1;
        if (setShapeSync && roomShape) v *= 1.05;
        if (shapeId == SHAPE_RING) v *= 1.04;
        return v;
    }
    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync) {
        return geometryFor(id, baseRange, baseArc, baseMax, setSync, false);
    }

    /** D2619: room×shape thin geometry (zero skill_mult / no ICD). */
    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync, boolean roomShape) {
        return geometryFor(id, baseRange, baseArc, baseMax, setSync, roomShape, null);
    }

    /** D3122: Q04 kit identity — thin geometry in Q04 (not Shape-native land / Shape×Room). */
    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync, boolean roomShape, String mapKey) {
        return geometryFor(id, baseRange, baseArc, baseMax, setSync, roomShape, mapKey, false, false, false);
    }

    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync,
                                    boolean roomShape, String mapKey, boolean postureAligned) {
        return geometryFor(id, baseRange, baseArc, baseMax, setSync, roomShape, mapKey, postureAligned, false, false);
    }

    /**
     * D5089 posture · D6305 mapResonance — shape widen (zero skill_mult / CD / dmg mult).
     */
    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync,
                                    boolean roomShape, String mapKey, boolean postureAligned, boolean mapResonance) {
        return geometryFor(id, baseRange, baseArc, baseMax, setSync, roomShape, mapKey, postureAligned, mapResonance, false);
    }

    /** D11857: mapResonance day boost — thin extra widen (zero skill_mult). */
    public static Shape geometryFor(int id, double baseRange, double baseArc, int baseMax, boolean setSync,
                                    boolean roomShape, String mapKey, boolean postureAligned, boolean mapResonance,
                                    boolean dayBoost) {
        boolean bonus = setSync && roomShape;
        boolean q04 = mapKey != null && "q04".equalsIgnoreCase(mapKey);
        boolean q01 = mapKey != null && "q01".equalsIgnoreCase(mapKey);
        boolean day = mapResonance && dayBoost;
        if (id == SHAPE_LINE) {
            double lw = setSync ? 0.95 : 0.7;
            int max = setSync ? 4 : 3;
            double range = Math.max(baseRange, 6.0) + (bonus ? 0.35 : 0) + (q04 ? 0.2 : 0) + (q01 ? 0.15 : 0);
            if (postureAligned) { range += 0.25; lw = Math.min(lw + 0.08, 1.15); }
            if (mapResonance) { range += 0.2; lw = Math.min(lw + 0.05, 1.2); }
            if (day) { range += EmberMapResonanceDayBoost.EXTRA_LINE; lw = Math.min(lw + EmberMapResonanceDayBoost.EXTRA_LINE_W, 1.25); }
            return new Shape(id, range, baseArc, 6.0, lw, max, false, shapeName(id));
        }
        if (id == SHAPE_RING) {
            double range = (setSync ? 3.9 : 3.5) + (bonus ? 0.25 : 0) + (q04 ? 0.15 : 0);
            int max = setSync ? 5 : 4;
            if (postureAligned) range = Math.min(range + 0.2, 4.4);
            if (mapResonance) range = Math.min(range + 0.15, 4.5);
            if (day) range = Math.min(range + EmberMapResonanceDayBoost.EXTRA_RANGE, 4.6);
            return new Shape(id, range, 360.0, 0, 0.0, max, false, shapeName(id));
        }
        double arc = Math.max(baseArc, 110.0);
        if (setSync) arc += 12.0;
        if (bonus) arc += 8.0;
        if (q04) arc += 4.0;
        if (postureAligned) arc += 8.0;
        if (mapResonance) arc += 6.0;
        if (day) arc += 3.0;
        int fanMax = baseMax + (q01 ? 1 : 0); // D3153 Q01 clear trash
        return new Shape(SHAPE_FAN, baseRange, arc, 0, 0.0, fanMax, false, shapeName(SHAPE_FAN));
    }

    /** True when the player is inside a P1 dungeon instance (shape swap forbidden — X1). Hub / AFK are OK. */
    public static boolean inDungeon(Player p, EmberRunService runs) {
        return p != null && runs != null && runs.isRunWorld(p.getWorld());
    }
}
