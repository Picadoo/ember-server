package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberSigStampDryTest {
    @Test public void expectClears() {
        assertEquals(9, EmberSigStampDry.EXPECT_CLEARS); // ceil(1/0.12)
    }
    @Test public void missAppendEmptyWhenZero() {
        assertEquals("", EmberSigStampDry.missAppend(null));
    }
}
