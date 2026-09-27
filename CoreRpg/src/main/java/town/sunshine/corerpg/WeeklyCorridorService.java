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
 * P3 (2026-09-27): ember_weekly 深核廊 — 前厅→中核→深室，折角廊+高低差+掩体。
 * Admin: /corerpg weeklybuild
 * Dark theme: stone brick / nether brick / netherrack (distinct from elite quartz/gold).
 */
public class WeeklyCorridorService {

    static final String WORK_WORLD = "ember_weekly_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_weekly";

    static final int CX = -40;

    /** 前厅 feet Y / spawn */
    static final int R1_Y = 65;
    static final int SPAWN_X = -40;
    static final int SPAWN_Z = 270;
    /** 前厅战斗中心 / wave1 */
    static final int R1_Z = 278;
    static final int W1A_X = -40, W1A_Z = 280;
    static final int W1B_X = -42, W1B_Z = 282;

    /** 中核 raised */
    static final int R2_Y = 68;
    static final int R2_Z = 310;
    static final int W2A_X = -40, W2A_Z = 308;
    static final int W2B_X = -42, W2B_Z = 312;

    /** 深室 lowered */
    static final int R3_Y = 63;
    static final int R3_Z = 342;
    static final int W3_X = -40, W3_Z = 340;
    static final int BOSS_X = -40, BOSS_Z = 342;

    private final CoreRpgPlugin plugin;

