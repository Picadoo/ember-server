package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12217 枪刺 lance — STRIP: every {@code every} s a thin forward rectangle from the elite toward the nearest
 * player (direction locked at warn start). length ≤ 6, width ≤ 1.4, dmg ≤ 0.55×atk, warn ≥ 1.4, every ≥ 6.5;
 * each player hit at most once per cast. Differentiates from charge (wider/higher dmg), pierce (bidirectional),
 * cleave (cone).
 */
public final class AffixLance implements AffixBehavior {

    public static final AffixLance INSTANCE = new AffixLance();
    public static final double REACH = 16;
    public static final double ENGAGE = 8;

    private AffixLance() { }

    @Override public String id() { return "lance"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.lanceEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "正前细枪刺，侧移躲开";
    }

    /** thin forward strip (charge shape); kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "charge");
        m.put("name", "枪刺条带");
        m.put("length", v.lanceLength);
        m.put("width", v.lanceWidth);
        m.put("warn", v.lanceWarn);
        m.put("dmg", atk * v.lanceDmg);
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
