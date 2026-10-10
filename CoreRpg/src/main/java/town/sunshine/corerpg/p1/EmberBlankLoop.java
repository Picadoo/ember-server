package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;

/**
 * D482: dismantle → blank spend acquisition loop (upgrade / brand craft).
 * Chat buttons only — no ActionBar HUD family. Zero blank yield / craft ratio / AFK change.
 */
public final class EmberBlankLoop {

    private EmberBlankLoop() {}

    /** After successful dismantle that paid ≥1 blank. */
    public static void afterDismantle(Player p, int blanks) {
        if (p == null || blanks <= 0) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e胚料 ×" + blanks + " §7已进仓 · 选下一步花法（不改产量/价）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[去升阶]", "/corerpg p1 upgrade", "手持件升阶预览（花胚料）", "GREEN"},
                new String[]{"[去烙纹合成]", "/corerpg p1 brand craft", "碎片→烙纹（胚料另用于升阶）", "GOLD"},
                new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"},
                new String[]{"[稍后再说]", null, "材料仍在仓库", "GRAY"});
    }

    /** Admin / unit: button set label. */
    public static String followHint() {
        return "[去升阶]|[去烙纹合成]|[工坊]";
    }

    public static String chatLine(int blanks) {
        return "§e胚料 ×" + Math.max(0, blanks) + " §7已进仓 · 选下一步花法";
    }
}
