package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12223 泥沼 mire — CIRCLE: every {@code every} s a circle lands at the elite's floored feet (offset 0.0 along
 * elite→player). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast.
 * Differentiates from well(0.25)/sink(0.5)/pond(0.75)/hook(past)/jailer(at player).
 */
public final class AffixMire implements AffixBehavior {

    public static final AffixMire INSTANCE = new AffixMire();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixMire() { }

    @Override public String id() { return "mire"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.mireEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "精英脚下泥沼，拉开距离";
    }

    /** one mire circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "泥沼圈");
        m.put("radius", v.mireRadius);
        m.put("warn", v.mireWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.mireDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at elite feet (offset 0.0 along elite→player; Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
