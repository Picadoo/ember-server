package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D658: quality spend → next forge chase.
 * D1931 FG-Q3: forge-goal QUALITY complete (mirrors RefineLoop). Zero quality rate / price change.
 */
public final class EmberQualityLoop {

    private EmberQualityLoop() {}

    public static void afterQuality(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        String P = EmberRunService.P;
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.QUALITY;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f成色升档");
            try { p.sendActionBar("§a工坊目标 · 成色完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/精工/烙纹…", "GREEN"},
                    new String[]{"[再成色]", "/corerpg p1 quality", "同件再投", "YELLOW"},
                    new String[]{"[去精工]", "/corerpg p1 refine", "成色后精工", "AQUA"});
            return;
        }
        String hint = winnerHint(list);
        p.sendMessage(P + "§e成色已投入" + (hint.isEmpty() ? "" : " §7· " + hint)
                + " §7· 选下一步（不改成色价）：");
        try {
            p.sendActionBar(hint.isEmpty() ? "§e成色完成 · §f选下一步" : ("§e成色 · §f" + hint));
        } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[去精工]", "/corerpg p1 refine", "精工预览", "AQUA"},
                new String[]{"[去强化]", "/corerpg p1 enhance", "强化预览", "GREEN"},
                new String[]{"[成套追]", "/corerpg p1 setchase", "换装继续", "GOLD"});
    }

    static String winnerHint(List<TxnItem> list) {
        if (list == null) return "";
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            return t.after.shortLabel() + " 成色" + t.after.quality;
        }
        return "";
    }

    public static String followHint() { return "[去精工]|[再成色]|[去强化]"; }
}
