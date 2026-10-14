package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberChallengePathTest {
    @Test public void resolve() {
        assertTrue(EmberChallengePath.resolve(false, EmberChallengePath.PREFER, true, true));
        assertFalse(EmberChallengePath.resolve(true, EmberChallengePath.EASY, true, true));
        assertFalse(EmberChallengePath.resolve(false, EmberChallengePath.FOLLOW, true, true));
        assertTrue(EmberChallengePath.resolve(true, EmberChallengePath.FOLLOW, true, true));
        assertFalse(EmberChallengePath.resolve(false, EmberChallengePath.PREFER, false, true)); // not cleared
        assertFalse(EmberChallengePath.resolve(false, EmberChallengePath.PREFER, true, false)); // locked
        assertEquals(EmberChallengePath.PREFER, EmberChallengePath.parse("偏挑战"));
    }
    @Test public void glance_menuFirst_noBareChallengepath() {
        String g = EmberChallengePath.glance(EmberChallengePath.NONE, true);
        assertTrue(g.contains("点下一环"));
        assertFalse(g.contains("/corerpg p1 challengepath"));
        assertTrue(EmberChallengePath.glance(EmberChallengePath.PREFER, true).contains("偏挑战"));
    }
}
