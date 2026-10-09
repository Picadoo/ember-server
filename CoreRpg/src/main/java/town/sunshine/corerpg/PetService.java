package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Cosmetic familiar (使魔) — ArmorStand follower. Not sold as power pets.
 * docs/design/DESIGN-ember-pet-bestiary.md · NI ember-pets.yml
 */
public final class PetService implements Listener {

    public static final String PREFIX = ChatColor.GREEN + "[使魔] " + ChatColor.RESET;

    public static final class PetDef {
        public final String id;
        public final String display;
        public final int powerBonus;
        PetDef(String id, String display, int powerBonus) {
            this.id = id;
            this.display = display == null ? id : display;
            this.powerBonus = Math.max(0, powerBonus);
        }
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge niBridge;

    private boolean enabled = true;
    private int maxActive = 1;
    private int followTicks = 10;
    private double followDistance = 2.5;
    private boolean feedEnabled = true;
    private String feedItem = "mat_ember_soul_dust";
    private int feedMaxLevel = 10;
    private int feedCostBase = 1;
    private int feedCostPerLevel = 1;
    private int feedPowerPerLevel = 1;
    private final Map<String, PetDef> pets = new LinkedHashMap<String, PetDef>();

    /** player UUID -> active pet entity UUID */
    private final Map<UUID, UUID> activeEntities = new LinkedHashMap<UUID, UUID>();
    private BukkitTask followTask;

