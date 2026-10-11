package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberImprintCoinSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(50, EmberImprintCoinSoft.apply(d, 100, true));
        assertEquals(100, EmberImprintCoinSoft.apply(d, 100, true));
    }
}
