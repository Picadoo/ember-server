package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberChainNeedSoftTest {
    @Test public void once() {
        EmberChainNeedSoft.clearAll();
        assertEquals(1, EmberChainNeedSoft.consumeLess("r1"));
        assertEquals(0, EmberChainNeedSoft.consumeLess("r1"));
    }
}
