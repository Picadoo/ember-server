package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.PlayerData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * D233 / ARCH S3-4: fixed-seed proofs that the raid weekly counter, entry / label / fail text and
 * S12 settle grant shapes match the pre-extract behaviour and the bundled {@code ember-v1-runs.yml} (bv58).
 */
public final class EmberRaidServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberRaidServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    /** a raid def with its own counter (no cap_group) — only the fields the raid text reads */
    private static EmberRunMaps.MapDef ownCounterRaid() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("name", "测试团");
        m.put("tier", 3);
        m.put("requires", "q07");
        m.put("weekly_cap", 2);
        m.put("loot", java.util.Collections.singletonMap("family", "sustain"));
        EmberRunMaps.MapDef d = new EmberRunMaps.MapDef("rx", m);
        d.raid = true;
        return d;
    }

    @Test public void bundledRaidsShareOneWeeklyCounter_unchanged() {
        EmberRunMaps maps = bundled();
        assertEquals(Arrays.asList("r01", "r02", "r03"), new java.util.ArrayList<String>(maps.raids.keySet()));
        for (EmberRunMaps.MapDef r : maps.raids.values()) {
            assertTrue(r.raid);
            assertEquals("q07", r.requires);
            assertEquals(3, r.weeklyCap);
            assertEquals("raid", EmberRaidService.capKey(r));
            assertEquals("p2_raid_raid", EmberRaidService.counterKey(r));
            assertEquals(3, maps.partyMin(r));
            assertEquals(5, maps.partyMax(r));
            assertEquals(50, maps.cost(r));
            assertEquals(3, r.tier);
        }
        assertEquals(1, maps.raidItemQualityFloor);
        assertEquals("burst", maps.raids.get("r01").lootFamily);
        assertEquals("sustain", maps.raids.get("r02").lootFamily);
        assertEquals("scorch", maps.raids.get("r03").lootFamily);
        // the run-service delegate keeps the same answer
        assertEquals("raid", EmberRunService.capKey(maps.raids.get("r02")));
        assertEquals(EmberRaidService.C_RAID, EmberRunService.C_RAID);
    }

    @Test public void applyClearCountOnlyOnNewRaidMarkRow_perCapGroup() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef r01 = maps.raids.get("r01"), r02 = maps.raids.get("r02");
        PlayerData pd = new PlayerData();
        String wk = "2026-W41", wk2 = "2026-W42";
        assertFalse(EmberRaidService.applyClearCount(pd, r01, wk, "raid_mark", false)); // ledger row existed
        assertFalse(EmberRaidService.applyClearCount(pd, r01, wk, "raid_item", true));  // other raid row
        assertFalse(EmberRaidService.applyClearCount(pd, r01, wk, "rot_mark", true));
        assertEquals(0, EmberRaidService.weekCount(pd, r01, wk));
        assertTrue(EmberRaidService.applyClearCount(pd, r01, wk, "raid_mark", true));
        assertTrue(EmberRaidService.applyClearCount(pd, r02, wk, "raid_mark", true));
        assertEquals(2, EmberRaidService.weekCount(pd, r01, wk)); // r01 + r02 share cap_group raid
        assertEquals(2, EmberRaidService.weekCount(pd, r02, wk));
        assertEquals(2, pd.periodCount("p2_raid_raid", wk));
        assertEquals(0, EmberRaidService.weekCount(pd, r01, wk2)); // next week starts at 0

        EmberRunMaps.MapDef own = ownCounterRaid();
        assertEquals("rx", EmberRaidService.capKey(own));
        assertTrue(EmberRaidService.applyClearCount(pd, own, wk, "raid_mark", true));
        assertEquals(1, pd.periodCount("p2_raid_rx", wk));
        assertEquals(2, EmberRaidService.weekCount(pd, r01, wk)); // own counter does not touch the group
    }

    @Test public void entryProblemText_capAndUnlock_unchanged() {
        EmberRunMaps.MapDef r01 = bundled().raids.get("r01");
        assertEquals("A 未开放团本（需本人首通 Q07）", EmberRaidService.entryProblemText("A", r01, false, 0));
        assertNull(EmberRaidService.entryProblemText("A", r01, true, 0));
        assertNull(EmberRaidService.entryProblemText("A", r01, true, 2));
        assertEquals("A 本周团本次数已满（3/3，周一 0 点重置）", EmberRaidService.entryProblemText("A", r01, true, 3));
        assertTrue(EmberRaidService.capReached(r01, 4));
        assertFalse(EmberRaidService.capReached(r01, 2));
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("requires", "q07");
        EmberRunMaps.MapDef uncapped = new EmberRunMaps.MapDef("ry", m); // weekly_cap 0 = no limit
        assertFalse(EmberRaidService.capReached(uncapped, 99));
        assertNull(EmberRaidService.entryProblemText("A", uncapped, true, 99));
    }

    @Test public void labelAndFailText_unchanged() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef r01 = maps.raids.get("r01");
        assertEquals("需本人首通 Q07", EmberRaidService.labelText(r01, false, 0, 3, 5, 50));
        assertEquals("本周 1/3（团本合计） · 3～5 人 · 50 体力", EmberRaidService.labelText(r01, true, 1, maps.partyMin(r01), maps.partyMax(r01), maps.cost(r01)));
        EmberRunMaps.MapDef own = ownCounterRaid();
        assertEquals("本周 0/2 · 3～5 人 · 50 体力", EmberRaidService.labelText(own, true, 0, 3, 5, 50));

        assertEquals("§a本周团本次数没有扣§7：还是 1/3（团本合计），体力够就可以再来（只有通关才算一次）", EmberRaidService.failNoBurnText(r01, 1));
        assertEquals("§a本周团本次数没有扣§7：还是 3/3（团本合计）", EmberRaidService.failNoBurnText(r01, 3));
        assertEquals("§a本周团本次数没有扣§7：还是 0/2，体力够就可以再来（只有通关才算一次）", EmberRaidService.failNoBurnText(own, 0));

        String st = EmberRaidService.startText(4, 3.55, 1.6);
        assertTrue(st.startsWith("§6团本开始 §7· 4 人 · 掉落 T3 · 敌方生命 ×3.55 伤害 ×1.60 · "));
        assertTrue(st.endsWith("首领死后统一结算"));
    }

    @Test public void settleGrantsAreS12Shape_matchRaidItemFormula() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef r01 = maps.raids.get("r01");
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "r01-abc-def";
        in.player = "00000000-0000-0000-0000-000000000001";
        in.seed = 123456789L;
        in.tier = 3;
        in.bossKilled = true;
        in.qualityWeights = maps.challenge.quality;

        List<EmberRunRules.Grant> g = EmberRaidService.settleGrants(in, r01, maps.raidItemQualityFloor, 3);
        assertEquals(2, g.size());
        EmberRunRules.Grant item = g.get(0), mark = g.get(1);
        assertEquals("raid_item", item.key);
        assertEquals(EmberRunRules.Kind.ITEM, item.kind);
        assertEquals(1, item.amount);
        assertNotNull(item.item);
        assertEquals("burst", item.item.family); // no target → raid loot family
        assertEquals(3, item.item.tier);
        assertTrue(item.item.quality >= maps.raidItemQualityFloor);
        // identical to the pre-extract call (same seed → same roll)
        EmberRunRules.Grant ref = EmberRunRules.raidItem(in, "raid_item", r01.lootFamily, maps.raidItemQualityFloor);
        assertEquals(ref.encode(), item.encode());

        assertEquals("raid_mark", mark.key);
        assertEquals(EmberRunRules.Kind.MARK, mark.kind);
        assertEquals("3", mark.id);
        assertEquals(1, mark.amount);
        assertEquals("S12", EmberEconomy.sourceForGrantKey("raid_mark"));

        in.target = "scorch"; // a target family wins over the raid family
        assertEquals("scorch", EmberRaidService.settleGrants(in, r01, maps.raidItemQualityFloor, 3).get(0).item.family);
    }

    @Test public void counterKeyOwnedByRaidService() {
        EmberCounters.Family f = EmberCounters.byKey("p2_raid_");
        assertNotNull(f);
        assertEquals("EmberRaidService", f.system);
        assertTrue(f.prefix);
        assertTrue(f.matches("p2_raid_raid"));
        assertTrue(EmberCounters.clockGuarded(EmberCounters.lookup("p2_raid_raid")));
    }

    @Test public void enterReveal_matchesMenuCard() {
        assertEquals("冲撞撞墙", EmberRaidService.cardTag("r01"));
        assertEquals("半血砸地", EmberRaidService.cardTag("r02"));
        assertEquals("烬核分摊", EmberRaidService.cardTag("r03"));
        assertEquals("?", EmberRaidService.cardTag("r99"));
        assertEquals("§6本局：§f锈轨矿道·团 §7· 名片：§d冲撞撞墙",
                EmberRaidService.enterRevealText("锈轨矿道·团", "r01"));
        assertEquals("§6本局：§f霜封哨所·团 §7· 名片：§d半血砸地",
                EmberRaidService.enterRevealText("霜封哨所·团", "r02"));
        assertEquals("§6本局：§f断塔回廊·团 §7· 名片：§d烬核分摊",
                EmberRaidService.enterRevealText("断塔回廊·团", "r03"));
        assertEquals("§6本局：§f? §7· 名片：§d?", EmberRaidService.enterRevealText(null, null));
    }

    @Test public void softSegmentCues_matchD195Anchors() {
        assertEquals("§b本间：§f装卸场 §a已清 §7· 三房推进", EmberRaidService.roomClearCue("装卸场"));
        assertTrue(EmberRaidService.bossCue("r01").contains("冲撞撞墙破绽"));
        assertTrue(EmberRaidService.bossCue("r02").contains("半血砸地破绽"));
        assertTrue(EmberRaidService.bossCue("r03").contains("烬核分摊"));
        assertEquals("§e半血增援 §7· 四角加怪", EmberRaidService.halfHpCue("r01"));
        assertEquals("§e半血转阶段 §7· 砸地接横扫", EmberRaidService.halfHpCue("r02"));
        assertNull(EmberRaidService.halfHpCue("r03"));
    }

}
