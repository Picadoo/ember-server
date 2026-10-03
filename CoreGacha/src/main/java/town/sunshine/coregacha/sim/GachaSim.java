package town.sunshine.coregacha.sim;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import org.yaml.snakeyaml.Yaml;

import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.Engine;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.PityState;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Result;
import town.sunshine.coregacha.engine.Tier;

/**
 * Offline simulator: {@code java -cp <classes>:<snakeyaml> town.sunshine.coregacha.sim.GachaSim <gacha.yml> [pulls] [seed]}.
 * Prints a markdown report (rates observed vs published, pity bounds, no-repeat, spark, shard economy). Exit code 1
 * when a check fails.
 */
public final class GachaSim {
    static String f(String fmt, Object... a) { return String.format(Locale.ROOT, fmt, a); }

    @SuppressWarnings("unchecked")
    public static GachaConfig load(String path) throws Exception {
        try (Reader r = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) {
            return GachaConfig.parse((Map<String, Object>) new Yaml().load(r));
        }
    }

    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "src/main/resources/gacha.yml";
        long n = args.length > 1 ? Long.parseLong(args[1]) : 1_000_000L;
        long seed = args.length > 2 ? Long.parseLong(args[2]) : 20261004L;
        GachaConfig cfg = load(path);
        RateMath rm = new RateMath(cfg);
        StringBuilder o = new StringBuilder();
        List<String> fails = new ArrayList<String>();
        o.append("# CoreGacha 离线模拟报告\n\n");
        o.append(f("- 配置：`%s`（和线上同一份）· 引擎：插件里的 `town.sunshine.coregacha.engine.Engine`（同一份代码）\n", path));
        o.append(f("- 每个池 %,d 抽，种子 %d；公示值来自 `RateMath`（马尔可夫稳态，和引擎是两份独立代码）\n", n, seed));
        o.append(f("- 规则：基础 传说 %s / 史诗 %s / 稀有 %s；软保底第 %d 抽后每抽 +%s；硬保底 %d；每 %d 抽至少一件史诗或以上；不连出同一传说=%s；火花 %d\n\n",
                RateMath.pct(cfg.baseLegend), RateMath.pct(cfg.baseEpic), RateMath.pct(cfg.baseRare), cfg.softStart, RateMath.pct(cfg.softStep),
                cfg.hardPity, cfg.tier2Every, cfg.noRepeatLegend, cfg.spark));
        o.append(f("公示：传说综合 %s（平均 %.2f 抽一件；60 抽内 %.2f%%；走到硬保底 %.5f%%）\n\n", RateMath.pct(rm.effective.get(Tier.LEGEND)),
                rm.expectedPullsPerLegend, rm.pWithinSoft * 100, rm.pHard * 100));

