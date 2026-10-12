package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12236 坑洼 pit — CIRCLE: every {@code every} s a circle lands at 1.625 × elite→player horizontal from
 * the elite's floored feet (past hollow 1.5 / slade 1.375 / combe 1.25 / dale 1.125 / vale 1.0 …).
 * radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most once per cast. NO root/CC.
 */
public final class AffixPit implements AffixBehavior {

    public static final AffixPit INSTANCE = new AffixPit();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixPit() { }

    @Override public String id() { return "pit"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.pitEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下坑洼，走开";
    }

    /** one pit circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "坑洼圈");
        m.put("radius", v.pitRadius);
        m.put("warn", v.pitWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.pitDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 1.625 along elite→player horizontal (Y = floor of elite Y) — past player.
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 1.625);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 1.625);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
