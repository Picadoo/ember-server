package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStaminaFirstSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(25, EmberStaminaFirstSoft.consumeCost(d, 30));
        assertEquals(30, EmberStaminaFirstSoft.consumeCost(d, 30));
    }
}
