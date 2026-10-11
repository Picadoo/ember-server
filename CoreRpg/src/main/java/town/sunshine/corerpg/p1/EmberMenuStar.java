package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * D12129: sticky hotbar «余烬星» (nether_star) opens TrMenu {@code ember_hub} via Items binding.
 * Also redirects players who landed in vanilla {@code world*} into ember_hub plaza near 灰烛.
 * Zero power / AFK / sx / forge rates — menu entry only. Never teach typing /ember.
 */
public final class EmberMenuStar implements Listener {

    public static final String DISPLAY = ChatColor.GOLD + "余烬星";
    public static final String HUB = "ember_hub";
    /** Plaza near 灰烛 (quest.yml guide ~-16.5,58,106.5); feet here face the guide. */
    public static final double HUB_X = -16.5;
    public static final double HUB_Y = 58.0;
    public static final double HUB_Z = 110.0;
    public static final float HUB_YAW = 180.0f;
    /** Hotbar slot 8 (rightmost); slot 0–1 reserved for blade / potions. */
    public static final int HOTBAR_SLOT = 8;

    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    private static final Set<String> VANILLA = new HashSet<String>(Arrays.asList(
            "world", "world_nether", "world_the_end"));

    private final CoreRpgPlugin plugin;

    public EmberMenuStar(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /** Bukkit-free: strip color and match display name. */
    public static boolean isMenuStarName(String display) {
        if (display == null) return false;
        return "余烬星".equals(ChatColor.stripColor(display).trim());
    }

    public static boolean isMenuStar(ItemStack s) {
        if (s == null || s.getType() != Material.NETHER_STAR) return false;
        ItemMeta m = s.getItemMeta();
        return m != null && m.hasDisplayName() && isMenuStarName(m.getDisplayName());
    }

    public static ItemStack create() {
        ItemStack s = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta m = s.getItemMeta();
        m.setDisplayName(DISPLAY);
        m.setLore(Arrays.asList(
                ChatColor.GRAY + "右键打开 " + ChatColor.WHITE + "冒险枢纽",
                ChatColor.DARK_GRAY + "绑定 · 勿丢 · 勿敲指令"));
        s.setItemMeta(m);
        return s;
    }

    /** True when inventory already holds a named 余烬星. */
    public static boolean hasStar(PlayerInventory inv) {
        if (inv == null) return false;
        for (ItemStack s : inv.getContents()) {
            if (isMenuStar(s)) return true;
        }
        return false;
    }

    /**
     * Place star in hotbar slot 8 if empty or already a star; else first empty hotbar; else add.
     * Returns true when a new star was given.
     */
    public static boolean ensure(Player p) {
        if (p == null || !p.isOnline()) return false;
        PlayerInventory inv = p.getInventory();
        if (hasStar(inv)) return false;
        ItemStack star = create();
        ItemStack slot = inv.getItem(HOTBAR_SLOT);
        if (slot == null || slot.getType() == Material.AIR) {
            inv.setItem(HOTBAR_SLOT, star);
            return true;
        }
        // find empty hotbar 2..8 (keep 0–1 for blade/pots)
        for (int i = 2; i <= 8; i++) {
            ItemStack cur = inv.getItem(i);
            if (cur == null || cur.getType() == Material.AIR) {
                inv.setItem(i, star);
                return true;
            }
        }
        inv.addItem(star);
        return true;
    }

    public static boolean isVanillaWorld(String name) {
        return name != null && VANILLA.contains(name);
    }

    public static Location hubLocation() {
        World w = Bukkit.getWorld(HUB);
        if (w == null) return null;
        return new Location(w, HUB_X, HUB_Y, HUB_Z, HUB_YAW, 0f);
    }

    /** Teleport if currently in vanilla overworld/nether/end. */
    public static boolean maybeRedirectToHub(Player p) {
        if (p == null || !p.isOnline()) return false;
        if (!isVanillaWorld(p.getWorld().getName())) return false;
        Location dest = hubLocation();
        if (dest == null) return false;
        p.teleport(dest);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        // Delay: AuthMe/Bungee + Multiverse settle first.
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) return;
                boolean moved = maybeRedirectToHub(p);
                boolean gave = ensure(p);
                if (moved) {
                    p.sendMessage(P + "已送到枢纽广场 · 北边是引路人·灰烛。");
                }
                if (gave) {
                    p.sendMessage(P + "热键栏拿到 " + DISPLAY + ChatColor.GRAY + " · 右键打开枢纽（勿敲指令）。");
                } else if (!moved) {
                    p.sendMessage(P + "右键热键栏 " + DISPLAY + ChatColor.GRAY + " 打开枢纽。");
                }
            }
        }, 25L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (p.isOnline()) ensure(p);
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (!isMenuStar(e.getItemDrop().getItemStack())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "余烬星绑定热键栏，不能丢弃 · 右键打开枢纽。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        List<ItemStack> drops = e.getDrops();
        if (drops == null || drops.isEmpty()) return;
        Iterator<ItemStack> it = drops.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            if (isMenuStar(it.next())) {
                it.remove();
                removed = true;
            }
        }
        if (removed) {
            // keepInventory may already keep it; ensure on respawn covers the rest
        }
    }
}
