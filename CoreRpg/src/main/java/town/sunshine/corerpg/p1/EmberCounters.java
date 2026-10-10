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
 * <p>Numbers and keys are unchanged (balance_version 58); renames proposed by the REG doc (codex, insignia) are not done.
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
        x("p1_convert_path", "EmberConvertPath", A, SE, OW, "D469 convert path 0=none 1=scorch 2=burst 3=sustain");
        x("p1_convert_path_offer", "EmberConvertPath", W, CL, RL, "D469 once-per-week offer latch");
        x("p1_echo_path", "EmberEchoPath", A, SE, OW, "D468 echo path 0=none 1..7=q01..q07");
        x("p1_echo_path_offer", "EmberEchoPath", W, CL, RL, "D468 weekly/@all offer latch");
        x("p1_brand_path", "EmberBrandPath", A, SE, OW, "D467 brand path 0=none 1=hunt 2=ember 3=bind");
        x("p1_brand_path_offer", "EmberBrandPath", W, CL, RL, "D467 once-per-week offer latch");
        x("p1_dual_lead", "EmberDualLead", A, SE, OW, "D466 dual-sig path 0=none 1=blade 2=charm 3=both");
        x("p1_dual_lead_offer", "EmberDualLead", A, CL, RL, "D466 once latch after Q03 dual unlock");
        x("p1_set_focus", "EmberSetFocus", A, SE, OW, "D465 set-family focus 0=none 1=scorch 2=burst 3=sustain");
        x("p1_set_focus_offer", "EmberSetFocus", W, CL, RL, "D465 once-per-week offer latch @rotationWeekKey");
        x("p1_sig_chase_ready", "EmberSigChase", A, SE, OW, "D470 ready-cue latch = Def.code while marks≥need");
        x("p1_sig_chase", "EmberSigChase", A, SE, OW, "D464 imprint chase Def.code 0=none 1..15=L01..L15");
        x("p1_sig_chase_offered", "EmberSigChase", A, PR, NV, "D464 imprint-unlock first-pick latch");
        px("p1_mode_path_offered_", "EmberModePath", A, PR, NV, "D462 Q04/Q05/Q06 first-path pick latch");
        x("p1_forge_goal", "EmberForgeGoal", A, SE, OW, "D461 weekly forge craft-goal id 0=none 1=enh 2=ref 3=brand 4=roll 5=convert");
        x("p1_forge_goal_offer", "EmberForgeGoal", W, CL, RL, "D461 once-per-week offer latch @rotationWeekKey");
        x("p1_sidestep_path", "EmberSidestepPath", A, PR, NV, "D572 sidestep auto/ask/mute");
        px("p1_sidestep_path_offer", "EmberSidestepPath", A, PR, NV, "D572 weekly offer latch");
        x("p1_dodge_path", "EmberDodgePath", A, PR, NV, "D571 dodge auto/ask/mute");
        px("p1_dodge_path_offer", "EmberDodgePath", A, PR, NV, "D571 weekly offer latch");
        x("p1_free_path", "EmberFreePath", A, PR, NV, "D570 free auto/ask/mute");
        px("p1_free_path_offer", "EmberFreePath", A, PR, NV, "D570 weekly offer latch");
        x("p1_breach_path", "EmberBreachPath", A, PR, NV, "D569 breach auto/ask/mute");
        px("p1_breach_path_offer", "EmberBreachPath", A, PR, NV, "D569 weekly offer latch");
        x("p1_relay_path", "EmberRelayPath", A, PR, NV, "D568 relay auto/ask/mute");
        px("p1_relay_path_offer", "EmberRelayPath", A, PR, NV, "D568 weekly offer latch");
        x("p1_beacon_path", "EmberBeaconPath", A, PR, NV, "D567 beacon auto/ask/mute");
        px("p1_beacon_path_offer", "EmberBeaconPath", A, PR, NV, "D567 weekly offer latch");
        x("p1_hold_path", "EmberHoldPath", A, PR, NV, "D566 hold auto/ask/mute");
        px("p1_hold_path_offer", "EmberHoldPath", A, PR, NV, "D566 weekly offer latch");
        x("p1_guard_path", "EmberGuardPath", A, PR, NV, "D565 guard auto/ask/mute");
        px("p1_guard_path_offer", "EmberGuardPath", A, PR, NV, "D565 weekly offer latch");
        x("p1_smash_path", "EmberSmashPath", A, PR, NV, "D564 smash auto/ask/mute");
        px("p1_smash_path_offer", "EmberSmashPath", A, PR, NV, "D564 weekly offer latch");
        x("p1_burst_path", "EmberBurstPath", A, PR, NV, "D563 burst auto/ask/mute");
        px("p1_burst_path_offer", "EmberBurstPath", A, PR, NV, "D563 weekly offer latch");
        x("p1_rally_path", "EmberRallyPath", A, PR, NV, "D562 rally auto/ask/mute");
        px("p1_rally_path_offer", "EmberRallyPath", A, PR, NV, "D562 weekly offer latch");
        x("p1_thaw_path", "EmberThawPath", A, PR, NV, "D561 thaw auto/ask/mute");
        px("p1_thaw_path_offer", "EmberThawPath", A, PR, NV, "D561 weekly offer latch");
        x("p1_duck_path", "EmberDuckPath", A, PR, NV, "D560 duck auto/ask/mute");
        px("p1_duck_path_offer", "EmberDuckPath", A, PR, NV, "D560 weekly offer latch");
        x("p1_hunt_path", "EmberHuntPath", A, PR, NV, "D559 hunt auto/ask/mute");
        px("p1_hunt_path_offer", "EmberHuntPath", A, PR, NV, "D559 weekly offer latch");
        x("p1_link_path", "EmberLinkPath", A, PR, NV, "D558 link auto/ask/mute");
        px("p1_link_path_offer", "EmberLinkPath", A, PR, NV, "D558 weekly offer latch");
        x("p1_call_path", "EmberCallPath", A, PR, NV, "D557 call auto/ask/mute");
        px("p1_call_path_offer", "EmberCallPath", A, PR, NV, "D557 weekly offer latch");
        x("p1_gate_path", "EmberGatePath", A, PR, NV, "D556 gate auto/ask/mute");
        px("p1_gate_path_offer", "EmberGatePath", A, PR, NV, "D556 weekly offer latch");
        x("p1_pack_path", "EmberPackPath", A, PR, NV, "D555 pack auto/ask/mute");
        px("p1_pack_path_offer", "EmberPackPath", A, PR, NV, "D555 weekly offer latch");
        x("p1_flee_path", "EmberFleePath", A, PR, NV, "D554 flee auto/ask/mute");
        px("p1_flee_path_offer", "EmberFleePath", A, PR, NV, "D554 weekly offer latch");
        x("p1_chest_path", "EmberChestPath", A, PR, NV, "D553 chest auto/ask/mute");
        px("p1_chest_path_offer", "EmberChestPath", A, PR, NV, "D553 weekly offer latch");
        x("p1_sight_path", "EmberSightPath", A, PR, NV, "D552 sight auto/ask/mute");
        px("p1_sight_path_offer", "EmberSightPath", A, PR, NV, "D552 weekly offer latch");
        x("p1_charm_path", "EmberCharmPath", A, PR, NV, "D551 charm auto/ask/mute");
        px("p1_charm_path_offer", "EmberCharmPath", A, PR, NV, "D551 weekly offer latch");
        x("p1_grip_path", "EmberGripPath", A, PR, NV, "D550 grip auto/ask/mute");
        px("p1_grip_path_offer", "EmberGripPath", A, PR, NV, "D550 weekly offer latch");
        x("p1_armor_path", "EmberArmorPath", A, PR, NV, "D549 armor auto/ask/mute");
        px("p1_armor_path_offer", "EmberArmorPath", A, PR, NV, "D549 weekly offer latch");
        x("p1_sip_path", "EmberSipPath", A, PR, NV, "D548 sip auto/ask/mute");
        px("p1_sip_path_offer", "EmberSipPath", A, PR, NV, "D548 weekly offer latch");
        x("p1_bread_path", "EmberBreadPath", A, PR, NV, "D547 bread auto/ask/mute");
        px("p1_bread_path_offer", "EmberBreadPath", A, PR, NV, "D547 weekly offer latch");
        x("p1_brew_path", "EmberBrewPath", A, PR, NV, "D546 brew auto/ask/mute");
        px("p1_brew_path_offer", "EmberBrewPath", A, PR, NV, "D546 weekly offer latch");
        x("p1_rod_path", "EmberRodPath", A, PR, NV, "D545 rod auto/ask/mute");
        px("p1_rod_path_offer", "EmberRodPath", A, PR, NV, "D545 weekly offer latch");
        x("p1_bite_path", "EmberBitePath", A, PR, NV, "D544 bite auto/ask/mute");
        px("p1_bite_path_offer", "EmberBitePath", A, PR, NV, "D544 weekly offer latch");
        x("p1_cook_path", "EmberCookPath", A, PR, NV, "D543 cook auto/ask/mute");
        px("p1_cook_path_offer", "EmberCookPath", A, PR, NV, "D543 weekly offer latch");
        x("p1_makeup_path", "EmberMakeupPath", A, PR, NV, "D542 makeup auto/ask/mute");
        px("p1_makeup_path_offer", "EmberMakeupPath", A, PR, NV, "D542 weekly offer latch");
        x("p1_credit_path", "EmberCreditPath", A, PR, NV, "D541 credit spend/hold/ask");
        px("p1_credit_path_offer", "EmberCreditPath", A, PR, NV, "D541 weekly offer latch");
        x("p1_bank_path", "EmberBankPath", A, PR, NV, "D540 bank auto/ask/mute");
        px("p1_bank_path_offer", "EmberBankPath", A, PR, NV, "D540 weekly offer latch");
        x("p1_dose_path", "EmberDosePath", A, PR, NV, "D539 dose auto/ask/mute");
        px("p1_dose_path_offer", "EmberDosePath", A, PR, NV, "D539 weekly offer latch");
        x("p1_scrap_path", "EmberScrapPath", A, PR, NV, "D538 scrap auto/ask/mute");
        px("p1_scrap_path_offer", "EmberScrapPath", A, PR, NV, "D538 weekly offer latch");
        x("p1_deal_path", "EmberDealPath", A, PR, NV, "D537 deal open/gate/busy");
        px("p1_deal_path_offer", "EmberDealPath", A, PR, NV, "D537 weekly offer latch");
        px("p1_deal_pend_n", "EmberDealPath", A, PR, NV, "D537 pending sale count");
        px("p1_deal_pend_coin", "EmberDealPath", A, PR, NV, "D537 pending sale coin");
        x("p1_ticket_path", "EmberTicketPath", A, PR, NV, "D536 ticket auto/ask/mute");
        px("p1_ticket_path_offer", "EmberTicketPath", A, PR, NV, "D536 weekly offer latch");
        x("p1_gem_path", "EmberGemPath", A, PR, NV, "D535 gem sharp/steady/drain/gale");
        px("p1_gem_path_offer", "EmberGemPath", A, PR, NV, "D535 weekly offer latch");
        x("p1_glow_path", "EmberGlowPath", A, PR, NV, "D534 glow auto/ask/mute");
        px("p1_glow_path_offer", "EmberGlowPath", A, PR, NV, "D534 weekly offer latch");
        x("p1_ping_path", "EmberPingPath", A, PR, NV, "D533 ping open/gate/busy");
        px("p1_ping_path_offer", "EmberPingPath", A, PR, NV, "D533 weekly offer latch");
        x("p1_title_path", "EmberTitlePath", A, PR, NV, "D532 title auto/ask/mute");
        px("p1_title_path_offer", "EmberTitlePath", A, PR, NV, "D532 weekly offer latch");
        x("p1_junk_path", "EmberJunkPath", A, PR, NV, "D531 junk auto/ask/mute");
        px("p1_junk_path_offer", "EmberJunkPath", A, PR, NV, "D531 weekly offer latch");
        x("p1_party_path", "EmberPartyPath", A, PR, NV, "D530 party open/gate/busy");
        px("p1_party_path_offer", "EmberPartyPath", A, PR, NV, "D530 weekly offer latch");
        x("p1_trail_path", "EmberTrailPath", A, PR, NV, "D529 trail auto/ask/mute");
        px("p1_trail_path_offer", "EmberTrailPath", A, PR, NV, "D529 weekly offer latch");
        x("p1_feed_path", "EmberFeedPath", A, PR, NV, "D528 feed auto/ask/mute");
        px("p1_feed_path_offer", "EmberFeedPath", A, PR, NV, "D528 weekly offer latch");
        x("p1_guild_path", "EmberGuildPath", A, PR, NV, "D527 guild open/gate/busy");
        px("p1_guild_path_offer", "EmberGuildPath", A, PR, NV, "D527 weekly offer latch");
        x("p1_stash_path", "EmberStashPath", A, PR, NV, "D526 stash auto/ask/mute");
        px("p1_stash_path_offer", "EmberStashPath", A, PR, NV, "D526 weekly offer latch");
        x("p1_pet_path", "EmberPetPath", A, PR, NV, "D525 pet auto/ask/mute");
        px("p1_pet_path_offer", "EmberPetPath", A, PR, NV, "D525 weekly offer latch");
        x("p1_mentor_path", "EmberMentorPath", A, PR, NV, "D524 mentor open/gate/busy");
        px("p1_mentor_path_offer", "EmberMentorPath", A, PR, NV, "D524 weekly offer latch");
        x("p1_mail_path", "EmberMailPath", A, PR, NV, "D523 mail auto/ask/mute");
        px("p1_mail_path_offer", "EmberMailPath", A, PR, NV, "D523 weekly offer latch");
        x("p1_codex_path", "EmberCodexPath", A, PR, NV, "D522 codex auto/ask/mute");
        px("p1_codex_path_offer", "EmberCodexPath", A, PR, NV, "D522 weekly offer latch");
        x("p1_goal_path", "EmberGoalPath", A, PR, NV, "D521 goal featured/abyss/raid/bounty/flex");
        px("p1_goal_path_offer", "EmberGoalPath", A, PR, NV, "D521 weekly offer latch");
        x("p1_sign_path", "EmberSignPath", A, PR, NV, "D520 sign auto/ask/mute");
        px("p1_sign_path_offer", "EmberSignPath", A, PR, NV, "D520 weekly offer latch");
        x("p1_challenge_path", "EmberChallengePath", A, PR, NV, "D519 challenge prefer/easy/follow");
        px("p1_challenge_path_offer", "EmberChallengePath", A, PR, NV, "D519 weekly offer latch");
        x("p1_online_path", "EmberOnlinePath", A, PR, NV, "D518 online auto/ask/mute");
        px("p1_online_path_offer", "EmberOnlinePath", A, PR, NV, "D518 weekly offer latch");
        x("p1_equip_path", "EmberEquipPath", A, PR, NV, "D517 equip keep/quick/bold");
        px("p1_equip_path_offer", "EmberEquipPath", A, PR, NV, "D517 weekly offer latch");
        x("p1_friend_path", "EmberFriendPath", A, PR, NV, "D516 friend open/gate/busy");
        px("p1_friend_path_offer", "EmberFriendPath", A, PR, NV, "D516 weekly offer latch");
        x("p1_claim_path", "EmberClaimPath", A, PR, NV, "D515 claim auto/hold/brief");
        px("p1_claim_path_offer", "EmberClaimPath", A, PR, NV, "D515 weekly offer latch");
        x("p1_daily_path", "EmberDailyPath", A, PR, NV, "D514 daily light/full/flex");
        px("p1_daily_path_offer", "EmberDailyPath", A, PR, NV, "D514 weekly offer latch");
        x("p1_recruit_path", "EmberRecruitPath", A, PR, NV, "D513 recruit open/gate/mute");
        px("p1_recruit_path_offer", "EmberRecruitPath", A, PR, NV, "D513 weekly offer latch");
        x("p1_bar_path", "EmberBarPath", A, PR, NV, "D512 bar right/left/key5");
        px("p1_bar_path_offer", "EmberBarPath", A, PR, NV, "D512 weekly offer latch");
        x("p1_fee_path", "EmberFeePath", A, PR, NV, "D511 fee coin/mark/auto");
        px("p1_fee_path_offer", "EmberFeePath", A, PR, NV, "D511 weekly offer latch");
        x("p1_break_path", "EmberBreakPath", A, PR, NV, "D510 break full/short/snap");
        px("p1_break_path_offer", "EmberBreakPath", A, PR, NV, "D510 weekly offer latch");
        x("p1_rest_path", "EmberRestPath", A, PR, NV, "D509 rest full/light/bare");
        px("p1_rest_path_offer", "EmberRestPath", A, PR, NV, "D509 weekly offer latch");
        x("p1_bounty_path", "EmberBountyPath", A, PR, NV, "D508 bounty affix/event/both");
        px("p1_bounty_path_offer", "EmberBountyPath", A, PR, NV, "D508 weekly offer latch");
        x("p1_fail_path", "EmberFailPath", A, PR, NV, "D507 fail keep/light/skip");
        px("p1_fail_path_offer", "EmberFailPath", A, PR, NV, "D507 weekly offer latch");
        x("p1_room_path", "EmberRoomPath", A, PR, NV, "D506 room front/mid/back");
        px("p1_room_path_offer", "EmberRoomPath", A, PR, NV, "D506 weekly offer latch");
        x("p1_refund_path", "EmberRefundPath", A, PR, NV, "D505 refund full/light/bare");
        px("p1_refund_path_offer", "EmberRefundPath", A, PR, NV, "D505 weekly offer latch");
        x("p1_twist_path", "EmberTwistPath", A, PR, NV, "D504 twist prim/alt/rotate");
        px("p1_twist_path_offer", "EmberTwistPath", A, PR, NV, "D504 weekly offer latch");
        x("p1_short_path", "EmberShortPath", A, PR, NV, "D503 short fc/day/late");
        px("p1_short_path_offer", "EmberShortPath", A, PR, NV, "D503 weekly offer latch");
        x("p1_prep_path", "EmberPrepPath", A, PR, NV, "D502 prep light/full/bare");
        px("p1_prep_path_offer", "EmberPrepPath", A, PR, NV, "D502 weekly offer latch");
        x("p1_extra_path", "EmberExtraPath", A, PR, NV, "D501 extra treasure/elite/chest");
        px("p1_extra_path_offer", "EmberExtraPath", A, PR, NV, "D501 weekly offer latch");
        x("p1_spice_path", "EmberSpicePath", A, PR, NV, "D500 spice blaze/control/objective");
        px("p1_spice_path_offer", "EmberSpicePath", A, PR, NV, "D500 weekly offer latch");
        x("p1_stride_path", "EmberStridePath", A, PR, NV, "D499 stride push/bail/bare");
        px("p1_stride_path_offer", "EmberStridePath", A, PR, NV, "D499 weekly offer latch");
        x("p1_tune_path", "EmberTunePath", A, PR, NV, "D498 tune sharp/ward/full");
        px("p1_tune_path_offer", "EmberTunePath", A, PR, NV, "D498 weekly offer latch");
        x("p1_seal_path", "EmberSealPath", A, PR, NV, "D497 seal hunt/ember/bind");
        px("p1_seal_path_offer", "EmberSealPath", A, PR, NV, "D497 weekly offer latch");
        x("p1_trial_path", "EmberTrialPath", A, PR, NV, "D496 trial cautious/aggressive/pressure");
        px("p1_trial_path_offer", "EmberTrialPath", A, PR, NV, "D496 weekly offer latch");
        x("p1_posture_path", "EmberPosturePath", A, PR, NV, "D495 posture strike/guard/sweep");
        px("p1_posture_path_offer", "EmberPosturePath", A, PR, NV, "D495 weekly offer latch");
        x("p1_flex_path", "EmberFlexPath", A, PR, NV, "D494 flex path on/off");
        px("p1_flex_path_offer", "EmberFlexPath", A, PR, NV, "D494 weekly offer latch");
        x("p1_family_path", "EmberFamilyPath", A, PR, NV, "D493 family combat path scorch/burst/sustain");
        px("p1_family_path_offer", "EmberFamilyPath", A, PR, NV, "D493 weekly offer latch");
        x("p1_attune_path", "EmberAttunePath", A, PR, NV, "D492 attune path origin/alt");
        px("p1_attune_path_offer", "EmberAttunePath", A, PR, NV, "D492 weekly offer latch");
        x("p1_step_path", "EmberStepPath", A, PR, NV, "D491 step path forward/back");
        px("p1_step_path_offer", "EmberStepPath", A, PR, NV, "D491 weekly offer latch");
        x("p1_shape_path", "EmberShapePath", A, PR, NV, "D490 slash shape path fan/line/ring");
        px("p1_shape_path_offer", "EmberShapePath", A, PR, NV, "D490 weekly offer latch");
        x("p1_featured_path", "EmberFeaturedPath", A, PR, NV, "D488 featured path challenge/normal");
        px("p1_featured_path_offer", "EmberFeaturedPath", A, PR, NV, "D488 weekly offer latch");
        x("p1_abyss_path", "EmberAbyssPath", A, PR, NV, "D486 abyss path push/farm");
        px("p1_abyss_path_offer", "EmberAbyssPath", A, PR, NV, "D486 weekly offer latch");
        x("p1_raid_path", "EmberRaidPath", A, PR, NV, "D483 raid path r01/r02/r03");
        px("p1_raid_path_offer", "EmberRaidPath", A, PR, NV, "D483 weekly offer latch");
        x("p1_pledge_path", "EmberPledgePath", A, PR, NV, "D481 pledge path lean/reverse/both");
        px("p1_pledge_path_offer", "EmberPledgePath", A, PR, NV, "D481 weekly offer latch");
        x("p1_counter_path", "EmberCounterPath", A, PR, NV, "D480 counterplay path wall/whiff/break");
        px("p1_counter_path_offer", "EmberCounterPath", A, PR, NV, "D480 weekly offer latch");
        x("p1_shape_pick_offered", "EmberSkillUnlock", A, PR, NV, "D460 Q04 first-pick latch (buttons sent once)");
        x("p1_step_pick_offered", "EmberSkillUnlock", A, PR, NV, "D460 Q05 first-pick latch (buttons sent once)");
        x("p1_step_dir", "FlexSkillService", A, SE, OW, "D219 身法方向 0=前冲 1=后撤 (Q01+; shares 14s CD; 火痕·后撤 ignites takeoff)");
        x("p1_starter", "EmberRunService", A, CL, NV, "starter pack claimed");
        x("p1_six_mig", "EmberSixMigration", A, CL, NV, "D318 six-slot migration done flag (record file plugins/CoreRpg/p1-six/<uuid>.yml, not the PlayerData blob)");
        x("p1_blank_tenths", "EmberForgeService", A, AS, ZR, "D318 six-slot armor dismantle 胚零头 in tenths of a blank (0.1 × tier; 10 → 1 blank)");
        px("p1_mark_t", "EmberRunService/EmberPay", A, AS, ZR, "forge marks t1..t3; EmberDelivery may re-credit (MARK_COUNTER)");
        x("p2_rotation", "EmberRunService", W, CL, RL, "featured-week extra runs (weekly_cap 3)");
        px("p2_raid_", "EmberRaidService", W, CL, RL, "raid weekly cap per cap_group / map");
        x("p2_abyss_best", "EmberAbyssService", A, ST, OW, "best abyss floor");
        x("p2_bounty", "EmberRunService", D, CL, RL, "daily bounty");
        px("p4_vb_", "EmberRunService", D, CL, RL, "variety bounties settled today per kind");
        x("p4_rush", "EmberRushService", W, ST, RL, "rush attempts (stat only since D160)");
        // D296 W1b account first-of-kind event teach
        px("p1_evteach_", "EmberEventTeach", A, HI, NV, "D296 room-event kind first flash (@all)");
        // D297 W1c weekly attune confirm shown
        x("p1_attuneprompt", "EmberGrowthService", LW, HI, RL, "D297 weekly attune enter confirm shown");
        // D298 playfeel telemetry (STAT · PWEEK · design §3)
        x("p1_pf_runs", "EmberPlayfeelTelemetry", W, ST, RL, "D298 eligible q01-q07 settle/fail runs this P-week");
        x("p1_pf_wall", "EmberPlayfeelTelemetry", W, ST, RL, "D298 wall counterplay successes");
        x("p1_pf_whiff", "EmberPlayfeelTelemetry", W, ST, RL, "D298 whiff counterplay successes");
        x("p1_pf_break", "EmberPlayfeelTelemetry", W, ST, RL, "D298 break counterplay successes");
        x("p1_pf_evt_roll", "EmberPlayfeelTelemetry", W, ST, RL, "D298 runs with event room rolled");
        x("p1_pf_evt_ok", "EmberPlayfeelTelemetry", W, ST, RL, "D298 runs with event completed");
        x("p1_pf_sig_wear", "EmberPlayfeelTelemetry", W, ST, RL, "D298 runs with ≥1 active signature");
        x("p1_pf_sig_alt", "EmberPlayfeelTelemetry", W, ST, RL, "D298 runs with ≥1 attune edition active");
        x("p1_pf_vb_hit", "EmberPlayfeelTelemetry", W, ST, RL, "D298 runs with variety-bounty progress");

        x("p4_rush_claim", "EmberRushService", W, CL, RL, "main rush weekly reward claim");
        add("p4_outpost_claim", false, "ember-v1-runs.yml rush.claim", W, CL, RL, false, true, "outpost weekly claim (config)");
        add("p4_echo_claim", false, "ember-v1-runs.yml rush.claim", W, CL, RL, false, true, "echo weekly claim, shared by echo_q01..q07 (config)");
        px("p1_pledge_", "EmberPledgeService", A, SE, NV, "Q06 chosen pledge rule");
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
        x("p4_conv", "EmberGrowthService", LW, CL, RL, "D431 weekly family convert (DailyService.weekId)");
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
        x("p1_sx01_day", "EmberShortService", D, CL, RL, "short expedition sx01 rewarded clears today (S40)");
        x("p1_sx02_day", "EmberShortService", D, CL, RL, "short expedition sx02 rewarded clears today (S41)");
        x("p1_sx03_day", "EmberShortService", D, CL, RL, "short expedition sx03 rewarded clears today (S42)");
        x("p1_sx04_day", "EmberShortService", D, CL, RL, "short expedition sx04 rewarded clears today (S43)");
        x("p1_sx05_day", "EmberShortService", D, CL, RL, "short expedition sx05 rewarded clears today (S44)");
        x("p1_sx06_day", "EmberShortService", D, CL, RL, "short expedition sx06 rewarded clears today (S45)");
        x("p1_sx07_day", "EmberShortService", D, CL, RL, "short expedition sx07 rewarded clears today (S46)");
        x("p1_sx08_day", "EmberShortService", D, CL, RL, "short expedition sx08 rewarded clears today (S47)");
        x("p1_sx09_day", "EmberShortService", D, CL, RL, "short expedition sx09 rewarded clears today (S48)");
        x("p1_sx10_day", "EmberShortService", D, CL, RL, "short expedition sx10 rewarded clears today (S49)");
        x("p1_sx11_day", "EmberShortService", D, CL, RL, "short expedition sx11 rewarded clears today (S50)");
        x("p1_sx12_day", "EmberShortService", D, CL, RL, "short expedition sx12 rewarded clears today (S51)");
        x("p1_sx13_day", "EmberShortService", D, CL, RL, "short expedition sx13 rewarded clears today (S52)");
        x("p1_sx14_day", "EmberShortService", D, CL, RL, "short expedition sx14 rewarded clears today (S53)");
        x("p1_sx15_day", "EmberShortService", D, CL, RL, "short expedition sx15 rewarded clears today (S54)");
        x("p1_sx16_day", "EmberShortService", D, CL, RL, "short expedition sx16 rewarded clears today (S55)");
        x("p1_sx17_day", "EmberShortService", D, CL, RL, "short expedition sx17 rewarded clears today (S56)");
        x("p1_sx18_day", "EmberShortService", D, CL, RL, "short expedition sx18 rewarded clears today (S57)");
        x("p1_sx19_day", "EmberShortService", D, CL, RL, "short expedition sx19 rewarded clears today (S58)");
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
        if (f.category == Category.CLAIM && (f.period == Period.PWEEK || f.period == Period.DAY) && (f.config || f.key.equals("p4_rush_claim") || f.key.startsWith("p1_sx"))) return null;
        return "claim counter " + claim + " collides with registered family " + f;
    }
}
