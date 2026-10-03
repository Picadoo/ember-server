package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * D143 词条洗练 (pure rules, no Bukkit): one affix slot per T1+ blade / charm. A try costs a duplicate piece (same slot +
 * tier) or shards, plus coins; the value tier is rolled from {@code tier_weights} truncated at the quality cap
 * (标准 1 / 精良 2 / 卓越 3 / 极品 4); after {@code pity} tries in a row below the cap the next one is the cap tier.
 * Parameters: {@code ember-v1-growth.yml reroll:}.
 */
public final class EmberAffix {

    public static final class Def {
        public final String id, name, slot, key, desc;
        public final int code;
        public final double[] values;
        Def(String id, int code, String name, String slot, String key, String desc, double[] values) {
            this.id = id; this.code = code; this.name = name; this.slot = slot; this.key = key; this.desc = desc; this.values = values;
        }
        /** value of tier 1..4 */
        public double value(int tier) { return values[Math.max(1, Math.min(values.length, tier)) - 1]; }
        /** "对词缀精英伤害 +8%" */
        public String text(int tier) {
            double v = value(tier);
            return desc + " " + EmberGrowth.signedPct(v);
        }
    }

    public static final class Rules {
        public final int pity;
        public final int[] tierWeights, tierCap, coin, shard;
        public final List<Def> defs;
        Rules(int pity, int[] tierWeights, int[] tierCap, int[] coin, int[] shard, List<Def> defs) {
            this.pity = pity; this.tierWeights = tierWeights; this.tierCap = tierCap; this.coin = coin; this.shard = shard; this.defs = defs;
        }
        public Def def(String id) { if (id != null) for (Def d : defs) if (d.id.equals(id)) return d; return null; }
        /** stable code stored in the player save (yml {@code code:}) */
        public Def byCode(int code) { for (Def d : defs) if (d.code == code) return d; return null; }
        public List<Def> pool(String slot) {
            List<Def> l = new ArrayList<Def>();
            for (Def d : defs) if (d.slot.equals(slot)) l.add(d);
            return l;
        }
        /** highest value tier this quality can carry */
        public int cap(int quality) { return tierCap[Math.max(0, Math.min(tierCap.length - 1, quality))]; }
        public int coinFor(int itemTier) { return coin[Math.max(0, Math.min(coin.length - 1, itemTier))]; }
        public int shardFor(int itemTier) { return shard[Math.max(0, Math.min(shard.length - 1, itemTier))]; }
    }

    /** Result of one try: the candidate and the item's new pity counter. */
    public static final class Roll {
        public final String id;
        public final int tier, pityAfter;
        public final boolean forced;
        Roll(String id, int tier, int pityAfter, boolean forced) { this.id = id; this.tier = tier; this.pityAfter = pityAfter; this.forced = forced; }
    }

    /** Tier probabilities after truncating at the cap (index 0 = tier 1). */
    public static double[] tierOdds(Rules r, int cap) {
        int n = Math.max(1, Math.min(cap, r.tierWeights.length));
        double sum = 0;
        for (int i = 0; i < n; i++) sum += r.tierWeights[i];
        double[] p = new double[n];
        for (int i = 0; i < n; i++) p[i] = sum <= 0 ? (i == n - 1 ? 1 : 0) : r.tierWeights[i] / sum;
        return p;
    }

    /**
     * One try for an item of {@code slot} / {@code quality} whose pity counter is {@code pity}. The affix is uniform over
     * the slot pool; the tier never exceeds the quality cap; {@code pity} misses in a row → the cap tier.
     */
    public static Roll roll(Rules r, String slot, int quality, int pity, Random rng) {
        List<Def> pool = r.pool(slot);
        if (pool.isEmpty()) return null;
        Def d = pool.get(rng.nextInt(pool.size()));
        int cap = r.cap(quality);
        boolean forced = pity >= r.pity;
        int tier;
        if (forced) tier = cap;
        else {
            double[] p = tierOdds(r, cap);
            double u = rng.nextDouble(), acc = 0;
            tier = p.length;
            for (int i = 0; i < p.length; i++) { acc += p[i]; if (u < acc) { tier = i + 1; break; } }
        }
        return new Roll(d.id, tier, tier >= cap ? 0 : pity + 1, forced);
    }

