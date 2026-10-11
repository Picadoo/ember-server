package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberUpgradeSureTest {
    @Test public void nullGoal() {
        assertFalse(EmberUpgradeSure.goalReady(null));
    }
}
