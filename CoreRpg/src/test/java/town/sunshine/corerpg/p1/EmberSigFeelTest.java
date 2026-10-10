package town.sunshine.corerpg.p1;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;
public class EmberSigFeelTest {
    @Test public void owner_finds_dodge_heal_L02() {
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertNotNull(l02);
        assertEquals("L02", EmberSigFeel.owner(Collections.singletonList(l02), "dodge_heal").id);
        assertNull(EmberSigFeel.owner(Collections.singletonList(l02), "skill_ignite"));
    }
    @Test public void procLine_and_runLine() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertTrue(EmberSigFeel.procLine(l02, "躲开回气").contains("门楼余烬"));
        assertTrue(EmberSigFeel.runLine(Arrays.asList(l01, l02)).contains("本局签名"));
        assertEquals("", EmberSigFeel.runLine(Collections.<EmberSignature.Def>emptyList()));
    }
    @Test public void debounce_window() {
        assertFalse(EmberSigFeel.wouldAllow(1000L, 1000L + 400));
        assertTrue(EmberSigFeel.wouldAllow(1000L, 1000L + 800));
    }
}
