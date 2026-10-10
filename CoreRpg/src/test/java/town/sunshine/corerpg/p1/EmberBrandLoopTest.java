package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBrandLoopTest {
    @Test public void craft_follow_hint() {
        assertTrue(EmberBrandLoop.craftFollowHint(EmberBrandPath.HUNT).contains("猎缀"));
        assertTrue(EmberBrandLoop.craftFollowHint(EmberBrandPath.NONE).contains("余烬"));
    }
}
