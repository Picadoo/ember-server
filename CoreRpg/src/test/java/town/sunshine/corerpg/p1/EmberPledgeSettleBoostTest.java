package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPledgeSettleBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(3, EmberPledgeSettleBoost.apply(d, 2));
        assertEquals(2, EmberPledgeSettleBoost.apply(d, 2));
    }
}
