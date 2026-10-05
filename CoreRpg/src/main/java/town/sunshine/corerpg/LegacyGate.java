package town.sunshine.corerpg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
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
 *
 * <p><b>S0-3</b> (D199) — route-level deny-by-default for {@code /corerpg}: while P1 is on, a non-admin
 * <em>player</em> may only run the subcommands in {@code ember-v1.yml legacy_gate.allow} (falls back to
 * {@link #DEFAULT_ALLOW} when the section is missing). Each entry maps a canonical subcommand to either
 * {@code "*"} (any action) or the list of allowed first actions ({@code ""} = no action). Aliases are folded by
 * {@link #canonical(String)}, so the list only names canonical subcommands. Console / OP / {@code corerpg.admin}
 * always pass (DP and MM reward scripts call {@code /corerpg} as console).</p>
 */
public final class LegacyGate {

    /** Short refusal shown to players (S0-2). */
    public static final String CLOSED_MSG = "P1 模式下旧副本已关闭";

    /** Legacy DP gate ids read through {@code %corerpg_gate_<id>%} (progress.yml level_gates + elite). */
    public static final Set<String> LEGACY_GATE_IDS = Collections.unmodifiableSet(new LinkedHashSet<String>(
            Arrays.asList("daily", "weekly", "abyss", "raid", "elite")));

    /** S0-3 refusal shown to players. */
    public static final String ROUTE_CLOSED_MSG = "P1 模式下这个旧功能已关闭，请用 /ember 主菜单。";

    /** Any action allowed. */
    public static final String ANY = "*";

    private static final Map<String, String> ALIASES = new HashMap<String, String>();
    static {
        String[][] a = {
                {"属性", "stats"}, {"ember", "p1"}, {"锻造", "forge"}, {"部件", "part"},
                {"mainline", "quest"}, {"主线", "quest"},
                {"hubplaza", "hubbuild"}, {"workshopnpc", "hubnpc"}, {"abyssshaft", "abyssbuild"},
                {"weeklycorridor", "weeklybuild"}, {"elitecorridor", "elitebuild"},
                {"dailycourtyard", "dailybuild"}, {"courtyard", "dailybuild"},
                {"dailyash", "ashbuild"}, {"ashcorridor", "ashbuild"}, {"dailycrypt", "cryptbuild"}, {"crypt", "cryptbuild"},
                {"dailytide", "tidebuild"}, {"tide", "tidebuild"}, {"dailyspire", "spirebuild"}, {"spire", "spirebuild"},
                {"dailyfrost", "frostbuild"}, {"frost", "frostbuild"}, {"dailyrail", "railbuild"}, {"rail", "railbuild"},
                {"raidhall", "raidbuild"}, {"eventbuild", "calamitybuild"}, {"calamitybasin", "calamitybuild"},
                {"挂机", "afk"}, {"vendor", "life"}, {"补给", "life"}, {"生活", "life"},
                {"lv", "level"}, {"等级", "level"}, {"eliteweekly", "elite"}, {"精英试炼", "elite"},
                {"体力", "stamina"}, {"进本", "enter"}, {"ticket", "tickets"}, {"战令", "pass"},
                {"ec", "enderchest"}, {"末影箱", "enderchest"}, {"friends", "friend"}, {"setting", "settings"},
                {"pets", "pet"}, {"alliance", "guild"}, {"盟约", "guild"}, {"竞技", "arena"}, {"pvp", "arena"},
                {"寄售", "auction"}, {"ah", "auction"}, {"仓库", "warehouse"}, {"wh", "warehouse"},
                {"技能", "skill"}, {"轻技", "flex"}, {"团本", "raid"}, {"套装", "set"}, {"mmxp", "mmgive"},
        };
        for (String[] e : a) ALIASES.put(e[0], e[1]);
    }

    /**
     * Built-in whitelist (mirrors the shipped {@code ember-v1.yml legacy_gate.allow}; AUDIT §5.1).
     * Subcommands absent here (arena / pass free|claim / vip / calamity join / guild / scrap / reforge / socket /
     * enhance / forge / part / covenant / talent / shop / monthly / stamina convert / elite start / abyss settle|
     * evacuate, builders, admin tools, unknown) are refused for players while P1 is on.
     */
    public static final Map<String, Set<String>> DEFAULT_ALLOW;
    static {
        Map<String, Set<String>> m = new LinkedHashMap<String, Set<String>>();
        for (String s : new String[] {"status", "coin", "sign", "activity", "bounty", "stats", "p1", "quest", "afk",
                "life", "level", "enter", "tickets", "cash", "storage", "mail", "friend", "settings", "ladder", "pet",
                "skill", "flex", "set", "enderchest", "warehouse", "raid", "auction"}) {
            m.put(s, Collections.singleton(ANY));
        }
        m.put("pass", set("", "show", "info", "rewards", "season"));
        m.put("stamina", set("", "show", "info"));
        m.put("calamity", set("", "status", "info"));
        m.put("abyss", set("", "status", "info"));
        m.put("elite", set("status"));
        DEFAULT_ALLOW = Collections.unmodifiableMap(m);
    }

    private LegacyGate() {}

    private static Set<String> set(String... v) {
        return Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(v)));
    }

    /** Folds a {@code /corerpg} first argument to its canonical subcommand (lower-case). */
    public static String canonical(String sub) {
        if (sub == null) return "";
        String s = sub.toLowerCase(Locale.ROOT);
        String c = ALIASES.get(s);
        return c != null ? c : s;
    }

    /**
     * S0-3: true → refuse this {@code /corerpg} call.
     *
     * @param isPlayer  sender is a player (console / command blocks always pass)
     * @param privileged OP or {@code corerpg.admin}
     * @param allow     canonical subcommand → allowed actions ({@link #ANY} = all); null → {@link #DEFAULT_ALLOW}
     * @param args      raw command args (args[0] = subcommand)
     */
    public static boolean refuseRoute(boolean p1Active, boolean isPlayer, boolean privileged,
                                      Map<String, Set<String>> allow, String[] args) {
        if (!p1Active || !isPlayer || privileged) return false;
        if (args == null || args.length == 0) return false; // bare /corerpg = help
        String sub = canonical(args[0]);
        if ("help".equals(sub)) return false;
        Map<String, Set<String>> a = allow != null ? allow : DEFAULT_ALLOW;
        Set<String> acts = a.get(sub);
        if (acts == null) return true; // deny by default
        if (acts.contains(ANY)) return false;
        String act = args.length >= 2 && args[1] != null ? args[1].toLowerCase(Locale.ROOT) : "";
        return !acts.contains(act);
    }

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
