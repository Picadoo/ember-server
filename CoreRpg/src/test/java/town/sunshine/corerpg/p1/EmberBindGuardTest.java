package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** D276 / ARCH O9: bind guards live outside AFK and follow P1 only. */
public class EmberBindGuardTest {

    @Test public void bindGuardsFollowP1MasterSwitchNotAfkFlag() {
        assertFalse(EmberBindGuard.bindGuardsActive());
        assertEquals(EmberMode.active(), EmberBindGuard.bindGuardsActive());
    }

    @Test public void playerInvAndEnderChestAreNotBlockedContainers() {
        assertFalse(EmberBindGuard.blockContainer(null, org.bukkit.event.inventory.InventoryType.CRAFTING));
        assertFalse(EmberBindGuard.blockContainer(null, org.bukkit.event.inventory.InventoryType.ENDER_CHEST));
        assertFalse(EmberBindGuard.blockContainer(null, org.bukkit.event.inventory.InventoryType.PLAYER));
        assertFalse(EmberBindGuard.blockContainer(null, org.bukkit.event.inventory.InventoryType.WORKBENCH));
        // null holder + CHEST is not BlockState/Entity → also not blocked by shape alone
        assertFalse(EmberBindGuard.blockContainer(null, org.bukkit.event.inventory.InventoryType.CHEST));
    }

    @Test public void boundMatIdsMatchUpgradeMaterials() {
        assertTrue(EmberBindGuard.BOUND_MATS.contains(EmberUpgradeRules.MAT_SHARD));
        assertTrue(EmberBindGuard.BOUND_MATS.contains(EmberUpgradeRules.MAT_CORE));
        assertTrue(EmberBindGuard.BOUND_MATS.contains(EmberUpgradeRules.MAT_BONE));
        assertTrue(EmberBindGuard.BOUND_MATS.contains(EmberUpgradeRules.MAT_BLANK));
        assertEquals(4, EmberBindGuard.BOUND_MATS.size());
    }
}
