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
 * S1 (2026-09-28): ember_daily 余烬窟·庭院 — 门廊 → 房1前厅 → 门1 → 房2回廊 → 门2 → Boss 终厅。
 * 保留灰烬庭院材质（石砖/砂砾/火盆），拉出独立前厅与第二房；禁止单房三波。
 * Admin: /corerpg dailybuild
 *
 * Coords (feet Y=65 corridor / 66 boss pad):
 *   spawn 0,65,0 (porch, face +Z toward door1)
 *   door1 z=13 (x=-1..1, y=65..67 IRON_FENCE ×9) — opens after room1 clear
 *   door2 z=25 — opens after room2 clear
 *   room1 spawns ~z=6..10; room2 ~z=16..22; boss pad 0,66,31
 */
public class DailyCourtyardService {

    static final String WORK_WORLD = "ember_daily_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_daily";

    static final int GROUND_Y = 65;
    static final int PAD_Y = 66;

    static final int SPAWN_X = 0;
    static final int SPAWN_Z = 0;

    /** Outer bounds (elongated N-S; distinct from weekly dark corridor / abyss shaft) */
    static final int X0 = -10;
    static final int X1 = 9;
    static final int Z0 = -4;
    static final int Z1 = 38;

    /** Door planes (IRON_FENCE gate fill) */
    static final int DOOR1_Z = 13;
    static final int DOOR2_Z = 25;
    static final int DOOR_X0 = -1;
    static final int DOOR_X1 = 1;

    /** Room Z ranges (inclusive walkable) */
    // porch Z0+1 .. 2 ; room1 3..12 ; room2 14..24 ; boss 26..Z1-1
    static final int R1_Z0 = 3, R1_Z1 = 12;
    static final int R2_Z0 = 14, R2_Z1 = 24;
    static final int BOSS_Z0 = 26, BOSS_Z1 = 37;

    static final int BOSS_X = 0;
    static final int BOSS_Z = 31;
    static final int PAD_HALF = 4;

