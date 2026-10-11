package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12218 剪切 shear — STRIP×2: every {@code every} s two parallel thin strips flanking the elite→player axis
 * (direction locked at warn start). length ≤ 6, width ≤ 1.2, gap 1.0–3.0, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5;
 * each player hit at most once per cast across both strips. Differentiates from lance (single center strip).
 */
public final class AffixShear implements AffixBehavior {

    public static final AffixShear INSTANCE = new AffixShear();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixShear() { }

    @Override public String id() { return "shear"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.shearEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "两侧平行细带，站中间或外侧躲开";
    }

    /** thin forward strip (charge shape); kb 0 — same helper as lance, applied per flank */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "charge");
        m.put("name", "剪切条带");
        m.put("length", v.shearLength);
        m.put("width", v.shearWidth);
        m.put("warn", v.shearWarn);
        m.put("dmg", atk * v.shearDmg);
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
     * Two floored strip origins flanking {@code origin} by ±{@code gap}/2 along the horizontal perpendicular to {@code dir}.
     */
    public static Location[] strips(Location origin, Vector dir, double gap) {
        Vector d = dir == null ? new Vector(1, 0, 0) : dir.clone();
        if (d.lengthSquared() < 1e-6) d = new Vector(1, 0, 0);
        else d = d.normalize();
        Vector perp = new Vector(-d.getZ(), 0, d.getX());
        double half = gap * 0.5;
        double y = Math.floor(origin.getY());
        Location left = new Location(origin.getWorld(),
                origin.getX() + perp.getX() * half, y,
                origin.getZ() + perp.getZ() * half);
        Location right = new Location(origin.getWorld(),
                origin.getX() - perp.getX() * half, y,
                origin.getZ() - perp.getZ() * half);
        return new Location[]{left, right};
    }
}
