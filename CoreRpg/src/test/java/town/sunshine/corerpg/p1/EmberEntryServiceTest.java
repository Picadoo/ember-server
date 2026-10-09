package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * D235 / ARCH S3-6: the entry gates moved from {@code EmberRunService.enter} into {@link EmberEntryService}
 * (plus the abyss / rush per-member gates folded into their services). Pins gate wording against the
 * pre-extract string concatenations, the party-size / stamina predicates against the bundled
 * {@code ember-v1-runs.yml} (bv58), and the P2-8 / D94 weekly-rule selection.
 */
public final class EmberEntryServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberEntryServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void partySizeGate_bundledBounds_unchanged() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        EmberRunMaps.MapDef r01 = maps.byKey("r01");
        assertEquals(1, maps.partyMin(q01));
        assertEquals(3, maps.partyMax(q01));
        assertEquals(30, maps.cost(q01));
        assertEquals(3, maps.partyMin(r01));
        assertEquals(5, maps.partyMax(r01));
        assertEquals(50, maps.cost(r01));
        for (int n = 1; n <= 3; n++) assertNull(EmberEntryService.partySizeProblem(n, 1, 3));
        assertEquals("人数 1～3，当前 0", EmberEntryService.partySizeProblem(0, 1, 3));
        assertEquals("人数 1～3，当前 4", EmberEntryService.partySizeProblem(4, 1, 3));
        assertEquals("人数 3～5，当前 2", EmberEntryService.partySizeProblem(2, maps.partyMin(r01), maps.partyMax(r01)));
        assertNull(EmberEntryService.partySizeProblem(5, maps.partyMin(r01), maps.partyMax(r01)));
    }

    @Test public void staminaAndPresenceGates_wordingMatchesPreExtract() {
        String n = "FreshQ813";
        int cost = 30;
        assertNull(EmberEntryService.staminaProblem(n, 30, cost));
        assertNull(EmberEntryService.staminaProblem(n, 200, cost));
        assertEquals(n + " 体力不足（需 " + cost + "，当前 " + 29 + "）", EmberEntryService.staminaProblem(n, 29, cost));
        assertEquals("FreshQ813 体力不足（需 50，当前 0）", EmberEntryService.staminaProblem(n, 0, 50));
        assertEquals("队员不在线：" + n, EmberEntryService.offlineText(n));
        assertEquals(n + " 已在另一局主线本中", EmberEntryService.busyText(n));
        assertEquals(n + " 仍在副本内", EmberEntryService.inDungeonText(n));
        assertTrue(EmberEntryService.inDungeonWorld("dungeon_EmberQ01_abc"));
        assertFalse(EmberEntryService.inDungeonWorld("world"));
        assertFalse(EmberEntryService.inDungeonWorld("ember_afk"));
        assertEquals("未知主线本 q99", EmberEntryService.unknownMapText("q99"));
        assertEquals("新模式（ember-v1.0-P1）尚未开启，主线 Q 本暂不可进入。", EmberEntryService.MSG_MODE_OFF);
        assertEquals("组队时由队长开本。", EmberEntryService.MSG_NOT_LEADER);
        assertEquals("体力服务未就绪", EmberEntryService.MSG_NO_STAMINA_SVC);
    }

    @Test public void modeVariantAndUnlockGates_wordingAndOrder() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef rush = maps.byKey("rush");
        assertNotNull(rush);
        assertTrue(EmberEntryService.variantProblems(false, false, false, false, "", true, 0).isEmpty());
        assertTrue(EmberEntryService.variantProblems(true, false, false, false, "", false, 0).isEmpty());
        assertEquals(Collections.singletonList("团本没有挑战 / 深渊版本"), EmberEntryService.variantProblems(true, false, false, false, "", true, 0));
        assertEquals(Collections.singletonList("活动本没有挑战 / 深渊版本"), EmberEntryService.variantProblems(false, true, false, false, "", false, 2));
        assertEquals(Collections.singletonList(rush.rushLabel + "没有挑战 / 深渊版本"),
                EmberEntryService.variantProblems(rush.raid, rush.event, rush.rush, false, rush.rushLabel, true, 0));
        assertEquals(Arrays.asList("团本没有挑战 / 深渊版本", "活动本没有挑战 / 深渊版本", "X没有挑战 / 深渊版本"),
                EmberEntryService.variantProblems(true, true, true, false, "X", true, 1));
        assertEquals(Collections.singletonList("短征没有挑战 / 深渊版本"),
                EmberEntryService.variantProblems(false, false, false, true, "", true, 0));
        assertEquals("A 未开放挑战版（需本人首通 Q07）", EmberEntryService.challengeLockedText("A", maps.challenge.requires));
        EmberRunMaps.MapDef q02 = maps.byKey("q02");
        EmberRunMaps.MapDef req = maps.byKey(q02.requires);
        assertEquals("A 未解锁（需先首通 Q01 " + req.name + "）", EmberEntryService.lockedText("A", q02.requires, req.key, req.name));
        assertEquals("A 未解锁（需先首通 zz9）", EmberEntryService.lockedText("A", "zz9", null, null));
        // per-mode gates folded into their services (abyss / rush)
        assertEquals("A 未开放深渊（需本人首通 Q07）", EmberAbyssService.lockedText("A", maps.abyssRequires));
        assertEquals("A 深渊最高只能开第 2 层（先完整通关第 1 层）", EmberAbyssService.tierTooHighText("A", 2, 1));
        assertEquals("A 余烬币不足（这一层 300，当前 10），多出来的 T3 印记也不够抵（1 枚抵 200 币，留 8 枚）",
                EmberAbyssService.feeShortText("A", 300, 10, 200, 8));
        assertEquals("A 余烬币不足（这一层 300，当前 10）", EmberAbyssService.feeShortText("A", 300, 10, 0, 8));
        assertNull(EmberRushService.entryProblemText("A", rush, true, null));
        assertEquals("A 未开放" + rush.rushLabel + "（需本人首通 Q07）", EmberRushService.entryProblemText("A", rush, false, null));
        assertEquals("A 未开放" + rush.rushLabel + "（需本人首通 Q07）", EmberRushService.entryProblemText("A", rush, false, "q05"));
        assertEquals("A 还没首通 Q06（" + rush.rushLabel + "只打已首通的图的首领）", EmberRushService.entryProblemText("A", rush, true, "q06"));
    }

    @Test public void plainMainRunPredicate_bundledKinds() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        assertTrue(EmberEntryService.plainMainRun(q01, false, 0));
        assertFalse(EmberEntryService.plainMainRun(q01, true, 0));
        assertFalse(EmberEntryService.plainMainRun(q01, true, 3));
        assertFalse(EmberEntryService.plainMainRun(maps.byKey("r01"), false, 0));
        assertFalse(EmberEntryService.plainMainRun(maps.byKey("rush"), false, 0));
        for (EmberRunMaps.MapDef m : maps.maps.values()) assertTrue(m.key, EmberEntryService.plainMainRun(m, false, 0));
    }

    @Test public void weeklyRuleSelection_challengeAndNormal() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.Modifier lean = maps.modifier("lean");       // normal: true
        EmberRunMaps.Modifier casters = maps.modifier("casters"); // challenge-only
        assertTrue(lean.normal);
        assertFalse(casters.normal);
        // P2-8 challenge: featured map gets the week's rule (any), others none
        assertSame(casters, EmberEntryService.challengeRule(true, casters));
        assertSame(lean, EmberEntryService.challengeRule(true, lean));
        assertNull(EmberEntryService.challengeRule(false, casters));
        assertNull(EmberEntryService.challengeRule(true, null));
        // D94 normal: featured + normal:true + everyone first-cleared
        assertSame(lean, EmberEntryService.normalRule(true, lean, true));
        assertNull(EmberEntryService.normalRule(true, lean, false));   // first clears stay canonical
        assertNull(EmberEntryService.normalRule(true, casters, true)); // 术者换防 challenge-only
        assertNull(EmberEntryService.normalRule(false, lean, true));
        assertNull(EmberEntryService.normalRule(true, null, true));
        // every bundled weekly rule over one 98-week cycle: normal pick only ever returns normal:true rules
        java.time.LocalDate d = java.time.LocalDate.of(2026, 10, 5);
        for (int w = 0; w < 98; w++, d = d.plusWeeks(1)) {
            EmberRunMaps.Modifier wk = maps.modifierFor(d);
            assertNotNull(wk);
            EmberRunMaps.Modifier pick = EmberEntryService.normalRule(true, wk, true);
            assertTrue(pick == null || pick.normal);
            assertEquals(wk.normal, pick != null);
        }
    }

    @Test public void readinessText_matchesPreExtract() {
        assertEquals("§eQ03 首通推荐：T1 刃 + T1 护符，两件都来自 Q01（护符 = Q01 首通自选，刃 = Q01 掉落）。", EmberEntryService.t1HeadText("q03"));
        assertTrue(EmberEntryService.t1HeadText("q02").endsWith("Q02 首通送一次免费定向兑换（自选族和部位的 T1 件），用来补齐同族的那一件。"));
        assertEquals("§7这样首通 Q02 几乎打不过，倒下不退体力。", EmberEntryService.t1TailText("q02"));
        assertEquals("B 主手还没有 T1 刃：回 Q01 多打几局（Q01 偏向掉刃），拿到会自动放到快捷栏第 1 格", EmberEntryService.t1BladeWarn("B"));
        assertEquals("B 还没有生效的 T1 护符（生命只有一半）：先领 Q01 首通自选的护符", EmberEntryService.t1CharmWarn("B"));
        List<String> low = Arrays.asList("A", "B");
        assertEquals("§e挑战版按 T3 装备来调；A、B 主手还不是 T3 刃。", EmberEntryService.t3WarnText(low));
    }
}
