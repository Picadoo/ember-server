package town.sunshine.corerpg.p1;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;
public class EmberSigFeelTest {
    @Test public void owner_finds_dodge_heal_L02() {
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertNotNull(l02);
        assertEquals("L02", EmberSigFeel.owner(Collections.singletonList(l02), "dodge_heal").id);
        assertNull(EmberSigFeel.owner(Collections.singletonList(l02), "skill_ignite"));
    }
    @Test public void procLine_and_runLine() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertTrue(EmberSigFeel.procLine(l02, "躲开回气").contains("门楼余烬"));
        assertTrue(EmberSigFeel.runLine(Arrays.asList(l01, l02)).contains("本局签名"));
        assertEquals("", EmberSigFeel.runLine(Collections.<EmberSignature.Def>emptyList()));
    }
    @Test public void debounce_window() {
        assertFalse(EmberSigFeel.wouldAllow(1000L, 1000L + 400));
        assertTrue(EmberSigFeel.wouldAllow(1000L, 1000L + 800));
    }

    /** D12289 / D12295: menuLine state matrix — active / off / chase-only / empty. */
    @Test public void menuLine_activeSingle() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        String line = EmberSigFeel.menuLine(Collections.singletonList(l01), null, 0);
        assertTrue(line, line.startsWith("§d签名 §f残门焚斧"));
        assertTrue(line, line.contains("改打法·不加伤"));
        assertFalse(line, line.contains("追烙"));
    }

    @Test public void menuLine_activeDual() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        String line = EmberSigFeel.menuLine(Arrays.asList(l01, l02), EmberSignature.byId("L03"), 9);
        assertTrue(line, line.contains("§d签名 §f残门焚斧§7·§f门楼余烬"));
        assertTrue(line, line.contains("改打法·不加伤"));
        // active wins over chase
        assertFalse(line, line.contains("追烙"));
    }

    @Test public void menuLine_wornOff() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        String line = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(),
                Collections.singletonList(l01),
                EmberSignature.byId("L02"),
                1);
        assertEquals("§c签名关闭 §7· §f残门焚斧 §8点页重开", line);
    }

    @Test public void menuLine_chaseOnly() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        int need = EmberSignature.imprintMarks();
        String line = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(), l01, 2);
        assertEquals("§e追烙 §fL01 残门焚斧 §7· 徽记 2/" + need, line);
        // TIP-AO: full-tier uses readyTail (lo==null → def.slot 刃)
        String full = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(), l01, need);
        assertEquals("§e追烙就绪 §fL01 残门焚斧 §7· 可烙刃", full);
    }

    /** DESIGN §2.3 TIP-AO: chase-full menuLine follows worn target / missing piece. */
    @Test public void menuLine_D12737_chaseReady_target() {
        EmberTables tables = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        int need = EmberSignature.imprintMarks();
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout loHead = EmberLoadout.compute(tables, null, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        String withHead = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(), null, l02, need, loHead);
        assertTrue(withHead, withHead.startsWith("§e追烙就绪"));
        assertTrue(withHead, withHead.contains("头盔") || withHead.contains("头"));
        assertFalse(withHead, withHead.contains("可烙护符"));

        EmberLoadout empty = EmberLoadout.compute(tables, null, null, 10);
        String miss = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(), null, l02, need, empty);
        assertTrue(miss, miss.contains("缺可烙穿着件"));

        String under = EmberSigFeel.menuLine(
                Collections.<EmberSignature.Def>emptyList(), null, l02, 2, loHead);
        assertEquals("§e追烙 §fL02 " + l02.name + " §7· 徽记 2/" + need, under);
        assertFalse(under, under.contains("缺可烙穿着件"));
        assertFalse(under, under.contains("可烙头"));
    }

    @Test public void menuLine_empty() {
        assertEquals("§8无签名 · 点左开页 · 右选追烙",
                EmberSigFeel.menuLine(null, null, 0));
        assertEquals("§8无签名 · 点左开页 · 右选追烙",
                EmberSigFeel.menuLine(Collections.<EmberSignature.Def>emptyList(), null, 0));
        assertEquals("§8无签名 · 点左开页 · 右选追烙",
                EmberSigFeel.menuLine(Collections.<EmberSignature.Def>emptyList(),
                        Collections.<EmberSignature.Def>emptyList(), null, 0));
    }
}
