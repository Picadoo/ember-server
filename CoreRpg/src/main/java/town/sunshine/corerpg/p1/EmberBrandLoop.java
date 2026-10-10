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
        try {
            p.sendActionBar("§6烙纹 ×1 §7· 可定向到刃/护符");
        } catch (Throwable ignored) { }
        p.sendMessage(P + "§e烙纹已入手 · 选下一步定向（不改价）：");
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
        if (p == null) return;
        String name = affixName == null || affixName.isEmpty() ? "烙纹" : affixName;
        String line = "§6烙纹 · §f" + name + " §a定向完成";
        try {
            p.sendActionBar(line);
        } catch (Throwable ignored) { }
    }

    /** Pure: craft-follow button priority label for tests. */
    public static String craftFollowHint(int brandPathId) {
        if (EmberBrandPath.valid(brandPathId))
            return EmberBrandPath.pinLabel(brandPathId);
        return "[猎缀]|[余烬]|[定身]";
    }
}
