package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

import java.util.List;
import java.util.Locale;

/**
 * D240 / ARCH S3-11: PlaceholderAPI {@code %corerpg_p1_*%} split out of {@link EmberRunService} into named sections,
 * with <b>no behaviour change</b> (same keys, same values, same first-match order).
 * <p>{@link #route} is a Bukkit-free, ordered copy of the old if-chain: it returns the section that the old code would
 * have entered first. A section returns {@code null} only where the old chain <i>fell through</i> (rush_ entry not in the
 * rush table, season / cosmetics / festival absent or declining) and then — exactly like before — the per-map
 * {@code <map>_<field>} tail ({@link Section#MAP}) answers. Every other section always answers non-null.
 * <p>{@link EmberRunService#placeholder} is a one-line delegate; {@code CoreRpgExpansion} still strips {@code p1_}.
 */
public final class EmberRunPapi {

    /** Section of the {@code p1_} namespace (declaration order = old if-chain order of first match). */
    public enum Section {
        /** pass_ / passd_ / target / marks_t / pending / active / next / failrefund / recruits */
        ENTRY,
        /** vbounty / rush / pledge_ / rush_&lt;entry&gt; (falls through when not a rush entry) */
        RUSH,
        /** sign_ / online_ / afk_ / sig_ / reroll_ / honor_ / spec_ (delegated services) */
        GROWTH,
        /** forge_t2 / forge_t3 / challenge / access_q01 / access_q07 / q07done / q0Ndone */
        GATE,
        /** featured / featured_key / modifier / rule_ / bounty / loot_ / mapid_ */
        ROTATION,
        /** top_abyss_N / top_featured_N */
        BOARD,
        /** goal* / season* / sboard_ / srank_ / badges (falls through when season absent / declines) */
        SEASON,
        /** shop* / shop_ / title / honors (shop keys fall through when cosmetics absent / declines) */
        COSMETIC,
        /** fest_ (falls through when festival absent) */
        FESTIVAL,
        /** abyss_best / abyss_state / abyss_tN */
        ABYSS,
        /** raid_&lt;raid&gt; */
        RAID,
        /** awaken_route / awaken / awaken_next / set_progress / stats / ehp / blade / charm / D299 next_* / held_next_* / D307 held_*_cost|lack / D333 held_is_armor */
        LOADOUT,
        /** codex* */
        CODEX,
        /** D318 armor / armor_* (六槽护甲页, {@link EmberSixPapi}) */
        ARMOR,
        /** D395 short day-cap + D406 fc aggregate: sx0N_day* / sx_day_left_sum / sx_fc_left / sx_fc_pending_line */
        SHORT,
        /** D404 vault balance + recipe gap: vault_* / recipe_gap_* */
        VAULT,
        /** tail: &lt;map&gt;_&lt;state|open|cleared|name|cost|tier|purpose|fc&gt;, else "" */
        MAP
    }

    private final EmberRunService runs;

    EmberRunPapi(EmberRunService runs) {
        this.runs = runs;
    }

    // ------------------------------------------------------------------ Bukkit-free routing / texts

    /** Ordered copy of the pre-D240 if-chain (first match wins). Never null. */
    public static Section route(String key) {
        if (key == null) return Section.MAP;
        if (key.startsWith("pass_")) return Section.ENTRY;
        if ("target".equals(key)) return Section.ENTRY;
        if (key.startsWith("marks_t")) return Section.ENTRY;
        if ("pending".equals(key)) return Section.ENTRY;
        if ("active".equals(key)) return Section.ENTRY;
        if ("vbounty".equals(key)) return Section.RUSH;
        if ("rush".equals(key)) return Section.RUSH;
        if (key.startsWith("pledge_")) return Section.RUSH;
        if (key.startsWith("rush_")) return Section.RUSH;
        if (key.startsWith("passd_")) return Section.ENTRY;
        if ("afk_rec_flower".equals(key) || "afk_rec_flower_name".equals(key)
                || "afk_rec_flower_kind".equals(key) || "afk_rec_flower_ok".equals(key)) return Section.LOADOUT; // TIP-CJ D13315 before afk_→GROWTH
        if ("brand_back".equals(key)) return Section.GROWTH; // D13323 TIP-CR before brand_path→LOADOUT
        if ("convert_back".equals(key)) return Section.GROWTH; // D13324 TIP-CS before convert_path→LOADOUT
        if ("forgeroll_back".equals(key)) return Section.LOADOUT; // D13325 TIP-CT forgeroll return label
        if (key.startsWith("sign_") || key.startsWith("online_") || key.startsWith("afk_") || key.startsWith("sig_")
                || key.startsWith("reroll_") || key.startsWith("honor_") || key.startsWith("spec_")) return Section.GROWTH;
        if ("identity_settle".equals(key) || key.startsWith("identity_settle_")) return Section.GATE; // TIP-DC D13334
        if ("challenge_settle".equals(key) || key.startsWith("challenge_settle_")
                || "challenge_path".equals(key) || (key != null && key.startsWith("challenge_path_"))) return Section.GATE; // TIP-DA D13332 + TIP-DF D13337
        if ("forge_t2".equals(key) || "forge_t3".equals(key) || "challenge".equals(key) || "q07done".equals(key)
                || "access_q01".equals(key) || "access_q07".equals(key)) return Section.GATE; // TIP-BR MG2
        if (isQDone(key)) return Section.GATE;
        if ("featured".equals(key) || "modifier".equals(key) || key.startsWith("rule_")) return Section.ROTATION;
        if (key.startsWith("top_abyss_") || key.startsWith("top_featured_")) return Section.BOARD;
        if (key.startsWith("goal") || key.startsWith("season") || key.startsWith("sboard_") || key.startsWith("srank_")
                || "badges".equals(key)) return Section.SEASON;
        if (key.startsWith("shop")) return Section.COSMETIC; // shop* and shop_* (old: two checks, both cosmetics)
        if (key.startsWith("fest_")) return Section.FESTIVAL;
        if ("title".equals(key) || "honors".equals(key)) return Section.COSMETIC;
        if (key.startsWith("loot_") || key.startsWith("mapid_") || "mapid_board".equals(key)) return Section.ROTATION;
        if ("abyss_best".equals(key) || "abyss_hope".equals(key)) return Section.ABYSS;
        if ("featured_settle".equals(key) || (key != null && key.startsWith("featured_settle_"))) return Section.ROTATION; // TIP-DD D13335
        if ("featured_path".equals(key) || (key != null && key.startsWith("featured_path_"))) return Section.ROTATION; // TIP-DE D13336
        if ("bounty".equals(key) || "bounty_hope".equals(key) || "featured_hope".equals(key)) return Section.ROTATION;
        if ("set_focus".equals(key) || "set_focus_line".equals(key)) return Section.LOADOUT; // D465
        if ("setchase_line".equals(key)) return Section.LOADOUT; // D12266 hub 套装 menuLine
        if ("dual_lead".equals(key) || "dual_lead_line".equals(key) || "dual_hope".equals(key)) return Section.LOADOUT; // D466/D636
        if ("brand_path".equals(key) || "brand_path_line".equals(key) || "brand_hope".equals(key)) return Section.LOADOUT; // D467/D638
        if ("echo_path".equals(key) || "echo_path_line".equals(key) || "echo_hope".equals(key)) return Section.LOADOUT; // D468
        if ("convert_path".equals(key) || "convert_path_line".equals(key) || "convert_hope".equals(key)) return Section.LOADOUT; // D469
        if ("route_pri".equals(key) || "route_sec".equals(key) || "route_hope".equals(key) || "featured_left".equals(key)) return Section.ENTRY; // D306/D635
        if ("next".equals(key)) return Section.ENTRY;
        if (key.startsWith("raid_")) return Section.RAID;
        if ("abyss_settle".equals(key) || key.startsWith("abyss_settle_")
                || "abyss_path".equals(key) || "abyss_path_label".equals(key)
                || "abyss_path_tip".equals(key) || "abyss_path_line".equals(key)) return Section.ABYSS; // TIP-DB D13333 + TIP-DI D13340
        if ("abyss_state".equals(key) || key.startsWith("abyss_t")) return Section.ABYSS;
        if ("recruits".equals(key) || "failrefund".equals(key)) return Section.ENTRY;
        if ("featured_key".equals(key)) return Section.ROTATION;
        if ("event_chase".equals(key) || "event_chase_name".equals(key) || "event_chase_ok".equals(key) || "event_soft_ready".equals(key)) return Section.ROTATION; // TIP-CE D13310
        if ("short_div".equals(key) || key.startsWith("short_div_")) return Section.SHORT; // TIP-CU D13326
        if ("short_path".equals(key) || key.startsWith("short_path_")) return Section.SHORT; // TIP-CV D13327
        if ("short_settle".equals(key) || key.startsWith("short_settle_")) return Section.SHORT; // TIP-CW D13328
        if ("short_var".equals(key) || key.startsWith("short_var_")) return Section.SHORT; // TIP-CY D13330
        if ("today_triple".equals(key) || "today_triple_name".equals(key) || "today_triple_kind".equals(key) || "today_triple_ok".equals(key)) return Section.SHORT; // TIP-CH D13313
        if ("awaken_route".equals(key) || isLoadoutKey(key)) return Section.LOADOUT;
        if (key.startsWith("codex") || "codex_hope".equals(key)) return Section.CODEX;
        if (key.startsWith("armor_")) return Section.ARMOR; // D318 (no earlier prefix matches "armor")
        if (isShortDayKey(key)) return Section.SHORT; // D395 short day-cap (before map tail sx0N_*)
        if (isVaultKey(key)) return Section.VAULT; // D404 vault / recipe gap (before map tail)
        return Section.MAP;
    }

    /** D174 stage 2b: q04done / q05done / q06done (7 chars, q0?done). */
    static boolean isQDone(String key) {
        return key.length() == 7 && key.startsWith("q0") && key.endsWith("done");
    }

