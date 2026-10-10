package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShortPathTest {
    @Test public void parse_and_late_band() {
        assertEquals(EmberShortPath.FC, EmberShortPath.parse("首通"));
        assertEquals(EmberShortPath.DAY, EmberShortPath.parse("有奖"));
        assertEquals(EmberShortPath.LATE, EmberShortPath.parse("后段"));
        assertEquals(EmberShortPath.NONE, EmberShortPath.parse("clear"));
        assertTrue(EmberShortPath.tip(EmberShortPath.FC).contains("首通"));
        EmberRunMaps.MapDef late = fake("sx15");
        EmberRunMaps.MapDef early = fake("sx03");
        assertTrue(EmberShortPath.inLateBand(late));
        assertFalse(EmberShortPath.inLateBand(early));
    }
    private static EmberRunMaps.MapDef fake(String key) {
        return new EmberRunMaps.MapDef(key, java.util.Collections.emptyMap());
    }
}
