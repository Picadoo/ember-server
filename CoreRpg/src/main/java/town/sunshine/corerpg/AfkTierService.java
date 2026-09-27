package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 1.14.0 (phase 3): tiered public AFK zones in the AFK world (Lv10/20/30/40).
 * Level-gated entry (/corerpg afk n, menu), region enforcement, keepInventory + safe respawn on the tier's pad,
 * spawn protection, suffocation guard, level-up unlock hints. Drops stay in MythicMobs → corerpg mmgive, so every
 * tier shares the same per-player daily caps (afk_caps, keyed by item, not by tier).
 */
public class AfkTierService implements Listener {

    static final class Tier {
        int n; String name; int level; String desc;
        double x, y, z; float yaw;
        boolean hasRegion; int minX, minZ, maxX, maxZ;
        // build (sky arena) — null floor = natural terrain, no build
        int cx, by, cz, r; String floor, wall, light;
        boolean inside(Location l) {
            return hasRegion && l.getBlockX() >= minX && l.getBlockX() <= maxX && l.getBlockZ() >= minZ && l.getBlockZ() <= maxZ;
        }
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final List<Tier> tiers = new ArrayList<Tier>();
    private final Map<UUID, Integer> deathTier = new HashMap<UUID, Integer>();
    private final Map<UUID, Long> lastKick = new HashMap<UUID, Long>();
    private boolean enabled;
    private String worldName;
    private int protectSeconds;
    private int taskId = -1;

