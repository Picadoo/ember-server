package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCrammingSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5.0, EmberCrammingSoft.consume(d, 10.0), 1e-9);
        assertEquals(10.0, EmberCrammingSoft.consume(d, 10.0), 1e-9);
    }
}
