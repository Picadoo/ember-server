package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberEventStartBraceTest {
    @Test public void ticks() {
        EmberEventStartBrace.clearAll();
        assertEquals(40, EmberEventStartBrace.RESIST_TICKS);
    }
}
