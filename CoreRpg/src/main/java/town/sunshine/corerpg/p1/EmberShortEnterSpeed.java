package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.PlayerData;

/**
 * D8593: first short-expedition enter of the day → Speed I {@link #SPEED_TICKS}.
 * Zero skill_mult. Not DoorOpen densify (short enter, not room door).
 */
public final class EmberShortEnterSpeed {

    public static final String C_USED = "p1_short_enter_speed";
    public static final int SPEED_TICKS = 40; // 2s

    private EmberShortEnterSpeed() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeSpeed(Player p, PlayerData d) {
        if (p == null || !p.isOnline() || p.isDead() || !ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_TICKS, 0, false, true), true);
            p.sendMessage(EmberRunService.P + "§b短征疾步 · 速度 I §f" + (SPEED_TICKS / 20) + "§bs（今日一次）");
            try { p.sendActionBar("§b短征疾步"); } catch (Throwable ignored) { }
        } catch (Throwable ignored) { }
        return true;
    }
}
