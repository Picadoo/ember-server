package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberVarietyBountyRewardBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(10, EmberVarietyBountyRewardBoost.applyAmount(d, 5, true));
        assertEquals(5, EmberVarietyBountyRewardBoost.applyAmount(d, 5, true));
    }
}