    static boolean isLoadoutKey(String key) {
        return "awaken".equals(key) || "awaken_next".equals(key) || "set_progress".equals(key) || "set_run".equals(key)
                || "set_blurb".equals(key) // TIP-BI D13054 gear page set effect follows activeSet
                || "set_focus".equals(key) || "set_focus_line".equals(key)
                || "setchase_line".equals(key)
                || "dual_lead".equals(key) || "dual_lead_line".equals(key) || "dual_hope".equals(key)
                || "brand_path".equals(key) || "brand_path_line".equals(key) || "brand_hope".equals(key)
                || "echo_hope".equals(key) || "convert_hope".equals(key)
                || "echo_path".equals(key) || "echo_path_line".equals(key)
                || "convert_path".equals(key) || "convert_path_line".equals(key) || "stats".equals(key)
                || "ehp".equals(key) || "blade".equals(key) || "charm".equals(key)
                // D299 再刷短反馈：装备页近档 + 工坊手持近档
                || "blade_next_q".equals(key) || "blade_next_c".equals(key)
                || "charm_next_q".equals(key) || "charm_next_c".equals(key)
                || "held_next_q".equals(key) || "held_next_c".equals(key)
                // D307 工坊菜单诚实：本次费用 / 缺料 / 互换免费 / 分解得胚
                || "held_enhance_cost".equals(key) || "held_enhance_lack".equals(key)
                || "held_upgrade_cost".equals(key) || "held_upgrade_lack".equals(key)
                || "held_refine_lack".equals(key) || "held_quality_lack".equals(key)
                || "held_swap_cost".equals(key) || "held_dismantle_yield".equals(key)
                // D333 工坊手持甲菜单闸：主手可信 P1 甲 → 1，否则 0
                || "held_is_armor".equals(key)
                // D601 hope meters: enhance pity glance (worn + held)
                || "enhance_blade_pity".equals(key) || "enhance_charm_pity".equals(key)
                || "held_enhance_pity".equals(key)
                || "held_enhance_rate".equals(key) // TIP-BN
                // D602 quality hope meters
                || "quality_blade_hope".equals(key) || "quality_charm_hope".equals(key)
                || "held_quality_hope".equals(key)
                // D603 refine hope meters
                || "refine_blade_hope".equals(key) || "refine_charm_hope".equals(key)
                || "held_refine_hope".equals(key)
                // D608 gem clear-pity hope
                || "gem_pity_hope".equals(key)
                || "hope_now".equals(key) // D12306 hub hope glance
                || "drop_q_hope".equals(key)
                || "event_soft_hope".equals(key) // D728
                || "gearlib_hope".equals(key)
                // TIP-CJ D13315 AFK courtyard recommend flower
                || "afk_rec_flower".equals(key) || "afk_rec_flower_name".equals(key)
                || "afk_rec_flower_kind".equals(key) || "afk_rec_flower_ok".equals(key)
                || "forgeroll_back".equals(key); // D13325 TIP-CT
    }

    /** D395 day-cap + D406 fc aggregate (not per-map fields; sx0N_fc stays MAP). */
    static boolean isShortDayKey(String key) {
        if (key == null) return false;
        if ("short_settle".equals(key) || key.startsWith("short_settle_")) return true; // TIP-CW D13328
        if ("short_var".equals(key) || key.startsWith("short_var_")) return true; // TIP-CY D13330
        if ("sx_day_left_sum".equals(key)) return true;
        if ("sx_fc_left".equals(key) || "sx_fc_pending_line".equals(key) || "sx_fc_hope".equals(key)) return true; // D406/D634
        if ("sx_today".equals(key) || "sx_today_name".equals(key) || "sx_today_day_line".equals(key)) return true; // TIP-CD D13309
        // sxNN_day | sxNN_day_line | sxNN_day_left  (map key length 4: sx01..sx19..)
        if (key.length() < 8 || !key.startsWith("sx")) return false;
        int us = key.indexOf('_');
        if (us != 4) return false;
        char a = key.charAt(2), b = key.charAt(3);
        if (a < '0' || a > '9' || b < '0' || b > '9') return false;
        String rest = key.substring(5);
        return "day".equals(rest) || "day_line".equals(rest) || "day_left".equals(rest);
    }

    /** D404: vault_* / recipe_gap_* · D432 forge_gap_brand|roll|convert. */
    static boolean isVaultKey(String key) {
        if (key == null) return false;
        return "vault_shard".equals(key) || "vault_bone".equals(key) || "vault_core".equals(key) || "vault_blank".equals(key)
                || "recipe_gap_enhance1".equals(key) || "recipe_gap_upgrade_t2".equals(key) || "recipe_gap_refine1".equals(key) || "recipe_gap_quality1".equals(key) || "recipe_gap_gem".equals(key) || "recipe_gap_lock".equals(key)
                || "forge_gap_brand".equals(key) || "forge_gap_roll".equals(key) || "forge_gap_roll_floor".equals(key) || "forge_gap_convert".equals(key)
                || "forge_gap_pin".equals(key)
                || "forge_goal".equals(key) || "forge_goal_gap".equals(key) || "forge_goal_line".equals(key)
                || "forge_goal_hope".equals(key) || "vault_hope".equals(key);
    }

    /** D404: gap = max(0, need − have); Bukkit-free. */
    static int recipeGap(int need, long have) {
        long h = Math.max(0L, have);
        long n = Math.max(0L, need);
        long g = n - h;
        return g <= 0 ? 0 : (int) Math.min(Integer.MAX_VALUE, g);
    }

    /** D404 %corerpg_p1_recipe_gap_enhance1% — 强化+1 碎片 gap（镜像 enhanceCost(0).shards=4）. */
    static String recipeGapEnhance1(long shardHave) {
        EmberUpgradeRules.Cost c = EmberUpgradeRules.enhanceCost(0);
        int need = c == null ? 0 : c.shards;
        return String.valueOf(recipeGap(need, shardHave));
    }

    /**
     * D404 %corerpg_p1_recipe_gap_upgrade_t2% — T1→T2 多料拼接半行（钉死形态，非拆三键）.
     * 例 {@code 碎差N·核差M·胚差K}；镜像 upgradeCost(1)=60/12/6.
     */
    static String recipeGapUpgradeT2(long shardHave, long coreHave, long blankHave) {
        EmberUpgradeRules.Cost c = EmberUpgradeRules.upgradeCost(1);
        int sh = c == null ? 0 : c.shards, co = c == null ? 0 : c.cores, bl = c == null ? 0 : c.blanks;
        return "碎差" + recipeGap(sh, shardHave) + "·核差" + recipeGap(co, coreHave) + "·胚差" + recipeGap(bl, blankHave);
    }

    /** D404 可选 %corerpg_p1_recipe_gap_refine1% — 精工0→1 胚/骨拼接（镜像 refineCost(0)=胚3·骨5）. */
    static String recipeGapRefine1(long blankHave, long boneHave) {
        EmberUpgradeRules.Cost c = EmberUpgradeRules.refineCost(0);
        int bl = c == null ? 0 : c.blanks, bo = c == null ? 0 : c.bone;
        return "胚差" + recipeGap(bl, blankHave) + "·骨差" + recipeGap(bo, boneHave);
    }

    /** D1930 %corerpg_p1_recipe_gap_quality1% — 成色0→1 胚/骨/币（镜像 qualityCost(0)；不改价）. */
    static String recipeGapQuality1(long blankHave, long boneHave, long coinHave) {
        EmberUpgradeRules.Cost c = EmberUpgradeRules.qualityCost(0);
        int bl = c == null ? 0 : c.blanks, bo = c == null ? 0 : c.bone, co = c == null ? 0 : c.coins;
        return "胚差" + recipeGap(bl, blankHave) + "·骨差" + recipeGap(bo, boneHave)
                + "·币差" + recipeGap(co, coinHave);
    }

    /** D1983 gem craft gap 碎/骨/币（镜像 EmberGemAcquire.craftCost）. 一元 ≡ null pd 全额. */
    static String recipeGapGemCraft(long shardHave, long boneHave, long coinHave) {
        return recipeGapGemCraft(shardHave, boneHave, coinHave, null);
    }

    /** TIP-AT: soft peek GemCraftCostSoft when pd ready. */
    static String recipeGapGemCraft(long shardHave, long boneHave, long coinHave, PlayerData pd) {
        EmberUpgradeRules.Cost c = EmberGearNextHint.softCost("gem", pd, EmberGemAcquire.craftCost());
        return "碎差" + recipeGap(c.shards, shardHave) + "·骨差" + recipeGap(c.bone, boneHave)
                + "·币差" + recipeGap(c.coins, coinHave);
    }

    /** D2050 lock-reroll gap T1 mirror C11. */
    static String recipeGapLock(long shardHave, long coinHave, EmberAffix.Rules rules) {
        if (rules == null) return "碎差0·币差0";
        int shard = rules.shardFor(1) + rules.lockShardFor(1);
        int coin = rules.coinFor(1);
        return "碎差" + recipeGap(shard, shardHave) + "·币差" + recipeGap(coin, coinHave);
    }

    /** D432 %corerpg_p1_forge_gap_brand% — 烙纹合成碎差（镜像 EmberBrandRules.craftShards()=40）. 一元 ≡ null pd. */
    static String forgeGapBrand(long shardHave) {
        return forgeGapBrand(shardHave, null);
    }

    /** TIP-AT: soft peek BrandCraftSoft when pd ready. */
    static String forgeGapBrand(long shardHave, PlayerData pd) {
        EmberUpgradeRules.Cost c = EmberGearNextHint.softCost("brand", pd,
                new EmberUpgradeRules.Cost(EmberBrandRules.craftShards(), 0, 0, 0, 0));
        return String.valueOf(recipeGap(c.shards, shardHave));
    }

    /** D432 %corerpg_p1_forge_gap_roll% — 随机锻胚/币差（4 胚 + 500 币）. 一元 ≡ null pd. */
    static String forgeGapRoll(long blankHave, long coinHave) {
        return forgeGapRoll(blankHave, coinHave, null);
    }

    /** TIP-AT: soft peek ForgeRollCostSoft when pd ready. */
    static String forgeGapRoll(long blankHave, long coinHave, PlayerData pd) {
        EmberUpgradeRules.Cost c = EmberGearNextHint.softCost("roll", pd, EmberForgeRollRules.matCost());
        return "胚差" + recipeGap(c.blanks, blankHave) + "·币差" + recipeGap(c.coins, coinHave);
    }

    /** D1165 %corerpg_p1_forge_gap_roll_floor% — 下限锻胚/骨/币差（配方差 · 非 hope）. 一元 ≡ null pd. */
    static String forgeGapRollFloor(long blankHave, long boneHave, long coinHave) {
        return forgeGapRollFloor(blankHave, boneHave, coinHave, null);
    }

    /** TIP-AT: soft peek ForgeRollCostSoft on floor mat when pd ready. */
    static String forgeGapRollFloor(long blankHave, long boneHave, long coinHave, PlayerData pd) {
        EmberUpgradeRules.Cost c = EmberGearNextHint.softCost("roll", pd, EmberForgeRollRules.matCostFloor());
        return "胚差" + recipeGap(c.blanks, blankHave)
                + "·骨差" + recipeGap(c.bone, boneHave)
                + "·币差" + recipeGap(c.coins, coinHave);
    }

    /**
     * D432 %corerpg_p1_forge_gap_convert% — 转化胚/币差。
     * {@code tier} 1–3 from held piece; else T1 defaults. 一元 ≡ null pd.
     */
    static String forgeGapConvert(int tier, long blankHave, long coinHave) {
        return forgeGapConvert(tier, blankHave, coinHave, null);
    }

    /** TIP-AT: soft peek ConvertCostSoft when pd ready. */
    static String forgeGapConvert(int tier, long blankHave, long coinHave, PlayerData pd) {
        int t = (tier >= 1 && tier <= 3) ? tier : 1;
        EmberUpgradeRules.Cost c = EmberGearNextHint.softCost("convert", pd, EmberConvertRules.cost(t));
        return "胚差" + recipeGap(c.blanks, blankHave)
                + "·币差" + recipeGap(c.coins, coinHave);
    }