        for (Banner b : cfg.banners.values()) {
            long now = b.limited() ? b.start + 1 : (cfg.banners.containsKey("gq26") ? cfg.banner("gq26").end + 1 : System.currentTimeMillis());
            Simulator s = Simulator.run(cfg, b, now, n, seed + b.id.hashCode());
            Banner.Pool pool = cfg.pool(b, now);
            Map<Item, Double> shares = RateMath.shares(pool, cfg.noRepeatLegend);
            o.append(f("## 池 `%s` %s（%s，模拟时间 %s，池内 %d 件）\n\n", b.id, Item.stripColor(b.name), b.limited() ? "限定" : "常驻",
                    java.time.Instant.ofEpochMilli(now).atOffset(java.time.ZoneOffset.ofHours(8)).toLocalDateTime(), pool.all().size()));
            o.append("| 品质 | 公示综合概率 | 观测 | 观测次数 | 差 / σ | 结果 |\n|---|---|---|---|---|---|\n");
            for (Tier t : Tier.values()) {
                double p = rm.effective.get(t), obs = s.rate(t), sd = Math.sqrt(p * (1 - p) / n), z = (obs - p) / sd;
                boolean ok = Math.abs(z) <= 4;
                if (!ok) fails.add(b.id + " " + t + " z=" + z);
                o.append(f("| %s | %s | %s | %,d | %+.2f | %s |\n", t.label, RateMath.pct(p), RateMath.pct(obs), s.tierCount.get(t), z, ok ? "PASS" : "FAIL"));
            }
            double meanGap = s.legendTotal == 0 ? 0 : s.gapSum / (double) s.legendTotal;
            o.append(f("\n- 传说平均间隔：观测 %.2f 抽，公示 %.2f 抽\n", meanGap, rm.expectedPullsPerLegend));
            boolean hard = s.maxLegendGap <= cfg.hardPity && s.pity5Overflow == 0;
            if (!hard) fails.add(b.id + " hard pity exceeded " + s.maxLegendGap);
            o.append(f("- 最长连续无传说：%d 抽（硬保底 %d）→ %s；走到硬保底的次数 %,d（公示概率 %.5f%% / 每件传说，期望 %.1f 次）\n", s.maxLegendGap, cfg.hardPity,
                    hard ? "PASS" : "FAIL", s.ruleCount.getOrDefault("hard", 0L), rm.pHard * 100, rm.pHard * s.legendTotal));
            boolean t2 = s.maxEpicGap <= cfg.tier2Every && s.tier2Violations == 0;
            if (!t2) fails.add(b.id + " tier2 gap " + s.maxEpicGap);
            o.append(f("- 最长连续无史诗或以上：%d 抽（二档保底 %d）→ %s；二档保底触发 %,d 次\n", s.maxEpicGap, cfg.tier2Every, t2 ? "PASS" : "FAIL", s.ruleCount.getOrDefault("tier2", 0L)));
            boolean rep = s.sameLegendTwice == 0 || !cfg.noRepeatLegend;
            if (!rep) fails.add(b.id + " same legend twice " + s.sameLegendTwice);
            o.append(f("- 连续两次同一件传说：%,d 次 → %s\n", s.sameLegendTwice, rep ? "PASS" : "FAIL"));
            o.append(f("- 软保底区（第 %d 抽后）出的传说：%,d / %,d（%.1f%%）\n\n", cfg.softStart, s.ruleCount.getOrDefault("soft", 0L), s.legendTotal,
                    100.0 * s.ruleCount.getOrDefault("soft", 0L) / Math.max(1, s.legendTotal)));
            o.append("| 传说 | 公示单件概率 | 观测 | 传说内份额 公示 / 观测 | 差 / σ |\n|---|---|---|---|---|\n");
            for (Map.Entry<Item, Double> e : pool.tier(Tier.LEGEND).entrySet()) {
                Item it = e.getKey();
                double pr = rm.itemRate(it, shares), obs = s.itemCount.getOrDefault(it.id, 0L) / (double) n;
                double share = shares.get(it), oshare = s.legendCount.getOrDefault(it.id, 0L) / (double) Math.max(1, s.legendTotal);
                double z = (obs - pr) / Math.sqrt(pr * (1 - pr) / n);
                if (Math.abs(z) > 4) fails.add(b.id + " item " + it.id + " z=" + z);
                o.append(f("| %s | %s | %s | %.2f%% / %.2f%% | %+.2f |\n", Item.stripColor(it.name), RateMath.pct(pr), RateMath.pct(obs), share * 100, oshare * 100, z));
            }
            if (b.limited()) {
                double lim = 0, olim = 0;
                for (Map.Entry<Item, Double> e : pool.tier(Tier.LEGEND).entrySet()) if (e.getKey().id.startsWith("gq_")) {
                    lim += shares.get(e.getKey()); olim += s.legendCount.getOrDefault(e.getKey().id, 0L);
                }
                o.append(f("\n- 传说里限定件的份额：公示 %.2f%%，观测 %.2f%%（权重比 70%%；「不连出」把两件限定互相挡掉一部分）\n", lim * 100, olim * 100 / Math.max(1, s.legendTotal)));
            }
            o.append("\n");
        }

