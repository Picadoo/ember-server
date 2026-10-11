package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12211 脉爆 pulse — CIRCLE: every {@code every} s an expanding ring centred on the elite (Diablo Desecrator /
 * Mortar ground linger; single ring at land). Radius ≤ 3.5, dmg ≤ 0.55×atk; each player hit at most once per cast.
 */
public final class AffixPulse implements AffixBehavior {

    public static final AffixPulse INSTANCE = new AffixPulse();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixPulse() { }

    @Override public String id() { return "pulse"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.pulseEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下会爆开一圈冲击波，躲开再打";
    }

    /** circle at elite feet; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "脉爆圈");
        m.put("radius", v.pulseRadius);
        m.put("warn", v.pulseWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.pulseDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }
}
