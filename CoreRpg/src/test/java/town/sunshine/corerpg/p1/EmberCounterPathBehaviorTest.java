package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberCounterPathBehaviorTest {
    @Test
    public void enterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markCounterEnter(d));
        assertTrue(EmberSkillKit.counterPathUnlocked(d, null));
    }

    @Test
    public void bonusConstants() {
        assertEquals(1, EmberCounterPath.KIND_WALL);
        assertEquals(2, EmberCounterPath.KIND_WHIFF);
        assertEquals(3, EmberCounterPath.KIND_BREAK);
    }
}
