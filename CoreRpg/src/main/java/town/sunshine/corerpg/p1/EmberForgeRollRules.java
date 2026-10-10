package town.sunshine.corerpg.p1;

import java.util.Random;

/**
 * D430 · 烬砧随机锻造纯规则（D167 §3.2 切片）。极品成色权重必须为 0。
 * 新件不扣锻造次数（次数只对洗练/烙纹定向）。
 */
public final class EmberForgeRollRules {
    private EmberForgeRollRules() {}

    public static final int MARKS = EmberRunRules.MARKS_PER_EXCHANGE; // 8 golden mirror
    public static final int BLANKS = 4; // golden mirror
    public static final int COINS = 500; // golden mirror
    /** 成色：标准/精良/卓越/极品 — 极品硬 0 */
    public static final int[] QUALITY_W = {70, 23, 7, 0};
    /** 精工 0..3 */
    public static final int[] CRAFT_W = {70, 20, 9, 1};

    /** D952: C22 marks from economy yml SoT. */
    public static int marks() { return EmberEconomy.amount("C22", "marks"); }

    /** D952: C22 blank from economy yml SoT. */
    public static int blanks() { return EmberEconomy.amount("C22", "blank"); }

    /** D952: C22 coin from economy yml SoT. */
    public static int coins() { return EmberEconomy.amount("C22", "coin"); }

    public static EmberUpgradeRules.Cost matCost() {
        return new EmberUpgradeRules.Cost(0, 0, blanks(), 0, coins());
    }

    /** D1142 K2: base + C06 q0 full surcharge (floor.blank/bone/coin). */
    public static int floorBlank() { return EmberEconomy.amount("C22", "floor.blank"); }
    public static int floorBone() { return EmberEconomy.amount("C22", "floor.bone"); }
    public static int floorCoin() { return EmberEconomy.amount("C22", "floor.coin"); }

    public static EmberUpgradeRules.Cost matCostFloor() {
        return new EmberUpgradeRules.Cost(0, 0, blanks() + floorBlank(), floorBone(), coins() + floorCoin());
    }

    /** Quality weights with 标准 removed (极品仍 0). */
    public static final int[] QUALITY_W_FLOOR = {0, 23, 7, 0};

    public static int rollQuality(Random rng) {
        return weighted(QUALITY_W, rng);
    }

    /** D1142: roll among ≥精良 only. */
    public static int rollQualityFloor(Random rng) {
        return weighted(QUALITY_W_FLOOR, rng);
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

    /**
     * D1843: display-only cost remain (Bukkit-free).
     * {@code 印记3/8 · 胚2/4 · 币500/500 · 还差印5胚2} or {@code 料够}.
     */
    public static String remainLine(int haveM, int haveBlank, int haveBone, int haveCoin, boolean floor) {
        EmberUpgradeRules.Cost c = floor ? matCostFloor() : matCost();
        int needM = marks();
        int gM = Math.max(0, needM - Math.max(0, haveM));
        int gB = Math.max(0, c.blanks - Math.max(0, haveBlank));
        int gBone = Math.max(0, c.bone - Math.max(0, haveBone));
        int gC = Math.max(0, c.coins - Math.max(0, haveCoin));
        StringBuilder sb = new StringBuilder();
        sb.append("印记").append(Math.max(0, haveM)).append("/").append(needM);
        sb.append(" · 胚").append(Math.max(0, haveBlank)).append("/").append(c.blanks);
        if (c.bone > 0) sb.append(" · 骨").append(Math.max(0, haveBone)).append("/").append(c.bone);
        sb.append(" · 币").append(Math.max(0, haveCoin)).append("/").append(c.coins);
        if (gM + gB + gBone + gC == 0) return sb.append(" · 料够").toString();
        sb.append(" · 还差");
        if (gM > 0) sb.append("印").append(gM);
        if (gB > 0) sb.append("胚").append(gB);
        if (gBone > 0) sb.append("骨").append(gBone);
        if (gC > 0) sb.append("币").append(gC);
        return sb.toString();
    }

    /** null = ok */
    public static String refusal(int haveMarks, int tier, String fam, String slot, boolean gateOpen) {
        if (!"blade".equals(slot) && !"charm".equals(slot)) return "部位只能是 blade 或 charm（本窗不开护甲）";
        String err = EmberRunRules.exchangeCheck(haveMarks, tier, tier, fam, slot, gateOpen);
        if (err != null) return err;
        if (QUALITY_W.length != 4 || QUALITY_W[3] != 0) return "成色表损坏：极品权重必须为 0";
        if (QUALITY_W_FLOOR.length != 4 || QUALITY_W_FLOOR[0] != 0 || QUALITY_W_FLOOR[3] != 0) return "下限成色表损坏";
        return null;
    }

    public static String oddsLineFloor() {
        return "下限成色：§f必精良§7 · 精良 " + QUALITY_W_FLOOR[1] + " · 卓越 " + QUALITY_W_FLOOR[2]
                + "（重归一；极品 0）· 加扣胚" + floorBlank() + "+骨" + floorBone() + "+币" + floorCoin();
    }

    public static String oddsLine() {
        return "成色：标准 " + QUALITY_W[0] + "% · 精良 " + QUALITY_W[1] + "% · 卓越 " + QUALITY_W[2]
                + "%（锻造出不了极品）· 精工表 " + CRAFT_W[0] + "/" + CRAFT_W[1] + "/" + CRAFT_W[2] + "/" + CRAFT_W[3];
    }
}
