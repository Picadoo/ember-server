package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSipPathTest {
    @Test public void policies() {
        assertTrue(EmberSipPath.shouldAuto(EmberSipPath.AUTO));
        assertTrue(EmberSipPath.shouldMute(EmberSipPath.MUTE));
        assertEquals(EmberSipPath.ASK, EmberSipPath.parse("提醒"));
        assertTrue(EmberSipPath.suppressLowHpBar(null) == false);
    }
}
