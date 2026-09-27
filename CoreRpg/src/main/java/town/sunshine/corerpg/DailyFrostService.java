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
 * S4 (2026-09-28): ember_daily_frost 余烬窟·霜晶裂隙 —
 * 冰蓝裂隙冻台横移 + ≥2 真铁栅门；裂隙底有落点禁虚空。
 * Admin: /corerpg frostbuild
 *
 * Coords (feet Y=70):
 *   spawn 0,70,0 (dry ice pad, face +Z)
 *   door1 z=16 (x=-1..1, y=70..72 IRON_FENCE)
 *   door2 z=34
 *   room1 niches ~z=6..14; room2 ~z=22..32; boss frost hall 0,70,48
 */
public class DailyFrostService {

    static final String WORK_WORLD = "ember_daily_frost_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_frost";
    static final String MV_WORLD = "ember_daily_frost";

    static final int GROUND_Y = 70;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    static final int X0 = -8;
    static final int X1 = 8;
    static final int Z0 = -4;
    static final int Z1 = 58;

    /** Walkable freeze-pad half-width (x=-2..2 → width 5) */
    static final int PW0 = -2;
    static final int PW1 = 2;

    /** Side rift channels */
    static final int RIFT_L0 = -6, RIFT_L1 = -4;
    static final int RIFT_R0 = 4, RIFT_R1 = 6;
    static final int RIFT_BOTTOM = 64;

    static final int DOOR1_Z = 16;
    static final int DOOR2_Z = 34;
    static final int DOOR_X0 = -1;
    static final int DOOR_X1 = 1;

    static final int R1_Z0 = 3, R1_Z1 = 15;
    static final int R2_Z0 = 18, R2_Z1 = 33;
    static final int BOSS_Z0 = 36, BOSS_Z1 = 54;

    static final int BOSS_X = 0;
    static final int BOSS_Z = 48;
    static final int HALL_HALF = 6;

    /** Wave open cells */
    static final int W1A_X = -3, W1A_Z = 8;
    static final int W1B_X = 3, W1B_Z = 8;
    static final int W1C_X = 0, W1C_Z = 12;
    static final int W1D_X = -3, W1D_Z = 14;
    static final int W1E_X = 3, W1E_Z = 6;
    static final int W2A_X = -4, W2A_Z = 24;
    static final int W2B_X = 4, W2B_Z = 28;
    static final int W2C_X = 0, W2C_Z = 30;
    static final int W2D_X = -4, W2D_Z = 26;
    static final int W2E_X = 4, W2E_Z = 22;
    static final int W2F_X = 4, W2F_Z = 28; // fold pad y+1 nearby

    private final CoreRpgPlugin plugin;

