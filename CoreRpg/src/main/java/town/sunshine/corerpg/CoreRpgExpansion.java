package town.sunshine.corerpg;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public final class CoreRpgExpansion extends PlaceholderExpansion {
    private final CoreRpgPlugin plugin;
    public CoreRpgExpansion(CoreRpgPlugin plugin) { this.plugin = plugin; }
    @Override public String getIdentifier() { return "corerpg"; }
    @Override public String getAuthor() { return "sunshine-town"; }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) return "";
        PlayerData data = plugin.getDataStore().get(player.getUniqueId());
        String key = params.toLowerCase();
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
        if ("talent_points".equals(key) || "talent_available".equals(key)) {
            return String.valueOf(data.getTalentPointsAvailable());
        }
        if ("ember_xp".equals(key)) return String.valueOf(data.getEmberXp());
        if ("ember_level".equals(key) || "level".equals(key)) return String.valueOf(data.getEmberLevel());
        if (key.startsWith("gate_")) {
            ProgressService ps = plugin.getProgressService();
            return ps == null || ps.passesGate(data, key.substring(5)) ? "yes" : "no";
        }
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
        if ("guildboss_pass".equals(key)) {
            GuildService gs = plugin.getGuildService();
            return gs != null && gs.hasBossPass(player.getUniqueId()) ? "yes" : "no";
        }
        if ("mail_unread".equals(key)) {
            MailService mail = plugin.getMailService();
            return mail == null ? "0" : String.valueOf(mail.countUnread(player.getUniqueId()));
        }
        return null;
    }
}
