package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSlashFirstHealTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSlashFirstHeal.ready(d));
        d.addPeriodCount(EmberSlashFirstHeal.C_USED, "day", 1);
        assertFalse(EmberSlashFirstHeal.ready(d));
    }
}
