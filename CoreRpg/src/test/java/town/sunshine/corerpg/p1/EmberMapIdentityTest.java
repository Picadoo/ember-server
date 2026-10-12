package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

public class EmberMapIdentityTest {
    @Test public void remainLine_nullMap() {
        assertTrue(EmberMapIdentity.remainLine(null, null).contains("未登记"));
    }

    @Test public void stripSection_keepsText() {
        String out = EmberSetChase.stripSection("§e成套追 · 焦点§f焚烬 §7· 缺刃");
        assertTrue(out.startsWith("§e"));
        assertTrue(out.contains("成套追"));
        assertTrue(out.contains("焚烬"));
        assertTrue(out.contains("缺刃"));
        assertFalse(out.contains("§f"));
        assertFalse(out.contains("§7"));
    }

    @Test public void focusCodexRemain_noFocus() {
        assertEquals("", EmberSetChase.focusCodexRemain(null));
    }

    @Test public void methodLineComposesWithoutGate() {
        EmberRunMaps.MapDef m = loadQ("q01");
        String line = EmberMapIdentity.methodLine(m, null);
        assertTrue(line.contains("本图方法"));
        assertTrue(line.contains("不锁通关"));
        assertTrue(EmberMapIdentity.mainlineQ(m));
        assertFalse(EmberMapIdentity.mainlineQ(loadShort("sx01")));
    }


    @Test public void menuLine_nullMap() {
        assertEquals("本图身份未登记", EmberMapIdentity.menuLine(null, null));
    }

    @Test public void menuLine_noFamily() {
        EmberRunMaps.MapDef m = new EmberRunMaps.MapDef("tmp", new java.util.HashMap<String, Object>());
        assertEquals("本图无装备偏向", EmberMapIdentity.menuLine(m, null));
    }

    @Test public void menuLine_incompleteAndComplete() {
        EmberRunMaps.MapDef m = loadQ("q01");
        assertNotNull(m.lootFamily);
        String miss = EmberMapIdentity.menuLine(m, null);
        assertTrue(miss.contains("图鉴"));
        assertTrue(miss.contains("还差"));
        assertFalse(miss.contains("不锁通关"));
        assertFalse(miss.contains("每局1装"));
        PlayerData d = new PlayerData();
        String fam = m.lootFamily;
        String dl = m.dropLabel == null ? "T1" : m.dropLabel.trim().toUpperCase();
        int tier = 1;
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Integer.parseInt(dl.substring(1, 2)); } catch (NumberFormatException ignored) {}
        }
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[]{"blade", "charm"}) {
                EmberCodex.register(d, fam, slot, t, "drop");
            }
        }
        String done = EmberMapIdentity.menuLine(m, d);
        assertTrue(done.contains("图鉴齐"));
        assertFalse(done.contains("还差"));
        assertFalse(done.contains("不锁通关"));
    }

    @Test public void remainLine_mg2Copy() {
        String s = EmberMapIdentity.remainLine(loadQ("q01"), null);
        assertTrue(s.contains("通关不锁掉落") || s.contains("进过即计"));
        assertFalse(s.contains("首通后才开身份"));
    }

    private static EmberRunMaps.MapDef loadQ(String key) {
        try (InputStream in = EmberMapIdentityTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps.MapDef m = EmberRunMaps.parse(y).byKey(key);
            assertNotNull(m);
            return m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static EmberRunMaps.MapDef loadShort(String key) {
        EmberRunMaps.MapDef m = loadQ(key);
        assertTrue("expected short expedition: " + key, m.shortExpedition);
        return m;
    }
}
