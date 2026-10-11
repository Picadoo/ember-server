package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemPityNudgeTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberGemPityNudge.consumeExtra(d));
        assertEquals(0, EmberGemPityNudge.consumeExtra(d));
    }
}
