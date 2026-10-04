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
 * P0 (2026-09-27): tiers 2–4 build as ground/cave scenic pads (no y110 sky islands).
 */
public class AfkTierService implements Listener {

    static final class Tier {
        int n; String name; int level; String desc;
        double x, y, z; float yaw;
        boolean hasRegion; int minX, minZ, maxX, maxZ;
        // build — floor==null means natural terrain (tier 1), no build
        int cx, by, cz, r; String floor, wall, light, accent;
        String mode; // sky | ground | cave (theme also via mode/theme)
        String theme; // ruins | scorched | cave
        boolean relief;
        int clearOldY;
        boolean inside(Location l) {
            return hasRegion && l.getBlockX() >= minX && l.getBlockX() <= maxX && l.getBlockZ() >= minZ && l.getBlockZ() <= maxZ;
        }
        boolean isGroundish() {
            return "ground".equalsIgnoreCase(mode) || "cave".equalsIgnoreCase(mode)
                    || "ruins".equalsIgnoreCase(theme) || "scorched".equalsIgnoreCase(theme) || "cave".equalsIgnoreCase(theme);
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
                t.cx = b.getInt("cx"); t.by = b.getInt("y", b.getInt("base_y", 70)); t.cz = b.getInt("cz");
                t.r = Math.max(6, b.getInt("r", 18));
                t.floor = b.getString("floor", "SMOOTH_BRICK");
                t.wall = b.getString("wall", "COBBLE_WALL");
                t.light = b.getString("light", "GLOWSTONE");
                t.accent = b.getString("accent", "COBBLESTONE");
                t.mode = b.getString("mode", "sky");
                t.theme = b.getString("theme", t.mode);
                t.relief = b.getBoolean("relief", true);
                t.clearOldY = b.getInt("clear_old_y", 110);
                // P0: ground/cave spawn at center pad; legacy sky = north edge facing +z
                t.x = t.cx + 0.5;
                t.y = t.by + 1;
                if (t.isGroundish()) {
                    t.z = t.cz + 0.5;
                    t.yaw = 0f;
                } else {
                    t.z = t.cz - t.r + 2.5;
                    t.yaw = 0f;
                }
                int m = Math.max(0, b.getInt("region_margin", 24));
                t.hasRegion = true; t.minX = t.cx - t.r - m; t.maxX = t.cx + t.r + m; t.minZ = t.cz - t.r - m; t.maxZ = t.cz + t.r + m;
            } else {
                t.x = c.getDouble("x"); t.y = c.getDouble("y"); t.z = c.getDouble("z"); t.yaw = (float) c.getDouble("yaw", 0);
            }
            // explicit spawn override (optional)
            if (c.contains("x")) t.x = c.getDouble("x");
            if (c.contains("y")) t.y = c.getDouble("y");
            if (c.contains("z")) t.z = c.getDouble("z");
            if (c.contains("yaw")) t.yaw = (float) c.getDouble("yaw");
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

    /** D177: under P1 the tiers open by main-story first clears (ember-v1.yml afk.tiers), else by ember level. */
    private static town.sunshine.corerpg.p1.EmberAfkService p1afk() {
        town.sunshine.corerpg.p1.EmberAfkService a = town.sunshine.corerpg.p1.EmberAfkService.get();
        return a != null && a.p1() ? a : null;
    }

    private boolean unlocked(Player p, Tier t) {
        town.sunshine.corerpg.p1.EmberAfkService a = p1afk();
        return a != null ? a.tierUnlocked(p, t.n) : level(p) >= t.level;
    }

    private String reqText(Player p, Tier t) {
        town.sunshine.corerpg.p1.EmberAfkService a = p1afk();
        if (a != null) return "需要" + a.requiresLabel(t.n);
        return "需要余烬等级 Lv." + t.level + "（当前 Lv." + level(p) + "）";
    }

