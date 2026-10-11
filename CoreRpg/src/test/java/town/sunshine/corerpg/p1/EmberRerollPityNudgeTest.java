package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRerollPityNudgeTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberRerollPityNudge.consumeExtra(d));
        assertEquals(0, EmberRerollPityNudge.consumeExtra(d));
    }
}
