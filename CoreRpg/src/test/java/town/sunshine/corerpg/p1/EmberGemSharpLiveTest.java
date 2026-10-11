package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberGemSharpLiveTest {
    @Test
    public void crossesWhenT1toT2WithSharp() {
        EmberItemData before = EmberItemData.create("scorch", "blade", 1, 1, 0, 5, true, "drop");
        EmberItemData after = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        after = after.withSocketsNextRev(EmberGems.CODE_SHARP, 0);
        assertTrue(EmberGemSharpLive.hasSharp(after));
        assertTrue(EmberGemSharpLive.crossesSharpFloor(before, after));
    }

    @Test
    public void noCrossWithoutSharp() {
        EmberItemData before = EmberItemData.create("scorch", "blade", 1, 1, 0, 5, true, "drop");
        EmberItemData after = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        assertFalse(EmberGemSharpLive.crossesSharpFloor(before, after));
    }

    @Test
    public void noCrossAlreadyT2() {
        EmberItemData before = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        EmberItemData after = EmberItemData.create("scorch", "blade", 3, 1, 0, 5, true, "drop");
        after = after.withSocketsNextRev(EmberGems.CODE_SHARP, 0);
        assertFalse(EmberGemSharpLive.crossesSharpFloor(before, after));
    }
}
