package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.PlayerData;

/** D1258 unary + D12751 tip-AP nudge-aware remain honesty. */
public class EmberGemAcquireRemainTest {
    @Test public void remainLine_unary_regression() {
        assertEquals(5, EmberGemAcquire.clearsLeft(0));
        assertEquals(1, EmberGemAcquire.clearsLeft(4));
        assertEquals(0, EmberGemAcquire.clearsLeft(5));
        assertEquals("通关保底 0/5 · 再通关5次得石", EmberGemAcquire.remainLine(0));
        assertEquals("通关保底 4/5 · 再通关1次得石", EmberGemAcquire.remainLine(4));
        assertEquals("通关保底已满 · 下次通关得石", EmberGemAcquire.remainLine(5));
    }

    @Test public void clearsLeft_nudgeReady() {
        assertEquals(1, EmberGemAcquire.clearsLeft(3, true));   // 2-1
        assertEquals(2, EmberGemAcquire.clearsLeft(2, true));   // 3-1
        assertEquals(1, EmberGemAcquire.clearsLeft(4, true));   // left==1, no subtract
        assertEquals(2, EmberGemAcquire.clearsLeft(3, false));
        assertEquals(0, EmberGemAcquire.clearsLeft(5, true));
    }

    @Test public void remainLine_nudgeReady_cases() {
        String r3 = EmberGemAcquire.remainLine(3, true);
        assertTrue(r3.contains("再通关1次"));
        assertTrue(r3.contains("今日加码待用"));

        String r2 = EmberGemAcquire.remainLine(2, true);
        assertTrue(r2.contains("再通关2次"));
        assertTrue(r2.contains("今日加码待用"));

        String r4 = EmberGemAcquire.remainLine(4, true);
        assertTrue(r4.contains("再通关1次"));
        assertTrue(r4.contains("今日加码待用"));

        String r3f = EmberGemAcquire.remainLine(3, false);
        assertEquals("通关保底 3/5 · 再通关2次得石", r3f);
        assertFalse(r3f.contains("今日加码待用"));

        assertEquals("通关保底已满 · 下次通关得石", EmberGemAcquire.remainLine(5, true));
        assertFalse(EmberGemAcquire.remainLine(5, true).contains("今日加码待用"));
    }

    @Test public void remainLine_pd_feeds_ready() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberGemAcquire.C_PITY, "all", 3);
        assertTrue(EmberGemPityNudge.ready(d));
        String rem = EmberGemAcquire.remainLine(d);
        assertTrue(rem.contains("再通关1次"));
        assertTrue(rem.contains("今日加码待用"));

        EmberGemPityNudge.consumeExtra(d);
        assertFalse(EmberGemPityNudge.ready(d));
        String after = EmberGemAcquire.remainLine(d);
        assertEquals("通关保底 3/5 · 再通关2次得石", after);
        assertFalse(after.contains("今日加码待用"));
    }

    @Test public void hopeLine_same_effective_as_remain() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberGemAcquire.C_PITY, "all", 3);
        assertEquals(EmberGemAcquire.remainLine(d), EmberGemAcquire.hopeLine(d));
        assertNotNull(EmberGemAcquire.hopeLine(null));
        assertEquals(EmberGemAcquire.remainLine(null), EmberGemAcquire.hopeLine(null));
    }
}
