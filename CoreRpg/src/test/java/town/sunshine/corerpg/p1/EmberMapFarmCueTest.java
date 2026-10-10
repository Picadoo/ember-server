package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberMapFarmCueTest {
    @Test public void nextLineNamesChase() {
        String a = EmberMapFarmCue.nextLine("scorch", "blade", null, null, 0, 0);
        assertTrue(a.contains("刃"));
        String b = EmberMapFarmCue.nextLine("burst", "charm", "burst", "护符", 2, 5);
        assertTrue(b.contains("同族"));
        String c = EmberMapFarmCue.nextLine("sustain", "blade", "scorch", "刃", 1, 3);
        assertTrue(c.contains("现穿"));
    }
}
