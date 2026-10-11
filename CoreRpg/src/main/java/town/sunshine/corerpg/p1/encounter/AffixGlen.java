package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12230 峡洼 glen — CIRCLE: every {@code every} s a circle lands at 0.875 × elite→player horizontal from
 * the elite's floored feet (beyond dell 0.75 / tarn 0.625 / basin 0.5 / bog 0.375 / fen 0.125 / well 0.25).
 * radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixGlen implements AffixBehavior {

    public static final AffixGlen INSTANCE = new AffixGlen();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixGlen() { }

    @Override public String id() { return "glen"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.glenEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "身前峡洼，走开";
    }

    /** one glen circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "峡洼圈");
        m.put("radius", v.glenRadius);
        m.put("warn", v.glenWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.glenDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 0.875 along elite→player horizontal (Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 0.875);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 0.875);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
