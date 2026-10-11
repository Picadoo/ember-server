package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;

public class EmberStepSlashArmTest {
    @Test
    public void arm_consume() {
        UUID id = UUID.randomUUID();
        EmberStepSlashArm.clear(id);
        assertEquals(0.0, EmberStepSlashArm.consume(id), 1e-9);
        EmberStepSlashArm.arm(id);
        assertTrue(EmberStepSlashArm.isArmed(id));
        assertEquals(EmberStepSlashArm.BONUS, EmberStepSlashArm.consume(id), 1e-9);
        assertFalse(EmberStepSlashArm.isArmed(id));
    }
}
