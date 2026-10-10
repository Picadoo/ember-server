package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;

/**
 * D662: variety-bounty complete → spend/next-clear chase. Zero bounty table change.
 */
public final class EmberVarietyLoop {

    private EmberVarietyLoop() {}

    public static void afterVarietyDone(Player p, List<String> vbDone) {
        if (p == null || vbDone == null || vbDone.isEmpty()) return;
        String P = EmberRunService.P;
        try { p.sendActionBar("§a花样委托 · §f完成 " + vbDone.size() + " 档"); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7花样完成 · 下一步：",
                new String[]{"[冒险页]", "/ember_p1_adventure", "换图继续花样", "GREEN"},
                new String[]{"[工坊]", "/corerpg p1 forge", "花委托材料", "GOLD"},
                new String[]{"[稍后再说]", null, "奖励已入账", "GRAY"});
    }

    public static String followHint() { return "[冒险页]|[工坊]"; }
}
