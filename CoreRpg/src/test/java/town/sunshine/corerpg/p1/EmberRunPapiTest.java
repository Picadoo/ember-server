package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static town.sunshine.corerpg.p1.EmberRunPapi.Section.*;

/**
 * D240 / ARCH S3-11: {@link EmberRunPapi} routing is an ordered copy of the pre-split {@code EmberRunService.placeholder}
 * if-chain, and the text helpers it extracted keep the old concatenations byte-for-byte.
 */
public final class EmberRunPapiTest {

    private static void r(EmberRunPapi.Section s, String... keys) {
        for (String k : keys) assertEquals(k, s, EmberRunPapi.route(k));
    }

    @Test public void routesEveryMenuFamily() {
        r(ENTRY, "pass_q01", "pass_rush", "pass_gq26", "passd_emberq01", "target", "marks_t1", "marks_t3", "marks_tx",
                "pending", "active", "next", "recruits", "failrefund", "route_pri", "route_sec", "featured_left");
        r(RUSH, "vbounty", "rush", "rush_q0r1", "rush_nosuch", "pledge_head", "pledge_n_lean", "pledge_ok");
        r(GROWTH, "sign_state", "online_d1", "online_min", "afk_today", "afk_t1", "afk_remain", "afk_eta_min", "afk_remain_line", "sig_q01", "reroll_blade_cap",
                "honor_name_1", "spec_x");
        r(GATE, "forge_t2", "forge_t3", "challenge", "q07done", "q04done", "q05done", "q06done", "q09done");
        r(ROTATION, "featured", "featured_key", "modifier", "rule_q01", "bounty", "loot_q01", "loot_r01");
        r(BOARD, "top_abyss_1", "top_abyss_10", "top_featured_3", "top_abyss_x");
        r(SEASON, "goals", "goal_core", "goal_short", "goalnosuch", "season", "season_week", "sboard_1", "srank_abyss", "badges"); // D401 goal_short
        r(COSMETIC, "shop", "shopprice_x", "shopsel_name", "shop_x", "title", "honors");
        r(FESTIVAL, "fest_name", "fest_charm_price", "fest_nosuch");
        r(ABYSS, "abyss_best", "abyss_state", "abyss_t1", "abyss_t10", "abyss_tx");
        r(RAID, "raid_r01", "raid_r03");
        r(LOADOUT, "awaken_route", "awaken", "awaken_next", "set_progress", "stats", "ehp", "blade", "charm", "blade_next_q", "blade_next_c", "charm_next_q", "charm_next_c", "held_next_q", "held_next_c", "held_enhance_cost", "held_enhance_lack", "held_upgrade_cost", "held_upgrade_lack", "held_refine_lack", "held_quality_lack", "held_swap_cost", "held_dismantle_yield", "held_is_armor");
        r(CODEX, "codex_count", "codex_stage_0", "codex_burst_blade_t1", "codexx");
        r(ARMOR, "armor_on", "armor_head", "armor_chest_cand", "armor_legs_delta", "armor_boots_fam", "armor_set", "armor_set_active", "armor_set_busy", "armor_sa", "armor_sb", "armor_all", "armor_stash_line"); // D318 + D335 sa/sb
        r(SHORT, "sx01_day", "sx02_day", "sx03_day", "sx04_day", "sx05_day", "sx06_day", "sx01_day_line", "sx02_day_line", "sx03_day_line", "sx04_day_line", "sx05_day_line", "sx06_day_line",
                "sx01_day_left", "sx02_day_left", "sx03_day_left", "sx04_day_left", "sx05_day_left", "sx06_day_left", "sx_day_left_sum"); // D395+D397+D400+D403
        r(VAULT, "vault_shard", "vault_bone", "vault_core", "vault_blank", "recipe_gap_enhance1", "recipe_gap_upgrade_t2", "recipe_gap_refine1"); // D404
        r(MAP, "q01_state", "q07_fc", "r01_name", "q01_nosuch", "nosuch", "", null, "sx01_state", "sx02_open", "sx03_fc", "sx04_fc", "sx05_fc", "sx06_fc");
    }

