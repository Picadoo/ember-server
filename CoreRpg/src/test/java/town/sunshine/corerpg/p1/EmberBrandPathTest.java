package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBrandPathTest {
    @Test public void parse_and_pin() {
        assertEquals(EmberBrandPath.HUNT, EmberBrandPath.parse("猎缀"));
        assertEquals(EmberBrandPath.EMBER, EmberBrandPath.parse("b_set"));
        assertEquals(EmberBrandPath.BIND, EmberBrandPath.parse("定身"));
        assertEquals(EmberBrandPath.NONE, EmberBrandPath.parse("clear"));
        assertTrue(EmberBrandPath.pinCmd(EmberBrandPath.HUNT).contains("b_affix"));
        assertTrue(EmberBrandPath.glance(EmberBrandPath.EMBER).contains("余烬"));
    }
}
