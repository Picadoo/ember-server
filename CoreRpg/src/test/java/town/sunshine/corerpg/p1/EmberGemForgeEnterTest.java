package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberGemForgeEnterTest {
    @Test public void gemLatch() {
        assertEquals("p1_gem_enter", EmberSkillKit.C_GEM_ENTER);
        assertFalse(EmberSkillKit.markGemEnter(null));
        assertFalse(EmberSkillKit.gemPathUnlocked(null, null));
    }
    @Test public void directedT1Always() {
        assertTrue(EmberSkillKit.directedForgeOpen(null, null, 1));
        assertFalse(EmberSkillKit.directedForgeOpen(null, null, 2));
        assertFalse(EmberSkillKit.directedForgeOpen(null, null, 3));
    }
}
