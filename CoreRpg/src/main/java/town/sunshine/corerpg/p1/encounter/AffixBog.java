package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12226 泥潭 bog — CIRCLE: every {@code every} s a circle lands at 0.375 × elite→player horizontal from
 * the elite's floored feet (beyond fen 0.125 / well 0.25). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3,
 * every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixBog implements AffixBehavior {

    public static final AffixBog INSTANCE = new AffixBog();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixBog() { }

    @Override public String id() { return "bog"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.bogEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "身前泥潭，走开";
    }

    /** one bog circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "泥潭圈");
        m.put("radius", v.bogRadius);
        m.put("warn", v.bogWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.bogDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 0.375 along elite→player horizontal (Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 0.375);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 0.375);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
