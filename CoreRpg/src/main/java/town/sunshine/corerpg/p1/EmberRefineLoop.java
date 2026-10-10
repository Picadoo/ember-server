package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D658/D660: refine spend chase + forge-goal REFINE complete. Zero refine rate / price.
 */
public final class EmberRefineLoop {

    private EmberRefineLoop() {}

    public static void afterRefine(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        String P = EmberRunService.P;
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.REFINE;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f精工+1");
            try { p.sendActionBar("§a工坊目标 · 精工完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/烙纹/转化…", "GREEN"},
                    new String[]{"[再精工]", "/corerpg p1 refine", "同件再投", "AQUA"},
                    new String[]{"[去强化]", "/corerpg p1 enhance", "强化预览", "GOLD"});
            return;
        }
        p.sendMessage(P + "§e精工已投入 §7· 选下一步（不改精工价）：");
        try { p.sendActionBar("§e精工完成 · §f选下一步"); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P, // D884
                new String[]{"[再精工]", "/corerpg p1 refine", "同件再投", "AQUA"},
                new String[]{"[去强化]", "/corerpg p1 enhance", "强化预览", "GREEN"},
                new String[]{"[成套追]", "/corerpg p1 setchase", "换装继续", "GOLD"});
    }

    public static String followHint(boolean goalHit) {
        return goalHit ? "[换下周目标]|[再精工]" : "[再精工]|[去强化]|[去成色]";
    }
}
