package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDropQualityNudgeTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberDropQualityNudge.consumeExtra(d));
        assertEquals(0, EmberDropQualityNudge.consumeExtra(d));
    }
}
