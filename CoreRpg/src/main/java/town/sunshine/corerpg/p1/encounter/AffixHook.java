package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12219 钩落 hook — CIRCLE: every {@code every} s a circle lands past the nearest player along the elite→player
 * horizontal axis (origin = player feet + dir×reach). radius ≤ 1.5, reach 1.0–3.0, dmg ≤ 0.5×atk, warn ≥ 1.3,
 * every ≥ 7; each player hit at most once per cast. Differentiates from jailer/spore (at player feet) and brace
 * (elite+player dual).
 */
public final class AffixHook implements AffixBehavior {

    public static final AffixHook INSTANCE = new AffixHook();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixHook() { }

    @Override public String id() { return "hook"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "CLOUD"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.hookEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "落点在身前钩落圈，后退或侧移躲开";
    }

    /** one hook circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "钩落圈");
        m.put("radius", v.hookRadius);
        m.put("warn", v.hookWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.hookDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Floored origin past the player along elite→player horizontal axis: player feet + dir×reach.
     */
    public static Location origin(Location elite, Location player, double reach) {
        Vector dir = new Vector(player.getX() - elite.getX(), 0, player.getZ() - elite.getZ());
        if (dir.lengthSquared() < 1e-6) dir = new Vector(1, 0, 0);
        else dir = dir.normalize();
        Location o = player.clone();
        o.setX(o.getX() + dir.getX() * reach);
        o.setZ(o.getZ() + dir.getZ() * reach);
        o.setY(Math.floor(o.getY()));
        return o;
    }
}
