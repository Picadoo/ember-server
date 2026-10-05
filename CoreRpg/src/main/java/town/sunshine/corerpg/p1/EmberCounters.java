package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * D206 (ARCH S1-1 · REG-ember-counter-registry §4-1/§4-2): the registry of every {@link town.sunshine.corerpg.PlayerData}
 * period-counter key family ({@code name@period}). Each family has an owner system, a period kind, a category, a
 * cleanup rule and whether it is an asset. Pure data + lookups, no Bukkit, no behaviour of its own:
 * {@code EmberCountersTest} scans {@code src/main/java} and fails on any counter key literal that is not registered
 * here, on dead entries, and on prefix families that swallow one another (except the listed exemptions).
 * Config-defined weekly claim keys ({@code ember-v1-runs.yml rush.<key>.claim}) are checked against it on load
 * ({@link #rushClaimError}).
 * <p>Numbers and keys are unchanged (balance_version 57); renames proposed by the REG doc (codex, insignia) are not done.
 */
public final class EmberCounters {
    /** How the {@code @period} part is written. */
    public enum Period {
        /** {@code @all}, permanent. */ ALL,
        /** {@code DailyService.today()} {@code yyyy-MM-dd}. */ DAY,
        /** P1 rotation week {@code w<n>} ({@code EmberRunRules.rotationWeekKey} / {@code EmberSeason.weekKey}). */ PWEEK,
        /** Legacy {@code DailyService.weekId()} {@code yyyy-Www}. */ LWEEK,
        /** {@code yyyy-MM} (sign-in month). */ MONTH,
        /** Map {@code content_version} (first-clear package). */ VER,
        /** Constant {@code 1} (delivery / payment markers). */ CONST,
        /** The period slot holds the selected id (cosmetic selectors), value 1. */ VALUE_IN_PERIOD,
        /** Several of the above (legacy life counters: day / legacy week / all). */ MIXED
    }

    /** What the value means. */
    public enum Category {
        /** Spendable / convertible value or an anti-transfer allowance. */ ASSET,
        /** Idempotency / cap counter that stops a reward from paying twice. */ CLAIM,
        /** Unlock / first clear / learned. */ PROGRESS,
        /** Belongs to one item (keyed by item uid) — S1-4 moves these onto the item. */ ITEM,
        /** In-flight payment / delivery recovery state. */ TXN,
        /** Player toggle / choice. */ SETTING,
        /** Display or estimate only. */ STAT,
        /** Only stops a hint from repeating. */ HINT,
        /** Admin / test only; normal play must not write it. */ ADMIN
    }

    /** When the key goes away today. */
    public enum Cleanup {
        /** Stays in the blob forever. */ NEVER,
        /** Dropped by {@code addPeriodCount} when the same name is written in a new period. */ PERIOD_ROLL,
        /** Removed when the value reaches 0. */ ZERO,
        /** Overwritten in place (single live value). */ OVERWRITE
    }

    /** One key family. */
    public static final class Family {
        public final String key;        // exact name, or prefix when {@link #prefix}
        public final boolean prefix;    // true: key + suffix (map / uid / id …)
        public final String system;     // owning class / system
        public final Period period;
        public final Category category;
        public final Cleanup cleanup;
        public final boolean legacy;    // pre-P1 system key (S0 keeps it unwritten while P1 is on, except life_)
        public final boolean config;    // defined by config (rush claim keys), not by a Java literal
        public final String note;

        Family(String key, boolean prefix, String system, Period period, Category category, Cleanup cleanup,
               boolean legacy, boolean config, String note) {
            this.key = key; this.prefix = prefix; this.system = system; this.period = period; this.category = category;
            this.cleanup = cleanup; this.legacy = legacy; this.config = config; this.note = note;
        }

        public boolean asset() { return category == Category.ASSET; }
        public boolean item() { return category == Category.ITEM || (category == Category.TXN && key.equals("p4_rro_")); }

        /** Does counter name {@code name} (without {@code @period}) belong to this family? */
        public boolean matches(String name) { return prefix ? name.startsWith(key) && name.length() > key.length() : name.equals(key); }

        @Override public String toString() { return key + (prefix ? "*" : "") + " [" + system + ", " + period + ", " + category + "]"; }
    }

    /**
     * Prefix pairs that are allowed to contain one another; the longest match wins in {@link #lookup}.
     * {@code p1_codex_} (codex entries) vs {@code p1_codex_stage_} (stage claims): nothing scans by {@code p1_codex_},
     * a rename would need a key migration (REG §2.9) — deferred, documented here instead.
     */
    public static final List<String[]> PREFIX_EXEMPT = Collections.unmodifiableList(java.util.Arrays.asList(
        new String[] {"p1_codex_", "p1_codex_stage_"},
        new String[] {"afk_", "afk_coin"},
        new String[] {"life_", "life_xp"}));

    private static final Map<String, Family> BY_KEY = new LinkedHashMap<String, Family>();

    private static final Period A = Period.ALL, D = Period.DAY, W = Period.PWEEK, LW = Period.LWEEK, M = Period.MONTH;
    private static final Category AS = Category.ASSET, CL = Category.CLAIM, PR = Category.PROGRESS, IT = Category.ITEM,
        TX = Category.TXN, SE = Category.SETTING, ST = Category.STAT, HI = Category.HINT, AD = Category.ADMIN;
    private static final Cleanup NV = Cleanup.NEVER, RL = Cleanup.PERIOD_ROLL, ZR = Cleanup.ZERO, OW = Cleanup.OVERWRITE;

    private static void x(String key, String sys, Period p, Category c, Cleanup cl, String note) { add(key, false, sys, p, c, cl, false, false, note); }
    private static void px(String key, String sys, Period p, Category c, Cleanup cl, String note) { add(key, true, sys, p, c, cl, false, false, note); }
    private static void add(String key, boolean prefix, String sys, Period p, Category c, Cleanup cl, boolean legacy, boolean config, String note) {
        if (BY_KEY.put(key, new Family(key, prefix, sys, p, c, cl, legacy, config, note)) != null)
            throw new IllegalStateException("duplicate counter family " + key);
    }

    static {
        // 2.1 main line, raids, rush (EmberRunService, EmberRunMaps, EmberFirstClear)
        px("p1_first_clear_", "EmberFirstClear", A, PR, NV, "fact @all (D205); legacy @<ver> read as fact+paid and migrated before writes");
        px("p1_fcpay_", "EmberFirstClear", Period.VER, CL, RL, "first-clear package paid @<content_version> (D205)");
        px("p1_unlock_", "EmberRunService", A, PR, NV, "map unlocks granted by settlement / admin");
        x("p1_target", "EmberForgeService", A, SE, OW, "current forge target");
        x("p1_slash_shape", "SkillService", A, SE, OW, "D211 烬斩符文 0=fan 1=line 2=ring (Q04+); signature shape overrides");
        x("p1_step_dir", "FlexSkillService", A, SE, OW, "D219 身法方向 0=前冲 1=后撤 (Q01+; shares 14s CD; 火痕·后撤 ignites takeoff)");
        x("p1_starter", "EmberRunService", A, CL, NV, "starter pack claimed");
        px("p1_mark_t", "EmberRunService/EmberPay", A, AS, ZR, "forge marks t1..t3; EmberDelivery may re-credit (MARK_COUNTER)");
        x("p2_rotation", "EmberRunService", W, CL, RL, "featured-week extra runs (weekly_cap 3)");
        px("p2_raid_", "EmberRunService", W, CL, RL, "raid weekly cap per cap_group / map");
        x("p2_abyss_best", "EmberRunService", A, ST, OW, "best abyss floor");
        x("p2_bounty", "EmberRunService", D, CL, RL, "daily bounty");
        px("p4_vb_", "EmberRunService", D, CL, RL, "variety bounties settled today per kind");
        x("p4_rush", "EmberRunService", W, ST, RL, "rush attempts (stat only since D160)");
        x("p4_rush_claim", "EmberRunService", W, CL, RL, "main rush weekly reward claim");
        add("p4_outpost_claim", false, "ember-v1-runs.yml rush.claim", W, CL, RL, false, true, "outpost weekly claim (config)");
        add("p4_echo_claim", false, "ember-v1-runs.yml rush.claim", W, CL, RL, false, true, "echo weekly claim, shared by the echo halls (config)");
        px("p1_pledge_", "EmberRunService", A, SE, NV, "Q06 chosen pledge rule");
        // 2.2 signatures, insignia, tuning (EmberSignature, EmberGrowthService)
        px("p1_sig_", "EmberSignature", A, IT, NV, "LEGACY item signature code by uid — migrated to EmberItemData.sigCode / cr_p1_item.sig_code (S1-4 done, D208); read only for v1 items, cleared when folded");
        px("p1_sigmark_", "EmberSignature", A, AS, ZR, "boss insignia per map (6 sources)");
        px("p1_sigfc_", "EmberSignature", A, CL, NV, "first-clear insignia paid, once per map");
        px("p1_sigaltu_", "EmberSignature", A, PR, NV, "tuned variant unlocked");
        px("p1_sigalt_", "EmberSignature", A, SE, ZR, "tuned variant selected");
        px("p1_sigoff_", "EmberSignature", A, SE, ZR, "signature slot switched off");
        px("p1_sigseen_", "EmberSignature", A, PR, NV, "codex: signature obtained");
        // 2.3 growth (EmberGrowthService, EmberPayRules)
        px("p4_spec_learn_", "EmberGrowthService", A, PR, NV, "talent node learned");
        px("p4_spec_row", "EmberGrowthService", A, SE, OW, "selected node per row (no '_' before the row number)");
        x("p4_spec_resets", "EmberGrowthService", A, CL, NV, "talent resets (first free)");
        px("p4_chal_", "EmberGrowthService", A, PR, NV, "challenge first clear (talent point source); scanned by prefix");
        px("p4_honor_", "EmberGrowthService", A, HI, NV, "honor unlocked hint shown");
        px("p4_honortest_", "EmberGrowthService", A, AD, NV, "admin test grant");
        px("p4_af_", "EmberGrowthService", A, IT, NV, "LEGACY item affix by uid — migrated to EmberItemData.affix / cr_p1_item.affix (S1-4 done, D208); read only for v1 items, cleared when folded");
        px("p4_afp_", "EmberGrowthService", A, IT, NV, "LEGACY item reroll pity by uid — migrated to EmberItemData.afPity / cr_p1_item.af_pity (S1-4 done, D208); read only for v1 items");
        px("p4_rrn_", "EmberPayRules", A, IT, NV, "LEGACY paid reroll sequence by uid — migrated to EmberItemData.rerollN / cr_p1_item.reroll_n (S1-4 done, D208); read only for v1 items");
        px("p4_rro_", "EmberPayRules", A, TX, ZR, "LEGACY reroll paid, result pending (n*2+lock) — never written since D208 (the roll commits in the cr_p1_txn row with the payment); recoverRolls still settles old ones");
        // 2.4 season (EmberSeason)
        px("p3_goal_", "EmberSeason", W, PR, RL, "weekly goal progress");
        px("p3_goalpay_", "EmberSeason", W, CL, RL, "weekly goal paid (+ goal id | all)");
        x("p3_badge", "EmberSeason", A, AS, ZR, "ember badge balance (festival writes too)");
        x("p3_grad", "EmberSeason", A, PR, NV, "epoch day of own Q07 first clear");
        px("p3_season_", "EmberSeason", A, ST, NV, "season award times earned");
        px("p3_seasonlast_", "EmberSeason", A, CL, NV, "last season an award was earned");
        // 2.5 sign-in / online (EmberSignService)
        x("p1_sign_mask", "EmberSignService", M, CL, RL, "signed-day bitmap");
        x("p1_sign_mk", "EmberSignService", M, CL, RL, "make-ups this month");
        x("p1_sign_mkd", "EmberSignService", D, CL, RL, "made up today");
        x("p1_sign_last", "EmberSignService", A, CL, OW, "last real sign-in yyyymmdd (clock-back guard)");
        x("p1_on_min", "EmberSignService", D, PR, RL, "online minutes counted today (AFK world counts, cap 120)");
        x("p1_on_claim", "EmberSignService", D, CL, RL, "online tier claims bitmap");
        x("p1_on_last", "EmberSignService", A, TX, OW, "day the online counters belong to");
        // 2.6 AFK court (EmberAfkService)
        x("p1_afk_kill", "EmberAfkService", D, CL, RL, "paid kills today (online + offline)");
        x("p1_afk_offk", "EmberAfkService", D, CL, RL, "offline kills today");
        px("p1_afk_acc_", "EmberAfkService", D, AS, RL, "fractional resource carry; dropping it at day roll is by design");
        x("p1_afk_quit", "EmberAfkService", A, TX, OW, "logout epoch minute");
        x("p1_afk_kpm", "EmberAfkService", A, ST, OW, "kills/min x100 EMA");
        x("p1_afk_fmin", "EmberAfkService", A, ST, OW, "auto-combat minutes (cap 10000)");
        x("p1_afk_lt", "EmberAfkService", A, SE, OW, "last auto-combat tier");
        // 2.7 vault / gear library
        px("p1_vbound_", "EmberVault", A, AS, ZR, "account-bound units of a warehouse entry (WarehouseService reads)");
        x("p5_vault_off", "EmberVault", A, SE, ZR, "auto-deposit off");
        x("p5_glib_mode", "EmberGearLib", A, SE, ZR, "gear library mode");
        // 2.8 delivery / payment markers
        px("p1dlv_", "EmberDelivery", Period.CONST, TX, ZR, "delivered locally, DB not confirmed (save same tick)");
        px("p1paid_", "EmberDelivery", Period.CONST, TX, ZR, "forge payment marker hex(hashCode(rid)) (32-bit, REG §2.8)");
        // 2.9 codex
        px("p1_codex_", "EmberCodex", A, PR, NV, "codex entry seen (contains p1_codex_stage_, exempt)");
        px("p1_codex_stage_", "EmberCodex", A, CL, NV, "codex stage reward claimed");
        // 2.10 cosmetics (paused; registered only)
        px("p2_cosbuy_", "EmberCosmetics", A, PR, NV, "bought");
        px("p2_title_", "EmberCosmetics", A, ST, NV, "raid clears (title unlock; festival writes too)");
        x("p2_colorsel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        x("p2_flairsel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        x("p2_glowsel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        x("p2_animsel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        x("p2_titlesel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        x("p2_trailsel", "EmberCosmetics", Period.VALUE_IN_PERIOD, SE, OW, "selected id in the period slot");
        // 2.11 festival
        px("p3_fest_entry_", "EmberFestival", D, CL, RL, "festival runs today");
        px("p3_fest_charm_", "EmberFestival", A, PR, NV, "festival charm owned");
        px("p3_fest_charmon_", "EmberFestival", A, SE, ZR, "festival charm worn");
        px("p3_fest_trail_", "EmberFestival", A, PR, NV, "festival trail (cosmetic, paused)");
        px("p3_fest_xbadge_", "EmberFestival", A, CL, NV, "badge conversion done for the event");
        // 3. legacy (S0 gates keep them unwritten while P1 is on, except life_)
        add("afk_", true, "CoreRpgPlugin/AfkTierService", D, CL, RL, true, false, "legacy kill drops per item (+ afk_coin)");
        add("afk_coin", false, "CoreRpgPlugin/AfkTierService", D, CL, RL, true, false, "legacy kill coin cap");
        add("arena_coin_matches", false, "ArenaCoinRules", D, CL, RL, true, false, "arena matches that paid coin (D201)");
        add("calamity_weekly_first", false, "CalamityService", LW, CL, RL, true, false, "public calamity weekly first");
        add("ever_forge", false, "QuestService/ForgeService", A, PR, NV, true, false, "legacy quest condition");
        add("ever_anvil", false, "QuestService", A, PR, NV, true, false, "legacy quest condition");
        add("ever_enchant", false, "QuestService", A, PR, NV, true, false, "legacy quest condition");
        add("life_", true, "LifeService", Period.MIXED, CL, RL, true, false, "life offer counters (day / legacy week); still written under P1, pays no P1 asset");
        add("life_xp", false, "LifeService", A, PR, NV, true, false, "life xp");
        add("mig_quest_1110", false, "QuestService", A, PR, NV, true, false, "one-off migration flag (read only)");
        add("abyss_run_floor", false, "AbyssSettleService", A, TX, ZR, true, false, "legacy abyss pending floor");
    }

    private EmberCounters() {}

    /** All families, registry order. */
    public static List<Family> all() { return Collections.unmodifiableList(new ArrayList<Family>(BY_KEY.values())); }

    /** The family registered under exactly {@code key} (exact name or prefix), or null. */
    public static Family byKey(String key) { return key == null ? null : BY_KEY.get(key); }

    /** The family a counter name (no {@code @period}) belongs to: exact match first, else the longest matching prefix; null = unregistered. */
    public static Family lookup(String name) {
        if (name == null) return null;
        int at = name.indexOf('@');
        if (at >= 0) name = name.substring(0, at);
        Family exact = BY_KEY.get(name);
        if (exact != null && !exact.prefix) return exact;
        Family best = null;
        for (Family f : BY_KEY.values()) if (f.prefix && f.matches(name) && (best == null || f.key.length() > best.key.length())) best = f;
        return best;
    }

    /**
     * D207 (ARCH S1-5 · REG §4-5): periodic CLAIM families get the clock-rollback guard in
     * {@link town.sunshine.corerpg.PlayerData#periodCount} / {@code addPeriodCount}: while the blob already holds a key of
     * the same name for a <em>later</em> period, the older period reads as saturated and cannot be written, so a server
     * clock set back cannot re-open a daily / weekly / monthly claim. Assets, progress, stats and hints are not guarded.
     */
    public static boolean clockGuarded(Family f) {
        if (f == null || f.category != Category.CLAIM) return false;
        return f.period == Period.DAY || f.period == Period.PWEEK || f.period == Period.LWEEK || f.period == Period.MONTH;
    }

    private static final java.util.regex.Pattern P_DAY = java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final java.util.regex.Pattern P_MONTH = java.util.regex.Pattern.compile("\\d{4}-\\d{2}");
    private static final java.util.regex.Pattern P_LWEEK = java.util.regex.Pattern.compile("\\d{4}-W\\d{2}");
    private static final java.util.regex.Pattern P_PWEEK = java.util.regex.Pattern.compile("w-?\\d{1,12}");

    /**
     * Is period {@code a} strictly later than period {@code b} for this period kind? False when either side does not
     * parse in that kind's format (unknown shapes are never guarded), and for non-periodic kinds.
     */
    public static boolean laterPeriod(Period kind, String a, String b) {
        if (kind == null || a == null || b == null) return false;
        switch (kind) {
            case DAY: return P_DAY.matcher(a).matches() && P_DAY.matcher(b).matches() && a.compareTo(b) > 0;
            case MONTH: return P_MONTH.matcher(a).matches() && P_MONTH.matcher(b).matches() && a.compareTo(b) > 0;
            case LWEEK: return P_LWEEK.matcher(a).matches() && P_LWEEK.matcher(b).matches() && a.compareTo(b) > 0;
            case PWEEK:
                if (!P_PWEEK.matcher(a).matches() || !P_PWEEK.matcher(b).matches()) return false;
                return Long.parseLong(a.substring(1)) > Long.parseLong(b.substring(1));
            default: return false;
        }
    }

    /** Pairs (a, b) where prefix family a swallows b's key and the pair is not in {@link #PREFIX_EXEMPT}. Empty = clean. */
    public static List<String> prefixConflicts() {
        List<String> out = new ArrayList<String>();
        for (Family a : BY_KEY.values()) {
            if (!a.prefix) continue;
            for (Family b : BY_KEY.values()) {
                if (a == b || !b.key.startsWith(a.key)) continue;
                boolean ok = false;
                for (String[] e : PREFIX_EXEMPT) if (e[0].equals(a.key) && e[1].equals(b.key)) ok = true;
                if (!ok) out.add(a.key + " swallows " + b.key);
            }
        }
        return out;
    }

    /**
     * Load-time check for a config weekly claim key ({@code rush.<key>.claim}): null when it is the main rush claim, a
     * registered config claim, or an unregistered {@code p4_*} name that no other family owns; otherwise the reason.
     */
    public static String rushClaimError(String claim) {
        if (claim == null || claim.isEmpty()) return "claim counter missing";
        if (claim.indexOf('@') >= 0) return "claim counter " + claim + " must not contain '@'";
        Family f = lookup(claim);
        if (f == null) return null; // new config claim: allowed, EmberCountersTest asks for a registry row before release
        if (f.category == Category.CLAIM && f.period == Period.PWEEK && (f.config || f.key.equals("p4_rush_claim"))) return null;
        return "claim counter " + claim + " collides with registered family " + f;
    }
}
