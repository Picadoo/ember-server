package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.PlayerData;

/**
 * D213 registry + D215/D216 (ARCH S2-2/S2-3): {@link EmberEconomy} matches REG-ember-source-sink-cap and the live game —
 * counter families exist, golden amounts equal the constants / shipped plugin yml, the unmodelled set is pinned,
 * settle / shop / sign / grantCoin/Mark/Xp/Mat / spendCoin route through the registry without changing amounts,
 * and a scoped {@code p1/} addCoin scan fails on new grant paths that skip the registry. D218 (S2-4): workshop / mark
 * exchange / abyss fee / talent / reroll / imprint / attune spends (C03–C13) route through spend* and a {@code p1/}
 * takeCoin scan guards new direct spends. D223 (S2-6): delivery coin debit via spendCoinDelivery;
 * EmberDelivery off the takeCoin allowlist. D224: ember-v1-economy.yml is amount() SoT (load + dual-assert golden).
 * D228 (S2-8): insignia/badge grants + account-counter scan. D229 (S2-9): abyss floor SourceId S13 + vault-write scan.
 */
public class EmberEconomyTest {
    private static final double EPS = 1e-9;

    /** The deployed plugin data file (plugins/CoreRpg/…), falling back to the bundled default. */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> yml(String file) throws Exception {
        Path live = Paths.get("..", "plugins", "CoreRpg", file);
        Reader r;
        if (Files.isRegularFile(live)) r = Files.newBufferedReader(live, StandardCharsets.UTF_8);
        else {
            InputStream in = EmberEconomyTest.class.getResourceAsStream("/" + file);
            assertNotNull(file, in);
            r = new InputStreamReader(in, StandardCharsets.UTF_8);
        }
        try { return (Map<String, Object>) new Yaml().load(r); } finally { r.close(); }
    }

