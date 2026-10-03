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
}
