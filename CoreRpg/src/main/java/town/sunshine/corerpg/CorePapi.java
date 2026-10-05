package town.sunshine.corerpg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * D240 / ARCH S3-11: section routing for PlaceholderAPI {@code %corerpg_*%} ({@link CoreRpgExpansion}).
 * Bukkit-free so unit tests pin which section answers each key. Sections:
 * {@link CorePapiAccount} (account / quest / mail / guild / cash), {@link CorePapiKit} (flex + skill kit),
 * {@link CorePapiProgress} (level / talent / vip / gate_), {@link CorePapiStamina} (S0 stamina),
 * {@code p1_} → {@code EmberRunService.placeholder} ({@code town.sunshine.corerpg.p1.EmberRunPapi}).
 * The pre-D240 chain had no overlapping keys, so routing order only matters for {@code p1_} / {@code gate_} prefixes.
 */
public final class CorePapi {

    private CorePapi() {}

    public enum Section { ACCOUNT, CASH, KIT, PROGRESS, GATE, P1, STAMINA, NONE }

    static final Set<String> ACCOUNT = set("coin", "quest", "quest_objective", "quest_chapter", "signed", "activity",
            "abyss_used", "calamity_next", "covenant", "guildboss_pass", "mail_unread");
    static final Set<String> CASH = set("crystal_cash", "cash", "monthly", "daily_tickets", "daily_cap");
    static final Set<String> KIT = set("flex_skill", "flex_skill_id", "flex_skill_name", "flex_display",
            "kit_shape", "slash_shape", "kit_shape_key", "kit_dash", "kit_dash_unlock", "kit_shape_unlock",
            "kit_step_unlock", "kit_huohen_unlock", "kit_step", "kit_step_name", "kit_huohen",
            "kit_step_dir", "step_dir", "kit_step_dir_key", "kit_charge", "skill_charge_ready");
    static final Set<String> PROGRESS = set("talent_points", "talent_available", "ember_xp", "ember_level", "level",
            "ember_xp_need", "vip_title", "talent_spent", "talent_earned");
    static final Set<String> STAMINA = set("stamina", "stamina_max", "stamina_bank",
            "stamina_cost_daily", "stamina_cost_weekly", "stamina_cost_abyss", "stamina_cost_elite", "stamina_cost_raid",
            "stamina_reset", "stamina_credit_weekly", "stamina_credit_elite", "stamina_credit_raid",
            "stamina_blocked_weekly", "stamina_blocked_raid", "stamina_blocked_elite");

    /** key is already lower-cased. Never null. */
    public static Section route(String key) {
        if (key == null) return Section.NONE;
        if (ACCOUNT.contains(key)) return Section.ACCOUNT;
        if (KIT.contains(key)) return Section.KIT;
        if (PROGRESS.contains(key)) return Section.PROGRESS;
        if (key.startsWith("p1_")) return Section.P1; // G04 冒险 page / DP entry pass
        if (key.startsWith("gate_")) return Section.GATE;
        if (CASH.contains(key)) return Section.CASH;
        if (STAMINA.contains(key)) return Section.STAMINA;
        return Section.NONE;
    }

    private static Set<String> set(String... keys) {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(keys)));
    }
}
