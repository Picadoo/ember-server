package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.bukkit.Particle;
import org.junit.Test;

public class EmberMeleeFeelTest {
    @Test
    public void setSparksDiffer() {
        assertEquals(Particle.FLAME, EmberMeleeFeel.sparkParticle("scorch"));
        assertEquals(Particle.CRIT, EmberMeleeFeel.sparkParticle("burst"));
        assertEquals(Particle.HEART, EmberMeleeFeel.sparkParticle("sustain"));
        assertEquals(Particle.CRIT_MAGIC, EmberMeleeFeel.sparkParticle("none"));
    }

    @Test
    public void critMoreSparks() {
        assertTrue(EmberMeleeFeel.sparkCount(true) > EmberMeleeFeel.sparkCount(false));
    }
}
