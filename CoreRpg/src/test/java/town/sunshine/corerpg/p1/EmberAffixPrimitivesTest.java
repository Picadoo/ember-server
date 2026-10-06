package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.junit.Test;
import town.sunshine.corerpg.p1.encounter.AffixArcane;
import town.sunshine.corerpg.p1.encounter.AffixBehavior;
import town.sunshine.corerpg.p1.encounter.AffixBlazing;
import town.sunshine.corerpg.p1.encounter.AffixCharge;
import town.sunshine.corerpg.p1.encounter.AffixCycle;
import town.sunshine.corerpg.p1.encounter.AffixFamily;
import town.sunshine.corerpg.p1.encounter.AffixFirechain;
import town.sunshine.corerpg.p1.encounter.AffixFrost;
import town.sunshine.corerpg.p1.encounter.AffixJailer;
import town.sunshine.corerpg.p1.encounter.AffixMolten;
import town.sunshine.corerpg.p1.encounter.AffixMortar;
import town.sunshine.corerpg.p1.encounter.AffixRegen;
import town.sunshine.corerpg.p1.encounter.AffixShield;
import town.sunshine.corerpg.p1.encounter.AffixSplit;
import town.sunshine.corerpg.p1.encounter.AffixVenom;
import town.sunshine.corerpg.p1.encounter.EmberAffixes;
import town.sunshine.corerpg.p1.encounter.EmberShape;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/** D241 / ARCH S3-12: one primitive class per affix (p1.encounter.Affix*) — bundled bv58 numbers. */
public final class EmberAffixPrimitivesTest {

    private static final EmberRunMaps.Variety V = new EmberRunMaps.Variety(EmberAffixReplayTest.bundledVariety());
    private static final double ATK = 20;
    private static Location at(double x, double y, double z) { return new Location(null, x, y, z); }

    @Test public void registryCoversKnownInOrderWithFamiliesAndValidGlow() {
        assertEquals(EmberRunMaps.Variety.KNOWN, new ArrayList<String>(EmberAffixes.all().keySet()));
        for (AffixBehavior b : EmberAffixes.all().values()) {
            assertSame(b, EmberAffixes.of(b.id()));
            assertEquals(EmberRunMaps.Variety.label(b.id()), b.label());
            Particle.valueOf(b.fx()); // throws if not a 1.12 particle
            assertEquals(Particle.valueOf(b.fx()), EmberRunDirector.affixFx(b.id()));
            assertTrue(b.firstEvery(V) > 0);
            assertFalse(b.how(V).isEmpty());
        }
        assertEquals(AffixFamily.CIRCLE, AffixBlazing.INSTANCE.family());
        assertEquals(AffixFamily.STRIP, AffixCharge.INSTANCE.family());
        assertEquals(AffixFamily.CROSS, AffixVenom.INSTANCE.family());
        assertEquals(AffixFamily.BEAM, AffixArcane.INSTANCE.family());
        assertEquals(AffixFamily.TETHER, AffixFirechain.INSTANCE.family());
        assertEquals(AffixFamily.AURA, AffixFrost.INSTANCE.family());
        assertEquals(AffixFamily.CHANNEL, AffixRegen.INSTANCE.family());
        assertEquals(AffixFamily.DEATH_BLAST, AffixMolten.INSTANCE.family());
        assertEquals(AffixFamily.DEATH_SPAWN, AffixSplit.INSTANCE.family());
        assertEquals(AffixFamily.STAT, AffixShield.INSTANCE.family());
        // unknown id → the Director's old fallbacks
        assertNull(EmberAffixes.of("vortex"));
        assertNull(EmberAffixes.of(null));
        assertEquals(V.blazeEvery, EmberAffixes.firstEvery("vortex", V), 0);
        assertEquals("vortex", EmberAffixes.how("vortex", V));
        assertEquals(Particle.END_ROD, EmberRunDirector.affixFx("vortex"));
        assertEquals(Particle.END_ROD, EmberRunDirector.affixFx(null));
    }

    @Test public void cycleClock() {
        assertEquals(1000 + 1500 + 3000, AffixCycle.firstNext(1000, 3.0));
        assertEquals(1000 + 1299, AffixCycle.after(1000, 1.2999)); // truncates like the old (long) cast
        assertTrue(AffixCycle.ready(5, 5));
        assertFalse(AffixCycle.ready(4, 5));
        assertFalse(AffixCycle.armed(0));
        assertTrue(AffixCycle.lands(10, 10));
        assertEquals("3", AffixCycle.fmt(3.0));
        assertEquals("1.5", AffixCycle.fmt(1.5));
    }

