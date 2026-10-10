package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Collections;

public class EmberGrowthNextHopeTest {
    @Test public void nextHopeUsesSpecWordingNotHope() {
        String s = EmberGrowth.nextHope(null, Collections.<Integer, String>emptyMap(), 0, 0);
        assertTrue(s.contains("未配置") || s.contains("专精"));
        assertFalse(s.contains("天赋希望"));
    }
}
