package town.sunshine.corerpg;

import org.bukkit.entity.Player;

/** D240 / ARCH S3-11: %corerpg_*% account / quest / mail / guild + cash sections (moved verbatim from CoreRpgExpansion). */
final class CorePapiAccount {

    private CorePapiAccount() {}

    static String account(CoreRpgPlugin plugin, Player player, PlayerData data, String key) {
        if ("coin".equals(key)) return String.valueOf(data.getCoin());
        if ("quest".equals(key) || "quest_objective".equals(key)) {
            QuestService qs = plugin.getQuestService();
            return qs == null ? "" : qs.objective(player);
        }
        if ("quest_chapter".equals(key)) {
            QuestService qs = plugin.getQuestService();
            return qs == null ? "" : qs.chapterLabel(player);
        }
        if ("signed".equals(key)) return DailyService.isToday(data.getLastSignDate()) ? "1" : "0";
        if ("activity".equals(key)) return String.valueOf(data.getActivity());
        if ("abyss_used".equals(key)) return String.valueOf(data.getAbyssUsedToday());
        if ("calamity_next".equals(key)) {
            CalamityService c = plugin.getCalamityService();
            return c == null ? "-" : c.nextWindowLabel();
        }
        if ("covenant".equals(key)) return data.getCovenant();
        if ("guildboss_pass".equals(key)) {
            // D198 / S0-1: P1 on → legacy guild boss closed (OP passes via %player_is_op% in option.yml)
            if (LegacyGate.guildBossPassClosed(town.sunshine.corerpg.p1.EmberMode.active())) return "no";
            GuildService gs = plugin.getGuildService();
            return gs != null && gs.hasBossPass(player.getUniqueId()) ? "yes" : "no";
        }
        if ("mail_unread".equals(key)) {
            MailService mail = plugin.getMailService();
            return mail == null ? "0" : String.valueOf(mail.countUnread(player.getUniqueId()));
        }
        return null;
    }

    static String cash(CoreRpgPlugin plugin, PlayerData data, String key) {
        if ("crystal_cash".equals(key) || "cash".equals(key)) {
            return String.valueOf(data.getCrystalCash());
        }
        if ("monthly".equals(key)) {
            CashService cash = plugin.getCashService();
            boolean active = cash != null && cash.isMonthlyActive(data);
            return active ? "1" : "0";
        }
        if ("daily_tickets".equals(key)) {
            if (plugin.getCashService() != null) plugin.getCashService().ensureCashDay(data);
            return String.valueOf(data.getDailyTicketsGranted());
        }
        if ("daily_cap".equals(key)) {
            CashService cash = plugin.getCashService();
            return cash == null ? "6" : String.valueOf(cash.getHardCap());
        }
        return null;
    }
}
