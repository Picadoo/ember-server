package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSigAcqTest {
    @Test public void rate_and_miss_and_menu() {
        assertEquals(12, EmberSigAcq.ratePct());
        assertTrue(EmberSigAcq.missLine("q01").contains("约12%"));
        assertTrue(EmberSigAcq.missLine("q01").contains("Q01"));
        assertTrue(EmberSigAcq.missLine("q01").contains("烙印"));
        assertTrue(EmberSigAcq.menuLine().contains("约12%"));
        assertTrue(EmberSigAcq.isRepeatMarkKey("sig_mark"));
        assertFalse(EmberSigAcq.isRepeatMarkKey("fc_sigmark"));
    }
}
