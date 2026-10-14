package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * D238 / ARCH S3 step 9: P1 settlement extracted from {@link EmberRunService} with <b>no behaviour change</b>.
 * Owns, in the original order:
 * <ol>
 *   <li>{@link #onBossKilled}: eligibility per participant → festival / rush / {@link #settleFor}; session COMPLETE; end instance.</li>
 *   <li>{@link #settleFor}: grant tree (base settle + rotation / raid / signature / pledge / variety / bounty / honor / season) through the shared ledger.</li>
 *   <li>{@link #failRefund} / {@link #failRefundLabel}: D128 first failed challenge/abyss stamina refund (once per stamina day).</li>
 * </ol>
 * <p>Shares the session map + ledger with {@link EmberSessionService} via package accessors on {@link EmberRunService}.
 * Thin delegates remain on {@link EmberRunService} ({@code fail} → failRefund; death event → onBossKilled; PAPI → failRefundLabel).
 * <p>Bukkit-free helpers below pin fail-refund / not-eligible / rotation texts and grant-key order (bv58 unchanged).
 */
public final class EmberSettleService {

    private final EmberRunService runs;

    EmberSettleService(EmberRunService runs) {
        this.runs = runs;
    }

    // ------------------------------------------------------------------ Bukkit-free helpers

    /** D128 PAPI when today's fail refund is still available. */
    static String failRefundAvailableText(int pct, int amount) {
        return "§a每天第一次失败退还 " + pct + "% 体力（" + amount + " 点；不给掉落和币）";
    }

    /** D128 PAPI when today's fail refund was already used. */
    static String failRefundUsedText() {
        return "§8今天的失败退还已用过（明天 0 点再有）";
    }

    /** Chat suffix when failRefund returned {@code back > 0}. */
    static String failRefundGrantedSuffix(int back, int pct, boolean abyss) {
        return "§a · 今天第一次挑战失败：退还 " + back + " 体力（花费的 " + pct + "%，每天一次；不给掉落和币"
                + (abyss ? "，层费不退" : "") + "）";
    }

    /** Chat suffix when failRefund returned 0 (already used today). */
    static String failRefundAlreadyUsedSuffix() {
        return "§c（今天的失败退还已经用过，明天再有；未结算的额外奖励作废）";
    }

    /** Chat suffix when failRefund returned -1 (not eligible). */
    static String failRefundIneligibleSuffix() {
        return "§c（已开战不退体力；未结算的额外奖励作废）";
    }

    static String notEligibleText() {
        return ChatColor.RED + "本局没有你的结算资格（未参与战斗或中途离开）。";
    }

    static String bossClearedText(String bossName) {
        return "§a" + bossName + " 已击败 · 结算完成，实例稍后关闭";
    }

    /**
     * D283: one settlement line — counterplay counts + event/affix outcome. Empty when nothing to show.
     * Bukkit-free for unit tests. Verbs only; no numbers beyond counts.
     */
    /** D292: settle chat for variety bounty progress — always lead with visible +1 when the counter moved. */
    static String varietyBountyTip(java.util.List<String> vbDone, String progressLine) {
        StringBuilder sb = new StringBuilder("§e花样委托 §a+1");
        if (vbDone != null && !vbDone.isEmpty())
            sb.append(" §7· §a完成：").append(String.join("、", vbDone));
        if (progressLine != null && !progressLine.isEmpty())
            sb.append(" §7· ").append(progressLine);
        return sb.toString();
    }

    static String playfeelSummary(int wall, int whiff, int brk,
                                  boolean affixDone, String affixId,
                                  boolean eventRolled, boolean eventDone, String eventKind) {
        List<String> parts = new ArrayList<String>();
        if (wall > 0) parts.add("撞墙破绽 ×" + wall);
        if (whiff > 0) parts.add("落空破绽 ×" + whiff);
        if (brk > 0) parts.add("破招 ×" + brk);
        if (affixDone && affixId != null && !affixId.isEmpty())
            parts.add("词缀「" + EmberRunMaps.Variety.label(affixId) + "」✔");
        if (eventRolled) {
            String el = EmberRunMaps.Variety.eventLabel(eventKind == null ? "" : eventKind);
            parts.add(el + (eventDone ? " ✔" : " ✘"));
        }
        if (parts.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("§e本局：§f");
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) sb.append(" §7· §f");
            sb.append(parts.get(i));
        }
        return sb.toString();
    }

    /** D295: same summary line for fail/wipe — players need the lesson when they lose. */
    static String failPlayfeelLine(EmberRunSession s) {
        if (s == null) return "";
        return playfeelSummary(s.wallHits, s.whiffHits, s.breakHits,
                s.affixDone, s.affix, s.eventRoom != null && !s.eventRoom.isEmpty(), s.eventDone, s.eventKind);
    }



    /**
     * Append the post-base bonuses {@link #settleFor} adds after {@link EmberRunRules#settle}, in the same order
     * (raid → rot_mark → signature → pledge → fc_sigmark → variety → bounty). Bukkit-free so unit tests pin
     * grant key order and amounts. Honor / variety-bounty need live player state and stay in {@link #settleFor}.
     */
    static void appendBonuses(List<EmberRunRules.Grant> grants, EmberRunRules.SettleInput in, EmberRunMaps.MapDef m,
                              int raidItemQualityFloor, int tier, boolean rotation, int rotMarks,
                              boolean sigRepeat, EmberRunRules.Grant baseItem, int pledged,
                              boolean sigFc, boolean varietyEligible, boolean affixDone, int affixShard,
                              boolean eventDone, int eventCore, String eventKind,
                              List<EmberRunRules.Grant> bountyPaid) {
        appendBonuses(grants, in, m, raidItemQualityFloor, tier, rotation, rotMarks, sigRepeat, baseItem, pledged,
                sigFc, varietyEligible, affixDone, affixShard, eventDone, eventCore, eventKind, bountyPaid, false);
    }

    static void appendBonuses(List<EmberRunRules.Grant> grants, EmberRunRules.SettleInput in, EmberRunMaps.MapDef m,
                              int raidItemQualityFloor, int tier, boolean rotation, int rotMarks,
                              boolean sigRepeat, EmberRunRules.Grant baseItem, int pledged,
                              boolean sigFc, boolean varietyEligible, boolean affixDone, int affixShard,
                              boolean eventDone, int eventCore, String eventKind,
                              List<EmberRunRules.Grant> bountyPaid, boolean forceStamp) {
        if (m != null && m.raid)
            grants.addAll(EmberRaidService.settleGrants(in, m, raidItemQualityFloor, tier));
        if (rotation) grants.add(new EmberRunRules.Grant("rot_mark", EmberRunRules.Kind.MARK, String.valueOf(tier), rotMarks, null));
        if (sigRepeat && m != null) grants.addAll(EmberRunRules.signatureGrants(m.key, in, baseItem, forceStamp)); // D6433
        if (sigRepeat && m != null && pledged > 0) {
            EmberRunRules.Grant pg = EmberPledgeService.settleGrant(m.key, pledged);
            if (pg != null) grants.add(pg);
        }
        if (sigFc && m != null)
            grants.add(new EmberRunRules.Grant("fc_sigmark", EmberRunRules.Kind.SIGMARK, m.key, EmberSignature.fcMarks(), null));
        if (varietyEligible)
            grants.addAll(EmberRunRules.varietyGrants(in.firstClear != null, affixDone, affixShard, eventDone, eventCore, eventKind));
        if (bountyPaid != null) grants.addAll(bountyPaid);
    }

    /** Stable key list for assertions. */
    static List<String> grantKeys(List<EmberRunRules.Grant> grants) {
        List<String> keys = new ArrayList<String>(grants.size());
        for (EmberRunRules.Grant g : grants) keys.add(g.key);
        return keys;
    }

    /** Rotation mark amount for the settle path (challenge → rotationBonusMarks, else normal_bonus). */
    static int rotationMarkAmount(boolean challenge, int challengeBonus, int normalBonus) {
        return challenge ? challengeBonus : normalBonus;
    }

    /** D128: pct shown in label = round(failRefund * 100). */
    static int failRefundPct(double share) {
        return (int) Math.round(share * 100);
    }

    int failRefund(UUID u, EmberRunSession s) {
        double table = runs.maps().failRefund;
        if (table <= 0) return -1;
        PlayerData pd = runs.dataOf(u);
        int path = EmberFailPath.get(pd);
        double share = EmberFailPath.effectiveShare(path, table);
        Integer c = s.cost.get(u);
        int back = EmberRunRules.failRefundAmount(c == null ? 0 : c, share);
        StaminaService st = runs.plugin().getStaminaService();
        if (st == null) return -1;
        EmberRunRules.Ledger l = runs.store().ledger(u);
        EmberRunRules.Row cost = l.get(s.runId, "cost");
        if (cost == null || EmberRunRules.ST_RELEASED.equals(cost.status)) return -1;
        String day = town.sunshine.corerpg.DailyService.today();
        boolean[] created = new boolean[1];
        EmberRunRules.Row r = l.record(EmberRunRules.failRefundRun(day), EmberRunRules.FAIL_REFUND_KEY,
                "stamina:" + back + ":" + s.runId, EmberRunRules.ST_DELIVERED, System.currentTimeMillis(), created);
        if (!created[0]) {
            runs.log().info("[P1 run] fail refund " + u + " " + day + ": already used today (" + r.result + ")");
            return 0;
        }
        try {
            int boosted = EmberFailRefundFlatBoost.apply(pd, back); // D10753
            if (boosted > back) back = boosted;
        } catch (Throwable ignored) { }
        runs.store().saveLedger(u, Collections.singletonList(r));
        if (back > 0) st.releaseFlat(u, back);
        if (EmberFailPath.valid(path) && share != table) {
            runs.log().info("[P1 run] failpath " + EmberFailPath.key(path) + " " + u
                    + " share " + table + "→" + share + " back " + back);
        } else {
            runs.log().info("[P1 run] fail refund " + u + " " + day + " run " + s.runId + ": " + back + " stamina (cost " + c + ")");
        }
        if (path == EmberFailPath.SKIP && back == 0) return EmberFailPath.RESULT_SKIP;
        return back;
    }

    String failRefundLabel(UUID u) {
        if (runs.maps().failRefund <= 0) return "";
        int pct = failRefundPct(runs.maps().failRefund);
        boolean used = runs.store().ledger(u).get(EmberRunRules.failRefundRun(town.sunshine.corerpg.DailyService.today()), EmberRunRules.FAIL_REFUND_KEY) != null;
        return used ? failRefundUsedText() : failRefundAvailableText(pct, EmberRunRules.failRefundAmount(runs.maps().cost, runs.maps().failRefund));
    }

    void onBossKilled(EmberRunDirector d, LivingEntity boss, boolean byParticipant) {
        EmberRunSession s = d.s;
        if (!s.open() || EmberRunSession.SETTLING.equals(s.state)) return; // duplicate death event → nothing (E02)
        if (!byParticipant) {
            runs.onBroken(s, "首领非玩家击杀（" + (boss.getLastDamageCause() == null ? "?" : boss.getLastDamageCause().getCause()) + "）");
            return;
        }
        s.state = EmberRunSession.SETTLING;
        runs.store().save(s);
        if (d.bossSpawnedAt > 0) runs.log().info(String.format(Locale.ROOT, "[P1 run] %s boss killed after %.1f s", s.runId, (System.currentTimeMillis() - d.bossSpawnedAt) / 1000.0));
        EmberRunMaps.MapDef m = d.def;
        World w = d.w;
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            boolean present = !s.left.contains(u) && (p == null || !p.isOnline() || p.getWorld().equals(w));
            boolean ok = EmberRunRules.eligible(s.committed.contains(u), s.acted.contains(u), present, s.died.contains(u));
            if (!ok) {
                if (p != null) p.sendMessage(EmberRunService.P + notEligibleText());
                runs.log().info("[P1 run] " + s.runId + " not eligible " + u + " committed=" + s.committed.contains(u)
                        + " acted=" + s.acted.contains(u) + " present=" + present + " died=" + s.died.contains(u));
                continue;
            }
            if (m.event) { if (runs.festival() != null) runs.festival().onClear(s, u, runs.cosmetics()); } // D139: no main-line settlement
            else if (m.rush) runs.rush().settle(s, m, u); // D144: marks / 余烬徽 / title only
            else if (m.shortExpedition) runs.shortExpedition().settle(s, m, u); // D391 S40
            else settleFor(s, m, u);
        }
        s.state = EmberRunSession.COMPLETE;
        runs.store().save(s);
        runs.tellRun(s, bossClearedText(d.bossDef().name));
        if (!d.anomalies.isEmpty()) runs.log().warning("[P1 run] " + s.runId + " anomalies: " + d.anomalies);
        runs.endInstance(s, true);
    }

    /** D10081: variety bounty with soft need count. */
    static EmberRunRules.VarietyBounty softVb(EmberRunRules.VarietyBounty vb, int need) {
        if (vb == null || need == vb.count) return vb;
        return new EmberRunRules.VarietyBounty(vb.kind, need, vb.coin, vb.shard);
    }

    void settleFor(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = runs.dataOf(u);
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = s.runId;
        in.player = u.toString();
        in.seed = s.seed;
        in.tier = s.tier;
        in.target = s.targetOf(u);
        in.bossKilled = true;
        in.extra = s.extra;
        in.extraDone = s.extraDone;
        // §18.1: challenge runs never carry the first-clear package (first clears are per map + content version, normal)
        in.firstClear = s.challenge || m.raid || runs.firstCleared(pd, m) ? null : m.firstClear;
        // D648: first challenge clear of this map (talent C_CHAL rising edge) → map-identity base_item
        in.challengeFirst = s.challenge && s.abyss == 0 && !m.raid
                && pd.periodCount(EmberGrowthService.C_CHAL + m.key, "all") <= 0;
        // D654: first clear of this raid key
        in.raidFirst = m.raid && EmberCosmetics.raidClears(pd, m.key) <= 0;
        if (m.raid && runs.maps().challenge != null) in.qualityWeights = runs.maps().challenge.quality; // P2-5: no exclusive drop table
        if (s.challenge && runs.maps().challenge != null) in.qualityWeights = runs.maps().challenge.quality;
        EmberRunMaps.AbyssTier abT = s.abyss > 0 ? runs.maps().abyssTier(s.abyss) : null;
        if (abT != null) in.qualityWeights = abT.quality; // P2-2 tier quality table
        in.loot = runs.maps().lootBias(m); // P2-9 (D81); D654 raid also keeps bias for first-clear slot
        if (EmberSixSlot.enabled()) { // D318 六槽 T1-7: only the clears p1sim models (normal main-story settle; no challenge / abyss / raid / event / rush)
            boolean modelled = !s.challenge && s.abyss == 0 && !m.raid && !m.event && !m.rush;
            in.sixArmor = modelled;
            in.sixStarter = modelled && in.firstClear != null && "q01".equals(m.key);
        }
        List<EmberRunRules.Grant> grants = new ArrayList<EmberRunRules.Grant>(EmberRunRules.settle(in));
        try {
            Player tip = Bukkit.getPlayer(u);
            if (EmberBaseShardBoost.apply(pd, grants)) { // D10273
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a结算加码 · 碎片 +" + EmberBaseShardBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a结算加码 · 碎片"); } catch (Throwable ignored) { }
                }
            }
            if (EmberBaseBoneBoost.apply(pd, grants)) { // D10289
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a结算加码 · 骨头 +" + EmberBaseBoneBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a结算加码 · 骨头"); } catch (Throwable ignored) { }
                }
            }
            if (EmberBaseCoreBoost.apply(pd, grants)) { // D10305
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a结算加码 · 核心 +" + EmberBaseCoreBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a结算加码 · 核心"); } catch (Throwable ignored) { }
                }
            }
            if (EmberBaseXpBoost.apply(pd, grants)) { // D10337
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a结算加码 · 经验 +" + EmberBaseXpBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a结算加码 · 经验"); } catch (Throwable ignored) { }
                }
            }
            if (EmberBaseMarkBoost.apply(pd, grants)) { // D10353
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a结算加码 · 印记 +" + EmberBaseMarkBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a结算加码 · 印记"); } catch (Throwable ignored) { }
                }
            }
            if (EmberExtraEliteBoost.apply(pd, grants)) { // D10369
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a额外精英加码 · 奖励 ×2（今日一次）");
                    try { tip.sendActionBar("§a额外精英加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberExtraTreasureBoost.apply(pd, grants)) { // D10401
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a宝藏加码 · 余烬币 ×2（今日一次）");
                    try { tip.sendActionBar("§a宝藏加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberExtraChestFloorSoft.apply(pd, grants)) { // D10417
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a额外宝箱 · 成色≥精良（今日一次）");
                    try { tip.sendActionBar("§a额外宝箱成色底"); } catch (Throwable ignored) { }
                }
            }
            if (EmberFcBlankBoost.apply(pd, grants)) { // D10433
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a首通加码 · 空白纸 +" + EmberFcBlankBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a首通空白纸加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberFcShardBoost.apply(pd, grants)) { // D10465
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a首通加码 · 碎片 +" + EmberFcShardBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a首通碎片加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberFcCoreBoost.apply(pd, grants)) { // D10481
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a首通加码 · 核心 +" + EmberFcCoreBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a首通核心加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberFcCoinBoost.apply(pd, grants)) { // D10497
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a首通加码 · 余烬币 +" + EmberFcCoinBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a首通币加码"); } catch (Throwable ignored) { }
                }
            }
            if (EmberFcBoneBoost.apply(pd, grants)) { // D11009
                if (tip != null && tip.isOnline()) {
                    tip.sendMessage(EmberRunService.P + "§a首通加码 · 骨尘 +" + EmberFcBoneBoost.BONUS + "（今日一次）");
                    try { tip.sendActionBar("§a首通骨尘加码"); } catch (Throwable ignored) { }
                }
            }
        } catch (Throwable ignored) { }
        boolean dropQForced = false;
        boolean dropCForced = false;
        if (EmberDropQualityPity.applicable(s, m)) { // D611 drop quality pity · mainline base_item only
            dropQForced = EmberDropQualityPity.applyToGrants(pd, grants);
        }
        if (EmberDropCraftPity.applicable(s, m)) { // D6817 craft pity · orthogonal to quality
            dropCForced = EmberDropCraftPity.applyToGrants(pd, grants);
        }
        // P2-1 weekly challenge rotation: featured map, first 3 challenge clears of the week → +1 mark of the run tier
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        String week = EmberRunRules.rotationWeekKey(today);
        boolean rotation = s.challenge && s.abyss == 0 && m.key.equals(runs.featured(today)) && runs.maps().rotationBonusMarks > 0
                && pd.periodCount(EmberRunService.C_ROTATION, week) < runs.maps().rotationWeeklyCap;
        // D108: a repeat NORMAL clear of the featured T1/T2 map (Q01–Q06) → +normal_bonus_marks of the map tier; one
        // weekly counter with the challenge bonus (EmberRunService.C_ROTATION), so the week's featured bonus stays 3 clears in total
        boolean featNormal = !s.challenge && s.abyss == 0 && !m.raid && in.firstClear == null && s.tier < 3
                && m.key.equals(runs.featured(today)) && runs.maps().rotationNormalBonusMarks > 0
                && !runs.progressFlag(pd, "q07") // E-review #7: after the own Q07 first clear a normal repeat neither pays nor uses a challenge slot
                && pd.periodCount(EmberRunService.C_ROTATION, week) < runs.maps().rotationWeeklyCap;
        if (featNormal) rotation = true;
        int rotMarks = rotationMarkAmount(s.challenge, runs.maps().rotationBonusMarks, runs.maps().rotationNormalBonusMarks);
        try {
            if (rotation) {
                int soft = EmberRotMarkBoost.apply(pd, rotMarks); // D10161
                if (soft > rotMarks) {
                    rotMarks = soft;
                    Player tip = Bukkit.getPlayer(u);
                    if (tip != null && tip.isOnline()) {
                        tip.sendMessage(EmberRunService.P + "§a精选加码 · 印记 +" + EmberRotMarkBoost.BONUS + "（今日一次）");
                        try { tip.sendActionBar("§a精选加码"); } catch (Throwable ignored) { }
                    }
                }
            }
        } catch (Throwable ignored) { }
        if (m.raid) { // P2-5 + P2-9 (D82): one targeted T3 roll (floor 精良) + 1 T3 mark; titles / trail are cosmetic (D233 → EmberRaidService)
            grants.addAll(EmberRaidService.settleGrants(in, m, runs.maps().raidItemQualityFloor, s.tier));
            try {
                if (EmberRaidMarkBoost.apply(pd, grants)) { // D10241
                    Player tip = Bukkit.getPlayer(u);
                    if (tip != null && tip.isOnline()) {
                        tip.sendMessage(EmberRunService.P + "§a团本加码 · 印记 +" + EmberRaidMarkBoost.BONUS + "（今日一次）");
                        try { tip.sendActionBar("§a团本加码"); } catch (Throwable ignored) { }
                    }
                }
            } catch (Throwable ignored) { }
        }
        if (rotation) grants.add(new EmberRunRules.Grant("rot_mark", EmberRunRules.Kind.MARK, String.valueOf(s.tier), rotMarks, null));
        // D174 签名传奇: repeat NORMAL clear of a signature map → 1 insignia + maybe a signature stamp on the base item;
        // the map's first clear → the first-clear insignia, once per map (not per content version, C_FC)
        boolean sigRun = !s.challenge && s.abyss == 0 && !m.raid && !m.event && !m.rush && EmberSignature.hasMap(m.key);
        if (sigRun && in.firstClear == null && runs.progressFlag(pd, m.key)) {
            EmberRunRules.Grant base = null;
            for (EmberRunRules.Grant g : grants) if ("base_item".equals(g.key)) base = g;
            boolean forceSig = EmberSigStampPity.ready(pd); // D6433
            grants.addAll(EmberRunRules.signatureGrants(m.key, in, base, forceSig));
            try {
                for (int i = 0; i < grants.size(); i++) {
                    EmberRunRules.Grant g = grants.get(i);
                    if (g == null || !"sig_mark".equals(g.key)) continue;
                    int boosted = EmberSigMarkBoost.apply(pd, g.amount); // D10209
                    if (boosted > g.amount) {
                        grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, boosted, g.item));
                        Player tip = Bukkit.getPlayer(u);
                        if (tip != null && tip.isOnline()) {
                            tip.sendMessage(EmberRunService.P + "§a签名加码 · 徽记 +" + EmberSigMarkBoost.BONUS + "（今日一次）");
                            try { tip.sendActionBar("§a签名加码"); } catch (Throwable ignored) { }
                        }
                    }
                    break;
                }
            } catch (Throwable ignored) { }
            if (EmberMarkNearBreak.maybeBoost(pd, m.key, grants)) { // D6625
                Player cue2 = Bukkit.getPlayer(u);
                if (cue2 != null && cue2.isOnline()) {
                    cue2.sendMessage(EmberRunService.P + "§a徽记临门 · 本局徽记加倍（距烙印差1 · 每图一次）");
                    try { cue2.sendActionBar("§a徽记临门 · 加倍"); } catch (Throwable ignored) { }
                }
            }
            if (forceSig) {
                Player cue = Bukkit.getPlayer(u);
                if (cue != null && cue.isOnline()) {
                    try { cue.sendActionBar("§a签名软保底 · 本局必出盖章候选"); } catch (Throwable ignored) { }
                    cue.sendMessage(EmberRunService.P + "§a签名软保底触发 · 连空达期望局（零抬 STAMP_RATE）");
                }
            }
        }
        final int pledged = runs.pledge().count(s.modifier);
        if (sigRun && in.firstClear == null && runs.progressFlag(pd, m.key) && pledged > 0) { // D174 stage 2b 自选誓约: +1 insignia per pledged rule (D232 → EmberPledgeService)
            EmberRunRules.Grant pg = EmberPledgeService.settleGrant(m.key, pledged);
            if (pg != null) {
                try {
                    int boosted = EmberPledgeSettleBoost.apply(pd, pg.amount); // D10177
                    if (boosted > pg.amount) {
                        pg = new EmberRunRules.Grant(pg.key, pg.kind, pg.id, boosted, pg.item);
                        Player tip = Bukkit.getPlayer(u);
                        if (tip != null && tip.isOnline()) {
                            tip.sendMessage(EmberRunService.P + "§a誓约加码 · 徽记 +" + EmberPledgeSettleBoost.BONUS + "（今日一次）");
                            try { tip.sendActionBar("§a誓约加码"); } catch (Throwable ignored) { }
                        }
                    }
                } catch (Throwable ignored) { }
                grants.add(pg);
            }
        }
        final boolean sigFc = sigRun && in.firstClear != null && pd.periodCount(EmberSignature.C_FC + m.key, "all") <= 0;
        if (sigFc) {
            int fcAmt = EmberSignature.fcMarks();
            try {
                int boosted = EmberFcSigMarkBoost.apply(pd, fcAmt); // D10225
                if (boosted > fcAmt) {
                    fcAmt = boosted;
                    Player tip = Bukkit.getPlayer(u);
                    if (tip != null && tip.isOnline()) {
                        tip.sendMessage(EmberRunService.P + "§a首通加码 · 徽记 +" + EmberFcSigMarkBoost.BONUS + "（今日一次）");
                        try { tip.sendActionBar("§a首通加码"); } catch (Throwable ignored) { }
                    }
                }
            } catch (Throwable ignored) { }
            grants.add(new EmberRunRules.Grant("fc_sigmark", EmberRunRules.Kind.SIGMARK, m.key, fcAmt, null));
        }
        boolean affixBoost = false;
        if (!s.challenge && s.abyss == 0 && !m.raid) { // D138 repeat-run variety (rolled only when every member had the first clear)
            grants.addAll(EmberRunRules.varietyGrants(in.firstClear != null, s.affixDone, runs.maps().variety.affixShard, s.eventDone, runs.maps().variety.eventCore, s.eventKind));
            affixBoost = EmberAffixShardBoost.apply(pd, s.affixDone, grants); // D7585
            try {
                if (EmberEventCoreBoost.apply(pd, s.eventDone, grants)) { // D10145
                    Player tip = Bukkit.getPlayer(u);
                    if (tip != null && tip.isOnline()) {
                        tip.sendMessage(EmberRunService.P + "§a事件加赠 · 核心 +" + EmberEventCoreBoost.BONUS + "（今日一次）");
                        try { tip.sendActionBar("§a事件加赠"); } catch (Throwable ignored) { }
                    }
                }
            } catch (Throwable ignored) { }
        }
        EmberRunRules.Ledger l = runs.store().ledger(u);
        // P2-7 daily bounty (D79): the n-th settled clear of the stamina day; counted once per run (fresh = no base row yet)
        final boolean fresh = l.get(s.runId, "base_coin") == null;
        final String bDay = town.sunshine.corerpg.DailyService.today();
        final List<EmberRunRules.BountyTier> tiers = runs.bountyTiers();
        int bountyW = m.raid ? 2 : 1; // D105: a raid clear (50 stamina) counts as 2 runs toward the daily bounty
        try {
            if (fresh) {
                int softW = EmberBountyWeightBoost.apply(pd, bountyW, m.raid); // D10049
                if (softW > bountyW) {
                    bountyW = softW;
                    Player bp = Bukkit.getPlayer(u);
                    if (bp != null && bp.isOnline()) {
                        bp.sendMessage(EmberRunService.P + "§a委托加码 · 本局算 " + bountyW + " 局（今日一次）");
                        try { bp.sendActionBar("§a委托加码"); } catch (Throwable ignored) { }
                    }
                }
            }
        } catch (Throwable ignored) { }
        final int bountyPrev = fresh ? pd.periodCount(EmberRunService.C_BOUNTY, bDay) : 0;
        final int bountyN = fresh ? bountyPrev + bountyW : 0;
        List<EmberRunRules.Grant> bountyPaid = fresh ? EmberRunRules.bountyGrants(tiers, bountyPrev, bountyN) : Collections.<EmberRunRules.Grant>emptyList();
        grants.addAll(bountyPaid);
        // D144 花样委托: repeat normal runs only (where the variety rolls); counted once per run (fresh)
        final boolean varRun = fresh && !s.challenge && s.abyss == 0 && !m.raid && !m.event && in.firstClear == null;
        final List<EmberRunRules.VarietyBounty> vbs = varRun ? runs.varietyBounties() : Collections.<EmberRunRules.VarietyBounty>emptyList();
        final List<String> vbDone = new ArrayList<String>();
        boolean vbMoved = false;
        for (EmberRunRules.VarietyBounty vb : vbs) {
            boolean hit = "affix".equals(vb.kind) ? s.affixDone : s.eventDone;
            if (!hit) continue;
            int need = vb.count;
            int prev = pd.periodCount(EmberRunService.C_VBOUNTY + vb.kind, bDay);
            try {
                int soft = EmberVarietyBountyCountSoft.softCount(pd, vb.count); // D10081
                if (soft < vb.count && prev < vb.count) {
                    need = soft;
                    if (prev < soft && EmberVarietyBountyCountSoft.consume(pd)) {
                        Player tip = Bukkit.getPlayer(u);
                        if (tip != null && tip.isOnline()) {
                            tip.sendMessage(EmberRunService.P + "§a花样减负 · 需求 −1（今日一次 · " + vb.label() + "）");
                            try { tip.sendActionBar("§a花样减负"); } catch (Throwable ignored) { }
                        }
                    } else if (EmberVarietyBountyCountSoft.usedToday(pd)) {
                        need = soft;
                    }
                }
            } catch (Throwable ignored) { }
            List<EmberRunRules.Grant> g = EmberRunRules.varietyBountyGrants(
                    softVb(vb, need), prev, true);
            if (prev < need) { pd.addPeriodCount(EmberRunService.C_VBOUNTY + vb.kind, bDay, 1); vbMoved = true; }
            if (!g.isEmpty()) {
                try {
                    if (EmberVarietyBountyRewardBoost.ready(pd)) {
                        List<EmberRunRules.Grant> boosted = new ArrayList<EmberRunRules.Grant>();
                        boolean consumed = false;
                        for (EmberRunRules.Grant gr : g) {
                            int amt = EmberVarietyBountyRewardBoost.applyAmount(pd, gr.amount, !consumed); // D10097
                            if (amt > gr.amount) consumed = true;
                            boosted.add(new EmberRunRules.Grant(gr.key, gr.kind, gr.id, amt, gr.item));
                        }
                        if (consumed) {
                            g = boosted;
                            Player tip = Bukkit.getPlayer(u);
                            if (tip != null && tip.isOnline()) {
                                tip.sendMessage(EmberRunService.P + "§a花样加码 · 奖励 ×2（今日一次）");
                                try { tip.sendActionBar("§a花样加码"); } catch (Throwable ignored) { }
                            }
                        }
                    }
                } catch (Throwable ignored) { }
                grants.addAll(g);
                vbDone.add(vb.label() + "（" + vb.rewardText() + "）");
            }
        }
        try {
            if (fresh && !s.challenge && s.abyss == 0 && !m.raid && !m.event && !m.rush
                    && EmberMainlineCoinBoost.consume(pd)) { // D8417
                grants.add(new EmberRunRules.Grant("main_coin_boost", EmberRunRules.Kind.COIN, null, EmberMainlineCoinBoost.BONUS, null));
                Player mp = Bukkit.getPlayer(u);
                if (mp != null && mp.isOnline()) {
                    mp.sendMessage(EmberRunService.P + "§a主线加赠 · 余烬币 +§f" + EmberMainlineCoinBoost.BONUS + "§a（今日一次）");
                    try { mp.sendActionBar("§a主线加赠 · 币+" + EmberMainlineCoinBoost.BONUS); } catch (Throwable ignored) { }
                }
            }
            if (fresh && m.raid && EmberRaidCoinBoost.consume(pd)) { // D8465
                grants.add(new EmberRunRules.Grant("raid_coin_boost", EmberRunRules.Kind.COIN, null, EmberRaidCoinBoost.BONUS, null));
                Player rp = Bukkit.getPlayer(u);
                if (rp != null && rp.isOnline()) {
                    rp.sendMessage(EmberRunService.P + "§a团本加赠 · 余烬币 +§f" + EmberRaidCoinBoost.BONUS + "§a（今日一次）");
                    try { rp.sendActionBar("§a团本加赠 · 币+" + EmberRaidCoinBoost.BONUS); } catch (Throwable ignored) { }
                }
            }
        } catch (Throwable ignored) { }
        EmberGrowthService growth = EmberGrowthService.get(); // D142 余烬勋记: settlement coin % / +shards (own ledger rows)
        Player gp = Bukkit.getPlayer(u);
        if (growth != null && gp != null) {
            int coins = 0;
            for (EmberRunRules.Grant g : grants) if (g.kind == EmberRunRules.Kind.COIN) coins += g.amount;
            int hc = growth.honorCoin(gp, coins), hs = growth.honorShard(gp);
            try {
                int boosted = EmberHonorCoinBoost.apply(pd, hc); // D10113
                if (boosted > hc) {
                    hc = boosted;
                    Player tip = Bukkit.getPlayer(u);
                    if (tip != null && tip.isOnline()) {
                        tip.sendMessage(EmberRunService.P + "§a勋记加码 · 余烬币 +" + EmberHonorCoinBoost.BONUS + "（今日一次）");
                        try { tip.sendActionBar("§a勋记加码"); } catch (Throwable ignored) { }
                    }
                }
            } catch (Throwable ignored) { }
            if (hc > 0) grants.add(new EmberRunRules.Grant("honor_coin", EmberRunRules.Kind.COIN, null, hc, null));
            if (hs > 0) grants.add(new EmberRunRules.Grant("honor_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, hs, null));
        }
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        long now = System.currentTimeMillis();
        boolean[] created = new boolean[1];
        for (EmberRunRules.Grant g : grants) {
            String st = g.kind == EmberRunRules.Kind.CHOICE ? EmberRunRules.ST_AWAIT : EmberRunRules.ST_PENDING;
            EmberRunRules.Row r = l.record(s.runId, g.key, g.encode(), st, now, created);
            if (created[0]) changed.add(r);
            if (created[0] && "rot_mark".equals(g.key)) pd.addPeriodCount(EmberRunService.C_ROTATION, week, 1); // counted once per run (ledger key)
            boolean raidMoved = EmberRaidService.applyClearCount(pd, m, week, g.key, created[0]); // P2-5 weekly cap (P2-6: per cap_group), D233
            if (raidMoved && m.raid) {
                Player rp = Bukkit.getPlayer(u);
                if (rp != null && rp.isOnline()) {
                    int used = EmberRaidService.weekCount(pd, m, week);
                    boolean wasFc = EmberCosmetics.raidClears(pd, m.key) <= 1; // onRaidClear may not have run yet
                    EmberRaidLoop.afterWeeklyClear(rp, m, used, m.weeklyCap, wasFc);
                }
            }
            if (created[0] && "fc_sigmark".equals(g.key)) pd.addPeriodCount(EmberSignature.C_FC + m.key, "all", 1); // D174: once per map
        }
        if (fresh) pd.addPeriodCount(EmberRunService.C_BOUNTY, bDay, bountyW);
        if (fresh && m.raid && runs.cosmetics() != null) {
            boolean raidFc = EmberCosmetics.raidClears(pd, m.key) <= 0;
            runs.cosmetics().onRaidClear(Bukkit.getPlayer(u), pd, m.key); // P2-9 (D83)
            if (raidFc) {
                Player rp = Bukkit.getPlayer(u);
                if (rp != null && rp.isOnline()) {
                    String fam = (m.lootFamily != null && EmberRunRules.validFamily(m.lootFamily))
                            ? EmberItemData.familyName(m.lootFamily) : "本团偏向族";
                    rp.sendMessage(EmberRunService.P + "§d团本首通 · §f" + m.name + " §7保底 §e" + fam
                            + " §8（团本图身份）");
                    try { rp.sendActionBar("§d团本首通 · §f" + fam + " §7保底"); } catch (Throwable ignored) { }
                    ConfirmTokens.sendButtons(rp, EmberRunService.P + "§7下一步：", // D672
                            new String[]{"[工坊]", "/corerpg p1 forge", "养成团本件", "GOLD"},
                            new String[]{"[赛季页]", "/ember_p1_season", "团本周目标", "LIGHT_PURPLE"},
                            new String[]{"[冒险页]", "/ember_p1_adventure", "下一团本", "GREEN"});
                }
            }
        }
        if (fresh && runs.season() != null && s.coreClean.contains(u)) runs.season().addGoal(u, pd, "core", 1); // D144 烬核同心 (optional goal)
        boolean chalFirstNow = s.challenge && s.abyss == 0 && pd.periodCount(EmberGrowthService.C_CHAL + m.key, "all") == 0;
        if (chalFirstNow) {
            pd.addPeriodCount(EmberGrowthService.C_CHAL + m.key, "all", 1); // D141: challenge first clear per map (talent point / honors)
            // D648: visible challenge-FC acquisition feel
            Player chp = Bukkit.getPlayer(u);
            if (chp != null && chp.isOnline()) {
                String fam = (in.loot != null && EmberRunRules.validFamily(in.loot.family))
                        ? EmberItemData.familyName(in.loot.family) : "本图偏向族";
                chp.sendMessage(EmberRunService.P + "§c挑战首通 · §f" + m.name + " §7保底 §e" + fam
                        + " §8（挑战图身份 · 专精点进度）");
                try { chp.sendActionBar("§c挑战首通 · §f" + fam + " §7保底"); } catch (Throwable ignored) { }
                ConfirmTokens.sendButtons(chp, EmberRunService.P + "§7下一步：", // D671 TIP-DA D13332 工坊→菜单；主钮开薄页
                        new String[]{EmberChallengeSettle.SETTLE_NEXT_LOOP_LABEL, EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD,
                                "再挑战/选图/路径/工坊", "GREEN"},
                        new String[]{"[工坊]", EmberChallengeSettle.SETTLE_FORGE_CMD, "养成这件", "GOLD"},
                        new String[]{"[冒险页]", EmberChallengeSettle.SETTLE_ADVENTURE_CMD, "下一张挑战", "AQUA"});
            }
        }
        if (s.challenge && s.abyss == 0) { // D762
            Player chp2 = Bukkit.getPlayer(u);
            if (chp2 != null && chp2.isOnline()) {
                EmberChallengePath.afterSettle(chp2, pd, true, chalFirstNow, m.key);
                try { EmberChallengeCoreBoost.maybeGift(chp2, pd); } catch (Throwable ignored) { } // D8337
                try { EmberChallengeShardBoost.maybeGift(chp2, pd); } catch (Throwable ignored) { } // D10817
            }
        }
        if (in.firstClear != null) {
            boolean skillNew = !EmberFirstClear.fact(pd, m.key); // D459: announce only on first fact write
            EmberFirstClear.record(pd, m.key, m.contentVersion); // §9.4 + D205: package once per content version; fact @all never deleted
            if (runs.cosmetics() != null) runs.cosmetics().onFirstClear(Bukkit.getPlayer(u), m.key); // D103 milestone titles
            if (skillNew) {
                Player skp = Bukkit.getPlayer(u);
                if (skp != null && skp.isOnline()) EmberSkillUnlock.announce(skp, m.key);
            }
            // D641: first-clear map-identity piece is guaranteed — one visible line (not a PAPI twin)
            if (in.loot != null && EmberRunRules.validFamily(in.loot.family)) {
                Player idp = Bukkit.getPlayer(u);
                if (idp != null && idp.isOnline()) {
                    String fam = EmberItemData.familyName(in.loot.family);
                    String slot = in.loot.slot == null ? "刃/护符" : EmberItemData.slotName(in.loot.slot);
                    idp.sendMessage(EmberRunService.P + "§6图身份首通 · §f" + m.name + " §7保底 §e" + fam + " · " + slot
                            + " §8（本图特色件，可见追）");
                    try { idp.sendActionBar("§6图身份 · §f" + fam + " §7首通保底"); } catch (Throwable ignored) { }
                    EmberIdentitySettle.remember(pd, m.key, in.loot.family, in.loot.slot); // TIP-DC D13334 settle 图/族/槽
                    ConfirmTokens.sendButtons(idp, EmberRunService.P + "§7下一步：", // D673 TIP-DC D13334 下一环→薄页；工坊→菜单
                            new String[]{EmberIdentitySettle.SETTLE_NEXT_LOOP_LABEL, EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD,
                                    "工坊/装备/冒险/图装追", "GOLD"},
                            new String[]{"[工坊]", EmberIdentitySettle.SETTLE_FORGE_CMD, "养成特色件", "GOLD"},
                            new String[]{"[装备页]", EmberIdentitySettle.SETTLE_GEAR_CMD, "穿上同族", "AQUA"});
                }
            }
        }
        boolean newBest = false;
        int oldBest = runs.abyssBest(pd);
        if (s.abyss > 0) {
            EmberAbyssService.FloorResult floor = EmberAbyssService.applyFloorGrant(pd, s.abyss); // P2-2: opens tier + 1
            newBest = floor.newBest;
            oldBest = floor.oldBest;
            EmberAbyssSettle.remember(pd, s.abyss); // TIP-DB D13333 settle 层覆盖
        }
        if (newBest && runs.cosmetics() != null) runs.cosmetics().onAbyssBest(Bukkit.getPlayer(u), oldBest, s.abyss); // P2-9 (D83)
        if (newBest) {
            try {
                Player bp = Bukkit.getPlayer(u);
                if (bp != null && bp.isOnline())
                    EmberAbyssNewBestBone.maybeGift(bp, pd); // D8257
                    try { EmberAbyssNewBestShard.maybeGift(bp, pd); } catch (Throwable ignored) { } // D10801
            } catch (Throwable ignored) { }
        }
        if (growth != null && gp != null) growth.refreshHonors(gp); // D142: one-time unlock notice
        // D623 W2a soft pity: variety-eligible repeat settle updates dry streak
        boolean varietyEligibleSettle = s.varietyRolled && in.firstClear == null
                && !s.challenge && s.abyss == 0 && !m.raid && !m.rush && !m.event;
        if (fresh && varietyEligibleSettle) {
            EmberEventSoftPity.onSettle(pd, true, s.eventRoom != null && !s.eventRoom.isEmpty());
        }
        // D608 gem Stage3 clear-pity (Q01–Q07 only · no AFK/MM raise)
        if (gp != null && gp.isOnline()) EmberGemAcquire.onClear(gp, pd, m, s, runs.plugin());
        // D1323: drop quality soft-floor remain（非 hope celebration PAPI）
        if (gp != null && gp.isOnline() && EmberDropQualityPity.applicable(s, m)) {
            if (dropQForced) {
                gp.sendMessage(EmberRunService.P + "§a成色软保底触发 · §f本次主线装≥精良");
                try { gp.sendActionBar("§a成色软保底 · §f精良底"); } catch (Throwable ignored) { }
            } else {
                String rem = EmberDropQualityPity.remainLine(pd);
                gp.sendMessage(EmberRunService.P + "§7掉落成色 · §f" + rem);
                try { gp.sendActionBar("§e掉落成色 · §f" + rem); } catch (Throwable ignored) { }
            }
        }
        if (gp != null && gp.isOnline() && EmberDropCraftPity.applicable(s, m)) { // D6817 / D12410–D12413 TIP-Q
            if (dropCForced) {
                gp.sendMessage(EmberRunService.P + "§a精工软保底触发 · §f本次主线装≥精工1");
                try { gp.sendActionBar("§a精工软保底 · §f精工1底"); } catch (Throwable ignored) { }
            } else if (EmberDropCraftPity.settleRemainEnabled()) {
                String rem = EmberDropCraftPity.remainLine(pd);
                gp.sendMessage(EmberRunService.P + "§7掉落精工 · §f" + rem);
                try { gp.sendActionBar("§e掉落精工 · §f" + rem); } catch (Throwable ignored) { }
            }
        }
        if (gp != null && gp.isOnline() && affixBoost) {
            gp.sendMessage(EmberRunService.P + "§a词缀碎片加赠 · §f+" + EmberAffixShardBoost.BONUS + "（花样词缀 · 本日一次）");
            try { gp.sendActionBar("§a词缀碎片 · +" + EmberAffixShardBoost.BONUS); } catch (Throwable ignored) { }
        }
        if (runs.leaderboard() != null && (s.abyss > 0 || fresh)) { // P2-10 (D84); abyss: idempotent, also lists older records
            String nm = Bukkit.getOfflinePlayer(u).getName();
            if (s.abyss > 0) runs.leaderboard().abyssBest(u, nm, runs.abyssBest(pd));
            if (fresh && s.challenge && s.abyss == 0 && m.key.equals(runs.featured(today))) runs.leaderboard().featuredClear(u, nm, week);
        }
        if (runs.season() != null && fresh) { // D116 season boards + D117 weekly goals (display / cosmetic currency only)
            String nm = Bukkit.getOfflinePlayer(u).getName();
            if (s.abyss > 0) { // F-review #4: the clear time breaks ties on the abyss board
                long a0 = s.fightStart > 0 ? s.fightStart : s.created;
                runs.season().onAbyss(u, nm, s.abyss, a0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - a0) / 1000L) : 0);
                runs.season().addGoal(u, pd, "abyss", 1);
            }
            if (s.challenge && s.abyss == 0 && m.key.equals(runs.featured(today))) { runs.season().onFeatured(u, nm); runs.season().addGoal(u, pd, "featured", 1); }
            if (m.raid) {
                long t0 = s.fightStart > 0 ? s.fightStart : s.created;
                runs.season().onRaid(u, nm, m.key, t0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - t0) / 1000L) : 0);
                runs.season().addGoal(u, pd, "raid", 1);
            }
            int topClears = tiers.isEmpty() ? 0 : tiers.get(tiers.size() - 1).clears;
            if (topClears > 0 && bountyPrev < topClears && bountyN >= topClears) runs.season().addGoal(u, pd, "bounty", 1);
        }
        runs.store().saveLedger(u, changed);
        runs.plugin().getDataStore().flushMutation(u);
        runs.log().info("[P1 run] " + s.runId + " settle " + u + " rows+" + changed.size() + (in.firstClear != null ? " (first clear)" : "")
                + (s.abyss > 0 ? " (abyss " + s.abyss + ")" : s.challenge ? " (challenge T" + s.tier + ")" : ""));
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline() && s.abyss > 0) {
            p.sendMessage(EmberRunService.P + "§5深渊第 " + s.abyss + " 层 已完整通关" + (newBest ? " §a· 新纪录，开放第 " + runs.abyssMaxStart(pd) + " 层" : "")
                    + " §7（下一层需重新确认与付费：冒险页 → 深渊）");
            if (newBest) { // D675 TIP-DB D13333 下一环→薄页；工坊→菜单
                try { p.sendActionBar("§5深渊新纪录 · §f第 " + s.abyss + " 层"); } catch (Throwable ignored) { }
                ConfirmTokens.sendButtons(p, EmberRunService.P + "§7下一步：",
                        new String[]{EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL, EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD,
                                "再深渊/选层/目标/工坊", "LIGHT_PURPLE"},
                        new String[]{"[冒险页]", EmberAbyssSettle.SETTLE_ADVENTURE_CMD, "回冒险", "GREEN"},
                        new String[]{"[工坊]", EmberAbyssSettle.SETTLE_FORGE_CMD, "花层奖", "GOLD"});
            }
            // D649: first clear of layer 1 — gift was free fee; celebrate once on rising best
            if (newBest && s.abyss == 1) {
                p.sendMessage(EmberRunService.P + "§5首层礼兑现 · §f第 1 层免费通关 §a· 第 2 层已开放");
                try { p.sendActionBar("§5首层礼 · §a已兑现"); } catch (Throwable ignored) { }
            }
            if (newBest) EmberAbyssTarget.afterBest(p, pd, s.abyss); // D696 TIP-DB 菜单优先
            if (newBest && EmberAbyssTarget.get(pd) <= 0) { // D712 TIP-DB 禁教手打 abysstarget
                p.sendMessage(EmberRunService.P + "§7可设深渊层目标 · 点下一环设目标");
            }
        }
        if (p != null && p.isOnline() && rotation) {
            int rotNow = pd.periodCount(EmberRunService.C_ROTATION, week);
            int rotCap = runs.maps().rotationWeeklyCap;
            p.sendMessage(EmberRunService.P + "§b本周精选" + (s.challenge ? "挑战" : "重打") + " §f" + m.name + "§b：额外 T" + s.tier + " 锻造印记 +" + rotMarks
                    + "§7（本周 " + rotNow + "/" + rotCap + "）");
            // D771: hard-path glance when featured challenge hit
            if (s.challenge) {
                int cpath = EmberChallengePath.get(pd);
                if (EmberChallengePath.valid(cpath))
                    p.sendMessage(EmberRunService.P + "§c硬本路径 · 精选挑战入账 §8· " + EmberChallengePath.label(cpath));
            }
            // D782: week-full → path glance + change (mirror raid D758)
            if (rotCap > 0 && rotNow >= rotCap) {
                int cpath = EmberChallengePath.get(pd);
                p.sendMessage(EmberRunService.P + "§e精选周帽已满 §8· "
                        + (EmberChallengePath.valid(cpath) ? EmberChallengePath.label(cpath) : "未选硬本路径"));
                try { p.sendActionBar("§e精选周帽已满"); } catch (Throwable ignored) { }
                ConfirmTokens.sendButtons(p, EmberRunService.P + "§7下一步：", // D794
                        new String[]{"[换精选路径]", "/corerpg p1 featurepath", "挑战/普通精选", "GOLD"},
                        new String[]{"[换硬本路径]", "/corerpg p1 challengepath", "改挑战/普通偏好", "AQUA"},
                        new String[]{"[冒险页]", "/ember_p1_adventure", "换图继续", "GREEN"});
            }
        }
        // D790 weekly featured settle path
        if (p != null && p.isOnline() && s.abyss == 0 && !m.raid) {
            String feat = runs.featured(today);
            if (feat != null && feat.equalsIgnoreCase(m.key))
                EmberFeaturedPath.afterSettle(p, pd, true, s.challenge, rotation, m.key);
        }
        if (p != null && p.isOnline() && vbMoved) { // D144 / D292
            p.sendMessage(EmberRunService.P + varietyBountyTip(vbDone, runs.varietyBountyLine(pd)));
            EmberVarietyLoop.afterVarietyDone(p, vbDone); // D662
        }
        if (p != null && p.isOnline() && fresh && !tiers.isEmpty()) {
            p.sendMessage(EmberRunService.P + "§e每日委托 §7" + (bountyW > 1 ? "§7团本算 " + bountyW + " 局 · " : "") + (bountyPaid.isEmpty() ? "" : "§a完成第 " + bountyN + " 局档 §7· ")
                    + EmberRunRules.bountyLine(tiers, bountyN));
        }
        if (p != null && p.isOnline() && fresh) // D514 daily clear-bounty path chase
            EmberDailyPath.tellAfterSettle(p, pd, runs, tiers, bountyN);
        if (p != null && p.isOnline()) {
            // D682: map-farm chase hit when a grant item family matches map loot identity
            if (m.lootFamily != null) {
                boolean farmHit = false;
                for (EmberRunRules.Grant g : grants) {
                    if (g == null || g.item == null) continue;
                    String hit = EmberMapFarmCue.hitLine(m.lootFamily, g.item.family, g.item.slot);
                    if (hit != null) {
                        farmHit = true;
                        p.sendMessage(EmberRunService.P + hit);
                        try { p.sendActionBar("§a图装追到 · §f" + EmberItemData.familyName(g.item.family)); } catch (Throwable ignored) { }
                        try { EmberMapFarmHitBone.maybeGift(p, pd); } catch (Throwable ignored) { } // D11841
                        // D789: farm hit → equip chase if focus/loadout gap
                        if (runs.loadouts() != null) {
                            EmberLoadout hitLo = runs.loadouts().get(p);
                            EmberSetChase.tell(p, pd, hitLo, runs.loadouts());
                        }
                        EmberMapFarmCue.afterHit(p, m); // D833
                        break;
                    }
                }
                if (!farmHit) {
                    try { EmberMapFarmMissShard.maybeGift(p, pd); } catch (Throwable ignored) { } // D12129
                }
            }
            String feel = playfeelSummary(s.wallHits, s.whiffHits, s.breakHits,
                    s.affixDone, s.affix, !s.eventRoom.isEmpty(), s.eventDone, s.eventKind);
            if (!feel.isEmpty()) p.sendMessage(EmberRunService.P + feel);
            EmberClaimPath.settleDeliver(p, pd, runs); // D515 claim path
            EmberCodexPath.maybeAfterProgress(p, runs); // D522 codex path
            // D687: after deliver, refresh chase vs worn (1 tick so bag/loadout settle)
            final EmberRunMaps.MapDef farmMap = m;
            Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> {
                if (!p.isOnline()) return;
                EmberLoadout lo2 = runs.loadouts() == null ? null : runs.loadouts().refresh(p);
                EmberMapFarmCue.afterSettle(p, farmMap, lo2);
                EmberBagSeat.scheduleScan(p); // D5665
                EmberBagBetter.scheduleScan(p); // D6177
                EmberCodexBreak.schedule(p); // D6465
                EmberGrowthService eg = EmberGrowthService.get();
                EmberLoadout loRest = runs.loadouts() == null ? null : runs.loadouts().refresh(p);
                EmberForgeChargeRest.maybeAfterSettle(p, pd, loRest, eg); // D6561

                if (runs.loadouts() != null) // D747 setchase equip after deliver
                    EmberSetChase.afterEquip(p, pd, runs.loadouts());
            }, 5L);
            Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> {
                if (p.isOnline()) EmberStashPath.maybeAfterProgress(p); // D526 stash path
                if (p.isOnline()) EmberJunkPath.maybeAfterProgress(p); // D531 junk path
                if (p.isOnline()) EmberScrapPath.maybeAfterProgress(p); // D538 scrap path
                if (p.isOnline()) EmberDosePath.maybeAfterProgress(p); // D539 dose path
                if (p.isOnline()) EmberBankPath.maybeAfterProgress(p); // D540 bank path
                if (p.isOnline()) EmberCookPath.maybeAfterProgress(p); // D543 cook path
                if (p.isOnline()) EmberBitePath.maybeAfterProgress(p); // D544 bite path
                if (p.isOnline()) EmberRodPath.maybeAfterProgress(p); // D545 rod path
                if (p.isOnline()) EmberBrewPath.maybeAfterProgress(p); // D546 brew path
                if (p.isOnline()) EmberBreadPath.maybeAfterProgress(p); // D547 bread path
                if (p.isOnline()) EmberArmorPath.maybeAfterProgress(p); // D549 armor path
                if (p.isOnline()) EmberCharmPath.maybeAfterProgress(p); // D551 charm path
            }, 40L);
            if (in.firstClear != null && runs.maps().challenge != null && m.key.equals(runs.maps().challenge.requires)) endOfP1(p);
        }
        // D298 W1a(+R): playfeel telemetry — clear path (vbMoved = variety bounty progress)
        EmberPlayfeelTelemetry.record(s, m, u, pd, true, vbMoved, runs.log(), runs.plugin().getDataFolder());
        runs.plugin().getDataStore().flushMutation(u);
    }

    private void endOfP1(Player p) {
        p.sendMessage(EmberRunService.P + "§6§l余烬主线完结§r §7— 已首通最后一张主线图 Q07。");
        p.sendMessage(EmberRunService.P + "§a已开放：§fT3 定向锻造§7（工坊）· §fT2→T3 升阶§7 · §f七图挑战版、深渊、团本§7（冒险页，掉落 T3 与 T3 印记）");
        p.sendMessage(EmberRunService.P + "§7长线目标：同族 T3 两件套 +9 = 觉醒III（装备页看成套进度）");
        // D104 (midgame #2): the challenge is tuned for T3 — say how to get there before the first attempt
        p.sendMessage(EmberRunService.P + "§e挑战版按 T3 装备来调。§7刚首通：先用 T3 印记兑换（或升阶）把刃换到 T3，再到工坊「互换」免费把强化挪过去；"
                + "七张挑战图强度相同，只是掉落偏向的族 / 部位不同。");
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (runs.season() != null) { runs.season().markGraduated(d); runs.flushData(p.getUniqueId()); } // D134: the bounty goal is prorated in the graduation week
        if (runs.season() != null && runs.season().goalsOn()) {
            // F-review #8: today's daily bounty counts for the week even when it was finished before the Q07 clear
            List<EmberRunRules.BountyTier> tiers = runs.bountyTiers();
            int topClears = tiers.isEmpty() ? 0 : tiers.get(tiers.size() - 1).clears;
            if (topClears > 0 && d.periodCount(EmberRunService.C_BOUNTY, town.sunshine.corerpg.DailyService.today()) >= topClears && runs.season().progress(d, "bounty") == 0)
                runs.season().addGoal(p.getUniqueId(), d, "bounty", 1);
            // F-review #3: the weekly goals and the season are the reason to come back — say so at graduation
            p.sendMessage(EmberRunService.P + "§d周目标和赛季已开放：§7" + runs.goalsShort(d) + "（每周 4 个，完成得余烬徽换外观；赛季榜 4 周一季）");
        }
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, EmberRunService.P, new String[]{"[打开冒险页]", "/ember_p1_adventure", "挑战版 / 深渊 / 团本都在这里", "GREEN"},
                new String[]{"[赛季 · 周目标]", "/ember_p1_season", "本周目标、排行榜、赛季奖励、外观商店", "LIGHT_PURPLE"});
        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

}
