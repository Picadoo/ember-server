package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Hub workshop NPCs (烬砧 / 余晶 / 灰粮): Adyeshach persistent villager + Bukkit invisible
 * hitbox (same grade as 灰烛 / QuestService). Does NOT touch ember_guide talk/coords.
 */
public class HubNpcService implements Listener {

    /** Metadata key; value = npc id (ember_smith / …). Distinct from QuestService META_QUEST_NPC. */
    static final String META_HUB_NPC = "corerpg_hub_npc";

    private static final String[] ADY_INTERACT_EVENTS = {
            "ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent",
            "ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent"
    };

    private final CoreRpgPlugin plugin;
    private boolean enabled = true;
    private boolean adyHooked = false;
    private final List<HubNpc> npcs = new ArrayList<HubNpc>();
    private final Map<UUID, Long> interactCooldown = new HashMap<UUID, Long>();
    private static final long COOLDOWN_MS = 600L;

    static final class HubNpc {
        String id;
        String name;
        String world = "ember_hub";
        double x, y, z;
        float yaw;
        String action; // forge | life | enchant_guide
        String tell;
        String menu; // optional TrMenu id
        boolean openMenu = true;
    }

    public HubNpcService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        npcs.clear();
        File file = new File(plugin.getDataFolder(), "hub_npcs.yml");
        if (!file.exists()) plugin.saveResource("hub_npcs.yml", false);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enabled = cfg.getBoolean("enabled", true);
        ConfigurationSection list = cfg.getConfigurationSection("npcs");
        if (list != null) {
            for (String key : list.getKeys(false)) {
                ConfigurationSection s = list.getConfigurationSection(key);
                if (s == null || !s.getBoolean("enabled", true)) continue;
                HubNpc n = new HubNpc();
                n.id = s.getString("id", key);
                n.name = color(s.getString("name", n.id));
                n.world = s.getString("world", "ember_hub");
                n.x = s.getDouble("x");
                n.y = s.getDouble("y");
                n.z = s.getDouble("z");
                n.yaw = (float) s.getDouble("yaw", 0.0);
                n.action = s.getString("action", "forge");
                n.tell = color(s.getString("tell", ""));
                n.menu = s.getString("menu", "");
                n.openMenu = s.getBoolean("open_menu", true);
                // 总控纠偏：余晶必须开引导薄菜单；烬砧只开 forge（方案 A，无潜行分流）
                if ("enchant_guide".equals(n.action)) {
                    n.openMenu = true;
                    if (n.menu == null || n.menu.isEmpty()) n.menu = "ember_enchant_guide";
                }
                if ("forge".equals(n.action)) {
                    n.menu = "ember_forge"; // lock scheme A
                }
                npcs.add(n);
            }
        }
        if (npcs.isEmpty()) {
            // hard-coded fallback if yaml empty
            npcs.add(def("ember_smith", "§6锻炉师 · 烬砧", -11.5, 58.0, 105.5, 90f, "forge",
                    "§6烬砧：§f刃与护符，放到菜单里锻。材料不够就去挂机庭。", "ember_forge", true));
            npcs.add(def("ember_enchanter", "§d咒火师 · 余晶", -21.5, 58.0, 104.5, 270f, "enchant_guide",
                    "§d余晶：§f把附魔晶放在热键栏，对着旁边的附魔台用。\n§7刃与护符分表；晶不够去日常或补给看看。",
                    "ember_enchant_guide", true));
            npcs.add(def("ember_quartermaster", "§a补给官 · 灰粮", -18.5, 58.0, 114.5, 0f, "life",
                    "§a灰粮：§f面包、竿子、代烤，点菜单就行。", "ember_life", true));
        }
        plugin.getLogger().info("HubNpc: loaded " + npcs.size() + " workshop NPCs (enabled=" + enabled + ")");
    }

    private static HubNpc def(String id, String name, double x, double y, double z, float yaw,
                                  String action, String tell, String menu, boolean open) {
        HubNpc n = new HubNpc();
        n.id = id; n.name = name; n.world = "ember_hub";
        n.x = x; n.y = y; n.z = z; n.yaw = yaw;
        n.action = action; n.tell = tell; n.menu = menu; n.openMenu = open;
        return n;
    }

    // ---------------- commands ----------------

    /** /corerpg hubnpc [ensure|reload|list] */
    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String act = args.length >= 2 ? args[1].toLowerCase() : "ensure";
        if ("reload".equals(act)) {
            reload();
            ensureAll(true);
            sender.sendMessage("[CoreRpg] hubnpc reloaded · n=" + npcs.size() + " hooked=" + adyHooked);
            return true;
        }
        if ("list".equals(act)) {
            for (HubNpc n : npcs) {
                sender.sendMessage(ChatColor.GRAY + "  " + n.id + " " + n.name + ChatColor.GRAY
                        + " @ " + n.world + " " + n.x + "," + n.y + "," + n.z
                        + " yaw=" + n.yaw + " action=" + n.action + " menu=" + n.menu);
            }
            sender.sendMessage("[CoreRpg] hubnpc list · n=" + npcs.size() + " hooked=" + adyHooked);
            return true;
        }
        // ensure (default)
        ensureAll(true);
        updateForgeSign();
        sender.sendMessage("[CoreRpg] hubnpc ensured · n=" + npcs.size() + " hooked=" + adyHooked);
        return true;
    }

    // ---------------- Adyeshach (reflection, soft) ----------------

    public void hookAdyeshach() {
        if (!enabled || adyHooked) return;
        final Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        if (ady == null || !ady.isEnabled()) return;
        final ClassLoader cl = ady.getClass().getClassLoader();
        int registered = 0;
        for (String className : ADY_INTERACT_EVENTS) {
            try {
                @SuppressWarnings("unchecked")
                final Class<? extends Event> evt = (Class<? extends Event>) Class.forName(className, true, cl);
                final Method getEntity = evt.getMethod("getEntity");
                final Method getPlayer = evt.getMethod("getPlayer");
                Bukkit.getPluginManager().registerEvent(evt, this, EventPriority.NORMAL, new EventExecutor() {
                    @Override public void execute(Listener l, Event event) {
                        if (!evt.isInstance(event)) return;
                        try {
                            Object ent = getEntity.invoke(event);
                            final HubNpc matched = matchAdyNpc(ent);
                            if (matched == null) return;
                            final Player p = (Player) getPlayer.invoke(event);
                            if (p == null) return;
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override public void run() { if (p.isOnline()) interact(p, matched); }
                            });
                        } catch (Throwable t) {
                            plugin.getLogger().log(Level.WARNING, "HubNpc Adyeshach interact (" + className + ")", t);
                        }
                    }
                }, plugin);
                registered++;
                plugin.getLogger().info("HubNpc: Adyeshach interact hook registered: " + className);
            } catch (ClassNotFoundException cnf) {
                plugin.getLogger().info("HubNpc: Adyeshach event class absent (ok): " + className);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "HubNpc: Adyeshach hook failed for " + className, t);
            }
        }
        if (registered > 0) {
            adyHooked = true;
            plugin.getLogger().info("HubNpc: Adyeshach interact hooks active=" + registered);
        }
    }

    private HubNpc matchAdyNpc(Object ent) {
        if (ent == null) return null;
        String id = null;
        try {
            id = String.valueOf(ent.getClass().getMethod("getId").invoke(ent));
        } catch (Throwable ignored) {}
        if (id != null) {
            for (HubNpc n : npcs) {
                if (n.id.equals(id)) return n;
            }
        }
        // name / distance fallback
        String plain = null;
        try {
            Object cn = ent.getClass().getMethod("getCustomName").invoke(ent);
            if (cn != null) plain = ChatColor.stripColor(String.valueOf(cn));
        } catch (Throwable ignored) {}
        Location loc = null;
        try {
            for (String m : new String[]{"getLocation", "getWorldPosition", "getExactLocation"}) {
                try {
                    Object locObj = ent.getClass().getMethod(m).invoke(ent);
                    if (locObj instanceof Location) { loc = (Location) locObj; break; }
                } catch (NoSuchMethodException ignored) {}
            }
        } catch (Throwable ignored) {}
        for (HubNpc n : npcs) {
            if (plain != null) {
                String want = ChatColor.stripColor(n.name);
                if (want != null && !want.isEmpty() && plain.contains(want.replace("§", ""))) {
                    // stripColor already applied; match by known short names
                }
                if (plain.contains("烬砧") && "ember_smith".equals(n.id)) return n;
                if (plain.contains("余晶") && "ember_enchanter".equals(n.id)) return n;
                if (plain.contains("灰粮") && "ember_quartermaster".equals(n.id)) return n;
            }
            if (loc != null) {
                World w = Bukkit.getWorld(n.world);
                if (w != null && w.equals(loc.getWorld())
                        && loc.distanceSquared(new Location(w, n.x, n.y, n.z)) <= 4.0) {
                    return n;
                }
            }
        }
        return null;
    }

    public void ensureAll(boolean force) {
        if (!enabled) return;
        for (HubNpc n : npcs) {
            ensureOne(n, force);
        }
        updateForgeSign();
    }

    private void ensureOne(HubNpc n, boolean force) {
        Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        World w = Bukkit.getWorld(n.world);
        if (ady == null || !ady.isEnabled() || w == null) {
            ensureHitbox(n);
            return;
        }
        try {
            ClassLoader cl = ady.getClass().getClassLoader();
            Class<?> adyC = Class.forName("ink.ptms.adyeshach.core.Adyeshach", true, cl);
            Object api = adyC.getMethod("api").invoke(adyC.getField("INSTANCE").get(null));
            Class<?> mtC = Class.forName("ink.ptms.adyeshach.core.entity.manager.ManagerType", true, cl);
            Object persistent = mtC.getField("PERSISTENT").get(null);
            Class<?> apiC = Class.forName("ink.ptms.adyeshach.core.AdyeshachAPI", true, cl);
            Object mgr = apiC.getMethod("getPublicEntityManager", mtC).invoke(api, persistent);
            Class<?> mgrC = Class.forName("ink.ptms.adyeshach.core.entity.manager.Manager", true, cl);
            List<?> found = (List<?>) mgrC.getMethod("getEntityById", String.class).invoke(mgr, n.id);
            Class<?> eiC = Class.forName("ink.ptms.adyeshach.core.entity.EntityInstance", true, cl);
            Class<?> geC = Class.forName("ink.ptms.adyeshach.core.entity.GenericEntity", true, cl);
            Location loc = new Location(w, n.x, n.y, n.z, n.yaw, 0f);
            Object ent;
            if (found != null && !found.isEmpty()) {
                ent = found.get(0);
                if (force) eiC.getMethod("teleport", Location.class).invoke(ent, loc);
            } else {
                Class<?> etC = Class.forName("ink.ptms.adyeshach.core.entity.EntityTypes", true, cl);
                Object villager = etC.getField("VILLAGER").get(null);
                ent = mgrC.getMethod("create", etC, Location.class).invoke(mgr, villager, loc);
                eiC.getMethod("setId", String.class).invoke(ent, n.id);
                plugin.getLogger().info("HubNpc: created Adyeshach NPC " + n.id + " at " + n.world
                        + " " + n.x + "," + n.y + "," + n.z);
            }
            geC.getMethod("setCustomName", String.class).invoke(ent, n.name);
            geC.getMethod("setCustomNameVisible", boolean.class).invoke(ent, true);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "HubNpc: ensure Adyeshach NPC " + n.id + " failed", t);
        }
        ensureHitbox(n);
    }

    private void ensureHitbox(HubNpc n) {
        World w = Bukkit.getWorld(n.world);
        if (w == null) return;
        Location loc = new Location(w, n.x, n.y, n.z, n.yaw, 0f);
        // Purge ALL hub hitboxes with this id in the world (chunk-loaded). Prevents accumulate across ensure races.
        for (Entity e : w.getEntitiesByClass(LivingEntity.class)) {
            if (!e.hasMetadata(META_HUB_NPC)) continue;
            List<MetadataValue> vals = e.getMetadata(META_HUB_NPC);
            if (vals == null || vals.isEmpty()) continue;
            if (!n.id.equals(String.valueOf(vals.get(0).value()))) continue;
            e.remove();
        }
        Villager v;
        try {
            v = (Villager) w.spawnEntity(loc, EntityType.VILLAGER);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "HubNpc: spawn hitbox villager failed " + n.id + ", ArmorStand", t);
            ensureHitboxArmorStand(n, loc);
            return;
        }
        try {
            v.setAI(false);
            v.setSilent(true);
            v.setInvulnerable(true);
            v.setCollidable(false);
            v.setRemoveWhenFarAway(false);
            v.setCanPickupItems(false);
            v.setCustomNameVisible(false);
            try { v.setProfession(Villager.Profession.NITWIT); } catch (Throwable ignored) {}
            try { v.setRecipes(Collections.<org.bukkit.inventory.MerchantRecipe>emptyList()); } catch (Throwable ignored) {}
            v.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false), true);
            v.setMetadata(META_HUB_NPC, new FixedMetadataValue(plugin, n.id));
            plugin.getLogger().info("HubNpc: Bukkit hitbox villager ensured " + n.id + " at " + n.world
                    + " " + n.x + "," + n.y + "," + n.z);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "HubNpc: configure hitbox failed " + n.id, t);
            v.remove();
            ensureHitboxArmorStand(n, loc);
        }
    }

    private void ensureHitboxArmorStand(HubNpc n, Location loc) {
        try {
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setGravity(false);
            as.setBasePlate(false);
            as.setArms(false);
            as.setMarker(false);
            as.setSmall(false);
            as.setCustomNameVisible(false);
            as.setInvulnerable(true);
            as.setCollidable(false);
            as.setRemoveWhenFarAway(false);
            as.setMetadata(META_HUB_NPC, new FixedMetadataValue(plugin, n.id));
            plugin.getLogger().info("HubNpc: Bukkit hitbox ArmorStand ensured " + n.id + " at " + loc);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "HubNpc: spawn hitbox ArmorStand failed " + n.id, t);
        }
    }

    // ---------------- interact dispatch ----------------

    private HubNpc byId(String id) {
        if (id == null) return null;
        for (HubNpc n : npcs) if (n.id.equals(id)) return n;
        return null;
    }

    private boolean isHubHitbox(Entity e) {
        return e != null && e.hasMetadata(META_HUB_NPC);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onHitboxInteract(PlayerInteractEntityEvent e) {
        if (!enabled) return;
        Entity clicked = e.getRightClicked();
        if (!isHubHitbox(clicked)) return;
        try {
            if (e.getHand() != null && e.getHand() != EquipmentSlot.HAND && e.getHand() != EquipmentSlot.OFF_HAND) {
                return;
            }
        } catch (Throwable ignored) {}
        List<MetadataValue> vals = clicked.getMetadata(META_HUB_NPC);
        if (vals == null || vals.isEmpty()) return;
        final HubNpc n = byId(String.valueOf(vals.get(0).value()));
        if (n == null) return;
        e.setCancelled(true);
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() { if (p.isOnline()) interact(p, n); }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHitboxDamage(EntityDamageEvent e) {
        if (isHubHitbox(e.getEntity())) e.setCancelled(true);
    }

    void interact(Player p, HubNpc n) {
        if (p == null || n == null) return;
        long now = System.currentTimeMillis();
        Long last = interactCooldown.get(p.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) return;
        interactCooldown.put(p.getUniqueId(), now);

        if (n.tell != null && !n.tell.isEmpty()) {
            for (String line : n.tell.split("\\n")) {
                if (line != null && !line.isEmpty()) p.sendMessage(line);
            }
        }
        // 余晶：禁止只 tell 不开菜单
        if ("enchant_guide".equals(n.action)) n.openMenu = true;
        if (!n.openMenu) return;
        String menu = n.menu;
        if (menu == null || menu.isEmpty()) {
            if ("forge".equals(n.action)) menu = "ember_forge";
            else if ("life".equals(n.action)) menu = "ember_life";
            else if ("enchant_guide".equals(n.action)) menu = "ember_enchant_guide";
        }
        if (menu == null || menu.isEmpty()) return;
        final String menuId = menu;
        final String playerName = p.getName();
        // Console open so no player-permission / command-teach path
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menuId + " " + playerName);
                } catch (Throwable t) {
                    plugin.getLogger().log(Level.WARNING, "HubNpc: open menu " + menuId + " for " + playerName, t);
                }
            }
        });
    }

    /** Downgrade forge sign to point at 烬砧 (design §4). Safe to call repeatedly. */
    public void updateForgeSign() {
        World w = Bukkit.getWorld("ember_hub");
        if (w == null) return;
        Block b = w.getBlockAt(-13, 58, 105);
        try {
            if (b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN) {
                b.setType(Material.SIGN_POST, false);
            }
            if (b.getState() instanceof Sign) {
                Sign s = (Sign) b.getState();
                s.setLine(0, "§6锻炉师·烬砧");
                s.setLine(1, "§7就在棚里");
                s.setLine(2, "§7右键他");
                s.setLine(3, "§8勿手打指令");
                s.update(true, false);
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "HubNpc: update forge sign failed", t);
        }
    }

    private static String color(String s) {
        if (s == null) return "";
        // Design YAML uses §; also accept &
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
