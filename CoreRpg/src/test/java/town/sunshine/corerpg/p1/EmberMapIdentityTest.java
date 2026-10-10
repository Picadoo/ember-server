package town.sunshine.corerpg.p1;

import org.junit.Test;
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
}
