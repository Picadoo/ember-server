package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TIP-BJ D13064–D13079: gem shortEffect honesty — format ≤1 line from EmberGems constants.
 * Zero flat raise / soft / hole / Anvil / SF4.
 */
public class EmberGemShortEffectHonestyTest {

    @Test
    public void sharpHasAtkMagnitudeAndTierGateNoTrace() {
        String s = EmberGems.shortEffect(EmberGems.CODE_SHARP);
        assertNotNull(s);
        assertTrue(s.contains(EmberGems.fmtFlat(EmberGems.SHARP_ATK)));
        assertTrue(s.contains("T" + EmberGems.SHARP_MIN_TIER));
        assertTrue(s.contains("锋利"));
        assertTrue(s.contains("攻击"));
        assertFalse(s.contains("微量"));
        assertFalse(s.contains("\n"));
    }

    @Test
    public void steadyDrainGaleTrackConstants() {
        String steady = EmberGems.shortEffect(EmberGems.CODE_STEADY);
        assertNotNull(steady);
        assertTrue(steady.contains("稳固"));
        assertTrue(steady.contains("防御"));
        assertTrue(steady.contains(EmberGems.fmtFlat(EmberGems.STEADY_DEF)));
        assertFalse(steady.contains("微量"));

        String drain = EmberGems.shortEffect(EmberGems.CODE_DRAIN);
        assertNotNull(drain);
        assertTrue(drain.contains("汲取"));
        assertTrue(drain.contains("吸血"));
        assertTrue(drain.contains(EmberGems.fmtPct(EmberGems.DRAIN_LS)));
        assertFalse(drain.contains("微量"));

        String gale = EmberGems.shortEffect(EmberGems.CODE_GALE);
        assertNotNull(gale);
        assertTrue(gale.contains("疾风"));
        assertTrue(gale.contains("移速"));
        assertTrue(gale.contains(EmberGems.fmtPct(EmberGems.GALE_MS)));
        assertFalse(gale.contains("微量"));
    }

    @Test
    public void emptyAndUnknownNull() {
        assertNull(EmberGems.shortEffect(0));
        assertNull(EmberGems.shortEffect(EmberGems.CODE_EMPTY));
        assertNull(EmberGems.shortEffect(99));
        assertNull(EmberGems.shortEffect(-1));
    }

    @Test
    public void effectLineReusesShortEffect() {
        int[] codes = {
                EmberGems.CODE_SHARP,
                EmberGems.CODE_STEADY,
                EmberGems.CODE_DRAIN,
                EmberGems.CODE_GALE,
                0,
                99
        };
        for (int c : codes) {
            assertEquals(EmberGems.shortEffect(c), EmberGemSeatLive.effectLine(c));
        }
    }
}
