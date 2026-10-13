package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * TIP-CD D13309: day-rotating short key sx_today (floorMod epochDay, 19) → sx01…sx19.
 */
public class EmberShortTodayRotTest {

    @Test
    public void todayKeyModBoundaries() {
        assertEquals(19, EmberShortRules.SHORT_KEYS.length);
        assertEquals("sx01", EmberShortRules.todayKey(0L));
        assertEquals("sx19", EmberShortRules.todayKey(18L));
        assertEquals("sx01", EmberShortRules.todayKey(19L));
        assertEquals("sx02", EmberShortRules.todayKey(1L));
        assertEquals("sx01", EmberShortRules.todayKey(38L));
    }

    @Test
    public void todayKeySameDayStable() {
        String a = EmberShortRules.todayKey();
        String b = EmberShortRules.todayKey();
        assertEquals(a, b);
        // must be one of SHORT_KEYS
        boolean found = false;
        for (String k : EmberShortRules.SHORT_KEYS) {
            if (k.equals(a)) { found = true; break; }
        }
        assertEquals(true, found);
    }

    @Test
    public void todayKeyNegativeEpochFloorMod() {
        // Math.floorMod(-1, 19) == 18 → sx19
        assertEquals("sx19", EmberShortRules.todayKey(-1L));
        // Math.floorMod(-19, 19) == 0 → sx01
        assertEquals("sx01", EmberShortRules.todayKey(-19L));
    }
}
