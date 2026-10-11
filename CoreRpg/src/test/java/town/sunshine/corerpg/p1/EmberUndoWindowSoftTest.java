package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberUndoWindowSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(15, EmberUndoWindowSoft.apply(d, 10));
        assertEquals(15, EmberUndoWindowSoft.apply(d, 10));
    }
}
