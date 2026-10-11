package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12213 散射 scatter — CIRCLE×3: every {@code every} s three small circles on a ring around the elite
 * (not under feet, not ahead-only). Radius ≤ 1.3, ring ≤ 3.5, dmg ≤ 0.5×atk, count fixed 3;
 * each player hit at most once per cast across all three.
 */
public final class AffixScatter implements AffixBehavior {

    public static final AffixScatter INSTANCE = new AffixScatter();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixScatter() { }

    @Override public String id() { return "scatter"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.scatterEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "周围会落下 3 个小圈，躲开再打";
    }

    /** one scatter circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "散射圈");
        m.put("radius", v.scatterRadius);
        m.put("warn", v.scatterWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.scatterDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Three floored origins on a ring of radius {@code ring} around {@code elite}, spaced 120°.
     * First pop is offset 60° from the vector toward {@code target} so none sits under feet or dead-ahead-only.
     */
    public static Location[] origins(Location elite, Location target, EmberRunMaps.Variety v) {
        int n = 3;
        Location[] out = new Location[n];
        double y = Math.floor(elite.getY());
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        double base = Math.atan2(fwd.getZ(), fwd.getX());
        if (fwd.lengthSquared() < 1e-6) base = 0.0;
        // offset π/3 so first is off-axis (not dead ahead / not under)
        base += Math.PI / 3.0;
        double r = v.scatterRing;
        for (int i = 0; i < n; i++) {
            double a = base + i * (2.0 * Math.PI / 3.0);
            out[i] = new Location(elite.getWorld(),
                    elite.getX() + Math.cos(a) * r, y,
                    elite.getZ() + Math.sin(a) * r);
        }
        return out;
    }
}
