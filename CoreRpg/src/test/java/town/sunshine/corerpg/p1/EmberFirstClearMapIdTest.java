package town.sunshine.corerpg.p1;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class EmberFirstClearMapIdTest {
    @Test public void firstClearGuaranteesMapFamily() {
        EmberRunRules.LootBias lb = new EmberRunRules.LootBias("scorch", "blade", 0.625, 0.5, 0.7);
        for (int i = 0; i < 40; i++) {
            EmberRunRules.ItemRoll r = EmberRunRules.rollFirstClearMapIdentity(2, lb, new Random(i * 17L), null);
            assertEquals("scorch", r.family);
        }
    }

    @Test public void flagOnlyOnBaseItemFirstClear() {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.loot = new EmberRunRules.LootBias("burst", "charm", 0.625, 0.5, 0.7);
        in.firstClear = new EmberRunRules.FirstClear("q02", null, 1, 0, 0, 0, "q03");
        assertTrue(EmberRunRules.firstClearMapIdentity(in, "base_item"));
        assertFalse(EmberRunRules.firstClearMapIdentity(in, "extra_chest_item"));
        in.firstClear = null;
        assertFalse(EmberRunRules.firstClearMapIdentity(in, "base_item"));
    }

    @Test public void shapeStage2Distinct() {
        EmberSkillKit.Shape L = new EmberSkillKit.Shape(EmberSkillKit.SHAPE_LINE, 6.0, 100.0, 6.0, 0.7, 3, false, "直线穿刺");
        EmberSkillKit.Shape R = new EmberSkillKit.Shape(EmberSkillKit.SHAPE_RING, 3.5, 360.0, 0, 0.0, 4, false, "原地环斩");
        EmberSkillKit.Shape F = new EmberSkillKit.Shape(EmberSkillKit.SHAPE_FAN, 3.5, 110.0, 0, 0.0, 5, false, "扇形");
        assertTrue(L.range >= 6.0);
        assertEquals(0.7, L.lineWidth, 0.001);
        assertEquals(4, R.maxTargets);
        assertTrue(R.range >= 3.5);
        assertTrue(F.arc >= 110.0 - 0.01);
    }
}