    public PetService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge niBridge) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.niBridge = niBridge;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "pet.yml");
        if (!file.exists()) {
            plugin.saveResource("pet.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("pet.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        maxActive = Math.max(1, cfg.getInt("max_active", 1));
        followTicks = Math.max(5, cfg.getInt("follow_ticks", 10));
        followDistance = Math.max(1.0, cfg.getDouble("follow_distance", 2.5));
        feedEnabled = cfg.getBoolean("feed.enabled", true);
        feedItem = cfg.getString("feed.item", "mat_ember_soul_dust");
        if (feedItem == null || feedItem.isEmpty()) feedItem = "mat_ember_soul_dust";
        feedMaxLevel = Math.max(1, cfg.getInt("feed.max_level", 10));
        feedCostBase = Math.max(0, cfg.getInt("feed.cost_base", 1));
        feedCostPerLevel = Math.max(0, cfg.getInt("feed.cost_per_level", 1));
        feedPowerPerLevel = Math.max(0, cfg.getInt("feed.power_per_level", 1));
        pets.clear();
        ConfigurationSection sec = cfg.getConfigurationSection("pets");
        if (sec != null) {
            for (String id : sec.getKeys(false)) {
                ConfigurationSection p = sec.getConfigurationSection(id);
                if (p == null) continue;
                String display = color(p.getString("display", id));
                int bonus = p.getInt("power_bonus", 0);
                pets.put(id, new PetDef(id, display, bonus));
            }
        }
        if (pets.isEmpty()) {
            pets.put("pet_ember_ashling", new PetDef("pet_ember_ashling", color("§a余烬灰灵"), 5));
            pets.put("pet_ember_cinder", new PetDef("pet_ember_cinder", color("§6余烬烬火"), 5));
        }
        restartFollowTask();
    }

    public boolean isEnabled() { return enabled; }

    public PetDef getDef(String id) {
        if (id == null) return null;
        return pets.get(id);
    }

    public Map<String, PetDef> getPets() {
        return Collections.unmodifiableMap(pets);
    }

    /** Display-only ladder bonus when player has an unlocked+active pet id. */
    public int getActivePowerBonus(PlayerData data) {
        return getPowerBonus(data);
    }

    /**
     * Display ladder bonus: base power_bonus + (level-1)*power_per_level.
     * Cosmetic only — tiny display ladder points.
     */
    public int getPowerBonus(PlayerData data) {
        if (!enabled || data == null) return 0;
        String active = data.getActivePet();
        if (active == null || active.isEmpty()) return 0;
        if (!data.isPetUnlocked(active)) return 0;
        PetDef def = pets.get(active);
        if (def == null) return 0;
        int level = data.getPetLevel(active);
        int levelBonus = Math.max(0, level - 1) * feedPowerPerLevel;
        return def.powerBonus + levelBonus;
    }

    /** Dust cost to go from {@code level} to level+1. */
    public int feedCostFor(int level) {
        int L = Math.max(1, level);
        return feedCostBase + (L - 1) * feedCostPerLevel;
    }

    /** Configured feed.max_level (read-only for PAPI). */
    public int getFeedMaxLevel() {
        return feedMaxLevel;
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        restartFollowTask();
    }

    public void shutdown() {
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }
        List<UUID> owners = new ArrayList<UUID>(activeEntities.keySet());
        for (UUID id : owners) {
            removeEntity(id, false);
        }
        activeEntities.clear();
    }

    private void restartFollowTask() {
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }
        if (!enabled) return;
        followTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override public void run() { tickFollow(); }
        }, followTicks, followTicks);
    }

    private void tickFollow() {
        if (activeEntities.isEmpty()) return;
        List<UUID> stale = new ArrayList<UUID>();
        for (Map.Entry<UUID, UUID> e : activeEntities.entrySet()) {
            UUID ownerId = e.getKey();
            UUID entId = e.getValue();
            Player p = Bukkit.getPlayer(ownerId);
            if (p == null || !p.isOnline()) {
                stale.add(ownerId);
                continue;
            }
            Entity ent = findEntity(p.getWorld(), entId);
            if (ent == null || ent.isDead()) {
                // try other worlds briefly
                ent = findEntityAnywhere(entId);
            }
            if (ent == null || ent.isDead()) {
                stale.add(ownerId);
                continue;
            }
            if (!ent.getWorld().equals(p.getWorld())) {
                ent.remove();
                stale.add(ownerId);
                continue;
            }
            Location target = followLoc(p);
            if (ent.getLocation().distanceSquared(target) > 0.04) {
                ent.teleport(target);
            }
        }
        for (UUID id : stale) {
            removeEntity(id, false);
        }
    }

    private Location followLoc(Player p) {
        Location base = p.getLocation().clone();
        Vector dir = base.getDirection().clone().setY(0);
        if (dir.lengthSquared() < 0.0001) {
            dir = new Vector(0, 0, 1);
        } else {
            dir.normalize();
        }
        // stand slightly behind and to the side
        Vector back = dir.clone().multiply(-followDistance);
        Vector side = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.6);
        Location dest = base.add(back).add(side);
        dest.setY(base.getY());
        dest.setYaw(p.getLocation().getYaw());
        dest.setPitch(0);
        return dest;
    }

    private Entity findEntity(World world, UUID entId) {
        if (world == null || entId == null) return null;
        for (Entity ent : world.getEntities()) {
            if (entId.equals(ent.getUniqueId())) return ent;
        }
        return null;
    }

    private Entity findEntityAnywhere(UUID entId) {
        if (entId == null) return null;
        for (World w : Bukkit.getWorlds()) {
            Entity e = findEntity(w, entId);
            if (e != null) return e;
        }
        return null;
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "使魔系统未启用。");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.pet") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.pet");
            return;
        }
        if (args.length < 2) {
            cmdList(p);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if ("list".equals(sub) || "ls".equals(sub)) {
            cmdList(p);
            return;
        }
        if ("summon".equals(sub) || "spawn".equals(sub)) {
            cmdSummon(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("dismiss".equals(sub) || "despawn".equals(sub) || "remove".equals(sub)) {
            cmdDismiss(p);
            return;
        }
        if ("unlock".equals(sub)) {
            cmdUnlock(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("feed".equals(sub)) {
            int amount = 1;
            if (args.length >= 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg pet feed [次数]");
                    return;
                }
            }
            cmdFeed(p, amount);
            return;
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW
                + "/corerpg pet [list|summon [id]|dismiss|unlock <id>|feed]");
    }

    private void cmdList(Player p) {
        PlayerData data = dataStore.get(p.getUniqueId());
        List<String> unlocked = data.getPetsUnlocked();
        String active = data.getActivePet();
        boolean spawned = activeEntities.containsKey(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.YELLOW + "已解锁 §f" + unlocked.size()
                + ChatColor.GRAY + " · 出战上限 " + maxActive);
        if (unlocked.isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "  （空）· 持蛋 /corerpg pet unlock <id> 或 summon");
        } else {
            for (String id : unlocked) {
                PetDef def = pets.get(id);
                String name = def != null ? def.display : id;
                boolean isActive = id.equals(active);
                String mark = isActive
                        ? (spawned ? ChatColor.GREEN + "[出战]" : ChatColor.YELLOW + "[选定]")
                        : ChatColor.DARK_GRAY + "[收藏]";
                int level = data.getPetLevel(id);
                int base = def != null ? def.powerBonus : 0;
                int bonus = base + Math.max(0, level - 1) * feedPowerPerLevel;
                p.sendMessage(ChatColor.GRAY + "  " + mark + " " + name
                        + ChatColor.AQUA + " Lv." + level
                        + ChatColor.DARK_GRAY + " (" + id + ")"
                        + (bonus > 0 ? ChatColor.GRAY + " 展示+" + bonus : ""));
            }
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg pet summon [id] · dismiss · unlock <id> · feed");
        p.sendMessage(ChatColor.DARK_GRAY + "  偏外观 · 不卖满级战力宠 · 属性≤战力展示 5%");
    }

    private void cmdUnlock(Player p, String id) {
        if (id == null || id.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg pet unlock <id>");
            p.sendMessage(ChatColor.GRAY + "  可用: " + joinIds() + " · 别名 ashling/cinder");
            return;
        }
        id = resolvePetId(id);
        PetDef def = pets.get(id);
        if (def == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "未知使魔: " + id);
            p.sendMessage(ChatColor.GRAY + "  可用: " + joinIds());
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        if (data.isPetUnlocked(id)) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "已解锁 " + def.display
                    + ChatColor.GRAY + " · 直接 /corerpg pet summon " + id);
            return;
        }
        if (niBridge == null || niBridge.countInInventory(p, id) < 1) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要持有使魔蛋: " + def.display
                    + ChatColor.DARK_GRAY + " (" + id + ")");
            return;
        }
        if (!niBridge.consumeExact(p, id, 1)) {
            p.sendMessage(PREFIX + ChatColor.RED + "消耗使魔蛋失败。");
            return;
        }
        data.unlockPet(id);
        data.setActivePet(id);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "解锁成功 " + def.display
                + ChatColor.GRAY + "（消耗蛋×1 · 可免费 summon）");
        if (plugin.getLadderService() != null) {
            plugin.getLadderService().recomputePower(p);
        }
    }

    private void cmdSummon(Player p, String idArg) {
        PlayerData data = dataStore.get(p.getUniqueId());
        String id = idArg;
        if (id != null && !id.isEmpty()) id = resolvePetId(id);
        if (id == null || id.isEmpty()) {
            id = data.getActivePet();
            if (id == null || id.isEmpty()) {
                List<String> unlocked = data.getPetsUnlocked();
                if (!unlocked.isEmpty()) id = unlocked.get(unlocked.size() - 1);
            }
        }
        if (id == null || id.isEmpty()) {
            // try any egg in inventory matching known pets
            id = findHeldPetEgg(p);
        }
        if (id == null || id.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "没有可出战的使魔。先 unlock 或持蛋 summon。");
            p.sendMessage(ChatColor.GRAY + "  可用: " + joinIds());
            return;
        }
        PetDef def = pets.get(id);
        if (def == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "未知使魔: " + id);
            return;
        }

        boolean unlocked = data.isPetUnlocked(id);
        if (!unlocked) {
            // first summon while holding egg → unlock consuming 1
            if (niBridge == null || niBridge.countInInventory(p, id) < 1) {
                p.sendMessage(PREFIX + ChatColor.RED + "未解锁，且背包无对应使魔蛋: " + def.display);
                return;
            }
            if (!niBridge.consumeExact(p, id, 1)) {
                p.sendMessage(PREFIX + ChatColor.RED + "消耗使魔蛋失败。");
                return;
            }
            data.unlockPet(id);
            p.sendMessage(PREFIX + ChatColor.GREEN + "首次出战解锁 " + def.display
                    + ChatColor.GRAY + "（消耗蛋×1）");
        }

        data.setActivePet(id);
        dataStore.flushMutation(p.getUniqueId());

        // one active: despawn previous
        removeEntity(p.getUniqueId(), false);

        ArmorStand stand = spawnStand(p, def);
        if (stand == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "召唤失败。");
            return;
        }
        activeEntities.put(p.getUniqueId(), stand.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "出战 " + def.display
                + ChatColor.GRAY + " · /corerpg pet dismiss 收回");
        if (plugin.getLadderService() != null) {
            plugin.getLadderService().recomputePower(p);
        }
    }

    private void cmdDismiss(Player p) {
        if (!activeEntities.containsKey(p.getUniqueId())) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "当前没有出战使魔。");
            return;
        }
        removeEntity(p.getUniqueId(), true);
        p.sendMessage(PREFIX + ChatColor.GRAY + "使魔已收回。");
        if (plugin.getLadderService() != null) {
            plugin.getLadderService().recomputePower(p);
        }
    }


    /**
     * Feed active (or last unlocked) pet with 魂尘.
     * {@code times} = number of level-ups to attempt (default 1).
     */
    private void cmdFeed(Player p, int times) {
        if (!feedEnabled) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "魂尘喂养未启用。");
            return;
        }
        if (times <= 0) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg pet feed [次数] · 次数≥1");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        List<String> unlocked = data.getPetsUnlocked();
        if (unlocked == null || unlocked.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "没有已解锁的使魔。");
            return;
        }
        String id = data.getActivePet();
        if (id == null || id.isEmpty() || !data.isPetUnlocked(id)) {
            id = unlocked.get(unlocked.size() - 1);
        }
        PetDef def = pets.get(id);
        String display = def != null ? def.display : id;

        int level = data.getPetLevel(id);
        if (level >= feedMaxLevel) {
            p.sendMessage(PREFIX + ChatColor.RED + display + ChatColor.RED
                    + " 已达最高等级 Lv." + feedMaxLevel);
            return;
        }

        int from = level;
        int consumed = 0;
        int gained = 0;
        for (int i = 0; i < times && level < feedMaxLevel; i++) {
            int cost = feedCostFor(level);
            if (cost <= 0) cost = 1;
            // prefer main hand dust, else whole inventory via NiBridge
            if (niBridge == null || niBridge.countInInventory(p, feedItem) < cost) {
                if (gained == 0) {
                    p.sendMessage(PREFIX + ChatColor.RED + "没有足够的魂尘（需要 "
                            + cost + "×" + feedItem + "）。");
                }
                break;
            }
            if (!consumeFeedPreferHand(p, cost)) {
                if (gained == 0) {
                    p.sendMessage(PREFIX + ChatColor.RED + "消耗魂尘失败。");
                }
                break;
            }
            consumed += cost;
            level++;
            gained++;
            data.setPetLevel(id, level);
        }

        if (gained <= 0) {
            if (level >= feedMaxLevel) {
                p.sendMessage(PREFIX + ChatColor.RED + display + ChatColor.RED
                        + " 已达最高等级 Lv." + feedMaxLevel);
            }
            // no-dust / consume-fail already messaged
            return;
        }

        if (data.getActivePet() == null || data.getActivePet().isEmpty()) {
            data.setActivePet(id);
        }
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "消耗魂尘×" + consumed
                + ChatColor.GRAY + " · " + display
                + ChatColor.WHITE + " Lv." + from + "→" + level);
        if (plugin.getLadderService() != null) {
            plugin.getLadderService().recomputePower(p);
        }
    }

    /** Consume feed item; prefer main-hand stack first, then rest of inventory. */
    private boolean consumeFeedPreferHand(Player p, int amount) {
        if (niBridge == null || amount <= 0) return amount <= 0;
        if (niBridge.countInInventory(p, feedItem) < amount) return false;
        int need = amount;
        try {
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand != null && hand.getType() != org.bukkit.Material.AIR
                    && niBridge.matchesNiId(hand, feedItem)) {
                int take = Math.min(need, hand.getAmount());
                int left = hand.getAmount() - take;
                if (left <= 0) {
                    p.getInventory().setItemInMainHand(null);
                } else {
                    hand.setAmount(left);
                    p.getInventory().setItemInMainHand(hand);
                }
                need -= take;
            }
        } catch (Throwable ignored) {
            // 1.12 getItemInHand fallback below
            try {
                ItemStack hand = p.getItemInHand();
                if (hand != null && hand.getType() != org.bukkit.Material.AIR
                        && niBridge.matchesNiId(hand, feedItem)) {
                    int take = Math.min(need, hand.getAmount());
                    int left = hand.getAmount() - take;
                    if (left <= 0) {
                        p.setItemInHand(null);
                    } else {
                        hand.setAmount(left);
                        p.setItemInHand(hand);
                    }
                    need -= take;
                }
            } catch (Throwable ignored2) {}
        }
        if (need > 0) {
            int got = niBridge.consume(p, feedItem, need);
            if (got < need) return false;
        } else {
            p.updateInventory();
        }
        return true;
    }


    /** Resolve short aliases: ashling→pet_ember_ashling, cinder→pet_ember_cinder. */
    private String resolvePetId(String raw) {
        if (raw == null || raw.isEmpty()) return raw;
        if (pets.containsKey(raw)) return raw;
        String lower = raw.toLowerCase(Locale.ROOT);
        if ("ashling".equals(lower) || "灰灵".equals(raw)) return "pet_ember_ashling";
        if ("cinder".equals(lower) || "烬火".equals(raw)) return "pet_ember_cinder";
        String prefixed = "pet_ember_" + lower;
        if (pets.containsKey(prefixed)) return prefixed;
        return raw;
    }

    private String findHeldPetEgg(Player p) {
        if (niBridge == null) return null;
        for (String id : pets.keySet()) {
            if (niBridge.countInInventory(p, id) > 0) return id;
        }
        return null;
    }

    private ArmorStand spawnStand(Player p, PetDef def) {
        try {
            Location loc = followLoc(p);
            ArmorStand stand = p.getWorld().spawn(loc, ArmorStand.class);
            stand.setSmall(true);
            stand.setArms(false);
            stand.setBasePlate(false);
            stand.setGravity(false);
            stand.setVisible(true);
            stand.setCustomName(def.display);
            stand.setCustomNameVisible(true);
            stand.setInvulnerable(true);
            stand.setSilent(true);
            stand.setAI(false);
            stand.setCollidable(false);
            try {
                stand.setMarker(true);
            } catch (Throwable ignored) {
                // older API without marker — still fine as small stand
            }
            try {
                stand.setCanMove(false);
            } catch (Throwable ignored) {}
            stand.setRemoveWhenFarAway(false);
            return stand;
        } catch (Throwable t) {
            plugin.getLogger().warning("Pet spawn failed: " + t.getMessage());
            return null;
        }
    }

    /** Remove entity for player; keep activePet id in data. */
    public void removeEntity(UUID playerId, boolean message) {
        UUID entId = activeEntities.remove(playerId);
        if (entId == null) return;
        Entity ent = findEntityAnywhere(entId);
        if (ent != null && !ent.isDead()) {
            ent.remove();
        }
    }

    public void onPlayerQuit(Player player) {
        if (player == null) return;
        removeEntity(player.getUniqueId(), false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        removeEntity(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (isOurPet(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (isOurPet(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    private boolean isOurPet(Entity entity) {
        if (entity == null) return false;
        UUID id = entity.getUniqueId();
        return activeEntities.containsValue(id);
    }

    private String joinIds() {
        StringBuilder sb = new StringBuilder();
        for (String id : pets.keySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(id);
        }
        return sb.toString();
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }
}
