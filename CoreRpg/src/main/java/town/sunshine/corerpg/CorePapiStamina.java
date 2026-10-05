package town.sunshine.corerpg;

/** D240 / ARCH S3-11: %corerpg_stamina*% section — S0 stamina (design A.6), moved verbatim from CoreRpgExpansion. */
final class CorePapiStamina {

    private CorePapiStamina() {}

    /** null when StaminaService is absent or the key is unknown (PAPI leaves the placeholder as-is, as before). */
    static String resolve(StaminaService st, PlayerData d, String key) {
        if (st == null) return null;
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
        return null;
    }
}