    @Test public void blazing() {
        EmberRunMaps.Skill s = AffixBlazing.skill(ATK, V);
        assertEquals("circle", s.type);
        assertEquals(1.5, s.radius, 0);
        assertEquals(20.0, s.dmg, 0);
        assertEquals(0.0, s.kb, 0);
        assertTrue(EmberShape.inShape(s, at(0, 64, 0), new Vector(1, 0, 0), at(1.4, 64, 0)));
        assertFalse(EmberShape.inShape(s, at(0, 64, 0), new Vector(1, 0, 0), at(1.6, 64, 0)));
        assertEquals(3.0, AffixBlazing.INSTANCE.firstEvery(V), 0);
        assertEquals("脚下每 3 秒落一圈火（半径 1.5，1 秒预警，看到火圈就退开）", AffixBlazing.INSTANCE.how(V));
        assertEquals(6, AffixBlazing.ENGAGE, 0);
    }

    @Test public void split() {
        assertEquals(2, AffixSplit.count(V));
        assertEquals(15.0, AffixSplit.addMaxHp(30, V), 0);
        assertEquals(1.0, AffixSplit.addMaxHp(1, V), 0); // never below 1
        assertEquals("死后分裂成 2 个小怪（门要等它们也倒下）", AffixSplit.INSTANCE.how(V));
    }

    @Test public void shield() {
        assertEquals(160.0, AffixShield.maxHp(100, V), 1e-9);
        assertEquals("生命 ×1.6", AffixShield.INSTANCE.how(V));
        assertEquals("END_ROD", AffixShield.INSTANCE.fx());
    }

    @Test public void regen() {
        assertEquals(1500, AffixRegen.windowEnd(0, V));
        assertFalse(AffixRegen.interrupted(4.9, 100, V)); // < 5 % of max HP
        assertTrue(AffixRegen.interrupted(5.0, 100, V));
        assertEquals(10.0, AffixRegen.heal(50, 100, V), 1e-9);
        assertEquals(3.0, AffixRegen.heal(97, 100, V), 1e-9);  // capped at missing HP
        assertTrue(AffixRegen.heal(100, 100, V) <= 0);
        assertTrue(AffixRegen.counts(1, 0.1));
        assertFalse(AffixRegen.counts(0, 5));
        assertFalse(AffixRegen.counts(1, 0));
    }

    @Test public void charge() {
        EmberRunMaps.Skill s = AffixCharge.skill(ATK, V, new Vector(1, 0, 0));
        assertEquals("charge", s.type);
        assertEquals(7.0, s.length, 0);
        assertEquals(2.5, s.width, 0);
        assertEquals(20.0, s.dmg, 0);
        assertTrue(AffixCharge.roomFor(2.0));
        assertFalse(AffixCharge.roomFor(1.99));
        Vector d = AffixCharge.aim(0, 0, 3, 4);
        assertEquals(0.6, d.getX(), 1e-12);
        assertEquals(0.8, d.getZ(), 1e-12);
        assertEquals(0.0, d.getY(), 0);
        Vector z = AffixCharge.aim(1, 1, 1, 1);
        assertEquals(1.0, z.getZ(), 0);
    }

    @Test public void frost() {
        assertTrue(AffixFrost.inAura(9.0, V));
        assertFalse(AffixFrost.inAura(9.01, V));
        assertEquals(20, AffixFrost.slowTicks(V));     // 0.5 s → 10 + 10
        assertTrue(AffixFrost.ownSlow(1, 25, V));
        assertFalse(AffixFrost.ownSlow(1, 26, V));      // a longer slow from elsewhere stays
        assertFalse(AffixFrost.ownSlow(2, 10, V));      // other amplifier stays
        assertEquals(500, AffixFrost.nextTick(0, V));
    }

    @Test public void mortar() {
        EmberRunMaps.Skill s = AffixMortar.skill(ATK, V);
        assertEquals("circle", s.type);
        assertEquals(2.0, s.radius, 0);
        assertEquals(0.0, s.kb, 0);
        assertEquals(20.0, s.dmg, 0);
        assertEquals(16, AffixMortar.REACH, 0);
    }

    @Test public void molten() {
        assertTrue(AffixMolten.triggers(false));
        assertFalse(AffixMolten.triggers(true));
        assertEquals(24.0, AffixMolten.blastDmg(ATK, V), 1e-9);
        assertEquals(400, AffixMolten.warnAt(0, V));
        assertEquals(400 + 1300, AffixMolten.boomAt(400, V));
        EmberRunMaps.Skill a = AffixMolten.skill(ATK, V), b = AffixMolten.blast(AffixMolten.blastDmg(ATK, V), V);
        assertEquals(a.dmg, b.dmg, 0);
        assertEquals(2.5, a.radius, 0);
        assertEquals(0.0, a.kb, 0);
    }

