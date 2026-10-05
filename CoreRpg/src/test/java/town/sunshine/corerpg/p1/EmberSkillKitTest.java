package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** D211 skill-kit S1: shape preference + resolve (signature override) without Bukkit. */
public class EmberSkillKitTest {

    private static EmberGrowth.Mods mods(Object... kv) {
        Map<String, Double> m = new HashMap<String, Double>();
        for (int i = 0; i + 1 < kv.length; i += 2) m.put(String.valueOf(kv[i]), ((Number) kv[i + 1]).doubleValue());
        return EmberGrowth.Mods.combine(Collections.singletonList(m));
    }

    @Test
    public void parseAndNames() {
        assertEquals(EmberSkillKit.SHAPE_FAN, EmberSkillKit.parseShape("fan"));
        assertEquals(EmberSkillKit.SHAPE_LINE, EmberSkillKit.parseShape("直线"));
        assertEquals(EmberSkillKit.SHAPE_RING, EmberSkillKit.parseShape("ring"));
        assertEquals(-1, EmberSkillKit.parseShape("nope"));
        assertEquals("扇形", EmberSkillKit.shapeName(0));
        assertEquals("line", EmberSkillKit.shapeKey(1));
    }

    @Test
    public void setShapeRoundTrip() {
        PlayerData d = new PlayerData();
        assertEquals(0, d.periodCount(EmberSkillKit.C_SHAPE, "all"));
        assertTrue(EmberSkillKit.setShape(d, EmberSkillKit.SHAPE_LINE));
        assertEquals(1, d.periodCount(EmberSkillKit.C_SHAPE, "all"));
        assertFalse(EmberSkillKit.setShape(d, EmberSkillKit.SHAPE_LINE));
        assertTrue(EmberSkillKit.setShape(d, EmberSkillKit.SHAPE_RING));
        assertEquals(2, d.periodCount(EmberSkillKit.C_SHAPE, "all"));
        assertTrue(EmberSkillKit.setShape(d, EmberSkillKit.SHAPE_FAN));
        assertEquals(0, d.periodCount(EmberSkillKit.C_SHAPE, "all"));
    }

    @Test
    public void resolveFanDefault() {
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, null, null, null, EmberGrowth.Mods.NONE);
        assertEquals(EmberSkillKit.SHAPE_FAN, sh.id);
        assertEquals(3.5, sh.range, 1e-9);
        assertEquals(100.0, sh.arc, 1e-9);
        assertEquals(0, sh.line, 1e-9);
        assertEquals(5, sh.maxTargets);
        assertFalse(sh.sigOverride);
    }

    @Test
    public void lockedShapeIgnoresStoredPreference() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberSkillKit.C_SHAPE, "all", EmberSkillKit.SHAPE_LINE);
        // no EmberRunService → shapeUnlocked false → fan numbers
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, d, null, null, EmberGrowth.Mods.NONE);
        assertEquals(EmberSkillKit.SHAPE_FAN, sh.id);
        assertEquals(5, sh.maxTargets);
        assertEquals(0, sh.line, 1e-9);
    }

    @Test
    public void signatureL07OverridesRune() {
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(EmberSignature.byId("L07").mods));
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, null, null, null, m);
        assertTrue(sh.sigOverride);
        assertEquals(5.0, sh.line, 1e-9);
        assertEquals(3, sh.maxTargets);
        assertTrue(sh.label.contains("签名"));
    }

    @Test
    public void signatureL09OverridesRune() {
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(EmberSignature.byId("L09").mods));
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, null, null, null, m);
        assertTrue(sh.sigOverride);
        assertEquals(360.0, sh.arc, 1e-9);
        assertEquals(3.0, sh.range, 1e-9);
        assertEquals(3, sh.maxTargets);
    }

    @Test
    public void l08IgniteDoesNotOverrideShape() {
        // L08 has skill_var + ignite but no line/ring/plus/charge → rune (fan when locked) still applies
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(EmberSignature.byId("L08").mods));
        EmberSkillKit.Shape sh = EmberSkillKit.resolve(null, null, null, null, m);
        assertFalse(sh.sigOverride);
        assertEquals(EmberSkillKit.SHAPE_FAN, sh.id);
        assertEquals(5, sh.maxTargets);
    }

    @Test
    public void dashConstantsMatchDesign() {
        assertEquals(4.0, EmberSkillKit.DASH_DISTANCE, 1e-9);
        assertEquals(3, EmberSkillKit.DASH_MAX_TARGETS);
        assertEquals(0.5, EmberSkillKit.DASH_BOSS_MULT, 1e-9);
        assertEquals("q02", EmberSkillKit.UNLOCK_DASH);
        assertEquals("q04", EmberSkillKit.UNLOCK_SHAPE);
    }

    @Test
    public void counterFamilyRegistered() {
        EmberCounters.Family f = EmberCounters.lookup(EmberSkillKit.C_SHAPE);
        assertNotNull(f);
        assertEquals(EmberCounters.Category.SETTING, f.category);
    }
}
