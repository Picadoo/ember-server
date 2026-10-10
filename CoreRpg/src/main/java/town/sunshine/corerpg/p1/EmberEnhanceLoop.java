package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D485: enhance commit → forge-branch spend chase (continue / quality / refine).
 * Chat buttons only — no ActionBar HUD family. Zero enhance rate / pity / price change.
 */
public final class EmberEnhanceLoop {

    private EmberEnhanceLoop() {}

    /** After forge {@code enhance} commit OK (success or fail both chase). */
    public static void afterEnhance(Player p, List<TxnItem> list) {
        if (p == null) return;
        boolean won = leveled(list);
        String P = EmberRunService.P;
        if (won) {
            p.sendMessage(P + "§e强化成功 §7· 选下一步花法（不改强化价/成色/精工价）：");
            ConfirmTokens.sendButtons(p, P,
                    new String[]{"[再强化]", "/corerpg p1 enhance", "手持件再冲一档", "GREEN"},
                    new String[]{"[去成色]", "/corerpg p1 quality", "成色预览（花材料）", "GOLD"},
                    new String[]{"[去精工]", "/corerpg p1 refine", "精工预览（花材料）", "AQUA"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "GRAY"});
        } else {
            p.sendMessage(P + "§e强化未升档 §7· 材料已扣 · 选下一步（不改保底规则）：");
            ConfirmTokens.sendButtons(p, P,
                    new String[]{"[再试一次]", "/corerpg p1 enhance", "同件再强化（保底累计）", "GREEN"},
                    new String[]{"[去成色]", "/corerpg p1 quality", "先投成色", "GOLD"},
                    new String[]{"[去精工]", "/corerpg p1 refine", "先投精工", "AQUA"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "GRAY"});
        }
    }

    /** Admin smoke: success button set by default; pass fail for fail set. */
    public static void afterEnhance(Player p, boolean won) {
        if (p == null) return;
        if (won) afterEnhance(p, nullWon());
        else afterEnhance(p, nullFail());
    }

    static boolean leveled(List<TxnItem> list) {
        if (list == null) return true; // admin default = success set
        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            if (t.after.enhance > t.before.enhance) return true;
        }
        return false;
    }

    private static List<TxnItem> nullWon() { return null; }
    private static List<TxnItem> nullFail() {
        // empty non-null → leveled=false
        return java.util.Collections.emptyList();
    }

    public static String followHint(boolean won) {
        return won ? "[再强化]|[去成色]|[去精工]" : "[再试一次]|[去成色]|[去精工]";
    }

    public static String chatLine(boolean won) {
        return won ? "§e强化成功 §7· 选下一步花法" : "§e强化未升档 §7· 选下一步";
    }
}
