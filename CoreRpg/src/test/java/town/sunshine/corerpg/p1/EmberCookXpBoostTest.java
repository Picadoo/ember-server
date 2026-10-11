package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCookXpBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(4, EmberCookXpBoost.consumeXp(d, 2));
        assertEquals(2, EmberCookXpBoost.consumeXp(d, 2));
    }
}
