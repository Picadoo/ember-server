package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGemsTest {
    private static EmberItemData blade(int s0, int s1) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t1", "burst", "blade", 1, 0, 0, 5, 0, false, "drop", EmberItemData.DATA_VERSION, 0,
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
    @Test public void sharp_cap_two() {
        assertEquals(0.5, EmberGems.flatA(blade(1, 1), null), 0.001);
    }
    @Test public void steady_on_charm() {
        assertEquals(0.25, EmberGems.flatD(null, charm(2)), 0.001);
    }
    @Test public void full_and_ls_ms() {
        EmberItemData b = blade(1, 1);
        EmberItemData c = charm(2);
        assertEquals(0.5, EmberGems.flatA(b, c), 0.001);
        assertEquals(0.25, EmberGems.flatD(b, c), 0.001);
        assertEquals(0.02, EmberGems.lifeStealPct(blade(3, 3), null), 0.0001);
        assertEquals(0.04, EmberGems.moveSpeedPct(blade(4, 4), null), 0.0001);
        assertEquals(0.06, EmberGems.moveSpeedPct(blade(4, 4), charm(4)), 0.0001); // 3 holes max
    }
    @Test public void formula_adds_flat_outside_bracket() {
        EmberTables t = EmberTables.defaults();
        double base = EmberFormula.baseAttack(t, 0, 0, 0, 0, 10);
        double with = EmberFormula.baseAttack(t, 0, 0, 0, 0, 10) + EmberGems.flatA(blade(1, 1), null);
        assertEquals(base + 0.5, with, 0.001);
    }
}
