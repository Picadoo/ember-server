package town.sunshine.corerpg.p1;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDailyPathTest {
    private static List<EmberRunRules.BountyTier> tiers() {
        return Arrays.asList(
                new EmberRunRules.BountyTier(1, 30, 0, 0, 0),
                new EmberRunRules.BountyTier(3, 60, 6, 0, 0));
    }
    @Test public void targetsAndChase() {
        assertEquals(1, EmberDailyPath.targetClears(EmberDailyPath.LIGHT, tiers()));
        assertEquals(3, EmberDailyPath.targetClears(EmberDailyPath.FULL, tiers()));
        assertEquals(0, EmberDailyPath.targetClears(EmberDailyPath.FLEX, tiers()));
        assertEquals(0, EmberDailyPath.targetClears(EmberDailyPath.FULL, Collections.emptyList()));
        assertTrue(EmberDailyPath.chaseLine(EmberDailyPath.FULL, tiers(), 0).contains("0/3"));
        assertTrue(EmberDailyPath.chaseLine(EmberDailyPath.FULL, tiers(), 3).contains("达标"));
        assertTrue(EmberDailyPath.chaseLine(EmberDailyPath.LIGHT, tiers(), 1).contains("达标"));
        assertNull(EmberDailyPath.chaseLine(EmberDailyPath.FLEX, tiers(), 1));
        assertEquals(EmberDailyPath.FULL, EmberDailyPath.parse("满委"));
    }
}
