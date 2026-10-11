package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRerollLockShardSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberRerollLockShardSoft.apply(d, 10, true));
        assertEquals(10, EmberRerollLockShardSoft.apply(d, 10, true));
    }
}
