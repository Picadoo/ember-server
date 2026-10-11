package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFcSigMarkBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(3, EmberFcSigMarkBoost.apply(d, 2));
        assertEquals(2, EmberFcSigMarkBoost.apply(d, 2));
    }
}
