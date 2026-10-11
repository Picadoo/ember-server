package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Collections;
public class EmberKillStreakSpeedTest {
    @Test public void countsThenLatches() {
        EmberKillStreakSpeed.clearAll();
        for (int i = 0; i < EmberKillStreakSpeed.NEED - 1; i++)
            EmberKillStreakSpeed.noteKill("r1", Collections.<org.bukkit.entity.Player>emptyList());
        EmberKillStreakSpeed.noteKill("r1", Collections.<org.bukkit.entity.Player>emptyList());
        EmberKillStreakSpeed.noteKill("r1", Collections.<org.bukkit.entity.Player>emptyList()); // no-op after latch
        EmberKillStreakSpeed.clear("r1");
    }
}
