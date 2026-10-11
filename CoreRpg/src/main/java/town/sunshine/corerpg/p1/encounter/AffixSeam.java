package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12223 缝斩 seam — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite→player horizontal snapped to the nearest cardinal (±X or ±Z). length ≤ 6, width ≤ 1.2,
 * dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast. Differentiates from edge
 * (free angle along elite→player), crest (perp), venom (world-axis cross), pierce/lance (from elite),
 * shear/fan/skew.
 */
public final class AffixSeam implements AffixBehavior {

    public static final AffixSeam INSTANCE = new AffixSeam();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixSeam() { }

    @Override public String id() { return "seam"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.seamEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下方位缝斩，侧移躲开";
    }

    /** one seam strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "缝斩条带");
        m.put("start", -v.seamLength * 0.5);
        m.put("length", v.seamLength);
        m.put("width", v.seamWidth);
        m.put("warn", v.seamWarn);
        m.put("dmg", atk * v.seamDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /** direction = elite→player horizontal snapped to nearest cardinal (±X or ±Z). */
    public static Vector dir(Location elite, Location player) {
        double dx = player.getX() - elite.getX();
        double dz = player.getZ() - elite.getZ();
        if (dx * dx + dz * dz < 1e-6) return new Vector(1, 0, 0);
        if (Math.abs(dx) >= Math.abs(dz)) {
            return new Vector(dx >= 0 ? 1 : -1, 0, 0);
        }
        return new Vector(0, 0, dz >= 0 ? 1 : -1);
    }
}