    public DailyFrostService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "frostbuild".equals(a) || "frost".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg frostbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[frostbuild] S4 霜晶裂隙 · 冻台横移+裂隙底");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  door1 z=" + DOOR1_Z + " x=" + DOOR_X0 + ".." + DOOR_X1
                + " · door2 z=" + DOOR2_Z + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  rift L x=" + RIFT_L0 + ".." + RIFT_L1
                + " R x=" + RIFT_R0 + ".." + RIFT_R1 + " bottomY=" + RIFT_BOTTOM);
        sender.sendMessage(ChatColor.GRAY + "  w1A=(" + W1A_X + "," + GROUND_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + GROUND_Y + "," + W1B_Z + ")"
                + " w1C=(" + W1C_X + "," + GROUND_Y + "," + W1C_Z + ")"
                + " w1D=(" + W1D_X + "," + GROUND_Y + "," + W1D_Z + ")"
                + " w1E=(" + W1E_X + "," + GROUND_Y + "," + W1E_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w2A=(" + W2A_X + "," + GROUND_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + GROUND_Y + "," + W2B_Z + ")"
                + " w2C=(" + W2C_X + "," + GROUND_Y + "," + W2C_Z + ")"
                + " w2D=(" + W2D_X + "," + GROUND_Y + "," + W2D_Z + ")"
                + " w2E=(" + W2E_X + "," + GROUND_Y + "," + W2E_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  boss=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[frostbuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[frostbuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[frostbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[frostbuild] S4 霜晶裂隙施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[frostbuild] scrub → 裂隙槽+冻台");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildRiftShell(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[frostbuild] 外壳 → 房1冻台");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildRoom1(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[frostbuild] 房1 → 过渡+房2");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildTransition(w);
                        changed += buildRoom2(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[frostbuild] 房2 → 霜厅+门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBossHall(w);
                        changed += placeDoors(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[frostbuild] 门/霜厅 → 灯火告示");
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
                            sender.sendMessage(ChatColor.RED + "[frostbuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[frostbuild] frost rift done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
                                + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[frostbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("frostbuild fail: " + t);
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
                for (int y = 58; y <= 84; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 57, z), Material.STONE);
                n += set(w.getBlockAt(x, 56, z), Material.STONE);
            }
        }
        return n;
    }

    /** Freeze pads + side rifts with snow/ice bottom (no void, no canal water). */
    private int buildRiftShell(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean inPad = x >= PW0 && x <= PW1;
                boolean inRiftL = x >= RIFT_L0 && x <= RIFT_L1;
                boolean inRiftR = x >= RIFT_R0 && x <= RIFT_R1;
                boolean inHall = Math.abs(x - BOSS_X) <= HALL_HALF && z >= BOSS_Z0 && z <= BOSS_Z1;
                boolean walk = inPad || inHall;

                if ((inRiftL || inRiftR) && !inHall) {
                    // rift trough: packed ice / snow bottom, air up to pad
                    n += set(w.getBlockAt(x, RIFT_BOTTOM, z), Material.PACKED_ICE);
                    n += set(w.getBlockAt(x, RIFT_BOTTOM + 1, z), Material.SNOW_BLOCK);
                    for (int y = RIFT_BOTTOM + 2; y <= GROUND_Y + 5; y++) {
                        n += set(w.getBlockAt(x, y, z), Material.AIR);
                    }
                    continue;
                }
                if (!walk) {
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wallMat(x, z));
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.PACKED_ICE);
                    continue;
                }
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z),
                        inHall ? Material.QUARTZ_BLOCK : Material.PACKED_ICE);
            }
        }
        // outer rift walls
        for (int z = Z0; z <= BOSS_Z0 - 1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(RIFT_L0 - 1, GROUND_Y + y, z), Material.PACKED_ICE);
                n += set(w.getBlockAt(RIFT_R1 + 1, GROUND_Y + y, z), Material.PACKED_ICE);
            }
        }
        // south entry arch
        for (int x = PW0 - 1; x <= PW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.QUARTZ_BLOCK);
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
        if (h == 0) return Material.PACKED_ICE;
        if (h == 1) return Material.SNOW_BLOCK;
        if (h == 2) return Material.QUARTZ_BLOCK;
        return Material.PACKED_ICE;
    }

    private int buildPartitionWall(World w, int z) {
        int n = 0;
        for (int x = PW0 - 1; x <= PW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PACKED_ICE);
            }
        }
        // seal rift channels so door is the only pass
        for (int x = RIFT_L0; x <= RIFT_L1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PACKED_ICE);
            }
            n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PACKED_ICE);
            n += set(w.getBlockAt(x, RIFT_BOTTOM + 1, z), Material.PACKED_ICE);
        }
        for (int x = RIFT_R0; x <= RIFT_R1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PACKED_ICE);
            }
            n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PACKED_ICE);
            n += set(w.getBlockAt(x, RIFT_BOTTOM + 1, z), Material.PACKED_ICE);
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(x, GROUND_Y + 3, z), Material.QUARTZ_BLOCK);
        }
        return n;
    }

    private int buildRoom1(World w) {
        int n = 0;
        // dry spawn pad
        for (int x = PW0; x <= PW1; x++) {
            for (int z = Z0 + 1; z <= 2; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.QUARTZ_BLOCK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.PACKED_ICE);
            }
        }
        // room1 freeze corridor
        for (int x = PW0; x <= PW1; x++) {
            for (int z = R1_Z0; z <= R1_Z1; z++) {
                if (z == DOOR1_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // side niches at ±3 (jump pads over rift lip)
        int[] nicheZ = {6, 8, 12, 14};
        for (int nz : nicheZ) {
            for (int side : new int[]{-3, 3}) {
                n += set(w.getBlockAt(side, GROUND_Y - 1, nz), Material.PACKED_ICE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(side, GROUND_Y + y, nz), Material.AIR);
                }
                // open lip from pad wall
                int wallX = side < 0 ? PW0 - 1 : PW1 + 1;
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(wallX, GROUND_Y + y, nz), Material.AIR);
                }
            }
        }
        // narrow cross-rift stepping stones (not water)
        int[] fordZ = {8, 12};
        for (int fz : fordZ) {
            for (int x = RIFT_L0; x <= RIFT_L1; x++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, fz), Material.ICE);
                n += set(w.getBlockAt(x, GROUND_Y, fz), Material.AIR);
                n += set(w.getBlockAt(x, RIFT_BOTTOM, fz), Material.SNOW_BLOCK);
            }
            for (int x = RIFT_R0; x <= RIFT_R1; x++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, fz), Material.ICE);
                n += set(w.getBlockAt(x, GROUND_Y, fz), Material.AIR);
                n += set(w.getBlockAt(x, RIFT_BOTTOM, fz), Material.SNOW_BLOCK);
            }
        }
        // light step
        n += set(w.getBlockAt(0, GROUND_Y - 1, 6), Material.QUARTZ_BLOCK);
        n += set(w.getBlockAt(0, GROUND_Y, 6), Material.PACKED_ICE);
        n += set(w.getBlockAt(0, GROUND_Y + 1, 6), Material.AIR);
        return n;
    }

    private int buildTransition(World w) {
        int n = 0;
        for (int z = DOOR1_Z + 1; z <= 20; z++) {
            for (int x = DOOR_X0; x <= DOOR_X1; x++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PACKED_ICE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
            n += set(w.getBlockAt(DOOR_X0 - 1, GROUND_Y, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, z), Material.IRON_FENCE);
            n += set(w.getBlockAt(DOOR_X0 - 1, GROUND_Y - 1, z), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, z), Material.QUARTZ_BLOCK);
        }
        return n;
    }

    private int buildRoom2(World w) {
        int n = 0;
        for (int z = R2_Z0; z <= R2_Z1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            int xLo = PW0;
            int xHi = PW1;
            // fold pad feel: widen east slightly mid room
            if (z >= 24 && z <= 30) {
                xLo = -2;
                xHi = 3;
            }
            for (int x = PW0 - 1; x <= PW1 + 2; x++) {
                if (x >= xLo && x <= xHi) {
                    n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.PACKED_ICE);
                }
            }
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(xLo - 1, GROUND_Y + y, z), Material.PACKED_ICE);
                n += set(w.getBlockAt(xHi + 1, GROUND_Y + y, z), Material.PACKED_ICE);
            }
        }
        // fold raised pad at (4,71,28)
        n += set(w.getBlockAt(4, GROUND_Y - 1, 28), Material.QUARTZ_BLOCK);
        n += set(w.getBlockAt(4, GROUND_Y, 28), Material.PACKED_ICE);
        n += set(w.getBlockAt(4, GROUND_Y + 1, 28), Material.AIR);
        n += set(w.getBlockAt(4, GROUND_Y + 2, 28), Material.AIR);
        // room2 side niches ±4
        int[] nicheZ = {22, 24, 26, 28, 30};
        for (int nz : nicheZ) {
            for (int side : new int[]{-4, 4}) {
                n += set(w.getBlockAt(side, GROUND_Y - 1, nz), Material.PACKED_ICE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(side, GROUND_Y + y, nz), Material.AIR);
                }
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
                // corner rift windows with snow bottom — no void
                boolean riftCorner = edge && Math.abs(dx) == HALL_HALF && Math.abs(dz) == HALL_HALF;
                if (riftCorner) {
                    n += set(w.getBlockAt(x, RIFT_BOTTOM, z), Material.SNOW_BLOCK);
                    n += set(w.getBlockAt(x, RIFT_BOTTOM + 1, z), Material.PACKED_ICE);
                    for (int y = RIFT_BOTTOM + 2; y <= GROUND_Y + 5; y++) {
                        n += set(w.getBlockAt(x, y, z), Material.AIR);
                    }
                    continue;
                }
                n += set(w.getBlockAt(x, GROUND_Y - 1, z),
                        edge ? Material.QUARTZ_BLOCK : floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.PACKED_ICE);
                    }
                    if (dz == -HALL_HALF && Math.abs(dx) <= 1) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                        }
                    }
                    if (Math.abs(dx) == HALL_HALF && Math.abs(dz) < HALL_HALF - 1 && (dz & 1) == 0) {
                        n += set(w.getBlockAt(x, GROUND_Y + 1, z), Material.IRON_FENCE);
                        n += set(w.getBlockAt(x, GROUND_Y + 2, z), Material.IRON_FENCE);
                    }
                }
                n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.QUARTZ_BLOCK);
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y - 1, BOSS_Z + dz), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y, BOSS_Z + dz), Material.AIR);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y + 1, BOSS_Z + dz), Material.AIR);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = DOOR2_Z + 1; z < BOSS_Z - HALL_HALF; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.PACKED_ICE);
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
        if (h == 0 || h == 1) return Material.PACKED_ICE;
        if (h == 2) return Material.ICE;
        if (h == 3) return Material.QUARTZ_BLOCK;
        if (h == 4) return Material.SNOW_BLOCK;
        return Material.PACKED_ICE;
    }

    private int placeLights(World w) {
        int n = 0;
        int[][] pts = {
                {-2, 1}, {2, 1},
                {-2, 8}, {2, 8},
                {-2, 14}, {2, 14},
                {-1, 20}, {1, 20},
                {-2, 26}, {2, 30},
                {-2, 32}, {1, 32},
                {-4, 48}, {4, 48}, {0, 52}
        };
        for (int[] p : pts) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.GLOWSTONE);
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§b余烬窟·霜晶", "§7裂隙安全区", "§e清尽再开闸", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR1_Z - 1), Material.PACKED_ICE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR1_Z - 1),
                "§e第一道冰闸", "§7清尽冻台", "§7后冰闸敞开", "§8→ 对岸台");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR2_Z - 1), Material.PACKED_ICE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR2_Z - 1),
                "§eBoss闸", "§7清尽折台", "§7后入霜厅", "§8→ 尽端");
        n += set(w.getBlockAt(BOSS_X + 3, GROUND_Y - 1, BOSS_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(BOSS_X + 3, GROUND_Y, BOSS_Z),
                "§b霜厅·霜核", "§7蛮兵在此", "§7通关回枢纽", "§8菜单亦可回");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, GROUND_Y, SPAWN_Z},
                {W1A_X, GROUND_Y, W1A_Z},
                {W1B_X, GROUND_Y, W1B_Z},
                {W1C_X, GROUND_Y, W1C_Z},
                {W1D_X, GROUND_Y, W1D_Z},
                {W1E_X, GROUND_Y, W1E_Z},
                {W2A_X, GROUND_Y, W2A_Z},
                {W2B_X, GROUND_Y, W2B_Z},
                {W2C_X, GROUND_Y, W2C_Z},
                {W2D_X, GROUND_Y, W2D_Z},
                {W2E_X, GROUND_Y, W2E_Z},
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
                    if (p[0] == SPAWN_X && p[2] == SPAWN_Z) floor = Material.QUARTZ_BLOCK;
                    if (p[0] == BOSS_X && p[2] == BOSS_Z) floor = Material.QUARTZ_BLOCK;
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
