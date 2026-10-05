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

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.PlayerData;

/**
 * D213 registry + D215/D216 (ARCH S2-2/S2-3): {@link EmberEconomy} matches REG-ember-source-sink-cap and the live game —
 * counter families exist, golden amounts equal the constants / shipped plugin yml, the unmodelled set is pinned,
 * settle / shop / sign / grantCoin/Mark/Xp/Mat / spendCoin route through the registry without changing amounts,
 * and a scoped {@code p1/} addCoin scan fails on new grant paths that skip the registry. D218 (S2-4): workshop / mark
 * exchange / abyss fee / talent / reroll / imprint / attune spends (C03–C13) route through spend* and a {@code p1/}
 * takeCoin scan guards new direct spends.
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

    @Test
    public void idsAreCompleteAndOrdered() {
        List<String> want = new ArrayList<String>();
        for (int i = 1; i <= 32; i++) want.add(String.format("S%02d", i));
        for (int i = 1; i <= 5; i++) want.add("LS" + i);
        for (int i = 1; i <= 18; i++) want.add(String.format("C%02d", i));
        List<String> got = new ArrayList<String>();
        for (EmberEconomy.Row r : EmberEconomy.all()) got.add(r.id);
        assertEquals(want, got);
        assertEquals(32, EmberEconomy.sources().size());
        assertEquals(5, EmberEconomy.legacySources().size());
        assertEquals(18, EmberEconomy.sinks().size());
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
        for (String h : Arrays.asList("echo_q01", "echo_q02", "echo_q03", "echo_q04")) {
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
        assertEquals(Arrays.asList("S07", "S08", "S09", "S16", "S17", "S18", "S27", "S30", "S31", "S32",
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
        assertEquals(null, EmberEconomy.sourceForGrant("n1c", "other-run"));
        assertEquals("S01", EmberEconomy.sourceForGrant("base_coin", "p1sign-ignored")); // key wins over run

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
                "EmberRunService.java"     // abyss fee release + deliver unrouted fallback
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
     * allowlisted file whose sink is not routed yet (C15 paused, C18 festival, delivery debit, EmberPay untagged undo).
     */
    @Test
    public void p1TakeCoinIsEconomyOrAllowlisted() throws Exception {
        Set<String> allowFiles = new HashSet<String>(Arrays.asList(
                "EmberEconomy.java",   // spendCoin
                "EmberPay.java",       // untagged price (dismantle undo) direct path
                "EmberDelivery.java",  // A01 durable txn debit
                "EmberCosmetics.java", // C15 (paused, OUT of the model)
                "EmberFestival.java"   // C18 festival shop (PART) — next slice
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
}
