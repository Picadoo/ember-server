package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** D298 W1b summary formatting + key constants. */
public class EmberPlayfeelTelemetryTest {

    @Test public void summaryLines_D298_rates() {
        Map<String, Integer> c = new LinkedHashMap<String, Integer>();
        c.put(EmberPlayfeelTelemetry.C_RUNS, 10);
        c.put(EmberPlayfeelTelemetry.C_WALL, 5);
        c.put(EmberPlayfeelTelemetry.C_WHIFF, 2);
        c.put(EmberPlayfeelTelemetry.C_BREAK, 1);
        c.put(EmberPlayfeelTelemetry.C_EVT_ROLL, 8);
        c.put(EmberPlayfeelTelemetry.C_EVT_OK, 4);
        c.put(EmberPlayfeelTelemetry.C_SIG_WEAR, 6);
        c.put(EmberPlayfeelTelemetry.C_SIG_ALT, 3);
        c.put(EmberPlayfeelTelemetry.C_VB_HIT, 2);
        List<String> lines = EmberPlayfeelTelemetry.summaryLines("t", c);
        assertEquals(5, lines.size());
        assertTrue(lines.get(0).contains("10"));
        assertTrue(lines.get(1).contains("墙5"));
        assertTrue(lines.get(2).contains("出房8"));
        assertTrue(lines.get(3).contains("调律3"));
    }

    @Test public void weekKey_PWeekShape() {
        String wk = EmberPlayfeelTelemetry.weekKey();
        assertTrue(wk.startsWith("w"));
        assertTrue(wk.length() >= 2);
    }

    @Test public void countersRegistered() {
        assertEquals("EmberPlayfeelTelemetry", EmberCounters.byKey("p1_pf_runs").system);
        assertEquals(EmberCounters.Category.STAT, EmberCounters.byKey("p1_pf_wall").category);
        assertEquals(EmberCounters.Period.PWEEK, EmberCounters.byKey("p1_pf_evt_ok").period);
        assertFalse(EmberCounters.byKey("p1_attuneprompt") == null);
        assertFalse(EmberCounters.byKey("p1_evteach_") == null);
    }
}
