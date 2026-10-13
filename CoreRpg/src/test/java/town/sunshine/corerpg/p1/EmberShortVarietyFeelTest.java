package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * TIP-CP D13321: short light variety — eligible / HUD copy / settle zero variety economy.
 */
public class EmberShortVarietyFeelTest {

    @Test
    public void varietyEligible_shortAfterFirstClearGate_plainOrShort() {
        assertTrue(EmberShortVarietyFeel.varietyEligible(false, 0, false, false, true, true));
        assertTrue(EmberShortVarietyFeel.varietyEligible(false, 0, false, false, false, true));
        assertFalse(EmberShortVarietyFeel.varietyEligible(true, 0, false, false, true, true));
        assertFalse(EmberShortVarietyFeel.varietyEligible(false, 2, false, false, true, true));
        assertFalse(EmberShortVarietyFeel.varietyEligible(false, 0, true, false, true, true));
        assertFalse(EmberShortVarietyFeel.varietyEligible(false, 0, false, true, true, true));
        assertFalse(EmberShortVarietyFeel.varietyEligible(false, 0, false, false, true, false));
    }

    @Test
    public void sxMaps_passEligible_whenVarietyOn() {
        EmberRunMaps maps = maps();
        assertTrue(maps.variety.on());
        EmberRunMaps.MapDef sx01 = maps.byKey("sx01");
        EmberRunMaps.MapDef sx12 = maps.byKey("sx12");
        assertNotNull(sx01);
        assertNotNull(sx12);
        assertTrue(sx01.shortExpedition);
        assertTrue(EmberSessionService.varietyEligible(false, 0, sx01.raid, sx01.rush,
                sx01.shortExpedition, maps.variety.on()));
        assertTrue(EmberSessionService.varietyEligible(false, 0, sx12.raid, sx12.rush,
                sx12.shortExpedition, maps.variety.on()));
        // first clear still gated at call site (predicate alone is not enough)
        assertFalse(EmberFirstClear.paid(null, "sx01", "v1"));
    }

    @Test
    public void hudCopy_affixAndEvent() {
        assertTrue(EmberShortVarietyFeel.AFFIX_TELL.contains("短征花样"));
        assertTrue(EmberShortVarietyFeel.AFFIX_TELL.contains("词缀精英"));
        assertTrue(EmberShortVarietyFeel.EVENT_TELL.contains("短征花样"));
        assertTrue(EmberShortVarietyFeel.EVENT_TELL.contains("房间事件"));
        assertEquals("§6短征花样 · §f炽热", EmberShortVarietyFeel.affixAb("炽热"));
        assertEquals("§6短征花样 · §f限时清房", EmberShortVarietyFeel.eventAb("限时清房"));
        String how = EmberShortVarietyFeel.affixHowTell("炽热", "站远打");
        assertTrue(how.contains("炽热"));
        assertFalse(how.contains("余烬碎片"));
        assertFalse(how.contains("核心"));
    }

    @Test
    public void settleGrants_neverVarietyEconomy() {
        // rewarded + unpaid FC
        List<EmberRunRules.Grant> g1 = EmberShortRules.settleGrants("sx01", 0, 3, false);
        assertFalse(g1.isEmpty());
        for (EmberRunRules.Grant g : g1) {
            assertFalse("unexpected variety key " + g.key, EmberShortVarietyFeel.isVarietyEconomyKey(g.key));
        }
        // practice (daycap full)
        List<EmberRunRules.Grant> g2 = EmberShortRules.settleGrants("sx07", 3, 3, true);
        for (EmberRunRules.Grant g : g2) {
            assertFalse(EmberShortVarietyFeel.isVarietyEconomyKey(g.key));
        }
        // varietyGrants WOULD emit keys — pin detector
        List<EmberRunRules.Grant> vg = EmberRunRules.varietyGrants(false, true, 2, true, 1, "timed");
        boolean saw = false;
        for (EmberRunRules.Grant g : vg) {
            if (EmberShortVarietyFeel.isVarietyEconomyKey(g.key)) saw = true;
        }
        assertTrue(saw);
    }

    @Test
    public void roll_reusesPool_zeroYamlChange() {
        EmberRunMaps maps = maps();
        // event_rate / affix pool unchanged — just smoke that roll returns 4-tuple for short seed space
        String[] v = maps.variety.roll(42L);
        assertEquals(4, v.length);
        assertNotNull(v[0]);
        assertNotNull(v[1]);
        assertNotNull(v[2]);
        assertNotNull(v[3]);
    }

    private static EmberRunMaps maps() {
        try (InputStream in = EmberShortVarietyFeelTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            return EmberRunMaps.parse(y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