    @Test public void venom() {
        EmberRunMaps.Skill arm = AffixVenom.skill(ATK, V);
        assertEquals(-4.0, arm.stripFrom(), 0);
        assertEquals(4.0, arm.stripTo(), 0);
        Location o = at(0, 64, 0);
        assertTrue(AffixVenom.hits(arm, o, AffixVenom.dirs(false), at(3, 64, 0)));
        assertFalse(AffixVenom.hits(arm, o, AffixVenom.dirs(false), at(2.5, 64, 2.5)));
        assertTrue(AffixVenom.hits(arm, o, AffixVenom.dirs(true), at(2.5, 64, 2.5)));
        assertFalse(AffixVenom.hits(arm, o, AffixVenom.dirs(false), at(3, 67, 0))); // > 2.5 above
    }

    @Test public void jailer() {
        EmberRunMaps.Skill s = AffixJailer.skill(ATK, V);
        assertEquals(1.6, s.radius, 0);
        assertEquals(10.0, s.dmg, 0);
        assertEquals(20, AffixJailer.rootTicks(V));
        assertEquals("脚下亮小圈就走开，被罩住会定身 1 秒", AffixJailer.INSTANCE.how(V));
    }

    @Test public void arcane() {
        double sw = AffixArcane.sweepRad(V);
        assertEquals(Math.PI, sw, 1e-12);
        Location o = at(0, 64, 0);
        double start = AffixArcane.startAngle(o, at(0, 64, 3), 1, sw); // target on +z (π/2) → start at 0
        assertEquals(0.0, start, 1e-12);
        assertEquals(Math.PI / 2, AffixArcane.angle(start, 1, sw, 3.0, 1.5), 1e-12);
        assertEquals(Math.PI, AffixArcane.angle(start, 1, sw, 3.0, 9), 1e-12); // stops at the end
        assertTrue(AffixArcane.swept(o, 0, Math.PI / 2, 5, 1.2, at(2, 64, 2)));
        assertFalse(AffixArcane.swept(o, 0, Math.PI / 2, 5, 1.2, at(-2, 64, -2)));
        assertFalse(AffixArcane.swept(o, 0, Math.PI / 2, 5, 1.2, at(4, 64, 4)));  // beyond the beam length
        assertTrue(AffixArcane.swept(o, 0, 0.01, 5, 1.2, at(0.3, 64, 0.3)));      // the pivot
        assertEquals(20.0, AffixArcane.dmg(ATK, V), 0);
        assertTrue(AffixArcane.spun(3.0, V));
        assertFalse(AffixArcane.spun(2.99, V));
    }

    @Test public void firechain() {
        Location a = at(0, 64, 0), b = at(6, 64, 0);
        assertTrue(AffixFirechain.touches(a, b, 1.0, at(3, 64, 0.4)));
        assertFalse(AffixFirechain.touches(a, b, 1.0, at(3, 64, 0.6)));
        assertFalse(AffixFirechain.touches(a, b, 1.0, at(7, 64, 0)));  // past the partner
        assertTrue(AffixFirechain.burnReady(null, 0, 1.0));
        assertFalse(AffixFirechain.burnReady(1000L, 1999, 1.0));
        assertTrue(AffixFirechain.burnReady(1000L, 2000, 1.0));
        assertEquals(6.0, AffixFirechain.dmg(ATK, V), 1e-9);
        assertFalse(AffixFirechain.tooFar(196, V));   // (10 + 4)²
        assertTrue(AffixFirechain.tooFar(196.01, V));
        assertEquals(1200, AffixFirechain.liveAt(0, V));
    }

    /** the Director's kept statics are now one-line delegates — same objects / numbers */
    @Test public void directorDelegates() {
        EmberRunDirector.Tracked t = new EmberRunDirector.Tracked(null, "heavy", "r1", null, null, ATK, 3, 2, null);
        assertEquals(AffixBlazing.skill(ATK, V).dmg, EmberRunDirector.blazeSkill(t, V).dmg, 0);
        assertEquals(AffixVenom.skill(ATK, V).width, EmberRunDirector.venomSkill(t, V).width, 0);
        assertEquals(AffixJailer.rootTicks(V), EmberRunDirector.jailerRootTicks(V));
        assertEquals(AffixMolten.skill(ATK, V).dmg, EmberRunDirector.moltenSkill(ATK, V).dmg, 0);
    }
}
