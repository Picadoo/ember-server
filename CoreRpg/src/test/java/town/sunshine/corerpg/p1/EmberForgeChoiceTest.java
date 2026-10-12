package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

/** D12257–D12259: forge choice-feel half-lines pure. */
public class EmberForgeChoiceTest {

    @Test
    public void brandWillGet() {
        String s = EmberForgeChoice.brandWillGet("猎缀");
        assertTrue(s.contains("将得到"));
        assertTrue(s.contains("猎缀"));
        assertTrue(s.contains("手感"));
        assertTrue(s.contains("不加伤"));
    }

    @Test
    public void convertLeftoverQiWhenZero() {
        String s = EmberForgeChoice.convertLeftover(0, "§f焚烬");
        assertTrue(s.contains("还可转 §f0"));
        assertTrue(s.contains("齐"));
    }

    @Test
    public void convertLeftoverGapWhenLeft() {
        String s = EmberForgeChoice.convertLeftover(1, "§f焚烬");
        assertTrue(s.contains("还可转 §f1"));
        assertTrue(s.contains("焚烬"));
        assertFalse(s.contains("齐"));
    }

    @Test
    public void rerollChoiceLockVsRandom() {
        assertEquals("§7选的是：§f锁定·只重抽档", EmberForgeChoice.rerollChoice(true));
        assertEquals("§7选的是：§f随机词条", EmberForgeChoice.rerollChoice(false));
    }
}
