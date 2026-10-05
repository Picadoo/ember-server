package town.sunshine.corerpg;

import org.bukkit.entity.Player;

/** D240 / ARCH S3-11: %corerpg_*% level / talent / vip + gate_ section (moved verbatim from CoreRpgExpansion). */
final class CorePapiProgress {

    private CorePapiProgress() {}

    static String progress(CoreRpgPlugin plugin, PlayerData data, String key) {
        if ("talent_points".equals(key) || "talent_available".equals(key)) {
            return String.valueOf(data.getTalentPointsAvailable());
        }
        if ("ember_xp".equals(key)) return String.valueOf(data.getEmberXp());
        if ("ember_level".equals(key) || "level".equals(key)) return String.valueOf(data.getEmberLevel());
        if ("ember_xp_need".equals(key)) {
            ProgressService ps = plugin.getProgressService();
            return ps == null ? "-" : String.valueOf(ps.xpToNext(data.getEmberLevel()));
        }
        if ("vip_title".equals(key)) {
            ProgressService ps = plugin.getProgressService();
            return ps == null ? String.valueOf(data.getVipTier()) : ps.tierName(data.getVipTier());
        }
        if ("talent_spent".equals(key)) return String.valueOf(data.getTalentPointsSpent());
        if ("talent_earned".equals(key)) return String.valueOf(data.getTalentPointsEarned());
        return null;
    }

    /** %corerpg_gate_&lt;id&gt;% (DP option.yml conditions). */
    static String gate(CoreRpgPlugin plugin, Player player, PlayerData data, String gateId) {
        // D198 / S0-1: P1 on → legacy DP gates closed for everyone (OP passes via %player_is_op% in option.yml)
        if (LegacyGate.gateClosed(town.sunshine.corerpg.p1.EmberMode.active(), gateId)) return "no";
        // Stage 4.4 / S0: elite gate = Lv + 本周未通关（体力 / 本周免费在 TicketEntryService 扣；OP 由 DP || %player_is_op%）
        if ("elite".equals(gateId)) {
            EliteService es = plugin.getEliteService();
            return es != null && es.passesGate(player, data) ? "yes" : "no";
        }
        ProgressService ps = plugin.getProgressService();
        return ps == null || ps.passesGate(data, gateId) ? "yes" : "no";
    }
}
