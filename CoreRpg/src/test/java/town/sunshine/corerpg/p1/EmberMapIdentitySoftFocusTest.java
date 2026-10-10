package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.PlayerData;

/** D2208: soft-focus pure gates + identity complete. */
public class EmberMapIdentitySoftFocusTest {

    @Test
    public void softLatchKey() {
        assertEquals("p1_map_id_soft_", EmberMapIdentity.C_SOFT);
    }

    @Test
    public void maybeSoftFocusSkipsWhenFocusSet() {
        PlayerData d = new PlayerData();
        EmberSetFocus.set(d, EmberSetFocus.SCORCH);
        assertFalse(EmberMapIdentity.maybeSoftFocusAfterCodex(null, d, "burst", null));
        assertEquals(EmberSetFocus.SCORCH, EmberSetFocus.get(d));
    }

    @Test
    public void identityCompleteAfterCodexFill() throws Exception {
        EmberRunMaps maps = loadMaps();
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        assertNotNull(q01);
        assertNotNull(q01.lootFamily);
        PlayerData d = new PlayerData();
        assertFalse(EmberMapIdentity.identityComplete(q01, d));
        int[] hn = EmberMapIdentity.haveNeed(q01, d);
        assertTrue(hn[1] > 0);
        // fill all identity keys for this map
        String fam = q01.lootFamily;
        String dl = q01.dropLabel == null ? "T1" : q01.dropLabel.trim().toUpperCase();
        int tier = 1;
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Integer.parseInt(dl.substring(1, 2)); } catch (NumberFormatException ignored) {}
        }
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[]{"blade", "charm"}) {
                EmberCodex.register(d, fam, slot, t, "drop");
            }
        }
        assertTrue(EmberMapIdentity.identityComplete(q01, d));
        // no player → soft focus returns false but would have passed gates except player null
        assertFalse(EmberMapIdentity.maybeSoftFocusAfterCodex(null, d, fam, maps));
    }

    private static EmberRunMaps loadMaps() throws Exception {
        try (InputStream in = EmberMapIdentitySoftFocusTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            return EmberRunMaps.parse(y);
        }
    }
}
