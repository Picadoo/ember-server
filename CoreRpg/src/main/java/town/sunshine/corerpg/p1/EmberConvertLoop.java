package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D474: weekly-convert success feel + post-convert spend chase (enhance was zeroed).
 * Zero convert price / weekly cap / AFK change. Not a path picker.
 */
public final class EmberConvertLoop {

    private EmberConvertLoop() {}

    /**
     * After successful convert.
     * @param toFam destination family key
     * @param hadEnhance whether piece had enhance&gt;0 before convert (now 0)
     */
    public static void afterConvert(Player p, PlayerData d, String toFam, boolean hadEnhance) {
        afterConvert(p, d, toFam, hadEnhance, null);
    }

    /** D2089: optional after piece for commit-next. */
    public static void afterConvert(Player p, PlayerData d, String toFam, boolean hadEnhance, EmberItemData after) {
        if (p == null || toFam == null) return;
        String famName = EmberItemData.familyName(toFam);
        String feel = "§a转化 · §f" + famName + " §7完成";
        try { p.sendActionBar(feel); } catch (Throwable ignored) { }

        String P = EmberRunService.P;
        int path = EmberConvertPath.get(d);
        String pathFam = EmberConvertPath.familyKey(path);
        boolean pathHit = pathFam != null && pathFam.equals(toFam);
        if (pathHit) {
            p.sendMessage(P + "§a转化路径达成：§f" + famName + " §7· 本周转到目标族了");
            // D728: weekly convert loop complete (path + spend)
            p.sendMessage(P + "§a本周转化环完成 · §7强化已归零可去工坊 · 下周可再转");
            try { p.sendActionBar("§a转化环 · 本周完成"); } catch (Throwable ignored) { }
        }
        p.sendMessage(P + EmberConvertPath.weekLine(d)); // D736 always show remain/done

        // D643: weekly forge-goal CONVERT complete loop (acquisition feel · zero price/cap change)
        boolean goalHit = EmberForgeGoal.get(d) == EmberForgeGoal.CONVERT;
        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f每周转化 → " + famName);
            if (!pathHit) {
                try { p.sendActionBar("§a工坊目标 · 转化完成"); } catch (Throwable ignored) { }
            }
        }

        // enhance wiped — piece-aware enhance commit; optional align setfocus
        if (goalHit) {
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    EmberForgeCommitNext.enhance(),
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/精工/烙纹…", "GREEN"},
                    new String[]{"[对齐套装焦点]", "/corerpg p1 setfocus " + toFam,
                            "套装焦点也设成" + famName, "AQUA"});
        } else {
            String[] b = EmberForgeCommitNext.quality(after);
            if (b == null) b = EmberForgeCommitNext.refine(after);
            if (b == null) b = new String[]{"[对齐套装焦点]", "/corerpg p1 setfocus " + toFam,
                    "套装焦点也设成" + famName, "AQUA"};
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    EmberForgeCommitNext.enhance(),
                    b,
                    new String[]{"[转化页]", "/corerpg p1 convert menu", "看本周剩余", "GRAY"});
        }
    }

    /** Pure: path celebration when convert dest matches convertpath. */
    public static boolean pathHit(int convertPathId, String toFam) {
        String fk = EmberConvertPath.familyKey(convertPathId);
        return fk != null && toFam != null && fk.equals(toFam);
    }
}
