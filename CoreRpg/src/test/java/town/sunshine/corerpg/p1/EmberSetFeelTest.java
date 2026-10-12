package town.sunshine.corerpg.p1;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSetFeelTest {
    @After
    public void resetFlag() {
        EmberSetFeel.setAwkDeltaEnabledForTest(null);
    }

    @Test public void runLine_set_and_4pc() {
        assertEquals("", EmberSetFeel.runLine("none", 1, 0, false));
        String s = EmberSetFeel.runLine("scorch", 1, 1, false);
        assertTrue(s.contains("本局套装"));
        assertTrue(s.contains("焚烬"));
        assertTrue(s.contains("点燃"));
        assertTrue(s.contains("1/2"));
        String on = EmberSetFeel.runLine("burst", 2, 2, true);
        assertTrue(on.contains("烬爆"));
        assertTrue(on.contains("四件套受伤"));
        assertTrue(on.contains("觉醒II"));
    }

    @Test public void awkDelta_scorch_rises() {
        String i2 = EmberSetFeel.awkDeltaLine("scorch", 1, 2);
        String i3 = EmberSetFeel.awkDeltaLine("scorch", 2, 3);
        assertTrue(i2.contains("燃跳更烫"));
        assertTrue(i3.contains("燃跳更烫"));
        assertEquals(i2, EmberSetFeel.awkDeltaLine("焚烬", 1, 2));
        assertFalse(i2.contains("0."));
    }

    @Test public void awkDelta_burst_radius_only_ii_to_iii() {
        String i2 = EmberSetFeel.awkDeltaLine("burst", 1, 2);
        String i3 = EmberSetFeel.awkDeltaLine("burst", 2, 3);
        assertTrue(i2.contains("圈伤更厚"));
        assertFalse(i2.contains("半径"));
        assertTrue(i3.contains("圈伤更厚"));
        assertTrue(i3.contains("半径略扩"));
        assertEquals(i2, EmberSetFeel.awkDeltaLine("烬爆", 1, 2));
        assertFalse(i2.contains("0."));
        assertFalse(i3.contains("0."));
    }

    @Test public void awkDelta_sustain_rises() {
        String i2 = EmberSetFeel.awkDeltaLine("sustain", 1, 2);
        String i3 = EmberSetFeel.awkDeltaLine("sustain", 2, 3);
        assertTrue(i2.contains("回血略增"));
        assertTrue(i3.contains("回血略增"));
        assertEquals(i2, EmberSetFeel.awkDeltaLine("炽愈", 1, 2));
    }

    @Test public void awkDelta_empty_when_no_rise_or_none() {
        assertEquals("", EmberSetFeel.awkDeltaLine("scorch", 2, 2));
        assertEquals("", EmberSetFeel.awkDeltaLine("scorch", 3, 2));
        assertEquals("", EmberSetFeel.awkDeltaLine("none", 1, 2));
        assertEquals("", EmberSetFeel.awkDeltaLine("", 1, 2));
        assertEquals("", EmberSetFeel.awkDeltaLine(null, 1, 2));
        assertEquals("", EmberSetFeel.awkDeltaLine("unknown", 1, 2));
    }

    @Test public void setRules_arrays_unchanged() {
        assertArrayEquals(new double[] {0, 0.26, 0.34, 0.42}, EmberSetRules.BURN_COEF, 1e-12);
        assertArrayEquals(new double[] {0, 0.65, 0.80, 0.95}, EmberSetRules.BURST_COEF, 1e-12);
        assertArrayEquals(new double[] {0, 3.0, 3.0, 3.5}, EmberSetRules.BURST_RADIUS, 1e-12);
        assertArrayEquals(new double[] {0, 0.025, 0.0325, 0.04}, EmberSetRules.SUSTAIN_PCT, 1e-12);
        assertEquals(3, EmberSetRules.SCORCH_EVERY);
        assertEquals(5, EmberSetRules.BURST_EVERY);
        assertEquals(5, EmberSetRules.SUSTAIN_EVERY);
        assertEquals(3000L, EmberSetRules.BURST_ICD_MS);
        assertEquals(6000L, EmberSetRules.SUSTAIN_ICD_MS);
    }

    @Test public void awkDeltaEnabled_defaultTrue_and_testHook() {
        EmberSetFeel.setAwkDeltaEnabledForTest(null);
        assertTrue(EmberSetFeel.awkDeltaEnabled());
        EmberSetFeel.setAwkDeltaEnabledForTest(Boolean.FALSE);
        assertFalse(EmberSetFeel.awkDeltaEnabled());
        EmberSetFeel.setAwkDeltaEnabledForTest(Boolean.TRUE);
        assertTrue(EmberSetFeel.awkDeltaEnabled());
    }
}
