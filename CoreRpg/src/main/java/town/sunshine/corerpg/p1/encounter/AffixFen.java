package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12225 沼洼 fen — CIRCLE: every {@code every} s a circle lands at 0.125 × elite→player horizontal from
 * the elite's floored feet (between mire 0.0 and well 0.25). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3,
 * every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixFen implements AffixBehavior {

    public static final AffixFen INSTANCE = new AffixFen();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixFen() { }

    @Override public String id() { return "fen"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.fenEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "近精英沼洼，拉开";
    }

    /** one fen circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "沼洼圈");
        m.put("radius", v.fenRadius);
        m.put("warn", v.fenWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.fenDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 0.125 along elite→player horizontal (Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 0.125);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 0.125);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
