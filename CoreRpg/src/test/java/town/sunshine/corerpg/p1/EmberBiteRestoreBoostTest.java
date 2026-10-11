package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBiteRestoreBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberBiteRestoreBoost.consumeExtra(d));
        assertEquals(0, EmberBiteRestoreBoost.consumeExtra(d));
    }
}
