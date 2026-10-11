package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFishXpBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(4, EmberFishXpBoost.consumeXp(d, 2));
        assertEquals(2, EmberFishXpBoost.consumeXp(d, 2));
    }
}
