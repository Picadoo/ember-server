package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * D12211 埋雷 mines — CIRCLE×N: every {@code every} s {@code count} small circles behind the elite (opposite the
 * facing toward the nearest player; hail already covers ahead). Radius ≤ 1.4, dmg ≤ 0.6×atk; each player hit at most
 * once per cast across all mines.
 */
public final class AffixMines implements AffixBehavior {

    public static final AffixMines INSTANCE = new AffixMines();
    public static final double REACH = 16;
    public static final double ENGAGE = 10;

    private AffixMines() { }

    @Override public String id() { return "mines"; }
    @Override public AffixFamily family() { return AffixFamily.CIRCLE; }
    @Override public String fx() { return "SMOKE_NORMAL"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.minesEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "身后会埋 " + v.minesCount + " 枚小雷，躲开再打";
    }

    /** one mine circle; kb 0 */
    public static EmberRunMaps.Skill skill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "埋雷圈");
        m.put("radius", v.minesRadius);
        m.put("warn", v.minesWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.minesDmg);
        m.put("kb", 0);
        return EmberRunMaps.Skill.of(m);
    }

    /**
     * {@code count} floored origins behind {@code elite} opposite the vector toward {@code target}.
     * Centre at {@code behind}; side pops offset by {@code spread} perpendicular.
     */
    public static Location[] origins(Location elite, Location target, EmberRunMaps.Variety v) {
        int n = Math.max(1, v.minesCount);
        Location[] out = new Location[n];
        double y = Math.floor(elite.getY());
        Vector fwd = new Vector(target.getX() - elite.getX(), 0, target.getZ() - elite.getZ());
        if (fwd.lengthSquared() < 1e-6) fwd = new Vector(1, 0, 0);
        else fwd.normalize();
        Vector side = new Vector(-fwd.getZ(), 0, fwd.getX());
        if (side.lengthSquared() < 1e-6) side = new Vector(0, 0, 1);
        else side.normalize();
        // behind = opposite facing
        Vector tip = fwd.clone().multiply(-v.minesBehind);
        if (n == 1) {
            out[0] = new Location(elite.getWorld(), elite.getX() + tip.getX(), y, elite.getZ() + tip.getZ());
            return out;
        }
        double[] lat = n == 2 ? new double[]{-0.5, 0.5} : new double[]{0.0, -1.0, 1.0};
        if (n > 3) {
            lat = new double[n];
            for (int i = 0; i < n; i++) lat[i] = (i - (n - 1) / 2.0);
        }
        for (int i = 0; i < n; i++) {
            double lo = lat[Math.min(i, lat.length - 1)];
            Vector o = tip.clone().add(side.clone().multiply(lo * v.minesSpread));
            out[i] = new Location(elite.getWorld(), elite.getX() + o.getX(), y, elite.getZ() + o.getZ());
        }
        return out;
    }
}
