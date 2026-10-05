package town.sunshine.corerpg;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * D198 / ARCH S0-1 + S0-2 (docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md §5.2, LEAK L1 / L2):
 * while the P1 mode ({@code EmberMode.active()}) is on, the pre-P1 dungeons are closed to players.
 *
 * <ul>
 *   <li><b>S0-1</b> — the DungeonPlus start conditions of the legacy dungeons read {@code %corerpg_gate_<id>%}
 *       (daily / weekly / abyss / raid / elite) and {@code %corerpg_guildboss_pass%}; they now answer {@code "no"}
 *       so {@code /dp start EmberDaily} etc. is refused for every party member, whoever starts it. OPs still pass
 *       through the existing {@code ||'%player_is_op%'=='yes'} clause in each option.yml. No P1 dungeon
 *       (EmberQ0*) reads these placeholders.</li>
 *   <li><b>S0-2</b> — {@code /corerpg enter <legacy kind>} and {@code /corerpg elite [start]} are refused for
 *       non-admin players. Console never reaches this path (cmdEnter / elite start are player-only), and admins
 *       ({@code corerpg.admin} or OP) always pass.</li>
 * </ul>
 * P1 off (legacy mode): both helpers return false, behaviour unchanged.
 */
public final class LegacyGate {

    /** Short refusal shown to players (S0-2). */
    public static final String CLOSED_MSG = "P1 模式下旧副本已关闭";

    /** Legacy DP gate ids read through {@code %corerpg_gate_<id>%} (progress.yml level_gates + elite). */
    public static final Set<String> LEGACY_GATE_IDS = Collections.unmodifiableSet(new LinkedHashSet<String>(
            Arrays.asList("daily", "weekly", "abyss", "raid", "elite")));

    private LegacyGate() {}

    /** S0-1: true → {@code %corerpg_gate_<gateId>%} must answer "no". */
    public static boolean gateClosed(boolean p1Active, String gateId) {
        return p1Active && gateId != null && LEGACY_GATE_IDS.contains(gateId.toLowerCase(Locale.ROOT));
    }

    /** S0-1: true → {@code %corerpg_guildboss_pass%} must answer "no" (legacy guild boss). */
    public static boolean guildBossPassClosed(boolean p1Active) {
        return p1Active;
    }

    /**
     * S0-2: true → refuse a {@code TicketEntryService} legacy entry.
     *
     * @param privileged OP or {@code corerpg.admin}
     */
    public static boolean refuseLegacyEnter(boolean p1Active, boolean kindIsP1, boolean privileged) {
        return p1Active && !kindIsP1 && !privileged;
    }
}
