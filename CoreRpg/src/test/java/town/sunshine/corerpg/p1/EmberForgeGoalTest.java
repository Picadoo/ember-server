package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberForgeGoalTest {
    @Test public void parse_and_gap() {
        assertEquals(EmberForgeGoal.ENHANCE, EmberForgeGoal.parse("enhance"));
        assertEquals(EmberForgeGoal.CONVERT, EmberForgeGoal.parse("转化"));
        assertEquals(EmberForgeGoal.NONE, EmberForgeGoal.parse("clear"));
        assertEquals("强化+1", EmberForgeGoal.label(EmberForgeGoal.ENHANCE));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ENHANCE, "0", "", "", "", "").contains("已够"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ENHANCE, "3", "", "", "", "").contains("3"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ROLL, "", "", "", "胚差0·币差0", "").contains("已够"));
        assertTrue(EmberForgeGoal.glance(EmberForgeGoal.BRAND, "还差").contains("合成烙纹"));
    }
}
