package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12214 连落 cascade — CIRCLE×3: every {@code every} s three small circles stepping from the elite
 * toward the nearest player at distances step / 2·step / 3·step. Radius ≤ 1.3, step 1.5–3.0, dmg ≤ 0.5×atk,
 * count fixed 3; each player hit at most once per cast across all three.
 */
public final class AffixCascade implements AffixBehavior {

    public static final AffixCascade INSTANCE = new AffixCascade();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixCascade() { }

    @Override public String id() { return "cascade"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.cascadeEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下连着三圈朝你落，侧步躲开";
    }

    /** one cascade circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "连落圈");
        m.put("radius", v.cascadeRadius);
        m.put("warn", v.cascadeWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.cascadeDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Three floored origins stepping from {@code elite} toward {@code target} at
     * {@code step * i} for i = 1..3 (count fixed 3).
     */
    public static Location[] origins(Location elite, Location target, EmberRunMaps.Variety v) {
        int n = 3;
        Location[] out = new Location[n];
        double y = Math.floor(elite.getY());
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) fwd = new Vector(1, 0, 0);
        else fwd = fwd.normalize();
        double step = v.cascadeStep;
        for (int i = 1; i <= n; i++) {
            out[i - 1] = new Location(elite.getWorld(),
                    elite.getX() + fwd.getX() * step * i, y,
                    elite.getZ() + fwd.getZ() * step * i);
        }
        return out;
    }
}
