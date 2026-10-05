package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.PlayerData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * D232 / ARCH S3-3: fixed-seed (no RNG) proofs that pledge counter / modifier / S09 grant
 * shapes match the pre-extract behaviour and the bundled {@code ember-v1-runs.yml} (bv58).
 */
public final class EmberPledgeServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberPledgeServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    private static List<String> poolIds(EmberRunMaps maps) {
        List<String> ids = new ArrayList<String>();
        for (EmberRunMaps.Modifier m : maps.pledgePool()) ids.add(m.id);
        return ids;
    }

    @Test public void bundledPledgePoolIsNormalOnly_unchanged() {
        EmberRunMaps maps = bundled();
        List<String> ids = poolIds(maps);
        assertFalse(ids.isEmpty());
        assertTrue("lean is pledgable", ids.contains("lean"));
        assertTrue("reverse is pledgable", ids.contains("reverse"));
        assertFalse("casters is challenge-only", ids.contains("casters"));
        for (EmberRunMaps.Modifier m : maps.pledgePool()) assertTrue(m.normal);
    }

    @Test public void applyToggleAndOff_roundTrip_unchanged() {
        PlayerData pd = new PlayerData();
        assertFalse(EmberPledgeService.isOn(pd, "lean"));
        assertTrue(EmberPledgeService.applyToggle(pd, "lean")); // now on
        assertTrue(EmberPledgeService.isOn(pd, "lean"));
        assertEquals(1, pd.periodCount(EmberPledgeService.C_PLEDGE + "lean", "all"));

        assertFalse(EmberPledgeService.applyToggle(pd, "lean")); // now off
        assertFalse(EmberPledgeService.isOn(pd, "lean"));
        assertEquals(0, pd.periodCount(EmberPledgeService.C_PLEDGE + "lean", "all"));

        EmberPledgeService.applyToggle(pd, "lean");
        EmberPledgeService.applyToggle(pd, "reverse");
        assertEquals(2, EmberPledgeService.applyOff(pd, Arrays.asList("lean", "reverse", "casters")));
        assertFalse(EmberPledgeService.isOn(pd, "lean"));
        assertFalse(EmberPledgeService.isOn(pd, "reverse"));
        assertEquals(0, EmberPledgeService.applyOff(pd, Arrays.asList("lean", "reverse")));
    }

    @Test public void encodeKeyAndCountInModifier_matchMapsPledgeIds() {
        EmberRunMaps maps = bundled();
        List<String> pool = poolIds(maps);

        assertNull(EmberPledgeService.encodeKey(null));
        assertNull(EmberPledgeService.encodeKey(new ArrayList<String>()));
        assertEquals("pledge:lean", EmberPledgeService.encodeKey(Arrays.asList("lean")));
        assertEquals("pledge:lean+reverse", EmberPledgeService.encodeKey(Arrays.asList("lean", "reverse")));

        assertEquals(0, EmberPledgeService.countInModifier("", pool));
        assertEquals(0, EmberPledgeService.countInModifier("lean", pool)); // bare weekly id, not pledge:
        assertEquals(1, EmberPledgeService.countInModifier("pledge:lean", pool));
        assertEquals(2, EmberPledgeService.countInModifier("pledge:lean+reverse", pool));
        assertEquals(2, EmberPledgeService.countInModifier("pledge:lean+reverse+lean", pool)); // dedupe in pledgeIds
        assertEquals(0, EmberPledgeService.countInModifier("pledge:casters", pool)); // not in pool
        assertEquals(1, EmberPledgeService.countInModifier("pledge:lean+casters", pool)); // only lean counts
    }

    @Test public void activeIdsFollowsPoolOrder() {
        EmberRunMaps maps = bundled();
        PlayerData pd = new PlayerData();
        // turn on reverse first, then lean — activeIds must follow pool config order
        EmberPledgeService.applyToggle(pd, "reverse");
        EmberPledgeService.applyToggle(pd, "lean");
        List<String> active = EmberPledgeService.activeIds(pd, maps.pledgePool());
        List<String> expected = new ArrayList<String>();
        for (EmberRunMaps.Modifier m : maps.pledgePool()) {
            if ("lean".equals(m.id) || "reverse".equals(m.id)) expected.add(m.id);
        }
        assertEquals(expected, active);
        assertEquals(EmberPledgeService.encodeKey(active),
                EmberRunMaps.PLEDGE + expected.get(0) + (expected.size() > 1 ? "+" + expected.get(1) : ""));
    }

    @Test public void settleGrantIsS09Shape_oneInsigniaPerRule() {
        assertNull(EmberPledgeService.settleGrant("q01", 0));
        assertNull(EmberPledgeService.settleGrant("q01", -1));
        EmberRunRules.Grant g = EmberPledgeService.settleGrant("q03", 2);
        assertNotNull(g);
        assertEquals("pledge_sigmark", g.key);
        assertEquals(EmberRunRules.Kind.SIGMARK, g.kind);
        assertEquals("q03", g.id);
        assertEquals(2, g.amount);
        assertEquals("S09", EmberEconomy.sourceForGrantKey("pledge_sigmark"));
    }

    @Test public void counterKeyOwnedByPledgeService() {
        EmberCounters.Family f = EmberCounters.byKey("p1_pledge_");
        assertNotNull(f);
        assertEquals("EmberPledgeService", f.system);
        assertTrue(f.prefix);
        assertTrue(f.matches("p1_pledge_lean"));
    }
}
