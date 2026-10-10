package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGemPathTest {
    @Test public void policies() {
        assertEquals(EmberGemPath.GEM_SHARP, EmberGemPath.gemId(EmberGemPath.SHARP));
        assertEquals(EmberGemPath.SHARP, EmberGemPath.parse("锋刃"));
        assertEquals(EmberGemPath.GALE, EmberGemPath.parse("疾风"));
        assertTrue(EmberGemPath.valid(EmberGemPath.DRAIN));
    }
}
