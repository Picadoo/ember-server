package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

/**
 * D2230: AFK gear-stable rising-edge feel — when loadout band hits 够稳 on a tier.
 * Zero AFK table / spawn / regen / ConfirmTokens. Not a hope PAPI twin.
 */
public final class EmberAfkGearFeel {

    /** Latch prefix: p1_afk_gear_stable_t{1..4}@day */
    public static final String C_STABLE = "p1_afk_gear_stable_t";

    private EmberAfkGearFeel() {}

    /**
     * @return true when cue fired this call
     */
    public static boolean maybeStableCue(Player p, PlayerData d, int tier, double atk, double hp) {
        if (p == null || d == null || tier < 1 || tier > 4) return false;
        String band = EmberAfkGearEff.band(atk, hp, tier);
        if (!"够稳".equals(band)) return false;
        String day = DailyService.today();
        String key = C_STABLE + tier;
        if (d.periodCount(key, day) > 0) return false;
        d.addPeriodCount(key, day, 1);
        String line = "§a挂机装效 · §fT" + tier + " 够稳 §7· 这层站得住了（不改日表）";
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
        return true;
    }
}
