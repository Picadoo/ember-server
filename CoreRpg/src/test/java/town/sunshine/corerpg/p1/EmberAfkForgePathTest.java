package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberAfkForgePathTest {
    @Test public void preferEnhanceWhenGoal() {
        String s = EmberAfkForgePath.suggestLine(20, 0, 0, 0, EmberForgeGoal.ENHANCE);
        assertTrue(s.contains("强化"));
    }
    @Test public void autoShardPath() {
        String s = EmberAfkForgePath.suggestLine(15, 0, 0, 0, EmberForgeGoal.NONE);
        assertTrue(s.contains("强化"));
    }
    @Test public void preferQualityWhenGoal() {
        String s = EmberAfkForgePath.suggestLine(0, 20, 20, 2000, EmberForgeGoal.QUALITY);
        assertTrue(s.contains("成色"));
    }
    @Test public void preferGemWhenGoal() {
        EmberUpgradeRules.Cost c = EmberGemAcquire.craftCost();
        String s = EmberAfkForgePath.suggestLine(c.shards, c.bone, 0, c.coins, EmberForgeGoal.GEM);
        assertTrue(s.contains("宝石"));
    }
}
