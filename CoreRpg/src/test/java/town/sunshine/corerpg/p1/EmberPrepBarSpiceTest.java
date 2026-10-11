package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2913+ Prep/Bar/Spice. */
public class EmberPrepBarSpiceTest {

    @Test
    public void prepBarEnterLatches() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markPrepEnter(d));
        assertTrue(EmberSkillKit.prepPathUnlocked(d, null));
        PlayerData d2 = new PlayerData();
        assertTrue(EmberSkillKit.markBarEnter(d2));
        assertTrue(EmberSkillKit.barPathUnlocked(d2, null));
    }

    @Test
    public void spiceEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markSpiceEnter(d));
        assertTrue(EmberSkillKit.spicePathUnlocked(d, null));
    }

    @Test
    public void prepTargets() {
        assertEquals(3, EmberPrepPath.targetBottles(EmberPrepPath.LIGHT));
        assertEquals(5, EmberPrepPath.targetBottles(EmberPrepPath.FULL));
        assertEquals(0, EmberPrepPath.targetBottles(EmberPrepPath.BARE));
    }

    @Test
    public void barSlotOrdersDiffer() {
        assertEquals(8, EmberBarPath.slotOrder(EmberBarPath.RIGHT)[0]);
        assertEquals(1, EmberBarPath.slotOrder(EmberBarPath.LEFT)[0]);
        assertEquals(4, EmberBarPath.slotOrder(EmberBarPath.KEY5)[0]);
    }

    @Test
    public void spiceMatchHelpers() {
        String any = EmberSpicePath.BLAZE_AFFIX.iterator().next();
        assertTrue(EmberSpicePath.matchesAffix(EmberSpicePath.BLAZE, any));
        String obj = EmberSpicePath.OBJECTIVE_EVENT.iterator().next();
        assertTrue(EmberSpicePath.matchesEvent(EmberSpicePath.OBJECTIVE, obj));
        assertFalse(EmberSpicePath.matchesEvent(EmberSpicePath.BLAZE, obj));
    }

    @Test
    public void biasAlwaysPreferred() {
        List<String> aff = new ArrayList<String>(EmberSpicePath.BLAZE_AFFIX);
        List<String> ev = new ArrayList<String>(EmberSpicePath.OBJECTIVE_EVENT);
        String[] rolled = new String[]{"r1", "frost", "r2", "timed"};
        String[] out = EmberSpicePath.applyBias(rolled, EmberSpicePath.BLAZE, aff, ev, 42L);
        assertTrue(EmberSpicePath.BLAZE_AFFIX.contains(out[1]));
        String[] out2 = EmberSpicePath.applyBias(rolled, EmberSpicePath.OBJECTIVE, aff, ev, 7L);
        assertTrue(EmberSpicePath.OBJECTIVE_EVENT.contains(out2[3]));
    }
}
