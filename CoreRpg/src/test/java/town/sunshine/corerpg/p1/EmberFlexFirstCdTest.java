package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFlexFirstCdTest {
    @Test public void softensOnce() {
        PlayerData d = new PlayerData();
        assertEquals(11, EmberFlexFirstCd.consumeSecs(d, 14));
        assertEquals(14, EmberFlexFirstCd.consumeSecs(d, 14));
    }
}
