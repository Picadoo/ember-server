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
 * S2 (2026-09-28): ember_daily_crypt 余烬窟·残誓地窖 —
 * 多层下阶 + 中厅 + 底层圆厅；≥2 真铁栅门。
 * Admin: /corerpg cryptbuild
 *
 * Coords:
 *   spawn 0,72,0 (well platform, face +Z / down-stair)
 *   upper hall y=72; door1 seals stair to mid
 *   mid hall y=66; door2 seals stair to boss
 *   boss round y=60 center (0,60,48) + ambulatory ring
 */
public class DailyCryptService {

    static final String WORK_WORLD = "ember_daily_crypt_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_crypt";
    static final String MV_WORLD = "ember_daily_crypt";

    static final int UPPER_Y = 72;
    static final int MID_Y = 66;
    static final int BOSS_Y = 60;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    static final int X0 = -10;
    static final int X1 = 9;
    static final int Z0 = -4;
    static final int Z1 = 60;

    /** Upper hall */
    static final int UH_X0 = -8, UH_X1 = 7;
    static final int UH_Z0 = 2, UH_Z1 = 17;

    /** Door1: seals stair mouth at upper → mid (z=18 plane on upper) */
    static final int DOOR1_Z = 18;
    static final int DOOR_X0 = -1, DOOR_X1 = 1;

    /** Mid hall */
    static final int MH_X0 = -7, MH_X1 = 6;
    static final int MH_Z0 = 26, MH_Z1 = 39;

    /** Door2: seals mid → boss stair at z=40 */
    static final int DOOR2_Z = 40;

    /** Boss round */
    static final int BOSS_X = 0, BOSS_Z = 48;
    static final int BOSS_R = 6;      // inner hall radius
    static final int RING_R = 8;      // ambulatory outer

    /** Spawn pads */
    static final int W1A_X = -6, W1A_Z = 6;
    static final int W1B_X = 5, W1B_Z = 6;
    static final int W1C_X = -6, W1C_Z = 14;
    static final int W1D_X = 5, W1D_Z = 14;
    static final int W2A_X = -5, W2A_Z = 30;
    static final int W2B_X = 4, W2B_Z = 30;
    static final int W2C_X = -5, W2C_Z = 36;
    static final int W2D_X = 4, W2D_Z = 36;

    private final CoreRpgPlugin plugin;

