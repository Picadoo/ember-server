package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberGemSeatLiveTest {
    @Test
    public void effectLinesExist() {
        assertNotNull(EmberGemSeatLive.effectLine(EmberGems.CODE_STEADY));
        assertNotNull(EmberGemSeatLive.effectLine(EmberGems.CODE_DRAIN));
        assertNotNull(EmberGemSeatLive.effectLine(EmberGems.CODE_GALE));
        assertNotNull(EmberGemSeatLive.effectLine(EmberGems.CODE_SHARP));
        assertNull(EmberGemSeatLive.effectLine(0));
    }

    @Test
    public void steadyActiveWithoutTierGate() {
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 5, true, "drop");
        blade = blade.withSocketsNextRev(EmberGems.CODE_STEADY, 0);
        assertTrue(EmberGemSeatLive.combatActive(EmberGems.CODE_STEADY, blade, null));
        assertFalse(EmberGemSeatLive.combatActive(EmberGems.CODE_SHARP, blade, null)); // T1 no sharp A
    }

    @Test
    public void sharpNeedsT2() {
        EmberItemData t1 = EmberItemData.create("scorch", "blade", 1, 1, 0, 5, true, "drop");
        t1 = t1.withSocketsNextRev(EmberGems.CODE_SHARP, 0);
        EmberItemData t2 = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        t2 = t2.withSocketsNextRev(EmberGems.CODE_SHARP, 0);
        assertFalse(EmberGemSeatLive.combatActive(EmberGems.CODE_SHARP, t1, null));
        assertTrue(EmberGemSeatLive.combatActive(EmberGems.CODE_SHARP, t2, null));
    }

    @Test
    public void drainAndGaleActive() {
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 5, true, "drop");
        EmberItemData drain = blade.withSocketsNextRev(EmberGems.CODE_DRAIN, 0);
        EmberItemData gale = blade.withSocketsNextRev(EmberGems.CODE_GALE, 0);
        assertTrue(EmberGemSeatLive.combatActive(EmberGems.CODE_DRAIN, drain, null));
        assertTrue(EmberGemSeatLive.combatActive(EmberGems.CODE_GALE, gale, null));
    }
}
