package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFestivalEntrySoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberFestivalEntrySoft.consume(d));
        assertFalse(EmberFestivalEntrySoft.consume(d));
    }
}
