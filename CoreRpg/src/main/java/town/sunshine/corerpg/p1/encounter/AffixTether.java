package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12207 牵丝 tether — CIRCLE (+soft root/pull): every {@code every} s a circle at a random in-range player's feet;
 * inside when it lands = light hit + brief root (≤0.8 s) and a soft pull toward the centre (Diablo Vortex-lite,
 * jailer-clone; no hard vacuum kill). AffixFamily stays CIRCLE (firechain already owns TETHER).
 */
public final class AffixTether implements AffixBehavior {

    public static final AffixTether INSTANCE = new AffixTether();
    public static final double REACH = 16;

    private AffixTether() { }

    @Override public String id() { return "tether"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "PORTAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.tetherEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下亮紫圈就走开，被罩住会轻拉并定身 " + AffixCycle.fmt(v.tetherRoot) + " 秒";
    }

    /** circle at a player's feet; kb 0 (pull is applied separately) */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "牵丝圈");
        m.put("radius", v.tetherRadius);
        m.put("warn", v.tetherWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.tetherDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** root length in ticks */
    public static int rootTicks(EmberRunMaps.Variety v) { return (int) Math.round(v.tetherRoot * 20); }
}
