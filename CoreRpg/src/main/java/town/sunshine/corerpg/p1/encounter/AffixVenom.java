package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D189 毒十字 venom — CROSS: every {@code every} s (a player within 8) two lines of half-length {@code arm} crossing at
 * the elite's floored feet, "+" then "x" (flips every cast); a player inside either arm is hit once per cast.
 */
public final class AffixVenom implements AffixBehavior {

    public static final AffixVenom INSTANCE = new AffixVenom();
    public static final double ENGAGE = 8;

    private AffixVenom() { }

    @Override public String id() { return "venom"; }
    @Override public AffixFamily family() { return AffixFamily.CROSS; }
    @Override public String fx() { return "SPELL_MOB"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.venomEvery; }
    @Override public String how(EmberRunMaps.Variety v) { return "身上会亮十字（+ 和 × 轮换），站到两条线之间的空隙里"; }

    /** one arm: a line through the centre, {@code -arm .. +arm}; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "毒十字");
        m.put("start", -v.venomArm);
        m.put("length", 2 * v.venomArm);
        m.put("width", v.venomWidth);
        m.put("warn", v.venomWarn);
        m.put("dmg", atk * v.venomDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** the two arm directions — "+" (x / z axes) or "x" (the two diagonals) */
    public static Vector[] dirs(boolean diag) {
        if (!diag) return new Vector[]{new Vector(1, 0, 0), new Vector(0, 0, 1)};
        double c = Math.sqrt(0.5);
        return new Vector[]{new Vector(c, 0, c), new Vector(c, 0, -c)};
    }

    /** inside either arm (the crossing square counts once — the caller hits each player at most once) */
    public static boolean hits(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p) {
        for (Vector d : dirs) if (EmberShape.inShape(arm, o, d, p)) return true;
        return false;
    }
}
