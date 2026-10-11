package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDropCraftNudgeTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberDropCraftNudge.consumeExtra(d));
        assertEquals(0, EmberDropCraftNudge.consumeExtra(d));
    }
}
