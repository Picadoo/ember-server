package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

/** Pure band gate for D4241 forgive (Bukkit-free). */
public class EmberAfkGearForgiveTest {
    @Test
    public void stableBandIsEnough() {
        // T1 rec atk18/hp55 — atk18 hp55 → 够稳
        assertEquals("够稳", EmberAfkGearEff.band(18, 55, 1));
        assertNotEquals("够稳", EmberAfkGearEff.band(5, 20, 1));
    }

    @Test
    public void forgiveKeyPrefix() {
        assertEquals("p1_afk_gear_forgive_t", EmberAfkGearFeel.C_FORGIVE);
    }
}
