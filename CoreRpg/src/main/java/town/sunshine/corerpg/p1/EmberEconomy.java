package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import town.sunshine.corerpg.PlayerData;

/**
 * D213 registry + D215–D218/D221/D223 (ARCH S2-2…S2-6) grant/spend routes: every P1 economy <b>source</b> (S01–S32),
 * closed <b>legacy source</b> (L-S1…L-S5) and <b>sink</b> (C01–C18) from
 * {@code docs/design/REG-ember-source-sink-cap-2026-10-05.md}.
 * <p>Lookups stay Bukkit-free. {@link #amount} reads {@code ember-v1-economy.yml} (SoT) for routed rows (settle S01–S03,
 * shop C14, signin S23 daily/fallback/makeup, online S24 totals, festival C18 goldens). {@link #grantCoin} /
 * {@link #grantMark} / {@link #grantXp} / {@link #grantMat} / {@link #spendCoin} / {@link #spendMark} /
 * {@link #spendInsignia} / {@link #spendMat} / {@link #spendBadge} / {@link #spendFestCoin} /
 * {@link #spendCoinDelivery} are the tagged entry points (PlayerData only where possible — no Bukkit). D218 (S2-4):
 * workshop C03–C13. D221 (S2-5): festival shop C18 spends + AFK S22 grant entry via {@link #sourceForGrant}
 * {@code p1afk-} run id. D223 (S2-6): EmberDelivery coin debits via {@link #spendCoinDelivery} +
 * {@link #sinkForDeliveryRequest}; E1 {@code ember-v1-economy.yml} is the {@link #amount} source of truth (D224):
 * startup / classpath {@link #loadEconomyYml}; fail-closed if missing/corrupt; Java {@code golden} stays a secondary assert
 * (drift → SEVERE / refuse). Unrouted paths still call {@code PlayerData.addCoin}/{@code takeCoin} directly.
 * <p>{@code EmberEconomyTest} pins loaded yml amounts to Java golden, proves settle / shop / sign / fest / AFK / delivery /
 * grant* routing, scans {@code p1/} for direct {@code addCoin}/{@code takeCoin} outside the allowlist, and fails on drift.
 * Amounts unchanged from bv57; balance_version tracks runs (60 = D296 event mustfeel; was 58 D227).
 * D229 (S2-9): abyss floor settle tags as S13 via {@link #sourceForGrant} (run-id head {@code <map>a<n>});
 * vault write scan in {@code EmberEconomyTest} (callers must {@code grantMat} first or tag {@code econ-ok:}).
 * D243 (S4-2): S33 codex stage coin (run {@code codex} → tagged grantCoin), S34 chest extra gear roll, S35 starter kit,
 * S09 {@code per_rule} yml key — amounts unchanged (tags / registry only); docs/design/ember-source-map.yml lists them.
 * D244 (S4-3, G10): S36 fishing (CoreFish), S37 gacha tickets, S38 gacha pulls (cosmetics), C19 gacha pull cost —
 * registry rows only (other plugins pay / take them; no goldens, nothing routes here); stocks in ember-source-map.yml.
 */
public final class EmberEconomy {
    /** What a row pays or takes. */
    public enum Account {
        COIN, XP, SHARD, CORE, BONE, BLANK, BOUND_MAT, MARK, INSIGNIA, BADGE, STAMINA, POTION, GEAR, TITLE,
        FEST_COIN, LIFE_ITEM, GACHA_TICKET, COSMETIC, MAIL
    }

    /** How often it can pay (REG §0 column 周期). */
    public enum Period { RUN, DAY, WEEK, MONTH, ALL, ONCE, EVENT, NONE }

    /** Offline model coverage (REG §5): FULL ✓, PARTIAL △, NONE ✗, OUT = deliberately outside the power model. */
    public enum Model { FULL, PARTIAL, NONE, OUT }

    /** One source or sink row. */
    public static final class Row {
        public final String id;            // S01 … / C01 … / LS1 …
        public final boolean sink;
        public final boolean legacy;       // closed while P1 is on (S0-1…S0-5); must stay 0 for non-OP players
        public final String name;
        public final String owner;         // class / config that pays or takes it today
        public final Set<Account> accounts;
        public final Period period;
        public final List<String> counters; // EmberCounters family keys used as cap / account / claim (may be empty)
        public final List<String> ledgers;  // ledger row kinds that are not PlayerData counters (cr_p1_txn / ledger)
        public final Model model;
        public final Map<String, Double> golden; // amounts the test checks against live constants / yml

        Row(String id, boolean sink, boolean legacy, String name, String owner, Set<Account> accounts, Period period,
            List<String> counters, List<String> ledgers, Model model, Map<String, Double> golden) {
            this.id = id; this.sink = sink; this.legacy = legacy; this.name = name; this.owner = owner;
            this.accounts = Collections.unmodifiableSet(accounts); this.period = period;
            this.counters = Collections.unmodifiableList(counters); this.ledgers = Collections.unmodifiableList(ledgers);
            this.model = model; this.golden = Collections.unmodifiableMap(golden);
        }

        public double golden(String k) {
            Double v = golden.get(k);
            if (v == null) throw new IllegalArgumentException(id + " has no golden amount " + k);
            return v;
        }

        @Override public String toString() { return id + " " + name + " [" + period + ", " + model + "]"; }
    }

    private static final Map<String, Row> BY_ID = new LinkedHashMap<String, Row>();

    private static final Account COIN = Account.COIN, XP = Account.XP, SHARD = Account.SHARD, CORE = Account.CORE,
        BONE = Account.BONE, BLANK = Account.BLANK, MARK = Account.MARK, INS = Account.INSIGNIA, BADGE = Account.BADGE,
        STA = Account.STAMINA, POT = Account.POTION, GEAR = Account.GEAR;
    private static final Model FULL = Model.FULL, PART = Model.PARTIAL, NONE = Model.NONE, OUT = Model.OUT;

    /** builder for one row */
    private static final class B {
        final String id; final boolean sink; final boolean legacy; final String name; final String owner; final Period p;
        final EnumSet<Account> acc = EnumSet.noneOf(Account.class);
        final List<String> counters = new ArrayList<String>(), ledgers = new ArrayList<String>();
        final Map<String, Double> golden = new LinkedHashMap<String, Double>();
        Model model = NONE;
        B(String id, boolean sink, boolean legacy, String name, String owner, Period p) {
            this.id = id; this.sink = sink; this.legacy = legacy; this.name = name; this.owner = owner; this.p = p;
        }
        B acc(Account... a) { acc.addAll(Arrays.asList(a)); return this; }
        B keys(String... k) { counters.addAll(Arrays.asList(k)); return this; }
        B ledger(String... k) { ledgers.addAll(Arrays.asList(k)); return this; }
        B model(Model m) { model = m; return this; }
        B g(String k, double v) { golden.put(k, v); return this; }
        void done() {
            if (BY_ID.put(id, new Row(id, sink, legacy, name, owner, acc, p, counters, ledgers, model, golden)) != null)
                throw new IllegalStateException("duplicate economy row " + id);
        }
    }

    private static B src(String id, String name, String owner, Period p) { return new B(id, false, false, name, owner, p); }
    private static B old(String id, String name, String owner) { return new B(id, false, true, name, owner, Period.NONE); }
    private static B sink(String id, String name, String owner, Period p) { return new B(id, true, false, name, owner, p); }

