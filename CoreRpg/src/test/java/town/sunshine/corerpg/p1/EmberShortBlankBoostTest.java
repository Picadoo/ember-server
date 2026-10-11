package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShortBlankBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberShortBlankBoost.consume(d));
        assertFalse(EmberShortBlankBoost.consume(d));
    }
}
