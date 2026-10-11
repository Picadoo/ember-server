package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12217 新星 nova — ANNULUS: every {@code every} s a thin ring at fixed radius around the elite
 * (inner safe, ring hits). Max radius ≤ 3.5, band half-width ≤ 0.7, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7;
 * each player hit at most once per cast. Tighter than ripple (radius 4 / half_w 1).
 */
public final class AffixNova implements AffixBehavior {

    public static final AffixNova INSTANCE = new AffixNova();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixNova() { }

    @Override public String id() { return "nova"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.novaEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下新星环炸开，站圈上会挨打";
    }

    /** metadata skill (circle) for warn/dmg; hit test uses {@link #inBand}. kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "新星环");
        m.put("radius", v.novaRadius);
        m.put("warn", v.novaWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.novaDmg);
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
