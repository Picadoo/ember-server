package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * P1 (2026-09-27): minimal ember_hub plaza — steps/corridor boundary + workshop anchor
 * aligned with 灰烛 NPC, no empty flat spawn pad. Admin: /corerpg hubbuild
 */
public class HubPlazaService {

    // Spawn feet (MV): (-18.5, 58.0, 110.5) → floor block (-19, 57, 110)
    // 灰烛 NPC: (-16.5, 58.0, 106.5) → (-17, 57, 106) — north of spawn
    static final String WORLD = "ember_hub";
    static final int FLOOR_Y = 57;
    static final int CX = -19;
    static final int CZ = 110;
    static final int NPC_X = -17;
    static final int NPC_Z = 106;
    /** Face north toward 灰烛 (MC yaw 180 = -Z). */
    static final float SPAWN_YAW = 180.0f;

    private final CoreRpgPlugin plugin;

    public HubPlazaService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String a = args.length >= 2 ? args[1].toLowerCase() : "build";
        if ("build".equals(a) || "plaza".equals(a) || "hubbuild".equals(a)) {
            return doBuild(sender);
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg hubbuild [build]");
        return true;
    }

    private boolean doBuild(CommandSender sender) {
        World w = Bukkit.getWorld(WORLD);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "[hubbuild] world " + WORLD + " not loaded");
            return true;
        }
        int changed = 0;
        changed += clearVolume(w, CX - 10, FLOOR_Y + 1, CZ - 12, CX + 12, FLOOR_Y + 6, CZ + 8);
        changed += buildPlazaFloor(w);
        changed += buildRimAndSteps(w);
        changed += buildCorridors(w);
        changed += buildSpawnPad(w);
        changed += buildNpcPath(w);
        changed += buildWorkshop(w);
        changed += placeSigns(w);
        // Align Bukkit spawn (yaw handled by Multiverse worlds.yml)
        w.setSpawnLocation(CX, FLOOR_Y + 1, CZ);
        sender.sendMessage(ChatColor.GREEN + "[hubbuild] plaza done · blocks≈" + changed
                + " · spawn=(" + (CX + 0.5) + "," + (FLOOR_Y + 1) + "," + (CZ + 0.5)
                + ") yaw=" + SPAWN_YAW + " · npc@(" + (NPC_X + 0.5) + "," + (FLOOR_Y + 1) + "," + (NPC_Z + 0.5) + ")");
        plugin.getLogger().info("hubbuild changed≈" + changed);
        // Keep 灰烛 Ady + Bukkit hitbox in sync with plaza
        if (plugin.getQuestService() != null) {
            plugin.getQuestService().ensureNpc(true);
        }
        if (plugin.getHubNpcService() != null) {
            plugin.getHubNpcService().ensureAll(true);
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.teleport(new org.bukkit.Location(w, CX + 0.5, FLOOR_Y + 1, CZ + 0.5, SPAWN_YAW, 0f));
        }
        return true;
    }

    /** Air-clear above floor so rim/roof don't trap players; keep floor itself. */
    private int clearVolume(World w, int x0, int y0, int z0, int x1, int y1, int z1) {
        int n = 0;
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++) {
            for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) {
                    n += set(w.getBlockAt(x, y, z), Material.AIR);
                }
            }
        }
        return n;
    }

    private int buildPlazaFloor(World w) {
        int n = 0;
        // Main plaza ~21×17, mixed stone brick / cobble / gravel for texture (not flat void)
        for (int dx = -9; dx <= 10; dx++) {
            for (int dz = -10; dz <= 6; dz++) {
                int x = CX + dx, z = CZ + dz;
                Material m;
                int h = (dx * 17 + dz * 31) & 7;
                if (h == 0) m = Material.COBBLESTONE;
                else if (h == 1) m = Material.GRAVEL;
                else if (h == 2) m = Material.STONE;
                else m = Material.SMOOTH_BRICK;
                n += set(w.getBlockAt(x, FLOOR_Y, z), m);
                // thin subfill so it feels grounded
                n += set(w.getBlockAt(x, FLOOR_Y - 1, z), Material.STONE);
            }
        }
        return n;
    }

    private int buildRimAndSteps(World w) {
        int n = 0;
        // Low rim walls (readable boundary) on S/E/W — leave north open toward 灰烛
        for (int dx = -9; dx <= 10; dx++) {
            int x = CX + dx;
            // south rim
            n += set(w.getBlockAt(x, FLOOR_Y + 1, CZ + 6), Material.COBBLE_WALL);
            // south outer step down (高低差)
            n += set(w.getBlockAt(x, FLOOR_Y - 1, CZ + 7), Material.SMOOTH_BRICK);
            n += setStairs(w.getBlockAt(x, FLOOR_Y, CZ + 7), Material.SMOOTH_STAIRS, (byte) 2); // face south
            n += set(w.getBlockAt(x, FLOOR_Y - 1, CZ + 8), Material.COBBLESTONE);
        }
        for (int dz = -10; dz <= 6; dz++) {
            int z = CZ + dz;
            // west rim
            n += set(w.getBlockAt(CX - 9, FLOOR_Y + 1, z), Material.COBBLE_WALL);
            n += setStairs(w.getBlockAt(CX - 10, FLOOR_Y, z), Material.SMOOTH_STAIRS, (byte) 0); // face east→west descend
            n += set(w.getBlockAt(CX - 10, FLOOR_Y - 1, z), Material.SMOOTH_BRICK);
            // east rim (leave workshop gap later patched)
            if (z < 102 || z > 108) {
                n += set(w.getBlockAt(CX + 10, FLOOR_Y + 1, z), Material.COBBLE_WALL);
            }
            n += setStairs(w.getBlockAt(CX + 11, FLOOR_Y, z), Material.SMOOTH_STAIRS, (byte) 1);
            n += set(w.getBlockAt(CX + 11, FLOOR_Y - 1, z), Material.SMOOTH_BRICK);
        }
        // corner pillars (height accent)
        int[][] corners = {{-9, -10}, {-9, 6}, {10, -10}, {10, 6}};
        for (int[] c : corners) {
            int x = CX + c[0], z = CZ + c[1];
            for (int y = 1; y <= 3; y++) n += set(w.getBlockAt(x, FLOOR_Y + y, z), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(x, FLOOR_Y + 4, z), Material.SEA_LANTERN);
        }
        return n;
    }

    private int buildCorridors(World w) {
        int n = 0;
        // West & east colonnade (回廊): columns every 3, slab roof
        for (int dz = -9; dz <= 5; dz += 3) {
            int z = CZ + dz;
            // west columns
            for (int y = 1; y <= 3; y++) n += set(w.getBlockAt(CX - 7, FLOOR_Y + y, z), Material.SMOOTH_BRICK);
            n += set(w.getBlockAt(CX - 7, FLOOR_Y + 4, z), Material.WOOD_STEP);
            // east columns (skip workshop z band)
            if (z < 102 || z > 108) {
                for (int y = 1; y <= 3; y++) n += set(w.getBlockAt(CX + 8, FLOOR_Y + y, z), Material.SMOOTH_BRICK);
                n += set(w.getBlockAt(CX + 8, FLOOR_Y + 4, z), Material.WOOD_STEP);
            }
        }
        // connecting roof slabs along colonnade
        for (int dz = -9; dz <= 5; dz++) {
            n += set(w.getBlockAt(CX - 7, FLOOR_Y + 4, CZ + dz), Material.WOOD_STEP);
            if (CZ + dz < 102 || CZ + dz > 108) {
                n += set(w.getBlockAt(CX + 8, FLOOR_Y + 4, CZ + dz), Material.WOOD_STEP);
            }
        }
        return n;
    }

    private int buildSpawnPad(World w) {
        int n = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(CX + dx, FLOOR_Y, CZ + dz), Material.QUARTZ_BLOCK);
                for (int y = 1; y <= 3; y++) n += set(w.getBlockAt(CX + dx, FLOOR_Y + y, CZ + dz), Material.AIR);
            }
        }
        // torch posts beside pad (not blocking north view)
        n += set(w.getBlockAt(CX - 2, FLOOR_Y + 1, CZ), Material.FENCE);
        n += set(w.getBlockAt(CX - 2, FLOOR_Y + 2, CZ), Material.TORCH);
        n += set(w.getBlockAt(CX + 2, FLOOR_Y + 1, CZ), Material.FENCE);
        n += set(w.getBlockAt(CX + 2, FLOOR_Y + 2, CZ), Material.TORCH);
        return n;
    }

    private int buildNpcPath(World w) {
        int n = 0;
        // Path north toward 灰烛
        for (int z = NPC_Z; z <= CZ - 2; z++) {
            n += set(w.getBlockAt(NPC_X, FLOOR_Y, z), Material.QUARTZ_BLOCK);
            n += set(w.getBlockAt(NPC_X - 1, FLOOR_Y, z), Material.STEP); // stone slab full? STEP is stone slab
            // Ensure air for standing
            for (int y = 1; y <= 2; y++) {
                n += set(w.getBlockAt(NPC_X, FLOOR_Y + y, z), Material.AIR);
                n += set(w.getBlockAt(NPC_X - 1, FLOOR_Y + y, z), Material.AIR);
            }
        }
        // NPC stand pad + lantern
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                n += set(w.getBlockAt(NPC_X + dx, FLOOR_Y, NPC_Z + dz), Material.SMOOTH_BRICK);
                for (int y = 1; y <= 3; y++) n += set(w.getBlockAt(NPC_X + dx, FLOOR_Y + y, NPC_Z + dz), Material.AIR);
            }
        }
        n += set(w.getBlockAt(NPC_X, FLOOR_Y, NPC_Z), Material.SEA_LANTERN);
        // tiny rail ring so NPC anchor is readable
        n += set(w.getBlockAt(NPC_X - 2, FLOOR_Y + 1, NPC_Z), Material.COBBLE_WALL);
        n += set(w.getBlockAt(NPC_X + 2, FLOOR_Y + 1, NPC_Z), Material.COBBLE_WALL);
        n += set(w.getBlockAt(NPC_X, FLOOR_Y + 1, NPC_Z - 2), Material.COBBLE_WALL);
        return n;
    }

    private int buildWorkshop(World w) {
        int n = 0;
        // Shed east of 灰烛: x=-12..-8, z=103..107 — visible when facing north from spawn
        int x0 = -12, x1 = -8, z0 = 103, z1 = 107;
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                n += set(w.getBlockAt(x, FLOOR_Y, z), Material.SMOOTH_BRICK);
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean door = x == x0 && z == 105; // west door toward plaza/NPC
                for (int y = 1; y <= 3; y++) {
                    if (door && y <= 2) n += set(w.getBlockAt(x, FLOOR_Y + y, z), Material.AIR);
                    else if (edge) n += set(w.getBlockAt(x, FLOOR_Y + y, z), Material.WOOD);
                    else n += set(w.getBlockAt(x, FLOOR_Y + y, z), Material.AIR);
                }
                // roof
                n += set(w.getBlockAt(x, FLOOR_Y + 4, z), Material.WOOD_STEP);
            }
        }
        // Function props (decorative; forge opens via menu)
        n += set(w.getBlockAt(-9, FLOOR_Y + 1, 105), Material.FURNACE);
        n += set(w.getBlockAt(-9, FLOOR_Y + 1, 104), Material.ANVIL);
        n += set(w.getBlockAt(-10, FLOOR_Y + 1, 106), Material.WORKBENCH);
        n += set(w.getBlockAt(-10, FLOOR_Y + 1, 103), Material.CHEST);
        // exterior accent
        n += set(w.getBlockAt(-13, FLOOR_Y + 1, 106), Material.FENCE);
        n += set(w.getBlockAt(-13, FLOOR_Y + 2, 106), Material.TORCH);
        return n;
    }

    private int placeSigns(World w) {
        int n = 0;
        // Entrance / menu — south of spawn (behind player when facing 灰烛)
        n += set(w.getBlockAt(CX, FLOOR_Y, CZ + 3), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(CX, FLOOR_Y + 1, CZ + 3),
                "§6枢纽广场", "§7右键灰烛·看主线", "§7工坊：右键 NPC", "§8或打开枢纽菜单");
        // 灰烛 pointer near path
        n += set(w.getBlockAt(NPC_X - 2, FLOOR_Y, NPC_Z + 1), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(NPC_X - 2, FLOOR_Y + 1, NPC_Z + 1),
                "§6引路人·灰烛", "§7右键交谈", "§8主线在此", "");
        // Workshop forge direction
        n += set(w.getBlockAt(-13, FLOOR_Y, 105), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(-13, FLOOR_Y + 1, 105),
                "§6锻炉师·烬砧", "§7就在棚里", "§7右键他", "§8勿手打指令");
        // Evacuate / return semantic near south rim (hub IS hub — remind menu)
        n += set(w.getBlockAt(CX + 3, FLOOR_Y, CZ + 5), Material.SMOOTH_BRICK);
        n += writeSign(w.getBlockAt(CX + 3, FLOOR_Y + 1, CZ + 5),
                "§e回枢纽", "§7你已在枢纽", "§7打开枢纽菜单", "§7继续旅程");
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

    @SuppressWarnings("deprecation")
    private static int setStairs(Block b, Material m, byte data) {
        if (b.getType() == m && b.getData() == data) return 0;
        b.setType(m, false);
        b.setData(data, false);
        return 1;
    }

    private static int set(Block b, Material m) {
        if (b.getType() == m) return 0;
        b.setType(m, false);
        return 1;
    }
}
