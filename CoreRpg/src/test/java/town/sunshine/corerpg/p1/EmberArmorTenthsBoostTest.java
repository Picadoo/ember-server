package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberArmorTenthsBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberArmorTenthsBoost.consume(d));
        assertEquals(0, EmberArmorTenthsBoost.consume(d));
    }
}
