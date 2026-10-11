package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.bukkit.Particle;
import org.junit.Test;
import town.sunshine.corerpg.p1.encounter.EmberAffixTelegraph;

public class EmberAffixTelegraphTest {
    @Test
    public void blazeVsFrostDiffer() {
        assertTrue(EmberAffixTelegraph.differs("blazing", "frost"));
    }

    @Test
    public void chargeUsesCrit() {
        assertEquals(Particle.CRIT, EmberAffixTelegraph.warnParticle("charge"));
    }

    @Test
    public void unknownFallsBack() {
        assertEquals(Particle.FLAME, EmberAffixTelegraph.warnParticle("nope_xyz"));
    }
}
