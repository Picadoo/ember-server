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


    /**
     * D2156: armor dismantle paid only tenths (no whole blank yet).
     * Show progress toward next blank + piece-aware spend if blanks already in vault.
     * Zero tenths table change.
     */
    public static void afterTenths(Player p, int tenthsNow, int gained) {
        if (p == null || gained <= 0) return;
        String P = EmberRunService.P;
        int show = tenthsNow % 10;
        int need = show == 0 ? 10 : (10 - show);
        p.sendMessage(P + "§e护甲零头 §f0." + show + " §7/ 1.0 §8（+" + gained + " · 满 10 出 1 胚）"
                + " §7· 再攒 §f0." + need);
        try {
            p.sendActionBar("§e零头 · §f0." + show + "/1.0 §7· +" + gained);
        } catch (Throwable ignored) { }
        EmberItemData held = heldGear(p);
        String[] a = new String[]{"[再分解]", "/corerpg p1 scrap", "继续攒零头", "YELLOW"};
        String[] b = EmberForgeCommitNext.quality(held);
        if (b == null) b = EmberForgeCommitNext.refine(held);
        // TIP-AL D12687: when quality/refine both null (armor / empty), never hardcode upgrade-on-armor
        if (b == null) b = EmberForgeCommitNext.setchase();
        EmberForgeCommitNext.send3(p, P + "§7零头下一环：", a, b,
                EmberForgeCommitNext.forge());
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
