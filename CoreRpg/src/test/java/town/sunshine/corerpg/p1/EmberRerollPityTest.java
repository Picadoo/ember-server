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
}
