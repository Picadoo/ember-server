package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.LifeService;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-AZ DESIGN §2.3: life buy soft latch — peek gate; reject without consume;
 * consume only on successful pay path.
 */
public class EmberLifeBuySoftLatchAlignTest {

    /** Simulate LifeService.buy coin gate + consume (no Bukkit). materialsOk skips mat latch. */
    static int[] gateAndPay(PlayerData d, String offerId, int coinHave, int tableCoin, boolean materialsOk) {
        if (!materialsOk) {
            // material gate first — never soft
            return new int[] {0, 0, EmberLifeBuySoftLatchAlignTest.peek(offerId, d, tableCoin)}; // refused, no consume
        }
        int payShow = LifeService.peekBuyCoin(offerId, d, tableCoin);
        if (coinHave < payShow) {
            return new int[] {0, 0, payShow}; // refuse, no latch
        }
        int payCoin = LifeService.consumeBuyCoin(offerId, d, tableCoin);
        return new int[] {1, payCoin, payShow}; // ok, paid
    }

    static int peek(String id, PlayerData d, int coin) {
        return LifeService.peekBuyCoin(id, d, coin);
    }

    @Test
    public void rod_ready_coin25_table60_refuse_noConsume() {
        PlayerData d = new PlayerData();
        d.setCoin(25);
        assertEquals(30, EmberRodBuySoft.peekCoin(d, 60));
        int[] r = gateAndPay(d, LifeService.ROD_OFFER_ID, 25, 60, true);
        assertEquals(0, r[0]);
        assertEquals(0, r[1]);
        assertEquals(30, r[2]);
        assertTrue(EmberRodBuySoft.ready(d));
        assertFalse(EmberRodBuySoft.usedToday(d));
    }

    @Test
    public void bread_ready_coin15_table40_refuse_noConsume() {
        PlayerData d = new PlayerData();
        d.setCoin(15);
        assertEquals(20, EmberBreadBuySoft.peekCoin(d, 40));
        int[] r = gateAndPay(d, LifeService.BREAD_OFFER_ID, 15, 40, true);
        assertEquals(0, r[0]);
        assertTrue(EmberBreadBuySoft.ready(d));
        assertFalse(EmberBreadBuySoft.usedToday(d));
    }

    @Test
    public void rod_ready_coin40_table60_pays30_consumesOnce() {
        PlayerData d = new PlayerData();
        d.setCoin(40);
        int[] r = gateAndPay(d, LifeService.ROD_OFFER_ID, 40, 60, true);
        assertEquals(1, r[0]);
        assertEquals(30, r[1]);
        assertEquals(30, r[2]);
        assertFalse(EmberRodBuySoft.ready(d));
        assertTrue(EmberRodBuySoft.usedToday(d));
        // second buy full
        assertEquals(60, EmberRodBuySoft.peekCoin(d, 60));
        assertEquals(60, EmberRodBuySoft.consumeCoin(d, 60));
    }

    @Test
    public void rod_used_coin40_table60_refuse_noSecondSoft() {
        PlayerData d = new PlayerData();
        EmberRodBuySoft.consumeCoin(d, 60); // latch
        assertFalse(EmberRodBuySoft.ready(d));
        int[] r = gateAndPay(d, LifeService.ROD_OFFER_ID, 40, 60, true);
        assertEquals(0, r[0]); // 40 < 60 full
        assertEquals(60, r[2]);
        assertFalse(EmberRodBuySoft.ready(d));
    }

    @Test
    public void brew_ready_matsMissing_noConsume() {
        PlayerData d = new PlayerData();
        d.setCoin(100);
        int[] r = gateAndPay(d, LifeService.BREW_OFFER_ID, 100, 10, false);
        assertEquals(0, r[0]);
        assertTrue(EmberBrewCoinSoft.ready(d));
        assertFalse(EmberBrewCoinSoft.usedToday(d));
        assertEquals(5, EmberBrewCoinSoft.peekCoin(d, 10));
    }

