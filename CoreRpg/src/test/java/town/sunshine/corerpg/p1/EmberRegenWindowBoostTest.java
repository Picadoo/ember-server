package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberRegenWindowBoostTest {
    @Test public void once() {
        EmberRegenWindowBoost.clearAll();
        assertEquals(400L, EmberRegenWindowBoost.consumeExtraMs("r1"));
        assertEquals(0L, EmberRegenWindowBoost.consumeExtraMs("r1"));
    }
}
