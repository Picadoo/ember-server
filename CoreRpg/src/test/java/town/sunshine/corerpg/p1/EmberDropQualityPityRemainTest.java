package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDropQualityPityRemainTest {
    @Test public void remainLine() {
        assertEquals(5, EmberDropQualityPity.clearsLeft(0));
        assertEquals(0, EmberDropQualityPity.clearsLeft(5));
        assertEquals("成色软保底 0/5 · 再连出标准5次触发精良底", EmberDropQualityPity.remainLine(0));
        assertEquals("成色软保底已满 · 下次主线装必≥精良", EmberDropQualityPity.remainLine(5));
    }
}
