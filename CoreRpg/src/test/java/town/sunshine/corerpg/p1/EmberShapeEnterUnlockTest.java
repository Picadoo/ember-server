package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2280: Q04 first-enter unlocks shape without clear. */
public class EmberShapeEnterUnlockTest {

    @Test
    public void markShapeEnterOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markShapeEnter(d));
        assertFalse(EmberSkillKit.markShapeEnter(d));
        assertEquals(1, d.periodCount(EmberSkillKit.C_SHAPE_ENTER, "all"));
    }

    @Test
    public void shapeUnlockedViaEnterWithoutClear() {
        PlayerData d = new PlayerData();
        assertFalse(EmberSkillKit.shapeUnlocked(d, null));
        EmberSkillKit.markShapeEnter(d);
        assertTrue(EmberSkillKit.shapeUnlocked(d, null));
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, d, null, null, EmberGrowth.Mods.NONE, null);
        // with enter latch, stored LINE applies
        EmberSkillKit.setShape(d, EmberSkillKit.SHAPE_LINE);
        sh = EmberSkillKit.resolve(null, d, null, null, EmberGrowth.Mods.NONE, "burst");
        assertEquals(EmberSkillKit.SHAPE_LINE, sh.id);
        assertEquals(0.95, sh.lineWidth, 1e-6); // synced burst+line
    }
}