    static {
        // §2.1 main line / challenge / abyss / raid (EmberRunRules + EmberRunService)
        src("S01", "主线 / 挑战结算基线", "EmberRunRules.BASE_*", Period.RUN).acc(COIN, SHARD, BONE, CORE, XP, MARK, GEAR)
            .model(FULL).g("coin", 300).g("shard", 24).g("bone", 6).g("core", 2).g("xp", 120).g("mark", 1).done();
        src("S02", "宝箱怪", "EmberRunRules.TREASURE_COIN", Period.RUN).acc(COIN).model(FULL).g("coin", 100).done();
        src("S03", "精英房", "EmberRunRules.ELITE_*", Period.RUN).acc(SHARD, CORE).model(FULL).g("shard", 10).g("core", 1).done();
        src("S04", "词缀精英结算", "ember-v1-runs.yml variety.affix_shard", Period.RUN).acc(SHARD).model(FULL).g("shard", 2).done();
        src("S05", "房间事件达标", "ember-v1-runs.yml variety.event_core", Period.RUN).acc(CORE).model(FULL).g("core", 1).done();
        src("S06", "首通包", "ember-v1-runs.yml maps.*.first_clear", Period.ONCE).acc(GEAR, SHARD, CORE, BONE, BLANK, COIN)
            .keys("p1_first_clear_", "p1_fcpay_").model(FULL)
            .g("q03.shard", 30).g("q03.core", 4).g("q03.coin", 600)
            .g("q04.blank", 6).g("q04.core", 6).g("q04.coin", 2100)
            .g("q05.bone", 20).g("q05.blank", 6).g("q05.coin", 900)
            .g("q06.core", 10).g("q06.coin", 1200)
            .g("q07.blank", 12).g("q07.core", 12).g("q07.coin", 1800).done();
        src("S07", "首通徽记", "EmberSignature.FC_MARKS", Period.ONCE).acc(INS).keys("p1_sigfc_", "p1_sigmark_").model(NONE).g("insignia", 3).done();
        src("S08", "重打普通主线（签名图）", "EmberSignature.CLEAR_MARKS / STAMP_RATE", Period.RUN).acc(INS, GEAR)
            .keys("p1_sigmark_").model(NONE).g("insignia", 1).g("stamp_rate", 0.12).done();
        src("S09", "自选誓约", "EmberPledgeService C_PLEDGE", Period.RUN).acc(INS).keys("p1_pledge_", "p1_sigmark_").model(PART)
            .g("per_rule", 1).done(); // D243 (G8): insignia per pledged rule, now an economy yml key (same value)
        src("S10", "精选周挑战加印", "ember-v1-runs.yml rotation", Period.WEEK).acc(MARK).keys("p2_rotation", "p1_mark_t").model(FULL)
            .g("mark", 1).g("weekly_cap", 3).done();
        src("S11", "精选周普通重打加印", "ember-v1-runs.yml rotation.normal_bonus_marks", Period.WEEK).acc(MARK)
            .keys("p2_rotation", "p1_mark_t").model(FULL).g("weekly_cap", 3).done();
        src("S12", "团本结算", "ember-v1-runs.yml raids.* + raid_item", Period.WEEK).acc(GEAR, MARK).keys("p2_raid_", "p1_mark_t").model(FULL)
            .g("weekly_cap", 3).done();
        src("S13", "深渊层结算", "ember-v1-runs.yml abyss", Period.RUN).acc(COIN, SHARD, BONE, CORE, XP, MARK, GEAR)
            .keys("p2_abyss_best").model(FULL).done();
        src("S14", "失败退体力", "ember-v1-runs.yml fail_refund", Period.DAY).acc(STA).ledger("failrefund@<day>").model(FULL)
            .g("rate", 0.5).g("cost", 30).done();
        src("S15", "当日首次倒下退药", "ember-v1.yml death_refund", Period.DAY).acc(POT).ledger("death_refund potions").model(FULL)
            .g("max_potions", 5).done();
        // §2.2 periodic modes
        src("S16", "余烬连战", "ember-v1-runs.yml rush.rush", Period.WEEK).acc(MARK, BADGE, Account.TITLE)
            .keys("p4_rush_claim", "p4_rush", "p1_mark_t", "p3_badge").model(PART).g("mark", 1).g("badge", 20).done();
        src("S17", "连战·前哨", "ember-v1-runs.yml rush.outpost", Period.WEEK).acc(INS, MARK)
            .keys("p4_outpost_claim", "p1_sigmark_", "p1_mark_t").model(PART).g("mark", 1).g("mark_tier", 2).g("insignia", 2).g("weekly", 1).done();
        src("S18", "首领残响", "ember-v1-runs.yml rush.echo_q01..q07", Period.WEEK).acc(INS)
            .keys("p4_echo_claim", "p1_sigmark_").model(NONE).g("insignia", 2).g("weekly", 3).done();
        src("S19", "周目标", "EmberSeason / ember-v1-runs.yml weekly_goals", Period.WEEK).acc(BADGE)
            .keys("p3_goal_", "p3_goalpay_", "p3_badge").model(OUT).done();
        src("S20", "每日委托", "ember-v1.yml bounty.daily", Period.DAY).acc(COIN, SHARD).keys("p2_bounty").model(FULL)
            .g("c1.coin", 30).g("c3.coin", 60).g("c3.shard", 6).done();
        src("S21", "花样委托", "ember-v1.yml bounty.variety", Period.DAY).acc(COIN).keys("p4_vb_").model(FULL).g("coin", 20).done();
        // §2.3 AFK / sign-in / online
        src("S22", "挂机庭击杀", "ember-v1.yml afk", Period.DAY).acc(COIN, XP, Account.BOUND_MAT)
            .keys("p1_afk_kill", "p1_afk_offk", "p1_afk_acc_", "p1_vbound_").ledger("bmat:").model(FULL)
            .g("daily_kills", 2400).g("offline_max_kills", 1200).done();
        src("S23", "每日签到", "ember-v1.yml signin", Period.MONTH).acc(COIN, XP, MARK, INS)
            .keys("p1_sign_mask", "p1_sign_mk", "p1_sign_mkd", "p1_sign_last", "p1_mark_t", "p1_sigmark_").model(FULL)
            .g("daily.coin", 20).g("daily.xp", 5).g("makeup_per_month", 3).g("sigmark_fallback_coin", 60).done();
        src("S24", "在线时长档", "ember-v1.yml online.milestones", Period.DAY).acc(COIN, XP).keys("p1_on_min", "p1_on_claim").model(FULL)
            .g("coin_total", 70).g("xp_total", 20).g("top_min", 120).done();
        // §2.4 growth bypasses / events / other
        src("S25", "勋记", "ember-v1-growth.yml honors", Period.ALL).acc(COIN, SHARD).keys("p4_chal_", "p4_honor_").model(FULL).done();
        src("S26", "国庆本 gq26", "ember-v1-festival.yml drops", Period.EVENT).acc(Account.FEST_COIN).keys("p3_fest_entry_").model(FULL).done();
        src("S27", "国庆兑换徽", "ember-v1-festival.yml", Period.EVENT).acc(BADGE).keys("p3_fest_xbadge_", "p3_badge").model(PART).done();
        src("S28", "8 印记兑换", "EmberRunRules.MARKS_PER_EXCHANGE", Period.NONE).acc(GEAR).keys("p1_mark_t").model(FULL).g("marks", 8).done();
        src("S29", "分解装备", "EmberForgeService dismantle", Period.NONE).acc(BLANK).keys("p1_blank_tenths").model(FULL).done(); // D318: armor 0.1 × tier tenths (flag off → never written)
        src("S30", "生活玩法产出", "life.yml", Period.DAY).acc(Account.LIFE_ITEM, POT).keys("life_", "life_xp").model(NONE).done();
        src("S31", "旧任务线（P1 下不推进）", "quest.yml / QuestService", Period.NONE).acc(XP).model(NONE).done();
        src("S32", "邮件附件", "MailService", Period.NONE).acc(COIN, Account.MAIL).model(NONE).done();
        // D243 / ARCH S4-2: sources found by the D242 source map (G1–G3) — registered with unchanged amounts (tags only)
        src("S33", "图录阶段奖励", "EmberCodex.STAGE_COIN (ledger run codex)", Period.ONCE).acc(COIN)
            .keys("p1_codex_", "p1_codex_stage_").ledger("codex/stage<n>").model(FULL)
            .g("at5.coin", 200).g("at10.coin", 400).g("at15.coin", 600).g("at20.coin", 1000).done();
        src("S34", "宝箱额外装备", "EmberRunRules.EXTRA_WEIGHTS chest", Period.RUN).acc(GEAR).ledger("extra_chest_item").model(FULL)
            .g("chest_weight", 5).done();
        src("S35", "起步包", "EmberRunService.giveStarter + EmberSupplyService.giveStarter", Period.ONCE).acc(GEAR, POT)
            .keys("p1_starter").ledger("starter/starter_<slot>").model(FULL).g("pieces", 2).g("potions", 5).done();
        // D244 (G10): item-level sources outside CoreRpg's grant code — registered for the audit / source map (tags only,
        // no goldens: their numbers live in the other plugins' configs; CoreRpg never pays them, so nothing routes here)
        src("S36", "钓鱼产出（CoreFish）", "plugins/CoreFish/config.yml tables", Period.NONE).acc(Account.LIFE_ITEM)
            .model(NONE).done();
        src("S37", "扭蛋券发放（CoreGacha）", "plugins/CoreGacha/config.yml tickets", Period.DAY).acc(Account.GACHA_TICKET)
            .model(NONE).done();
        src("S38", "扭蛋抽取产出（CoreGacha，外观）", "plugins/CoreGacha/gacha.yml items", Period.DAY).acc(Account.COSMETIC)
            .model(OUT).done();
        // D318 六槽 T1-7: +1 armor piece per modelled full clear (gear.six_slot.enabled, default off → never pays); p1sim models it
        // only under SIX (T0″ F arm), the default sim does not — PARTIAL. Registry + tag only, no economy yml key, no golden.
        src("S39", "六槽护甲掉落（每局 +1，开关默认关）", "EmberRunRules.settle six_armor (gear.six_slot.enabled)", Period.RUN).acc(GEAR)
            .ledger("six_armor").model(PART).done();
        // D391 短征 sx01：日有奖帽 3；有奖通关币/碎片/骨尘 + 生涯首通包；日帽后无奖。金样见 ember-v1-economy.yml；p1sim 另号。
        src("S40", "短征通关（sx01）", "EmberShortRules / ember-v1-runs.yml short.sx01", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx01_day").ledger("sx_clear_", "sx_fc_", "sx_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 200).g("fc.shard", 8).g("fc.blank", 1).g("daily_cap", 3).done();
        // D392 短征 sx02：有奖同 S40 量级；首通略薄；日帽独立 p1_sx02_day；p1sim 另号。
        src("S41", "短征通关（sx02）", "EmberShortRules / ember-v1-runs.yml short.sx02", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx02_day").ledger("sx02_clear_", "sx02_fc_", "sx02_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 180).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D393 短征 sx03：有奖同 S40/S41 量级；首通略薄 160/8/1；日帽独立 p1_sx03_day；p1sim 另号。
        src("S42", "短征通关（sx03）", "EmberShortRules / ember-v1-runs.yml short.sx03", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx03_day").ledger("sx03_clear_", "sx03_fc_", "sx03_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 160).g("fc.shard", 8).g("fc.blank", 1).g("daily_cap", 3).done();
        // D397 短征 sx04：有奖同量级；首通略薄 150/6/1；日帽独立 p1_sx04_day；p1sim 另号。
        src("S43", "短征通关（sx04）", "EmberShortRules / ember-v1-runs.yml short.sx04", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx04_day").ledger("sx04_clear_", "sx04_fc_", "sx04_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 150).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D400 短征 sx05：有奖同量级；首通略薄 140/6/1；日帽独立 p1_sx05_day；p1sim 另号。
        src("S44", "短征通关（sx05）", "EmberShortRules / ember-v1-runs.yml short.sx05", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx05_day").ledger("sx05_clear_", "sx05_fc_", "sx05_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 140).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D403 短征 sx06：有奖同量级；首通略薄 130/6/1；日帽独立 p1_sx06_day；p1sim 另号。
        src("S45", "短征通关（sx06）", "EmberShortRules / ember-v1-runs.yml short.sx06", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx06_day").ledger("sx06_clear_", "sx06_fc_", "sx06_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 130).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D407 短征 sx07：有奖同量级；首通略薄 120/6/1；日帽独立 p1_sx07_day；p1sim 另号。
        src("S46", "短征通关（sx07）", "EmberShortRules / ember-v1-runs.yml short.sx07", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx07_day").ledger("sx07_clear_", "sx07_fc_", "sx07_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 120).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D410 短征 sx08：有奖同量级；首通略薄 110/6/1；日帽独立 p1_sx08_day；p1sim 另号。
        src("S47", "短征通关（sx08）", "EmberShortRules / ember-v1-runs.yml short.sx08", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx08_day").ledger("sx08_clear_", "sx08_fc_", "sx08_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 110).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D412 短征 sx09：有奖同量级；首通略薄 100/6/1；日帽独立 p1_sx09_day；p1sim 另号。
        src("S48", "短征通关（sx09）", "EmberShortRules / ember-v1-runs.yml short.sx09", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx09_day").ledger("sx09_clear_", "sx09_fc_", "sx09_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 100).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D414 短征 sx10：有奖同量级；首通略薄 90/6/1；日帽独立 p1_sx10_day；p1sim 另号。
        src("S49", "短征通关（sx10）", "EmberShortRules / ember-v1-runs.yml short.sx10", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx10_day").ledger("sx10_clear_", "sx10_fc_", "sx10_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 90).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D417 短征 sx11：有奖同量级；首通略薄 80/6/1；日帽独立 p1_sx11_day；p1sim 另号。
        src("S50", "短征通关（sx11）", "EmberShortRules / ember-v1-runs.yml short.sx11", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx11_day").ledger("sx11_clear_", "sx11_fc_", "sx11_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 80).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D419 短征 sx12：有奖同量级；首通略薄 70/6/1；日帽独立 p1_sx12_day；p1sim 另号。
        src("S51", "短征通关（sx12）", "EmberShortRules / ember-v1-runs.yml short.sx12", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx12_day").ledger("sx12_clear_", "sx12_fc_", "sx12_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 70).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D421 短征 sx13：有奖同量级；首通略薄 60/6/1；日帽独立 p1_sx13_day；p1sim 另号。
        src("S52", "短征通关（sx13）", "EmberShortRules / ember-v1-runs.yml short.sx13", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx13_day").ledger("sx13_clear_", "sx13_fc_", "sx13_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 60).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D423 短征 sx14：有奖同量级；首通更薄 50/6/1；日帽独立 p1_sx14_day；p1sim 另号。
        src("S53", "短征通关（sx14）", "EmberShortRules / ember-v1-runs.yml short.sx14", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx14_day").ledger("sx14_clear_", "sx14_fc_", "sx14_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 50).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D424 短征 sx15：有奖同量级；首通更薄 40/6/1；日帽独立 p1_sx15_day；p1sim 另号。
        src("S54", "短征通关（sx15）", "EmberShortRules / ember-v1-runs.yml short.sx15", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx15_day").ledger("sx15_clear_", "sx15_fc_", "sx15_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 40).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D425 短征 sx16：有奖同量级；首通更薄 30/6/1；日帽独立 p1_sx16_day；p1sim 另号。
        src("S55", "短征通关（sx16）", "EmberShortRules / ember-v1-runs.yml short.sx16", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx16_day").ledger("sx16_clear_", "sx16_fc_", "sx16_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 30).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D426 短征 sx17：有奖同量级；首通更薄 25/6/1；日帽独立 p1_sx17_day；p1sim 另号。
        src("S56", "短征通关（sx17）", "EmberShortRules / ember-v1-runs.yml short.sx17", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx17_day").ledger("sx17_clear_", "sx17_fc_", "sx17_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 25).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D427 短征 sx18：有奖同量级；首通更薄 20/6/1；日帽独立 p1_sx18_day；p1sim 另号。
        src("S57", "短征通关（sx18）", "EmberShortRules / ember-v1-runs.yml short.sx18", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx18_day").ledger("sx18_clear_", "sx18_fc_", "sx18_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 20).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D428 短征 sx19：有奖同量级；首通更薄 18/6/1；日帽独立 p1_sx19_day；p1sim 另号。
        src("S58", "短征通关（sx19）", "EmberShortRules / ember-v1-runs.yml short.sx19", Period.DAY).acc(COIN, SHARD, BONE, BLANK)
            .keys("p1_sx19_day").ledger("sx19_clear_", "sx19_fc_", "sx19_practice").model(FULL)
            .g("clear.coin", 80).g("clear.shard", 4).g("clear.bone", 3)
            .g("fc.coin", 18).g("fc.shard", 6).g("fc.blank", 1).g("daily_cap", 3).done();
        // D430 随机锻造产出
        src("S59", "随机锻造产出", "EmberForgeRollRules / EmberRunService forgeRoll", Period.NONE).acc(GEAR)
            .keys("p1_mark_t").ledger("forge_roll").model(FULL).done();
        // §2.5 legacy sources — closed by S0-1…S0-5 while P1 is on (must stay 0 for non-OP players)
        old("LS1", "/dp start · /corerpg enter 旧本", "CoreRpgExpansion gate / TicketEntryService").keys("abyss_run_floor").model(OUT).done();
        old("LS2", "竞技场对战币 / 日箱", "ArenaService + ArenaCoinRules").keys("arena_coin_matches").model(OUT).done();
        old("LS3", "pass / vip / 月卡登录币", "CoreRpgPlugin legacyRouteRefused").model(OUT).done();
        old("LS4", "灾厄 / 旧击杀币", "CalamityService / ProgressService.legacyXpBlocked").keys("calamity_weekly_first", "afk_coin").model(OUT).done();
        old("LS5", "scrap / reforge / 旧 enhance", "CoreRpgPlugin legacyRouteRefused").model(OUT).done();
        // §3 sinks
        sink("C01", "进主线 / 挑战", "ember-v1-runs.yml cost", Period.RUN).acc(STA).model(FULL).g("stamina", 30).done();
        sink("C02", "进团本", "ember-v1-runs.yml raids.*.cost", Period.RUN).acc(STA).keys("p2_raid_").model(FULL).done();
        sink("C03", "强化", "EmberUpgradeRules", Period.NONE).acc(SHARD, CORE, COIN).model(FULL).done();
        sink("C04", "升阶", "EmberUpgradeRules", Period.NONE).acc(SHARD, CORE, BLANK, COIN).model(FULL).done();
        sink("C05", "精工", "EmberUpgradeRules", Period.NONE).acc(BLANK, BONE, COIN).model(FULL).done();
        sink("C06", "成色", "EmberUpgradeRules", Period.NONE).acc(BLANK, BONE, COIN).model(FULL).done();
        sink("C07", "8 印记兑换", "EmberRunRules.MARKS_PER_EXCHANGE", Period.NONE).acc(MARK).keys("p1_mark_t").model(FULL).g("marks", 8).done();
        sink("C08", "深渊层费", "ember-v1-runs.yml abyss", Period.RUN).acc(COIN, MARK).model(FULL).done();
        sink("C09", "天赋学习", "ember-v1-growth.yml talents", Period.ALL).acc(COIN).keys("p4_spec_learn_").model(FULL)
            .g("row1.coin", 800).g("row2.coin", 2000).g("row3.coin", 4000).done();
        sink("C10", "天赋重置", "ember-v1-growth.yml respec_coin", Period.NONE).acc(COIN).keys("p4_spec_resets").model(PART)
            .g("respec_coin", 2000).done();
        sink("C11", "洗练", "EmberGrowthService / EmberPayRules", Period.NONE).acc(COIN, SHARD, GEAR).model(FULL).done();
        sink("C12", "烬炉烙印", "EmberSignature.IMPRINT_*", Period.NONE).acc(INS, COIN).keys("p1_sigmark_").model(NONE)
            .g("insignia", 5).g("coin_per_tier", 300).done();
        sink("C13", "签名调律", "EmberSignature.ALT_MARKS", Period.ALL).acc(INS).keys("p1_sigmark_", "p1_sigaltu_").model(NONE)
            .g("insignia", 10).done();
        sink("C14", "回复药购买", "ember-v1.yml shop.heal_potion.price", Period.NONE).acc(COIN).model(FULL).g("coin", 10).done();
        sink("C15", "外观商店（暂停）", "EmberCosmetics", Period.NONE).acc(COIN, BADGE, MARK, Account.COSMETIC).keys("p2_cosbuy_").model(OUT).done();
        sink("C16", "扭蛋兑券（暂停）", "CoreGacha exchange", Period.DAY).acc(COIN, BADGE, Account.GACHA_TICKET).model(NONE).done();
        sink("C17", "生活 offer", "life.yml", Period.DAY).acc(COIN, CORE, Account.LIFE_ITEM).keys("life_").model(NONE).done(); // D244: offers eat life items too
        sink("C18", "国庆商店", "ember-v1-festival.yml shop", Period.EVENT).acc(Account.FEST_COIN, COIN, BADGE).model(PART)
            .g("charm_event", 60).g("trail_event", 30).g("after_coin", 15000).g("after_badge", 300)
            .g("memo", 120).g("badge_rate", 5).g("badge_cap", 40).done();
        sink("C19", "扭蛋抽取（耗券，CoreGacha）", "plugins/CoreGacha/gacha.yml cost_per_pull / daily_pull_cap", Period.DAY)
            .acc(Account.GACHA_TICKET).model(OUT).done(); // D244 (G10): registered, tags only
        // D429 烙纹：合成吃碎片；定向吃币（烙纹 NI 另扣，记 GEAR 账户口径）
        sink("C20", "烙纹合成", "EmberBrandRules.CRAFT_SHARDS", Period.NONE).acc(SHARD).model(FULL).g("shard", 40).done();
        sink("C21", "烙纹定向", "EmberBrandRules pin_coin/pin_brand", Period.NONE).acc(COIN, GEAR).model(FULL).done();
        sink("C22", "随机锻造", "EmberForgeRollRules", Period.NONE).acc(MARK, BLANK, COIN).model(FULL)
            .g("marks", 8).g("blank", 4).g("coin", 500).done();
    }

