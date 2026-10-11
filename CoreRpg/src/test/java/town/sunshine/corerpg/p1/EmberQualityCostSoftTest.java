package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
public class EmberQualityCostSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        Cost c = new Cost(0, 0, 2, 1, 100);
        Cost s = EmberQualityCostSoft.apply(d, c, true);
        assertEquals(1, s.blanks);
        assertEquals(50, s.coins);
        assertEquals(c, EmberQualityCostSoft.apply(d, c, true));
    }
}
