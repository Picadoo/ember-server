package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPetHatchSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberPetHatchSoft.isPetOffer("pet_ashling"));
        assertEquals(300, EmberPetHatchSoft.consumeCoin(d, 600));
        assertEquals(600, EmberPetHatchSoft.consumeCoin(d, 600));
    }
}
