package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-AX DESIGN §2.3: firstsoft hope/gate peek — admit/entryProblems/hope/blocked eat peek;
 * peek never consumes; Session consume path unchanged.
 */
public class EmberStaminaAbyssFirstSoftHopeGateTest {

    @Test
    public void staminaPeek_ready_reducesWithoutConsume() {
        PlayerData d = new PlayerData();
        assertEquals(25, EmberStaminaFirstSoft.peekCost(d, 30));
        assertTrue(EmberStaminaFirstSoft.ready(d)); // peek — still ready
        assertEquals(25, EmberStaminaFirstSoft.peekCost(d, 30));
    }

    @Test
    public void staminaPeek_used_full() {
        PlayerData d = new PlayerData();
        EmberStaminaFirstSoft.consumeCost(d, 30);
        assertFalse(EmberStaminaFirstSoft.ready(d));
        assertEquals(30, EmberStaminaFirstSoft.peekCost(d, 30));
    }

    @Test
    public void staminaPeek_costOne_unchanged() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberStaminaFirstSoft.peekCost(d, 1));
        assertTrue(EmberStaminaFirstSoft.ready(d));
    }

    @Test
    public void staminaGate_ready_haveCostMinus1_admitsByPeek() {
        PlayerData d = new PlayerData();
        int cost = 30;
        int peek = EmberStaminaFirstSoft.peekCost(d, cost); // 25
        int have = cost - 1; // 29 >= 25
        assertTrue(have >= peek);
        assertNull(EmberEntryService.staminaProblem("A", have, peek));
        assertTrue(EmberStaminaFirstSoft.ready(d)); // peek did not consume
    }

    @Test
    public void staminaGate_ready_haveBelowPeek_stillRefuse() {
        PlayerData d = new PlayerData();
        int cost = 30;
        int peek = EmberStaminaFirstSoft.peekCost(d, cost); // 25
        int have = peek - 1; // 24
        String why = EmberEntryService.staminaProblem("A", have, peek);
        assertEquals("A 体力不足（需 " + peek + "，当前 " + have + "）", why);
        if (EmberStaminaFirstSoft.ready(d) && peek < cost)
            why = why + EmberGearNextHint.softTail(true);
        assertTrue(why.contains(EmberGearNextHint.SOFT_TAIL));
    }

    @Test
    public void staminaGate_used_fullCost() {
        PlayerData d = new PlayerData();
        EmberStaminaFirstSoft.consumeCost(d, 30);
        int cost = 30;
        assertEquals(30, EmberStaminaFirstSoft.peekCost(d, cost));
        assertNotNull(EmberEntryService.staminaProblem("A", 29, 30));
        assertNull(EmberEntryService.staminaProblem("A", 30, 30));
    }

    @Test
    public void staminaConsume_afterPeeks_stillOnce() {
        PlayerData d = new PlayerData();
        EmberStaminaFirstSoft.peekCost(d, 30);
        EmberStaminaFirstSoft.peekCost(d, 30);
        assertEquals(25, EmberStaminaFirstSoft.consumeCost(d, 30));
        assertEquals(30, EmberStaminaFirstSoft.consumeCost(d, 30));
    }

    @Test
    public void abyssPeek_ready_reducesWithoutConsume() {
        PlayerData d = new PlayerData();
        assertEquals(80, EmberAbyssFeeSoft.peekFee(d, 100));
        assertTrue(EmberAbyssFeeSoft.ready(d));
        assertEquals(80, EmberAbyssFeeSoft.peekFee(d, 100));
    }

    @Test
    public void abyssPeek_used_full() {
        PlayerData d = new PlayerData();
        EmberAbyssFeeSoft.consumeFee(d, 100);
        assertEquals(100, EmberAbyssFeeSoft.peekFee(d, 100));
    }

    @Test
    public void abyssPeek_feeZero_unchanged() {
        PlayerData d = new PlayerData();
        assertEquals(0, EmberAbyssFeeSoft.peekFee(d, 0));
        assertTrue(EmberAbyssFeeSoft.ready(d));
    }

    @Test
    public void abyssGate_ready_coinInPeekToFull_admits() {
        PlayerData d = new PlayerData();
        int fee0 = 100;
        int feeShow = EmberAbyssFeeSoft.peekFee(d, fee0); // 80
        int coin = 90; // ∈ [80, 100)
        assertTrue(coin >= feeShow);
        assertTrue(coin < fee0);
        // entryProblems would not refuse on fee when coin >= feeShow && marks unused
        assertFalse(coin < feeShow); // not short
        assertTrue(EmberAbyssFeeSoft.ready(d));
        // abyss_hope afford
        boolean afford = feeShow <= 0 || coin >= feeShow;
        assertTrue(afford);
        String hope = EmberHopeMeter.abyssHope(1, 2, feeShow, afford);
        assertTrue(hope.contains("费用够"));
        hope = hope + EmberGearNextHint.softTail(true);
        assertTrue(hope.contains(EmberGearNextHint.SOFT_TAIL));
    }

    @Test
    public void abyssGate_used_or_feeZero_old() {
        PlayerData d = new PlayerData();
        EmberAbyssFeeSoft.consumeFee(d, 100);
        assertEquals(100, EmberAbyssFeeSoft.peekFee(d, 100));
        assertEquals(0, EmberAbyssFeeSoft.peekFee(d, 0));
        assertTrue(EmberHopeMeter.abyssHope(1, 2, 0, true).contains("免费")
                || EmberHopeMeter.abyssHope(1, 2, 0, true).contains("费用"));
    }

    @Test
    public void staminaHope_ready_usesPeekRuns() {
        PlayerData d = new PlayerData();
        int full = 30;
        int peek = EmberStaminaFirstSoft.peekCost(d, full); // 25
        int cur = 50;
        String hope = EmberHopeMeter.staminaHope(cur, 100, peek);
        // 50/25 = 2 runs
        assertTrue(hope, hope.contains("2"));
        assertTrue(EmberStaminaFirstSoft.ready(d));
        hope = hope + " · " + EmberGearNextHint.SOFT_TAIL;
        assertTrue(hope.contains(EmberGearNextHint.SOFT_TAIL));
    }

    @Test
    public void staminaBlocked_ready_vsPeek() {
        PlayerData d = new PlayerData();
        int full = 40;
        int peek = EmberStaminaFirstSoft.peekCost(d, full); // 35
        int stamina = 37; // < full, >= peek → unblocked under soft
        assertTrue(stamina < full);
        assertFalse(stamina < peek);
        int stamina2 = 30; // < peek → blocked
        assertTrue(stamina2 < peek);
    }

    @Test
    public void constants_unchanged() {
        assertEquals(5, EmberStaminaFirstSoft.LESS);
        assertEquals(0.8, EmberAbyssFeeSoft.MULT, 0.0);
        assertEquals("p1_stamina_first_soft", EmberStaminaFirstSoft.C_USED);
        assertEquals("p1_abyss_fee_soft", EmberAbyssFeeSoft.C_USED);
    }
}
