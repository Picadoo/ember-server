package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberSetEngine.Hit;
import town.sunshine.corerpg.p1.EmberSetEngine.Kind;
import town.sunshine.corerpg.p1.EmberSetEngine.Outcome;
import town.sunshine.corerpg.p1.EmberSetEngine.Trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/** G02 SetRuntime rules, 策划书 §4.2–4.4 and §22.2 C06/C07/C09/C10/C11/C15/C16. */
public class EmberSetEngineTest {

    private static final double B = 100.0, H = 400.0, EPS = 1e-9;
    private long root = 0;

    private EmberSetEngine engine(String fam, int awk) {
        EmberSetEngine e = new EmberSetEngine();
        e.setLoadout(fam, awk);
        return e;
    }

    private Outcome hit(EmberSetEngine e, long now) {
        return e.onHit(Hit.melee(++root, 1.0, 50, "mob"), now, B, H);
    }

    // ---------------------------------------------------------------- trigger cadence

    @Test public void scorchIgnitesEveryThirdValidHit() {
        EmberSetEngine e = engine("scorch", 1);
        assertEquals(Trigger.NONE, hit(e, 0).trigger);
        assertEquals(Trigger.NONE, hit(e, 700).trigger);
        Outcome o = hit(e, 1400);
        assertEquals(Trigger.IGNITE, o.trigger);
        assertEquals(0.26 * B, o.amount, EPS);
        assertEquals(0, e.counter());
        hit(e, 2100); hit(e, 2800);
        assertEquals(Trigger.IGNITE, hit(e, 3500).trigger); // no cooldown for scorch
    }

    @Test public void burstEveryFifthWithHeldCounterDuringIcd() {
        EmberSetEngine e = engine("burst", 3);
        long t = 0;
        for (int i = 0; i < 4; i++) assertEquals(Trigger.NONE, hit(e, t += 100).trigger);
        Outcome o = hit(e, t += 100); // 5th at 500 ms
        assertEquals(Trigger.EXPLODE, o.trigger);
        assertEquals(0.95 * B, o.amount, EPS);
        assertEquals(3.5, o.radius, EPS);
        // five more fast hits inside the 3 s ICD: counter holds at 5, no explosion
        for (int i = 0; i < 8; i++) {
            assertEquals(Trigger.NONE, hit(e, t += 100).trigger);
            assertTrue(e.counter() <= 5);
        }
        assertEquals(5, e.counter());
        assertTrue(e.burstCdRemaining(t) > 0);
        // nothing fires in the background when the ICD ends: still 5 until the next valid hit
        assertEquals(5, e.counter());
        assertEquals(Trigger.EXPLODE, hit(e, 3500).trigger);
        assertEquals(0, e.counter());
    }

    @Test public void burstRadiusAndCoefficientPerAwakening() {
        assertEquals(0.65, EmberSetRules.burstCoef(1), EPS);
        assertEquals(0.80, EmberSetRules.burstCoef(2), EPS);
        assertEquals(3.0, EmberSetRules.burstRadius(1), EPS);
        assertEquals(3.0, EmberSetRules.burstRadius(2), EPS);
        assertEquals(3.5, EmberSetRules.burstRadius(3), EPS);
        assertEquals(0.34, EmberSetRules.burnCoef(2), EPS);
    }

    // ---------------------------------------------------------------- C10 sustain

    @Test public void sustainHealsOnFifthWithSixSecondIcd_C10() {
        EmberSetEngine e = engine("sustain", 2);
        long t = 0;
        Outcome last = null;
        for (int i = 0; i < 5; i++) last = hit(e, t += 100);
        assertEquals(Trigger.HEAL, last.trigger);
        assertEquals(0.0325 * H, last.amount, EPS);
        int heals = 0;
        for (int i = 0; i < 50; i++) if (hit(e, t += 100).trigger == Trigger.HEAL) heals++; // 5 s of 10 hits/s
        assertEquals("no second heal inside 6 s", 0, heals);
        assertEquals(5, e.counter());
        assertEquals(Trigger.HEAL, hit(e, 6600).trigger);
    }

