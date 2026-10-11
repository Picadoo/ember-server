package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberForgeRollFloorSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberForgeRollFloorSoft.consume(d));
        assertFalse(EmberForgeRollFloorSoft.consume(d));
    }
}
