package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberConvertCapSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberConvertCapSoft.consume(d));
        assertFalse(EmberConvertCapSoft.consume(d));
    }
}
