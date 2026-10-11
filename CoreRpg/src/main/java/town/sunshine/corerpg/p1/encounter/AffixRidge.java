package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12224 脊斩 ridge — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite→player horizontal snapped to the nearest diagonal (±45°; sign(dx),sign(dz) normalized).
 * length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast.
 * Differentiates from seam (cardinal snap), edge (free angle), crest (perp), venom (world-axis cross).
 */
public final class AffixRidge implements AffixBehavior {

    public static final AffixRidge INSTANCE = new AffixRidge();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;
    private static final double INV_SQRT2 = 0.7071067811865476;

    private AffixRidge() { }

    @Override public String id() { return "ridge"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.ridgeEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下斜向脊斩，侧移躲开";
    }

    /** one ridge strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "脊斩条带");
        m.put("start", -v.ridgeLength * 0.5);
        m.put("length", v.ridgeLength);
        m.put("width", v.ridgeWidth);
        m.put("warn", v.ridgeWarn);
        m.put("dmg", atk * v.ridgeDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /** direction = elite→player horizontal snapped to nearest diagonal (sign(dx),sign(dz) normalized). */
    public static Vector dir(Location elite, Location player) {
        double dx = player.getX() - elite.getX();
        double dz = player.getZ() - elite.getZ();
        if (dx * dx + dz * dz < 1e-6) return new Vector(INV_SQRT2, 0, INV_SQRT2);
        double sx = dx >= 0 ? 1.0 : -1.0;
        double sz = dz >= 0 ? 1.0 : -1.0;
        return new Vector(sx * INV_SQRT2, 0, sz * INV_SQRT2);
    }
}
