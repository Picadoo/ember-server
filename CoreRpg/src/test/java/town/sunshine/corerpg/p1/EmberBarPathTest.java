package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBarPathTest {
    @Test public void slotOrder() {
        assertArrayEquals(new int[]{8,7,6,5,4,3,2,1}, EmberBarPath.slotOrder(EmberBarPath.NONE));
        assertArrayEquals(new int[]{8,7,6,5,4,3,2,1}, EmberBarPath.slotOrder(EmberBarPath.RIGHT));
        assertArrayEquals(new int[]{1,2,3,4,5,6,7,8}, EmberBarPath.slotOrder(EmberBarPath.LEFT));
        assertEquals(4, EmberBarPath.slotOrder(EmberBarPath.KEY5)[0]);
        assertEquals(EmberBarPath.KEY5, EmberBarPath.parse("五键"));
        assertEquals(EmberBarPath.LEFT, EmberBarPath.parse("左栏"));
    }
}
