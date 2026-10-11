package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberIframeForgiveTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(3.5, EmberIframeForgive.consume(d, 10.0), 1e-9);
        assertEquals(0.0, EmberIframeForgive.consume(d, 10.0), 1e-9);
    }
}
