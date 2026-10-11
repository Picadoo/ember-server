package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberDoorOpenSpeedTest {
    @Test public void ticks() {
        EmberDoorOpenSpeed.clearAll();
        assertEquals(40, EmberDoorOpenSpeed.SPEED_TICKS);
    }
}
