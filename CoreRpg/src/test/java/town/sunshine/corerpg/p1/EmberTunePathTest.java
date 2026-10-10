package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberTunePathTest {
    @Test public void parse_map_lead_attune() {
        assertEquals(EmberTunePath.SHARP, EmberTunePath.parse("锋刃"));
        assertEquals(EmberTunePath.WARD, EmberTunePath.parse("ward"));
        assertEquals(EmberTunePath.FULL, EmberTunePath.parse("双签"));
        assertEquals(EmberDualLead.BLADE, EmberTunePath.leadId(EmberTunePath.SHARP));
        assertEquals(EmberAttunePath.ORIGIN, EmberTunePath.attuneId(EmberTunePath.SHARP));
        assertEquals(EmberDualLead.CHARM, EmberTunePath.leadId(EmberTunePath.WARD));
        assertEquals(EmberAttunePath.ALT, EmberTunePath.attuneId(EmberTunePath.WARD));
        assertEquals(EmberDualLead.BOTH, EmberTunePath.leadId(EmberTunePath.FULL));
        assertTrue(EmberTunePath.tip(EmberTunePath.FULL).contains("调律"));
    }
}
