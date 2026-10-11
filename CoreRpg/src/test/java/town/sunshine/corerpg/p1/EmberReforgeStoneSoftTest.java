package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberReforgeStoneSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(15, EmberReforgeStoneSoft.consumeCoin(d, 30));
        assertEquals(30, EmberReforgeStoneSoft.consumeCoin(d, 30));
    }
}
