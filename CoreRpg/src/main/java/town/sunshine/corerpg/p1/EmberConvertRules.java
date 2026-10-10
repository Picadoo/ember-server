package town.sunshine.corerpg.p1;

/**
 * D431 · 每周跨族转化纯规则（D167 §3.5 切片）。成色截卓越；强化归零；uid 不变；不扣锻造次数。
 */
public final class EmberConvertRules {
    private EmberConvertRules() {}

    public static final String C_CONV = "p4_conv";
    public static final int WEEKLY_CAP = 1;
    public static final int QUALITY_CAP = 2; // 卓越
    private static final int[] BLANKS = {0, 2, 4, 6};
    private static final int[] COINS = {0, 300, 600, 1000};

    public static int blanks(int tier) {
        if (tier < 1 || tier > 3) return -1;
        return BLANKS[tier];
    }

    public static int coins(int tier) {
        if (tier < 1 || tier > 3) return -1;
        return COINS[tier];
    }

    public static EmberUpgradeRules.Cost cost(int tier) {
        int b = blanks(tier), c = coins(tier);
        if (b < 0) return EmberUpgradeRules.Cost.NONE;
        return new EmberUpgradeRules.Cost(0, 0, b, 0, c);
    }

    public static int qualityAfter(int quality) {
        return Math.min(Math.max(0, quality), QUALITY_CAP);
    }

    /** null = ok; plan after = converted data (rev not bumped — caller +1) */
    public static String refusal(EmberItemData it, String toFam, int weekUsed, boolean gateOpen) {
        if (it == null) return "请手持余烬刃或护符";
        if (!"blade".equals(it.slot) && !"charm".equals(it.slot)) return "本窗只转化刃/护符";
        if (it.tier < 1 || it.tier > 3) return "T" + it.tier + " 不可转化（需 T1–T3）";
        if (!EmberRunRules.validFamily(toFam)) return "目标族：scorch / burst / sustain";
        if (toFam.equals(it.family)) return "目标族不能与当前相同（" + EmberItemData.familyName(it.family) + "）";
        if (weekUsed >= WEEKLY_CAP) return "本周转化次数已用完（每周 " + WEEKLY_CAP + " 次）";
        String flag = EmberRunRules.directedForgeFlag(it.tier);
        if (flag != null && !gateOpen) return "T" + it.tier + " 转化需本人首通 " + flag.toUpperCase(java.util.Locale.ROOT);
        return null;
    }

    /** same uid; new family/ni; quality capped; enhance 0; craft/affix/pity/sig/rerollN/origin kept; rev unchanged */
    public static EmberItemData plan(EmberItemData it, String toFam) {
        int q = qualityAfter(it.quality);
        return new EmberItemData(it.uid, EmberItemData.templateId(toFam, it.slot, it.tier), toFam, it.slot, it.tier,
                q, it.craft, 0, it.pity, true, it.source, it.version, it.rev,
                it.affix, it.afPity, it.sigCode, it.rerollN, it.origin);
    }
}