    /** D433 %corerpg_p1_forge_gap_pin% — 定向烙纹/币差（镜像 pinBrands/pinCoins；空手 T1）. 一元 ≡ null pd. */
    static String forgeGapPin(int tier, long brandHave, long coinHave) {
        return forgeGapPin(tier, brandHave, coinHave, null);
    }

    /** TIP-AT: soft peek BrandPinCostSoft when pd ready. */
    static String forgeGapPin(int tier, long brandHave, long coinHave, PlayerData pd) {
        int t = (tier >= 1 && tier <= 3) ? tier : 1;
        int brands = EmberBrandRules.pinBrands(t);
        int coins = EmberBrandRules.pinCoins(t);
        if (brands < 0) { brands = EmberBrandRules.pinBrands(1); coins = EmberBrandRules.pinCoins(1); }
        int[] soft = EmberGearNextHint.softPin(pd, brands, coins);
        return "烙差" + recipeGap(soft[0], brandHave) + "·币差" + recipeGap(soft[1], coinHave);
    }


    /** D144 余烬连战 menu line (weekly reward still open vs. practice only). */
    static String rushLine(boolean pays) {
        return pays ? "§a本周奖励未领 · 失败可无限重试" : "§7本周奖励已领 · 可练习（无奖励）";
    }

    /** D174 stage 2b %corerpg_p1_rush_&lt;entry&gt;% once unlocked. */
    static String rushEntryLine(boolean pays, int used, int weekly) {
        return pays ? "§a本周已领 " + used + "/" + weekly + " · 失败可无限重试" : "§7本周 " + weekly + " 次已领完 · 可练习（无奖励）";
    }

    static String needFirstClear(String mapKey) {
        return "§8需本人首通 " + mapKey.toUpperCase(Locale.ROOT);
    }

    /** P2-10 legacy weekly board row (no season). */
    static String topRowText(String name, int value, boolean abyss) {
        return name + " · " + (abyss ? "第 " + value + " 层" : value + " 次");
    }

    /** top_abyss_N / top_featured_N → N in 1..10, else -1. */
    static int topRank(String key) {
        boolean ab = key.startsWith("top_abyss_");
        int i;
        try { i = Integer.parseInt(key.substring(ab ? 10 : 13)); } catch (NumberFormatException e) { return -1; }
        return i < 1 || i > 10 ? -1 : i;
    }

    static String abyssStateLine(boolean configured, boolean open, String requires, int best, int maxStart) {
        if (!configured) return "未配置";
        if (!open) return "需本人首进或首通 " + requires.toUpperCase(Locale.ROOT);
        return "最高第 " + best + " 层 · 可开 1～" + maxStart + " 层";
    }

    /** B2.174 §19.1 装备页 actual B / H / D line. */
    static String statsLine(double b, double h, double d, double m, int level) {
        return String.format(Locale.ROOT, "攻击 %.1f · 生命 %.0f · 防御 %.0f（承伤 ×%.2f）· Lv%d", b, h, d, m, level);
    }

    static String awakenRouteLine(List<String> r) {
        return r.isEmpty() ? "" : "§7路线：" + r.get(0) + (r.size() > 1 ? " §8（还有 " + (r.size() - 1) + " 条，点开看）" : "");
    }

    // ------------------------------------------------------------------ dispatcher

    /** %corerpg_p1_&lt;key&gt;% (key already without {@code p1_}). */
    public String placeholder(Player p, String key) {
        EmberRunMaps maps = runs.maps();
        if (p == null || maps == null) return "";
        PlayerData d = runs.plugin().getDataStore().get(p.getUniqueId());
        String v;
        switch (route(key)) {
            case ENTRY: v = entry(p, d, key, maps); break;
            case RUSH: v = rush(p, d, key, maps); break;
            case GROWTH: v = growth(p, d, key); break;
            case GATE: v = gate(d, key); break;
            case ROTATION: v = rotation(d, key, maps); break;
            case BOARD: v = board(key); break;
            case SEASON: v = season(p, d, key); break;
            case COSMETIC: v = cosmetic(p, d, key); break;
            case FESTIVAL: v = festival(p, d, key); break;
            case ABYSS: v = abyss(d, key, maps); break;
            case RAID: v = runs.raid().papi(d, key.substring(5)); break; // P2-5 %corerpg_p1_raid_r01% (D233 → EmberRaidService)
            case LOADOUT: v = loadout(p, key); break;
            case CODEX: v = codex(p, d, key); break;
            case ARMOR: v = armor(p, key); break;
            case SHORT: v = shortDay(d, key, maps); break;
            case VAULT: v = vault(p, key); break;
            default: v = null;
        }
        return v != null ? v : map(d, key, maps);
    }

    // ------------------------------------------------------------------ sections

    /** D318 %corerpg_p1_armor_*% (switch gear.six_slot.enabled off → "" / armor_on 0; 待领 keys stay live, D320 ④) */
    private String armor(Player p, String key) {
        EmberSixSlotService s = EmberSixSlotService.get();
        boolean on = EmberSixSlot.enabled() && s != null;
        if (!on) return EmberSixPapi.text(false, null, s == null || p == null || !key.startsWith("armor_stash") ? 0 : s.stashCount(p.getUniqueId()), key);
        return EmberSixPapi.text(true, s.cachedView(p), s.stashCount(p.getUniqueId()), key);
    }

    /**
     * D395 %corerpg_p1_sx0N_day*% / sx_day_left_sum — read-only day-cap for short expeditions.
     * Empty/exception → day returns {@code 0}; line always present (reuses {@link EmberShortRules#dayLine}).
     */
    private String shortDay(PlayerData d, String key, EmberRunMaps maps) {
        EmberShortService shortEx = runs.shortExpedition();
        if ("short_div".equals(key) || (key != null && key.startsWith("short_div_"))) { // TIP-CU D13326
            return shortDayDiv(d, key, maps, shortEx);
        }
        if ("short_path".equals(key) || (key != null && key.startsWith("short_path_"))) { // TIP-CV D13327
            return shortPathChase(d, key);
        }
        if ("short_settle".equals(key) || (key != null && key.startsWith("short_settle_"))) { // TIP-CW D13328
            return shortSettle(d, key, maps, shortEx);
        }
        if ("short_var".equals(key) || (key != null && key.startsWith("short_var_"))) { // TIP-CY D13330
            return shortVariety(d, key);
        }
        if ("today_triple".equals(key) || "today_triple_name".equals(key)
                || "today_triple_kind".equals(key) || "today_triple_ok".equals(key)) { // TIP-CH D13313
            return todayTriple(d, key, maps, shortEx);
        }
        if ("sx_today".equals(key) || "sx_today_name".equals(key) || "sx_today_day_line".equals(key)) { // TIP-CD D13309
            String today = EmberShortRules.todayKey();
            if ("sx_today".equals(key)) return today;
            if ("sx_today_name".equals(key)) {
                EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(today);
                return m != null && m.name != null && !m.name.isEmpty() ? m.name : today;
            }
            int n = rewardedFor(d, maps, shortEx, today);
            int cap = capFor(maps, shortEx, today);
            return EmberShortRules.dayLine(n, cap);
        }
        if ("sx_fc_left".equals(key) || "sx_fc_pending_line".equals(key) || "sx_fc_hope".equals(key)) { // D406/D634
            int unpaid = 0;
            for (String mk : EmberShortRules.SHORT_KEYS) {
                EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(mk);
                if (m == null || !runs.firstCleared(d, m)) unpaid++;
            }
            unpaid = EmberShortRules.fcLeft(unpaid);
            if ("sx_fc_left".equals(key)) return String.valueOf(unpaid);
            if ("sx_fc_hope".equals(key)) return EmberHopeMeter.sxFcHope(unpaid);
            return EmberShortRules.fcPendingLine(unpaid);
        }
        if ("sx_day_left_sum".equals(key)) {
            int sum = 0;
            for (String mk : EmberShortRules.SHORT_KEYS) {
                sum += EmberShortRules.dayLeft(rewardedFor(d, maps, shortEx, mk), capFor(maps, shortEx, mk));
            }
            return String.valueOf(sum);
        }
        int us = key.indexOf('_');
        if (us <= 0) return "0";
        String mapKey = key.substring(0, us);
        String field = key.substring(us + 1);
        int n = rewardedFor(d, maps, shortEx, mapKey);
        int cap = capFor(maps, shortEx, mapKey);
        if ("day".equals(field)) return String.valueOf(n);
        if ("day_line".equals(field)) return EmberShortRules.dayLine(n, cap);
        if ("day_left".equals(field)) return String.valueOf(EmberShortRules.dayLeft(n, cap));
        return "";
    }

    private int rewardedFor(PlayerData d, EmberRunMaps maps, EmberShortService shortEx, String mapKey) {
        if (d == null) return 0;
        EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(mapKey);
        if (shortEx != null && m != null) return shortEx.rewardedToday(d, m);
        return Math.max(0, d.periodCount(EmberShortRules.claimKey(mapKey),
                town.sunshine.corerpg.DailyService.today()));
    }

    private static int capFor(EmberRunMaps maps, EmberShortService shortEx, String mapKey) {
        EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(mapKey);
        if (shortEx != null && m != null) return shortEx.dailyCap(m);
        return EmberShortRules.DAILY_CAP;
    }

    /** TIP-CH D13313 %corerpg_p1_today_triple*% — read-only aggregate; zero economy. */
    private String todayTriple(PlayerData d, String key, EmberRunMaps maps, EmberShortService shortEx) {
        String today = EmberShortRules.todayKey();
        EmberRunMaps.MapDef todayMap = maps == null ? null : maps.byKey(today);
        boolean todayUnlocked = EmberShortPath.unlocked(runs, d, todayMap);
        int todayLeft = EmberShortRules.dayLeft(rewardedFor(d, maps, shortEx, today), capFor(maps, shortEx, today));

        EmberSeason season = runs.season();
        boolean weekDone = false;
        if (season != null) {
            weekDone = "1".equals(EmberGoalShortChase.done(season.progress(d, "short"), season.target(d, "short")));
        }
        String weekChase = EmberGoalShortChase.chaseKey(today,
                k -> {
                    EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(k);
                    return EmberShortPath.unlocked(runs, d, m);
                },
                k -> EmberShortRules.dayLeft(rewardedFor(d, maps, shortEx, k), capFor(maps, shortEx, k)));

        String feat = runs.featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
        String eventChase = EmberEventChase.chaseKey(feat, k -> runs.progressFlag(d, k));

        EmberTodayTriple.Pick pick = EmberTodayTriple.pick(today, todayUnlocked, todayLeft, weekDone, weekChase, eventChase);
        if ("today_triple_kind".equals(key)) return pick.kind;
        if ("today_triple_ok".equals(key)) return EmberTodayTriple.ok(pick);
        if ("today_triple".equals(key)) return pick.mapKey;
        // today_triple_name
        if (!pick.present()) return "";
        EmberRunMaps.MapDef cm = maps == null ? null : maps.byKey(pick.mapKey);
        return cm != null && cm.name != null && !cm.name.isEmpty() ? cm.name : pick.mapKey;
    }

