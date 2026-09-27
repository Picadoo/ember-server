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
 * P5 (2026-09-27): ember_raid 团本大厅→通道→终厅。
 * 大厅 ~28×28 亮石砖+旗；通道偏暗圆石宽 7～9 + 左右真龛；终厅 ~24×24 Boss 垫。
 * Admin: /corerpg raidbuild
 *
 * Local:
 *   spawn 0,65,0 · wave1 left niche · wave2 right niche · wave3 converge · boss 40,65,0
 */
public class RaidHallService {

    static final String WORK_WORLD = "ember_raid_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_raid";

    static final int GROUND_Y = 65;
    static final int PAD_Y = 66; // boss pad +1

    static final int SPAWN_X = 0, SPAWN_Z = 0;

    /** Hall bounds inclusive (~28×28) */
    static final int HX0 = -14, HX1 = 13, HZ0 = -14, HZ1 = 13;

    /** Corridor: +X from hall edge, width z=-4..4 (9), length to boss hall door */
    static final int CX0 = 14, CX1 = 27;
    static final int CZ0 = -4, CZ1 = 4;

    /** Side niches (real left/right space) */
    static final int NICHE_Z = 8; // niches extend to z=±8

    /** Boss hall (~24×24) centered ~40 */
    static final int BX0 = 28, BX1 = 51, BZ0 = -12, BZ1 = 11;
    static final int BOSS_X = 40, BOSS_Z = 0;

    /** Wave open cells */
    static final int W1_X = 18, W1_Z = -7; // left (-Z) niche
    static final int W2_X = 22, W2_Z = 7;  // right (+Z) niche
    static final int W3_X = 30, W3_Z = 0;  // converge at hall door

    /** GuildBoss open cells (same map) */
    static final int G1A_X = 4, G1A_Z = -6;
    static final int G1B_X = 4, G1B_Z = 6;
    static final int G2_X = 20, G2_Z = 0;
    static final int GBOSS_X = 40, GBOSS_Z = 0;

    private final CoreRpgPlugin plugin;