    /** Wave open cells */
    static final int W1A_X = -5, W1A_Z = 7;
    static final int W1B_X = 5, W1B_Z = 7;
    static final int W1C_X = 0, W1C_Z = 10;
    static final int W2A_X = -6, W2A_Z = 17;
    static final int W2B_X = 6, W2B_Z = 17;
    static final int W2C_X = -6, W2C_Z = 21;
    static final int W2D_X = 6, W2D_Z = 21;

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
        sender.sendMessage(ChatColor.GOLD + "[dailybuild] S1 门廊→房1→门1→房2→门2→Boss");
        sender.sendMessage(ChatColor.GRAY + "  spawn=(" + SPAWN_X + "," + GROUND_Y + "," + SPAWN_Z + ")"
                + " · bounds x=" + X0 + ".." + X1 + " z=" + Z0 + ".." + Z1);
        sender.sendMessage(ChatColor.GRAY + "  door1 z=" + DOOR1_Z + " x=" + DOOR_X0 + ".." + DOOR_X1
                + " · door2 z=" + DOOR2_Z + " · pad=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w1A=(" + W1A_X + "," + GROUND_Y + "," + W1A_Z + ")"
                + " w1B=(" + W1B_X + "," + GROUND_Y + "," + W1B_Z + ")"
                + " w1C=(" + W1C_X + "," + GROUND_Y + "," + W1C_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  w2A=(" + W2A_X + "," + GROUND_Y + "," + W2A_Z + ")"
                + " w2B=(" + W2B_X + "," + GROUND_Y + "," + W2B_Z + ")"
                + " w2C=(" + W2C_X + "," + GROUND_Y + "," + W2C_Z + ")"
                + " w2D=(" + W2D_X + "," + GROUND_Y + "," + W2D_Z + ")");
        sender.sendMessage(ChatColor.GRAY + "  boss=(" + BOSS_X + "," + PAD_Y + "," + BOSS_Z + ")");
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
        sender.sendMessage(ChatColor.YELLOW + "[dailybuild] S1 分房施工（门廊→前厅→回廊→Boss）…");

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
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] scrub → 底板+外墙分区");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildShell(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 外壳 → 门廊+房1");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildPorchAndRoom1(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 房1 → 房2回廊");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildRoom2(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 房2 → Boss终厅+门");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBossHall(w);
                        changed += placeDoors(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[dailybuild] 门/Boss → 灯火告示");
                        return;
                    }
                    if (phase == 5) {
                        changed += placeBraziersAndLights(w);
                        changed += placeSigns(w);
                        changed += clearSpawnPads(w);
                        // re-seal doors after clearSpawnPads (pads may punch air into door plane)
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
                                + " · door1z=" + DOOR1_Z + " door2z=" + DOOR2_Z
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

    private int scrubLegacyRoom(World w) {
        int n = 0;
        // old P4 courtyard footprint + legacy far room
        for (int x = -20; x <= 20; x++) {
            for (int z = -8; z <= 40; z++) {
                for (int y = 54; y <= 80; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
            }
        }
        for (int x = -50; x <= -30; x++) {
            for (int z = 260; z <= 280; z++) {
                for (int y = 60; y <= 75; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    /** Outer shell + cross walls at door planes (openings filled later by iron gates). */
    private int buildShell(World w) {
        int n = 0;
        int fy = GROUND_Y - 1;
        for (int x = X0; x <= X1; x++) {
            for (int z = Z0; z <= Z1; z++) {
                boolean edge = x == X0 || x == X1 || z == Z0 || z == Z1;
                boolean corner = (x == X0 || x == X1) && (z == Z0 || z == Z1);
                n += set(w.getBlockAt(x, fy, z), floorMat(x, z));
                n += set(w.getBlockAt(x, fy - 1, z), Material.STONE);
                for (int y = 0; y <= 8; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                if (edge) {
                    Material wall = corner ? Material.COBBLESTONE : Material.SMOOTH_BRICK;
                    int h = corner ? 5 : 4;
                    for (int y = 0; y < h; y++) {
                        n += set(w.getBlockAt(x, GROUND_Y + y, z), wall);
                    }
                    n += set(w.getBlockAt(x, GROUND_Y + h, z),
                            corner ? Material.COBBLESTONE : Material.SMOOTH_BRICK);
                }
            }
        }
        // partition walls at door Z (full width); gate openings punched + iron later
        n += buildPartitionWall(w, DOOR1_Z);
        n += buildPartitionWall(w, DOOR2_Z);
        // south porch arch opening
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, Z0), Material.AIR);
            }
        }
        for (int x = DOOR_X0 - 1; x <= DOOR_X1 + 1; x++) {
            n += set(w.getBlockAt(x, GROUND_Y + 3, Z0), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, GROUND_Y + 4, Z0), Material.COBBLESTONE);
        }
        return n;
    }

    private int buildPartitionWall(World w, int z) {
        int n = 0;
        for (int x = X0; x <= X1; x++) {
            for (int y = 0; y <= 4; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.SMOOTH_BRICK);
            }
            n += set(w.getBlockAt(x, GROUND_Y + 5, z), Material.COBBLESTONE);
        }
        // punch gate opening (air; iron bars placed in placeDoors)
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            for (int y = 0; y <= 2; y++) {
                n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
            }
        }
        // lintel
        for (int x = DOOR_X0; x <= DOOR_X1; x++) {
            n += set(w.getBlockAt(x, GROUND_Y + 3, z), Material.SMOOTH_BRICK);
        }
        return n;
    }

    private int buildPorchAndRoom1(World w) {
        int n = 0;
        // porch partial roof (z=-2..2)
        for (int x = X0 + 1; x <= X1 - 1; x++) {
            for (int z = Z0 + 1; z <= 2; z++) {
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.SMOOTH_BRICK);
                for (int y = 0; y <= 3; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
                // covered porch ceiling
                n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.SMOOTH_BRICK);
            }
        }
        // room1 open court floor + side colonnade pillars (cover)
        for (int x = X0 + 1; x <= X1 - 1; x++) {
            for (int z = R1_Z0; z <= R1_Z1; z++) {
                if (z == DOOR1_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // room1 side pillars every 3
        for (int z = R1_Z0 + 1; z <= R1_Z1 - 1; z += 3) {
            n += pillar(w, X0 + 3, z);
            n += pillar(w, X1 - 3, z);
        }
        // room1 center low ash accent (not raised pad)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(dx, GROUND_Y - 1, 8 + dz), Material.NETHERRACK);
            }
        }
        // ensure spawn open
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y - 1, SPAWN_Z + dz), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y, SPAWN_Z + dz), Material.AIR);
                n += set(w.getBlockAt(SPAWN_X + dx, GROUND_Y + 1, SPAWN_Z + dz), Material.AIR);
            }
        }
        return n;
    }

    private int buildRoom2(World w) {
        int n = 0;
        for (int x = X0 + 1; x <= X1 - 1; x++) {
            for (int z = R2_Z0; z <= R2_Z1; z++) {
                if (z == DOOR1_Z || z == DOOR2_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // side alcove on +X (站位变化): bump wall inward on west, open east pocket
        for (int z = 18; z <= 22; z++) {
            // west cover wall stub
            for (int y = 0; y <= 3; y++) {
                n += set(w.getBlockAt(X0 + 2, GROUND_Y + y, z), Material.SMOOTH_BRICK);
            }
            // east alcove floor gravel
            n += set(w.getBlockAt(X1 - 2, GROUND_Y - 1, z), Material.GRAVEL);
            n += set(w.getBlockAt(X1 - 3, GROUND_Y - 1, z), Material.GRAVEL);
        }
        // room2 pillars (mask / cover)
        for (int z = R2_Z0 + 2; z <= R2_Z1 - 2; z += 3) {
            n += pillar(w, -3, z);
            n += pillar(w, 3, z);
        }
        // partial corner roofs (half-open, not full ceiling — distinct from weekly deep corridor)
        int[][] roofs = {{-8, 16}, {5, 16}, {-8, 21}, {5, 21}};
        for (int[] c : roofs) {
            for (int dx = 0; dx <= 2; dx++) {
                for (int dz = 0; dz <= 2; dz++) {
                    n += set(w.getBlockAt(c[0] + dx, GROUND_Y + 4, c[1] + dz), Material.SMOOTH_BRICK);
                }
            }
        }
        return n;
    }

    private int buildBossHall(World w) {
        int n = 0;
        for (int x = X0 + 1; x <= X1 - 1; x++) {
            for (int z = BOSS_Z0; z <= BOSS_Z1; z++) {
                if (z == DOOR2_Z) continue;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), floorMat(x, z));
                for (int y = 0; y <= 6; y++) {
                    n += set(w.getBlockAt(x, GROUND_Y + y, z), Material.AIR);
                }
            }
        }
        // raised pad
        int padTop = PAD_Y - 1;
        for (int dx = -PAD_HALF; dx < PAD_HALF; dx++) {
            for (int dz = -PAD_HALF; dz < PAD_HALF; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                boolean edge = Math.abs(dx) == PAD_HALF - 1 || Math.abs(dz) == PAD_HALF - 1
                        || dx == -PAD_HALF || dz == -PAD_HALF;
                n += set(w.getBlockAt(x, GROUND_Y - 1, z), Material.STONE);
                Material top = edge ? Material.SMOOTH_BRICK : Material.COBBLESTONE;
                if ((dx == 0 && dz == 0) || (Math.abs(dx) + Math.abs(dz) <= 1)) {
                    top = Material.NETHERRACK;
                }
                n += set(w.getBlockAt(x, padTop, z), top);
                n += set(w.getBlockAt(x, PAD_Y, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 1, z), Material.AIR);
                n += set(w.getBlockAt(x, PAD_Y + 2, z), Material.AIR);
            }
        }
        // approach steps south of pad
        int[][] steps = {
                {BOSS_X, BOSS_Z - PAD_HALF - 1},
                {BOSS_X - 1, BOSS_Z - PAD_HALF - 1},
                {BOSS_X + 1, BOSS_Z - PAD_HALF - 1}
        };
        for (int[] s : steps) {
            n += set(w.getBlockAt(s[0], GROUND_Y - 1, s[1]), Material.STONE);
            n += set(w.getBlockAt(s[0], GROUND_Y, s[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(s[0], GROUND_Y + 1, s[1]), Material.AIR);
        }
        // hall side pillars
        n += pillar(w, X0 + 3, 28);
        n += pillar(w, X1 - 3, 28);
        n += pillar(w, X0 + 3, 35);
        n += pillar(w, X1 - 3, 35);
        return n;
    }

    /** Closed iron-fence gates in door openings (DP clears to AIR on room clear). */
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
        int h = (x * 3 + z * 7) & 7;
        if (h == 0) return Material.GRAVEL;
        if (h == 1) return Material.COBBLESTONE;
        if (h == 2) return Material.NETHERRACK;
        if (h == 3) return Material.GRAVEL;
        return Material.STONE;
    }

    private int pillar(World w, int x, int z) {
        int n = 0;
        Material base = Material.SMOOTH_BRICK;
        n += set(w.getBlockAt(x, GROUND_Y - 1, z), base);
        for (int y = 0; y <= 3; y++) {
            n += set(w.getBlockAt(x, GROUND_Y + y, z), base);
        }
        n += set(w.getBlockAt(x, GROUND_Y + 4, z), Material.COBBLESTONE);
        if (((x + z) & 1) == 0) {
            n += set(w.getBlockAt(x + 1, GROUND_Y + 2, z), Material.TORCH);
        }
        return n;
    }

    private int placeBraziersAndLights(World w) {
        int n = 0;
        int[][] braziers = {
                {-7, 1}, {6, 1},           // porch
                {-7, 8}, {6, 8},           // room1
                {-7, 18}, {6, 18}, {-7, 22}, {6, 22}, // room2
                {-7, 30}, {6, 30}, {0, 36} // boss hall
        };
        for (int[] p : braziers) {
            n += set(w.getBlockAt(p[0], GROUND_Y - 1, p[1]), Material.COBBLESTONE);
            n += set(w.getBlockAt(p[0], GROUND_Y, p[1]), Material.NETHERRACK);
            n += set(w.getBlockAt(p[0], GROUND_Y + 1, p[1]), Material.GLOWSTONE);
        }
        n += set(w.getBlockAt(SPAWN_X + 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(SPAWN_X - 3, GROUND_Y, SPAWN_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(BOSS_X + 5, PAD_Y, BOSS_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(BOSS_X - 5, PAD_Y, BOSS_Z), Material.GLOWSTONE);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        n += set(w.getBlockAt(SPAWN_X + 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X + 2, GROUND_Y, SPAWN_Z),
                "§6余烬窟·庭院", "§7门廊安全区", "§e清尽前厅再开门", "§8→ 前方铁门");
        n += set(w.getBlockAt(SPAWN_X - 2, GROUND_Y - 1, SPAWN_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(SPAWN_X - 2, GROUND_Y, SPAWN_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7未通关撤离", "§8不发通关箱");
        // door1 hint (room1 side)
        n += set(w.getBlockAt(DOOR_X1 + 2, GROUND_Y - 1, DOOR1_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 2, GROUND_Y, DOOR1_Z - 1),
                "§e第一道门", "§7清尽前厅", "§7后铁门敞开", "§8→ 回廊");
        // door2 hint
        n += set(w.getBlockAt(DOOR_X1 + 2, GROUND_Y - 1, DOOR2_Z - 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(DOOR_X1 + 2, GROUND_Y, DOOR2_Z - 1),
                "§eBoss门", "§7清尽回廊", "§7后进入终厅", "§8→ 中央垫");
        n += set(w.getBlockAt(BOSS_X + 5, PAD_Y - 1, BOSS_Z), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(BOSS_X + 5, PAD_Y, BOSS_Z),
                "§c终厅·中央垫", "§7蛮兵在此", "§7通关回枢纽", "§8菜单亦可回");
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
                {BOSS_X, PAD_Y, BOSS_Z}
        };
        for (int[] p : pts) {
            int feetY = p[1];
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int bx = p[0] + dx;
                    int bz = p[2] + dz;
                    // never punch door planes open here
                    if (bz == DOOR1_Z || bz == DOOR2_Z) continue;
                    Material floor = (feetY == PAD_Y) ? Material.COBBLESTONE : floorMat(bx, bz);
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
