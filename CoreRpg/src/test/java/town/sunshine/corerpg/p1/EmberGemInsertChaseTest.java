package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberGemInsertChaseTest {
    @Test public void emptyNull() {
        assertEquals(-1, EmberGemInsertChase.firstEmptyHole(null));
    }
}
