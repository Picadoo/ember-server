package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D695: set completion chase — progress + next awakening + focus gap.
 * Zero set power / AFK / drop change. Chat+ActionBar only — no button twin.
 */
public final class EmberSetChase {

    private EmberSetChase() {}

    public static String focusGap(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null) return "";
        int focus = EmberSetFocus.get(d);
        String fk = EmberSetFocus.familyKey(focus);
        if (fk == null) return "";
        if (fk.equals(lo.activeSet)) return "";
        String need;
        if (lo.blade == null) need = "缺刃";
        else if (lo.charm == null) need = "缺同族护符";
        else if (!fk.equals(lo.blade.family) && !fk.equals(lo.charm.family))
            need = "刃/符都不是" + EmberItemData.familyName(fk);
        else if (!fk.equals(lo.blade.family)) need = "换" + EmberItemData.familyName(fk) + "刃";
        else need = "换" + EmberItemData.familyName(fk) + "护符";
        return "§e成套追 · 焦点§f" + EmberSetFocus.label(focus) + " §7· " + need;
    }

    public static void tell(Player p, PlayerData d, EmberLoadout lo) {
        if (p == null || lo == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e" + lo.setProgress());
        String next = lo.nextAwakeningHint();
        if (next != null && !next.isEmpty()) p.sendMessage(P + "§7" + next);
        String gap = focusGap(d, lo);
        if (gap != null && !gap.isEmpty()) p.sendMessage(P + gap);
        try {
            if (!"none".equals(lo.activeSet))
                p.sendActionBar("§e成套 · §f" + lo.setLabel());
            else if (gap != null && !gap.isEmpty())
                p.sendActionBar("§e成套追 · §f焦点未齐");
            else
                p.sendActionBar("§e成套 · §7未成套");
        } catch (Throwable ignored) { }
    }

    /** Rising-edge when 2pc forms or awakening bumps. */
    public static void maybeAfterSetChange(Player p, PlayerData d, String prevSet, int prevAwk, EmberLoadout after) {
        if (p == null || after == null) return;
        boolean formed = (prevSet == null || "none".equals(prevSet)) && !"none".equals(after.activeSet);
        boolean awkUp = after.awakening > prevAwk;
        if (!formed && !awkUp) return;
        tell(p, d, after);
    }
}
