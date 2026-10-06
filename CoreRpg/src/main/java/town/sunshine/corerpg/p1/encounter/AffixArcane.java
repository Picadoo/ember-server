package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D196 旋光 arcane — BEAM: every {@code every} s (a player within 8) a beam of {@code length} from the elite's locked
 * floored feet; after {@code warn} s it turns {@code sweep}° in {@code spin} s (direction flips every cast), the nearest
 * player in the middle of the swept arc. Swept-interval test, so a fast step cannot jump over it; once per player per cast.
 */
public final class AffixArcane implements AffixBehavior {

    public static final AffixArcane INSTANCE = new AffixArcane();
    public static final double ENGAGE = 8;

    private AffixArcane() { }

    @Override public String id() { return "arcane"; }
    @Override public AffixFamily family() { return AffixFamily.BEAM; }
    @Override public String fx() { return "SPELL_WITCH"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.arcaneEvery; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "脚下会亮起一道光束并转半圈（紫色预警标出起点和扫过的半边），退到 " + AffixCycle.fmt(v.arcaneLength) + " 格外或站到另半边";
    }

    /** per-hit damage */
    public static double dmg(double atk, EmberRunMaps.Variety v) { return atk * v.arcaneDmg; }

    /** sweep in radians */
    public static double sweepRad(EmberRunMaps.Variety v) { return Math.toRadians(v.arcaneSweep); }

    /** the spin is over ({@code elapsed} s after the warning) */
    public static boolean spun(double elapsed, EmberRunMaps.Variety v) { return elapsed >= v.arcaneSpin; }

    /** beam angle (rad) {@code elapsed} s into the spin; stops at the end of the sweep */
    public static double angle(double start, int sign, double sweepRad, double spin, double elapsed) {
        double f = spin <= 0 ? 1.0 : Math.max(0.0, Math.min(1.0, elapsed / spin));
        return start + (sign >= 0 ? 1 : -1) * sweepRad * f;
    }

    /** start angle so the target sits in the middle of the swept arc */
    public static double startAngle(Location o, Location target, int sign, double sweepRad) {
        double base = Math.atan2(target.getZ() - o.getZ(), target.getX() - o.getX());
        return base - (sign >= 0 ? 1 : -1) * sweepRad / 2.0;
    }

    /**
     * did the beam (length {@code len}, width {@code width}) pass over {@code p} while turning from {@code a0} to
     * {@code a1}? Swept-interval test on the player's polar angle, widened by the beam's half width at that distance.
     */
    public static boolean swept(Location o, double a0, double a1, double len, double width, Location p) {
        if (Math.abs(p.getY() - o.getY()) > 2.5) return false;
        double dx = p.getX() - o.getX(), dz = p.getZ() - o.getZ();
        double r = Math.sqrt(dx * dx + dz * dz);
        if (r > len) return false;
        if (r < 0.6) return true; // standing on the elite's feet: the pivot
        double lo = Math.min(a0, a1), span = Math.abs(a1 - a0);
        double half = Math.asin(Math.min(1.0, (width / 2.0) / r));
        double d = (Math.atan2(dz, dx) - lo + half) % (2 * Math.PI);
        if (d < 0) d += 2 * Math.PI;
        return d <= span + 2 * half;
    }
}
