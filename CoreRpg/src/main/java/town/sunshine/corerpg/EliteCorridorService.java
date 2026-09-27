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
 * P3 (2026-09-27): ember_elite 试炼短廊 — 三厅，石英/金砖中轴（与周本深核暗色可辨）。
 * Axis along +X (weekly is +Z) for layout distinction.
 * Admin: /corerpg elitebuild
 * B1.3 2026-09-27: seal R3 east door (was void cliff) + floor-embed lights.
 */
public class EliteCorridorService {

    static final String WORK_WORLD = "ember_elite_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_elite";

    static final int CZ = 270;

    /** 厅一 */
    static final int R1_Y = 70;
    static final int SPAWN_X = -35;
    static final int SPAWN_Z = 270;
    static final int R1_X = -30;
    static final int W1A_X = -30, W1A_Z = 270;
    static final int W1B_X = -28, W1B_Z = 272;

    /** 厅二 raised */
    static final int R2_Y = 72;
    static final int R2_X = -4;
    static final int W2A_X = -4, W2A_Z = 270;
    static final int W2B_X = -6, W2B_Z = 268;

    /** 厅三 lowered */
    static final int R3_Y = 68;
    static final int R3_X = 22;
    static final int W3_X = 22, W3_Z = 270;
    static final int BOSS_X = 24, BOSS_Z = 270;

    private final CoreRpgPlugin plugin;

