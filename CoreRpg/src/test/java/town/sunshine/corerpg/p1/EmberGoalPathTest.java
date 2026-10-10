package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGoalPathTest {
    @Test public void focusAndChase() {
        assertEquals("featured", EmberGoalPath.focusGoal(EmberGoalPath.FEATURED));
        assertEquals("bounty", EmberGoalPath.focusGoal(EmberGoalPath.BOUNTY));
        assertNull(EmberGoalPath.focusGoal(EmberGoalPath.FLEX));
        assertTrue(EmberGoalPath.chaseLine(EmberGoalPath.ABYSS, 0, 1, "深渊").contains("0/1"));
        assertTrue(EmberGoalPath.chaseLine(EmberGoalPath.ABYSS, 1, 1, "深渊").contains("达标"));
        assertNull(EmberGoalPath.chaseLine(EmberGoalPath.FLEX, 0, 1, "x"));
        assertEquals(EmberGoalPath.RAID, EmberGoalPath.parse("团本"));
    }
}
