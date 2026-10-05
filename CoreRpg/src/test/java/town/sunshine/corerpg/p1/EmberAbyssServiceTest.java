package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.PlayerData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * D231 / ARCH S3-2: fixed-seed (no RNG) proofs that abyss fee / floor grants match
 * the pre-extract behaviour and the bundled {@code ember-v1-runs.yml} amounts (bv58).
 */
public final class EmberAbyssServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberAbyssServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void feeMarksUsesBundledRateAndReserve_unchanged() {
        EmberRunMaps maps = bundled();
        assertEquals(200, maps.abyssFeeMarkCoin);
        assertEquals(8, EmberCosmetics.MARK_RESERVE);

        // tier 2 fee 60 → ceil(60/200)=1 mark; need owned >= 8+1
        assertEquals(0, EmberAbyssService.feeMarks(8, 60, 200, 8));
        assertEquals(1, EmberAbyssService.feeMarks(9, 60, 200, 8));
        // tier 10 fee 480 → ceil(480/200)=3 marks; need owned >= 11
        assertEquals(0, EmberAbyssService.feeMarks(10, 480, 200, 8));
        assertEquals(3, EmberAbyssService.feeMarks(11, 480, 200, 8));
        // off / zero fee
        assertEquals(0, EmberAbyssService.feeMarks(99, 0, 200, 8));
        assertEquals(0, EmberAbyssService.feeMarks(99, 60, 0, 8));
    }

    @Test public void applyFeeSpendPrefersCoinThenMarks_unchanged() {
        EmberRunMaps maps = bundled();
        int fee = maps.abyssTier(2).fee; // 60
        assertEquals(60, fee);

        PlayerData coin = new PlayerData();
        coin.addCoin(100);
        EmberAbyssService.FeeSpendResult r = EmberAbyssService.applyFeeSpend(coin, fee, maps.abyssFeeMarkCoin);
        assertTrue(r.ok);
        assertEquals(60, r.feeStored);
        assertEquals(0, r.marksSpent);
        assertEquals("coin:60", r.ledgerResult);
        assertEquals(40, coin.getCoin());

        PlayerData marks = new PlayerData();
        marks.addCoin(10); // short on coin
        marks.addPeriodCount(EmberEconomy.MARK_COUNTER + "3", "all", 12); // surplus 4 above reserve 8
        EmberAbyssService.FeeSpendResult m = EmberAbyssService.applyFeeSpend(marks, fee, maps.abyssFeeMarkCoin);
        assertTrue(m.ok);
        assertEquals(0, m.feeStored);
        assertEquals(1, m.marksSpent);
        assertEquals("mark:3:1", m.ledgerResult);
        assertEquals(10, marks.getCoin()); // untouched
        assertEquals(11, marks.periodCount(EmberEconomy.MARK_COUNTER + "3", "all"));

        PlayerData broke = new PlayerData();
        broke.addCoin(10);
        broke.addPeriodCount(EmberEconomy.MARK_COUNTER + "3", "all", 8); // no surplus
        EmberAbyssService.FeeSpendResult n = EmberAbyssService.applyFeeSpend(broke, fee, maps.abyssFeeMarkCoin);
        assertFalse(n.ok);
        assertNull(n.ledgerResult);
        assertEquals(10, broke.getCoin());
        assertEquals(8, broke.periodCount(EmberEconomy.MARK_COUNTER + "3", "all"));
    }

    @Test public void applyFeeRefundReturnsMarksOrCoin_unchanged() {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberEconomy.MARK_COUNTER + "3", "all", 5);
        EmberAbyssService.FeeRefundResult m = EmberAbyssService.applyFeeRefund(pd, "mark:3:2", 0);
        assertTrue(m.refunded);
        assertEquals(2, m.marksReturned);
        assertEquals(0, m.coinReturned);
        assertEquals(7, pd.periodCount(EmberEconomy.MARK_COUNTER + "3", "all"));

        PlayerData coin = new PlayerData();
        coin.addCoin(10);
        EmberAbyssService.FeeRefundResult c = EmberAbyssService.applyFeeRefund(coin, "coin:60", 60);
        assertTrue(c.refunded);
        assertEquals(60, c.coinReturned);
        assertEquals(0, c.marksReturned);
        assertEquals(70, coin.getCoin());
    }

    @Test public void applyFloorGrantOpensNextTier_unchanged() {
        PlayerData pd = new PlayerData();
        EmberAbyssService.FloorResult r0 = EmberAbyssService.applyFloorGrant(pd, 0);
        assertFalse(r0.newBest);
        assertEquals(0, r0.bestAfter);

        EmberAbyssService.FloorResult r1 = EmberAbyssService.applyFloorGrant(pd, 1);
        assertTrue(r1.newBest);
        assertEquals(0, r1.oldBest);
        assertEquals(1, r1.bestAfter);
        assertEquals(1, pd.periodCount(EmberAbyssService.C_ABYSS_BEST, "all"));

        // equal floor is no-op
        EmberAbyssService.FloorResult again = EmberAbyssService.applyFloorGrant(pd, 1);
        assertFalse(again.newBest);
        assertEquals(1, again.oldBest);
        assertEquals(1, again.bestAfter);

        // jump to 5 opens tier 6
        EmberAbyssService.FloorResult jump = EmberAbyssService.applyFloorGrant(pd, 5);
        assertTrue(jump.newBest);
        assertEquals(1, jump.oldBest);
        assertEquals(5, jump.bestAfter);
        assertEquals(5, pd.periodCount(EmberAbyssService.C_ABYSS_BEST, "all"));
    }

    @Test public void bundledTiersMatchFeeTable_forSpendTests() {
        EmberRunMaps maps = bundled();
        assertEquals(10, maps.abyss.size());
        assertEquals(0, maps.abyssTier(1).fee);
        assertEquals(60, maps.abyssTier(2).fee);
        assertEquals(480, maps.abyssTier(10).fee);
        // fee 0: applyFeeSpend is a no-op success path for callers that skip pfee>0
        PlayerData pd = new PlayerData();
        EmberAbyssService.FeeSpendResult z = EmberAbyssService.applyFeeSpend(pd, 0, maps.abyssFeeMarkCoin);
        assertFalse(z.ok); // fee<=0 → none (enter() skips when pfee==0)
    }

    @Test public void counterKeyOwnedByAbyssService() {
        EmberCounters.Family f = EmberCounters.byKey("p2_abyss_best");
        assertNotNull(f);
        assertEquals("EmberAbyssService", f.system);
    }
}
