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
            String gateId = key.substring(5);
            // Stage 4.4 / B0.1: elite gate = Lv + 本周未通关（票在 TicketEntryService 扣；OP 由 DP || %player_is_op%）
            if ("elite".equals(gateId)) {
                EliteService es = plugin.getEliteService();
                return es != null && es.passesGate(player, data) ? "yes" : "no";
            }
            ProgressService ps = plugin.getProgressService();
            return ps == null || ps.passesGate(data, gateId) ? "yes" : "no";
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
        // S0 stamina (design A.6)
        StaminaService st = plugin.getStaminaService();
        if (st != null) {
            PlayerData d = data;
            if ("stamina".equals(key)) return String.valueOf(st.getStamina(d));
            if ("stamina_max".equals(key)) return String.valueOf(st.getMax(d));
            if ("stamina_bank".equals(key)) { st.ensure(d); return String.valueOf(d.getStaminaBank()); }
            if ("stamina_cost_daily".equals(key)) return String.valueOf(st.costOf("daily"));
            if ("stamina_cost_weekly".equals(key)) return String.valueOf(st.costOf("weekly"));
            if ("stamina_cost_abyss".equals(key)) return String.valueOf(st.costOf("abyss"));
            if ("stamina_cost_elite".equals(key)) return String.valueOf(st.costOf("elite"));
            if ("stamina_cost_raid".equals(key)) return String.valueOf(st.costOf("raid"));
            if ("stamina_reset".equals(key)) return "今日 0:00";
            if ("stamina_credit_weekly".equals(key)) { st.ensure(d); return String.valueOf(d.getWeeklyGrantCreditWeekly()); }
            if ("stamina_credit_elite".equals(key)) { st.ensure(d); return String.valueOf(d.getWeeklyGrantCreditElite()); }
            if ("stamina_credit_raid".equals(key)) { st.ensure(d); return String.valueOf(d.getWeeklyGrantCreditRaid()); }
        }
        return null;
    }
}
