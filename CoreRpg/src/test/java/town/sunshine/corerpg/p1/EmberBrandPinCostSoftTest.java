package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBrandPinCostSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        int[] a = EmberBrandPinCostSoft.apply(d, 4, 600, true);
        assertEquals(2, a[0]);
        assertEquals(300, a[1]);
        int[] b = EmberBrandPinCostSoft.apply(d, 4, 600, true);
        assertEquals(4, b[0]);
    }
}
