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
 * S4 (2026-09-28): ember_daily_rail 余烬窟·锈轨矿道 —
 * 废弃矿轨 + 恰好 1 短支洞 → 尽头机房；巷宽 5～7；≠焦骨窄筒、≠垂直翻版。
 * Admin: /corerpg railbuild
 *
 * Coords (feet Y=64):
 *   spawn 0,64,0 (mine mouth, face +Z)
 *   door1 z=15 (x=-1..1, y=64..66 IRON_FENCE)
 *   spur entry 6,64,20 → end 12,64,20
 *   door2 z=33
 *   room1 ~z=5..13; room2 ~z=18..31; boss machine hall 0,64,46
 */
public class DailyRailService {

    static final String WORK_WORLD = "ember_daily_rail_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily_rail";
    static final String MV_WORLD = "ember_daily_rail";

    static final int GROUND_Y = 64;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    static final int X0 = -8;
    static final int X1 = 14;
    static final int Z0 = -4;
    static final int Z1 = 56;

    /** Main gallery half-width (x=-3..3 → width 7, wider than ash 5) */
    static final int CW0 = -3;
    static final int CW1 = 3;

    static final int DOOR1_Z = 15;
    static final int DOOR2_Z = 33;
    static final int DOOR_X0 = -1;
    static final int DOOR_X1 = 1;

    static final int R1_Z0 = 3, R1_Z1 = 14;
    static final int R2_Z0 = 17, R2_Z1 = 32;
    static final int BOSS_Z0 = 35, BOSS_Z1 = 52;

    static final int BOSS_X = 0;
    static final int BOSS_Z = 46;
    static final int HALL_HALF = 6;

    /** Exactly 1 short spur (+X) */
    static final int SPUR_Z0 = 18, SPUR_Z1 = 22;
    static final int SPUR_X0 = 4, SPUR_X1 = 12;

    /** Wave open cells */
    static final int W1A_X = -4, W1A_Z = 6;
    static final int W1B_X = 4, W1B_Z = 6;
    static final int W1C_X = 0, W1C_Z = 10;
    static final int W1D_X = -4, W1D_Z = 12;
    static final int W1E_X = 4, W1E_Z = 12;
    static final int W2A_X = -5, W2A_Z = 20;
    static final int W2B_X = 5, W2B_Z = 24;
    static final int W2C_X = -5, W2C_Z = 28;
    static final int W2D_X = 5, W2D_Z = 30;
    static final int W2E_X = 0, W2E_Z = 22;
    static final int W2F_X = 6, W2F_Z = 20; // spur mouth

    private final CoreRpgPlugin plugin;

