package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFeePathTest {
    @Test public void preferMark_and_parse() {
        assertTrue(EmberFeePath.preferMark(EmberFeePath.MARK));
        assertFalse(EmberFeePath.preferMark(EmberFeePath.COIN));
        assertFalse(EmberFeePath.preferMark(EmberFeePath.AUTO));
        assertFalse(EmberFeePath.preferMark(EmberFeePath.NONE));
        assertEquals(EmberFeePath.COIN, EmberFeePath.parse("币付"));
        assertEquals(EmberFeePath.MARK, EmberFeePath.parse("印付"));
        assertEquals(EmberFeePath.AUTO, EmberFeePath.parse("自动"));
    }
}