    private String tierLabel(Tier t) {
        town.sunshine.corerpg.p1.EmberAfkService a = p1afk();
        return a != null ? a.requiresLabel(t.n) : "Lv." + t.level;
    }

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
            if (t == null || unlocked(p, t)) continue;
            Tier b = highestUnlocked(p);
            if (b == null || !unlocked(p, b)) continue; // D177: nothing unlocked yet (P1 before Q01) — no teleport loop
            Location to = pad(b);
            if (to == null) continue;
            p.teleport(to);
            protect(p);
            Long last = lastKick.get(p.getUniqueId());
            if (last == null || System.currentTimeMillis() - last > 10000) {
                lastKick.put(p.getUniqueId(), System.currentTimeMillis());
                p.sendMessage(ChatColor.RED + "[挂机] " + t.name + " " + reqText(p, t) + "，已送回 " + b.name + "。");
            }
        }
    }

    private Tier highestUnlocked(Player p) {
        Tier best = base();
        for (Tier t : tiers) if (unlocked(p, t)) best = t;
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
        if (t == null || !unlocked(e.getPlayer(), t)) t = highestUnlocked(e.getPlayer());
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
        if (!enabled || p1afk() != null) return; // D177: P1 tiers open by first clears, not levels
        for (Tier t : tiers) {
            if (t.level > from && t.level <= to && t.level > 10) {
                p.sendMessage(ChatColor.GREEN + "[挂机] 新挂机层解锁：" + ChatColor.YELLOW + t.name + ChatColor.GREEN + "（Lv." + t.level + "）"
                        + ChatColor.GRAY + " · 打开 /ember → 挂机庭");
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
        if (t == null) { p.sendMessage(ChatColor.RED + "[挂机] 没有第 " + n + " 层。打开 /ember → 挂机庭"); return true; }
        if (!p.isOp() && !unlocked(p, t)) {
            p.sendMessage(ChatColor.RED + "[挂机] " + t.name + " " + ChatColor.YELLOW + reqText(p, t)
                    + ChatColor.GRAY + " · 打开 /ember 查看进度");
            return true;
        }
        QuestService qs = plugin.getQuestService();
        if (qs != null && qs.isInstanceWorld(p.getWorld())) { p.sendMessage(ChatColor.RED + "[挂机] 副本中请先离开副本。"); return true; }
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(p)) { p.sendMessage(ChatColor.RED + "[挂机] 主线本里请先离开副本。"); return true; }
        Location to = pad(t);
        if (to == null) { p.sendMessage(ChatColor.RED + "[挂机] 挂机世界未加载。"); return true; }
        p.teleport(to);
        protect(p);
        town.sunshine.corerpg.p1.EmberAfkService ea = p1afk();
        if (ea != null) { // D177
            p.sendMessage(ChatColor.GREEN + "[挂机庭] 已到达 " + ChatColor.YELLOW + t.name + ChatColor.GRAY
                    + " · 在挂机庭任意位置每 " + ea.roundMinutes() + " 分钟结算一轮（按你已解锁的最高层计）· 怪物不掉东西，打不打都行");
            p.sendMessage(ChatColor.GRAY + "  今日 " + ea.statusLine(p) + ChatColor.GRAY + " · 死亡不掉落，复活在本层入口 · 打开枢纽菜单可返回");
            return true;
        }
        p.sendMessage(ChatColor.GREEN + "[挂机] 已到达 " + ChatColor.YELLOW + t.name + ChatColor.GREEN + "（Lv." + t.level + "）"
                + ChatColor.GRAY + " · " + t.desc);
        p.sendMessage(ChatColor.GRAY + "  死亡不掉落，复活在本层入口 · 掉落与其它层共用每日上限（" + capLine(p) + "）· 打开枢纽菜单可返回");
        return true;
    }

    private void list(Player p) {
        town.sunshine.corerpg.p1.EmberAfkService a = p1afk();
        if (a != null) { // D177
            p.sendMessage(ChatColor.GOLD + "[挂机庭] 四层按主线首通开放 · 收益按已解锁的最高层 · 每日上限 " + a.dailyRounds() + " 轮");
            for (Tier t : tiers) {
                boolean ok = unlocked(p, t);
                p.sendMessage((ok ? ChatColor.GREEN + " ✔ " : ChatColor.DARK_GRAY + " ✖ ") + t.n + ". " + t.name + " " + tierLabel(t)
                        + (ok ? ChatColor.YELLOW + "  可进入" : ""));
            }
            p.sendMessage(ChatColor.GRAY + " 今日：" + a.statusLine(p));
            return;
        }
        int lv = level(p);
        p.sendMessage(ChatColor.GOLD + "[挂机] 分层挂机（当前 Lv." + lv + "）· 四层共用每日掉落上限");
        for (Tier t : tiers) {
            boolean ok = lv >= t.level;
            p.sendMessage((ok ? ChatColor.GREEN + " ✔ " : ChatColor.DARK_GRAY + " ✖ ") + t.n + ". " + t.name + " Lv." + t.level
                    + ChatColor.GRAY + " · " + t.desc + (ok ? ChatColor.YELLOW + "  可进入" : ""));
        }
        p.sendMessage(ChatColor.GRAY + " 今日：" + capLine(p) + " · 打开 /ember → 挂机庭");
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

    /** /corerpg afk build <n> — (re)build a tier pad from config (world save is not in git). */
    private boolean cmdBuild(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length < 3) { sender.sendMessage("/corerpg afk build <n>"); return true; }
        Tier t;
        try { t = tier(Integer.parseInt(args[2])); } catch (NumberFormatException e) { t = null; }
        World w = world();
        if (t == null || t.floor == null || w == null) { sender.sendMessage(ChatColor.RED + "该层没有 build 配置或世界未加载"); return true; }

        int cleared = clearOldSkyIsland(w, t);
        int changed;
        String theme = t.theme != null ? t.theme.toLowerCase() : "";
        String mode = t.mode != null ? t.mode.toLowerCase() : "";
        if ("cave".equals(theme) || "cave".equals(mode) || t.n == 4) {
            changed = buildCave(w, t);
        } else if ("scorched".equals(theme) || "ground".equals(mode) && t.n == 3 || t.n == 3) {
            changed = buildScorched(w, t);
        } else if ("ruins".equals(theme) || "ground".equals(mode) || t.n == 2) {
            changed = buildRuins(w, t);
        } else {
            changed = buildLegacySky(w, t);
        }

        // refresh spawn pad from tier fields set by builders
        plugin.getConfig().set("afk_tiers.tiers." + t.n + ".build.y", t.by);
        plugin.getConfig().set("afk_tiers.tiers." + t.n + ".x", t.x);
        plugin.getConfig().set("afk_tiers.tiers." + t.n + ".y", t.y);
        plugin.getConfig().set("afk_tiers.tiers." + t.n + ".z", t.z);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "[挂机] 已构建 " + t.name + " @ " + t.cx + "," + t.by + "," + t.cz
                + " r=" + t.r + " mode=" + mode + "/" + theme
                + "（改动 " + changed + " · 清旧岛 " + cleared + "）入口 " + fmt(t.x) + "," + fmt(t.y) + "," + fmt(t.z));
        plugin.getLogger().info("afk build tier " + t.n + " y=" + t.by + " spawn=" + t.x + "," + t.y + "," + t.z
                + " changed=" + changed + " cleared=" + cleared);
        return true;
    }

    private static String fmt(double d) {
        return String.format(java.util.Locale.US, "%.1f", d);
    }

    /** Air-out leftover y≈110 sky platforms so getHighestBlockYAt / players don't stick there. */
    private int clearOldSkyIsland(World w, Tier t) {
        int oldY = t.clearOldY > 0 ? t.clearOldY : 110;
        int R = t.r + 3;
        int changed = 0;
        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                int x = t.cx + dx, z = t.cz + dz;
                for (int y = oldY - 3; y <= oldY + 14; y++) {
                    Block b = w.getBlockAt(x, y, z);
                    if (b.getType() != Material.AIR) changed += set(b, Material.AIR);
                }
            }
        }
        return changed;
    }

    /** Highest solid non-barrier under yMax (after sky clear). */
    private int probeSurface(World w, int x, int z) {
        // Scan down from y85 so leftover floating pads above do not win.
        for (int y = 85; y >= 40; y--) {
            Material m = w.getBlockAt(x, y, z).getType();
            if (m.isSolid() && m != Material.BARRIER) return tClamp(y);
        }
        int hi = w.getHighestBlockYAt(x, z);
        return tClamp(hi > 0 ? hi : 64);
    }

    private static int tClamp(int y) {
        if (y < 55) return 64;
        if (y > 85) return 78;
        return y;
    }

    /** Deterministic low-amp height offset. */
    private static int reliefAt(int dx, int dz, int amp) {
        if (amp <= 0) return 0;
        int h = dx * 374761393 + dz * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        h = (h ^ (h >> 16)) & 0x7fffffff;
        return (h % (amp * 2 + 1)) - amp;
    }

    private static boolean nearCenter(int dx, int dz, int clearR) {
        return dx * dx + dz * dz <= clearR * clearR;
    }

    // ---- theme builders ----

    /** ② 荒原：贴地碎砖缓坡 + 半墙掩体 */
    private int buildRuins(World w, Tier t) {
        Material floor = mat(t.floor, Material.SMOOTH_BRICK);
        Material wall = mat(t.wall, Material.COBBLE_WALL);
        Material accent = mat(t.accent, Material.COBBLESTONE);
        Material light = mat(t.light, Material.GLOWSTONE);
        Material gravel = Material.GRAVEL;
        Material grass = Material.GRASS;

        int surface = probeSurface(w, t.cx, t.cz);
        // sample a few neighbors for stability
        surface = (surface + probeSurface(w, t.cx + 8, t.cz) + probeSurface(w, t.cx, t.cz + 8)) / 3;
        surface = tClamp(surface);
        t.by = surface;

        int changed = 0;
        int R = t.r;
        int amp = t.relief ? 2 : 0;

        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                int x = t.cx + dx, z = t.cz + dz;
                int hOff = reliefAt(dx, dz, amp);
                // gentle slope toward +z south
                hOff += dz / 9;
                int fy = surface + hOff;
                if (fy < surface - 2) fy = surface - 2;
                if (fy > surface + 3) fy = surface + 3;

                boolean edge = Math.abs(dx) == R || Math.abs(dz) == R;
                boolean open = nearCenter(dx, dz, 6);

                // fill column from a bit below to floor
                for (int y = surface - 4; y < fy; y++) changed += set(w.getBlockAt(x, y, z), accent);
                Material top = floor;
                int mix = Math.abs(reliefAt(dx + 3, dz - 2, 5));
                if (mix == 0) top = accent;
                else if (mix == 1) top = gravel;
                else if (mix == 2 && !edge) top = grass;
                changed += set(w.getBlockAt(x, fy, z), top);
                if (!edge && dx % 7 == 0 && dz % 7 == 0 && !open) changed += set(w.getBlockAt(x, fy, z), light);

                // clear stand space
                for (int y = 1; y <= 6; y++) changed += set(w.getBlockAt(x, fy + y, z), Material.AIR);

                // perimeter low wall (no barrier cage)
                if (edge) {
                    changed += set(w.getBlockAt(x, fy + 1, z), wall);
                }
            }
        }

        // half-wall cover props (2–3 clusters), away from center
        int[][] covers = { { -10, -6 }, { 9, -8 }, { -7, 10 }, { 11, 7 } };
        for (int[] c : covers) {
            int bx = t.cx + c[0], bz = t.cz + c[1];
            int fy = w.getHighestBlockYAt(bx, bz);
            if (fy > 90) fy = surface;
            for (int i = 0; i < 4; i++) {
                int x = bx + (i % 2), z = bz + (i / 2);
                int y0 = Math.min(fy, surface + 2);
                changed += set(w.getBlockAt(x, y0, z), floor);
                changed += set(w.getBlockAt(x, y0 + 1, z), wall);
                changed += set(w.getBlockAt(x, y0 + 2, z), wall);
                changed += set(w.getBlockAt(x, y0 + 3, z), Material.AIR);
            }
        }

        // entry pad + signs at center
        changed += placeEntryPad(w, t, surface, Material.QUARTZ_BLOCK);
        changed += placeTierSigns(w, t, surface, "§a挂机·②荒原");
        return changed;
    }

    /** ③ 焦土：焦裂谷地，落差 4～8，熔岩装饰沟+护栏 */
    private int buildScorched(World w, Tier t) {
        Material floor = mat(t.floor, Material.NETHER_BRICK);
        Material wall = mat(t.wall, Material.NETHER_FENCE);
        Material accent = mat(t.accent, Material.NETHERRACK);
        Material light = mat(t.light, Material.GLOWSTONE);
        Material obsidian = Material.OBSIDIAN;
        Material magma = Material.MAGMA;

        int surface = tClamp(probeSurface(w, t.cx, t.cz));
        // valley floor ~ surface-5
        int valley = surface - 5;
        if (valley < 58) valley = 58;
        t.by = valley;

        int changed = 0;
        int R = t.r;

        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                int x = t.cx + dx, z = t.cz + dz;
                double dist = Math.sqrt(dx * dx + dz * dz) / (double) R;
                // rim higher, center lower — drop ~4–8
                int drop = (int) Math.round((1.0 - dist) * 6.0);
                if (drop < 0) drop = 0;
                if (drop > 7) drop = 7;
                int fy = surface - drop + reliefAt(dx, dz, t.relief ? 1 : 0);
                if (fy < valley - 1) fy = valley - 1;
                if (fy > surface + 1) fy = surface + 1;

                boolean edge = Math.abs(dx) == R || Math.abs(dz) == R;
                boolean open = nearCenter(dx, dz, 5);

                for (int y = valley - 3; y < fy; y++) {
                    Material fill = (y < fy - 1) ? accent : floor;
                    changed += set(w.getBlockAt(x, y, z), fill);
                }
                Material top = floor;
                int mix = Math.abs(reliefAt(dx, dz + 1, 4));
                if (mix == 0) top = accent;
                else if (mix == 1) top = obsidian;
                changed += set(w.getBlockAt(x, fy, z), top);

                // decorative magma groove ring around r≈8, fenced
                int ad = Math.abs((int) Math.round(Math.sqrt(dx * dx + dz * dz)) - 8);
                if (ad == 0 && !open) {
                    changed += set(w.getBlockAt(x, fy, z), magma);
                    changed += set(w.getBlockAt(x, fy + 1, z), wall);
                } else {
                    for (int y = 1; y <= 7; y++) changed += set(w.getBlockAt(x, fy + y, z), Material.AIR);
                    if (!edge && dx % 6 == 0 && dz % 6 == 0) changed += set(w.getBlockAt(x, fy, z), light);
                }

                if (edge) {
                    changed += set(w.getBlockAt(x, fy + 1, z), wall);
                    // short barrier only on steep rim cliff drops into void-ish — skip if solid below
                }
            }
        }

        // a few raised nether-brick shelves
        int[][] shelves = { { -9, 4 }, { 8, -5 }, { -5, -9 } };
        for (int[] s : shelves) {
            int bx = t.cx + s[0], bz = t.cz + s[1];
            int fy = valley + 3;
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
                changed += set(w.getBlockAt(bx + i, fy, bz + j), floor);
                for (int y = 1; y <= 4; y++) changed += set(w.getBlockAt(bx + i, fy + y, bz + j), Material.AIR);
            }
        }

        changed += placeEntryPad(w, t, valley, Material.QUARTZ_BLOCK);
        changed += placeTierSigns(w, t, valley, "§6挂机·③焦土");
        return changed;
    }

    /** ④ 烬原深处：竖井/坡道 → 扩厅 + 柱，开阔防窒息 */
    private int buildCave(World w, Tier t) {
        Material floor = mat(t.floor, Material.RED_NETHER_BRICK);
        Material wall = mat(t.wall, Material.NETHER_FENCE);
        Material accent = mat(t.accent, Material.NETHERRACK);
        Material light = mat(t.light, Material.SEA_LANTERN);
        Material obsidian = Material.OBSIDIAN;

        int surface = tClamp(probeSurface(w, t.cx, t.cz));
        int hall = surface - 8;
        if (hall < 52) hall = 52;
        if (hall > surface - 6) hall = surface - 6;
        t.by = hall;

        int changed = 0;
        int R = t.r;
        int hallR = R - 2;

        // dig / shape hall volume
        for (int dx = -hallR; dx <= hallR; dx++) {
            for (int dz = -hallR; dz <= hallR; dz++) {
                if (dx * dx + dz * dz > hallR * hallR) continue;
                int x = t.cx + dx, z = t.cz + dz;
                boolean open = nearCenter(dx, dz, 5);
                int hOff = reliefAt(dx, dz, t.relief ? 1 : 0);
                int fy = hall + hOff;
                if (fy < hall - 1) fy = hall - 1;
                if (fy > hall + 2) fy = hall + 2;

                // floor + subfill
                for (int y = hall - 3; y < fy; y++) changed += set(w.getBlockAt(x, y, z), accent);
                Material top = floor;
                if (Math.abs(reliefAt(dx + 1, dz, 3)) == 0) top = accent;
                if (Math.abs(reliefAt(dx, dz + 2, 5)) == 1) top = obsidian;
                changed += set(w.getBlockAt(x, fy, z), top);

                // open hall headroom (prevent suffocation)
                for (int y = 1; y <= 6; y++) changed += set(w.getBlockAt(x, fy + y, z), Material.AIR);

                // roof shell lightly
                changed += set(w.getBlockAt(x, fy + 7, z), accent);
                if (!open && dx % 5 == 0 && dz % 5 == 0) changed += set(w.getBlockAt(x, fy, z), light);
            }
        }

        // pillars (not in center open)
        int[][] pillars = { { -8, -8 }, { 8, -8 }, { -8, 8 }, { 8, 8 }, { 0, -10 }, { 0, 10 } };
        for (int[] p : pillars) {
            int x = t.cx + p[0], z = t.cz + p[1];
            for (int y = hall; y <= hall + 6; y++) changed += set(w.getBlockAt(x, y, z), floor);
            changed += set(w.getBlockAt(x, hall + 7, z), light);
        }

        // raised side platforms
        for (int side = -1; side <= 1; side += 2) {
            int bx = t.cx + side * 10, bz = t.cz;
            for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) {
                changed += set(w.getBlockAt(bx + i, hall + 2, bz + j), floor);
                for (int y = 3; y <= 6; y++) changed += set(w.getBlockAt(bx + i, hall + y, bz + j), Material.AIR);
            }
        }

        // shaft + stair ramp from surface south approach into hall (+z side)
        int sx = t.cx, sz = t.cz + 4;
        for (int step = 0; step <= 8; step++) {
            int y = surface - step;
            int z = sz + (step < 2 ? 2 : 0);
            // clear shaft column
            for (int yy = hall + 1; yy <= surface + 2; yy++) {
                changed += set(w.getBlockAt(sx, yy, sz), Material.AIR);
                changed += set(w.getBlockAt(sx + 1, yy, sz), Material.AIR);
                changed += set(w.getBlockAt(sx - 1, yy, sz), Material.AIR);
            }
            // ramp blocks
            int rz = t.cz + 12 - step;
            changed += set(w.getBlockAt(sx, y, rz), floor);
            changed += set(w.getBlockAt(sx + 1, y, rz), floor);
            changed += set(w.getBlockAt(sx - 1, y, rz), floor);
            changed += set(w.getBlockAt(sx, y + 1, rz), Material.AIR);
            changed += set(w.getBlockAt(sx, y + 2, rz), Material.AIR);
            changed += set(w.getBlockAt(sx, y + 3, rz), Material.AIR);
        }
        // surface collar + fence around shaft mouth
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            if (Math.abs(dx) != 2 && Math.abs(dz) != 2) continue;
            changed += set(w.getBlockAt(sx + dx, surface, sz + dz), floor);
            changed += set(w.getBlockAt(sx + dx, surface + 1, sz + dz), wall);
        }

        changed += placeEntryPad(w, t, hall, Material.QUARTZ_BLOCK);
        changed += placeTierSigns(w, t, hall, "§c挂机·④烬原");
        return changed;
    }

    /** Legacy flat sky arena (kept for compatibility; not used by P0 tiers). */
    private int buildLegacySky(World w, Tier t) {
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
        int px = (int) Math.floor(t.x), pz = (int) Math.floor(t.z);
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) changed += set(w.getBlockAt(px + dx, t.by, pz + dz), Material.QUARTZ_BLOCK);
        t.x = t.cx + 0.5; t.y = t.by + 1; t.z = t.cz - t.r + 2.5;
        return changed;
    }

    private int placeEntryPad(World w, Tier t, int floorY, Material pad) {
        int changed = 0;
        // design: spawn at (cx+0.5, floorY+1, cz+0.5)
        t.x = t.cx + 0.5;
        t.y = floorY + 1;
        t.z = t.cz + 0.5;
        t.by = floorY;
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            changed += set(w.getBlockAt(t.cx + dx, floorY, t.cz + dz), pad);
            for (int y = 1; y <= 3; y++) changed += set(w.getBlockAt(t.cx + dx, floorY + y, t.cz + dz), Material.AIR);
        }
        return changed;
    }

    private int placeTierSigns(World w, Tier t, int floorY, String title) {
        int changed = 0;
        // entry sign north of pad
        int ex = t.cx, ez = t.cz - 3;
        changed += set(w.getBlockAt(ex, floorY, ez), mat(t.floor, Material.SMOOTH_BRICK));
        changed += writeSign(w.getBlockAt(ex, floorY + 1, ez), title, "§7枢纽菜单", "§7→挂机庭换层", "§8死亡回入口");
        // evacuate sign south of pad
        int vx = t.cx, vz = t.cz + 3;
        changed += set(w.getBlockAt(vx, floorY, vz), mat(t.floor, Material.SMOOTH_BRICK));
        changed += writeSign(w.getBlockAt(vx, floorY + 1, vz), "§e回枢纽", "§7打开枢纽菜单", "§7返回", "");
        return changed;
    }

    private int writeSign(Block b, String l0, String l1, String l2, String l3) {
        b.setType(Material.SIGN_POST, false);
        if (b.getState() instanceof Sign) {
            Sign s = (Sign) b.getState();
            s.setLine(0, color(l0));
            s.setLine(1, color(l1));
            s.setLine(2, color(l2));
            s.setLine(3, color(l3));
            s.update(true, false);
            return 1;
        }
        return 0;
    }

    private static String color(String s) {
        return s == null ? "" : s;
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
