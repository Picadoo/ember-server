package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D484: free enhance-swap → spend/chase acquisition loop.
 * Chat buttons only — no ActionBar HUD family. Zero enhance math / pity / price change.
 */
public final class EmberSwapLoop {

    private EmberSwapLoop() {}

    /** After successful forge {@code swap} commit. */
    public static void afterSwap(Player p, List<TxnItem> list) {
        if (p == null) return;
        String winner = winnerHint(list);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e强化已互换" + (winner.isEmpty() ? "" : " §7· 高强化在 " + winner)
                + " §7· 选下一步（不改强化价/保底）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[去强化]", "/corerpg p1 enhance", "手持件继续强化预览", "GREEN"},
                new String[]{"[装备页]", "/ember_p1_gear", "换上/查看套装", "GOLD"},
                new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"},
                new String[]{"[稍后再说]", null, "强化已在两件上", "GRAY"});
    }

    /** Admin smoke: same button set without a live txn. */
    public static void afterSwap(Player p) {
        afterSwap(p, null);
    }

    /** Prefer the piece that ended with higher enhance. */
    static String winnerHint(List<TxnItem> list) {
        if (list == null || list.isEmpty()) return "";
        EmberItemData best = null;
        int bestE = -1;
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            if (t.after.enhance > bestE) {
                bestE = t.after.enhance;
                best = t.after;
            }
        }
        return best == null ? "" : best.shortLabel() + " +" + bestE;
    }

    public static String followHint() {
        return "[去强化]|[装备页]|[工坊]";
    }

    public static String chatLine() {
        return "§e强化已互换 §7· 选下一步";
    }
}
