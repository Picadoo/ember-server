package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D471: brand craft → pin acquisition loop + pin-success feel flash.
 * Zero pin price / craft ratio / AFK change. Not a weekly path picker.
 */
public final class EmberBrandLoop {

    private EmberBrandLoop() {}

    /** After successful craft: cue pin next step (uses brandpath if set). */
    public static void afterCraft(Player p, PlayerData d) {
        if (p == null) return;
        String P = EmberRunService.P;
        int path = EmberBrandPath.get(d);
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.BRAND; // D660
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f合成烙纹");
            try { p.sendActionBar("§a工坊目标 · 烙纹完成"); } catch (Throwable ignored) { }
        } else {
            try {
                p.sendActionBar("§6烙纹 ×1 §7· 可定向到刃/护符");
            } catch (Throwable ignored) { }
        }
        p.sendMessage(P + "§e烙纹已入手 · 选下一步定向（不改价）：");
        if (goalHit) {
            ConfirmTokens.sendButtons(p, P,
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/精工/转化…", "GREEN"},
                    new String[]{EmberBrandPath.valid(path) ? EmberBrandPath.pinLabel(path) : "[猎缀]",
                            EmberBrandPath.valid(path) ? EmberBrandPath.pinCmd(path) : "/corerpg p1 brand pin blade b_affix",
                            EmberBrandPath.valid(path) ? EmberBrandPath.tip(path) : EmberBrandPath.tip(EmberBrandPath.HUNT), "GOLD"},
                    new String[]{"[烙纹页]", "/corerpg p1 brand menu", "五键定向", "AQUA"});
            return;
        }
        if (EmberBrandPath.valid(path)) {
            ConfirmTokens.sendButtons(p, P,
                    new String[]{EmberBrandPath.pinLabel(path), EmberBrandPath.pinCmd(path), EmberBrandPath.tip(path), "GREEN"},
                    new String[]{"[烙纹页]", "/corerpg p1 brand menu", "五键定向", "GOLD"},
                    new String[]{"[改路径]", "/corerpg p1 brandpath", "猎缀/余烬/定身", "GRAY"});
            return;
        }
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[猎缀]", "/corerpg p1 brand pin blade b_affix", EmberBrandPath.tip(EmberBrandPath.HUNT), "GOLD"},
                new String[]{"[余烬]", "/corerpg p1 brand pin blade b_set", EmberBrandPath.tip(EmberBrandPath.EMBER), "RED"},
                new String[]{"[定身]", "/corerpg p1 brand pin charm c_tele", EmberBrandPath.tip(EmberBrandPath.BIND), "AQUA"},
                new String[]{"[烙纹页]", "/corerpg p1 brand menu", "全部类型", "GRAY"});
    }

    /** Pin success feel: ActionBar only (chat already has 定向完成 detail). */
    public static void afterPin(Player p, String affixName) {
        afterPin(p, affixName, null, 0, 0);
    }

    /** D2088: piece-aware commit next after pin. */
    public static void afterPin(Player p, String affixName, EmberItemData after) {
        afterPin(p, affixName, after, 0, 0);
    }

    /**
     * D2182: if charges remain and brandpath set, prefer re-pin on same path.
     * Zero MAX_CHARGES / pin price change.
     */
    public static void afterPin(Player p, String affixName, EmberItemData after, int chargesLeft, int brandPathId) {
        if (p == null) return;
        String name = affixName == null || affixName.isEmpty() ? "烙纹" : affixName;
        String line = "§6烙纹 · §f" + name + " §a定向完成"
                + (chargesLeft > 0 ? (" §7· 剩 §f" + chargesLeft + "§7 次") : "");
        try {
            p.sendActionBar(line);
        } catch (Throwable ignored) { }
        String title = EmberRunService.P + "§7烙纹下一环：";
        if (chargesLeft > 0 && EmberBrandPath.valid(brandPathId)) {
            String[] a = new String[]{EmberBrandPath.pinLabel(brandPathId), EmberBrandPath.pinCmd(brandPathId),
                    EmberBrandPath.tip(brandPathId) + " · 剩" + chargesLeft, "LIGHT_PURPLE"};
            String[] b = EmberForgeCommitNext.enhance();
            String[] c = EmberForgeCommitNext.quality(after);
            if (c == null) c = EmberForgeCommitNext.refine(after);
            if (c == null) c = EmberForgeCommitNext.setchase();
            EmberForgeCommitNext.send3(p, title, a, b, c);
            return;
        }
        EmberForgeCommitNext.afterPowerReset(p, after, title);
    }

    /** Pure: craft-follow button priority label for tests. */
    public static String craftFollowHint(int brandPathId) {
        if (EmberBrandPath.valid(brandPathId))
            return EmberBrandPath.pinLabel(brandPathId);
        return "[猎缀]|[余烬]|[定身]";
    }
}