    /** TIP-CU D13326 %corerpg_p1_short_div*% — day-diversity chase; TIP-CX soft claim bits. */
    private String shortDayDiv(PlayerData d, String key, EmberRunMaps maps, EmberShortService shortEx) {
        java.util.function.Predicate<String> unlocked = k -> {
            EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(k);
            return EmberShortPath.unlocked(runs, d, m); // TIP-CG hotfix: progressFlag(req/q01), not C_UNLOCK
        };
        java.util.function.ToIntFunction<String> rewarded = k -> rewardedFor(d, maps, shortEx, k);
        java.util.function.ToIntFunction<String> dayLeft = k -> EmberShortRules.dayLeft(
                rewardedFor(d, maps, shortEx, k), capFor(maps, shortEx, k));
        String today = EmberShortRules.todayKey();
        int n = EmberShortDayDivChase.distinctRewarded(unlocked, rewarded);
        String chase = EmberShortDayDivChase.chaseKey(today, unlocked, dayLeft, rewarded);
        if ("short_div".equals(key)) return EmberShortDayDivChase.progress(n);
        if ("short_div_n".equals(key)) return String.valueOf(n);
        if ("short_div_line".equals(key)) return EmberShortDayDivChase.line(n);
        if ("short_div_chase".equals(key)) return chase;
        if ("short_div_chase_ok".equals(key)) return EmberShortDayDivChase.chaseOk(chase);
        if ("short_div_done".equals(key)) return EmberShortDayDivChase.done(n, chase);
        // TIP-CX D13329 soft claim bits (zero change chase/enter)
        boolean claimed = EmberShortDayDivSoftClaim.claimedToday(d);
        if ("short_div_claim_ok".equals(key)) return EmberShortDayDivSoftClaim.claimOk(n, claimed);
        if ("short_div_claimed".equals(key)) return EmberShortDayDivSoftClaim.claimedBit(claimed);
        if ("short_div_claim_line".equals(key)) return EmberShortDayDivSoftClaim.claimLine(n, claimed);
        if (chase.isEmpty()) return "";
        if ("short_div_chase_name".equals(key)) {
            EmberRunMaps.MapDef cm = maps == null ? null : maps.byKey(chase);
            return cm != null && cm.name != null && !cm.name.isEmpty() ? cm.name : chase;
        }
        // short_div_chase_day_line
        return EmberShortRules.dayLine(rewardedFor(d, maps, shortEx, chase), capFor(maps, shortEx, chase));
    }


    /** TIP-CY D13330 %corerpg_p1_short_var*% — last settled short variety labels; zero economy. */
    private String shortVariety(PlayerData d, String key) {
        if ("short_var_hit".equals(key)) return EmberShortVarietySettle.hitBit(d);
        if ("short_var_affix_label".equals(key)) return EmberShortVarietySettle.affixLabel(d);
        if ("short_var_event_label".equals(key)) return EmberShortVarietySettle.eventLabel(d);
        if ("short_var_line".equals(key) || "short_var".equals(key)) return EmberShortVarietySettle.line(d);
        return "";
    }

    /** TIP-CW D13328 %corerpg_p1_short_settle*% — last settled short map + day leftover; zero economy. */
    private String shortSettle(PlayerData d, String key, EmberRunMaps maps, EmberShortService shortEx) {
        int path = EmberShortPath.get(d);
        EmberRunMaps.MapDef pathNext = EmberShortPath.resolve(runs, d, path);
        String today = EmberShortRules.todayKey();
        String mk = EmberShortSettle.resolveMap(d, pathNext, today);
        if ("short_settle".equals(key) || "short_settle_map".equals(key)) return mk;
        EmberRunMaps.MapDef m = (maps == null || mk.isEmpty()) ? null : maps.byKey(mk);
        if ("short_settle_name".equals(key)) return EmberShortSettle.displayName(m, mk);
        if (mk.isEmpty()) {
            if ("short_settle_day_left".equals(key)) return "0";
            if ("short_settle_day_line".equals(key)) return EmberShortRules.dayLine(0, EmberShortRules.DAILY_CAP);
            return "";
        }
        int n = rewardedFor(d, maps, shortEx, mk);
        int cap = capFor(maps, shortEx, mk);
        if ("short_settle_day_left".equals(key)) return String.valueOf(EmberShortRules.dayLeft(n, cap));
        if ("short_settle_day_line".equals(key)) return EmberShortRules.dayLine(n, cap);
        return "";
    }

    /** TIP-CV D13327 %corerpg_p1_short_path*% — sticky ShortPath preference + resolve; zero algo change. */
    private String shortPathChase(PlayerData d, String key) {
        int path = EmberShortPath.get(d);
        EmberRunMaps.MapDef next = EmberShortPath.resolve(runs, d, path);
        String rk = EmberShortPathChase.resolveKey(next);
        String rn = EmberShortPathChase.resolveName(next);
        if ("short_path".equals(key)) return EmberShortPathChase.token(path);
        if ("short_path_label".equals(key)) return EmberShortPath.label(path);
        if ("short_path_tip".equals(key)) return EmberShortPath.tip(path);
        if ("short_path_resolve".equals(key)) return rk;
        if ("short_path_resolve_name".equals(key)) return rk.isEmpty() ? "" : rn;
        if ("short_path_ok".equals(key)) return EmberShortPathChase.ok(path, rk);
        if ("short_path_line".equals(key)) return EmberShortPathChase.line(path, rk, rn);
        return "";
    }

    /**
     * D404 %corerpg_p1_vault_*% / recipe_gap_* · D432 forge_gap_* — read-only vault + 镜像价 gap.
     * Empty / vault absent → {@code 0} / 拼接零差. Does not change UpgradeRules / AFK tables.
     */
    private String vault(Player p, String key) {
        long shard = vaultCount(p, EmberUpgradeRules.MAT_SHARD);
        long bone = vaultCount(p, EmberUpgradeRules.MAT_BONE);
        long core = vaultCount(p, EmberUpgradeRules.MAT_CORE);
        long blank = vaultCount(p, EmberUpgradeRules.MAT_BLANK);
        PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
        long coin = pd == null ? 0L : Math.max(0L, pd.getCoin());
        if ("vault_shard".equals(key)) return String.valueOf(shard);
        if ("vault_bone".equals(key)) return String.valueOf(bone);
        if ("vault_core".equals(key)) return String.valueOf(core);
        if ("vault_blank".equals(key)) return String.valueOf(blank);
        if ("recipe_gap_enhance1".equals(key)) return recipeGapEnhance1(shard);
        if ("recipe_gap_upgrade_t2".equals(key)) return recipeGapUpgradeT2(shard, core, blank);
        if ("recipe_gap_refine1".equals(key)) return recipeGapRefine1(blank, bone);
        if ("recipe_gap_quality1".equals(key)) return recipeGapQuality1(blank, bone, coin);
        if ("recipe_gap_gem".equals(key)) return recipeGapGemCraft(shard, bone, coin, pd);
        if ("recipe_gap_lock".equals(key)) {
            EmberGrowthService gs = EmberGrowthService.get();
            return recipeGapLock(shard, coin, gs == null ? null : gs.reroll());
        }
        // D432 烬砧仓差（镜像 Brand/Roll/Convert 价；不抬 AFK 表）；TIP-AT soft peek
        if ("forge_gap_brand".equals(key)) return forgeGapBrand(shard, pd);
        if ("forge_gap_roll".equals(key)) return forgeGapRoll(blank, coin, pd);
        if ("forge_gap_roll_floor".equals(key)) return forgeGapRollFloor(blank, bone, coin, pd);
        if ("forge_gap_convert".equals(key)) {
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            return forgeGapConvert(tier, blank, coin, pd);
        }
        if ("forge_gap_pin".equals(key)) {
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            return forgeGapPin(tier, brandCount(p), coin, pd);
        }
        // D461 forge craft-goal path
        if ("forge_goal".equals(key) || "forge_goal_gap".equals(key) || "forge_goal_line".equals(key)
                || "forge_goal_hope".equals(key)) {
            int gid = EmberForgeGoal.get(pd);
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            String enh = recipeGapEnhance1(shard);
            String ref = recipeGapRefine1(blank, bone);
            String br = forgeGapBrand(shard, pd);
            String ro = forgeGapRoll(blank, coin, pd);
            String cv = forgeGapConvert(tier, blank, coin, pd);
            String qg = recipeGapQuality1(blank, bone, coin);
            String gg = recipeGapGemCraft(shard, bone, coin, pd);
            EmberGrowthService gsL = EmberGrowthService.get();
            String lg = recipeGapLock(shard, coin, gsL == null ? null : gsL.reroll());
            String ug = recipeGapUpgradeT2(shard, core, blank);
            String gap = EmberForgeGoal.gapLine(gid, enh, ref, br, ro, cv, qg, gg, lg, ug);
            if ("forge_goal".equals(key)) return EmberForgeGoal.label(gid);
            if ("forge_goal_gap".equals(key)) return gap;
            if ("forge_goal_hope".equals(key)) return EmberHopeMeter.forgeGoalHope(EmberForgeGoal.label(gid), gap);
            return EmberForgeGoal.glance(gid, gap);
        }
        if ("vault_hope".equals(key)) { // D630
            EmberUpgradeRules.Cost ec = EmberUpgradeRules.enhanceCost(0);
            int enhNeed = ec == null ? 0 : ec.shards;
            long enhShort = Math.max(0L, enhNeed - shard);
            EmberUpgradeRules.Cost rc = EmberUpgradeRules.refineCost(0);
            int blNeed = rc == null ? 0 : rc.blanks, boNeed = rc == null ? 0 : rc.bone;
            long blShort = Math.max(0L, blNeed - blank), boShort = Math.max(0L, boNeed - bone);
            if (enhShort > 0) return EmberHopeMeter.vaultHope("强化还差碎片 ×" + enhShort);
            if (blShort > 0 || boShort > 0)
                return EmberHopeMeter.vaultHope("精工还差胚 ×" + blShort + " · 骨 ×" + boShort);
            return EmberHopeMeter.vaultHope(null);
        }
        return "";
    }

    /** D433: 烙纹背包计数（与 GrowthService pin 消耗同源；烙纹不进挂机仓）. */
    private long brandCount(Player p) {
        if (p == null) return 0L;
        try {
            town.sunshine.corerpg.NiBridge ni = runs.plugin().getNiBridge();
            if (ni == null) return 0L;
            return Math.max(0L, ni.countInInventory(p, EmberBrandRules.MAT_BRAND));
        } catch (RuntimeException e) { return 0L; }
    }