        // ---------------- shard / spark economy: many players, owned sets kept
        Banner std = cfg.banner("standard");
        long after = cfg.banners.containsKey("gq26") ? cfg.banner("gq26").end + 1 : System.currentTimeMillis();
        Banner.Pool pool = cfg.pool(std, after);
        int size = pool.all().size(), players = (int) Math.max(1, n / 400);
        int[] marks = {50, 100, 200, 400};
        double[] owned = new double[marks.length], ownedCraft = new double[marks.length], shardsAt = new double[marks.length];
        double pullsAllCommon = 0;
        long shardTot = 0;
        Engine e = new Engine(cfg);
        SplittableRandom rng = new SplittableRandom(seed ^ 0x5eed);
        int minCraftPrice = Integer.MAX_VALUE;
        for (int v : cfg.craftPrice.values()) minCraftPrice = Math.min(minCraftPrice, v);
        for (int p = 0; p < players; p++) {
            PityState st = new PityState();
            Set<String> have = new HashSet<String>(), haveC = new HashSet<String>();
            int shards = 0, shardsC = 0, mi = 0, commons = pool.tier(Tier.COMMON).size();
            boolean allCommon = false;
            for (int i = 1; i <= 400; i++) {
                Result r = e.pull(pool, st, have, rng::nextDouble);
                shards += r.shards;
                // crafting variant: same pulls, owned set includes crafted items (a crafted item later pulled = dup)
                boolean dupC = !haveC.add(r.item.id);
                if (dupC) shardsC += cfg.dupShards.get(r.tier);
                // craft the most expensive missing item we can afford (legend first: that is what players want)
                for (Tier t : Tier.values()) {
                    int price = cfg.craftPrice.get(t);
                    if (shardsC < price) continue;
                    for (Item it : pool.tier(t).keySet()) if (!haveC.contains(it.id) && shardsC >= price) { haveC.add(it.id); shardsC -= price; }
                }
                if (!allCommon) {
                    int c = 0;
                    for (Item it : pool.tier(Tier.COMMON).keySet()) if (have.contains(it.id)) c++;
                    if (c == commons) { allCommon = true; pullsAllCommon += i; }
                }
                if (mi < marks.length && i == marks[mi]) { owned[mi] += have.size(); ownedCraft[mi] += haveC.size(); shardsAt[mi] += shards; mi++; }
            }
            if (!allCommon) pullsAllCommon += 400;
            shardTot += shards;
        }
        double perPull = shardTot / (double) players / 400.0;
        o.append(f("## 光屑经济（常驻池，国庆限定已转入后共 %d 件；%,d 名玩家 × 400 抽 = %,d 抽，每人保留自己的收藏）\n\n", size, players, players * 400L));
        o.append("| 抽数 | 平均拥有（不兑换） | 平均拥有（光屑随时兑换） | 累计光屑（不兑换） |\n|---|---|---|---|\n");
        for (int i = 0; i < marks.length; i++)
            o.append(f("| %d | %.1f / %d | %.1f / %d | %.0f |\n", marks[i], owned[i] / players, size, ownedCraft[i] / players, size, shardsAt[i] / players));
        double legendShare = 1.0 / pool.tier(Tier.LEGEND).size();
        double pullsSpecific = rm.expectedPullsPerLegend / legendShare;
        double craftLegendPulls = cfg.craftPrice.get(Tier.LEGEND) / Math.max(1e-9, perPull);
        o.append(f("\n- 凑齐 %d 个普通徽记平均要 %.1f 抽\n", pool.tier(Tier.COMMON).size(), pullsAllCommon / players));
        o.append(f("- 400 抽内平均每抽 %.2f 光屑（前期少、后期多：前 50 抽 %.2f / 抽）\n", perPull, shardsAt[0] / players / 50));
        o.append(f("- 光屑换一件传说（%d）≈ %.0f 抽的光屑；抽到「任意传说」平均 %.1f 抽，抽到「指定传说」平均 %.0f 抽（常驻 %d 件均分）\n",
                cfg.craftPrice.get(Tier.LEGEND), craftLegendPulls, rm.expectedPullsPerLegend, pullsSpecific, pool.tier(Tier.LEGEND).size()));
        boolean sane = craftLegendPulls > rm.expectedPullsPerLegend && craftLegendPulls < pullsSpecific * 3;
        if (!sane) fails.add("shard economy: craft legend = " + craftLegendPulls + " pulls");
        o.append(f("- 判断：光屑换传说比直接抽「任意传说」贵（%.0f > %.1f），又不至于比抽「指定传说」贵太多（< 3 × %.0f）→ %s（光屑是补缺口的，不是捷径）\n",
                craftLegendPulls, rm.expectedPullsPerLegend, pullsSpecific, sane ? "PASS" : "FAIL"));
        Banner gq = cfg.banner("gq26");
        if (gq != null) {
            Banner.Pool gp = cfg.pool(gq, gq.start + 1);
            Map<Item, Double> sh = RateMath.shares(gp, cfg.noRepeatLegend);
            Item koi = cfg.items.get("gq_pet_koi");
            if (koi != null && sh.containsKey(koi))
                o.append(f("- 火花 %d：限定池里「指定一件限定传说」（锦鲤灵）平均 %.0f 抽能抽到，火花 %d 抽保证拿到 → 火花是 %.1f 倍期望的兜底\n",
                        cfg.spark, 1 / rm.itemRate(koi, sh), cfg.spark, cfg.spark * rm.itemRate(koi, sh)));
        }
        o.append("\n## 结论\n\n").append(fails.isEmpty() ? "全部 PASS。\n" : "FAIL：" + fails + "\n");
        System.out.print(o);
        if (!fails.isEmpty()) System.exit(1);
    }
}
