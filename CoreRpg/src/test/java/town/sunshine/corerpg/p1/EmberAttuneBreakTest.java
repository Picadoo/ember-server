package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberAttuneBreakTest {
    @Test public void latchPrefix() { assertEquals("p1_attune_auto_", EmberAttuneBreak.C_AUTO); }
    @Test public void townOkNull() { assertFalse(EmberAttuneBreak.townOk(null, null)); }
}
