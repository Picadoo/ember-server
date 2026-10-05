package town.sunshine.corerpg.p1;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * G03 pure rules for P1 item investment (策划书 §5.3, §6.1–6.4). No Bukkit, no IO: every operation returns the
 * cost and the exact {@link EmberItemData} after it (rev + 1), so the preview is the result (E09).
 */
public final class EmberUpgradeRules {

    private EmberUpgradeRules() {}

    public static final String MAT_SHARD = "mat_ember_shard";
    public static final String MAT_CORE = "mat_ember_core_fragment";
    public static final String MAT_BONE = "mat_ember_bone_dust";
    /** 胚料 — no template existed before G03; added to NeigeItems/Items/ember-v1-gear.yml */
    public static final String MAT_BLANK = "mat_ember_v1_blank";

    // §6.1, index = target level (1..10)
    static final double[] RATE = {0, 1.00, 1.00, 1.00, 0.85, 0.70, 0.55, 0.40, 0.30, 0.22, 0.15};
    static final int[] MAX_TRIES = {0, 1, 1, 1, 3, 4, 5, 6, 8, 10, 12};
    static final int[] SHARDS = {0, 4, 6, 8, 12, 16, 22, 30, 40, 54, 72};
    static final int[] CORES = {0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 6};
    static final int[] COINS = {0, 40, 60, 80, 120, 180, 260, 380, 540, 760, 1080};

    public static double rate(int target) { return RATE[target]; }
    public static int maxTries(int target) { return MAX_TRIES[target]; }

