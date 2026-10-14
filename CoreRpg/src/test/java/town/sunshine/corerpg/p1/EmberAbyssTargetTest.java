package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberAbyssTargetTest {
    @Test public void glanceUnset() {
        String g = EmberAbyssTarget.glance(null, 0);
        assertTrue(g.contains("未设"));
        assertTrue(g.contains("点下一环") || g.contains("设目标"));
        assertFalse(g.contains("/corerpg p1 abysstarget"));
    }
}
