package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.junit.Test;

import static org.junit.Assert.*;

/** D219: pure helpers for 后撤方向 / 严格落点 hazard / body half-width (no live world). */
public class EmberDashTest {

    @Test
    public void horizontalLookUsesYawWhenPitchStraightDown() {
        Location from = new Location(null, 0, 64, 0, 90f, 90f); // look straight down
        Vector v = EmberDash.horizontalLook(from);
        assertEquals(0.0, v.getY(), 1e-9);
        assertEquals(1.0, v.length(), 1e-6);
        // yaw 90 → -X in Bukkit
        assertEquals(-1.0, v.getX(), 1e-6);
        assertEquals(0.0, v.getZ(), 1e-6);
    }

    @Test
    public void horizontalLookForwardNegatesForBackstep() {
        Location from = new Location(null, 0, 64, 0, 0f, 0f); // +Z
        Vector fwd = EmberDash.horizontalLook(from);
        Vector back = fwd.clone().multiply(-1);
        assertEquals(0.0, fwd.getX(), 1e-6);
        assertEquals(1.0, fwd.getZ(), 1e-6);
        assertEquals(0.0, back.getX(), 1e-6);
        assertEquals(-1.0, back.getZ(), 1e-6);
    }

    @Test
    public void hazardMaterialsMatchStrictGroundB1() {
        assertTrue(EmberDash.isHazardMaterial(Material.LAVA));
        assertTrue(EmberDash.isHazardMaterial(Material.STATIONARY_LAVA));
        assertTrue(EmberDash.isHazardMaterial(Material.FIRE));
        assertTrue(EmberDash.isHazardMaterial(Material.CACTUS));
        assertTrue(EmberDash.isHazardMaterial(Material.WEB));
        assertTrue(EmberDash.isHazardMaterial(Material.MAGMA));
        assertFalse(EmberDash.isHazardMaterial(Material.AIR));
        assertFalse(EmberDash.isHazardMaterial(Material.STONE));
        assertFalse(EmberDash.isHazardMaterial(Material.WATER));
    }

    @Test
    public void bodyHalfMatchesDesignFootprint() {
        assertEquals(0.3, EmberDash.BODY_HALF, 1e-9);
    }
}
