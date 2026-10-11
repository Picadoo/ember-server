package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberHungerLockTest {
    @Test public void defaultsWhenModeNull() {
        // without Bukkit/EmberMode, enabled() is false — saturate is null-safe
        EmberHungerLock.saturate(null);
        assertFalse(EmberHungerLock.enabled());
    }
}
