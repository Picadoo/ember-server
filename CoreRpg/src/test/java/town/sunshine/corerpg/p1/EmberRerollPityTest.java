package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRerollPityTest {
    @Test public void glance_progress_and_ready() {
        assertTrue(EmberRerollPity.glance(2, 5).contains("2"));
        assertTrue(EmberRerollPity.glance(2, 5).contains("/5"));
        assertFalse(EmberRerollPity.glance(2, 5).contains("必出"));
        assertTrue(EmberRerollPity.glance(5, 5).contains("必出"));
        assertTrue(EmberRerollPity.glance(6, 5).contains("必出"));
    }

    @Test public void remainLine_andAttempts() {
        assertEquals(3, EmberRerollPity.attemptsUntilCap(0, 3));
        assertEquals(1, EmberRerollPity.attemptsUntilCap(3, 3));
        assertEquals(1, EmberRerollPity.attemptsUntilCap(5, 3));
        assertEquals("这次必出上限档", EmberRerollPity.remainLine(3, 3));
        assertEquals("第0/3 · 含本次≤3次必出上限档", EmberRerollPity.remainLine(0, 3));
        assertEquals("第2/3 · 含本次≤1次必出上限档", EmberRerollPity.remainLine(2, 3));
        assertEquals("下次洗练必出上限档", EmberRerollPity.afterRemainLine(3, 3));
        assertEquals("含下次≤2次必出上限档", EmberRerollPity.afterRemainLine(1, 3));
    }
}
