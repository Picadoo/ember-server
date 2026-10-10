package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import town.sunshine.corerpg.ConfirmTokens;

/**
 * D459: first-clear skill unlock path feel — announce the new combat key when Q02–Q05 clears.
 * Zero skill power / CD / gate change.
 */
public final class EmberSkillUnlock {

    private EmberSkillUnlock() {}

    /** @return display line without prefix, or null if this map unlocks no skill-kit key */
    public static String unlockLine(String mapKey) {
        if (mapKey == null) return null;
        if (EmberSkillKit.UNLOCK_DASH.equals(mapKey))
            return "烬突 · 潜行+F（与烬斩共享充能 · 挂机不自动烬突）";
        if (EmberSkillKit.UNLOCK_PARRY.equals(mapKey))
            return "余烬招架 · 单按 Q（首领预警砸中你后再按）";
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey))
            return "烬斩符文 · 扇形 / 直线 / 环斩（出本才可换）";
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey))
            return "身法 · 潜行+Q（前冲/后撤 · 两件套可变套装身法）";
        return null;
    }

    public static String shortName(String mapKey) {
        if (EmberSkillKit.UNLOCK_DASH.equals(mapKey)) return "烬突";
        if (EmberSkillKit.UNLOCK_PARRY.equals(mapKey)) return "余烬招架";
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey)) return "烬斩符文";
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey)) return "身法";
        return null;
    }

    /** Chat + button once per first-clear of a skill-unlock map. */
    public static void announce(Player p, String mapKey) {
        if (p == null) return;
        String line = unlockLine(mapKey);
        if (line == null) return;
        String P = EmberRunService.P;
        ConfirmTokens.sendButton(p, P + "§6新技能解锁：§e" + line + " ",
                "[打开技能组]", "trmenu open ember_skill_kit",
                "查看按键 / 符文 / 身法");
        p.sendMessage(P + "§7不用打命令：主菜单 → 技能组");
    }
}
