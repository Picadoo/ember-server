package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12212 侧袭 flank — CIRCLE×2: every {@code every} s two small circles left/right of the elite (perpendicular to the
 * vector toward the nearest player; hail=ahead, mines=behind, pulse=under → flank fills left/right). Radius ≤ 1.4,
 * dmg ≤ 0.55×atk; each player hit at most once per cast across both circles.
 */
public final class AffixFlank implements AffixBehavior {

    public static final AffixFlank INSTANCE = new AffixFlank();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixFlank() { }

    @Override public String id() { return "flank"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.flankEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "两侧会爆开小圈，躲开再打";
    }

    /** one flank circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "侧袭圈");
        m.put("radius", v.flankRadius);
        m.put("warn", v.flankWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.flankDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Two floored origins left/right of {@code elite}, perpendicular to the vector toward {@code target},
     * offset by {@code side}.
     */
    public static Location[] origins(Location elite, Location target, EmberRunMaps.Variety v) {
        Location[] out = new Location[2];
        double y = Math.floor(elite.getY());
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) fwd = new Vector(1, 0, 0);
        else fwd.normalize();
        Vector side = new Vector(-fwd.getZ(), 0, fwd.getX());
        if (side.lengthSquared() < 1e-6) side = new Vector(0, 0, 1);
        else side.normalize();
        double s = v.flankSide;
        out[0] = new Location(elite.getWorld(), elite.getX() + side.getX() * s, y, elite.getZ() + side.getZ() * s);
        out[1] = new Location(elite.getWorld(), elite.getX() - side.getX() * s, y, elite.getZ() - side.getZ() * s);
        return out;
    }
}
