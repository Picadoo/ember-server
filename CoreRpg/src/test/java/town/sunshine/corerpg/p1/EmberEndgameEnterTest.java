package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberEndgameEnterTest {
    @Test public void endgameDelegatesAttune() {
        assertFalse(EmberSkillKit.endgameUnlocked(null, null));
        assertFalse(EmberSkillKit.attuneUnlocked(null, null));
    }
    @Test public void directedStillSafe() {
        assertTrue(EmberSkillKit.directedForgeOpen(null, null, 1));
    }
}
