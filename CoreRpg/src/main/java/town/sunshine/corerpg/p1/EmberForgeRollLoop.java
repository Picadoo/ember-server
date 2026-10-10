package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D661/D664: forge-roll success → forge-goal ROLL complete + gear chase.
 * Zero roll price / quality table / AFK change.
 */
public final class EmberForgeRollLoop {

    private EmberForgeRollLoop() {}

    public static void afterRoll(Player p, PlayerData d, String fam, String slot, int tier) {
        if (p == null) return;
        String P = EmberRunService.P;
        String famName = fam == null ? "?" : EmberItemData.familyName(fam);
        String slotName = "charm".equals(slot) ? "护符" : "刃";
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.ROLL;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f随机锻造 → T" + tier + " " + famName + " " + slotName);
            try { p.sendActionBar("§a工坊目标 · 随机锻完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/精工/转化…", "GREEN"},
                    new String[]{"[装备库到账]", "/corerpg p1 deliver", "领进背包/库", "GOLD"},
                    new String[]{"[对齐套装]", "/corerpg p1 setfocus " + (fam == null ? "burst" : fam),
                            "套装焦点→" + famName, "AQUA"});
            return;
        }
        try { p.sendActionBar("§d随机锻完成 · §fT" + tier + " " + famName); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[装备库到账]", "/corerpg p1 deliver", "领进背包/库", "GOLD"},
                new String[]{"[去强化]", "/corerpg p1 enhance", "到手后冲强化", "GREEN"},
                new String[]{"[对齐套装]", "/corerpg p1 setfocus " + (fam == null ? "burst" : fam),
                        "套装焦点→" + famName, "AQUA"},
                new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "GRAY"});
    }

    public static String followHint(boolean goalHit) {
        return goalHit ? "[换下周目标]|[装备库到账]" : "[装备库到账]|[去强化]|[对齐套装]";
    }
}
