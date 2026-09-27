package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * S3 (2026-09-28): ember_daily_spire 余烬窟·断塔回廊 —
 * 向上攀塔 + 中层环廊 + 顶台 Boss（有栏）；≥2 真铁栅门；坠落实底可回爬。
 * Admin: /corerpg spirebuild
 *
 * Coords:
 *   spawn 0,64,0 (tower bottom, face +Z / up-stair)
 *   bottom hall y=64; door1 seals stair up at z=12
 *   mid ring y=70; door2 seals stair to top at z=12 (mid plane)
 *   boss top y=76 center (0,76,0) + iron rail — open sky
 */
public class DailySpireService {

    static final String WORK_WORLD = "ember_daily_spire_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_spire";
    static final String MV_WORLD = "ember_daily_spire";

    static final int BOTTOM_Y = 64;
    static final int MID_Y = 70;
    static final int TOP_Y = 76;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    static final int X0 = -9;
    static final int X1 = 9;
    static final int Z0 = -9;
    static final int Z1 = 9;

    /** Bottom hall ≈14×14 */
    static final int BH_HALF = 7;

    /** Door seals stair mouth (north of center on each floor) */
    static final int DOOR_Z = 4;
    static final int DOOR_X0 = -1, DOOR_X1 = 1;

    /** Mid ring: outer r≈7, corridor width 2 → inner hole r≈4 */
    static final int RING_OUTER = 7;
    static final int RING_INNER = 4;

    /** Top platform r≈5 + rail */
    static final int TOP_R = 5;

    /** Stair shaft north of door */
    static final int STAIR_Z0 = 5;
    static final int STAIR_Z1 = 7;

    /** Spawn pads */
    static final int W1A_X = -5, W1A_Z = -3;
    static final int W1B_X = 5, W1B_Z = -3;
    static final int W1C_X = -5, W1C_Z = 2;
    static final int W1D_X = 5, W1D_Z = 2;
    static final int W2A_X = -6, W2A_Z = 0;
    static final int W2B_X = 6, W2B_Z = 0;
    static final int W2C_X = 0, W2C_Z = -6;
    static final int W2D_X = 0, W2D_Z = 6;

    static final int BOSS_X = 0, BOSS_Z = 0;

    private final CoreRpgPlugin plugin;

