package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Boss / caster telegraph shapes: the damage area never exceeds the drawn warning (book ch. 11–13 §7). */
public class EmberRunShapeTest {

    private static EmberRunMaps.Skill skill(Object... kv) {
        Map<String, Object> m = new HashMap<String, Object>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return new EmberRunMaps.Skill(m);
    }

    private static final Location O = new Location(null, 0, 65, 0);
    private static final Vector NORTH = new Vector(0, 0, 1);

    private static boolean hit(EmberRunMaps.Skill s, double x, double z) {
        return EmberRunDirector.inShape(s, O, NORTH, new Location(null, x, 65, z));
    }

    @Test public void q01CleaveIs100DegreesFourBlocks() {
        EmberRunMaps.Skill s = skill("type", "cone", "angle", 100, "range", 4);
        assertTrue(hit(s, 0, 3.9));
        assertTrue(hit(s, Math.sin(Math.toRadians(49)) * 3, Math.cos(Math.toRadians(49)) * 3));
        assertFalse(hit(s, Math.sin(Math.toRadians(52)) * 3, Math.cos(Math.toRadians(52)) * 3));
        assertFalse(hit(s, 0, 4.2));
        assertFalse(hit(s, 0, -2));
        assertFalse(EmberRunDirector.inShape(s, O, NORTH, new Location(null, 0, 68, 2))); // 3 blocks above
    }

    @Test public void q02SmashIsRadiusThreeTwoAhead() {
        EmberRunMaps.Skill s = skill("type", "circle", "radius", 3, "ahead", 2);
        assertTrue(hit(s, 0, 4.9));
        assertTrue(hit(s, 0, -0.9));
        assertFalse(hit(s, 0, 5.2));
        assertFalse(hit(s, 3.1, 2));
    }

    @Test public void q03LineIsFiveByThree() {
        EmberRunMaps.Skill s = skill("type", "line", "length", 5, "width", 3);
        assertTrue(hit(s, 1.4, 4.9));
        assertFalse(hit(s, 1.6, 2));
        assertFalse(hit(s, 0, 5.2));
        assertFalse(hit(s, 0, -1));
        EmberRunMaps.Skill caster = skill("type", "line", "length", 6, "width", 1.5);
        assertTrue(hit(caster, 0.7, 5.9));
        assertFalse(hit(caster, 0.8, 3));
    }
}
