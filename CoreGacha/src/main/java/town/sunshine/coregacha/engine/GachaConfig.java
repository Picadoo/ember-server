package town.sunshine.coregacha.engine;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * gacha.yml parsed from a plain map (SnakeYAML output), so the plugin, the unit tests and the offline simulator read
 * the very same file the same way.
 */
public final class GachaConfig {
    public double baseLegend, baseEpic, baseRare;
    public int softStart, hardPity, tier2Every, spark, dailyPullCap, costPerPull;
    public double softStep;
    public boolean noRepeatLegend;
    public final Map<Tier, Integer> dupShards = new EnumMap<Tier, Integer>(Tier.class);
    public final Map<Tier, Integer> craftPrice = new EnumMap<Tier, Integer>(Tier.class);
    public int sparkLeftoverShard;
    public final Map<String, Item> items = new LinkedHashMap<String, Item>();
    public final Map<String, Banner> banners = new LinkedHashMap<String, Banner>();
    public final List<String> warnings = new ArrayList<String>();

    @SuppressWarnings("unchecked")
    static Map<String, Object> sec(Map<String, Object> m, String k) {
        Object o = m == null ? null : m.get(k);
        return o instanceof Map ? (Map<String, Object>) o : new LinkedHashMap<String, Object>();
    }

    static double d(Map<String, Object> m, String k, double def) {
        Object o = m.get(k);
        return o instanceof Number ? ((Number) o).doubleValue() : o == null ? def : Double.parseDouble(String.valueOf(o));
    }

    static int i(Map<String, Object> m, String k, int def) { return (int) Math.round(d(m, k, def)); }

    static String s(Map<String, Object> m, String k, String def) { Object o = m.get(k); return o == null ? def : String.valueOf(o); }

    static long time(Map<String, Object> m, String k) {
        Object o = m.get(k);
        if (o == null) return 0;
        if (o instanceof java.util.Date) return ((java.util.Date) o).getTime();
        return OffsetDateTime.parse(String.valueOf(o)).toInstant().toEpochMilli();
    }

    public static GachaConfig parse(Map<String, Object> root) {
        GachaConfig c = new GachaConfig();
        Map<String, Object> rates = sec(root, "rates");
        Map<String, Object> base = sec(rates, "base");
        c.baseLegend = d(base, "legend", 0.006);
        c.baseEpic = d(base, "epic", 0.051);
        c.baseRare = d(base, "rare", 0.143);
        Map<String, Object> soft = sec(rates, "soft_pity");
        c.softStart = i(soft, "start", 60);
        c.softStep = d(soft, "step", 0.04);
        c.hardPity = i(rates, "hard_pity", 80);
        c.tier2Every = i(rates, "tier2_every", 10);
        c.noRepeatLegend = !"false".equals(s(rates, "no_repeat_legend", "true"));
        c.spark = i(root, "spark", 200);
        c.dailyPullCap = i(root, "daily_pull_cap", 50);
        c.costPerPull = Math.max(1, i(root, "cost_per_pull", 1));
        Map<String, Object> sh = sec(root, "shards");
        Map<String, Object> dup = sec(sh, "dup"), craft = sec(sh, "craft");
        for (Tier t : Tier.values()) {
            c.dupShards.put(t, i(dup, t.key(), 0));
            c.craftPrice.put(t, i(craft, t.key(), 0));
        }
        c.sparkLeftoverShard = i(sh, "spark_leftover", 1);
        if (c.baseLegend + c.baseEpic + c.baseRare > 1.0) c.warnings.add("base rates sum > 1");
        if (c.hardPity <= c.softStart) c.warnings.add("hard_pity must be > soft_pity.start");

        for (Map.Entry<String, Object> e : sec(root, "items").entrySet()) {
            @SuppressWarnings("unchecked") Map<String, Object> m = e.getValue() instanceof Map ? (Map<String, Object>) e.getValue() : null;
            if (m == null) continue;
            Tier t = Tier.parse(s(m, "tier", null));
            String kind = s(m, "kind", "");
            if (t == null || !Item.KINDS.contains(kind)) { c.warnings.add("item " + e.getKey() + ": bad tier/kind, skipped"); continue; }
            Map<String, Object> props = new LinkedHashMap<String, Object>(m);
            c.items.put(e.getKey(), new Item(e.getKey(), s(m, "name", e.getKey()), t, kind, s(m, "ref", "corerpg".equals(kind) ? e.getKey() : null),
                    s(m, "icon", "PAPER"), props));
        }
        for (Map.Entry<String, Object> e : sec(root, "banners").entrySet()) {
            @SuppressWarnings("unchecked") Map<String, Object> m = e.getValue() instanceof Map ? (Map<String, Object>) e.getValue() : null;
            if (m == null) continue;
            Map<String, Double> w = new LinkedHashMap<String, Double>();
            for (Map.Entry<String, Object> it : sec(m, "items").entrySet()) {
                if (!c.items.containsKey(it.getKey())) { c.warnings.add("banner " + e.getKey() + ": unknown item " + it.getKey()); continue; }
                double wt = it.getValue() instanceof Number ? ((Number) it.getValue()).doubleValue() : 1.0;
                if (wt > 0) w.put(it.getKey(), wt);
            }
            String type = s(m, "type", "standard");
            c.banners.put(e.getKey(), new Banner(e.getKey(), s(m, "name", e.getKey()), type, s(m, "pity_group", "limited".equals(type) ? "limited" : "std"),
                    time(m, "start"), time(m, "end"), s(m, "retire_to", null), i(m, "retire_weight", 1), w, s(m, "icon", "CHEST"), s(m, "desc", "")));
        }
        return c;
    }

    public Banner banner(String id) { return id == null ? null : banners.get(id.toLowerCase(java.util.Locale.ROOT)); }

    /** Items of a banner at {@code now}: own weights, plus retired limited items for banners other banners retire into. */
    public Banner.Pool pool(Banner b, long now) {
        Banner.Pool p = new Banner.Pool();
        for (Tier t : Tier.values()) p.byTier.put(t, new LinkedHashMap<Item, Double>());
        for (Map.Entry<String, Double> e : b.weights.entrySet()) {
            Item it = items.get(e.getKey());
            if (it != null && !disabled.contains(it.id)) p.byTier.get(it.tier).put(it, e.getValue());
        }
        for (Banner o : banners.values()) {
            if (o == b || !o.limited() || !o.ended(now) || !b.id.equals(o.retireTo)) continue;
            for (String id : o.weights.keySet()) {
                Item it = items.get(id);
                if (it == null || disabled.contains(it.id)) continue;
                LinkedHashMap<Item, Double> m = p.byTier.get(it.tier);
                if (!m.containsKey(it)) m.put(it, (double) o.retireWeight);
            }
        }
        return p;
    }

    /** Items switched off at runtime (e.g. a CoreRpg shop id that no longer exists). */
    public final java.util.Set<String> disabled = new java.util.HashSet<String>();

    /** legendary probability of the k-th pull since the last legendary (k ≥ 1) */
    public double legendRate(int k) {
        if (k >= hardPity) return 1.0;
        if (k <= softStart) return baseLegend;
        return Math.min(1.0, baseLegend + softStep * (k - softStart));
    }
}
