package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12218 夹击 brace — CIRCLE×2: every {@code every} s two circles — one at elite feet, one at the nearest player's
 * location locked at warn start. radius ≤ 1.6, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7;
 * each player hit at most once per cast even if standing in both circles.
 */
public final class AffixBrace implements AffixBehavior {

    public static final AffixBrace INSTANCE = new AffixBrace();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixBrace() { }

    @Override public String id() { return "brace"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.braceEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下与精英同时落圈，错开站位";
    }

    /** one brace circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "夹击圈");
        m.put("radius", v.braceRadius);
        m.put("warn", v.braceWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.braceDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Two floored origins: elite feet then player feet (both locked at warn start).
     */
    public static Location[] origins(Location elite, Location player) {
        Location a = elite.clone();
        a.setY(Math.floor(a.getY()));
        Location b = player.clone();
        b.setY(Math.floor(b.getY()));
        return new Location[]{a, b};
    }
}
