package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12224 泽地 marsh — CIRCLE: every {@code every} s a circle lands at the player's warn-locked floored feet
 * (offset 0 on player, NOT elite). radius ≤ 2.0, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7; each player hit at most
 * once per cast. Differentiates from mire/well/sink/pond (elite-relative offsets), jailer (player circle WITH root —
 * marsh has NO root/CC).
 */
public final class AffixMarsh implements AffixBehavior {

    public static final AffixMarsh INSTANCE = new AffixMarsh();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixMarsh() { }

    @Override public String id() { return "marsh"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.marshEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下泽地，走开";
    }

    /** one marsh circle; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "泽地圈");
        m.put("radius", v.marshRadius);
        m.put("warn", v.marshWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.marshDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** Floored origin at player's warn-locked feet (offset 0 on player). */
    public static Location origin(Location player) {
        Location o = player.clone();
        o.setY(Math.floor(o.getY()));
        return o;
    }
}
