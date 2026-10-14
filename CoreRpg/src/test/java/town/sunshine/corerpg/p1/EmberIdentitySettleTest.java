package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-DC D13334: identity_settle_* remember map/family/slot + line helpers. */
public class EmberIdentitySettleTest {

    private static EmberRunMaps.MapDef map(String key, String name) {
        java.util.Map<String, Object> raw = new HashMap<String, Object>();
        if (name != null) raw.put("name", name);
        return new EmberRunMaps.MapDef(key, raw);
    }

    @Test
    public void indexAndKeyRoundTrip() {
        assertEquals(0, EmberIdentitySettle.indexOf(null));
        assertEquals(0, EmberIdentitySettle.indexOf("sx01"));
        assertEquals(0, EmberIdentitySettle.indexOf("q08"));
        assertEquals(1, EmberIdentitySettle.indexOf("q01"));
        assertEquals(3, EmberIdentitySettle.indexOf("Q03"));
        assertEquals(7, EmberIdentitySettle.indexOf("q07"));
        assertEquals("q01", EmberIdentitySettle.keyOf(1));
        assertEquals("q07", EmberIdentitySettle.keyOf(7));
        assertEquals("", EmberIdentitySettle.keyOf(0));
        assertEquals("", EmberIdentitySettle.keyOf(8));
    }

    @Test
    public void familyAndSlotIndexes() {
        assertEquals(1, EmberIdentitySettle.familyIndex("scorch"));
        assertEquals(2, EmberIdentitySettle.familyIndex("BURST"));
        assertEquals(3, EmberIdentitySettle.familyIndex("sustain"));
        assertEquals(0, EmberIdentitySettle.familyIndex("none"));
        assertEquals(0, EmberIdentitySettle.familyIndex(null));
        assertEquals("scorch", EmberIdentitySettle.familyOf(1));
        assertEquals("sustain", EmberIdentitySettle.familyOf(3));
        assertEquals("", EmberIdentitySettle.familyOf(0));

        assertEquals(1, EmberIdentitySettle.slotIndex("blade"));
        assertEquals(2, EmberIdentitySettle.slotIndex("charm"));
        assertEquals(6, EmberIdentitySettle.slotIndex("boots"));
        assertEquals(0, EmberIdentitySettle.slotIndex(null));
        assertEquals("blade", EmberIdentitySettle.slotOf(1));
        assertEquals("boots", EmberIdentitySettle.slotOf(6));
        assertEquals("", EmberIdentitySettle.slotOf(0));
    }

    @Test
    public void rememberAndLast_overwrite() {
        PlayerData d = new PlayerData();
        assertEquals("", EmberIdentitySettle.lastMap(d));
        assertEquals("", EmberIdentitySettle.lastFamily(d));
        assertEquals("", EmberIdentitySettle.lastSlot(d));
        assertTrue(EmberIdentitySettle.remember(d, "q03", "burst", "blade"));
        assertEquals("q03", EmberIdentitySettle.lastMap(d));
        assertEquals("burst", EmberIdentitySettle.lastFamily(d));
        assertEquals("blade", EmberIdentitySettle.lastSlot(d));
        assertFalse(EmberIdentitySettle.remember(d, "q03", "burst", "blade"));
        assertTrue(EmberIdentitySettle.remember(d, "q07", "scorch", "charm"));
        assertEquals("q07", EmberIdentitySettle.lastMap(d));
        assertEquals("scorch", EmberIdentitySettle.lastFamily(d));
        assertEquals("charm", EmberIdentitySettle.lastSlot(d));
        assertFalse(EmberIdentitySettle.remember(d, "sx01", "burst", "blade"));
        assertFalse(EmberIdentitySettle.remember(d, "q01", "none", "blade"));
        assertEquals("q07", EmberIdentitySettle.lastMap(d));
        // slot optional: family+map still remembered, slot 0
        assertTrue(EmberIdentitySettle.remember(d, "q01", "sustain", null));
        assertEquals("q01", EmberIdentitySettle.lastMap(d));
        assertEquals("sustain", EmberIdentitySettle.lastFamily(d));
        assertEquals("", EmberIdentitySettle.lastSlot(d));
    }

    @Test
    public void displayNameFamilySlotAndLine() {
        assertEquals("烬门庭", EmberIdentitySettle.displayName(map("q01", "烬门庭"), "q01"));
        assertEquals("q01", EmberIdentitySettle.displayName(null, "q01"));
        assertEquals("焚烬", EmberIdentitySettle.familyDisplay("scorch"));
        assertEquals("刃", EmberIdentitySettle.slotDisplay("blade"));
        assertEquals("", EmberIdentitySettle.familyDisplay("none"));
        assertEquals("", EmberIdentitySettle.slotDisplay(null));
        assertEquals("烬门庭 · 焚烬 · 刃",
                EmberIdentitySettle.line(map("q01", "烬门庭"), "q01", "scorch", "blade"));
        assertEquals("烬门庭 · 烬爆",
                EmberIdentitySettle.line(map("q01", "烬门庭"), "q01", "burst", null));
        assertEquals("烬门庭",
                EmberIdentitySettle.line(map("q01", "烬门庭"), "q01", null, null));
        assertEquals("", EmberIdentitySettle.line(null, "", "scorch", "blade"));
    }

    @Test
    public void settleNextLoopConstants_menuOpen() {
        assertEquals("/ember_p1_identity_settle", EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberIdentitySettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberIdentitySettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberIdentitySettle.SETTLE_FORGE_CMD);
        assertEquals("/ember_p1_gear", EmberIdentitySettle.SETTLE_GEAR_CMD);
        assertFalse(EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberIdentitySettle.SETTLE_FORGE_CMD.contains("corerpg p1 forge"));
    }
}
