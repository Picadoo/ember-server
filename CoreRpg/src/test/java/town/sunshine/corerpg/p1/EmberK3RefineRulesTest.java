package town.sunshine.corerpg.p1;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * D341 K3 offline prep · DESIGN §5.2 K3-1…K3-6 (+ set-progress invariant for drop tier).
 * Pure rules only — no Bukkit, no live jar/yml.
 */
public class EmberK3RefineRulesTest {

    @After
    public void reset() {
        EmberSixSlot.testEnabled = null;
        EmberSixSlot.testMigrate = null;
        EmberSixSlot.testSetBonus = null;
        EmberSixSlot.testK3Refine = null;
    }

    static EmberItemData armor(String fam, String slot, int tier, int q, int craft, int enh, boolean bound, String src) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, craft, enh, 0, bound, src, EmberItemData.DATA_VERSION, 0);
    }

    static EmberItemData blade(int craft) {
        return EmberItemData.create("burst", "blade", 2, 1, craft, 3, true, "drop");
    }

    private static void on() {
        EmberSixSlot.testEnabled = true;
        EmberSixSlot.testK3Refine = true;
    }

    // ---------------------------------------------------------------- K3-1 switch off = refuse / same as live

    @Test
    public void k3_1_switchDefaultsOffAndNeedsMaster() {
        assertFalse(EmberSixSlot.k3RefineEnabled());
        EmberSixSlot.testK3Refine = true;
        assertFalse("k3_refine alone never runs without master", EmberSixSlot.k3RefineEnabled());
        EmberSixSlot.testEnabled = true;
        EmberSixSlot.testK3Refine = true;
        assertTrue(EmberSixSlot.k3RefineEnabled());
        EmberSixSlot.testK3Refine = false;
        assertFalse(EmberSixSlot.k3RefineEnabled());
    }

    @Test
    public void k3_1_switchOffAlwaysRefuses() {
        EmberItemData t = armor("scorch", "chest", 2, 1, 0, 0, true, "drop");
        EmberItemData m = armor("burst", "chest", 2, 0, 3, 0, true, "drop");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertFalse(p.ok());
        assertEquals(EmberK3RefineRules.REFUSE_OFF, p.error);
        assertFalse(p.destroyMaterial);
        assertNull(p.targetAfter);
        assertEquals(EmberUpgradeRules.Cost.NONE, p.cost);
        // K0 forge path still refuses armor craft
        assertEquals(EmberUpgradeRules.ARMOR_REFUSE, EmberUpgradeRules.refine(t).error);
    }

    // ---------------------------------------------------------------- K3-2 only craft = max; other bytes unchanged

    @Test
    public void k3_2_onlyCraftChanges_qualityFamilyTierEnhanceAffixIntact() {
        on();
        EmberItemData t = new EmberItemData(EmberItemData.newUid(), "ember_v1_scorch_chest_t2", "scorch", "chest",
                2, 2, 1, 0, 0, true, "drop", EmberItemData.DATA_VERSION, 5, 12, 3, 7, 2, EmberItemData.Origin.NONE);
        EmberItemData m = armor("burst", "chest", 3, 0, 3, 9, true, "migrate");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertTrue(p.ok());
        assertEquals(3, p.targetAfter.craft);
        assertEquals(t.uid, p.targetAfter.uid);
        assertEquals(t.family, p.targetAfter.family);
        assertEquals(t.slot, p.targetAfter.slot);
        assertEquals(t.tier, p.targetAfter.tier);
        assertEquals(t.quality, p.targetAfter.quality);
        assertEquals(t.enhance, p.targetAfter.enhance);
        assertEquals(t.pity, p.targetAfter.pity);
        assertEquals(t.bound, p.targetAfter.bound);
        assertEquals(t.source, p.targetAfter.source);
        assertEquals(t.version, p.targetAfter.version);
        assertEquals(t.affix, p.targetAfter.affix);
        assertEquals(t.afPity, p.targetAfter.afPity);
        assertEquals(t.sigCode, p.targetAfter.sigCode);
        assertEquals(t.rerollN, p.targetAfter.rerollN);
        assertEquals(t.rev + 1, p.targetAfter.rev);
        assertEquals(t.ni, p.targetAfter.ni); // same tier → same template
    }

    // ---------------------------------------------------------------- K3-3 material destroy + audit; no half-destroy on refuse

    @Test
    public void k3_3_successDestroysMaterialAndEmitsAudit() {
        on();
        EmberItemData t = armor("sustain", "legs", 2, 1, 0, 0, true, "drop");
        EmberItemData m = armor("scorch", "legs", 1, 3, 2, 0, true, "drop");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertTrue(p.ok());
        assertTrue(p.destroyMaterial);
        assertNotNull(p.audit);
        assertTrue(p.audit.contains("k3_refine"));
        assertTrue(p.audit.contains(t.uid));
        assertTrue(p.audit.contains(m.uid));
        assertTrue(p.audit.contains("craft=0→2"));
        assertEquals(EmberUpgradeRules.Cost.NONE, p.cost);
    }

    @Test
    public void k3_3_refuseNeverMarksDestroy() {
        on();
        EmberItemData t = armor("scorch", "boots", 2, 0, 2, 0, true, "drop");
        EmberItemData m = armor("scorch", "boots", 2, 0, 1, 0, true, "drop"); // lower craft
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertFalse(p.ok());
        assertFalse(p.destroyMaterial);
        assertNull(p.audit);
        assertNull(p.targetAfter);
    }

    // ---------------------------------------------------------------- K3-4 slot / no-gain / worn material

    @Test
    public void k3_4_differentSlotRefused() {
        on();
        EmberItemData t = armor("scorch", "head", 2, 0, 0, 0, true, "drop");
        EmberItemData m = armor("scorch", "chest", 2, 0, 3, 0, true, "drop");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertFalse(p.ok());
        assertEquals(EmberK3RefineRules.REFUSE_SLOT, p.error);
    }

    @Test
    public void k3_4_noGainRefusedIncludingEqualAndMaxed() {
        on();
        EmberItemData t = armor("burst", "chest", 2, 1, 2, 0, true, "drop");
        EmberItemData eq = armor("burst", "chest", 3, 0, 2, 0, true, "drop");
        assertEquals(EmberK3RefineRules.REFUSE_NO_GAIN, EmberK3RefineRules.plan(t, eq, false).error);
        EmberItemData low = armor("burst", "chest", 1, 0, 1, 0, true, "drop");
        assertEquals(EmberK3RefineRules.REFUSE_NO_GAIN, EmberK3RefineRules.plan(t, low, false).error);
        EmberItemData maxed = armor("burst", "chest", 3, 3, 3, 0, true, "drop");
        EmberItemData maxMat = armor("scorch", "chest", 3, 0, 3, 0, true, "drop");
        assertEquals(EmberK3RefineRules.REFUSE_NO_GAIN, EmberK3RefineRules.plan(maxed, maxMat, false).error);
    }

    @Test
    public void k3_4_wornMaterialRefused_targetMayBeWorn() {
        on();
        EmberItemData wornTarget = armor("scorch", "head", 2, 0, 0, 0, true, "drop");
        EmberItemData bagMat = armor("burst", "head", 2, 0, 2, 0, true, "drop");
        EmberK3RefineRules.Plan ok = EmberK3RefineRules.plan(wornTarget, bagMat, false);
        assertTrue("target may be worn", ok.ok());
        EmberK3RefineRules.Plan bad = EmberK3RefineRules.plan(wornTarget, bagMat, true);
        assertEquals(EmberK3RefineRules.REFUSE_WORN_MAT, bad.error);
        assertFalse(bad.destroyMaterial);
    }

    @Test
    public void k3_4_bladeCharmAndUnboundRefused() {
        on();
        EmberItemData chest = armor("scorch", "chest", 2, 0, 0, 0, true, "drop");
        EmberItemData b = blade(3);
        assertEquals(EmberK3RefineRules.REFUSE_NOT_ARMOR, EmberK3RefineRules.plan(chest, b, false).error);
        assertEquals(EmberK3RefineRules.REFUSE_NOT_ARMOR, EmberK3RefineRules.plan(b, chest, false).error);
        EmberItemData unbound = armor("scorch", "chest", 2, 0, 3, 0, false, "drop");
        assertEquals(EmberK3RefineRules.REFUSE_UNBOUND, EmberK3RefineRules.plan(chest, unbound, false).error);
    }

    // ---------------------------------------------------------------- K3-5 zero cost; cost tables untouched

    @Test
    public void k3_5_zeroCostAndRefineCostTableUnchanged() {
        on();
        EmberItemData t = armor("scorch", "boots", 2, 0, 0, 0, true, "drop");
        EmberItemData m = armor("scorch", "boots", 2, 0, 1, 0, true, "drop");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertTrue(p.ok());
        assertEquals(EmberUpgradeRules.Cost.NONE, p.cost);
        assertEquals(0, p.cost.shards);
        assertEquals(0, p.cost.cores);
        assertEquals(0, p.cost.blanks);
        assertEquals(0, p.cost.bone);
        assertEquals(0, p.cost.coins);
        // price tables still the forge ones (K3 must not rewrite them)
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 3, 5, 300), EmberUpgradeRules.refineCost(0));
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 6, 10, 600), EmberUpgradeRules.refineCost(1));
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 12, 20, 1200), EmberUpgradeRules.refineCost(2));
        assertNull(EmberUpgradeRules.refineCost(3));
    }

    // ---------------------------------------------------------------- K3-6 / K3-7 drop tier unchanged → set progress unchanged

    @Test
    public void k3_6_dropTierUnchanged_setProgressUnchanged() {
        on();
        EmberItemData bl = EmberItemData.create("scorch", "blade", 2, 1, 1, 3, true, "drop");
        EmberItemData ch = EmberItemData.create("scorch", "charm", 2, 1, 1, 3, true, "drop");
        EmberItemData head = armor("scorch", "head", 2, 0, 0, 0, true, "drop");
        EmberItemData chest = armor("scorch", "chest", 2, 1, 1, 0, true, "drop");
        EmberItemData[] wornBefore = new EmberItemData[]{head, chest, null, null};
        Object[] before = EmberSixRank.setProgress(bl, ch, wornBefore);

        EmberItemData mat = armor("burst", "chest", 3, 3, 3, 0, true, "drop"); // higher craft, different fam/tier — target keeps own
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(chest, mat, false);
        assertTrue(p.ok());
        assertEquals(chest.tier, p.targetAfter.tier);
        assertEquals(chest.family, p.targetAfter.family);
        EmberItemData[] wornAfter = new EmberItemData[]{head, p.targetAfter, null, null};
        Object[] after = EmberSixRank.setProgress(bl, ch, wornAfter);
        assertEquals(before[0], after[0]);
        assertEquals(before[1], after[1]);
        assertEquals(before[2], after[2]);
        assertEquals(before[3], after[3]);
        assertEquals(true, after[3]);
    }

    @Test
    public void migrateSourceAllowedAsTargetOrMaterial() {
        on();
        EmberItemData t = armor("scorch", "legs", 2, 0, 0, 0, true, "migrate");
        EmberItemData m = armor("burst", "legs", 1, 0, 2, 0, true, "migrate");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, m, false);
        assertTrue(p.ok());
        assertEquals(2, p.targetAfter.craft);
        assertEquals("migrate", p.targetAfter.source);
    }

    @Test
    public void selfAsMaterialRefused() {
        on();
        EmberItemData t = armor("scorch", "head", 2, 0, 0, 0, true, "drop");
        EmberK3RefineRules.Plan p = EmberK3RefineRules.plan(t, t, false);
        assertEquals(EmberK3RefineRules.REFUSE_SELF, p.error);
    }
}
