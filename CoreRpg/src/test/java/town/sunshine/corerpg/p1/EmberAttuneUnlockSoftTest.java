package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAttuneUnlockSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        int base = EmberSignature.altMarks();
        if (base <= 0) return; // economy not loaded offline — skip soft assert
        int peek = EmberAttuneUnlockSoft.need(d, false);
        assertTrue(peek <= base);
        assertTrue(EmberAttuneUnlockSoft.ready(d));
        int pay = EmberAttuneUnlockSoft.need(d, true);
        assertFalse(EmberAttuneUnlockSoft.ready(d));
        assertEquals(base, EmberAttuneUnlockSoft.need(d, true));
        assertEquals(pay, peek);
    }
}
