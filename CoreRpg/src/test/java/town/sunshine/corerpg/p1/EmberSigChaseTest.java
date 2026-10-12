package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSigChaseTest {
    @Test public void parse_and_progress() {
        assertEquals("L01", EmberSigChase.parse("L01").id);
        assertEquals("L02", EmberSigChase.parse("l02").id);
        String mid = EmberSigChase.progressLine(EmberSignature.byId("L01"), 2);
        assertTrue(mid.contains("L01"));
        assertTrue(mid.contains("2/5"));
        assertTrue(mid.contains("不锁通关"));
        String ready = EmberSigChase.progressLine(EmberSignature.byId("L01"), 5);
        assertTrue(ready.contains("5/5"));
        assertTrue(ready.contains("可烙刃")); // TIP-AO lo==null fallback
        assertFalse(ready.contains("满可烙印"));
        assertTrue(ready.contains("不锁通关"));
        assertEquals("L01 残门焚斧", EmberSigChase.label(EmberSignature.byId("L01")));
    }
}
