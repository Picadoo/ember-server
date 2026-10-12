package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * D3937 held-ready latch + TIP-AN D12716–D12720 defense-side sameSlotPiece / ready cue.
 */
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

    /** Pre-AN: blade worn preferred; charm-def with no defense piece → null. */
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

    /** DESIGN §2.3: charm-def (L02) + same-family helmet worn · no charm → helmet. */
    @Test
    public void sameSlot_D12716_charmDef_headWin_noCharm() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertEquals("charm", l02.slot);
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(t, null, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        EmberItemData got = EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, l02);
        assertNotNull(got);
        assertEquals(head.uid, got.uid);
        assertEquals("head", got.slot);
        EmberItemData worn = EmberSigChase.imprintWornTarget(lo, l02);
        assertNotNull(worn);
        assertEquals(head.uid, worn.uid);
    }

    /** DESIGN §2.3: charm-def + charm+head → charm prio (imprintWornTarget order). */
    @Test
    public void sameSlot_D12716_charmDef_charmBeatsHead() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        EmberItemData charm = EmberItemData.create("scorch", "charm", 1, 1, 0, 0, true, "drop");
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(t, null, charm, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        EmberItemData got = EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, l02);
        assertNotNull(got);
        assertEquals(charm.uid, got.uid);
    }

    /** DESIGN §2.3: blade-def (L01) + blade → still blade (regression). */
    @Test
    public void sameSlot_D12716_bladeL01_regression() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(t, blade, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        EmberItemData got = EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, l01);
        assertNotNull(got);
        assertEquals(blade.uid, got.uid);
        assertEquals("blade", got.slot);
    }

    /** DESIGN §2.3: charm-def + no charm no armor → null. */
    @Test
    public void sameSlot_D12716_charmDef_empty_null() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        EmberLoadout lo = EmberLoadout.compute(t, null, null, 10);
        assertNull(EmberSigChase.sameSlotPiece(null, new PlayerData(), lo, l02));
        assertNull(EmberSigChase.imprintWornTarget(lo, l02));
    }

    /** DESIGN §2.3: ready cue slot word for helmet contains 头/头盔, not forced 护符. */
    @Test
    public void readyCueSlotWord_D12718_headNotForcedCharm() {
        EmberTables t = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(t, null, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        String word = EmberSigChase.readyCueSlotWord(lo, l02);
        assertTrue(word.contains("头") || word.contains("盔"));
        assertFalse("must not force 护符 when armor is target", "护符".equals(word));
        // empty worn → fall back to def.slot name (护符)
        EmberLoadout empty = EmberLoadout.compute(t, null, null, 10);
        assertEquals("护符", EmberSigChase.readyCueSlotWord(empty, l02));
        // blade-def unchanged
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberLoadout blo = EmberLoadout.compute(t, blade, null, 10);
        assertEquals("刃", EmberSigChase.readyCueSlotWord(blo, l01));
    }

    /** NON: armor stampable stays empty; fitsDefense ≠ fitsStamp for head. */
    @Test
    public void stampable_armorStillEmpty_NON() {
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertTrue(EmberSignature.stampable("q01", "head", "scorch").isEmpty());
        assertTrue(EmberSignature.stampable("q01", "chest", "scorch").isEmpty());
        assertTrue(EmberSignature.fitsStamp(l02, "charm", "scorch"));
        assertFalse(EmberSignature.fitsStamp(l02, "head", "scorch"));
        assertTrue(EmberSignature.fitsDefense(l02, "head", "scorch"));
    }
}
