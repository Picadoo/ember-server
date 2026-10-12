package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TIP-AJ D12655 / DESIGN §2.6: sig-page defense summary slot pick + catalog where-clause.
 * GrowthService.defenseActiveSlot uses the same prio via previewSlot then signed fallback.
 */
public class EmberSigPageDefenseSummaryTest {

    private static boolean[] flags(boolean charm, boolean head, boolean chest, boolean legs, boolean boots) {
        return new boolean[]{charm, head, chest, legs, boots};
    }

    /** DESIGN §2.6: charm-only active → charm (same as pre-AJ). */
    @Test public void pick_D12655_charmOnly() {
        assertEquals("charm", EmberGrowthService.pickDefenseSummarySlot(
                flags(true, false, false, false, false),
                flags(true, false, false, false, false)));
    }

    /** DESIGN §2.6: charm empty, head wins → head (not fake charm empty). */
    @Test public void pick_D12655_headWin_noCharm() {
        assertEquals("head", EmberGrowthService.pickDefenseSummarySlot(
                flags(false, true, false, false, false),
                flags(false, true, false, false, false)));
    }

    /** DESIGN §2.6: charm+head both signed & active → charm prio. */
    @Test public void pick_D12655_prioCharmOverHead() {
        assertEquals("charm", EmberGrowthService.pickDefenseSummarySlot(
                flags(true, true, false, false, false),
                flags(true, true, false, false, false)));
    }

    /** DESIGN §2.6: no defense-side → null (honest empty). */
    @Test public void pick_D12655_none_null() {
        assertNull(EmberGrowthService.pickDefenseSummarySlot(
                flags(false, false, false, false, false),
                flags(false, false, false, false, false)));
        assertNull(EmberGrowthService.pickDefenseSummarySlot(null, null));
    }

    /** DESIGN §2.6: toggle off winner → active gone, signed fallback keeps slot for 关掉 + toggle-back. */
    @Test public void pick_D12655_toggleOff_fallbackSigned() {
        // head was active; after sigOff(head) active[] all false but signed[head]=true
        assertEquals("head", EmberGrowthService.pickDefenseSummarySlot(
                flags(false, false, false, false, false),
                flags(false, true, false, false, false)));
    }

    /** DESIGN §2.6: charm off + head active → report head (combat honest), not charm. */
    @Test public void pick_D12655_charmOff_headActive() {
        assertEquals("head", EmberGrowthService.pickDefenseSummarySlot(
                flags(false, true, false, false, false),
                flags(true, true, false, false, false)));
    }

    /** DESIGN §2.6: dual armor, no charm → head over boots. */
    @Test public void pick_D12655_dualArmor_headOverBoots() {
        assertEquals("head", EmberGrowthService.pickDefenseSummarySlot(
                flags(false, true, false, false, true),
                flags(false, true, false, false, true)));
    }

    /** DESIGN §2.6: catalog a_ non-blade → 护符或防具; blade → 刃. */
    @Test public void imprintWhere_D12654_bladeVsDefense() {
        EmberSignature.Def blade = EmberSignature.byId("L01");
        EmberSignature.Def charm = EmberSignature.byId("L02");
        assertEquals("刃", EmberGrowthService.imprintActionWhere(blade));
        assertEquals("护符或防具", EmberGrowthService.imprintActionWhere(charm));
        assertEquals("护符或防具", EmberGrowthService.imprintActionWhere(null));
        assertFalse(EmberGrowthService.imprintActionWhere(charm).contains("护符上"));
        assertTrue(EmberGrowthService.imprintActionWhere(charm).contains("防具"));
    }

    /** DESIGN §2.6: imprintWornTarget on armor (hope tier follows armor, not forced charm). */
    @Test public void imprintWornTarget_D12654_armorTierNotForcedCharm() {
        // L02 is charm-def / any-family / fitsDefense on head
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        assertNotNull(l02);
        EmberItemData head = EmberItemData.create("scorch", "head", 3, 1, 0, 0, true, "drop");
        EmberItemData charm = EmberItemData.create("scorch", "charm", 1, 1, 0, 0, true, "drop");
        // no GrowthService: assert fitsDefense so hope path can pick armor when charm absent
        assertTrue(EmberSignature.fitsDefense(l02, head.slot, head.family));
        assertTrue(EmberSignature.fitsDefense(l02, charm.slot, charm.family));
        assertEquals(3, head.tier);
        assertEquals(1, charm.tier);
        // coin need must follow target tier (armor T3 ≠ charm T1)
        assertTrue(EmberSignature.imprintCoin(head.tier) > EmberSignature.imprintCoin(charm.tier));
    }

    private static void assertNotNull(Object o) {
        org.junit.Assert.assertNotNull(o);
    }
}
