package town.sunshine.corerpg.p1;

import org.junit.After;
import org.junit.Test;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberK3RefineService.CommitIntent;

import static org.junit.Assert.*;

/**
 * D341 K3 offline · service skeleton wiring (preview / commitIntent).
 * No Bukkit, no live jar/yml, switch defaults off.
 */
public class EmberK3RefineServiceTest {

    @After
    public void reset() {
        EmberSixSlot.testEnabled = null;
        EmberSixSlot.testMigrate = null;
        EmberSixSlot.testSetBonus = null;
        EmberSixSlot.testK3Refine = null;
    }

    static EmberItemData armor(String fam, String slot, int tier, int q, int craft, int enh, boolean bound) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, craft, enh, 0, bound, "drop", EmberItemData.DATA_VERSION, 0);
    }

    private static void on() {
        EmberSixSlot.testEnabled = true;
        EmberSixSlot.testK3Refine = true;
    }

    @Test
    public void switchOff_commitIntentRefuses_noTxnItems() {
        EmberItemData t = armor("scorch", "chest", 2, 1, 0, 0, true);
        EmberItemData m = armor("burst", "chest", 2, 0, 3, 0, true);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, false, "tester");
        assertFalse(c.ok());
        assertEquals(EmberK3RefineRules.REFUSE_OFF, c.error);
        assertTrue(c.items.isEmpty());
        assertNull(c.audit);
        assertEquals(EmberUpgradeRules.Cost.NONE, c.cost);
        EmberK3RefineRules.Plan prev = EmberK3RefineService.preview(t, m, false);
        assertEquals(EmberK3RefineRules.REFUSE_OFF, prev.error);
    }

    @Test
    public void success_buildsTxn_destroyMaterial_audit_zeroCost_bytesIntact() {
        on();
        EmberItemData t = new EmberItemData(EmberItemData.newUid(), "ember_v1_scorch_chest_t2", "scorch", "chest",
                2, 2, 1, 0, 0, true, "drop", EmberItemData.DATA_VERSION, 5, 12, 3, 7, 2, EmberItemData.Origin.NONE);
        EmberItemData m = armor("burst", "chest", 3, 0, 3, 9, true);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, false, "op-uuid");
        assertTrue(c.ok());
        assertEquals(EmberUpgradeRules.Cost.NONE, c.cost);
        assertEquals(0, c.cost.shards);
        assertEquals(0, c.cost.cores);
        assertEquals(0, c.cost.blanks);
        assertEquals(0, c.cost.bone);
        assertEquals(0, c.cost.coins);
        assertEquals(2, c.items.size());

        TxnItem targetTxn = c.items.get(0);
        assertSame(t, targetTxn.before);
        assertNotNull(targetTxn.after);
        assertEquals(3, targetTxn.after.craft);
        assertNull(targetTxn.retireState);
        assertEquals(t.quality, targetTxn.after.quality);
        assertEquals(t.family, targetTxn.after.family);
        assertEquals(t.tier, targetTxn.after.tier);
        assertEquals(t.enhance, targetTxn.after.enhance);
        assertEquals(t.uid, targetTxn.after.uid);

        TxnItem matTxn = c.items.get(1);
        assertSame(m, matTxn.before);
        assertNull(matTxn.after);
        assertEquals(EmberK3RefineService.RETIRE_MATERIAL, matTxn.retireState);

        assertNotNull(c.audit);
        assertTrue(c.audit.contains("k3_refine"));
        assertTrue(c.audit.contains(t.uid));
        assertTrue(c.audit.contains(m.uid));
        assertTrue(c.audit.contains("craft=1→3"));
        assertTrue(c.audit.contains("op=op-uuid"));
        assertNotNull(c.note);
        assertEquals(EmberK3RefineService.KIND, "k3_refine");
    }

    @Test
    public void refuse_differentSlot_noTxn() {
        on();
        EmberItemData t = armor("scorch", "head", 2, 0, 0, 0, true);
        EmberItemData m = armor("scorch", "chest", 2, 0, 3, 0, true);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, false);
        assertFalse(c.ok());
        assertEquals(EmberK3RefineRules.REFUSE_SLOT, c.error);
        assertTrue(c.items.isEmpty());
        assertNull(c.audit);
    }

    @Test
    public void refuse_noGain_noTxn() {
        on();
        EmberItemData t = armor("burst", "boots", 2, 0, 2, 0, true);
        EmberItemData m = armor("scorch", "boots", 2, 0, 1, 0, true);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, false);
        assertFalse(c.ok());
        assertEquals(EmberK3RefineRules.REFUSE_NO_GAIN, c.error);
        assertTrue(c.items.isEmpty());
    }

    @Test
    public void refuse_wornMaterial_noTxn() {
        on();
        EmberItemData t = armor("scorch", "legs", 2, 0, 0, 0, true);
        EmberItemData m = armor("burst", "legs", 2, 0, 2, 0, true);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, true, "x");
        assertFalse(c.ok());
        assertEquals(EmberK3RefineRules.REFUSE_WORN_MAT, c.error);
        assertTrue(c.items.isEmpty());
        assertNull(c.audit);
    }

    @Test
    public void previewMatchesPlan_onSuccess() {
        on();
        EmberItemData t = armor("sustain", "chest", 2, 1, 0, 0, true);
        EmberItemData m = armor("scorch", "chest", 1, 0, 2, 0, true);
        EmberK3RefineRules.Plan prev = EmberK3RefineService.preview(t, m, false);
        EmberK3RefineRules.Plan plan = EmberK3RefineRules.plan(t, m, false);
        assertTrue(prev.ok());
        assertEquals(plan.targetAfter.craft, prev.targetAfter.craft);
        assertEquals(plan.destroyMaterial, prev.destroyMaterial);
        CommitIntent c = EmberK3RefineService.commitIntent(t, m, false);
        assertTrue(c.ok());
        assertEquals(prev.targetAfter.craft, c.items.get(0).after.craft);
    }
}
