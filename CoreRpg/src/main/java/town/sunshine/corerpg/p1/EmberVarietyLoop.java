package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;

/**
 * D662 / D2116: variety-bounty complete → held-piece commit next. Zero bounty table change.
 */
public final class EmberVarietyLoop {

    private EmberVarietyLoop() {}

    public static void afterVarietyDone(Player p, List<String> vbDone) {
        if (p == null || vbDone == null || vbDone.isEmpty()) return;
        String P = EmberRunService.P;
        try { p.sendActionBar("§a花样委托 · §f完成 " + vbDone.size() + " 档"); } catch (Throwable ignored) { }
        EmberItemData held = heldGear(p);
        String[] a = EmberForgeCommitNext.quality(held);
        if (a == null) a = EmberForgeCommitNext.refine(held);
        if (a == null) a = EmberForgeCommitNext.enhance();
        String[] b = EmberForgeCommitNext.refine(held);
        if (b == null || b[1].equals(a[1])) b = EmberForgeCommitNext.enhance();
        if (b[1].equals(a[1])) b = new String[]{"[冒险页]", "/ember_p1_adventure", "换图继续花样", "GREEN"};
        ConfirmTokens.sendButtons(p, P + "§7花样完成 · 同件下一槽：",
                a, b,
                new String[]{"[冒险页]", "/ember_p1_adventure", "换图继续花样", "GRAY"});
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

    public static String followHint() { return "[去成色]|[去强化]|[冒险页]"; }
}
