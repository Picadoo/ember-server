package town.sunshine.corerpg.p1;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** D177 rev 2 P1 挂机庭 auto-combat: per-kill accumulators, caps, offline conversion, shipped config vs design / p1sim. */
public class EmberAfkServiceTest {

    @Test public void accumulatorsPayWholeUnitsOnly() {
        assertArrayEquals(new int[]{0, 0}, EmberAfkService.settleUnits(0, 600));
        assertArrayEquals(new int[]{0, 599}, EmberAfkService.settleUnits(599, 600));
        assertArrayEquals(new int[]{1, 0}, EmberAfkService.settleUnits(600, 600));
        assertArrayEquals(new int[]{2, 150}, EmberAfkService.settleUnits(1350, 600));
        assertArrayEquals(new int[]{0, 0}, EmberAfkService.settleUnits(-5, 600));
        assertArrayEquals(new int[]{0, 7}, EmberAfkService.settleUnits(7, 0));
        // a full day at T4 (120 coin / 2400 kills), settled every 100 kills: exactly 120 coin, nothing left over
        int acc = 0, paid = 0;
        for (int k = 0; k < 2400; k++) {
            acc += 120;
            if (k % 100 == 99) { int[] s = EmberAfkService.settleUnits(acc, 2400); paid += s[0]; acc = s[1]; }
        }
        assertEquals(120, paid);
        assertEquals(0, acc);
    }

    @Test public void capsAndOfflineSubCap() {
        assertEquals(1, EmberAfkService.grantable(1, 0, 600, false, 0, 300));
        assertEquals(0, EmberAfkService.grantable(1, 600, 600, false, 0, 300));
        assertEquals(300, EmberAfkService.grantable(900, 0, 600, true, 0, 300));
        assertEquals(100, EmberAfkService.grantable(900, 0, 600, true, 200, 300));
        assertEquals(50, EmberAfkService.grantable(900, 550, 600, true, 0, 300));
        assertEquals(0, EmberAfkService.grantable(-3, 0, 600, false, 0, 300));
    }

    @Test public void offlineFollowsTheMeasuredRate() {
        assertEquals(0, EmberAfkService.offlineKills(480, 0, 0.25, 12));      // never fought online → nothing
        assertEquals(1200, EmberAfkService.offlineKills(480, 1000, 0.25, 12)); // 10 kills/min × 480 min × 25 %
        assertEquals(600, EmberAfkService.offlineKills(480, 500, 0.25, 12));
        assertEquals(1800, EmberAfkService.offlineKills(48 * 60, 1000, 0.25, 12)); // window capped at 12 h
        assertEquals(0, EmberAfkService.offlineKills(-5, 1000, 0.25, 12));     // clock went backwards
        // EMA: first minute takes the measurement, later minutes move 20 %
        assertEquals(800, EmberAfkService.ema(0, 0, 8));
        assertEquals(840, EmberAfkService.ema(800, 10, 10));
    }

    private static ConfigurationSection afk() {
        YamlConfiguration y = YamlConfiguration.loadConfiguration(new InputStreamReader(
                EmberAfkServiceTest.class.getResourceAsStream("/ember-v1.yml"), StandardCharsets.UTF_8));
        ConfigurationSection s = y.getConfigurationSection("afk");
        assertNotNull("ember-v1.yml afk:", s);
        assertFalse("legacy auction stays closed in P1 by default", y.getBoolean("legacy_auction", true));
        assertTrue("the AFK world is in P1 scope (real P1 stats in auto-combat)", y.getStringList("scope.worlds").contains("ember_afk"));
        return s;
    }