    private EmberEconomy() {}

    public static List<Row> all() { return Collections.unmodifiableList(new ArrayList<Row>(BY_ID.values())); }

    public static Row byId(String id) { return id == null ? null : BY_ID.get(id); }

    public static List<Row> sources() {
        List<Row> out = new ArrayList<Row>();
        for (Row r : BY_ID.values()) if (!r.sink && !r.legacy) out.add(r);
        return out;
    }

    public static List<Row> legacySources() {
        List<Row> out = new ArrayList<Row>();
        for (Row r : BY_ID.values()) if (r.legacy) out.add(r);
        return out;
    }

    public static List<Row> sinks() {
        List<Row> out = new ArrayList<Row>();
        for (Row r : BY_ID.values()) if (r.sink) out.add(r);
        return out;
    }

    /** P1 rows (sources + sinks) that the offline sim does not fully model — the S2 gap list (REG §5). */
    public static List<String> modelGaps() {
        List<String> out = new ArrayList<String>();
        for (Row r : BY_ID.values()) if (!r.legacy && (r.model == Model.NONE || r.model == Model.PARTIAL)) out.add(r.id);
        return out;
    }

    /** Rows that pay or take {@code a}. */
    public static List<Row> touching(Account a) {
        List<Row> out = new ArrayList<Row>();
        for (Row r : BY_ID.values()) if (r.accounts.contains(a)) out.add(r);
        return out;
    }

