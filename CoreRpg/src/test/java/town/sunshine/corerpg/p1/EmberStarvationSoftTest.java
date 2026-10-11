package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStarvationSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberStarvationSoft.consumeCancel(d));
        assertFalse(EmberStarvationSoft.consumeCancel(d));
    }
}
