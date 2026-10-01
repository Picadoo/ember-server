package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Plan;
import town.sunshine.corerpg.p1.EmberUpgradeRules.SwapPlan;

import java.util.Random;

import static org.junit.Assert.*;

/** G03: 策划书 §5.3, §6.1–6.4 and §22.3 E05–E09. */
public class EmberUpgradeRulesTest {

    private static EmberItemData item(String fam, String slot, int tier, int q, int craft, int enh, String src) {
        return EmberItemData.create(fam, slot, tier, q, craft, enh, false, src);
    }

    // ---------------------------------------------------------------- E06

    @Test public void alwaysFailingReachesPityExactlyOnTheNthAttempt_E06() {
        EmberItemData d = item("burst", "blade", 2, 0, 0, 0, "drop");
        int[] attempts = new int[11];
        int shards = 0, cores = 0, coins = 0;
        while (d.enhance < 10) {
            int target = d.enhance + 1;
            Plan p = EmberUpgradeRules.enhance(d, 0.999999); // worst luck: every roll fails unless guaranteed
            assertTrue(p.ok());
            shards += p.cost.shards; cores += p.cost.cores; coins += p.cost.coins;
            attempts[target]++;
            assertEquals(d.rev + 1, p.after.rev);
            assertTrue("never downgrades", p.after.enhance >= d.enhance);
            if (p.after.enhance == target) assertEquals("the Nth try is the success", EmberUpgradeRules.maxTries(target), attempts[target]);
            else assertEquals(d.pity + 1, p.after.pity);
            d = p.after;
        }
        int total = 0;
        for (int t = 1; t <= 10; t++) { assertEquals(EmberUpgradeRules.maxTries(t), attempts[t]); total += attempts[t]; }
        assertEquals(51, total);
        assertEquals(2132, shards);
        assertEquals(157, cores);
        assertEquals(29720, coins);
        assertEquals(0, d.pity);
    }

    @Test public void firstThreeAlwaysSucceed_andGuaranteeFlag() {
        EmberItemData d = item("scorch", "charm", 1, 0, 0, 0, "drop");
        for (int i = 0; i < 3; i++) {
            assertTrue(EmberUpgradeRules.guaranteed(d));
            d = EmberUpgradeRules.enhance(d, 0.999).after;
        }
        assertEquals(3, d.enhance);
        assertFalse(EmberUpgradeRules.guaranteed(d));
        d = EmberUpgradeRules.enhance(d, 0.9).after;  // 85% → fail
        d = EmberUpgradeRules.enhance(d, 0.9).after;  // fail #2
        assertEquals(2, d.pity);
        assertTrue(EmberUpgradeRules.guaranteed(d));   // 3rd attempt of 3
        Plan p = EmberUpgradeRules.enhance(d, 0.9);
        assertEquals(4, p.after.enhance);
        assertTrue(p.note.contains("保底"));
        assertEquals(0, p.after.pity);
    }

    @Test public void successResetsPityAndEachTargetHasItsOwnCount() {
        EmberItemData d = item("burst", "blade", 3, 0, 0, 4, "drop");
        d = EmberUpgradeRules.enhance(d, 0.99).after;
        assertEquals(1, d.pity);
        d = EmberUpgradeRules.enhance(d, 0.10).after; // 70% → success
        assertEquals(5, d.enhance);
        assertEquals(0, d.pity);
    }