    /** Counter families named by a row but missing from {@link EmberCounters} ("row:key"); empty = consistent. */
    public static List<String> counterErrors() {
        List<String> out = new ArrayList<String>();
        for (Row r : BY_ID.values())
            for (String k : r.counters) if (EmberCounters.byKey(k) == null) out.add(r.id + ":" + k);
        return out;
    }

    /**
     * Integer amount for a registered row from {@code ember-v1-economy.yml} (SoT, D224).
     * Dual-asserts the Java golden; throws if the yml is not loaded/corrupt, the key is missing, or they drift.
     * Call {@link #loadEconomyYml} (or rely on classpath auto-load) before first use.
     */
    public static int amount(String id, String key) {
        ensureClasspathEconomyYml();
        double golden = require(id).golden(key);
        if (!ymlOk) {
            throw new IllegalStateException(ECONOMY_YML + " not loaded/corrupt: " + ymlError);
        }
        Double y = ymlAmount(id, key);
        if (y == null) {
            throw new IllegalArgumentException(id + " has no yml amount " + key);
        }
        if (Math.abs(y.doubleValue() - golden) > 1e-9) {
            throw new IllegalStateException(id + "." + key + ": yml " + y + " != golden " + golden);
        }
        return (int) Math.round(y.doubleValue());
    }

