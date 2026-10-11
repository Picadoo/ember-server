package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12216 绽放 bloom — CIRCLE×4: every {@code every} s four petal circles around the elite at equal 90°
 * on a ring of radius {@code orbit} (2.0–4.0). Petal radius ≤ 1.2, dmg ≤ 0.5×atk, warn ≥ 1.3, every ≥ 7;
 * count fixed 4; each player hit at most once per cast across all petals.
 */
public final class AffixBloom implements AffixBehavior {

    public static final AffixBloom INSTANCE = new AffixBloom();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;
    public static final int COUNT = 4;

    private AffixBloom() { }

    @Override public String id() { return "bloom"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SPELL_INSTANT"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.bloomEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "四周绽放 4 瓣圈，躲开再打";
    }

    /** one petal circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "绽放瓣");
        m.put("radius", v.bloomRadius);
        m.put("warn", v.bloomWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.bloomDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * Four floored origins at equal 90° on a ring of radius {@code orbit} around {@code elite}:
     * {@code elite + (cos(i·π/2), 0, sin(i·π/2)) · orbit} for i = 0..3.
     */
    public static Location[] origins(Location elite, EmberRunMaps.Variety v) {
        Location[] out = new Location[COUNT];
        double y = Math.floor(elite.getY());
        double r = v.bloomOrbit;
        for (int i = 0; i < COUNT; i++) {
            double a = i * (Math.PI / 2.0);
            out[i] = new Location(elite.getWorld(),
                    elite.getX() + Math.cos(a) * r, y,
                    elite.getZ() + Math.sin(a) * r);
        }
        return out;
    }
}
