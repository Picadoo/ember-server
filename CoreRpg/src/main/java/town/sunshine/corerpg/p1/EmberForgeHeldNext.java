package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D1943 FH1: held-piece next deterministic forge step — display only.
 * Prefers an affordable quality → refine → enhance step; else nearest gap.
 * Zero price / rate / pity invent.
 */
public final class EmberForgeHeldNext {

    private EmberForgeHeldNext() {}

    public static final class Mats {
        public final long shard, blank, bone, coin;
        public Mats(long shard, long blank, long bone, long coin) {
            this.shard = Math.max(0, shard);
            this.blank = Math.max(0, blank);
            this.bone = Math.max(0, bone);
            this.coin = Math.max(0, coin);
        }
    }

    static boolean canPay(Cost c, Mats m) {
        if (c == null || m == null) return false;
        return m.shard >= c.shards && m.blank >= c.blanks && m.bone >= c.bone && m.coin >= c.coins;
    }

    static long shortfall(Cost c, Mats m) {
        if (c == null || m == null) return Long.MAX_VALUE / 4;
        long s = 0;
        if (c.shards > m.shard) s += c.shards - m.shard;
        if (c.blanks > m.blank) s += c.blanks - m.blank;
        if (c.bone > m.bone) s += c.bone - m.bone;
        if (c.coins > m.coin) s += c.coins - m.coin;
        return s;
    }

    /**
     * One-glance next step for held blade/charm.
     * {@code 下一确定 · 成色表盘 0/2 · 料够 · /corerpg p1 quality}
     */
    public static String line(EmberItemData d, Mats m) {
        if (d == null) return "下一确定 · 空手 · 手持刃/护符";
        if (m == null) m = new Mats(0, 0, 0, 0);

        Cost q = EmberUpgradeRules.qualityCost(d.quality);
        Cost r = EmberUpgradeRules.refineCost(d.craft);
        EmberUpgradeRules.Plan enh = EmberUpgradeRules.enhanceCheck(d);
        Cost e = (enh != null && enh.ok()) ? enh.cost : null;

        // Affordable deterministic first: quality → refine → enhance
        if (q != null && canPay(q, m))
            return "下一确定 · " + EmberQualityCraftDial.qualityDial(d) + " · 料够 · /corerpg p1 quality";
        if (r != null && canPay(r, m))
            return "下一确定 · " + EmberQualityCraftDial.craftDial(d) + " · 料够 · /corerpg p1 refine";
        if (e != null && canPay(e, m))
            return "下一确定 · " + EmberGearNextHint.enhanceLine(d) + " · 料够 · /corerpg p1 enhance";

        // Nearest gap among available ladders
        long best = Long.MAX_VALUE / 4;
        String pick = null;
        if (q != null) {
            long g = shortfall(q, m);
            if (g < best) { best = g; pick = "成色 · " + EmberQualityCraftDial.qualityDial(d) + " · 还差料 · /corerpg p1 quality"; }
        }
        if (r != null) {
            long g = shortfall(r, m);
            if (g < best) { best = g; pick = "精工 · " + EmberQualityCraftDial.craftDial(d) + " · 还差料 · /corerpg p1 refine"; }
        }
        if (e != null) {
            long g = shortfall(e, m);
            if (g < best) { best = g; pick = "强化 · " + EmberGearNextHint.enhanceLine(d) + " · 还差料 · /corerpg p1 enhance"; }
        }
        if (pick != null) return "下一确定 · " + pick;

        // All capped
        String qDone = EmberQualityCraftDial.qualityDial(d);
        String cDone = EmberQualityCraftDial.craftDial(d);
        if (e == null)
            return "下一确定 · 手持满档 · " + qDone + " · " + cDone;
        return "下一确定 · " + EmberGearNextHint.enhanceLine(d);
    }
}
