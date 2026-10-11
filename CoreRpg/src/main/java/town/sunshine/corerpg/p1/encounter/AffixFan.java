package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12220 扇刃 fan — STRIP×3: every {@code every} s three thin rays from the elite at −30°/0°/+30° to the
 * elite→player axis (direction locked at warn start). length ≤ 6, width ≤ 1.0, dmg ≤ 0.55×atk, warn ≥ 1.4,
 * every ≥ 6.5; each player hit at most once per cast across all three. Differentiates from lance (single forward),
 * shear (parallel flanks), skew (±45° through-elite), cleave (cone).
 */
public final class AffixFan implements AffixBehavior {

    public static final AffixFan INSTANCE = new AffixFan();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixFan() { }

    @Override public String id() { return "fan"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT_MAGIC"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.fanEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "三道扇刃向前，站到空隙躲开";
    }

    /** one fan ray (forward charge strip); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "charge");
        m.put("name", "扇刃条带");
        m.put("length", v.fanLength);
        m.put("width", v.fanWidth);
        m.put("warn", v.fanWarn);
        m.put("dmg", atk * v.fanDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at elite feet; direction toward {@code target} (horizontal). */
    public static Vector dir(Location elite, Location target) {
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(1, 0, 0);
        return fwd.normalize();
    }

    /**
     * Three horizontal unit directions at −30°/0°/+30° to {@code axis} (Y-rotation).
     */
    public static Vector[] dirs(Vector axis) {
        Vector d = axis == null ? new Vector(1, 0, 0) : axis.clone();
        if (d.lengthSquared() < 1e-6) d = new Vector(1, 0, 0);
        else d = d.normalize();
        double rad = Math.PI / 6.0; // 30°
        double c = Math.cos(rad), s = Math.sin(rad);
        double x = d.getX(), z = d.getZ();
        Vector mid = d.clone();
        // +30°: (x' = x*c − z*s, z' = x*s + z*c)
        Vector plus = new Vector(c * x - s * z, 0, s * x + c * z);
        // −30°: (x' = x*c + z*s, z' = −x*s + z*c)
        Vector minus = new Vector(c * x + s * z, 0, -s * x + c * z);
        if (plus.lengthSquared() < 1e-6) plus = new Vector(1, 0, 0);
        else plus = plus.normalize();
        if (minus.lengthSquared() < 1e-6) minus = new Vector(0, 0, 1);
        else minus = minus.normalize();
        return new Vector[]{minus, mid, plus};
    }
}
