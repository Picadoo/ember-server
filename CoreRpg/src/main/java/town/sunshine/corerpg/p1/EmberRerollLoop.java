package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D2047 RL1: affix reroll / lock spend → next chase (Tempering-style).
 * Zero coin/shard/lock_shard / pity / pool change. Not a status remainLine twin.
 */
public final class EmberRerollLoop {

    private EmberRerollLoop() {}

    /**
     * After empty-slot install or keep-new commit.
     * @param slot blade|charm
     * @param lockUsed true if this roll was a lock roll
     * @param affixName display name of current affix (may be empty)
     * @param canLockAgain true if lock is offered (rollable + below quality cap)
     */
    public static void afterSettled(Player p, PlayerData d, String slot, boolean lockUsed,
                                    String affixName, boolean canLockAgain) {
        if (p == null) return;
        String P = EmberRunService.P;
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.LOCK;
        String name = affixName == null || affixName.isEmpty() ? "词条" : affixName;
        String sl = "charm".equals(slot) ? "charm" : "blade";

        if (goalHit && lockUsed) {
            p.sendMessage(P + "§a本周工坊目标完成：§f锁定冲档");
            try { p.sendActionBar("§a工坊目标 · 锁定洗练完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/成色…", "GREEN"},
                    new String[]{"[再锁定洗]", "/corerpg p1 reroll " + sl + " shard lock", "同词条再冲档", "AQUA"},
                    new String[]{"[去强化]", "/corerpg p1 enhance", "词条定了冲强化", "GOLD"});
            return;
        }

        if (canLockAgain) {
            try {
                p.sendActionBar(lockUsed ? ("§a锁定洗练 · §f" + name) : ("§e洗练落定 · §f可锁定 " + name));
            } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7锁定冲档下一环：",
                    new String[]{"[锁定" + strip(name) + "再洗]", "/corerpg p1 reroll " + sl + " shard lock",
                            "保留类型只重抽档 · 另加锁碎", "AQUA"},
                    new String[]{"[普通再洗]", "/corerpg p1 reroll " + sl + " shard", "换类型赌", "GRAY"},
                    new String[]{"[去强化]", "/corerpg p1 enhance", "先强化", "GREEN"});
            return;
        }

        try { p.sendActionBar("§a词条落定 · §f" + name); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7洗练下一环：",
                new String[]{"[去强化]", "/corerpg p1 enhance", "词条定了冲强化", "GREEN"},
                new String[]{"[成套追]", "/corerpg p1 setchase", "换装", "GOLD"},
                new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"});
    }

    static String strip(String s) {
        if (s == null) return "词条";
        String t = s.replaceAll("§.", "");
        if (t.length() > 6) t = t.substring(0, 6);
        return t.isEmpty() ? "词条" : t;
    }

    public static String followHint() { return "[锁定再洗]|[普通再洗]|[去强化]"; }
}
