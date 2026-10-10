package town.sunshine.corerpg.p1;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberTwistPathTest {
    @Test public void parse_keys() {
        assertEquals(EmberTwistPath.PRIM, EmberTwistPath.parse("固招"));
        assertEquals(EmberTwistPath.ALT, EmberTwistPath.parse("变招"));
        assertEquals(EmberTwistPath.ROTATE, EmberTwistPath.parse("轮换"));
        assertEquals("prim", EmberTwistPath.key(EmberTwistPath.PRIM));
        assertTrue(EmberTwistPath.tip(EmberTwistPath.ALT).contains("第二"));
    }

    @Test public void applySkills_prim_drops_alt() {
        EmberRunMaps.EliteTwists.Twist tw = twistWithAlt();
        EmberRunMaps.Skill[] sk = EmberTwistPath.applySkills(tw, 10.0, EmberTwistPath.PRIM);
        assertNotNull(sk[0]);
        assertNull(sk[1]);
    }

    @Test public void applySkills_alt_uses_second() {
        EmberRunMaps.EliteTwists.Twist tw = twistWithAlt();
        EmberRunMaps.Skill[] sk = EmberTwistPath.applySkills(tw, 10.0, EmberTwistPath.ALT);
        assertNotNull(sk[0]);
        assertNull(sk[1]);
        // alt-only: the attached skill name should be the alt move's display name
        assertEquals(tw.alt.name, sk[0].name);
    }

    @Test public void applySkills_rotate_keeps_both() {
        EmberRunMaps.EliteTwists.Twist tw = twistWithAlt();
        EmberRunMaps.Skill[] sk = EmberTwistPath.applySkills(tw, 10.0, EmberTwistPath.ROTATE);
        assertNotNull(sk[0]);
        assertNotNull(sk[1]);
        assertEquals(tw.name, sk[0].name);
        assertEquals(tw.alt.name, sk[1].name);
    }

    private static EmberRunMaps.EliteTwists.Twist twistWithAlt() {
        Map<String, Object> alt = new HashMap<String, Object>();
        alt.put("move", "stomp");
        alt.put("shape", "circle");
        alt.put("radius", 2);
        Map<String, Object> raw = new HashMap<String, Object>();
        raw.put("move", "shove");
        raw.put("shape", "line");
        raw.put("length", 5);
        raw.put("width", 2);
        raw.put("alt", alt);
        return new EmberRunMaps.EliteTwists.Twist("shove", raw);
    }
}
