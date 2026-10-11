package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAbyssEnterBraceTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAbyssEnterBrace.ready(d));
        d.addPeriodCount(EmberAbyssEnterBrace.C_USED, "day", 1);
        assertFalse(EmberAbyssEnterBrace.ready(d));
    }
}
