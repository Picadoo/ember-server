package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D4289: first time a gem code is combat-active after seat → one-shot live cue.
 * Steady/drain/gale always-on; sharp only when blade T2+. Zero flat raise / ConfirmTokens.
 */
public final class EmberGemSeatLive {

    /** Latch: p1_gem_seat_live_{code}@all */
    public static final String C_LIVE = "p1_gem_seat_live_";

    private EmberGemSeatLive() {}

    public static String effectLine(int code) {
        return EmberGems.shortEffect(code);
    }

    /** True when this code would contribute to loadout combat right now. */
    public static boolean combatActive(int code, EmberItemData blade, EmberItemData charm) {
        if (code <= 0) return false;
        if (code == EmberGems.CODE_SHARP) {
            return EmberGems.flatA(blade, charm) > 0;
        }
        if (code == EmberGems.CODE_STEADY) return EmberGems.flatD(blade, charm) > 0;
        if (code == EmberGems.CODE_DRAIN) return EmberGems.lifeStealPct(blade, charm) > 0;
        if (code == EmberGems.CODE_GALE) return EmberGems.moveSpeedPct(blade, charm) > 0;
        return false;
    }

    /**
     * After a gem was seated on held piece: if code newly combat-active and never latched, fire cue.
     * @return true when cue fired
     */
    public static boolean maybeAfterSeat(Player p, PlayerData d, int code, EmberLoadoutService loadouts) {
        if (p == null || code <= 0 || loadouts == null) return false;
        String line = effectLine(code);
        if (line == null) return false;
        EmberLoadout lo = loadouts.get(p);
        if (lo == null) return false;
        if (!combatActive(code, lo.blade, lo.charm)) return false;
        String key = C_LIVE + code;
        if (d != null && d.periodCount(key, "all") > 0) return false;
        if (d != null) d.addPeriodCount(key, "all", 1);
        String P = EmberRunService.P;
        p.sendMessage(P + "§a宝石生效 · §f" + EmberItemData.gemName(code) + " §7· " + line
                + " §8（不改数值表）");
        try {
            p.sendActionBar("§a宝石生效 · §f" + EmberItemData.gemName(code));
        } catch (Throwable ignored) { }
        return true;
    }
}
