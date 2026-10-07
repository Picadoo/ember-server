package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.p1.EmberLoadoutService;
import town.sunshine.corerpg.p1.EmberRunService;
import town.sunshine.corerpg.p1.EmberParry;
import town.sunshine.corerpg.p1.EmberSkillKit;

/** D240 / ARCH S3-11: %corerpg_*% flex skill + skill kit section (moved verbatim from CoreRpgExpansion). */
final class CorePapiKit {

    private CorePapiKit() {}

    static String resolve(CoreRpgPlugin plugin, Player player, PlayerData data, String key) {
        if ("flex_skill".equals(key) || "flex_skill_id".equals(key)) return data.getFlexSkillId();
        if ("flex_skill_name".equals(key) || "flex_display".equals(key)) {
            FlexSkillService fs = plugin.getFlexSkillService();
            if (fs == null || !data.hasFlexSkill()) return "未装配";
            boolean stepLike = FlexSkillService.PILOT_ID.equals(data.getFlexSkillId())
                    || (fs.getFlex(data.getFlexSkillId()) != null
                        && "step".equals(fs.getFlex(data.getFlexSkillId()).type));
            if (stepLike) {
                boolean huohen = fs.isHuohenActive(player);
                boolean back = EmberSkillKit.stepBackward(data);
                return EmberSkillKit.stepDisplayName(huohen, back);
            }
            FlexSkillService.FlexDef def = fs.getFlex(data.getFlexSkillId());
            return def == null ? data.getFlexSkillId() : ChatColor.stripColor(def.display);
        }
        if ("kit_shape".equals(key) || "slash_shape".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            int id = EmberSkillKit.shapeId(data, runs);
            return EmberSkillKit.shapeName(id);
        }
        if ("kit_shape_key".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            return EmberSkillKit.shapeKey(EmberSkillKit.shapeId(data, runs));
        }
        if ("kit_dash".equals(key) || "kit_dash_unlock".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            return EmberSkillKit.dashUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_shape_unlock".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            return EmberSkillKit.shapeUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_step_unlock".equals(key) || "kit_huohen_unlock".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            return EmberSkillKit.stepVariantUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_step".equals(key) || "kit_step_name".equals(key) || "kit_huohen".equals(key)) {
            FlexSkillService fs = plugin.getFlexSkillService();
            boolean huohen = fs != null && fs.isHuohenActive(player);
            boolean back = EmberSkillKit.stepBackward(data);
            return EmberSkillKit.stepDisplayName(huohen, back);
        }
        if ("kit_step_dir".equals(key) || "step_dir".equals(key)) {
            return EmberSkillKit.stepDirName(EmberSkillKit.stepDirId(data));
        }
        if ("kit_step_dir_key".equals(key)) {
            return EmberSkillKit.stepDirKey(EmberSkillKit.stepDirId(data));
        }
        if ("kit_charge".equals(key) || "skill_charge_ready".equals(key)) {
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            if (ls == null) return "就绪";
            long left = ls.state(player.getUniqueId()).skillCdUntil - System.currentTimeMillis();
            return left > 0 ? ("冷却 " + (int) Math.ceil(left / 1000.0) + "s") : "就绪";
        }
        if ("kit_parry".equals(key) || "kit_parry_unlock".equals(key)) {
            EmberRunService runs = plugin.getEmberRuns();
            return EmberSkillKit.parryUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_parry_name".equals(key)) {
            return EmberSkillKit.DISPLAY_PARRY;
        }
        if ("kit_parry_cd".equals(key)) {
            EmberParry parry = EmberParry.get();
            if (parry == null) return "就绪";
            int left = parry.cdLeftSeconds(player.getUniqueId());
            return left > 0 ? ("冷却 " + left + "s") : "就绪";
        }
        return null;
    }
}
