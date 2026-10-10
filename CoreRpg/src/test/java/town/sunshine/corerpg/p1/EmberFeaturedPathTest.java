package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFeaturedPathTest {
    @Test public void parse_identity_enter() {
        assertEquals(EmberFeaturedPath.CHALLENGE, EmberFeaturedPath.parse("挑战"));
        assertEquals(EmberFeaturedPath.NORMAL, EmberFeaturedPath.parse("normal"));
        assertEquals(EmberFeaturedPath.NONE, EmberFeaturedPath.parse("clear"));
        assertTrue(EmberFeaturedPath.identityLine("Q01 锈轨", "学预警", "烬爆 · 刃").contains("名片"));
        assertTrue(EmberFeaturedPath.enterCmd(EmberFeaturedPath.CHALLENGE, "q02").contains("challenge"));
        assertTrue(EmberFeaturedPath.enterCmd(EmberFeaturedPath.NORMAL, "q02").endsWith("q02"));
        assertTrue(EmberFeaturedPath.tip(EmberFeaturedPath.NORMAL).contains("名片"));
    }
}
