package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * B-flex-2: one equipable light-skill slot (parallel to covenant SkillService.cast).
 * Pilot: flex_ember_step — horizontal look-dir dash ~5 blocks, no wall clip, no damage, CD 14s, zero stamina.
 * B-flex-4: sneak+Q (PlayerDropItemEvent) world hotkey proxy → cast when flex equipped.
 */
public final class FlexSkillService implements Listener {

    private static final String PREFIX = ChatColor.GREEN + "[轻技] " + ChatColor.RESET;
    public static final String PILOT_ID = "flex_ember_step";

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    /** B-flex-4: world hotkey proxy (sneak+drop). skills.yml flex.hotkey; off/false disables. */
    private boolean hotkeyEnabled = true;
    private final Map<String, FlexDef> flexSkills = new LinkedHashMap<String, FlexDef>();
    /** UUID -> skillId -> expireMillis */
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<UUID, Map<String, Long>>();

    public static final class FlexDef {
        public final String id;
        public final String type;
        public final String display;
        public final int cooldownSeconds;
        public final double distance;
        public final String particles;
        public final String sound;

        FlexDef(String id, String type, String display, int cooldownSeconds,
                double distance, String particles, String sound) {
            this.id = id;
            this.type = type == null ? "" : type.toLowerCase();
            this.display = display;
            this.cooldownSeconds = cooldownSeconds;
            this.distance = distance;
            this.particles = particles;
            this.sound = sound;
        }
    }

