package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

/**
 * D2230: AFK gear-stable rising-edge feel — when loadout band hits 够稳 on a tier.
 * D4241: 够稳层当日首次阵亡不计入 death_stop（真容错 · 零日表）.
 * Zero AFK table / spawn / regen / ConfirmTokens. Not a hope PAPI twin.
 */
public final class EmberAfkGearFeel {

    /** Latch prefix: p1_afk_gear_stable_t{1..4}@day */
    public static final String C_STABLE = "p1_afk_gear_stable_t";
    public static final String C_WEAK = "p1_afk_gear_weak_t";
    public static final String C_FORGIVE = "p1_afk_gear_forgive_t";

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

    /**
     * Rising-edge when band is 偏弱 and fight already has ≥2 deaths — suggest forge, no teleport / table.
     */
    public static boolean maybeWeakCue(Player p, PlayerData d, int tier, double atk, double hp, int deaths) {
        if (p == null || d == null || tier < 1 || tier > 4 || deaths < 2) return false;
        if (!"偏弱".equals(EmberAfkGearEff.band(atk, hp, tier))) return false;
        String day = DailyService.today();
        String key = C_WEAK + tier;
        if (d.periodCount(key, day) > 0) return false;
        d.addPeriodCount(key, day, 1);
        String line = "§c挂机装效 · §fT" + tier + " 偏弱 §7· 阵亡" + deaths
                + " · 去工坊强化后再挂（不改日表）";
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
        return true;
    }

    /**
     * D4241: 够稳层当日首次阵亡不计入 death_stop 窗口。零日表改。
     * @return true when this death should be forgiven (not counted)
     */
    public static boolean maybeStableDeathForgive(Player p, PlayerData d, int tier, double atk, double hp) {
        if (p == null || d == null || tier < 1 || tier > 4) return false;
        if (!"够稳".equals(EmberAfkGearEff.band(atk, hp, tier))) return false;
        String day = DailyService.today();
        String key = C_FORGIVE + tier;
        if (d.periodCount(key, day) > 0) return false;
        d.addPeriodCount(key, day, 1);
        String line = "§a挂机装效容错 · §fT" + tier + " 够稳 §7· 本日首次阵亡不计入停挂（不改日表）";
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
        return true;
    }
}
