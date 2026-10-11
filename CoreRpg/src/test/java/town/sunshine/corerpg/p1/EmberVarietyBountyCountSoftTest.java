package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberVarietyBountyCountSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberVarietyBountyCountSoft.softCount(d, 3));
        assertTrue(EmberVarietyBountyCountSoft.consume(d));
        assertEquals(2, EmberVarietyBountyCountSoft.softCount(d, 3));
        assertFalse(EmberVarietyBountyCountSoft.consume(d));
    }
}
