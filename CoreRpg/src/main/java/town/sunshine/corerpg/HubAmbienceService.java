package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitTask;
import town.sunshine.corerpg.p1.EmberCosmetics;
import town.sunshine.corerpg.p1.EmberLeaderboard;
import town.sunshine.corerpg.p1.EmberRunService;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * D97 hub atmosphere — display only, never touches stats: NPC small talk, floating signposts, particles, a leaderboard
 * board and an honors showcase in ember_hub. Text lives in the jar resource {@code hub_ambience.yml}; every floating line
 * is a marker armor stand tagged {@link #TAG}, removed on disable and purged whenever a chunk loads with a stale one.
 * Honor holders are kept in {@code p1-runs/honors-wall.yml} (runtime data, never staged).
 */
public final class HubAmbienceService implements Listener {

    static final String WORLD = "ember_hub";
    static final String TAG = "ember_ambience";
    private static final double LINE = 0.28;

    private final CoreRpgPlugin plugin;
    private YamlConfiguration cfg;
    private final List<ArmorStand> stands = new ArrayList<ArmorStand>();
    private final List<ArmorStand> header = new ArrayList<ArmorStand>(), board = new ArrayList<ArmorStand>(), honors = new ArrayList<ArmorStand>();
    private final Set<UUID> live = new HashSet<UUID>();
    private final List<BukkitTask> tasks = new ArrayList<BukkitTask>();
    private final Map<String, ArmorStand> talking = new LinkedHashMap<String, ArmorStand>();
    private final File wallFile;
    private YamlConfiguration wall;
    private long tick;

    public HubAmbienceService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        this.wallFile = new File(new File(plugin.getDataFolder(), "p1-runs"), "honors-wall.yml");
    }

    public void start() {
        stop();
        try (InputStreamReader r = new InputStreamReader(plugin.getResource("hub_ambience.yml"), StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(r);
        } catch (Exception e) {
            plugin.getLogger().warning("[hub ambience] hub_ambience.yml: " + e.getMessage());
            return;
        }
        wall = wallFile.isFile() ? YamlConfiguration.loadConfiguration(wallFile) : new YamlConfiguration();
        if (!cfg.getBoolean("enabled", true)) return;
        World w = Bukkit.getWorld(WORLD);
        if (w == null) { plugin.getLogger().warning("[hub ambience] world " + WORLD + " not loaded"); return; }
        purge(w.getEntities());
        for (Map<?, ?> s : cfg.getMapList("signs")) {
            Location at = loc(w, s.get("at"));
            if (at != null) spawnLines(at, strings(s.get("lines")), stands);
        }
        refreshBoards();
        tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, this::particles, 40L, 10L));
        tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, this::refreshBoards, 1200L, 1200L));
        long every = Math.max(5, cfg.getInt("talk_every_s", 25)) * 20L;
        tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, this::talk, every, every));
        plugin.getLogger().info("[hub ambience] " + stands.size() + " sign lines, " + cfg.getConfigurationSection("npcs").getKeys(false).size() + " NPCs talk");
    }

    public void stop() {
        for (BukkitTask t : tasks) t.cancel();
        tasks.clear();
        for (ArmorStand a : talking.values()) a.remove();
        talking.clear();
        for (List<ArmorStand> l : java.util.Arrays.asList(stands, header, board, honors)) { for (ArmorStand a : l) a.remove(); l.clear(); }
        live.clear();
    }

    public boolean cmd(CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        String a = args.length >= 2 ? args[1].toLowerCase() : "";
        if ("reload".equals(a)) { start(); s.sendMessage(ChatColor.GREEN + "[枢纽氛围] 已重载：" + live.size() + " 行悬浮字"); return true; }
        if ("talk".equals(a)) { talk(); s.sendMessage(ChatColor.GREEN + "[枢纽氛围] 触发一句闲话"); return true; }
        s.sendMessage(ChatColor.YELLOW + "/corerpg hubambience <reload|talk>");
        return true;
    }

    // ------------------------------------------------------------------ boards

    void refreshBoards() {
        World w = Bukkit.getWorld(WORLD);
        if (w == null || cfg == null) return;
        EmberRunService runs = plugin.getEmberRuns();
        String featured = runs == null ? "—" : runs.featuredShort();
        recordOnline(runs);
        ConfigurationSection h = cfg.getConfigurationSection("dungeon_header");
        if (h != null) {
            List<String> lines = new ArrayList<String>();
            for (String l : h.getStringList("lines")) lines.add(l.replace("{featured}", featured));
            replace(header, loc(w, h.getList("at")), lines);
        }
        ConfigurationSection b = cfg.getConfigurationSection("board");
        if (b != null && runs != null) {
            int n = b.getInt("rows", 3);
            List<String> lines = new ArrayList<String>();
            lines.add("§6§l余烬排行榜");
            lines.add("§5深渊 · 最高层");
            List<EmberLeaderboard.Row> a = runs.topRows(true, n);
            if (a.isEmpty()) lines.add("§8暂无，第一名等你");
            for (int i = 0; i < a.size(); i++) lines.add(medal(i) + a.get(i).name + " §7第 " + a.get(i).value + " 层");
            lines.add("§b本周精选挑战 · 通关次数");
            List<EmberLeaderboard.Row> f = runs.topRows(false, n);
            if (f.isEmpty()) lines.add("§8暂无，第一名等你");
            for (int i = 0; i < f.size(); i++) lines.add(medal(i) + f.get(i).name + " §7" + f.get(i).value + " 次");
            lines.add("§8完整榜单和你的名次：主菜单「荣誉与排行」");
            replace(board, loc(w, b.getList("at")), lines);
        }
        ConfigurationSection hs = cfg.getConfigurationSection("honors");
        if (hs != null) {
            List<String> lines = new ArrayList<String>();
            lines.add("§d§l荣誉陈列");
            lines.add("§7称号与足迹只做展示，不加属性");
            for (EmberCosmetics.Cosmetic c : EmberCosmetics.ALL) {
                List<String> who = new ArrayList<String>(wall.getStringList(c.id + ".names"));
                who.removeIf(town.sunshine.corerpg.p1.EmberMode::boardExcluded); // D102: no test / bot accounts
                lines.add(c.label + " §8· §7" + c.how + (who.isEmpty() ? " §8· 尚无人获得" : " §7· 首位 §f" + who.get(0) + " §7· 共 " + who.size() + " 人")); // D103: 9 entries, one line each
            }
            lines.add("§8装上：主菜单 → 荣誉与排行");
            replace(honors, loc(w, hs.getList("at")), lines);
        }
    }

    private static String medal(int i) { return i == 0 ? "§6① §f" : i == 1 ? "§7② §f" : i == 2 ? "§c③ §f" : "§7" + (i + 1) + ". §f"; }

    /** honors wall: online players who hold a cosmetic get added once (first come stays first) */
    private void recordOnline(EmberRunService runs) {
        if (runs == null || plugin.getDataStore() == null) return;
        boolean dirty = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (town.sunshine.corerpg.p1.EmberMode.boardExcluded(p.getName())) continue; // D102
            PlayerData d = plugin.getDataStore().get(p.getUniqueId());
            if (d == null) continue;
            int best = runs.abyssBest(d);
            for (EmberCosmetics.Cosmetic c : EmberCosmetics.ALL) {
                if (!EmberCosmetics.earned(d, best, c)) continue;
                List<String> ids = wall.getStringList(c.id + ".uuids");
                if (ids.contains(p.getUniqueId().toString())) continue;
                List<String> names = wall.getStringList(c.id + ".names");
                ids.add(p.getUniqueId().toString());
                names.add(p.getName());
                wall.set(c.id + ".uuids", ids);
                wall.set(c.id + ".names", names);
                dirty = true;
            }
        }
        if (dirty) {
            try { wallFile.getParentFile().mkdirs(); wall.save(wallFile); }
            catch (Exception e) { plugin.getLogger().warning("[hub ambience] honors-wall save: " + e.getMessage()); }
        }
    }

    // ------------------------------------------------------------------ talk + particles

    void talk() {
        World w = Bukkit.getWorld(WORLD);
        ConfigurationSection n = cfg == null ? null : cfg.getConfigurationSection("npcs");
        if (w == null || n == null || w.getPlayers().isEmpty()) return;
        List<String> near = new ArrayList<String>();
        for (String k : n.getKeys(false)) {
            Location at = loc(w, n.getList(k + ".at"));
            if (at == null || talking.containsKey(k)) continue;
            for (Player p : w.getPlayers()) if (p.getLocation().distanceSquared(at) <= 14 * 14) { near.add(k); break; }
        }
        if (near.isEmpty()) return;
        final String k = near.get(ThreadLocalRandom.current().nextInt(near.size()));
        List<String> lines = n.getStringList(k + ".lines");
        if (lines.isEmpty()) return;
        EmberRunService runs = plugin.getEmberRuns();
        String line = lines.get(ThreadLocalRandom.current().nextInt(lines.size())).replace("{featured}", runs == null ? "—" : runs.featuredShort());
        Location at = loc(w, n.getList(k + ".at"));
        boolean clerk = "clerk".equals(k); // the dungeon header already sits above the clerk's name
        final ArmorStand a = spawn(at.clone().add(0, clerk ? 3.75 : 2.75, 0), "§f「" + line + "」");
        talking.put(k, a);
        Bukkit.getScheduler().runTaskLater(plugin, () -> { a.remove(); live.remove(a.getUniqueId()); talking.remove(k); },
                Math.max(2, cfg.getInt("talk_show_s", 6)) * 20L);
    }

    void particles() {
        World w = Bukkit.getWorld(WORLD);
        if (w == null || w.getPlayers().isEmpty()) return;
        tick++;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        // forge: furnace fire + smoke, anvil sparks
        w.spawnParticle(Particle.FLAME, new Location(w, -8.5, 59.05, 105.5), 2, 0.2, 0.05, 0.2, 0.005);
        w.spawnParticle(Particle.SMOKE_NORMAL, new Location(w, -8.5, 59.3, 105.5), 1, 0.15, 0.1, 0.15, 0.01);
        if (tick % 6 == 0) w.spawnParticle(Particle.LAVA, new Location(w, -8.5, 59.1, 105.5), 1, 0.1, 0.0, 0.1, 0);
        if (tick % 4 == 0) w.spawnParticle(Particle.CRIT, new Location(w, -8.5, 59.1, 104.5), 6, 0.15, 0.1, 0.15, 0.25);
        // dungeon gate: portal swirl around the clerk
        w.spawnParticle(Particle.PORTAL, new Location(w, -14.5, 59.0, 110.5), 10, 0.5, 0.8, 0.5, 0.3);
        // 灰烛: candle glow over the guide's pad
        if (tick % 2 == 0) w.spawnParticle(Particle.ENCHANTMENT_TABLE, new Location(w, -16.5, 59.6, 106.5), 4, 0.4, 0.5, 0.4, 0.4);
        // drifting embers over the plaza
        for (int i = 0; i < 3; i++)
            w.spawnParticle(Particle.FLAME, new Location(w, -27.5 + r.nextDouble() * 18, 58.3 + r.nextDouble() * 2.5, 100.5 + r.nextDouble() * 16), 0, 0, 0.6, 0, 0.02);
        if (tick % 3 == 0) w.spawnParticle(Particle.SMOKE_LARGE, new Location(w, -27.5 + r.nextDouble() * 18, 58.2, 100.5 + r.nextDouble() * 16), 0, 0, 1, 0, 0.02);
        // board + showcase sparkle
        if (tick % 2 == 0) {
            w.spawnParticle(Particle.END_ROD, new Location(w, -24.5, 61.6, 109.0), 1, 0.5, 0.2, 0.3, 0.01);
            w.spawnParticle(Particle.SPELL_WITCH, new Location(w, -24.5, 62.9, 112.6), 2, 0.5, 0.3, 0.3, 0);
        }
    }

    // ------------------------------------------------------------------ stands

    @EventHandler
    public void onChunk(ChunkLoadEvent e) {
        if (WORLD.equals(e.getWorld().getName())) purge(java.util.Arrays.asList(e.getChunk().getEntities()));
    }

    private void purge(Iterable<? extends Entity> es) {
        for (Entity e : es)
            if (e.getType() == EntityType.ARMOR_STAND && e.getScoreboardTags().contains(TAG) && !live.contains(e.getUniqueId())) e.remove();
    }

    private void replace(List<ArmorStand> into, Location top, List<String> lines) {
        if (top == null) return;
        if (into.size() == lines.size()) { // same shape → rename in place, no flicker
            for (int i = 0; i < lines.size(); i++) if (!lines.get(i).equals(into.get(i).getCustomName())) into.get(i).setCustomName(lines.get(i));
            return;
        }
        for (ArmorStand a : into) { a.remove(); live.remove(a.getUniqueId()); }
        into.clear();
        spawnLines(top, lines, into);
    }

    private void spawnLines(Location top, List<String> lines, List<ArmorStand> into) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty()) continue;
            into.add(spawn(top.clone().subtract(0, i * LINE, 0), lines.get(i)));
        }
    }

    private ArmorStand spawn(Location at, String name) {
        ArmorStand a = (ArmorStand) at.getWorld().spawnEntity(at, EntityType.ARMOR_STAND);
        a.setVisible(false);
        a.setMarker(true);
        a.setSmall(true);
        a.setGravity(false);
        a.setInvulnerable(true);
        a.setBasePlate(false);
        a.setCollidable(false);
        a.setSilent(true);
        a.setCustomName(name);
        a.setCustomNameVisible(true);
        a.addScoreboardTag(TAG);
        live.add(a.getUniqueId());
        return a;
    }

    private static Location loc(World w, Object o) {
        if (!(o instanceof List) || ((List<?>) o).size() < 3) return null;
        List<?> l = (List<?>) o;
        try {
            return new Location(w, Double.parseDouble(String.valueOf(l.get(0))), Double.parseDouble(String.valueOf(l.get(1))), Double.parseDouble(String.valueOf(l.get(2))));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<String> strings(Object o) {
        List<String> out = new ArrayList<String>();
        if (o instanceof List) for (Object x : (List<?>) o) out.add(String.valueOf(x));
        return out;
    }
}
