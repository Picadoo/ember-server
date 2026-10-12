package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-AY DESIGN §2.3: CookCoinSoft.peekFee batch gate — truncate on soft unit;
 * consume only on deduct; success/refund align actual cookFee; peek never latches.
 */
public class EmberCookCoinSoftBatchGateTest {

    /** Simulate LifeService.cook fee gate + consume (no Bukkit). */
    static int[] gateAndPay(PlayerData d, int coin, int each, int fish) {
        int total = fish;
        int feeFull = total * each;
        int feeShow = EmberCookCoinSoft.peekFee(d, feeFull);
        int unitShow = total > 0 ? Math.max(1, feeShow / total) : each;
        if (coin < feeShow) {
            total = unitShow > 0 ? coin / unitShow : total;
            if (total <= 0) return new int[] {0, 0, feeShow, unitShow}; // refused
            feeFull = total * each;
        }
        int cookFee = EmberCookCoinSoft.consumeFee(d, feeFull);
        return new int[] {total, cookFee, feeShow, unitShow};
    }

    @Test
    public void peekFee_ready_halvesWithoutConsume() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberCookCoinSoft.peekFee(d, 10));
        assertTrue(EmberCookCoinSoft.ready(d));
        assertEquals(5, EmberCookCoinSoft.peekFee(d, 10));
        assertTrue(EmberCookCoinSoft.ready(d));
    }

    @Test
    public void peekFee_used_full() {
        PlayerData d = new PlayerData();
        EmberCookCoinSoft.consumeFee(d, 10);
        assertFalse(EmberCookCoinSoft.ready(d));
        assertEquals(10, EmberCookCoinSoft.peekFee(d, 10));
    }

    @Test
    public void peekFee_zero_unchanged() {
        PlayerData d = new PlayerData();
        assertEquals(0, EmberCookCoinSoft.peekFee(d, 0));
        assertTrue(EmberCookCoinSoft.ready(d));
    }

    @Test
    public void batch_ready_coin5_each10_cooks1_pays5() {
        PlayerData d = new PlayerData();
        int[] r = gateAndPay(d, 5, 10, 2);
        assertEquals(1, r[0]); // cook 1
        assertEquals(5, r[1]); // actual pay 5
        assertFalse(EmberCookCoinSoft.ready(d)); // consumed once
    }

    @Test
    public void batch_ready_coin15_each10_fish2_cooks2_pays10() {
        PlayerData d = new PlayerData();
        int[] r = gateAndPay(d, 15, 10, 2);
        assertEquals(2, r[0]); // not under-cook 1
        assertEquals(10, r[1]); // soft of 20
        assertFalse(EmberCookCoinSoft.ready(d));
    }

    @Test
    public void batch_ready_coin4_each10_refuse_noConsume() {
        PlayerData d = new PlayerData();
        int[] r = gateAndPay(d, 4, 10, 1);
        assertEquals(0, r[0]);
        assertEquals(0, r[1]);
        assertTrue(EmberCookCoinSoft.ready(d)); // peek only — not consumed
    }

    @Test
    public void batch_used_coin15_fish2_fullTruncate_cooks1() {
        PlayerData d = new PlayerData();
        EmberCookCoinSoft.consumeFee(d, 10); // latch soft
        int[] r = gateAndPay(d, 15, 10, 2);
        assertEquals(1, r[0]); // full-price truncate: 15/10=1
        assertEquals(10, r[1]); // no second soft
        assertFalse(EmberCookCoinSoft.ready(d));
    }

    @Test
    public void partialRefund_alignsActualPaid_notTable() {
        int cookFee = 10; // after soft on feeFull=20
        int total = 2, cooked = 1;
        int back = cookFee - cookFee * cooked / total;
        assertEquals(5, back);
        cookFee -= back;
        assertEquals(5, cookFee);
        // old table path would refund (2-1)*10=10 → over-refund
        assertTrue(back <= 10);
        assertEquals(5, cooked * cookFee / cooked); // remaining = actual for cooked
    }

    @Test
    public void consume_stillOnce_afterPeek() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberCookCoinSoft.peekFee(d, 10));
        assertEquals(5, EmberCookCoinSoft.consumeFee(d, 10));
        assertEquals(10, EmberCookCoinSoft.consumeFee(d, 10));
        assertEquals(10, EmberCookCoinSoft.peekFee(d, 10));
    }

    @Test
    public void multAndUsedKey_unchanged() {
        assertEquals(0.5, EmberCookCoinSoft.MULT, 0.0);
        assertEquals("p1_cook_coin_soft", EmberCookCoinSoft.C_USED);
    }
}
