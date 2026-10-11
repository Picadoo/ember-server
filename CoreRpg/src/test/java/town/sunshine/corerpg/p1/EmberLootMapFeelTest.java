package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.bukkit.Particle;
import org.junit.Test;
public class EmberLootMapFeelTest {
    @Test public void mapsDiffer() {
        assertNotEquals(EmberLootMapFeel.particleForMap("q01"), EmberLootMapFeel.particleForMap("q04"));
        assertNotEquals(EmberLootMapFeel.particleForMap("q05"), EmberLootMapFeel.particleForMap("q07"));
        assertEquals(Particle.FLAME, EmberLootMapFeel.particleForMap("q04"));
    }
    @Test public void pitchSpread() {
        assertTrue(EmberLootMapFeel.pitch("q01") < EmberLootMapFeel.pitch("q06"));
    }
    @Test public void mapLabels() {
        assertEquals("符文·试炼", EmberLootMapFeel.mapLabel("q04"));
        assertNotNull(EmberLootMapFeel.mapLabel("q01"));
    }
}

