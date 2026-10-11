package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapResonanceDayBoostTest {
    @Test public void latchBoost() {
        PlayerData d = new PlayerData();
        assertFalse(EmberMapResonanceDayBoost.activeToday(d));
        assertTrue(EmberMapResonanceDayBoost.maybeLatch(d));
        assertTrue(EmberMapResonanceDayBoost.activeToday(d));
        assertFalse(EmberMapResonanceDayBoost.maybeLatch(d));
        assertTrue(EmberMapResonanceDayBoost.boost(d, true));
        assertFalse(EmberMapResonanceDayBoost.boost(d, false));
    }
}
