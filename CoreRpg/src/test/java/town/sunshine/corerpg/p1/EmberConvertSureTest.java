package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberConvertSureTest {
    @Test public void eligibleEnhance() {
        assertFalse(EmberConvertSure.eligible(null));
    }
}