    private static long vaultCount(Player p, String niId) {
        if (p == null || niId == null) return 0L;
        EmberVault v = EmberVault.get();
        if (v == null) return 0L;
        try { return Math.max(0L, v.count(p, niId)); } catch (RuntimeException e) { return 0L; }
    }

    /** D306: hub 「今天该打哪」 primary / secondary from real stamina / afk_full / featured / q07. */
    private String routeLine(Player p, PlayerData d, String key) {
        StaminaService st = runs.plugin().getStaminaService();
        int stamina = st == null ? 0 : st.getStamina(d);
        int staminaMax = st == null ? 90 : st.getMax(d);
        int daily = st == null ? 30 : st.costOf("daily");
        int abyss = st == null ? 30 : st.costOf("abyss");
        int raid = st == null ? 50 : st.costOf("raid");
        boolean q07 = runs.progressFlag(d, "q07");
        int featuredLeft = runs.featuredLeft(d);
        String featuredShort = runs.featuredShort();
        String next = runs.nextStep(d, p == null ? null : p.getUniqueId());
        boolean afkFull = false;
        EmberAfkService afk = EmberAfkService.get();
        if (afk != null && p != null) {
            String f = afk.papi(p, d, "full");
            afkFull = "1".equals(f);
        }
        if ("route_sec".equals(key)) {
            return EmberHubRoute.secondary(stamina, daily, abyss, raid, afkFull, q07, featuredLeft);
        }
        return EmberHubRoute.primary(next, stamina, staminaMax, daily, abyss, raid, afkFull, q07, featuredLeft, featuredShort);
    }

    private String entry(Player p, PlayerData d, String key, EmberRunMaps maps) {
        if (key.startsWith("pass_")) return runs.hasPass(p.getUniqueId(), key.substring(5)) ? "yes" : "no";
        if ("target".equals(key)) { String t = runs.target(d); return t == null ? "未选择" : EmberItemData.familyName(t); }
        if (key.startsWith("marks_t")) {
            try { return String.valueOf(runs.marks(d, Integer.parseInt(key.substring(7)))); } catch (NumberFormatException e) { return "0"; }
        }
        if ("pending".equals(key)) { // D93 / D1034: exclude await_choice; LedgerGlance.claimable
            return String.valueOf(EmberLedgerGlance.claimable(runs, p.getUniqueId()));
        }
        if ("active".equals(key)) return EmberMode.active() ? "yes" : "no";
        if (key.startsWith("passd_")) { // D174 stage 2b: entries sharing one DP hall dungeon check the pass by dungeon
            Object[] ps = runs.passOf(p.getUniqueId());
            EmberRunMaps.MapDef pm = ps == null || System.currentTimeMillis() > (Long) ps[1] ? null : maps.byKey((String) ps[0]);
            return pm != null && pm.dungeon.equalsIgnoreCase(key.substring(6)) ? "yes" : "no";
        }
        if ("featured_left".equals(key)) return String.valueOf(runs.featuredLeft(d)); // D306
        if ("route_pri".equals(key) || "route_sec".equals(key)) return routeLine(p, d, key); // D306 hub daily routing
        if ("route_hope".equals(key)) return EmberHopeMeter.routeHope(runs.nextStep(d, p == null ? null : p.getUniqueId())); // D635
        if ("next".equals(key)) return runs.nextStep(d, p.getUniqueId()); // new-player polish %corerpg_p1_next%
        if ("recruits".equals(key)) return runs.recruitsLabel(); // E-review #5
        return runs.failRefundLabel(p.getUniqueId()); // failrefund · D128
    }

    private String rush(Player p, PlayerData d, String key, EmberRunMaps maps) {
        if ("vbounty".equals(key)) { String l = runs.varietyBountyLine(d); return l.isEmpty() ? "—" : l; } // D144 花样委托
        if ("rush".equals(key)) { // D144 余烬连战 menu line
            if (maps.rush.isEmpty()) return "未配置";
            if (!EmberSkillKit.endgameUnlocked(d, runs)) return "§8需本人首进或首通 Q07";
            int used = runs.rushWeek(d, p.getUniqueId()); // D160
            return rushLine(EmberRunRules.rushPaysReward(used, EmberRunService.RUSH_WEEKLY));
        }
        if (key.startsWith("pledge_")) return runs.pledgePapi(d, key.substring(7)); // D174 stage 2b 自选誓约
        EmberRunMaps.MapDef rm = maps.rush.get(key.substring(5)); // rush_<entry>
        if (rm == null) return null; // old chain: not a rush entry → per-map tail
        if (!EmberSkillKit.mapAccessUnlocked(d, runs, rm.requires)) return "§8需本人首进或首通 " + rm.requires.toUpperCase(java.util.Locale.ROOT);
        for (String ck : rm.chainKeys) if (!runs.progressFlag(d, ck)) return needFirstClear(ck);
        int used = runs.rushWeek(d, p.getUniqueId(), rm);
        return rushEntryLine(EmberRunRules.rushPaysReward(used, rm.rushWeekly), used, rm.rushWeekly);
    }

