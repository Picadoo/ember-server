package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12207 雹霰 hail — CIRCLE×N: every {@code every} s a cone of {@code count} small circles ahead of the elite toward
 * the nearest player (Diablo Frozen / Orbiter-adjacent, mortar multi-pop). Each circle radius ≤ 1.5, dmg ≤ 0.7×atk;
 * each player is hit at most once per cast across all pops.
 */
public final class AffixHail implements AffixBehavior {

    public static final AffixHail INSTANCE = new AffixHail();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixHail() { }

    @Override public String id() { return "hail"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SNOWBALL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.hailEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "前方会落下 " + v.hailCount + " 个小冰圈，躲开再打";
    }

    /** one small circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "雹霰圈");
        m.put("radius", v.hailRadius);
        m.put("warn", v.hailWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.hailDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * {@code count} floored origins in a cone ahead of {@code elite} toward {@code target}.
     * Centre pop at {@code ahead}; side pops offset by {@code spread} perpendicular.
     */
    public static Location[] origins(Location elite, Location target, EmberRunMaps.Variety v) {
        int n = Math.max(1, v.hailCount);
        Location[] out = new Location[n];
        double y = Math.floor(elite.getY());
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) fwd = new Vector(1, 0, 0);
        else fwd.normalize();
        Vector side = new Vector(-fwd.getZ(), 0, fwd.getX());
        if (side.lengthSquared() < 1e-6) side = new Vector(0, 0, 1);
        else side.normalize();
        Vector tip = fwd.clone().multiply(v.hailAhead);
        if (n == 1) {
            out[0] = new Location(elite.getWorld(), elite.getX() + tip.getX(), y, elite.getZ() + tip.getZ());
            return out;
        }
        // centre + left/right (and extras fan further if count > 3)
        double[] lat = n == 2 ? new double[]{-0.5, 0.5} : new double[]{0.0, -1.0, 1.0};
        if (n > 3) {
            lat = new double[n];
            for (int i = 0; i < n; i++) lat[i] = (i - (n - 1) / 2.0);
        }
        for (int i = 0; i < n; i++) {
            double lo = lat[Math.min(i, lat.length - 1)];
            Vector o = tip.clone().add(side.clone().multiply(lo * v.hailSpread));
            out[i] = new Location(elite.getWorld(), elite.getX() + o.getX(), y, elite.getZ() + o.getZ());
        }
        return out;
    }
}
