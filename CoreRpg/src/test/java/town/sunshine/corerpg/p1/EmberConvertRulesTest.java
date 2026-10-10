package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

/** D431 每周转化纯规则 */
public class EmberConvertRulesTest {
    @Test public void costs() {
        assertEquals(2, EmberConvertRules.blanks(1));
        assertEquals(6, EmberConvertRules.blanks(3));
        assertEquals(300, EmberConvertRules.coins(1));
        assertEquals(1000, EmberConvertRules.coins(3));
        assertEquals(2, EmberConvertRules.cost(1).blanks);
    }

    @Test public void qualityCap() {
        assertEquals(2, EmberConvertRules.qualityAfter(3));
        assertEquals(1, EmberConvertRules.qualityAfter(1));
        assertEquals(0, EmberConvertRules.qualityAfter(0));
    }

    @Test public void planKeepsUidResetsEnhance() {
        EmberItemData before = EmberItemData.create("scorch", "blade", 2, 3, 2, 5, true, "drop");
        EmberItemData after = EmberConvertRules.plan(before, "burst");
        assertEquals(before.uid, after.uid);
        assertEquals("burst", after.family);
        assertEquals(2, after.quality); // capped from 3
        assertEquals(0, after.enhance);
        assertEquals(before.craft, after.craft);
        assertEquals(before.slot, after.slot);
        assertEquals(before.tier, after.tier);
    }

    @Test public void refuseSameFamilyAndCap() {
        EmberItemData it = EmberItemData.create("scorch", "blade", 1, 0, 0, 0, true, "drop");
        assertTrue(EmberConvertRules.refusal(it, "scorch", 0, true).contains("相同"));
        assertTrue(EmberConvertRules.refusal(it, "burst", 1, true).contains("用完"));
    }
}
