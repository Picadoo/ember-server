package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberHopeMeterD623Test {
    @Test public void signHope() {
        assertTrue(EmberHopeMeter.signHope(false, 3, "币").contains("今天第"));
        assertTrue(EmberHopeMeter.signHope(true, 3, "币").contains("已签"));
    }
    @Test public void onlineHope() {
        assertTrue(EmberHopeMeter.onlineHope(10, 15, "奖").contains("10/15"));
        assertTrue(EmberHopeMeter.onlineHope(120, 0, "").contains("档位已齐"));
    }
    @Test public void abyssHope() {
        assertTrue(EmberHopeMeter.abyssHope(0, 0, 0, false).contains("未开放"));
        assertTrue(EmberHopeMeter.abyssHope(3, 4, 200, true).contains("费用够"));
    }
    @Test public void raidHope() {
        assertTrue(EmberHopeMeter.raidHope(1, 3).contains("1/3"));
        assertTrue(EmberHopeMeter.raidHope(3, 3).contains("已满"));
    }
    @Test public void gearlibHope() {
        assertTrue(EmberHopeMeter.gearlibHope(12, 4, 10).contains("库存"));
    }
}
