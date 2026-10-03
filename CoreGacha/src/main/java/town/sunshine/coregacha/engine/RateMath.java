package town.sunshine.coregacha.engine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exact published rates (design §3.3). Tier rates = stationary distribution of the (pity5, pity4) Markov chain;
 * 传说 item shares under the no-repeat rule = stationary distribution of the item chain i→j ∝ w_j (j ≠ i).
 * Independent of {@link Engine} on purpose: the simulator checks one against the other.
 */
public final class RateMath {
    public final Map<Tier, Double> effective = new EnumMap<Tier, Double>(Tier.class);
    public double expectedPullsPerLegend, pWithinSoft, pHard;
    public final int maxPullsPerLegend;

    public RateMath(GachaConfig c) {
        int H = c.hardPity, T = c.tier2Every, n = H * T;
        double[][] next = new double[n][]; // sparse: per state up to 3 targets
        int[][] to = new int[n][];
        double[][] outTier = new double[n][4];
        for (int a = 0; a < H; a++) for (int b = 0; b < T; b++) {
            int s = a * T + b, k = a + 1;
            double q5 = c.legendRate(k), rest = 1 - q5;
            int a1 = Math.min(a + 1, H - 1);
            List<Integer> ts = new ArrayList<Integer>(); List<Double> ps = new ArrayList<Double>();
            ts.add(0); ps.add(q5); outTier[s][0] += q5;
            if (b + 1 >= T) { ts.add(a1 * T); ps.add(rest); outTier[s][1] += rest; }
            else {
                double q4 = Math.min(c.baseEpic, rest), q3 = Math.min(c.baseRare, rest - q4), q1 = rest - q4 - q3;
                ts.add(a1 * T); ps.add(q4); outTier[s][1] += q4;
                ts.add(a1 * T + b + 1); ps.add(q3 + q1); outTier[s][2] += q3; outTier[s][3] += q1;
            }
            to[s] = new int[ts.size()]; next[s] = new double[ps.size()];
            for (int i = 0; i < ts.size(); i++) { to[s][i] = ts.get(i); next[s][i] = ps.get(i); }
        }
        double[] pi = new double[n];
        pi[0] = 1;
        for (int it = 0; it < 20000; it++) {
            double[] q = new double[n];
            for (int s = 0; s < n; s++) if (pi[s] != 0) for (int i = 0; i < to[s].length; i++) q[to[s][i]] += pi[s] * next[s][i];
            double diff = 0;
            for (int s = 0; s < n; s++) diff += Math.abs(q[s] - pi[s]);
            // average two steps to kill any periodicity, then stop when settled
            for (int s = 0; s < n; s++) pi[s] = 0.5 * (q[s] + pi[s]);
            if (diff < 1e-15) break;
        }
        double[] tier = new double[4];
        for (int s = 0; s < n; s++) for (int t = 0; t < 4; t++) tier[t] += pi[s] * outTier[s][t];
        effective.put(Tier.LEGEND, tier[0]); effective.put(Tier.EPIC, tier[1]); effective.put(Tier.RARE, tier[2]); effective.put(Tier.COMMON, tier[3]);
        double surv = 1, e = 0, within = 0, last = 0;
        int max = 0;
        for (int k = 1; k <= H; k++) {
            double q = c.legendRate(k), pk = surv * q;
            e += k * pk;
            if (k <= c.softStart) within += pk;
            if (pk > 0) max = k;
            last = pk;
            surv *= 1 - q;
        }
        expectedPullsPerLegend = e; pWithinSoft = within; pHard = last; maxPullsPerLegend = max;
    }

    /** share of each item inside its tier (sums to 1 per tier) */
    public static Map<Item, Double> shares(Banner.Pool pool, boolean noRepeat) {
        Map<Item, Double> out = new LinkedHashMap<Item, Double>();
        for (Tier t : Tier.values()) {
            LinkedHashMap<Item, Double> m = pool.tier(t);
            double W = 0;
            for (double w : m.values()) W += w;
            if (m.isEmpty()) continue;
            if (t != Tier.LEGEND || !noRepeat || m.size() < 2) {
                for (Map.Entry<Item, Double> e : m.entrySet()) out.put(e.getKey(), e.getValue() / W);
                continue;
            }
            List<Item> items = new ArrayList<Item>(m.keySet());
            int n = items.size();
            double[] w = new double[n];
            for (int i = 0; i < n; i++) w[i] = m.get(items.get(i));
            double[] pi = new double[n];
            for (int i = 0; i < n; i++) pi[i] = 1.0 / n;
            for (int it = 0; it < 100000; it++) {
                double[] q = new double[n];
                for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (i != j) q[j] += pi[i] * w[j] / (W - w[i]);
                double diff = 0;
                for (int i = 0; i < n; i++) { diff += Math.abs(q[i] - pi[i]); pi[i] = 0.5 * (q[i] + pi[i]); }
                if (diff < 1e-15) break;
            }
            for (int i = 0; i < n; i++) out.put(items.get(i), pi[i]);
        }
        return out;
    }

    /** long-run probability that one pull on this pool is this item */
    public double itemRate(Item it, Map<Item, Double> shares) {
        Double s = shares.get(it);
        return s == null ? 0 : s * effective.get(it.tier);
    }

    public static String pct(double p) {
        if (p >= 0.1) return String.format(java.util.Locale.ROOT, "%.2f%%", p * 100);
        if (p >= 0.001) return String.format(java.util.Locale.ROOT, "%.3f%%", p * 100);
        return String.format(java.util.Locale.ROOT, "%.4f%%", p * 100);
    }
}
