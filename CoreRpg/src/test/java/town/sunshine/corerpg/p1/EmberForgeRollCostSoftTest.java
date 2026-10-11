package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
public class EmberForgeRollCostSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(4, EmberForgeRollCostSoft.softMarks(d, 8, true));
        assertEquals(8, EmberForgeRollCostSoft.softMarks(d, 8, true));
        Cost c = new Cost(0, 0, 4, 0, 500);
        Cost s = EmberForgeRollCostSoft.softCost(c);
        assertEquals(2, s.blanks);
        assertEquals(250, s.coins);
    }
}
