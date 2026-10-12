package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12239 湾洼 gulf — CIRCLE: every {@code every} s a circle lands at 2.0 × elite→player horizontal from
 * the elite's floored feet (past chasm 1.875 / crater 1.75 / pit 1.625 / hollow 1.5 / slade 1.375 …).
 * radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixGulf implements AffixBehavior {

    public static final AffixGulf INSTANCE = new AffixGulf();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixGulf() { }

    @Override public String id() { return "gulf"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.gulfEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下湾洼，走开";
    }

    /** one gulf circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "湾洼圈");
        m.put("radius", v.gulfRadius);
        m.put("warn", v.gulfWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.gulfDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 2.0 along elite→player horizontal (Y = floor of elite Y) — past player.
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 2.0);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 2.0);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
