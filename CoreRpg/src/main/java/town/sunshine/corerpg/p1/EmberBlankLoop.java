package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.CoreRpgPlugin;

/**
 * D482 / D2101: dismantle → blank spend on held piece (quality/refine/upgrade).
 * Zero blank yield / craft ratio / AFK change.
 */
public final class EmberBlankLoop {

    private EmberBlankLoop() {}

    /** After successful dismantle that paid ≥1 blank. */
    public static void afterDismantle(Player p, int blanks) {
        afterDismantle(p, blanks, heldGear(p));
    }

    /** D2101: optional held piece for piece-aware blank spend. */
    public static void afterDismantle(Player p, int blanks, EmberItemData held) {
        if (p == null || blanks <= 0) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e胚料 ×" + blanks + " §7已进仓 · 按手持件承诺花法（不改产量/价）：");
        try { p.sendActionBar("§e分解入账 · 胚料 ×" + blanks); } catch (Throwable ignored) { }
        EmberForgeCommitNext.afterBlanks(p, held);
    }

    static EmberItemData heldGear(Player p) {
        if (p == null) return null;
        try {
            Plugin raw = Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (!(raw instanceof CoreRpgPlugin)) return null;
            CoreRpgPlugin pl = (CoreRpgPlugin) raw;
            if (pl.getEmberLoadouts() == null) return null;
            ItemStack hand = p.getInventory().getItemInMainHand();
            EmberItems.Read r = pl.getEmberLoadouts().items().read(hand);
            return r != null && r.ok() ? r.data : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String followHint() {
        return "[去成色]|[去精工]|[去升阶]";
    }

    public static String chatLine(int blanks) {
        return "§e胚料 ×" + Math.max(0, blanks) + " §7已进仓 · 选下一步花法";
    }
}
