package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.*;

/** D198 / ARCH S0-1 + S0-2: legacy dungeon gates while the P1 mode is active. */
public class LegacyGateTest {

    @Test public void s01LegacyGateIdsClosedOnlyWhenP1Active() {
        for (String id : new String[] {"daily", "weekly", "abyss", "raid", "elite", "DAILY", "Elite"}) {
            assertTrue(id, LegacyGate.gateClosed(true, id));
            assertFalse(id, LegacyGate.gateClosed(false, id)); // P1 off: legacy level gate decides as before
        }
    }

    @Test public void s01NonLegacyOrUnknownGateIdsUntouched() {
        for (String id : new String[] {"q01", "q07", "calamity", "", "p1_pass_q01"}) {
            assertFalse(id, LegacyGate.gateClosed(true, id));
            assertFalse(id, LegacyGate.gateClosed(false, id));
        }
        assertFalse(LegacyGate.gateClosed(true, null));
    }

    @Test public void s01GuildBossPassClosedOnlyWhenP1Active() {
        assertTrue(LegacyGate.guildBossPassClosed(true));
        assertFalse(LegacyGate.guildBossPassClosed(false));
    }

    @Test public void s02RefuseLegacyEnterOnlyForPlayersWhileP1Active() {
        assertTrue(LegacyGate.refuseLegacyEnter(true, false, false));   // P1 on, legacy kind, player → refused
        assertFalse(LegacyGate.refuseLegacyEnter(true, false, true));   // admin / OP passes
        assertFalse(LegacyGate.refuseLegacyEnter(true, true, false));   // P1 kind (q0x) never refused here
        assertFalse(LegacyGate.refuseLegacyEnter(true, true, true));
        assertFalse(LegacyGate.refuseLegacyEnter(false, false, false)); // P1 off: unchanged
        assertFalse(LegacyGate.refuseLegacyEnter(false, false, true));
        assertFalse(LegacyGate.refuseLegacyEnter(false, true, false));
    }

    @Test public void everyLegacyKindIsCoveredAndNoP1KindIs() {
        int legacy = 0;
        for (TicketEntryService.Kind k : TicketEntryService.Kind.values()) {
            if (k.p1()) {
                assertFalse(k.name() + " P1 kind must not use a legacy gate id", LegacyGate.gateClosed(true, k.gateId));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), false));
            } else {
                legacy++;
                assertTrue(k.name() + " legacy kind's DP gate must close", LegacyGate.gateClosed(true, k.gateId));
                assertTrue(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), false));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), true));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(false, k.p1(), false));
            }
        }
        assertEquals(11, legacy); // 7 daily + weekly + abyss + raid + elite
        assertEquals("ELITE routes through tryEnter", "elite", TicketEntryService.Kind.ELITE.gateId);
    }

    @Test public void refusalMessageIsTheShortChineseLine() {
        assertEquals("P1 模式下旧副本已关闭", LegacyGate.CLOSED_MSG);
    }
}
