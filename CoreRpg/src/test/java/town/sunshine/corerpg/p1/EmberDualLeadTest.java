package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDualLeadTest {
    @Test public void parse_label() {
        assertEquals(EmberDualLead.BLADE, EmberDualLead.parse("刃优先"));
        assertEquals(EmberDualLead.CHARM, EmberDualLead.parse("charm"));
        assertEquals(EmberDualLead.BOTH, EmberDualLead.parse("双开"));
        assertEquals(EmberDualLead.NONE, EmberDualLead.parse("clear"));
        assertTrue(EmberDualLead.glance(EmberDualLead.BLADE, true).contains("刃"));
        assertTrue(EmberDualLead.glance(0, false).contains("Q03"));
    }
}
