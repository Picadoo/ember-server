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

/**
 * D213 (ARCH S2-1): {@link EmberEconomy} matches REG-ember-source-sink-cap and the live game — counter families exist,
 * golden amounts equal the constants / shipped plugin yml that really pay them, and the unmodelled set is pinned.
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
}
