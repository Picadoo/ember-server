package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberGemHoleSeatTest {
    @Test
    public void bladeHole1UnlocksAtEnhance5() {
        EmberItemData before = EmberItemData.create("scorch", "blade", 2, 1, 0, 4, true, "drop");
        EmberItemData after = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        // same uid needed for realism — recreate with enhance via with? use copy pattern
        // sockUnlocked uses enhance field only
        assertFalse(before.sockUnlocked(1));
        assertTrue(after.sockUnlocked(1));
        assertEquals(1, EmberGemHoleSeat.newEmptyHole(before, after));
    }

    @Test
    public void noUnlockWhenSameEnhance() {
        EmberItemData a = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        assertEquals(-1, EmberGemHoleSeat.newEmptyHole(a, a));
    }

    @Test
    public void charmHoleUnlocksAtEnhance3() {
        EmberItemData before = EmberItemData.create("sustain", "charm", 2, 1, 0, 2, true, "drop");
        EmberItemData after = EmberItemData.create("sustain", "charm", 2, 1, 0, 3, true, "drop");
        assertFalse(before.sockUnlocked(0));
        assertTrue(after.sockUnlocked(0));
        assertEquals(0, EmberGemHoleSeat.newEmptyHole(before, after));
    }

    @Test
    public void noNewHoleWhenAlreadyUnlocked() {
        EmberItemData a = EmberItemData.create("scorch", "blade", 2, 1, 0, 6, true, "drop");
        EmberItemData b = EmberItemData.create("scorch", "blade", 2, 1, 0, 7, true, "drop");
        // both already unlock hole1
        assertTrue(a.sockUnlocked(1));
        assertEquals(-1, EmberGemHoleSeat.newEmptyHole(a, b));
    }
}
