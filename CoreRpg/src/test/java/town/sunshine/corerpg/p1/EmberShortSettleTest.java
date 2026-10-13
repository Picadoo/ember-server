package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-CW D13328: short_settle_* remember + resolve fallback. */
public class EmberShortSettleTest {

    private static EmberRunMaps.MapDef map(String key, String name) {
        java.util.Map<String, Object> raw = new HashMap<String, Object>();
        if (name != null) raw.put("name", name);
        EmberRunMaps.MapDef m = new EmberRunMaps.MapDef(key, raw);
        m.shortExpedition = true;
        return m;
    }

    @Test
    public void indexAndKeyRoundTrip() {
        assertEquals(0, EmberShortSettle.indexOf(null));
        assertEquals(0, EmberShortSettle.indexOf("sx20"));
        assertEquals(1, EmberShortSettle.indexOf("sx01"));
        assertEquals(7, EmberShortSettle.indexOf("SX07"));
        assertEquals(19, EmberShortSettle.indexOf("sx19"));
        assertEquals("sx01", EmberShortSettle.keyOf(1));
        assertEquals("sx19", EmberShortSettle.keyOf(19));
        assertEquals("", EmberShortSettle.keyOf(0));
        assertEquals("", EmberShortSettle.keyOf(20));
    }

    @Test
    public void rememberAndLastMap() {
        PlayerData d = new PlayerData();
        assertEquals("", EmberShortSettle.lastMap(d));
        assertTrue(EmberShortSettle.remember(d, "sx07"));
        assertEquals("sx07", EmberShortSettle.lastMap(d));
        assertFalse(EmberShortSettle.remember(d, "sx07"));
        assertTrue(EmberShortSettle.remember(d, "sx03"));
        assertEquals("sx03", EmberShortSettle.lastMap(d));
        assertFalse(EmberShortSettle.remember(d, "q01"));
        assertEquals("sx03", EmberShortSettle.lastMap(d));
    }

    @Test
    public void resolvePrefersRememberedThenPathThenToday() {
        PlayerData d = new PlayerData();
        // no remember → path resolve wins over today
        assertEquals("sx09", EmberShortSettle.resolveMap(d, map("sx09", "X"), "sx05"));
        EmberShortSettle.remember(d, "sx12");
        assertEquals("sx12", EmberShortSettle.resolveMap(d, map("sx09", "X"), "sx05"));
        PlayerData empty = new PlayerData();
        assertEquals("sx09", EmberShortSettle.resolveMap(empty, map("sx09", "X"), "sx05"));
        assertEquals("sx05", EmberShortSettle.resolveMap(empty, null, "sx05"));
        assertEquals("", EmberShortSettle.resolveMap(empty, null, null));
    }

    @Test
    public void displayNameFallsBackToKey() {
        assertEquals("烬塔", EmberShortSettle.displayName(map("sx07", "烬塔"), "sx07"));
        assertEquals("sx07", EmberShortSettle.displayName(map("sx07", ""), "sx07"));
        assertEquals("sx07", EmberShortSettle.displayName(null, "sx07"));
    }
}