    public WeeklyCorridorService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "weeklybuild".equals(a) || "corridor".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg weeklybuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "[weeklybuild] spawn=(" + SPAWN_X + "," + R1_Y + "," + SPAWN_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R1前厅 Y=" + R1_Y + " centerZ=" + R1_Z
                + " w1A=(" + W1A_X + "," + R1_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + R1_Y + "," + W1B_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R2中核 Y=" + R2_Y + " centerZ=" + R2_Z
                + " w2A=(" + W2A_X + "," + R2_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + R2_Y + "," + W2B_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R3深室 Y=" + R3_Y + " centerZ=" + R3_Z
                + " w3=(" + W3_X + "," + R3_Y + "," + W3_Z + ")"
                + " boss=(" + BOSS_X + "," + R3_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[weeklybuild] map missing: " + mapDir.getAbsolutePath());
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
            sender.sendMessage(ChatColor.RED + "[weeklybuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[weeklybuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[weeklybuild] 开始分批施工深核廊…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrubLegacy(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[weeklybuild] scrub → R1前厅");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildRoom(w, CX, R1_Y, R1_Z, 6, 8, false);
                        changed += clearPad(w, SPAWN_X, R1_Y, SPAWN_Z, 2);
                        changed += clearPad(w, W1A_X, R1_Y, W1A_Z, 2);
                        changed += clearPad(w, W1B_X, R1_Y, W1B_Z, 2);
                        changed += placeCover(w, CX - 4, R1_Y, R1_Z - 2, true);
                        changed += placeCover(w, CX + 4, R1_Y, R1_Z + 2, true);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[weeklybuild] R1 → 折角廊1");
                        return;
                    }
                    if (phase == 2) {
                        // dogleg west: R1 north exit → x=-52 → north → back to CX at R2
                        changed += buildDoglegWest(w, R1_Y, 287, R2_Y, 301);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[weeklybuild] 廊1 → R2中核");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildRoom(w, CX, R2_Y, R2_Z, 6, 8, true);
                        changed += clearPad(w, W2A_X, R2_Y, W2A_Z, 2);
                        changed += clearPad(w, W2B_X, R2_Y, W2B_Z, 2);
                        changed += placeCover(w, CX - 5, R2_Y, R2_Z, true);
                        changed += placeCover(w, CX + 5, R2_Y, R2_Z, true);
                        // raised dais in center
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                changed += set(w.getBlockAt(CX + dx, R2_Y - 1, R2_Z + dz), Material.NETHER_BRICK);
                            }
                        }
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[weeklybuild] R2 → 折角廊2");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildDoglegEast(w, R2_Y, 319, R3_Y, 333);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[weeklybuild] 廊2 → R3深室");
                        return;
                    }
                    if (phase == 5) {
                        changed += buildRoom(w, CX, R3_Y, R3_Z, 8, 9, true);
                        changed += clearPad(w, W3_X, R3_Y, W3_Z, 2);
                        changed += clearPad(w, BOSS_X, R3_Y, BOSS_Z, 2);
                        // boss pad
                        for (int dx = -2; dx <= 2; dx++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                Material m = (Math.abs(dx) + Math.abs(dz) <= 1) ? Material.OBSIDIAN : Material.NETHER_BRICK;
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y - 1, BOSS_Z + dz), m);
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y, BOSS_Z + dz), Material.AIR);
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y + 1, BOSS_Z + dz), Material.AIR);
                            }
                        }
                        changed += placeCover(w, CX - 6, R3_Y, R3_Z - 3, true);
                        changed += placeCover(w, CX + 6, R3_Y, R3_Z + 3, true);
                        phase = 6;
                        return;
                    }
                    if (phase == 6) {
                        changed += placeLights(w);
                        changed += placeSigns(w);
                        phase = 7;
                        return;
                    }
                    if (phase == 7) {
                        cancel();
                        w.setSpawnLocation(SPAWN_X, R1_Y, SPAWN_Z);
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
                            sender.sendMessage(ChatColor.RED + "[weeklybuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[weeklybuild] corridor done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + R1_Y + "," + SPAWN_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[weeklybuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("weeklybuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    /** Clear old small room around legacy spawn. */
    private int scrubLegacy(World w) {
        int n = 0;
        for (int x = -55; x <= -25; x++) {
            for (int z = 255; z <= 360; z++) {
                for (int y = 55; y <= 80; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // solid base slab under corridor band so we don't fall forever
                n += set(w.getBlockAt(x, 54, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildRoom(World w, int cx, int feetY, int cz, int halfX, int halfZ, boolean deepMat) {
        int n = 0;
        int fy = feetY - 1;
        for (int dx = -halfX; dx <= halfX; dx++) {
            for (int dz = -halfZ; dz <= halfZ; dz++) {
                boolean edge = Math.abs(dx) == halfX || Math.abs(dz) == halfZ;
                boolean corner = Math.abs(dx) == halfX && Math.abs(dz) == halfZ;
                Material floor = floorMat(deepMat, dx, dz);
                n += set(w.getBlockAt(cx + dx, fy, cz + dz), floor);
                n += set(w.getBlockAt(cx + dx, fy - 1, cz + dz), Material.STONE);
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), Material.AIR);
                }
                if (edge) {
                    Material wall = deepMat ? Material.NETHER_BRICK : Material.SMOOTH_BRICK;
                    if (corner) wall = Material.OBSIDIAN;
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), wall);
                    }
                    // doorway openings on north/south mid
                    if (Math.abs(dx) <= 1 && (dz == halfZ || dz == -halfZ)) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), Material.AIR);
                        }
                    }
                }
            }
        }
        // ceiling rim
        for (int dx = -halfX; dx <= halfX; dx++) {
            for (int dz = -halfZ; dz <= halfZ; dz++) {
                if (Math.abs(dx) == halfX || Math.abs(dz) == halfZ) {
                    n += set(w.getBlockAt(cx + dx, feetY + 4, cz + dz),
                            deepMat ? Material.NETHER_BRICK : Material.SMOOTH_BRICK);
                }
            }
        }
        return n;
    }

    private Material floorMat(boolean deep, int dx, int dz) {
        int h = (dx * 3 + dz * 5) & 7;
        if (deep) {
            if (h == 0) return Material.OBSIDIAN;
            if (h <= 2) return Material.NETHERRACK;
            return Material.NETHER_BRICK;
        }
        if (h == 0) return Material.COBBLESTONE;
        if (h == 1) return Material.NETHERRACK;
        if (h == 2) return Material.NETHER_BRICK;
        return Material.SMOOTH_BRICK;
    }

    /** West dogleg corridor with steps and half-wall cover. */
    private int buildDoglegWest(World w, int yFrom, int zFrom, int yTo, int zTo) {
        int n = 0;
        int jogX = CX - 12; // -52
        // segment A: north from room along CX
        for (int z = zFrom; z <= zFrom + 4; z++) {
            n += carveHallCell(w, CX, yFrom, z, true);
        }
        // turn west
        for (int x = CX; x >= jogX; x--) {
            n += carveHallCell(w, x, yFrom, zFrom + 4, true);
            // half-wall cover on south
            n += set(w.getBlockAt(x, yFrom, zFrom + 3), Material.NETHER_BRICK);
            n += set(w.getBlockAt(x, yFrom + 1, zFrom + 3), Material.NETHER_BRICK);
        }
        // north along jog with Y ramp
        int zMidStart = zFrom + 4;
        int zMidEnd = zTo - 4;
        int len = Math.max(1, zMidEnd - zMidStart);
        for (int z = zMidStart; z <= zMidEnd; z++) {
            double t = (z - zMidStart) / (double) len;
            int y = (int) Math.round(yFrom + (yTo - yFrom) * t);
            n += carveHallCell(w, jogX, y, z, true);
            // cover pillars every 3
            if ((z - zMidStart) % 3 == 0) {
                n += set(w.getBlockAt(jogX - 2, y, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(jogX - 2, y + 1, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(jogX - 2, y + 2, z), Material.TORCH);
            }
        }
        // turn back east to CX
        for (int x = jogX; x <= CX; x++) {
            n += carveHallCell(w, x, yTo, zTo - 4, true);
            n += set(w.getBlockAt(x, yTo, zTo - 3), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, yTo + 1, zTo - 3), Material.SMOOTH_BRICK);
        }
        // approach room
        for (int z = zTo - 4; z <= zTo; z++) {
            n += carveHallCell(w, CX, yTo, z, true);
        }
        return n;
    }

    /** East dogleg (opposite fold). */
    private int buildDoglegEast(World w, int yFrom, int zFrom, int yTo, int zTo) {
        int n = 0;
        int jogX = CX + 12; // -28
        for (int z = zFrom; z <= zFrom + 4; z++) {
            n += carveHallCell(w, CX, yFrom, z, true);
        }
        for (int x = CX; x <= jogX; x++) {
            n += carveHallCell(w, x, yFrom, zFrom + 4, true);
            n += set(w.getBlockAt(x, yFrom, zFrom + 3), Material.NETHER_BRICK);
            n += set(w.getBlockAt(x, yFrom + 1, zFrom + 3), Material.NETHER_BRICK);
        }
        int zMidStart = zFrom + 4;
        int zMidEnd = zTo - 4;
        int len = Math.max(1, zMidEnd - zMidStart);
        for (int z = zMidStart; z <= zMidEnd; z++) {
            double t = (z - zMidStart) / (double) len;
            int y = (int) Math.round(yFrom + (yTo - yFrom) * t);
            n += carveHallCell(w, jogX, y, z, true);
            if ((z - zMidStart) % 3 == 0) {
                n += set(w.getBlockAt(jogX + 2, y, z), Material.NETHER_BRICK);
                n += set(w.getBlockAt(jogX + 2, y + 1, z), Material.NETHER_BRICK);
                n += set(w.getBlockAt(jogX + 2, y + 2, z), Material.TORCH);
            }
        }
        for (int x = jogX; x >= CX; x--) {
            n += carveHallCell(w, x, yTo, zTo - 4, true);
            n += set(w.getBlockAt(x, yTo, zTo - 3), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, yTo + 1, zTo - 3), Material.SMOOTH_BRICK);
        }
        for (int z = zTo - 4; z <= zTo; z++) {
            n += carveHallCell(w, CX, yTo, z, true);
        }
        return n;
    }

    private int carveHallCell(World w, int x, int feetY, int z, boolean dark) {
        int n = 0;
        int fy = feetY - 1;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                boolean wall = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                if (wall && !(Math.abs(dx) <= 1 || Math.abs(dz) <= 1)) {
                    // corner of 5x5 — wall
                }
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    Material wm = dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK;
                    for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), wm);
                    n += set(w.getBlockAt(x + dx, fy, z + dz), wm);
                    continue;
                }
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    // side walls but leave tunnel axis open — only wall the perpendicular sides lightly
                    if (Math.abs(dx) == 2 && Math.abs(dz) <= 1) {
                        Material wm = dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK;
                        for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), wm);
                        n += set(w.getBlockAt(x + dx, fy, z + dz), wm);
                        continue;
                    }
                    if (Math.abs(dz) == 2 && Math.abs(dx) <= 1) {
                        Material wm = dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK;
                        for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), wm);
                        n += set(w.getBlockAt(x + dx, fy, z + dz), wm);
                        continue;
                    }
                }
                n += set(w.getBlockAt(x + dx, fy, z + dz), dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x + dx, feetY + y, z + dz), Material.AIR);
                }
            }
        }
        // force open center 3x3
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(x + dx, fy, z + dz), dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x + dx, feetY, z + dz), Material.AIR);
                n += set(w.getBlockAt(x + dx, feetY + 1, z + dz), Material.AIR);
                n += set(w.getBlockAt(x + dx, feetY + 2, z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int clearPad(World w, int x, int feetY, int z, int r) {
        int n = 0;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                n += set(w.getBlockAt(x + dx, feetY - 1, z + dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x + dx, feetY, z + dz), Material.AIR);
                n += set(w.getBlockAt(x + dx, feetY + 1, z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int placeCover(World w, int x, int feetY, int z, boolean dark) {
        int n = 0;
        Material m = dark ? Material.NETHER_BRICK : Material.SMOOTH_BRICK;
        for (int y = 0; y <= 1; y++) {
            n += set(w.getBlockAt(x, feetY + y, z), m);
            n += set(w.getBlockAt(x + 1, feetY + y, z), m);
        }
        n += set(w.getBlockAt(x, feetY + 2, z), Material.TORCH);
        return n;
    }

    private int placeLights(World w) {
        int n = 0;
        int[][] pts = {
                {SPAWN_X, R1_Y, SPAWN_Z},
                {W1A_X, R1_Y, W1A_Z},
                {W2A_X, R2_Y, W2A_Z},
                {BOSS_X, R3_Y, BOSS_Z},
                {CX, R1_Y, R1_Z},
                {CX, R2_Y, R2_Z},
                {CX, R3_Y, R3_Z}
        };
        for (int[] p : pts) {
            n += set(w.getBlockAt(p[0] + 3, p[1], p[2]), Material.SEA_LANTERN);
            n += set(w.getBlockAt(p[0] - 3, p[1], p[2]), Material.GLOWSTONE);
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        // spawn: hub return + theme
        n += set(w.getBlockAt(SPAWN_X + 2, R1_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, R1_Y, SPAWN_Z),
                "§c深核·周", "§7前厅→中核→深室", "§7清波推进", "§8折角廊有掩体");
        n += set(w.getBlockAt(SPAWN_X - 2, R1_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, R1_Y, SPAWN_Z),
                "§6回枢纽", "§7通关后自动回城", "§7或打开菜单", "§8→ 回枢纽");
        // mid room
        n += set(w.getBlockAt(CX + 3, R2_Y - 1, R2_Z), Material.NETHER_BRICK);
        n += writeSign(w.getBlockAt(CX + 3, R2_Y, R2_Z),
                "§c中核", "§7混合增压", "§7清完进深室", "§8注意远程");
        // deep room
        n += set(w.getBlockAt(CX + 4, R3_Y - 1, R3_Z), Material.OBSIDIAN);
        n += writeSign(w.getBlockAt(CX + 4, R3_Y, R3_Z),
                "§4深室·终局", "§7蛮兵在此", "§6通关回枢纽", "§8菜单亦可回");
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
