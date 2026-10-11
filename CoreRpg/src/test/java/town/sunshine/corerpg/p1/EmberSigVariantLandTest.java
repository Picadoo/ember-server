package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberSigVariantLandTest {
    @Test
    public void chargeSlowSync() {
        assertEquals(16, EmberSkillKit.chargeLandSlowTicks(false));
        assertEquals(20, EmberSkillKit.chargeLandSlowTicks(true));
    }

    @Test
    public void shieldResistSync() {
        assertEquals(10, EmberSkillKit.shieldLandResistTicks(false));
        assertEquals(16, EmberSkillKit.shieldLandResistTicks(true));
    }
}
