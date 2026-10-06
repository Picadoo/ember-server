package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.encounter.AffixArcane;
import town.sunshine.corerpg.p1.encounter.AffixBlazing;
import town.sunshine.corerpg.p1.encounter.AffixCharge;
import town.sunshine.corerpg.p1.encounter.AffixCycle;
import town.sunshine.corerpg.p1.encounter.AffixFirechain;
import town.sunshine.corerpg.p1.encounter.AffixFrost;
import town.sunshine.corerpg.p1.encounter.AffixMolten;
import town.sunshine.corerpg.p1.encounter.AffixMortar;
import town.sunshine.corerpg.p1.encounter.AffixRegen;
import town.sunshine.corerpg.p1.encounter.AffixShield;
import town.sunshine.corerpg.p1.encounter.AffixSplit;
import town.sunshine.corerpg.p1.encounter.AffixVenom;
import town.sunshine.corerpg.p1.encounter.EmberAffixes;

/**
 * D241 replay — the NEW code path: exactly what the 1.65.67 Director calls (its static delegates where it kept one,
 * else the {@code p1.encounter.Affix*} primitive directly).
 */
class PrimitiveAffixPath implements AffixPath {

    private static EmberRunDirector.Tracked elite(double atk) {
        return new EmberRunDirector.Tracked(null, "heavy", "r1", null, null, atk, 3, 2, null);
    }

    @Override public String name() { return "primitives-d241"; }
    @Override public double firstEvery(String id, EmberRunMaps.Variety v) { return EmberAffixes.firstEvery(id, v); }
    @Override public String how(String id, EmberRunMaps.Variety v) { return EmberAffixes.how(id, v); }
    @Override public String fx(String id) { return EmberRunDirector.affixFx(id).name(); }
    @Override public long firstNext(long now, double every) { return AffixCycle.firstNext(now, every); }
    @Override public long after(long now, double secs) { return AffixCycle.after(now, secs); }
    @Override public double shieldHp(double base, EmberRunMaps.Variety v) { return AffixShield.maxHp(base, v); }
    @Override public double engage(String id) {
        return "blazing".equals(id) ? AffixBlazing.ENGAGE : "regen".equals(id) ? AffixRegen.ENGAGE : "charge".equals(id) ? AffixCharge.ENGAGE
                : "venom".equals(id) ? AffixVenom.ENGAGE : "arcane".equals(id) ? AffixArcane.ENGAGE : 0;
    }
    @Override public double mortarReach() { return AffixMortar.REACH; }
    @Override public long relinkRetryMs() { return AffixFirechain.RELINK_RETRY_MS; }

    @Override public EmberRunMaps.Skill blaze(double atk, EmberRunMaps.Variety v) { return EmberRunDirector.blazeSkill(elite(atk), v); }
    @Override public EmberRunMaps.Skill charge(double atk, EmberRunMaps.Variety v, Vector dir) { return EmberRunDirector.chargeSkill(elite(atk), v, dir); }
    @Override public EmberRunMaps.Skill mortar(double atk, EmberRunMaps.Variety v) { return EmberRunDirector.mortarSkill(elite(atk), v); }
    @Override public EmberRunMaps.Skill molten(double atk, EmberRunMaps.Variety v) { return EmberRunDirector.moltenSkill(atk, v); }
    @Override public EmberRunMaps.Skill moltenBlast(double absDmg, EmberRunMaps.Variety v) { return AffixMolten.blast(absDmg, v); }
    @Override public double moltenDmg(double atk, EmberRunMaps.Variety v) { return AffixMolten.blastDmg(atk, v); }
    @Override public long moltenWarnAt(long now, EmberRunMaps.Variety v) { return AffixMolten.warnAt(now, v); }
    @Override public long moltenBoomAt(long warnAt, EmberRunMaps.Variety v) { return AffixMolten.boomAt(warnAt, v); }
    @Override public EmberRunMaps.Skill venom(double atk, EmberRunMaps.Variety v) { return EmberRunDirector.venomSkill(elite(atk), v); }
    @Override public EmberRunMaps.Skill jailer(double atk, EmberRunMaps.Variety v) { return EmberRunDirector.jailerSkill(elite(atk), v); }
    @Override public int jailerRootTicks(EmberRunMaps.Variety v) { return EmberRunDirector.jailerRootTicks(v); }
    @Override public double arcaneDmg(double atk, EmberRunMaps.Variety v) { return AffixArcane.dmg(atk, v); }
    @Override public double chainDmg(double atk, EmberRunMaps.Variety v) { return AffixFirechain.dmg(atk, v); }

