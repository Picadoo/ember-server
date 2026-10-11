package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSigMarkBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberSigMarkBoost.apply(d, 1));
        assertEquals(1, EmberSigMarkBoost.apply(d, 1));
    }
}