    /** = tools/p1sim/afk.py TIERS / DAILY_KILLS / PACK / RESPAWN and DESIGN-ember-afk-p1 §3 (rev 2). */
    @Test public void shippedConfigMatchesDesign() {
        ConfigurationSection s = afk();
        assertFalse(s.getBoolean("legacy_payouts", true));
        assertEquals(2400, s.getInt("daily_kills"));
        assertEquals(3, s.getInt("combat.pack"));
        assertEquals(4.0, s.getDouble("combat.respawn_seconds"), 1e-9);
        assertEquals(0.08, s.getDouble("combat.regen_pct"), 1e-9);
        assertEquals(3, s.getInt("combat.death_stop"));
        assertEquals(0.25, s.getDouble("offline.ratio"), 1e-9);
        assertEquals(1200, s.getInt("offline.max_kills"));
        String[] req = {"q01", "q03", "q05", "q07"};
        double[][] mob = {{160, 1.5}, {280, 3.5}, {480, 5.5}, {720, 9}};
        int[][] daily = {{60, 10, 2, 2, 0, 0}, {80, 15, 3, 3, 1, 0}, {100, 20, 3, 4, 1, 1}, {120, 25, 4, 5, 2, 1}};
        List<Map<?, ?>> ts = s.getMapList("tiers");
        assertEquals(4, ts.size());
        for (int i = 0; i < 4; i++) {
            Map<?, ?> t = ts.get(i);
            assertEquals(req[i], String.valueOf(t.get("requires")));
            assertEquals("tier " + (i + 1) + " hp", mob[i][0], Double.parseDouble(String.valueOf(t.get("hp"))), 1e-9);
            assertEquals("tier " + (i + 1) + " atk", mob[i][1], Double.parseDouble(String.valueOf(t.get("atk"))), 1e-9);
            for (int r = 0; r < EmberAfkService.RES.length; r++)
                assertEquals("tier " + (i + 1) + " " + EmberAfkService.RES[r] + "/day", daily[i][r],
                        Integer.parseInt(String.valueOf(t.get(EmberAfkService.RES[r]))));
            assertTrue("a full AFK day never pays more coin than one clear's base", daily[i][0] <= EmberRunRules.BASE_COIN);
            assertTrue("a full AFK day never pays more xp than one clear's base", daily[i][1] <= EmberRunRules.BASE_XP);
        }
    }

    @Test
    public void boundLootNeverLeavesTheAccount() {
        // AFK material rows are bmat: (vault-bound counter), round-trip through the ledger text
        EmberRunRules.Grant g = EmberRunRules.Grant.decode("p1afk-2026-10-04/s100", "bmat:" + EmberUpgradeRules.MAT_SHARD + ":2");
        assertEquals(EmberRunRules.Kind.BMAT, g.kind);
        assertEquals(EmberUpgradeRules.MAT_SHARD, g.id);
        assertEquals(2, g.amount);
        assertEquals("bmat:" + EmberUpgradeRules.MAT_SHARD + ":2", g.encode());
        // spending consumes the bound part first, and bound never exceeds what is left
        assertEquals(3, EmberVault.boundAfterSpend(5, 2, 10));
        assertEquals(0, EmberVault.boundAfterSpend(5, 7, 10));
        assertEquals(4, EmberVault.boundAfterSpend(5, 0, 4));   // an admin take / undo debit clamps
        assertEquals(0, EmberVault.boundAfterSpend(0, 3, 8));
        // the player's own inventory / crafting grid / ender chest / plugin GUIs are not "containers"
        assertFalse(EmberAfkService.blockContainer(null, org.bukkit.event.inventory.InventoryType.CRAFTING));
        assertFalse(EmberAfkService.blockContainer(null, org.bukkit.event.inventory.InventoryType.ENDER_CHEST));
        assertFalse(EmberAfkService.blockContainer(null, org.bukkit.event.inventory.InventoryType.CHEST));
    }

    /** D275 / ARCH O9: bind guards follow P1 master switch; with no EmberMode instance they stay off. */
    @Test public void bindGuardsFollowP1MasterSwitchNotAfkFlag() {
        assertFalse(EmberAfkService.bindGuardsActive());
        assertEquals(EmberMode.active(), EmberAfkService.bindGuardsActive());
    }
}
