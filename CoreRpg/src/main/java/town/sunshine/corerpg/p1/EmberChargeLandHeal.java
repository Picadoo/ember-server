package town.sunshine.corerpg.p1;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

/**
 * D8145: first charged skill land (hit≥1) of the day → heal {@link #PCT} max HP.
 * Zero skill_mult. Not ChargeMiss densify.
 */
public final class EmberChargeLandHeal {

    public static final String C_USED = "p1_charge_land_heal";
    public static final double PCT = 0.025;

    private EmberChargeLandHeal() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeHeal(Player p, PlayerData d) {
        if (p == null || !p.isOnline() || p.isDead() || !ready(d)) return false;
        AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (a == null) return false;
        double max = a.getValue();
        if (max <= 0) return false;
        double cur = p.getHealth();
        if (cur >= max - 0.01) return false;
        d.addPeriodCount(C_USED, "day", 1);
        double add = max * PCT;
        p.setHealth(Math.min(max, cur + add));
        try {
            p.sendMessage(EmberRunService.P + "§a蓄力回春 · 回复 §f" + String.format(java.util.Locale.ROOT, "%.1f", add) + "§a（今日一次）");
            p.sendActionBar("§a蓄力回春");
        } catch (Throwable ignored) { }
        return true;
    }
}
