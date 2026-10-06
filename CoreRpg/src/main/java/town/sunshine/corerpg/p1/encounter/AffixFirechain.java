package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D196 火链 firechain — TETHER: a burning link between the elite and its nearest room mob (within {@code range});
 * touching it burns atk × {@code dmg}, at most once per {@code tick} s per player. Partner dead / gone / too far →
 * re-link to the next nearest after a {@code warn} s smoke line; no partner left → no chain (retry every 1 s).
 */
public final class AffixFirechain implements AffixBehavior {

    public static final AffixFirechain INSTANCE = new AffixFirechain();
    /** retry delay when no partner is in range */
    public static final long RELINK_RETRY_MS = 1000L;

    private AffixFirechain() { }

    @Override public String id() { return "firechain"; }
    @Override public AffixFamily family() { return AffixFamily.TETHER; }
    @Override public String fx() { return "FLAME"; }
    @Override public double firstEvery(EmberRunMaps.Variety v) { return v.chainLinkWarn; }
    @Override public String how(EmberRunMaps.Variety v) {
        return "和身边一只怪连着一条火链，别站在两只怪之间；先杀掉被连的那只，火链会换人（换之前有 " + AffixCycle.fmt(v.chainLinkWarn) + " 秒烟线预警）";
    }

    /** per-burn damage */
    public static double dmg(double atk, EmberRunMaps.Variety v) { return atk * v.chainLinkDmg; }

    /** a new link goes live (burns) after its warning */
    public static long liveAt(long now, EmberRunMaps.Variety v) { return AffixCycle.after(now, v.chainLinkWarn); }

    /** the linked partner drifted too far (squared distance) — drop the link */
    public static boolean tooFar(double dist2, EmberRunMaps.Variety v) {
        return dist2 > (v.chainLinkRange + 4) * (v.chainLinkRange + 4);
    }

    /** is {@code p} touching the chain segment a–b (horizontal distance ≤ width/2, within the two mobs' heights)? */
    public static boolean touches(Location a, Location b, double width, Location p) {
        double lo = Math.min(a.getY(), b.getY()) - 1.0, hi = Math.max(a.getY(), b.getY()) + 2.5;
        if (p.getY() < lo || p.getY() > hi) return false;
        double ax = a.getX(), az = a.getZ(), bx = b.getX() - ax, bz = b.getZ() - az;
        double px = p.getX() - ax, pz = p.getZ() - az;
        double l2 = bx * bx + bz * bz;
        double f = l2 < 1e-9 ? 0.0 : Math.max(0.0, Math.min(1.0, (px * bx + pz * bz) / l2));
        double ex = px - f * bx, ez = pz - f * bz;
        return ex * ex + ez * ez <= (width / 2.0) * (width / 2.0);
    }

    /** per-player burn cooldown */
    public static boolean burnReady(Long last, long now, double tick) {
        return last == null || now - last >= (long) (tick * 1000);
    }
}
