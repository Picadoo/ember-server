
package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

/** D2434: goal-aware HeldNext convert/brand. */
public class EmberForgeHeldNextGoalTest {
    private static EmberItemData blade() {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1,
                0, 0, 0, 0, true, "drop", 1, 1);
    }

    @Test
    public void convertGoalWhenPayable() {
        EmberForgeHeldNext.Mats m = new EmberForgeHeldNext.Mats(0, 99, 0, 9999);
        String line = EmberForgeHeldNext.line(blade(), m, true, EmberForgeGoal.CONVERT);
        assertTrue(line, line.contains("跨族转化"));
        assertTrue(line, line.contains("料够"));
    }

    @Test
    public void brandGoalWhenPayable() {
        EmberForgeHeldNext.Mats m = new EmberForgeHeldNext.Mats(EmberBrandRules.craftShards(), 0, 0, 0);
        String line = EmberForgeHeldNext.line(blade(), m, true, EmberForgeGoal.BRAND);
        assertTrue(line, line.contains("合成烙纹"));
    }
}
