package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/** D181 投弹 mortar — CIRCLE: every {@code every} s a circle at the nearest player's feet (+{@code ahead} towards the elite), kb 0. */
public final class AffixMortar implements AffixBehavior {

    public static final AffixMortar INSTANCE = new AffixMortar();
    /** nearest-player search radius for the target feet */
    public static final double REACH = 16;

    private AffixMortar() { }

    @Override public String id() { return "mortar"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "FLAME"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.mortarEvery; }
    @Override public String how(EmberRunMaps.Variety v) { return "脚下附近会亮圈，走开再打"; }

    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "投弹圈");
        m.put("radius", v.mortarRadius);
        m.put("warn", v.mortarWarn);
        m.put("ahead", v.mortarAhead);
        m.put("dmg", atk * v.mortarDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }
}
