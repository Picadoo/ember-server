package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * P5 (2026-09-27): ember_event 灾厄祭坛盆地 —
 * 外沿落差 3～6、中央 11×11 抬高 2 祭坛台、半径~20 战斗环、环阶看台、四角火盆柱。
 * 玩家落点南侧 ≠ Boss 脚。Admin: /corerpg calamitybuild | eventbuild
 *
 * Boss feet keep (-96.5, 64, 266.5); pad top block Y=63; ring feet Y=62; rim/approach ~65–67.
 * Player spawn south ~16: (-96.5, 65, 282.5) yaw 180 face altar.
 */
public class CalamityBasinService {

    static final String WORLD = "ember_event";

    /** Boss block center (feet at +0.5) — matches calamity.yml */
    static final int BOSS_X = -97;
    static final int BOSS_Z = 266;
    /** Boss / pad feet Y (pad surface block = PAD_FEET - 1) */
    static final int PAD_FEET = 64;
    /** Combat ring feet Y (pad raised 2) */
    static final int RING_FEET = 62;
    /** Player approach / rim feet Y */
    static final int RIM_FEET = 65;

    static final int PLAYER_X = -97;
    static final int PLAYER_Z = 282; // Boss south ~16
    static final float PLAYER_YAW = 180.0f; // face -Z toward altar

    /** Pad half-extent → 11×11 (-5..+5) */
    static final int PAD_HALF = 5;
    /** Combat ring radius */
    static final int RING_R = 20;
    /** Outer basin / rim radius */
    static final int OUTER_R = 26;

    private final CoreRpgPlugin plugin;

