package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-DA D13332: challenge_settle_* remember + line helpers. */
public class EmberChallengeSettleTest {

    private static EmberRunMaps.MapDef map(String key, String name) {
        java.util.Map<String, Object> raw = new HashMap<String, Object>();
        if (name != null) raw.put("name", name);
        return new EmberRunMaps.MapDef(key, raw);
    }

    @Test
    public void indexAndKeyRoundTrip() {
        assertEquals(0, EmberChallengeSettle.indexOf(null));
        assertEquals(0, EmberChallengeSettle.indexOf("sx01"));
        assertEquals(0, EmberChallengeSettle.indexOf("q08"));
        assertEquals(1, EmberChallengeSettle.indexOf("q01"));
        assertEquals(3, EmberChallengeSettle.indexOf("Q03"));
        assertEquals(7, EmberChallengeSettle.indexOf("q07"));
        assertEquals("q01", EmberChallengeSettle.keyOf(1));
        assertEquals("q07", EmberChallengeSettle.keyOf(7));
        assertEquals("", EmberChallengeSettle.keyOf(0));
        assertEquals("", EmberChallengeSettle.keyOf(8));
    }

    @Test
    public void rememberAndLastMap_overwrite() {
        PlayerData d = new PlayerData();
        assertEquals("", EmberChallengeSettle.lastMap(d));
        assertTrue(EmberChallengeSettle.remember(d, "q03"));
        assertEquals("q03", EmberChallengeSettle.lastMap(d));
        assertFalse(EmberChallengeSettle.remember(d, "q03"));
        assertTrue(EmberChallengeSettle.remember(d, "q07"));
        assertEquals("q07", EmberChallengeSettle.lastMap(d));
        assertFalse(EmberChallengeSettle.remember(d, "sx01"));
        assertEquals("q07", EmberChallengeSettle.lastMap(d));
    }

    @Test
    public void displayNameAndLine() {
        assertEquals("锈廊", EmberChallengeSettle.displayName(map("q03", "锈廊"), "q03"));
        assertEquals("q03", EmberChallengeSettle.displayName(null, "q03"));
        assertEquals("锈廊 · 硬本·偏挑战",
                EmberChallengeSettle.line(map("q03", "锈廊"), "q03", EmberChallengePath.PREFER));
        assertEquals("锈廊", EmberChallengeSettle.line(map("q03", "锈廊"), "q03", EmberChallengePath.NONE));
        assertEquals("", EmberChallengeSettle.line(null, "", EmberChallengePath.PREFER));
    }

    @Test
    public void settleNextLoopConstants_menuOpen() {
        assertEquals("/ember_p1_challenge_settle", EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberChallengeSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberChallengeSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberChallengeSettle.SETTLE_FORGE_CMD);
        assertEquals("/ember_p1_challenge", EmberChallengeSettle.SETTLE_CHALLENGE_CMD);
        assertFalse(EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1 enter"));
        assertFalse(EmberChallengeSettle.SETTLE_FORGE_CMD.contains("corerpg p1 forge"));
    }
}
