package town.sunshine.corerpg.p1;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.PlayerData;

/**
 * D8225: first melee hit of the day → Slow I {@link #TICKS} on target.
 * Zero skill_mult. Not Slash/Charge heal densify.
 */
public final class EmberMeleeFirstSlow {

    public static final String C_USED = "p1_melee_first_slow";
    public static final int TICKS = 20; // 1s

    private EmberMeleeFirstSlow() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeSlow(Player p, PlayerData d, LivingEntity target) {
        if (p == null || target == null || target.isDead() || !ready(d)) return false;
        if (target instanceof Player) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TICKS, 0, false, true), true);
            p.sendMessage(EmberRunService.P + "§a首刀减速 · 缓速 I §f1§as（今日一次）");
            try { p.sendActionBar("§a首刀减速"); } catch (Throwable ignored) { }
        } catch (Throwable ignored) { }
        return true;
    }
}
