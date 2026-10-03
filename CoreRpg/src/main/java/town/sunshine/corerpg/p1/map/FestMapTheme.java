package town.sunshine.corerpg.p1.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * D139: re-themes a copy of an existing DungeonPlus template (the base stays untouched) for a limited-time event —
 * a block-id palette swap only (same geometry, same walkable cells, same doors / points), e.g. the Q04 tide stone into
 * red / gold. Config (festival yml {@code map_theme:}): {@code box: [x0, y0, z0, x1, y1, z1]} and
 * {@code palette: {"98": "215", "98:1": "159:14", …}} ("id" or "id:data" → "id[:data]"). One x column per tick, then
 * the same export as {@link P1MapBuilder} (region + level.dat → plugins/DungeonPlus/map/&lt;name&gt;).
 */
public final class FestMapTheme {

    private final Plugin plugin;

    public FestMapTheme(Plugin plugin) { this.plugin = plugin; }

    @SuppressWarnings("deprecation")
    public void build(final CommandSender sender, final String name, final String baseMap, Map<?, ?> cfg) {
        final int[] box = new int[6];
        Object bo = cfg.get("box");
        if (!(bo instanceof List) || ((List<?>) bo).size() < 6) { sender.sendMessage(ChatColor.RED + "[festmap] map_theme.box missing"); return; }
        for (int i = 0; i < 6; i++) box[i] = ((Number) ((List<?>) bo).get(i)).intValue();
        final Map<Integer, int[]> pal = new HashMap<Integer, int[]>(); // key id*16+data (data −1 = any) → {id, data}
        if (cfg.get("palette") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) cfg.get("palette")).entrySet()) {
            int[] from = idData(String.valueOf(e.getKey())), to = idData(String.valueOf(e.getValue()));
            if (from == null || to == null) continue;
            pal.put(from[1] < 0 ? -(from[0] + 1) : from[0] * 16 + from[1], new int[]{to[0], Math.max(0, to[1])});
        }
        if (pal.isEmpty()) { sender.sendMessage(ChatColor.RED + "[festmap] map_theme.palette empty"); return; }
        final File root = plugin.getServer().getWorldContainer();
        final File base = new File(root, "plugins/DungeonPlus/map/" + baseMap);
        final File out = new File(root, "plugins/DungeonPlus/map/" + name);
        final String work = name + "_build";
        final File workDir = new File(root, work);
        if (!base.isDirectory()) { sender.sendMessage(ChatColor.RED + "[festmap] base map missing: " + base); return; }
        if (base.getAbsoluteFile().equals(out.getAbsoluteFile())) { sender.sendMessage(ChatColor.RED + "[festmap] base == output"); return; }
        try {
            World ex = Bukkit.getWorld(work);
            if (ex != null) Bukkit.unloadWorld(ex, false);
            deleteRecursive(workDir.toPath());
            copyRecursive(base.toPath(), workDir.toPath());
            new File(workDir, "session.lock").delete();
            new File(workDir, "uid.dat").delete();
        } catch (IOException e) { sender.sendMessage(ChatColor.RED + "[festmap] prepare failed: " + e.getMessage()); return; }
        WorldCreator wc = new WorldCreator(work);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) { sender.sendMessage(ChatColor.RED + "[festmap] createWorld failed"); return; }
        w.setAutoSave(false);
        final org.bukkit.Location spawn = w.getSpawnLocation();
        final Map<String, Integer> seen = new TreeMap<String, Integer>(), swapped = new TreeMap<String, Integer>();
        sender.sendMessage(ChatColor.YELLOW + "[festmap] " + baseMap + " → " + name + " · " + (box[3] - box[0] + 1) + " columns…");
        new BukkitRunnable() {
            int x = Math.min(box[0], box[3]);
            final int x1 = Math.max(box[0], box[3]), y0 = Math.min(box[1], box[4]), y1 = Math.max(box[1], box[4]),
                    z0 = Math.min(box[2], box[5]), z1 = Math.max(box[2], box[5]);
            long n;
            @Override public void run() {
                try {
                    if (x <= x1) {
                        for (int z = z0; z <= z1; z++) for (int y = y0; y <= y1; y++) {
                            Block b = w.getBlockAt(x, y, z);
                            int id = b.getTypeId();
                            if (id == 0) continue;
                            int dt = b.getData();
                            String k = id + ":" + dt;
                            seen.merge(k, 1, Integer::sum);
                            int[] to = pal.get(id * 16 + dt);
                            if (to == null) to = pal.get(-(id + 1));
                            if (to == null) continue;
                            b.setTypeIdAndData(to[0], (byte) to[1], false);
                            swapped.merge(k, 1, Integer::sum);
                            n++;
                        }
                        x++;
                        return;
                    }
                    cancel();
                    int removed = 0;
                    for (Entity e : w.getEntities()) if (!(e instanceof Player)) { e.remove(); removed++; }
                    w.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
                    w.save();
                    boolean unloaded = Bukkit.unloadWorld(w, true);
                    deleteRecursive(out.toPath());
                    new File(out, "region").mkdirs();
                    File[] mcas = new File(workDir, "region").listFiles();
                    if (mcas != null) for (File f : mcas)
                        if (f.getName().endsWith(".mca")) Files.copy(f.toPath(), new File(out, "region/" + f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(new File(workDir, "level.dat").toPath(), new File(out, "level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if (unloaded) deleteRecursive(workDir.toPath());
                    sender.sendMessage(ChatColor.GREEN + "[festmap] " + name + " done · swapped=" + n + " · entities removed=" + removed + " · unloaded=" + unloaded + " → " + out.getPath());
                    plugin.getLogger().info("[festmap] " + name + " seen " + seen);
                    plugin.getLogger().info("[festmap] " + name + " swapped " + swapped);
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[festmap] fail: " + t);
                    plugin.getLogger().warning("festmap fail: " + t);
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    /** "98" → {98, −1}; "98:1" → {98, 1}; null when unparsable */
    static int[] idData(String s) {
        try {
            String[] p = s.trim().split(":");
            return new int[]{Integer.parseInt(p[0].trim()), p.length > 1 ? Integer.parseInt(p[1].trim()) : -1};
        } catch (RuntimeException e) { return null; }
    }

    private static void copyRecursive(final Path src, final Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes a) throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                Files.copy(f, dst.resolve(src.relativize(f)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteRecursive(Path root) throws IOException {
        if (!Files.exists(root)) return;
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException { Files.delete(f); return FileVisitResult.CONTINUE; }
            @Override public FileVisitResult postVisitDirectory(Path d, IOException e) throws IOException { Files.delete(d); return FileVisitResult.CONTINUE; }
        });
    }
}
