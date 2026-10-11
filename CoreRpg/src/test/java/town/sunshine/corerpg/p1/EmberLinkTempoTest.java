package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberLinkTempoTest {
    @Test
    public void muteHidesCue() {
        assertFalse(EmberLinkTempo.showCue(EmberLinkPath.MUTE));
        assertTrue(EmberLinkTempo.showCue(EmberLinkPath.ASK));
    }

    @Test
    public void askNearDenser() {
        int base = EmberLinkTempo.pulseCount(EmberLinkPath.ASK);
        int near = EmberLinkTempo.pulseCountNear(EmberLinkPath.ASK, true);
        assertTrue(near > base);
        assertEquals(base, EmberLinkTempo.pulseCountNear(EmberLinkPath.ASK, false));
    }

    @Test
    public void nearEntry() {
        assertTrue(EmberLinkTempo.nearEntry(9.0));
        assertFalse(EmberLinkTempo.nearEntry(25.0));
    }

    @Test
    public void bars() {
        assertTrue(EmberLinkTempo.actionBar("").contains("可跃迁"));
        assertTrue(EmberLinkTempo.landActionBar("上行").contains("上行"));
    }
}
