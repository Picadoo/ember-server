package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSigStampNudgeTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        EmberSigStampDry.onMiss(d);
        assertEquals(1, EmberSigStampDry.get(d));
        assertEquals(1, EmberSigStampNudge.maybeExtra(d));
        assertEquals(2, EmberSigStampDry.get(d));
        assertEquals(0, EmberSigStampNudge.maybeExtra(d));
        assertEquals(2, EmberSigStampDry.get(d));
    }
}
