package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class KnockbackGuardTest {
    @Test
    public void cancelsOnlyInsideWindowAndOnce() {
        KnockbackGuard g = new KnockbackGuard();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        g.mark(a, 1000);
        assertFalse(g.consume(b, 1000));
        assertTrue(g.consume(a, 1100));
        assertFalse("mark is consumed", g.consume(a, 1100));
        g.mark(a, 2000);
        assertFalse("late velocity (a later vanilla hit) is not cancelled", g.consume(a, 2000 + KnockbackGuard.WINDOW_MS + 1));
    }

    @Test
    public void pruneDropsStale() {
        KnockbackGuard g = new KnockbackGuard();
        g.mark(UUID.randomUUID(), 0);
        g.mark(UUID.randomUUID(), 1000);
        g.prune(500);
        assertEquals(1, g.size());
    }
}
