package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberEquipPathTest {
    @Test public void adjust() {
        assertEquals(EmberRunRules.UP_ASK, EmberEquipPath.adjust(EmberRunRules.UP_AUTO, EmberEquipPath.KEEP));
        assertEquals(EmberRunRules.UP_AUTO, EmberEquipPath.adjust(EmberRunRules.UP_AUTO, EmberEquipPath.QUICK));
        assertEquals(EmberRunRules.UP_AUTO, EmberEquipPath.adjust(EmberRunRules.UP_ASK, EmberEquipPath.BOLD));
        assertEquals(EmberRunRules.UP_ASK, EmberEquipPath.adjust(EmberRunRules.UP_ASK, EmberEquipPath.QUICK));
        assertEquals(EmberRunRules.UP_ASK_SWAP, EmberEquipPath.adjust(EmberRunRules.UP_ASK_SWAP, EmberEquipPath.BOLD));
        assertEquals(EmberEquipPath.BOLD, EmberEquipPath.parse("大胆"));
    }
}