    public AfkTierService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void reload() {
        tiers.clear();
        ConfigurationSection s = plugin.getConfig().getConfigurationSection("afk_tiers");
        enabled = s != null && s.getBoolean("enabled", false);
        if (s == null) return;
        worldName = s.getString("world", "ember_afk");
        protectSeconds = Math.max(0, s.getInt("spawn_protect_seconds", 5));
        ConfigurationSection ts = s.getConfigurationSection("tiers");
        if (ts != null) for (String k : ts.getKeys(false)) {
            ConfigurationSection c = ts.getConfigurationSection(k);
            if (c == null) continue;
            Tier t = new Tier();
            try { t.n = Integer.parseInt(k); } catch (NumberFormatException e) { continue; }
            t.name = c.getString("name", "挂机层 " + k);
            t.level = c.getInt("level", 0);
            t.desc = c.getString("desc", "");
            ConfigurationSection b = c.getConfigurationSection("build");
            if (b != null) {
                t.cx = b.getInt("cx"); t.by = b.getInt("y"); t.cz = b.getInt("cz"); t.r = Math.max(6, b.getInt("r", 18));
                t.floor = b.getString("floor", "SMOOTH_BRICK"); t.wall = b.getString("wall", "COBBLE_WALL"); t.light = b.getString("light", "GLOWSTONE");
                // pad = north edge inside the arena, facing south (+z) toward the spawners
                t.x = t.cx + 0.5; t.y = t.by + 1; t.z = t.cz - t.r + 2.5; t.yaw = 0f;
                int m = Math.max(0, b.getInt("region_margin", 24));
                t.hasRegion = true; t.minX = t.cx - t.r - m; t.maxX = t.cx + t.r + m; t.minZ = t.cz - t.r - m; t.maxZ = t.cz + t.r + m;
            } else {
                t.x = c.getDouble("x"); t.y = c.getDouble("y"); t.z = c.getDouble("z"); t.yaw = (float) c.getDouble("yaw", 0);
            }
            List<Integer> reg = c.getIntegerList("region");
            if (reg.size() == 4) { t.hasRegion = true; t.minX = reg.get(0); t.minZ = reg.get(1); t.maxX = reg.get(2); t.maxZ = reg.get(3); }
            tiers.add(t);
        }
        java.util.Collections.sort(tiers, new java.util.Comparator<Tier>() {
            @Override public int compare(Tier a, Tier b) { return a.n - b.n; }
        });
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        if (enabled) {
            long period = Math.max(10L, s.getLong("check_ticks", 40L));
            taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override public void run() { tick(); }
            }, period, period).getTaskId();
        }
    }

    private World world() { return worldName == null ? null : Bukkit.getWorld(worldName); }
    private boolean inAfk(Location l) { return enabled && l != null && l.getWorld() != null && l.getWorld().getName().equals(worldName); }

    private Tier tier(int n) { for (Tier t : tiers) if (t.n == n) return t; return null; }
    private Tier base() { return tiers.isEmpty() ? null : tiers.get(0); }

    /** Tier whose region contains l; the lowest tier (natural terrain, no region) is the fallback for the rest of the world. */
    Tier tierAt(Location l) {
        for (Tier t : tiers) if (t.inside(l)) return t;
        return base();
    }

    private Location pad(Tier t) {
        World w = world();
        return w == null ? null : new Location(w, t.x, t.y, t.z, t.yaw, 0f);
    }

    private int level(Player p) { return dataStore.get(p.getUniqueId()).getEmberLevel(); }

    private void protect(final Player p) {
        if (protectSeconds <= 0) return;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) return;
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, protectSeconds * 20, 4, true, false), true);
            }
        }, 2L);
    }

    private void tick() {
        World w = world();
        if (w == null) return;
        for (Player p : w.getPlayers()) {
            if (p.isOp() || p.isDead()) continue;
            Tier t = tierAt(p.getLocation());
            if (t == null || level(p) >= t.level) continue;
            Tier b = highestUnlocked(p);
            Location to = b == null ? null : pad(b);
            if (to == null) continue;
            p.teleport(to);
            protect(p);
            Long last = lastKick.get(p.getUniqueId());
            if (last == null || System.currentTimeMillis() - last > 10000) {
                lastKick.put(p.getUniqueId(), System.currentTimeMillis());
                p.sendMessage(ChatColor.RED + "[挂机] " + t.name + " 需要余烬等级 Lv." + t.level + "（当前 Lv." + level(p) + "），已送回 " + b.name + "。");
            }
        }
    }

    private Tier highestUnlocked(Player p) {
        Tier best = base();
        int lv = level(p);
        for (Tier t : tiers) if (lv >= t.level) best = t;
        return best;
    }

    // ---- events ----

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!inAfk(p.getLocation())) return;
        e.setKeepInventory(true);
        e.setKeepLevel(true);
        e.getDrops().clear();
        e.setDroppedExp(0);
        Tier t = tierAt(p.getLocation());
        if (t != null) deathTier.put(p.getUniqueId(), t.n);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Integer n = deathTier.remove(e.getPlayer().getUniqueId());
        if (n == null || !enabled) return;
        Tier t = tier(n);
        if (t == null || level(e.getPlayer()) < t.level) t = highestUnlocked(e.getPlayer());
        Location to = t == null ? null : pad(t);
        if (to == null) return;
        e.setRespawnLocation(to);
        protect(e.getPlayer());
    }

    /** Mobs (or players) pushed into blocks in the AFK world are lifted out instead of suffocating. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSuffocate(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) return;
        if (!(e.getEntity() instanceof LivingEntity) || !inAfk(e.getEntity().getLocation())) return;
        e.setCancelled(true);
        LivingEntity le = (LivingEntity) e.getEntity();
        if (le instanceof Player) {
            Tier t = tierAt(le.getLocation());
            Location to = t == null ? null : pad(t);
            if (to != null) le.teleport(to);
            return;
        }
        Location l = le.getLocation();
        Block b = l.getBlock();
        for (int i = 0; i < 12; i++) {
            Block feet = b.getRelative(0, i, 0), head = b.getRelative(0, i + 1, 0);
            if (!feet.getType().isSolid() && !head.getType().isSolid()) {
                le.teleport(new Location(l.getWorld(), l.getX(), feet.getY(), l.getZ(), l.getYaw(), l.getPitch()));
                if (plugin.getConfig().getBoolean("afk_tiers.debug", false))
                    plugin.getLogger().info("afk suffocation lift " + le.getType() + " +" + i + " at " + feet.getX() + "," + feet.getY() + "," + feet.getZ());
                return;
            }
        }
    }

    /** Called by ProgressService after a level-up. */
    public void onLevelUp(Player p, int from, int to) {
        if (!enabled) return;
        for (Tier t : tiers) {
            if (t.level > from && t.level <= to && t.level > 10) {
                p.sendMessage(ChatColor.GREEN + "[挂机] 新挂机层解锁：" + ChatColor.YELLOW + t.name + ChatColor.GREEN + "（Lv." + t.level + "）"
                        + ChatColor.GRAY + " · /ember → 挂机庭，或 /corerpg afk " + t.n);
            }
        }
    }

    // ---- command ----

    public boolean cmd(CommandSender sender, String[] args) {
        String a = args.length >= 2 ? args[1].toLowerCase() : "";
        if ("build".equals(a)) return cmdBuild(sender, args);
        if (!(sender instanceof Player)) { sender.sendMessage("players only"); return true; }
        Player p = (Player) sender;
        if (!enabled) { p.sendMessage(ChatColor.RED + "[挂机] 分层挂机未开启。"); return true; }
        if (a.isEmpty() || "list".equals(a) || "status".equals(a)) { list(p); return true; }
        int n;
        try { n = Integer.parseInt(a); } catch (NumberFormatException e) { list(p); return true; }
        Tier t = tier(n);
        if (t == null) { p.sendMessage(ChatColor.RED + "[挂机] 没有第 " + n + " 层。/corerpg afk 查看"); return true; }
        if (!p.isOp() && level(p) < t.level) {
            p.sendMessage(ChatColor.RED + "[挂机] " + t.name + " 需要余烬等级 " + ChatColor.YELLOW + "Lv." + t.level
                    + ChatColor.RED + "（当前 Lv." + level(p) + "）" + ChatColor.GRAY + " · /corerpg level 查看升级进度");
            return true;
        }
        QuestService qs = plugin.getQuestService();
        if (qs != null && qs.isInstanceWorld(p.getWorld())) { p.sendMessage(ChatColor.RED + "[挂机] 副本中请先 /dp leave。"); return true; }
        Location to = pad(t);
        if (to == null) { p.sendMessage(ChatColor.RED + "[挂机] 挂机世界未加载。"); return true; }
        p.teleport(to);
        protect(p);
        p.sendMessage(ChatColor.GREEN + "[挂机] 已到达 " + ChatColor.YELLOW + t.name + ChatColor.GREEN + "（Lv." + t.level + "）"
                + ChatColor.GRAY + " · " + t.desc);
        p.sendMessage(ChatColor.GRAY + "  死亡不掉落，复活在本层入口 · 掉落与其它层共用每日上限（" + capLine(p) + "）· /hub 回城");
        return true;
    }

    private void list(Player p) {
        int lv = level(p);
        p.sendMessage(ChatColor.GOLD + "[挂机] 分层挂机（当前 Lv." + lv + "）· 四层共用每日掉落上限");
        for (Tier t : tiers) {
            boolean ok = lv >= t.level;
            p.sendMessage((ok ? ChatColor.GREEN + " ✔ " : ChatColor.DARK_GRAY + " ✖ ") + t.n + ". " + t.name + " Lv." + t.level
                    + ChatColor.GRAY + " · " + t.desc + (ok ? ChatColor.YELLOW + "  /corerpg afk " + t.n : ""));
        }
        p.sendMessage(ChatColor.GRAY + " 今日：" + capLine(p));
    }

    String capLine(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        StringBuilder sb = new StringBuilder();
        String[][] items = { { "mat_ember_shard", "碎片" }, { "mat_ember_bone_dust", "骨尘" }, { "mat_ember_core_fragment", "核心碎片" }, { "mat_ember_soul_dust", "魂尘" } };
        for (String[] it : items) {
            int cap = plugin.getConfig().getInt("afk_caps.items." + it[0], -1);
            if (cap < 0) continue;
            if (sb.length() > 0) sb.append(" · ");
            sb.append(it[1]).append(' ').append(d.periodCount("afk_" + it[0], today)).append('/').append(cap);
        }
        int coinCap = plugin.getConfig().getInt("afk_caps.kill_coin", -1);
        if (coinCap >= 0) sb.append(" · 击杀币 ").append(d.periodCount("afk_coin", today)).append('/').append(coinCap);
        return sb.toString();
    }

    /** /corerpg afk build <n> — (re)build a tier's sky arena from config (the world save is not in git). */
    private boolean cmdBuild(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length < 3) { sender.sendMessage("/corerpg afk build <n>"); return true; }
        Tier t;
        try { t = tier(Integer.parseInt(args[2])); } catch (NumberFormatException e) { t = null; }
        World w = world();
        if (t == null || t.floor == null || w == null) { sender.sendMessage(ChatColor.RED + "该层没有 build 配置或世界未加载"); return true; }
        Material floor = mat(t.floor, Material.SMOOTH_BRICK), wall = mat(t.wall, Material.COBBLE_WALL), light = mat(t.light, Material.GLOWSTONE);
        int changed = 0;
        int R = t.r + 1;
        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                int x = t.cx + dx, z = t.cz + dz;
                boolean edge = Math.abs(dx) == R || Math.abs(dz) == R;
                boolean lamp = !edge && dx % 6 == 0 && dz % 6 == 0;
                changed += set(w.getBlockAt(x, t.by - 1, z), floor);
                changed += set(w.getBlockAt(x, t.by, z), lamp ? light : floor);
                for (int y = 1; y <= 10; y++) {
                    Material m = Material.AIR;
                    if (edge && y == 1) m = wall;
                    else if (edge && y <= 5) m = Material.BARRIER;
                    changed += set(w.getBlockAt(x, t.by + y, z), m);
                }
            }
        }
        // entry pad marker (3×3)
        int px = (int) Math.floor(t.x), pz = (int) Math.floor(t.z);
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) changed += set(w.getBlockAt(px + dx, t.by, pz + dz), Material.QUARTZ_BLOCK);
        sender.sendMessage(ChatColor.GREEN + "[挂机] 已构建 " + t.name + " @ " + t.cx + "," + t.by + "," + t.cz + " r=" + t.r + "（改动 " + changed + " 格）");
        plugin.getLogger().info("afk build tier " + t.n + " changed " + changed);
        return true;
    }

    private static Material mat(String s, Material def) {
        Material m = s == null ? null : Material.matchMaterial(s);
        return m == null ? def : m;
    }

    private static int set(Block b, Material m) {
        if (b.getType() == m) return 0;
        b.setType(m, false);
        return 1;
    }
}
