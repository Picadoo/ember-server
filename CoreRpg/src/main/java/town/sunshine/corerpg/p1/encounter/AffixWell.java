package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12222 浅井 well — CIRCLE: every {@code every} s a circle lands at 25% of the elite→player horizontal vector
 * from the elite (nearer than sink midpoint). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player
 * hit at most once per cast. Differentiates from sink (0.5 mid), pond (0.75), hook (past player),
 * jailer/spore/mirror (on feet), brace (dual), flank (elite sides).
 */
public final class AffixWell implements AffixBehavior {

    public static final AffixWell INSTANCE = new AffixWell();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixWell() { }

    @Override public String id() { return "well"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.wellEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "近身浅井圈，后撤躲开";
    }

    /** one well circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "浅井圈");
        m.put("radius", v.wellRadius);
        m.put("warn", v.wellWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.wellDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 25% along elite→player horizontal (Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 0.25);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 0.25);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
