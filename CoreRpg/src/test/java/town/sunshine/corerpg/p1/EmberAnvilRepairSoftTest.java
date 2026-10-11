package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAnvilRepairSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberAnvilRepairSoft.consumeFee(d, 10));
        assertEquals(10, EmberAnvilRepairSoft.consumeFee(d, 10));
    }
}
