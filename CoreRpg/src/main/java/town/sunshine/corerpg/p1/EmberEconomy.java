package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * D213 (ARCH S2-1 · REG-ember-source-sink-cap §6.1/§6.2, first step): the registry of every P1 economy
 * <b>source</b> (S01–S32), every closed <b>legacy source</b> (L-S1…L-S5) and every <b>sink</b> (C01–C18) from
 * {@code docs/design/REG-ember-source-sink-cap-2026-10-05.md}. Pure data + lookups, no Bukkit, no behaviour:
 * nothing calls through it yet (S2-2 routes {@code grant} / {@code spend} through these ids).
 * <p>{@code EmberEconomyTest} pins it to the live game: every counter family named here must exist in
 * {@link EmberCounters}; every golden amount must equal the Java constant or shipped yml value that really pays it;
 * the set of sources the offline sim does not model ({@link Model#NONE} / {@link Model#PARTIAL}) is pinned so a new
 * unmodelled source fails the build until the table and p1sim are updated. Numbers unchanged (balance_version 57).
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
        src("S01", "主线 / 挑战 / 深渊结算基线", "EmberRunRules.BASE_*", Period.RUN).acc(COIN, SHARD, BONE, CORE, XP, MARK, GEAR)
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
        src("S09", "自选誓约", "EmberRunService C_PLEDGE", Period.RUN).acc(INS).keys("p1_pledge_", "p1_sigmark_").model(PART).done();
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
        src("S18", "首领残响", "ember-v1-runs.yml rush.echo_q01..q04", Period.WEEK).acc(INS)
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
        src("S29", "分解装备", "EmberForgeService dismantle", Period.NONE).acc(BLANK).model(FULL).done();
        src("S30", "生活玩法产出", "life.yml", Period.DAY).acc(Account.LIFE_ITEM, POT).keys("life_", "life_xp").model(NONE).done();
        src("S31", "旧任务线（P1 下不推进）", "quest.yml / QuestService", Period.NONE).acc(XP).model(NONE).done();
        src("S32", "邮件附件", "MailService", Period.NONE).acc(COIN, Account.MAIL).model(NONE).done();
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
        sink("C09", "天赋学习", "ember-v1-growth.yml talents", Period.ALL).acc(COIN).keys("p4_spec_learn_").model(FULL).done();
        sink("C10", "天赋重置", "ember-v1-growth.yml respec_coin", Period.NONE).acc(COIN).keys("p4_spec_resets").model(PART).done();
        sink("C11", "洗练", "EmberGrowthService / EmberPayRules", Period.NONE).acc(COIN, SHARD, GEAR).model(FULL).done();
        sink("C12", "烬炉烙印", "EmberSignature.IMPRINT_*", Period.NONE).acc(INS, COIN).keys("p1_sigmark_").model(NONE)
            .g("insignia", 5).g("coin_per_tier", 300).done();
        sink("C13", "签名调律", "EmberSignature.ALT_MARKS", Period.ALL).acc(INS).keys("p1_sigmark_", "p1_sigaltu_").model(NONE)
            .g("insignia", 10).done();
        sink("C14", "回复药购买", "ember-v1.yml shop.heal_potion.price", Period.NONE).acc(COIN).model(FULL).g("coin", 10).done();
        sink("C15", "外观商店（暂停）", "EmberCosmetics", Period.NONE).acc(COIN, BADGE, MARK, Account.COSMETIC).keys("p2_cosbuy_").model(OUT).done();
        sink("C16", "扭蛋兑券（暂停）", "CoreGacha exchange", Period.DAY).acc(COIN, BADGE, Account.GACHA_TICKET).model(NONE).done();
        sink("C17", "生活 offer", "life.yml", Period.DAY).acc(COIN, CORE).keys("life_").model(NONE).done();
        sink("C18", "国庆商店", "ember-v1-festival.yml shop", Period.EVENT).acc(Account.FEST_COIN, COIN, BADGE).model(PART).done();
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
}
