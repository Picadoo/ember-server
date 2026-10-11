package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDrownSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberDrownSoft.consumeCancel(d));
        assertFalse(EmberDrownSoft.consumeCancel(d));
    }
}
