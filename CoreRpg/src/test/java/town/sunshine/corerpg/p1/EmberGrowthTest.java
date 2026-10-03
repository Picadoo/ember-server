package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/** D141 talents: the bundled growth file parses; combine rules match tools/p1sim/growth.py; tree / points / respec rules. */
public class EmberGrowthTest {

    private static EmberGrowth.Talents talents() {
        Map<?, ?> root = (Map<?, ?>) new Yaml().load(new InputStreamReader(EmberGrowthTest.class.getResourceAsStream("/" + EmberGrowth.FILE), StandardCharsets.UTF_8));
        return EmberGrowth.parseTalents(root);
    }

    private static Map<String, Double> m(Object... kv) {
        Map<String, Double> m = new HashMap<String, Double>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], ((Number) kv[i + 1]).doubleValue());
        return m;
    }

    @Test public void bundledFileParses() {
        EmberGrowth.Talents t = talents();
        assertNotNull(t);
        assertEquals(6, t.maxPoints());
        assertEquals(3, t.rows.size());
        assertEquals(9, t.nodes.size());
        int pts = 0;
        for (EmberGrowth.Row r : t.rows) { assertEquals(3, t.inRow(r.row).size()); pts += r.points; }
        assertEquals("all three rows = all six points", 6, pts);
        for (EmberGrowth.Node n : t.nodes) {
            assertFalse(n.id, n.mods.isEmpty());
            assertNotNull(n.id, n.good);
            assertNotNull(n.id, n.bad);
            assertTrue(n.id + " every node has a cost", n.bad.length() > 0);
        }
        for (EmberGrowth.Node n : t.inRow(3)) assertNotNull("row 3 is set-gated: " + n.id, n.set);
        assertTrue(t.respecCoin > 0);
    }

    @Test public void combineRules() {
        List<Map<String, Double>> parts = new ArrayList<Map<String, Double>>();
        parts.add(m("dmg_boss", 0.97, "dodge_secs", 3, "dodge_icd", 8, "burst_every", 1, "share_w", 2));
        parts.add(m("dmg_boss", 1.10, "dodge_secs", 4, "dodge_icd", 6, "burst_every", 1, "share_w", 2));
        EmberGrowth.Mods x = EmberGrowth.Mods.combine(parts);
        assertEquals(0.97 * 1.10, x.get("dmg_boss"), 1e-9);
        assertEquals(4, x.get("dodge_secs"), 1e-9);
        assertEquals(6, x.get("dodge_icd"), 1e-9);
        assertEquals(2, x.get("burst_every"), 1e-9);
        assertEquals(3, x.get("share_w"), 1e-9);
        assertEquals(1.0, x.get("taken_tele"), 1e-9);  // absent multiplier = 1
        assertEquals(0.0, x.get("dodge_heal"), 1e-9);  // absent additive = 0
        assertSame(EmberGrowth.Mods.NONE, EmberGrowth.Mods.combine(new ArrayList<Map<String, Double>>()));
        assertEquals(1.0, EmberGrowth.Mods.NONE.get("share_w"), 1e-9);
    }

    @Test public void treeAndPoints() {
        EmberGrowth.Talents t = talents();
        Map<Integer, String> picks = new LinkedHashMap<Integer, String>();
        assertNull(EmberGrowth.canPick(t, picks, "t1a", 1));
        assertNotNull("row 2 needs row 1", EmberGrowth.canPick(t, picks, "t2a", 6));
        assertNotNull("not enough points", EmberGrowth.canPick(t, picks, "t1a", 0));
        picks.put(1, "t1a");
        assertNotNull("one per row", EmberGrowth.canPick(t, picks, "t1b", 6));
        assertNotNull("row 2 = 2 points, 1 left of 2", EmberGrowth.canPick(t, picks, "t2b", 2));
        assertNull(EmberGrowth.canPick(t, picks, "t2b", 3));
        picks.put(2, "t2b");
        assertNotNull("row 3 needs 3 more", EmberGrowth.canPick(t, picks, "t3c", 5));
        assertNull(EmberGrowth.canPick(t, picks, "t3c", 6));
        picks.put(3, "t3c");
        assertEquals(6, EmberGrowth.pointsUsed(t, picks));
        assertEquals("set-gated row 3 drops out with another set", 2, EmberGrowth.talentParts(t, picks, "burst").size());
        assertEquals(3, EmberGrowth.talentParts(t, picks, "sustain").size());
    }

    @Test public void respecFirstFree() {
        EmberGrowth.Talents t = talents();
        assertEquals(0, EmberGrowth.respecCost(t, 0));
        assertEquals(t.respecCoin, EmberGrowth.respecCost(t, 1));
        assertEquals(t.respecCoin, EmberGrowth.respecCost(t, 5));
    }

    @Test public void weightedShare() {
        double[] s = EmberRunDirector.shareSplit(300, new double[]{2, 1, 1});
        assertEquals(150, s[0], 1e-9);
        assertEquals(75, s[1], 1e-9);
        assertArrayEquals(new double[]{100, 100, 100}, EmberRunDirector.shareSplit(300, new double[]{1, 1, 1}), 1e-9);
        assertEquals(Arrays.toString(new double[]{300}), Arrays.toString(EmberRunDirector.shareSplit(300, new double[]{2})));
    }

    @Test public void burnTicksAndTransfer() {
        EmberBurnBook b = new EmberBurnBook();
        b.setTicks(6);
        b.ignite("a", 10, 0);
        assertEquals(6 * EmberSetRules.BURN_INTERVAL_MS, b.get("a").endAt());
        assertTrue(b.transfer("a", "b", 1500, 2000));
        assertNull(b.get("a"));
        assertEquals("at most 2 s move over", 3500, b.get("b").endAt());
        assertFalse("nothing left to move", b.transfer("a", "c", 1500, 2000));
    }

    private static Map<?, ?> root() {
        return (Map<?, ?>) new Yaml().load(new InputStreamReader(EmberGrowthTest.class.getResourceAsStream("/" + EmberGrowth.FILE), StandardCharsets.UTF_8));
    }

    @Test public void honorsCappedAndNoMainMapCombat() {
        EmberGrowth.Honors h = EmberGrowth.parseHonors(root());
        assertNotNull(h);
        assertEquals(7, h.list.size());
        List<String> all = new ArrayList<String>();
        for (EmberGrowth.Honor x : h.list) {
            all.add(x.id);
            for (String k : x.mods.keySet())
                assertTrue(x.id + ": honors only touch economy / abyss keys, got " + k,
                        Arrays.asList("coin", "shard_bonus", "abyss_fee", "abyss_taken", "reroll_coin").contains(k));
        }
        Map<String, Double> m = EmberGrowth.honorParts(h, all);
        assertTrue(m.get("coin") <= 1.04 + 1e-9);
        assertTrue(m.get("shard_bonus") <= 1 + 1e-9);
        assertTrue(m.get("abyss_fee") >= 0.95 - 1e-9);
        assertTrue(m.get("abyss_taken") >= 0.98 - 1e-9);
        assertTrue(m.get("reroll_coin") >= 0.85 - 1e-9);
        // a cap actually bites when the list sums past it
        Map<String, Double> caps = new HashMap<String, Double>(); caps.put("coin", 1.04);
        List<EmberGrowth.Honor> l = new ArrayList<EmberGrowth.Honor>();
        for (int i = 0; i < 5; i++) l.add(new EmberGrowth.Honor("h" + i, "h", "x", "", "", m("coin", 1.02)));
        EmberGrowth.Honors big = new EmberGrowth.Honors(l, caps);
        assertEquals(1.04, EmberGrowth.honorParts(big, Arrays.asList("h0", "h1", "h2", "h3", "h4")).get("coin"), 1e-9);
        assertTrue(EmberGrowth.honorParts(h, new ArrayList<String>()).isEmpty());
    }

    @Test public void honorTestHookMatches() { // Part A 10-04: /corerpg p1 honor test <id|kind|kind:arg|all>
        EmberGrowth.Honors h = EmberGrowth.parseHonors(root());
        assertEquals(java.util.Arrays.asList("h_raid1"), EmberGrowth.honorMatch(h, "h_raid1"));
        assertEquals(java.util.Arrays.asList("h_raid1", "h_raid3"), EmberGrowth.honorMatch(h, "raid_count"));
        assertEquals(java.util.Arrays.asList("h_abyss5"), EmberGrowth.honorMatch(h, "abyss_floor:5"));
        assertEquals(h.list.size(), EmberGrowth.honorMatch(h, "all").size());
        assertTrue(EmberGrowth.honorMatch(h, "nope").isEmpty());
        // all seven together: coin 1.02 × 1.02 = 1.0404 → capped at 1.04; reroll 0.90 × 0.95 = 0.855 (cap 0.85 not reached)
        java.util.Map<String, Double> m = EmberGrowth.honorParts(h, EmberGrowth.honorMatch(h, "all"));
        assertEquals(1.04, m.get("coin"), 1e-9);
        assertEquals(0.855, m.get("reroll_coin"), 1e-9);
        assertEquals(1.0, m.get("shard_bonus"), 1e-9);
    }

    @Test public void affixRulesQualityCapAndPity() {
        EmberAffix.Rules r = EmberAffix.parse(root());
        assertNotNull(r);
        assertEquals(5, r.pity);
        assertEquals(1, r.cap(0)); assertEquals(2, r.cap(1)); assertEquals(3, r.cap(2)); assertEquals(4, r.cap(3));
        assertFalse(r.pool("blade").isEmpty()); assertFalse(r.pool("charm").isEmpty());
        java.util.Set<Integer> codes = new java.util.HashSet<Integer>();
        for (EmberAffix.Def d : r.defs) { assertTrue(d.id, d.code > 0); assertTrue("unique code " + d.id, codes.add(d.code)); assertEquals(4, d.values.length); }
        assertArrayEquals(new double[]{1.0}, EmberAffix.tierOdds(r, 1), 1e-9);
        double[] o2 = EmberAffix.tierOdds(r, 2);
        assertEquals(50.0 / 80, o2[0], 1e-9); assertEquals(30.0 / 80, o2[1], 1e-9);
        java.util.Random rng = new java.util.Random(5);
        for (int q = 0; q <= 3; q++) {
            int pity = 0, worst = 0, run = 0;
            for (int i = 0; i < 4000; i++) {
                EmberAffix.Roll x = EmberAffix.roll(r, i % 2 == 0 ? "blade" : "charm", q, pity, rng);
                assertTrue("never above the quality cap", x.tier <= r.cap(q) && x.tier >= 1);
                if (x.tier < r.cap(q)) run++; else run = 0;
                worst = Math.max(worst, run);
                pity = x.pityAfter;
            }
            assertTrue("pity: at most " + r.pity + " misses in a row, got " + worst, worst <= r.pity);
        }
        EmberAffix.Roll forced = EmberAffix.roll(r, "blade", 3, 5, rng);
        assertTrue(forced.forced); assertEquals(4, forced.tier); assertEquals(0, forced.pityAfter);
        EmberAffix.Def d = r.pool("blade").get(0);
        int enc = EmberAffix.encode(d, 4);
        assertSame(d, EmberAffix.decodeDef(r, enc)); assertEquals(4, EmberAffix.decodeTier(enc));
        // a tier-4 affix on a piece that is now 标准 counts as tier 1 (cap re-applied)
        Map<String, Double> parts = EmberAffix.parts(r, new int[][]{{enc, 0}, null});
        assertEquals(d.values[0], parts.get(d.key), 1e-9);
        assertTrue(EmberAffix.parts(r, new int[][]{{0, 3}}).isEmpty());
    }

    @Test public void affixDuplicateRules() {
        EmberItemData t = EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop");
        EmberItemData ok = EmberItemData.create("scorch", "blade", 2, 0, 0, 0, false, "drop");
        assertNull(EmberAffix.duplicateOk(t, ok, false));
        assertNotNull("own affix = investment", EmberAffix.duplicateOk(t, ok, true));
        assertNotNull("same piece", EmberAffix.duplicateOk(t, t, false));
        assertNotNull("other tier", EmberAffix.duplicateOk(t, EmberItemData.create("burst", "blade", 1, 0, 0, 0, false, "drop"), false));
        assertNotNull("other slot", EmberAffix.duplicateOk(t, EmberItemData.create("burst", "charm", 2, 0, 0, 0, false, "drop"), false));
        assertNotNull("enhanced", EmberAffix.duplicateOk(t, EmberItemData.create("burst", "blade", 2, 0, 0, 3, false, "drop"), false));
        assertNotNull("T0 has no slot", EmberAffix.eligible(EmberItemData.create("burst", "blade", 0, 0, 0, 0, false, "starter")));
    }
}
