package town.sunshine.corerpg.p1;

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
}
