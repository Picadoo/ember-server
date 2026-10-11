package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12227 扫斩 slash — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite's look yaw snapped to the nearest of 8 dirs (cardinals + diagonals; k·π/4).
 * length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast.
 * Differentiates from gleam (player yaw), spur (8-dir elite→player), seam (cardinal), ridge (diag),
 * edge (free), crest (perp), lance (from elite ray).
 */
public final class AffixSlash implements AffixBehavior {

    public static final AffixSlash INSTANCE = new AffixSlash();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;
    private static final double PI_OVER_4 = Math.PI / 4.0;

    private AffixSlash() { }

    @Override public String id() { return "slash"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.slashEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "精英朝向扫斩，侧移躲开";
    }

    /** one slash strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "扫斩条带");
        m.put("start", -v.slashLength * 0.5);
        m.put("length", v.slashLength);
        m.put("width", v.slashWidth);
        m.put("warn", v.slashWarn);
        m.put("dmg", atk * v.slashDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at player's warn-locked feet. */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }

    /**
     * Direction = elite look yaw snapped to nearest of 8 dirs (k·π/4).
     * Bukkit yaw degrees → look vector (−sin/cos), then atan2 snap.
     */
    public static Vector dir(Location elite) {
        double yawRad = Math.toRadians(elite.getYaw());
        double dx = -Math.sin(yawRad);
        double dz = Math.cos(yawRad);
        if (dx * dx + dz * dz < 1e-6) return new Vector(1.0, 0, 0);
        double angle = Math.atan2(dz, dx);
        double snapped = Math.round(angle / PI_OVER_4) * PI_OVER_4;
        return new Vector(Math.cos(snapped), 0, Math.sin(snapped));
    }
}