    public EliteCorridorService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "elitebuild".equals(a) || "corridor".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg elitebuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[elitebuild] spawn=(" + SPAWN_X + "," + R1_Y + "," + SPAWN_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R1厅一 Y=" + R1_Y + " centerX=" + R1_X
                + " w1A=(" + W1A_X + "," + R1_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + R1_Y + "," + W1B_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R2厅二 Y=" + R2_Y + " centerX=" + R2_X
                + " w2A=(" + W2A_X + "," + R2_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + R2_Y + "," + W2B_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  R3厅三 Y=" + R3_Y + " centerX=" + R3_X
                + " w3=(" + W3_X + "," + R3_Y + "," + W3_Z + ")"
                + " boss=(" + BOSS_X + "," + R3_Y + "," + BOSS_Z + ")");
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[elitebuild] map missing: " + mapDir.getAbsolutePath()
                    + " （请先复制 weekly 壳到 ember_elite）");
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
            sender.sendMessage(ChatColor.RED + "[elitebuild] prepare failed: " + e.getMessage());
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[elitebuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[elitebuild] 开始分批施工试炼短廊…");

        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrubBand(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[elitebuild] scrub → R1厅一");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildRoom(w, R1_X, R1_Y, CZ, 6, 6);
                        changed += clearPad(w, SPAWN_X, R1_Y, SPAWN_Z, 2);
                        changed += clearPad(w, W1A_X, R1_Y, W1A_Z, 2);
                        changed += clearPad(w, W1B_X, R1_Y, W1B_Z, 2);
                        changed += goldAxis(w, R1_X - 5, R1_X + 5, R1_Y, CZ);
                        changed += placeCover(w, R1_X, R1_Y, CZ - 4);
                        changed += placeCover(w, R1_X, R1_Y, CZ + 4);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[elitebuild] R1 → 折角廊1");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildDoglegSouth(w, R1_Y, -23, R2_Y, -11);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[elitebuild] 廊1 → R2厅二");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildRoom(w, R2_X, R2_Y, CZ, 6, 6);
                        changed += clearPad(w, W2A_X, R2_Y, W2A_Z, 2);
                        changed += clearPad(w, W2B_X, R2_Y, W2B_Z, 2);
                        changed += goldAxis(w, R2_X - 5, R2_X + 5, R2_Y, CZ);
                        changed += placeCover(w, R2_X - 4, R2_Y, CZ);
                        changed += placeCover(w, R2_X + 4, R2_Y, CZ);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[elitebuild] R2 → 折角廊2");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildDoglegNorth(w, R2_Y, 3, R3_Y, 15);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[elitebuild] 廊2 → R3厅三");
                        return;
                    }
                    if (phase == 5) {
                        changed += buildRoom(w, R3_X, R3_Y, CZ, 7, 7);
                        changed += clearPad(w, W3_X, R3_Y, W3_Z, 2);
                        changed += clearPad(w, BOSS_X, R3_Y, BOSS_Z, 2);
                        // gold boss pad
                        for (int dx = -2; dx <= 2; dx++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                Material m = (Math.abs(dx) + Math.abs(dz) <= 1) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y - 1, BOSS_Z + dz), m);
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y, BOSS_Z + dz), Material.AIR);
                                changed += set(w.getBlockAt(BOSS_X + dx, R3_Y + 1, BOSS_Z + dz), Material.AIR);
                            }
                        }
                        changed += goldAxis(w, R3_X - 6, R3_X + 6, R3_Y, CZ);
                        changed += placeCover(w, R3_X, R3_Y, CZ - 5);
                        changed += placeCover(w, R3_X, R3_Y, CZ + 5);
                        // B1.3 2026-09-27: buildRoom opens E/W mid-doors; R3 east leads to scrub void
                        // (stone only at y59). Seal east door + apron so Boss/近战不会掉崖。
                        changed += sealRoomEastDoor(w, R3_X, R3_Y, CZ, 7);
                        changed += eastSafetyApron(w, R3_X + 7, R3_Y, CZ);
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
                            sender.sendMessage(ChatColor.RED + "[elitebuild] export failed: " + e.getMessage());
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[elitebuild] corridor done · blocks≈" + changed
                                + " · spawn=(" + SPAWN_X + "," + R1_Y + "," + SPAWN_Z + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[elitebuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("elitebuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    private int scrubBand(World w) {
        int n = 0;
        for (int x = -50; x <= 40; x++) {
            for (int z = 250; z <= 290; z++) {
                for (int y = 60; y <= 85; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 59, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildRoom(World w, int cx, int feetY, int cz, int halfX, int halfZ) {
        int n = 0;
        int fy = feetY - 1;
        for (int dx = -halfX; dx <= halfX; dx++) {
            for (int dz = -halfZ; dz <= halfZ; dz++) {
                boolean edge = Math.abs(dx) == halfX || Math.abs(dz) == halfZ;
                boolean corner = Math.abs(dx) == halfX && Math.abs(dz) == halfZ;
                Material floor = quartzFloor(dx, dz);
                n += set(w.getBlockAt(cx + dx, fy, cz + dz), floor);
                n += set(w.getBlockAt(cx + dx, fy - 1, cz + dz), Material.STONE);
                for (int y = 0; y <= 4; y++) {
                    n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), Material.AIR);
                }
                if (edge) {
                    Material wall = corner ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                    for (int y = 0; y <= 3; y++) {
                        n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), wall);
                    }
                    // east/west mid doorways (axis along +X)
                    if (Math.abs(dz) <= 1 && (dx == halfX || dx == -halfX)) {
                        for (int y = 0; y <= 2; y++) {
                            n += set(w.getBlockAt(cx + dx, feetY + y, cz + dz), Material.AIR);
                        }
                    }
                }
            }
        }
        for (int dx = -halfX; dx <= halfX; dx++) {
            for (int dz = -halfZ; dz <= halfZ; dz++) {
                if (Math.abs(dx) == halfX || Math.abs(dz) == halfZ) {
                    n += set(w.getBlockAt(cx + dx, feetY + 4, cz + dz), Material.QUARTZ_BLOCK);
                }
            }
        }
        return n;
    }

    private Material quartzFloor(int dx, int dz) {
        if (dz == 0) return Material.GOLD_BLOCK; // gold mid-axis
        int h = (dx + dz) & 3;
        if (h == 0) return Material.SMOOTH_BRICK;
        return Material.QUARTZ_BLOCK;
    }

    private int goldAxis(World w, int x0, int x1, int feetY, int z) {
        int n = 0;
        int lo = Math.min(x0, x1), hi = Math.max(x0, x1);
        for (int x = lo; x <= hi; x++) {
            n += set(w.getBlockAt(x, feetY - 1, z), Material.GOLD_BLOCK);
            n += set(w.getBlockAt(x, feetY, z), Material.AIR);
            n += set(w.getBlockAt(x, feetY + 1, z), Material.AIR);
        }
        return n;
    }

    /** South dogleg then east (between R1 and R2). xFrom near R1 east, xTo near R2 west. */
    private int buildDoglegSouth(World w, int yFrom, int xFrom, int yTo, int xTo) {
        int n = 0;
        int jogZ = CZ - 10; // 260
        for (int x = xFrom; x <= xFrom + 3; x++) {
            n += carveHallCell(w, x, yFrom, CZ);
        }
        // turn south
        for (int z = CZ; z >= jogZ; z--) {
            n += carveHallCell(w, xFrom + 3, yFrom, z);
            n += set(w.getBlockAt(xFrom + 2, yFrom, z), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(xFrom + 2, yFrom + 1, z), Material.QUARTZ_BLOCK);
        }
        int xMidStart = xFrom + 3;
        int xMidEnd = xTo - 3;
        int len = Math.max(1, xMidEnd - xMidStart);
        for (int x = xMidStart; x <= xMidEnd; x++) {
            double t = (x - xMidStart) / (double) len;
            int y = (int) Math.round(yFrom + (yTo - yFrom) * t);
            n += carveHallCell(w, x, y, jogZ);
            if ((x - xMidStart) % 3 == 0) {
                n += set(w.getBlockAt(x, y, jogZ - 2), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(x, y + 1, jogZ - 2), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(x, y + 2, jogZ - 2), Material.TORCH);
            }
        }
        for (int z = jogZ; z <= CZ; z++) {
            n += carveHallCell(w, xTo - 3, yTo, z);
            n += set(w.getBlockAt(xTo - 2, yTo, z), Material.GOLD_BLOCK);
            n += set(w.getBlockAt(xTo - 2, yTo + 1, z), Material.QUARTZ_BLOCK);
        }
        for (int x = xTo - 3; x <= xTo; x++) {
            n += carveHallCell(w, x, yTo, CZ);
        }
        return n;
    }

    /** North dogleg then east (between R2 and R3). */
    private int buildDoglegNorth(World w, int yFrom, int xFrom, int yTo, int xTo) {
        int n = 0;
        int jogZ = CZ + 10; // 280
        for (int x = xFrom; x <= xFrom + 3; x++) {
            n += carveHallCell(w, x, yFrom, CZ);
        }
        for (int z = CZ; z <= jogZ; z++) {
            n += carveHallCell(w, xFrom + 3, yFrom, z);
            n += set(w.getBlockAt(xFrom + 2, yFrom, z), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(xFrom + 2, yFrom + 1, z), Material.QUARTZ_BLOCK);
        }
        int xMidStart = xFrom + 3;
        int xMidEnd = xTo - 3;
        int len = Math.max(1, xMidEnd - xMidStart);
        for (int x = xMidStart; x <= xMidEnd; x++) {
            double t = (x - xMidStart) / (double) len;
            int y = (int) Math.round(yFrom + (yTo - yFrom) * t);
            n += carveHallCell(w, x, y, jogZ);
            if ((x - xMidStart) % 3 == 0) {
                n += set(w.getBlockAt(x, y, jogZ + 2), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(x, y + 1, jogZ + 2), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(x, y + 2, jogZ + 2), Material.TORCH);
            }
        }
        for (int z = jogZ; z >= CZ; z--) {
            n += carveHallCell(w, xTo - 3, yTo, z);
            n += set(w.getBlockAt(xTo - 2, yTo, z), Material.GOLD_BLOCK);
            n += set(w.getBlockAt(xTo - 2, yTo + 1, z), Material.QUARTZ_BLOCK);
        }
        for (int x = xTo - 3; x <= xTo; x++) {
            n += carveHallCell(w, x, yTo, CZ);
        }
        return n;
    }

    private int carveHallCell(World w, int x, int feetY, int z) {
        int n = 0;
        int fy = feetY - 1;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) <= 1) {
                    for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), Material.QUARTZ_BLOCK);
                    n += set(w.getBlockAt(x + dx, fy, z + dz), Material.QUARTZ_BLOCK);
                    continue;
                }
                if (Math.abs(dz) == 2 && Math.abs(dx) <= 1) {
                    for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), Material.QUARTZ_BLOCK);
                    n += set(w.getBlockAt(x + dx, fy, z + dz), Material.QUARTZ_BLOCK);
                    continue;
                }
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    for (int y = 0; y <= 3; y++) n += set(w.getBlockAt(x + dx, feetY + y, z + dz), Material.GOLD_BLOCK);
                    continue;
                }
                Material fl = (dz == 0) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                n += set(w.getBlockAt(x + dx, fy, z + dz), fl);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x + dx, feetY + y, z + dz), Material.AIR);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Material fl = (dz == 0) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                n += set(w.getBlockAt(x + dx, fy, z + dz), fl);
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
                Material fl = (dz == 0) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                n += set(w.getBlockAt(x + dx, feetY - 1, z + dz), fl);
                n += set(w.getBlockAt(x + dx, feetY, z + dz), Material.AIR);
                n += set(w.getBlockAt(x + dx, feetY + 1, z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int placeCover(World w, int x, int feetY, int z) {
        int n = 0;
        for (int y = 0; y <= 1; y++) {
            n += set(w.getBlockAt(x, feetY + y, z), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(x + 1, feetY + y, z), Material.QUARTZ_BLOCK);
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
                {R1_X, R1_Y, CZ},
                {R2_X, R2_Y, CZ},
                {R3_X, R3_Y, CZ}
        };
        // embed in floor (feetY-1) — avoid solid pillars at stand height that block bot/近战
        for (int[] p : pts) {
            n += set(w.getBlockAt(p[0], p[1] - 1, p[2] + 3), Material.SEA_LANTERN);
            n += set(w.getBlockAt(p[0], p[1] - 1, p[2] - 3), Material.GLOWSTONE);
            n += set(w.getBlockAt(p[0], p[1], p[2] + 3), Material.AIR);
            n += set(w.getBlockAt(p[0], p[1] + 1, p[2] + 3), Material.AIR);
            n += set(w.getBlockAt(p[0], p[1], p[2] - 3), Material.AIR);
            n += set(w.getBlockAt(p[0], p[1] + 1, p[2] - 3), Material.AIR);
        }
        return n;
    }

    /** Seal east mid-doorway of a room (buildRoom always opens E/W). */
    private int sealRoomEastDoor(World w, int cx, int feetY, int cz, int halfX) {
        int n = 0;
        int x = cx + halfX;
        for (int dz = -1; dz <= 1; dz++) {
            Material wall = (dz == 0) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
            n += set(w.getBlockAt(x, feetY - 1, cz + dz), wall);
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(x, feetY + y, cz + dz), wall);
            }
            n += set(w.getBlockAt(x, feetY + 4, cz + dz), Material.QUARTZ_BLOCK);
        }
        return n;
    }

    /** Short apron east of sealed door — soft landing if anything still slips out. */
    private int eastSafetyApron(World w, int wallX, int feetY, int cz) {
        int n = 0;
        for (int x = wallX + 1; x <= wallX + 4; x++) {
            for (int dz = -3; dz <= 3; dz++) {
                Material fl = (dz == 0) ? Material.GOLD_BLOCK : Material.QUARTZ_BLOCK;
                n += set(w.getBlockAt(x, feetY - 1, cz + dz), fl);
                n += set(w.getBlockAt(x, feetY - 2, cz + dz), Material.STONE);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, feetY + y, cz + dz), Material.AIR);
                }
            }
        }
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X, R1_Y - 1, SPAWN_Z + 2), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(SPAWN_X, R1_Y, SPAWN_Z + 2),
                "§e精英试炼", "§7三厅短廊", "§7石英·金砖中轴", "§8词缀会咬人");
        n += set(w.getBlockAt(SPAWN_X, R1_Y - 1, SPAWN_Z - 2), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(SPAWN_X, R1_Y, SPAWN_Z - 2),
                "§6回枢纽", "§7通关后自动回城", "§7或打开菜单", "§8→ 回枢纽");
        n += set(w.getBlockAt(R2_X, R2_Y - 1, CZ + 3), Material.GOLD_BLOCK);
        n += writeSign(w.getBlockAt(R2_X, R2_Y, CZ + 3),
                "§e试炼二", "§7蛮压词缀", "§7清完进终厅", "§8注意混纹");
        n += set(w.getBlockAt(R3_X, R3_Y - 1, CZ + 4), Material.GOLD_BLOCK);
        n += writeSign(w.getBlockAt(R3_X, R3_Y, CZ + 4),
                "§6试炼终", "§7烬纹执行官", "§6通关回枢纽", "§8菜单亦可回");
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
