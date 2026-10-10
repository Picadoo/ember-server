package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** D945 / ARCH S2 follow-up: ledger glance pure strings. */
public class EmberLedgerGlanceTest {

    @Test
    public void lineEmpty() {
        assertEquals("§8补领：无待领", EmberLedgerGlance.line(0));
        assertEquals("§8补领：无待领", EmberLedgerGlance.line(-1));
    }

    @Test
    public void linePending() {
        String s = EmberLedgerGlance.line(3);
        assertTrue(s.contains("3"));
        assertTrue(s.contains("待领"));
        assertTrue(s.contains("/corerpg p1 claim"));
    }
}