    /** null = this item can carry an affix; else why not */
    public static String eligible(EmberItemData d) {
        if (d == null) return "不是 P1 装备";
        if (d.tier < 1) return "T0 起步件没有词条槽（T1 起才有）";
        return null;
    }

    /**
     * null = {@code dup} may be eaten as the duplicate for {@code target}; else why not. {@code dupHasAffix}: the
     * duplicate carries an affix of its own (kept out, it is an investment).
     */
    public static String duplicateOk(EmberItemData target, EmberItemData dup, boolean dupHasAffix) {
        if (dup == null) return "没有可用的重复件";
        if (dup.uid.equals(target.uid)) return "不能拿自己当重复件";
        if (!dup.slot.equals(target.slot) || dup.tier != target.tier) return "重复件要同部位、同阶（" + EmberItemData.slotName(target.slot) + " T" + target.tier + "）";
        if (!"drop".equals(dup.source)) return "只有掉落来的件能当重复件";
        if (dup.enhance > 0 || dup.quality > 0 || dup.craft > 0 || dupHasAffix)
            return "这件有投入（强化 / 成色 / 精工 / 词条），不拿来当重复件";
        return null;
    }

    /** stored value = code * 10 + tier (0 = empty slot) */
    public static int encode(Def d, int tier) { return d == null ? 0 : d.code * 10 + Math.max(1, Math.min(9, tier)); }
    public static Def decodeDef(Rules r, int v) { return r == null || v <= 0 ? null : r.byCode(v / 10); }
    public static int decodeTier(int v) { return v <= 0 ? 0 : v % 10; }

    public static Rules parse(Map<?, ?> root) {
        Object o = root == null ? null : root.get("reroll");
        if (!(o instanceof Map)) return null;
        Map<?, ?> m = (Map<?, ?>) o;
        List<Def> defs = new ArrayList<Def>();
        Object af = m.get("affixes");
        if (af instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) af).entrySet()) {
                String slot = String.valueOf(e.getKey());
                for (Map<?, ?> x : EmberGrowth.maps(e.getValue())) {
                    List<?> vs = x.get("values") instanceof List ? (List<?>) x.get("values") : Collections.emptyList();
                    double[] v = new double[vs.size()];
                    for (int i = 0; i < v.length; i++) v[i] = ((Number) vs.get(i)).doubleValue();
                    defs.add(new Def(EmberGrowth.s(x, "id"), EmberGrowth.i(x, "code"), EmberGrowth.s(x, "name"), slot, EmberGrowth.s(x, "key"), EmberGrowth.s(x, "desc"), v));
                }
            }
        }
        return new Rules(EmberGrowth.i(m, "pity"), ints(m.get("tier_weights")), ints(m.get("tier_cap")), ints(m.get("coin")), ints(m.get("shard")),
                Collections.unmodifiableList(defs));
    }

    static int[] ints(Object o) {
        if (!(o instanceof List)) return new int[]{0};
        List<?> l = (List<?>) o;
        int[] a = new int[l.size()];
        for (int i = 0; i < a.length; i++) a[i] = ((Number) l.get(i)).intValue();
        return a;
    }

    /**
     * Modifiers from the equipped pieces' affixes: each entry {stored value, item quality}; the tier is clamped to
     * the quality cap again (in case the yml changed).
     */
    public static Map<String, Double> parts(Rules r, int[][] items) {
        Map<String, Double> out = new LinkedHashMap<String, Double>();
        if (r == null) return out;
        for (int[] it : items) {
            if (it == null) continue;
            Def def = decodeDef(r, it[0]);
            if (def == null) continue;
            double v = def.value(Math.min(decodeTier(it[0]), r.cap(it[1])));
            Double cur = out.get(def.key);
            out.put(def.key, (cur == null ? 1.0 : cur) * v);
        }
        return out;
    }

    private EmberAffix() {}
}
