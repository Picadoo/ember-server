package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSigChaseTest {
    @Test public void parse_and_progress() {
        assertEquals("L01", EmberSigChase.parse("L01").id);
        assertEquals("L02", EmberSigChase.parse("l02").id);
        String mid = EmberSigChase.progressLine(EmberSignature.byId("L01"), 2);
        assertTrue(mid.contains("L01"));
        assertTrue(mid.contains("2"));
        assertTrue(EmberSigChase.progressLine(EmberSignature.byId("L01"), 5).contains("就绪"));
        assertEquals("L01 残门焚斧", EmberSigChase.label(EmberSignature.byId("L01")));
    }
}
