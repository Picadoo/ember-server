package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberConvertCostSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        int[] a = EmberConvertCostSoft.apply(d, 4, 600, true);
        assertEquals(2, a[0]);
        assertEquals(300, a[1]);
        int[] b = EmberConvertCostSoft.apply(d, 4, 600, true);
        assertEquals(4, b[0]);
    }
}