    public FlexSkillService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "skills.yml");
        if (!file.exists()) {
            plugin.saveResource("skills.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("skills.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        loadFrom(cfg);
    }

    private void loadFrom(FileConfiguration cfg) {
        flexSkills.clear();
        enabled = cfg.getBoolean("enabled", true);
        String hotkey = cfg.getString("flex.hotkey", "sneak_drop");
        hotkeyEnabled = hotkey != null
                && !hotkey.isEmpty()
                && !"off".equalsIgnoreCase(hotkey)
                && !"false".equalsIgnoreCase(hotkey)
                && !"none".equalsIgnoreCase(hotkey);
        ConfigurationSection root = cfg.getConfigurationSection("skills");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            String type = sec.getString("type", "");
            boolean isFlex = "step".equalsIgnoreCase(type)
                    || "dash".equalsIgnoreCase(type)
                    || "flex".equalsIgnoreCase(type)
                    || (id != null && id.startsWith("flex_"));
            if (!isFlex) continue;
            // Skip covenant-bound skills even if mistagged
            String covenant = sec.getString("covenant", "");
            if (covenant != null && !covenant.isEmpty()) continue;
            FlexDef def = new FlexDef(
                    id,
                    type == null || type.isEmpty() ? "step" : type,
                    color(sec.getString("display", id)),
                    Math.max(0, sec.getInt("cooldown_seconds", 14)),
                    Math.max(0.5, sec.getDouble("distance", 5.0)),
                    sec.getString("particles", "CLOUD"),
                    sec.getString("sound", "ENTITY_ENDERDRAGON_FLAP"));
            flexSkills.put(id, def);
        }
        // Ensure pilot always present if YAML missing the block
        if (!flexSkills.containsKey(PILOT_ID)) {
            flexSkills.put(PILOT_ID, new FlexDef(
                    PILOT_ID, "step", color("§a余烬踏步"), 14, 5.0, "CLOUD", "ENTITY_ENDERDRAGON_FLAP"));
        }
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }

    public boolean isEnabled() { return enabled; }

    public FlexDef getFlex(String id) {
        return id == null ? null : flexSkills.get(id);
    }

    public boolean isEquippable(String id) {
        return id != null && flexSkills.containsKey(id);
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player player = (Player) sender;
        if (!enabled) {
            player.sendMessage(PREFIX + ChatColor.RED + "功能未启用");
            return;
        }
        String act = args.length >= 2 ? args[1].toLowerCase() : "cast";
        if ("equip".equals(act) || "装配".equals(act)) {
            cmdEquip(player, args.length >= 3 ? args[2] : PILOT_ID);
            return;
        }
        if ("unequip".equals(act) || "卸下".equals(act) || "clear".equals(act)) {
            cmdUnequip(player);
            return;
        }
        if ("info".equals(act) || "status".equals(act)) {
            cmdInfo(player);
            return;
        }
        if ("cast".equals(act) || "释放".equals(act) || "use".equals(act)) {
            cast(player);
            return;
        }
        // bare /corerpg flex → cast
        cast(player);
    }

    public void cmdEquip(Player player, String skillId) {
        if (skillId == null || skillId.isEmpty()) skillId = PILOT_ID;
        skillId = skillId.toLowerCase();
        if (!isEquippable(skillId)) {
            player.sendMessage(PREFIX + ChatColor.RED + "未知轻技");
            return;
        }
        FlexDef def = flexSkills.get(skillId);
        PlayerData data = dataStore.get(player.getUniqueId());
        data.setFlexSkillId(skillId);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(PREFIX + ChatColor.GREEN + "已装配 " + def.display
                + ChatColor.GRAY + " · CD " + def.cooldownSeconds + "s · 零体力");
    }

    public void cmdUnequip(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasFlexSkill()) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "轻技槽为空");
            return;
        }
        data.setFlexSkillId("none");
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(PREFIX + ChatColor.GREEN + "已卸下轻技");
    }

    public void cmdInfo(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasFlexSkill()) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "未装配轻技 · 可在枢纽轻技页装配余烬踏步");
            return;
        }
        FlexDef def = flexSkills.get(data.getFlexSkillId());
        if (def == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "装配的轻技已失效，请重新装配");
            return;
        }
        long remainMs = remainingCooldownMs(player.getUniqueId(), def.id);
        String cdRemain = remainMs > 0
                ? ChatColor.RED + "冷却中 " + String.format("%.0f", Math.ceil(remainMs / 1000.0)) + "s"
                : ChatColor.GREEN + "就绪";
        player.sendMessage(PREFIX + def.display + ChatColor.GRAY + " · " + cdRemain);
        player.sendMessage(ChatColor.GRAY + "  冷却 " + def.cooldownSeconds + "s · 位移约 "
                + (int) Math.round(def.distance) + " 格 · 零体力 · 无伤害");
    }

    public void cast(Player player) {
        if (!enabled) {
            player.sendMessage(PREFIX + ChatColor.RED + "功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasFlexSkill()) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "尚未装配轻技");
            return;
        }
        String skillId = data.getFlexSkillId();
        FlexDef def = flexSkills.get(skillId);
        if (def == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "装配的轻技已失效，请重新装配");
            return;
        }
        long remainMs = remainingCooldownMs(player.getUniqueId(), def.id);
        if (remainMs > 0) {
            int secs = (int) Math.ceil(remainMs / 1000.0);
            player.sendMessage(PREFIX + ChatColor.RED + "冷却中，剩余 " + secs + "s");
            return;
        }
        boolean ok;
        if ("step".equals(def.type) || "dash".equals(def.type) || PILOT_ID.equals(def.id)) {
            ok = castStep(player, def);
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "未知轻技类型");
            return;
        }
        if (!ok) return;
        startCooldown(player.getUniqueId(), def.id, def.cooldownSeconds);
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 " + def.display);
    }

    /**
     * Horizontal look-dir dash up to {@code def.distance} blocks.
     * Does not clip through solid blocks; on failure stays put + short tip.
     * No damage. No stamina. Optional ≤5 tick invuln omitted (not needed for thin pilot).
     */
    private boolean castStep(Player player, FlexDef def) {
        Location from = player.getLocation();
        if (from.getWorld() == null) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "无法踏步");
            return false;
        }
        Vector look = from.getDirection().clone();
        look.setY(0);
        if (look.lengthSquared() < 1.0e-6) {
            // Looking straight up/down — use yaw
            double yaw = Math.toRadians(from.getYaw());
            look = new Vector(-Math.sin(yaw), 0, Math.cos(yaw));
        }
        look.normalize();

        Location best = null;
        double step = 0.25;
        double max = def.distance;
        for (double d = step; d <= max + 1.0e-6; d += step) {
            Location cand = from.clone().add(look.clone().multiply(d));
            cand.setYaw(from.getYaw());
            cand.setPitch(from.getPitch());
            if (!isPassableFeet(cand)) break;
            // Prefer standing on solid ground when possible (drop ≤1)
            Location grounded = snapGround(cand);
            if (grounded != null) {
                best = grounded;
            } else if (isPassableFeet(cand) && isPassableHead(cand)) {
                best = cand;
            } else {
                break;
            }
        }

        if (best == null || best.distanceSquared(from) < 0.36) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "前方受阻，无法踏步");
            return false;
        }

        spawnParticles(from.clone().add(0, 0.2, 0), def.particles, 10);
        player.teleport(best);
        spawnParticles(best.clone().add(0, 0.2, 0), def.particles, 14);
        playSound(best, def.sound);
        return true;
    }

    private static boolean isPassableFeet(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        Block feet = loc.getBlock();
        Block head = loc.clone().add(0, 1, 0).getBlock();
        return !isSolid(feet) && !isSolid(head);
    }

    private static boolean isPassableHead(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        return !isSolid(loc.clone().add(0, 1, 0).getBlock());
    }

    /** Drop at most 1 block to stand on solid; null if no safe footing nearby. */
    private static Location snapGround(Location cand) {
        if (cand == null || cand.getWorld() == null) return null;
        for (int drop = 0; drop <= 1; drop++) {
            Location tryLoc = cand.clone().add(0, -drop, 0);
            Block below = tryLoc.clone().add(0, -0.05, 0).getBlock();
            if (!isPassableFeet(tryLoc)) continue;
            if (isSolid(below) || below.getType() == Material.WATER || below.getType() == Material.STATIONARY_WATER) {
                tryLoc.setX(Math.floor(tryLoc.getX()) + 0.5);
                tryLoc.setZ(Math.floor(tryLoc.getZ()) + 0.5);
                return tryLoc;
            }
        }
        // Allow mid-air short hop if feet+head clear (e.g. jumping over gap start)
        if (isPassableFeet(cand)) {
            Location mid = cand.clone();
            mid.setX(Math.floor(mid.getX()) + 0.5);
            mid.setZ(Math.floor(mid.getZ()) + 0.5);
            return mid;
        }
        return null;
    }

    private static boolean isSolid(Block b) {
        if (b == null) return true;
        Material m = b.getType();
        if (m == null || m == Material.AIR) return false;
        // 1.12: isSolid covers most; exclude non-colliding plants etc. via isSolid
        return m.isSolid();
    }

    private void startCooldown(UUID uuid, String skillId, int seconds) {
        if (seconds <= 0) return;
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) {
            map = new HashMap<String, Long>();
            cooldowns.put(uuid, map);
        }
        map.put(skillId, Long.valueOf(System.currentTimeMillis() + seconds * 1000L));
    }

    private long remainingCooldownMs(UUID uuid, String skillId) {
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) return 0L;
        Long exp = map.get(skillId);
        if (exp == null) return 0L;
        long left = exp.longValue() - System.currentTimeMillis();
        if (left <= 0) {
            map.remove(skillId);
            return 0L;
        }
        return left;
    }

    private void spawnParticles(Location loc, String name, int count) {
        if (loc == null || loc.getWorld() == null || name == null || name.isEmpty()) return;
        try {
            Particle p = Particle.valueOf(name.toUpperCase());
            loc.getWorld().spawnParticle(p, loc, count, 0.25, 0.15, 0.25, 0.01);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void playSound(Location loc, String name) {
        if (loc == null || loc.getWorld() == null || name == null || name.isEmpty()) return;
        try {
            Sound s = Sound.valueOf(name.toUpperCase());
            loc.getWorld().playSound(loc, s, 0.7f, 1.35f);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * B-flex-4: sneak + Q (drop) → cancel drop + cast when flex equipped and not in a GUI.
     * Bare Q / unequipped / hotkey off: do not cancel (vanilla drop).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDropHotkey(PlayerDropItemEvent event) {
        if (!enabled || !hotkeyEnabled) return;
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;
        if (!player.isSneaking()) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        if (data == null || !data.hasFlexSkill()) return;
        // Skip when a real GUI/menu is open (crafting/creative view = world inventory)
        InventoryType top = player.getOpenInventory().getType();
        if (top != InventoryType.CRAFTING && top != InventoryType.CREATIVE) return;
        event.setCancelled(true);
        cast(player);
    }

    public void onQuit(UUID uuid) {
        if (uuid == null) return;
        cooldowns.remove(uuid);
    }
}