    @Test public void sustainAmountFollowsMaxHpAtTrigger_noShieldMath_C10() {
        EmberSetEngine e = engine("sustain", 3);
        Outcome o = null;
        for (int i = 0; i < 5; i++) o = e.onHit(Hit.melee(++root, 1, 10, "a"), i * 100, B, 250.0);
        assertEquals(0.04 * 250.0, o.amount, EPS); // the heal itself is capped at max HP by EmberHeal (no shield)
    }

    @Test public void sustainMultiTargetDoesNotHealPerTarget_C10() {
        // one swing (one root id) hitting many mobs is one count; extra events with the same root never count
        EmberSetEngine e = engine("sustain", 1);
        long r = ++root;
        assertTrue(e.onHit(Hit.melee(r, 1, 10, "a"), 0, B, H).counted);
        for (String t : Arrays.asList("b", "c", "d", "e")) {
            assertFalse(e.onHit(Hit.melee(r, 1, 10, t), 0, B, H).counted);
            assertFalse(e.onHit(Hit.melee(r, 1, 10, t).kind(Kind.SWEEP), 0, B, H).counted);
        }
        assertEquals(1, e.counter());
    }

    // ---------------------------------------------------------------- C06 / C07 sources

    @Test public void onlyDirectMainTargetMeleeCounts_C06_C07() {
        EmberSetEngine e = engine("burst", 1);
        for (Kind k : Kind.values()) {
            if (k == Kind.MELEE_MAIN) continue;
            for (int i = 0; i < 10; i++) {
                Outcome o = e.onHit(Hit.melee(++root, 1, 50, "m" + i).kind(k), 100, B, H);
                assertFalse(k + " must not count", o.counted);
            }
        }
        assertEquals(0, e.counter());
    }

    @Test public void sweepOfFiveCountsOnce_skillOfFiveCountsZero_C06() {
        EmberSetEngine e = engine("scorch", 1);
        long r = ++root;
        e.onHit(Hit.melee(r, 1, 30, "main"), 0, B, H);
        for (int i = 0; i < 4; i++) e.onHit(Hit.melee(r, 1, 30, "s" + i).kind(Kind.SWEEP), 0, B, H);
        assertEquals(1, e.counter());
        for (int i = 0; i < 5; i++) e.onHit(Hit.melee(++root, 1, 90, "k" + i).kind(Kind.SKILL), 0, B, H);
        assertEquals(1, e.counter());
    }

    @Test public void burnAndExplosionKillsDoNotChain_C07() {
        EmberSetEngine e = engine("burst", 1);
        for (int i = 0; i < 5; i++) hit(e, i * 100);
        // explosion and burn events (including killing blows) feed back into the engine: no count, no trigger
        for (int i = 0; i < 20; i++) {
            Hit h = Hit.melee(++root, 1, 1000, "x" + i).kind(i % 2 == 0 ? Kind.EXPLOSION : Kind.BURN);
            h.targetAlive = true;
            Outcome o = e.onHit(h, 600, B, H);
            assertFalse(o.counted);
            assertEquals(Trigger.NONE, o.trigger);
        }
        assertEquals(0, e.counter());
    }

    // ---------------------------------------------------------------- C09 invalid hits

    @Test public void invalidHitsNeverCount_C09() {
        EmberSetEngine e = engine("sustain", 1);
        Hit inv = Hit.melee(++root, 1, 50, "a"); inv.targetInvulnerable = true;
        Hit zero = Hit.melee(++root, 1, 0, "a");
        Hit cancel = Hit.melee(++root, 1, 50, "a"); cancel.cancelled = true;
        Hit low = Hit.melee(++root, 0.89, 50, "a");
        Hit dead = Hit.melee(++root, 1, 50, "a"); dead.targetAlive = false;
        Hit friend = Hit.melee(++root, 1, 50, "a"); friend.targetEnemy = false;
        Hit noRoot = Hit.melee(0, 1, 50, "a");
        for (Hit h : Arrays.asList(inv, zero, cancel, low, dead, friend, noRoot)) {
            for (int i = 0; i < 6; i++) {
                Outcome o = e.onHit(h, 100 + i, B, H);
                assertFalse(o.counted);
                assertEquals(Trigger.NONE, o.trigger);
            }
        }
        assertEquals(0, e.counter());
        Hit ok = Hit.melee(++root, 1, 50, "a");
        assertTrue(e.onHit(ok, 200, B, H).counted);
        for (int i = 0; i < 10; i++) assertFalse("replayed root", e.onHit(ok, 300 + i, B, H).counted);
        assertEquals(1, e.counter());
    }

