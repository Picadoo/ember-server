package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberMentorPathTest {
    @Test public void policies() {
        assertTrue(EmberMentorPath.shouldAutoAccept(EmberMentorPath.OPEN));
        assertFalse(EmberMentorPath.shouldAutoAccept(EmberMentorPath.GATE));
        assertTrue(EmberMentorPath.shouldAutoDeny(EmberMentorPath.BUSY));
        assertEquals(EmberMentorPath.OPEN, EmberMentorPath.parse("敞开"));
    }
}
