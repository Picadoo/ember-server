package town.sunshine.corerpg.p1;

import java.util.Random;

/**
 * D430 · 烬砧随机锻造纯规则（D167 §3.2 切片）。极品成色权重必须为 0。
 * 新件不扣锻造次数（次数只对洗练/烙纹定向）。
 */
public final class EmberForgeRollRules {
    private EmberForgeRollRules() {}

    public static final int MARKS = EmberRunRules.MARKS_PER_EXCHANGE; // 8
    public static final int BLANKS = 4;
    public static final int COINS = 500;
    /** 成色：标准/精良/卓越/极品 — 极品硬 0 */
    public static final int[] QUALITY_W = {70, 23, 7, 0};
    /** 精工 0..3 */
    public static final int[] CRAFT_W = {70, 20, 9, 1};

    public static EmberUpgradeRules.Cost matCost() {
        return new EmberUpgradeRules.Cost(0, 0, BLANKS, 0, COINS);
    }

    public static int rollQuality(Random rng) {
        return weighted(QUALITY_W, rng);
    }

    public static int rollCraft(Random rng) {
        return weighted(CRAFT_W, rng);
    }

    static int weighted(int[] w, Random rng) {
        int sum = 0;
        for (int x : w) sum += Math.max(0, x);
        if (sum <= 0) return 0;
        int r = rng.nextInt(sum), acc = 0;
        for (int i = 0; i < w.length; i++) {
            acc += Math.max(0, w[i]);
            if (r < acc) return i;
        }
        return w.length - 1;
    }

    /** null = ok */
    public static String refusal(int haveMarks, int tier, String fam, String slot, boolean gateOpen) {
        if (!"blade".equals(slot) && !"charm".equals(slot)) return "部位只能是 blade 或 charm（本窗不开护甲）";
        String err = EmberRunRules.exchangeCheck(haveMarks, tier, tier, fam, slot, gateOpen);
        if (err != null) return err;
        if (QUALITY_W.length != 4 || QUALITY_W[3] != 0) return "成色表损坏：极品权重必须为 0";
        return null;
    }

    public static String oddsLine() {
        return "成色：标准 " + QUALITY_W[0] + "% · 精良 " + QUALITY_W[1] + "% · 卓越 " + QUALITY_W[2]
                + "%（锻造出不了极品）· 精工表 " + CRAFT_W[0] + "/" + CRAFT_W[1] + "/" + CRAFT_W[2] + "/" + CRAFT_W[3];
    }
}
