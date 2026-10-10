package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D484 / D2100: free enhance-swap → piece-aware commit on winner.
 * Zero enhance math / pity / price change.
 */
public final class EmberSwapLoop {

    private EmberSwapLoop() {}

    /** After successful forge {@code swap} commit. */
    public static void afterSwap(Player p, List<TxnItem> list) {
        if (p == null) return;
        EmberItemData winner = winnerAfter(list);
        String winnerHint = winner == null ? "" : winner.shortLabel() + " +" + winner.enhance;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e强化已互换" + (winnerHint.isEmpty() ? "" : " §7· 高强化在 " + winnerHint)
                + " §7· 承诺同件下一槽（不改强化价/保底）：");
        try { p.sendActionBar("§e互换 · §f同件下一槽"); } catch (Throwable ignored) { }
        EmberForgeCommitNext.afterSwapWinner(p, winner);
    }

    public static void afterSwap(Player p) {
        afterSwap(p, null);
    }

    static EmberItemData winnerAfter(List<TxnItem> list) {
        if (list == null || list.isEmpty()) return null;
        EmberItemData best = null;
        int bestE = -1;
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            if (t.after.enhance > bestE) {
                bestE = t.after.enhance;
                best = t.after;
            }
        }
        return best;
    }

    static String winnerHint(List<TxnItem> list) {
        EmberItemData best = winnerAfter(list);
        return best == null ? "" : best.shortLabel() + " +" + best.enhance;
    }

    public static String followHint() {
        return "[去强化]|[去成色]|[成套追]";
    }

    public static String chatLine() {
        return "§e强化已互换 §7· 选下一步";
    }
}
