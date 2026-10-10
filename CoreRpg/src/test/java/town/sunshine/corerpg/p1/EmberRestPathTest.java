package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRestPathTest {
    @Test public void effectiveHeal_never_raises() {
        assertEquals(0.3, EmberRestPath.effectiveHeal(EmberRestPath.NONE, 0.3), 1e-9);
        assertEquals(0.3, EmberRestPath.effectiveHeal(EmberRestPath.FULL, 0.3), 1e-9);
        assertEquals(0.15, EmberRestPath.effectiveHeal(EmberRestPath.LIGHT, 0.3), 1e-9);
        assertEquals(0.0, EmberRestPath.effectiveHeal(EmberRestPath.BARE, 0.3), 1e-9);
        assertEquals(0.0, EmberRestPath.effectiveHeal(EmberRestPath.FULL, 0.0), 1e-9);
    }
    @Test public void parse() {
        assertEquals(EmberRestPath.FULL, EmberRestPath.parse("满休"));
        assertEquals(EmberRestPath.LIGHT, EmberRestPath.parse("轻休"));
        assertEquals(EmberRestPath.BARE, EmberRestPath.parse("不休"));
    }
}
