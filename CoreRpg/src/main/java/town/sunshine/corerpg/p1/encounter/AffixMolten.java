package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D181 亡爆 molten — DEATH_BLAST: when the elite (not a split add) dies, after {@code delay} s a {@code warn} s circle
 * telegraph at its feet, then one hit of atk(at death) × {@code dmg}; kb 0. No live tick while alive.
 */
public final class AffixMolten implements AffixBehavior {

    public static final AffixMolten INSTANCE = new AffixMolten();

    private AffixMolten() { }

    @Override public String id() { return "molten"; }
    @Override public AffixFamily family() { return AffixFamily.DEATH_BLAST; }
    @Override public String fx() { return "LAVA"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.blazeEvery; } // no live tick
    @Override public String how(EmberRunMaps.Variety v) { return "杀掉后尸体要炸，立刻退开"; }

    /** split adds never blast */
    public static boolean triggers(boolean splitAdd) { return !splitAdd; }

    /** absolute damage snapped at death */
    public static double blastDmg(double atk, EmberRunMaps.Variety v) { return atk * v.moltenDmg; }

    /** telegraph start after death at {@code now} */
    public static long warnAt(long now, EmberRunMaps.Variety v) { return AffixCycle.after(now, v.moltenDelay); }

    /** damage time */
    public static long boomAt(long warnAt, EmberRunMaps.Variety v) { return AffixCycle.after(warnAt, v.moltenWarn); }

    /** blast circle with an already-absolute damage */
    public static EmberRunMaps.Skill blast(double absDmg, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "亡爆圈");
        m.put("radius", v.moltenRadius);
        m.put("warn", v.moltenWarn);
        m.put("dmg", absDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /** blast circle for an elite of attack {@code atk} */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) { return blast(blastDmg(atk, v), v); }
}
