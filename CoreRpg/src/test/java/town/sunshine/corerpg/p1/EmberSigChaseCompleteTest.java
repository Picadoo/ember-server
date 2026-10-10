package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSigChaseCompleteTest {
    @Test public void closes_chase_when_same_def() {
        EmberSignature.Def a = EmberSignature.byId("L01");
        EmberSignature.Def b = EmberSignature.byId("L02");
        assertNotNull(a);
        assertNotNull(b);
        assertTrue(EmberSigChase.closesChase(a, a));
        assertFalse(EmberSigChase.closesChase(a, b));
        assertFalse(EmberSigChase.closesChase(null, a));
    }
}
