package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D658/D660 / D2087: refine → piece-aware commit next. Zero refine price.
 */
public final class EmberRefineLoop {

    private EmberRefineLoop() {}

    public static void afterRefine(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        String P = EmberRunService.P;
        EmberItemData after = firstAfter(list);
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.REFINE;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f精工+1");
            try { p.sendActionBar("§a工坊目标 · 精工完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/烙纹/转化…", "GREEN"},
                    new String[]{"[再精工]", "/corerpg p1 refine", "同件再投", "AQUA"},
                    EmberForgeCommitNext.enhance());
            return;
        }
        String rh = refineHint(list);
        p.sendMessage(P + "§e精工已投入" + (rh.isEmpty() ? "" : " §7· " + rh)
                + " §7· 承诺同件下一槽（不改精工价）：");
        try {
            p.sendActionBar(rh.isEmpty() ? "§e精工 · §f同件下一槽" : ("§e精工 · §f" + rh));
        } catch (Throwable ignored) { }
        EmberForgeCommitNext.afterRefine(p, after);
    }

    static EmberItemData firstAfter(List<TxnItem> list) {
        if (list == null) return null;
        for (TxnItem t : list) {
            if (t != null && t.after != null) return t.after;
        }
        return null;
    }

    static String refineHint(List<TxnItem> list) {
        if (list == null) return "";
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            return t.after.shortLabel() + " 精工" + (t.after.craft * 2) + "%";
        }
        return "";
    }

    public static String followHint(boolean goalHit) {
        return goalHit ? "[换下周目标]|[再精工]" : "[再精工]|[去强化]|[去成色]";
    }
}
