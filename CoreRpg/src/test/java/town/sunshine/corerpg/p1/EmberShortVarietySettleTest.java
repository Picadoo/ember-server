package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-CY D13330: short_var_* remember + labels; zero economy grants. */
public class EmberShortVarietySettleTest {

    @Test
    public void indexRoundTrip() {
        assertEquals(0, EmberShortVarietySettle.indexOfAffix(null));
        assertEquals(0, EmberShortVarietySettle.indexOfAffix(""));
        assertEquals(0, EmberShortVarietySettle.indexOfAffix("not_a_real_affix"));
        assertEquals(1, EmberShortVarietySettle.indexOfAffix("blazing"));
        assertEquals(1, EmberShortVarietySettle.indexOfAffix("BLAZING"));
        assertEquals(0, EmberShortVarietySettle.indexOfEvent(null));
        assertEquals(1, EmberShortVarietySettle.indexOfEvent("timed"));
        assertEquals(2, EmberShortVarietySettle.indexOfEvent("crystal"));
        assertEquals("blazing", EmberShortVarietySettle.affixOf(1));
        assertEquals("timed", EmberShortVarietySettle.eventOf(1));
        assertEquals("", EmberShortVarietySettle.affixOf(0));
        assertEquals("", EmberShortVarietySettle.eventOf(0));
    }

    @Test
    public void emptyRunNoVariety() {
        PlayerData d = new PlayerData();
        EmberRunSession s = new EmberRunSession();
        assertTrue(EmberShortVarietySettle.remember(d, s));
        assertFalse(EmberShortVarietySettle.hit(d));
        assertEquals("0", EmberShortVarietySettle.hitBit(d));
        assertEquals("", EmberShortVarietySettle.affixLabel(d));
        assertEquals("", EmberShortVarietySettle.eventLabel(d));
        assertEquals(EmberShortVarietySettle.LINE_NONE, EmberShortVarietySettle.line(d));
    }

    @Test
    public void affixOnly() {
        PlayerData d = new PlayerData();
        EmberRunSession s = new EmberRunSession();
        s.affix = "venom";
        s.eventKind = "";
        EmberShortVarietySettle.remember(d, s);
        assertTrue(EmberShortVarietySettle.hit(d));
        assertEquals("1", EmberShortVarietySettle.hitBit(d));
        assertEquals("毒十字", EmberShortVarietySettle.affixLabel(d));
        assertEquals("", EmberShortVarietySettle.eventLabel(d));
        assertEquals("毒十字", EmberShortVarietySettle.line(d));
    }

    @Test
    public void eventOnly() {
        PlayerData d = new PlayerData();
        EmberRunSession s = new EmberRunSession();
        s.affix = "";
        s.eventKind = "breach";
        EmberShortVarietySettle.remember(d, s);
        assertTrue(EmberShortVarietySettle.hit(d));
        assertEquals("裂隙", EmberShortVarietySettle.eventLabel(d));
        assertEquals("", EmberShortVarietySettle.affixLabel(d));
        assertEquals("裂隙", EmberShortVarietySettle.line(d));
    }

    @Test
    public void bothAffixAndEvent() {
        PlayerData d = new PlayerData();
        EmberRunSession s = new EmberRunSession();
        s.affix = "blazing";
        s.eventKind = "crystal";
        EmberShortVarietySettle.remember(d, s);
        assertEquals("炽热", EmberShortVarietySettle.affixLabel(d));
        assertEquals("砸余烬晶", EmberShortVarietySettle.eventLabel(d));
        assertEquals("炽热 / 砸余烬晶", EmberShortVarietySettle.line(d));
    }

    @Test
    public void overwriteClearsPrevious() {
        PlayerData d = new PlayerData();
        EmberShortVarietySettle.remember(d, "shield", "hold");
        assertEquals("厚甲 / 占点", EmberShortVarietySettle.line(d));
        EmberRunSession empty = new EmberRunSession();
        EmberShortVarietySettle.remember(d, empty);
        assertFalse(EmberShortVarietySettle.hit(d));
        assertEquals(EmberShortVarietySettle.LINE_NONE, EmberShortVarietySettle.line(d));
        EmberShortVarietySettle.remember(d, "frost", "timed");
        assertEquals("凝霜 / 限时清房", EmberShortVarietySettle.line(d));
    }

    @Test
    public void nullTrimAndSessionNull() {
        PlayerData d = new PlayerData();
        EmberShortVarietySettle.remember(d, "  charge  ", "  escort  ");
        assertEquals("冲锋", EmberShortVarietySettle.affixLabel(d));
        assertEquals("护宝兔", EmberShortVarietySettle.eventLabel(d));
        assertTrue(EmberShortVarietySettle.remember(d, (EmberRunSession) null));
        assertFalse(EmberShortVarietySettle.hit(d));
    }

    @Test
    public void zeroEconomyGrants() {
        PlayerData d = new PlayerData();
        Map<String, Integer> before = new java.util.HashMap<String, Integer>(d.getCounters());
        EmberShortVarietySettle.remember(d, "blazing", "crystal");
        EmberShortVarietySettle.hit(d);
        EmberShortVarietySettle.affixLabel(d);
        EmberShortVarietySettle.eventLabel(d);
        EmberShortVarietySettle.line(d);
        Map<String, Integer> after = d.getCounters();
        for (Map.Entry<String, Integer> e : after.entrySet()) {
            String k = e.getKey();
            assertFalse("must not write economy grant key: " + k,
                    k.startsWith("var_affix") || k.startsWith("var_event")
                            || k.contains("event_core") || k.contains("affix_shard")
                            || k.contains("mat_ember") || k.startsWith("ni_"));
        }
        // only our two period keys may appear as new writes
        for (String k : after.keySet()) {
            if (!before.containsKey(k)) {
                assertTrue("unexpected new counter " + k,
                        k.startsWith(EmberShortVarietySettle.C_AFFIX + "@")
                                || k.startsWith(EmberShortVarietySettle.C_EVENT + "@"));
            }
        }
        assertTrue(after.containsKey(EmberShortVarietySettle.C_AFFIX + "@all"));
        assertTrue(after.containsKey(EmberShortVarietySettle.C_EVENT + "@all"));
    }
}