    public DailySpireService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "spirebuild".equals(a) || "spire".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg spirebuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "[spirebuild] S3 断塔回廊 · 上攀+环廊+顶台");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + BOTTOM_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  bottom y=" + BOTTOM_Y + " · door1 z=" + DOOR_Z + "@y" + BOTTOM_Y);
        sender.sendMessage(ChatColor.GRAY + "  mid y=" + MID_Y + " ring outer=" + RING_OUTER + " inner=" + RING_INNER
                + " · door2 z=" + DOOR_Z + "@y" + MID_Y);
        sender.sendMessage(ChatColor.GRAY + "  top y=" + TOP_Y + " center=(" + BOSS_X + "," + TOP_Y + "," + BOSS_Z + ")"
                + " r=" + TOP_R + " + rail");
        sender.sendMessage(ChatColor.GRAY + "  w1A=(" + W1A_X + "," + BOTTOM_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + BOTTOM_Y + "," + W1B_Z + ")"
                + " w1C=(" + W1C_X + "," + BOTTOM_Y + "," + W1C_Z + ")"
                + " w1D=(" + W1D_X + "," + BOTTOM_Y + "," + W1D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w2A=(" + W2A_X + "," + MID_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + MID_Y + "," + W2B_Z + ")"
                + " w2C=(" + W2C_X + "," + MID_Y + "," + W2C_Z + ")"
                + " w2D=(" + W2D_X + "," + MID_Y + "," + W2D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  boss=(" + BOSS_X + "," + TOP_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[spirebuild] map missing: " + mapDir.getAbsolutePath());
            return true;
        }
        File workDir = new File(serverRoot, WORK_WORLD);
        try {
            World existing = Bukkit.getWorld(WORK_WORLD);
            if (existing != null) Bukkit.unloadWorld(existing, false);
            if (workDir.exists()) deleteRecursive(workDir.toPath());
            copyRecursive(mapDir.toPath(), workDir.toPath());
            File lock = new File(workDir, "session.lock");
            if (lock.exists()) lock.delete();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "[spirebuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[spirebuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[spirebuild] S3 断塔回廊施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[spirebuild] scrub → 塔底大厅");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildBottomHall(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[spirebuild] 塔底 → 上阶1+中层环廊");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildStairBottomToMid(w);
                        changed += buildMidRing(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[spirebuild] 中层 → 上阶2+顶台");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildStairMidToTop(w);
                        changed += buildTopPlatform(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[spirebuild] 顶台 → 门+灯火告示");
                        return;
                    }
                    if (phase == 4) {
                        changed += placeDoors(w);
                        changed += placeLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        changed += placeDoors(w);
                        changed += buildFallSafety(w);
                        phase = 5;
                        return;
                    }
                    if (phase == 5) {
                        cancel();
                        w.setSpawnLocation(SPAWN_X, BOTTOM_Y, SPAWN_Z);
                        w.save();
                        for (org.bukkit.Chunk c : w.getLoadedChunks()) c.unload(true);
                        boolean unloaded = Bukkit.unloadWorld(w, true);
                        try {
                            exportTo(mapDir, workDir);
                            File mvDir = new File(serverRoot, MV_WORLD);
                            exportMvWorld(mvDir, workDir);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.RED + "[spirebuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[spirebuild] spire done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + BOTTOM_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR_Z + "@y" + BOTTOM_Y
                                + " · door2z=" + DOOR_Z + "@y" + MID_Y
                                + " · boss=(" + BOSS_X + "," + TOP_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[spirebuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("spirebuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    private void exportTo(File mapDir, File workDir) throws IOException {
        File mapRegion = new File(mapDir, "region");
        File workRegion = new File(workDir, "region");
        if (!mapRegion.exists()) mapRegion.mkdirs();
        File[] mcas = workRegion.listFiles();
        if (mcas != null) {
            for (File f : mcas) {
                if (f.getName().endsWith(".mca") && !f.getName().contains(".bak")) {
                    Files.copy(f.toPath(), new File(mapRegion, f.getName()).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        File ld = new File(workDir, "level.dat");
        if (ld.exists()) {
            Files.copy(ld.toPath(), new File(mapDir, "level.dat").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void exportMvWorld(File mvDir, File workDir) throws IOException {
        World existing = Bukkit.getWorld(MV_WORLD);
        if (existing != null) Bukkit.unloadWorld(existing, false);
        if (mvDir.exists()) deleteRecursive(mvDir.toPath());
        copyRecursive(workDir.toPath(), mvDir.toPath());
        File lock = new File(mvDir, "session.lock");
        if (lock.exists()) lock.delete();
        File uid = new File(mvDir, "uid.dat");
        if (uid.exists()) uid.delete();
    }

    private int scrub(World w) {
        int n = 0;
        for (int x = X0 - 3; x <= X1 + 3; x++) {
            for (int z = Z0 - 3; z <= Z1 + 3; z++) {
                for (int y = 50; y <= 90; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // solid catch floor under tower (fall safety — no void)
                n += set(w.getBlockAt(x, 49, z), Material.STONE);
                n += set(w.getBlockAt(x, 48, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildBottomHall(World w) {
        int n = 0;
        int fy = BOTTOM_Y - 1;
        // outer shell
        for (int x = -BH_HALF - 1; x <= BH_HALF + 1; x++) {
            for (int z = -BH_HALF - 1; z <= BH_HALF + 1; z++) {
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, BOTTOM_Y + y, z), Material.SMOOTH_BRICK);
                }
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
            }
        }
        // hollow hall
        for (int x = -BH_HALF; x <= BH_HALF; x++) {
            for (int z = -BH_HALF; z <= BH_HALF; z++) {
                n += set(w.getBlockAt(x, fy, z), floorBottom(x, z));
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, BOTTOM_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, BOTTOM_Y + 5, z), Material.SMOOTH_BRICK);
            }
        }
        // wood beam accents on ceiling
        for (int x = -BH_HALF; x <= BH_HALF; x += 3) {
            for (int z = -BH_HALF; z <= BH_HALF; z++) {
                n += set(w.getBlockAt(x, BOTTOM_Y + 4, z), Material.LOG);
            }
        }
        // pillars
        int[][] pillars = {{-5, -5}, {5, -5}, {-5, 3}, {5, 3}, {0, -5}};
        for (int[] p : pillars) {
            n += pillar(w, p[0], BOTTOM_Y, p[1]);
        }
        // door1 partition at z=DOOR_Z (seals north stair)
        for (int x = -BH_HALF - 1; x <= BH_HALF + 1; x++) {
            for (int y = 0; y <= 5; y++) {
                n += set(w.getBlockAt(x, BOTTOM_Y + y, DOOR_Z), Material.SMOOTH_BRICK);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, BOTTOM_Y + y, DOOR_Z), Material.AIR);
            }
            n += set(w.getBlockAt(x, BOTTOM_Y + 3, DOOR_Z), Material.SMOOTH_BRICK);
        }
        // spawn clear
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                n += set(w.getBlockAt(SPAWN_X + dx, fy, SPAWN_Z + dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(SPAWN_X + dx, BOTTOM_Y, SPAWN_Z + dz), Material.AIR);
                n += set(w.getBlockAt(SPAWN_X + dx, BOTTOM_Y + 1, SPAWN_Z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int buildStairBottomToMid(World w) {
        int n = 0;
        // stair shaft z=5..7, climb BOTTOM_Y → MID_Y (6)
        for (int step = 0; step < 7; step++) {
            int z = STAIR_Z0 + (step % 3); // zig within shaft
            if (step < 3) z = STAIR_Z0;
            else if (step < 5) z = STAIR_Z0 + 1;
            else z = STAIR_Z1;
            int yFeet = BOTTOM_Y + step;
            if (yFeet > MID_Y) yFeet = MID_Y;
            // actually rise one per step along +z then wrap — simpler: step along z then y
        }
        // linear stair: each step +1z and +1y from door plane
        for (int step = 0; step < 7; step++) {
            int z = STAIR_Z0 + Math.min(step, 2); // stay in shaft z=5..7
            // better: 6 steps in z progressing north then land
            z = STAIR_Z0 + (step <= 2 ? step : 2);
            int yFeet = BOTTOM_Y + step;
            if (yFeet > MID_Y) yFeet = MID_Y;
            // Use dedicated layout: step index maps z and y
        }
        // Clean implementation: 6 ascending steps along +Z
        for (int step = 0; step < 6; step++) {
            int z = STAIR_Z0 + step;
            if (z > STAIR_Z1 + 2) z = STAIR_Z1 + 2;
            int yFeet = BOTTOM_Y + step;
            if (yFeet > MID_Y) yFeet = MID_Y;
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, yFeet - 1, z), Material.SMOOTH_BRICK);
                for (int y = yFeet; y <= MID_Y + 4; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // side walls / beams
                for (int y = BOTTOM_Y - 1; y <= MID_Y + 4; y++) {
                    n += set(w.getBlockAt(DOOR_X0 - 1, y, z), Material.COBBLESTONE);
                    n += set(w.getBlockAt(DOOR_X1 + 1, y, z), Material.COBBLESTONE);
                }
                // riser
                if (step < 5) {
                    n += set(w.getBlockAt(x, yFeet, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, yFeet + 1, z), Material.AIR);
                    n += set(w.getBlockAt(x, yFeet + 2, z), Material.AIR);
                }
            }
            // rail on stairs
            n += set(w.getBlockAt(DOOR_X0 - 1, yFeet, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X1 + 1, yFeet, z), Material.IRON_FENCE);
        }
        // landing at mid south of ring entry
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = STAIR_Z0 + 5; z <= STAIR_Z0 + 6; z++) {
                n += set(w.getBlockAt(x, MID_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildMidRing(World w) {
        int n = 0;
        int fy = MID_Y - 1;
        // ring corridor between RING_INNER and RING_OUTER
        for (int dx = -RING_OUTER - 1; dx <= RING_OUTER + 1; dx++) {
            for (int dz = -RING_OUTER - 1; dz <= RING_OUTER + 1; dz++) {
                int x = dx;
                int z = dz;
                int r2 = dx * dx + dz * dz;
                int rOut = (RING_OUTER + 1) * (RING_OUTER + 1);
                int rOuter = RING_OUTER * RING_OUTER;
                int rInner = RING_INNER * RING_INNER;
                if (r2 > rOut) continue;

                if (r2 > rOuter) {
                    // outer wall
                    for (int y = 0; y <= 4; y++) {
                        Material m = ((dx + dz) & 1) == 0 ? Material.SMOOTH_BRICK : Material.COBBLESTONE;
                        n += set(w.getBlockAt(x, MID_Y + y, z), m);
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                    continue;
                }
                if (r2 >= rInner) {
                    // walkable ring
                    n += set(w.getBlockAt(x, fy, z), floorMid(x, z));
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                    }
                    // partial ceiling / beams
                    if ((dx + dz) % 3 == 0) {
                        n += set(w.getBlockAt(x, MID_Y + 3, z), Material.LOG);
                    } else {
                        n += set(w.getBlockAt(x, MID_Y + 4, z), Material.SMOOTH_BRICK);
                    }
                    // gap rail toward inner hole (visible drop, safe)
                    int rInnerEdge = (RING_INNER) * (RING_INNER);
                    // approximate: if just outside inner
                    if (r2 < (RING_INNER + 1) * (RING_INNER + 1) && r2 >= rInner) {
                        n += set(w.getBlockAt(x, MID_Y, z), Material.IRON_FENCE);
                        // but need walkable floor — put rail on inner-facing cells only
                    }
                    continue;
                }
                // inner hole: open air looking down — catch platform below already at y=49
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                }
                // no floor in hole
                n += set(w.getBlockAt(x, fy, z), Material.AIR);
            }
        }
        // fix: clear rails that block walk — place rail only on cells adjacent to hole
        for (int dx = -RING_OUTER; dx <= RING_OUTER; dx++) {
            for (int dz = -RING_OUTER; dz <= RING_OUTER; dz++) {
                int r2 = dx * dx + dz * dz;
                if (r2 < RING_INNER * RING_INNER || r2 > RING_OUTER * RING_OUTER) continue;
                // neighbor toward center is hole?
                boolean nearHole = false;
                int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                for (int[] d : dirs) {
                    int ndx = dx + d[0], ndz = dz + d[1];
                    if (ndx * ndx + ndz * ndz < RING_INNER * RING_INNER) {
                        nearHole = true;
                        break;
                    }
                }
                if (nearHole) {
                    // floor solid, rail one block up on hole side — actually put fence ON the floor cell edge by replacing air at feet with fence only if we keep path width
                    // Keep floor; place fence at MID_Y on the cell (player can walk around)
                    // Better: don't put fence on walk cell — put fence in the hole-adjacent air at same Y standing as barrier one step into hole
                    int hx = dx, hz = dz;
                    // step one toward 0
                    int sx = Integer.signum(-dx);
                    int sz = Integer.signum(-dz);
                    int fx = dx + sx, fz = dz + sz;
                    if (fx * fx + fz * fz < RING_INNER * RING_INNER) {
                        n += set(w.getBlockAt(fx, MID_Y, fz), Material.IRON_FENCE);
                        n += set(w.getBlockAt(fx, MID_Y - 1, fz), Material.SMOOTH_BRICK); // ledge
                    }
                }
                // ensure walk floor
                n += set(w.getBlockAt(dx, fy, dz), floorMid(dx, dz));
                n += set(w.getBlockAt(dx, MID_Y, dz), Material.AIR);
                n += set(w.getBlockAt(dx, MID_Y + 1, dz), Material.AIR);
            }
        }
        // door2 partition on mid ring at z=DOOR_Z (north, seals up-stair)
        for (int x = -RING_OUTER - 1; x <= RING_OUTER + 1; x++) {
            int z = DOOR_Z;
            // only across the ring band
            if (x * x + z * z > (RING_OUTER + 1) * (RING_OUTER + 1)) continue;
            if (x * x + z * z < RING_INNER * RING_INNER) continue;
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, z), Material.SMOOTH_BRICK);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, DOOR_Z), Material.AIR);
            }
            n += set(w.getBlockAt(x, MID_Y + 3, DOOR_Z), Material.SMOOTH_BRICK);
        }
        // connect stair landing into ring from north
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = 5; z <= 7; z++) {
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildStairMidToTop(World w) {
        int n = 0;
        // from mid door north, climb MID_Y → TOP_Y
        for (int step = 0; step < 6; step++) {
            int z = STAIR_Z0 + step;
            int yFeet = MID_Y + step;
            if (yFeet > TOP_Y) yFeet = TOP_Y;
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, yFeet - 1, z), Material.SMOOTH_BRICK);
                for (int y = yFeet; y <= TOP_Y + 3; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                for (int y = MID_Y - 1; y <= TOP_Y + 3; y++) {
                    n += set(w.getBlockAt(DOOR_X0 - 1, y, z), Material.COBBLESTONE);
                    n += set(w.getBlockAt(DOOR_X1 + 1, y, z), Material.COBBLESTONE);
                }
                if (step < 5) {
                    n += set(w.getBlockAt(x, yFeet, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, yFeet + 1, z), Material.AIR);
                    n += set(w.getBlockAt(x, yFeet + 2, z), Material.AIR);
                }
            }
            n += set(w.getBlockAt(DOOR_X0 - 1, yFeet, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X1 + 1, yFeet, z), Material.IRON_FENCE);
        }
        // approach onto top platform from north
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = STAIR_Z0 + 5; z <= TOP_R; z++) {
                n += set(w.getBlockAt(x, TOP_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, TOP_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildTopPlatform(World w) {
        int n = 0;
        int fy = TOP_Y - 1;
        for (int dx = -TOP_R - 1; dx <= TOP_R + 1; dx++) {
            for (int dz = -TOP_R - 1; dz <= TOP_R + 1; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                int r2 = dx * dx + dz * dz;
                int rPad = TOP_R * TOP_R;
                int rRail = (TOP_R + 1) * (TOP_R + 1);
                if (r2 > rRail) continue;
                if (r2 > rPad) {
                    // rail ring — iron fence on solid ledge
                    n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, TOP_Y, z), Material.IRON_FENCE);
                    n += set(w.getBlockAt(x, TOP_Y + 1, z), Material.IRON_FENCE);
                    continue;
                }
                n += set(w.getBlockAt(x, fy, z), floorTop(x, z));
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, TOP_Y + y, z), Material.AIR);
                }
                // open sky — no ceiling
            }
        }
        // open approach from stair (north) — punch rail
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            n += set(w.getBlockAt(x, fy, TOP_R), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, TOP_Y, TOP_R), Material.AIR);
            n += set(w.getBlockAt(x, TOP_Y + 1, TOP_R), Material.AIR);
            n += set(w.getBlockAt(x, fy, TOP_R + 1), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, TOP_Y, TOP_R + 1), Material.AIR);
        }
        // center boss pad
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, fy, BOSS_Z + dz), Material.COBBLESTONE);
                n += set(w.getBlockAt(BOSS_X + dx, TOP_Y, BOSS_Z + dz), Material.AIR);
            }
        }
        // wood beam cross on pad for visual
        n += set(w.getBlockAt(0, TOP_Y - 1, -3), Material.LOG);
        n += set(w.getBlockAt(0, TOP_Y - 1, 3), Material.LOG);
        n += set(w.getBlockAt(-3, TOP_Y - 1, 0), Material.LOG);
        n += set(w.getBlockAt(3, TOP_Y - 1, 0), Material.LOG);
        return n;
    }

    /** Catch nets / ledges under ring hole and outside tower so fall ≠ void death loop */
    private int buildFallSafety(World w) {
        int n = 0;
        // mid-height catch platform under inner hole at y=60
        for (int dx = -RING_INNER; dx <= RING_INNER; dx++) {
            for (int dz = -RING_INNER; dz <= RING_INNER; dz++) {
                if (dx * dx + dz * dz > RING_INNER * RING_INNER) continue;
                n += set(w.getBlockAt(dx, 59, dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(dx, 60, dz), Material.AIR);
                n += set(w.getBlockAt(dx, 61, dz), Material.AIR);
            }
        }
        // ladder back up from catch to bottom hall ceiling punch? — simple: stairs from catch to bottom via west
        for (int step = 0; step < 5; step++) {
            int y = 60 + step;
            int x = -RING_INNER - 1 - step;
            n += set(w.getBlockAt(x, y - 1, 0), Material.COBBLESTONE);
            n += set(w.getBlockAt(x, y, 0), Material.AIR);
            n += set(w.getBlockAt(x, y + 1, 0), Material.AIR);
            n += set(w.getBlockAt(x, y, 1), Material.IRON_FENCE);
            n += set(w.getBlockAt(x, y, -1), Material.IRON_FENCE);
        }
        // ground apron around tower at y=56 so outer fall lands soft
        for (int x = X0 - 2; x <= X1 + 2; x++) {
            for (int z = Z0 - 2; z <= Z1 + 2; z++) {
                if (Math.abs(x) <= BH_HALF && Math.abs(z) <= BH_HALF) continue;
                n += set(w.getBlockAt(x, 55, z), Material.GRASS);
                n += set(w.getBlockAt(x, 54, z), Material.DIRT);
            }
        }
        return n;
    }

    private int placeDoors(World w) {
        int n = 0;
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, BOTTOM_Y + y, DOOR_Z), Material.IRON_FENCE);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, DOOR_Z), Material.IRON_FENCE);
            }
        }
        return n;
    }

    private Material floorBottom(int x, int z) {
        int h = (x * 3 + z * 5) & 7;
        if (h == 0) return Material.COBBLESTONE;
        if (h == 1) return Material.MOSSY_COBBLESTONE;
        return Material.SMOOTH_BRICK;
    }

    private Material floorMid(int x, int z) {
        int h = (x * 7 + z) & 7;
        if (h <= 1) return Material.COBBLESTONE;
        if (h == 2) return Material.MOSSY_COBBLESTONE;
        return Material.SMOOTH_BRICK;
    }

    private Material floorTop(int x, int z) {
        int h = (x * 2 + z * 3) & 7;
        if (h == 0) return Material.COBBLESTONE;
        if (h == 1) return Material.WOOD;
        return Material.SMOOTH_BRICK;
    }

    private int pillar(World w, int x, int feetY, int z) {
        int n = 0;
        n += set(w.getBlockAt(x, feetY - 1, z), Material.SMOOTH_BRICK);
        for (int y = 0; y <= 3; y++) {
            n += set(w.getBlockAt(x, feetY + y, z), Material.COBBLESTONE);
        }
        n += set(w.getBlockAt(x, feetY + 4, z), Material.LOG);
        return n;
    }

    private int placeLights(World w) {
        int n = 0;
        int[][] bottom = {{-6, -6}, {6, -6}, {-6, 2}, {6, 2}, {0, -6}, {-3, 0}, {3, 0}};
        for (int[] p : bottom) {
            n += set(w.getBlockAt(p[0], BOTTOM_Y - 1, p[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], BOTTOM_Y, p[1]), Material.GLOWSTONE);
        }
        int[][] mid = {{-6, 0}, {6, 0}, {0, -6}, {0, 6}, {-5, -5}, {5, 5}};
        for (int[] p : mid) {
            n += set(w.getBlockAt(p[0], MID_Y - 1, p[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], MID_Y, p[1]), Material.GLOWSTONE);
        }
        int[][] top = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}, {0, 4}};
        for (int[] p : top) {
            n += set(w.getBlockAt(p[0], TOP_Y - 1, p[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], TOP_Y, p[1]), Material.GLOWSTONE);
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, BOTTOM_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, BOTTOM_Y, SPAWN_Z),
                "§7余烬窟·断塔", "§7塔底安全区", "§e清尽再上阶", "§8→ 北方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, BOTTOM_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, BOTTOM_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 1, BOTTOM_Y - 1, DOOR_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, BOTTOM_Y, DOOR_Z - 1),
                "§e上阶门", "§7清尽底层", "§7后铁门敞开", "§8↑ 中层环廊");
        n += set(w.getBlockAt(DOOR_X1 + 1, MID_Y - 1, DOOR_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, MID_Y, DOOR_Z - 1),
                "§e顶门", "§7清尽环廊", "§7后上顶台", "§8↑ Boss");
        n += set(w.getBlockAt(BOSS_X + 3, TOP_Y - 1, BOSS_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(BOSS_X + 3, TOP_Y, BOSS_Z),
                "§7顶台·守望", "§7有栏可绕", "§7通关回枢纽", "§8菜单亦可回");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] bottom = {
                {SPAWN_X, BOTTOM_Y, SPAWN_Z},
                {W1A_X, BOTTOM_Y, W1A_Z},
                {W1B_X, BOTTOM_Y, W1B_Z},
                {W1C_X, BOTTOM_Y, W1C_Z},
                {W1D_X, BOTTOM_Y, W1D_Z}
        };
        for (int[] p : bottom) {
            n += clearPad(w, p[0], p[1], p[2], BOTTOM_Y);
        }
        int[][] mid = {
                {W2A_X, MID_Y, W2A_Z},
                {W2B_X, MID_Y, W2B_Z},
                {W2C_X, MID_Y, W2C_Z},
                {W2D_X, MID_Y, W2D_Z}
        };
        for (int[] p : mid) {
            n += clearPad(w, p[0], p[1], p[2], MID_Y);
        }
        n += clearPad(w, BOSS_X, TOP_Y, BOSS_Z, TOP_Y);
        return n;
    }

    private int clearPad(World w, int x, int feetY, int z, int doorY) {
        int n = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int bx = x + dx;
                int bz = z + dz;
                if (bz == DOOR_Z && feetY == doorY && Math.abs(bx) <= 1) continue;
                Material floor = feetY == TOP_Y ? floorTop(bx, bz)
                        : feetY == MID_Y ? floorMid(bx, bz) : floorBottom(bx, bz);
                if (x == SPAWN_X && z == SPAWN_Z) floor = Material.SMOOTH_BRICK;
                if (x == BOSS_X && z == BOSS_Z && feetY == TOP_Y) floor = Material.COBBLESTONE;
                n += set(w.getBlockAt(bx, feetY - 1, bz), floor);
                n += set(w.getBlockAt(bx, feetY, bz), Material.AIR);
                n += set(w.getBlockAt(bx, feetY + 1, bz), Material.AIR);
            }
        }
        return n;
    }

    private static int writeSign(Block b, String l0, String l1, String l2, String l3) {
        b.setType(Material.SIGN_POST, false);
        if (b.getState() instanceof Sign) {
            Sign s = (Sign) b.getState();
            s.setLine(0, l0 == null ? "" : l0);
            s.setLine(1, l1 == null ? "" : l1);
            s.setLine(2, l2 == null ? "" : l2);
            s.setLine(3, l3 == null ? "" : l3);
            s.update(true, false);
            return 1;
        }
        return 0;
    }

    private static int set(Block b, Material m) {
        if (b.getType() == m) return 0;
        b.setType(m, false);
        return 1;
    }

    private static void copyRecursive(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path rel = dst.resolve(src.relativize(dir).toString());
                if (!Files.exists(rel)) Files.createDirectories(rel);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, dst.resolve(src.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteRecursive(Path root) throws IOException {
        if (!Files.exists(root)) return;
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
