package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.bukkit.Particle;
import org.junit.Test;

import town.sunshine.corerpg.p1.encounter.EmberTelegraph;

public class EmberBossTelegraphTest {
    @Test
    public void urgencyRamp() {
        assertEquals(0.0, EmberTelegraph.urgency(0, 2000, 2000), 1e-9);
        assertEquals(1.0, EmberTelegraph.urgency(2000, 2000, 2000), 1e-9);
        assertEquals(0.5, EmberTelegraph.urgency(1000, 2000, 2000), 1e-9);
    }

    @Test
    public void hotDensity() {
        assertEquals(1, EmberTelegraph.densityMult(0.3));
        assertEquals(2, EmberTelegraph.densityMult(0.75));
        assertTrue(EmberTelegraph.hot(0.7));
        assertFalse(EmberTelegraph.hot(0.69));
    }

    @Test
    public void shapeParticlesDiffer() {
        assertEquals(Particle.CRIT, EmberTelegraph.outlineParticle("line", false, 0.0));
        assertEquals(Particle.SPELL_WITCH, EmberTelegraph.outlineParticle("fan", false, 0.0));
        assertEquals(Particle.VILLAGER_HAPPY, EmberTelegraph.outlineParticle("circle", true, 0.9));
        assertEquals(Particle.LAVA, EmberTelegraph.outlineParticle("circle", false, 0.9));
    }

    @Test
    public void insideSlowGate() {
        assertFalse(EmberTelegraph.shouldInsideSlow(0.2, true));
        assertTrue(EmberTelegraph.shouldInsideSlow(0.5, true));
        assertFalse(EmberTelegraph.shouldInsideSlow(0.9, false));
    }

    @Test
    public void actionBarCopy() {
        String in = EmberTelegraph.insideActionBar(true, 2, "圆");
        assertTrue(in.contains("危险"));
        assertTrue(in.contains("2s"));
        String out = EmberTelegraph.insideActionBar(false, 3, "冲撞");
        assertTrue(out.contains("安全"));
    }
}
