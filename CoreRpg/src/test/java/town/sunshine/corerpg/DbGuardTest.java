package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.*;

/** Pure engage decision for 1.62 MySQL-down join guard. */
public class DbGuardTest {

    @Test public void shouldEngageOnlyWhenMysqlConfiguredDownGuardOnAndP1() {
        assertTrue(DbGuard.shouldEngage(true, false, true, true));
        assertFalse(DbGuard.shouldEngage(true, true, true, true));   // connected
        assertFalse(DbGuard.shouldEngage(false, false, true, true)); // yaml storage
        assertFalse(DbGuard.shouldEngage(true, false, false, true)); // guard off
        assertFalse(DbGuard.shouldEngage(true, false, true, false)); // P1 off
        assertFalse(DbGuard.shouldEngage(false, false, false, false));
    }
}
