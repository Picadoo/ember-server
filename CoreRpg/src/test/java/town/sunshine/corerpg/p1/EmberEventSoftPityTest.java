package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

public class EmberEventSoftPityTest {

    @Test
    public void dry_increment_and_reset() {
        PlayerData pd = new PlayerData();
        assertEquals(0, EmberEventSoftPity.get(pd));
        EmberEventSoftPity.onSettle(pd, false, false);
        assertEquals(0, EmberEventSoftPity.get(pd)); // not eligible
        EmberEventSoftPity.onSettle(pd, true, false);
        assertEquals(1, EmberEventSoftPity.get(pd));
        EmberEventSoftPity.onSettle(pd, true, false);
        assertEquals(1, EmberEventSoftPity.get(pd)); // capped at NEED
        EmberEventSoftPity.onSettle(pd, true, true);
        assertEquals(0, EmberEventSoftPity.get(pd));
    }

    @Test
    public void ensureEvent_forces_when_empty() {
        String[] v = new String[]{"r1", "blazing", "", ""};
        List<String> events = Arrays.asList("crystal", "hold", "timed");
        assertTrue(EmberEventSoftPity.ensureEvent(v, events, 42L));
        assertFalse(v[2].isEmpty());
        assertFalse(v[3].isEmpty());
        assertTrue(Arrays.asList("r1", "r2", "r3").contains(v[2]));
        assertTrue(events.contains(v[3]));
    }

    @Test
    public void ensureEvent_no_double() {
        String[] v = new String[]{"r1", "blazing", "r2", "escort"};
        List<String> events = Arrays.asList("crystal", "hold");
        assertFalse(EmberEventSoftPity.ensureEvent(v, events, 99L));
        assertEquals("r2", v[2]);
        assertEquals("escort", v[3]);
    }

    @Test
    public void ensureEvent_empty_pool_noop() {
        String[] v = new String[]{"", "", "", ""};
        assertFalse(EmberEventSoftPity.ensureEvent(v, Collections.<String>emptyList(), 1L));
        assertEquals("", v[2]);
    }

    @Test
    public void hope_line() {
        PlayerData pd = new PlayerData();
        String h0 = EmberEventSoftPity.hope(pd);
        assertTrue(h0.contains("事件保底"));
        assertTrue(h0.contains("/1"));
        pd.addPeriodCount(EmberEventSoftPity.C_DRY, "all", 1);
        assertTrue(EmberEventSoftPity.hope(pd).contains("必有房间事件"));
    }

    @Test
    public void ensureEvent_deterministic_from_seed() {
        List<String> events = Arrays.asList("timed", "crystal", "hold");
        String[] a = new String[]{"", "", "", ""};
        String[] b = new String[]{"", "", "", ""};
        assertTrue(EmberEventSoftPity.ensureEvent(a, events, 12345L));
        assertTrue(EmberEventSoftPity.ensureEvent(b, events, 12345L));
        assertEquals(a[2], b[2]);
        assertEquals(a[3], b[3]);
    }
}
