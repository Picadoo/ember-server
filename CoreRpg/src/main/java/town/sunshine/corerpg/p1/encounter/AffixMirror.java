package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12216 残影 mirror — CIRCLE: every {@code every} s a delayed circle at the nearest player's feet
 * captured when the warn starts (player must leave that spot). Radius ≤ 2.0, dmg ≤ 0.55×atk,
 * warn ≥ 1.4, every ≥ 6.5; each player hit at most once per cast.
 */
public final class AffixMirror implements AffixBehavior {

    public static final AffixMirror INSTANCE = new AffixMirror();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixMirror() { }

    @Override public String id() { return "mirror"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.mirrorEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下会留下残影圈，走开躲开";
    }

    /** circle at captured player feet; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "残影圈");
        m.put("radius", v.mirrorRadius);
        m.put("warn", v.mirrorWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.mirrorDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }
}
