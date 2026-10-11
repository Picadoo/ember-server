package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLifeDailyCapSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLifeDailyCapSoft.consume(d));
        assertFalse(EmberLifeDailyCapSoft.consume(d));
    }
}
