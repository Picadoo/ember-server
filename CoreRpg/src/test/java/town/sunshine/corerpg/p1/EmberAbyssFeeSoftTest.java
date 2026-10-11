package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAbyssFeeSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(80, EmberAbyssFeeSoft.consumeFee(d, 100));
        assertEquals(100, EmberAbyssFeeSoft.consumeFee(d, 100));
    }
}
