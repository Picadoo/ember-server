package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2865+ ExtraPath behavior. */
public class EmberExtraPathBehaviorTest {

    @Test
    public void enterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markExtraEnter(d));
        assertFalse(EmberSkillKit.markExtraEnter(d));
        assertTrue(EmberSkillKit.extraPathUnlocked(d, null));
    }

    @Test
    public void applyBiasForcesPreferred() {
        assertEquals(EmberRunRules.Extra.TREASURE,
                EmberExtraPath.applyBias(EmberRunRules.Extra.CHEST, EmberExtraPath.TREASURE));
        assertEquals(EmberRunRules.Extra.ELITE,
                EmberExtraPath.applyBias(EmberRunRules.Extra.NONE, EmberExtraPath.ELITE));
        assertEquals(EmberRunRules.Extra.CHEST,
                EmberExtraPath.applyBias(EmberRunRules.Extra.TREASURE, EmberExtraPath.CHEST));
    }

    @Test
    public void toExtraMaps() {
        assertEquals(EmberRunRules.Extra.TREASURE, EmberExtraPath.toExtra(EmberExtraPath.TREASURE));
        assertEquals(EmberRunRules.Extra.NONE, EmberExtraPath.toExtra(EmberExtraPath.NONE));
    }

    @Test
    public void typeHighlightDiff() {
        assertEquals(120, EmberExtraPath.glowTicksFor(EmberRunRules.Extra.ELITE));
        assertEquals(100, EmberExtraPath.glowTicksFor(EmberRunRules.Extra.TREASURE));
        assertTrue(EmberExtraPath.treasureGetsSlow(EmberRunRules.Extra.TREASURE));
        assertFalse(EmberExtraPath.treasureGetsSlow(EmberRunRules.Extra.ELITE));
    }
}
