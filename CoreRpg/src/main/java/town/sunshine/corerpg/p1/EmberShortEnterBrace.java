package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.PlayerData;

/**
 * D9537: first short-expedition enter of the day → Speed I {@link #TICKS}.
 * Zero skill_mult. Not ShortEnterSpeed densify (potion brace, not move speed latch).
 * Not Challenge/Raid/Rush/Abyss/Main densify (short).
 */
public final class EmberShortEnterBrace {

    public static final String C_USED = "p1_short_enter_brace";
    public static final int TICKS = 40; // 2s

    private EmberShortEnterBrace() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean maybeBrace(Player p, PlayerData d) {
        if (p == null || !p.isOnline() || p.isDead() || !ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TICKS, 0, false, true), true);
            p.sendMessage(EmberRunService.P + "§a短征起跑 · 速度 I §f" + (TICKS / 20) + "§as（今日一次）");
            try { p.sendActionBar("§a短征起跑"); } catch (Throwable ignored) { }
        } catch (Throwable ignored) { }
        return true;
    }
}