    // ---------------------------------------------------------------- C16 abnormal input

    @Test public void abnormalNumbersRejected_C16() {
        EmberSetEngine e = engine("burst", 1);
        assertFalse(e.onHit(Hit.melee(++root, Double.NaN, 50, "a"), 0, B, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1.5, 50, "a"), 0, B, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, Double.NaN, "a"), 0, B, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, Double.POSITIVE_INFINITY, "a"), 0, B, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, -5, "a"), 0, B, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, 50, "a"), 0, Double.NaN, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, 50, "a"), 0, -1, H).counted);
        assertFalse(e.onHit(Hit.melee(++root, 1, 50, "a"), 0, B, 0).counted);
        assertFalse(e.onHit(null, 0, B, H).counted);
        EmberSetEngine unknown = engine("frost", 2);
        assertFalse(unknown.onHit(Hit.melee(++root, 1, 50, "a"), 0, B, H).counted);
        assertEquals(0, e.counter());
    }

    // ---------------------------------------------------------------- combat timeout / C11 switching

    @Test public void countersClearAfterEightSecondsOutOfCombat() {
        EmberSetEngine e = engine("burst", 1);
        hit(e, 0); hit(e, 500); hit(e, 1000);
        assertEquals(3, e.counter());
        hit(e, 9001); // 8.001 s after the last combat → cleared first, then this hit counts as 1
        assertEquals(1, e.counter());
        e.touch(15000);  // took enemy damage at 15 s
        hit(e, 22000);   // 7 s later: still in combat
        assertEquals(2, e.counter());
    }

    @Test public void setChangeClearsCounterAndBurnsButNotCooldowns_C11() {
        EmberSetEngine e = engine("burst", 1);
        for (int i = 0; i < 5; i++) hit(e, i * 10);
        long cd = e.burstCdRemaining(100);
        assertTrue(cd > 0);
        hit(e, 200); hit(e, 210);
        assertTrue(e.setLoadout("sustain", 1));
        assertEquals(0, e.counter());
        assertTrue(e.setLoadout("burst", 1));
        assertEquals(cd, e.burstCdRemaining(100)); // switching back does not refresh the ICD
        // same family again (e.g. other blade of the same set) keeps the counter
        hit(e, 300); hit(e, 310);
        assertFalse(e.setLoadout("burst", 2));
        assertEquals(2, e.counter());
        // burns cleared on set change
        EmberSetEngine s = engine("scorch", 1);
        s.burns().ignite("mob", 26, 0);
        s.setLoadout("none", 0);
        assertTrue(s.burns().isEmpty());
    }

    @Test public void reconnectRestoresRemainingCooldowns_C11() {
        EmberSetEngine e = engine("sustain", 1);
        for (int i = 0; i < 5; i++) hit(e, 1000 + i);
        long remain = e.sustainCdRemaining(3000);       // ≈ 4004 ms left at quit
        long burstRemain = e.burstCdRemaining(3000);
        e.clearCombat();
        // new session on a different monotonic clock
        EmberSetEngine back = engine("sustain", 1);
        back.restoreCooldowns(burstRemain, remain, 50_000_000L);
        assertEquals(remain, back.sustainCdRemaining(50_000_000L));
        for (int i = 0; i < 5; i++) assertEquals(Trigger.NONE, hit(back, 50_000_000L + i).trigger);
        assertEquals(Trigger.HEAL, hit(back, 50_000_000L + remain).trigger);
        // a tampered huge remaining value is capped to one ICD
        EmberSetEngine capped = engine("burst", 1);
        capped.restoreCooldowns(999_999, 0, 0);
        assertEquals(EmberSetRules.BURST_ICD_MS, capped.burstCdRemaining(0));
    }

    @Test public void deathOrLeavingClearsCounterAndBurns() {
        EmberSetEngine e = engine("scorch", 1);
        hit(e, 0); hit(e, 1);
        e.burns().ignite("m", 10, 0);
        e.clearCombat();
        assertEquals(0, e.counter());
        assertTrue(e.burns().isEmpty());
    }

    // ---------------------------------------------------------------- C15 same-frame peaks

    @Test public void sameFrameMeleeSkillExplosionAllowed_C15() {
        EmberSetEngine e = engine("burst", 3);
        Outcome o = null;
        for (int i = 0; i < 5; i++) o = hit(e, 1000); // five valid swings landing in one tick (lag) — all distinct roots
        assertEquals(Trigger.EXPLODE, o.trigger); // no per-second limiter blocks a legal burst
        // a skill in the same frame is not blocked and not counted
        assertFalse(e.onHit(Hit.melee(++root, 1, 150, "m").kind(Kind.SKILL), 1000, B, H).counted);
    }

    // ---------------------------------------------------------------- explosion targets

    @Test public void explosionTargetsSortedByDistanceThenEntityId() {
        List<EmberSetEngine.Candidate> in = new ArrayList<EmberSetEngine.Candidate>();
        in.add(new EmberSetEngine.Candidate("far", 1, 3.4));
        in.add(new EmberSetEngine.Candidate("tieB", 9, 1.0));
        in.add(new EmberSetEngine.Candidate("main", 50, 0.0));
        in.add(new EmberSetEngine.Candidate("tieA", 7, 1.0));
        in.add(new EmberSetEngine.Candidate("out", 2, 3.6));
        in.add(new EmberSetEngine.Candidate("mid", 3, 2.0));
        in.add(new EmberSetEngine.Candidate("x", 4, 2.5));
        List<EmberSetEngine.Candidate> a = EmberSetEngine.pickTargets(in, 3.5);
        assertEquals(5, a.size());
        assertEquals("main", a.get(0).id);
        assertEquals("tieA", a.get(1).id);
        assertEquals("tieB", a.get(2).id);
        assertEquals("mid", a.get(3).id);
        assertEquals("x", a.get(4).id);
        java.util.Collections.reverse(in); // query order must not matter
        List<EmberSetEngine.Candidate> b = EmberSetEngine.pickTargets(in, 3.5);
        for (int i = 0; i < 5; i++) assertEquals(a.get(i).id, b.get(i).id);
        // radius 3 (awakening I/II) drops the 3.4 one even when there is room
        assertEquals(2, EmberSetEngine.pickTargets(Arrays.asList(
                new EmberSetEngine.Candidate("main", 1, 0), new EmberSetEngine.Candidate("far", 2, 3.4),
                new EmberSetEngine.Candidate("near", 3, 2.9)), 3.0).size());
    }

    // ---------------------------------------------------------------- D439 almost-ready counter (UI hook precondition)
    @Test public void almostReadyCounterIsEveryMinusOne_reasonCount_D439() {
        for (String fam : Arrays.asList("burst", "sustain", "scorch")) {
            EmberSetEngine e = engine(fam, 1);
            int ev = e.every();
            assertTrue(fam + " every>1", ev > 1);
            long t = 0;
            Outcome last = null;
            for (int i = 0; i < ev - 1; i++) {
                last = hit(e, t += 100);
                assertEquals(fam + " pre-proc", Trigger.NONE, last.trigger);
                assertEquals(fam + " reason", "count", last.reason);
            }
            assertEquals(fam + " almost", ev - 1, last.counter);
            assertEquals(fam + " engine", ev - 1, e.counter());
        }
    }

    @Test public void hudText() {
        EmberSetEngine e = engine("burst", 1);
        assertNull(e.hud(0));
        hit(e, 0); hit(e, 1); hit(e, 2); hit(e, 3);
        assertEquals("烬爆 4/5", e.hud(10));
        hit(e, 4);
        assertTrue(e.hud(1000).startsWith("烬爆 0/5 · 冷却 2.0s"));
    }
}
