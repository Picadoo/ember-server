package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.PlayerData;

/**
 * D8721: first challenge-mode enter of the day → Resist I {@link #TICKS}.
 * Zero skill_mult. Not EventBrace/ShortEnter densify (challenge enter Resist).
 */
public final class EmberChallengeEnterBrace {

    public static final String C_USED = "p1_chal_enter_brace";
    public static final int TICKS = 40; // 2s

    private EmberChallengeEnterBrace() {}

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
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, TICKS, 0, false, true), true);
            p.sendMessage(EmberRunService.P + "§a挑战护体 · 抗性 I §f" + (TICKS / 20) + "§as（今日一次）");
            try { p.sendActionBar("§a挑战护体"); } catch (Throwable ignored) { }
        } catch (Throwable ignored) { }
        return true;
    }
}
