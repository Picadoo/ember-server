package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLifeWeeklyCapSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLifeWeeklyCapSoft.consume(d));
        assertFalse(EmberLifeWeeklyCapSoft.consume(d));
    }
}
