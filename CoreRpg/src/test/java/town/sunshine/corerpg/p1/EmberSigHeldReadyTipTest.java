package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberSigHeldReadyTipTest {
    @Test
    public void heldLatchClearsWithReady() {
        PlayerData d = new PlayerData();
        EmberSignature.Def def = EmberSignature.byId("L01");
        assertTrue(EmberSigChase.set(d, def));
        d.addPeriodCount(EmberSigChase.C_HELD_TIP, "all", def.code);
        assertEquals(def.code, d.periodCount(EmberSigChase.C_HELD_TIP, "all"));
        EmberSigChase.clearReadyLatch(d);
        assertEquals(0, d.periodCount(EmberSigChase.C_HELD_TIP, "all"));
    }

    @Test
    public void sameSlotWornPreferred() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def def = EmberSignature.byId("L01");
        assertEquals("blade", def.slot);
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(t, blade, null, 10);
        EmberItemData got = EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, def);
        assertNotNull(got);
        assertEquals(blade.uid, got.uid);
        EmberSignature.Def charmDef = EmberSignature.byId("L02");
        assertEquals("charm", charmDef.slot);
        assertNull(EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, charmDef));
    }
}
