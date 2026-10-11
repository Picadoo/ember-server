
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

    @Test
    public void gemGoalWhenPayable() {
        EmberForgeHeldNext.Mats m = new EmberForgeHeldNext.Mats(99, 0, 99, 9999);
        String line = EmberForgeHeldNext.line(blade(), m, true, EmberForgeGoal.GEM);
        assertTrue(line, line.contains("合成宝石"));
    }

    @Test
    public void lockGoalWhenPayable() {
        EmberUpgradeRules.Cost lock = EmberForgeHeldNext.lockRerollCost(1);
        EmberForgeHeldNext.Mats m = new EmberForgeHeldNext.Mats(lock.shards, 0, 0, lock.coins);
        String line = EmberForgeHeldNext.line(blade(), m, true, EmberForgeGoal.LOCK);
        assertTrue(line, line.contains("锁定洗练"));
    }

    @Test
    public void postureLandHelpers() {
        assertEquals(20, EmberSkillKit.slashLandSlowTicks(false, false));
        assertEquals(26, EmberSkillKit.slashLandSlowTicks(true, true)); // +4 sync +2 posture
        assertEquals(EmberSkillKit.SLASH_HEAL_PCT * 1.25 * 1.1,
                EmberSkillKit.slashLandHealPct(true, true), 1e-9);
    }
}
