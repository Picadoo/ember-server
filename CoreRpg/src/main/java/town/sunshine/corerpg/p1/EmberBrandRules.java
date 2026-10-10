package town.sunshine.corerpg.p1;

/**
 * D429 · 烙纹定向纯规则（DESIGN-ember-forge-brand-playstyle / D167 切片）。
 * 不抬 AFK 表、不改 enhance 价；只定合成/定向价与锻造次数。
 */
public final class EmberBrandRules {
    private EmberBrandRules() {}

    public static final String MAT_BRAND = "mat_ember_brand";
    /** 40 余烬碎片 → 1 烙纹（D167 / 批 A 锁定） */
    public static final int CRAFT_SHARDS = 40;
    /** 每件锻造次数上限（洗练与定向共用已用计数） */
    public static final int MAX_CHARGES = 13;
    /** T0 不开定向；T1/T2/T3 */
    private static final int[] PIN_BRANDS = {0, 2, 4, 6};
    private static final int[] PIN_COINS = {0, 300, 600, 1000};

    public static int pinBrands(int tier) {
        if (tier < 1 || tier > 3) return -1;
        return PIN_BRANDS[tier];
    }

    public static int pinCoins(int tier) {
        if (tier < 1 || tier > 3) return -1;
        return PIN_COINS[tier];
    }

    public static int chargesLeft(int used) {
        return Math.max(0, MAX_CHARGES - Math.max(0, used));
    }

    public static boolean canPinTier(int tier) {
        return tier >= 1 && tier <= 3;
    }

    /** null = ok */
    public static String pinRefusal(int tier, int chargesUsed, String affixId, EmberAffix.Rules rules, String slot) {
        if (!canPinTier(tier)) return "T" + tier + " 不开烙纹定向（需 T1–T3）";
        if (chargesLeft(chargesUsed) <= 0) return "锻造次数已用完，只能换底子";
        if (affixId == null || affixId.isEmpty()) return "未指定词条类型";
        if (rules == null) return "词条表未加载";
        EmberAffix.Def d = rules.def(affixId);
        if (d == null) return "未知词条：" + affixId;
        if (!d.slot.equals(slot)) return d.name + " 不属于本部位（" + slot + "）";
        if (!d.rollable) return d.name + " 已移出洗练池，不能定向";
        return null;
    }
}