    private static String growth(Player p, PlayerData d, String key) {
        if (key.startsWith("sign_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.signPapi(p, d, key.substring(5)); } // D180
        if (key.startsWith("online_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.onlinePapi(p, d, key.substring(7)); } // D180
        if (key.startsWith("afk_")) { EmberAfkService a = EmberAfkService.get(); return a == null ? "" : a.papi(p, d, key.substring(4)); } // D177
        EmberGrowthService g = EmberGrowthService.get();
        if (key.startsWith("sig_")) return g == null ? "" : g.sigPapi(p, d, key.substring(4)); // D174 stage 1.5
        if (key.startsWith("reroll_")) return g == null ? "" : g.rerollPapi(p, d, key.substring(7)); // D143
        if (key.startsWith("brand_")) return g == null ? "" : g.brandPapi(p, d, key.substring(6)); // D13323 TIP-CR
        if (key.startsWith("convert_")) return g == null ? "" : g.convertPapi(p, d, key.substring(8)); // D13324 TIP-CS
        if (key.startsWith("honor_")) return g == null ? "" : g.honorPapi(p, d, key.substring(6)); // D142
        return g == null ? "" : g.papi(p, d, key.substring(5)); // spec_ · D141
    }

    /** TIP-DC D13334 %corerpg_p1_identity_settle*% — last identity-FC map + family + slot; zero economy. */
    private String identitySettle(PlayerData d, String key) {
        String mk = EmberIdentitySettle.lastMap(d);
        String fam = EmberIdentitySettle.lastFamily(d);
        String slot = EmberIdentitySettle.lastSlot(d);
        EmberRunMaps maps = runs.maps();
        EmberRunMaps.MapDef m = (maps == null || mk.isEmpty()) ? null : maps.byKey(mk);
        if ("identity_settle".equals(key) || "identity_settle_map".equals(key)) return mk;
        if ("identity_settle_name".equals(key)) return EmberIdentitySettle.displayName(m, mk);
        if ("identity_settle_family".equals(key)) return EmberIdentitySettle.familyDisplay(fam);
        if ("identity_settle_slot".equals(key)) return EmberIdentitySettle.slotDisplay(slot);
        if ("identity_settle_line".equals(key)) return EmberIdentitySettle.line(m, mk, fam, slot);
        return "";
    }

    /** TIP-DA D13332 %corerpg_p1_challenge_settle*% — last settled challenge map + path; zero economy. */
    private String challengeSettle(PlayerData d, String key) {
        String mk = EmberChallengeSettle.lastMap(d);
        int path = EmberChallengePath.get(d);
        EmberRunMaps maps = runs.maps();
        EmberRunMaps.MapDef m = (maps == null || mk.isEmpty()) ? null : maps.byKey(mk);
        if ("challenge_settle".equals(key) || "challenge_settle_map".equals(key)) return mk;
        if ("challenge_settle_name".equals(key)) return EmberChallengeSettle.displayName(m, mk);
        if ("challenge_settle_path".equals(key) || "challenge_path".equals(key)) return String.valueOf(path);
        if ("challenge_settle_path_label".equals(key) || "challenge_path_label".equals(key))
            return EmberChallengePath.label(path);
        if ("challenge_path_tip".equals(key)) return EmberChallengePath.tip(path); // TIP-DF D13337
        if ("challenge_path_line".equals(key)) return EmberChallengePath.menuLine(path); // TIP-DF D13337
        if ("challenge_settle_line".equals(key)) return EmberChallengeSettle.line(m, mk, path);
        return "";
    }

    private String gate(PlayerData d, String key) {
        if ("identity_settle".equals(key) || (key != null && key.startsWith("identity_settle_"))) // TIP-DC D13334
            return identitySettle(d, key);
        if ("challenge_settle".equals(key) || (key != null && key.startsWith("challenge_settle_"))
                || "challenge_path".equals(key) || (key != null && key.startsWith("challenge_path_"))) // TIP-DA + TIP-DF D13337
            return challengeSettle(d, key);
        if ("forge_t2".equals(key)) return EmberSkillKit.directedForgeOpen(d, runs, 2) ? "已开放" : "需本人首进或首通 Q04";
        if ("forge_t3".equals(key)) return EmberSkillKit.directedForgeOpen(d, runs, 3) ? "已开放" : "需本人首进或首通 Q07";
        if ("challenge".equals(key)) return runs.challengeOpen(d) ? "已开放" : "需本人首进/首通 Q07";
        if ("access_q01".equals(key)) return accessBit(EmberSkillKit.gemPathUnlocked(d, runs)); // TIP-BR MG2
        if ("access_q07".equals(key)) return accessBit(EmberSkillKit.endgameUnlocked(d, runs)); // TIP-BR MG2
        if ("q07done".equals(key)) return runs.progressFlag(d, "q07") ? "1" : "0"; // D99 menu condition (post-Q07 icons)
        return runs.progressFlag(d, key.substring(0, 3)) ? "1" : "0"; // D174 stage 2b: q04done / q05done / q06done
    }

    /**
     * TIP-BR MG2: menu access flag from SoT unlock boolean ({@link EmberSkillKit#gemPathUnlocked} /
     * {@link EmberSkillKit#endgameUnlocked}). Display only — zero unlock math edits.
     */
    static String accessBit(boolean unlocked) {
        return unlocked ? "1" : "0";
    }

    /** TIP-DE D13336 %corerpg_p1_featured_path*% — sticky FeaturedPath preference + lore line; zero economy. */
    private String featuredPath(PlayerData d, String key) {
        int path = EmberFeaturedPath.get(d);
        String fl = runs.featuredLabel(d);
        if ("featured_path".equals(key)) return String.valueOf(path);
        if ("featured_path_label".equals(key)) return EmberFeaturedPath.label(path);
        if ("featured_path_tip".equals(key)) return EmberFeaturedPath.tip(path);
        if ("featured_path_line".equals(key)) return EmberFeaturedPath.menuLine(path, fl);
        return "";
    }

    /** TIP-DD D13335 %corerpg_p1_featured_settle*% — last featured settle map + path + week-cap; zero economy. */
    private String featuredSettle(PlayerData d, String key, EmberRunMaps maps) {
        String mk = EmberFeaturedSettle.lastMap(d);
        int path = EmberFeaturedSettle.lastPath(d);
        boolean cap = EmberFeaturedSettle.lastCapFull(d);
        EmberRunMaps.MapDef m = (maps == null || mk.isEmpty()) ? null : maps.byKey(mk);
        if ("featured_settle".equals(key) || "featured_settle_map".equals(key)) return mk;
        if ("featured_settle_name".equals(key)) return EmberFeaturedSettle.displayName(m, mk);
        if ("featured_settle_path".equals(key)) return String.valueOf(path);
        if ("featured_settle_path_label".equals(key)) return EmberFeaturedPath.label(path);
        if ("featured_settle_cap".equals(key) || "featured_settle_cap_full".equals(key)) return cap ? "1" : "0";
        if ("featured_settle_cap_label".equals(key)) return cap ? "周帽已满" : "周帽未满";
        if ("featured_settle_line".equals(key)) return EmberFeaturedSettle.line(m, mk, path, cap);
        return "";
    }

        private String rotation(PlayerData d, String key, EmberRunMaps maps) {
        if ("featured_settle".equals(key) || (key != null && key.startsWith("featured_settle_"))) // TIP-DD D13335
            return featuredSettle(d, key, maps);
        if ("featured_path".equals(key) || (key != null && key.startsWith("featured_path_"))) // TIP-DE D13336
            return featuredPath(d, key);
        if ("featured".equals(key)) return runs.featuredLabel(d); // P2-1
        if ("modifier".equals(key)) return runs.modifierLabel(); // P2-8
        if (key.startsWith("rule_")) return runs.ruleLine(d, key.substring(5)); // D94
        if (key.startsWith("loot_")) {
            EmberRunMaps.MapDef lm = maps.byKey(key.substring(5));
            if (lm == null) lm = maps.raids.get(key.substring(5));
            return EmberRunMaps.lootLabel(lm) + runs.lootOdds(d, lm);
        }
        if ("mapid_board".equals(key)) { // D616 readable bias board
            return EmberHopeMeter.mapGearBoard(maps);
        }
        if ("mapid_focus".equals(key)) { // D12307: setfocus → suggestMap menuLine (or no-focus tip)
            int fid = EmberSetFocus.get(d);
            String fam = EmberSetFocus.familyKey(fid);
            if (fam == null || fam.isEmpty()) return "§8未选套装焦点 · 右键套装格";
            String mk = EmberMapIdentity.suggestMapForFamily(maps, fam);
            if (mk == null || mk.isEmpty()) return "§8未选套装焦点 · 右键套装格";
            EmberRunMaps.MapDef lm = maps.byKey(mk);
            if (lm == null) lm = maps.raids.get(mk);
            return EmberMapIdentity.menuLine(lm, d);
        }
        if (key.startsWith("mapid_")) { // D12250: per-map menuLine (board stays above)
            EmberRunMaps.MapDef lm = maps.byKey(key.substring(6));
            if (lm == null) lm = maps.raids.get(key.substring(6));
            return EmberMapIdentity.menuLine(lm, d);
        }
        if ("bounty".equals(key)) return runs.bountyLabel(d); // P2-7 %corerpg_p1_bounty%
        if ("bounty_hope".equals(key)) { // D620
            java.util.List<EmberRunRules.BountyTier> tiers = runs.bountyTiers();
            int done = d.periodCount(EmberRunService.C_BOUNTY, town.sunshine.corerpg.DailyService.today());
            for (EmberRunRules.BountyTier bt : tiers) {
                if (bt.clears > done) return EmberHopeMeter.bountyHope(done, bt.clears, bt.rewardText());
            }
            return EmberHopeMeter.bountyHope(done, 0, "已全部完成");
        }
        if ("featured_hope".equals(key)) { // D620
            int left = runs.featuredLeft(d);
            int cap = runs.maps() == null ? 0 : runs.maps().rotationWeeklyCap;
            return EmberHopeMeter.featuredHope(left, cap);
        }
        if ("event_chase".equals(key) || "event_chase_name".equals(key) || "event_chase_ok".equals(key) || "event_soft_ready".equals(key)) { // TIP-CE D13310
            if ("event_soft_ready".equals(key))
                return EmberEventChase.softReady(EmberEventSoftPity.get(d));
            String feat = runs.featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
            String chase = EmberEventChase.chaseKey(feat, k -> runs.progressFlag(d, k));
            if ("event_chase".equals(key)) return chase;
            if ("event_chase_ok".equals(key)) return EmberEventChase.chaseOk(chase); // TIP-CE FAIL 修 · 单条件灰
            if (chase.isEmpty()) return "";
            EmberRunMaps.MapDef cm = maps.byKey(chase);
            return cm != null && cm.name != null && !cm.name.isEmpty() ? cm.name : chase;
        }
        return String.valueOf(runs.featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()))); // featured_key
    }

    private String board(String key) { // P2-10 %corerpg_p1_top_abyss_1%
        boolean ab = key.startsWith("top_abyss_");
        int i = topRank(key);
        if (i < 0) return "";
        EmberSeason season = runs.season();
        if (season != null) { // F-review #2: the season week board (same rows as the season page)
            List<EmberSeason.Row> sr = season.weekTop(ab ? "abyss" : "featured", i);
            return sr.size() < i ? "—" : sr.get(i - 1).name + " · " + EmberSeason.rowText(ab ? "abyss" : "featured", sr.get(i - 1));
        }
        EmberLeaderboard top = runs.top();
        if (top == null) return "";
        List<EmberLeaderboard.Row> rows = top.top(ab, EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())), i);
        if (rows.size() < i) return "—";
        EmberLeaderboard.Row r = rows.get(i - 1);
        return topRowText(r.name, r.value, ab);
    }

    private String season(Player p, PlayerData d, String key) { // D116 / D117
        // TIP-CG D13312: goal_short_chase* / goal_short_done (route via goal* → SEASON)
        if ("goal_short_chase".equals(key) || "goal_short_chase_name".equals(key)
                || "goal_short_chase_day_line".equals(key) || "goal_short_chase_ok".equals(key)
                || "goal_short_done".equals(key)) {
            return goalShortChase(p, d, key);
        }
        EmberSeason season = runs.season();
        return season == null ? null : season.papi(p.getUniqueId(), d, key);
    }

    /** TIP-CG D13312 %corerpg_p1_goal_short_chase*% / goal_short_done — read-only; zero economy. */
    private String goalShortChase(Player p, PlayerData d, String key) {
        EmberSeason season = runs.season();
        if ("goal_short_done".equals(key)) {
            if (season == null) return "0";
            return EmberGoalShortChase.done(season.progress(d, "short"), season.target(d, "short"));
        }
        EmberRunMaps maps = runs.maps();
        EmberShortService shortEx = runs.shortExpedition();
        String today = EmberShortRules.todayKey();
        String chase = EmberGoalShortChase.chaseKey(today,
                k -> {
                    EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(k);
                    return EmberShortPath.unlocked(runs, d, m); // TIP-CG hotfix: progressFlag(req/q01), not C_UNLOCK
                },
                k -> EmberShortRules.dayLeft(rewardedFor(d, maps, shortEx, k), capFor(maps, shortEx, k)));
        if ("goal_short_chase".equals(key)) return chase;
        if ("goal_short_chase_ok".equals(key)) return EmberGoalShortChase.chaseOk(chase);
        if (chase.isEmpty()) return "";
        if ("goal_short_chase_name".equals(key)) {
            EmberRunMaps.MapDef cm = maps == null ? null : maps.byKey(chase);
            return cm != null && cm.name != null && !cm.name.isEmpty() ? cm.name : chase;
        }
        // goal_short_chase_day_line
        return EmberShortRules.dayLine(rewardedFor(d, maps, shortEx, chase), capFor(maps, shortEx, chase));
    }

    private String cosmetic(Player p, PlayerData d, String key) {
        EmberCosmetics cosmetics = runs.cosmetics();
        if ("title".equals(key)) return cosmetics == null ? "" : cosmetics.titleMenuText(d); // P2-9 %corerpg_p1_title% (D103: never empty)
        if ("honors".equals(key)) return cosmetics == null ? "0/0" : cosmetics.earnedCount(d) + "/" + EmberCosmetics.ALL.size();
        if (cosmetics == null) return null; // old chain: shop keys without cosmetics → per-map tail
        if (key.startsWith("shop_")) return cosmetics.shopLabel(p.getUniqueId(), d, key.substring(5)); // D119
        return cosmetics.papi(p.getUniqueId(), d, key); // F-review #2 (D121); null → per-map tail as before
    }

    private String festival(Player p, PlayerData d, String key) { // D139
        EmberFestival festival = runs.festival();
        if (festival == null) return null;
        String v = festival.papi(p, d, key.substring(5));
        return v == null ? "" : v;
    }


    /** TIP-DB D13333 %corerpg_p1_abyss_settle*% / abyss_path* — last settled floor + path; zero economy. */
    private String abyssSettle(PlayerData d, String key, EmberRunMaps maps) {
        int floor = EmberAbyssSettle.lastFloor(d);
        int path = EmberAbyssPath.get(d);
        if ("abyss_path".equals(key)) return String.valueOf(path);
        if ("abyss_path_label".equals(key)) return EmberAbyssPath.label(path);
        if ("abyss_path_tip".equals(key)) return EmberAbyssPath.tip(path); // TIP-DI D13340
        if ("abyss_path_line".equals(key)) return EmberAbyssPath.menuLine(path); // TIP-DI D13340
        if ("abyss_settle".equals(key) || "abyss_settle_floor".equals(key))
            return floor > 0 ? String.valueOf(floor) : "";
        if ("abyss_settle_name".equals(key)) return EmberAbyssSettle.displayName(floor);
        if ("abyss_settle_path".equals(key)) return String.valueOf(path);
        if ("abyss_settle_path_label".equals(key)) return EmberAbyssPath.label(path);
        if ("abyss_settle_line".equals(key)) return EmberAbyssSettle.line(floor, path);
        int next = floor > 0 ? Math.min(EmberAbyssSettle.MAX_FLOOR, floor + 1) : 0;
        if ("abyss_settle_next".equals(key))
            return next > 0 ? String.valueOf(next) : "";
        if ("abyss_settle_can_next".equals(key)) {
            if (next <= 0 || maps == null || maps.abyss.isEmpty() || !runs.abyssOpen(d)) return "0";
            int max = runs.abyssMaxStart(d);
            if (next > max) return "0";
            EmberRunMaps.AbyssTier tier = maps.abyssTier(next);
            if (tier == null) return "0";
            int fee0 = tier.fee;
            int feeShow = EmberAbyssFeeSoft.peekFee(d, fee0);
            boolean afford = feeShow <= 0 || d.getCoin() >= feeShow || runs.feeMarks(d, feeShow) > 0;
            return afford ? "1" : "0";
        }
        return "";
    }

