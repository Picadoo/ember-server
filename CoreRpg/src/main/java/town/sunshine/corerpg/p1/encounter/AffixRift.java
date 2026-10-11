package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12214 裂隙 rift — LINE: every {@code every} s one line through the elite perpendicular to the vector
 * toward the nearest player (orthogonal to pierce). half_len ≤ 6, half_w thin ≤ 1.5, dmg ≤ 0.55×atk;
 * each player hit at most once per cast.
 */
public final class AffixRift implements AffixBehavior {

    public static final AffixRift INSTANCE = new AffixRift();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixRift() { }

    @Override public String id() { return "rift"; }
    @Override public AffixFamily family() { return AffixFamily.CROSS; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.riftEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "两侧裂开一条线，横着躲开";
    }

    /** one line through the centre perpendicular to player vector, {@code -half_len .. +half_len}; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "裂隙线");
        m.put("start", -v.riftHalfLen);
        m.put("length", 2 * v.riftHalfLen);
        m.put("width", v.riftHalfW);
        m.put("warn", v.riftWarn);
        m.put("dmg", atk * v.riftDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at elite feet; direction perpendicular to vector toward {@code target} (horizontal). */
    public static Vector dir(Location elite, Location target) {
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(0, 0, 1);
        Vector n = fwd.normalize();
        // rotate 90° in XZ: (-z, 0, x)
        return new Vector(-n.getZ(), 0, n.getX());
    }
}
