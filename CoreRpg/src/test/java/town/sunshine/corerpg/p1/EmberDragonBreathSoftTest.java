package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDragonBreathSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5.0, EmberDragonBreathSoft.consume(d, 10.0), 1e-9);
        assertEquals(10.0, EmberDragonBreathSoft.consume(d, 10.0), 1e-9);
    }
}
