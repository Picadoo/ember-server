package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRaidPathTest {
    @Test public void parse_labels_enter() {
        assertEquals(EmberRaidPath.R01, EmberRaidPath.parse("r01"));
        assertEquals(EmberRaidPath.R02, EmberRaidPath.parse("霜封"));
        assertEquals(EmberRaidPath.R03, EmberRaidPath.parse("scorch"));
        assertEquals(EmberRaidPath.NONE, EmberRaidPath.parse("clear"));
        assertEquals("r02", EmberRaidPath.mapKey(EmberRaidPath.R02));
        assertTrue(EmberRaidPath.tip(EmberRaidPath.R01).contains("撞墙"));
        assertTrue(EmberRaidPath.enterCmd(EmberRaidPath.R03).contains("r03"));
        assertTrue(EmberRaidPath.glance(EmberRaidPath.R02, true).contains("R02"));
    }
}
