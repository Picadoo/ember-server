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
        }

        // enhance wiped — chase forge; optional align setfocus
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[去工坊强化]", "/corerpg p1 forge",
                        hadEnhance ? "强化已归零 · 去重新强化" : "去工坊继续养成", "GOLD"},
                new String[]{"[对齐套装焦点]", "/corerpg p1 setfocus " + toFam,
                        "套装焦点也设成" + famName, "AQUA"},
                new String[]{"[转化页]", "/corerpg p1 convert menu", "看本周剩余", "GRAY"});
    }

    /** Pure: path celebration when convert dest matches convertpath. */
    public static boolean pathHit(int convertPathId, String toFam) {
        String fk = EmberConvertPath.familyKey(convertPathId);
        return fk != null && toFam != null && fk.equals(toFam);
    }
}
