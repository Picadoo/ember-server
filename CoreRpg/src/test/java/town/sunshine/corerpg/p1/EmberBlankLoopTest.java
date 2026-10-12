package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBlankLoopTest {
    @Test public void lines() {
        assertTrue(EmberBlankLoop.chatLine(3).contains("胚料 ×3"));
        // TIP-AM D12703: followHint drops fake 成色/精工/升阶; honest pack vocabulary
        String h = EmberBlankLoop.followHint();
        assertTrue(h.contains("分解") || h.contains("成套") || h.contains("工坊"));
        assertFalse(h.contains("成色"));
        assertFalse(h.contains("精工"));
        assertFalse(h.contains("升阶"));
    }
}
