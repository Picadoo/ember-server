package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBulkBlankBoostTest {
    @Test public void needAndOnce() {
        PlayerData d = new PlayerData();
        assertFalse(EmberBulkBlankBoost.consume(d, 2));
        assertTrue(EmberBulkBlankBoost.consume(d, 3));
        assertFalse(EmberBulkBlankBoost.consume(d, 5));
    }
}
