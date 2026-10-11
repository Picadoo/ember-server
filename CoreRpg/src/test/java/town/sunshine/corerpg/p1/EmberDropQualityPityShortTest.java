package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberDropQualityPityShortTest {
    @Test public void needUnchanged() {
        assertEquals(5, EmberDropQualityPity.NEED);
        assertEquals(1, EmberDropQualityPity.FLOOR);
    }
}
