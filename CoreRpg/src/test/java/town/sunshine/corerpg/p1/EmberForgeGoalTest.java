package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberForgeGoalTest {
    @Test public void parse_and_gap() {
        assertEquals(EmberForgeGoal.ENHANCE, EmberForgeGoal.parse("enhance"));
        assertEquals(EmberForgeGoal.CONVERT, EmberForgeGoal.parse("转化"));
        assertEquals(EmberForgeGoal.QUALITY, EmberForgeGoal.parse("成色"));
        assertEquals(EmberForgeGoal.QUALITY, EmberForgeGoal.parse("quality"));
        assertEquals(EmberForgeGoal.NONE, EmberForgeGoal.parse("clear"));
        assertEquals("强化+1", EmberForgeGoal.label(EmberForgeGoal.ENHANCE));
        assertEquals("成色升档", EmberForgeGoal.label(EmberForgeGoal.QUALITY));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ENHANCE, "0", "", "", "", "").contains("已够"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ENHANCE, "3", "", "", "", "").contains("3"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.ROLL, "", "", "", "胚差0·币差0", "").contains("已够"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.QUALITY, "", "", "", "", "", "胚差0·骨差0·币差0").contains("已够"));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.QUALITY, "", "", "", "", "", "胚差2·骨差0·币差0").contains("胚差2"));
        assertTrue(EmberForgeGoal.glance(EmberForgeGoal.BRAND, "还差").contains("合成烙纹"));
        assertTrue(EmberForgeGoal.valid(EmberForgeGoal.QUALITY));
        assertEquals(EmberForgeGoal.GEM, EmberForgeGoal.parse("宝石"));
        assertEquals("宝石合成", EmberForgeGoal.label(EmberForgeGoal.GEM));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.GEM, "", "", "", "", "", null, "碎差0·骨差0·币差0").contains("已够"));
        assertTrue(EmberForgeGoal.valid(EmberForgeGoal.GEM));
        assertEquals(EmberForgeGoal.LOCK, EmberForgeGoal.parse("锁定"));
        assertEquals("锁定冲档", EmberForgeGoal.label(EmberForgeGoal.LOCK));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.LOCK, "", "", "", "", "", null, null, "碎差0·币差0").contains("已够"));
        assertTrue(EmberForgeGoal.valid(EmberForgeGoal.LOCK));
        assertEquals(EmberForgeGoal.UPGRADE, EmberForgeGoal.parse("升阶"));
        assertEquals("升阶冲战力", EmberForgeGoal.label(EmberForgeGoal.UPGRADE));
        assertTrue(EmberForgeGoal.gapLine(EmberForgeGoal.UPGRADE, "", "", "", "", "", null, null, null, "碎差0·核差0·胚差0").contains("已够"));
        assertTrue(EmberForgeGoal.valid(EmberForgeGoal.UPGRADE));
    }
}
