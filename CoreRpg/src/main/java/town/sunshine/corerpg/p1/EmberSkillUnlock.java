package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D459: first-clear skill unlock path feel.
 * D460: on Q04/Q05 unlock, offer a one-shot playstyle pick (符文三选一 / 身法方向二选一).
 * Zero skill power / CD table / gate-map change.
 */
public final class EmberSkillUnlock {

    /** Rising-edge offer latch (1 = buttons already sent for this character). */
    public static final String C_SHAPE_OFFERED = "p1_shape_pick_offered";
    public static final String C_STEP_OFFERED = "p1_step_pick_offered";

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

    /** Chat + button once per first-clear of a skill-unlock map; Q04/Q05 also offer playstyle pick. */
    public static void announce(Player p, String mapKey) {
        if (p == null) return;
        String line = unlockLine(mapKey);
        if (line == null) return;
        String P = EmberRunService.P;
        ConfirmTokens.sendButton(p, P + "§6新技能解锁：§e" + line + " ",
                "[打开技能组]", "trmenu open ember_skill_kit",
                "查看按键 / 符文 / 身法");
        p.sendMessage(P + "§7不用打命令：主菜单 → 技能组");
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey)) scheduleShapePick(p);
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey)) scheduleStepPick(p);
    }

    /** Admin / smoke: force re-offer without latch (clears then offers). */
    public static void forceOfferShape(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_SHAPE_OFFERED, "all");
            if (cur > 0) d.addPeriodCount(C_SHAPE_OFFERED, "all", -cur);
        }
        offerShapePick(p);
    }

    public static void forceOfferStep(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_STEP_OFFERED, "all");
            if (cur > 0) d.addPeriodCount(C_STEP_OFFERED, "all", -cur);
        }
        offerStepPick(p);
    }

    static void scheduleShapePick(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerShapePick(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerShapePick(p);
        }, 40L); // ~2s — settle often still mid-exit
    }

    static void scheduleStepPick(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerStepPick(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerStepPick(p);
        }, 40L);
    }

    /** One-shot 三选一 for 烬斩符文. Latched per character. */
    public static void offerShapePick(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_SHAPE_OFFERED, "all") > 0) return;
        d.addPeriodCount(C_SHAPE_OFFERED, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一个烬斩符文（玩法侧移 · 随时可回城再换）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[扇形]", "/corerpg skill shape fan", "近距扇面 · 默认", "GOLD"},
                new String[]{"[直线穿刺]", "/corerpg skill shape line", "中距直线", "AQUA"},
                new String[]{"[原地环斩]", "/corerpg skill shape ring", "环身近战", "LIGHT_PURPLE"});
    }

    /** One-shot 二选一 for 身法方向. Latched per character. */
    public static void offerStepPick(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_STEP_OFFERED, "all") > 0) return;
        d.addPeriodCount(C_STEP_OFFERED, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一个身法方向（前冲 / 后撤 · 随时可回城再换）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[前冲]", "/corerpg skill dir forward", "潜行+Q 向前", "GREEN"},
                new String[]{"[后撤]", "/corerpg skill dir back", "潜行+Q 向后", "YELLOW"});
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        return ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}
