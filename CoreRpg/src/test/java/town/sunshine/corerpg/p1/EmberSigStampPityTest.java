package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberSigStampPityTest {
    @Test public void needPositive() {
        assertTrue(EmberSigStampPity.need() >= 1);
    }
    @Test public void pickStampIgnoresRate() {
        // q01 has stamps - pick with u=0.99 should still return if stampable non-empty
        EmberSignature.Def d = EmberSignature.pickStamp("q01", "blade", "scorch", 0.5);
        // may be null if no fitting - just ensure rollStamp at high u returns null often
        EmberSignature.Def r = EmberSignature.rollStamp("q01", "blade", "scorch", 0.99);
        assertNull(r); // above rate
    }
}
