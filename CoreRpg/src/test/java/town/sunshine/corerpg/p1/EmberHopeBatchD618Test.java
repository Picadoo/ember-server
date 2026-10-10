package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmberHopeBatchD618Test {

    @Test
    public void bounty_featured_hope() {
        assertTrue(EmberHopeMeter.bountyHope(0, 1, "30币").contains("0/1"));
        assertTrue(EmberHopeMeter.bountyHope(3, 0, "x").contains("已全部完成"));
        assertTrue(EmberHopeMeter.featuredHope(2, 3).contains("还剩 2"));
        assertTrue(EmberHopeMeter.featuredHope(0, 3).contains("已领满"));
    }

    @Test
    public void talent_next_hope_null() {
        assertTrue(EmberGrowth.nextHope(null, null, 0, 0).contains("未配置"));
    }
}