    /** Row by id, or throw. */
    public static Row require(String id) {
        Row r = byId(id);
        if (r == null) throw new IllegalArgumentException("unknown economy id " + id);
        return r;
    }

    /** Forge-mark counter family prefix (tier 1..3 → p1_mark_t1..t3). */
    public static final String MARK_COUNTER = "p1_mark_t";

    /**
     * Map a settle / ledger grant key to the REG source that pays it (S2-2/S2-3 routed set).
     * Unknown / not-yet-routed keys return null — callers keep the prior direct path (or use
     * {@link #sourceForGrant(String, String)} with the ledger run id for sign/online).
     */
    public static String sourceForGrantKey(String key) {
        if (key == null || key.isEmpty()) return null;
        if (key.startsWith("base_")) return "S01";
        if (key.startsWith("extra_treasure_")) return "S02";
        if (key.startsWith("extra_elite_")) return "S03";
        if (key.startsWith("var_affix_")) return "S04";
        if (key.startsWith("var_event_") || key.startsWith("event_")) return "S05";
        if (key.startsWith("bounty_")) return "S20";
        if (key.startsWith("vb_")) return "S21";
        // D228 (S2-8): insignia / weekly-mark ledger keys whose source the key alone names
        if ("fc_sigmark".equals(key)) return "S07"; // before the generic fc_ (S06 first-clear pack) prefix
        if (key.startsWith("fc_")) return "S06";
        if (key.startsWith("honor_")) return "S25";
        if ("sig_mark".equals(key)) return "S08";
        if ("pledge_sigmark".equals(key)) return "S09";
        if ("raid_mark".equals(key)) return "S12";
        if ("extra_chest_item".equals(key)) return "S34"; // D243 (G2): the chest's extra gear roll (item row; tag only)
        if ("six_armor".equals(key)) return "S39"; // D318 六槽: per-clear armor drop (switch default off)
        if (key.startsWith("sx05_clear_") || key.startsWith("sx05_fc_") || "sx05_practice".equals(key)) return "S44"; // D400 sx05
        if (key.startsWith("sx06_clear_") || key.startsWith("sx06_fc_") || "sx06_practice".equals(key)) return "S45"; // D403 sx06
        if (key.startsWith("sx19_clear_") || key.startsWith("sx19_fc_") || "sx19_practice".equals(key)) return "S58"; // D428 sx19
        if (key.startsWith("sx18_clear_") || key.startsWith("sx18_fc_") || "sx18_practice".equals(key)) return "S57"; // D427 sx18
        if (key.startsWith("sx17_clear_") || key.startsWith("sx17_fc_") || "sx17_practice".equals(key)) return "S56"; // D426 sx17
        if (key.startsWith("sx16_clear_") || key.startsWith("sx16_fc_") || "sx16_practice".equals(key)) return "S55"; // D425 sx16
        if (key.startsWith("sx15_clear_") || key.startsWith("sx15_fc_") || "sx15_practice".equals(key)) return "S54"; // D424 sx15
        if (key.startsWith("sx14_clear_") || key.startsWith("sx14_fc_") || "sx14_practice".equals(key)) return "S53"; // D423 sx14
        if (key.startsWith("sx13_clear_") || key.startsWith("sx13_fc_") || "sx13_practice".equals(key)) return "S52"; // D421 sx13
        if (key.startsWith("sx12_clear_") || key.startsWith("sx12_fc_") || "sx12_practice".equals(key)) return "S51"; // D419 sx12
        if (key.startsWith("sx11_clear_") || key.startsWith("sx11_fc_") || "sx11_practice".equals(key)) return "S50"; // D417 sx11
        if (key.startsWith("sx10_clear_") || key.startsWith("sx10_fc_") || "sx10_practice".equals(key)) return "S49"; // D414 sx10
        if (key.startsWith("sx09_clear_") || key.startsWith("sx09_fc_") || "sx09_practice".equals(key)) return "S48"; // D412 sx09
        if (key.startsWith("sx08_clear_") || key.startsWith("sx08_fc_") || "sx08_practice".equals(key)) return "S47"; // D410 sx08
        if (key.startsWith("sx07_clear_") || key.startsWith("sx07_fc_") || "sx07_practice".equals(key)) return "S46"; // D407 sx07
        if (key.startsWith("sx04_clear_") || key.startsWith("sx04_fc_") || "sx04_practice".equals(key)) return "S43"; // D397 sx04
        if (key.startsWith("sx03_clear_") || key.startsWith("sx03_fc_") || "sx03_practice".equals(key)) return "S42"; // D393 sx03
        if (key.startsWith("sx02_clear_") || key.startsWith("sx02_fc_") || "sx02_practice".equals(key)) return "S41"; // D392 sx02
        if (key.startsWith("sx_clear_") || key.startsWith("sx_fc_") || "sx_practice".equals(key)) return "S40"; // D391 sx01
        return null;
    }

