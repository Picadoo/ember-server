package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

/** D12257–D12259 + D12442–D12449 TIP-S: forge choice-feel half-lines pure. */
public class EmberForgeChoiceTest {

    @Test
    public void brandWillGet() {
        String s = EmberForgeChoice.brandWillGet("猎缀");
        assertTrue(s.contains("将得到"));
        assertTrue(s.contains("猎缀"));
        assertTrue(s.contains("手感"));
        assertTrue(s.contains("不加伤"));
    }

    @Test
    public void convertLeftoverQiWhenZero() {
        String s = EmberForgeChoice.convertLeftover(0, "§f焚烬");
        assertTrue(s.contains("还可转 §f0"));
        assertTrue(s.contains("齐"));
    }

    @Test
    public void convertLeftoverGapWhenLeft() {
        String s = EmberForgeChoice.convertLeftover(1, "§f焚烬");
        assertTrue(s.contains("还可转 §f1"));
        assertTrue(s.contains("焚烬"));
        assertFalse(s.contains("齐"));
    }

    @Test
    public void rerollChoiceLockVsRandom() {
        assertEquals("§7选的是：§f锁定·只重抽档", EmberForgeChoice.rerollChoice(true));
        assertEquals("§7选的是：§f随机词条", EmberForgeChoice.rerollChoice(false));
    }

    @Test
    public void stableChoice_threeKinds() {
        assertEquals("§7选的是：§f稳线·成色确定+1 §8· 非旁路赌锻", EmberForgeChoice.stableChoice("成色"));
        assertEquals("§7选的是：§f稳线·精工确定+1 §8· 非旁路赌锻", EmberForgeChoice.stableChoice("精工"));
        assertEquals("§7选的是：§f稳线·强化确定+1 §8· 非旁路赌锻", EmberForgeChoice.stableChoice("强化"));
    }

    @Test
    public void gambleChoice_floorBranches() {
        assertEquals("§7选的是：§f旁路·随机锻（极品仍0）", EmberForgeChoice.gambleChoice(false));
        assertEquals("§7选的是：§f旁路·下限锻（成色≥精良·极品仍0）", EmberForgeChoice.gambleChoice(true));
    }

    @Test
    public void goalBypassHint_stableGoalsOnly() {
        String expect = "§8本周目标是稳线/确定路 · 此为旁路";
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.ENHANCE));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.QUALITY));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.REFINE));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.UPGRADE));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.BRAND));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.GEM));
        assertEquals(expect, EmberForgeChoice.goalBypassHint(EmberForgeGoal.LOCK));
        assertEquals("", EmberForgeChoice.goalBypassHint(EmberForgeGoal.NONE));
        assertEquals("", EmberForgeChoice.goalBypassHint(EmberForgeGoal.ROLL));
        assertEquals("", EmberForgeChoice.goalBypassHint(EmberForgeGoal.CONVERT));
    }

    @Test
    public void stableGambleEnabled_defaultTrue_and_testHook() {
        EmberForgeChoice.setStableGambleEnabledForTest(null);
        assertTrue(EmberForgeChoice.stableGambleEnabled());
        EmberForgeChoice.setStableGambleEnabledForTest(Boolean.FALSE);
        assertFalse(EmberForgeChoice.stableGambleEnabled());
        EmberForgeChoice.setStableGambleEnabledForTest(null);
        assertTrue(EmberForgeChoice.stableGambleEnabled());
    }

    @Test
    public void forgeRollQualityWeights_unchangedLiterals() {
        assertEquals(4, EmberForgeRollRules.QUALITY_W.length);
        assertEquals(0, EmberForgeRollRules.QUALITY_W[3]);
        assertArrayEquals(new int[]{70, 23, 7, 0}, EmberForgeRollRules.QUALITY_W);
        assertArrayEquals(new int[]{0, 23, 7, 0}, EmberForgeRollRules.QUALITY_W_FLOOR);
    }
}
