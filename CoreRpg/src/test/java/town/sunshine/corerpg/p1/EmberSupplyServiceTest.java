package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmberSupplyServiceTest {
    @Test
    public void affordableCapsByCoinsSlotsAndMax() {
        assertEquals(5, EmberSupplyService.affordable(5, 300, 10, 20));
        assertEquals(3, EmberSupplyService.affordable(5, 39, 10, 20));   // coins
        assertEquals(2, EmberSupplyService.affordable(5, 300, 10, 2));   // free slots (potions do not stack)
        assertEquals(EmberSupplyService.MAX_PER_BUY, EmberSupplyService.affordable(99, 10000, 10, 36));
        assertEquals(0, EmberSupplyService.affordable(1, 9, 10, 36));
        assertEquals(0, EmberSupplyService.affordable(0, 100, 10, 36));
    }

    @Test
    public void defaultsWithoutConfigMatchBookAndD28() {
        if (EmberMode.get() == null) {
            assertEquals(10, EmberSupplyService.price());
            assertEquals(5, EmberSupplyService.starterPotions());
        }
    }

    @Test
    public void lowHpHintTellsHowToDrink() {
        assertNull(EmberSupplyService.lowHpHint(50, 100, 7, 0));            // not low
        assertNull(EmberSupplyService.lowHpHint(30, 100, 7, 4000));         // cooldown running
        assertTrue(EmberSupplyService.lowHpHint(30, 100, 7, 0).contains("数字键 8"));
        assertTrue(EmberSupplyService.lowHpHint(30, 100, 9, 0).contains("按 E"));
        assertTrue(EmberSupplyService.lowHpHint(30, 100, -1, 0).contains("没带回复药"));
        assertNull(EmberSupplyService.lowHpHint(0, 100, 7, 0));             // dead
    }
}
