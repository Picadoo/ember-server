package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12240 澳洼 cove — CIRCLE: every {@code every} s a circle lands at 2.125 × elite→player horizontal from
 * the elite's floored feet (past gulf 2.0 / chasm 1.875 / crater 1.75 / pit 1.625 / hollow 1.5 / slade 1.375 …).
 * radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixCove implements AffixBehavior {

    public static final AffixCove INSTANCE = new AffixCove();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixCove() { }

    @Override public String id() { return "cove"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.coveEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下澳洼，走开";
    }

    /** one cove circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "澳洼圈");
        m.put("radius", v.coveRadius);
        m.put("warn", v.coveWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.coveDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 2.125 along elite→player horizontal (Y = floor of elite Y) — past player.
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 2.125);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 2.125);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
