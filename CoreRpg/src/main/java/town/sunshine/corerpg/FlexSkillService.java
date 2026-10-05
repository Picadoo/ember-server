package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;

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
 * D214 / skill-kit S2: when Q05 first-cleared + 焚烬 2pc, the same cast becomes 火痕步 (landing ignites 1 enemy
 * at set burn rate ×1.0). D219: skill-page 前冲/后撤 direction (4 blocks back, no dmg, shared CD; 火痕·后撤
 * ignites takeoff). AFK never auto-casts flex (X7).
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

    /** D91 onboarding: the book's step (§4.2) is part of every kit, so new players start with it equipped. */
    public boolean autoEquipStarter(Player player) {
        if (!enabled || !isEquippable(PILOT_ID)) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        if (data == null || data.hasFlexSkill()) return false;
        data.setFlexSkillId(PILOT_ID);
        dataStore.flushMutation(player.getUniqueId());
        return true;
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
        boolean huohen = isHuohenActive(player);
        boolean back = town.sunshine.corerpg.p1.EmberSkillKit.stepBackward(data);
        String name = (PILOT_ID.equals(def.id) || "step".equals(def.type))
                ? town.sunshine.corerpg.p1.EmberSkillKit.stepDisplayName(huohen, back) : def.display;
        player.sendMessage(PREFIX + name + ChatColor.GRAY + " · " + cdRemain);
        double dist = back ? town.sunshine.corerpg.p1.EmberSkillKit.BACKSTEP_DISTANCE : def.distance;
        String igniteNote = huohen
                ? (back ? " · 起跳点燃 1（焚烬同系数）" : " · 落点点燃 1（焚烬同系数）")
                : " · 无伤害";
        player.sendMessage(ChatColor.GRAY + "  冷却 " + def.cooldownSeconds + "s · 位移约 "
                + (int) Math.round(dist) + " 格 · 零体力" + igniteNote
                + ChatColor.DARK_GRAY + " · 方向 "
                + town.sunshine.corerpg.p1.EmberSkillKit.stepDirName(
                        town.sunshine.corerpg.p1.EmberSkillKit.stepDirId(data)));
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
        boolean huohen = lastCastWasHuohen(player);
        boolean back = lastCastWasBack(player);
        String shown = (PILOT_ID.equals(def.id) || "step".equals(def.type) || "dash".equals(def.type))
                ? town.sunshine.corerpg.p1.EmberSkillKit.stepDisplayName(huohen, back)
                : ChatColor.stripColor(def.display);
        String tip = "";
        if (huohen) tip = back ? ChatColor.GRAY + " · 起跳点燃" : ChatColor.GRAY + " · 落点点燃";
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 " + ChatColor.RESET + shown + tip);
    }

    /** Set by {@link #castStep} when 火痕步 variant is active for the cast. */
    private final java.util.Map<UUID, Boolean> lastHuohen = new HashMap<UUID, Boolean>();
    /** Set by {@link #castStep} when the cast used 后撤 direction. */
    private final java.util.Map<UUID, Boolean> lastBack = new HashMap<UUID, Boolean>();

    private boolean lastCastWasHuohen(Player player) {
        Boolean v = lastHuohen.remove(player.getUniqueId());
        return v != null && v.booleanValue();
    }

    private boolean lastCastWasBack(Player player) {
        Boolean v = lastBack.remove(player.getUniqueId());
        return v != null && v.booleanValue();
    }

    /**
     * Horizontal look-dir dash. Forward uses {@code def.distance} (5); back uses {@link
     * town.sunshine.corerpg.p1.EmberSkillKit#BACKSTEP_DISTANCE} (4) + strictGround.
     * Does not clip through solid blocks; on failure stays put + short tip. No damage / stamina / invuln.
     */
    private boolean castStep(Player player, FlexDef def) {
        Location from = player.getLocation();
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean back = town.sunshine.corerpg.p1.EmberSkillKit.stepBackward(data);
        double dist = back ? town.sunshine.corerpg.p1.EmberSkillKit.BACKSTEP_DISTANCE : def.distance;
        Location best = town.sunshine.corerpg.p1.EmberDash.tryDash(player, dist, back, back);
        if (best == null) {
            String fail = from.getWorld() == null ? "无法踏步"
                    : (back ? "后方受阻，无法后撤" : "前方受阻，无法踏步");
            player.sendMessage(PREFIX + ChatColor.YELLOW + fail);
            lastHuohen.put(player.getUniqueId(), Boolean.FALSE);
            lastBack.put(player.getUniqueId(), Boolean.valueOf(back));
            return false;
        }
        spawnParticles(from.clone().add(0, 0.2, 0), def.particles, 10);
        player.teleport(best);
        spawnParticles(best.clone().add(0, 0.2, 0), def.particles, 14);
        playSound(best, def.sound);
        // Display name follows unlock+焚烬 even in hub; ignite only fires in P1 worlds.
        boolean variant = isHuohenActive(player);
        if (variant) {
            // D219: 火痕·后撤 ignites takeoff; forward 火痕步 still ignites landing (B-R4 / D217).
            applyHuohenIfActive(player, back ? from : best);
        }
        lastHuohen.put(player.getUniqueId(), Boolean.valueOf(variant));
        lastBack.put(player.getUniqueId(), Boolean.valueOf(back));
        return true;
    }

    /**
     * D214/D219 火痕步: Q05 + 焚烬 2pc → ignite nearest enemy within
     * {@link town.sunshine.corerpg.p1.EmberSkillKit#STEP_IGNITE_RADIUS} of {@code at}
     * (landing for forward, takeoff for back) at set burn ×1.0.
     */
    private boolean applyHuohenIfActive(Player player, Location at) {
        if (player == null || at == null || at.getWorld() == null) return false;
        if (!town.sunshine.corerpg.p1.EmberMode.isP1(player)) return false;
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        PlayerData data = dataStore.get(player.getUniqueId());
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        town.sunshine.corerpg.p1.EmberLoadout lo = ls == null ? null : ls.get(player);
        String fam = lo == null ? "none" : lo.activeSet;
        if (!town.sunshine.corerpg.p1.EmberSkillKit.huohenActive(data, runs, fam)) return false;
        town.sunshine.corerpg.p1.EmberSetService sets = plugin.getEmberSets();
        if (sets == null) return true;
        LivingEntity target = nearestEnemy(player, at,
                town.sunshine.corerpg.p1.EmberSkillKit.STEP_IGNITE_RADIUS);
        if (target == null) return true;
        sets.skillIgnite(player, target, town.sunshine.corerpg.p1.EmberSkillKit.STEP_BURN_MULT);
        at.getWorld().spawnParticle(Particle.FLAME, at.clone().add(0, 0.3, 0), 18, 0.4, 0.2, 0.4, 0.02);
        return true;
    }

    private LivingEntity nearestEnemy(Player player, Location at, double radius) {
        LivingEntity best = null;
        double bestD = radius + 1.0e-6;
        for (Entity e : at.getWorld().getNearbyEntities(at, radius, radius, radius)) {
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity le = (LivingEntity) e;
            if (!isStepEnemy(player, le)) continue;
            double d = le.getLocation().distance(at);
            if (d < bestD) {
                bestD = d;
                best = le;
            }
        }
        return best;
    }

    private boolean isStepEnemy(Player player, LivingEntity le) {
        if (le == null || le.isDead() || !le.isValid()) return false;
        if (le instanceof Player || le instanceof ArmorStand) return false;
        if (le.equals(player)) return false;
        if (le instanceof Tameable && ((Tameable) le).isTamed()) return false;
        if (le instanceof Monster || le instanceof Slime || le instanceof Ghast || le instanceof Shulker
                || le instanceof ComplexLivingEntity) return true;
        QuestService q = plugin.getQuestService();
        return q != null && q.isMythic(le);
    }

    /** Hub / kit UI: whether 火痕步 is currently replacing 踏步 for this player. */
    public boolean isHuohenActive(Player player) {
        if (player == null) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        town.sunshine.corerpg.p1.EmberLoadout lo = ls == null ? null : ls.get(player);
        return town.sunshine.corerpg.p1.EmberSkillKit.huohenActive(data, runs, lo == null ? "none" : lo.activeSet);
    }

    private void startCooldown(UUID uuid, String skillId, int seconds) {
        if (seconds <= 0) return;
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) {
            map = new HashMap<String, Long>();
            cooldowns.put(uuid, map);
        }
        map.put(skillId, Long.valueOf(nowMs() + seconds * 1000L));
    }

    private long remainingCooldownMs(UUID uuid, String skillId) {
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) return 0L;
        Long exp = map.get(skillId);
        if (exp == null) return 0L;
        long left = exp.longValue() - nowMs();
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

    /** Monotonic ms (book §20.6: a system clock change must not shorten or reset a cooldown). */
    private static long nowMs() { return System.nanoTime() / 1000000L; }

    /**
     * D219: skill-page direction swap puts the shared 身法 CD on full (same rule as X1 for 烬斩符文).
     * No-op when no flex equipped.
     */
    public void putEquippedOnFullCooldown(Player player) {
        if (player == null) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        if (data == null || !data.hasFlexSkill()) return;
        FlexDef def = flexSkills.get(data.getFlexSkillId());
        if (def == null) return;
        startCooldown(player.getUniqueId(), def.id, def.cooldownSeconds);
    }

    /** Book §20.6 / G05: relogging must not reset the step cooldown, so only expired entries are dropped here. */
    public void onQuit(UUID uuid) {
        if (uuid == null) return;
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) return;
        long now = nowMs();
        java.util.Iterator<Map.Entry<String, Long>> it = map.entrySet().iterator();
        while (it.hasNext()) if (it.next().getValue().longValue() <= now) it.remove();
        if (map.isEmpty()) cooldowns.remove(uuid);
    }
}
