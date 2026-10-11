package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import town.sunshine.corerpg.PlayerData;

/**
 * D9313: first raid enter of the day → Resist I {@link #TICKS}.
 * Zero skill_mult. Not ChallengeEnterBrace densify (raid enter).
 */
public final class EmberRaidEnterBrace {

    public static final String C_USED = "p1_raid_enter_brace";
    public static final int TICKS = 40; // 2s

    private EmberRaidEnterBrace() {}

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
            p.sendMessage(EmberRunService.P + "§a团本护体 · 抗性 I §f" + (TICKS / 20) + "§as（今日一次）");
            try { p.sendActionBar("§a团本护体"); } catch (Throwable ignored) { }
        } catch (Throwable ignored) { }
        return true;
    }
}
