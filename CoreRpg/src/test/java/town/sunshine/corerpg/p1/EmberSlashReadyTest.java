package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSlashReadyTest {
    @Test public void ready_lines() {
        assertTrue(EmberSlashReady.readyActionBar().contains("充能"));
        assertTrue(EmberSlashReady.readyActionBar().contains("烬斩"));
        assertTrue(EmberSlashReady.readyActionBar().contains("就绪"));
        assertEquals("烬斩充能就绪", EmberSlashReady.readyChat());
    }
}
