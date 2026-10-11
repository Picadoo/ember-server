package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberWeeklyGoalTargetSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberWeeklyGoalTargetSoft.softTarget(d, 3));
        assertTrue(EmberWeeklyGoalTargetSoft.consume(d));
        assertEquals(2, EmberWeeklyGoalTargetSoft.softTarget(d, 3));
        assertFalse(EmberWeeklyGoalTargetSoft.consume(d));
    }
}
