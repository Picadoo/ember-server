package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberHonorCoinBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(8, EmberHonorCoinBoost.apply(d, 3));
        assertEquals(3, EmberHonorCoinBoost.apply(d, 3));
        assertEquals(0, EmberHonorCoinBoost.apply(new PlayerData(), 0));
    }
}
