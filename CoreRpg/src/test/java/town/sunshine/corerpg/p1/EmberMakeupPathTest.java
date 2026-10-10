package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberMakeupPathTest {
    @Test public void policies() {
        assertTrue(EmberMakeupPath.shouldAuto(EmberMakeupPath.AUTO));
        assertTrue(EmberMakeupPath.shouldMute(EmberMakeupPath.MUTE));
        assertEquals(EmberMakeupPath.ASK, EmberMakeupPath.parse("提醒"));
    }
    @Test public void makeupBlockRules() {
        // today signed (bit day10), miss day1, enough mins
        int mask = 1 << 9; // day 10
        assertNull(EmberSignService.makeupBlock(mask, 10, 0, 3, 0, 60, 60));
        assertNotNull(EmberSignService.makeupBlock(mask, 10, 0, 3, 0, 10, 60)); // mins
    }
}
