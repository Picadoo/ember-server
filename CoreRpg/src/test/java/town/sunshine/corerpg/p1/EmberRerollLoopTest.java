package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRerollLoopTest {
    @Test public void followHintMentionsLock() {
        assertTrue(EmberRerollLoop.followHint().contains("锁定"));
    }
}
