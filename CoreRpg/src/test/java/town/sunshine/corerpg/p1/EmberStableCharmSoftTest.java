package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStableCharmSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(150, EmberStableCharmSoft.consumeCoin(d, 300));
        assertEquals(300, EmberStableCharmSoft.consumeCoin(d, 300));
    }
}
