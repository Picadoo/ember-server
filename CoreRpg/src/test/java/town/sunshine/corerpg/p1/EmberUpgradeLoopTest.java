package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberUpgradeLoopTest {
    @Test public void followHintHasDeterministicSlots() {
        String h = EmberUpgradeLoop.followHint();
        assertTrue(h.contains("强化"));
        assertTrue(h.contains("成色") || h.contains("精工"));
    }
    @Test public void primaryPrefersEnhanceWhenAvailable() {
        EmberItemData d = new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 2), "scorch", "blade", 2, 0, 0, 0, 0, true, "drop", 1, 1);
        String[] btn = EmberUpgradeLoop.primaryBtn(d);
        assertEquals("[去强化]", btn[0]);
    }
}
