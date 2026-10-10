package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDosePathTest {
    @Test public void policies() {
        assertTrue(EmberDosePath.shouldAuto(EmberDosePath.AUTO));
        assertTrue(EmberDosePath.shouldMute(EmberDosePath.MUTE));
        assertEquals(EmberDosePath.ASK, EmberDosePath.parse("提醒"));
    }
}
