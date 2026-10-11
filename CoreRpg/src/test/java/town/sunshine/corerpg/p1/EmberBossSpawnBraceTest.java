package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberBossSpawnBraceTest {
    @Test public void ticks() {
        EmberBossSpawnBrace.clearAll();
        assertEquals(40, EmberBossSpawnBrace.RESIST_TICKS);
    }
}
