package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12225 刺斩 spur — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite→player horizontal snapped to the nearest of 8 dirs (cardinals + diagonals; k·π/4).
 * length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast.
 * Differentiates from ridge (4-diag only), seam (4-cardinal), edge (free), crest (perp), lance (from elite ray).
 */
public final class AffixSpur implements AffixBehavior {

    public static final AffixSpur INSTANCE = new AffixSpur();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;
    private static final double PI_OVER_4 = Math.PI / 4.0;

    private AffixSpur() { }

    @Override public String id() { return "spur"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.spurEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下八向刺斩，侧移躲开";
    }

    /** one spur strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "刺斩条带");
        m.put("start", -v.spurLength * 0.5);
        m.put("length", v.spurLength);
        m.put("width", v.spurWidth);
        m.put("warn", v.spurWarn);
        m.put("dmg", atk * v.spurDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /** direction = elite→player horizontal snapped to nearest of 8 dirs (k·π/4). */
    public static Vector dir(Location elite, Location player) {
        double dx = player.getX() - elite.getX();
        double dz = player.getZ() - elite.getZ();
        if (dx * dx + dz * dz < 1e-6) return new Vector(1.0, 0, 0);
        double angle = Math.atan2(dz, dx);
        double snapped = Math.round(angle / PI_OVER_4) * PI_OVER_4;
        return new Vector(Math.cos(snapped), 0, Math.sin(snapped));
    }
}
