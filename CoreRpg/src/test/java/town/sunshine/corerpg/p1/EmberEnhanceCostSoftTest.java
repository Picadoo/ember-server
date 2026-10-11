package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
public class EmberEnhanceCostSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        Cost c = new Cost(10, 2, 4, 0, 100);
        Cost soft = EmberEnhanceCostSoft.apply(d, c, false);
        assertEquals(5, soft.shards);
        assertEquals(1, soft.cores);
        assertEquals(2, soft.blanks);
        assertEquals(50, soft.coins);
        assertTrue(EmberEnhanceCostSoft.ready(d));
        EmberEnhanceCostSoft.apply(d, c, true);
        assertFalse(EmberEnhanceCostSoft.ready(d));
        assertEquals(c, EmberEnhanceCostSoft.apply(d, c, true));
    }
}
