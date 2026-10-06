package town.sunshine.corerpg.p1.encounter;

import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D171 冲锋 charge — STRIP: every {@code every} s (a player within 10) a strip from the elite towards that player,
 * clipped by walls (skip when shorter than {@link EmberCounterplay#CHARGE_MIN}); telegraph + strip damage only, the
 * body never dashes out of its leash.
 */
public final class AffixCharge implements AffixBehavior {

    public static final AffixCharge INSTANCE = new AffixCharge();
    public static final double ENGAGE = 10;

    private AffixCharge() { }

    @Override public String id() { return "charge"; }
    @Override public AffixFamily family() { return AffixFamily.STRIP; }
    @Override public String fx() { return "CRIT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.chargeEvery; }
    @Override public String how(EmberRunMaps.Variety v) { return "看见脚下亮带就躲开"; }

    /** strip (config max length; the caller already checked the clear run). {@code dir} unused (kept for the old signature). */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v, Vector dir) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "charge");
        m.put("name", "冲锋条带");
        m.put("length", v.chargeLength);
        m.put("width", v.chargeWidth);
        m.put("warn", v.chargeWarn);
        m.put("dmg", atk * v.chargeDmg);
        return EmberRunMaps.Skill.of(m);
    }

    /** enough floor in front of the elite to telegraph a strip */
    public static boolean roomFor(double clearRun) { return !(clearRun < EmberCounterplay.CHARGE_MIN); }

    /** horizontal unit direction elite → target ((0,0,1) when on top of each other) */
    public static Vector aim(double ox, double oz, double tx, double tz) {
        Vector dir = new Vector(tx - ox, 0, tz - oz);
        if (dir.lengthSquared() < 1e-6) dir = new Vector(0, 0, 1);
        return dir.normalize();
    }
}
