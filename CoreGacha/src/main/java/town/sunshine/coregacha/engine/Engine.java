package town.sunshine.coregacha.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;

/**
 * The pull rules (design §3), pure Java: soft pity 1–softStart flat then +step per pull, hard pity, 史诗-or-better every
 * tier2Every pulls, no two identical 传说 in a row on a banner, spark +1 per pull, duplicates → 光屑.
 * The same class runs live, in the unit tests and in the 1,000,000-pull simulator.
 */
public final class Engine {
    private final GachaConfig cfg;

    public Engine(GachaConfig cfg) { this.cfg = cfg; }

    public GachaConfig config() { return cfg; }

    /**
     * One pull. {@code owned} is updated (a second copy inside the same batch is a duplicate). {@code st} is advanced.
     * {@code rng} returns uniform [0,1).
     */
    public Result pull(Banner.Pool pool, PityState st, Set<String> owned, DoubleSupplier rng) {
        int p5b = st.pity5, p4b = st.pity4;
        int k = st.pity5 + 1;
        double p5 = cfg.legendRate(k);
        double r = rng.getAsDouble();
        Tier tier;
        String rule = "base";
        if (k >= cfg.hardPity) { tier = Tier.LEGEND; rule = "hard"; }
        else if (r < p5) { tier = Tier.LEGEND; rule = k > cfg.softStart ? "soft" : "base"; }
        else if (st.pity4 + 1 >= cfg.tier2Every) { tier = Tier.EPIC; rule = "tier2"; }
        else if (r < p5 + cfg.baseEpic) tier = Tier.EPIC;
        else if (r < p5 + cfg.baseEpic + cfg.baseRare) tier = Tier.RARE;
        else tier = Tier.COMMON;
        tier = available(pool, tier);
        Item item = pick(pool.tier(tier), tier == Tier.LEGEND && cfg.noRepeatLegend ? st.lastLegend : null, rng.getAsDouble());
        if (tier == Tier.LEGEND) { st.pity5 = 0; st.pity4 = 0; st.lastLegend = item.id; }
        else if (tier == Tier.EPIC) { st.pity5++; st.pity4 = 0; }
        else { st.pity5++; st.pity4++; }
        if (st.pity5 >= cfg.hardPity) st.pity5 = cfg.hardPity - 1; // never reached (hard pity forces a legend first)
        st.spark++;
        boolean dup = !owned.add(item.id);
        return new Result(tier, item, dup, dup ? cfg.dupShards.get(tier) : 0, p5b, st.pity5, p4b, st.pity4, st.spark, rule);
    }

    /** a tier with no items in this pool falls to the next lower tier that has some (config guard; shipped pools are full) */
    static Tier available(Banner.Pool pool, Tier t) {
        Tier[] order = Tier.values();
        for (int i = t.ordinal(); i < order.length; i++) if (!pool.tier(order[i]).isEmpty()) return order[i];
        for (int i = t.ordinal() - 1; i >= 0; i--) if (!pool.tier(order[i]).isEmpty()) return order[i];
        throw new IllegalStateException("empty pool");
    }

    /** weighted pick; {@code exclude} is skipped when the tier has more than one item */
    static Item pick(LinkedHashMap<Item, Double> items, String exclude, double r) {
        double total = 0;
        boolean ex = exclude != null && items.size() > 1;
        for (Map.Entry<Item, Double> e : items.entrySet()) if (!(ex && e.getKey().id.equals(exclude))) total += e.getValue();
        double x = r * total;
        Item last = null;
        for (Map.Entry<Item, Double> e : items.entrySet()) {
            if (ex && e.getKey().id.equals(exclude)) continue;
            last = e.getKey();
            x -= e.getValue();
            if (x < 0) return e.getKey();
        }
        return last;
    }

    public List<Result> pullN(Banner.Pool pool, PityState st, Set<String> owned, int n, DoubleSupplier rng) {
        List<Result> out = new ArrayList<Result>(n);
        for (int i = 0; i < n; i++) out.add(pull(pool, st, owned, rng));
        return out;
    }
}
