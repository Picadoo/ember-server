package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CX D13329: short_div soft claim — n&lt;3 拒；n≥3 未领可领；已领拒；period day latch；PAPI bits；零改 SOFT_TARGET。
 */
public class EmberShortDayDivSoftClaimTest {

    @Test
    public void softTargetUnchanged() {
        assertEquals(3, EmberShortDayDivChase.SOFT_TARGET);
        assertEquals(2, EmberShortDayDivSoftClaim.BONUS);
        assertEquals("p1_short_div_soft_claim", EmberShortDayDivSoftClaim.C_CLAIM);
    }

    @Test
    public void nBelowSoftTargetNotClaimable() {
        assertFalse(EmberShortDayDivSoftClaim.claimable(0, false));
        assertFalse(EmberShortDayDivSoftClaim.claimable(1, false));
        assertFalse(EmberShortDayDivSoftClaim.claimable(2, false));
        assertEquals("0", EmberShortDayDivSoftClaim.claimOk(2, false));
        assertEquals("未满 · 2/3", EmberShortDayDivSoftClaim.claimLine(2, false));
    }

    @Test
    public void nAtOrAboveSoftTargetClaimableWhenUnclaimed() {
        assertTrue(EmberShortDayDivSoftClaim.claimable(3, false));
        assertTrue(EmberShortDayDivSoftClaim.claimable(4, false));
        assertEquals("1", EmberShortDayDivSoftClaim.claimOk(3, false));
        assertEquals("0", EmberShortDayDivSoftClaim.claimedBit(false));
        assertEquals("可领 · 骨尘×2 · 3/3", EmberShortDayDivSoftClaim.claimLine(3, false));
    }

    @Test
    public void claimedRejectsEvenWhenNMet() {
        assertFalse(EmberShortDayDivSoftClaim.claimable(3, true));
        assertFalse(EmberShortDayDivSoftClaim.claimable(5, true));
        assertEquals("0", EmberShortDayDivSoftClaim.claimOk(3, true));
        assertEquals("1", EmberShortDayDivSoftClaim.claimedBit(true));
        assertEquals("已领 · 3/3", EmberShortDayDivSoftClaim.claimLine(3, true));
    }

    @Test
    public void periodDayLatch() {
        PlayerData d = new PlayerData();
        assertFalse(EmberShortDayDivSoftClaim.claimedToday(d));
        assertTrue(EmberShortDayDivSoftClaim.claimable(d, 3));
        d.addPeriodCount(EmberShortDayDivSoftClaim.C_CLAIM, "day", 1);
        assertTrue(EmberShortDayDivSoftClaim.claimedToday(d));
        assertFalse(EmberShortDayDivSoftClaim.claimable(d, 3));
        assertFalse(EmberShortDayDivSoftClaim.claimable(d, 9));
    }

    @Test
    public void rejectHintsHonest() {
        assertTrue(EmberShortDayDivSoftClaim.rejectHint(1, false).contains("未满"));
        assertTrue(EmberShortDayDivSoftClaim.rejectHint(3, true).contains("已领"));
    }

    @Test
    public void nullSafe() {
        assertFalse(EmberShortDayDivSoftClaim.claimedToday(null));
        assertFalse(EmberShortDayDivSoftClaim.claimable(null, 3));
        assertFalse(EmberShortDayDivSoftClaim.tryClaim(null, new PlayerData(), 3));
        assertFalse(EmberShortDayDivSoftClaim.tryClaim(null, null, 3));
    }
}