    /** Map part of a run id ({@code <mapKey>[c|a<n>]-<ts36>-<rnd36>}); null when there is no {@code -}. */
    static String runHead(String runId) {
        if (runId == null) return null;
        int cut = runId.indexOf('-');
        return cut > 0 ? runId.substring(0, cut) : null;
    }

    /**
     * D229: abyss entry stamps the run id as {@code <mapKey>a<tier>-…} (challenge uses {@code c}, normal has neither).
     * Head alone is enough — no need to parse the tier number for SourceId routing.
     */
    static boolean isAbyssHead(String head) {
        return head != null && head.matches("[a-z0-9_]+a\\d+");
    }

    /**
     * Resolve the REG source for a ledger grant: key prefix first, then sign/online run id
     * ({@code p1sign-…} → S23, {@code p1online-…} → S24).
     */
    public static String sourceForGrant(String key, String runId) {
        String head = runHead(runId);
        // D229 (S2-9): abyss floor settle — same BASE_* amounts as S01, separate SourceId S13
        if (key != null && key.startsWith("base_") && isAbyssHead(head)) return "S13";
        String s = sourceForGrantKey(key);
        if (s != null) return s;
        // D228 (S2-8): weekly-mode ledger keys shared by several modes — the run id's map part tells which
        if (key != null && head != null) {
            if ("rot_mark".equals(key)) // featured-map bonus: challenge run id q0Nc → S10, normal repeat q0N → S11
                return head.matches("q\\d\\dc") ? "S10" : head.matches("q\\d\\d") ? "S11" : null;
            if ("rush_mark".equals(key)) return "rush".equals(head) ? "S16" : "outpost".equals(head) ? "S17" : null;
            if (key.startsWith("rush_sig_")) return "outpost".equals(head) ? "S17" : head.startsWith("echo_") ? "S18" : null;
        }
        if (runId != null) {
            if (runId.startsWith("p1sign-")) return "S23";
            if (runId.startsWith("p1online-")) return "S24";
            if (runId.startsWith("p1afk-")) return "S22"; // D221 / ARCH S2-5
            // D243 (G1 / G3): codex stage coin (run "codex", key stage<i>) and the starter kit (run "starter")
            if (EmberCodex.LEDGER_RUN.equals(runId) && key != null && key.startsWith("stage")) return "S33";
            if ("starter".equals(runId) && key != null && key.startsWith("starter_")) return "S35";
        }
        return null;
    }

    /** True when {@code r} may pay {@code a} (P1 source, not sink/legacy). */
    public static boolean pays(String sourceId, Account a) {
        Row r = byId(sourceId);
        return r != null && !r.sink && !r.legacy && a != null && r.accounts.contains(a);
    }

    /**
     * Coin grant tagged with a registered P1 source. Refuses sinks, legacy rows, rows that do not pay COIN,
     * non-positive amounts, and a null data row. Does not look up golden — the caller supplies the amount
     * (settle / sign already took it from {@link #amount} where routed).
     */
    public static boolean grantCoin(PlayerData d, String sourceId, int amount) {
        if (d == null || amount <= 0) return false;
        ensureClasspathEconomyYml();
        if (!ymlOk) return false; // fail-closed: refuse grant when yml missing/corrupt
        if (!pays(sourceId, Account.COIN)) return false;
        d.addCoin(amount);
        return true;
    }

    /**
     * Forge-mark grant tagged with a registered P1 source. Tier must be 1..3. Writes
     * {@code p1_mark_t<tier>@all}.
     */
    public static boolean grantMark(PlayerData d, String sourceId, int tier, int amount) {
        if (d == null || amount <= 0 || tier < 1 || tier > 3) return false;
        if (!pays(sourceId, Account.MARK)) return false;
        d.addPeriodCount(MARK_COUNTER + tier, "all", amount);
        return true;
    }

    /**
     * D228 (S2-8): boss-insignia grant tagged with a registered P1 source (S07 first clear, S08 repeat, S09 pledge,
     * S17 outpost, S18 echo, S23 sign-in). Refuses rows that do not pay INSIGNIA, an empty map and non-positive amounts.
     * Writes {@code p1_sigmark_<map>@all}.
     */
    public static boolean grantInsignia(PlayerData d, String sourceId, String map, int amount) {
        if (d == null || amount <= 0 || map == null || map.isEmpty()) return false;
        if (!pays(sourceId, Account.INSIGNIA)) return false;
        d.addPeriodCount(INSIGNIA_COUNTER + map, "all", amount);
        return true;
    }

    /**
     * D228 (S2-8): ember-badge grant tagged with a registered P1 source (S16 rush, S19 weekly goals, S27 festival
     * exchange). Refuses rows that do not pay BADGE and non-positive amounts. Writes {@code p3_badge@all}.
     */
    public static boolean grantBadge(PlayerData d, String sourceId, int amount) {
        if (d == null || amount <= 0) return false;
        if (!pays(sourceId, Account.BADGE)) return false;
        d.addPeriodCount(BADGE_COUNTER, "all", amount);
        return true;
    }

    /** Outcome of a ledger-row credit ({@link #creditMarkLedger} / {@link #creditInsigniaLedger}). */
    public enum Credit {
        /** paid through the resolved REG source */
        TAGGED,
        /** no REG source for this key / run id (pre-S2 ledger row, admin row) — credited anyway so a durable row is never lost */
        UNTAGGED,
        /** a source resolved but refused (wrong account / bad tier / bad map / amount ≤ 0) — nothing written */
        REFUSED
    }

    /**
     * D228 (S2-8): deliver one forge-mark ledger row. A resolvable source ({@link #sourceForGrant}) goes through
     * {@link #grantMark}; an unresolvable one is credited untagged (same as the pre-S2 direct path) so the only
     * {@code p1_mark_t} write for ledger rows lives here.
     */
    public static Credit creditMarkLedger(PlayerData d, String key, String runId, int tier, int amount) {
        if (d == null || amount <= 0 || tier < 1 || tier > 3) return Credit.REFUSED;
        String src = sourceForGrant(key, runId);
        if (src != null) return grantMark(d, src, tier, amount) ? Credit.TAGGED : Credit.REFUSED;
        d.addPeriodCount(MARK_COUNTER + tier, "all", amount);
        return Credit.UNTAGGED;
    }

    /** D228 (S2-8): deliver one boss-insignia ledger row — {@link #grantInsignia} when the source resolves, else untagged. */
    public static Credit creditInsigniaLedger(PlayerData d, String key, String runId, String map, int amount) {
        if (d == null || amount <= 0 || map == null || map.isEmpty()) return Credit.REFUSED;
        String src = sourceForGrant(key, runId);
        if (src != null) return grantInsignia(d, src, map, amount) ? Credit.TAGGED : Credit.REFUSED;
        d.addPeriodCount(INSIGNIA_COUNTER + map, "all", amount);
        return Credit.UNTAGGED;
    }

