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
 * D12208 W3: optional variety.weights; timed 0.65 vs others 1.0 → P(timed)≈0.0751.
 */
public class EmberEventKindWeightTest {

    private static final List<String> NINE = Arrays.asList(
            "timed", "crystal", "escort", "hold", "beacon", "relay", "breach", "chain", "unscathed");

    private static EmberRunMaps.Variety weightedVariety() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("affix_rate", 0.0);
        m.put("event_rate", 1.0);
        m.put("affixes", Arrays.asList("blazing"));
        m.put("events", NINE);
        Map<String, Object> w = new HashMap<String, Object>();
        w.put("timed", 0.65);
        m.put("weights", w);
        m.put("affix_shard", 2);
        m.put("event_core", 1);
        return new EmberRunMaps.Variety(m);
    }

    private static EmberRunMaps.Variety uniformVariety() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("affix_rate", 0.0);
        m.put("event_rate", 1.0);
        m.put("affixes", Arrays.asList("blazing"));
        m.put("events", NINE);
        m.put("affix_shard", 2);
        m.put("event_core", 1);
        return new EmberRunMaps.Variety(m);
    }

    @Test
    public void pickEventKind_timed_downweighted() {
        EmberRunMaps.Variety v = weightedVariety();
        assertEquals(0.65, v.eventWeights.get("timed"), 1e-9);
        int n = 80000;
        Map<String, Integer> c = new HashMap<String, Integer>();
        for (String k : NINE) c.put(k, 0);
        Random r = new Random(7);
        for (int i = 0; i < n; i++) {
            String k = v.pickEventKind(r);
            assertTrue(k, NINE.contains(k));
            c.put(k, c.get(k) + 1);
        }
        double pTimed = c.get("timed") / (double) n;
        // 0.65 / 8.65 ≈ 0.07514 → band 0.06–0.09
        assertTrue("P(timed)=" + pTimed, pTimed > 0.06 && pTimed < 0.09);
        for (String k : NINE) {
            if ("timed".equals(k)) continue;
            double p = c.get(k) / (double) n;
            // 1/8.65 ≈ 0.1156 → band 0.10–0.13
            assertTrue(k + "=" + p, p > 0.10 && p < 0.13);
        }
    }

    @Test
    public void missing_weights_uniform() {
        EmberRunMaps.Variety v = uniformVariety();
        assertTrue(v.eventWeights.isEmpty());
        int n = 45000;
        Map<String, Integer> c = new HashMap<String, Integer>();
        for (String k : NINE) c.put(k, 0);
        Random r = new Random(11);
        for (int i = 0; i < n; i++) {
            String k = v.pickEventKind(r);
            c.put(k, c.get(k) + 1);
        }
        for (String k : NINE) {
            double p = c.get(k) / (double) n;
            assertTrue(k + "=" + p, p > 0.09 && p < 0.13); // ~1/9
        }
    }

    @Test
    public void roll_uses_weights() {
        EmberRunMaps.Variety v = weightedVariety();
        int n = 40000;
        int timed = 0, evN = 0;
        for (long seed = 1; seed <= n; seed++) {
            String[] roll = v.roll(seed);
            if (roll[2] != null && !roll[2].isEmpty()) {
                evN++;
                if ("timed".equals(roll[3])) timed++;
            }
        }
        assertEquals(n, evN);
        double p = timed / (double) evN;
        assertTrue("roll timed=" + p, p > 0.06 && p < 0.09);
    }

    @Test
    public void softPity_ensure_uses_weights() {
        EmberRunMaps.Variety v = weightedVariety();
        int n = 40000;
        int timed = 0;
        for (long seed = 1; seed <= n; seed++) {
            String[] slot = new String[]{"", "", "", ""};
            assertTrue(EmberEventSoftPity.ensureEvent(slot, v, seed));
            if ("timed".equals(slot[3])) timed++;
        }
        double p = timed / (double) n;
        assertTrue("pity timed=" + p, p > 0.06 && p < 0.09);
    }

    @Test
    public void unknown_weight_key_ignored() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("event_rate", 1.0);
        m.put("events", Arrays.asList("timed", "breach"));
        Map<String, Object> w = new HashMap<String, Object>();
        w.put("timed", 0.5);
        w.put("nope", 99.0);
        w.put("breach", 0.0); // ≤0 skipped
        m.put("weights", w);
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(m);
        assertEquals(1, v.eventWeights.size());
        assertEquals(0.5, v.eventWeights.get("timed"), 1e-9);
        assertFalse(v.eventWeights.containsKey("breach"));
        assertFalse(v.eventWeights.containsKey("nope"));
    }

    @Test
    public void bundled_yml_has_timed_weight() {
        InputStream in = EmberEventKindWeightTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        EmberRunMaps maps = EmberRunMaps.parse((Map<?, ?>) root);
        assertEquals(9, maps.variety.events.size());
        assertEquals(0.65, maps.variety.eventWeights.get("timed"), 1e-9);
        String[] roll = maps.variety.roll(99L);
        assertEquals(4, roll.length);
    }
}
