package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * D12206 W2b: event room bag weight r2:r1:r3 = 2:1:1; affix rooms stay uniform.
 */
public class EmberEventRoomWeightTest {

    private static EmberRunMaps.Variety miniVariety() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("affix_rate", 1.0);
        m.put("event_rate", 1.0);
        m.put("affixes", Arrays.asList("blazing", "split", "shield"));
        m.put("events", Arrays.asList("timed", "breach", "chain"));
        m.put("affix_shard", 2);
        m.put("event_core", 1);
        return new EmberRunMaps.Variety(m);
    }

    @Test
    public void pickEventRoom_bag_half_mid() {
        int n = 40000;
        int c1 = 0, c2 = 0, c3 = 0;
        Random r = new Random(42);
        for (int i = 0; i < n; i++) {
            String room = EmberRunMaps.Variety.pickEventRoom(r);
            if ("r1".equals(room)) c1++;
            else if ("r2".equals(room)) c2++;
            else if ("r3".equals(room)) c3++;
            else fail(room);
        }
        assertEquals(n, c1 + c2 + c3);
        assertTrue("r1=" + c1, c1 > n * 0.225 && c1 < n * 0.275);
        assertTrue("r2=" + c2, c2 > n * 0.475 && c2 < n * 0.525);
        assertTrue("r3=" + c3, c3 > n * 0.225 && c3 < n * 0.275);
    }

    @Test
    public void roll_event_prefers_mid_affix_uniform() {
        EmberRunMaps.Variety v = miniVariety();
        int n = 20000;
        int[] ev = new int[3];
        int[] af = new int[3];
        int evN = 0, afN = 0;
        for (long seed = 1; seed <= n; seed++) {
            String[] roll = v.roll(seed);
            if (roll[2] != null && !roll[2].isEmpty()) {
                evN++;
                if ("r1".equals(roll[2])) ev[0]++;
                else if ("r2".equals(roll[2])) ev[1]++;
                else if ("r3".equals(roll[2])) ev[2]++;
            }
            if (roll[0] != null && !roll[0].isEmpty()) {
                afN++;
                if ("r1".equals(roll[0])) af[0]++;
                else if ("r2".equals(roll[0])) af[1]++;
                else if ("r3".equals(roll[0])) af[2]++;
            }
        }
        assertEquals(n, evN);
        assertEquals(n, afN);
        double er2 = ev[1] / (double) evN;
        double er1 = ev[0] / (double) evN;
        double er3 = ev[2] / (double) evN;
        assertTrue("ev r2=" + er2, er2 > 0.45 && er2 < 0.55);
        assertTrue("ev r1=" + er1, er1 > 0.20 && er1 < 0.30);
        assertTrue("ev r3=" + er3, er3 > 0.20 && er3 < 0.30);

        double ar1 = af[0] / (double) afN;
        double ar2 = af[1] / (double) afN;
        double ar3 = af[2] / (double) afN;
        assertTrue("af r1=" + ar1, ar1 > 0.29 && ar1 < 0.37);
        assertTrue("af r2=" + ar2, ar2 > 0.29 && ar2 < 0.37);
        assertTrue("af r3=" + ar3, ar3 > 0.29 && ar3 < 0.37);
    }

    @Test
    public void softPity_ensure_uses_mid_weight() {
        int n = 20000;
        int c2 = 0;
        List<String> events = Arrays.asList("timed", "breach", "chain");
        for (long seed = 1; seed <= n; seed++) {
            String[] v = new String[]{"", "", "", ""};
            assertTrue(EmberEventSoftPity.ensureEvent(v, events, seed));
            if ("r2".equals(v[2])) c2++;
        }
        double p = c2 / (double) n;
        assertTrue("pity r2=" + p, p > 0.45 && p < 0.55);
    }

    @Test
    public void bundled_variety_still_rolls() {
        InputStream in = EmberEventRoomWeightTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        EmberRunMaps maps = EmberRunMaps.parse((Map<?, ?>) root);
        String[] roll = maps.variety.roll(7L);
        assertEquals(4, roll.length);
        if (!roll[2].isEmpty()) {
            assertTrue(Arrays.asList("r1", "r2", "r3").contains(roll[2]));
            assertFalse(roll[3].isEmpty());
        }
    }
}
