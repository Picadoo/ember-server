package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12215 劈斩 cleave — CONE: every {@code every} s a frontal cone from the elite toward the nearest player.
 * Full angle ≤ 90° (half ≤ 45°), range ≤ 5, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5;
 * each player hit at most once per cast.
 */
public final class AffixCleave implements AffixBehavior {

    public static final AffixCleave INSTANCE = new AffixCleave();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixCleave() { }

    @Override public String id() { return "cleave"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.cleaveEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "正前扇形劈斩，侧移躲开";
    }

    /** frontal cone toward the target; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "cone");
        m.put("name", "劈斩扇");
        m.put("angle", v.cleaveAngle);
        m.put("range", v.cleaveRange);
        m.put("warn", v.cleaveWarn);
        m.put("dmg", atk * v.cleaveDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at elite feet; direction toward {@code target} (horizontal). */
    public static Vector dir(Location elite, Location target) {
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(1, 0, 0);
        return fwd.normalize();
    }
}
