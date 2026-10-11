package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;
public class EmberParrySlashArmTest {
    @Test public void armConsume() {
        UUID id = UUID.randomUUID();
        EmberParrySlashArm.clear(id);
        EmberParrySlashArm.arm(id);
        assertEquals(EmberParrySlashArm.BONUS, EmberParrySlashArm.consume(id), 1e-9);
        assertEquals(0.0, EmberParrySlashArm.consume(id), 1e-9);
    }
}