    public CalamityBasinService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "calamitybuild".equals(a) || "eventbuild".equals(a) || "basin".equals(a)) {
            return doBuild(sender);
        }
        if ("table".equals(a) || "coords".equals(a)) {
            dumpTable(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg calamitybuild|eventbuild [build|table]");
        return true;
    }

    private void dumpTable(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "[calamitybuild] Boss feet=("
                + (BOSS_X + 0.5) + "," + PAD_FEET + "," + (BOSS_Z + 0.5) + ")");
        sender.sendMessage(ChatColor.GRAY + "  playerSpawn=(" + (PLAYER_X + 0.5) + "," + RIM_FEET + ","
                + (PLAYER_Z + 0.5) + ") yaw=" + PLAYER_YAW);
        sender.sendMessage(ChatColor.GRAY + "  ringFeetY=" + RING_FEET + " padHalf=" + PAD_HALF
                + " ringR=" + RING_R + " outerR=" + OUTER_R);
    }

    private boolean doBuild(CommandSender sender) {
        World w = Bukkit.getWorld(WORLD);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[calamitybuild] world " + WORLD + " not loaded");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "[calamitybuild] 开始分批施工灾厄祭坛盆地…");
        new BukkitRunnable() {
            int phase = 0;
            int changed = 0;

            @Override
            public void run() {
                try {
                    if (phase == 0) {
                        changed += scrubVolume(w);
                        phase = 1;
                        sender.sendMessage(ChatColor.GRAY + "[calamitybuild] scrub → 盆地底板/落差");
                        return;
                    }
                    if (phase == 1) {
                        changed += buildBasinTerrain(w);
                        phase = 2;
                        sender.sendMessage(ChatColor.GRAY + "[calamitybuild] 地形 → 中央祭坛台");
                        return;
                    }
                    if (phase == 2) {
                        changed += buildAltarPad(w);
                        phase = 3;
                        sender.sendMessage(ChatColor.GRAY + "[calamitybuild] 祭坛 → 环阶看台");
                        return;
                    }
                    if (phase == 3) {
                        changed += buildRingSteps(w);
                        phase = 4;
                        sender.sendMessage(ChatColor.GRAY + "[calamitybuild] 看台 → 火盆柱");
                        return;
                    }
                    if (phase == 4) {
                        changed += buildBrazierPillars(w);
                        phase = 5;
                        sender.sendMessage(ChatColor.GRAY + "[calamitybuild] 火盆 → 入口平台/告示");
                        return;
                    }
                    if (phase == 5) {
                        changed += buildApproach(w);
                        changed += placeSigns(w);
                        changed += clearOpenPads(w);
                        phase = 6;
                        return;
                    }
                    if (phase == 6) {
                        cancel();
                        w.setSpawnLocation(PLAYER_X, RIM_FEET, PLAYER_Z);
                        w.save();
                        sender.sendMessage(ChatColor.GREEN + "[calamitybuild] basin done · blocks≈" + changed
                                + " · Boss=(" + (BOSS_X + 0.5) + "," + PAD_FEET + "," + (BOSS_Z + 0.5) + ")"
                                + " · player=(" + (PLAYER_X + 0.5) + "," + RIM_FEET + "," + (PLAYER_Z + 0.5) + ")"
                                + " yaw=" + PLAYER_YAW);
                        dumpTable(sender);
                        plugin.getLogger().info("calamitybuild changed≈" + changed);
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            p.teleport(new org.bukkit.Location(w, PLAYER_X + 0.5, RIM_FEET, PLAYER_Z + 0.5,
                                    PLAYER_YAW, 0f));
                        }
                    }
                } catch (Throwable t) {
                    cancel();
                    sender.sendMessage(ChatColor.RED + "[calamitybuild] fail: " + t.getMessage());
                    plugin.getLogger().warning("calamitybuild fail: " + t);
                    t.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        return true;
    }

    private int scrubVolume(World w) {
        int n = 0;
        for (int x = BOSS_X - OUTER_R - 2; x <= BOSS_X + OUTER_R + 2; x++) {
            for (int z = BOSS_Z - OUTER_R - 2; z <= BOSS_Z + OUTER_R + 8; z++) {
                for (int y = 55; y <= 78; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                n += set(w.getBlockAt(x, 54, z), Material.STONE);
            }
        }
        return n;
    }

    /** Basin bowl: ring floor at RING_FEET-1, rim rises outward. */
    private int buildBasinTerrain(World w) {
        int n = 0;
        for (int x = BOSS_X - OUTER_R; x <= BOSS_X + OUTER_R; x++) {
            for (int z = BOSS_Z - OUTER_R; z <= BOSS_Z + OUTER_R + 6; z++) {
                double dx = x - BOSS_X + 0.0;
                double dz = z - BOSS_Z + 0.0;
                double r = Math.sqrt(dx * dx + dz * dz);
                if (r > OUTER_R + 0.5) continue;

                Material floor;
                int feetY;
                if (r <= RING_R) {
                    // combat ring — soul sand / netherrack / stone brick mix
                    floor = ringMat(x, z);
                    feetY = RING_FEET;
                } else {
                    // outer slope / rim — rise 3～6 above ring
                    double t = (r - RING_R) / (OUTER_R - RING_R);
                    int rise = 3 + (int) Math.floor(t * 3.5); // 3..6
                    if (rise > 6) rise = 6;
                    feetY = RING_FEET + rise;
                    floor = Material.SMOOTH_BRICK;
                    if (((x + z) & 3) == 0) floor = Material.COBBLESTONE;
                }
                int fy = feetY - 1;
                // fill under
                for (int y = 55; y < fy; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.STONE);
                }
                n += set(w.getBlockAt(x, fy, z), floor);
                // clear air above
                for (int y = feetY; y <= feetY + 8; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
                // low rim wall at outer edge
                if (r >= OUTER_R - 0.8 && r <= OUTER_R + 0.2) {
                    n += set(w.getBlockAt(x, feetY, z), Material.SMOOTH_BRICK);
                    n += set(w.getBlockAt(x, feetY + 1, z), Material.COBBLESTONE);
                }
            }
        }
        return n;
    }

    private Material ringMat(int x, int z) {
        int h = (x * 5 + z * 11) & 7;
        if (h == 0 || h == 1) return Material.SOUL_SAND;
        if (h == 2 || h == 3) return Material.NETHERRACK;
        if (h == 4) return Material.COBBLESTONE;
        return Material.SMOOTH_BRICK;
    }

    private int buildAltarPad(World w) {
        int n = 0;
        int padTop = PAD_FEET - 1; // 63
        // fill column from ring up to pad
        for (int dx = -PAD_HALF; dx <= PAD_HALF; dx++) {
            for (int dz = -PAD_HALF; dz <= PAD_HALF; dz++) {
                int x = BOSS_X + dx;
                int z = BOSS_Z + dz;
                boolean edge = Math.abs(dx) == PAD_HALF || Math.abs(dz) == PAD_HALF;
                boolean heart = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                for (int y = RING_FEET - 1; y < padTop; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.SMOOTH_BRICK);
                }
                Material top;
                if (heart) top = Material.NETHERRACK;
                else if (edge) top = Material.QUARTZ_BLOCK;
                else top = Material.QUARTZ_BLOCK;
                if (!edge && !heart && ((dx + dz) & 1) == 0) top = Material.SMOOTH_BRICK;
                n += set(w.getBlockAt(x, padTop, z), top);
                for (int y = 0; y <= 5; y++) {
                    n += set(w.getBlockAt(x, PAD_FEET + y, z), Material.AIR);
                }
            }
        }
        // stepped approach on four mid-sides (from ring up to pad)
        int[][] steps = {
                {BOSS_X, BOSS_Z - PAD_HALF - 1},
                {BOSS_X, BOSS_Z + PAD_HALF + 1},
                {BOSS_X - PAD_HALF - 1, BOSS_Z},
                {BOSS_X + PAD_HALF + 1, BOSS_Z}
        };
        for (int[] s : steps) {
            // mid step at RING_FEET+1
            n += set(w.getBlockAt(s[0], RING_FEET - 1, s[1]), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(s[0], RING_FEET, s[1]), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(s[0], RING_FEET + 1, s[1]), Material.AIR);
            n += set(w.getBlockAt(s[0], RING_FEET + 2, s[1]), Material.AIR);
        }
        return n;
    }

    /** 1～2 step bleachers at ring outer (r≈RING_R-2..RING_R). */
    private int buildRingSteps(World w) {
        int n = 0;
        for (int x = BOSS_X - RING_R; x <= BOSS_X + RING_R; x++) {
            for (int z = BOSS_Z - RING_R; z <= BOSS_Z + RING_R; z++) {
                double dx = x - BOSS_X + 0.0;
                double dz = z - BOSS_Z + 0.0;
                double r = Math.sqrt(dx * dx + dz * dz);
                if (r < RING_R - 2.2 || r > RING_R - 0.3) continue;
                // skip south approach corridor
                if (z > BOSS_Z + 8 && Math.abs(x - BOSS_X) <= 3) continue;
                int stepFeet = RING_FEET + 1;
                if (r >= RING_R - 1.2) stepFeet = RING_FEET + 2;
                n += set(w.getBlockAt(x, stepFeet - 1, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x, stepFeet, z), Material.AIR);
                n += set(w.getBlockAt(x, stepFeet + 1, z), Material.AIR);
            }
        }
        return n;
    }

    private int buildBrazierPillars(World w) {
        int n = 0;
        // four corners of pad outer + four at ring mid-cardinals
        int[][] pillars = {
                {BOSS_X - PAD_HALF - 3, BOSS_Z - PAD_HALF - 3},
                {BOSS_X + PAD_HALF + 3, BOSS_Z - PAD_HALF - 3},
                {BOSS_X - PAD_HALF - 3, BOSS_Z + PAD_HALF + 3},
                {BOSS_X + PAD_HALF + 3, BOSS_Z + PAD_HALF + 3},
                {BOSS_X - 14, BOSS_Z},
                {BOSS_X + 14, BOSS_Z},
                {BOSS_X, BOSS_Z - 14},
                {BOSS_X, BOSS_Z + 14}
        };
        for (int[] p : pillars) {
            int x = p[0], z = p[1];
            for (int y = RING_FEET - 1; y <= RING_FEET + 3; y++) {
                n += set(w.getBlockAt(x, y, z), Material.SMOOTH_BRICK);
            }
            n += set(w.getBlockAt(x, RING_FEET + 4, z), Material.NETHERRACK);
            n += set(w.getBlockAt(x, RING_FEET + 5, z), Material.GLOWSTONE);
        }
        return n;
    }

    private int buildApproach(World w) {
        int n = 0;
        // south entrance platform at RIM_FEET around player spawn
        for (int x = PLAYER_X - 4; x <= PLAYER_X + 4; x++) {
            for (int z = PLAYER_Z - 3; z <= PLAYER_Z + 2; z++) {
                n += set(w.getBlockAt(x, RIM_FEET - 1, z), Material.QUARTZ_BLOCK);
                for (int y = RIM_FEET; y <= RIM_FEET + 4; y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
            }
        }
        // stair path from approach down toward ring (north toward boss)
        for (int z = PLAYER_Z - 4; z >= BOSS_Z + PAD_HALF + 2; z--) {
            int dist = PLAYER_Z - z;
            int feet = RIM_FEET;
            if (dist >= 4) feet = RING_FEET + 2;
            if (dist >= 8) feet = RING_FEET + 1;
            if (dist >= 12) feet = RING_FEET;
            for (int x = PLAYER_X - 2; x <= PLAYER_X + 2; x++) {
                n += set(w.getBlockAt(x, feet - 1, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(x, feet, z), Material.AIR);
                n += set(w.getBlockAt(x, feet + 1, z), Material.AIR);
                n += set(w.getBlockAt(x, feet + 2, z), Material.AIR);
            }
        }
        // sea lantern accents on approach
        n += set(w.getBlockAt(PLAYER_X + 3, RIM_FEET, PLAYER_Z), Material.SEA_LANTERN);
        n += set(w.getBlockAt(PLAYER_X - 3, RIM_FEET, PLAYER_Z), Material.SEA_LANTERN);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        // approach: hub return (menu semantics, no command teach)
        n += set(w.getBlockAt(PLAYER_X + 2, RIM_FEET - 1, PLAYER_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(PLAYER_X + 2, RIM_FEET, PLAYER_Z),
                "§e回枢纽", "§7打开枢纽菜单", "§7返回", "§8不教打指令");
        n += set(w.getBlockAt(PLAYER_X - 2, RIM_FEET - 1, PLAYER_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(PLAYER_X - 2, RIM_FEET, PLAYER_Z),
                "§4灾厄祭坛", "§7南入口平台", "§7面向中央台", "§8盆地·仪式场");
        // pad edge marker
        n += set(w.getBlockAt(BOSS_X + PAD_HALF + 1, PAD_FEET - 1, BOSS_Z), Material.QUARTZ_BLOCK);
        n += writeSign(w.getBlockAt(BOSS_X + PAD_HALF + 1, PAD_FEET, BOSS_Z),
                "§4灾厄使", "§7中央台", "§7窗内苏醒", "§8公共祭坛");
        return n;
    }

    private int clearOpenPads(World w) {
        int n = 0;
        int[][] pts = {
                {PLAYER_X, RIM_FEET, PLAYER_Z},
                {BOSS_X, PAD_FEET, BOSS_Z},
                {BOSS_X, RING_FEET, BOSS_Z + 10},
                {BOSS_X - 8, RING_FEET, BOSS_Z},
                {BOSS_X + 8, RING_FEET, BOSS_Z}
        };
        for (int[] p : pts) {
            int feet = p[1];
            Material floor = (feet == PAD_FEET) ? Material.QUARTZ_BLOCK : Material.SMOOTH_BRICK;
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
}
