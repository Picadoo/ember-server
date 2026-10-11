package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12212 孢雾 spore — CIRCLE: every {@code every} s a circle at a random in-range player's feet (Diablo Plagued delayed
 * ground; NO root — differentiates jailer/tether). Radius ≤ 2.2, dmg ≤ 0.5×atk; each player hit at most once per cast.
 */
public final class AffixSpore implements AffixBehavior {

    public static final AffixSpore INSTANCE = new AffixSpore();
    public static final double REACH = 16;

    private AffixSpore() { }

    @Override public String id() { return "spore"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_WITCH"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.sporeEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下会起孢雾，走开再打";
    }

    /** circle at a player's feet; kb 0; no root */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "孢雾圈");
        m.put("radius", v.sporeRadius);
        m.put("warn", v.sporeWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.sporeDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }
}
