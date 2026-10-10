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
        /** forge_t2 / forge_t3 / challenge / q07done / q0Ndone */
        GATE,
        /** featured / featured_key / modifier / rule_ / bounty / loot_ */
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
        if (key.startsWith("sign_") || key.startsWith("online_") || key.startsWith("afk_") || key.startsWith("sig_")
                || key.startsWith("reroll_") || key.startsWith("honor_") || key.startsWith("spec_")) return Section.GROWTH;
        if ("forge_t2".equals(key) || "forge_t3".equals(key) || "challenge".equals(key) || "q07done".equals(key)) return Section.GATE;
        if (isQDone(key)) return Section.GATE;
        if ("featured".equals(key) || "modifier".equals(key) || key.startsWith("rule_")) return Section.ROTATION;
        if (key.startsWith("top_abyss_") || key.startsWith("top_featured_")) return Section.BOARD;
        if (key.startsWith("goal") || key.startsWith("season") || key.startsWith("sboard_") || key.startsWith("srank_")
                || "badges".equals(key)) return Section.SEASON;
        if (key.startsWith("shop")) return Section.COSMETIC; // shop* and shop_* (old: two checks, both cosmetics)
        if (key.startsWith("fest_")) return Section.FESTIVAL;
        if ("title".equals(key) || "honors".equals(key)) return Section.COSMETIC;
        if (key.startsWith("loot_")) return Section.ROTATION;
        if ("abyss_best".equals(key)) return Section.ABYSS;
        if ("bounty".equals(key)) return Section.ROTATION;
        if ("set_focus".equals(key) || "set_focus_line".equals(key)) return Section.LOADOUT; // D465
        if ("dual_lead".equals(key) || "dual_lead_line".equals(key)) return Section.LOADOUT; // D466
        if ("brand_path".equals(key) || "brand_path_line".equals(key)) return Section.LOADOUT; // D467
        if ("route_pri".equals(key) || "route_sec".equals(key) || "featured_left".equals(key)) return Section.ENTRY; // D306
        if ("next".equals(key)) return Section.ENTRY;
        if (key.startsWith("raid_")) return Section.RAID;
        if ("abyss_state".equals(key) || key.startsWith("abyss_t")) return Section.ABYSS;
        if ("recruits".equals(key) || "failrefund".equals(key)) return Section.ENTRY;
        if ("featured_key".equals(key)) return Section.ROTATION;
        if ("awaken_route".equals(key) || isLoadoutKey(key)) return Section.LOADOUT;
        if (key.startsWith("codex")) return Section.CODEX;
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
                || "set_focus".equals(key) || "set_focus_line".equals(key)
                || "dual_lead".equals(key) || "dual_lead_line".equals(key)
                || "brand_path".equals(key) || "brand_path_line".equals(key) || "stats".equals(key)
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
                || "held_is_armor".equals(key);
    }

    /** D395 day-cap + D406 fc aggregate (not per-map fields; sx0N_fc stays MAP). */
    static boolean isShortDayKey(String key) {
        if (key == null) return false;
        if ("sx_day_left_sum".equals(key)) return true;
        if ("sx_fc_left".equals(key) || "sx_fc_pending_line".equals(key)) return true; // D406
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
                || "recipe_gap_enhance1".equals(key) || "recipe_gap_upgrade_t2".equals(key) || "recipe_gap_refine1".equals(key)
                || "forge_gap_brand".equals(key) || "forge_gap_roll".equals(key) || "forge_gap_convert".equals(key)
                || "forge_gap_pin".equals(key)
                || "forge_goal".equals(key) || "forge_goal_gap".equals(key) || "forge_goal_line".equals(key);
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

    /** D432 %corerpg_p1_forge_gap_brand% — 烙纹合成碎差（镜像 EmberBrandRules.CRAFT_SHARDS=40）. */
    static String forgeGapBrand(long shardHave) {
        return String.valueOf(recipeGap(EmberBrandRules.CRAFT_SHARDS, shardHave));
    }

    /** D432 %corerpg_p1_forge_gap_roll% — 随机锻胚/币差（4 胚 + 500 币）. */
    static String forgeGapRoll(long blankHave, long coinHave) {
        return "胚差" + recipeGap(EmberForgeRollRules.BLANKS, blankHave) + "·币差" + recipeGap(EmberForgeRollRules.COINS, coinHave);
    }

    /**
     * D432 %corerpg_p1_forge_gap_convert% — 转化胚/币差。
     * {@code tier} 1–3 from held piece; else T1 defaults.
     */
    static String forgeGapConvert(int tier, long blankHave, long coinHave) {
        int t = (tier >= 1 && tier <= 3) ? tier : 1;
        return "胚差" + recipeGap(EmberConvertRules.blanks(t), blankHave)
                + "·币差" + recipeGap(EmberConvertRules.coins(t), coinHave);
    }

    /** D433 %corerpg_p1_forge_gap_pin% — 定向烙纹/币差（镜像 pinBrands/pinCoins；空手 T1）. */
    static String forgeGapPin(int tier, long brandHave, long coinHave) {
        int t = (tier >= 1 && tier <= 3) ? tier : 1;
        int brands = EmberBrandRules.pinBrands(t);
        int coins = EmberBrandRules.pinCoins(t);
        if (brands < 0) { brands = EmberBrandRules.pinBrands(1); coins = EmberBrandRules.pinCoins(1); }
        return "烙差" + recipeGap(brands, brandHave) + "·币差" + recipeGap(coins, coinHave);
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
        if (!open) return "需本人首通 " + requires.toUpperCase(Locale.ROOT);
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
        if ("sx_fc_left".equals(key) || "sx_fc_pending_line".equals(key)) { // D406 optional aggregate
            int unpaid = 0;
            for (String mk : EmberShortRules.SHORT_KEYS) {
                EmberRunMaps.MapDef m = maps == null ? null : maps.byKey(mk);
                if (m == null || !runs.firstCleared(d, m)) unpaid++;
            }
            unpaid = EmberShortRules.fcLeft(unpaid);
            if ("sx_fc_left".equals(key)) return String.valueOf(unpaid);
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
        // D432 烬砧仓差（镜像 Brand/Roll/Convert 价；不抬 AFK 表）
        if ("forge_gap_brand".equals(key)) return forgeGapBrand(shard);
        if ("forge_gap_roll".equals(key)) return forgeGapRoll(blank, coin);
        if ("forge_gap_convert".equals(key)) {
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            return forgeGapConvert(tier, blank, coin);
        }
        if ("forge_gap_pin".equals(key)) {
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            return forgeGapPin(tier, brandCount(p), coin);
        }
        // D461 forge craft-goal path
        if ("forge_goal".equals(key) || "forge_goal_gap".equals(key) || "forge_goal_line".equals(key)) {
            int gid = EmberForgeGoal.get(pd);
            EmberItemData held = p == null ? null : heldTrusted(p);
            int tier = (held != null && held.tier >= 1 && held.tier <= 3) ? held.tier : 1;
            String enh = recipeGapEnhance1(shard);
            String ref = recipeGapRefine1(blank, bone);
            String br = forgeGapBrand(shard);
            String ro = forgeGapRoll(blank, coin);
            String cv = forgeGapConvert(tier, blank, coin);
            String gap = EmberForgeGoal.gapLine(gid, enh, ref, br, ro, cv);
            if ("forge_goal".equals(key)) return EmberForgeGoal.label(gid);
            if ("forge_goal_gap".equals(key)) return gap;
            return EmberForgeGoal.glance(gid, gap);
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
        if ("pending".equals(key)) { // D93: a first-clear choice waiting for a click is not "stuck in storage"
            int n = 0;
            for (EmberRunRules.Row r : runs.store().ledger(p.getUniqueId()).open()) if (!EmberRunRules.ST_AWAIT.equals(r.status)) n++;
            return String.valueOf(n);
        }
        if ("active".equals(key)) return EmberMode.active() ? "yes" : "no";
        if (key.startsWith("passd_")) { // D174 stage 2b: entries sharing one DP hall dungeon check the pass by dungeon
            Object[] ps = runs.passOf(p.getUniqueId());
            EmberRunMaps.MapDef pm = ps == null || System.currentTimeMillis() > (Long) ps[1] ? null : maps.byKey((String) ps[0]);
            return pm != null && pm.dungeon.equalsIgnoreCase(key.substring(6)) ? "yes" : "no";
        }
        if ("featured_left".equals(key)) return String.valueOf(runs.featuredLeft(d)); // D306
        if ("route_pri".equals(key) || "route_sec".equals(key)) return routeLine(p, d, key); // D306 hub daily routing
        if ("next".equals(key)) return runs.nextStep(d, p.getUniqueId()); // new-player polish %corerpg_p1_next%
        if ("recruits".equals(key)) return runs.recruitsLabel(); // E-review #5
        return runs.failRefundLabel(p.getUniqueId()); // failrefund · D128
    }

    private String rush(Player p, PlayerData d, String key, EmberRunMaps maps) {
        if ("vbounty".equals(key)) { String l = runs.varietyBountyLine(d); return l.isEmpty() ? "—" : l; } // D144 花样委托
        if ("rush".equals(key)) { // D144 余烬连战 menu line
            if (maps.rush.isEmpty()) return "未配置";
            if (!runs.progressFlag(d, "q07")) return "§8需本人首通 Q07";
            int used = runs.rushWeek(d, p.getUniqueId()); // D160
            return rushLine(EmberRunRules.rushPaysReward(used, EmberRunService.RUSH_WEEKLY));
        }
        if (key.startsWith("pledge_")) return runs.pledgePapi(d, key.substring(7)); // D174 stage 2b 自选誓约
        EmberRunMaps.MapDef rm = maps.rush.get(key.substring(5)); // rush_<entry>
        if (rm == null) return null; // old chain: not a rush entry → per-map tail
        if (!runs.progressFlag(d, rm.requires)) return needFirstClear(rm.requires);
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
        if (key.startsWith("honor_")) return g == null ? "" : g.honorPapi(p, d, key.substring(6)); // D142
        return g == null ? "" : g.papi(p, d, key.substring(5)); // spec_ · D141
    }

    private String gate(PlayerData d, String key) {
        if ("forge_t2".equals(key)) return runs.progressFlag(d, "q04") ? "已开放" : "需本人首通 Q04";
        if ("forge_t3".equals(key)) return runs.progressFlag(d, "q07") ? "已开放" : "需本人首通 Q07";
        if ("challenge".equals(key)) return runs.challengeOpen(d) ? "已开放" : "需本人首通 Q07";
        if ("q07done".equals(key)) return runs.progressFlag(d, "q07") ? "1" : "0"; // D99 menu condition (post-Q07 icons)
        return runs.progressFlag(d, key.substring(0, 3)) ? "1" : "0"; // D174 stage 2b: q04done / q05done / q06done
    }

    private String rotation(PlayerData d, String key, EmberRunMaps maps) {
        if ("featured".equals(key)) return runs.featuredLabel(d); // P2-1
        if ("modifier".equals(key)) return runs.modifierLabel(); // P2-8
        if (key.startsWith("rule_")) return runs.ruleLine(d, key.substring(5)); // D94
        if (key.startsWith("loot_")) {
            EmberRunMaps.MapDef lm = maps.byKey(key.substring(5));
            if (lm == null) lm = maps.raids.get(key.substring(5));
            return EmberRunMaps.lootLabel(lm) + runs.lootOdds(d, lm);
        }
        if ("bounty".equals(key)) return runs.bountyLabel(d); // P2-7 %corerpg_p1_bounty%
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
        EmberSeason season = runs.season();
        return season == null ? null : season.papi(p.getUniqueId(), d, key);
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

    private String abyss(PlayerData d, String key, EmberRunMaps maps) {
        if ("abyss_best".equals(key)) return String.valueOf(runs.abyssBest(d)); // P2-2
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
            case "dual_lead":
            case "dual_lead_line": { // D466 dual-sig path
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                boolean dual = pd != null && runs.progressFlag(pd, EmberSignature.DUAL_UNLOCK);
                return EmberDualLead.glance(EmberDualLead.get(pd), dual);
            }
            case "brand_path":
            case "brand_path_line": { // D467 brand playstyle path
                PlayerData pd = p == null ? null : runs.plugin().getDataStore().get(p.getUniqueId());
                return EmberBrandPath.glance(EmberBrandPath.get(pd));
            }
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
            // D299 W1a：穿着刃/护符成色·精工近档
            case "blade_next_q": return l.blade == null ? "§8主手无刃" : EmberGearNextHint.qualityLine(l.blade);
            case "blade_next_c": return l.blade == null ? "§8主手无刃" : EmberGearNextHint.craftLine(l.blade);
            case "charm_next_q": return l.charm == null ? "§8未选护符" : EmberGearNextHint.qualityLine(l.charm);
            case "charm_next_c": return l.charm == null ? "§8未选护符" : EmberGearNextHint.craftLine(l.charm);
            // D299 W1b：工坊手持件近档（无手持 / 非 P1 → 提示）
            case "held_next_q": return heldNext(p, true);
            case "held_next_c": return heldNext(p, false);
            // D307 W1a/b：工坊本次费用 / 缺料 / 互换免费 / 分解得胚
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

    /** D299 W1b：主手 P1 件的成色/精工近档；非 P1 或空手则短提示。 */
    private String heldNext(Player p, boolean quality) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符看近档";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, "next"); // D318 K0
        return quality ? EmberGearNextHint.qualityLine(d) : EmberGearNextHint.craftLine(d);
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

    /** D307 W1a：费用/闸/得胚行（与 UpgradeRules / Forge 预览同源）。 */
    private String heldForgeLine(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null) return "§8手持刃或护符";
        if (d.isArmor()) return EmberSixPapi.heldArmorLine(d, kind); // D318 K0 / 分解 ×0.1
        if ("enhance".equals(kind)) return EmberGearNextHint.enhanceLine(d);
        if ("upgrade".equals(kind)) {
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(runs.plugin().getDataStore().get(p.getUniqueId()), flag);
            return EmberGearNextHint.upgradeLine(d, gate);
        }
        if ("dismantle".equals(kind)) return EmberGearNextHint.dismantleYieldLine(d);
        return "";
    }

    /** D307 W1b：缺料半行；材料够 / 无费用 → 空。 */
    private String heldForgeLack(Player p, String kind) {
        EmberItemData d = heldTrusted(p);
        if (d == null || d.isArmor()) return ""; // D318: armor pays nothing (K0)
        EmberUpgradeRules.Cost cost = null;
        if ("enhance".equals(kind)) {
            EmberUpgradeRules.Plan c = EmberUpgradeRules.enhanceCheck(d);
            if (!c.ok()) return "";
            cost = c.cost;
        } else if ("upgrade".equals(kind)) {
            cost = EmberUpgradeRules.upgradeCost(d.tier);
            if (cost == null) return "";
            String flag = EmberUpgradeRules.upgradeFlag(d.tier);
            boolean gate = flag != null && runs.progressFlag(runs.plugin().getDataStore().get(p.getUniqueId()), flag);
            if (!gate) return ""; // gate message already on cost line
        } else if ("refine".equals(kind)) {
            cost = EmberUpgradeRules.refineCost(d.craft);
        } else if ("quality".equals(kind)) {
            cost = EmberUpgradeRules.qualityCost(d.quality);
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
