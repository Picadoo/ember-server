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

    @Test public void percentTextKeepsQuarterPoints() { // 10-04: 余烬 1 / 3 档 used to read +0.2% / +0.8%
        assertEquals("+0.25%", EmberGrowth.signedPct(1.0025));
        assertEquals("+0.75%", EmberGrowth.signedPct(1.0075));
        assertEquals("+1%", EmberGrowth.signedPct(1.01));
        assertEquals("-14.5%", EmberGrowth.signedPct(0.855));
        assertEquals("+4%", EmberGrowth.signedPct(1.04));
        assertEquals("-0.5%", EmberGrowth.signedPct(0.995));
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

    @Test public void lockKeepsTheAffixAndAffixNamesNeverCollideWithTalents_D147_D148() {
        EmberAffix.Rules r = EmberAffix.parse(root());
        assertEquals(120, r.lockShardFor(3)); assertEquals(40, r.lockShardFor(1));
        java.util.Random rng = new java.util.Random(11);
        EmberAffix.Def want = r.pool("charm").get(1);
        int pity = 0, worst = 0, run = 0;
        for (int i = 0; i < 3000; i++) {
            EmberAffix.Roll x = EmberAffix.roll(r, "charm", 3, pity, rng, want.id);
            assertEquals("lock keeps the affix", want.id, x.id);
            if (x.tier < 4) run++; else run = 0;
            worst = Math.max(worst, run); pity = x.pityAfter;
        }
        assertTrue("same pity under lock", worst <= r.pity);
        // a lock id from the other slot is ignored (normal roll from the slot pool)
        EmberAffix.Roll other = EmberAffix.roll(r, "blade", 3, 0, rng, want.id);
        assertEquals("blade", r.def(other.id).slot);
        java.util.Set<String> talentNames = new java.util.HashSet<String>();
        for (EmberGrowth.Node n : talents().nodes) { talentNames.add(n.name); assertFalse("fit line " + n.id, n.fit.isEmpty()); }
        for (EmberAffix.Def d : r.defs) {
            assertFalse("affix name " + d.name + " is also a talent", talentNames.contains(d.name));
            assertTrue("affix names end with 纹: " + d.name, d.name.endsWith("纹"));
        }
        for (EmberGrowth.Row row : talents().rows) assertFalse("no internal D-number in " + row.theme, row.theme.matches(".*D\\d+.*"));
    }

    @Test public void t2cIsArmorBreak_D164() {
        // build-diversity P7: 拆分 → 破甲 — shield elites ×1.6, blazing / split elites (clones too) ×0.8. Baseline damage 100.
        EmberGrowth.Talents tal = talents();
        EmberGrowth.Node ab = null;
        for (EmberGrowth.Node n : tal.nodes) if ("t2c".equals(n.id)) ab = n;
        assertNotNull(ab);
        assertEquals("破甲", ab.name);
        assertEquals(1.60, ab.mods.get("dmg_affix_shield"), 1e-9);
        assertEquals(0.80, ab.mods.get("dmg_affix_blazing"), 1e-9);
        assertEquals(0.80, ab.mods.get("dmg_affix_split"), 1e-9);
        assertFalse("no flat dmg_affix on 破甲", ab.mods.containsKey("dmg_affix"));
        assertTrue("row-2 text says 仅主线重打生效", ab.good.contains("仅主线重打生效"));

        EmberGrowth.Mods only = EmberGrowth.Mods.combine(java.util.Arrays.asList(ab.mods));
        assertEquals(160.0, 100 * EmberGrowthService.classMult(only, "affix", "shield"), 1e-9);
        assertEquals(80.0, 100 * EmberGrowthService.classMult(only, "affix", "blazing"), 1e-9);
        assertEquals(80.0, 100 * EmberGrowthService.classMult(only, "affix", "split"), 1e-9);
        assertEquals("clones of a split elite", 80.0, 100 * EmberGrowthService.classMult(only, "split", null), 1e-9);
        assertEquals(100.0, 100 * EmberGrowthService.classMult(only, "mob", null), 1e-9);
        assertEquals(100.0, 100 * EmberGrowthService.classMult(only, "boss", null), 1e-9);
        assertEquals("unknown elite type = neutral", 100.0, 100 * EmberGrowthService.classMult(only, "affix", null), 1e-9);

        EmberAffix.Rules ar = EmberAffix.parse(root());
        EmberAffix.Def splitAff = null, affixAff = null;
        for (EmberAffix.Def d : ar.defs) {
            if ("dmg_split".equals(d.key)) splitAff = d;
            if ("dmg_affix".equals(d.key)) affixAff = d;
        }
        assertNotNull(splitAff); assertNotNull(affixAff);
        // 猎缀纹 4 档 + 破甲 → shield body 1.16 × 1.6 = 185.6; blazing 1.16 × 0.8 = 92.8
        Map<String, Double> aff4 = EmberAffix.parts(ar, new int[][]{{EmberAffix.encode(affixAff, 4), 3}, null});
        EmberGrowth.Mods withAff = EmberGrowth.Mods.combine(java.util.Arrays.asList(ab.mods, aff4));
        assertEquals(185.6, 100 * EmberGrowthService.classMult(withAff, "affix", "shield"), 1e-9);
        assertEquals(92.8, 100 * EmberGrowthService.classMult(withAff, "affix", "blazing"), 1e-9);
        // an old 裂身纹 4 档 still applies on clones (retired from the pool, not removed): 1.20 × 0.8 = 96
        Map<String, Double> split4 = EmberAffix.parts(ar, new int[][]{{EmberAffix.encode(splitAff, 4), 3}, null});
        EmberGrowth.Mods withSplitAff = EmberGrowth.Mods.combine(java.util.Arrays.asList(ab.mods, split4));
        assertEquals(96.0, 100 * EmberGrowthService.classMult(withSplitAff, "split", null), 1e-9);
        // B01 key still works for any node that uses it (none since D164): body only
        Map<String, Double> body = new HashMap<String, Double>(); body.put("dmg_affix_body", 0.8); body.put("dmg_split", 1.5);
        EmberGrowth.Mods b01 = EmberGrowth.Mods.combine(java.util.Arrays.asList(body));
        assertEquals(80.0, 100 * EmberGrowthService.classMult(b01, "affix"), 1e-9);
        assertEquals(150.0, 100 * EmberGrowthService.classMult(b01, "split"), 1e-9);
        // 破缀 (t2a) → body and clones both × 1.30, any type
        EmberGrowth.Node crush = null;
        for (EmberGrowth.Node n : tal.nodes) if ("t2a".equals(n.id)) crush = n;
        assertNotNull(crush);
        EmberGrowth.Mods crushOnly = EmberGrowth.Mods.combine(java.util.Arrays.asList(crush.mods));
        assertEquals(130.0, 100 * EmberGrowthService.classMult(crushOnly, "affix", "shield"), 1e-9);
        assertEquals(130.0, 100 * EmberGrowthService.classMult(crushOnly, "split", null), 1e-9);
        for (EmberGrowth.Node n : tal.nodes) if (n.row == 2) assertTrue(n.id + " good text: 仅主线重打生效", n.good.contains("仅主线重打生效"));
        assertTrue(tal.row(2).theme.contains("仅主线重打生效"));
    }

    @Test public void splitAffixRetiredFromPool_D165() {
        EmberAffix.Rules r = EmberAffix.parse(root());
        EmberAffix.Def gone = r.def("b_split");
        assertNotNull("still decodable (existing items keep it)", gone);
        assertFalse(gone.rollable);
        assertEquals(gone, EmberAffix.decodeDef(r, EmberAffix.encode(gone, 2)));
        assertEquals(2, r.pool("blade").size());
        assertFalse(r.pool("blade").contains(gone));
        assertEquals(java.util.Collections.singletonList(gone), r.retired("blade"));
        assertEquals(3, r.pool("charm").size());
        java.util.Random rng = new java.util.Random(7);
        for (int i = 0; i < 3000; i++) assertNotEquals("never rolled", "b_split", EmberAffix.roll(r, "blade", 3, i % 6, rng).id);
        for (int i = 0; i < 500; i++) assertNotEquals("lock on a retired affix = a normal roll", "b_split", EmberAffix.roll(r, "blade", 3, 0, rng, "b_split").id);
        // D166 scope notes
        assertTrue(r.def("b_affix").text(1).contains("仅主线重打生效"));
        assertTrue(r.def("c_affix").text(1).contains("仅主线重打生效"));
        assertTrue(gone.text(1).contains("仅主线重打生效"));
        assertEquals("no note on the general ones", "", r.def("b_set").note);
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
