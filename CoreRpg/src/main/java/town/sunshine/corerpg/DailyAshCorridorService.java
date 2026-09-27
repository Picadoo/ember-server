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
 * S2 (2026-09-28): ember_daily_ash 余烬窟·焦骨甬道 —
 * 狭长焦土 + 假岔 + 尽端鼓室；≥2 真铁栅门。
 * Admin: /corerpg ashbuild
 *
 * Coords (feet Y=65; boss pad Y=65):
 *   spawn 0,65,0 (arch, face +Z)
 *   door1 z=16 (x=-1..1, y=65..67 IRON_FENCE)
 *   fake fork +X at z≈10..12 → dead bone wall x=9
 *   door2 z=36
 *   room1 spawns ~z=6..14 niches; room2 ~z=22..34; boss drum 0,65,44
 */
public class DailyAshCorridorService {

    static final String WORK_WORLD = "ember_daily_ash_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_ash";
    static final String MV_WORLD = "ember_daily_ash";

    static final int GROUND_Y = 65;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    /** Outer scrub / shell bounds */
    static final int X0 = -6;
    static final int X1 = 10;
    static final int Z0 = -4;
    static final int Z1 = 54;

    /** Main corridor half-width (x=-2..2 → width 5) */
    static final int CW0 = -2;
    static final int CW1 = 2;

    static final int DOOR1_Z = 16;
    static final int DOOR2_Z = 36;
    static final int DOOR_X0 = -1;
    static final int DOOR_X1 = 1;

    static final int R1_Z0 = 3, R1_Z1 = 15;
    static final int R2_Z0 = 17, R2_Z1 = 35;
    static final int BOSS_Z0 = 38, BOSS_Z1 = 52;

    static final int BOSS_X = 0;
    static final int BOSS_Z = 44;
    static final int DRUM_HALF = 5;

    /** Fake fork */
    static final int FORK_Z0 = 9, FORK_Z1 = 12;
    static final int FORK_X0 = 3, FORK_X1 = 9;

    /** Wave open cells (for DP next baton / STATUS) */
    static final int W1A_X = -3, W1A_Z = 8;
    static final int W1B_X = 3, W1B_Z = 8;
    static final int W1C_X = 0, W1C_Z = 13;
    static final int W2A_X = -2, W2A_Z = 24;
    static final int W2B_X = 1, W2B_Z = 28;
    static final int W2C_X = -1, W2C_Z = 32;
    static final int W2D_X = 0, W2D_Z = 22;

    private final CoreRpgPlugin plugin;

