package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberAfkGearEffTest {
    @Test public void band_ok_marginal_weak_D454() {
        assertEquals("够稳", EmberAfkGearEff.band(30, 85, 2));
        assertEquals("够稳", EmberAfkGearEff.band(29, 81, 2)); // ~0.95
        assertEquals("勉强", EmberAfkGearEff.band(22, 70, 2));
        assertEquals("偏弱", EmberAfkGearEff.band(15, 40, 2));
        assertEquals("空", EmberAfkGearEff.band(99, 99, 0));
    }
    @Test public void line_contains_vs_D454() {
        String s = EmberAfkGearEff.line(47, 125, 3);
        assertTrue(s.contains("vs 推荐"));
        assertTrue(s.contains("攻50"));
    }
}
