package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12221 浪脊 crest — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * perpendicular to the elite→player axis (direction locked at warn start). length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk,
 * warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast. Differentiates from rift (perp through elite),
 * pierce/lance (along-axis), shear (parallel flanks), fan (triple forward), skew (±45 through elite), venom (world-axis).
 */
public final class AffixCrest implements AffixBehavior {

    public static final AffixCrest INSTANCE = new AffixCrest();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixCrest() { }

    @Override public String id() { return "crest"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "WATER_WAKE"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.crestEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "身前浪脊横斩，前后躲开";
    }

    /** one crest strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "浪脊条带");
        m.put("start", -v.crestLength * 0.5);
        m.put("length", v.crestLength);
        m.put("width", v.crestWidth);
        m.put("warn", v.crestWarn);
        m.put("dmg", atk * v.crestDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /** direction perpendicular to elite→player horizontal: (−dz, 0, dx). */
    public static Vector dir(Location elite, Location player) {
        Vector fwd = new Vector(player.getX() - elite.getX(), 0, player.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(0, 0, 1);
        Vector n = fwd.normalize();
        return new Vector(-n.getZ(), 0, n.getX());
    }
}