    @Test public void maxedOrCorruptCannotEnhance_E07_C16() {
        assertFalse(EmberUpgradeRules.enhanceCheck(item("burst", "blade", 3, 0, 0, 10, "drop")).ok());
        assertFalse(EmberUpgradeRules.enhance(item("burst", "blade", 3, 0, 0, 4, "drop"), Double.NaN).ok());
        assertFalse(EmberUpgradeRules.enhance(item("burst", "blade", 3, 0, 0, 4, "drop"), 1.0).ok());
        EmberItemData bad = new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t3", "burst", "blade", 3, 0, 0, 4, 3,
                false, "drop", 1, 0); // pity 3 at +5 is fine (max 4); pity 4 is not
        assertTrue(EmberUpgradeRules.enhanceCheck(bad).ok());
        EmberItemData bad2 = new EmberItemData(bad.uid, bad.ni, "burst", "blade", 3, 0, 0, 4, 4, false, "drop", 1, 0);
        assertFalse(EmberUpgradeRules.enhanceCheck(bad2).ok());
        assertFalse(EmberUpgradeRules.enhanceCheck(null).ok());
    }

    @Test public void costTableMatchesBook() {
        assertEquals(new Cost(4, 0, 0, 0, 40), EmberUpgradeRules.enhanceCost(0));
        assertEquals(new Cost(16, 1, 0, 0, 180), EmberUpgradeRules.enhanceCost(4));
        assertEquals(new Cost(72, 6, 0, 0, 1080), EmberUpgradeRules.enhanceCost(9));
        assertNull(EmberUpgradeRules.enhanceCost(10));
    }

    // ---------------------------------------------------------------- E08

    @Test public void swapTwiceRestoresAndBindsBoth_E08() {
        EmberItemData oldB = new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 1, 2, 8, 3, false, "drop", 1, 4);
        EmberItemData newB = item("sustain", "blade", 3, 3, 0, 2, "drop");
        SwapPlan s = EmberUpgradeRules.swap(oldB, newB);
        assertTrue(s.ok());
        assertEquals(2, s.a.enhance); assertEquals(0, s.a.pity);
        assertEquals(8, s.b.enhance); assertEquals(3, s.b.pity);
        assertTrue(s.a.bound && s.b.bound);
        // everything else unchanged
        assertEquals("burst", s.a.family); assertEquals(2, s.a.tier); assertEquals(1, s.a.quality); assertEquals(2, s.a.craft);
        assertEquals("sustain", s.b.family); assertEquals(3, s.b.tier); assertEquals(3, s.b.quality);
        assertEquals(oldB.rev + 1, s.a.rev); assertEquals(newB.rev + 1, s.b.rev);
        // total enhancement is conserved (no duplication)
        assertEquals(oldB.enhance + newB.enhance, s.a.enhance + s.b.enhance);
        SwapPlan back = EmberUpgradeRules.swap(s.a, s.b);
        assertEquals(8, back.a.enhance); assertEquals(3, back.a.pity);
        assertEquals(2, back.b.enhance);
    }

    @Test public void swapRefusesOtherSlotOrSelf_E08() {
        EmberItemData b = item("burst", "blade", 2, 0, 0, 8, "drop");
        assertFalse(EmberUpgradeRules.swap(b, item("burst", "charm", 2, 0, 0, 0, "drop")).ok());
        assertFalse(EmberUpgradeRules.swap(b, b).ok());
        assertFalse(EmberUpgradeRules.swap(b, null).ok());
    }

    @Test public void swapReplayIsANoOp_E08() {
        // the idempotency ledger lives in cr_p1_txn; modelled here as a request-id → result map
        java.util.Map<String, SwapPlan> ledger = new java.util.HashMap<String, SwapPlan>();
        EmberItemData a = item("burst", "blade", 2, 0, 0, 8, "drop");
        EmberItemData b = item("burst", "blade", 3, 0, 0, 1, "drop");
        String rid = "req-1";
        for (int i = 0; i < 3; i++) {
            SwapPlan s = ledger.get(rid);
            if (s == null) { s = EmberUpgradeRules.swap(a, b); ledger.put(rid, s); a = s.a; b = s.b; }
        }
        assertEquals(1, a.enhance);
        assertEquals(8, b.enhance);
    }

    // ---------------------------------------------------------------- E09

    @Test public void upgradeKeepsEverything_E09() {
        EmberItemData d = new EmberItemData(EmberItemData.newUid(), "ember_v1_scorch_charm_t1", "scorch", "charm", 1, 2, 3, 8, 5, true, "quest", 1, 7);
        assertFalse(EmberUpgradeRules.upgrade(d, false).ok());
        Plan p = EmberUpgradeRules.upgrade(d, true);
        assertTrue(p.ok());
        assertEquals(new Cost(60, 12, 6, 0, 1500), p.cost);
        EmberItemData u = p.after;
        assertEquals(d.uid, u.uid);
        assertEquals("ember_v1_scorch_charm_t2", u.ni);
        assertEquals(2, u.tier); assertEquals(8, u.enhance); assertEquals(5, u.pity);
        assertEquals(2, u.quality); assertEquals(3, u.craft); assertTrue(u.bound);
        assertNull(u.validate());
        Plan p2 = EmberUpgradeRules.upgrade(u, true);
        assertEquals(new Cost(120, 30, 12, 0, 3600), p2.cost);
        assertEquals("ember_v1_scorch_charm_t3", p2.after.ni);
        assertFalse(EmberUpgradeRules.upgrade(p2.after, true).ok());
        assertFalse(EmberUpgradeRules.upgrade(item("none", "blade", 0, 0, 0, 0, "quest"), true).ok());
        assertEquals("q04", EmberUpgradeRules.upgradeFlag(1));
        assertEquals("q07", EmberUpgradeRules.upgradeFlag(2));
    }

    @Test public void upgradeAlwaysRaisesBaseStat_E09() {
        EmberTables t = EmberTables.defaults();
        for (int q = 0; q <= 3; q++) for (int f = 0; f <= 3; f++) for (int e = 0; e <= 10; e++) for (int tier = 1; tier <= 2; tier++) {
            EmberItemData blade = item("burst", "blade", tier, q, f, e, "drop");
            EmberItemData charm = item("burst", "charm", tier, q, f, e, "drop");
            EmberItemData ub = EmberUpgradeRules.upgrade(blade, true).after, uc = EmberUpgradeRules.upgrade(charm, true).after;
            EmberLoadout before = EmberLoadout.compute(t, blade, charm, 30), after = EmberLoadout.compute(t, ub, uc, 30);
            assertTrue(after.b > before.b);
            assertTrue(after.h > before.h);
        }
    }

    @Test public void refineAndQualityKeepInvestmentAndPreviewEqualsResult_E09() {
        EmberItemData d = new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, 7, 2, true, "drop", 1, 3);
        Cost[] craftCosts = {new Cost(0, 0, 3, 5, 300), new Cost(0, 0, 6, 10, 600), new Cost(0, 0, 12, 20, 1200)};
        for (int i = 0; i < 3; i++) {
            Plan preview = EmberUpgradeRules.refine(d);
            Plan real = EmberUpgradeRules.refine(d);
            assertEquals(preview.after, real.after); // deterministic: the preview is the result
            assertEquals(craftCosts[i], real.cost);
            assertEquals(d.quality, real.after.quality);
            assertEquals(7, real.after.enhance); assertEquals(2, real.after.pity);
            d = real.after;
        }
        assertEquals(3, d.craft);
        assertFalse(EmberUpgradeRules.refine(d).ok());
        Plan q1 = EmberUpgradeRules.quality(d);
        assertEquals(new Cost(0, 0, 8, 8, 800), q1.cost);
        Plan q2 = EmberUpgradeRules.quality(q1.after);
        assertEquals(new Cost(0, 0, 16, 16, 1600), q2.cost);
        assertEquals(2, q2.after.quality);
        assertEquals(3, q2.after.craft);
        assertFalse("极品 only drops", EmberUpgradeRules.quality(q2.after).ok());
    }

    // ---------------------------------------------------------------- E05 dismantle

    @Test public void dismantleOnlyNormalDrops_E05() {
        assertEquals(1, EmberUpgradeRules.dismantleYield(item("burst", "blade", 1, 0, 0, 9, "drop")));
        assertEquals(2, EmberUpgradeRules.dismantleYield(item("burst", "blade", 2, 3, 3, 0, "drop")));
        assertEquals(3, EmberUpgradeRules.dismantleYield(item("burst", "charm", 3, 0, 0, 0, "drop")));
        for (String src : new String[]{"quest", "admin", "reissue", "migrate"}) {
            EmberItemData d = item("burst", "blade", 3, 0, 0, 0, src);
            assertEquals(0, EmberUpgradeRules.dismantleYield(d));
            assertNotNull(EmberUpgradeRules.dismantleCheck(d));
        }
        assertNotNull(EmberUpgradeRules.dismantleCheck(item("none", "blade", 0, 0, 0, 0, "drop")));
    }

    // ---------------------------------------------------------------- §6.2 pity model

    @Test public void exactDistributionReproducesBook_6_2() {
        // per target: P(k) = (1-p)^(k-1) p for k < N, remainder at N; convolve all ten targets
        double[] dist = {1.0};
        double eShards = 0, eCores = 0, eCoins = 0;
        for (int t = 1; t <= 10; t++) {
            double p = EmberUpgradeRules.rate(t);
            int n = EmberUpgradeRules.maxTries(t);
            double[] step = new double[n + 1];
            double fail = 1.0, mean = 0;
            for (int k = 1; k < n; k++) { step[k] = fail * p; mean += k * step[k]; fail *= 1 - p; }
            step[n] = fail; mean += n * fail;
            eShards += mean * EmberUpgradeRules.SHARDS[t];
            eCores += mean * EmberUpgradeRules.CORES[t];
            eCoins += mean * EmberUpgradeRules.COINS[t];
            double[] nd = new double[dist.length + n];
            for (int i = 0; i < dist.length; i++) for (int k = 1; k <= n; k++) nd[i + k] += dist[i] * step[k];
            dist = nd;
        }
        double mean = 0, cum = 0;
        int median = -1, p90 = -1, p95 = -1, worst = 0;
        for (int i = 0; i < dist.length; i++) {
            mean += i * dist[i];
            cum += dist[i];
            if (median < 0 && cum >= 0.5) median = i;
            if (p90 < 0 && cum >= 0.9) p90 = i;
            if (p95 < 0 && cum >= 0.95) p95 = i;
            if (dist[i] > 0) worst = i;
        }
        assertEquals(22.784, mean, 0.0005);
        assertEquals(22, median);
        assertEquals(31, p90);
        assertEquals(33, p95);
        assertEquals(51, worst);
        assertEquals(927.87, eShards, 0.005);
        assertEquals(68.37, eCores, 0.005);
        assertEquals(12984.12, eCoins, 0.005);
    }

    @Test public void seededMonteCarloThroughTheRealRules_6_2() {
        Random rng = new Random(20261001L);
        int runs = 200_000;
        long sum = 0, shards = 0;
        int worst = 0;
        int[] hist = new int[60];
        for (int r = 0; r < runs; r++) {
            EmberItemData d = item("burst", "blade", 3, 0, 0, 0, "drop");
            int n = 0;
            while (d.enhance < 10) {
                Plan p = EmberUpgradeRules.enhance(d, rng.nextDouble());
                shards += p.cost.shards;
                d = p.after;
                n++;
            }
            sum += n;
            hist[n]++;
            worst = Math.max(worst, n);
        }
        double mean = sum / (double) runs;
        assertEquals(22.784, mean, 0.05);
        assertEquals(927.87, shards / (double) runs, 2.0);
        assertTrue(worst <= 51);
        int cum = 0, median = -1;
        for (int i = 0; i < hist.length; i++) { cum += hist[i]; if (cum >= runs / 2) { median = i; break; } }
        assertEquals(22, median);
    }
}
