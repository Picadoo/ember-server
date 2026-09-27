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
 * S3 (2026-09-28): ember_daily_tide 余烬窟·潮蚀水道 —
 * 淹水运河横推 + 桥闸 + 闸厅 Boss；≥2 真铁栅门；落水有底禁虚空。
 * Admin: /corerpg tidebuild
 *
 * Coords (feet Y=64):
 *   spawn 0,64,0 (dry bank inside gate, face +Z)
 *   door1 z=18 (x=-1..1, y=64..66 IRON_FENCE) bridge gate
 *   door2 z=38
 *   room1 bank ~z=6..16; room2 ~z=26..36; boss hall 0,64,46
 */
public class DailyTideService {

    static final String WORK_WORLD = "ember_daily_tide_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_tide";
    static final String MV_WORLD = "ember_daily_tide";

    static final int GROUND_Y = 64;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    static final int X0 = -8;
    static final int X1 = 8;
    static final int Z0 = -4;
    static final int Z1 = 56;

    /** Walkable bank half-width (x=-2..2) */
    static final int BW0 = -2;
    static final int BW1 = 2;

    static final int DOOR1_Z = 18;
    static final int DOOR2_Z = 38;
    static final int DOOR_X0 = -1;
    static final int DOOR_X1 = 1;

    static final int R1_Z0 = 3, R1_Z1 = 17;
    static final int R2_Z0 = 20, R2_Z1 = 37;
    static final int BOSS_Z0 = 40, BOSS_Z1 = 54;

    static final int BOSS_X = 0;
    static final int BOSS_Z = 46;
    static final int HALL_HALF = 6;

    /** Short dead bridge fork +X */
    static final int FORK_Z0 = 10, FORK_Z1 = 13;
    static final int FORK_X0 = 3, FORK_X1 = 8;

    /** Wave open cells */
    static final int W1A_X = -2, W1A_Z = 8;
    static final int W1B_X = 2, W1B_Z = 8;
    static final int W1C_X = 0, W1C_Z = 14;
    static final int W2A_X = -2, W2A_Z = 26;
    static final int W2B_X = 2, W2B_Z = 28;
    static final int W2C_X = -1, W2C_Z = 32;
    static final int W2D_X = 1, W2D_Z = 34;

    private final CoreRpgPlugin plugin;

