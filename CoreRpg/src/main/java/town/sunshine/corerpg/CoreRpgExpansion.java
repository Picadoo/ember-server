package town.sunshine.corerpg;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
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
        if ("flex_skill".equals(key) || "flex_skill_id".equals(key)) return data.getFlexSkillId();
        if ("flex_skill_name".equals(key) || "flex_display".equals(key)) {
            FlexSkillService fs = plugin.getFlexSkillService();
            if (fs == null || !data.hasFlexSkill()) return "未装配";
            if (fs.isHuohenActive(player)
                    && (FlexSkillService.PILOT_ID.equals(data.getFlexSkillId())
                        || (fs.getFlex(data.getFlexSkillId()) != null
                            && "step".equals(fs.getFlex(data.getFlexSkillId()).type)))) {
                return "火痕步";
            }
            FlexSkillService.FlexDef def = fs.getFlex(data.getFlexSkillId());
            return def == null ? data.getFlexSkillId() : ChatColor.stripColor(def.display);
        }
        if ("kit_shape".equals(key) || "slash_shape".equals(key)) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            int id = town.sunshine.corerpg.p1.EmberSkillKit.shapeId(data, runs);
            return town.sunshine.corerpg.p1.EmberSkillKit.shapeName(id);
        }
        if ("kit_shape_key".equals(key)) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            return town.sunshine.corerpg.p1.EmberSkillKit.shapeKey(town.sunshine.corerpg.p1.EmberSkillKit.shapeId(data, runs));
        }
        if ("kit_dash".equals(key) || "kit_dash_unlock".equals(key)) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            return town.sunshine.corerpg.p1.EmberSkillKit.dashUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_shape_unlock".equals(key)) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            return town.sunshine.corerpg.p1.EmberSkillKit.shapeUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_step_unlock".equals(key) || "kit_huohen_unlock".equals(key)) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            return town.sunshine.corerpg.p1.EmberSkillKit.stepVariantUnlocked(data, runs) ? "yes" : "no";
        }
        if ("kit_step".equals(key) || "kit_step_name".equals(key) || "kit_huohen".equals(key)) {
            FlexSkillService fs = plugin.getFlexSkillService();
            boolean huohen = fs != null && fs.isHuohenActive(player);
            return town.sunshine.corerpg.p1.EmberSkillKit.stepDisplayName(huohen);
        }
        if ("kit_charge".equals(key) || "skill_charge_ready".equals(key)) {
            town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
            if (ls == null) return "就绪";
            long left = ls.state(player.getUniqueId()).skillCdUntil - System.currentTimeMillis();
            return left > 0 ? ("冷却 " + (int) Math.ceil(left / 1000.0) + "s") : "就绪";
        }
        if ("talent_points".equals(key) || "talent_available".equals(key)) {
            return String.valueOf(data.getTalentPointsAvailable());
        }
        if ("ember_xp".equals(key)) return String.valueOf(data.getEmberXp());
        if ("ember_level".equals(key) || "level".equals(key)) return String.valueOf(data.getEmberLevel());
        if (key.startsWith("p1_")) { // G04 冒险 page / DP entry pass
            town.sunshine.corerpg.p1.EmberRunService r = plugin.getEmberRuns();
            return r == null ? "" : r.placeholder(player, key.substring(3));
        }
        if (key.startsWith("gate_")) {
            String gateId = key.substring(5);
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
            // D198 / S0-1: P1 on → legacy guild boss closed (OP passes via %player_is_op% in option.yml)
            if (LegacyGate.guildBossPassClosed(town.sunshine.corerpg.p1.EmberMode.active())) return "no";
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
            // Non-daily enter gray: blocked=1 when no weekly free credit AND stamina < cost (read-only).
            if ("stamina_blocked_weekly".equals(key)) {
                st.ensure(d);
                if (d.getWeeklyGrantCreditWeekly() > 0) return "0";
                return st.getStamina(d) < st.costOf("weekly") ? "1" : "0";
            }
            if ("stamina_blocked_raid".equals(key)) {
                st.ensure(d);
                if (d.getWeeklyGrantCreditRaid() > 0) return "0";
                return st.getStamina(d) < st.costOf("raid") ? "1" : "0";
            }
            if ("stamina_blocked_elite".equals(key)) {
                st.ensure(d);
                if (d.getWeeklyGrantCreditElite() > 0) return "0";
                return st.getStamina(d) < st.costOf("elite") ? "1" : "0";
            }
        }
        return null;
    }
}
