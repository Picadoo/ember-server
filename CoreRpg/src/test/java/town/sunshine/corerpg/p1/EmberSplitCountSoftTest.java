package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberSplitCountSoftTest {
    @Test public void once() {
        EmberSplitCountSoft.clearAll();
        assertEquals(2, EmberSplitCountSoft.consumeCount("r1", 3));
        assertEquals(3, EmberSplitCountSoft.consumeCount("r1", 3));
        assertEquals(1, EmberSplitCountSoft.consumeCount("r2", 1)); // no soft when base<=1
    }
}
