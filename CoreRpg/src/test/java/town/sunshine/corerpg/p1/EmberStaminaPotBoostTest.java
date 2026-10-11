package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStaminaPotBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberStaminaPotBoost.consumeBonus(d));
        assertEquals(0, EmberStaminaPotBoost.consumeBonus(d));
    }
}