    /** D228 (S2-8): REG source of a rush-hall settlement by mode (rush S16 · outpost S17 · echo S18); null = unknown mode. */
    public static String sourceForRushMode(String mode) {
        if ("rush".equals(mode)) return "S16";
        if ("outpost".equals(mode)) return "S17";
        if ("echo".equals(mode)) return "S18";
        return null;
    }

    /**
     * XP grant tagged with a registered P1 source. Validates only — the caller still applies XP via
     * {@code ProgressService.grantFlatEmberXp} (needs Player / level-up). Returns false when refused.
     */
    public static boolean grantXp(String sourceId, int amount) {
        if (amount <= 0) return false;
        return pays(sourceId, Account.XP);
    }

    /**
     * Material grant tagged with a registered P1 source. Validates only — the caller still delivers via
     * vault / Ni / mail. Known mat ids must match the row's SHARD/CORE/BONE/BLANK account; other ids need
     * BOUND_MAT (or GEAR) on the row.
     */
    public static boolean grantMat(String sourceId, String matId, int amount) {
        if (amount <= 0) return false;
        Row r = byId(sourceId);
        if (r == null || r.sink || r.legacy) return false;
        Account a = matAccount(matId);
        if (a != null) {
            if (r.accounts.contains(a)) return true;
            // S22 AFK (and similar) pays the four warehouse mats as account-bound credit under BOUND_MAT
            return r.accounts.contains(Account.BOUND_MAT);
        }
        return r.accounts.contains(Account.BOUND_MAT) || r.accounts.contains(Account.GEAR);
    }

    /** Map a known P1 material id to its registry account; null = not one of the four warehouse mats. */
    public static Account matAccount(String matId) {
        if (matId == null) return null;
        if (EmberUpgradeRules.MAT_SHARD.equals(matId)) return Account.SHARD;
        if (EmberUpgradeRules.MAT_CORE.equals(matId)) return Account.CORE;
        if (EmberUpgradeRules.MAT_BONE.equals(matId)) return Account.BONE;
        if (EmberUpgradeRules.MAT_BLANK.equals(matId)) return Account.BLANK;
        if (EmberBrandRules.MAT_BRAND.equals(matId)) return Account.GEAR; // D429 烙纹
        return null;
    }

    /**
     * Coin spend tagged with a registered P1 sink. Refuses sources, legacy rows, rows that do not take COIN,
     * and non-positive amounts. Returns false when the balance is short (same as {@link PlayerData#takeCoin}).
     */
    public static boolean spendCoin(PlayerData d, String sinkId, int amount) {
        if (d == null || amount <= 0) return false;
        Row r = byId(sinkId);
        if (r == null || !r.sink || r.legacy || !r.accounts.contains(Account.COIN)) return false;
        return d.takeCoin(amount);
    }

    /** Boss-insignia counter family prefix (+ map key, period all) — same string as {@code EmberSignature.C_MARK}. */
    public static final String INSIGNIA_COUNTER = "p1_sigmark_";

    /** True when {@code sinkId} is a registered P1 sink (not a source / legacy row) that takes {@code a}. */
    public static boolean takes(String sinkId, Account a) {
        Row r = byId(sinkId);
        return r != null && r.sink && !r.legacy && a != null && r.accounts.contains(a);
    }

    /**
     * Forge-mark spend tagged with a registered P1 sink (C07 exchange, C08 abyss fee). Tier 1..3; refuses rows that do
     * not take MARK, non-positive amounts and a short balance (nothing changes then). Writes {@code p1_mark_t<tier>@all}.
     */
    public static boolean spendMark(PlayerData d, String sinkId, int tier, int amount) {
        if (d == null || amount <= 0 || tier < 1 || tier > 3) return false;
        if (!takes(sinkId, Account.MARK)) return false;
        String k = MARK_COUNTER + tier;
        if (d.periodCount(k, "all") < amount) return false;
        d.addPeriodCount(k, "all", -amount);
        return true;
    }

    /**
     * Boss-insignia spend tagged with a registered P1 sink (C12 imprint, C13 attune unlock). Refuses rows that do not
     * take INSIGNIA, an empty map, non-positive amounts and a short balance. Writes {@code p1_sigmark_<map>@all}.
     */
    public static boolean spendInsignia(PlayerData d, String sinkId, String map, int amount) {
        if (d == null || amount <= 0 || map == null || map.isEmpty()) return false;
        if (!takes(sinkId, Account.INSIGNIA)) return false;
        String k = INSIGNIA_COUNTER + map;
        if (d.periodCount(k, "all") < amount) return false;
        d.addPeriodCount(k, "all", -amount);
        return true;
    }

    /**
     * Warehouse-material spend tagged with a registered P1 sink. Validates only — the caller still consumes via Ni
     * (backpack / warehouse). The mat id must be one of the four P1 mats and its account on the sink row.
     */
    public static boolean spendMat(String sinkId, String matId, int amount) {
        if (amount <= 0) return false;
        Account a = matAccount(matId);
        return a != null && takes(sinkId, a);
    }

    /** Ember badge counter family (period all) — same string as {@code EmberSeason.C_BADGE}. */
    public static final String BADGE_COUNTER = "p3_badge";

    /**
     * Badge spend tagged with a registered P1 sink (C18 festival after-event charm). Refuses rows that do not take
     * BADGE, non-positive amounts and a short balance. Writes {@code p3_badge@all}.
     */
    public static boolean spendBadge(PlayerData d, String sinkId, int amount) {
        if (d == null || amount <= 0) return false;
        if (!takes(sinkId, Account.BADGE)) return false;
        if (d.periodCount(BADGE_COUNTER, "all") < amount) return false;
        d.addPeriodCount(BADGE_COUNTER, "all", -amount);
        return true;
    }

    /**
     * Festival-coin spend tagged with a registered P1 sink (C18 shop). Validates only — the caller still consumes via
     * Ni ({@code consumeExact} on the event coin item). Same shape as {@link #spendMat}.
     */
    public static boolean spendFestCoin(String sinkId, int amount) {
        if (amount <= 0) return false;
        return takes(sinkId, Account.FEST_COIN);
    }

    /**
     * Workshop op kind (EmberForgeService / EmberGrowthService commit kinds) → REG sink: enhance C03, upgrade C04,
     * refine (精工) C05, quality (成色) C06, reroll (洗练) C11, imprint (烙印) C12. Null = not a registered spend
     * (dismantle, swap, dismantle undo — those give or move, they do not sink).
     */
    public static String sinkForForge(String kind) {
        if (kind == null) return null;
        switch (kind) {
            case "enhance": return "C03";
            case "upgrade": return "C04";
            case "refine": return "C05";
            case "quality": return "C06";
            case "reroll": return "C11";
            case "imprint": return "C12";
            default: return null;
        }
    }

    /**
     * Map a durable delivery {@code request_id} to the REG sink that originally spent the coins (D223 / ARCH S2-6).
     * Strips a leading {@code refund:} (hold id) so a clawback of an enhance hold still tags C03. Null = unmapped /
     * not a coin sink (undo / dismantle / swap / unknown) — {@link #spendCoinDelivery} then takes without a sink tag.
     */
    public static String sinkForDeliveryRequest(String request) {
        if (request == null || request.isEmpty()) return null;
        String r = request.startsWith("refund:") ? request.substring(7) : request;
        if (r.startsWith("enh:")) return "C03";
        if (r.startsWith("upgrade:")) return "C04";
        if (r.startsWith("refine:")) return "C05";
        if (r.startsWith("quality:")) return "C06";
        if (r.startsWith("afx:") || r.startsWith("reroll:")) return "C11";
        if (r.startsWith("imp:")) return "C12";
        return null;
    }