    private String abyss(PlayerData d, String key, EmberRunMaps maps) {
        if ("abyss_settle".equals(key) || (key != null && key.startsWith("abyss_settle_"))
                || "abyss_path".equals(key) || "abyss_path_label".equals(key)
                || "abyss_path_tip".equals(key) || "abyss_path_line".equals(key)) // TIP-DB D13333 + TIP-DI D13340
            return abyssSettle(d, key, maps);
        if ("abyss_best".equals(key)) return String.valueOf(runs.abyssBest(d)); // P2-2
        if ("abyss_hope".equals(key)) { // D624 · TIP-AX peekFee
            if (maps.abyss.isEmpty() || !runs.abyssOpen(d)) return EmberHopeMeter.abyssHope(0, 0, 0, false);
            int best = runs.abyssBest(d);
            int max = runs.abyssMaxStart(d);
            EmberRunMaps.AbyssTier next = maps.abyssTier(max);
            int fee0 = next == null ? 0 : next.fee;
            int feeShow = EmberAbyssFeeSoft.peekFee(d, fee0);
            boolean afford = feeShow <= 0 || d.getCoin() >= feeShow || runs.feeMarks(d, feeShow) > 0;
            String hope = EmberHopeMeter.abyssHope(best, max, feeShow, afford);
            if (EmberAbyssFeeSoft.ready(d) && feeShow < fee0)
                hope = hope + EmberGearNextHint.softTail(true);
            return hope;
        }
        if ("abyss_state".equals(key)) {
            boolean configured = !maps.abyss.isEmpty();
            boolean open = configured && runs.abyssOpen(d);
            return abyssStateLine(configured, open, maps.abyssRequires, open ? runs.abyssBest(d) : 0, open ? runs.abyssMaxStart(d) : 0);
        }
        try {
            EmberRunMaps.AbyssTier t = maps.abyssTier(Integer.parseInt(key.substring(7)));
            return t == null ? "" : runs.abyssLine(d, t);
        } catch (NumberFormatException e) { return ""; }
    }