    /** Order pins where a later check shares a prefix with an earlier one (old first-match must win). */
    @Test public void firstMatchOrderPreserved() {
        assertEquals(ENTRY, EmberRunPapi.route("pass_d"));      // "pass_" before "passd_"
        assertEquals(ENTRY, EmberRunPapi.route("passd_x"));     // "passd_" is not "pass_"
        assertEquals(GROWTH, EmberRunPapi.route("sign_x"));     // sign_ ≠ sig_
        assertEquals(GROWTH, EmberRunPapi.route("sig_x"));
        assertEquals(ROTATION, EmberRunPapi.route("featured")); // featured exact (P2-1) vs featured_key later
        assertEquals(ROTATION, EmberRunPapi.route("featured_key"));
        assertEquals(BOARD, EmberRunPapi.route("top_featured_1"));
        assertEquals(ABYSS, EmberRunPapi.route("abyss_best"));  // before abyss_t*
        assertEquals(ABYSS, EmberRunPapi.route("abyss_tier"));  // abyss_t* (parse fails → "")
        assertEquals(COSMETIC, EmberRunPapi.route("shop_"));
        assertEquals(GATE, EmberRunPapi.route("q0xdone"));       // 7-char q0?done
        assertEquals(MAP, EmberRunPapi.route("q01done_x"));
        assertEquals(LOADOUT, EmberRunPapi.route("awaken_route"));
        assertEquals(SHORT, EmberRunPapi.route("sx01_day")); // D395 before map tail
        assertEquals(MAP, EmberRunPapi.route("sx01_state"));
        assertEquals(VAULT, EmberRunPapi.route("vault_shard")); // D404 before map
        assertEquals(VAULT, EmberRunPapi.route("recipe_gap_upgrade_t2"));
        assertEquals(VAULT, EmberRunPapi.route("recipe_gap_refine1"));
        assertTrue(EmberRunPapi.isQDone("q04done"));
        assertFalse(EmberRunPapi.isQDone("q10done"));
        assertFalse(EmberRunPapi.isQDone("q04don"));
        assertTrue(EmberRunPapi.isShortDayKey("sx01_day"));
        assertTrue(EmberRunPapi.isShortDayKey("sx03_day_line"));
        assertTrue(EmberRunPapi.isShortDayKey("sx04_day"));
        assertTrue(EmberRunPapi.isShortDayKey("sx04_day_line"));
        assertTrue(EmberRunPapi.isShortDayKey("sx05_day"));
        assertTrue(EmberRunPapi.isShortDayKey("sx05_day_line"));
        assertTrue(EmberRunPapi.isShortDayKey("sx06_day"));
        assertTrue(EmberRunPapi.isShortDayKey("sx06_day_line"));
        assertTrue(EmberRunPapi.isShortDayKey("sx_day_left_sum"));
        assertFalse(EmberRunPapi.isShortDayKey("sx01_state"));
        assertFalse(EmberRunPapi.isShortDayKey("sx01_day_extra"));
        assertTrue(EmberRunPapi.isVaultKey("vault_shard"));
        assertTrue(EmberRunPapi.isVaultKey("vault_bone"));
        assertTrue(EmberRunPapi.isVaultKey("vault_core"));
        assertTrue(EmberRunPapi.isVaultKey("vault_blank"));
        assertTrue(EmberRunPapi.isVaultKey("recipe_gap_enhance1"));
        assertTrue(EmberRunPapi.isVaultKey("recipe_gap_upgrade_t2"));
        assertTrue(EmberRunPapi.isVaultKey("recipe_gap_refine1"));
        assertFalse(EmberRunPapi.isVaultKey("vault_soul"));
        assertFalse(EmberRunPapi.isVaultKey("held_enhance_lack"));
    }

    /** Every %corerpg_p1_*% key the live TrMenu / DP / resource configs use routes to a named section, or is a map field. */
    @Test public void bundledAndRuntimeConfigKeysRoute() throws IOException {
        Pattern p = Pattern.compile("%corerpg_p1_([a-z0-9_]+)%");
        TreeSet<String> keys = new TreeSet<String>();
        for (String dir : Arrays.asList("src/main/resources", "../server-runtime/plugins/TrMenu", "../server-runtime/plugins/DungeonPlus")) {
            Path root = Paths.get(dir);
            if (!Files.isDirectory(root)) continue;
            try (Stream<Path> s = Files.walk(root)) {
                for (Path f : (Iterable<Path>) s.filter(x -> x.toString().endsWith(".yml"))::iterator) {
                    Matcher m = p.matcher(new String(Files.readAllBytes(f), StandardCharsets.UTF_8));
                    while (m.find()) keys.add(m.group(1));
                }
            }
        }
        Pattern mapField = Pattern.compile("[a-z0-9]+_(state|open|cleared|name|cost|tier|purpose|fc)");
        TreeSet<String> stray = new TreeSet<String>();
        for (String k : keys) if (EmberRunPapi.route(k) == MAP && !mapField.matcher(k).matches()) stray.add(k);
        assertEquals(Collections.<String>emptySet(), stray);
    }

