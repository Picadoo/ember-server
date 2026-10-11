package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12222 刃边 edge — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite→player horizontal axis (direction locked at warn start). length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk,
 * warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast. Differentiates from pierce/lance (origin at elite),
 * crest (perp through player), fan (triple from elite), shear (elite flanks), skew (±45 through elite).
 */
public final class AffixEdge implements AffixBehavior {

    public static final AffixEdge INSTANCE = new AffixEdge();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixEdge() { }

    @Override public String id() { return "edge"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.edgeEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下刃边纵斩，侧移躲开";
    }

    /** one edge strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "刃边条带");
        m.put("start", -v.edgeLength * 0.5);
        m.put("length", v.edgeLength);
        m.put("width", v.edgeWidth);
        m.put("warn", v.edgeWarn);
        m.put("dmg", atk * v.edgeDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /** direction along elite→player horizontal (normalize). */
    public static Vector dir(Location elite, Location player) {
        Vector fwd = new Vector(player.getX() - elite.getX(), 0, player.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(1, 0, 0);
        return fwd.normalize();
    }
}
