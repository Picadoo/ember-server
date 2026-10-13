package town.sunshine.corerpg.p1;

/**
 * TIP-CJ D13315 · 挂机庭「顺手推荐花」智路由钉死（只读 · 零经济 · ≠第五花料）。
 * 优先序：① enhance（gap_enhance1==0 且可强化）→ ② upgrade（非护甲·闸过·lack 空）
 * → ③ refine craft → ④ quality → ⑤ empty。
 * 护甲手持：跳过 2–4；若强化也不可 → empty。
 * kind：{@code enhance|upgrade|refine|quality|empty}；ok：非空→{@code 1}（TrMenu 单条件灰）。
 * 纯逻辑，无 Bukkit；禁抬 UpgradeRules / afk.tiers / daily_kills / 价；禁 confirm。
 */
public final class EmberAfkRecFlower {

    public static final String KIND_ENHANCE = "enhance";
    public static final String KIND_UPGRADE = "upgrade";
    public static final String KIND_REFINE = "refine";
    public static final String KIND_QUALITY = "quality";
    public static final String KIND_EMPTY = "empty";

    public static final String PREVIEW_ENHANCE = "enhance";
    public static final String PREVIEW_UPGRADE = "upgrade";
    public static final String PREVIEW_REFINE = "refine";
    public static final String PREVIEW_QUALITY = "quality";

    /** Immutable pick: kind + preview op (empty when kind=empty). */
    public static final class Pick {
        public final String kind;
        public final String previewOp;

        Pick(String kind, String previewOp) {
            this.kind = kind == null ? KIND_EMPTY : kind;
            this.previewOp = previewOp == null ? "" : previewOp;
        }

        public boolean present() {
            return previewOp != null && !previewOp.isEmpty() && !KIND_EMPTY.equals(kind);
        }
    }

    private static final Pick EMPTY = new Pick(KIND_EMPTY, "");
    private static final Pick ENHANCE = new Pick(KIND_ENHANCE, PREVIEW_ENHANCE);
    private static final Pick UPGRADE = new Pick(KIND_UPGRADE, PREVIEW_UPGRADE);
    private static final Pick REFINE = new Pick(KIND_REFINE, PREVIEW_REFINE);
    private static final Pick QUALITY = new Pick(KIND_QUALITY, PREVIEW_QUALITY);

    private EmberAfkRecFlower() {}

    /**
     * Resolve AFK courtyard recommend-flower.
     * @param armor held is armor → skip upgrade/refine/quality even if those flags true
     * @param enhanceReady recipe_gap_enhance1==0 and enhanceCheck ok
     * @param upgradeReady non-armor upgrade gate + lacking empty
     * @param refineReady non-armor refine plan ok + lacking empty
     * @param qualityReady non-armor quality plan ok + lacking empty
     */
    public static Pick pick(boolean armor, boolean enhanceReady, boolean upgradeReady,
                            boolean refineReady, boolean qualityReady) {
        if (enhanceReady) return ENHANCE;
        if (!armor) {
            if (upgradeReady) return UPGRADE;
            if (refineReady) return REFINE;
            if (qualityReady) return QUALITY;
        }
        return EMPTY;
    }

    /** Non-empty pick → {@code "1"}, else {@code "0"} (TrMenu single check). */
    public static String ok(Pick pick) {
        return pick != null && pick.present() ? "1" : "0";
    }

    /** Display name for menu lore (empty when none). */
    public static String name(Pick pick) {
        if (pick == null || !pick.present()) return "";
        if (KIND_ENHANCE.equals(pick.kind)) return "强化";
        if (KIND_UPGRADE.equals(pick.kind)) return "升阶";
        if (KIND_REFINE.equals(pick.kind)) return "精工";
        if (KIND_QUALITY.equals(pick.kind)) return "成色";
        return "";
    }

    /** Short overview line for {@code %corerpg_p1_afk_rec_flower%}. */
    public static String line(Pick pick) {
        if (pick == null || !pick.present()) return "§8暂无仓够花";
        if (KIND_ENHANCE.equals(pick.kind)) return "§a推荐·强化+1";
        if (KIND_UPGRADE.equals(pick.kind)) return "§a推荐·升阶";
        if (KIND_REFINE.equals(pick.kind)) return "§a推荐·精工";
        if (KIND_QUALITY.equals(pick.kind)) return "§a推荐·成色";
        return "§8暂无仓够花";
    }
}
