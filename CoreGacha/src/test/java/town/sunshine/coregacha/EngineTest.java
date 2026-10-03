package town.sunshine.coregacha;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.Engine;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.PityState;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Result;
import town.sunshine.coregacha.engine.Tier;

public class EngineTest {
    @SuppressWarnings("unchecked")
    static GachaConfig cfg() {
        return GachaConfig.parse((Map<String, Object>) new Yaml().load(new InputStreamReader(
                EngineTest.class.getResourceAsStream("/gacha.yml"), StandardCharsets.UTF_8)));
    }

    static final long OPEN = java.time.OffsetDateTime.parse("2026-10-04T12:00:00+08:00").toInstant().toEpochMilli();
    static final long AFTER = java.time.OffsetDateTime.parse("2026-10-08T00:00:00+08:00").toInstant().toEpochMilli();

    @Test public void configLoadsCleanAndCosmeticOnly() {
        GachaConfig c = cfg();
        assertTrue(c.warnings.toString(), c.warnings.isEmpty());
        for (Item i : c.items.values()) assertTrue(i.id, Item.KINDS.contains(i.kind));
        assertEquals(32, c.pool(c.banner("standard"), OPEN).all().size());
        assertEquals(38, c.pool(c.banner("standard"), AFTER).all().size()); // gq26 retired into the standard pool
        assertTrue(c.banner("gq26").open(OPEN));
        assertTrue(!c.banner("gq26").open(AFTER));
    }

    @Test public void legendRateCurve() {
        GachaConfig c = cfg();
        assertEquals(0.006, c.legendRate(1), 1e-12);
        assertEquals(0.006, c.legendRate(60), 1e-12);
        assertEquals(0.046, c.legendRate(61), 1e-12);
        assertEquals(0.766, c.legendRate(79), 1e-12);
        assertEquals(1.0, c.legendRate(80), 1e-12);
    }

    @Test public void hardPityAt80() {
        GachaConfig c = cfg();
        Engine e = new Engine(c);
        Banner.Pool pool = c.pool(c.banner("standard"), OPEN);
        PityState st = new PityState(79, 3, null, 0);
        Result r = e.pull(pool, st, new HashSet<String>(), () -> 0.999999);
        assertEquals(Tier.LEGEND, r.tier);
        assertEquals("hard", r.rule);
        assertEquals(79, r.pity5Before);
        assertEquals(0, r.pity5After);
        assertEquals(0, r.pity4After);
    }

    @Test public void tier2GuaranteeOnTenth() {
        GachaConfig c = cfg();
        Engine e = new Engine(c);
        Banner.Pool pool = c.pool(c.banner("standard"), OPEN);
        PityState st = new PityState();
        Set<String> owned = new HashSet<String>();
        for (int i = 0; i < 9; i++) assertEquals(Tier.COMMON, e.pull(pool, st, owned, () -> 0.99).tier);
        Result r = e.pull(pool, st, owned, () -> 0.99);
        assertEquals(Tier.EPIC, r.tier);
        assertEquals("tier2", r.rule);
        assertEquals(10, r.pity5After);
        assertEquals(0, r.pity4After);
    }

    @Test public void noSameLegendTwiceAndDupShards() {
        GachaConfig c = cfg();
        Engine e = new Engine(c);
        Banner.Pool pool = c.pool(c.banner("gq26"), OPEN);
        PityState st = new PityState();
        Set<String> owned = new HashSet<String>();
        String last = null;
        for (int i = 0; i < 200; i++) {
            st.pity5 = 79; // force a legend every pull
            Result r = e.pull(pool, st, owned, () -> 0.0);
            assertEquals(Tier.LEGEND, r.tier);
            if (last != null) assertNotEquals(last, r.item.id);
            if (r.dup) assertEquals(300, r.shards); else assertEquals(0, r.shards);
            last = r.item.id;
        }
        assertEquals(200, st.spark);
    }

    @Test public void publishedRatesMatchAnalytic() {
        RateMath m = new RateMath(cfg());
        assertEquals(1.0 / m.expectedPullsPerLegend, m.effective.get(Tier.LEGEND), 1e-9); // renewal theorem
        double sum = 0;
        for (double v : m.effective.values()) sum += v;
        assertEquals(1.0, sum, 1e-9);
        assertEquals(0.018325, m.effective.get(Tier.LEGEND), 2e-6);
        assertEquals(0.116924, m.effective.get(Tier.EPIC), 2e-6);
        assertEquals(80, m.maxPullsPerLegend);
    }

    @Test public void itemSharesSumToOneAndNoRepeatShrinksTheBigOnes() {
        GachaConfig c = cfg();
        Banner.Pool pool = c.pool(c.banner("gq26"), OPEN);
        Map<Item, Double> sh = RateMath.shares(pool, true);
        double leg = 0;
        for (Item i : pool.tier(Tier.LEGEND).keySet()) leg += sh.get(i);
        assertEquals(1.0, leg, 1e-9);
        double koi = sh.get(c.items.get("gq_pet_koi"));
        assertTrue("no-repeat lowers a 35% weight below 35%: " + koi, koi < 0.35 && koi > 0.25);
    }

    @Test public void observedMatchesPublishedShortRun() {
        GachaConfig c = cfg();
        RateMath m = new RateMath(c);
        town.sunshine.coregacha.sim.Simulator s = town.sunshine.coregacha.sim.Simulator.run(c, c.banner("standard"), OPEN, 200_000, 7);
        double p = m.effective.get(Tier.LEGEND);
        assertEquals(p, s.rate(Tier.LEGEND), 5 * Math.sqrt(p * (1 - p) / 200_000));
        assertTrue(s.maxLegendGap <= 80);
        assertTrue(s.maxEpicGap <= 10);
        assertEquals(0, s.sameLegendTwice);
        new SplittableRandom(1); // keep import used
    }
}
