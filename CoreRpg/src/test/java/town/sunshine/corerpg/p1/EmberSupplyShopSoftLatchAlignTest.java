package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-BA DESIGN §2.3: supply shop soft latch — peek gate; reject without consume;
 * consume only when n>0 before spend path.
 */
public class EmberSupplyShopSoftLatchAlignTest {

    /** Simulate cmd afford + soft consume (no Bukkit). */
    static int[] gateAndPay(PlayerData d, int coinHave, int tablePrice, int freeSlots, int want) {
        int priceShow = EmberSupplyShopSoft.peekPrice(d, tablePrice);
        int n = EmberSupplyService.affordable(want, coinHave, priceShow, freeSlots);
        if (n <= 0) {
            return new int[] {0, 0, priceShow, 0}; // refused, no consume
        }
        int pricePay = EmberSupplyShopSoft.apply(d, tablePrice, true);
        return new int[] {1, pricePay, priceShow, n};
    }

    @Test
    public void ready_coin3_table10_refuse_noConsume() {
        PlayerData d = new PlayerData();
        d.setCoin(3);
        assertEquals(5, EmberSupplyShopSoft.peekPrice(d, 10));
        int[] r = gateAndPay(d, 3, 10, 36, 1);
        assertEquals(0, r[0]);
        assertEquals(0, r[1]);
        assertEquals(5, r[2]);
        assertTrue(EmberSupplyShopSoft.ready(d));
        assertFalse(EmberSupplyShopSoft.usedToday(d));
    }

    @Test
    public void ready_coin20_freeSlots0_refuse_noConsume() {
        PlayerData d = new PlayerData();
        d.setCoin(20);
        assertEquals(5, EmberSupplyShopSoft.peekPrice(d, 10));
        int[] r = gateAndPay(d, 20, 10, 0, 1);
        assertEquals(0, r[0]);
        assertEquals(5, r[2]);
        assertTrue(EmberSupplyShopSoft.ready(d));
        assertFalse(EmberSupplyShopSoft.usedToday(d));
    }

    @Test
    public void ready_coin20_slotsOk_pays5_consumesOnce() {
        PlayerData d = new PlayerData();
        d.setCoin(20);
        int[] r = gateAndPay(d, 20, 10, 5, 1);
        assertEquals(1, r[0]);
        assertEquals(5, r[1]);
        assertEquals(5, r[2]);
        assertEquals(1, r[3]);
        assertFalse(EmberSupplyShopSoft.ready(d));
        assertTrue(EmberSupplyShopSoft.usedToday(d));
        // second buy full
        assertEquals(10, EmberSupplyShopSoft.peekPrice(d, 10));
        assertEquals(10, EmberSupplyShopSoft.apply(d, 10, true));
    }

    @Test
    public void used_coin20_fullPrice_noSecondSoft() {
        PlayerData d = new PlayerData();
        EmberSupplyShopSoft.apply(d, 10, true); // latch
        assertFalse(EmberSupplyShopSoft.ready(d));
        assertEquals(10, EmberSupplyShopSoft.peekPrice(d, 10));
        int[] r = gateAndPay(d, 20, 10, 5, 1);
        assertEquals(1, r[0]);
        assertEquals(10, r[1]);
        assertEquals(10, r[2]);
        assertFalse(EmberSupplyShopSoft.ready(d));
    }

    @Test
    public void peekPrice_neverLatches_equivApplyFalse() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberSupplyShopSoft.peekPrice(d, 10));
        assertEquals(5, EmberSupplyShopSoft.peekPrice(d, 10));
        assertEquals(5, EmberSupplyShopSoft.apply(d, 10, false));
        assertTrue(EmberSupplyShopSoft.ready(d));
        assertFalse(EmberSupplyShopSoft.usedToday(d));
        assertEquals(0, d.periodCount(EmberSupplyShopSoft.C_USED, "day"));
    }

    @Test
    public void multAndUsedKeys_unchanged() {
        assertEquals(0.5, EmberSupplyShopSoft.MULT, 0.0);
        assertEquals("p1_supply_shop_soft", EmberSupplyShopSoft.C_USED);
    }
}
