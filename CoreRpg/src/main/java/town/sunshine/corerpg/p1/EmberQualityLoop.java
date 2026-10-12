package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D658 / D2087: quality spend → piece-aware commit next. Zero quality price change.
 */
public final class EmberQualityLoop {

    private EmberQualityLoop() {}

    public static void afterQuality(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        String P = EmberRunService.P;
        EmberItemData after = firstAfter(list);
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.QUALITY;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f成色升档");
            try { p.sendActionBar("§a工坊目标 · 成色完成"); } catch (Throwable ignored) { }
            // D12262: send3
            EmberForgeCommitNext.send3(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/精工/烙纹…", "GREEN"},
                    new String[]{"[再成色]", "/corerpg p1 quality", "同件再投", "YELLOW"},
                    EmberForgeCommitNext.refine(after) != null
                            ? EmberForgeCommitNext.refine(after)
                            : EmberForgeCommitNext.enhance());
            return;
        }
        String hint = winnerHint(list);
        p.sendMessage(P + "§e成色已投入" + (hint.isEmpty() ? "" : " §7· " + hint)
                + " §7· 承诺同件下一槽（不改成色价）：");
        try {
            p.sendActionBar(hint.isEmpty() ? "§e成色 · §f同件下一槽" : ("§e成色 · §f" + hint));
        } catch (Throwable ignored) { }
        boolean canAgain = after != null && EmberUpgradeRules.qualityCost(after.quality) != null;
        EmberForgeCommitNext.afterQuality(p, after, canAgain);
    }

    static EmberItemData firstAfter(List<TxnItem> list) {
        if (list == null) return null;
        for (TxnItem t : list) {
            if (t != null && t.after != null) return t.after;
        }
        return null;
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