    @SuppressWarnings("unchecked")
    private static Object at(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            assertTrue(path + " (at " + p + ")", cur instanceof Map);
            cur = ((Map<String, Object>) cur).get(p);
            assertNotNull(path + " missing " + p, cur);
        }
        return cur;
    }

    private static double num(Map<String, Object> root, String path) { return ((Number) at(root, path)).doubleValue(); }

    private static void eq(String id, String k, double live) {
        assertEquals(id + " " + k + " (registry vs live)", EmberEconomy.byId(id).golden(k), live, EPS);
    }

    /** D224: each test starts with classpath yml loaded as SoT (isolates fail-closed tests). */
    @Before
    public void loadEconomyYmlFromClasspath() throws Exception {
        EmberEconomy.resetEconomyYmlForTest();
        Map<String, Object> y = yml(EmberEconomy.ECONOMY_YML);
        assertEquals(new ArrayList<String>(), EmberEconomy.loadEconomyYml(y));
        assertTrue(EmberEconomy.economyYmlReady());
    }

    @Test
    public void idsAreCompleteAndOrdered() {
        List<String> want = new ArrayList<String>();
        for (int i = 1; i <= 55; i++) want.add(String.format("S%02d", i)); // D425: S55; D423: S53; D421: S52 short sx13; D419: S51 short sx12; D417: S50 short sx11; D414: S49; D412: S48; D410: S47; D407: S46; D403: S45; D400: S44; D397: S43; D393: S42; D392: S41; D391: S40; D318: S39; D243: S33–S35; D244: S36–S38
        for (int i = 1; i <= 5; i++) want.add("LS" + i);
        for (int i = 1; i <= 19; i++) want.add(String.format("C%02d", i)); // D244: C19 gacha pull
        List<String> got = new ArrayList<String>();
        for (EmberEconomy.Row r : EmberEconomy.all()) got.add(r.id);
        assertEquals(want, got);
        assertEquals(55, EmberEconomy.sources().size());
        assertEquals(5, EmberEconomy.legacySources().size());
        assertEquals(19, EmberEconomy.sinks().size());
        for (EmberEconomy.Row r : EmberEconomy.all()) {
            assertFalse(r.id + " name", r.name.isEmpty());
            assertFalse(r.id + " owner", r.owner.isEmpty());
            assertEquals(r.id + " sink flag", r.id.startsWith("C"), r.sink);
            assertEquals(r.id + " legacy flag", r.id.startsWith("LS"), r.legacy);
            if (!r.legacy) assertFalse(r.id + " pays / takes nothing", r.accounts.isEmpty());
        }
    }

    @Test
    public void everyCounterFamilyIsRegistered() {
        assertEquals(new ArrayList<String>(), EmberEconomy.counterErrors());
        // legacy rows only name legacy families; P1 rows never lean on a legacy family
        for (EmberEconomy.Row r : EmberEconomy.all())
            for (String k : r.counters) {
                EmberCounters.Family f = EmberCounters.byKey(k);
                if (r.legacy) assertTrue(r.id + ":" + k + " should be a legacy family", f.legacy);
                else if (!r.id.equals("S30") && !r.id.equals("C17")) assertFalse(r.id + ":" + k + " is legacy", f.legacy);
            }
    }

    @Test
    public void everyAssetFamilyHasASourceAndASinkOrIsBound() {
        // REG §1: every spendable account has at least one P1 source; the insignia / mark accounts also have a sink
        Set<String> assetKeys = new HashSet<String>();
        for (EmberCounters.Family f : EmberCounters.all()) if (f.asset() && !f.legacy) assetKeys.add(f.key);
        Set<String> named = new HashSet<String>();
        for (EmberEconomy.Row r : EmberEconomy.all()) named.addAll(r.counters);
        for (String k : assetKeys) assertTrue("asset family " + k + " not in the economy table", named.contains(k));
        assertFalse(EmberEconomy.touching(EmberEconomy.Account.INSIGNIA).isEmpty());
        boolean insSink = false, markSink = false;
        for (EmberEconomy.Row r : EmberEconomy.sinks()) {
            insSink |= r.accounts.contains(EmberEconomy.Account.INSIGNIA);
            markSink |= r.accounts.contains(EmberEconomy.Account.MARK);
        }
        assertTrue("insignia sink", insSink);
        assertTrue("mark sink", markSink);
    }

    @Test
    public void goldenAmountsMatchJavaConstants() {
        eq("S01", "coin", EmberRunRules.BASE_COIN);
        eq("S01", "shard", EmberRunRules.BASE_SHARD);
        eq("S01", "bone", EmberRunRules.BASE_BONE);
        eq("S01", "core", EmberRunRules.BASE_CORE);
        eq("S01", "xp", EmberRunRules.BASE_XP);
        eq("S01", "mark", EmberRunRules.BASE_MARK);
        eq("S02", "coin", EmberRunRules.TREASURE_COIN);
        eq("S03", "shard", EmberRunRules.ELITE_SHARD);
        eq("S03", "core", EmberRunRules.ELITE_CORE);
        eq("S07", "insignia", EmberSignature.FC_MARKS);
        eq("S08", "insignia", EmberSignature.CLEAR_MARKS);
        eq("S08", "stamp_rate", EmberSignature.STAMP_RATE);
        eq("S28", "marks", EmberRunRules.MARKS_PER_EXCHANGE);
        eq("C07", "marks", EmberRunRules.MARKS_PER_EXCHANGE);
        eq("C12", "insignia", EmberSignature.IMPRINT_MARKS);
        eq("C12", "coin_per_tier", EmberSignature.IMPRINT_COIN_PER_TIER);
        eq("C13", "insignia", EmberSignature.ALT_MARKS);
        // D243 (S4-2): G1 codex stage coin, G2 chest extra roll, G3 starter kit, G8 pledge per rule
        for (int i = 0; i < EmberCodex.STAGE_AT.length; i++) eq("S33", "at" + EmberCodex.STAGE_AT[i] + ".coin", EmberCodex.STAGE_COIN[i]);
        assertEquals("S33 rows = codex stages", EmberCodex.STAGE_AT.length, EmberEconomy.byId("S33").golden.size());
        eq("S34", "chest_weight", EmberRunRules.EXTRA_WEIGHTS[3]);
        eq("S35", "pieces", 2);
        eq("S09", "per_rule", 1);
    }

    /** D243 (S4-2): new rows are tags only — routing of the codex coin / chest item / starter rows, amounts unchanged. */
    @Test
    public void d243RoutesCodexChestStarter() {
        assertEquals("S33", EmberEconomy.sourceForGrant("stage0", EmberCodex.LEDGER_RUN));
        assertEquals("S33", EmberEconomy.sourceForGrant("stage3", "codex"));
        assertEquals(null, EmberEconomy.sourceForGrant("stage0", "q01-abc-def"));
        assertEquals("S34", EmberEconomy.sourceForGrant("extra_chest_item", "q03-abc-def"));
        assertEquals("S34", EmberEconomy.sourceForGrantKey("extra_chest_item"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_item", "q03-abc-def"));
        assertEquals("S35", EmberEconomy.sourceForGrant("starter_blade", "starter"));
        assertEquals("S35", EmberEconomy.sourceForGrant("starter_charm", "starter"));
        assertTrue(EmberEconomy.pays("S33", EmberEconomy.Account.COIN));
        assertTrue(EmberEconomy.pays("S35", EmberEconomy.Account.POTION));
        assertTrue(EmberEconomy.pays("S34", EmberEconomy.Account.GEAR));
        PlayerData d = new PlayerData();
        int c0 = d.getCoin();
        assertTrue(EmberEconomy.grantCoin(d, EmberEconomy.sourceForGrant("stage1", "codex"), EmberCodex.STAGE_COIN[1]));
        assertEquals(c0 + 400, d.getCoin());
        assertEquals(1, EmberEconomy.amount("S09", "per_rule"));
        EmberRunRules.Grant g = EmberPledgeService.settleGrant("q02", 3);
        assertEquals(3, g.amount);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void goldenAmountsMatchShippedRunsYml() throws Exception {
        Map<String, Object> y = yml("ember-v1-runs.yml");
        eq("S04", "shard", num(y, "variety.affix_shard"));
        eq("S05", "core", num(y, "variety.event_core"));
        for (String m : Arrays.asList("q03", "q04", "q05", "q06", "q07")) {
            Map<String, Object> fc = (Map<String, Object>) at(y, "maps." + m + ".first_clear");
            int seen = 0;
            for (Map.Entry<String, Double> g : EmberEconomy.byId("S06").golden.entrySet()) {
                if (!g.getKey().startsWith(m + ".")) continue;
                String k = g.getKey().substring(m.length() + 1);
                assertNotNull("S06 " + g.getKey() + " not in yml", fc.get(k));
                assertEquals("S06 " + g.getKey(), g.getValue(), ((Number) fc.get(k)).doubleValue(), EPS);
                seen++;
            }
            assertEquals("S06 " + m + " every yml amount registered", fc.size(), seen);
        }
        eq("S10", "mark", num(y, "rotation.bonus_marks"));
        eq("S10", "weekly_cap", num(y, "rotation.weekly_cap"));
        eq("S11", "weekly_cap", num(y, "rotation.weekly_cap"));
        Map<String, Object> raids = (Map<String, Object>) at(y, "raids");
        for (Map.Entry<String, Object> e : raids.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            Map<String, Object> r = (Map<String, Object>) e.getValue();
            if (r.get("weekly_cap") == null) continue;
            eq("S12", "weekly_cap", ((Number) r.get("weekly_cap")).doubleValue());
            assertEquals("raid " + e.getKey() + " shares the raid cap group", "raid", r.get("cap_group"));
        }
        eq("S14", "rate", num(y, "fail_refund"));
        eq("S14", "cost", num(y, "cost"));
        eq("C01", "stamina", num(y, "cost"));
        eq("S16", "mark", num(y, "rush.rush.reward.marks"));
        eq("S16", "badge", num(y, "rush.rush.reward.badges"));
        eq("S17", "mark", num(y, "rush.outpost.reward.marks"));
        eq("S17", "mark_tier", num(y, "rush.outpost.reward.mark_tier"));
        eq("S17", "insignia", num(y, "rush.outpost.reward.sigmarks"));
        eq("S17", "weekly", num(y, "rush.outpost.weekly"));
        for (String h : Arrays.asList("echo_q01", "echo_q02", "echo_q03", "echo_q04", "echo_q05", "echo_q06", "echo_q07")) {
            eq("S18", "insignia", num(y, "rush." + h + ".reward.sigmarks"));
            eq("S18", "weekly", num(y, "rush." + h + ".weekly"));
            assertEquals(h + " shares p4_echo_claim", "p4_echo_claim", at(y, "rush." + h + ".claim"));
        }
        assertEquals("p4_outpost_claim", at(y, "rush.outpost.claim"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void goldenAmountsMatchShippedEmberYml() throws Exception {
        Map<String, Object> y = yml("ember-v1.yml");
        eq("S15", "max_potions", num(y, "death_refund.max_potions"));
        eq("S35", "potions", num(y, "starter.heal_potions")); // D243 (G3)
        List<Object> daily = (List<Object>) at(y, "bounty.daily");
        Map<String, Object> d1 = (Map<String, Object>) daily.get(0), d3 = (Map<String, Object>) daily.get(1);
        assertEquals(1, ((Number) d1.get("clears")).intValue());
        assertEquals(3, ((Number) d3.get("clears")).intValue());
        eq("S20", "c1.coin", ((Number) d1.get("coin")).doubleValue());
        eq("S20", "c3.coin", ((Number) d3.get("coin")).doubleValue());
        eq("S20", "c3.shard", ((Number) d3.get("shard")).doubleValue());
        for (Object o : (List<Object>) at(y, "bounty.variety"))
            eq("S21", "coin", ((Number) ((Map<String, Object>) o).get("coin")).doubleValue());
        eq("S22", "daily_kills", num(y, "afk.daily_kills"));
        eq("S22", "offline_max_kills", num(y, "afk.offline.max_kills"));
        eq("S23", "daily.coin", num(y, "signin.daily.coin"));
        eq("S23", "daily.xp", num(y, "signin.daily.xp"));
        eq("S23", "makeup_per_month", num(y, "signin.makeup_per_month"));
        eq("S23", "sigmark_fallback_coin", num(y, "signin.sigmark_fallback_coin"));
        double coin = 0, xp = 0, top = 0;
        for (Object o : (List<Object>) at(y, "online.milestones")) {
            Map<String, Object> m = (Map<String, Object>) o;
            coin += m.get("coin") == null ? 0 : ((Number) m.get("coin")).doubleValue();
            xp += m.get("xp") == null ? 0 : ((Number) m.get("xp")).doubleValue();
            top = Math.max(top, ((Number) m.get("min")).doubleValue());
        }
        eq("S24", "coin_total", coin);
        eq("S24", "xp_total", xp);
        eq("S24", "top_min", top);
        eq("C14", "coin", num(y, "shop.heal_potion.price"));
    }

    @Test
    public void modelGapsArePinned() {
        // REG §5: the S2 work list. Closing a gap (adding it to p1sim / p2econ) or adding an unmodelled source must
        // update this list, the REG table and the source-table D row together.
        assertEquals(Arrays.asList("S07", "S08", "S09", "S16", "S17", "S18", "S27", "S30", "S31", "S32", "S36", "S37", "S39",
                "C10", "C12", "C13", "C16", "C17", "C18"), EmberEconomy.modelGaps());
        // insignia: every source and both sinks are outside the offline sim today (REG §4 / §5 gap 1)
        for (EmberEconomy.Row r : EmberEconomy.touching(EmberEconomy.Account.INSIGNIA))
            assertTrue(r.id + " insignia modelled?", r.model != EmberEconomy.Model.FULL || r.id.equals("S23"));
    }

    @Test
    public void legacySourcesAreOutsideTheModel() {
        for (EmberEconomy.Row r : EmberEconomy.legacySources()) {
            assertEquals(r.id, EmberEconomy.Model.OUT, r.model);
            assertTrue(r.id + " pays nothing under P1", r.accounts.isEmpty());
        }
    }

    @Test
    public void settleBaseAndExtrasUseRegistryAmounts() {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.bossKilled = true;
        in.tier = 1;
        in.seed = 1L;
        in.player = "t";
        in.runId = "r";
        Map<String, EmberRunRules.Grant> g = new java.util.LinkedHashMap<String, EmberRunRules.Grant>();
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
        assertEquals(EmberEconomy.amount("S01", "coin"), g.get("base_coin").amount);
        assertEquals(EmberEconomy.amount("S01", "shard"), g.get("base_shard").amount);
        assertEquals(EmberEconomy.amount("S01", "bone"), g.get("base_bone").amount);
        assertEquals(EmberEconomy.amount("S01", "core"), g.get("base_core").amount);
        assertEquals(EmberEconomy.amount("S01", "xp"), g.get("base_xp").amount);
        assertEquals(EmberEconomy.amount("S01", "mark"), g.get("base_mark").amount);
        // still equal to the historical Java constants (no number change)
        assertEquals(EmberRunRules.BASE_COIN, g.get("base_coin").amount);
        assertEquals(EmberRunRules.BASE_SHARD, g.get("base_shard").amount);

        in.extraDone = true;
        in.extra = EmberRunRules.Extra.TREASURE;
        g.clear();
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
        assertEquals(EmberEconomy.amount("S02", "coin"), g.get("extra_treasure_coin").amount);
        assertEquals(EmberRunRules.TREASURE_COIN, g.get("extra_treasure_coin").amount);

        in.extra = EmberRunRules.Extra.ELITE;
        g.clear();
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
        assertEquals(EmberEconomy.amount("S03", "shard"), g.get("extra_elite_shard").amount);
        assertEquals(EmberEconomy.amount("S03", "core"), g.get("extra_elite_core").amount);
        assertEquals(EmberRunRules.ELITE_SHARD, g.get("extra_elite_shard").amount);
        assertEquals(EmberRunRules.ELITE_CORE, g.get("extra_elite_core").amount);
    }

    @Test
    public void shopPriceAndSpendRouteThroughC14() {
        assertEquals(EmberEconomy.amount("C14", "coin"), EmberSupplyService.price());
        assertEquals(10, EmberSupplyService.price()); // book / yml / golden = 10

        PlayerData d = new PlayerData();
        d.setCoin(100);
        assertTrue(EmberEconomy.spendCoin(d, "C14", EmberEconomy.amount("C14", "coin")));
        assertEquals(90, d.getCoin());
        assertFalse("short balance", EmberEconomy.spendCoin(d, "C14", 1000));
        assertEquals(90, d.getCoin());
        assertFalse("source is not a sink", EmberEconomy.spendCoin(d, "S01", 10));
        assertFalse("legacy refused", EmberEconomy.spendCoin(d, "LS1", 10));
        assertEquals(90, d.getCoin());
    }

    @Test
    public void grantCoinRoutesSourcesAndRefusesSinks() {
        assertEquals("S01", EmberEconomy.sourceForGrantKey("base_coin"));
        assertEquals("S02", EmberEconomy.sourceForGrantKey("extra_treasure_coin"));
        assertEquals("S03", EmberEconomy.sourceForGrantKey("extra_elite_shard"));
        assertEquals("S04", EmberEconomy.sourceForGrantKey("var_affix_shard"));
        assertEquals("S05", EmberEconomy.sourceForGrantKey("var_event_core"));
        assertEquals("S20", EmberEconomy.sourceForGrantKey("bounty_coin_1"));
        assertEquals("S06", EmberEconomy.sourceForGrantKey("fc_q03_coin"));
        assertEquals(null, EmberEconomy.sourceForGrantKey("unknown_key"));

        PlayerData d = new PlayerData();
        assertTrue(EmberEconomy.grantCoin(d, "S01", EmberEconomy.amount("S01", "coin")));
        assertEquals(EmberRunRules.BASE_COIN, d.getCoin());
        assertTrue(EmberEconomy.grantCoin(d, "S02", EmberEconomy.amount("S02", "coin")));
        assertEquals(EmberRunRules.BASE_COIN + EmberRunRules.TREASURE_COIN, d.getCoin());
        assertFalse("sink refused", EmberEconomy.grantCoin(d, "C14", 10));
        assertFalse("legacy refused", EmberEconomy.grantCoin(d, "LS2", 10));
        assertFalse("zero refused", EmberEconomy.grantCoin(d, "S01", 0));
        assertEquals(EmberRunRules.BASE_COIN + EmberRunRules.TREASURE_COIN, d.getCoin());
    }

    @Test
    public void signAndOnlineAmountsAndSources() {
        // S23 daily / makeup / fallback are the registry amounts EmberSignService.reload reads
        assertEquals(20, EmberEconomy.amount("S23", "daily.coin"));
        assertEquals(5, EmberEconomy.amount("S23", "daily.xp"));
        assertEquals(3, EmberEconomy.amount("S23", "makeup_per_month"));
        assertEquals(60, EmberEconomy.amount("S23", "sigmark_fallback_coin"));
        assertEquals(70, EmberEconomy.amount("S24", "coin_total"));
        assertEquals(20, EmberEconomy.amount("S24", "xp_total"));
        assertEquals(120, EmberEconomy.amount("S24", "top_min"));

        assertEquals("S23", EmberEconomy.sourceForGrant("n1c", "p1sign-2026-10"));
        assertEquals("S23", EmberEconomy.sourceForGrant("n7s", "p1sign-2026-10"));
        assertEquals("S24", EmberEconomy.sourceForGrant("m15c", "p1online-2026-10-06"));
        assertEquals("S24", EmberEconomy.sourceForGrant("m120x", "p1online-2026-10-06"));
        assertEquals("S22", EmberEconomy.sourceForGrant("c120", "p1afk-2026-10-06")); // D221 AFK ledger
        assertEquals("S22", EmberEconomy.sourceForGrant("s2400", "p1afk-2026-10-06"));
        assertEquals(null, EmberEconomy.sourceForGrant("n1c", "other-run"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", "p1sign-ignored")); // key wins over run
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", "p1afk-ignored"));

        PlayerData d = new PlayerData();
        assertTrue(EmberEconomy.grantCoin(d, "S23", EmberEconomy.amount("S23", "daily.coin")));
        assertEquals(20, d.getCoin());
        assertTrue(EmberEconomy.grantXp("S23", EmberEconomy.amount("S23", "daily.xp")));
        assertTrue(EmberEconomy.grantXp("S24", 10));
        assertFalse("sink", EmberEconomy.grantXp("C14", 10));
        assertFalse("no xp on S02", EmberEconomy.grantXp("S02", 10));
    }

    @Test
    public void grantMarkMatHelpers() {
        PlayerData d = new PlayerData();
        assertTrue(EmberEconomy.grantMark(d, "S01", 1, EmberEconomy.amount("S01", "mark")));
        assertEquals(1, d.periodCount(EmberEconomy.MARK_COUNTER + 1, "all"));
        assertTrue(EmberEconomy.grantMark(d, "S23", 2, 1));
        assertEquals(1, d.periodCount(EmberEconomy.MARK_COUNTER + 2, "all"));
        assertFalse("bad tier", EmberEconomy.grantMark(d, "S01", 4, 1));
        assertFalse("sink", EmberEconomy.grantMark(d, "C07", 1, 1));
        assertFalse("S02 pays no mark", EmberEconomy.grantMark(d, "S02", 1, 1));

        assertTrue(EmberEconomy.grantMat("S01", EmberUpgradeRules.MAT_SHARD, EmberEconomy.amount("S01", "shard")));
        assertTrue(EmberEconomy.grantMat("S03", EmberUpgradeRules.MAT_CORE, EmberEconomy.amount("S03", "core")));
        assertTrue(EmberEconomy.grantMat("S04", EmberUpgradeRules.MAT_SHARD, EmberEconomy.amount("S04", "shard")));
        assertTrue(EmberEconomy.grantMat("S05", EmberUpgradeRules.MAT_CORE, EmberEconomy.amount("S05", "core")));
        assertFalse("S02 pays coin only", EmberEconomy.grantMat("S02", EmberUpgradeRules.MAT_SHARD, 1));
        assertFalse("sink", EmberEconomy.grantMat("C03", EmberUpgradeRules.MAT_SHARD, 1));
        assertEquals(EmberEconomy.Account.SHARD, EmberEconomy.matAccount(EmberUpgradeRules.MAT_SHARD));
        assertEquals(EmberEconomy.Account.BLANK, EmberEconomy.matAccount(EmberUpgradeRules.MAT_BLANK));
        assertEquals(null, EmberEconomy.matAccount("mat_unknown"));
    }

    @Test
    public void settleMatXpMarkKeysMapToRegistry() {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.bossKilled = true; in.tier = 2; in.seed = 1L; in.player = "t"; in.runId = "r";
        for (EmberRunRules.Grant g : EmberRunRules.settle(in)) {
            if (g.kind == EmberRunRules.Kind.COIN || g.kind == EmberRunRules.Kind.XP
                    || g.kind == EmberRunRules.Kind.MARK || g.kind == EmberRunRules.Kind.MAT)
                assertNotNull(g.key + " needs a REG source", EmberEconomy.sourceForGrantKey(g.key));
        }
        for (EmberRunRules.Grant g : EmberRunRules.varietyGrants(false, true, 2, true, 1, "crystal"))
            assertNotNull(g.key, EmberEconomy.sourceForGrantKey(g.key));
    }

    /**
     * Scoped unregistered-grant scan (REG §6.4 first cut): every {@code .addCoin(} in {@code p1/} must live in
     * EmberEconomy.grantCoin or an allowlisted refund / txn / deliver-fallback file. A new direct grant path fails CI.
     */
    @Test
    public void p1AddCoinIsEconomyOrAllowlisted() throws Exception {
        Set<String> allowFiles = new HashSet<String>(Arrays.asList(
                "EmberEconomy.java",       // grantCoin
                "EmberSupplyService.java", // shop buy rollback
                "EmberPay.java",           // spend giveBack
                "EmberDelivery.java",      // A01 durable txn apply
                "EmberForgeService.java",  // legacy YAML giveBack (MySQL uses EmberPay)
                "EmberRunService.java",    // deliver unrouted fallback
                "EmberAbyssService.java"   // C08 abyss fee release (D231)
        ));
        Path root = Paths.get("src/main/java/town/sunshine/corerpg/p1");
        List<String> hits = new ArrayList<String>();
        try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
            for (Path f : walk.filter(x -> x.toString().endsWith(".java")).collect(java.util.stream.Collectors.toList())) {
                String name = f.getFileName().toString();
                String body = new String(Files.readAllBytes(f), StandardCharsets.UTF_8);
                int from = 0;
                while (true) {
                    int i = body.indexOf(".addCoin(", from);
                    if (i < 0) break;
                    int line = 1;
                    for (int c = 0; c < i; c++) if (body.charAt(c) == '\n') line++;
                    if (!allowFiles.contains(name)) hits.add(name + ":" + line);
                    from = i + 8;
                }
            }
        }
        assertEquals("route new p1 addCoin through EmberEconomy.grantCoin (or extend the refund allowlist)",
                new ArrayList<String>(), hits);
    }

    // ------------------------------------------------------------------ D218 / ARCH S2-4 spends

    @Test
    @SuppressWarnings("unchecked")
    public void talentGoldensMatchShippedGrowthYml() throws Exception {
        Map<String, Object> y = yml("ember-v1-growth.yml");
        eq("C10", "respec_coin", num(y, "talents.respec_coin"));
        List<Object> rows = (List<Object>) at(y, "talents.rows");
        assertEquals(3, rows.size());
        for (Object o : rows) {
            Map<String, Object> r = (Map<String, Object>) o;
            int row = ((Number) r.get("row")).intValue();
            eq("C09", "row" + row + ".coin", ((Number) r.get("coin")).doubleValue());
        }
        // parsed talents (the live owner) agree too
        EmberGrowth.Talents t = EmberGrowth.parseTalents(y);
        assertNotNull(t);
        assertEquals(EmberEconomy.amount("C10", "respec_coin"), EmberGrowth.respecCost(t, 1));
        assertEquals(0, EmberGrowth.respecCost(t, 0));
        for (EmberGrowth.Row r : t.rows) assertEquals(EmberEconomy.amount("C09", "row" + r.row + ".coin"), r.coin);
    }

    @Test
    public void forgeKindsMapToWorkshopSinks() {
        assertEquals("C03", EmberEconomy.sinkForForge("enhance"));
        assertEquals("C04", EmberEconomy.sinkForForge("upgrade"));
        assertEquals("C05", EmberEconomy.sinkForForge("refine"));
        assertEquals("C06", EmberEconomy.sinkForForge("quality"));
        assertEquals("C11", EmberEconomy.sinkForForge("reroll"));
        assertEquals("C12", EmberEconomy.sinkForForge("imprint"));
        assertEquals(null, EmberEconomy.sinkForForge("dismantle"));
        assertEquals(null, EmberEconomy.sinkForForge("swap"));
        assertEquals(null, EmberEconomy.sinkForForge(null));
        for (String k : new String[]{"enhance", "upgrade", "refine", "quality", "reroll", "imprint"}) {
            EmberEconomy.Row r = EmberEconomy.byId(EmberEconomy.sinkForForge(k));
            assertTrue(k + " → sink row", r.sink && !r.legacy);
        }
    }

    /** Every live workshop price must be fully accepted by its sink row — otherwise routing would refuse a forge. */
    @Test
    public void everyWorkshopCostIsTakenByItsSink() {
        List<EmberUpgradeRules.Cost> enh = new ArrayList<EmberUpgradeRules.Cost>();
        for (int e = 0; e < EmberTables.MAX_ENHANCE; e++) enh.add(EmberUpgradeRules.enhanceCost(e));
        assertCostsTaken("C03", enh);
        assertCostsTaken("C04", Arrays.asList(EmberUpgradeRules.upgradeCost(1), EmberUpgradeRules.upgradeCost(2)));
        assertCostsTaken("C05", Arrays.asList(EmberUpgradeRules.refineCost(0), EmberUpgradeRules.refineCost(1), EmberUpgradeRules.refineCost(2)));
        assertCostsTaken("C06", Arrays.asList(EmberUpgradeRules.qualityCost(0), EmberUpgradeRules.qualityCost(1)));
        assertCostsTaken("C11", Arrays.asList(new EmberUpgradeRules.Cost(40, 0, 0, 0, 1200)));
        // imprint = coins + boss insignia (EmberPay.Price.insignia), mark exchange = marks only
        assertEquals(null, EmberPay.Price.insignia(EmberSignature.IMPRINT_COIN_PER_TIER, "q01", EmberSignature.IMPRINT_MARKS).at("C12").sinkRefusal());
        assertEquals(null, EmberPay.Price.marks(2, EmberEconomy.amount("C07", "marks")).at("C07").sinkRefusal());
        // wrong tag is caught before anything is taken
        assertNotNull(EmberPay.Price.marks(2, 8).at("C03").sinkRefusal());
        assertNotNull(EmberPay.Price.of(EmberUpgradeRules.upgradeCost(1)).at("C03").sinkRefusal()); // C03 takes no blanks
        assertNotNull(EmberPay.Price.insignia(300, "q01", 5).at("C11").sinkRefusal());
        assertEquals("untagged (dismantle undo) keeps the direct path", null,
                EmberPay.Price.of(new EmberUpgradeRules.Cost(0, 0, 3, 0, 0)).sinkRefusal());
    }

    private static void assertCostsTaken(String sink, List<EmberUpgradeRules.Cost> costs) {
        for (EmberUpgradeRules.Cost c : costs) {
            assertNotNull(sink + " cost", c);
            EmberPay.Price p = EmberPay.Price.of(c).at(sink);
            assertEquals(sink + " " + c.json(), null, p.sinkRefusal());
            assertEquals("tag does not change the amount", EmberPay.Price.of(c).json(), p.json());
        }
    }

    @Test
    public void priceTagSurvivesPlusOnlyWhenSinksAgree() {
        EmberPay.Price a = EmberPay.Price.of(EmberUpgradeRules.refineCost(0)).at("C05");
        EmberPay.Price b = EmberPay.Price.of(EmberUpgradeRules.refineCost(1)).at("C05");
        assertEquals("C05", a.plus(b).sink);
        assertEquals("C05", a.plus(EmberPay.Price.of(EmberUpgradeRules.Cost.NONE)).sink);
        assertEquals(null, a.plus(EmberPay.Price.of(EmberUpgradeRules.qualityCost(0)).at("C06")).sink);
        assertEquals(null, EmberPay.Price.of(EmberUpgradeRules.Cost.NONE).sink);
    }

    @Test
    public void spendMarkInsigniaMatHelpers() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberEconomy.MARK_COUNTER + 2, "all", 10);
        assertTrue(EmberEconomy.spendMark(d, "C07", 2, EmberEconomy.amount("C07", "marks")));
        assertEquals(2, d.periodCount(EmberEconomy.MARK_COUNTER + 2, "all"));
        assertFalse("short", EmberEconomy.spendMark(d, "C07", 2, 8));
        assertEquals(2, d.periodCount(EmberEconomy.MARK_COUNTER + 2, "all"));
        d.addPeriodCount(EmberEconomy.MARK_COUNTER + 3, "all", 5);
        assertTrue("abyss fee in T3 marks", EmberEconomy.spendMark(d, "C08", 3, 2));
        assertEquals(3, d.periodCount(EmberEconomy.MARK_COUNTER + 3, "all"));
        assertFalse("C09 takes coin only", EmberEconomy.spendMark(d, "C09", 3, 1));
        assertFalse("source", EmberEconomy.spendMark(d, "S01", 3, 1));
        assertFalse("bad tier", EmberEconomy.spendMark(d, "C07", 0, 1));
        assertFalse("zero", EmberEconomy.spendMark(d, "C07", 3, 0));
        assertEquals(3, d.periodCount(EmberEconomy.MARK_COUNTER + 3, "all"));

        assertEquals(EmberSignature.C_MARK, EmberEconomy.INSIGNIA_COUNTER);
        d.addPeriodCount(EmberSignature.C_MARK + "q02", "all", 12);
        assertTrue(EmberEconomy.spendInsignia(d, "C13", "q02", EmberSignature.ALT_MARKS));
        assertEquals(2, d.periodCount(EmberSignature.C_MARK + "q02", "all"));
        assertFalse("short", EmberEconomy.spendInsignia(d, "C12", "q02", EmberSignature.IMPRINT_MARKS));
        assertFalse("C03 takes no insignia", EmberEconomy.spendInsignia(d, "C03", "q02", 1));
        assertFalse("source S07", EmberEconomy.spendInsignia(d, "S07", "q02", 1));
        assertFalse("no map", EmberEconomy.spendInsignia(d, "C12", "", 1));
        assertEquals(2, d.periodCount(EmberSignature.C_MARK + "q02", "all"));

        assertTrue(EmberEconomy.spendMat("C03", EmberUpgradeRules.MAT_SHARD, 1));
        assertTrue(EmberEconomy.spendMat("C05", EmberUpgradeRules.MAT_BONE, 1));
        assertFalse("C03 takes no bone", EmberEconomy.spendMat("C03", EmberUpgradeRules.MAT_BONE, 1));
        assertFalse("unknown mat", EmberEconomy.spendMat("C03", "mat_unknown", 1));
        assertFalse("source", EmberEconomy.spendMat("S01", EmberUpgradeRules.MAT_SHARD, 1));

        d.setCoin(10000);
        assertTrue(EmberEconomy.spendCoin(d, "C08", 500));
        assertTrue(EmberEconomy.spendCoin(d, "C09", EmberEconomy.amount("C09", "row1.coin")));
        assertTrue(EmberEconomy.spendCoin(d, "C10", EmberEconomy.amount("C10", "respec_coin")));
        assertEquals(10000 - 500 - 800 - 2000, d.getCoin());
        assertFalse("C07 takes no coin", EmberEconomy.spendCoin(d, "C07", 1));
        assertTrue(EmberEconomy.takes("C08", EmberEconomy.Account.MARK));
        assertFalse(EmberEconomy.takes("S13", EmberEconomy.Account.COIN));
    }

    /**
     * Scoped unregistered-spend scan (REG §6.4, S2-4): every {@code .takeCoin(} in {@code p1/} lives in EmberEconomy or an
     * allowlisted file whose sink is not routed yet (C15 paused, EmberPay untagged undo). Delivery debit routed D223.
     */
    @Test
    public void p1TakeCoinIsEconomyOrAllowlisted() throws Exception {
        Set<String> allowFiles = new HashSet<String>(Arrays.asList(
                "EmberEconomy.java",   // spendCoin / spendCoinDelivery
                "EmberPay.java",       // untagged price (dismantle undo) direct path
                "EmberCosmetics.java"  // C15 (paused, OUT of the model)
        ));
        Path root = Paths.get("src/main/java/town/sunshine/corerpg/p1");
        List<String> hits = new ArrayList<String>();
        try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
            for (Path f : walk.filter(x -> x.toString().endsWith(".java")).collect(java.util.stream.Collectors.toList())) {
                String name = f.getFileName().toString();
                String body = new String(Files.readAllBytes(f), StandardCharsets.UTF_8);
                int from = 0;
                while (true) {
                    int i = body.indexOf(".takeCoin(", from);
                    if (i < 0) break;
                    int line = 1;
                    for (int c = 0; c < i; c++) if (body.charAt(c) == '\n') line++;
                    if (!allowFiles.contains(name)) hits.add(name + ":" + line);
                    from = i + 9;
                }
            }
        }
        assertEquals("route new p1 takeCoin through EmberEconomy.spendCoin (or extend the allowlist)", new ArrayList<String>(), hits);
    }

    /** D221 / ARCH S2-5: C18 festival goldens match ember-v1-festival.yml; spendCoin/Badge/FestCoin refuse wrong rows. */
    @Test
    public void festivalC18AmountsAndSpends() throws Exception {
        assertEquals(60, EmberEconomy.amount("C18", "charm_event"));
        assertEquals(30, EmberEconomy.amount("C18", "trail_event"));
        assertEquals(15000, EmberEconomy.amount("C18", "after_coin"));
        assertEquals(300, EmberEconomy.amount("C18", "after_badge"));
        assertEquals(120, EmberEconomy.amount("C18", "memo"));
        assertEquals(5, EmberEconomy.amount("C18", "badge_rate"));
        assertEquals(40, EmberEconomy.amount("C18", "badge_cap"));

        // pin to deployed festival yml (no number change)
        Map<String, Object> fy = yml("ember-v1-festival.yml");
        eq("C18", "charm_event", num(fy, "charm.price_event"));
        eq("C18", "after_coin", num(fy, "charm.after.price_coin"));
        eq("C18", "after_badge", num(fy, "charm.after.price_badge"));
        eq("C18", "trail_event", num(fy, "trail.price_event"));
        eq("C18", "memo", num(fy, "exchange.memo.price"));
        eq("C18", "badge_rate", num(fy, "exchange.badge.rate"));
        eq("C18", "badge_cap", num(fy, "exchange.badge.cap"));

        assertTrue(EmberEconomy.takes("C18", EmberEconomy.Account.FEST_COIN));
        assertTrue(EmberEconomy.takes("C18", EmberEconomy.Account.COIN));
        assertTrue(EmberEconomy.takes("C18", EmberEconomy.Account.BADGE));
        assertTrue(EmberEconomy.spendFestCoin("C18", EmberEconomy.amount("C18", "charm_event")));
        assertFalse("source", EmberEconomy.spendFestCoin("S26", 1));
        assertFalse("zero", EmberEconomy.spendFestCoin("C18", 0));

        PlayerData d = new PlayerData();
        d.setCoin(20000);
        assertTrue(EmberEconomy.spendCoin(d, "C18", EmberEconomy.amount("C18", "after_coin")));
        assertEquals(5000, d.getCoin());
        assertFalse("short", EmberEconomy.spendCoin(d, "C18", 15000));
        assertEquals(5000, d.getCoin());

        d.addPeriodCount(EmberEconomy.BADGE_COUNTER, "all", 350);
        assertEquals(EmberSeason.C_BADGE, EmberEconomy.BADGE_COUNTER);
        assertTrue(EmberEconomy.spendBadge(d, "C18", EmberEconomy.amount("C18", "after_badge")));
        assertEquals(50, d.periodCount(EmberEconomy.BADGE_COUNTER, "all"));
        assertFalse("short badge", EmberEconomy.spendBadge(d, "C18", 300));
        assertFalse("C14 takes no badge", EmberEconomy.spendBadge(d, "C14", 1));
        assertFalse("source S27", EmberEconomy.spendBadge(d, "S27", 1));
        assertEquals(50, d.periodCount(EmberEconomy.BADGE_COUNTER, "all"));
    }

    /** D221 / ARCH S2-5: S22 AFK pays coin/xp/bound mats; grantMat accepts BOUND_MAT for known mats. */
    @Test
    public void afkS22GrantEntry() {
        assertEquals(2400, EmberEconomy.amount("S22", "daily_kills"));
        assertEquals(1200, EmberEconomy.amount("S22", "offline_max_kills"));
        assertTrue(EmberEconomy.pays("S22", EmberEconomy.Account.COIN));
        assertTrue(EmberEconomy.pays("S22", EmberEconomy.Account.XP));
        assertTrue(EmberEconomy.pays("S22", EmberEconomy.Account.BOUND_MAT));

        PlayerData d = new PlayerData();
        assertTrue(EmberEconomy.grantCoin(d, "S22", 60));
        assertEquals(60, d.getCoin());
        assertTrue(EmberEconomy.grantXp("S22", 10));
        assertTrue(EmberEconomy.grantMat("S22", EmberUpgradeRules.MAT_SHARD, 1));
        assertTrue(EmberEconomy.grantMat("S22", EmberUpgradeRules.MAT_BONE, 1));
        assertTrue(EmberEconomy.grantMat("S22", EmberUpgradeRules.MAT_CORE, 1));
        assertTrue(EmberEconomy.grantMat("S22", EmberUpgradeRules.MAT_BLANK, 1));
        assertFalse("sink", EmberEconomy.grantCoin(d, "C18", 1));
        assertFalse("S02 pays coin only", EmberEconomy.grantMat("S02", EmberUpgradeRules.MAT_SHARD, 1));
    }

    // ------------------------------------------------------------------ D223 / ARCH S2-6 delivery debit + E1 yml

    @Test
    public void deliveryRequestMapsToWorkshopSinks() {
        assertEquals("C03", EmberEconomy.sinkForDeliveryRequest("enh:uid:1"));
        assertEquals("C03", EmberEconomy.sinkForDeliveryRequest("refund:enh:uid:1"));
        assertEquals("C04", EmberEconomy.sinkForDeliveryRequest("upgrade:uid:2"));
        assertEquals("C05", EmberEconomy.sinkForDeliveryRequest("refine:uid:3"));
        assertEquals("C06", EmberEconomy.sinkForDeliveryRequest("quality:uid:4"));
        assertEquals("C11", EmberEconomy.sinkForDeliveryRequest("afx:uid:5:abc"));
        assertEquals("C11", EmberEconomy.sinkForDeliveryRequest("reroll:uid:5"));
        assertEquals("C12", EmberEconomy.sinkForDeliveryRequest("imp:uid:6:xyz"));
        assertEquals(null, EmberEconomy.sinkForDeliveryRequest("undo:uid:1"));
        assertEquals(null, EmberEconomy.sinkForDeliveryRequest("dis:uid:1"));
        assertEquals(null, EmberEconomy.sinkForDeliveryRequest("swap:abc"));
        assertEquals(null, EmberEconomy.sinkForDeliveryRequest(null));
        assertEquals(null, EmberEconomy.sinkForDeliveryRequest(""));
    }

    @Test
    public void spendCoinDeliveryTagsOrFallsBack() {
        PlayerData d = new PlayerData();
        d.setCoin(1000);
        assertTrue(EmberEconomy.spendCoinDelivery(d, "enh:u:1", 40));
        assertEquals(960, d.getCoin());
        assertTrue("mapped refine", EmberEconomy.spendCoinDelivery(d, "refund:refine:u:2", 100));
        assertEquals(860, d.getCoin());
        assertTrue("unmapped still takes", EmberEconomy.spendCoinDelivery(d, "undo:u:3", 10));
        assertEquals(850, d.getCoin());
        assertFalse("short", EmberEconomy.spendCoinDelivery(d, "enh:u:4", 9000));
        assertEquals(850, d.getCoin());
        assertFalse("zero", EmberEconomy.spendCoinDelivery(d, "enh:u:5", 0));
        assertFalse("null data", EmberEconomy.spendCoinDelivery(null, "enh:u:6", 1));
    }

    /** E1/D224: shipped yml mirrors every golden; load installs SoT; amount() == golden. */
    @Test
    public void economyYmlMirrorsGolden() throws Exception {
        assertEquals(new ArrayList<String>(), EmberEconomy.economyYmlDrift(null));
        assertEquals(new ArrayList<String>(), EmberEconomy.economyYmlDrift(new java.util.LinkedHashMap<String, Object>()));
        Map<String, Object> y = yml(EmberEconomy.ECONOMY_YML);
        assertEquals(EmberEconomy.ECONOMY_BV, ((Number) y.get("balance_version")).intValue());
        assertEquals("yml drifted from EmberEconomy.golden", new ArrayList<String>(), EmberEconomy.economyYmlDrift(y));
        // synthetic drift is caught
        @SuppressWarnings("unchecked")
        Map<String, Object> sinks = (Map<String, Object>) y.get("sinks");
        @SuppressWarnings("unchecked")
        Map<String, Object> c14 = (Map<String, Object>) sinks.get("C14");
        c14.put("coin", 11);
        List<String> drift = EmberEconomy.economyYmlDrift(y);
        assertFalse(drift.isEmpty());
        assertTrue(drift.toString(), drift.get(0).contains("C14.coin"));
    }

    /** D224: loadEconomyYml installs SoT; every golden key readable via amount() equals Java golden. */
    @Test
    public void amountReadsLoadedYmlMatchingGolden() throws Exception {
        EmberEconomy.resetEconomyYmlForTest();
        Map<String, Object> y = yml(EmberEconomy.ECONOMY_YML);
        assertEquals(new ArrayList<String>(), EmberEconomy.loadEconomyYml(y));
        assertTrue(EmberEconomy.economyYmlReady());
        for (EmberEconomy.Row r : EmberEconomy.all()) {
            for (Map.Entry<String, Double> g : r.golden.entrySet()) {
                // amount() is int-rounded; skip non-integral goldens (e.g. stamp_rate 0.12)
                if (Math.abs(g.getValue() - Math.rint(g.getValue())) > EPS) {
                    assertEquals(r.id + "." + g.getKey(), g.getValue(), EmberEconomy.ymlAmount(r.id, g.getKey()), EPS);
                    continue;
                }
                assertEquals(r.id + "." + g.getKey(), (int) Math.round(g.getValue()), EmberEconomy.amount(r.id, g.getKey()));
            }
        }
    }

    /** D224: missing / drifted yml → fail-closed (not ready; grantCoin refuses; amount throws). */
    @Test
    public void amountFailClosedWhenYmlMissingOrCorrupt() throws Exception {
        EmberEconomy.resetEconomyYmlForTest();
        assertFalse(EmberEconomy.loadEconomyYml(null).isEmpty());
        assertFalse(EmberEconomy.economyYmlReady());
        PlayerData d = new PlayerData();
        assertFalse("grant refused when yml missing", EmberEconomy.grantCoin(d, "S01", 300));
        assertEquals(0, d.getCoin());
        boolean threw = false;
        try { EmberEconomy.amount("S01", "coin"); } catch (IllegalStateException e) { threw = true; }
        assertTrue("amount throws when not ready", threw);

        EmberEconomy.resetEconomyYmlForTest();
        Map<String, Object> y = yml(EmberEconomy.ECONOMY_YML);
        @SuppressWarnings("unchecked")
        Map<String, Object> sinks = (Map<String, Object>) y.get("sinks");
        @SuppressWarnings("unchecked")
        Map<String, Object> c14 = (Map<String, Object>) sinks.get("C14");
        c14.put("coin", 11);
        assertFalse(EmberEconomy.loadEconomyYml(y).isEmpty());
        assertFalse(EmberEconomy.economyYmlReady());
        assertFalse(EmberEconomy.grantCoin(d, "S01", 300));
    }

    // ------------------------------------------------------------------ D228 / ARCH S2-8 insignia / badge / weekly marks

    /** D228: every insignia / weekly-mark ledger key resolves to its REG row (key alone or run id map part). */
    @Test
    public void s28LedgerKeysResolveToRegSources() {
        assertEquals("S07", EmberEconomy.sourceForGrantKey("fc_sigmark"));
        assertEquals("S06 pack keys unchanged", "S06", EmberEconomy.sourceForGrantKey("fc_q05_coin"));
        assertEquals("S08", EmberEconomy.sourceForGrantKey("sig_mark"));
        assertEquals("S09", EmberEconomy.sourceForGrantKey("pledge_sigmark"));
        assertEquals("S12", EmberEconomy.sourceForGrantKey("raid_mark"));
        assertEquals("S10", EmberEconomy.sourceForGrant("rot_mark", "q04c-mabc12-x1z"));
        assertEquals("S11", EmberEconomy.sourceForGrant("rot_mark", "q04-mabc12-x1z"));
        assertEquals(null, EmberEconomy.sourceForGrant("rot_mark", "r01-mabc12-x1z"));
        assertEquals("S16", EmberEconomy.sourceForGrant("rush_mark", "rush-mabc12-x1z"));
        assertEquals("S17", EmberEconomy.sourceForGrant("rush_mark", "outpost-mabc12-x1z"));
        assertEquals(null, EmberEconomy.sourceForGrant("rush_mark", "echo_q05-mabc12-x1z"));
        assertEquals("S17", EmberEconomy.sourceForGrant("rush_sig_q02", "outpost-mabc12-x1z"));
        assertEquals("S18", EmberEconomy.sourceForGrant("rush_sig_q07", "echo_q07-mabc12-x1z"));
        assertEquals(null, EmberEconomy.sourceForGrant("rush_sig_q07", "rush-mabc12-x1z"));
        assertEquals(null, EmberEconomy.sourceForGrant("rush_mark", null));
        assertEquals("S23 sign sigmark row", "S23", EmberEconomy.sourceForGrant("n7s", "p1sign-2026-10"));
        // each resolved row pays the account the ledger kind writes
        for (String[] c : new String[][]{{"fc_sigmark", ""}, {"sig_mark", ""}, {"pledge_sigmark", ""},
                {"rush_sig_q01", "outpost-a-b"}, {"rush_sig_q05", "echo_q05-a-b"}, {"n7s", "p1sign-2026-10"}})
            assertTrue(c[0], EmberEconomy.pays(EmberEconomy.sourceForGrant(c[0], c[1]), EmberEconomy.Account.INSIGNIA));
        for (String[] c : new String[][]{{"raid_mark", ""}, {"rot_mark", "q01c-a-b"}, {"rot_mark", "q01-a-b"},
                {"rush_mark", "rush-a-b"}, {"rush_mark", "outpost-a-b"}, {"base_mark", ""}})
            assertTrue(c[0] + " " + c[1], EmberEconomy.pays(EmberEconomy.sourceForGrant(c[0], c[1]), EmberEconomy.Account.MARK));
    }

    /** D228: literal MARK / SIGMARK ledger keys in p1/ are all known to the router — a new key fails until it is mapped. */
    @Test
    public void everyMarkAndInsigniaLedgerKeyIsKnown() throws Exception {
        Set<String> known = new HashSet<String>(Arrays.asList("base_mark", "raid_mark", "rot_mark", "rush_mark",
                "rush_practice", "rush_paid", // amount-0 DELIVERED markers (never credited)
                "sig_mark", "pledge_sigmark", "fc_sigmark", "rush_sig_"));
        java.util.regex.Pattern pat = java.util.regex.Pattern.compile(
                "new (?:EmberRunRules\\.)?Grant\\(\"([a-z_]+)\"(?: \\+ [a-zA-Z]+)?, (?:EmberRunRules\\.)?Kind\\.(MARK|SIGMARK),");
        Path root = Paths.get("src/main/java/town/sunshine/corerpg/p1");
        Set<String> seen = new HashSet<String>();
        List<String> unknown = new ArrayList<String>();
        try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
            for (Path f : walk.filter(x -> x.toString().endsWith(".java")).collect(java.util.stream.Collectors.toList())) {
                java.util.regex.Matcher m = pat.matcher(new String(Files.readAllBytes(f), StandardCharsets.UTF_8));
                while (m.find()) { seen.add(m.group(1)); if (!known.contains(m.group(1))) unknown.add(f.getFileName() + ":" + m.group(1)); }
            }
        }
        assertEquals("map the new ledger key in EmberEconomy.sourceForGrant (D228)", new ArrayList<String>(), unknown);
        assertTrue("scan still sees the keys " + seen, seen.containsAll(Arrays.asList("base_mark", "raid_mark", "rot_mark",
                "rush_mark", "sig_mark", "pledge_sigmark", "fc_sigmark", "rush_sig_")));
    }

    @Test
    public void grantInsigniaAndBadge() {
        PlayerData d = new PlayerData();
        for (String src : new String[]{"S07", "S08", "S09", "S17", "S18", "S23"})
            assertTrue(src, EmberEconomy.grantInsignia(d, src, "q05", 1));
        assertEquals(6, d.periodCount(EmberEconomy.INSIGNIA_COUNTER + "q05", "all"));
        assertFalse("S16 pays no insignia", EmberEconomy.grantInsignia(d, "S16", "q05", 1));
        assertFalse("sink", EmberEconomy.grantInsignia(d, "C12", "q05", 1));
        assertFalse("legacy", EmberEconomy.grantInsignia(d, "LS1", "q05", 1));
        assertFalse("no map", EmberEconomy.grantInsignia(d, "S08", "", 1));
        assertFalse("zero", EmberEconomy.grantInsignia(d, "S08", "q05", 0));
        assertFalse("null data", EmberEconomy.grantInsignia(null, "S08", "q05", 1));
        assertEquals(6, d.periodCount(EmberEconomy.INSIGNIA_COUNTER + "q05", "all"));

        for (String src : new String[]{"S16", "S19", "S27"}) assertTrue(src, EmberEconomy.grantBadge(d, src, 5));
        assertEquals(15, d.periodCount(EmberEconomy.BADGE_COUNTER, "all"));
        assertFalse("S17 pays no badge", EmberEconomy.grantBadge(d, "S17", 1));
        assertFalse("sink C18", EmberEconomy.grantBadge(d, "C18", 1));
        assertFalse("negative", EmberEconomy.grantBadge(d, "S19", -3));
        assertEquals(15, d.periodCount(EmberEconomy.BADGE_COUNTER, "all"));
        assertEquals(EmberSeason.C_BADGE, EmberEconomy.BADGE_COUNTER);
    }

    @Test
    public void creditLedgerTaggedUntaggedRefused() {
        PlayerData d = new PlayerData();
        assertEquals(EmberEconomy.Credit.TAGGED, EmberEconomy.creditMarkLedger(d, "rush_mark", "rush-a-b", 3, 1));
        assertEquals(EmberEconomy.Credit.TAGGED, EmberEconomy.creditMarkLedger(d, "rot_mark", "q02c-a-b", 1, 1));
        assertEquals("pre-S2 / unknown key keeps the credit", EmberEconomy.Credit.UNTAGGED,
                EmberEconomy.creditMarkLedger(d, "mystery_mark", "q02-a-b", 1, 2));
        assertEquals(EmberEconomy.Credit.REFUSED, EmberEconomy.creditMarkLedger(d, "rush_mark", "rush-a-b", 4, 1));
        assertEquals(EmberEconomy.Credit.REFUSED, EmberEconomy.creditMarkLedger(d, "rush_mark", "rush-a-b", 3, 0));
        assertEquals(1, d.periodCount(EmberEconomy.MARK_COUNTER + 3, "all"));
        assertEquals(3, d.periodCount(EmberEconomy.MARK_COUNTER + 1, "all"));

        assertEquals(EmberEconomy.Credit.TAGGED, EmberEconomy.creditInsigniaLedger(d, "rush_sig_q06", "echo_q06-a-b", "q06", 2));
        assertEquals(EmberEconomy.Credit.TAGGED, EmberEconomy.creditInsigniaLedger(d, "fc_sigmark", "q06-a-b", "q06", 3));
        assertEquals(EmberEconomy.Credit.UNTAGGED, EmberEconomy.creditInsigniaLedger(d, "old_sig", "x", "q06", 1));
        assertEquals("S06 fc pack key pays no insignia", EmberEconomy.Credit.REFUSED,
                EmberEconomy.creditInsigniaLedger(d, "fc_q06_coin", "q06-a-b", "q06", 1));
        assertEquals(EmberEconomy.Credit.REFUSED, EmberEconomy.creditInsigniaLedger(d, "sig_mark", "q06-a-b", "", 1));
        assertEquals(6, d.periodCount(EmberEconomy.INSIGNIA_COUNTER + "q06", "all"));
    }

    /** D228: each shipped rush-hall mode resolves to a REG row that pays every reward it configures (marks / badges / sigmarks). */
    @Test
    @SuppressWarnings("unchecked")
    public void rushModesPayTheirConfiguredRewards() throws Exception {
        Map<String, Object> rush = (Map<String, Object>) at(yml("ember-v1-runs.yml"), "rush");
        int n = 0;
        for (Map.Entry<String, Object> e : rush.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            Map<String, Object> h = (Map<String, Object>) e.getValue();
            if (h.get("chain") == null) continue;
            String mode = h.get("mode") == null ? "rush" : String.valueOf(h.get("mode")).toLowerCase(java.util.Locale.ROOT);
            String src = EmberEconomy.sourceForRushMode(mode);
            assertNotNull(e.getKey() + " mode " + mode, src);
            Map<String, Object> rw = h.get("reward") instanceof Map ? (Map<String, Object>) h.get("reward") : new java.util.HashMap<String, Object>();
            if (rw.get("marks") != null && ((Number) rw.get("marks")).intValue() > 0) {
                assertTrue(e.getKey() + " marks", EmberEconomy.pays(src, EmberEconomy.Account.MARK));
                assertEquals(e.getKey() + " rush_mark resolves to the mode row", src, EmberEconomy.sourceForGrant("rush_mark", e.getKey() + "-a-b"));
            }
            if (rw.get("badges") != null && ((Number) rw.get("badges")).intValue() > 0)
                assertTrue(e.getKey() + " badges", EmberEconomy.pays(src, EmberEconomy.Account.BADGE));
            if (rw.get("sigmarks") != null && ((Number) rw.get("sigmarks")).intValue() > 0) {
                assertTrue(e.getKey() + " sigmarks", EmberEconomy.pays(src, EmberEconomy.Account.INSIGNIA));
                assertEquals(e.getKey() + " rush_sig resolves to the mode row", src, EmberEconomy.sourceForGrant("rush_sig_q01", e.getKey() + "-a-b"));
            }
            n++;
        }
        assertEquals("rush + outpost + echo_q01..q07", 9, n);
        assertEquals(null, EmberEconomy.sourceForRushMode("nope"));
    }

    /**
     * D228 (REG §6.4 second cut): no direct write of the mark / insignia / badge account counters and no
     * {@code grantFlatEmberXp} in {@code p1/} outside EmberEconomy unless the line carries an {@code econ-ok:} reason
     * (admin hook, spend rollback, C15 paused spend, fee release). Line-level: a counter held in a local variable
     * (EmberPay untagged undo, EmberDelivery durable apply) is a reviewed exception, not caught here.
     */
    @Test
    public void p1AccountCounterWritesAreEconomyOrTagged() throws Exception {
        java.util.regex.Pattern write = java.util.regex.Pattern.compile(
                "addPeriodCount\\([^;]*(C_MARK|MARK_COUNTER|INSIGNIA_COUNTER|C_BADGE|BADGE_COUNTER|\"p1_mark_t|\"p1_sigmark_|\"p3_badge)");
        Path root = Paths.get("src/main/java/town/sunshine/corerpg/p1");
        List<String> hits = new ArrayList<String>();
        int tagged = 0;
        try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
            for (Path f : walk.filter(x -> x.toString().endsWith(".java")).collect(java.util.stream.Collectors.toList())) {
                String name = f.getFileName().toString();
                if ("EmberEconomy.java".equals(name)) continue;
                List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    String ln = lines.get(i);
                    if (!write.matcher(ln).find() && !ln.contains(".grantFlatEmberXp(")) continue;
                    // EmberSignature.C_MARK / EmberRunService.C_MARK are account counters; other C_MARK-like names are not used in p1/
                    if (ln.contains("econ-ok:")) { tagged++; continue; }
                    hits.add(name + ":" + (i + 1));
                }
            }
        }
        assertEquals("route through EmberEconomy.grantMark/grantInsignia/grantBadge (or tag the line `// econ-ok: <reason>`)",
                new ArrayList<String>(), hits);
        assertTrue("scan sees the tagged exceptions (" + tagged + ")", tagged >= 8);
    }

    // ------------------------------------------------------------------ D229 / ARCH S2-9 — S13 abyss + vault scan

    @Test
    public void abyssFloorSettleTagsAsS13NotS01() {
        assertTrue(EmberEconomy.isAbyssHead("q01a1"));
        assertTrue(EmberEconomy.isAbyssHead("q07a10"));
        assertFalse(EmberEconomy.isAbyssHead("q01"));
        assertFalse(EmberEconomy.isAbyssHead("q01c"));
        assertFalse(EmberEconomy.isAbyssHead("outpost"));
        assertFalse(EmberEconomy.isAbyssHead("echo_q01"));
        assertFalse(EmberEconomy.isAbyssHead(null));

        // key alone still names S01 (no run context); run id with abyss head remaps to S13
        assertEquals("S01", EmberEconomy.sourceForGrantKey("base_coin"));
        assertEquals("S13", EmberEconomy.sourceForGrant("base_coin", "q01a5-mabc12-x1z"));
        assertEquals("S13", EmberEconomy.sourceForGrant("base_shard", "q07a10-aaa-bbb"));
        assertEquals("S13", EmberEconomy.sourceForGrant("base_mark", "q03a1-ts-rnd"));
        assertEquals("S13", EmberEconomy.sourceForGrant("base_xp", "q02a2-ts-rnd"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", "q01-mabc12-x1z"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", "q01c-mabc12-x1z"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", null));
        // non-base keys are unaffected by abyss head
        assertEquals("S02", EmberEconomy.sourceForGrant("extra_treasure_coin", "q01a5-mabc12-x1z"));

        for (EmberEconomy.Account a : new EmberEconomy.Account[]{
                EmberEconomy.Account.COIN, EmberEconomy.Account.SHARD, EmberEconomy.Account.BONE,
                EmberEconomy.Account.CORE, EmberEconomy.Account.XP, EmberEconomy.Account.MARK, EmberEconomy.Account.GEAR}) {
            assertTrue("S13 pays " + a, EmberEconomy.pays("S13", a));
            assertTrue("S01 still pays " + a, EmberEconomy.pays("S01", a));
        }
        // amounts unchanged: settle still reads S01 goldens; S13 is a tag only
        assertEquals(EmberEconomy.amount("S01", "coin"), EmberRunRules.BASE_COIN);
        PlayerData d = new PlayerData();
        assertTrue(EmberEconomy.grantCoin(d, "S13", EmberEconomy.amount("S01", "coin")));
        assertEquals(EmberRunRules.BASE_COIN, d.getCoin());
        assertTrue(EmberEconomy.grantMat("S13", EmberUpgradeRules.MAT_SHARD, 24));
        assertTrue(EmberEconomy.grantMark(d, "S13", 3, 1));
    }

    /**
     * D229 (REG §6.4 vault cut): every warehouse credit in {@code p1/} outside {@code EmberVault} must carry an
     * {@code econ-ok:} reason (grantMat already validated, durable delivery apply, S29 dismantle legacy path).
     * Pickup / internal {@code add} live inside EmberVault and are not scanned here.
     */
    @Test
    public void p1VaultWritesAreEconomyOrTagged() throws Exception {
        java.util.regex.Pattern write = java.util.regex.Pattern.compile(
                "\\.(autoDeposit|creditBound)\\(|EmberVault\\.get\\(\\)\\.give\\(|\\.credit\\(p,");
        Path root = Paths.get("src/main/java/town/sunshine/corerpg/p1");
        List<String> hits = new ArrayList<String>();
        int tagged = 0;
        try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
            for (Path f : walk.filter(x -> x.toString().endsWith(".java")).collect(java.util.stream.Collectors.toList())) {
                String name = f.getFileName().toString();
                if ("EmberVault.java".equals(name) || "EmberEconomy.java".equals(name)) continue;
                List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    String ln = lines.get(i);
                    if (!write.matcher(ln).find()) continue;
                    if (ln.contains("econ-ok:")) { tagged++; continue; }
                    hits.add(name + ":" + (i + 1));
                }
            }
        }
        assertEquals("route vault credits through grantMat first (or tag the line `// econ-ok: <reason>`)",
                new ArrayList<String>(), hits);
        assertTrue("scan sees the tagged vault exceptions (" + tagged + ")", tagged >= 4);
    }


    /** D244 (G10): item-level rows outside CoreRpg — registered (tags only, no goldens), never a grant route */
    @Test
    public void d244ItemLevelRowsAreTagsOnly() {
        for (String id : new String[]{"S36", "S37", "S38", "C19"}) {
            EmberEconomy.Row r = EmberEconomy.byId(id);
            assertNotNull(id, r);
            assertTrue(id + " has no goldens", r.golden.isEmpty());
            assertTrue(id + " no counters", r.counters.isEmpty() && r.ledgers.isEmpty());
        }
        assertTrue(EmberEconomy.pays("S36", EmberEconomy.Account.LIFE_ITEM));
        assertTrue(EmberEconomy.pays("S37", EmberEconomy.Account.GACHA_TICKET));
        assertTrue(EmberEconomy.pays("S38", EmberEconomy.Account.COSMETIC));
        assertTrue(EmberEconomy.byId("C19").sink);
        for (String k : new String[]{"fish", "gacha", "ticket", "pull", "welcome"})
            for (String run : new String[]{null, "gacha", "fish", "life"})
                org.junit.Assert.assertNull(k + "@" + run, EmberEconomy.sourceForGrant(k, run));
    }
}
