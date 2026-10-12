package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.PlayerData;

/** D1323 unary + D12783 tip-AR DropCraft/Q nudge-aware remain honesty. */
public class EmberDropQualityPityRemainTest {
    @Test public void remainLine_unary_regression() {
        assertEquals(5, EmberDropQualityPity.clearsLeft(0));
        assertEquals(0, EmberDropQualityPity.clearsLeft(5));
        assertEquals("成色软保底 0/5 · 再连出标准5次触发精良底", EmberDropQualityPity.remainLine(0));
        assertEquals("成色软保底已满 · 下次主线装必≥精良", EmberDropQualityPity.remainLine(5));
    }

    @Test public void clearsLeft_nudgeReady() {
        assertEquals(1, EmberDropQualityPity.clearsLeft(3, true));  // 2-1
        assertEquals(2, EmberDropQualityPity.clearsLeft(2, true));  // 3-1
        assertEquals(1, EmberDropQualityPity.clearsLeft(4, true));  // left==1
        assertEquals(2, EmberDropQualityPity.clearsLeft(3, false));
        assertEquals(0, EmberDropQualityPity.clearsLeft(5, true));
    }

    @Test public void remainLine_nudgeReady_cases() {
        String r3 = EmberDropQualityPity.remainLine(3, true);
        assertTrue(r3.contains("再连出标准1次"));
        assertTrue(r3.contains("今日加码待用"));

        String r2 = EmberDropQualityPity.remainLine(2, true);
        assertTrue(r2.contains("再连出标准2次"));
        assertTrue(r2.contains("今日加码待用"));

        String r4 = EmberDropQualityPity.remainLine(4, true);
        assertTrue(r4.contains("再连出标准1次"));
        assertTrue(r4.contains("今日加码待用"));

        String r3f = EmberDropQualityPity.remainLine(3, false);
        assertEquals("成色软保底 3/5 · 再连出标准2次触发精良底", r3f);
        assertFalse(r3f.contains("今日加码待用"));

        assertEquals("成色软保底已满 · 下次主线装必≥精良", EmberDropQualityPity.remainLine(5, true));
        assertFalse(EmberDropQualityPity.remainLine(5, true).contains("今日加码待用"));
    }

    @Test public void remainLine_pd_feeds_qualityNudgeReady() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberDropQualityPity.C_PITY, "all", 3);
        assertTrue(EmberDropQualityNudge.ready(d));
        String rem = EmberDropQualityPity.remainLine(d);
        assertTrue(rem.contains("再连出标准1次"));
        assertTrue(rem.contains("今日加码待用"));

        EmberDropQualityNudge.consumeExtra(d);
        assertFalse(EmberDropQualityNudge.ready(d));
        String after = EmberDropQualityPity.remainLine(d);
        assertEquals("成色软保底 3/5 · 再连出标准2次触发精良底", after);
        assertFalse(after.contains("今日加码待用"));
    }

    @Test public void hope_same_effective_as_remain() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberDropQualityPity.C_PITY, "all", 3);
        assertEquals(EmberDropQualityPity.remainLine(d), EmberDropQualityPity.hope(d));
        assertTrue(EmberDropQualityPity.hope(d).contains("今日加码待用"));
        assertEquals(EmberDropQualityPity.remainLine(null), EmberDropQualityPity.hope(null));
    }
}