    public DailyTideService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "tidebuild".equals(a) || "tide".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg tidebuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[tidebuild] S3 潮蚀水道 · 运河+桥闸+闸厅");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  door1 z=" + DOOR1_Z + " x=" + DOOR_X0 + ".." + DOOR_X1
                + " · door2 z=" + DOOR2_Z + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  fork x=" + FORK_X0 + ".." + FORK_X1 + " z=" + FORK_Z0 + ".." + FORK_Z1);
        sender.sendMessage(ChatColor.GRAY + "  w1A=(" + W1A_X + "," + GROUND_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + GROUND_Y + "," + W1B_Z + ")"
                + " w1C=(" + W1C_X + "," + GROUND_Y + "," + W1C_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w2A=(" + W2A_X + "," + GROUND_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + GROUND_Y + "," + W2B_Z + ")"
                + " w2C=(" + W2C_X + "," + GROUND_Y + "," + W2C_Z + ")"
                + " w2D=(" + W2D_X + "," + GROUND_Y + "," + W2D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  boss=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[tidebuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[tidebuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[tidebuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[tidebuild] S3 潮蚀水道施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[tidebuild] scrub → 运河槽+干岸");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildCanalShell(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[tidebuild] 外壳 → 房1+死桥");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildRoom1(w);
                        changed += buildDeadBridge(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[tidebuild] 房1 → 桥闸过渡+房2");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildBridgeTransition(w);
                        changed += buildRoom2(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[tidebuild] 房2 → 闸厅+门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBossHall(w);
                        changed += placeDoors(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[tidebuild] 门/闸厅 → 灯火告示");
                        return;
                    }
                    if (phase == 5) {
                        changed += placeLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        changed += placeDoors(w);
                        phase = 6;
                        return;
                    }
                    if (phase == 6) {
                        cancel();
                        w.setSpawnLocation(SPAWN_X, GROUND_Y, SPAWN_Z);
                        w.save();
                        for (org.bukkit.Chunk c : w.getLoadedChunks()) c.unload(true);
                        boolean unloaded = Bukkit.unloadWorld(w, true);
                        try {
                            exportTo(mapDir, workDir);
                            File mvDir = new File(serverRoot, MV_WORLD);
                            exportMvWorld(mvDir, workDir);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.RED + "[tidebuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[tidebuild] tide canal done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
                                + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[tidebuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("tidebuild fail: " + t);
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
                for (int y = 54; y <= 78; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // solid bedrock-ish floor under everything (no void)
                n += set(w.getBlockAt(x, 53, z), Material.STONE);
                n += set(w.getBlockAt(x, 52, z), Material.STONE);
            }
        }
        return n;
    }

    /** Canal: central water channel x=±3..±5 with stone bottom; walkable bank x=-2..2 */
    private int buildCanalShell(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean inBank = x >= BW0 && x <= BW1;
                boolean inWaterL = x >= -5 && x <= -3;
                boolean inWaterR = x >= 3 && x <= 5;
                boolean inHall = Math.abs(x - BOSS_X) <= HALL_HALF && z >= BOSS_Z0 && z <= BOSS_Z1;
                boolean inFork = x >= FORK_X0 && x <= FORK_X1 && z >= FORK_Z0 && z <= FORK_Z1;
                boolean walk = inBank || inHall || inFork;

                if (inWaterL || inWaterR) {
                    // canal trough: stone bottom at y=61, water 62-63, air above bank level
                    n += set(w.getBlockAt(x, 61, z), Material.STONE);
                    n += set(w.getBlockAt(x, 62, z), Material.STATIONARY_WATER);
                    n += set(w.getBlockAt(x, 63, z), Material.STATIONARY_WATER);
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                    }
                    // prismarine canal walls at x=±6 / bank edge already handled
                    continue;
                }
                if (!walk && !inHall) {
                    // outer fill
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wallMat(x, z));
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.PRISMARINE);
                    continue;
                }
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z),
                        inHall ? Material.PRISMARINE : Material.SMOOTH_BRICK);
            }
        }
        // canal outer walls x=±6
        for (int z = Z0; z <= BOSS_Z0 - 1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(-6, GROUND_Y + y, z), Material.PRISMARINE);
                n += set(w.getBlockAt(6, GROUND_Y + y, z), Material.PRISMARINE);
            }
        }
        // bank side lips (iron fence rail feel on water edge at bank)
        for (int z = Z0 + 1; z <= BOSS_Z0 - 1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            // light rail on water-facing bank edges (not blocking walk)
            if ((z & 3) == 0) {
                n += set(w.getBlockAt(BW0, GROUND_Y, z), Material.AIR);
                n += set(w.getBlockAt(BW1, GROUND_Y, z), Material.AIR);
            }
        }
        // south entry arch
        for (int x = BW0 - 1; x <= BW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.PRISMARINE);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.AIR);
            }
        }
        n += buildPartitionWall(w, DOOR1_Z);
        n += buildPartitionWall(w, DOOR2_Z);
        return n;
    }

    private Material wallMat(int x, int z) {
        int h = (x * 3 + z) & 3;
        if (h == 0) return Material.PRISMARINE;
        if (h == 1) return Material.SMOOTH_BRICK;
        if (h == 2) return Material.PRISMARINE;
        return Material.SMOOTH_BRICK;
    }

    private int buildPartitionWall(World w, int z) {
        int n = 0;
        for (int x = BW0 - 1; x <= BW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PRISMARINE);
            }
        }
        // also seal water channel with prismarine pillars so door is the only pass
        for (int x = -5; x <= -3; x++) {
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PRISMARINE);
            }
            n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(x, 62, z), Material.PRISMARINE);
            n += set(w.getBlockAt(x, 63, z), Material.PRISMARINE);
        }
        for (int x = 3; x <= 5; x++) {
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PRISMARINE);
            }
            n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(x, 62, z), Material.PRISMARINE);
            n += set(w.getBlockAt(x, 63, z), Material.PRISMARINE);
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(x, GROUND_Y + 3, z), Material.PRISMARINE);
        }
        return n;
    }

    private int buildRoom1(World w) {
        int n = 0;
        // dry spawn bank
        for (int x = BW0; x <= BW1; x++) {
            for (int z = Z0 + 1; z <= 2; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.PRISMARINE);
            }
        }
        // room1 bank corridor
        for (int x = BW0; x <= BW1; x++) {
            for (int z = R1_Z0; z <= R1_Z1; z++) {
                if (z == DOOR1_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // shallow ford pads into water (walkable steps, not void)
        int[] fordZ = {8, 14};
        for (int fz : fordZ) {
            for (int x = -4; x <= -3; x++) {
                n += set(w.getBlockAt(x, 62, zSafeFloor(fz)), Material.PRISMARINE);
                n += set(w.getBlockAt(x, 63, zSafeFloor(fz)), Material.PRISMARINE);
                n += set(w.getBlockAt(x, GROUND_Y - 1, fz), Material.PRISMARINE);
                n += set(w.getBlockAt(x, GROUND_Y, fz), Material.AIR);
            }
            for (int x = 3; x <= 4; x++) {
                n += set(w.getBlockAt(x, 62, fz), Material.PRISMARINE);
                n += set(w.getBlockAt(x, 63, fz), Material.PRISMARINE);
                n += set(w.getBlockAt(x, GROUND_Y - 1, fz), Material.PRISMARINE);
                n += set(w.getBlockAt(x, GROUND_Y, fz), Material.AIR);
            }
        }
        // ±1 step
        n += set(w.getBlockAt(0, GROUND_Y - 1, 6), Material.PRISMARINE);
        n += set(w.getBlockAt(0, GROUND_Y, 6), Material.SMOOTH_BRICK);
        n += set(w.getBlockAt(0, GROUND_Y + 1, 6), Material.AIR);
        return n;
    }

    private int zSafeFloor(int z) { return z; }

    private int buildDeadBridge(World w) {
        int n = 0;
        // short iron-bar bridge over water into sealed dead end
        for (int x = FORK_X0; x < FORK_X1; x++) {
            for (int z = FORK_Z0 + 1; z <= FORK_Z1 - 1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.IRON_FENCE); // bridge deck feel via bars+slab look: use prismarine
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PRISMARINE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // opening from bank
        for (int z = FORK_Z0 + 1; z <= FORK_Z1 - 1; z++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(BW1 + 1, GROUND_Y + y, z), Material.AIR);
            }
            // bridge over right water channel
            n += set(w.getBlockAt(3, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(4, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(5, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(3, 62, z), Material.PRISMARINE);
            n += set(w.getBlockAt(4, 62, z), Material.PRISMARINE);
            n += set(w.getBlockAt(5, 62, z), Material.PRISMARINE);
            n += set(w.getBlockAt(3, 63, z), Material.AIR);
            n += set(w.getBlockAt(4, 63, z), Material.AIR);
            n += set(w.getBlockAt(5, 63, z), Material.AIR);
        }
        // sealed dead end
        for (int z = FORK_Z0; z <= FORK_Z1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(FORK_X1, GROUND_Y + y, z), Material.PRISMARINE);
            }
        }
        // side rails
        for (int x = FORK_X0; x < FORK_X1; x++) {
            n += set(w.getBlockAt(x, GROUND_Y, FORK_Z0), Material.IRON_FENCE);
            n += set(w.getBlockAt(x, GROUND_Y, FORK_Z1), Material.IRON_FENCE);
        }
        return n;
    }

    private int buildBridgeTransition(World w) {
        int n = 0;
        // after door1: short iron-rail bridge over water then continue bank
        for (int z = DOOR1_Z + 1; z <= 22; z++) {
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PRISMARINE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
            // side rails
            n += set(w.getBlockAt(DOOR_X0 - 1, GROUND_Y, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X0 - 1, GROUND_Y - 1, z), Material.PRISMARINE);
            n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, z), Material.PRISMARINE);
        }
        return n;
    }

    private int buildRoom2(World w) {
        int n = 0;
        // folded bridge feel: bank widens then offset
        for (int z = R2_Z0; z <= R2_Z1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            int xLo = BW0;
            int xHi = BW1;
            if (z >= 26 && z <= 32) {
                xLo = -1;
                xHi = 2; // slight offset east
            }
            for (int x = BW0 - 1; x <= BW1 + 1; x++) {
                if (x >= xLo && x <= xHi) {
                    n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.SMOOTH_BRICK);
                } else if (x >= BW0 - 1 && x <= BW1 + 1) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PRISMARINE);
                    }
                }
            }
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(xLo - 1, GROUND_Y + y, z), Material.PRISMARINE);
                n += set(w.getBlockAt(xHi + 1, GROUND_Y + y, z), Material.PRISMARINE);
            }
        }
        return n;
    }

    private int buildBossHall(World w) {
        int n = 0;
        for (int dx = -HALL_HALF; dx <= HALL_HALF; dx++) {
            for (int dz = -HALL_HALF; dz <= HALL_HALF; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                if (z < BOSS_Z0 || z > BOSS_Z1) continue;
                boolean edge = Math.abs(dx) == HALL_HALF || Math.abs(dz) == HALL_HALF;
                // water ring at outer corners (edge water with stone bottom — no void)
                boolean waterCorner = edge && Math.abs(dx) == HALL_HALF && Math.abs(dz) == HALL_HALF;
                if (waterCorner) {
                    n += set(w.getBlockAt(x, 61, z), Material.STONE);
                    n += set(w.getBlockAt(x, 62, z), Material.STATIONARY_WATER);
                    n += set(w.getBlockAt(x, 63, z), Material.STATIONARY_WATER);
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                    }
                    continue;
                }
                n += set(w.getBlockAt(x, GROUND_Y - 1, z),
                        edge ? Material.PRISMARINE : floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PRISMARINE);
                    }
                    // approach opening south (toward door2)
                    if (dz == -HALL_HALF && Math.abs(dx) <= 1) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                        }
                    }
                    // side water windows (rail + air, not solid wall full)
                    if (Math.abs(dx) == HALL_HALF && Math.abs(dz) < HALL_HALF - 1 && (dz & 1) == 0) {
                        n += set(w.getBlockAt(x, GROUND_Y + 2, z), Material.IRON_FENCE);
                        n += set(w.getBlockAt(x, GROUND_Y + 1, z), Material.IRON_FENCE);
                    }
                }
                n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.PRISMARINE);
            }
        }
        // center open for boss kite
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y - 1, BOSS_Z + dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y, BOSS_Z + dz), Material.AIR);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y + 1, BOSS_Z + dz), Material.AIR);
            }
        }
        // connect door2 to hall
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = DOOR2_Z + 1; z < BOSS_Z - HALL_HALF; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PRISMARINE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int placeDoors(World w) {
        int n = 0;
        n += fillGate(w, DOOR1_Z);
        n += fillGate(w, DOOR2_Z);
        return n;
    }

    private int fillGate(World w, int z) {
        int n = 0;
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.IRON_FENCE);
            }
        }
        return n;
    }

    private Material floorMat(int x, int z) {
        int h = (x * 5 + z * 3) & 7;
        if (h == 0 || h == 1) return Material.PRISMARINE;
        if (h == 2) return Material.SMOOTH_BRICK;
        if (h == 3) return Material.LAPIS_BLOCK;
        return Material.SMOOTH_BRICK;
    }

    private int placeLights(World w) {
        int n = 0;
        int[][] pts = {
                {-2, 1}, {2, 1},
                {-2, 8}, {2, 8},
                {-2, 14}, {2, 14},
                {-1, 22}, {1, 22},
                {-2, 28}, {2, 32},
                {-2, 36}, {1, 36},
                {-4, 46}, {4, 46}, {0, 50}
        };
        for (int[] p : pts) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.PRISMARINE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.SEA_LANTERN);
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§3余烬窟·潮蚀", "§7水道安全区", "§e清尽再开闸", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR1_Z - 1), Material.PRISMARINE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR1_Z - 1),
                "§e第一道闸", "§7清尽沿岸", "§7后桥闸敞开", "§8→ 对岸厅");
        n += set(w.getBlockAt(6, GROUND_Y - 1, 11), Material.PRISMARINE);
        n += writeSign(w.getBlockAt(6, GROUND_Y, 11),
                "§8死桥·断渠", "§7尽头封死", "§7勿久留", "§8回主岸");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR2_Z - 1), Material.PRISMARINE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR2_Z - 1),
                "§eBoss闸", "§7清尽折桥", "§7后入闸厅", "§8→ 尽端");
        n += set(w.getBlockAt(BOSS_X + 3, GROUND_Y - 1, BOSS_Z), Material.PRISMARINE);
        n += writeSign(w.getBlockAt(BOSS_X + 3, GROUND_Y, BOSS_Z),
                "§3闸厅·潮闸", "§7蛮兵在此", "§7通关回枢纽", "§8菜单亦可回");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, GROUND_Y, SPAWN_Z},
                {W1A_X, GROUND_Y, W1A_Z},
                {W1B_X, GROUND_Y, W1B_Z},
                {W1C_X, GROUND_Y, W1C_Z},
                {W2A_X, GROUND_Y, W2A_Z},
                {W2B_X, GROUND_Y, W2B_Z},
                {W2C_X, GROUND_Y, W2C_Z},
                {W2D_X, GROUND_Y, W2D_Z},
                {BOSS_X, GROUND_Y, BOSS_Z}
        };
        for (int[] p : pts) {
            int feetY = p[1];
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int bx = p[0] + dx;
                    int bz = p[2] + dz;
                    if (bz == DOOR1_Z || bz == DOOR2_Z) continue;
                    Material floor = floorMat(bx, bz);
                    if (p[0] == SPAWN_X && p[2] == SPAWN_Z) floor = Material.SMOOTH_BRICK;
                    if (p[0] == BOSS_X && p[2] == BOSS_Z) floor = Material.SMOOTH_BRICK;
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
