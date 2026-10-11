package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

/** D12097: first hole auto-seat → sync GemPath once/day. Not craft/remove path densify. */
public final class EmberGemHoleSeatPathSync {
    public static final String C_USED = "p1_gem_hole_seat_path_sync";
    private EmberGemHoleSeatPathSync() {}
    public static boolean usedToday(PlayerData d) { return d != null && d.periodCount(C_USED, "day") > 0; }
    public static boolean ready(PlayerData d) { return d != null && !usedToday(d); }
    public static boolean maybeSync(Player p, PlayerData d, int code) {
        if (p == null || d == null || !EmberGemPath.valid(code) || !ready(d)) return false;
        if (EmberGemPath.get(d) == code) { d.addPeriodCount(C_USED, "day", 1); return false; }
        if (!EmberGemPath.set(d, code)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        try {
            p.sendMessage(EmberRunService.P + "§a入座同步路径 · §f" + EmberGemPath.label(code) + " §a（今日一次）");
            p.sendActionBar("§a宝石路径 · §f" + EmberGemPath.label(code));
        } catch (Throwable ignored) { }
        return true;
    }
}
