package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * D237 / ARCH S3 step 8: the P1 session create / stamina+fee reservation / DP dispatch /
 * {@code verifyEntry} path extracted from {@link EmberRunService} with <b>no behaviour change</b>.
 * Owns, in the original order after {@link EmberEntryService} gates:
 * <ol>
 *   <li>{@link #start}: build {@link EmberRunSession} (runId / snapshot / seed / extra / D138 variety +
 *       admin forcedExtra / forcedVariety), stamp participants + target family, reserve stamina
 *       ({@code reserveFlat}) and abyss fee ({@link EmberAbyssService#applyFeeSpend}), register the
 *       session + passes, send {@link EmberEntryService#enteringLine}, {@code dispatchStart}, schedule
 *       {@link #verifyEntry}.</li>
 *   <li>{@link #verifyEntry}: commit anyone already in the instance world, release no-shows, abort when
 *       nobody entered, lock A18 party HP/dmg factors, mode opening lines (abyss / festival / rush /
 *       raid / pledge / weekly / main; D302 abyss rhythm reveal).</li>
 *   <li>{@link #commit} / {@link #release} / {@link #releaseFee}: ledger cost / cost_coin status flips
 *       shared with abort / world-change commit (thin delegates on {@link EmberRunService}).</li>
 * </ol>
 * <p>Settlement ({@code onBossKilled} / {@code settleFor} / {@code failRefund}) lives in
 * {@link EmberSettleService} (D238) — shares the same session map + ledger via package accessors.
 * <p>The static {@code *Text} / variety / runId helpers are Bukkit-free so unit tests pin shapes
 * (bv58 unchanged).
 */
public final class EmberSessionService {

    private final EmberRunService runs;
    /** admin test hook: the next started run uses this extra event instead of the seeded roll (one shot). */
    private volatile EmberRunRules.Extra forcedExtra;
    /** D138 admin test hook: the next normal run gets this affix / event ("blazing:r2", "event:r1"; one shot). */
    private volatile String forcedVariety;

    EmberSessionService(EmberRunService runs) {
        this.runs = runs;
    }

    private static String P() { return EmberRunService.P; }

    // ------------------------------------------------------------------ Bukkit-free helpers

    /**
     * Same runId shape as pre-extract: {@code <mapKey>[aN|c]-<now36>-<rnd36>} where the middle token is
     * {@code Long.toString(nowMs, 36)} and the last is {@code Integer.toString(rndToken, 36)} with
     * {@code rndToken} in {@code [0, 36^3)}.
     */
    static String runId(String mapKey, int abyss, boolean challenge, long nowMs, int rndToken) {
        return mapKey + (abyss > 0 ? "a" + abyss : challenge ? "c" : "")
                + "-" + Long.toString(nowMs, 36) + "-" + Integer.toString(rndToken, 36);
    }

    /** D138: variety rolls only on plain repeatable main-line runs (same predicate as the enter gate). */
    static boolean varietyEligible(boolean challenge, int abyss, boolean raid, boolean rush, boolean varietyOn) {
        return !challenge && abyss == 0 && !raid && !rush && varietyOn;
    }

    /**
     * Apply an admin {@code runs variety} force onto a rolled {@code [affixRoom, affix, eventRoom, eventKind]}
     * quadruple (length ≥ 3; index 3 optional). Mutates a copy; never touches the input array.
     * Specs: {@code regen:r1} / {@code crystal:r2} / {@code event:r1} / {@code escort:r2} / known affix id /
     * known event id / {@code timed} synonym for event.
     */
    static String[] applyForcedVariety(String[] rolled, String forcedVariety) {
        String[] v = new String[4];
        v[0] = rolled.length > 0 && rolled[0] != null ? rolled[0] : "";
        v[1] = rolled.length > 1 && rolled[1] != null ? rolled[1] : "";
        v[2] = rolled.length > 2 && rolled[2] != null ? rolled[2] : "";
        v[3] = rolled.length > 3 && rolled[3] != null ? rolled[3] : (!v[2].isEmpty() ? "timed" : "");
        if (forcedVariety == null || forcedVariety.isEmpty()) return v;
        String[] fv = forcedVariety.split(":");
        String id = fv[0], room = fv.length > 1 ? fv[1] : "r1";
        if (EmberRunMaps.Variety.KNOWN.contains(id)) { v[1] = id; v[0] = room; }
        else if ("event".equals(id) || "timed".equals(id)) { v[2] = room; v[3] = "timed"; }
        else if (EmberRunMaps.Variety.EVENTS.contains(id)) { v[2] = room; v[3] = id; }
        return v;
    }

    /** Stamp variety fields from a rolled (or forced) quadruple onto the session. */
    static void stampVariety(EmberRunSession s, String[] v) {
        s.affixRoom = v[0];
        s.affix = v[1];
        s.eventRoom = v[2];
        s.eventKind = v.length > 3 && v[3] != null ? v[3] : (!v[2].isEmpty() ? "timed" : "");
    }

    static String abyssFeeMarkText(int pfee, int marksSpent, int markCoin, int marksLeft) {
        return "§7这一层的费用 " + pfee + " 币用 §f" + marksSpent + " 枚 T3 印记§7抵了（余烬币不够；1 枚抵 " + markCoin
                + " 币，剩 " + marksLeft + " 枚）· 没打成会和体力一起退回";
    }

    static String feeShortPartyText(String name, int pfee) {
        return name + " 余烬币不足（这一层 " + pfee + "）";
    }

    static String nobodyEnteredText() {
        return "没能进入实例，预留的体力已退还。原因见上方 DP 提示（人数 / 冷却约 5 秒）。";
    }

    /**
     * D302 / D300: short rhythm tag from map event.after + any room door_delay > 0.
     * Matches TrMenu adventure/challenge 「节奏：…」 copy (事件偏早/居中/偏晚 · 门慢半拍).
     */
    static String rhythmTag(String eventAfter, boolean doorSlow) {
        String ev;
        if ("r1".equals(eventAfter)) ev = "事件偏早";
        else if ("r3".equals(eventAfter)) ev = "事件偏晚";
        else ev = "事件居中"; // r2 or missing → mid
        return doorSlow ? ev + " · 门慢半拍" : ev;
    }

    /** True if any room on the map has door_delay > 0 (D300 W1b). */
    static boolean mapHasDoorDelay(EmberRunMaps.MapDef m) {
        if (m == null || m.rooms == null) return false;
        for (EmberRunMaps.Room r : m.rooms) if (r != null && r.doorDelay > 0) return true;
        return false;
    }

    /** D302 W1b: abyss enter reveal — map display name + D300 rhythm short tag. */
    static String abyssRhythmRevealText(String mapName, String eventAfter, boolean doorSlow) {
        String name = mapName == null || mapName.isEmpty() ? "?" : mapName;
        return "§5本层地图：§f" + name + " §7· 节奏：§b" + rhythmTag(eventAfter, doorSlow);
    }

    static String abyssOpeningText(int abyss, double hp, double dmg, String qualityLabel) {
        return "§5深渊第 " + abyss + " 层 §7· 敌方生命 ×" + String.format(Locale.ROOT, "%.2f", hp) + " 伤害 ×"
                + String.format(Locale.ROOT, "%.2f", dmg) + "（在挑战版之上）· 掉落成色 " + qualityLabel
                + " · 打完首领才结算，失败只丢这一层的花费";
    }

    static String festivalOpeningText(String name, int partySize, double hpFactor, String coinName) {
        return "§c国庆 · " + name + " §7· " + partySize + " 人（敌方生命 ×" + String.format(Locale.ROOT, "%.2f", hpFactor)
                + "）· 小怪和首领掉" + coinName + " · 不发余烬币和装备 · 首次通关得限时称号 · 倒下即失败";
    }

    static String rushOpeningText(String rushLabel, int partySize, String chainText, double hpFactor, double dmgFactor,
                                  boolean multiBoss, long breakSecs, long healPct, String rewardText) {
        return "§c" + rushLabel + " §7· " + partySize + " 人 · " + chainText + " · 首领生命 ×"
                + String.format(Locale.ROOT, "%.2f", hpFactor) + " 伤害 ×" + String.format(Locale.ROOT, "%.2f", dmgFactor)
                + (multiBoss ? " · 每打倒一个休息 " + breakSecs + " 秒、站着的人回复 " + healPct + "% 生命" : "")
                + " · 倒下观战，没有复活 · 只发" + rewardText;
    }

    static String pledgeOpeningText(String modName, String modText, int npl) {
        return "§d自选誓约「" + modName + "」§7" + modText + " · 通关结算每人 +" + npl + " 枚本图首领徽记（掉落不变）";
    }

    static String weeklyModOpeningText(String modName, String modText, boolean challenge) {
        return "§b本周规则「" + modName + "」§7" + modText + "（奖励不变" + (challenge ? "" : "；本周精选图首通后的重打") + "）";
    }

    static String mainOpeningText(boolean abyss, boolean challenge, int partySize, double hpFactor) {
        return (abyss ? "§5深渊 §7· 掉落 T3 · " : challenge ? "§c挑战版 §7· 掉落 T3 · " : "§7")
                + "主线本开始 · " + partySize + " 人（敌方生命 ×" + String.format(Locale.ROOT, "%.2f", hpFactor)
                + "）· 走进前方房间开战 · 击败首领后统一结算";
    }

    // ------------------------------------------------------------------ admin hooks (moved from EmberRunService)

    void forceExtra(EmberRunRules.Extra e) { forcedExtra = e; }
    EmberRunRules.Extra forcedExtra() { return forcedExtra; }

    void forceVariety(String v) { forcedVariety = v; }
    String forcedVariety() { return forcedVariety; }

    // ------------------------------------------------------------------ start (create + reserve + dispatch)

    /**
     * After {@link EmberEntryService#admit} + {@link EmberEntryService#readinessHold}: create the session,
     * reserve stamina (+ abyss fee), register passes, dispatch DP, schedule verifyEntry.
     * Always returns {@code true} (same as the pre-extract {@code enter} tail).
     */
    boolean start(final Player leader, EmberEntryService.Admit a, final boolean challenge, final int abyss, long presetSeed) {
        final EmberRunMaps maps = runs.maps();
        final EmberRunMaps.MapDef m = a.map;
        final EmberRunMaps.AbyssTier at = a.tier;
        final List<Player> party = a.party;
        final int cost = a.cost;
        final StaminaService st = a.stamina;
        final EmberEntryService entry = runs.entry();

        // create the session first (seed, snapshot, extra event fixed now — never re-rolled on reconnect)
        final EmberRunSession s = new EmberRunSession();
        s.runId = runId(m.key, abyss, challenge, System.currentTimeMillis(), runs.nextRunToken());
        s.mapKey = m.key;
        s.dungeon = m.dungeon;
        s.mapVersion = m.mapVersion;
        s.ruleVersion = maps.ruleVersion;
        s.contentVersion = m.contentVersion;
        s.challenge = challenge;
        s.abyss = abyss;
        s.tier = challenge ? maps.challenge.tier : m.tier;
        entry.applyWeeklyRule(s, leader, m, party, challenge, abyss); // P2-8 / D94 / D174 (D235 → EmberEntryService)
        s.seed = presetSeed != 0L ? presetSeed : runs.nextSeed();
        s.created = System.currentTimeMillis();
        s.leader = leader.getUniqueId();
        s.extra = EmberRunRules.rollExtra(new java.util.Random(EmberRunRules.subSeed(s.seed, "extra")).nextDouble());
        if (forcedExtra != null) { // admin test hook, one shot
            runs.log().info("[P1 run] " + s.runId + " extra forced " + s.extra.id + " → " + forcedExtra.id + " (admin test)");
            s.extra = forcedExtra;
            forcedExtra = null;
        } else {
            // D501: party-leader extra path bias (admin force still absolute)
            PlayerData ld = runs.dataOf(leader.getUniqueId());
            int extraPath = EmberExtraPath.get(ld);
            if (EmberExtraPath.valid(extraPath)) {
                EmberRunRules.Extra before = s.extra;
                s.extra = EmberExtraPath.applyBias(s.extra, extraPath);
                if (before != s.extra) {
                    runs.log().info("[P1 run] " + s.runId + " extrapath " + EmberExtraPath.key(extraPath)
                            + " " + before.id + " → " + s.extra.id);
                }
            }
        }
        if (varietyEligible(challenge, abyss, m.raid, m.rush, maps.variety.on())) { // D138: repeat-run variety, first clears stay canonical
            boolean all = true;
            for (Player p : party) if (!runs.firstCleared(runs.dataOf(p.getUniqueId()), m)) { all = false; break; }
            if (all || forcedVariety != null) {
                String[] rolled = maps.variety.roll(EmberRunRules.subSeed(s.seed, "variety"));
                String force = forcedVariety;
                if (force != null) { // admin test hook, one shot
                    runs.log().info("[P1 run] " + s.runId + " variety forced " + force + " (admin test)");
                    forcedVariety = null;
                }
                String[] v = applyForcedVariety(rolled, force);
                // D500: party-leader spice bias (skip when admin force won — force stays absolute)
                if (force == null) {
                    PlayerData ld = runs.dataOf(leader.getUniqueId());
                    int spice = EmberSpicePath.get(ld);
                    if (EmberSpicePath.valid(spice)) {
                        String[] biased = EmberSpicePath.applyBias(v, spice, maps.variety.affixes, maps.variety.events,
                                EmberRunRules.subSeed(s.seed, "spice"));
                        if (!Arrays.equals(v, biased)) {
                            runs.log().info("[P1 run] " + s.runId + " spice " + EmberSpicePath.key(spice)
                                    + " " + v[1] + "/" + v[3] + " → " + biased[1] + "/" + biased[3]);
                            v = biased;
                        }
                    }
                }
                stampVariety(s, v);
            }
        }
        for (Player p : party) {
            s.participants.add(p.getUniqueId());
            String t = runs.target(runs.dataOf(p.getUniqueId()));
            s.target.put(p.getUniqueId(), t == null ? "" : t);
        }
        // D502: per-player potion prep top-up while still in town (before stamina reserve / DP)
        for (Player p : party) EmberPrepPath.maybeTopUpBeforeRun(p, runs);
        // reserve
        List<Player> reserved = new ArrayList<Player>();
        for (Player p : party) {
            StaminaService.ConsumeResult r = st.reserveFlat(p, cost);
            if (!r.ok) {
                for (Player q : reserved) release(s, q.getUniqueId(), "预留失败回滚");
                for (Player q : party) q.sendMessage(P() + ChatColor.RED + (r.failMessage == null ? "体力不足" : r.failMessage));
                return true;
            }
            s.cost.put(p.getUniqueId(), r.cost);
            runs.ledgerRow(p.getUniqueId(), s.runId, "cost", "stamina:" + r.cost, EmberRunRules.ST_RESERVED);
            reserved.add(p);
            final int pfee = at == null ? 0 : runs.feeFor(p, at.fee); // D142 深渊行者: own fee per player
            if (at != null && pfee > 0) { // P2-2: the segment fee rides with the stamina reservation (D231 → EmberAbyssService)
                PlayerData pd = runs.dataOf(p.getUniqueId());
                EmberAbyssService.FeeSpendResult spent = EmberAbyssService.applyFeeSpend(pd, pfee, maps.abyssFeeMarkCoin);
                if (!spent.ok) {
                    for (Player q : reserved) release(s, q.getUniqueId(), "预留失败回滚");
                    for (Player q : party) q.sendMessage(P() + ChatColor.RED + feeShortPartyText(p.getName(), pfee));
                    return true;
                }
                s.fee.put(p.getUniqueId(), spent.feeStored);
                runs.ledgerRow(p.getUniqueId(), s.runId, "cost_coin", spent.ledgerResult, EmberRunRules.ST_RESERVED);
                if (spent.marksSpent > 0) {
                    p.sendMessage(P() + abyssFeeMarkText(pfee, spent.marksSpent, maps.abyssFeeMarkCoin, runs.marks(pd, 3)));
                    runs.log().info("[P1 run] " + s.runId + " abyss fee " + p.getName() + ": " + spent.marksSpent + " T3 mark(s) for " + pfee + " coin");
                }
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            }
        }
        runs.putSession(s);
        runs.store().save(s);
        long until = System.currentTimeMillis() + maps.passSeconds * 1000L;
        for (Player p : party) {
            runs.putPass(p.getUniqueId(), m.key, until);
            p.sendMessage(P() + entry.enteringLine(p, m, at, abyss, challenge, cost)); // D235 → EmberEntryService
        }
        boolean ok = runs.plugin().getTicketEntryService() != null
                && runs.plugin().getTicketEntryService().dispatchStart(leader, m.dungeon);
        if (!ok) {
            runs.abort(s, "DP 实例创建失败", true);
            return true;
        }
        Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> verifyEntry(s), 40L);
        return true;
    }

    // ------------------------------------------------------------------ verifyEntry / commit / release

    void verifyEntry(EmberRunSession s) {
        if (!s.open()) return;
        for (UUID u : new ArrayList<UUID>(s.participants)) {
            Player p = Bukkit.getPlayer(u);
            boolean in = p != null && p.isOnline() && s.world != null && p.getWorld().getName().equals(s.world);
            if (in) commit(s, u);
            else if (!s.committed.contains(u)) {
                release(s, u, "未进入实例");
                s.participants.remove(u);
                if (p != null) p.sendMessage(P() + nobodyEnteredText());
            }
            runs.clearPass(u);
        }
        if (s.committed.isEmpty()) {
            s.state = EmberRunSession.ABORTED;
            s.reason = "nobody entered";
            runs.store().save(s);
            runs.removeSession(s.runId);
            return;
        }
        if (EmberRunSession.PREPARE.equals(s.state)) s.state = EmberRunSession.ENTERED;
        // D477: map-enter ActionBar for each committed player (before opening tellRun)
        EmberRunMaps.MapDef enterFeelMap = runs.maps().byKey(s.mapKey);
        for (UUID cu : s.committed) {
            Player cp = Bukkit.getPlayer(cu);
            if (cp != null && cp.isOnline()) EmberMapEnterFeel.flash(cp, enterFeelMap, s);
            if (cp != null && cp.isOnline()) EmberMapCardCue.cue(cp, enterFeelMap, s); // D489 chat 名片+loot
        }
        // A18: party HP multiplier locked now for the whole run
        s.partySize = s.committed.size();
        EmberRunMaps maps = runs.maps();
        EmberRunMaps.MapDef vm = maps.byKey(s.mapKey);
        s.hpFactor = EmberRunMaps.hpFactor(vm, s.partySize);
        s.dmgFactor = EmberRunMaps.dmgFactor(vm, s.partySize); // P2-5 raids only (1.0 elsewhere)
        runs.store().save(s);
        if (s.abyss > 0) {
            EmberRunMaps.AbyssTier t = maps.abyssTier(s.abyss);
            if (t != null) runs.tellRun(s, abyssOpeningText(s.abyss, t.hp, t.dmg, EmberRunService.qualityLabel(t.quality)));
            // D302 W1b: map display name + D300 rhythm short tag (real event.after / door_delay)
            if (vm != null) runs.tellRun(s, abyssRhythmRevealText(vm.name, vm.eventAfter, mapHasDoorDelay(vm)));
        }
        runs.potionCheck(s);
        if (vm != null && vm.event && runs.festival() != null) { // D139: the day's entry counts once the player is inside
            EmberFestival festival = runs.festival();
            for (UUID u : s.committed) { PlayerData pd = runs.dataOf(u); if (pd != null) { festival.countEntry(pd); runs.flushData(u); } }
            runs.tellRun(s, festivalOpeningText(vm.name, s.partySize, s.hpFactor, festival.coinName));
            return;
        }
        if (vm != null && vm.rush) { // D144: chain bosses × rush HP / damage on top of the party HP factor
            s.hpFactor = s.hpFactor * vm.rushHp;
            s.dmgFactor = vm.rushDmg;
            runs.store().save(s);
            runs.tellRun(s, rushOpeningText(vm.rushLabel, s.partySize, EmberRushService.chainText(vm), s.hpFactor, s.dmgFactor,
                    vm.chain.size() > 1, Math.round(vm.rushBreak), Math.round(vm.rushHeal * 100), EmberRushService.rewardText(vm)));
            return;
        }
        if (vm != null && vm.raid) { // D233 → EmberRaidService
            runs.raid().onStart(s, vm);
            return;
        }
        EmberRunMaps.Modifier mod = maps.modifier(s.modifier);
        int npl = EmberRunMaps.pledgeIds(s.modifier).size();
        if (mod != null && npl > 0) runs.tellRun(s, pledgeOpeningText(mod.name, mod.text, npl));
        else if (mod != null) runs.tellRun(s, weeklyModOpeningText(mod.name, mod.text, s.challenge));
        runs.tellRun(s, mainOpeningText(s.abyss > 0, s.challenge, s.partySize, s.hpFactor));
    }

    void commit(EmberRunSession s, UUID u) {
        if (!s.committed.add(u)) return;
        EmberRunRules.Ledger l = runs.store().ledger(u);
        l.mark(s.runId, "cost", EmberRunRules.ST_COMMITTED, System.currentTimeMillis());
        runs.store().saveLedger(u, Collections.singletonList(l.get(s.runId, "cost")));
        if (l.get(s.runId, "cost_coin") != null) {
            l.mark(s.runId, "cost_coin", EmberRunRules.ST_COMMITTED, System.currentTimeMillis());
            runs.store().saveLedger(u, Collections.singletonList(l.get(s.runId, "cost_coin")));
        }
    }

    /** Releases a reservation once (idempotent through the ledger "cost" row status). */
    void release(EmberRunSession s, UUID u, String why) {
        EmberRunRules.Ledger l = runs.store().ledger(u);
        EmberRunRules.Row r = l.get(s.runId, "cost");
        if (r == null || EmberRunRules.ST_RELEASED.equals(r.status)) return;
        Integer c = s.cost.get(u);
        StaminaService st = runs.plugin().getStaminaService();
        if (c != null && c > 0 && st != null) st.releaseFlat(u, c);
        l.mark(s.runId, "cost", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
        runs.store().saveLedger(u, Collections.singletonList(r));
        releaseFee(s, u, l);
        runs.log().info("[P1 run] " + s.runId + " release " + u + " (" + why + ")");
    }

    /** P2-2: the abyss fee goes back together with the stamina, once (ledger "cost_coin" status). D231 → EmberAbyssService. */
    void releaseFee(EmberRunSession s, UUID u, EmberRunRules.Ledger l) {
        EmberRunRules.Row f = l.get(s.runId, "cost_coin");
        if (f == null || EmberRunRules.ST_RELEASED.equals(f.status)) return;
        Integer fee = s.fee.get(u);
        EmberAbyssService.FeeRefundResult r = EmberAbyssService.applyFeeRefund(runs.dataOf(u), f.result, fee == null ? 0 : fee);
        if (r.refunded) runs.plugin().getDataStore().flushMutation(u);
        l.mark(s.runId, "cost_coin", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
        runs.store().saveLedger(u, Collections.singletonList(f));
    }
}
