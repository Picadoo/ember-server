package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberGemAcquireRemainTest {
    @Test public void remainLine() {
        assertEquals(5, EmberGemAcquire.clearsLeft(0));
        assertEquals(1, EmberGemAcquire.clearsLeft(4));
        assertEquals(0, EmberGemAcquire.clearsLeft(5));
        assertEquals("通关保底 0/5 · 再通关5次得石", EmberGemAcquire.remainLine(0));
        assertEquals("通关保底 4/5 · 再通关1次得石", EmberGemAcquire.remainLine(4));
        assertEquals("通关保底已满 · 下次通关得石", EmberGemAcquire.remainLine(5));
    }
}