    private String loadout(Player p, String key) {
        if ("forgeroll_back".equals(key)) return runs.forgerollPapi(p, "back"); // D13325 TIP-CT
        if ("afk_rec_flower".equals(key) || "afk_rec_flower_name".equals(key)
                || "afk_rec_flower_kind".equals(key) || "afk_rec_flower_ok".equals(key)) { // TIP-CJ D13315
            return afkRecFlower(p, key);
        }
        if ("awaken_route".equals(key)) { // D120: cheapest real route (first line)
            List<String> r;
            if (Bukkit.isPrimaryThread()) r = runs.breakthroughRoutes(p, null);
            else try { final Player fp = p; r = Bukkit.getScheduler().callSyncMethod(runs.plugin(), () -> runs.breakthroughRoutes(fp, null)).get(750, java.util.concurrent.TimeUnit.MILLISECONDS); }
            catch (Exception e) { return ""; }
            return awakenRouteLine(r);
        }
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        EmberLoadout l = ls == null ? null : ls.get(p);
        if (l == null && ls != null) l = ls.refresh(p);
        if (l == null) return "";
        switch (key) {
            case "awaken": return l.setLabel();
            case "set_progress": return l.setProgress();
            case "set_focus":
            case "set_focus_line": { // D465 set-family playstyle focus
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                int fid = EmberSetFocus.get(pd);
                return EmberSetFocus.glance(fid, l.activeSet, l.awakening, runs.target(pd));
            }
            case "setchase_line": { // D12266 hub 套装一眼 — do not alter set_focus chat glance
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberSetChase.menuLine(pd, l);
            }
            case "dual_lead":
            case "dual_lead_line":
            case "dual_hope": { // D466 dual-sig path · D636 hope
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                boolean dual = pd != null && runs.progressFlag(pd, EmberSignature.DUAL_UNLOCK);
                String glance = EmberDualLead.glance(EmberDualLead.get(pd), dual);
                if ("dual_hope".equals(key)) return EmberHopeMeter.dualHope(glance);
                return glance;
            }
            case "brand_path":
            case "brand_path_line":
            case "brand_hope": { // D467/D638
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                String g = EmberBrandPath.glance(EmberBrandPath.get(pd));
                if ("brand_hope".equals(key)) return EmberHopeMeter.brandHope(g);
                return g;
            }
            case "echo_path":
            case "echo_path_line":
            case "echo_hope": { // D468/D639
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                boolean un = pd != null && runs.progressFlag(pd, EmberEchoPath.UNLOCK);
                String g = EmberEchoPath.glance(EmberEchoPath.get(pd), un);
                if ("echo_hope".equals(key)) return EmberHopeMeter.echoHope(g);
                return g;
            }
            case "convert_path":
            case "convert_path_line":
            case "convert_hope": { // D469 convert destination path
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                int sf = EmberSetFocus.get(pd);
                String g = EmberConvertPath.glance(EmberConvertPath.get(pd), EmberSetFocus.familyKey(sf), runs.target(pd));
                if ("convert_hope".equals(key)) return EmberHopeMeter.convertHope(g);
                return g;
            }
            case "set_blurb": // TIP-BI D13054: gear page set effect follows activeSet
                return EmberItemData.setBlurb(l.activeSet);
            case "set_run": { // D457 enter-run style line (also for menus)
                EmberItemData[] worn = l.armorCopy();
                if (worn == null) worn = new EmberItemData[4];
                Object[] sp = EmberSixRank.setProgress(l.blade, l.charm, worn);
                int n = sp == null || sp[1] == null ? 0 : ((Integer) sp[1]).intValue();
                boolean on = sp != null && Boolean.TRUE.equals(sp[3]);
                String line = EmberSetFeel.runLine(l.activeSet, l.awakening, n, on);
                return line == null || line.isEmpty() ? "§8未成套" : line;
            }
            // B2.174 §19.1 装备页: actual B / H / D (formula output, §19.2), main hand + selected charm
            case "stats": return statsLine(l.b, l.h, l.d, l.m, l.level);
            case "ehp": return String.format(Locale.ROOT, "%.0f", l.ehp());
            case "blade": return l.blade == null ? "主手没拿余烬刃" : l.blade.shortLabel();
            case "charm": return l.charm == null ? "未选定护符" : l.charm.shortLabel();
            // D299 W1a：穿着刃/护符成色·精工近档 · TIP-AS soft-aware
            case "blade_next_q": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.blade == null ? "§8主手无刃" : EmberGearNextHint.qualityLine(l.blade, softPd);
            }
            case "blade_next_c": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.blade == null ? "§8主手无刃" : EmberGearNextHint.craftLine(l.blade, softPd);
            }
            case "charm_next_q": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.charm == null ? "§8未选护符" : EmberGearNextHint.qualityLine(l.charm, softPd);
            }
            case "charm_next_c": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.charm == null ? "§8未选护符" : EmberGearNextHint.craftLine(l.charm, softPd);
            }
            // D299 W1b：工坊手持件近档（无手持 / 非 P1 → 提示）
            case "held_next_q": return heldNext(p, true);
            case "held_next_c": return heldNext(p, false);
            // D307 W1a/b：工坊本次费用 / 缺料 / 互换免费 / 分解得胚
            case "enhance_blade_pity": // D601
                return l.blade == null ? EmberHopeMeter.emptySlot("刃") : EmberHopeMeter.enhance(l.blade);
            case "enhance_charm_pity": // D601
                return l.charm == null ? EmberHopeMeter.emptySlot("护符") : EmberHopeMeter.enhance(l.charm);
            case "held_enhance_pity": { // D601 forge preview
                EmberItemData hd = heldTrusted(p);
                if (hd == null) return EmberHopeMeter.emptySlot("手持");
                return EmberHopeMeter.enhance(hd);
            }
            case "held_enhance_rate": { // TIP-BN forge preview success rate
                EmberItemData hrate = heldTrusted(p);
                if (hrate == null) return EmberHopeMeter.emptySlot("手持");
                return EmberHopeMeter.enhanceRate(hrate);
            }
            case "quality_blade_hope": { // D602 · TIP-AS soft
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.blade == null ? EmberHopeMeter.emptySlot("刃") : EmberHopeMeter.quality(l.blade, softPd);
            }
            case "quality_charm_hope": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.charm == null ? EmberHopeMeter.emptySlot("护符") : EmberHopeMeter.quality(l.charm, softPd);
            }
            case "held_quality_hope": { // D602 forge preview
                EmberItemData hq = heldTrusted(p);
                if (hq == null) return EmberHopeMeter.emptySlot("手持");
                PlayerData softPd = runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberHopeMeter.quality(hq, softPd);
            }
            case "refine_blade_hope": { // D603 · TIP-AS soft
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.blade == null ? EmberHopeMeter.emptySlot("刃") : EmberHopeMeter.refine(l.blade, softPd);
            }
            case "refine_charm_hope": {
                PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return l.charm == null ? EmberHopeMeter.emptySlot("护符") : EmberHopeMeter.refine(l.charm, softPd);
            }
            case "held_refine_hope": { // D603 forge preview
                EmberItemData hr = heldTrusted(p);
                if (hr == null) return EmberHopeMeter.emptySlot("手持");
                PlayerData softPd = runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberHopeMeter.refine(hr, softPd);
            }
            case "hope_now": { // D12306 hub hope L1 — menuNow of current priority track
                return hopeNowLine(p);
            }
            case "gem_pity_hope": { // D608 / D12273: display remainLine (n/N·再通关得石); hopeLine kept for other callers
                PlayerData gd = runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberGemAcquire.remainLine(gd);
            }
            case "drop_q_hope": { // D611
                PlayerData dq = runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberDropQualityPity.hope(dq);
            }
            case "event_soft_hope": { // D728
                PlayerData es = runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberEventSoftPity.hope(es);
            }
            case "gearlib_hope": { // D626
                EmberGearLib gl = EmberGearLib.get();
                if (gl == null || !gl.usable()) return EmberHopeMeter.gearlibHope(0, 0, 0);
                int stored = gl.count(p.getUniqueId());
                int junk = gl.countPathJunk(p);
                return EmberHopeMeter.gearlibHope(stored, junk, gl.undoMinutes());
            }
            case "held_enhance_cost": return heldForgeLine(p, "enhance");
            case "held_enhance_lack": return heldForgeLack(p, "enhance");
            case "held_upgrade_cost": return heldForgeLine(p, "upgrade");
            case "held_upgrade_lack": return heldForgeLack(p, "upgrade");
            case "held_refine_lack": return heldForgeLack(p, "refine");
            case "held_quality_lack": return heldForgeLack(p, "quality");
            case "held_swap_cost": return EmberGearNextHint.swapLine();
            case "held_dismantle_yield": return heldForgeLine(p, "dismantle");
            case "held_is_armor": return EmberSixPapi.heldIsArmor(heldTrusted(p)); // D333
            default: return l.nextAwakeningHint();
        }
    }

    /** D299 W1b：主手 P1 件的成色/精工近档；非 P1 或空手则短提示。 TIP-AS soft. */
    private String heldNext(Player p, boolean quality) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符看近档";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, "next"); // D318 K0
        PlayerData softPd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
        return quality ? EmberGearNextHint.qualityLine(d, softPd) : EmberGearNextHint.craftLine(d, softPd);
    }

    /**
     * D12306: resolve live held/pd/mats/reroll/chase → {@link EmberHopeMeter#menuNow}.
     * Mirrors hope glance command inputs; display only.
     */
    private String hopeNowLine(Player p) {
        if (p == null) return EmberHopeMeter.menuNow(EmberHopeMeter.FALLBACK);
        PlayerData d = runs.plugin().getDataStore().get(p.getUniqueId());
        EmberItemData held = heldTrusted(p);
        EmberForgeHeldNext.Mats mats = new EmberForgeHeldNext.Mats(0, 0, 0, d == null ? 0 : Math.max(0, d.getCoin()));
        try {
            if (runs.plugin().getNiBridge() != null) {
                town.sunshine.corerpg.NiBridge ni = runs.plugin().getNiBridge();
                long shard = ni.countInInventory(p, EmberUpgradeRules.MAT_SHARD);
                long blank = ni.countInInventory(p, EmberUpgradeRules.MAT_BLANK);
                long bone = ni.countInInventory(p, EmberUpgradeRules.MAT_BONE);
                long coin = d == null ? 0L : Math.max(0L, d.getCoin());
                mats = new EmberForgeHeldNext.Mats(shard, blank, bone, coin);
            }
        } catch (Throwable ignored) { }
        int rerollPity = 0, rerollNeed = 5;
        try {
            EmberGrowthService gs = EmberGrowthService.get();
            if (gs != null && gs.reroll() != null) rerollNeed = Math.max(1, gs.reroll().pity);
            if (gs != null && d != null) {
                if (held != null && (held.isBlade() || held.isCharm())) {
                    rerollPity = Math.max(0, gs.afPityOf(d, held));
                }
                if (rerollPity <= 0) {
                    EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
                    EmberLoadout lo = ls == null ? null : ls.get(p);
                    if (lo != null) {
                        for (EmberItemData it : new EmberItemData[] { lo.blade, lo.charm }) {
                            if (it == null) continue;
                            int pity = Math.max(0, gs.afPityOf(d, it));
                            if (pity > rerollPity) rerollPity = pity;
                        }
                    }
                }
            }
        } catch (Throwable ignored) { }
        EmberSignature.Def chase = EmberSigChase.get(d);
        int marks = 0;
        if (chase != null && d != null) {
            marks = d.periodCount(EmberSignature.C_MARK + chase.map, "all");
        }
        return EmberHopeMeter.menuNow(held, d, mats, rerollPity, rerollNeed, chase, marks);
    }

    /** TIP-CJ D13315 %corerpg_p1_afk_rec_flower*% — read-only; preview only; zero economy. */
    private String afkRecFlower(Player p, String key) {
        EmberAfkRecFlower.Pick pick = resolveAfkRecFlower(p);
        if ("afk_rec_flower_kind".equals(key)) return pick.kind;
        if ("afk_rec_flower_ok".equals(key)) return EmberAfkRecFlower.ok(pick);
        if ("afk_rec_flower_name".equals(key)) return EmberAfkRecFlower.name(pick);
        return EmberAfkRecFlower.line(pick); // afk_rec_flower
    }

    /** Shared readiness for PAPI + enter afk-rec-flower (preview-only). */
    EmberAfkRecFlower.Pick resolveAfkRecFlower(Player p) {
        if (p == null) return EmberAfkRecFlower.pick(false, false, false, false, false);
        EmberItemData d = heldTrusted(p);
        boolean armor = d != null && d.isArmor();
        PlayerData softPd = runs.plugin().getDataStore().get(p.getUniqueId());
        EmberForgeService forge = runs.plugin().getEmberForge();

        boolean enhanceReady = false;
        if (d != null) {
            EmberUpgradeRules.Plan enh = EmberUpgradeRules.enhanceCheck(d);
            if (enh.ok() && enh.cost != null) {
                long shard = vaultCount(p, EmberUpgradeRules.MAT_SHARD);
                enhanceReady = "0".equals(recipeGapEnhance1(shard));
            }
        }

        boolean upgradeReady = false, refineReady = false, qualityReady = false;
        if (d != null && !armor && forge != null) {
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && softPd != null && runs.progressFlag(softPd, flag);
            EmberUpgradeRules.Plan up = EmberUpgradeRules.upgrade(d, gate);
            if (up.ok() && up.cost != null) {
                EmberUpgradeRules.Cost cost = EmberGearNextHint.softCost("upgrade", softPd, up.cost);
                java.util.List<String> lack = cost == null ? null : forge.lackingFor(p, cost);
                upgradeReady = lack == null || lack.isEmpty();
            }
            EmberUpgradeRules.Plan ref = EmberUpgradeRules.refine(d);
            if (ref.ok() && ref.cost != null) {
                EmberUpgradeRules.Cost cost = EmberGearNextHint.softCost("refine", softPd, ref.cost);
                java.util.List<String> lack = cost == null ? null : forge.lackingFor(p, cost);
                refineReady = lack == null || lack.isEmpty();
            }
            EmberUpgradeRules.Plan q = EmberUpgradeRules.quality(d);
            if (q.ok() && q.cost != null) {
                EmberUpgradeRules.Cost cost = EmberGearNextHint.softCost("quality", softPd, q.cost);
                java.util.List<String> lack = cost == null ? null : forge.lackingFor(p, cost);
                qualityReady = lack == null || lack.isEmpty();
            }
        }
        return EmberAfkRecFlower.pick(armor, enhanceReady, upgradeReady, refineReady, qualityReady);
    }

    /** D307：主手可信 P1 件；空手/非 P1/非本人 → null。 */
    private EmberItemData heldTrusted(Player p) {
        org.bukkit.inventory.ItemStack it = p.getInventory().getItemInMainHand();
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        if (ls == null || it == null || !ls.items().hasData(it)) return null;
        EmberItems.Read r = ls.items().read(it);
        if (r == null || r.data == null) return null;
        if (ls.trust(p, r.data) != null) return null;
        return r.data;
    }

    /** D307 W1a：费用/闸/得胚行（与 UpgradeRules / Forge 预览同源）。 TIP-AS soft. */
    private String heldForgeLine(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, kind); // D318 K0 / 分解 ×0.1
        PlayerData softPd = runs.plugin().getDataStore().get(p.getUniqueId());
        if ("enhance".equals(kind)) return EmberGearNextHint.enhanceLine(d, softPd);
        if ("upgrade".equals(kind)) {
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(softPd, flag);
            return EmberGearNextHint.upgradeLine(d, gate, softPd);
        }
        if ("dismantle".equals(kind)) return EmberGearNextHint.dismantleYieldLine(d, softPd);
        return "";
    }

    /** D307 W1b：缺料半行；材料够 / 无费用 → 空。 TIP-AS soft peek. */
    private String heldForgeLack(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null || d.isArmor()) return ""; // D318: armor pays nothing (K0)
        PlayerData softPd = runs.plugin().getDataStore().get(p.getUniqueId());
        EmberUpgradeRules.Cost cost = null;
        if ("enhance".equals(kind)) {
            EmberUpgradeRules.Plan c = EmberUpgradeRules.enhanceCheck(d);
            if (!c.ok()) return "";
            cost = EmberGearNextHint.softCost("enhance", softPd, c.cost);
        } else if ("upgrade".equals(kind)) {
            cost = EmberGearNextHint.softCost("upgrade", softPd, EmberUpgradeRules.upgradeCost(d.tier));
            if (cost == null) return "";
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(softPd, flag);
            if (!gate) return ""; // gate message already on cost line
        } else if ("refine".equals(kind)) {
            cost = EmberGearNextHint.softCost("refine", softPd, EmberUpgradeRules.refineCost(d.craft));
        } else if ("quality".equals(kind)) {
            cost = EmberGearNextHint.softCost("quality", softPd, EmberUpgradeRules.qualityCost(d.quality));
        }
        if (cost == null) return "";
        EmberForgeService forge = runs.plugin().getEmberForge();
        if (forge == null) return "";
        return EmberGearNextHint.lackHalf(forge.lackingFor(p, cost));
    }

    private String codex(Player p, PlayerData d, String key) { // B2.180 图录 · 装备 (display only, §19.5)
        EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
        if (ls != null) ls.backfillCodex(p);
        if ("codex_count".equals(key)) return EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size();
        if ("codex_hope".equals(key)) { // D633
            int have = EmberCodex.count(d), total = EmberCodex.ENTRIES.size();
            String next = null;
            java.util.List<Integer> claimable = EmberCodex.claimable(d);
            if (!claimable.isEmpty()) next = "阶段可领 · " + EmberCodex.stageLabel(d, claimable.get(0));
            else {
                for (int i = 0; i < EmberCodex.STAGE_AT.length; i++) {
                    if (EmberCodex.claimed(d, i)) continue;
                    next = EmberCodex.stageLabel(d, i);
                    break;
                }
            }
            return EmberHopeMeter.codexHope(have, total, next);
        }
        if (key.startsWith("codex_stage_")) {
            try {
                int i = Integer.parseInt(key.substring(12));
                return i >= 0 && i < EmberCodex.STAGE_AT.length ? EmberCodex.stageLabel(d, i) : "";
            } catch (NumberFormatException e) { return ""; }
        }
        if (key.startsWith("codex_")) return EmberCodex.has(d, key.substring(6)) ? "§a已登记" : "§8未获得";
        return "";
    }

    /** Tail: %corerpg_p1_&lt;map&gt;_&lt;field&gt;% (also where the fall-through keys end up). */
    private String map(PlayerData d, String key, EmberRunMaps maps) {
        int us = key.indexOf('_');
        if (us <= 0) return "";
        EmberRunMaps.MapDef m = maps.byKey(key.substring(0, us));
        if (m == null) return "";
        return mapField(m, key.substring(us + 1), d, maps);
    }

    private String mapField(EmberRunMaps.MapDef m, String f, PlayerData d, EmberRunMaps maps) {
        switch (f) {
            case "state": return runs.stateLabel(d, m);
            case "open": return runs.unlocked(d, m) ? "yes" : "no";
            case "cleared": return runs.firstClearDone(d, m) ? "yes" : "no";
            case "name": return m.name;
            case "cost": return maps.cost(m) + " 体力";
            case "tier": return m.dropLabel;
            case "purpose": return m.purpose;
            case "fc": return runs.firstCleared(d, m) ? "已领取" : m.firstClearLabel();
            default: return "";
        }
    }
}
