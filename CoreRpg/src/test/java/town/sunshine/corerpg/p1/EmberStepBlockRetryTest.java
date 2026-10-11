package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStepBlockRetryTest {
    @Test public void blockArmsOnce() {
        PlayerData d = new PlayerData();
        UUID id = UUID.randomUUID();
        EmberStepBlockRetry.clear(id);
        EmberStepBlockRetry.onBlock(d, id);
        assertTrue(EmberStepBlockRetry.isArmed(id));
        assertTrue(EmberStepBlockRetry.usedToday(d));
        EmberStepBlockRetry.onBlock(d, id); // no second arm latch
        assertEquals(EmberStepBlockRetry.BONUS, EmberStepBlockRetry.consume(id), 1e-9);
        assertFalse(EmberStepBlockRetry.isArmed(id));
    }
}
