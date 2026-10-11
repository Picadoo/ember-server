package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12221 积潭 pond — CIRCLE: every {@code every} s a circle lands at 75% of the elite→player horizontal vector
 * from the elite (between sink midpoint and player). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player
 * hit at most once per cast. Differentiates from sink (0.5 mid), hook (past player), jailer/spore/mirror (on feet),
 * brace (dual), flank (elite sides).
 */
public final class AffixPond implements AffixBehavior {

    public static final AffixPond INSTANCE = new AffixPond();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixPond() { }

    @Override public String id() { return "pond"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "WATER_BUBBLE"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.pondEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "偏近落点积潭，侧移躲开";
    }

    /** one pond circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "积潭圈");
        m.put("radius", v.pondRadius);
        m.put("warn", v.pondWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.pondDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at 75% along elite→player horizontal (Y = floor of elite Y).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX(elite.getX() + (player.getX() - elite.getX()) * 0.75);
        o.setZ(elite.getZ() + (player.getZ() - elite.getZ()) * 0.75);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
