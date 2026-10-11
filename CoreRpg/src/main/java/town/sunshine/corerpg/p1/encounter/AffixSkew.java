package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12219 斜切 skew — STRIP×2: every {@code every} s two thin strips through the elite at ±45° to the elite→player
 * axis (direction locked at warn start). length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5;
 * each player hit at most once per cast across both strips. Differentiates from shear (parallel flanks) and venom
 * (axis-aligned cross).
 */
public final class AffixSkew implements AffixBehavior {

    public static final AffixSkew INSTANCE = new AffixSkew();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixSkew() { }

    @Override public String id() { return "skew"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "SWEEP_ATTACK"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.skewEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "斜向双带穿过精英，站到空隙躲开";
    }

    /** one skew strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "斜切条带");
        m.put("start", -v.skewLength * 0.5);
        m.put("length", v.skewLength);
        m.put("width", v.skewWidth);
        m.put("warn", v.skewWarn);
        m.put("dmg", atk * v.skewDmg);
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
     * Two horizontal unit directions at ±45° to {@code axis} (Y-rotation).
     */
    public static Vector[] dirs(Vector axis) {
        Vector d = axis == null ? new Vector(1, 0, 0) : axis.clone();
        if (d.lengthSquared() < 1e-6) d = new Vector(1, 0, 0);
        else d = d.normalize();
        double c = Math.sqrt(0.5); // cos45 = sin45
        double x = d.getX(), z = d.getZ();
        // +45°: (x' = x*c − z*c, z' = x*c + z*c)
        Vector plus = new Vector(c * (x - z), 0, c * (x + z));
        // −45°: (x' = x*c + z*c, z' = −x*c + z*c)
        Vector minus = new Vector(c * (x + z), 0, c * (-x + z));
        if (plus.lengthSquared() < 1e-6) plus = new Vector(1, 0, 0);
        else plus = plus.normalize();
        if (minus.lengthSquared() < 1e-6) minus = new Vector(0, 0, 1);
        else minus = minus.normalize();
        return new Vector[]{plus, minus};
    }
}
