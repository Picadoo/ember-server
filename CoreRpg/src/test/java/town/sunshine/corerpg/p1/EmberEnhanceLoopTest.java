package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberEnhanceLoopTest {
    @Test public void hints_and_leveled() {
        assertTrue(EmberEnhanceLoop.followHint(true).contains("再强化"));
        assertTrue(EmberEnhanceLoop.followHint(false).contains("再试一次"));
        assertTrue(EmberEnhanceLoop.leveled(null));
        assertFalse(EmberEnhanceLoop.leveled(java.util.Collections.emptyList()));
        assertTrue(EmberEnhanceLoop.chatLine(true).contains("成功"));
    }
}
