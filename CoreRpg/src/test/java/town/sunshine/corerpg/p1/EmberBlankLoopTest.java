package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBlankLoopTest {
    @Test public void lines() {
        assertTrue(EmberBlankLoop.chatLine(3).contains("胚料 ×3"));
        assertTrue(EmberBlankLoop.followHint().contains("去升阶"));
        assertTrue(EmberBlankLoop.followHint().contains("烙纹合成"));
    }
}
