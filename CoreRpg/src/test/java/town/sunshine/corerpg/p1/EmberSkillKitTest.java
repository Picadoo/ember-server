package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** D211/D214 skill-kit S1+S2: shape preference + 火痕步 gates without Bukkit. */
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

    @Test
    public void huohenConstantsMatchDesign() {
        assertEquals("q05", EmberSkillKit.UNLOCK_STEP);
        assertEquals("scorch", EmberSkillKit.HUOHEN_FAMILY);
        assertEquals(1, EmberSkillKit.STEP_IGNITE_N);
        assertEquals(1.0, EmberSkillKit.STEP_BURN_MULT, 1e-9);
        assertEquals(3.0, EmberSkillKit.STEP_IGNITE_RADIUS, 1e-9);
        assertEquals("火痕步", EmberSkillKit.stepDisplayName(true));
        assertEquals("踏步", EmberSkillKit.stepDisplayName(false));
    }

    @Test
    public void huohenRequiresUnlockAndScorch() {
        // no runs → unlock false
        assertFalse(EmberSkillKit.huohenActive(new PlayerData(), null, "scorch"));
        assertFalse(EmberSkillKit.stepVariantUnlocked(new PlayerData(), null));
        // family mismatch even if somehow unlocked without runs stays false
        assertFalse(EmberSkillKit.huohenActive(new PlayerData(), null, "burst"));
        assertFalse(EmberSkillKit.huohenActive(new PlayerData(), null, "none"));
    }

    @Test
    public void backstepConstantsMatchDesign() {
        assertEquals(4.0, EmberSkillKit.BACKSTEP_DISTANCE, 1e-9);
        assertEquals(0, EmberSkillKit.DIR_FORWARD);
        assertEquals(1, EmberSkillKit.DIR_BACK);
        assertEquals("后撤步", EmberSkillKit.stepDisplayName(false, true));
        assertEquals("火痕·后撤", EmberSkillKit.stepDisplayName(true, true));
        assertEquals("踏步", EmberSkillKit.stepDisplayName(false, false));
        assertEquals("火痕步", EmberSkillKit.stepDisplayName(true, false));
    }

    @Test
    public void parseStepDirAndNames() {
        assertEquals(EmberSkillKit.DIR_FORWARD, EmberSkillKit.parseStepDir("forward"));
        assertEquals(EmberSkillKit.DIR_FORWARD, EmberSkillKit.parseStepDir("前冲"));
        assertEquals(EmberSkillKit.DIR_BACK, EmberSkillKit.parseStepDir("back"));
        assertEquals(EmberSkillKit.DIR_BACK, EmberSkillKit.parseStepDir("后撤"));
        assertEquals(EmberSkillKit.DIR_BACK, EmberSkillKit.parseStepDir("后撤步"));
        assertEquals(-1, EmberSkillKit.parseStepDir("sideways"));
        assertEquals("前冲", EmberSkillKit.stepDirName(0));
        assertEquals("后撤", EmberSkillKit.stepDirName(1));
        assertEquals("back", EmberSkillKit.stepDirKey(1));
    }

    @Test
    public void setStepDirRoundTrip() {
        PlayerData d = new PlayerData();
        assertEquals(EmberSkillKit.DIR_FORWARD, EmberSkillKit.stepDirId(d));
        assertFalse(EmberSkillKit.stepBackward(d));
        assertTrue(EmberSkillKit.setStepDir(d, EmberSkillKit.DIR_BACK));
        assertEquals(1, d.periodCount(EmberSkillKit.C_STEP_DIR, "all"));
        assertTrue(EmberSkillKit.stepBackward(d));
        assertFalse(EmberSkillKit.setStepDir(d, EmberSkillKit.DIR_BACK));
        assertTrue(EmberSkillKit.setStepDir(d, EmberSkillKit.DIR_FORWARD));
        assertEquals(0, d.periodCount(EmberSkillKit.C_STEP_DIR, "all"));
        assertFalse(EmberSkillKit.stepBackward(d));
    }

    @Test
    public void stepDirCounterFamilyRegistered() {
        EmberCounters.Family f = EmberCounters.lookup(EmberSkillKit.C_STEP_DIR);
        assertNotNull(f);
        assertEquals(EmberCounters.Category.SETTING, f.category);
    }

    @Test
    public void parryConstantsMatchT0b() {
        assertEquals("q03", EmberSkillKit.UNLOCK_PARRY);
        assertEquals(28, EmberSkillKit.PARRY_CD_SECONDS);
        assertEquals(28_000L, EmberSkillKit.PARRY_CD_MS);
        assertEquals(0.45, EmberSkillKit.PARRY_FLAT_MULT, 1e-9);
        assertEquals("余烬招架", EmberSkillKit.DISPLAY_PARRY);
        assertTrue(EmberSkillKit.PARRY_WINDOW_MS >= 350L);
        assertTrue(EmberSkillKit.PARRY_WINDOW_MS <= 550L);
    }

    @Test
    public void parryFlatAndWindowHelpers() {
        assertEquals(45.0, EmberSkillKit.parryFlat(100.0), 1e-9);
        assertEquals(0.0, EmberSkillKit.parryFlat(0.0), 1e-9);
        assertEquals(0.0, EmberSkillKit.parryFlat(-5.0), 1e-9);
        assertTrue(EmberSkillKit.parryWindowOpen(1000L, 1450L));
        assertTrue(EmberSkillKit.parryWindowOpen(1450L, 1450L));
        assertFalse(EmberSkillKit.parryWindowOpen(1451L, 1450L));
        assertFalse(EmberSkillKit.parryWindowOpen(1000L, 0L));
    }

    @Test
    public void parryUnlockRequiresRuns() {
        assertFalse(EmberSkillKit.parryUnlocked(new PlayerData(), null));
        assertFalse(EmberSkillKit.parryUnlocked(null, null));
    }


    @Test
    public void d434SetStepVariants() {
        assertEquals("爆闪步", EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_BAOSHAN, false));
        assertEquals("爆闪·后撤", EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_BAOSHAN, true));
        assertEquals("承护步", EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_CHENGHU, false));
        assertEquals("承护·后撤", EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_CHENGHU, true));
        assertEquals(EmberSkillKit.STEP_VARIANT_PLAIN, EmberSkillKit.stepSetVariant(new PlayerData(), null, "burst"));
        assertFalse(EmberSkillKit.baoshanActive(new PlayerData(), null, "burst"));
        assertFalse(EmberSkillKit.chenghuActive(new PlayerData(), null, "sustain"));
        assertEquals(30, EmberSkillKit.STEP_SLOW_TICKS);
        assertEquals(40, EmberSkillKit.STEP_RESIST_TICKS);
    }
}
