package town.sunshine.corerpg.p1.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Builds a {@link P1MapLayout} into a DungeonPlus template (B2.151): copies a base map into a work world, rewrites one
 * x column per tick over the chunk-aligned box (stone / grass ground, raised floors with solid fill, stone-brick stairs
 * on every rise cell, 2-thick walls, ceilings, low guards = 1 solid + 1 iron fence, flush glowstone, decor outside the
 * walkable area; no signs, no entities), then exports region + level.dat to plugins/DungeonPlus/map/&lt;name&gt;.
 */
public final class P1MapBuilder {

    private static final int Y0 = 40, Y1 = 160;

    private final Plugin plugin;

    public P1MapBuilder(Plugin plugin) { this.plugin = plugin; }

    public void build(final CommandSender sender, final String name, final String baseMap, final P1MapLayout l) {
        if (!l.problems.isEmpty()) { sender.sendMessage(ChatColor.RED + "[mapbuild] layout problems: " + l.problems); return; }
        final File root = plugin.getServer().getWorldContainer();
        final File base = new File(root, "plugins/DungeonPlus/map/" + baseMap);
        final File out = new File(root, "plugins/DungeonPlus/map/" + name);
        final String work = name + "_build";
        final File workDir = new File(root, work);
        if (!base.isDirectory()) { sender.sendMessage(ChatColor.RED + "[mapbuild] base map missing: " + base); return; }
        try {
            World ex = Bukkit.getWorld(work);
            if (ex != null) Bukkit.unloadWorld(ex, false);
            deleteRecursive(workDir.toPath());
            copyRecursive(base.toPath(), workDir.toPath());
            new File(workDir, "session.lock").delete();
            new File(workDir, "uid.dat").delete();
        } catch (IOException e) { sender.sendMessage(ChatColor.RED + "[mapbuild] prepare failed: " + e.getMessage()); return; }
        WorldCreator wc = new WorldCreator(work);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) { sender.sendMessage(ChatColor.RED + "[mapbuild] createWorld failed"); return; }
        w.setAutoSave(false);
        final Plan plan = new Plan(l);
        sender.sendMessage(ChatColor.YELLOW + "[mapbuild] " + name + " · " + (l.boxX1 - l.boxX0 + 1) + " columns…");
        new BukkitRunnable() {
            int x = l.boxX0;
            long blocks;
            @Override public void run() {
                try {
                    if (x <= l.boxX1) {
                        for (int z = l.boxZ0; z <= l.boxZ1; z++) blocks += column(w, plan, x, z);
                        if ((x - l.boxX0) % 24 == 0) sender.sendMessage(ChatColor.GRAY + "[mapbuild] x=" + x);
                        x++;
                        return;
                    }
                    cancel();
                    int removed = 0;
                    for (Entity e : w.getEntities()) if (!(e instanceof Player)) { e.remove(); removed++; }
                    w.setSpawnLocation(l.spawnX, l.spawnF, l.spawnZ);
                    w.save();
                    boolean unloaded = Bukkit.unloadWorld(w, true);
                    deleteRecursive(out.toPath());
                    new File(out, "region").mkdirs();
                    File[] mcas = new File(workDir, "region").listFiles();
                    if (mcas != null) for (File f : mcas)
                        if (f.getName().endsWith(".mca")) Files.copy(f.toPath(), new File(out, "region/" + f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(new File(workDir, "level.dat").toPath(), new File(out, "level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    sender.sendMessage(ChatColor.GREEN + "[mapbuild] " + name + " done · blocks=" + blocks + " · entities removed=" + removed
                            + " · unloaded=" + unloaded + " · exported → " + out.getPath());
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[mapbuild] fail: " + t);
                    plugin.getLogger().warning("mapbuild fail: " + t);
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    /** Per-cell wall info precomputed from the layout (pure). */
    static final class Plan {
        final P1MapLayout l;
        Plan(P1MapLayout l) { this.l = l; }

        /** wall top y (inclusive) for a non-cavity cell within 2 of the cavity, or NONE; [1] = 1 if guard-only, [2] = light y */
        int[] wall(int x, int z) {
            int top = P1MapLayout.NONE, light = P1MapLayout.NONE;
            boolean guardOnly = true, inner = false;
            for (int dx = -2; dx <= 2; dx++)
                for (int dz = -2; dz <= 2; dz++) {
                    P1MapLayout.Cell c = l.cell(x + dx, z + dz);
                    if (c == null || c.f == P1MapLayout.NONE) continue;
                    boolean near = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                    if (c.lowGuard) {
                        top = Math.max(top, c.f + (near ? 1 : 0)); // inner ring: solid at F + fence at F+1
                        if (near) inner = true;
                    } else {
                        guardOnly = false;
                        top = Math.max(top, c.f + c.h);
                        if (Math.abs(dx) + Math.abs(dz) == 1 && Math.floorMod(x + z, 6) == 0) light = Math.max(light, c.f + 3);
                    }
                }
            if (top == P1MapLayout.NONE) return null;
            return new int[] {top, guardOnly ? (inner ? 1 : 2) : 0, light};
        }
    }

    // block ids / data (1.12)
    private static final int AIR = 0, STONE = 1, GRASS = 2, DIRT = 3, COBBLE = 4, LOG = 17, GLOW = 89, BRICK = 98, BARS = 101, STAIRS = 109, MOSSY = 48;

    @SuppressWarnings("deprecation")
    private static int column(World w, Plan plan, int x, int z) {
        P1MapLayout l = plan.l;
        int[] id = new int[Y1 - Y0 + 1];
        byte[] data = new byte[id.length];
        int h = (x * 73856093) ^ (z * 19349663);
        // ground
        for (int y = Y0; y <= l.ground; y++) id[y - Y0] = y == l.ground ? GRASS : (y >= l.ground - 3 ? DIRT : STONE);
        P1MapLayout.Cell c = l.cell(x, z);
        if (c != null && c.f != P1MapLayout.NONE) {
            for (int y = l.ground; y <= c.f - 2; y++) { id[y - Y0] = STONE; data[y - Y0] = 0; }
            int fy = c.f - 1;
            boolean room = c.owner.startsWith("R") || c.owner.startsWith("E");
            if (c.stair != 0) { id[fy - Y0] = STAIRS; data[fy - Y0] = (byte) (c.stair == 1 ? 0 : c.stair == 2 ? 1 : c.stair == 3 ? 2 : 3); }
            else if (room && Math.floorMod(x, 6) == 0 && Math.floorMod(z, 6) == 0) id[fy - Y0] = GLOW;
            else { id[fy - Y0] = BRICK; int r = Math.floorMod(h, 11); data[fy - Y0] = (byte) (r == 0 ? 2 : r == 1 ? 1 : 0); }
            if (c.roofed) {
                int cy = c.f + c.h;
                if (cy <= Y1) {
                    boolean lamp = Math.floorMod(x, 4) == 0 && Math.floorMod(z, 4) == 0;
                    id[cy - Y0] = lamp ? GLOW : (Math.floorMod(c.owner.startsWith("C") ? 0 : x, 5) == 0 && !c.owner.startsWith("C") ? LOG : BRICK);
                    data[cy - Y0] = (byte) (id[cy - Y0] == LOG ? 1 : 0);
                    if (cy + 1 <= Y1) { id[cy + 1 - Y0] = BRICK; data[cy + 1 - Y0] = 0; }
                }
            }
        } else {
            int[] wl = plan.wall(x, z);
            if (wl != null) {
                int top = Math.min(wl[0], Y1);
                for (int y = l.ground; y <= top; y++) {
                    int r = Math.floorMod(h + y * 31, 13);
                    id[y - Y0] = r == 0 ? COBBLE : r == 1 ? MOSSY : BRICK;
                    data[y - Y0] = (byte) (id[y - Y0] == BRICK && r == 2 ? 2 : 0);
                }
                if (wl[1] == 1) id[top - Y0] = BARS; // inner guard ring: solid at F, iron fence at F+1
                if (wl[2] != P1MapLayout.NONE && wl[2] <= top) { id[wl[2] - Y0] = GLOW; data[wl[2] - Y0] = 0; }
            }
            for (int[] d : l.decor) decor(d, x, z, id, data);
        }
        int n = 0;
        for (int y = Y0; y <= Y1; y++) {
            Block b = w.getBlockAt(x, y, z);
            int i = id[y - Y0];
            byte dt = data[y - Y0];
            if (b.getTypeId() == i && b.getData() == dt) continue;
            b.setTypeIdAndData(i, dt, false);
            n++;
        }
        return n;
    }

    private static void decor(int[] d, int x, int z, int[] id, byte[] data) {
        int y0 = d[5], y1 = Math.min(d[6], Y1);
        if (d[0] == 0) {
            if (x < d[1] || x > d[2] || z < d[3] || z > d[4]) return;
            for (int y = y0; y <= y1; y++) {
                boolean crenelGap = y == y1 && Math.floorMod(x, 2) == 1;
                boolean window = Math.floorMod(x, 4) == 0 && y >= y0 + 24 && y <= y0 + 34 && Math.floorMod(y, 5) != 0;
                id[y - Y0] = crenelGap ? AIR : window ? BARS : BRICK;
                data[y - Y0] = 0;
            }
        } else if (d[0] == 2) {
            if (x < d[1] || x > d[2] || z < d[3] || z > d[4] || Math.floorMod(z, 2) != 0) return;
            for (int y = y0; y <= y1; y++) if (id[y - Y0] != AIR) { id[y - Y0] = BARS; data[y - Y0] = 0; }
        } else if (d[0] == 1) {
            int dx = x - d[1], dz = z - d[3], r = d[2];
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > r + 0.5) return;
            boolean shell = dist > r - 0.5;
            for (int y = y0; y <= y1; y++) {
                if (shell) { boolean gap = y == y1 && Math.floorMod(x + z, 2) == 1; id[y - Y0] = gap ? AIR : BRICK; data[y - Y0] = (byte) (Math.floorMod(y, 7) == 0 ? 2 : 0); }
                else if (y == y0 || y == y1 - 1) { id[y - Y0] = BRICK; data[y - Y0] = 0; } // capped, not enterable
                else id[y - Y0] = AIR;
            }
        }
    }

    private static void copyRecursive(final Path src, final Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes a) throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                Files.copy(f, dst.resolve(src.relativize(f).toString()), StandardCopyOption.REPLACE_EXISTING);
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
