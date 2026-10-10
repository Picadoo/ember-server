package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSigChaseReadyTest {
    @Test public void progress_ready_vs_gap() {
        EmberSignature.Def d = EmberSignature.byId("L01");
        assertNotNull(d);
        assertTrue(EmberSigChase.progressLine(d, EmberSignature.IMPRINT_MARKS).contains("就绪"));
        assertTrue(EmberSigChase.progressLine(d, EmberSignature.IMPRINT_MARKS - 1).contains("/"));
        assertFalse(EmberSigChase.progressLine(d, 0).contains("就绪"));
    }
}
