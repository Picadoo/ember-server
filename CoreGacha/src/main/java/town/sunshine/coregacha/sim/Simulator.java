package town.sunshine.coregacha.sim;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.Engine;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.PityState;
import town.sunshine.coregacha.engine.Result;
import town.sunshine.coregacha.engine.Tier;

/** One long pull stream through the real {@link Engine}; collects what the report and /gacha admin simulate need. */
public final class Simulator {
    public final Map<Tier, Long> tierCount = new EnumMap<Tier, Long>(Tier.class);
    public final Map<String, Long> legendCount = new LinkedHashMap<String, Long>();
    public final Map<String, Long> itemCount = new LinkedHashMap<String, Long>();
    public final Map<String, Long> ruleCount = new LinkedHashMap<String, Long>();
    public long n, legendTotal, maxLegendGap, maxEpicGap, sameLegendTwice, gapSum, pity5Overflow, tier2Violations;
    public long[] gapHist;

    public static Simulator run(GachaConfig cfg, Banner b, long now, long n, long seed) {
        Simulator s = new Simulator();
        Engine e = new Engine(cfg);
        Banner.Pool pool = cfg.pool(b, now);
        PityState st = new PityState();
        Set<String> owned = new HashSet<String>();
        SplittableRandom rng = new SplittableRandom(seed);
        for (Tier t : Tier.values()) s.tierCount.put(t, 0L);
        s.gapHist = new long[cfg.hardPity + 2];
        long sinceLegend = 0, sinceEpic = 0;
        String lastLegend = null;
        for (long i = 0; i < n; i++) {
            Result r = e.pull(pool, st, owned, rng::nextDouble);
            if (owned.size() > 64) owned.clear(); // the rate stream does not care about duplicates
            s.n++;
            s.tierCount.put(r.tier, s.tierCount.get(r.tier) + 1);
            s.itemCount.merge(r.item.id, 1L, Long::sum);
            s.ruleCount.merge(r.rule, 1L, Long::sum);
            sinceLegend++; sinceEpic++;
            if (r.tier == Tier.LEGEND) {
                s.legendTotal++;
                s.legendCount.merge(r.item.id, 1L, Long::sum);
                s.maxLegendGap = Math.max(s.maxLegendGap, sinceLegend);
                s.gapSum += sinceLegend;
                s.gapHist[(int) Math.min(sinceLegend, s.gapHist.length - 1)]++;
                if (r.item.id.equals(lastLegend)) s.sameLegendTwice++;
                lastLegend = r.item.id;
                sinceLegend = 0;
            }
            if (r.tier == Tier.LEGEND || r.tier == Tier.EPIC) {
                s.maxEpicGap = Math.max(s.maxEpicGap, sinceEpic);
                sinceEpic = 0;
            }
            if (sinceEpic >= cfg.tier2Every) s.tier2Violations++;
            if (r.pity5After >= cfg.hardPity) s.pity5Overflow++;
        }
        return s;
    }

    public double rate(Tier t) { return n == 0 ? 0 : tierCount.get(t) / (double) n; }
}
