package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D235 / ARCH S3 step 6: the P1 entry gate path extracted from {@link EmberRunService#tryEnter}
 * with <b>no behaviour change</b>. Owns, in the original order:
 * <ol>
 *   <li>{@link #admit}: mode on / map known / challenge &amp; abyss configured / team leader, party collection
 *       (offline members named), party size, mode-variant problems, stamina service, and the per-member gates —
 *       abyss ({@link EmberAbyssService#entryProblems}), challenge, festival ({@link EmberFestival#entryProblem}),
 *       rush ({@link EmberRushService#entryProblem}), raid ({@link EmberRaidService#entryProblem}), unlock —
 *       then "already in a run" / "still in a dungeon" / stamina. Every problem goes to every online member
 *       (and the leader if they are not in the party).</li>
 *   <li>{@link #readinessHold}: D96 T1 first-clear warning and the D104 challenge T3 warning (one-shot
 *       {@code force} bypass, D104 once per server session).</li>
 *   <li>{@link #applyWeeklyRule}: P2-8 challenge weekly rule, D94 featured-map normal rule (first clears stay
 *       canonical), admin one-shot forced rule, and the D174 自选誓约 fallback ({@link EmberPledgeService#sessionKey}).</li>
 *   <li>{@link #enteringLine}: the per-member「正在创建实例……」line.</li>
 * </ol>
 * <p>Session create / seed / extra / variety, stamina + abyss-fee <b>reservation</b>, DP dispatch and
 * {@code verifyEntry} commit/release live in {@link EmberSessionService} (D237). Settlement
 * ({@code settleFor} / {@code onBossKilled}) stays in {@link EmberRunService} (TODO).
 * <p>The static {@code *Text} / {@code *Problem} / {@code *Rule} helpers are Bukkit-free so unit tests pin
 * gate wording and rule selection (bv58 unchanged).
 */
public final class EmberEntryService {

    static final String MSG_MODE_OFF = "新模式（ember-v1.0-P1）尚未开启，主线 Q 本暂不可进入。";
    static final String MSG_NO_CHALLENGE = "挑战版未配置。";
    static final String MSG_NO_ABYSS = "深渊未配置。";
    static final String MSG_NOT_LEADER = "组队时由队长开本。";
    static final String MSG_NO_STAMINA_SVC = "体力服务未就绪";

    private final EmberRunService runs;
    /** D104: leaders already shown the challenge T3 warning this server session */
    private final Set<UUID> warnedT3 = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    /** D96: {@code /corerpg p1 enter … force} — skip the next readiness warning (one shot) */
    private final Set<UUID> forcedReady = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    /** P2-8 admin test hook: the next challenge / featured normal run uses this weekly rule (one shot). */
    private volatile EmberRunMaps.Modifier forcedModifier;

    EmberEntryService(EmberRunService runs) {
        this.runs = runs;
    }

    private static String P() { return EmberRunService.P; }

    /** What {@link #admit} hands back when every gate passed. */
    static final class Admit {
        final EmberRunMaps.MapDef map;
        final EmberRunMaps.AbyssTier tier; // null unless abyss > 0
        final List<Player> party;
        final int cost;
        final StaminaService stamina;     // non-null once admitted
        Admit(EmberRunMaps.MapDef map, EmberRunMaps.AbyssTier tier, List<Player> party, int cost, StaminaService stamina) {
            this.map = map; this.tier = tier; this.party = party; this.cost = cost; this.stamina = stamina;
        }
    }

    // ------------------------------------------------------------------ Bukkit-free gate text / predicates

    static String unknownMapText(String mapKey) { return "未知主线本 " + mapKey; }

    static String offlineText(String name) { return "队员不在线：" + name; }

    /** null when {@code min ≤ size ≤ max} */
    static String partySizeProblem(int size, int min, int max) {
        return size < min || size > max ? "人数 " + min + "～" + max + "，当前 " + size : null;
    }

    /** raid / event / rush have no challenge or abyss variant (each one line, original order) */
    static List<String> variantProblems(boolean raid, boolean event, boolean rush, String rushLabel, boolean challenge, int abyss) {
        List<String> out = new ArrayList<String>();
        boolean variant = challenge || abyss > 0;
        if (raid && variant) out.add("团本没有挑战 / 深渊版本");
        if (event && variant) out.add("活动本没有挑战 / 深渊版本");
        if (rush && variant) out.add(rushLabel + "没有挑战 / 深渊版本");
        return out;
    }

    static String challengeLockedText(String name, String requires) {
        return name + " 未开放挑战版（需本人首通 " + requires.toUpperCase(Locale.ROOT) + "）";
    }

    /** {@code reqKey/reqName} = the resolved required map (null = unknown key, the raw {@code requires} is shown) */
    static String lockedText(String name, String requires, String reqKey, String reqName) {
        return name + " 未解锁（需先首通 " + (reqKey == null ? requires : reqKey.toUpperCase(Locale.ROOT) + " " + reqName) + "）";
    }

    static String busyText(String name) { return name + " 已在另一局主线本中"; }

    static boolean inDungeonWorld(String world) { return world != null && world.startsWith("dungeon_"); }

    static String inDungeonText(String name) { return name + " 仍在副本内"; }

    /** null when {@code have ≥ cost} */
    static String staminaProblem(String name, int have, int cost) {
        return have < cost ? name + " 体力不足（需 " + cost + "，当前 " + have + "）" : null;
    }

    /** D94 / D96 / D138: a plain repeatable main-line run (no challenge / abyss / raid / event / rush) */
    static boolean plainMainRun(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        return !challenge && abyss == 0 && !m.raid && !m.event && !m.rush;
    }

    /** P2-8: the challenge weekly rule = this week's rule on the featured map only */
    static EmberRunMaps.Modifier challengeRule(boolean featured, EmberRunMaps.Modifier weekly) {
        return featured ? weekly : null;
    }

    /** D94: normal runs of the featured map get the rule only when it is {@code normal: true} and every member has the first clear */
    static EmberRunMaps.Modifier normalRule(boolean featured, EmberRunMaps.Modifier weekly, boolean allFirstCleared) {
        EmberRunMaps.Modifier mod = featured ? weekly : null;
        if (mod != null && !mod.normal) mod = null;
        if (mod != null && !allFirstCleared) mod = null;
        return mod;
    }

    static String t1HeadText(String mapKey) {
        return "§e" + mapKey.toUpperCase(Locale.ROOT) + " 首通推荐：T1 刃 + T1 护符，两件都来自 Q01"
                + "（护符 = Q01 首通自选，刃 = Q01 掉落）。" + (mapKey.equals("q02") ? "Q02 首通送一次免费定向兑换（自选族和部位的 T1 件），用来补齐同族的那一件。" : "");
    }

    static String t1BladeWarn(String name) {
        return name + " 主手还没有 T1 刃：回 Q01 多打几局（Q01 偏向掉刃），拿到会自动放到快捷栏第 1 格";
    }

    static String t1CharmWarn(String name) {
        return name + " 还没有生效的 T1 护符（生命只有一半）：先领 Q01 首通自选的护符";
    }

    static String t1TailText(String mapKey) {
        return "§7这样首通 " + mapKey.toUpperCase(Locale.ROOT) + " 几乎打不过，倒下不退体力。";
    }

    static String t3WarnText(List<String> low) {
        return "§e挑战版按 T3 装备来调；" + String.join("、", low) + " 主手还不是 T3 刃。";
    }

    // ------------------------------------------------------------------ one-shot state (cmd hooks)

    /** D96: {@code /corerpg p1 enter … force} */
    void markForced(UUID u) { forcedReady.add(u); }

    /** P2-8 admin: {@code /corerpg p1 runs modifier <id|clear>} */
    void forceModifier(EmberRunMaps.Modifier m) { forcedModifier = m; }

    EmberRunMaps.Modifier forcedModifier() { return forcedModifier; }

    // ------------------------------------------------------------------ live gates

    /**
     * All entry gates. Returns null when the entry was refused (the reason has been sent), else the admitted party.
     */
    Admit admit(Player leader, String mapKey, boolean challenge, int abyss) {
        if (!EmberMode.active()) {
            leader.sendMessage(P() + MSG_MODE_OFF);
            return null;
        }
        EmberRunMaps maps = runs.maps();
        final EmberRunMaps.MapDef m = maps.byKey(mapKey);
        if (m == null) { leader.sendMessage(P() + unknownMapText(mapKey)); return null; }
        if (challenge && maps.challenge == null) { leader.sendMessage(P() + MSG_NO_CHALLENGE); return null; }
        final EmberRunMaps.AbyssTier at = abyss > 0 ? maps.abyssTier(abyss) : null;
        if (abyss > 0 && at == null) { leader.sendMessage(P() + MSG_NO_ABYSS); return null; }
        if (!EmberRunBridges.teamLeader(leader)) { leader.sendMessage(P() + MSG_NOT_LEADER); return null; }
        List<UUID> ids = EmberRunBridges.teamMembers(leader);
        List<Player> party = new ArrayList<Player>();
        List<String> problems = new ArrayList<String>();
        for (UUID u : ids) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) { problems.add(offlineText(EmberRunService.nameOf(u))); continue; } // D101: a name, not a uuid
            party.add(p);
        }
        final int cost = maps.cost(m);
        String size = partySizeProblem(party.size(), maps.partyMin(m), maps.partyMax(m));
        if (size != null) problems.add(size);
        problems.addAll(variantProblems(m.raid, m.event, m.rush, m.rushLabel, challenge, abyss));
        StaminaService st = runs.plugin().getStaminaService();
        if (st == null) problems.add(MSG_NO_STAMINA_SVC);
        for (Player p : party) {
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (at != null) {
                runs.abyss().entryProblems(p, d, at, abyss, problems); // D231 → EmberAbyssService
            } else if (challenge && !runs.challengeOpen(d)) {
                problems.add(challengeLockedText(p.getName(), maps.challenge.requires));
            } else if (m.event) { // D139: festival window, own first clear of `requires`, daily entries
                EmberFestival festival = runs.festival();
                String why = festival == null ? "活动未加载" : festival.entryProblem(p, d);
                if (why != null) problems.add(why);
            } else if (m.rush) { // D144 / D160 / D174 → EmberRushService (D230)
                String why = runs.rush().entryProblem(p, d, m);
                if (why != null) problems.add(why);
            } else if (m.raid) { // P2-5: own Q07 first clear + weekly cap of settled clears (D233 → EmberRaidService)
                String why = runs.raid().entryProblem(p, d, m);
                if (why != null) problems.add(why);
            } else if (!challenge && !runs.unlocked(d, m)) {
                EmberRunMaps.MapDef req = maps.byKey(m.requires);
                problems.add(lockedText(p.getName(), m.requires, req == null ? null : req.key, req == null ? null : req.name));
            }
            if (runs.openSessionOf(p.getUniqueId()) != null) problems.add(busyText(p.getName()));
            if (inDungeonWorld(p.getWorld().getName())) problems.add(inDungeonText(p.getName()));
            if (st != null) {
                String why = staminaProblem(p.getName(), st.staminaOf(p), cost);
                if (why != null) problems.add(why);
            }
        }
        if (!problems.isEmpty()) {
            for (Player p : party) for (String s : problems) p.sendMessage(P() + ChatColor.RED + s);
            if (!party.contains(leader)) for (String s : problems) leader.sendMessage(P() + ChatColor.RED + s);
            return null;
        }
        return new Admit(m, at, party, cost, st);
    }

    /**
     * D96 / D104 readiness warnings. True = a warning with buttons was shown and the entry stops here
     * ({@code force} re-runs without it).
     */
    boolean readinessHold(Player leader, EmberRunMaps.MapDef m, List<Player> party, boolean challenge, int abyss) {
        EmberRunMaps maps = runs.maps();
        // D96: a first attempt at Q02+ without a T1 blade in hand or a selected T1 charm is a near-certain death (p1sim
        // 0 % even at dodge 0.5) that costs a third of the day's stamina: warn once, entering stays the player's choice.
        if (plainMainRun(m, challenge, abyss) && !forcedReady.remove(leader.getUniqueId())) {
            EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
            EmberRunMaps.MapDef q1 = maps.maps.isEmpty() ? null : maps.maps.values().iterator().next();
            List<String> warn = new ArrayList<String>();
            boolean noCharm = false;
            if (ls != null && q1 != null && !m.key.equals(q1.key)) for (Player p : party) {
                if (runs.firstClearDone(runs.dataOf(p.getUniqueId()), m)) continue;
                EmberLoadout l = ls.refresh(p);
                // D98: Q01 first clear now gives the T1 charm; the T1 blade comes from Q01 drops (Q01 leans to blades)
                if (l.blade == null || l.blade.tier < 1) warn.add(t1BladeWarn(p.getName()));
                if (l.charm == null || l.charm.tier < 1) {
                    warn.add(t1CharmWarn(p.getName()));
                    if (p.equals(leader)) noCharm = true;
                }
            }
            if (!warn.isEmpty()) {
                // D101 (midgame recheck #1): say the whole rule — both T1 pieces come from Q01; the Q02 first-clear blade
                // is a second, chosen-family blade (to match the charm), not the way in
                leader.sendMessage(P() + t1HeadText(m.key));
                for (String w : warn) leader.sendMessage(P() + ChatColor.YELLOW + "⚠ " + w);
                leader.sendMessage(P() + t1TailText(m.key));
                List<String[]> btn = new ArrayList<String[]>();
                if (noCharm) btn.add(new String[]{"[领 Q01 首通护符]", "/corerpg p1 firstclear", "领取 Q01 首通自选的 T1 护符（选族）", "GREEN"});
                btn.add(new String[]{"[回 Q01]", "/corerpg p1 enter " + q1.key, "开一局 Q01（30 体力），刷 T1 刃", "GREEN"});
                btn.add(new String[]{"[仍然进入]", "/corerpg p1 enter " + m.key + " force", "本次不再提醒，直接开本", "RED"});
                town.sunshine.corerpg.ConfirmTokens.sendButtons(leader, P(), btn.toArray(new String[0][]));
                return true;
            }
        }
        // D104 (midgame #2): the challenge is tuned for T3 — warn once per server session when someone still has a T2 blade
        if (challenge && abyss == 0 && !forcedReady.remove(leader.getUniqueId()) && warnedT3.add(leader.getUniqueId())) {
            EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
            List<String> low = new ArrayList<String>();
            if (ls != null) for (Player p : party) {
                EmberLoadout l = ls.refresh(p);
                if (l.blade == null || l.blade.tier < 3) low.add(p.getName());
            }
            if (!low.isEmpty()) {
                leader.sendMessage(P() + t3WarnText(low));
                leader.sendMessage(P() + "§7先用 8 枚 T3 印记兑换（或升阶）一把 T3 刃，再到工坊「互换」免费把强化挪过去，通关率会高很多。");
                town.sunshine.corerpg.ConfirmTokens.sendButtons(leader, P(),
                        new String[]{"[印记兑换]", "/corerpg p1 marks", "看看能兑换什么", "GREEN"},
                        new String[]{"[仍然进入]", "/corerpg p1 enter " + m.key + " challenge force", "本次不再提醒，直接开本", "RED"});
                return true;
            }
        }
        return false;
    }

    /**
     * P2-8 / D94 / D174: sets {@code s.modifier} (only on challenge and plain main runs; other entries keep it untouched).
     * {@code s.runId} must already be set (log lines).
     */
    void applyWeeklyRule(EmberRunSession s, Player leader, EmberRunMaps.MapDef m, List<Player> party, boolean challenge, int abyss) {
        EmberRunMaps maps = runs.maps();
        if (challenge && abyss == 0) { // P2-8 weekly rule, fixed at entry
            java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
            boolean featured = m.key.equals(runs.featured(today));
            EmberRunMaps.Modifier mod = challengeRule(featured, featured ? maps.modifierFor(today) : null);
            if (forcedModifier != null) {
                runs.log().info("[P1 run] " + s.runId + " modifier forced " + forcedModifier.id + " (admin test)");
                mod = forcedModifier;
                forcedModifier = null;
            }
            s.modifier = mod == null ? "" : mod.id;
        } else if (plainMainRun(m, challenge, abyss)) { // D94: repeat normal runs of the featured map get the rule too
            java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
            boolean featured = m.key.equals(runs.featured(today));
            EmberRunMaps.Modifier weekly = featured ? maps.modifierFor(today) : null;
            boolean all = true;
            if (weekly != null && weekly.normal) // first clears stay canonical (术者换防 stays challenge-only: +6～+17 points)
                for (Player p : party) if (!runs.firstCleared(runs.dataOf(p.getUniqueId()), m)) { all = false; break; }
            EmberRunMaps.Modifier mod = normalRule(featured, weekly, all);
            if (forcedModifier != null) {
                runs.log().info("[P1 run] " + s.runId + " modifier forced " + forcedModifier.id + " (admin test, normal run)");
                mod = forcedModifier;
                forcedModifier = null;
            }
            s.modifier = mod == null ? "" : mod.id;
            if (mod == null) { // D174 stage 2b 自选誓约: the leader's pledge, only where the weekly rule is off and everyone has the first clear (D232 → EmberPledgeService)
                String pk = runs.pledge().sessionKey(leader, m, party);
                if (pk != null) {
                    s.modifier = pk;
                    runs.log().info("[P1 run] " + s.runId + " pledge " + pk + " by " + leader.getName());
                }
            }
        }
    }

    /** the per-member「正在创建实例……」line (abyss / rush / festival / raid·main) */
    String enteringLine(Player p, EmberRunMaps.MapDef m, EmberRunMaps.AbyssTier at, int abyss, boolean challenge, int cost) {
        if (at != null) return "§5深渊 · 余烬层 第 " + abyss + " 层 §7→ §e" + m.key.toUpperCase(Locale.ROOT) + " " + m.name
                + " §7正在创建实例……（已预留体力 " + cost + (at.fee > 0 ? " · 余烬币 " + at.fee : "") + "）";
        if (m.rush) return "§c" + m.rushLabel + " §7正在创建实例……（不耗体力 · " + EmberRushService.ruleText(m) + " · "
                + (EmberRunRules.rushPaysReward(runs.rush().week(runs.dataOf(p.getUniqueId()), p.getUniqueId(), m), m.rushWeekly) ? "§a你本周奖励未领完§7" : "§e你本周已领完，这局是练习（无奖励）§7") + "）";
        if (m.event) {
            EmberFestival festival = runs.festival();
            return "§c国庆活动本 §6" + m.name + " §7正在创建实例……（不耗体力 · 今日第 " + (festival.entriesToday(runs.dataOf(p.getUniqueId())) + 1) + "/" + festival.dailyEntries + " 次）";
        }
        return "§e" + (m.raid ? "团本 " : "") + m.key.toUpperCase(Locale.ROOT) + " " + m.name + (challenge ? " §c挑战版" : "") + " §7正在创建实例……（已预留体力 " + cost + "）";
    }
}
