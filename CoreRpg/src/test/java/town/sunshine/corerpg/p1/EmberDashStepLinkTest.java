package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;

public class EmberDashStepLinkTest {
    @Test
    public void arm_consume_once() {
        UUID id = UUID.randomUUID();
        EmberDashStepLink.clear(id);
        assertFalse(EmberDashStepLink.isArmed(id));
        assertEquals(0.0, EmberDashStepLink.consume(id), 1e-9);
        EmberDashStepLink.arm(id);
        assertTrue(EmberDashStepLink.isArmed(id));
        assertEquals(EmberDashStepLink.BONUS, EmberDashStepLink.consume(id), 1e-9);
        assertFalse(EmberDashStepLink.isArmed(id));
        assertEquals(0.0, EmberDashStepLink.consume(id), 1e-9);
    }
}
