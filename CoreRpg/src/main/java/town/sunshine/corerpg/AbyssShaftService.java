package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
 * P2 (2026-09-27): ember_abyss vertical shaft — well-top spawn, ring platforms per floor,
 * deeper Watcher chamber, evacuate-menu signs. Admin: /corerpg abyssbuild
 *
 * Axis kept near (-40, z=270). Builds into a temp world then exports region back to
 * plugins/DungeonPlus/map/ember_abyss/.
 */
public class AbyssShaftService {

    static final String WORK_WORLD = "ember_abyss_build";
    static final String MAP_REL = "plugins/DungeonPlus/map/ember_abyss";

    /** Well center (same column as legacy placeholder). */
    static final int CX = -40;
    static final int CZ = 270;

    /** Player feet Y per role. Floor block = feetY - 1. */
    static final int SPAWN_Y = 90;
    /** floor index 1..12 → feet Y */
    static final int[] FLOOR_Y = {
            0,
            85, 80, 75, 70, 65, 60, 55, 50, 45,  // 1..9
            38, 33, 28                           // 10..12 wider
    };

    private final CoreRpgPlugin plugin;

    public AbyssShaftService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "shaft".equals(a) || "abyssbuild".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg abyssbuild [build|table]");
        return true;
    }

    public static int floorFeetY(int floor) {
        if (floor < 1 || floor > 12) return SPAWN_Y;
        return FLOOR_Y[floor];
    }

    /** Open spawn cell for floor (primary). */
    public static int[] floorSpawnPrimary(int floor) {
        int y = floorFeetY(floor);
        // alternate NE / SW open pads so ring feels spiral
        if ((floor & 1) == 1) return new int[]{CX + 2, y, CZ + 4};
        return new int[]{CX - 2, y, CZ - 4};
    }

    public static int[] floorSpawnSecondary(int floor) {
        int y = floorFeetY(floor);
        if ((floor & 1) == 1) return new int[]{CX - 2, y, CZ + 4};
        return new int[]{CX + 2, y, CZ - 4};
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "[abyssbuild] spawn feet=(" + CX + "," + SPAWN_Y + "," + CZ + ")");
        for (int f = 1; f <= 12; f++) {
            int[] a = floorSpawnPrimary(f);
            int[] b = floorSpawnSecondary(f);
            sender.sendMessage(ChatColor.GRAY + "  F" + f + " feetY=" + FLOOR_Y[f]
                    + " mobA=(" + a[0] + "," + a[1] + "," + a[2] + ")"
                    + " mobB=(" + b[0] + "," + b[1] + "," + b[2] + ")");
        }
    }

    private boolean doBuild(CommandSender sender) {
        File serverRoot = plugin.getServer().getWorldContainer();
        File mapDir = new File(serverRoot, MAP_REL);
        if (!mapDir.isDirectory()) {
            sender.sendMessage(ChatColor.RED + "[abyssbuild] map missing: " + mapDir.getAbsolutePath());
            return true;
        }
        File workDir = new File(serverRoot, WORK_WORLD);
        try {
            World existing = Bukkit.getWorld(WORK_WORLD);
            if (existing != null) {
                Bukkit.unloadWorld(existing, false);
            }
            if (workDir.exists()) {
                deleteRecursive(workDir.toPath());
            }
            copyRecursive(mapDir.toPath(), workDir.toPath());
            File lock = new File(workDir, "session.lock");
            if (lock.exists()) lock.delete();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "[abyssbuild] prepare failed: " + e.getMessage());
            plugin.getLogger().warning("abyssbuild prepare: " + e);
            return true;
        }

        WorldCreator wc = new WorldCreator(WORK_WORLD);
        wc.generateStructures(false);
        final World w = Bukkit.createWorld(wc);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[abyssbuild] createWorld failed");
            return true;
        }
        w.setAutoSave(true);
        sender.sendMessage(ChatColor.YELLOW + "[abyssbuild] 开始分批施工（防卡顿）…");
        plugin.getLogger().info("abyssbuild: scheduled layer build starting");

        new BukkitRunnable() {
            int phase = 0; // 0=carveY, 1=wallsY, 2=deepCarve, 3=deepWalls, 4=bottom, 5=spawn, 6=floors, 7=ladders, 8=lights, 9=signs, 10=export
            int yCursor = 96;
            int floorIdx = 1;
            int changed = 0;
            int tickBudget = 0;

            @Override
            public void run() {
                try {
                    tickBudget = 0;
                    if (phase == 0) {
                        // carve main well: one Y per tick
                        int y = yCursor;
                        int rInner = 9;
                        for (int dx = -rInner; dx <= rInner; dx++) {
                            for (int dz = -rInner; dz <= rInner; dz++) {
                                if (dx * dx + dz * dz <= rInner * rInner) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.AIR);
                                }
                            }
                        }
                        yCursor--;
                        if (yCursor < 24) { phase = 1; yCursor = 96; sender.sendMessage(ChatColor.GRAY + "[abyssbuild] carve done → walls"); }
                        return;
                    }
                    if (phase == 1) {
                        int y = yCursor;
                        Material wall = wallMat(y);
                        Material outer = (y <= 42) ? Material.BEDROCK : Material.OBSIDIAN;
                        for (int dx = -12; dx <= 12; dx++) {
                            for (int dz = -12; dz <= 12; dz++) {
                                int d2 = dx * dx + dz * dz;
                                if (d2 >= 10 * 10 && d2 <= 11 * 11) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), wall);
                                } else if (d2 > 11 * 11 && d2 <= 12 * 12 && y <= 42) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), outer);
                                }
                            }
                        }
                        yCursor--;
                        if (yCursor < 24) { phase = 2; yCursor = 42; sender.sendMessage(ChatColor.GRAY + "[abyssbuild] walls done → deep"); }
                        return;
                    }
                    if (phase == 2) {
                        int y = yCursor;
                        int rDeep = 14;
                        for (int dx = -rDeep; dx <= rDeep; dx++) {
                            for (int dz = -rDeep; dz <= rDeep; dz++) {
                                if (dx * dx + dz * dz <= rDeep * rDeep) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.AIR);
                                }
                            }
                        }
                        yCursor--;
                        if (yCursor < 24) { phase = 3; yCursor = 42; }
                        return;
                    }
                    if (phase == 3) {
                        int y = yCursor;
                        Material wall = wallMat(y);
                        for (int dx = -16; dx <= 16; dx++) {
                            for (int dz = -16; dz <= 16; dz++) {
                                int d2 = dx * dx + dz * dz;
                                if (d2 >= 14 * 14 && d2 <= 15 * 15) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), wall);
                                } else if (d2 > 15 * 15 && d2 <= 16 * 16) {
                                    changed += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.BEDROCK);
                                }
                            }
                        }
                        yCursor--;
                        if (yCursor < 24) { phase = 4; }
                        return;
                    }
                    if (phase == 4) {
                        for (int dx = -16; dx <= 16; dx++) {
                            for (int dz = -16; dz <= 16; dz++) {
                                if (dx * dx + dz * dz <= 16 * 16) {
                                    changed += set(w.getBlockAt(CX + dx, 24, CZ + dz), Material.BEDROCK);
                                    changed += set(w.getBlockAt(CX + dx, 25, CZ + dz), Material.OBSIDIAN);
                                }
                            }
                        }
                        for (int dx = -12; dx <= 12; dx++) {
                            for (int dz = -12; dz <= 12; dz++) {
                                int d2 = dx * dx + dz * dz;
                                if (d2 >= 9 * 9 && d2 <= 12 * 12) {
                                    changed += set(w.getBlockAt(CX + dx, 96, CZ + dz), Material.SMOOTH_BRICK);
                                }
                            }
                        }
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[abyssbuild] seal done → spawn platform");
                        return;
                    }
                    if (phase == 5) {
                        changed += buildSpawnPlatform(w);
                        phase = 6;
                        floorIdx = 1;
                        sender.sendMessage(ChatColor.GRAY + "[abyssbuild] spawn done → floors");
                        return;
                    }
                    if (phase == 6) {
                        changed += buildFloorPlatform(w, floorIdx);
                        sender.sendMessage(ChatColor.GRAY + "[abyssbuild] floor " + floorIdx + " @Y=" + FLOOR_Y[floorIdx]);
                        floorIdx++;
                        if (floorIdx > 12) phase = 7;
                        return;
                    }
                    if (phase == 7) {
                        changed += buildLadders(w);
                        phase = 8;
                        return;
                    }
                    if (phase == 8) {
                        changed += placeLights(w);
                        phase = 9;
                        return;
                    }
                    if (phase == 9) {
                        changed += placeSigns(w);
                        phase = 10;
                        return;
                    }
                    if (phase == 10) {
                        cancel();
                        w.setSpawnLocation(CX, SPAWN_Y, CZ);
                        w.save();
                        for (org.bukkit.Chunk c : w.getLoadedChunks()) {
                            c.unload(true);
                        }
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
                            sender.sendMessage(ChatColor.RED + "[abyssbuild] export failed: " + e.getMessage());
                            plugin.getLogger().warning("abyssbuild export: " + e);
                            return;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[abyssbuild] shaft done · blocks≈" + changed
                                + " · spawn=(" + CX + "," + SPAWN_Y + "," + CZ + ")"
                                + " · unloaded=" + unloaded
                                + " · exported → " + MAP_REL);
                        plugin.getLogger().info("abyssbuild changed≈" + changed + " exported to " + MAP_REL);
                        dumpTable(sender);
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[abyssbuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("abyssbuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    /** Hollow cylinder + clear interior from bottom to top. */
    private int carveWell(World w) {
        int n = 0;
        int yMin = 24;
        int yMax = 96;
        int rInner = 9;
        for (int y = yMin; y <= yMax; y++) {
            for (int dx = -rInner; dx <= rInner; dx++) {
                for (int dz = -rInner; dz <= rInner; dz++) {
                    if (dx * dx + dz * dz <= rInner * rInner) {
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.AIR);
                    }
                }
            }
        }
        // bottom chamber enlarge for floors 10-12
        int rDeep = 14;
        for (int y = 24; y <= 42; y++) {
            for (int dx = -rDeep; dx <= rDeep; dx++) {
                for (int dz = -rDeep; dz <= rDeep; dz++) {
                    if (dx * dx + dz * dz <= rDeep * rDeep) {
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.AIR);
                    }
                }
            }
        }
        return n;
    }

    private int buildWalls(World w) {
        int n = 0;
        // outer shell r=10..11 from y24..96; material bands
        for (int y = 24; y <= 96; y++) {
            Material wall = wallMat(y);
            Material outer = (y <= 42) ? Material.BEDROCK : Material.OBSIDIAN;
            for (int dx = -12; dx <= 12; dx++) {
                for (int dz = -12; dz <= 12; dz++) {
                    int d2 = dx * dx + dz * dz;
                    if (d2 >= 10 * 10 && d2 <= 11 * 11) {
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), wall);
                    } else if (d2 > 11 * 11 && d2 <= 12 * 12 && y <= 42) {
                        // deep outer bedrock shell (anti-escape)
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), outer);
                    }
                }
            }
        }
        // deep chamber wall r=14..15
        for (int y = 24; y <= 42; y++) {
            Material wall = wallMat(y);
            for (int dx = -16; dx <= 16; dx++) {
                for (int dz = -16; dz <= 16; dz++) {
                    int d2 = dx * dx + dz * dz;
                    if (d2 >= 14 * 14 && d2 <= 15 * 15) {
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), wall);
                    } else if (d2 > 15 * 15 && d2 <= 16 * 16) {
                        n += set(w.getBlockAt(CX + dx, y, CZ + dz), Material.BEDROCK);
                    }
                }
            }
        }
        // floor of well (bottom seal)
        for (int dx = -16; dx <= 16; dx++) {
            for (int dz = -16; dz <= 16; dz++) {
                if (dx * dx + dz * dz <= 16 * 16) {
                    n += set(w.getBlockAt(CX + dx, 24, CZ + dz), Material.BEDROCK);
                    n += set(w.getBlockAt(CX + dx, 25, CZ + dz), Material.OBSIDIAN);
                }
            }
        }
        // roof rim at top
        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 >= 9 * 9 && d2 <= 12 * 12) {
                    n += set(w.getBlockAt(CX + dx, 96, CZ + dz), Material.SMOOTH_BRICK);
                }
            }
        }
        return n;
    }

    private Material wallMat(int y) {
        if (y >= 78) return Material.SMOOTH_BRICK;      // top band
        if (y >= 60) return Material.COBBLESTONE;         // upper mid
        if (y >= 45) return Material.NETHER_BRICK;        // mid
        if (y >= 33) return Material.NETHERRACK;          // deep
        return Material.OBSIDIAN;                         // bottom
    }

    private int buildSpawnPlatform(World w) {
        int n = 0;
        int fy = SPAWN_Y - 1; // 89
        // ring platform + center look-down hole (r<=2 open)
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 <= 2 * 2) {
                    n += set(w.getBlockAt(CX + dx, fy, CZ + dz), Material.AIR); // look down
                    continue;
                }
                if (d2 <= 8 * 8) {
                    Material m = (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) ? Material.QUARTZ_BLOCK
                            : (((dx + dz) & 3) == 0 ? Material.COBBLESTONE : Material.SMOOTH_BRICK);
                    // quartz landing pad south of hole for spawn feet
                    if (dz >= 3 && dz <= 5 && Math.abs(dx) <= 2) m = Material.QUARTZ_BLOCK;
                    n += set(w.getBlockAt(CX + dx, fy, CZ + dz), m);
                    n += set(w.getBlockAt(CX + dx, fy - 1, CZ + dz), Material.STONE);
                    for (int y = 1; y <= 3; y++) {
                        n += set(w.getBlockAt(CX + dx, fy + y, CZ + dz), Material.AIR);
                    }
                }
            }
        }
        // rail around look-down hole
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 >= 3 * 3 && d2 <= 3 * 3 + 2) {
                    n += set(w.getBlockAt(CX + dx, SPAWN_Y, CZ + dz), Material.IRON_FENCE);
                }
            }
        }
        // spawn feet stand pad at (CX, SPAWN_Y, CZ+4) — quartz already; ensure air
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 3; dz <= 5; dz++) {
                n += set(w.getBlockAt(CX + dx, fy, CZ + dz), Material.QUARTZ_BLOCK);
                n += set(w.getBlockAt(CX + dx, SPAWN_Y, CZ + dz), Material.AIR);
                n += set(w.getBlockAt(CX + dx, SPAWN_Y + 1, CZ + dz), Material.AIR);
            }
        }
        // torch posts
        n += set(w.getBlockAt(CX - 4, SPAWN_Y, CZ + 4), Material.FENCE);
        n += set(w.getBlockAt(CX - 4, SPAWN_Y + 1, CZ + 4), Material.TORCH);
        n += set(w.getBlockAt(CX + 4, SPAWN_Y, CZ + 4), Material.FENCE);
        n += set(w.getBlockAt(CX + 4, SPAWN_Y + 1, CZ + 4), Material.TORCH);
        return n;
    }

    private int buildFloorPlatform(World w, int floor) {
        int n = 0;
        int feet = FLOOR_Y[floor];
        int fy = feet - 1;
        boolean deep = floor >= 10;
        int rOuter = deep ? 12 : 8;
        int rHole = deep ? (floor == 12 ? 0 : 2) : 3; // floor12 solid arena; 10-11 small hole

        // crescent / ring: emphasize one side for spiral readability
        int side = (floor & 1) == 1 ? 1 : -1; // +Z or -Z bias

        for (int dx = -rOuter; dx <= rOuter; dx++) {
            for (int dz = -rOuter; dz <= rOuter; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 > rOuter * rOuter) continue;
                if (rHole > 0 && d2 <= rHole * rHole) {
                    n += set(w.getBlockAt(CX + dx, fy, CZ + dz), Material.AIR);
                    continue;
                }
                // spiral bias: thin opposite arc (still walkable rim of width 2)
                if (!deep) {
                    boolean onPreferred = side > 0 ? dz >= -1 : dz <= 1;
                    boolean onRim = d2 >= (rOuter - 1) * (rOuter - 1);
                    if (!onPreferred && !onRim) {
                        // leave air on weak side so drop is visible — but keep 2-block walk ring
                        if (d2 < 5 * 5) {
                            n += set(w.getBlockAt(CX + dx, fy, CZ + dz), Material.AIR);
                            continue;
                        }
                    }
                }
                Material m = floorMat(floor, dx, dz);
                n += set(w.getBlockAt(CX + dx, fy, CZ + dz), m);
                n += set(w.getBlockAt(CX + dx, fy - 1, CZ + dz), Material.STONE);
                n += set(w.getBlockAt(CX + dx, feet, CZ + dz), Material.AIR);
                n += set(w.getBlockAt(CX + dx, feet + 1, CZ + dz), Material.AIR);
            }
        }

        // ensure primary/secondary spawn cells are solid+open (anti-suffocation)
        int[] a = floorSpawnPrimary(floor);
        int[] b = floorSpawnSecondary(floor);
        for (int[] p : new int[][]{a, b}) {
            n += set(w.getBlockAt(p[0], fy, p[2]), deep ? Material.NETHER_BRICK : Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(p[0], feet, p[2]), Material.AIR);
            n += set(w.getBlockAt(p[0], feet + 1, p[2]), Material.AIR);
            // 3x3 clear around spawn
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    n += set(w.getBlockAt(p[0] + dx, fy, p[2] + dz),
                            deep ? Material.NETHER_BRICK : Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(p[0] + dx, feet, p[2] + dz), Material.AIR);
                    n += set(w.getBlockAt(p[0] + dx, feet + 1, p[2] + dz), Material.AIR);
                }
            }
        }

        // low rail on hole edge (non-deep)
        if (!deep && rHole > 0) {
            for (int dx = -rHole - 1; dx <= rHole + 1; dx++) {
                for (int dz = -rHole - 1; dz <= rHole + 1; dz++) {
                    int d2 = dx * dx + dz * dz;
                    if (d2 >= rHole * rHole && d2 <= (rHole + 1) * (rHole + 1)) {
                        Block below = w.getBlockAt(CX + dx, fy, CZ + dz);
                        if (below.getType() != Material.AIR) {
                            n += set(w.getBlockAt(CX + dx, feet, CZ + dz), Material.IRON_FENCE);
                        }
                    }
                }
            }
            // clear rails off spawn pads
            for (int[] p : new int[][]{a, b}) {
                n += set(w.getBlockAt(p[0], feet, p[2]), Material.AIR);
            }
        }

        // floor number marker pillar on preferred side
        int mx = CX + (side > 0 ? 0 : 0);
        int mz = CZ + side * (deep ? 10 : 7);
        n += set(w.getBlockAt(mx, fy, mz), Material.QUARTZ_BLOCK);
        n += set(w.getBlockAt(mx, feet, mz), Material.SEA_LANTERN);
        return n;
    }

    private Material floorMat(int floor, int dx, int dz) {
        if (floor >= 10) {
            int h = (dx * 13 + dz * 7 + floor) & 7;
            if (h == 0) return Material.OBSIDIAN;
            if (h == 1) return Material.NETHERRACK;
            return Material.NETHER_BRICK;
        }
        if (floor >= 6) {
            int h = (dx + dz + floor) & 3;
            if (h == 0) return Material.NETHERRACK;
            if (h == 1) return Material.NETHER_BRICK;
            return Material.COBBLESTONE;
        }
        int h = (dx * 3 + dz * 5) & 3;
        if (h == 0) return Material.COBBLESTONE;
        if (h == 1) return Material.STONE;
        return Material.SMOOTH_BRICK;
    }

    private int buildLadders(World w) {
        int n = 0;
        // vertical ladder column on west wall for emergency climb / visual continuity
        int lx = CX - 9;
        int lz = CZ;
        for (int y = 26; y <= SPAWN_Y; y++) {
            n += set(w.getBlockAt(lx, y, lz), Material.AIR);
            n += set(w.getBlockAt(lx + 1, y, lz), Material.SMOOTH_BRICK); // backboard
            Block ladder = w.getBlockAt(lx, y, lz);
            ladder.setType(Material.LADDER, false);
            // data 4 = facing east (towards center) in 1.12
            try {
                ladder.setData((byte) 4, false);
            } catch (Throwable ignored) {
            }
            n++;
        }
        return n;
    }

    private int placeLights(World w) {
        int n = 0;
        for (int f = 1; f <= 12; f++) {
            int feet = FLOOR_Y[f];
            int fy = feet - 1;
            // glowstone in wall niches at cardinal points
            int[][] pts = {{CX + 8, CZ}, {CX - 8, CZ}, {CX, CZ + 8}, {CX, CZ - 8}};
            if (f >= 10) {
                pts = new int[][]{{CX + 12, CZ}, {CX - 12, CZ}, {CX, CZ + 12}, {CX, CZ - 12}};
            }
            for (int[] p : pts) {
                n += set(w.getBlockAt(p[0], fy + 2, p[1]), Material.GLOWSTONE);
            }
        }
        // spawn ring lights
        n += set(w.getBlockAt(CX + 7, SPAWN_Y, CZ), Material.SEA_LANTERN);
        n += set(w.getBlockAt(CX - 7, SPAWN_Y, CZ), Material.SEA_LANTERN);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        // well-top evacuate anchor (south of spawn pad)
        n += set(w.getBlockAt(CX, SPAWN_Y - 1, CZ + 6), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(CX, SPAWN_Y, CZ + 6),
                "§5上浮撤离", "§7打开深渊菜单", "§7→ 上浮撤离", "§8按最高层结算");
        // entry lore
        n += set(w.getBlockAt(CX + 3, SPAWN_Y - 1, CZ + 5), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(CX + 3, SPAWN_Y, CZ + 5),
                "§5余烬深渊", "§7下行竖井", "§7清层后下潜", "§8可到第12层");
        // mid-shaft evacuate reminder (near floor 5)
        int y5 = FLOOR_Y[5];
        n += set(w.getBlockAt(CX + 7, y5 - 1, CZ), Material.NETHER_BRICK);
        n += writeSign(w.getBlockAt(CX + 7, y5, CZ),
                "§5上浮撤离", "§7打开深渊菜单", "§7→ 上浮撤离", "§8票不退");
        // bottom chamber
        int y12 = FLOOR_Y[12];
        n += set(w.getBlockAt(CX, y12 - 1, CZ + 11), Material.OBSIDIAN);
        n += writeSign(w.getBlockAt(CX, y12, CZ + 11),
                "§4看守深域", "§7第10～12层", "§5上浮撤离", "§7打开深渊菜单");
        return n;
    }

    private int scrubLegacyRoom(World w) {
        // Old critic room was ~ x-43..-37 z267..273 y65 — already carved by well; ensure no floating leftovers
        int n = 0;
        for (int x = -50; x <= -30; x++) {
            for (int z = 260; z <= 280; z++) {
                for (int y = 62; y <= 72; y++) {
                    int dx = x - CX, dz = z - CZ;
                    int d2 = dx * dx + dz * dz;
                    // inside well column should be air except platforms
                    if (d2 <= 9 * 9) {
                        boolean onPlatform = false;
                        for (int f = 1; f <= 12; f++) {
                            if (y == FLOOR_Y[f] - 1 || y == FLOOR_Y[f] - 2) {
                                onPlatform = true;
                                break;
                            }
                        }
                        if (y == SPAWN_Y - 1 || y == SPAWN_Y - 2) onPlatform = true;
                        if (!onPlatform && w.getBlockAt(x, y, z).getType() != Material.AIR
                                && w.getBlockAt(x, y, z).getType() != Material.IRON_FENCE
                                && w.getBlockAt(x, y, z).getType() != Material.LADDER
                                && w.getBlockAt(x, y, z).getType() != Material.TORCH
                                && w.getBlockAt(x, y, z).getType() != Material.FENCE
                                && w.getBlockAt(x, y, z).getType() != Material.SIGN_POST
                                && w.getBlockAt(x, y, z).getType() != Material.WALL_SIGN
                                && w.getBlockAt(x, y, z).getType() != Material.GLOWSTONE
                                && w.getBlockAt(x, y, z).getType() != Material.SEA_LANTERN) {
                            // only scrub if it's the old room height and not a floor slab we placed
                            // skip — platforms handle structure; avoid wiping floor slabs at y64 (floor5)
                        }
                    }
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
