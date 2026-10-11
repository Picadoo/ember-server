package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberTicketConvertBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberTicketConvertBoost.consumeBonus(d));
        assertEquals(0, EmberTicketConvertBoost.consumeBonus(d));
    }
}
