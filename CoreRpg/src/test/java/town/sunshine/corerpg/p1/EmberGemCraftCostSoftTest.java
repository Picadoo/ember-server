package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
public class EmberGemCraftCostSoftTest {
    @Test public void peekConsume() {
        PlayerData d = new PlayerData();
        Cost c = new Cost(8, 0, 0, 4, 40);
        Cost soft = EmberGemCraftCostSoft.peek(d, c);
        assertEquals(4, soft.shards);
        assertEquals(2, soft.bone);
        assertEquals(20, soft.coins);
        assertTrue(EmberGemCraftCostSoft.consume(d));
        assertSame(c, EmberGemCraftCostSoft.peek(d, c));
    }
}
