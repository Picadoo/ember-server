package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFriendPathTest {
    @Test public void policies() {
        assertTrue(EmberFriendPath.shouldAutoAccept(EmberFriendPath.OPEN));
        assertFalse(EmberFriendPath.shouldAutoAccept(EmberFriendPath.GATE));
        assertTrue(EmberFriendPath.shouldAutoDeny(EmberFriendPath.BUSY));
        assertFalse(EmberFriendPath.shouldAutoDeny(EmberFriendPath.OPEN));
        assertEquals(EmberFriendPath.BUSY, EmberFriendPath.parse("静拒"));
        assertEquals(EmberFriendPath.OPEN, EmberFriendPath.parse("敞开"));
    }
}
