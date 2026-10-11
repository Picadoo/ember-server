package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberMoltenWarnExtraTest {
    @Test public void once() {
        EmberMoltenWarnExtra.clearAll();
        assertEquals(400L, EmberMoltenWarnExtra.consumeExtraMs("r1"));
        assertEquals(0L, EmberMoltenWarnExtra.consumeExtraMs("r1"));
    }
}
