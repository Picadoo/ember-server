package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRerollCoinSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(50, EmberRerollCoinSoft.apply(d, 100, true));
        assertEquals(100, EmberRerollCoinSoft.apply(d, 100, true));
    }
}
