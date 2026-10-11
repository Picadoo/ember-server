package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;
public class EmberRaidReviveBoostTest {
    @Test public void firstBoostThenBase() {
        EmberRaidReviveBoost.clearAll();
        UUID u = UUID.randomUUID();
        assertEquals(0.65, EmberRaidReviveBoost.healPct("run1", u), 1e-9);
        assertEquals(0.50, EmberRaidReviveBoost.healPct("run1", u), 1e-9);
        assertEquals(0.65, EmberRaidReviveBoost.healPct("run2", u), 1e-9);
    }
}
