package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * D241 replay: every number / predicate the affix tick consumes, behind one interface so the same scripted fight can
 * be replayed through the pre-D241 code ({@link LegacyAffixPath}, verbatim from 46736db) and through the primitives
 * ({@link PrimitiveAffixPath}).
 */
interface AffixPath {
    String name();
    // promotion
    double firstEvery(String id, EmberRunMaps.Variety v);
    String how(String id, EmberRunMaps.Variety v);
    String fx(String id);
    long firstNext(long now, double every);
    long after(long now, double secs);
    double shieldHp(double base, EmberRunMaps.Variety v);
    /** nearest-player range that lets a periodic affix arm (0 = no check) */
    double engage(String id);
    double mortarReach();
    long relinkRetryMs();
    // damage packets
    EmberRunMaps.Skill blaze(double atk, EmberRunMaps.Variety v);
    EmberRunMaps.Skill charge(double atk, EmberRunMaps.Variety v, Vector dir);
    EmberRunMaps.Skill mortar(double atk, EmberRunMaps.Variety v);
    EmberRunMaps.Skill molten(double atk, EmberRunMaps.Variety v);
    EmberRunMaps.Skill moltenBlast(double absDmg, EmberRunMaps.Variety v);
    double moltenDmg(double atk, EmberRunMaps.Variety v);
    long moltenWarnAt(long now, EmberRunMaps.Variety v);
    long moltenBoomAt(long warnAt, EmberRunMaps.Variety v);
    EmberRunMaps.Skill venom(double atk, EmberRunMaps.Variety v);
    EmberRunMaps.Skill jailer(double atk, EmberRunMaps.Variety v);
    int jailerRootTicks(EmberRunMaps.Variety v);
    double arcaneDmg(double atk, EmberRunMaps.Variety v);
    double chainDmg(double atk, EmberRunMaps.Variety v);
    // geometry
    boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p);
    Vector[] venomDirs(boolean diag);
    boolean venomHits(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p);
    Vector chargeAim(Location o, Location target);
    boolean chargeRoom(double run);
    double sweepRad(EmberRunMaps.Variety v);
    double arcaneAngle(double start, int sign, double sweepRad, double spin, double elapsed);
    double arcaneStartAngle(Location o, Location target, int sign, double sweepRad);
    boolean arcaneSwept(Location o, double a0, double a1, double len, double width, Location p);
    boolean arcaneSpun(double elapsed, EmberRunMaps.Variety v);
    boolean chainTouches(Location a, Location b, double width, Location p);
    boolean chainBurnReady(Long last, long now, double tick);
    boolean chainTooFar(double dist2, EmberRunMaps.Variety v);
    long chainLiveAt(long now, EmberRunMaps.Variety v);
    // regen / frost / split
    long regenWindowEnd(long now, EmberRunMaps.Variety v);
    boolean regenInterrupted(double hurt, double maxHp, EmberRunMaps.Variety v);
    double regenHeal(double hp, double maxHp, EmberRunMaps.Variety v);
    boolean regenCounts(long windowEnd, double dmg);
    long frostNext(long now, EmberRunMaps.Variety v);
    boolean frostIn(double dist2, EmberRunMaps.Variety v);
    int frostSlowTicks(EmberRunMaps.Variety v);
    boolean frostOwnSlow(int amp, int dur, EmberRunMaps.Variety v);
    int splitCount(EmberRunMaps.Variety v);
    double splitAddHp(double base, EmberRunMaps.Variety v);
}