    public DailyAshCorridorService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "ashbuild".equals(a) || "ash".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg ashbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[ashbuild] S2 焦骨甬道 · 狭长+假岔+鼓室");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  door1 z=" + DOOR1_Z + " x=" + DOOR_X0 + ".." + DOOR_X1
                + " · door2 z=" + DOOR2_Z + " · drum=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  fork x=" + FORK_X0 + ".." + FORK_X1 + " z=" + FORK_Z0 + ".." + FORK_Z1
                + " · sealed bone @x=" + FORK_X1);
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
            sender.sendMessage(ChatColor.RED + "[ashbuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[ashbuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[ashbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[ashbuild] S2 焦骨甬道施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[ashbuild] scrub → 外壳甬道");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildShell(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[ashbuild] 外壳 → 门廊+房1");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildPorchAndRoom1(w);
                        changed += buildFakeFork(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[ashbuild] 房1/假岔 → 房2收窄");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildRoom2(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[ashbuild] 房2 → 鼓室+门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBossDrum(w);
                        changed += placeDoors(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[ashbuild] 门/鼓室 → 灯火告示");
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
                            sender.sendMessage(ChatColor.RED + "[ashbuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[ashbuild] ash corridor done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
                                + " · drum=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[ashbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("ashbuild fail: " + t);
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
                n += set(w.getBlockAt(x, 53, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildShell(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean inMain = x >= CW0 && x <= CW1 && z >= Z0 && z <= Z1;
                boolean inFork = x >= FORK_X0 && x <= FORK_X1 && z >= FORK_Z0 && z <= FORK_Z1;
                boolean inDrum = Math.abs(x - BOSS_X) <= DRUM_HALF && z >= BOSS_Z0 && z <= BOSS_Z1;
                boolean walk = inMain || inFork || inDrum;
                if (!walk) {
                    // fill solid rock outside corridor
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.NETHERRACK);
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.NETHERRACK);
                    continue;
                }
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                // ceiling
                n += set(w.getBlockAt(x, GROUND_Y + 4, z),
                        inDrum ? Material.RED_SANDSTONE : Material.NETHERRACK);
            }
        }
        // corridor side walls
        for (int z = Z0; z <= BOSS_Z0 - 1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(CW0 - 1, GROUND_Y + y, z), wallMat(z));
                n += set(w.getBlockAt(CW1 + 1, GROUND_Y + y, z), wallMat(z));
            }
        }
        // south outer arch wall
        for (int x = CW0 - 1; x <= CW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.RED_SANDSTONE);
            }
        }
        // punch south arch
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.AIR);
            }
        }
        // partition walls at doors
        n += buildPartitionWall(w, DOOR1_Z);
        n += buildPartitionWall(w, DOOR2_Z);
        return n;
    }

    private Material wallMat(int z) {
        int h = z & 3;
        if (h == 0) return Material.RED_SANDSTONE;
        if (h == 1) return Material.NETHERRACK;
        if (h == 2) return Material.NETHER_FENCE;
        return Material.NETHERRACK;
    }

    private int buildPartitionWall(World w, int z) {
        int n = 0;
        for (int x = CW0 - 1; x <= CW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.RED_SANDSTONE);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(x, GROUND_Y + 3, z), Material.RED_SANDSTONE);
        }
        return n;
    }

    private int buildPorchAndRoom1(World w) {
        int n = 0;
        // porch floor solid red sandstone
        for (int x = CW0; x <= CW1; x++) {
            for (int z = Z0 + 1; z <= 2; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.RED_SANDSTONE);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.RED_SANDSTONE);
            }
        }
        // room1 corridor + side niches (open cells at x=±3)
        for (int x = CW0; x <= CW1; x++) {
            for (int z = R1_Z0; z <= R1_Z1; z++) {
                if (z == DOOR1_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // niches punched into side walls at spawn pads
        int[] nicheZ = {8, 13};
        for (int nz : nicheZ) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(CW0 - 1, GROUND_Y + y, nz), Material.AIR);
                n += set(w.getBlockAt(CW1 + 1, GROUND_Y + y, nz), Material.AIR);
            }
            n += set(w.getBlockAt(CW0 - 1, GROUND_Y - 1, nz), Material.SOUL_SAND);
            n += set(w.getBlockAt(CW1 + 1, GROUND_Y - 1, nz), Material.SOUL_SAND);
            // niche depth floor
            n += set(w.getBlockAt(-3, GROUND_Y - 1, nz), Material.SOUL_SAND);
            n += set(w.getBlockAt(3, GROUND_Y - 1, nz), Material.SOUL_SAND);
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(-3, GROUND_Y + y, nz), Material.AIR);
                n += set(w.getBlockAt(3, GROUND_Y + y, nz), Material.AIR);
            }
        }
        // small ±1 step for terrain feel
        n += set(w.getBlockAt(0, GROUND_Y - 1, 6), Material.NETHERRACK);
        n += set(w.getBlockAt(0, GROUND_Y, 6), Material.RED_SANDSTONE);
        n += set(w.getBlockAt(0, GROUND_Y + 1, 6), Material.AIR);
        n += set(w.getBlockAt(0, GROUND_Y + 2, 6), Material.AIR);
        // spawn clear
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y - 1, SPAWN_Z + dz), Material.RED_SANDSTONE);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y, SPAWN_Z + dz), Material.AIR);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y + 1, SPAWN_Z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int buildFakeFork(World w) {
        int n = 0;
        // open side branch +X
        for (int x = FORK_X0; x <= FORK_X1; x++) {
            for (int z = FORK_Z0; z <= FORK_Z1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.SOUL_SAND);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.NETHERRACK);
                // side walls of fork
                if (z == FORK_Z0 || z == FORK_Z1) {
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.NETHERRACK);
                    }
                    // re-open walkable center of fork
                    if (z == FORK_Z0) {
                        // keep south wall except opening from main
                    }
                }
            }
        }
        // clear walkable fork interior (z mid)
        for (int x = FORK_X0; x < FORK_X1; x++) {
            for (int z = FORK_Z0 + 1; z <= FORK_Z1 - 1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.SOUL_SAND);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // opening from main corridor into fork
        for (int z = FORK_Z0 + 1; z <= FORK_Z1 - 1; z++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(CW1 + 1, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(CW1 + 1, GROUND_Y - 1, z), Material.SOUL_SAND);
        }
        // sealed bone / netherrack dead end at x=FORK_X1
        for (int z = FORK_Z0; z <= FORK_Z1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(FORK_X1, GROUND_Y + y, z), Material.BONE_BLOCK);
            }
        }
        // decorative fake gate look mid-fork
        for (int y = 0; y <= 2; y++) {
            n += set(w.getBlockAt(FORK_X1 - 1, GROUND_Y + y, FORK_Z0 + 1), Material.IRON_FENCE);
            n += set(w.getBlockAt(FORK_X1 - 1, GROUND_Y + y, FORK_Z1 - 1), Material.IRON_FENCE);
        }
        return n;
    }

    private int buildRoom2(World w) {
        int n = 0;
        // narrow bend: offset west for z=20..28 (width 3: x=-2..0), then back
        for (int z = R2_Z0; z <= R2_Z1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            int xLo = CW0;
            int xHi = CW1;
            if (z >= 20 && z <= 28) {
                xLo = -2;
                xHi = 0; // narrow 3
            } else if (z >= 29 && z <= 34) {
                xLo = -1;
                xHi = 1;
            }
            // fill solid outside narrow section within corridor band
            for (int x = CW0 - 1; x <= CW1 + 1; x++) {
                if (x >= xLo && x <= xHi) {
                    n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.NETHERRACK);
                } else if (x >= CW0 - 1 && x <= CW1 + 1) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.NETHERRACK);
                    }
                }
            }
            // side walls
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(xLo - 1, GROUND_Y + y, z), Material.RED_SANDSTONE);
                n += set(w.getBlockAt(xHi + 1, GROUND_Y + y, z), Material.RED_SANDSTONE);
            }
        }
        // ±1 drop mid room2
        n += set(w.getBlockAt(-1, GROUND_Y - 1, 26), Material.NETHERRACK);
        n += set(w.getBlockAt(-1, GROUND_Y, 26), Material.AIR);
        n += set(w.getBlockAt(0, GROUND_Y - 1, 26), Material.SOUL_SAND);
        return n;
    }

    private int buildBossDrum(World w) {
        int n = 0;
        for (int dx = -DRUM_HALF; dx <= DRUM_HALF; dx++) {
            for (int dz = -DRUM_HALF; dz <= DRUM_HALF; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                if (z < BOSS_Z0 || z > BOSS_Z1) continue;
                boolean edge = Math.abs(dx) == DRUM_HALF || Math.abs(dz) == DRUM_HALF;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z),
                        edge ? Material.RED_SANDSTONE : floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.RED_SANDSTONE);
                    }
                    // leave approach opening south (toward door2)
                    if (dz == -DRUM_HALF && Math.abs(dx) <= 1) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                        }
                    }
                }
                n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.RED_SANDSTONE);
            }
        }
        // slightly raised center ring (pad feel, still walkable around)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y - 1, BOSS_Z + dz), Material.NETHERRACK);
                    n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y, BOSS_Z + dz), Material.RED_SANDSTONE);
                    n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y + 1, BOSS_Z + dz), Material.AIR);
                }
            }
        }
        // center open for boss
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y - 1, BOSS_Z + dz), Material.NETHERRACK);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y, BOSS_Z + dz), Material.AIR);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y + 1, BOSS_Z + dz), Material.AIR);
            }
        }
        // connect door2 plane to drum (air tunnel)
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = DOOR2_Z + 1; z < BOSS_Z - DRUM_HALF; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.RED_SANDSTONE);
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
        if (h == 0 || h == 1) return Material.NETHERRACK;
        if (h == 2) return Material.SOUL_SAND;
        if (h == 3) return Material.RED_SANDSTONE;
        if (h == 4) return Material.NETHERRACK;
        return Material.RED_SANDSTONE;
    }

    private int placeLights(World w) {
        int n = 0;
        int[][] pts = {
                {-2, 1}, {2, 1},
                {-2, 8}, {2, 8},
                {-2, 14}, {2, 14},
                {-2, 24}, {0, 28},
                {-2, 34}, {1, 34},
                {-4, 44}, {4, 44}, {0, 49}
        };
        for (int[] p : pts) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.NETHERRACK);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.GLOWSTONE);
        }
        // safe lava decor (walled, not on spawn pads)
        n += set(w.getBlockAt(-4, GROUND_Y - 1, 44), Material.NETHERRACK);
        n += set(w.getBlockAt(4, GROUND_Y - 1, 44), Material.NETHERRACK);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.RED_SANDSTONE);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§c余烬窟·焦骨", "§7甬道安全区", "§e清尽再开门", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.RED_SANDSTONE);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR1_Z - 1), Material.RED_SANDSTONE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR1_Z - 1),
                "§e第一道门", "§7清尽前段", "§7后铁门敞开", "§8→ 收窄弯");
        n += set(w.getBlockAt(6, GROUND_Y - 1, 11), Material.SOUL_SAND);
        n += writeSign(w.getBlockAt(6, GROUND_Y, 11),
                "§8假岔·死路", "§7尽头焦骨封死", "§7勿久留", "§8回主路");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR2_Z - 1), Material.RED_SANDSTONE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR2_Z - 1),
                "§eBoss门", "§7清尽弯道", "§7后入鼓室", "§8→ 尽端");
        n += set(w.getBlockAt(BOSS_X + 3, GROUND_Y - 1, BOSS_Z), Material.RED_SANDSTONE);
        n += writeSign(w.getBlockAt(BOSS_X + 3, GROUND_Y, BOSS_Z),
                "§c鼓室·焦核", "§7蛮兵在此", "§7通关回枢纽", "§8菜单亦可回");
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
                    if (p[0] == SPAWN_X && p[2] == SPAWN_Z) floor = Material.RED_SANDSTONE;
                    if (p[0] == BOSS_X && p[2] == BOSS_Z) floor = Material.NETHERRACK;
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
