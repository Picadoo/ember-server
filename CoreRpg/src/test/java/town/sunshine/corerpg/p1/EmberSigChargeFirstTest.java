package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSigChargeFirstTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(0.85, EmberSigChargeFirst.consumeMult(d), 1e-9);
        assertEquals(1.0, EmberSigChargeFirst.consumeMult(d), 1e-9);
    }
}
