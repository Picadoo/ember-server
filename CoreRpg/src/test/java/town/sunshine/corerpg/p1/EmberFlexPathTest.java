package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFlexPathTest {
    @Test public void parse_tips() {
        assertEquals(EmberFlexPath.ON, EmberFlexPath.parse("装配"));
        assertEquals(EmberFlexPath.OFF, EmberFlexPath.parse("卸下"));
        assertEquals(EmberFlexPath.NONE, EmberFlexPath.parse("clear"));
        assertTrue(EmberFlexPath.tip(EmberFlexPath.ON).contains("身法"));
        assertTrue(EmberFlexPath.tip(EmberFlexPath.OFF).contains("不放"));
    }
}
