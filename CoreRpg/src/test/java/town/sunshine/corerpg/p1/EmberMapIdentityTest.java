package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
