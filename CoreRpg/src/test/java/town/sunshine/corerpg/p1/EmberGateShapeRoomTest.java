package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2609+ Gate + Shape×Room. */
public class EmberGateShapeRoomTest {

    @Test
    public void gateEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markGateEnter(d));
        assertFalse(EmberSkillKit.markGateEnter(d));
        assertTrue(EmberSkillKit.gatePathUnlocked(d, null));
    }

    @Test
    public void gateAskCooldown() {
        assertEquals(5000L, EmberGatePath.askCooldownMs(EmberGatePath.ASK));
        assertEquals(8000L, EmberGatePath.askCooldownMs(EmberGatePath.NONE));
    }

    @Test
    public void roomShapeWidensLine() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 6.0, 0, 3, true, false);
        EmberSkillKit.Shape room = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 6.0, 0, 3, true, true);
        assertTrue(room.range > base.range);
    }

    @Test
    public void roomShapeWidensFanArc() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, true, false);
        EmberSkillKit.Shape room = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, true, true);
        assertTrue(room.arc > base.arc);
    }

    @Test
    public void dashRadiusRoomBonus() {
        double sync = EmberSkillKit.dashHitRadius(true, false);
        double room = EmberSkillKit.dashHitRadius(true, true);
        assertEquals(sync + 0.15, room, 1e-9);
        assertEquals(EmberSkillKit.DASH_HIT_RADIUS, EmberSkillKit.dashHitRadius(false, true), 1e-9);
    }

    @Test
    public void slashRoomBonusAndGateAutoResist() {
        assertEquals(20, EmberSkillKit.slashLandSlowTicks(false, false, false));
        assertEquals(24, EmberSkillKit.slashLandSlowTicks(true, false, false));
        assertEquals(26, EmberSkillKit.slashLandSlowTicks(true, false, true));
        assertEquals(40, EmberGatePath.gateResistTicks(EmberGatePath.AUTO));
        assertEquals(20, EmberGatePath.gateResistTicks(EmberGatePath.ASK));
    }
}
