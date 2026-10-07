package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.encounter.EmberEventTeach;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** D296: account first-of-kind teach flash + open HUD short name + event_rate gate. */
public class EmberEventTeachTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberEventTeachTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void teachFlash_D296_section34Verbs() {
        assertTrue(EmberEventTeach.teachFlash("timed").contains("限时清房"));
        assertTrue(EmberEventTeach.teachFlash("crystal").contains("砸余烬晶"));
        assertTrue(EmberEventTeach.teachFlash("escort").contains("护住宝兔"));
        assertTrue(EmberEventTeach.teachFlash("hold").contains("占住光圈"));
        assertTrue(EmberEventTeach.teachFlash("beacon").contains("护住灯柱"));
        assertTrue(EmberEventTeach.teachFlash("relay").contains("按序传火"));
        assertTrue(EmberEventTeach.teachFlash("breach").contains("裂隙"));
        assertTrue(EmberEventTeach.teachFlash("chain").contains("连斩别断"));
        assertTrue(EmberEventTeach.teachFlash("unscathed").contains("少挨打"));
        assertEquals("", EmberEventTeach.teachFlash(null));
        assertEquals("", EmberEventTeach.teachFlash("nope"));
    }

    @Test public void openHud_D296_usesEventLabel() {
        assertTrue(EmberEventTeach.openHud("crystal").contains("砸余烬晶"));
        assertTrue(EmberEventTeach.openHud("breach").contains("裂隙"));
        assertTrue(EmberEventTeach.openHud("").contains("限时清房"));
    }

    @Test public void tryMarkTeach_oncePerKind() {
        PlayerData d = new PlayerData();
        assertTrue(EmberEventTeach.tryMarkTeach(d, "crystal"));
        assertFalse(EmberEventTeach.tryMarkTeach(d, "crystal"));
        assertTrue(EmberEventTeach.tryMarkTeach(d, "breach"));
        assertFalse(EmberEventTeach.tryMarkTeach(d, "nope"));
        assertFalse(EmberEventTeach.tryMarkTeach(null, "crystal"));
        assertEquals(1, d.periodCount(EmberEventTeach.C_TEACH + "crystal", "all"));
    }

    @Test public void bundledEventRate_D296_monteCarlo() {
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(0.85, v.eventRate, 1e-9);
        assertEquals(1, v.eventCore); // must not raise core count
        int events = 0;
        final int N = 4000;
        for (long seed = 0; seed < N; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            if (!a[2].isEmpty()) events++;
        }
        double rate = events / (double) N;
        assertTrue("event roll rate " + rate + " not in [0.80, 0.90]", rate >= 0.80 && rate <= 0.90);
    }
}
