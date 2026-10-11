package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberParryCastFeelTest {
    @Test
    public void openRingReadable() {
        assertTrue(EmberParryFeel.openRingPoints() >= 10);
        assertTrue(EmberParryFeel.openRingRadius() > 0.5);
    }

    @Test
    public void successDenserThanOld() {
        assertTrue(EmberParryFeel.successPrimary() > 18);
        assertTrue(EmberParryFeel.successSecondary() >= 8);
    }
}
