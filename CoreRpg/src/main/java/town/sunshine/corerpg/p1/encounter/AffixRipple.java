package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12215 涟漪 ripple — ANNULUS: every {@code every} s an expanding ring band centred on the elite.
 * Max radius ≤ 4, band half-width ≤ 1.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7;
 * hit = on the ring (safe inside then outside); each player hit at most once per cast.
 */
public final class AffixRipple implements AffixBehavior {

    public static final AffixRipple INSTANCE = new AffixRipple();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixRipple() { }

    @Override public String id() { return "ripple"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.rippleEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "涟漪圈从脚下外扩，站圈上会挨打";
    }

    /** metadata skill (circle) for warn/dmg; hit test uses {@link #inBand}. kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "涟漪圈");
        m.put("radius", v.rippleRadius);
        m.put("warn", v.rippleWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.rippleDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * True when {@code p} sits on the annulus around {@code o}:
     * {@code |horizDist − radius| ≤ halfW}, and within ±2.5 height of the origin.
     */
    public static boolean inBand(Location o, double radius, double halfW, Location p) {
        if (o == null || p == null) return false;
        if (Math.abs(p.getY() - o.getY()) > 2.5) return false;
        double dx = p.getX() - o.getX(), dz = p.getZ() - o.getZ();
        double d = Math.sqrt(dx * dx + dz * dz);
        return Math.abs(d - radius) <= halfW;
    }
}
