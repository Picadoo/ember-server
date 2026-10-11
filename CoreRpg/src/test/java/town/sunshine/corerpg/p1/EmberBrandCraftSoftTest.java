package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBrandCraftSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(20, EmberBrandCraftSoft.apply(d, 40, true));
        assertEquals(40, EmberBrandCraftSoft.apply(d, 40, true));
    }
}
