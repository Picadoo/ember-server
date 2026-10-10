package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;

/**
 * D2086 CN1: shared piece-aware deterministic next buttons after forge spends.
 * Zero price / rate invent. Not a status remainLine twin.
 */
public final class EmberForgeCommitNext {

    private EmberForgeCommitNext() {}

    public static String[] enhance() {
        return new String[]{"[去强化]", "/corerpg p1 enhance", "同件强化", "GREEN"};
    }

    public static String[] quality(EmberItemData d) {
        if (d != null && EmberUpgradeRules.qualityCost(d.quality) != null)
            return new String[]{"[去成色]", "/corerpg p1 quality", "确定升成色", "YELLOW"};
        return null;
    }

    public static String[] refine(EmberItemData d) {
        if (d != null && EmberUpgradeRules.refineCost(d.craft) != null)
            return new String[]{"[去精工]", "/corerpg p1 refine", "确定升精工", "AQUA"};
        return null;
    }

    public static String[] lock(EmberItemData d) {
        String sl = d != null && "charm".equals(d.slot) ? "charm" : "blade";
        return new String[]{"[锁定洗练]", "/corerpg p1 reroll " + sl + " shard lock", "词条冲档", "BLUE"};
    }

    public static String[] setchase() {
        return new String[]{"[成套追]", "/corerpg p1 setchase", "换装继续", "GOLD"};
    }

    public static String[] forge() {
        return new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "GRAY"};
    }

    /** Fill nulls with fallbacks; always 3 buttons. */
    public static void send3(Player p, String title, String[] a, String[] b, String[] c) {
        if (p == null) return;
        if (a == null) a = enhance();
        if (b == null) b = setchase();
        if (c == null) c = forge();
        ConfirmTokens.sendButtons(p, title == null ? EmberRunService.P : title, a, b, c);
    }

    /**
     * After quality: refine if open, else enhance, else lock; second = enhance/quality-again/setchase.
     */
    public static void afterQuality(Player p, EmberItemData after, boolean canQualityAgain) {
        String[] a = refine(after);
        if (a == null) a = enhance();
        String[] b = canQualityAgain
                ? new String[]{"[再成色]", "/corerpg p1 quality", "同件再投", "YELLOW"}
                : enhance();
        if (a[1].equals(b[1])) b = refine(after) != null ? enhance() : lock(after);
        String[] c = setchase();
        send3(p, EmberRunService.P + "§7成色下一环：", a, b, c);
    }

    /** After refine: again if open, else enhance, else quality, else lock. */
    public static void afterRefine(Player p, EmberItemData after) {
        String[] a = refine(after);
        if (a == null) a = enhance();
        else a = new String[]{"[再精工]", "/corerpg p1 refine", "同件再投", "AQUA"};
        String[] b = quality(after);
        if (b == null) b = enhance();
        if (a[1].equals(b[1])) b = lock(after);
        send3(p, EmberRunService.P + "§7精工下一环：", a, b, setchase());
    }

    /** After brand pin / convert: enhance first (convert zeros enhance). */
    public static void afterPowerReset(Player p, EmberItemData after, String title) {
        String[] a = enhance();
        String[] b = quality(after);
        if (b == null) b = refine(after);
        if (b == null) b = lock(after);
        send3(p, title == null ? EmberRunService.P + "§7下一环：" : title, a, b, setchase());
    }

    /** After gem insert: another empty hole or enhance. */
    public static void afterGemInsert(Player p, EmberItemData after, int gemCode) {
        int hole = EmberGemInsertChase.firstEmptyHole(after);
        String[] a;
        if (hole >= 0 && gemCode > 0) {
            String key = EmberGemPath.key(gemCode);
            a = new String[]{"[再嵌孔" + hole + "]", "/corerpg p1 gem insert " + hole + " " + key,
                    "同石再嵌空孔", "LIGHT_PURPLE"};
        } else {
            a = enhance();
        }
        String[] b = quality(after);
        if (b == null) b = refine(after);
        if (b == null) b = new String[]{"[宝石 list]", "/corerpg p1 gem list", "看孔位", "AQUA"};
        send3(p, EmberRunService.P + "§7镶嵌下一环：", a, b, setchase());
    }

    /** After enhance-swap: push the high-enhance winner into craft slots. */
    public static void afterSwapWinner(Player p, EmberItemData winner) {
        String[] a = enhance();
        String[] b = quality(winner);
        if (b == null) b = refine(winner);
        if (b == null) b = lock(winner);
        send3(p, EmberRunService.P + "§7互换下一环：", a, b, setchase());
    }

    /**
     * After dismantle blanks: spend blanks on held piece — quality/refine/upgrade order.
     * {@code held} may be null → generic upgrade/brand.
     */
    public static void afterBlanks(Player p, EmberItemData held) {
        String[] a = quality(held);
        if (a == null) a = refine(held);
        if (a == null) a = new String[]{"[去升阶]", "/corerpg p1 upgrade", "手持件升阶（花胚料）", "GREEN"};
        String[] b = refine(held);
        if (b == null || (a != null && a[1].equals(b[1]))) {
            b = new String[]{"[去升阶]", "/corerpg p1 upgrade", "手持件升阶（花胚料）", "GOLD"};
        }
        if (a[1].equals(b[1])) {
            b = new String[]{"[去烙纹合成]", "/corerpg p1 brand craft", "碎片→烙纹", "GOLD"};
        }
        String[] c = new String[]{"[撤销分解]", "/corerpg p1 undo", "窗口内可找回", "YELLOW"};
        send3(p, EmberRunService.P + "§7胚料下一环：", a, b, c);
    }


    /** After gear delivery into backpack: commit first craft on that piece. */
    public static void afterGearDeliver(Player p, EmberItemData piece) {
        String[] a = enhance();
        String[] b = quality(piece);
        if (b == null) b = refine(piece);
        if (b == null) b = lock(piece);
        send3(p, EmberRunService.P + "§7到账下一环：", a, b, setchase());
    }

    /** Set formed / focus complete: enhance then craft or gem. */
    public static void afterSetReady(Player p, EmberItemData held, String title) {
        String[] a = enhance();
        String[] b = quality(held);
        if (b == null) b = refine(held);
        if (b == null) b = new String[]{"[镶嵌]", "/corerpg p1 gem list", "嵌空孔", "LIGHT_PURPLE"};
        send3(p, title == null ? EmberRunService.P + "§7成套下一环：" : title, a, b,
                new String[]{"[镶嵌]", "/corerpg p1 gem list", "嵌空孔", "AQUA"});
    }

}
