package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberEchoPathTest {
    @Test public void parse_map() {
        assertEquals(1, EmberEchoPath.parse("q01"));
        assertEquals(7, EmberEchoPath.parse("echo_q07"));
        assertEquals(0, EmberEchoPath.parse("clear"));
        assertEquals("echo_q03", EmberEchoPath.mapKey(3));
        assertTrue(EmberEchoPath.label(1).contains("Q01"));
        assertTrue(EmberEchoPath.glance(2, true).contains("Q02"));
    }
}
