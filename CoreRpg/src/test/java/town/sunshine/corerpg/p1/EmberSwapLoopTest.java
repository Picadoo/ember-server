package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSwapLoopTest {
    @Test public void hints() {
        assertTrue(EmberSwapLoop.followHint().contains("去强化"));
        assertTrue(EmberSwapLoop.chatLine().contains("互换"));
        assertEquals("", EmberSwapLoop.winnerHint(null));
    }
}
