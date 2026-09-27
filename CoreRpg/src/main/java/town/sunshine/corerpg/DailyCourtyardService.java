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
 * P4 (2026-09-27): ember_daily 灰烬庭院 — 四面回廊 + 中央抬高 Boss 垫 + 出入拱门。
 * Open / half-open courtyard (~32×32), distinct from weekly dark corridor / elite quartz.
 * Admin: /corerpg dailybuild
 *
 * Local coords (design):
 *   spawn 0,65,0 (south arch inside, face +Z toward pad)
 *   boss pad center 0,66,12 (pad surface feet Y=66)
 *   wave1 corridor open cells; wave2 far corridor; boss pad only
 */
public class DailyCourtyardService {

    static final String WORK_WORLD = "ember_daily_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily";

    /** Feet Y on courtyard ground / corridor */
    static final int GROUND_Y = 65;
    /** Feet Y on raised boss pad (+1) */
    static final int PAD_Y = 66;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    /** Outer courtyard bounds inclusive (~32×32) */
    static final int X0 = -16;
    static final int X1 = 15;
    static final int Z0 = -4;
    static final int Z1 = 27;

    /** Boss pad half-extent → ~8×8 centered (0,12) */
    static final int BOSS_X = 0;
    static final int BOSS_Z = 12;
    static final int PAD_HALF = 4; // x/z from -4..3 relative → 8 wide

    /** Wave spawn open cells (corridor) */
    static final int W1_X = -13, W1_Z = 6;
    static final int W2A_X = -13, W2A_Z = 24;
    static final int W2B_X = 12, W2B_Z = 24;

    private final CoreRpgPlugin plugin;

