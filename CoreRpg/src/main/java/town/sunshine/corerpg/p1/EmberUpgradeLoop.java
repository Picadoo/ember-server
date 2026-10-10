package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D487: upgrade commit → power spend chase (enhance / gear / forge).
 * Chat buttons only — no ActionBar HUD family. Zero upgrade cost / blank yield / AFK change.
 */
public final class EmberUpgradeLoop {

    private EmberUpgradeLoop() {}

    /** After successful forge {@code upgrade} commit. */
    public static void afterUpgrade(Player p, List<TxnItem> list) {
        if (p == null) return;
        String piece = pieceHint(list);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e升阶完成" + (piece.isEmpty() ? "" : " §7· " + piece)
                + " §7· 选下一步把战力做实（不改升阶价/胚料产量）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[去强化]", "/corerpg p1 enhance", "同件继续强化（强化轨保留）", "GREEN"},
                new String[]{"[装备页]", "/ember_p1_gear", "换上/看套装", "GOLD"},
                new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"},
                new String[]{"[稍后再说]", null, "升阶已落账", "GRAY"});
    }

    /** Admin smoke. */
    public static void afterUpgrade(Player p) {
        afterUpgrade(p, null);
    }

    static String pieceHint(List<TxnItem> list) {
        if (list == null || list.isEmpty()) return "";
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            String before = t.before == null ? "?" : ("T" + t.before.tier);
            return before + "→" + t.after.shortLabel();
        }
        return "";
    }

    public static String followHint() {
        return "[去强化]|[装备页]|[工坊]";
    }

    public static String chatLine() {
        return "§e升阶完成 §7· 选下一步把战力做实";
    }
}