    @Override public boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) { return EmberRunDirector.inShape(sk, o, dir, p); }
    @Override public Vector[] venomDirs(boolean diag) { return EmberRunDirector.venomDirs(diag); }
    @Override public boolean venomHits(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p) { return EmberRunDirector.venomHits(arm, o, dirs, p); }
    @Override public Vector chargeAim(Location o, Location t) { return AffixCharge.aim(o.getX(), o.getZ(), t.getX(), t.getZ()); }
    @Override public boolean chargeRoom(double run) { return AffixCharge.roomFor(run); }
    @Override public double sweepRad(EmberRunMaps.Variety v) { return AffixArcane.sweepRad(v); }
    @Override public double arcaneAngle(double start, int sign, double sweepRad, double spin, double elapsed) { return EmberRunDirector.arcaneAngle(start, sign, sweepRad, spin, elapsed); }
    @Override public double arcaneStartAngle(Location o, Location target, int sign, double sweepRad) { return EmberRunDirector.arcaneStartAngle(o, target, sign, sweepRad); }
    @Override public boolean arcaneSwept(Location o, double a0, double a1, double len, double width, Location p) { return EmberRunDirector.arcaneSwept(o, a0, a1, len, width, p); }
    @Override public boolean arcaneSpun(double el, EmberRunMaps.Variety v) { return AffixArcane.spun(el, v); }
    @Override public boolean chainTouches(Location a, Location b, double width, Location p) { return EmberRunDirector.chainTouches(a, b, width, p); }
    @Override public boolean chainBurnReady(Long last, long now, double tick) { return EmberRunDirector.chainBurnReady(last, now, tick); }
    @Override public boolean chainTooFar(double d2, EmberRunMaps.Variety v) { return AffixFirechain.tooFar(d2, v); }
    @Override public long chainLiveAt(long now, EmberRunMaps.Variety v) { return AffixFirechain.liveAt(now, v); }

    @Override public long regenWindowEnd(long now, EmberRunMaps.Variety v) { return AffixRegen.windowEnd(now, v); }
    @Override public boolean regenInterrupted(double hurt, double max, EmberRunMaps.Variety v) { return AffixRegen.interrupted(hurt, max, v); }
    @Override public double regenHeal(double hp, double max, EmberRunMaps.Variety v) { return AffixRegen.heal(hp, max, v); }
    @Override public boolean regenCounts(long windowEnd, double dmg) { return AffixRegen.counts(windowEnd, dmg); }
    @Override public long frostNext(long now, EmberRunMaps.Variety v) { return AffixFrost.nextTick(now, v); }
    @Override public boolean frostIn(double d2, EmberRunMaps.Variety v) { return AffixFrost.inAura(d2, v); }
    @Override public int frostSlowTicks(EmberRunMaps.Variety v) { return AffixFrost.slowTicks(v); }
    @Override public boolean frostOwnSlow(int amp, int dur, EmberRunMaps.Variety v) { return AffixFrost.ownSlow(amp, dur, v); }
    @Override public int splitCount(EmberRunMaps.Variety v) { return AffixSplit.count(v); }
    @Override public double splitAddHp(double base, EmberRunMaps.Variety v) { return AffixSplit.addMaxHp(base, v); }
}
