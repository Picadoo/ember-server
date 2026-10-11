package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberHuohenRadiusBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(0.35, EmberHuohenRadiusBoost.consume(d), 1e-9);
        assertEquals(0.0, EmberHuohenRadiusBoost.consume(d), 1e-9);
    }
}
