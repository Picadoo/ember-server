package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-DD D13335: featured_settle_* remember map/path/cap + line helpers. */
public class EmberFeaturedSettleTest {

    private static EmberRunMaps.MapDef map(String key, String name) {
        java.util.Map<String, Object> raw = new HashMap<String, Object>();
        if (name != null) raw.put("name", name);
        return new EmberRunMaps.MapDef(key, raw);
    }

    @Test
    public void indexAndKeyRoundTrip() {
        assertEquals(0, EmberFeaturedSettle.indexOf(null));
        assertEquals(0, EmberFeaturedSettle.indexOf("sx01"));
        assertEquals(0, EmberFeaturedSettle.indexOf("q08"));
        assertEquals(1, EmberFeaturedSettle.indexOf("q01"));
        assertEquals(3, EmberFeaturedSettle.indexOf("Q03"));
        assertEquals(7, EmberFeaturedSettle.indexOf("q07"));
        assertEquals("q01", EmberFeaturedSettle.keyOf(1));
        assertEquals("q07", EmberFeaturedSettle.keyOf(7));
        assertEquals("", EmberFeaturedSettle.keyOf(0));
        assertEquals("", EmberFeaturedSettle.keyOf(8));
    }

    @Test
    public void rememberAndLast_overwrite() {
        PlayerData d = new PlayerData();
        assertEquals("", EmberFeaturedSettle.lastMap(d));
        assertEquals(EmberFeaturedPath.NONE, EmberFeaturedSettle.lastPath(d));
        assertFalse(EmberFeaturedSettle.lastCapFull(d));
        assertTrue(EmberFeaturedSettle.remember(d, "q03", EmberFeaturedPath.CHALLENGE, false));
        assertEquals("q03", EmberFeaturedSettle.lastMap(d));
        assertEquals(EmberFeaturedPath.CHALLENGE, EmberFeaturedSettle.lastPath(d));
        assertFalse(EmberFeaturedSettle.lastCapFull(d));
        assertFalse(EmberFeaturedSettle.remember(d, "q03", EmberFeaturedPath.CHALLENGE, false));
        assertTrue(EmberFeaturedSettle.remember(d, "q07", EmberFeaturedPath.NORMAL, true));
        assertEquals("q07", EmberFeaturedSettle.lastMap(d));
        assertEquals(EmberFeaturedPath.NORMAL, EmberFeaturedSettle.lastPath(d));
        assertTrue(EmberFeaturedSettle.lastCapFull(d));
        assertFalse(EmberFeaturedSettle.remember(d, "sx01", EmberFeaturedPath.CHALLENGE, true));
        assertEquals("q07", EmberFeaturedSettle.lastMap(d));
        // path NONE still remembers map + cap
        assertTrue(EmberFeaturedSettle.remember(d, "q01", EmberFeaturedPath.NONE, false));
        assertEquals("q01", EmberFeaturedSettle.lastMap(d));
        assertEquals(EmberFeaturedPath.NONE, EmberFeaturedSettle.lastPath(d));
        assertFalse(EmberFeaturedSettle.lastCapFull(d));
    }

    @Test
    public void displayNameAndLine() {
        assertEquals("锈轨精选", EmberFeaturedSettle.displayName(map("q03", "锈轨精选"), "q03"));
        assertEquals("q03", EmberFeaturedSettle.displayName(null, "q03"));
        assertEquals("锈轨精选 · 精选·挑战 · 周帽未满",
                EmberFeaturedSettle.line(map("q03", "锈轨精选"), "q03", EmberFeaturedPath.CHALLENGE, false));
        assertEquals("锈轨精选 · 精选·普通 · 周帽已满",
                EmberFeaturedSettle.line(map("q03", "锈轨精选"), "q03", EmberFeaturedPath.NORMAL, true));
        assertEquals("锈轨精选 · 周帽已满",
                EmberFeaturedSettle.line(map("q03", "锈轨精选"), "q03", EmberFeaturedPath.NONE, true));
        assertEquals("", EmberFeaturedSettle.line(null, "", EmberFeaturedPath.CHALLENGE, false));
    }

    @Test
    public void settleNextLoopConstants_menuOpen() {
        assertEquals("/ember_p1_featured_settle", EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberFeaturedSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberFeaturedSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberFeaturedSettle.SETTLE_FORGE_CMD);
        assertFalse(EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberFeaturedSettle.SETTLE_FORGE_CMD.contains("corerpg p1 forge"));
    }
}
