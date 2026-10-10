package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D658: quality spend → next forge chase. Zero quality rate / price change.
 */
public final class EmberQualityLoop {

    private EmberQualityLoop() {}

    public static void afterQuality(Player p, List<TxnItem> list) {
        if (p == null) return;
        String P = EmberRunService.P;
        String hint = winnerHint(list);
        p.sendMessage(P + "§e成色已投入" + (hint.isEmpty() ? "" : " §7· " + hint)
                + " §7· 选下一步（不改成色价）：");
        try { p.sendActionBar("§e成色完成 · §f选下一步"); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P, // D884
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
