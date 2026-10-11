package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberForgeCraftFloorTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberForgeCraftFloor.consumeFloor(d, 0));
        assertEquals(0, EmberForgeCraftFloor.consumeFloor(d, 0));
    }
}
