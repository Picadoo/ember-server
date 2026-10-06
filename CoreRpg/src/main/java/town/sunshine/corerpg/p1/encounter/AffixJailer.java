package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D189 禁锢 jailer — CIRCLE (+root): every {@code every} s a small circle at the nearest player's feet; inside when it
 * lands = atk × {@code dmg} and rooted {@code root} s (config clamps ≤ 1.5 s).
 */
public final class AffixJailer implements AffixBehavior {

    public static final AffixJailer INSTANCE = new AffixJailer();

    private AffixJailer() { }

    @Override public String id() { return "jailer"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "CRIT_MAGIC"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.jailerEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下亮小圈就走开，被罩住会定身 " + AffixCycle.fmt(v.jailerRoot) + " 秒";
    }

    /** circle at a player's feet; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "禁锢圈");
        m.put("radius", v.jailerRadius);
        m.put("warn", v.jailerWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.jailerDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** root length in ticks */
    public static int rootTicks(EmberRunMaps.Variety v) { return (int) Math.round(v.jailerRoot * 20); }
}
