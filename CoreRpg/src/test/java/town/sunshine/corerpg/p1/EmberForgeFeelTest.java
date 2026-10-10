package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberForgeFeelTest {
    @Test public void enhance_ok_fail_and_refine() {
        assertTrue(EmberForgeFeel.line("enhance", "强化成功 +3 → +4（本档第 1/3 次）").contains("强化成功"));
        assertTrue(EmberForgeFeel.line("enhance", "强化失败，保持 +3（本档已失败 1 次，最多再 2 次必成）").contains("强化失败"));
        assertTrue(EmberForgeFeel.line("refine", "精工 0% → 2%（确定成功，成色不变）").contains("精工"));
        assertTrue(EmberForgeFeel.line("quality", "成色 标准 → 精良").contains("成色"));
        assertEquals("", EmberForgeFeel.line("dismantle", "分解 ok"));
    }
}
