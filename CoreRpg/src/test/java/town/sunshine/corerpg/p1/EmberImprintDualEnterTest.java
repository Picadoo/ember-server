package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberImprintDualEnterTest {
    @Test public void latchKeys() {
        assertEquals("p1_imprint_enter", EmberSkillKit.C_IMPRINT_ENTER);
        assertEquals("p1_dual_enter", EmberSkillKit.C_DUAL_ENTER);
    }
    @Test public void markNullSafe() {
        assertFalse(EmberSkillKit.markImprintEnter(null));
        assertFalse(EmberSkillKit.markDualEnter(null));
        assertFalse(EmberSkillKit.imprintFeatureUnlocked(null, null));
        assertFalse(EmberSkillKit.dualUnlocked(null, null));
    }
}
