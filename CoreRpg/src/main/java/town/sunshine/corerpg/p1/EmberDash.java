package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * D211 / skill-kit S1: shared horizontal look-dir dash used by 踏步 ({@code FlexSkillService}) and 烬突
 * ({@code SkillService}). Collision + door gate = solid feet/head checks (closed IRON_FENCE doors are solid, so the
 * destination clamps before an uncleared room — X4 / C7). No damage, no stamina, no invuln.
 * <p>D219: optional backward + strictGround (后撤步); body-footprint sampling closes the shared diagonal corner-slip.
 */
public final class EmberDash {
    /** Half-width of player body used for path sampling (~0.6 wide). */
    public static final double BODY_HALF = 0.3;

    private EmberDash() {}

    /**
     * Dash up to {@code distance} blocks along the horizontal look direction. Returns the landing location, or
     * {@code null} when the path is blocked (&lt; 0.6 blocks of travel).
     */
    public static Location tryDash(Player player, double distance) {
        return tryDash(player, distance, false, false);
    }

    /**
     * @param backward     reverse the horizontal look vector (后撤)
     * @param strictGround only accept landings with solid/water footing; reject mid-air, lava/fire/cactus/web (B1)
     */
    public static Location tryDash(Player player, double distance, boolean backward, boolean strictGround) {
        if (player == null) return null;
        Location from = player.getLocation();
        if (from.getWorld() == null) return null;
        Vector look = horizontalLook(from);
        if (backward) look.multiply(-1);

        Location best = null;
        double step = 0.25;
        double max = Math.max(0.5, distance);
        for (double d = step; d <= max + 1.0e-6; d += step) {
            Location cand = from.clone().add(look.clone().multiply(d));
            cand.setYaw(from.getYaw());
            cand.setPitch(from.getPitch());
            // Body-footprint: centre alone can slip through a diagonal corner gap two solids share (B2 / D219).
            if (!isPassableBody(cand)) break;
            Location grounded = strictGround ? snapGroundStrict(cand) : snapGround(cand);
            if (grounded != null) {
                best = grounded;
            } else if (!strictGround && isPassableFeet(cand) && isPassableHead(cand)) {
                best = cand;
            } else {
                break;
            }
        }
        if (best == null || best.distanceSquared(from) < 0.36) return null;
        return best;
    }

    /** Horizontal unit look vector from yaw (Y forced to 0). Package-visible for unit tests. */
    public static Vector horizontalLook(Location from) {
        Vector look = from.getDirection().clone();
        look.setY(0);
        if (look.lengthSquared() < 1.0e-6) {
            double yaw = Math.toRadians(from.getYaw());
            look = new Vector(-Math.sin(yaw), 0, Math.cos(yaw));
        }
        return look.normalize();
    }

    /** Sample points along the dash segment (inclusive ends) for hit detection; step ≈ 0.5 blocks. */
    public static java.util.List<Location> pathSamples(Location from, Location to) {
        java.util.List<Location> out = new java.util.ArrayList<Location>();
        if (from == null || to == null || from.getWorld() == null) return out;
        out.add(from.clone());
        double dist = from.distance(to);
        if (dist < 0.05) return out;
        Vector dir = to.toVector().subtract(from.toVector());
        int n = Math.max(1, (int) Math.ceil(dist / 0.5));
        for (int i = 1; i < n; i++) {
            out.add(from.clone().add(dir.clone().multiply((double) i / n)));
        }
        out.add(to.clone());
        return out;
    }

    public static boolean isPassableFeet(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        Block feet = loc.getBlock();
        Block head = loc.clone().add(0, 1, 0).getBlock();
        return !isSolid(feet) && !isSolid(head);
    }

    /** Centre + four corners of the ~0.6-wide body must all clear feet/head solids. */
    public static boolean isPassableBody(Location loc) {
        if (!isPassableFeet(loc)) return false;
        double r = BODY_HALF;
        return isPassableFeet(loc.clone().add(r, 0, r))
                && isPassableFeet(loc.clone().add(r, 0, -r))
                && isPassableFeet(loc.clone().add(-r, 0, r))
                && isPassableFeet(loc.clone().add(-r, 0, -r));
    }

    public static boolean isPassableHead(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        return !isSolid(loc.clone().add(0, 1, 0).getBlock());
    }

    /** Drop at most 1 block to stand on solid; null if no safe footing nearby. */
    public static Location snapGround(Location cand) {
        return snapGroundInternal(cand, false);
    }

    /** Strict footing for 后撤: solid/water only; no mid-air; no lava/fire/cactus/web (B1). */
    public static Location snapGroundStrict(Location cand) {
        return snapGroundInternal(cand, true);
    }

    private static Location snapGroundInternal(Location cand, boolean strict) {
        if (cand == null || cand.getWorld() == null) return null;
        for (int drop = 0; drop <= 1; drop++) {
            Location tryLoc = cand.clone().add(0, -drop, 0);
            Block below = tryLoc.clone().add(0, -0.05, 0).getBlock();
            if (!isPassableFeet(tryLoc)) continue;
            if (isHazardMaterial(below.getType()) || isHazardMaterial(tryLoc.getBlock().getType())) continue;
            if (isSolid(below) || below.getType() == Material.WATER || below.getType() == Material.STATIONARY_WATER) {
                tryLoc.setX(Math.floor(tryLoc.getX()) + 0.5);
                tryLoc.setZ(Math.floor(tryLoc.getZ()) + 0.5);
                return tryLoc;
            }
        }
        if (!strict && isPassableFeet(cand)) {
            Location mid = cand.clone();
            mid.setX(Math.floor(mid.getX()) + 0.5);
            mid.setZ(Math.floor(mid.getZ()) + 0.5);
            return mid;
        }
        return null;
    }

    /** Lava / fire / cactus / web / magma — unsafe for blind backstep landings. */
    public static boolean isHazardMaterial(Material m) {
        if (m == null || m == Material.AIR) return false;
        if (m == Material.LAVA || m == Material.STATIONARY_LAVA) return true;
        if (m == Material.FIRE || m == Material.CACTUS || m == Material.WEB) return true;
        return m == Material.MAGMA;
    }

    public static boolean isSolid(Block b) {
        if (b == null) return true;
        Material m = b.getType();
        if (m == null || m == Material.AIR) return false;
        return m.isSolid();
    }
}
