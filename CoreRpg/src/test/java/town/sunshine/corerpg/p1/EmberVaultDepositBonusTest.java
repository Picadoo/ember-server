package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberVaultDepositBonusTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberVaultDepositBonus.ready(d));
        d.addPeriodCount(EmberVaultDepositBonus.C_USED, "day", 1);
        assertFalse(EmberVaultDepositBonus.ready(d));
    }
}
