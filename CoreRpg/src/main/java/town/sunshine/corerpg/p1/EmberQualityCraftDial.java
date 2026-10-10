package town.sunshine.corerpg.p1;

/**
 * D1915 GS3-A: quality/craft certainty dial — display only.
 * Quality forge ladder 0→1→2 (legendary not craftable). Craft 0→1→2→3.
 * Never invents pity math.
 */
public final class EmberQualityCraftDial {

    private EmberQualityCraftDial() {}

    public static int qualityStep(int q) {
        if (q < 0) return 0;
        if (q > 2) return 2;
        return q;
    }

    public static int qualityMax() { return 2; }

    public static int craftStep(int craft) {
        if (craft < 0) return 0;
        if (craft > 3) return 3;
        return craft;
    }

    public static int craftMax() { return 3; }

    public static String qualityDial(EmberItemData d) {
        if (d == null) return "成色表盘 · 无件";
        if (d.quality >= 3) return "成色表盘 满·极品 · 只从掉落";
        if (d.quality >= 2)
            return "成色表盘 2/2 · 卓越 · 极品不可养成（只掉落）";
        int step = qualityStep(d.quality);
        String from = EmberItemData.qualityName(d.quality);
        String to = EmberItemData.qualityName(d.quality + 1);
        return "成色表盘 " + step + "/" + qualityMax() + " · " + from + "→" + to + " · 确定成功";
    }

    public static String craftDial(EmberItemData d) {
        if (d == null) return "精工表盘 · 无件";
        if (d.craft >= 3) return "精工表盘 3/3 · 6% · 已满";
        int step = craftStep(d.craft);
        int fromPct = d.craft * 2;
        int toPct = (d.craft + 1) * 2;
        return "精工表盘 " + step + "/" + craftMax() + " · " + fromPct + "%→" + toPct + "% · 确定成功";
    }

    public static String previewLine(EmberItemData d, boolean qualityNotCraft) {
        if (d == null) return "表盘 · 无件";
        return qualityNotCraft ? qualityDial(d) : craftDial(d);
    }
}
