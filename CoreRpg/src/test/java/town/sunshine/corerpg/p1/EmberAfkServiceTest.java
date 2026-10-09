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
    }

    @Test public void capStopText_D285_pointsToAdventure() {
        String s = EmberAfkService.capStopText(2400);
        assertTrue(s.contains("2400/2400"));
        assertTrue(s.contains("去冒险"));
        assertTrue(EmberAfkService.capActionBar(2400).contains("体力还在"));
        assertTrue(EmberAfkService.capStatusWord().contains("去冒险"));
    }

    @Test public void softIdentity_D305_cardsMatchDesignAndTiers() {
        assertEquals("入门稳挂", EmberAfkService.cardTag(1));
        assertEquals("核心入门", EmberAfkService.cardTag(2));
        assertEquals("胚料起步", EmberAfkService.cardTag(3));
        assertEquals("满表材料", EmberAfkService.cardTag(4));
        assertTrue(EmberAfkService.farmLine(1).contains("无核心"));
        assertTrue(EmberAfkService.farmLine(2).contains("核心"));
        assertTrue(EmberAfkService.farmLine(3).contains("胚料"));
        assertTrue(EmberAfkService.farmLine(4).contains("最高档"));
        String r1 = EmberAfkService.enterRevealText("灰坡", 1);
        assertTrue(r1.contains("名片"));
        assertTrue(r1.contains("入门稳挂"));
        assertTrue(r1.contains("养"));
        String r4 = EmberAfkService.enterRevealText("烬原深处", 4);
        assertTrue(r4.contains("满表材料"));
        // static vs shipped tiers: T1 no core/blank, T2 core, T3+ blank
        ConfigurationSection s = afk();
        java.util.List<java.util.Map<?, ?>> ts = s.getMapList("tiers");
        assertEquals(0, Integer.parseInt(String.valueOf(ts.get(0).get("core"))));
        assertEquals(0, Integer.parseInt(String.valueOf(ts.get(0).get("blank"))));
        assertEquals(1, Integer.parseInt(String.valueOf(ts.get(1).get("core"))));
        assertEquals(1, Integer.parseInt(String.valueOf(ts.get(2).get("blank"))));
        assertEquals(2, Integer.parseInt(String.valueOf(ts.get(3).get("core"))));
        assertTrue(EmberAfkService.upgradeHintText().contains("更高层"));
        assertTrue(EmberAfkService.shouldUpgradeHint(true, false, false, true, true, 40, 40, 800, 800));
        assertFalse("capped never hints", EmberAfkService.shouldUpgradeHint(true, false, true, true, true, 40, 40, 800, 800));
        assertFalse("hot-off", EmberAfkService.shouldUpgradeHint(false, false, false, true, true, 40, 40, 800, 800));
        assertFalse("no next unlock", EmberAfkService.shouldUpgradeHint(true, false, false, true, false, 40, 40, 800, 800));
        assertFalse("deaths block", EmberAfkService.shouldUpgradeHint(true, false, false, false, true, 40, 40, 800, 800));
        assertFalse("already hinted", EmberAfkService.shouldUpgradeHint(true, true, false, true, true, 40, 40, 800, 800));
        assertFalse("kph gate", EmberAfkService.shouldUpgradeHint(true, false, false, true, true, 40, 40, 799, 800));
        assertEquals(2400, s.getInt("daily_kills"));
        assertEquals(0.25, s.getDouble("offline.ratio"), 1e-9);
        assertTrue(s.getBoolean("feel.upgrade_hint", false));
        assertEquals(3, s.getInt("combat.death_stop"));
        assertTrue(EmberAfkService.capActionBar(2400).contains("去冒险"));
    }


    /** D405 remain / eta_min / remain_line — STATUS-nailed: remain/eta full=numeric 0; no-kph eta=「开打后估时」. */
    @Test public void remainEta_D405_readOnlyDerivatives() {
        assertEquals(2400, EmberAfkService.remainKills(0, 2400));
        assertEquals(400, EmberAfkService.remainKills(2000, 2400));
        assertEquals(0, EmberAfkService.remainKills(2400, 2400));
        assertEquals(0, EmberAfkService.remainKills(2500, 2400)); // over-cap clamps
        assertEquals(2400, EmberAfkService.remainKills(-1, 2400)); // negative kills clamped → treat as 0 kills
        assertEquals("0", EmberAfkService.etaMinText(0, 600));    // full
        assertEquals("开打后估时", EmberAfkService.etaMinText(400, 0));
        assertEquals("开打后估时", EmberAfkService.etaMinText(400, -1));
        // remain=600, kph=600 → ceil(600/600*60)=60
        assertEquals("60", EmberAfkService.etaMinText(600, 600));
        // remain=1, kph=600 → ceil(1/600*60)=ceil(0.1)=1 (at least 1)
        assertEquals("1", EmberAfkService.etaMinText(1, 600));
        // remain=100, kph=30 → ceil(100/30*60)=ceil(200)=200
        assertEquals("200", EmberAfkService.etaMinText(100, 30));
        assertEquals("今日已满 · 去冒险 / 去哪花", EmberAfkService.remainLine(0, 600));
        assertEquals("还差 400 只 · 开打后估时", EmberAfkService.remainLine(400, 0));
        assertEquals("还差 600 只 · 约 60 分钟满", EmberAfkService.remainLine(600, 600));
        // shipped daily_kills still 2400 (zero改日顶)
        assertEquals(2400, afk().getInt("daily_kills"));
    }

}
