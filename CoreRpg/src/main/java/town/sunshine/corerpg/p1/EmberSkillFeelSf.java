package town.sunshine.corerpg.p1;

/**
 * D1154 · skill Stage2 SF1–SF3: shape feel clarity (display only).
 * Zero skill_mult / CD / damage / sticky.
 */
public final class EmberSkillFeelSf {

    private EmberSkillFeelSf() {}

    /** SF1: short shape label for ActionBar. */
    public static String shapeShort(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return "直线";
        if (shapeId == EmberSkillKit.SHAPE_RING) return "环斩";
        return "扇形";
    }

    /**
     * SF1 + SF2: 烬斩命中确认文案（命中数 ≥2 时带 ×N，不改伤害）。
     * @return empty when hits &lt; 1
     */
    public static String slashConfirm(int shapeId, int hits) {
        if (hits < 1) return "";
        String s = shapeShort(shapeId) + "烬斩命中";
        if (hits > 1) s = s + " ×" + hits;
        return s;
    }

    /** D1208: 落空反馈（展示 only · 非 SF4）. */
    public static String slashMiss(int shapeId) {
        return shapeShort(shapeId) + "烬斩落空";
    }

    /** D1240: 烬突受阻（展示 only）. */
    public static String dashBlocked() {
        return "烬突受阻";
    }

    /**
     * SF1–SF3: confirm + optional set-fallout half (e.g. {@code " · 烬刃余烬"}).
     */
    public static String slashConfirmWithSet(int shapeId, int hits, String setFalloutHalf) {
        String base = slashConfirm(shapeId, hits);
        if (base.isEmpty()) return "";
        if (setFalloutHalf == null || setFalloutHalf.isEmpty()) return base;
        return base + setFalloutHalf;
    }

    /** SF3: 形状与套装落点同屏半行（展示 only）。 */
    public static String setShapeHalf(String family) {
        String shape = EmberSkillKit.preferredShapeLabel(family);
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) return "§e形状 §f" + shape;
        return "§e形状 §f" + shape + " §8· 落点 §7" + fall;
    }

    /** Worn-shape variant for in-combat / kit when actual shape id known. */
    public static String setShapeHalf(String family, int wornShapeId) {
        String shape = EmberSkillKit.shapeName(wornShapeId);
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) return "§e形状 §f" + shape;
        return "§e形状 §f" + shape + " §8· 落点 §7" + fall;
    }

    public static String setFalloutLabel(String family) {
        if ("scorch".equals(family)) return "点燃/火痕";
        if ("burst".equals(family)) return "缓速/闪";
        if ("sustain".equals(family)) return "微疗/护";
        return "";
    }

    /** ActionBar-safe set half matching {@link town.sunshine.corerpg.SkillService} slashSetTip style. */
    public static String setFalloutActionHalf(String family) {
        if ("scorch".equals(family)) return " · 烬刃余烬";
        if ("burst".equals(family)) return " · 烬刃缓速";
        if ("sustain".equals(family)) return " · 烬刃微疗";
        return "";
    }
}
