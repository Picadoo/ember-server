package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberUpgradeLoopTest {
    @Test public void hints() {
        assertTrue(EmberUpgradeLoop.followHint().contains("去强化"));
        assertTrue(EmberUpgradeLoop.chatLine().contains("升阶"));
        assertEquals("", EmberUpgradeLoop.pieceHint(null));
    }
}
