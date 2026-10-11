package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLifeLevelGateSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLifeLevelGateSoft.consume(d));
        assertFalse(EmberLifeLevelGateSoft.consume(d));
    }
}