    @Test
    public void peekCoin_allEight_readyHalves_usedFull() {
        PlayerData d = new PlayerData();
        assertEquals(30, EmberRodBuySoft.peekCoin(d, 60));
        assertEquals(20, EmberBreadBuySoft.peekCoin(d, 40));
        assertEquals(5, EmberBrewCoinSoft.peekCoin(d, 10));
        assertEquals(2, EmberSoulDustCoinSoft.peekCoin(d, 5));
        assertEquals(15, EmberReforgeStoneSoft.peekCoin(d, 30));
        assertEquals(40, EmberOffhandBuySoft.peekCoin(d, 80));
        assertEquals(150, EmberStableCharmSoft.peekCoin(d, 300));
        assertEquals(300, EmberPetHatchSoft.peekCoin(d, 600));
        // latch one
        EmberRodBuySoft.consumeCoin(d, 60);
        assertEquals(60, EmberRodBuySoft.peekCoin(d, 60));
        // others still ready
        assertTrue(EmberBreadBuySoft.ready(d));
        assertTrue(EmberBrewCoinSoft.ready(d));
    }

    @Test
    public void peekNeverLatches_consumeStillOnce() {
        PlayerData d = new PlayerData();
        assertEquals(30, EmberRodBuySoft.peekCoin(d, 60));
        assertEquals(30, EmberRodBuySoft.peekCoin(d, 60));
        assertTrue(EmberRodBuySoft.ready(d));
        assertEquals(30, EmberRodBuySoft.consumeCoin(d, 60));
        assertEquals(60, EmberRodBuySoft.consumeCoin(d, 60));
    }

    @Test
    public void multAndUsedKeys_unchanged() {
        assertEquals(0.5, EmberRodBuySoft.MULT, 0.0);
        assertEquals("p1_rod_buy_soft", EmberRodBuySoft.C_USED);
        assertEquals(0.5, EmberBreadBuySoft.MULT, 0.0);
        assertEquals("p1_bread_buy_soft", EmberBreadBuySoft.C_USED);
        assertEquals(0.5, EmberBrewCoinSoft.MULT, 0.0);
        assertEquals("p1_brew_coin_soft", EmberBrewCoinSoft.C_USED);
        assertEquals(0.5, EmberSoulDustCoinSoft.MULT, 0.0);
        assertEquals("p1_soul_dust_coin_soft", EmberSoulDustCoinSoft.C_USED);
        assertEquals(0.5, EmberReforgeStoneSoft.MULT, 0.0);
        assertEquals("p1_reforge_stone_soft", EmberReforgeStoneSoft.C_USED);
        assertEquals(0.5, EmberOffhandBuySoft.MULT, 0.0);
        assertEquals("p1_offhand_buy_soft", EmberOffhandBuySoft.C_USED);
        assertEquals(0.5, EmberStableCharmSoft.MULT, 0.0);
        assertEquals("p1_stable_charm_soft", EmberStableCharmSoft.C_USED);
        assertEquals(0.5, EmberPetHatchSoft.MULT, 0.0);
        assertEquals("p1_pet_hatch_soft", EmberPetHatchSoft.C_USED);
    }

    @Test
    public void lifeServiceHelpers_routeByOfferId() {
        PlayerData d = new PlayerData();
        assertEquals(30, LifeService.peekBuyCoin(LifeService.ROD_OFFER_ID, d, 60));
        assertEquals(20, LifeService.peekBuyCoin(LifeService.BREAD_OFFER_ID, d, 40));
        assertEquals(5, LifeService.peekBuyCoin(LifeService.BREW_OFFER_ID, d, 10));
        assertEquals(2, LifeService.peekBuyCoin("soul_dust", d, 5));
        assertEquals(15, LifeService.peekBuyCoin("reforge_stone", d, 30));
        assertEquals(40, LifeService.peekBuyCoin("offhand_ward", d, 80));
        assertEquals(150, LifeService.peekBuyCoin("stable_charm", d, 300));
        assertEquals(300, LifeService.peekBuyCoin("pet_ashling", d, 600));
        assertEquals(99, LifeService.peekBuyCoin("unknown", d, 99)); // non-soft
        assertTrue(LifeService.buySoftReady(LifeService.ROD_OFFER_ID, d));
        assertEquals(30, LifeService.consumeBuyCoin(LifeService.ROD_OFFER_ID, d, 60));
        assertFalse(LifeService.buySoftReady(LifeService.ROD_OFFER_ID, d));
    }
}
