package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberSigChaseHonestyTest {
    @Test public void remainLineReadyAndGap() {
        EmberSignature.Def def = EmberSignature.DEFS.get(0);
        assertNotNull(def);
        int need = EmberSignature.imprintMarks();
        assertTrue(EmberSigChase.remainLine(def, need).contains("可烙"));
        assertTrue(EmberSigChase.remainLine(def, 0).contains("还差"));
        assertTrue(EmberSigChase.remainLine(null, 0).contains("未选"));
    }

    @Test public void statusGlanceEmptyWithoutData() {
        assertEquals("", EmberSigChase.statusGlance(null));
    }

    /** DESIGN §2.3 TIP-AO: remainLine full follows readyTail / missing piece. */
    @Test public void remainLine_D12737_readyTail() {
        EmberTables tables = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        int need = EmberSignature.imprintMarks();
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout loHead = EmberLoadout.compute(tables, null, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        String withHead = EmberSigChase.remainLine(l02, need, loHead);
        assertTrue(withHead, withHead.contains("徽记够"));
        assertTrue(withHead, withHead.contains("头盔") || withHead.contains("头"));
        assertFalse(withHead, withHead.contains("可烙护符"));

        EmberLoadout empty = EmberLoadout.compute(tables, null, null, 10);
        assertTrue(EmberSigChase.remainLine(l02, need, empty).contains("缺可烙穿着件"));

        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberLoadout blo = EmberLoadout.compute(tables, blade, null, 10);
        assertTrue(EmberSigChase.remainLine(l01, need, blo).contains("可烙刃"));

        String gap = EmberSigChase.remainLine(l02, 0, loHead);
        assertTrue(gap.contains("还差"));
        assertFalse(gap.contains("缺可烙穿着件"));
        assertFalse(gap.contains("可烙头"));

        // lo==null full → 可烙 + def.slot
        assertTrue(EmberSigChase.remainLine(l01, need).contains("可烙刃"));
        assertEquals("缺可烙穿着件", EmberSigChase.readyTail(empty, l02));
        assertEquals("可烙头盔", EmberSigChase.readyTail(loHead, l02));
    }
}
