package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberAbyssTargetTest {
    @Test public void glanceUnset() {
        String g = EmberAbyssTarget.glance(null, 0);
        assertTrue(g.contains("未设"));
    }
}
