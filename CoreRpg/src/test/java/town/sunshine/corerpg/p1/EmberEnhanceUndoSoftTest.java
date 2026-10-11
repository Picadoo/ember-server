package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberEnhanceUndoSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        long w = EmberEnhanceUndoSoft.windowMs(d);
        assertEquals(EmberEnhanceUndo.WINDOW_MS + EmberEnhanceUndoSoft.EXTRA_MS, w);
        assertEquals(EmberEnhanceUndo.WINDOW_MS + EmberEnhanceUndoSoft.EXTRA_MS, EmberEnhanceUndoSoft.windowMs(d));
    }
}
