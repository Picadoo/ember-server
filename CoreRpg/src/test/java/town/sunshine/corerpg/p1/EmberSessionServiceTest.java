package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * D237 / ARCH S3-8: session create / variety / opening-line helpers moved from {@code EmberRunService}
 * into {@link EmberSessionService}. Pins runId shape, D138 variety force parsing, eligibility, and
 * the verifyEntry opening lines against the pre-extract string concatenations (bv60 pin).
 */
public final class EmberSessionServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberSessionServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void runId_shapeMatchesPreExtract() {
        // nowMs=0 → "0"; rndToken=1 → "1" in base36
        assertEquals("q01-0-1", EmberSessionService.runId("q01", 0, false, 0L, 1));
        assertEquals("q01c-0-1", EmberSessionService.runId("q01", 0, true, 0L, 1));
        assertEquals("q01a3-0-1", EmberSessionService.runId("q01", 3, false, 0L, 1));
        assertEquals("q01a3-0-1", EmberSessionService.runId("q01", 3, true, 0L, 1)); // abyss wins over challenge marker
        // 36^3-1 = 46655 → "zzz"
        assertEquals("r01-0-zzz", EmberSessionService.runId("r01", 0, false, 0L, 36 * 36 * 36 - 1));
        long now = 1_720_000_000_000L;
        String id = EmberSessionService.runId("q07", 0, true, now, 100);
        assertTrue(id.startsWith("q07c-"));
        assertEquals(3, id.split("-").length);
    }

    @Test public void varietyEligible_plainMainOnly() {
        EmberRunMaps maps = bundled();
        assertTrue(maps.variety.on());
        assertTrue(EmberSessionService.varietyEligible(false, 0, false, false, true));
        assertFalse(EmberSessionService.varietyEligible(true, 0, false, false, true));
        assertFalse(EmberSessionService.varietyEligible(false, 1, false, false, true));
        assertFalse(EmberSessionService.varietyEligible(false, 0, true, false, true));
        assertFalse(EmberSessionService.varietyEligible(false, 0, false, true, true));
        assertFalse(EmberSessionService.varietyEligible(false, 0, false, false, false));
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        EmberRunMaps.MapDef r01 = maps.byKey("r01");
        EmberRunMaps.MapDef rush = maps.byKey("rush");
        assertTrue(EmberSessionService.varietyEligible(false, 0, q01.raid, q01.rush, maps.variety.on()));
        assertFalse(EmberSessionService.varietyEligible(false, 0, r01.raid, r01.rush, maps.variety.on()));
        assertFalse(EmberSessionService.varietyEligible(false, 0, rush.raid, rush.rush, maps.variety.on()));
    }

    @Test public void applyForcedVariety_knownAffixAndEvent() {
        String[] rolled = new String[]{"r2", "blazing", "r3", "timed"};
        String[] affix = EmberSessionService.applyForcedVariety(rolled, "regen:r1");
        assertEquals(Arrays.asList("r1", "regen", "r3", "timed"), Arrays.asList(affix));
        // original untouched
        assertEquals("r2", rolled[0]);
        String[] ev = EmberSessionService.applyForcedVariety(rolled, "crystal:r2");
        assertEquals("r2", ev[0]); // affix room unchanged
        assertEquals("blazing", ev[1]);
        assertEquals("r2", ev[2]);
        assertEquals("crystal", ev[3]);
        String[] timed = EmberSessionService.applyForcedVariety(rolled, "event:r1");
        assertEquals("r1", timed[2]);
        assertEquals("timed", timed[3]);
        String[] timed2 = EmberSessionService.applyForcedVariety(rolled, "timed:r3");
        assertEquals("r3", timed2[2]);
        assertEquals("timed", timed2[3]);
        String[] none = EmberSessionService.applyForcedVariety(rolled, null);
        assertEquals(Arrays.asList("r2", "blazing", "r3", "timed"), Arrays.asList(none));
        // short roll (no eventKind) defaults eventKind to timed when eventRoom non-empty
        String[] shortRoll = EmberSessionService.applyForcedVariety(new String[]{"r1", "split", "r2"}, null);
        assertEquals("timed", shortRoll[3]);
        EmberRunSession s = new EmberRunSession();
        EmberSessionService.stampVariety(s, affix);
        assertEquals("r1", s.affixRoom);
        assertEquals("regen", s.affix);
        assertEquals("r3", s.eventRoom);
        assertEquals("timed", s.eventKind);
    }

    @Test public void feeAndNobodyTexts_matchPreExtract() {
        assertEquals("§7这一层的费用 300 币用 §f2 枚 T3 印记§7抵了（余烬币不够；1 枚抵 200 币，剩 8 枚）· 没打成会和体力一起退回",
                EmberSessionService.abyssFeeMarkText(300, 2, 200, 8));
        assertEquals("FreshQ821 余烬币不足（这一层 300）", EmberSessionService.feeShortPartyText("FreshQ821", 300));
        assertEquals("没能进入实例，预留的体力已退还。原因见上方 DP 提示（人数 / 冷却约 5 秒）。",
                EmberSessionService.nobodyEnteredText());
    }

    @Test public void openingLines_matchPreExtract() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.AbyssTier t1 = maps.abyssTier(1);
        assertNotNull(t1);
        assertEquals("§5深渊第 1 层 §7· 敌方生命 ×" + String.format(java.util.Locale.ROOT, "%.2f", t1.hp)
                        + " 伤害 ×" + String.format(java.util.Locale.ROOT, "%.2f", t1.dmg)
                        + "（在挑战版之上）· 掉落成色 " + EmberRunService.qualityLabel(t1.quality)
                        + " · 打完首领才结算，失败只丢这一层的花费",
                EmberSessionService.abyssOpeningText(1, t1.hp, t1.dmg, EmberRunService.qualityLabel(t1.quality)));
        assertEquals("§c国庆 · 烟火庙会 §7· 2 人（敌方生命 ×1.65）· 小怪和首领掉国庆币 · 不发余烬币和装备 · 首次通关得限时称号 · 倒下即失败",
                EmberSessionService.festivalOpeningText("烟火庙会", 2, 1.65, "国庆币"));
        EmberRunMaps.MapDef rush = maps.byKey("rush");
        String rushLine = EmberSessionService.rushOpeningText(rush.rushLabel, 1, EmberRushService.chainText(rush),
                1.0 * rush.rushHp, rush.rushDmg, rush.chain.size() > 1,
                Math.round(rush.rushBreak), Math.round(rush.rushHeal * 100), EmberRushService.rewardText(rush));
        assertTrue(rushLine.startsWith("§c" + rush.rushLabel + " §7· 1 人 · "));
        assertTrue(rushLine.contains("倒下观战，没有复活 · 只发"));
        EmberRunMaps.Modifier lean = maps.modifier("lean");
        assertEquals("§d自选誓约「" + lean.name + "」§7" + lean.text + " · 通关结算每人 +1 枚本图首领徽记（掉落不变）",
                EmberSessionService.pledgeOpeningText(lean.name, lean.text, 1));
        assertEquals("§b本周规则「" + lean.name + "」§7" + lean.text + "（奖励不变；本周精选图首通后的重打）",
                EmberSessionService.weeklyModOpeningText(lean.name, lean.text, false));
        assertEquals("§b本周规则「" + lean.name + "」§7" + lean.text + "（奖励不变）",
                EmberSessionService.weeklyModOpeningText(lean.name, lean.text, true));
        assertEquals("§7主线本开始 · 1 人（敌方生命 ×1.00）· 走进前方房间开战 · 击败首领后统一结算",
                EmberSessionService.mainOpeningText(false, false, 1, 1.0));
        assertEquals("§c挑战版 §7· 掉落 T3 · 主线本开始 · 2 人（敌方生命 ×1.65）· 走进前方房间开战 · 击败首领后统一结算",
                EmberSessionService.mainOpeningText(false, true, 2, 1.65));
        assertEquals("§5深渊 §7· 掉落 T3 · 主线本开始 · 1 人（敌方生命 ×1.00）· 走进前方房间开战 · 击败首领后统一结算",
                EmberSessionService.mainOpeningText(true, true, 1, 1.0));
    }

    @Test public void bundledPassSecondsAndCost_unchanged() {
        EmberRunMaps maps = bundled();
        assertEquals(30, maps.cost);
        assertEquals(30, maps.cost(maps.byKey("q01")));
        assertTrue(maps.passSeconds > 0);
        assertEquals(78, maps.balanceVersion); // bv78 pin — session extract must not bump numbers
    }

    @Test public void abyssRhythmReveal_matchesD300Copy() {
        EmberRunMaps maps = bundled();
        // D300 W1c TrMenu copy pins — must stay identical for abyss enter reveal
        String[][] expect = {
                {"q01", "灰烬庭院", "事件偏早 · 门慢半拍"},
                {"q02", "焦骨甬道", "事件居中"},
                {"q03", "残誓地窖", "事件偏晚 · 门慢半拍"},
                {"q04", "潮蚀水道", "事件居中"},
                {"q05", "断塔回廊", "事件偏早"},
                {"q06", "霜封哨所", "事件偏晚 · 门慢半拍"},
                {"q07", "锈轨矿道", "事件居中 · 门慢半拍"},
        };
        for (String[] row : expect) {
            EmberRunMaps.MapDef m = maps.byKey(row[0]);
            assertNotNull(m);
            assertEquals(row[1], m.name);
            boolean door = EmberSessionService.mapHasDoorDelay(m);
            assertEquals(row[2], EmberSessionService.rhythmTag(m.eventAfter, door));
            assertEquals("§5本层地图：§f" + row[1] + " §7· 节奏：§b" + row[2],
                    EmberSessionService.abyssRhythmRevealText(m.name, m.eventAfter, door));
        }
        assertEquals("§5本层地图：§f? §7· 节奏：§b事件居中",
                EmberSessionService.abyssRhythmRevealText(null, null, false));
    }

}
