package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBankDrawBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(3, EmberBankDrawBoost.consumeBonus(d));
        assertEquals(0, EmberBankDrawBoost.consumeBonus(d));
    }
}
