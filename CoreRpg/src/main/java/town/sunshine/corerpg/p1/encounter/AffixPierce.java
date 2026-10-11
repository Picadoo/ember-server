package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12213 贯穿 pierce — LINE: every {@code every} s one line through the elite toward the nearest player
 * (half of venom's cross; single axis only). half_len ≤ venom arm clamp, half_w thin, dmg ≤ 0.55×atk;
 * each player hit at most once per cast.
 */
public final class AffixPierce implements AffixBehavior {

    public static final AffixPierce INSTANCE = new AffixPierce();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixPierce() { }

    @Override public String id() { return "pierce"; }
    @Override public AffixFamily family() { return AffixFamily.CROSS; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.pierceEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "朝你会射出一条贯穿线，侧移躲开";
    }

    /** one line through the centre toward the target, {@code -half_len .. +half_len}; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "贯穿线");
        m.put("start", -v.pierceHalfLen);
        m.put("length", 2 * v.pierceHalfLen);
        m.put("width", v.pierceHalfW);
        m.put("warn", v.pierceWarn);
        m.put("dmg", atk * v.pierceDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** floored origin at elite feet; direction toward {@code target} (horizontal). */
    public static Vector dir(Location elite, Location target) {
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) return new Vector(1, 0, 0);
        return fwd.normalize();
    }
}
