package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberForgeQualityFloorTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberForgeQualityFloor.consumeFloor(d, 0));
        assertEquals(0, EmberForgeQualityFloor.consumeFloor(d, 0));
    }
}
