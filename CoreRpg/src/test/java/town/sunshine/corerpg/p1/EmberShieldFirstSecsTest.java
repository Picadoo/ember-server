package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShieldFirstSecsTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1.0, EmberShieldFirstSecs.consume(d), 1e-9);
        assertEquals(0.0, EmberShieldFirstSecs.consume(d), 1e-9);
    }
}
