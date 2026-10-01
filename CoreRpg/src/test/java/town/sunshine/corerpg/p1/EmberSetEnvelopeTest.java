package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberSetEngine.Hit;
import town.sunshine.corerpg.p1.EmberSetEngine.Outcome;
import town.sunshine.corerpg.p1.EmberSetEngine.Trigger;

import static org.junit.Assert.*;

/**
 * C14: top gear (T3 ×2 +10, 极品, 满精工, Lv60) B/H/D, and the §8.4 long-window envelopes reproduced by
 * driving the real engine: 1.6 swings/s all crit, 烬斩 every 8 s, burns / explosions as the engine allows.
 * Book: single target 焚烬 404.810 / 烬爆 389.196 / 炽愈 348.278; five targets 731.888 / 653.820 / 449.228.
 */
public class EmberSetEnvelopeTest {

    private final EmberTables t = EmberTables.defaults();

    private EmberLoadout top(String fam) {
        return EmberLoadout.compute(t, EmberItemData.create(fam, "blade", 3, 3, 3, 10, true, "admin"),
                EmberItemData.create(fam, "charm", 3, 3, 3, 10, true, "admin"), 60);
    }

    @Test public void topGearStats_C14() {
        EmberLoadout l = top("sustain");
        assertEquals(134.6, l.b, 1e-9);
        assertEquals(407.344, l.h, 1e-9);
        assertEquals(14.0, l.d, 1e-9);
        assertEquals(549.9144, l.ehp(), 1e-6);
        assertEquals(3, l.awakening);
    }

    /** average damage per second over a long window with {@code targets} mobs in range */
    private double simulate(String fam, int targets) {
        EmberLoadout l = top(fam);
        EmberSetEngine e = new EmberSetEngine();
        e.setLoadout(fam, l.awakening);
        long windowMs = 4_000_000L; // 4000 s
        double total = 0;
        long root = 0;
        long swingMs = 625; // 1.6/s
        int burnTargetCursor = 0;
        for (long now = 0; now <= windowMs; now += 25) {
            if (now % swingMs == 0) {
                total += EmberFormula.melee(t, l.b, 1.0, true); // all crits, main target only
                // spread ignites over all targets (main target rotates) to keep up to five burns alive
                String target = "m" + (burnTargetCursor++ % targets);
                Outcome o = e.onHit(Hit.melee(++root, 1.0, 1, target), now, l.b, l.h);
                if (o.trigger == Trigger.IGNITE) e.burns().ignite(target, o.amount, now);
                if (o.trigger == Trigger.EXPLODE) total += o.amount * Math.min(targets, EmberSetRules.BURST_MAX_TARGETS);
            }
            if (now % 8000 == 0) total += EmberFormula.skill(t, l.b) * Math.min(targets, 5);
            for (EmberBurnBook.Tick k : e.burns().due(now)) total += k.amount;
        }
        return total / (windowMs / 1000.0);
    }

    @Test public void singleTargetEnvelopes_C14() {
        assertEquals(404.810, simulate("scorch", 1), 0.5);
        assertEquals(389.196, simulate("burst", 1), 0.5);
        assertEquals(348.278, simulate("sustain", 1), 0.5);
    }

    @Test public void fiveTargetEnvelopes_C14() {
        // Book §8.4 writes 5 × 0.42B/s of burn for five targets. Under §4.2 (one ignite per 3 hits, 4 ticks each)
        // at most 1.6/3 × 4 = 2.133 burn ticks/s exist, so the reachable value is lower: the book figure is a
        // safe upper envelope, not a target. Reachable = 2.4B + 5×1.5B/8 + 2.1333×0.42B.
        double b = top("scorch").b;
        double reachable = 1.6 * 1.5 * b + 5 * 1.5 * b / 8.0 + (1.6 / 3.0 * 4) * 0.42 * b;
        double sim = simulate("scorch", 5);
        assertEquals(reachable, sim, 1.0);
        assertTrue(sim <= 731.888);
        assertEquals(653.820, simulate("burst", 5), 1.0);
        assertEquals(449.228, simulate("sustain", 5), 1.0);
    }

    @Test public void analyticEnvelopesMatchBook() {
        double b = top("burst").b;
        double base = 1.6 * 1.5 * b + 1.5 * b / 8.0;
        assertEquals(348.278, base, 0.001);
        assertEquals(404.810, base + 0.42 * b, 0.001);
        assertEquals(389.196, base + 1.6 / 5.0 * 0.95 * b, 0.001);
        assertEquals(731.888, 1.6 * 1.5 * b + 5 * 1.5 * b / 8.0 + 5 * 0.42 * b, 0.001);
        assertEquals(653.820, 1.6 * 1.5 * b + 5 * 1.5 * b / 8.0 + 5 * 1.6 / 5.0 * 0.95 * b, 0.001);
    }
}
