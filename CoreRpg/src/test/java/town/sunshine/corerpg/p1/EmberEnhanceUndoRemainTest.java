package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;

public class EmberEnhanceUndoRemainTest {
    @Test public void remainSec() {
        EmberEnhanceUndo.Snap s = new EmberEnhanceUndo.Snap(
                UUID.randomUUID(), "u", 3, 1, 2, 1,
                EmberUpgradeRules.Cost.NONE, System.currentTimeMillis());
        assertTrue(EmberEnhanceUndo.remainSec(s, System.currentTimeMillis()) >= 59);
    }
}
