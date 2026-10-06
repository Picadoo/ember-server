package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

import static town.sunshine.corerpg.p1.encounter.AffixCycle.fmt;

/** D138 炽热 blazing — CIRCLE: every {@code every} s (a player within 6) a fire circle at the elite's feet, {@code warn} s warning. */
public final class AffixBlazing implements AffixBehavior {

    public static final AffixBlazing INSTANCE = new AffixBlazing();
    /** a player must be within this many blocks to arm a circle */
    public static final double ENGAGE = 6;

    private AffixBlazing() { }

    @Override public String id() { return "blazing"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "FLAME"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.blazeEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下每 " + fmt(v.blazeEvery) + " 秒落一圈火（半径 " + fmt(v.blazeRadius) + "，" + fmt(v.blazeWarn) + " 秒预警，看到火圈就退开）";
    }

    /** the fire circle (centre = locked elite feet); damage = atk × dmg */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "炽热火圈");
        m.put("radius", v.blazeRadius);
        m.put("warn", v.blazeWarn);
        m.put("dmg", atk * v.blazeDmg);
        return EmberRunMaps.Skill.of(m);
    }
}
