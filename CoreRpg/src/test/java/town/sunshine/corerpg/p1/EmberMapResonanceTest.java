package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberMapResonanceTest {
    @Test public void baseMap() {
        assertEquals("q01", EmberMapResonance.baseMap("q01"));
        assertEquals("q01", EmberMapResonance.baseMap("q01c"));
        assertEquals("q07", EmberMapResonance.baseMap("q07a2"));
        assertEquals("", EmberMapResonance.baseMap(null));
    }
    @Test public void matchOrigin() {
        EmberItemData.Origin o = EmberItemData.Origin.of("q04", "S01", "run1", 1L);
        assertTrue(EmberMapResonance.matches(o, "q04"));
        assertTrue(EmberMapResonance.matches(o, "q04c"));
        assertFalse(EmberMapResonance.matches(o, "q05"));
        assertFalse(EmberMapResonance.matches(EmberItemData.Origin.NONE, "q04"));
    }
    @Test public void geomResonanceWidensFan() {
        EmberSkillKit.Shape a = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, false);
        EmberSkillKit.Shape b = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, true);
        assertTrue(b.arc > a.arc);
    }
}
