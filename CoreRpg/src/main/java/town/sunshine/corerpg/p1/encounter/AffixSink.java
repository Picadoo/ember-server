package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12220 沉渊 sink — CIRCLE: every {@code every} s a circle lands at the midpoint between elite and nearest
 * player (locked at warn start). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most
 * once per cast. Differentiates from jailer/spore (at player feet), hook (past player), brace (elite+player dual).
 */
public final class AffixSink implements AffixBehavior {

    public static final AffixSink INSTANCE = new AffixSink();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixSink() { }

    @Override public String id() { return "sink"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "WATER_SPLASH"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.sinkEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "中点沉渊圈，侧移躲开";
    }

    /** one sink circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "沉渊圈");
        m.put("radius", v.sinkRadius);
        m.put("warn", v.sinkWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.sinkDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin at midpoint elite↔player (horizontal average of X/Z, floor Y of elite).
     */
    public static Location origin(Location elite, Location player) {
        Location o = elite.clone();
        o.setX((elite.getX() + player.getX()) * 0.5);
        o.setZ((elite.getZ() + player.getZ()) * 0.5);
        o.setY(Math.floor(elite.getY()));
        return o;
    }
}
