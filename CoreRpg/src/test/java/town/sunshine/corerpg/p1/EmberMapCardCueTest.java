package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberMapCardCueTest {
    @Test public void chat_line_has_card_and_loot() {
        String s = EmberMapCardCue.chatLine("锈轨矿道", "主线", "学预警 · 重斩落空破绽", "焚烬 · 刃");
        assertTrue(s.contains("入场名片"));
        assertTrue(s.contains("学预警"));
        assertTrue(s.contains("焚烬"));
        assertTrue(EmberMapCardCue.chatLine(null, false, 0).contains("无名片"));
    }
}
