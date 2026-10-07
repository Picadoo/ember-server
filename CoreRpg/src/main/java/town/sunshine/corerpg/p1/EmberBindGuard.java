package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * D276 / ARCH O9: account-bound P1 materials and gear anti-transfer.
 * Lives outside {@link EmberAfkService} and gates only on {@link EmberMode#active()} —
 * turning AFK off must not disable these guards (fixed in D275; extracted here).
 */
public final class EmberBindGuard implements Listener {

    static final Set<String> BOUND_MATS = new HashSet<String>(Arrays.asList(
            EmberUpgradeRules.MAT_SHARD, EmberUpgradeRules.MAT_CORE, EmberUpgradeRules.MAT_BONE, EmberUpgradeRules.MAT_BLANK));

    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;

    private final CoreRpgPlugin plugin;

    public EmberBindGuard(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /** True when account-bound transfer guards run. Follows P1 master switch only. */
    public static boolean bindGuardsActive() {
        return EmberMode.active();
    }

    /** P1 upgrade materials and P1 gear (bound shards / cores enhance gear; blanks forge it). */
    boolean boundMat(ItemStack s) {
        if (s == null || s.getType() == Material.AIR || !EmberMode.active()) return false;
        NiBridge ni = plugin.getNiBridge();
        String id = ni == null ? null : ni.getNiId(s);
        if (id != null && BOUND_MATS.contains(id)) return true;
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        return ls != null && ls.items().hasData(s);
    }

    /** item frames / armor stands as a mailbox */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFrame(org.bukkit.event.player.PlayerInteractEntityEvent e) {
        if (!bindGuardsActive() || !(e.getRightClicked() instanceof org.bukkit.entity.ItemFrame)) return;
        if (!boundMat(e.getPlayer().getInventory().getItemInMainHand()) && !boundMat(e.getPlayer().getInventory().getItemInOffHand())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能放进展示框。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStand(org.bukkit.event.player.PlayerArmorStandManipulateEvent e) {
        if (!bindGuardsActive() || !boundMat(e.getPlayerItem())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能放到盔甲架上。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (!bindGuardsActive() || !boundMat(e.getItemDrop().getItemStack())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能丢出或转给别人（材料存仓库，装备放装备库）。");
    }

    /** world storage another account could open: chests / shulkers / hoppers / droppers / furnaces … and entity storage
     *  (horse, mule, storage minecart). Not the player's own inventory / crafting grid / ender chest / workbench, not plugin GUIs. */
    static boolean blockContainer(InventoryHolder h, InventoryType t) {
        if (t == InventoryType.CRAFTING || t == InventoryType.PLAYER || t == InventoryType.CREATIVE || t == InventoryType.ENDER_CHEST
                || t == InventoryType.WORKBENCH || h instanceof Player) return false;
        return h instanceof org.bukkit.block.BlockState || h instanceof org.bukkit.block.DoubleChest || h instanceof Entity;
    }

    /** Putting P1 materials into world containers (chests, shulkers, hoppers, horses …) — the alt-account mailbox. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onContainerClick(InventoryClickEvent e) {
        if (!bindGuardsActive() || e.getView().getTopInventory() == null) return;
        if (!blockContainer(e.getView().getTopInventory().getHolder(), e.getView().getTopInventory().getType())) return;
        boolean top = e.getRawSlot() >= 0 && e.getRawSlot() < e.getView().getTopInventory().getSize();
        boolean in = (top && boundMat(e.getCursor()))                                   // place into the container
                || (!top && e.isShiftClick() && boundMat(e.getCurrentItem()))            // shift-move into it
                || (top && e.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY && e.getHotbarButton() >= 0
                    && boundMat(e.getWhoClicked().getInventory().getItem(e.getHotbarButton()))); // hotbar swap into it
        if (!in) return;
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(P + "§c余烬材料和装备账号绑定，不能放进箱子 / 容器（材料存仓库，装备放装备库）。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onContainerDrag(InventoryDragEvent e) {
        if (!bindGuardsActive() || !boundMat(e.getOldCursor())) return;
        if (!blockContainer(e.getView().getTopInventory().getHolder(), e.getView().getTopInventory().getType())) return;
        int size = e.getView().getTopInventory().getSize();
        for (int raw : e.getRawSlots()) if (raw < size) { e.setCancelled(true); return; }
    }
}
