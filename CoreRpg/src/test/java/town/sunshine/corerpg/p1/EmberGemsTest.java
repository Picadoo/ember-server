package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGemsTest {
    private static EmberItemData blade(int tier, int s0, int s1) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId("burst", "blade", tier), "burst", "blade", tier, 0, 0, 5, 0, false, "drop", EmberItemData.DATA_VERSION, 0,
                0, 0, 0, 0, EmberItemData.Origin.NONE, s0, s1);
    }
    private static EmberItemData charm(int s0) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_charm_t1", "burst", "charm", 1, 0, 0, 3, 0, false, "drop", EmberItemData.DATA_VERSION, 0,
                0, 0, 0, 0, EmberItemData.Origin.NONE, s0, 0);
    }
    @Test public void empty_zero() {
        assertEquals(0.0, EmberGems.flatA(null, null), 0);
        assertEquals(0.0, EmberGems.flatD(null, null), 0);
    }
    @Test public void sharp_t1_no_combat() {
        assertEquals(0.0, EmberGems.flatA(blade(1, 1, 1), null), 0.001);
    }
    @Test public void sharp_t2_cap() {
        assertEquals(0.2, EmberGems.flatA(blade(2, 1, 1), null), 0.001);
    }
    @Test public void steady_tiny() {
        assertEquals(0.1, EmberGems.flatD(null, charm(2)), 0.001);
    }
    @Test public void drain_gale() {
        assertEquals(0.02, EmberGems.lifeStealPct(blade(2, 3, 3), null), 0.0001);
        assertEquals(0.04, EmberGems.moveSpeedPct(blade(2, 4, 4), null), 0.0001);
    }
}