    public DailyCourtyardService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "dailybuild".equals(a) || "courtyard".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg dailybuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[dailybuild] spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1
                + " · padCenter=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w1=(" + W1_X + "," + GROUND_Y + "," + W1_Z + ")"
                + " w2A=(" + W2A_X + "," + GROUND_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + GROUND_Y + "," + W2B_Z + ")"
                + " boss=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[dailybuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[dailybuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[dailybuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[dailybuild] 开始分批施工灰烬庭院…");

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
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] scrub → 底板+外墙");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildFloorAndWalls(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 外墙 → 回廊柱廊");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildColonnade(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 柱廊 → 中央 Boss 垫");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildBossPad(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] Boss垫 → 拱门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildArches(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 拱门 → 灯/火盆/告示");
                        return;
                    }
                    if (phase == 5) {
                        changed += placeBraziersAndLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
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
                            sender.sendMessage(ChatColor.RED + "[dailybuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[dailybuild] courtyard done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                                + " · pad=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[dailybuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("dailybuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    /** Clear volume for new courtyard around origin. */
    private int scrubBuildVolume(World w) {
        int n = 0;
        for (int x = X0 - 4; x <= X1 + 4; x++) {
            for (int z = Z0 - 4; z <= Z1 + 4; z++) {
                for (int y = 54; y <= 80; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 53, z), Material.STONE);
            }
        }
        return n;
    }

    /** Clear legacy ~5×5 room near old spawn so leftover never confuses. */
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

    private int buildFloorAndWalls(World w) {
        int n = 0;
        int fy = GROUND_Y - 1; // 64
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean edge = x == X0 || x == X1 || z == Z0 || z == Z1;
                boolean corner = (x == X0 || x == X1) && (z == Z0 || z == Z1);
                // floor mix: stone / gravel / cobble / occasional netherrack — not flat same material
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                // clear air column (open sky)
                for (int y = 0; y <= 8; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    Material wall = corner ? Material.COBBLESTONE : Material.SMOOTH_BRICK;
                    int h = corner ? 5 : 4;
                    for (int y = 0; y < h; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wall);
                    }
                    // rim / embattlement on top (partial cover, not full roof)
                    n += set(w.getBlockAt(x, GROUND_Y + h, z),
                            corner ? Material.COBBLESTONE : Material.SMOOTH_BRICK);
                }
            }
        }
        // partial corner roofs (3×3 lintels) for half-open feel — NOT full ceiling
        int[][] roofCorners = {
                {X0 + 1, Z0 + 1}, {X1 - 3, Z0 + 1},
                {X0 + 1, Z1 - 3}, {X1 - 3, Z1 - 3}
        };
        for (int[] c : roofCorners) {
            for (int dx = 0; dx <= 2; dx++) {
                for (int dz = 0; dz <= 2; dz++) {
                    n += set(w.getBlockAt(c[0] + dx, GROUND_Y + 4, c[1] + dz), Material.SMOOTH_BRICK);
                }
            }
        }
        return n;
    }

    private Material floorMat(int x, int z) {
        int h = (x * 3 + z * 7) & 7;
        if (h == 0) return Material.GRAVEL;
        if (h == 1) return Material.COBBLESTONE;
        if (h == 2) return Material.NETHERRACK;
        if (h == 3) return Material.GRAVEL;
        return Material.STONE;
    }

    /** Inner colonnade defining corridor ~3 wide along four sides. */
    private int buildColonnade(World w) {
        int n = 0;
        // corridor inner edge: 3 blocks in from outer wall
        int ix0 = X0 + 4; // -12
        int ix1 = X1 - 4; // 11
        int iz0 = Z0 + 4; // 0
        int iz1 = Z1 - 4; // 23
        // pillars every 3 along the inner ring; skip arch midlines so spawn/north stay open
        for (int x = ix0; x <= ix1; x += 3) {
            if (x >= -2 && x <= 1) continue; // south/north arch gap
            n += pillar(w, x, iz0);
            n += pillar(w, x, iz1);
        }
        for (int z = iz0 + 3; z <= iz1 - 3; z += 3) {
            n += pillar(w, ix0, z);
            n += pillar(w, ix1, z);
        }
        // ensure corridor walkway (between wall and pillars) is open + solid floor
        for (int x = X0 + 1; x <= X1 - 1; x++) {
            for (int z = Z0 + 1; z <= Z1 - 1; z++) {
                boolean inOuterRing =
                        x <= X0 + 3 || x >= X1 - 3 || z <= Z0 + 3 || z >= Z1 - 3;
                if (!inOuterRing) continue;
                // skip if we just placed a pillar cell
                Block b = w.getBlockAt(x, GROUND_Y, z);
                if (b.getType() != Material.AIR && b.getType() != Material.TORCH) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                n += set(w.getBlockAt(x, GROUND_Y, z), Material.AIR);
                n += set(w.getBlockAt(x, GROUND_Y + 1, z), Material.AIR);
                n += set(w.getBlockAt(x, GROUND_Y + 2, z), Material.AIR);
            }
        }
        // open court center floor (between pillars) — gravel/stone mix, same Y (not one flat plate only)
        for (int x = ix0 + 1; x <= ix1 - 1; x++) {
            for (int z = iz0 + 1; z <= iz1 - 1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int pillar(World w, int x, int z) {
        int n = 0;
        Material base = Material.SMOOTH_BRICK;
        n += set(w.getBlockAt(x, GROUND_Y - 1, z), base);
        for (int y = 0; y <= 3; y++) {
            n += set(w.getBlockAt(x, GROUND_Y + y, z), base);
        }
        n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE); // capital
        // occasional torch on side
        if (((x + z) & 1) == 0) {
            n += set(w.getBlockAt(x + 1, GROUND_Y + 2, z), Material.TORCH);
        }
        return n;
    }

    private int buildBossPad(World w) {
        int n = 0;
        // pad top surface at y=65 (feet 66); ground elsewhere feet 65
        int padTop = PAD_Y - 1; // 65
        for (int dx = -PAD_HALF; dx < PAD_HALF; dx++) {
            for (int dz = -PAD_HALF; dz < PAD_HALF; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                boolean edge = Math.abs(dx) == PAD_HALF - 1 || Math.abs(dz) == PAD_HALF - 1
                        || dx == -PAD_HALF || dz == -PAD_HALF;
                // fill under pad
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.STONE);
                Material top = edge ? Material.SMOOTH_BRICK : Material.COBBLESTONE;
                if ((dx == 0 && dz == 0) || (Math.abs(dx) + Math.abs(dz) <= 1)) {
                    top = Material.NETHERRACK; // ash heart
                }
                n += set(w.getBlockAt(x, padTop, z), top);
                // clear pad air
                n += set(w.getBlockAt(x, PAD_Y, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 1, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 2, z), Material.AIR);
            }
        }
        // step ring at ground level around pad (stairs into pad) — stone slab feel via full blocks cut as steps
        // four mid-side step blocks at GROUND_Y (=65) as approach
        int[][] steps = {
                {BOSS_X, BOSS_Z - PAD_HALF - 1},
                {BOSS_X, BOSS_Z + PAD_HALF},
                {BOSS_X - PAD_HALF - 1, BOSS_Z},
                {BOSS_X + PAD_HALF, BOSS_Z}
        };
        for (int[] s : steps) {
            n += set(w.getBlockAt(s[0], GROUND_Y - 1, s[1]), Material.STONE);
            n += set(w.getBlockAt(s[0], GROUND_Y, s[1]), Material.SMOOTH_BRICK); // step up
            n += set(w.getBlockAt(s[0], GROUND_Y + 1, s[1]), Material.AIR);
            n += set(w.getBlockAt(s[0], GROUND_Y + 2, s[1]), Material.AIR);
        }
        return n;
    }

    /** South main entrance arch (spawn) + north evacuation visual arch. */
    private int buildArches(World w) {
        int n = 0;
        // SOUTH arch at z=Z0, opening x=-2..1 (4 wide), height 3
        for (int x = -2; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.AIR);
            }
        }
        // lintel
        for (int x = -3; x <= 2; x++) {
            n += set(w.getBlockAt(x, GROUND_Y + 3, Z0), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, GROUND_Y + 4, Z0), Material.COBBLESTONE);
        }
        // pillars flanking arch
        for (int y = 0; y <= 4; y++) {
            n += set(w.getBlockAt(-3, GROUND_Y + y, Z0), Material.COBBLESTONE);
            n += set(w.getBlockAt(2, GROUND_Y + y, Z0), Material.COBBLESTONE);
        }
        // small porch outside south (spawn approach floor)
        for (int x = -2; x <= 1; x++) {
            for (int z = Z0 - 2; z <= Z0 - 1; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x, GROUND_Y, z), Material.AIR);
                n += set(w.getBlockAt(x, GROUND_Y + 1, z), Material.AIR);
            }
        }
        // ensure spawn cell open
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y - 1, SPAWN_Z + dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y, SPAWN_Z + dz), Material.AIR);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y + 1, SPAWN_Z + dz), Material.AIR);
            }
        }

        // NORTH arch (evac visual) at z=Z1
        for (int x = -2; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z1), Material.AIR);
            }
        }
        for (int x = -3; x <= 2; x++) {
            n += set(w.getBlockAt(x, GROUND_Y + 3, Z1), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, GROUND_Y + 4, Z1), Material.COBBLESTONE);
        }
        for (int y = 0; y <= 4; y++) {
            n += set(w.getBlockAt(-3, GROUND_Y + y, Z1), Material.COBBLESTONE);
            n += set(w.getBlockAt(2, GROUND_Y + y, Z1), Material.COBBLESTONE);
        }
        return n;
    }

    private int placeBraziersAndLights(World w) {
        int n = 0;
        // braziers: netherrack pedestal + glowstone (1.12-safe, no fire spread)
        int[][] braziers = {
                {-14, 2}, {13, 2}, {-14, 24}, {13, 24},
                {-8, 12}, {7, 12}, {0, 4}, {0, 20}
        };
        for (int[] p : braziers) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.COBBLESTONE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.NETHERRACK);
            n += set(w.getBlockAt(p[0], GROUND_Y + 1, p[1]), Material.GLOWSTONE);
        }
        // sea lantern accents near spawn / pad
        n += set(w.getBlockAt(SPAWN_X + 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(SPAWN_X - 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(BOSS_X + 5, PAD_Y, BOSS_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(BOSS_X - 5, PAD_Y, BOSS_Z), Material.GLOWSTONE);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        // spawn: theme + hub return (no /dp /hub teach)
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§6灰烬庭院", "§7回廊清潮", "§7后上中央垫", "§8露天·庭院");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        // pad edge
        n += set(w.getBlockAt(BOSS_X + 5, PAD_Y - 1, BOSS_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(BOSS_X + 5, PAD_Y, BOSS_Z),
                "§c中央垫", "§7蛮兵在此", "§7通关回枢纽", "§8菜单亦可回");
        // north arch
        n += set(w.getBlockAt(2, GROUND_Y - 1, Z1 - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(2, GROUND_Y, Z1 - 1),
                "§e回枢纽", "§7通关后自动", "§7或开菜单返回", "§8→ 枢纽");
        return n;
    }

    private int clearSpawnPads(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, GROUND_Y, SPAWN_Z},
                {W1_X, GROUND_Y, W1_Z},
                {W2A_X, GROUND_Y, W2A_Z},
                {W2B_X, GROUND_Y, W2B_Z},
                {BOSS_X, PAD_Y, BOSS_Z}
        };
        for (int[] p : pts) {
            int feetY = p[1];
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Material floor = (feetY == PAD_Y) ? Material.COBBLESTONE : Material.SMOOTH_BRICK;
                    if (p[0] == W1_X || p[0] == W2A_X || p[0] == W2B_X) {
                        floor = floorMat(p[0] + dx, p[2] + dz);
                    }
                    n += set(w.getBlockAt(p[0] + dx, feetY - 1, p[2] + dz), floor);
                    n += set(w.getBlockAt(p[0] + dx, feetY, p[2] + dz), Material.AIR);
                    n += set(w.getBlockAt(p[0] + dx, feetY + 1, p[2] + dz), Material.AIR);
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