    public DailyCryptService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "cryptbuild".equals(a) || "crypt".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg cryptbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GRAY + "[cryptbuild] S2 残誓地窖 · 三层下阶+圆厅");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + UPPER_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  upper y=" + UPPER_Y + " hall z=" + UH_Z0 + ".." + UH_Z1
                + " · door1 z=" + DOOR1_Z);
        sender.sendMessage(ChatColor.GRAY + "  mid y=" + MID_Y + " hall z=" + MH_Z0 + ".." + MH_Z1
                + " · door2 z=" + DOOR2_Z);
        sender.sendMessage(ChatColor.GRAY + "  boss y=" + BOSS_Y + " center=(" + BOSS_X + "," + BOSS_Y + "," + BOSS_Z + ")"
                + " r=" + BOSS_R + " ring=" + RING_R);
        sender.sendMessage(ChatColor.GRAY + "  w1A=(" + W1A_X + "," + UPPER_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + UPPER_Y + "," + W1B_Z + ")"
                + " w1C=(" + W1C_X + "," + UPPER_Y + "," + W1C_Z + ")"
                + " w1D=(" + W1D_X + "," + UPPER_Y + "," + W1D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w2A=(" + W2A_X + "," + MID_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + MID_Y + "," + W2B_Z + ")"
                + " w2C=(" + W2C_X + "," + MID_Y + "," + W2C_Z + ")"
                + " w2D=(" + W2D_X + "," + MID_Y + "," + W2D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  boss=(" + BOSS_X + "," + BOSS_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[cryptbuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[cryptbuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[cryptbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[cryptbuild] S2 残誓地窖施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[cryptbuild] scrub → 井口+上层厅");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildWellAndUpper(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[cryptbuild] 上层 → 下阶1+中层");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildStairUpperToMid(w);
                        changed += buildMidHall(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[cryptbuild] 中层 → 下阶2+圆厅");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildStairMidToBoss(w);
                        changed += buildBossRound(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[cryptbuild] 圆厅 → 门+灯火告示");
                        return;
                    }
                    if (phase == 4) {
                        changed += placeDoors(w);
                        changed += placeLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        changed += placeDoors(w);
                        phase = 5;
                        return;
                    }
                    if (phase == 5) {
                        cancel();
                        w.setSpawnLocation(SPAWN_X, UPPER_Y, SPAWN_Z);
                        w.save();
                        for (org.bukkit.Chunk c : w.getLoadedChunks()) c.unload(true);
                        boolean unloaded = Bukkit.unloadWorld(w, true);
                        try {
                            exportTo(mapDir, workDir);
                            File mvDir = new File(serverRoot, MV_WORLD);
                            exportMvWorld(mvDir, workDir);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.RED + "[cryptbuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[cryptbuild] crypt done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + UPPER_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
                                + " · boss=(" + BOSS_X + "," + BOSS_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[cryptbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("cryptbuild fail: " + t);
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
        for (int x = X0 - 2; x <= X1 + 2; x++) {
            for (int z = Z0 - 2; z <= Z1 + 2; z++) {
                for (int y = 50; y <= 84; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 49, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildWellAndUpper(World w) {
        int n = 0;
        int fy = UPPER_Y - 1;
        // solid shell fill around upper volume
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= UH_Z1 + 2; z++) {
                for (int y = UPPER_Y; y <= UPPER_Y + 5; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.SMOOTH_BRICK);
                }
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
            }
        }
        // well / spawn platform z=-2..1
        for (int x = -3; x <= 3; x++) {
            for (int z = -2; z <= 1; z++) {
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, UPPER_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, UPPER_Y + 4, z), Material.SMOOTH_BRICK);
            }
        }
        // well rim (open to sky feel: small hole above spawn)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(dx, UPPER_Y + 4, dz), Material.AIR);
                n += set(w.getBlockAt(dx, UPPER_Y + 5, dz), Material.IRON_FENCE);
            }
        }
        // upper hall hollow
        for (int x = UH_X0; x <= UH_X1; x++) {
            for (int z = UH_Z0; z <= UH_Z1; z++) {
                n += set(w.getBlockAt(x, fy, z), floorUpper(x, z));
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, UPPER_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, UPPER_Y + 5, z), Material.SMOOTH_BRICK);
            }
        }
        // walls of upper hall
        for (int x = UH_X0 - 1; x <= UH_X1 + 1; x++) {
            for (int z = UH_Z0 - 1; z <= UH_Z1 + 1; z++) {
                boolean edge = x == UH_X0 - 1 || x == UH_X1 + 1 || z == UH_Z0 - 1 || z == UH_Z1 + 1;
                if (!edge) continue;
                for (int y = 0; y <= 5; y++) {
                    Material m = ((x + z) & 1) == 0 ? Material.SMOOTH_BRICK : Material.MOSSY_COBBLESTONE;
                    n += set(w.getBlockAt(x, UPPER_Y + y, z), m);
                }
            }
        }
        // open from well into hall (south of spawn)
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(x, UPPER_Y + y, UH_Z0 - 1), Material.AIR);
            }
        }
        // pillars
        int[][] pillars = {{-5, 5}, {4, 5}, {-5, 12}, {4, 12}, {0, 9}};
        for (int[] p : pillars) {
            n += pillar(w, p[0], UPPER_Y, p[1]);
        }
        // door1 partition at z=DOOR1_Z
        for (int x = UH_X0 - 1; x <= UH_X1 + 1; x++) {
            for (int y = 0; y <= 5; y++) {
                n += set(w.getBlockAt(x, UPPER_Y + y, DOOR1_Z), Material.SMOOTH_BRICK);
            }
        }
        // punch gate
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, UPPER_Y + y, DOOR1_Z), Material.AIR);
            }
            n += set(w.getBlockAt(x, UPPER_Y + 3, DOOR1_Z), Material.SMOOTH_BRICK);
        }
        return n;
    }

    private int buildStairUpperToMid(World w) {
        int n = 0;
        // stair shaft z=19..25, drop UPPER_Y → MID_Y (6 blocks)
        int zStart = DOOR1_Z + 1;
        for (int step = 0; step < 7; step++) {
            int z = zStart + step;
            int yFeet = UPPER_Y - step;
            if (yFeet < MID_Y) yFeet = MID_Y;
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, yFeet - 1, z), Material.SMOOTH_BRICK);
                for (int y = yFeet; y <= UPPER_Y + 4; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // side walls
                for (int y = MID_Y - 1; y <= UPPER_Y + 4; y++) {
                    n += set(w.getBlockAt(DOOR_X0 - 1, y, z), Material.MOSSY_COBBLESTONE);
                    n += set(w.getBlockAt(DOOR_X1 + 1, y, z), Material.MOSSY_COBBLESTONE);
                }
            }
            // step riser behind
            if (step < 6) {
                for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                    n += set(w.getBlockAt(x, yFeet, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, yFeet + 1, z), Material.AIR);
                    n += set(w.getBlockAt(x, yFeet + 2, z), Material.AIR);
                }
            }
        }
        // landing at mid before hall
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = zStart + 6; z <= MH_Z0 - 1; z++) {
                n += set(w.getBlockAt(x, MID_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildMidHall(World w) {
        int n = 0;
        int fy = MID_Y - 1;
        // fill mid shell
        for (int x = MH_X0 - 1; x <= MH_X1 + 1; x++) {
            for (int z = MH_Z0 - 1; z <= MH_Z1 + 1; z++) {
                for (int y = MID_Y; y <= MID_Y + 5; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.SMOOTH_BRICK);
                }
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
            }
        }
        // hollow mid hall
        for (int x = MH_X0; x <= MH_X1; x++) {
            for (int z = MH_Z0; z <= MH_Z1; z++) {
                n += set(w.getBlockAt(x, fy, z), floorMid(x, z));
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, MID_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, MID_Y + 5, z), Material.SMOOTH_BRICK);
            }
        }
        // walls with iron bars high windows (sparse light)
        for (int x = MH_X0 - 1; x <= MH_X1 + 1; x++) {
            for (int z = MH_Z0 - 1; z <= MH_Z1 + 1; z++) {
                boolean edge = x == MH_X0 - 1 || x == MH_X1 + 1 || z == MH_Z0 - 1 || z == MH_Z1 + 1;
                if (!edge) continue;
                for (int y = 0; y <= 5; y++) {
                    Material m = Material.MOSSY_COBBLESTONE;
                    if (y == 3 && (x + z) % 3 == 0) m = Material.IRON_FENCE;
                    else if ((x + z) % 2 == 0) m = Material.SMOOTH_BRICK;
                    n += set(w.getBlockAt(x, MID_Y + y, z), m);
                }
            }
        }
        // entrance from stair landing
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, MH_Z0 - 1), Material.AIR);
            }
        }
        // pillars / cover
        int[][] pillars = {{-4, 30}, {3, 30}, {-4, 36}, {3, 36}};
        for (int[] p : pillars) {
            n += pillar(w, p[0], MID_Y, p[1]);
        }
        // door2 partition
        for (int x = MH_X0 - 1; x <= MH_X1 + 1; x++) {
            for (int y = 0; y <= 5; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, DOOR2_Z), Material.SMOOTH_BRICK);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, DOOR2_Z), Material.AIR);
            }
            n += set(w.getBlockAt(x, MID_Y + 3, DOOR2_Z), Material.SMOOTH_BRICK);
        }
        return n;
    }

    private int buildStairMidToBoss(World w) {
        int n = 0;
        int zStart = DOOR2_Z + 1;
        for (int step = 0; step < 7; step++) {
            int z = zStart + step;
            int yFeet = MID_Y - step;
            if (yFeet < BOSS_Y) yFeet = BOSS_Y;
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, yFeet - 1, z), Material.SMOOTH_BRICK);
                for (int y = yFeet; y <= MID_Y + 4; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                for (int y = BOSS_Y - 1; y <= MID_Y + 4; y++) {
                    n += set(w.getBlockAt(DOOR_X0 - 1, y, z), Material.MOSSY_COBBLESTONE);
                    n += set(w.getBlockAt(DOOR_X1 + 1, y, z), Material.MOSSY_COBBLESTONE);
                }
            }
            if (step < 6) {
                for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                    n += set(w.getBlockAt(x, yFeet, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, yFeet + 1, z), Material.AIR);
                    n += set(w.getBlockAt(x, yFeet + 2, z), Material.AIR);
                }
            }
        }
        // approach to round hall
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = zStart + 6; z <= BOSS_Z - RING_R - 1; z++) {
                n += set(w.getBlockAt(x, BOSS_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, BOSS_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildBossRound(World w) {
        int n = 0;
        int fy = BOSS_Y - 1;
        for (int dx = -RING_R - 1; dx <= RING_R + 1; dx++) {
            for (int dz = -RING_R - 1; dz <= RING_R + 1; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                int r2 = dx * dx + dz * dz;
                int rOuter = (RING_R + 1) * (RING_R + 1);
                int rRing = RING_R * RING_R;
                int rInner = BOSS_R * BOSS_R;
                if (r2 > rOuter) continue;

                if (r2 > rRing) {
                    // outer solid wall
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, BOSS_Y + y, z), Material.SMOOTH_BRICK);
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                    continue;
                }
                if (r2 > rInner) {
                    // ambulatory corridor (width ~2)
                    n += set(w.getBlockAt(x, fy, z), Material.MOSSY_COBBLESTONE);
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, BOSS_Y + y, z), Material.AIR);
                    }
                    n += set(w.getBlockAt(x, BOSS_Y + 4, z), Material.SMOOTH_BRICK);
                    // columns every ~45deg on ring
                    continue;
                }
                // inner round hall
                n += set(w.getBlockAt(x, fy, z), floorBoss(x, z));
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, BOSS_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, BOSS_Y + 5, z), Material.SMOOTH_BRICK);
            }
        }
        // ring columns (kite points)
        int[][] cols = {
                {BOSS_X + BOSS_R, BOSS_Z}, {BOSS_X - BOSS_R, BOSS_Z},
                {BOSS_X, BOSS_Z + BOSS_R}, {BOSS_X, BOSS_Z - BOSS_R},
                {BOSS_X + 4, BOSS_Z + 4}, {BOSS_X - 4, BOSS_Z + 4},
                {BOSS_X + 4, BOSS_Z - 4}, {BOSS_X - 4, BOSS_Z - 4}
        };
        for (int[] c : cols) {
            n += pillar(w, c[0], BOSS_Y, c[1]);
        }
        // open approach from south (toward stairs)
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = BOSS_Z - RING_R; z <= BOSS_Z - BOSS_R; z++) {
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, BOSS_Y + y, z), Material.AIR);
                }
            }
        }
        // center boss pad slightly raised feel (still same Y walk)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, fy, BOSS_Z + dz), Material.MOSSY_COBBLESTONE);
                n += set(w.getBlockAt(BOSS_X + dx, BOSS_Y, BOSS_Z + dz), Material.AIR);
                n += set(w.getBlockAt(BOSS_X + dx, BOSS_Y + 1, BOSS_Z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int placeDoors(World w) {
        int n = 0;
        // door1 at UPPER_Y
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, UPPER_Y + y, DOOR1_Z), Material.IRON_FENCE);
            }
        }
        // door2 at MID_Y
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, MID_Y + y, DOOR2_Z), Material.IRON_FENCE);
            }
        }
        return n;
    }

    private Material floorUpper(int x, int z) {
        int h = (x * 3 + z * 5) & 7;
        if (h == 0) return Material.MOSSY_COBBLESTONE;
        if (h == 1) return Material.COBBLESTONE;
        return Material.SMOOTH_BRICK;
    }

    private Material floorMid(int x, int z) {
        int h = (x * 7 + z) & 7;
        if (h <= 1) return Material.MOSSY_COBBLESTONE;
        if (h == 2) return Material.COBBLESTONE;
        return Material.SMOOTH_BRICK;
    }

    private Material floorBoss(int x, int z) {
        int h = (x * 2 + z * 3) & 7;
        if (h == 0) return Material.MOSSY_COBBLESTONE;
        if (h == 1) return Material.NETHERRACK;
        return Material.SMOOTH_BRICK;
    }

    private int pillar(World w, int x, int feetY, int z) {
        int n = 0;
        n += set(w.getBlockAt(x, feetY - 1, z), Material.SMOOTH_BRICK);
        for (int y = 0; y <= 3; y++) {
            n += set(w.getBlockAt(x, feetY + y, z), Material.SMOOTH_BRICK);
        }
        n += set(w.getBlockAt(x, feetY + 4, z), Material.MOSSY_COBBLESTONE);
        return n;
    }

    private int placeLights(World w) {
        int n = 0;
        // sparse torches / glow — upper
        int[][] up = {{-7, 4}, {6, 4}, {-7, 15}, {6, 15}, {0, 1}};
        for (int[] p : up) {
            n += set(w.getBlockAt(p[0], UPPER_Y, p[1]), Material.TORCH);
        }
        // mid sparse
        int[][] mid = {{-6, 28}, {5, 28}, {-6, 37}, {5, 37}};
        for (int[] p : mid) {
            n += set(w.getBlockAt(p[0], MID_Y, p[1]), Material.TORCH);
        }
        // boss few
        n += set(w.getBlockAt(BOSS_X + 5, BOSS_Y, BOSS_Z), Material.TORCH);
        n += set(w.getBlockAt(BOSS_X - 5, BOSS_Y, BOSS_Z), Material.TORCH);
        n += set(w.getBlockAt(BOSS_X, BOSS_Y, BOSS_Z + 5), Material.GLOWSTONE);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, UPPER_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, UPPER_Y, SPAWN_Z),
                "§8余烬窟·残誓", "§7地窖井口", "§e清上层再下阶", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, UPPER_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, UPPER_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 2, UPPER_Y - 1, DOOR1_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 2, UPPER_Y, DOOR1_Z - 1),
                "§e上层门", "§7清尽上层厅", "§7后下阶中层", "§8↓");
        n += set(w.getBlockAt(DOOR_X1 + 2, MID_Y - 1, DOOR2_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 2, MID_Y, DOOR2_Z - 1),
                "§e中层门", "§7清尽中厅", "§7后下底层", "§8↓ 圆厅");
        n += set(w.getBlockAt(BOSS_X + 3, BOSS_Y - 1, BOSS_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(BOSS_X + 3, BOSS_Y, BOSS_Z),
                "§c底层·圆厅", "§7残誓守墓", "§7廊可风筝", "§8通关回枢纽");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, UPPER_Y, SPAWN_Z},
                {W1A_X, UPPER_Y, W1A_Z},
                {W1B_X, UPPER_Y, W1B_Z},
                {W1C_X, UPPER_Y, W1C_Z},
                {W1D_X, UPPER_Y, W1D_Z},
                {W2A_X, MID_Y, W2A_Z},
                {W2B_X, MID_Y, W2B_Z},
                {W2C_X, MID_Y, W2C_Z},
                {W2D_X, MID_Y, W2D_Z},
                {BOSS_X, BOSS_Y, BOSS_Z}
        };
        for (int[] p : pts) {
            int feetY = p[1];
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int bx = p[0] + dx;
                    int bz = p[2] + dz;
                    if (feetY == UPPER_Y && bz == DOOR1_Z) continue;
                    if (feetY == MID_Y && bz == DOOR2_Z) continue;
                    Material floor = Material.SMOOTH_BRICK;
                    if (feetY == UPPER_Y) floor = floorUpper(bx, bz);
                    else if (feetY == MID_Y) floor = floorMid(bx, bz);
                    else floor = floorBoss(bx, bz);
                    if (p[0] == SPAWN_X && p[2] == SPAWN_Z) floor = Material.SMOOTH_BRICK;
                    n += set(w.getBlockAt(bx, feetY - 1, bz), floor);
                    n += set(w.getBlockAt(bx, feetY, bz), Material.AIR);
                    n += set(w.getBlockAt(bx, feetY + 1, bz), Material.AIR);
                }
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