    @Test public void textsMatchPreSplit() {
        assertEquals("§a本周奖励未领 · 失败可无限重试", EmberRunPapi.rushLine(true));
        assertEquals("§7本周奖励已领 · 可练习（无奖励）", EmberRunPapi.rushLine(false));
        assertEquals("§a本周已领 1/3 · 失败可无限重试", EmberRunPapi.rushEntryLine(true, 1, 3));
        assertEquals("§7本周 3 次已领完 · 可练习（无奖励）", EmberRunPapi.rushEntryLine(false, 3, 3));
        assertEquals("§8需本人首通 Q07", EmberRunPapi.needFirstClear("q07"));
        assertEquals("Bob · 第 12 层", EmberRunPapi.topRowText("Bob", 12, true));
        assertEquals("Bob · 4 次", EmberRunPapi.topRowText("Bob", 4, false));
        assertEquals(1, EmberRunPapi.topRank("top_abyss_1"));
        assertEquals(10, EmberRunPapi.topRank("top_featured_10"));
        assertEquals(-1, EmberRunPapi.topRank("top_abyss_11"));
        assertEquals(-1, EmberRunPapi.topRank("top_abyss_0"));
        assertEquals(-1, EmberRunPapi.topRank("top_abyss_x"));
        assertEquals("未配置", EmberRunPapi.abyssStateLine(false, false, "q04", 0, 0));
        assertEquals("需本人首通 Q04", EmberRunPapi.abyssStateLine(true, false, "q04", 0, 0));
        assertEquals("最高第 7 层 · 可开 1～5 层", EmberRunPapi.abyssStateLine(true, true, "q04", 7, 5));
        assertEquals("攻击 12.3 · 生命 140 · 防御 8（承伤 ×0.87）· Lv12", EmberRunPapi.statsLine(12.34, 140.2, 8.4, 0.8712, 12));
        assertEquals("", EmberRunPapi.awakenRouteLine(Collections.<String>emptyList()));
        assertEquals("§7路线：A", EmberRunPapi.awakenRouteLine(Collections.singletonList("A")));
        assertEquals("§7路线：A §8（还有 2 条，点开看）", EmberRunPapi.awakenRouteLine(Arrays.asList("A", "B", "C")));
        // D404 recipe gap (mirror UpgradeRules; upgrade_t2 钉死拼接半行，非拆三键)
        assertEquals(0, EmberRunPapi.recipeGap(4, 4));
        assertEquals(0, EmberRunPapi.recipeGap(4, 10));
        assertEquals(4, EmberRunPapi.recipeGap(4, 0));
        assertEquals(1, EmberRunPapi.recipeGap(4, 3));
        assertEquals("4", EmberRunPapi.recipeGapEnhance1(0));
        assertEquals("0", EmberRunPapi.recipeGapEnhance1(4));
        assertEquals("0", EmberRunPapi.recipeGapEnhance1(99));
        assertEquals("1", EmberRunPapi.recipeGapEnhance1(3));
        assertEquals("碎差60·核差12·胚差6", EmberRunPapi.recipeGapUpgradeT2(0, 0, 0));
        assertEquals("碎差0·核差0·胚差0", EmberRunPapi.recipeGapUpgradeT2(60, 12, 6));
        assertEquals("碎差10·核差2·胚差1", EmberRunPapi.recipeGapUpgradeT2(50, 10, 5));
        assertEquals("胚差3·骨差5", EmberRunPapi.recipeGapRefine1(0, 0));
        assertEquals("胚差0·骨差0", EmberRunPapi.recipeGapRefine1(3, 5));
        assertEquals("胚差1·骨差2", EmberRunPapi.recipeGapRefine1(2, 3));
    }

    /** EmberRunService keeps only a one-line delegate; no section body is left behind. */
    @Test public void runServiceIsThinDelegate() throws IOException {
        String s = new String(Files.readAllBytes(Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRunService.java")), StandardCharsets.UTF_8);
        assertTrue(s.contains("public String placeholder(Player p, String key) { return papi.placeholder(p, key); }"));
        assertFalse(s.contains("key.startsWith(\"top_abyss_\")"));
        assertFalse(s.contains("case \"purpose\": return m.purpose;"));
    }
}