    public DailyRailService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "railbuild".equals(a) || "rail".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg railbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[railbuild] S4 锈轨矿道 · 主巷+1支洞+机房");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1
                + " · gallery w=" + (CW1 - CW0 + 1));
        sender.sendMessage(ChatColor.GRAY + "  door1 z=" + DOOR1_Z + " x=" + DOOR_X0 + ".." + DOOR_X1
                + " · door2 z=" + DOOR2_Z + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  spur x=" + SPUR_X0 + ".." + SPUR_X1
                + " z=" + SPUR_Z0 + ".." + SPUR_Z1 + " · sealed @x=" + SPUR_X1);
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
            sender.sendMessage(ChatColor.RED + "[railbuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[railbuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[railbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[railbuild] S4 锈轨矿道施工…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrub(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[railbuild] scrub → 主巷外壳");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildGalleryShell(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[railbuild] 外壳 → 房1+轨");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildRoom1(w);
                        changed += placeRails(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[railbuild] 房1 → 支洞+房2");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildSpur(w);
                        changed += buildRoom2(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[railbuild] 房2 → 机房+门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBossHall(w);
                        changed += placeDoors(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[railbuild] 门/机房 → 灯柱告示");
                        return;
                    }
                    if (phase == 5) {
                        changed += placePillarsAndLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        changed += placeDoors(w);
                        changed += placeRails(w);
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
                            sender.sendMessage(ChatColor.RED + "[railbuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[railbuild] rail mine done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
                                + " · hall=(" + BOSS_X + "," + GROUND_Y + "," + BOSS_Z + ")"
                                + " · spur x=" + SPUR_X0 + ".." + SPUR_X1
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL + " + MV " + MV_WORLD);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[railbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("railbuild fail: " + t);
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
                n += set(w.getBlockAt(x, 52, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildGalleryShell(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean inMain = x >= CW0 && x <= CW1;
                boolean inSpur = x >= SPUR_X0 && x <= SPUR_X1 && z >= SPUR_Z0 && z <= SPUR_Z1;
                boolean inHall = Math.abs(x - BOSS_X) <= HALL_HALF && z >= BOSS_Z0 && z <= BOSS_Z1;
                boolean walk = inMain || inSpur || inHall;
                if (!walk) {
                    for (int y = 0; y <= 5; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wallMat(x, z));
                    }
                    n += set(w.getBlockAt(x, fy, z), Material.COBBLESTONE);
                    continue;
                }
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z),
                        inHall ? Material.LOG : Material.COBBLESTONE);
            }
        }
        // gallery side walls (timber + cobble)
        for (int z = Z0; z <= BOSS_Z0 - 1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(CW0 - 1, GROUND_Y + y, z), wallMat(CW0 - 1, z));
                n += set(w.getBlockAt(CW1 + 1, GROUND_Y + y, z), wallMat(CW1 + 1, z));
            }
        }
        // south entry wood frame
        for (int x = CW0 - 1; x <= CW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.LOG);
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
        if (h == 0) return Material.COBBLESTONE;
        if (h == 1) return Material.MOSSY_COBBLESTONE;
        if (h == 2) return Material.LOG;
        return Material.COBBLESTONE;
    }

    private int buildPartitionWall(World w, int z) {
        int n = 0;
        for (int x = CW0 - 1; x <= CW1 + 1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.COBBLESTONE);
            }
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(x, GROUND_Y + 3, z), Material.LOG);
        }
        return n;
    }

    private int buildRoom1(World w) {
        int n = 0;
        for (int x = CW0; x <= CW1; x++) {
            for (int z = Z0 + 1; z <= 2; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.WOOD);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.LOG);
            }
        }
        for (int x = CW0; x <= CW1; x++) {
            for (int z = R1_Z0; z <= R1_Z1; z++) {
                if (z == DOOR1_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // pillar niches ±4
        int[] nicheZ = {6, 10, 12};
        for (int nz : nicheZ) {
            for (int side : new int[]{-4, 4}) {
                n += set(w.getBlockAt(side, GROUND_Y - 1, nz), Material.COBBLESTONE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(side, GROUND_Y + y, nz), Material.AIR);
                }
                int wallX = side < 0 ? CW0 - 1 : CW1 + 1;
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(wallX, GROUND_Y + y, nz), Material.AIR);
                }
            }
        }
        // ±1 sleeper step
        n += set(w.getBlockAt(0, GROUND_Y - 1, 5), Material.COBBLESTONE);
        n += set(w.getBlockAt(0, GROUND_Y, 5), Material.WOOD);
        n += set(w.getBlockAt(0, GROUND_Y + 1, 5), Material.AIR);
        return n;
    }

    private int placeRails(World w) {
        int n = 0;
        // decorative center rail line (not rideable required)
        for (int z = Z0 + 1; z <= BOSS_Z0 - 1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            n += set(w.getBlockAt(0, GROUND_Y - 1, z), Material.COBBLESTONE);
            n += set(w.getBlockAt(0, GROUND_Y, z), Material.RAILS);
        }
        // spur rails
        for (int x = SPUR_X0; x < SPUR_X1; x++) {
            for (int z = 19; z <= 21; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.COBBLESTONE);
                n += set(w.getBlockAt(x, GROUND_Y, z), Material.RAILS);
            }
        }
        return n;
    }

    private int buildSpur(World w) {
        int n = 0;
        // open from gallery into spur
        for (int z = SPUR_Z0 + 1; z <= SPUR_Z1 - 1; z++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(CW1 + 1, GROUND_Y + y, z), Material.AIR);
            }
        }
        for (int x = SPUR_X0; x < SPUR_X1; x++) {
            for (int z = SPUR_Z0 + 1; z <= SPUR_Z1 - 1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE);
            }
        }
        // sealed dead end
        for (int z = SPUR_Z0; z <= SPUR_Z1; z++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(SPUR_X1, GROUND_Y + y, z), Material.COBBLESTONE);
            }
        }
        // side fences along spur
        for (int x = SPUR_X0; x < SPUR_X1; x++) {
            n += set(w.getBlockAt(x, GROUND_Y, SPUR_Z0), Material.FENCE);
            n += set(w.getBlockAt(x, GROUND_Y, SPUR_Z1), Material.FENCE);
        }
        // ore accents at spur end
        n += set(w.getBlockAt(SPUR_X1 - 1, GROUND_Y + 1, 20), Material.IRON_ORE);
        n += set(w.getBlockAt(SPUR_X1 - 1, GROUND_Y + 2, 19), Material.COAL_ORE);
        return n;
    }

    private int buildRoom2(World w) {
        int n = 0;
        for (int z = R2_Z0; z <= R2_Z1; z++) {
            if (z == DOOR1_Z || z == DOOR2_Z) continue;
            int xLo = CW0;
            int xHi = CW1;
            for (int x = xLo; x <= xHi; x++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE);
            }
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(xLo - 1, GROUND_Y + y, z), wallMat(xLo - 1, z));
                n += set(w.getBlockAt(xHi + 1, GROUND_Y + y, z), wallMat(xHi + 1, z));
            }
        }
        // room2 side niches ±5
        int[] nicheZ = {20, 24, 28, 30};
        for (int nz : nicheZ) {
            for (int side : new int[]{-5, 5}) {
                n += set(w.getBlockAt(side, GROUND_Y - 1, nz), Material.COBBLESTONE);
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(side, GROUND_Y + y, nz), Material.AIR);
                }
                int wallX = side < 0 ? CW0 - 1 : CW1 + 1;
                for (int y = 0; y <= 2; y++) {
                    n += set(w.getBlockAt(wallX, GROUND_Y + y, nz), Material.AIR);
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
                n += set(w.getBlockAt(x, GROUND_Y - 1, z),
                        edge ? Material.LOG : floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.COBBLESTONE);
                    }
                    if (dz == -HALL_HALF && Math.abs(dx) <= 1) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                        }
                    }
                }
                n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.LOG);
            }
        }
        // center open + 1–2 scrap-cart cover posts
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y - 1, BOSS_Z + dz), Material.COBBLESTONE);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y, BOSS_Z + dz), Material.AIR);
                n += set(w.getBlockAt(BOSS_X + dx, GROUND_Y + 1, BOSS_Z + dz), Material.AIR);
            }
        }
        // scrap cover pillars
        for (int[] p : new int[][]{{-3, 44}, {3, 48}}) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.COBBLESTONE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.FENCE);
            n += set(w.getBlockAt(p[0], GROUND_Y + 1, p[1]), Material.FENCE);
            n += set(w.getBlockAt(p[0], GROUND_Y + 2, p[1]), Material.IRON_ORE);
        }
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int z = DOOR2_Z + 1; z < BOSS_Z - HALL_HALF; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.COBBLESTONE);
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
        if (h == 0 || h == 1) return Material.COBBLESTONE;
        if (h == 2) return Material.MOSSY_COBBLESTONE;
        if (h == 3) return Material.WOOD;
        if (h == 4) return Material.GRAVEL;
        return Material.COBBLESTONE;
    }

    private int placePillarsAndLights(World w) {
        int n = 0;
        // oak fence pillars along gallery
        int[] pillarZ = {4, 8, 12, 18, 22, 26, 30, 40, 46};
        for (int pz : pillarZ) {
            for (int side : new int[]{CW0, CW1}) {
                n += set(w.getBlockAt(side, GROUND_Y - 1, pz), Material.COBBLESTONE);
                n += set(w.getBlockAt(side, GROUND_Y, pz), Material.FENCE);
                n += set(w.getBlockAt(side, GROUND_Y + 1, pz), Material.FENCE);
                n += set(w.getBlockAt(side, GROUND_Y + 2, pz), Material.TORCH);
            }
        }
        int[][] lamps = {
                {-2, 1}, {2, 1},
                {-2, 8}, {2, 8},
                {-2, 12}, {2, 12},
                {6, 20}, {10, 20},
                {-2, 24}, {2, 28},
                {-2, 32}, {1, 32},
                {-4, 46}, {4, 46}, {0, 50}
        };
        for (int[] p : lamps) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.COBBLESTONE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.TORCH);
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.WOOD);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§6余烬窟·锈轨", "§7矿道安全区", "§e清尽再开闸", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.WOOD);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR1_Z - 1), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR1_Z - 1),
                "§e第一道闸", "§7清尽主巷", "§7后轨闸敞开", "§8→ 后段");
        n += set(w.getBlockAt(10, GROUND_Y - 1, 20), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(10, GROUND_Y, 20),
                "§8短支洞", "§7尽头封死", "§7可探可回", "§8非迷宫");
        n += set(w.getBlockAt(DOOR_X1 + 1, GROUND_Y - 1, DOOR2_Z - 1), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(DOOR_X1 + 1, GROUND_Y, DOOR2_Z - 1),
                "§eBoss闸", "§7清尽混编", "§7后入机房", "§8→ 尽端");
        n += set(w.getBlockAt(BOSS_X + 3, GROUND_Y - 1, BOSS_Z), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(BOSS_X + 3, GROUND_Y, BOSS_Z),
                "§6机房·矿监", "§7锈轨矿监", "§7通关回枢纽", "§8菜单亦可回");
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
                {W2F_X, GROUND_Y, W2F_Z},
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
                    if (p[0] == SPAWN_X && p[2] == SPAWN_Z) floor = Material.WOOD;
                    if (p[0] == BOSS_X && p[2] == BOSS_Z) floor = Material.COBBLESTONE;
                    n += set(w.getBlockAt(bx, feetY - 1, bz), floor);
                    // keep center rail if on main axis
                    if (!(bx == 0 && bz != DOOR1_Z && bz != DOOR2_Z && bz > Z0 && bz < BOSS_Z0)) {
                        n += set(w.getBlockAt(bx, feetY, bz), Material.AIR);
                    }
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
