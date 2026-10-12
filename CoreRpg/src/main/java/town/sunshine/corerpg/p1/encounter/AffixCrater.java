package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12237 陨洼 crater — CIRCLE: every {@code every} s a circle lands at 1.75 × elite→player horizontal from
 * the elite's floored feet (past pit 1.625 / hollow 1.5 / slade 1.375 / combe 1.25 …).
 * radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixCrater implements AffixBehavior {

    public static final AffixCrater INSTANCE = new AffixCrater();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixCrater() { }

    @Override public String id() { return "crater"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.craterEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下陨洼，走开";
    }

    /** one crater circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "陨洼圈");
        m.put("radius", v.craterRadius);
        m.put("warn", v.craterWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.craterDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 1.75 along elite→player horizontal (Y = floor of elite Y) — past player.
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 1.75);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 1.75);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