    public RaidHallService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "raidbuild".equals(a) || "hall".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg raidbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "[raidbuild] spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  hall x=" + HX0 + ".." + HX1 + " z=" + HZ0 + ".." + HZ1
                + " · corr x=" + CX0 + ".." + CX1 + " · bossHall x=" + BX0 + ".." + BX1);
        sender.sendMessage(ChatColor.GRAY + "  w1L=(" + W1_X + "," + GROUND_Y + "," + W1_Z + ")"
                + " w2R=(" + W2_X + "," + GROUND_Y + "," + W2_Z + ")"
                + " w3=(" + W3_X + "," + GROUND_Y + "," + W3_Z + ")"
                + " boss=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  guild w1A=(" + G1A_X + "," + GROUND_Y + "," + G1A_Z + ")"
                + " w1B=(" + G1B_X + "," + GROUND_Y + "," + G1B_Z + ")"
                + " w2=(" + G2_X + "," + GROUND_Y + "," + G2_Z + ")"
                + " boss=(" + GBOSS_X + "," + PAD_Y + "," + GBOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[raidbuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[raidbuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[raidbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[raidbuild] 开始分批施工团本大厅通道…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrubBuildVolume(w);
                        changed += scrubLegacyRoom(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[raidbuild] scrub → 大厅");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildHall(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[raidbuild] 大厅 → 通道+龛");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildCorridor(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[raidbuild] 通道 → 终厅");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildBossHall(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[raidbuild] 终厅 → 灯/旗/告示");
                        return;
                    }
                    if (phase == 4) {
                        changed += placeFlagsAndLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        phase = 5;
                        return;
                    }
                    if (phase == 5) {
                        cancel();
                        w.setSpawnLocation(SPAWN_X, GROUND_Y, SPAWN_Z);
                        w.save();
                        for (org.bukkit.Chunk c : w.getLoadedChunks()) c.unload(true);
                        boolean unloaded = Bukkit.unloadWorld(w, true);
                        try {
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
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.RED + "[raidbuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[raidbuild] hall done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · boss=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL);
                        dumpTable(sender);
                        plugin.getLogger().info("raidbuild changed≈" + changed + " exported to " + MAP_REL);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[raidbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("raidbuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    private int scrubBuildVolume(World w) {
        int n = 0;
        for (int x = HX0 - 4; x <= BX1 + 4; x++) {
            for (int z = Math.min(HZ0, BZ0) - 4; z <= Math.max(HZ1, BZ1) + 4; z++) {
                for (int y = 54; y <= 80; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 53, z), Material.STONE);
            }
        }
        return n;
    }

    private int scrubLegacyRoom(World w) {
        int n = 0;
        for (int x = -50; x <= -30; x++) {
            for (int z = 260; z <= 280; z++) {
                for (int y = 60; y <= 75; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    /** Bright stone-brick hall with partial roof / flags. */
    private int buildHall(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = HX0; x <= HX1; x++) {
            for (int z = HZ0; z <= HZ1; z++) {
                boolean edge = x == HX0 || x == HX1 || z == HZ0 || z == HZ1;
                boolean eastDoor = x == HX1 && z >= CZ0 && z <= CZ1; // corridor mouth
                n += set(w.getBlockAt(x, fy, z), hallFloor(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 6; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge && !eastDoor) {
                    Material wall = Material.SMOOTH_BRICK;
                    int h = ((x == HX0 || x == HX1) && (z == HZ0 || z == HZ1)) ? 5 : 4;
                    for (int y = 0; y < h; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wall);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + h, z), Material.SEA_LANTERN);
                }
            }
        }
        // open east into corridor
        for (int z = CZ0; z <= CZ1; z++) {
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(HX1, GROUND_Y + y, z), Material.AIR);
            }
            n += set(w.getBlockAt(HX1, GROUND_Y + 4, z), Material.SMOOTH_BRICK);
        }
        // partial bright roof strips (not full box)
        for (int x = HX0 + 2; x <= HX1 - 2; x += 4) {
            for (int z = HZ0 + 2; z <= HZ1 - 2; z += 4) {
                n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x + 1, GROUND_Y + 5, z), Material.GLOWSTONE);
            }
        }
        // spawn carpet feel
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                n += set(w.getBlockAt(SPAWN_X + dx, fy, SPAWN_Z + dz), Material.SMOOTH_BRICK);
            }
        }
        return n;
    }

    private Material hallFloor(int x, int z) {
        int h = (x * 3 + z * 7) & 7;
        if (h == 0) return Material.STONE;
        if (h == 1) return Material.QUARTZ_BLOCK;
        return Material.SMOOTH_BRICK;
    }

    /** Dark cobble corridor + left/right niches. */
    private int buildCorridor(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = CX0; x <= CX1; x++) {
            for (int z = CZ0; z <= CZ1; z++) {
                boolean edge = z == CZ0 || z == CZ1;
                n += set(w.getBlockAt(x, fy, z), corrFloor(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.COBBLESTONE);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE);
                }
            }
            // roof every other
            if ((x & 1) == 0) {
                for (int z = CZ0 + 1; z <= CZ1 - 1; z++) {
                    n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE);
                }
            }
        }
        // left niche (-Z) around W1
        n += buildNiche(w, W1_X, -NICHE_Z, true);
        // right niche (+Z) around W2
        n += buildNiche(w, W2_X, NICHE_Z, false);
        // punch openings from corridor into niches
        for (int x = W1_X - 1; x <= W1_X + 1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, CZ0), Material.AIR);
            }
        }
        for (int x = W2_X - 1; x <= W2_X + 1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, CZ1), Material.AIR);
            }
        }
        return n;
    }

    private int buildNiche(World w, int cx, int nicheZ, boolean left) {
        int n = 0;
        int fy = GROUND_Y - 1;
        int z0 = left ? nicheZ : CZ1;
        int z1 = left ? CZ0 : nicheZ;
        if (z0 > z1) { int t = z0; z0 = z1; z1 = t; }
        for (int x = cx - 3; x <= cx + 3; x++) {
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == cx - 3 || x == cx + 3 || z == nicheZ;
                n += set(w.getBlockAt(x, fy, z), Material.COBBLESTONE);
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.COBBLESTONE);
                    }
                }
            }
        }
        // open pad at niche center
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(cx + dx, fy, nicheZ + (left ? 1 : -1) * Math.abs(dz == 0 ? 0 : 0)), Material.COBBLESTONE);
            }
        }
        n += set(w.getBlockAt(cx, fy, nicheZ + (left ? 1 : -1)), Material.NETHERRACK);
        n += set(w.getBlockAt(cx, GROUND_Y, nicheZ + (left ? 1 : -1)), Material.GLOWSTONE);
        return n;
    }

    private Material corrFloor(int x, int z) {
        int h = (x * 2 + z * 5) & 3;
        if (h == 0) return Material.STONE;
        if (h == 1) return Material.NETHERRACK;
        return Material.COBBLESTONE;
    }

    /** Boss hall — calamity-inspired quartz pad, but room layout (not basin). */
    private int buildBossHall(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = BX0; x <= BX1; x++) {
            for (int z = BZ0; z <= BZ1; z++) {
                boolean edge = x == BX0 || x == BX1 || z == BZ0 || z == BZ1;
                boolean westDoor = x == BX0 && z >= CZ0 && z <= CZ1;
                n += set(w.getBlockAt(x, fy, z), bossFloor(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 6; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge && !westDoor) {
                    Material wall = Material.SMOOTH_BRICK;
                    if ((x + z) % 3 == 0) wall = Material.QUARTZ_BLOCK;
                    for (int y = 0; y <= 4; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wall);
                    }
                }
            }
        }
        // west door from corridor
        for (int z = CZ0; z <= CZ1; z++) {
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(BX0, GROUND_Y + y, z), Material.AIR);
            }
        }
        // raised boss pad ~9×9
        int half = 4;
        int padTop = PAD_Y - 1;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                int x = BOSS_X + dx, z = BOSS_Z + dz;
                boolean edge = Math.abs(dx) == half || Math.abs(dz) == half;
                boolean heart = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                n += set(w.getBlockAt(x, fy, z), Material.SMOOTH_BRICK);
                Material top = heart ? Material.NETHERRACK : (edge ? Material.QUARTZ_BLOCK : Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(x, padTop, z), top);
                n += set(w.getBlockAt(x, PAD_Y, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 1, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 2, z), Material.AIR);
            }
        }
        // steps onto pad
        int[][] steps = {
                {BOSS_X, BOSS_Z - half - 1},
                {BOSS_X, BOSS_Z + half + 1},
                {BOSS_X - half - 1, BOSS_Z},
                {BOSS_X + half + 1, BOSS_Z}
        };
        for (int[] s : steps) {
            n += set(w.getBlockAt(s[0], fy, s[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(s[0], GROUND_Y, s[1]), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(s[0], GROUND_Y + 1, s[1]), Material.AIR);
        }
        // corner braziers
        int[][] br = {
                {BX0 + 2, BZ0 + 2}, {BX1 - 2, BZ0 + 2},
                {BX0 + 2, BZ1 - 2}, {BX1 - 2, BZ1 - 2}
        };
        for (int[] p : br) {
            n += set(w.getBlockAt(p[0], fy, p[1]), Material.COBBLESTONE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.NETHERRACK);
            n += set(w.getBlockAt(p[0], GROUND_Y + 1, p[1]), Material.GLOWSTONE);
        }
        return n;
    }

    private Material bossFloor(int x, int z) {
        int h = (x * 3 + z * 5) & 7;
        if (h == 0) return Material.SOUL_SAND;
        if (h == 1) return Material.NETHERRACK;
        if (h == 2) return Material.QUARTZ_BLOCK;
        return Material.SMOOTH_BRICK;
    }

    private int placeFlagsAndLights(World w) {
        int n = 0;
        // hall flag pillars (wool as banner stand-ins — 1.12 safe)
        int[][] flags = {
                {-10, -10}, {9, -10}, {-10, 9}, {9, 9},
                {-6, 0}, {6, 0}, {0, -8}, {0, 8}
        };
        for (int[] p : flags) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], GROUND_Y + 1, p[1]), Material.WOOL);
            n += set(w.getBlockAt(p[0], GROUND_Y + 2, p[1]), Material.WOOL);
            n += set(w.getBlockAt(p[0], GROUND_Y + 3, p[1]), Material.GLOWSTONE);
        }
        // corridor torches via glowstone niches
        for (int x = CX0 + 2; x <= CX1 - 2; x += 3) {
            n += set(w.getBlockAt(x, GROUND_Y + 2, CZ0 + 1), Material.TORCH);
            n += set(w.getBlockAt(x, GROUND_Y + 2, CZ1 - 1), Material.TORCH);
        }
        n += set(w.getBlockAt(SPAWN_X + 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(SPAWN_X - 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§9团本大厅", "§7左道卫兵", "§7右道射手", "§7汇合后终厅");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7返回", "§8菜单撤离");
        n += set(w.getBlockAt(W1_X, GROUND_Y - 1, W1_Z - 1), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(W1_X, GROUND_Y, W1_Z - 1),
                "§9左道", "§7卫兵", "§7清潮", "§8—Z龛");
        n += set(w.getBlockAt(W2_X, GROUND_Y - 1, W2_Z + 1), Material.COBBLESTONE);
        n += writeSign(w.getBlockAt(W2_X, GROUND_Y, W2_Z + 1),
                "§9右道", "§7射手", "§7压远程", "§8+Z龛");
        n += set(w.getBlockAt(BOSS_X + 5, PAD_Y - 1, BOSS_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(BOSS_X + 5, PAD_Y, BOSS_Z),
                "§4终厅", "§7使徒垫", "§7通关回枢纽", "§8菜单亦可");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, GROUND_Y, SPAWN_Z},
                {W1_X, GROUND_Y, W1_Z},
                {W2_X, GROUND_Y, W2_Z},
                {W3_X, GROUND_Y, W3_Z},
                {BOSS_X, PAD_Y, BOSS_Z},
                {G1A_X, GROUND_Y, G1A_Z},
                {G1B_X, GROUND_Y, G1B_Z},
                {G2_X, GROUND_Y, G2_Z}
        };
        for (int[] p : pts) {
            int feet = p[1];
            Material floor = (feet == PAD_Y) ? Material.QUARTZ_BLOCK : Material.SMOOTH_BRICK;
            if (p[0] == W1_X || p[0] == W2_X || p[0] == W3_X || p[0] == G2_X) {
                floor = Material.COBBLESTONE;
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    n += set(w.getBlockAt(p[0] + dx, feet - 1, p[2] + dz), floor);
                    n += set(w.getBlockAt(p[0] + dx, feet, p[2] + dz), Material.AIR);
                    n += set(w.getBlockAt(p[0] + dx, feet + 1, p[2] + dz), Material.AIR);
                    n += set(w.getBlockAt(p[0] + dx, feet + 2, p[2] + dz), Material.AIR);
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