    /**
     * Coin debit from a durable delivery row (negative {@code kind=coin} amount). Tags the debit with
     * {@link #sinkForDeliveryRequest} when mappable; otherwise {@link PlayerData#takeCoin} inside this helper so
     * {@code EmberDelivery} stays off the direct-takeCoin allowlist. Amounts unchanged.
     */
    public static boolean spendCoinDelivery(PlayerData d, String request, int amount) {
        if (d == null || amount <= 0) return false;
        String sink = sinkForDeliveryRequest(request);
        if (sink != null) return spendCoin(d, sink, amount);
        return d.takeCoin(amount);
    }

    /** Bundled / deployed economy amounts filename (REG §6.3 · E1 → SoT D224). */
    public static final String ECONOMY_YML = "ember-v1-economy.yml";

    /** Expected balance_version in economy yml (must match ember-v1.yml). */
    public static final int ECONOMY_BV = 60;

    /** id → (key → amount) from the last successful {@link #loadEconomyYml}. */
    private static volatile Map<String, Map<String, Double>> ymlAmounts = Collections.emptyMap();
    private static volatile boolean ymlOk;
    private static volatile String ymlError = "not loaded";
    private static volatile boolean classpathTried;
    /** True after {@link #loadEconomyYml} — blocks classpath auto-load from undoing fail-closed. */
    private static volatile boolean explicitLoad;

    /** True after a successful load with zero drift against Java golden. */
    public static boolean economyYmlReady() { return ymlOk; }

    /** Last load error / "not loaded" when {@link #economyYmlReady} is false. */
    public static String economyYmlError() { return ymlError; }

    /** Yml amount for id.key, or null if missing / not loaded. */
    public static Double ymlAmount(String id, String key) {
        Map<String, Map<String, Double>> m = ymlAmounts;
        if (m == null || id == null || key == null) return null;
        Map<String, Double> block = m.get(id);
        return block == null ? null : block.get(key);
    }

    /**
     * Load / replace the runtime amount table from a parsed {@code ember-v1-economy.yml} tree.
     * Null / empty → fail-closed (not ready). Drift vs Java golden → fail-closed and return the drift list.
     * Success → {@link #amount} reads yml; golden remains the secondary assert inside {@link #amount}.
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<String> loadEconomyYml(Map<String, Object> root) {
        explicitLoad = true;
        List<String> drift = economyYmlDrift(root);
        if (root == null || root.isEmpty()) {
            ymlAmounts = Collections.emptyMap();
            ymlOk = false;
            ymlError = "missing";
            return drift.isEmpty() ? Collections.singletonList("missing") : drift;
        }
        if (!drift.isEmpty()) {
            ymlAmounts = Collections.emptyMap();
            ymlOk = false;
            ymlError = "drift: " + drift;
            return drift;
        }
        Map<String, Map<String, Double>> next = new LinkedHashMap<String, Map<String, Double>>();
        Map<String, Object> sources = (Map<String, Object>) root.get("sources");
        Map<String, Object> sinks = (Map<String, Object>) root.get("sinks");
        for (Row r : BY_ID.values()) {
            if (r.golden.isEmpty()) continue;
            Map<String, Object> block = r.sink
                    ? (sinks == null ? null : (Map<String, Object>) sinks.get(r.id))
                    : (sources == null ? null : (Map<String, Object>) sources.get(r.id));
            if (block == null) continue;
            Map<String, Double> flat = new LinkedHashMap<String, Double>();
            for (Map.Entry<String, Double> g : r.golden.entrySet()) {
                Object v = block.get(g.getKey());
                if (v instanceof Number) flat.put(g.getKey(), ((Number) v).doubleValue());
            }
            next.put(r.id, Collections.unmodifiableMap(flat));
        }
        ymlAmounts = Collections.unmodifiableMap(next);
        ymlOk = true;
        ymlError = "";
        return Collections.emptyList();
    }

    /** Auto-load the bundled classpath resource once when nothing has been loaded yet (unit tests / early callers). */
    public static synchronized void ensureClasspathEconomyYml() {
        if (ymlOk || classpathTried || explicitLoad) return;
        classpathTried = true;
        try {
            java.io.InputStream in = EmberEconomy.class.getResourceAsStream("/" + ECONOMY_YML);
            if (in == null) {
                loadEconomyYml(null);
                ymlError = "classpath missing";
                return;
            }
            try {
                Object root = new org.yaml.snakeyaml.Yaml().load(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                @SuppressWarnings("unchecked")
                Map<String, Object> map = root instanceof Map ? (Map<String, Object>) root : null;
                loadEconomyYml(map);
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            ymlAmounts = Collections.emptyMap();
            ymlOk = false;
            ymlError = "classpath: " + t.getMessage();
        }
    }

    /** Reset load state (tests only). Next {@link #amount} will classpath-load again. */
    public static synchronized void resetEconomyYmlForTest() {
        ymlAmounts = Collections.emptyMap();
        ymlOk = false;
        ymlError = "not loaded";
        classpathTried = false;
        explicitLoad = false;
    }

    /**
     * Compare a parsed {@code ember-v1-economy.yml} tree to the built-in golden map (fail-on-drift).
     * Null / empty root → empty list (caller treats as missing). Every golden key must appear under
     * {@code sources.<id>} or {@code sinks.<id>} with the same number; wrong {@code balance_version} is reported.
     * Does not mutate runtime state — use {@link #loadEconomyYml} to install.
     */
    @SuppressWarnings("unchecked")
    public static List<String> economyYmlDrift(Map<String, Object> root) {
        List<String> out = new ArrayList<String>();
        if (root == null || root.isEmpty()) return out;
        Object bv = root.get("balance_version");
        if (!(bv instanceof Number) || ((Number) bv).intValue() != ECONOMY_BV)
            out.add("balance_version: want " + ECONOMY_BV + " got " + bv);
        Map<String, Object> sources = root.get("sources") instanceof Map ? (Map<String, Object>) root.get("sources") : null;
        Map<String, Object> sinks = root.get("sinks") instanceof Map ? (Map<String, Object>) root.get("sinks") : null;
        for (Row r : BY_ID.values()) {
            if (r.golden.isEmpty()) continue;
            Map<String, Object> block = r.sink
                    ? (sinks == null ? null : (Map<String, Object>) sinks.get(r.id))
                    : (sources == null ? null : (Map<String, Object>) sources.get(r.id));
            if (block == null) {
                out.add(r.id + ": missing block");
                continue;
            }
            for (Map.Entry<String, Double> g : r.golden.entrySet()) {
                Object v = block.get(g.getKey());
                if (!(v instanceof Number)) {
                    out.add(r.id + "." + g.getKey() + ": missing");
                    continue;
                }
                double live = ((Number) v).doubleValue();
                if (Math.abs(live - g.getValue()) > 1e-9)
                    out.add(r.id + "." + g.getKey() + ": yml " + live + " != golden " + g.getValue());
            }
        }
        return out;
    }

}