    /** Materials + coins of one operation. */
    public static final class Cost {
        public final int shards, cores, blanks, bone, coins;
        public Cost(int shards, int cores, int blanks, int bone, int coins) {
            this.shards = shards; this.cores = cores; this.blanks = blanks; this.bone = bone; this.coins = coins;
        }
        public static final Cost NONE = new Cost(0, 0, 0, 0, 0);
        /** NI id → amount, only non-zero materials (coins separate) */
        public Map<String, Integer> materials() {
            Map<String, Integer> m = new LinkedHashMap<String, Integer>();
            if (shards > 0) m.put(MAT_SHARD, shards);
            if (cores > 0) m.put(MAT_CORE, cores);
            if (blanks > 0) m.put(MAT_BLANK, blanks);
            if (bone > 0) m.put(MAT_BONE, bone);
            return m;
        }
        public String json() {
            return String.format(Locale.ROOT, "{\"shard\":%d,\"core\":%d,\"blank\":%d,\"bone\":%d,\"coin\":%d}", shards, cores, blanks, bone, coins);
        }
        public String label() {
            StringBuilder sb = new StringBuilder();
            if (shards > 0) sb.append("碎片×").append(shards).append(' ');
            if (cores > 0) sb.append("核心碎片×").append(cores).append(' ');
            if (blanks > 0) sb.append("胚料×").append(blanks).append(' ');
            if (bone > 0) sb.append("骨尘×").append(bone).append(' ');
            if (coins > 0) sb.append("余烬币×").append(coins);
            return sb.length() == 0 ? "免费" : sb.toString().trim();
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Cost)) return false;
            Cost c = (Cost) o;
            return c.shards == shards && c.cores == cores && c.blanks == blanks && c.bone == bone && c.coins == coins;
        }
        @Override public int hashCode() { return ((shards * 31 + cores) * 31 + blanks) * 31 + bone * 7 + coins; }
        @Override public String toString() { return json(); }
    }

    /** Outcome of a check-then-apply step. {@code error != null} means: nothing paid, nothing changed. */
    public static final class Plan {
        public final String error;
        public final Cost cost;
        public final EmberItemData after;
        public final String note;
        Plan(String error, Cost cost, EmberItemData after, String note) {
            this.error = error; this.cost = cost; this.after = after; this.note = note;
        }
        static Plan fail(String why) { return new Plan(why, Cost.NONE, null, null); }
        public boolean ok() { return error == null; }
    }

    static EmberItemData copy(EmberItemData d, int tier, int q, int craft, int enh, int pity, boolean bound) {
        String fam = d.family;
        // D208: the item keys (affix / pity / signature / reroll sequence) stay with the piece; a v1 piece is folded to v2
        // by the forge commit (EmberItemKeys.fold) with its legacy counters
        return new EmberItemData(d.uid, EmberItemData.templateId(fam, d.slot, tier), fam, d.slot, tier, q, craft, enh, pity,
                bound, d.source, d.version, d.rev + 1, d.affix, d.afPity, d.sigCode, d.rerollN);
    }

    private static String basic(EmberItemData d) {
        if (d == null) return "不是 P1 装备";
        String v = d.validate();
        return v == null ? null : "物品数据无效: " + v;
    }

    // ------------------------------------------------------------------ §6.1 enhance

    public static Cost enhanceCost(int currentEnh) {
        int t = currentEnh + 1;
        if (t < 1 || t > EmberTables.MAX_ENHANCE) return null;
        return new Cost(SHARDS[t], CORES[t], 0, 0, COINS[t]);
    }

    /** Pre-roll check: everything that can refuse an attempt without touching RNG. */
    public static Plan enhanceCheck(EmberItemData d) {
        String b = basic(d);
        if (b != null) return Plan.fail(b);
        if (d.enhance >= EmberTables.MAX_ENHANCE) return Plan.fail("已是 +" + EmberTables.MAX_ENHANCE);
        int t = d.enhance + 1;
        if (d.pity >= MAX_TRIES[t]) return Plan.fail("保底计数异常 " + d.pity + " ≥ " + MAX_TRIES[t]);
        return new Plan(null, enhanceCost(d.enhance), null, null);
    }

    /** Attempt number (1-based) the next try at the current target would be. */
    public static int attemptNo(EmberItemData d) { return d.pity + 1; }

    /** True when the next attempt is the guaranteed one (the Nth try IS the success, §6.1). */
    public static boolean guaranteed(EmberItemData d) {
        return d.enhance < EmberTables.MAX_ENHANCE && d.pity + 1 >= MAX_TRIES[d.enhance + 1];
    }

    /**
     * One paid attempt. {@code roll} is uniform in [0,1): success when it is below the rate or the attempt is the
     * guaranteed one. Success: enhance+1, pity 0. Failure: pity+1, no downgrade, no break.
     */
    public static Plan enhance(EmberItemData d, double roll) {
        Plan c = enhanceCheck(d);
        if (!c.ok()) return c;
        if (!(roll >= 0 && roll < 1)) return Plan.fail("随机数异常");
        int t = d.enhance + 1;
        int n = d.pity + 1;
        boolean pity = n >= MAX_TRIES[t];
        boolean ok = pity || roll < RATE[t];
        EmberItemData after = ok ? copy(d, d.tier, d.quality, d.craft, t, 0, d.bound)
                : copy(d, d.tier, d.quality, d.craft, d.enhance, n, d.bound);
        String note = ok ? String.format(Locale.ROOT, "强化成功 +%d → +%d（本档第 %d/%d 次%s）", d.enhance, t, n, MAX_TRIES[t], pity && RATE[t] < 1 ? "，保底" : "")
                : String.format(Locale.ROOT, "强化失败，保持 +%d（本档已失败 %d 次，最多再 %d 次必成）", d.enhance, n, MAX_TRIES[t] - n);
        return new Plan(null, c.cost, after, note);
    }

    // ------------------------------------------------------------------ §6.3 swap

    /** Result of a track swap: both new states. */
    public static final class SwapPlan {
        public final String error;
        public final EmberItemData a, b;
        SwapPlan(String error, EmberItemData a, EmberItemData b) { this.error = error; this.a = a; this.b = b; }
        public boolean ok() { return error == null; }
    }

    /** Exchanges enhance + current pity between two same-slot items; everything else stays; both become bound. */
    public static SwapPlan swap(EmberItemData a, EmberItemData b) {
        String x = basic(a);
        if (x == null) x = basic(b);
        if (x != null) return new SwapPlan(x, null, null);
        if (a.uid.equals(b.uid)) return new SwapPlan("不能与自身互换", null, null);
        if (!a.slot.equals(b.slot)) return new SwapPlan("不同部位不能互换（刃只能和刃，护符只能和护符）", null, null);
        EmberItemData na = copy(a, a.tier, a.quality, a.craft, b.enhance, b.pity, true);
        EmberItemData nb = copy(b, b.tier, b.quality, b.craft, a.enhance, a.pity, true);
        String va = na.validate(), vb = nb.validate();
        if (va != null || vb != null) return new SwapPlan("互换后数据无效: " + (va != null ? va : vb), null, null);
        return new SwapPlan(null, na, nb);
    }

    // ------------------------------------------------------------------ §6.4 tier upgrade

    /** Progress flag required for upgrading FROM {@code tier}: q04 for T1→T2, q07 for T2→T3; null = not upgradable. */
    public static String upgradeFlag(int tier) {
        return tier == 1 ? "q04" : tier == 2 ? "q07" : null;
    }

    public static Cost upgradeCost(int tier) {
        if (tier == 1) return new Cost(60, 12, 6, 0, 1500);
        if (tier == 2) return new Cost(60, 15, 6, 0, 1800); // D104 (b13): halved — was 120/30/12/3600, about twice an 8-mark exchange + free swap
        return null;
    }

    /** In-place same-family upgrade: keeps uid, family, enhance track (level + pity), quality, craft, bound. */
    public static Plan upgrade(EmberItemData d, boolean firstClearDone) {
        String b = basic(d);
        if (b != null) return Plan.fail(b);
        Cost c = upgradeCost(d.tier);
        if (c == null) return Plan.fail(d.tier == 0 ? "T0 不能升阶（同族升阶只有 T1→T2、T2→T3）" : "已是最高阶 T" + d.tier);
        if (!firstClearDone) return Plan.fail("需要本人已首通 " + upgradeFlag(d.tier).toUpperCase(Locale.ROOT));
        EmberItemData after = copy(d, d.tier + 1, d.quality, d.craft, d.enhance, d.pity, d.bound);
        return new Plan(null, c, after, "升阶 T" + d.tier + " → T" + after.tier + "（强化/成色/精工/绑定全部保留）");
    }

    // ------------------------------------------------------------------ §5.3 craft (精工) and quality (成色)

    public static Cost refineCost(int craft) {
        if (craft == 0) return new Cost(0, 0, 3, 5, 300);
        if (craft == 1) return new Cost(0, 0, 6, 10, 600);
        if (craft == 2) return new Cost(0, 0, 12, 20, 1200);
        return null;
    }

    public static Plan refine(EmberItemData d) {
        String b = basic(d);
        if (b != null) return Plan.fail(b);
        Cost c = refineCost(d.craft);
        if (c == null) return Plan.fail("精工已满（6%）");
        EmberItemData after = copy(d, d.tier, d.quality, d.craft + 1, d.enhance, d.pity, d.bound);
        return new Plan(null, c, after, String.format(Locale.ROOT, "精工 %d%% → %d%%（确定成功，成色不变）", d.craft * 2, after.craft * 2));
    }

    public static Cost qualityCost(int q) {
        if (q == 0) return new Cost(0, 0, 8, 8, 800);
        if (q == 1) return new Cost(0, 0, 16, 16, 1600);
        return null;
    }

    public static Plan quality(EmberItemData d) {
        String b = basic(d);
        if (b != null) return Plan.fail(b);
        Cost c = qualityCost(d.quality);
        if (c == null) return Plan.fail(d.quality == 2 ? "卓越 → 极品不能养成（极品只从正常随机掉落获得）" : "已是极品");
        EmberItemData after = copy(d, d.tier, d.quality + 1, d.craft, d.enhance, d.pity, d.bound);
        return new Plan(null, c, after, "成色 " + EmberItemData.qualityName(d.quality) + " → " + EmberItemData.qualityName(after.quality));
    }

    // ------------------------------------------------------------------ §5.3 dismantle

    /** Blanks returned; only normal random drops (src=drop) of T1–T3. Nothing else is refunded. */
    public static int dismantleYield(EmberItemData d) {
        if (d == null || d.validate() != null || !"drop".equals(d.source) || d.tier < 1) return 0;
        return d.tier;
    }

    public static String dismantleCheck(EmberItemData d) {
        String b = basic(d);
        if (b != null) return b;
        if (!"drop".equals(d.source)) return "这件是" + EmberCompare.sourceName(d.source) + "：只有副本里随机掉落的装备能分解";
        if (d.tier < 1) return "T0 装备不可分解";
        return null;
    }
}
