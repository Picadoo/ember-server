package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12239 撕斩 rend — STRIP: every {@code every} s one thin strip through the player's warn-locked position,
 * along the elite's look yaw + 225° snapped to the nearest of 8 dirs (cardinals + diagonals; k·π/4).
 * length ≤ 6, width ≤ 1.2, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast.
 * Continues elite-yaw offset family: slash=+0 → carve=+45 → score=+90 → etch=+135 → sever=+180 → rend=+225.
 */
public final class AffixRend implements AffixBehavior {

    public static final AffixRend INSTANCE = new AffixRend();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;
    private static final double PI_OVER_4 = Math.PI / 4.0;

    private AffixRend() { }

    @Override public String id() { return "rend"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.rendEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "斜对撕斩，侧移躲开";
    }

    /** one rend strip through centre (line −L/2 .. +L/2); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "撕斩条带");
        m.put("start", -v.rendLength * 0.5);
        m.put("length", v.rendLength);
        m.put("width", v.rendWidth);
        m.put("warn", v.rendWarn);
        m.put("dmg", atk * v.rendDmg);
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
     * Direction = elite look yaw + 225° snapped to nearest of 8 dirs (k·π/4).
     * After sever (=elite yaw +180); Bukkit yaw degrees → look vector (−sin/cos), then atan2 snap.
     */
    public static Vector dir(Location elite) {
        double yawRad = Math.toRadians(elite.getYaw()) + Math.PI * 5.0 / 4.0;
        double dx = -Math.sin(yawRad);
        double dz = Math.cos(yawRad);
        if (dx * dx + dz * dz < 1e-6) return new Vector(1.0, 0, 0);
        double angle = Math.atan2(dz, dx);
        double snapped = Math.round(angle / PI_OVER_4) * PI_OVER_4;
        return new Vector(Math.cos(snapped), 0, Math.sin(snapped));
    }
}
