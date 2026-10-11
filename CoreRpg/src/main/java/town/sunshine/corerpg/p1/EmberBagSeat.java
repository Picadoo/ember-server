package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D5665: empty blade/charm slot → auto-seat best bag piece (town/hub).
 * Zero skill_mult / gem flat / path densify / ConfirmTokens.
 */
public final class EmberBagSeat {

    /** Latch per day: p1_bag_seat@day — at most one auto-seat burst per calendar day */
    public static final String C_SEAT = "p1_bag_seat";

    private EmberBagSeat() {}

    public static EmberItemData bestEmpty(Player p, EmberLoadout lo, EmberLoadoutService loadouts, String slot) {
        if (p == null || lo == null || loadouts == null || slot == null) return null;
        String preferFam = null;
        if ("blade".equals(slot) && lo.charm != null && !"none".equals(lo.charm.family))
            preferFam = lo.charm.family;
        if ("charm".equals(slot) && lo.blade != null && !"none".equals(lo.blade.family))
            preferFam = lo.blade.family;
        EmberItemData best = null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!slot.equals(x.slot)) continue;
            if (lo.blade != null && x.uid.equals(lo.blade.uid)) continue;
            if (lo.charm != null && x.uid.equals(lo.charm.uid)) continue;
            if (best == null) { best = x; continue; }
            boolean prefer = preferFam != null && preferFam.equals(x.family) && !preferFam.equals(best.family);
            boolean demote = preferFam != null && preferFam.equals(best.family) && !preferFam.equals(x.family);
            if (demote) continue;
            if (prefer || x.tier > best.tier || (x.tier == best.tier && x.enhance > best.enhance)
                    || (x.tier == best.tier && x.enhance == best.enhance && x.quality > best.quality))
                best = x;
        }
        return best;
    }

    /**
     * Fill at most one empty slot (blade first, then charm).
     * @return true when seated
     */
    public static boolean maybeFill(Player p, PlayerData d, EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || d == null || loadouts == null || runs == null) return false;
        if (EmberSkillKit.inDungeon(p, runs)) return false;
        EmberLoadout lo = loadouts.refresh(p);
        if (lo == null) return false;
        boolean any = false;
        for (int pass = 0; pass < 2; pass++) {
            lo = loadouts.refresh(p);
            if (lo == null) break;
            String slot = null;
            if (lo.blade == null) slot = "blade";
            else if (lo.charm == null) slot = "charm";
            if (slot == null) break;
            EmberItemData cand = bestEmpty(p, lo, loadouts, slot);
            if (cand == null) break;
            String where = runs.equipPiece(p, cand, null);
            if (where == null) break;
            any = true;
            p.sendMessage(EmberRunService.P + "§a空槽入座 · §f" + cand.shortLabel()
                    + " §7（" + where + " · 背包补空）");
            try {
                p.sendActionBar("§a入座 · §f" + ChatColor.stripColor(cand.shortLabel()));
            } catch (Throwable ignored) { }
        }
        if (any) d.addPeriodCount(C_SEAT, "day", 1);
        return any;
    }

    public static void scheduleScan(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = (CoreRpgPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null || pl.getEmberRuns() == null) return;
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (!p.isOnline()) return;
            PlayerData d = pl.getDataStore().get(p.getUniqueId());
            maybeFill(p, d, pl.getEmberRuns().loadouts(), pl.getEmberRuns());
        }, 80L);
    }
}
