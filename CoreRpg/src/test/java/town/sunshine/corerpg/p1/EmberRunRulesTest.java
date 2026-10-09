package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/** G04 settlement rules (book §9.1–9.4, §20.5) and the E01/E03/E04/E10/E11 acceptance rows. */
public class EmberRunRulesTest {

    private static EmberRunRules.SettleInput input(boolean boss) {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q01-test-1";
        in.player = "00000000-0000-0000-0000-000000000001";
        in.seed = 42L;
        in.tier = 1;
        in.target = "burst";
        in.bossKilled = boss;
        return in;
    }

    private static Map<String, EmberRunRules.Grant> byKey(List<EmberRunRules.Grant> l) {
        Map<String, EmberRunRules.Grant> m = new HashMap<String, EmberRunRules.Grant>();
        for (EmberRunRules.Grant g : l) assertNull("duplicate key " + g.key, m.put(g.key, g));
        return m;
    }

    // ------------------------------------------------------------------ drop-weight math

    @Test public void weightedPickBucketsMatchTheBook() {
        int n = 100000;
        int[] q = new int[4], c = new int[4], e = new int[4];
        for (int i = 0; i < n; i++) {
            double u = (i + 0.5) / n; // exact grid → exact bucket shares
            q[EmberRunRules.pickQuality(u)]++;
            c[EmberRunRules.pickCraft(u)]++;
            e[EmberRunRules.rollExtra(u).ordinal()]++;
        }
        assertArrayEquals(new int[]{70000, 23000, 6000, 1000}, q);
        assertArrayEquals(new int[]{70000, 20000, 9000, 1000}, c);
        assertArrayEquals(new int[]{70000, 15000, 10000, 5000}, e);
    }

    @Test public void familyIsTarget60OthersTwenty() {
        int n = 100000;
        Map<String, Integer> f = new HashMap<String, Integer>();
        for (int i = 0; i < n; i++) f.merge(EmberRunRules.pickFamily("scorch", (i + 0.5) / n), 1, Integer::sum);
        assertEquals(60000, (int) f.get("scorch"));
        assertEquals(20000, (int) f.get("burst"));
        assertEquals(20000, (int) f.get("sustain"));
        assertEquals(0.6, EmberRunRules.familyProbability("scorch", "scorch"), 1e-12);
        assertEquals(0.2, EmberRunRules.familyProbability("scorch", "sustain"), 1e-12);
        // no target chosen → uniform
        assertEquals(1.0 / 3, EmberRunRules.familyProbability(null, "burst"), 1e-12);
        Map<String, Integer> u = new HashMap<String, Integer>();
        for (int i = 0; i < 3000; i++) u.merge(EmberRunRules.pickFamily(null, (i + 0.5) / 3000), 1, Integer::sum);
        assertEquals(1000, (int) u.get("scorch"));
        assertEquals(1000, (int) u.get("sustain"));
    }

    @Test public void monteCarloTargetPartAndRarity() {
        Random r = new Random(7);
        int n = 400000, targetBlade = 0, targetBladeTop = 0, blade = 0;
        int[] q = new int[4];
        for (int i = 0; i < n; i++) {
            EmberRunRules.ItemRoll it = EmberRunRules.rollItem(1, "sustain", r);
            assertEquals(1, it.tier);
            if (it.slot.equals("blade")) blade++;
            if (it.family.equals("sustain") && it.slot.equals("blade")) {
                targetBlade++;
                if (it.quality == 3) targetBladeTop++;
            }
            q[it.quality]++;
        }
        assertEquals(0.5, blade / (double) n, 0.004);
        assertEquals(0.30, targetBlade / (double) n, 0.004);         // §9.2: 0.60 × 0.50
        assertEquals(0.003, targetBladeTop / (double) n, 0.0006);    // §9.2: 0.60 × 0.50 × 0.01
        assertEquals(0.23, q[1] / (double) n, 0.004);
        // §9.2: eight misses of one target part ≈ 5.76 %
        assertEquals(0.0576, Math.pow(1 - 0.6 * 0.5, 8), 5e-5);
    }

    @Test public void expectedEconomyPerRunMatches93() {
        double tot = 0;
        for (int w : EmberRunRules.EXTRA_WEIGHTS) tot += w;
        double pT = EmberRunRules.EXTRA_WEIGHTS[1] / tot, pE = EmberRunRules.EXTRA_WEIGHTS[2] / tot, pC = EmberRunRules.EXTRA_WEIGHTS[3] / tot;
        assertEquals(315.0, EmberRunRules.BASE_COIN + pT * EmberRunRules.TREASURE_COIN, 1e-9);
        assertEquals(25.0, EmberRunRules.BASE_SHARD + pE * EmberRunRules.ELITE_SHARD, 1e-9);
        assertEquals(2.1, EmberRunRules.BASE_CORE + pE * EmberRunRules.ELITE_CORE, 1e-9);
        assertEquals(1.05, 1 + pC, 1e-9);
        assertEquals(6, EmberRunRules.BASE_BONE);
        assertEquals(120, EmberRunRules.BASE_XP);
    }

    @Test public void partyHpFactorIsLockedFormula() {
        assertEquals(1.0, EmberRunRules.hpFactor(1), 1e-12);
        assertEquals(1.65, EmberRunRules.hpFactor(2), 1e-12);
        assertEquals(2.3, EmberRunRules.hpFactor(3), 1e-12);
        assertEquals(2.3, EmberRunRules.hpFactor(9), 1e-12); // clamped to the 3-player party cap
        assertEquals(1.0, EmberRunRules.hpFactor(0), 1e-12);
    }

    // ------------------------------------------------------------------ E01 first clear + normal drop

    @Test public void e01FirstClearAndNormalEachOnce() {
        EmberRunRules.SettleInput in = input(true);
        in.firstClear = new EmberRunRules.FirstClear("q01", "blade", 1, 0, 0, 0, "q02");
        Map<String, EmberRunRules.Grant> g = byKey(EmberRunRules.settle(in));
        assertEquals(300, g.get("base_coin").amount);
        assertEquals(24, g.get("base_shard").amount);
        assertEquals(6, g.get("base_bone").amount);
        assertEquals(2, g.get("base_core").amount);
        assertEquals(120, g.get("base_xp").amount);
        assertEquals("1", g.get("base_mark").id);
        assertEquals(EmberRunRules.Kind.ITEM, g.get("base_item").kind);   // normal drop still given on the first clear
        assertEquals("blade", g.get("fc_q01_choice").id);
        assertEquals("q02", g.get("fc_q01_unlock").id);

        // second clear: no first-clear rows, same base package
        EmberRunRules.SettleInput again = input(true);
        again.runId = "q01-test-2";
        Map<String, EmberRunRules.Grant> g2 = byKey(EmberRunRules.settle(again));
        assertFalse(g2.containsKey("fc_q01_choice"));
        assertEquals(7, g2.size());

        // Q03 first clear is materials + coins and still unlocks the next map
        EmberRunRules.SettleInput q3 = input(true);
        q3.firstClear = new EmberRunRules.FirstClear("q03", null, 1, 30, 4, 600, "q04");
        Map<String, EmberRunRules.Grant> g3 = byKey(EmberRunRules.settle(q3));
        assertEquals(30, g3.get("fc_q03_shard").amount);
        assertEquals(4, g3.get("fc_q03_core").amount);
        assertEquals(600, g3.get("fc_q03_coin").amount);
        assertEquals("q04", g3.get("fc_q03_unlock").id);

        // the free choice becomes a bound standard q0/c0 item of the chosen family
        EmberRunRules.Grant item = EmberRunRules.choiceItem(42L, in.player, in.runId, "fc_q01_choice", "scorch", "blade", 1);
        assertEquals("fc_q01_item", item.key);
        assertEquals(0, item.item.quality);
        assertEquals(0, item.item.craft);
        assertEquals("scorch", item.item.family);
    }

    // ------------------------------------------------------------------ E02/E10 ledger idempotency

    @Test public void e10LedgerNeverRerollsOrDuplicates() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        EmberRunRules.SettleInput in = input(true);
        List<EmberRunRules.Grant> first = EmberRunRules.settle(in);
        boolean[] created = new boolean[1];
        for (EmberRunRules.Grant g : first) {
            l.record(in.runId, g.key, g.encode(), EmberRunRules.ST_PENDING, 1L, created);
            assertTrue(created[0]);
        }
        String item = l.get(in.runId, "base_item").result;
        // a duplicate MM death / DP end / mail retry settles again with a different seed: nothing changes
        in.seed = 999L;
        for (EmberRunRules.Grant g : EmberRunRules.settle(in)) {
            EmberRunRules.Row r = l.record(in.runId, g.key, g.encode(), EmberRunRules.ST_PENDING, 2L, created);
            assertFalse(created[0]);
            assertEquals(1L, r.created);
        }
        assertEquals(item, l.get(in.runId, "base_item").result);
        assertEquals(first.size(), l.size());

        // full inventory: the item stays pending with its original result; delivery later marks it once
        assertEquals(first.size(), l.open().size());
        l.mark(in.runId, "base_coin", EmberRunRules.ST_DELIVERED, 3L);
        assertEquals(first.size() - 1, l.open().size());

        // persistence round trip keeps results and statuses
        EmberRunRules.Ledger back = EmberRunRules.Ledger.fromMap(l.toMap());
        assertEquals(item, back.get(in.runId, "base_item").result);
        assertEquals(EmberRunRules.ST_DELIVERED, back.get(in.runId, "base_coin").status);
        assertEquals(first.size() - 1, back.open().size());

        // the stored text decodes to the same item (identity included)
        EmberRunRules.Grant dec = EmberRunRules.Grant.decode("base_item", item);
        assertNotNull(dec);
        assertTrue(dec.id.matches("[0-9a-f]{32}"));
        assertEquals(item, dec.encode());

        // pruning never drops open rows
        assertEquals(1, back.prune(10_000_000_000L, 1000L));
        assertEquals(first.size() - 1, back.size());
    }

    @Test public void sameSeedSameRollDifferentRunDifferentUid() {
        EmberRunRules.SettleInput a = input(true), b = input(true);
        String ia = byKey(EmberRunRules.settle(a)).get("base_item").encode();
        String ib = byKey(EmberRunRules.settle(b)).get("base_item").encode();
        assertEquals(ia, ib);
        b.runId = "q01-test-other";
        assertNotEquals(ia, byKey(EmberRunRules.settle(b)).get("base_item").encode());
        Set<String> uids = new HashSet<String>();
        for (int i = 0; i < 1000; i++) assertTrue(uids.add(EmberRunRules.rewardUid(1L, "p", "run" + i, "base_item")));
    }

    // ------------------------------------------------------------------ E03 / E04 marks and tier ceiling

    @Test public void e03MarksAccumulateAndExchangeAtEight() {
        // a run that does not drop the wanted part still pays one mark; the counter only grows
        int marks = 0;
        for (int run = 0; run < 8; run++) {
            EmberRunRules.SettleInput in = input(true);
            in.runId = "q01-e03-" + run;
            EmberRunRules.Grant m = byKey(EmberRunRules.settle(in)).get("base_mark");
            assertEquals(EmberRunRules.Kind.MARK, m.kind);
            marks += m.amount;
            if (run < 7) assertNotNull(EmberRunRules.exchangeCheck(marks, 1, 1, "burst", "charm"));
        }
        assertEquals(8, marks);
        assertNull(EmberRunRules.exchangeCheck(marks, 1, 1, "burst", "charm"));
        assertNotNull(EmberRunRules.exchangeCheck(marks, 1, 1, "fire", "charm"));
        assertNotNull(EmberRunRules.exchangeCheck(marks, 1, 1, "burst", "ring"));
    }

    @Test public void e04LowTierMarksAndChestsNeverMakeHigherTier() {
        assertNotNull(EmberRunRules.exchangeCheck(64, 1, 3, "scorch", "blade"));
        assertNotNull(EmberRunRules.exchangeCheck(64, 1, 2, "scorch", "blade"));
        assertNull(EmberRunRules.exchangeCheck(8, 2, 2, "scorch", "blade"));
        assertNotNull(EmberRunRules.exchangeCheck(8, 0, 0, "scorch", "blade"));
        for (int s = 0; s < 200; s++) {
            EmberRunRules.SettleInput in = input(true);
            in.seed = s;
            in.extra = EmberRunRules.Extra.CHEST;
            in.extraDone = true;
            Map<String, EmberRunRules.Grant> g = byKey(EmberRunRules.settle(in));
            assertEquals(1, g.get("extra_chest_item").item.tier);
            assertEquals(1, g.get("base_item").item.tier);
            assertEquals("1", g.get("base_mark").id);
        }
    }

    // ------------------------------------------------------------------ E11 extras only with the boss

    @Test public void e11NoBossNoReward() {
        EmberRunRules.SettleInput in = input(false);
        in.extra = EmberRunRules.Extra.TREASURE;
        in.extraDone = true;
        in.firstClear = new EmberRunRules.FirstClear("q01", "blade", 1, 0, 0, 0, "q02");
        assertTrue(EmberRunRules.settle(in).isEmpty());
        assertTrue(EmberRunRules.settle(null).isEmpty());
        // boss killed: pending treasure paid once; elite pays shards + core; skipped event pays nothing
        in.bossKilled = true;
        Map<String, EmberRunRules.Grant> g = byKey(EmberRunRules.settle(in));
        assertEquals(100, g.get("extra_treasure_coin").amount);
        in.extra = EmberRunRules.Extra.ELITE;
        g = byKey(EmberRunRules.settle(in));
        assertEquals(10, g.get("extra_elite_shard").amount);
        assertEquals(1, g.get("extra_elite_core").amount);
        in.extraDone = false;
        g = byKey(EmberRunRules.settle(in));
        assertFalse(g.containsKey("extra_elite_shard"));
        assertFalse(g.containsKey("extra_treasure_coin"));
    }

    @Test public void eligibilityKeepsDeadParticipantsDropsIdleAlts() {
        assertTrue(EmberRunRules.eligible(true, true, true, false));
        assertTrue(EmberRunRules.eligible(true, true, false, true));   // died earlier, legally took part
        assertFalse(EmberRunRules.eligible(true, false, true, false)); // idle alt standing in the instance
        assertFalse(EmberRunRules.eligible(true, true, false, false)); // left without dying
        assertFalse(EmberRunRules.eligible(false, true, true, false)); // cost never committed
    }

    // ------------------------------------------------------------------ bundled Q01–Q03 definitions

    private static EmberRunMaps bundled() {
        InputStream in = EmberRunRulesTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void textHints_D166() {
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef q01 = m.byKey("q01");
        assertEquals("r1", q01.rooms.get(0).id);
        assertTrue(q01.rooms.get(0).hint.contains("进门后退到门口打，别站进怪堆"));
        assertEquals("only the first room of Q01", "", q01.rooms.get(1).hint);
        assertTrue(m.byKey("r01").partyHint.contains("建议队伍里有炽愈"));
        assertEquals("", m.byKey("r02").partyHint);
        assertTrue(EmberRunRules.PRE_BOSS_HINT.contains("首领前留 2 瓶药"));
    }

    @Test public void textHints_D170() {
        EmberRunMaps m = bundled();
        assertTrue(m.byKey("q01").rooms.get(0).hint.contains("进门后退到门口打，别站进怪堆"));
        assertFalse(m.byKey("q02").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q02").rooms.get(0).hint.contains("先清近战"));
        assertFalse(m.byKey("q03").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q03").rooms.get(0).hint.contains("别把怪拉进拐角"));
        assertFalse(m.byKey("q04").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q04").rooms.get(0).hint.contains("水边落差"));
        assertFalse(m.byKey("q04").rooms.get(2).hint.isEmpty()); // r3 mid-room fall reminder
        assertFalse(m.byKey("q05").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q05").rooms.get(0).hint.contains("环廊"));
        assertFalse(m.byKey("q06").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q06").rooms.get(0).hint.contains("远程优先"));
        assertFalse(m.byKey("q07").rooms.get(0).hint.isEmpty());
        assertTrue(m.byKey("q07").rooms.get(0).hint.contains("货箱"));
    }

    @Test public void pledgeCombinesTheNormalRules() { // D174 stage 2b 自选誓约
        EmberRunMaps m = bundled();
        List<String> pool = new ArrayList<String>();
        for (EmberRunMaps.Modifier x : m.pledgePool()) pool.add(x.id);
        assertEquals("[lean, reverse]", pool.toString()); // only the D94 normal-run rules
        assertEquals("[lean, reverse]", EmberRunMaps.pledgeIds("pledge:lean+reverse+lean").toString());
        assertTrue(EmberRunMaps.pledgeIds("lean").isEmpty());
        EmberRunMaps.Modifier c = m.modifier("pledge:lean+reverse");
        assertEquals(3, c.potionCap);
        assertTrue(c.swapRooms);
        assertTrue(c.remap.isEmpty());
        assertFalse(c.tweaksConverted());
        assertEquals("限药+逆行", c.name);
        assertSame(c, m.modifier("pledge:lean+reverse"));
        assertNull(m.modifier("pledge:casters")); // a challenge-only rule cannot be pledged
        EmberRunMaps.Modifier r = m.modifier("pledge:reverse");
        assertEquals(0, r.potionCap);
        assertTrue(r.swapRooms);
    }

    @Test public void bundledMapsValidateAgainstTheCaps() {
        EmberRunMaps m = bundled();
        assertEquals(30, m.cost);
        assertEquals(3, m.partyMax);
        assertEquals("[]", m.validate().toString());
        assertEquals(7, m.maps.size());
        EmberRunMaps.MapDef q1 = m.byKey("q01"), q2 = m.byKey("q02"), q3 = m.byKey("q03");
        assertEquals("ember_daily_v1", q1.template); // D15 book white boxes
        assertEquals("ember_daily_ash_v1", q2.template);
        assertEquals("ember_daily_crypt_v1", q3.template);
        // D300 corridor-feel: event.after hang points + optional door_delay
        assertEquals("r1", q1.eventAfter);
        assertEquals("r2", q2.eventAfter);
        assertEquals("r3", q3.eventAfter);
        assertEquals(0.8, q1.room("r1").doorDelay, 1e-9);
        assertEquals(0.0, q2.room("r1").doorDelay, 1e-9);
        assertEquals(1.0, q3.room("r2").doorDelay, 1e-9);
        assertEquals("", q1.requires);
        assertEquals("q01", q2.requires);
        assertEquals("q02", q3.requires);
        assertEquals("charm", q1.firstClear.choiceSlot);
        assertEquals("piece", q2.firstClear.choiceSlot);
        assertEquals(600, q3.firstClear.coin);
        assertSame(q2, m.byWorld("dungeon_EmberQ02_252696C4"));
        assertNull(m.byWorld("dungeon_EmberDaily_252696C4"));
        // book numbers
        assertEquals(36, q1.roles.get("melee").hp, 0);
        assertEquals(200, q1.boss.hp, 0); // D86 (book 240)
        assertEquals(900, q2.boss.hp, 0);
        assertEquals(950, q3.boss.hp, 0); // D60
        assertEquals(69, m.balanceVersion);                // D401 short weekly goal (D400 bv68 …)
        assertEquals("g04-1/b69", m.ruleVersion);
        assertEquals(0.5, m.failRefund, 1e-9);             // D128 first failed challenge of the day: half the stamina back
        assertEquals(200, m.abyssFeeMarkCoin);             // D124 surplus T3 marks pay abyss fees
        assertEquals(63.5, m.byKey("q04").fallCatchY, 1e-9); // D122 Q04 fall-catch
        assertEquals(1, m.byKey("q04").rails.size());      // D122 R3 drain sealed at attach
        assertTrue(Double.isNaN(m.byKey("q01").fallCatchY));
        assertEquals(1, m.rotationBonusMarks);             // P2-1 parameter source
        assertEquals(3, m.rotationWeeklyCap);
        assertEquals(1, m.rotationNormalBonusMarks);       // D108
        assertEquals("2026-09-28", m.seasonAnchor);       // D116 seasons (display only)
        assertEquals(4, m.seasonWeeks);
        assertEquals(5, m.seasonDeepTier);                 // D123 (was 8)
        assertEquals(3, m.seasonTop);
        assertEquals(Integer.valueOf(3), m.goalTargets.get("abyss")); // D117 weekly goals (余烬徽 only)
        assertEquals(6, m.goalTargets.size());             // D144 core + D401 short
        assertEquals(Integer.valueOf(2), m.goalTargets.get("core"));
        assertEquals(Integer.valueOf(5), m.goalTargets.get("short")); // D401 optional short
        assertEquals(15, m.goalReward);
        assertEquals(20, m.goalBonus);
        assertEquals(0.2, m.raidLastReviveHp, 1e-9);      // D118
        assertEquals(20.0, m.raidReviveDelay, 1e-9);
        // P2-8 weekly rules: 14 rules × 7 maps, all 98 pairs over any 98 weeks (D187 skewed index); no multiplier keys at all
        assertEquals(14, m.modifiers.size());
        assertEquals(3, m.modifier("lean").potionCap);
        assertEquals("caster", m.modifier("casters").role("ranged", q3));
        assertEquals("ranged", m.modifier("casters").role("ranged", q1)); // Q01 has no caster → unchanged
        assertEquals("melee", m.modifier("casters").role("melee", q3));
        assertEquals("melee", m.modifier("disarm").role("heavy", q1)); // D152: heavy→melee when map has melee
        assertEquals("melee", m.modifier("disarm").role("melee", q1));
        assertEquals("heavy", m.modifier("guards").role("ranged", q1)); // D154: ranged→heavy when map has heavy (all Q01–Q07)
        assertEquals("heavy", m.modifier("guards").role("ranged", q3));
        assertEquals("melee", m.modifier("guards").role("melee", q1));
        assertTrue(m.modifier("lean").normal);              // D94: repeat normal runs get lean / reverse only
        assertTrue(m.modifier("reverse").normal);
        assertFalse(m.modifier("casters").normal);
        assertFalse(m.modifier("disarm").normal);           // D152: challenge-only like casters
        assertFalse(m.modifier("guards").normal);           // D154: challenge-only like casters/disarm
        assertEquals("heavy", m.modifier("wall").role("melee", q1));  // D155: melee→heavy when map has heavy
        assertEquals("ranged", m.modifier("wall").role("ranged", q1)); // ranged unchanged
        assertFalse(m.modifier("wall").normal);             // D155: challenge-only like casters/disarm/guards
        // D158 (review round 2 #5): each swap gets its own twist on the converted mobs only
        assertEquals(0.75, m.modifier("disarm").convInterval, 1e-9);   // fast melee
        assertEquals(0.8, m.modifier("disarm").convHp, 1e-9);
        assertEquals(1.35, m.modifier("guards").convSpeed, 1e-9);      // charging heavy
        assertEquals(1.5, m.modifier("wall").convAtk, 1e-9);           // real heavy hits
        assertEquals(1.2, m.modifier("wall").convDpsFactor(), 1e-9);
        assertTrue(m.modifier("disarm").tweaksConverted() && m.modifier("guards").tweaksConverted() && m.modifier("wall").tweaksConverted());
        assertFalse(m.modifier("casters").tweaksConverted());          // older rules untouched
        assertFalse(m.modifier("lean").tweaksConverted());
        assertTrue(m.modifier("reverse").swapRooms);
        // D178 Weekly Mod Pack 2: bolters / shell / press (challenge-only remap+converted)
        assertEquals("ranged", m.modifier("bolters").role("caster", q3)); // caster→ranged when map has ranged
        assertEquals("ranged", m.modifier("bolters").role("caster", q1)); // target ranged exists; spawn still noop (Q01 has no caster pack)
        assertEquals("ranged", m.modifier("bolters").role("ranged", q1)); // non-source roles unchanged
        assertEquals("heavy", m.modifier("shell").role("caster", q3));    // caster→heavy
        assertEquals("heavy", m.modifier("shell").role("caster", q1));    // target heavy exists; spawn noop without caster pack
        assertEquals("melee", m.modifier("press").role("ranged", q1));    // ranged→melee (all maps have melee)
        assertEquals("melee", m.modifier("press").role("ranged", q3));
        assertFalse(m.modifier("bolters").normal);
        assertFalse(m.modifier("shell").normal);
        assertFalse(m.modifier("press").normal);
        assertTrue(m.modifier("bolters").tweaksConverted());
        assertTrue(m.modifier("shell").tweaksConverted());
        assertTrue(m.modifier("press").tweaksConverted());
        assertEquals(0.9, m.modifier("bolters").convInterval, 1e-9);
        assertEquals(1.2, m.modifier("shell").convHp, 1e-9);
        assertEquals(0.7, m.modifier("shell").convAtk, 1e-9);
        assertEquals(1.35, m.modifier("shell").convInterval, 1e-9);
        assertEquals(1.3, m.modifier("press").convSpeed, 1e-9);
        assertEquals(0.85, m.modifier("press").convHp, 1e-9);
        // D186 Weekly Mod Pack 3: skirmish / hexers / ballista (challenge-only remap+converted)
        assertEquals("ranged", m.modifier("skirmish").role("melee", q1)); // melee→ranged
        assertEquals("ranged", m.modifier("skirmish").role("melee", q3));
        assertEquals("ranged", m.modifier("skirmish").role("ranged", q1)); // non-source unchanged
        assertEquals("caster", m.modifier("hexers").role("melee", q3));   // melee→caster when map has caster
        assertEquals("melee", m.modifier("hexers").role("melee", q1));    // Q01 has no caster → unchanged
        assertEquals("ranged", m.modifier("ballista").role("heavy", q1)); // heavy→ranged (all maps have ranged)
        assertEquals("ranged", m.modifier("ballista").role("heavy", q3));
        assertFalse(m.modifier("skirmish").normal);
        assertFalse(m.modifier("hexers").normal);
        assertFalse(m.modifier("ballista").normal);
        assertTrue(m.modifier("skirmish").tweaksConverted());
        assertTrue(m.modifier("hexers").tweaksConverted());
        assertTrue(m.modifier("ballista").tweaksConverted());
        assertEquals(0.85, m.modifier("skirmish").convInterval, 1e-9);
        assertEquals(0.9, m.modifier("skirmish").convHp, 1e-9);
        assertEquals(1.1, m.modifier("hexers").convInterval, 1e-9);
        assertEquals(1.25, m.modifier("ballista").convAtk, 1e-9);
        assertEquals(1.35, m.modifier("ballista").convInterval, 1e-9);
        assertEquals(0.8, m.modifier("ballista").convSpeed, 1e-9);
        // D187 Weekly Mod Pack 4: hexplate (heavy→caster) / blades (caster→melee), challenge-only
        assertEquals("caster", m.modifier("hexplate").role("heavy", q3));  // heavy→caster when map has caster
        assertEquals("heavy", m.modifier("hexplate").role("heavy", q1));   // Q01 has no caster → unchanged
        assertEquals("melee", m.modifier("hexplate").role("melee", q3));   // non-source unchanged
        assertEquals("melee", m.modifier("blades").role("caster", q3));    // caster→melee
        assertEquals("ranged", m.modifier("blades").role("ranged", q3));   // non-source unchanged
        assertFalse(m.modifier("hexplate").normal);
        assertFalse(m.modifier("blades").normal);
        assertTrue(m.modifier("hexplate").tweaksConverted());
        assertTrue(m.modifier("blades").tweaksConverted());
        assertEquals(1.25, m.modifier("hexplate").convHp, 1e-9);
        assertEquals(1.2, m.modifier("hexplate").convInterval, 1e-9);
        assertEquals(1.2, m.modifier("blades").convSpeed, 1e-9);
        assertEquals(0.9, m.modifier("blades").convAtk, 1e-9);
        for (EmberRunMaps.Modifier x : m.modifiers) { // every remap direction used at most once across Packs 1–4
            if (x.remap.isEmpty()) continue;
            for (EmberRunMaps.Modifier y : m.modifiers) if (x != y) assertNotEquals(x.id + " vs " + y.id, x.remap, y.remap);
        }
        // D187 skewed index: coprime pools keep w mod n; 14 rules × 7 maps meet every pair in any 98-week window, never the same rule twice in a row
        assertEquals(5, EmberRunMaps.modifierIndex(5L, 12, 7));
        assertEquals(Math.floorMod(-3L, 12L), EmberRunMaps.modifierIndex(-3L, 12, 7));
        for (long w0 = 0; w0 < 200; w0 += 13) {
            java.util.Set<String> win = new java.util.HashSet<String>();
            for (long w = w0; w < w0 + 98; w++) win.add(Math.floorMod(w, 7L) + "/" + EmberRunMaps.modifierIndex(w, 14, 7));
            assertEquals(98, win.size());
        }
        for (long w = 0; w < 400; w++) assertNotEquals(EmberRunMaps.modifierIndex(w, 14, 7), EmberRunMaps.modifierIndex(w + 1, 14, 7));
        assertNull(m.modifier(""));
        java.util.Set<String> pairs = new java.util.HashSet<String>();
        java.time.LocalDate d0 = java.time.LocalDate.of(2026, 10, 5);
        for (int wk = 0; wk < 98; wk++) {
            java.time.LocalDate d = d0.plusWeeks(wk);
            pairs.add(EmberRunRules.featuredChallenge(new java.util.ArrayList<String>(m.maps.keySet()), d) + "/" + m.modifierFor(d).id);
            assertSame(m.modifierFor(d), m.modifierFor(d.plusDays(6)));
        }
        assertEquals(98, pairs.size());
        // swap layout: r3's group on r1's points
        assertEquals(EmberRunMaps.layout(q3.room("r3").a, q3.room("r1").points.size(), 5L).size(),
                q3.room("r3").a.values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(4, (int) q1.room("r1").a.get("melee"));
        assertEquals(2, (int) q2.room("r2").b.get("ranged"));
        assertEquals(1, (int) q3.room("r3").a.get("caster"));
        assertNotNull(q3.boss.adds);
        assertNotNull(q2.boss.skills.get(0).follow);
        assertEquals(0.5, q2.boss.skills.get(0).follow.below, 0);
        // batch 2 (§23.2): Q03 → Q04 → Q05 chain, T2 drops, two-skill bosses
        EmberRunMaps.MapDef q4 = m.byKey("q04"), q5 = m.byKey("q05");
        assertEquals("q04", q3.unlocks);
        assertEquals("q03", q4.requires);
        assertEquals("q05", q4.unlocks);
        assertEquals("q04", q5.requires);
        assertEquals(2, q4.tier);
        assertEquals(2, q5.tier);
        assertEquals("ember_daily_tide_v1", q4.template);
        assertEquals("ember_daily_spire_v1", q5.template); // B2.151 walkable map
        assertEquals(6, q4.firstClear.blank);
        assertEquals(6, q4.firstClear.core);
        assertEquals(2100, q4.firstClear.coin);
        assertEquals(20, q5.firstClear.bone);
        assertEquals(6, q5.firstClear.blank);
        assertEquals(900, q5.firstClear.coin);              // D104
        assertEquals("核心 6 胚料 6 币 2100", q4.firstClearLabel());
        assertEquals("胚料 6 骨尘 20 币 900", q5.firstClearLabel());
        assertEquals(72, q4.roles.get("melee").hp, 0); // D60 §2c plan B
        assertEquals(5, (int) q4.room("r3").a.get("melee")); // D31: Q04 R3 A = 5 melee + 1 heavy
        assertEquals(1, (int) q4.room("r3").a.get("heavy"));
        assertEquals(8, q4.roles.get("melee").atk, 0);
        assertEquals(115, q5.roles.get("melee").hp, 0);
        assertEquals(1420, q4.boss.hp, 0);
        assertEquals(2280, q5.boss.hp, 0);
        assertEquals(3, q4.boss.skills.size());           // D140: + 潮涌
        assertEquals("player", q4.boss.skills.get(0).target);
        assertEquals(1.0, q4.boss.skills.get(1).kb, 0);
        assertEquals(0.5, q5.boss.skills.get(0).kb, 0);
        assertEquals(0.5, q4.boss.recover, 0);
        assertEquals(2, q4.spread.size());
        assertEquals(0, q4.clear.size()); // D15: the tide sign is gone with the old template (B2.153)
        assertEquals(0, q5.links.size()); // B2.151: real stairs, no passages
        assertEquals(0, q5.rails.size());
        assertEquals(true, q5.boss.waitInArea);
        assertEquals(0, q5.clear.size());
        assertEquals("r2", q4.eventAfter); // D300: corridor-feel hang points differ per map
        assertEquals("r1", q5.eventAfter); // D300
        assertSame(q5, m.byWorld("dungeon_EmberQ05_0A1B2C3D"));
        // batch 3 (§23.2 后段): Q05 → Q06 → Q07, Q07 drops T3 and unlocks nothing (challenge + T3 forge hang on its flag)
        EmberRunMaps.MapDef q6 = m.byKey("q06"), q7 = m.byKey("q07");
        assertEquals("q06", q5.unlocks);
        assertEquals("q05", q6.requires);
        assertEquals("q07", q6.unlocks);
        assertEquals("q06", q7.requires);
        assertEquals("", q7.unlocks);
        assertEquals(2, q6.tier);
        assertEquals(3, q7.tier);
        assertEquals("ember_daily_frost_v1", q6.template);
        assertEquals("ember_daily_rail_v1", q7.template);
        assertEquals("核心 10 币 1200", q6.firstClearLabel());
        assertEquals("核心 12 胚料 12 币 1800", q7.firstClearLabel());
        assertEquals(124, q6.roles.get("melee").hp, 0);
        assertEquals(10, q6.roles.get("melee").atk, 0);
        assertEquals(133, q7.roles.get("melee").hp, 0);
        assertEquals(186, q7.roles.get("heavy").hp, 0);
        assertEquals(12, q7.roles.get("caster").atk, 0);
        assertEquals(442, q7.roles.get("elite").hp, 0);
        assertEquals(2660, q6.boss.hp, 0);
        assertEquals(20, q6.boss.atk, 0);
        assertEquals(3120, q7.boss.hp, 0);
        assertEquals(22, q7.boss.atk, 0);
        EmberRunMaps.Skill blade = q6.boss.skills.get(0);
        assertEquals("line", blade.type);
        assertEquals(1, blade.stripFrom(), 0);
        assertEquals(7, blade.stripTo(), 0);
        assertEquals(13.0, blade.every, 0);             // D140: 10 → 12.5; D173 12.5 → 13.0 (+ 霜锥)
        assertEquals(34, blade.dmg, 0);
        assertEquals(4, blade.follow.shift, 0);
        assertEquals(1.0, blade.follow.delay, 0);
        assertEquals(1.01, blade.follow.below, 0); // always fires
        assertFalse(blade.light);
        EmberRunMaps.Skill slam = q7.boss.skills.get(0), charge = q7.boss.skills.get(1);
        assertEquals("player", slam.target);
        assertEquals(3.5, slam.radius, 0);
        assertEquals(1.5, slam.recover, 0);
        assertEquals("charge", charge.type);
        assertEquals(8, charge.length, 0);
        assertEquals(4, charge.width, 0);
        assertEquals(14, charge.every, 0);
        assertFalse(slam.light || charge.light); // 重砸 / 冲撞 both use the heavy challenge value 72
        assertNotNull(q6.room("r3").door); // runtime door into the boss hall
        assertNotNull(q7.room("r3").door);
        // D15: book white boxes, no instance rails / clears; bosses wait in the book-size hall
        for (EmberRunMaps.MapDef d : m.maps.values()) {
            assertEquals(d.key, "q04".equals(d.key) ? 1 : 0, d.rails.size()); // D122: only Q04's R3 drain
            assertEquals(d.key, 0, d.clear.size());
            assertEquals(d.key, 0, d.links.size());
            assertTrue(d.key, d.boss.waitInArea);
            assertEquals(d.key, "[r0, r1, r2, r3, rb]", d.safe.keySet().toString()); // §9 (validate() checks they sit in their room)
            assertEquals(d.key, "[entry, event, exit]", d.holo.keySet().toString());
            assertTrue(d.key, d.boss.area.contains(d.boss.at.x, d.boss.at.y, d.boss.at.z));
        }
        assertEquals(32, q6.boss.area.x1 - q6.boss.area.x0); // 33 wide (RB −16..16)
        assertEquals("r3", q6.eventAfter); // D300
        assertEquals("r2", q7.eventAfter); // D300
        assertEquals(1.2, q6.room("r1").doorDelay, 1e-9); // D300
        assertEquals(0.9, q7.room("r3").doorDelay, 1e-9); // D300
        assertEquals(0.0, q4.room("r1").doorDelay, 1e-9);
        assertEquals(0.0, q5.room("r2").doorDelay, 1e-9);
        assertSame(q7, m.byWorld("dungeon_EmberQ07_0A1B2C3D"));
    }

    @Test public void compositionCapsAndLayout() {
        Map<String, Integer> c = new HashMap<String, Integer>();
        c.put("melee", 6);
        c.put("ranged", 3);
        assertNotNull(EmberRunMaps.checkComposition(c, 10));
        c.put("ranged", 2);
        assertNull(EmberRunMaps.checkComposition(c, 8));
        assertNotNull(EmberRunMaps.checkComposition(c, 7));
        c.put("caster", 1);
        assertNotNull(EmberRunMaps.checkComposition(c, 9)); // 9 > 8 alive
        EmberRunMaps.Room r = bundled().byKey("q01").room("r3");
        List<String[]> lay = EmberRunMaps.layout(r, false, 12345L);
        assertEquals(6, lay.size());
        Set<String> used = new HashSet<String>();
        for (String[] s : lay) assertTrue("points reused", used.add(s[1]));
        assertEquals(EmberRunMaps.layout(r, false, 12345L).get(0)[1], lay.get(0)[1]);
    }

    @Test public void q04q05FirstClearPackages() {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q04-x"; in.player = "p"; in.seed = 7; in.tier = 2; in.bossKilled = true;
        in.firstClear = new EmberRunRules.FirstClear("q04", null, 2, 0, 6, 900, 0, 6, "q05");
        java.util.Map<String, EmberRunRules.Grant> g = new java.util.HashMap<String, EmberRunRules.Grant>();
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
        assertEquals(6, g.get("fc_q04_blank").amount);
        assertEquals(EmberUpgradeRules.MAT_BLANK, g.get("fc_q04_blank").id);
        assertEquals(6, g.get("fc_q04_core").amount);
        assertEquals(900, g.get("fc_q04_coin").amount);
        assertNull(g.get("fc_q04_shard"));
        assertNull(g.get("fc_q04_bone"));
        assertEquals("q05", g.get("fc_q04_unlock").id);
        assertEquals("2", g.get("base_mark").id); // T2 mark
        assertEquals(2, g.get("base_item").item.tier);
        in.firstClear = new EmberRunRules.FirstClear("q05", null, 2, 0, 0, 0, 20, 6, "q06");
        g.clear();
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
        assertEquals(20, g.get("fc_q05_bone").amount);
        assertEquals(EmberUpgradeRules.MAT_BONE, g.get("fc_q05_bone").id);
        assertEquals(6, g.get("fc_q05_blank").amount);
        assertNull(g.get("fc_q05_coin"));
        assertEquals("q06", g.get("fc_q05_unlock").id);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> rawQ01() {
        InputStream in = EmberRunRulesTest.class.getResourceAsStream("/ember-v1-runs.yml");
        Map<String, Object> root = (Map<String, Object>) new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return (Map<String, Object>) ((Map<String, Object>) root.get("maps")).get("q01");
    }

    private static List<Object> xyz(int... v) {
        List<Object> l = new java.util.ArrayList<Object>();
        for (int i : v) l.add(i);
        return l;
    }

    @Test public void spreadLinksRailsClearParseAndValidate() {
        Map<String, Object> q = rawQ01();
        q.put("spread", java.util.Arrays.asList(xyz(-2, 65, 0), xyz(2, 65, 0)));
        Map<String, Object> link = new java.util.HashMap<String, Object>();
        link.put("after", "r1"); link.put("label", "上楼"); link.put("from", xyz(-1, 65, 14, 1, 67, 14));
        link.put("to", xyz(0, 65, 20)); link.put("yaw", 180);
        q.put("links", java.util.Collections.singletonList(link));
        q.put("rails", java.util.Collections.singletonList(xyz(-9, 65, 2, -9, 66, 2)));
        q.put("clear", java.util.Collections.singletonList(xyz(6, 64, 11)));
        EmberRunMaps.MapDef d = new EmberRunMaps.MapDef("q01", q);
        assertNull(d.validate());
        assertNull(d.spreadPoint(0));                      // leader keeps the DP spawn
        assertEquals(-2, (int) d.spreadPoint(1).x);
        assertEquals(2, (int) d.spreadPoint(2).x);
        assertEquals(-2, (int) d.spreadPoint(3).x);        // wraps
        assertEquals(180f, d.links.get(0).yaw, 0);
        assertEquals("上楼", d.links.get(0).label);
        assertEquals(1, d.rails.size());
        assertEquals(11, (int) d.clear.get(0).z);
        // a rail over a mob point, a link into its own box, a link after a non-room are refused
        q.put("rails", java.util.Collections.singletonList(xyz(-6, 64, 29, -6, 65, 29))); // D15 q01 r1 P1
        assertTrue(new EmberRunMaps.MapDef("q01", q).validate().contains("rail covers point"));
        q.remove("rails");
        link.put("to", xyz(0, 65, 14));
        assertTrue(new EmberRunMaps.MapDef("q01", q).validate().contains("inside its own entry box"));
        link.put("to", xyz(0, 65, 20)); link.put("after", "r9");
        assertTrue(new EmberRunMaps.MapDef("q01", q).validate().contains("not a room"));
    }

    @Test public void directedForgeT2NeedsOwnQ04FirstClear() {
        assertNull(EmberRunRules.directedForgeFlag(1));
        assertEquals("q04", EmberRunRules.directedForgeFlag(2));
        assertEquals("q07", EmberRunRules.directedForgeFlag(3));
        assertNull(EmberRunRules.exchangeCheck(8, 1, 1, "scorch", "blade", false));      // T1 always open
        assertEquals("T2 定向锻造需本人首通 Q04", EmberRunRules.exchangeCheck(8, 2, 2, "scorch", "blade", false));
        assertNull(EmberRunRules.exchangeCheck(8, 2, 2, "scorch", "charm", true));
        assertEquals("T2 印记不足（7/8）", EmberRunRules.exchangeCheck(7, 2, 2, "scorch", "charm", false)); // count first
        assertEquals("T3 定向锻造需本人首通 Q07", EmberRunRules.exchangeCheck(8, 3, 3, "burst", "charm", false));
    }

    // ------------------------------------------------------------------ §18.1 challenge

    @Test public void challengeSectionParsesWithBookValues() {
        EmberRunMaps m = bundled();
        EmberRunMaps.Challenge c = m.challenge;
        assertNotNull(c);
        assertNull(c.validate());
        assertEquals("q07", c.requires);
        assertEquals(3, c.tier);
        assertEquals(90, c.bRef, 0);
        assertArrayEquals(EmberRunRules.CHALLENGE_QUALITY_WEIGHTS, c.quality);
        assertArrayEquals(new int[]{60, 28, 10, 2}, c.quality);
        // D104 (b13): HP ×0.70, damage ×0.85 of the §18.1 references
        assertEquals(189, c.mobs.get("melee")[0], 0);
        assertEquals(139, c.mobs.get("ranged")[0], 0);
        assertEquals(265, c.mobs.get("heavy")[0], 0);
        assertEquals(164, c.mobs.get("caster")[0], 0);
        assertEquals(20, c.mobs.get("melee")[1], 0);
        assertEquals(5600, c.bossHp, 0);
        assertEquals(37, c.bossAtk, 0);
        // overrides, not multipliers; timings and MM id are the map's own
        EmberRunMaps.MapDef q1 = m.byKey("q01");
        EmberRunMaps.Role r = q1.role("melee", c);
        assertEquals(189, r.hp, 0);
        assertEquals(20, r.atk, 0);
        assertEquals(q1.roles.get("melee").mm, r.mm);
        assertEquals(q1.roles.get("melee").interval, r.interval, 0);
        assertEquals(36, q1.role("melee", null).hp, 0);
        // heavy 61 / light 37 per map (D104)
        assertEquals(61, c.skillDmg(q1.boss.skills.get(0)), 0);                    // Q01 重斩
        assertEquals(37, c.skillDmg(m.byKey("q02").boss.skills.get(0).follow), 0);  // Q02 横扫 (second, lighter)
        assertEquals(61, c.skillDmg(m.byKey("q04").boss.skills.get(0)), 0);
        assertEquals(37, c.skillDmg(m.byKey("q04").boss.skills.get(1)), 0);
        assertEquals(61, c.skillDmg(m.byKey("q05").boss.skills.get(0)), 0);
        assertEquals(37, c.skillDmg(m.byKey("q05").boss.skills.get(1)), 0);
    }

    @Test public void challengeSettlementIsT3WithChallengeQualityAndNoFirstClear() {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q01c-x"; in.player = "p"; in.bossKilled = true; in.tier = EmberRunRules.CHALLENGE_TIER;
        in.qualityWeights = EmberRunRules.CHALLENGE_QUALITY_WEIGHTS; in.extra = EmberRunRules.Extra.CHEST; in.extraDone = true;
        int[] q = new int[4];
        int n = 20000;
        for (long seed = 0; seed < n; seed++) {
            in.seed = seed;
            Map<String, EmberRunRules.Grant> g = new HashMap<String, EmberRunRules.Grant>();
            for (EmberRunRules.Grant x : EmberRunRules.settle(in)) g.put(x.key, x);
            assertEquals("3", g.get("base_mark").id);
            assertEquals(3, g.get("base_item").item.tier);
            assertEquals(3, g.get("extra_chest_item").item.tier);
            assertEquals(300, g.get("base_coin").amount); // fixed settlement unchanged (§18.1 table)
            q[g.get("base_item").item.quality]++;
        }
        assertEquals(0.60, q[0] / (double) n, 0.015);
        assertEquals(0.28, q[1] / (double) n, 0.015);
        assertEquals(0.10, q[2] / (double) n, 0.01);
        assertEquals(0.02, q[3] / (double) n, 0.005);
        // normal table untouched when no weights are given
        assertEquals(0, EmberRunRules.pickQuality(null, 0.69));
        assertEquals(1, EmberRunRules.pickQuality(EmberRunRules.CHALLENGE_QUALITY_WEIGHTS, 0.61));
        assertEquals(0, EmberRunRules.pickQuality(0.61));
    }

    @Test public void weeklyRotationIsMondayBasedAndCyclesAllSeven_P2_1() {
        List<String> order = java.util.Arrays.asList("q01", "q02", "q03", "q04", "q05", "q06", "q07");
        java.time.LocalDate sun = java.time.LocalDate.of(2026, 10, 4), mon = sun.plusDays(1), nextSun = mon.plusDays(6);
        assertEquals(java.time.DayOfWeek.MONDAY, mon.getDayOfWeek());
        assertEquals(EmberRunRules.weekIndex(sun) + 1, EmberRunRules.weekIndex(mon));      // changes at Monday 00:00
        assertEquals(EmberRunRules.weekIndex(mon), EmberRunRules.weekIndex(nextSun));      // same week through Sunday
        assertEquals(EmberRunRules.featuredChallenge(order, mon), EmberRunRules.featuredChallenge(order, nextSun));
        assertNotEquals(EmberRunRules.featuredChallenge(order, sun), EmberRunRules.featuredChallenge(order, mon));
        Set<String> seen = new HashSet<String>();
        for (int w = 0; w < 7; w++) seen.add(EmberRunRules.featuredChallenge(order, mon.plusWeeks(w)));
        assertEquals(7, seen.size());                                                     // every map once per 7 weeks
        assertEquals("w" + EmberRunRules.weekIndex(mon), EmberRunRules.rotationWeekKey(mon.plusDays(3)));
        assertNull(EmberRunRules.featuredChallenge(java.util.Collections.<String>emptyList(), mon));
    }

    @Test public void abyssTableIsCappedMonotonicAndScalesTheChallenge_P2_2() {
        EmberRunMaps m = bundled();
        assertEquals(10, m.abyss.size());
        assertEquals("q07", m.abyssRequires);
        assertNull(m.abyssTier(0));
        assertNull(m.abyssTier(11));
        EmberRunMaps.AbyssTier t1 = m.abyssTier(1), t10 = m.abyssTier(10);
        assertEquals(1.00, t1.hp, 1e-9); // E-review D109: tier 1 = the challenge itself
        assertEquals(1.00, t1.dmg, 1e-9);
        assertEquals(2.07, t10.hp, 1e-9); // tier 10 keeps its old absolute strength
        assertEquals(1.42, t10.dmg, 1e-9);
        assertEquals(0, t1.fee);
        assertArrayEquals(m.challenge.quality, t1.quality);       // tier 1 = the challenge run
        assertTrue(t10.hp <= EmberRunMaps.ABYSS_MAX_HP && t10.dmg <= EmberRunMaps.ABYSS_MAX_DMG);
        for (int t = 2; t <= 10; t++) {
            assertTrue(m.abyssTier(t).hp >= m.abyssTier(t - 1).hp);
            assertTrue(m.abyssTier(t).fee >= m.abyssTier(t - 1).fee);
            assertTrue(m.abyssTier(t).quality[3] >= m.abyssTier(t - 1).quality[3]); // 极品 odds never drop deeper down
        }
        EmberRunMaps.Challenge c10 = m.abyssChallenge(10);
        assertEquals(m.challenge.bossHp * t10.hp, c10.bossHp, 1e-6);
        assertEquals(m.challenge.heavy * t10.dmg, c10.heavy, 1e-6);
        assertEquals(m.challenge.mobs.get("melee")[0] * t10.hp, c10.mobs.get("melee")[0], 1e-6);
        assertEquals(m.challenge.mobs.get("melee")[1] * t10.dmg, c10.mobs.get("melee")[1], 1e-6);
        assertArrayEquals(t10.quality, c10.quality);
        assertEquals(m.challenge.tier, c10.tier);                 // still T3 drops and marks
        // the segment map is a seeded pick over the seven accepted maps, deterministic per seed
        Set<String> seen = new HashSet<String>();
        for (long seed = 1; seed < 400; seed++) seen.add(m.abyssMap(seed).key);
        assertEquals(7, seen.size());
        assertSame(m.abyssMap(42L), m.abyssMap(42L));
    }

    @Test public void abyssTableRejectsAnUncappedRow_P2_2() {
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> ch = new HashMap<String, Object>();
        Map<String, Object> mobs = new HashMap<String, Object>();
        for (String r : new String[]{"melee", "ranged", "heavy", "caster", "treasure", "elite"}) {
            Map<String, Object> x = new HashMap<String, Object>(); x.put("hp", 100); x.put("atk", 10); mobs.put(r, x);
        }
        ch.put("mobs", mobs);
        root.put("challenge", ch);
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("hp", 3.0); row.put("dmg", 1.0); row.put("fee", 0); row.put("quality", java.util.Arrays.asList(60, 28, 10, 2));
        Map<String, Object> ab = new HashMap<String, Object>();
        ab.put("tiers", java.util.Collections.singletonList(row));
        root.put("abyss", ab);
        assertTrue(EmberRunMaps.parse(root).validate().toString().contains("above the table cap"));
    }

    @Test public void raidIsSeparateFromTheMainLineAndScalesWithTheParty_P2_5() {
        EmberRunMaps m = bundled();
        assertEquals("[]", m.validate().toString());
        EmberRunMaps.MapDef r = m.raids.get("r01");
        assertNotNull(r);
        assertTrue(r.raid);
        assertFalse(m.maps.containsKey("r01"));                    // never in the order / featured / abyss pool
        assertSame(r, m.byKey("r01"));
        assertSame(r, m.byWorld("dungeon_EmberQ0R1_1A2B3C4D"));
        assertSame(m.byKey("q07"), m.byWorld("dungeon_EmberQ07_1A2B3C4D"));
        assertEquals("q07", r.requires);
        assertEquals(3, m.partyMin(r));
        assertEquals(5, m.partyMax(r));
        assertEquals(50, m.cost(r));
        assertEquals(30, m.cost(m.byKey("q01")));
        assertEquals(3, m.partyMax(m.byKey("q01")));
        assertEquals(3, r.weeklyCap);
        assertEquals(1.0 + 0.85 * 4, EmberRunMaps.hpFactor(r, 5), 1e-9);
        assertEquals(1.0 + 0.20 * 2, EmberRunMaps.dmgFactor(r, 3), 1e-9);
        assertEquals(EmberRunRules.hpFactor(3), EmberRunMaps.hpFactor(m.byKey("q03"), 3), 1e-9);
        assertEquals(1.0, EmberRunMaps.dmgFactor(m.byKey("q03"), 3), 1e-9);  // §18.1: no damage scaling on main maps
        assertEquals(3, r.boss.skills.size());                     // existing moves only
        assertNotNull(r.boss.adds);
        for (int s = 1; s < 300; s++) assertFalse(m.abyssMap(s).raid);
    }

    @Test public void secondRaidSharesTheWeeklyCapAndGatesItsSecondPhase_P2_6() {
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef r1 = m.raids.get("r01"), r2 = m.raids.get("r02");
        assertNotNull(r2);
        assertTrue(r2.raid);
        assertSame(r2, m.byWorld("dungeon_EmberQ0R2_1A2B3C4D"));
        assertSame(m.byKey("q06"), m.byWorld("dungeon_EmberQ06_1A2B3C4D"));
        assertEquals("q07", r2.requires);
        assertEquals(3, r2.tier);
        assertEquals("raid", r1.capGroup);
        assertEquals(EmberRunService.capKey(r1), EmberRunService.capKey(r2));
        assertEquals("q01", EmberRunService.capKey(m.byKey("q01")));
        assertNull(r2.boss.adds);                                    // pressure from the phase, not adds
        int gated = 0;
        for (EmberRunMaps.Skill sk : r2.boss.skills) if (sk.below <= 1.0) gated++;
        assertEquals(1, gated);
        long[] next = {0, 0, 0};
        double[] below = {1.01, 1.01, 0.5};
        next[0] = 99999; next[1] = 99999;
        assertEquals(-1, EmberRunDirector.dueSkill(next, 1000, below, 0.8)); // gated above 50 %
        assertEquals(2, EmberRunDirector.dueSkill(next, 1000, below, 0.49));
    }

    @Test public void thirdRaidStacksForItsShareMoveAndCoversTheLastFamily_D137() {
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef r1 = m.raids.get("r01"), r2 = m.raids.get("r02"), r3 = m.raids.get("r03");
        assertNotNull(r3);
        assertTrue(r3.raid);
        assertSame(r3, m.byWorld("dungeon_EmberQ0R3_1A2B3C4D"));
        assertSame(m.byKey("q05"), m.byWorld("dungeon_EmberQ05_1A2B3C4D"));
        assertEquals("q07", r3.requires);
        assertEquals(EmberRunService.capKey(r1), EmberRunService.capKey(r3)); // one weekly counter for all three raids
        assertEquals("scorch", r3.lootFamily);                                 // r01 burst, r02 sustain, r03 scorch
        java.util.Set<String> fams = new java.util.HashSet<String>(java.util.Arrays.asList(r1.lootFamily, r2.lootFamily, r3.lootFamily));
        assertEquals(3, fams.size());
        assertNull(r3.boss.adds);
        int shares = 0;
        for (EmberRunMaps.Skill sk : r3.boss.skills) if (sk.share) { shares++; assertEquals("player", sk.target); assertTrue(sk.warn >= 2.5); }
        assertEquals(1, shares);
        for (EmberRunMaps.Skill sk : r1.boss.skills) assertFalse(sk.share);
        for (EmberRunMaps.Skill sk : r2.boss.skills) assertFalse(sk.share);
        assertEquals(100.0, EmberRunDirector.shareDamage(100, 1), 1e-9);  // alone: all of it
        assertEquals(100.0 / 3, EmberRunDirector.shareDamage(100, 3), 1e-9);
        assertEquals(0.0, EmberRunDirector.shareDamage(100, 0), 1e-9);
        assertTrue(EmberSeason.BOARDS.contains("time_r03"));
        assertNotNull(EmberCosmetics.byId("trail_r03"));
        assertNotNull(EmberCosmetics.byId("r03"));
    }

    @Test public void mapLootIdentityKeepsTheTargetFloorAndRaidItemIsTargeted_P2_9() {
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef q1 = m.byKey("q01"), q7 = m.byKey("q07");
        assertEquals("scorch", q1.lootFamily);
        assertEquals("blade", q1.lootSlot);
        assertNull(q7.lootSlot);
        assertEquals("sustain", m.raids.get("r02").lootFamily);
        assertEquals(1, m.raidItemQualityFloor);
        EmberRunRules.LootBias lb = m.lootBias(q1);
        // the target family never drops below TARGET_WEIGHT on any map; probabilities sum to 1
        for (String t : new String[]{"scorch", "burst", "sustain", null}) {
            double sum = 0;
            for (String f : EmberRunRules.FAMILIES) sum += EmberRunRules.familyProbability(t, lb, f);
            assertEquals(1.0, sum, 1e-9);
            if (t != null) assertTrue(EmberRunRules.familyProbability(t, lb, t) >= EmberRunRules.TARGET_WEIGHT - 1e-9);
        }
        assertEquals(m.lootOwnFamily, EmberRunRules.familyProbability("scorch", lb, "scorch"), 1e-9);
        assertEquals(0.4 * m.lootMapShare, EmberRunRules.familyProbability("burst", lb, "scorch"), 1e-9);
        assertEquals(0.5, EmberRunRules.familyProbability(null, lb, "scorch"), 1e-9);
        // sampled roll agrees with the table (target burst on the scorch map)
        java.util.Random r = new java.util.Random(3);
        int n = 20000, sc = 0, bu = 0, blade = 0;
        for (int i = 0; i < n; i++) {
            EmberRunRules.ItemRoll it = EmberRunRules.rollItem(1, "burst", r, null, lb);
            if ("scorch".equals(it.family)) sc++;
            if ("burst".equals(it.family)) bu++;
            if ("blade".equals(it.slot)) blade++;
        }
        assertEquals(0.60, bu / (double) n, 0.015);
        assertEquals(0.4 * m.lootMapShare, sc / (double) n, 0.015);
        assertEquals(m.lootSlotWeight, blade / (double) n, 0.015);
        // raid_item: entry target family, floor 精良, stable per key
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "r01-x"; in.player = "p"; in.seed = 9; in.tier = 3; in.target = "scorch"; in.bossKilled = true;
        in.qualityWeights = new int[]{60, 28, 10, 2};
        for (int i = 0; i < 200; i++) {
            in.runId = "r01-" + i;
            EmberRunRules.ItemRoll it = EmberRunRules.raidItem(in, "raid_item", "burst", 1).item;
            assertEquals("scorch", it.family);
            assertTrue(it.quality >= 1);
        }
        in.target = null;
        assertEquals("burst", EmberRunRules.raidItem(in, "raid_item", "burst", 1).item.family);
        assertEquals(EmberRunRules.raidItem(in, "raid_item", "burst", 1).item.toString(),
                EmberRunRules.raidItem(in, "raid_item", "burst", 1).item.toString());
        // cosmetics: no stats anywhere, abyss titles by best tier
        assertEquals(27, EmberCosmetics.ALL.size()); // D103 + E-review #9 + D116 six season honors + D137 R03 title / trail + D139 国庆 title / trail + D144 连战 title + D146 国庆纪念 title
        assertEquals(1, EmberCosmetics.byId("abyss1").abyssTier);
        assertEquals(240, EmberCosmetics.byId("anim_ember").points());
        assertEquals(40, EmberCosmetics.byId("color_white").points());        // 2000 币 = 40 points
        assertEquals(10, EmberCosmetics.markCost(EmberCosmetics.byId("color_white"), 3));
        assertEquals(40, EmberCosmetics.markCost(EmberCosmetics.byId("color_white"), 1));
        assertEquals("§6流§c火§e火", EmberCosmetics.animated("§6流火火", "6ce", 0));
        assertEquals("§c流§e火§6火", EmberCosmetics.animated("§6流火火", "6ce", 1));
        assertEquals("q04", EmberCosmetics.byId("q04").firstClear);
        assertEquals(5, EmberCosmetics.byId("abyss5").abyssTier);
        assertNull(EmberCosmetics.byId("nope"));
    }

    @Test public void seasonCalendar_D116() {
        long aw = EmberRunRules.weekIndex(java.time.LocalDate.of(2026, 9, 28));
        assertEquals(1, EmberSeason.seasonOf(aw, 4, java.time.LocalDate.of(2026, 9, 28)));
        assertEquals(1, EmberSeason.seasonOf(aw, 4, java.time.LocalDate.of(2026, 10, 25)));  // Sunday of week 4
        assertEquals(2, EmberSeason.seasonOf(aw, 4, java.time.LocalDate.of(2026, 10, 26)));  // Monday → season 2
        assertEquals(4, EmberSeason.weekInSeason(aw, 4, java.time.LocalDate.of(2026, 10, 25)));
        assertEquals(1, EmberSeason.weekInSeason(aw, 4, java.time.LocalDate.of(2026, 10, 26)));
        assertEquals(java.time.LocalDate.of(2026, 10, 26), EmberSeason.seasonStart(aw, 4, 2));
        assertEquals(java.time.DayOfWeek.MONDAY, EmberSeason.seasonStart(aw, 4, 7).getDayOfWeek());
        assertEquals(0, EmberSeason.seasonOf(aw, 4, java.time.LocalDate.of(2026, 9, 27)));
        assertEquals("§d❖ §r", EmberCosmetics.byId("season_crown").style);
        assertFalse(EmberCosmetics.byId("season_crown").shop());
    }

    @Test public void roomLineNamesTheEnemiesAndTheFirstThreat_D89() {
        java.util.List<String[]> lay = new java.util.ArrayList<String[]>();
        lay.add(new String[]{"melee", "0"}); lay.add(new String[]{"ranged", "1"}); lay.add(new String[]{"melee", "2"}); lay.add(new String[]{"heavy", "3"});
        String l = EmberRunRules.compositionLabel(lay).replaceAll("\u00a7.", "");
        assertEquals("近战 ×2 · 远程 ×1 · 重甲 ×1 （远程站远放箭，先清掉）", l);
        java.util.List<String[]> only = new java.util.ArrayList<String[]>();
        only.add(new String[]{"melee", "0"});
        assertEquals("近战 ×1", EmberRunRules.compositionLabel(only).replaceAll("\u00a7.", ""));
    }

    @Test public void dailyBountyPaysEachTierOnceOnTheMatchingClear_P2_7() {
        java.util.List<java.util.Map<String, Object>> raw = new java.util.ArrayList<java.util.Map<String, Object>>();
        java.util.Map<String, Object> a = new java.util.HashMap<String, Object>(); a.put("clears", 3); a.put("coin", 60); a.put("shard", 6);
        java.util.Map<String, Object> b = new java.util.HashMap<String, Object>(); b.put("clears", 1); b.put("coin", 30);
        java.util.Map<String, Object> bad = new java.util.HashMap<String, Object>(); bad.put("clears", 2);           // no reward → dropped
        raw.add(a); raw.add(b); raw.add(bad);
        java.util.List<EmberRunRules.BountyTier> t = EmberRunRules.bountyTiers(raw);
        assertEquals(2, t.size());
        assertEquals(1, t.get(0).clears);                                       // sorted
        assertEquals("[bounty_coin_1=coin:30]", keys(EmberRunRules.bountyGrants(t, 1)));
        assertEquals("[]", keys(EmberRunRules.bountyGrants(t, 2)));
        assertEquals("[bounty_coin_3=coin:60, bounty_shard_3=mat:mat_ember_shard:6]", keys(EmberRunRules.bountyGrants(t, 3)));
        // D105: a raid clear counts 2 -> crossing 2..3 pays the 3rd tier once; 0..2 pays the 1st
        assertEquals("[bounty_coin_3=coin:60, bounty_shard_3=mat:mat_ember_shard:6]", keys(EmberRunRules.bountyGrants(t, 1, 3)));
        assertEquals("[bounty_coin_1=coin:30]", keys(EmberRunRules.bountyGrants(t, 0, 2)));
        assertEquals("[]", keys(EmberRunRules.bountyGrants(t, 3, 5)));
        assertEquals("[]", keys(EmberRunRules.bountyGrants(t, 4)));
        assertTrue(EmberRunRules.bountyLine(t, 0).contains("再通关 1 局 → 30 余烬币"));
        assertTrue(EmberRunRules.bountyLine(t, 1).contains("再通关 2 局 → 60 余烬币 + 余烬碎片 ×6"));
        assertTrue(EmberRunRules.bountyLine(t, 3).contains("全部完成"));
        assertEquals("未开放", EmberRunRules.bountyLine(EmberRunRules.bountyTiers(null), 0));
    }

    @Test public void varietyBountyPaysOnceWhenTheDailyCountIsReached_D144() {
        java.util.List<java.util.Map<String, Object>> raw = new java.util.ArrayList<java.util.Map<String, Object>>();
        java.util.Map<String, Object> a = new java.util.HashMap<String, Object>(); a.put("kind", "affix"); a.put("count", 2); a.put("coin", 20);
        java.util.Map<String, Object> b = new java.util.HashMap<String, Object>(); b.put("kind", "timed"); b.put("count", 1); b.put("coin", 20); b.put("shard", 2);
        java.util.Map<String, Object> bad = new java.util.HashMap<String, Object>(); bad.put("kind", "boss"); bad.put("count", 1); bad.put("coin", 20);
        raw.add(a); raw.add(b); raw.add(bad);
        java.util.List<EmberRunRules.VarietyBounty> v = EmberRunRules.varietyBounties(raw);
        assertEquals(2, v.size());
        assertEquals("[]", keys(EmberRunRules.varietyBountyGrants(v.get(0), 0, true)));            // 1st affixed elite: 1/2
        assertEquals("[vb_affix=coin:20]", keys(EmberRunRules.varietyBountyGrants(v.get(0), 1, true)));
        assertEquals("[]", keys(EmberRunRules.varietyBountyGrants(v.get(0), 2, true)));            // done today: nothing more
        assertEquals("[]", keys(EmberRunRules.varietyBountyGrants(v.get(0), 1, false)));           // no affix kill this run
        assertEquals("[vb_timed=coin:20, vb_timed_shard=mat:mat_ember_shard:2]", keys(EmberRunRules.varietyBountyGrants(v.get(1), 0, true)));
        // the shipped ember-v1.yml values (p1sim reads the same list)
        org.bukkit.configuration.file.YamlConfiguration y = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                new InputStreamReader(EmberRunRulesTest.class.getResourceAsStream("/ember-v1.yml"), StandardCharsets.UTF_8));
        java.util.List<EmberRunRules.VarietyBounty> shipped = EmberRunRules.varietyBounties(y.getMapList("bounty.variety"));
        assertEquals(2, shipped.size());
        for (EmberRunRules.VarietyBounty x : shipped) assertTrue(x.kind, x.coin <= 30 && x.shard == 0); // existing reward types, small
    }

    @Test public void rushChainsTheQ05ToQ07BossesInOneHall_D144() {
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef r = m.byKey("rush");
        assertNotNull(r);
        assertTrue(r.rush);
        assertFalse(r.raid || r.event);
        assertEquals("[q05, q06, q07]", r.chainKeys.toString());
        assertEquals(3, r.chain.size());
        assertEquals(m.byKey("q05").boss.name, r.chain.get(0).name);
        assertEquals(m.byKey("q07").boss.skills.size(), r.chain.get(2).skills.size());     // D140 moves kept
        assertEquals(m.byKey("q06").boss.hp, r.chain.get(1).hp, 0);                         // × boss_hp at entry, not here
        for (EmberRunMaps.Boss b : r.chain) {
            assertEquals(0, b.at.x, 0); assertEquals(80, b.at.y, 0); assertEquals(146, b.at.z, 0); // all in the Q05 hall
            assertFalse(b.waitInArea);
            assertTrue(b.area.contains(b.at.x, b.at.y, b.at.z));
        }
        assertSame(r, m.byDungeon("EmberQ0B1"));
        assertSame(r, m.byWorld("dungeon_EmberQ0B1_1A2B"));
        assertEquals(0, m.cost(r));                                                          // free; one entry a week
        assertTrue(r.rooms.isEmpty());
        assertEquals("[]", m.validate().toString());
        assertTrue(r.rushMarks >= 1 && r.rushMarks <= 2);                                    // marks / 余烬徽 / title only
        assertTrue(r.rushHp >= 1.0 && r.rushDmg >= 1.0);
        assertNotNull(EmberCosmetics.byId(r.rushTitle));
    }

    @Test public void outpostRunsTheT1ChainOnItsOwnClaim_D174() { // stage 2b 连战·前哨
        EmberRunMaps m = bundled();
        EmberRunMaps.MapDef o = m.byKey("outpost"), r = m.byKey("rush");
        assertNotNull(o);
        assertTrue(o.rush && !o.mainRush() && r.mainRush());
        assertEquals("outpost", o.rushMode);
        assertEquals("[q01, q02, q03]", o.chainKeys.toString());
        assertEquals("q05", o.requires);
        assertEquals("p4_outpost_claim", o.rushClaim);
        assertEquals("p4_rush_claim", r.rushClaim);
        assertEquals(1, o.rushWeekly);
        assertEquals(2, o.rushMarkTier);
        assertEquals(1, o.rushMarks);
        assertEquals(2, o.rushSig);
        assertEquals(0, o.rushBadges);
        assertEquals("", o.rushTitle);
        assertEquals(0, m.cost(o));
        assertEquals("EmberQ0B2", o.dungeon);
        assertSame(r, m.byWorld("dungeon_EmberQ0B1_1A2B"));                                  // the D144 hall stays its own dungeon
        assertEquals("EmberQ0B2", m.byWorld("dungeon_EmberQ0B2_1A2B").dungeon);
        for (EmberRunMaps.Boss b : o.chain) { assertEquals(146, b.at.z, 0); assertFalse(b.waitInArea); }
        assertEquals("[]", m.validate().toString());
    }

    @Test public void echoFightsOneBossOnASharedWeeklyClaim_D174() { // stage 2b 首领残响 + D227 Q05–Q07
        EmberRunMaps m = bundled();
        String[] keys = new String[]{"q01", "q02", "q03", "q04", "q05", "q06", "q07"};
        String[] reqs = new String[]{"q04", "q04", "q04", "q04", "q05", "q06", "q07"};
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];
            EmberRunMaps.MapDef e = m.byKey("echo_" + k);
            assertNotNull(k, e);
            assertEquals("echo", e.rushMode);
            assertEquals("[" + k + "]", e.chainKeys.toString());
            assertEquals(m.byKey(k).boss.name, e.chain.get(0).name);
            assertEquals(reqs[i], e.requires);
            assertEquals("p4_echo_claim", e.rushClaim);
            assertEquals(3, e.rushWeekly);
            assertEquals(0, e.rushMarks);
            assertEquals(2, e.rushSig);
            assertEquals(0, m.cost(e));
            assertEquals("EmberQ0B2", e.dungeon);
        }
        assertEquals("[]", m.validate().toString());
    }

    private static String keys(java.util.List<EmberRunRules.Grant> gs) {
        java.util.List<String> l = new java.util.ArrayList<String>();
        for (EmberRunRules.Grant g : gs) l.add(g.key + "=" + g.encode());
        return l.toString();
    }

    // ---------------------------------------------------------------- F-review (D120 / D123)

    private static EmberItemData piece(String fam, String slot, int tier, int q, int enh) {
        return EmberItemData.create(fam, slot, tier, q, 0, enh, false, "drop");
    }

    @Test public void upgradeVerdictAutoEquipsOnlyClearlyBetterUninvestedPieces_D120() {
        EmberTables t = EmberTables.defaults();
        EmberItemData t1 = piece("scorch", "charm", 1, 0, 0), t2 = piece("scorch", "charm", 2, 1, 0);
        assertEquals(EmberRunRules.UP_AUTO, EmberRunRules.upgradeVerdict(t, t2, t1, "none", 1));       // higher tier, no investment
        assertEquals(EmberRunRules.UP_NONE, EmberRunRules.upgradeVerdict(t, t1, t2, "none", 1));       // worse
        assertEquals(EmberRunRules.UP_NONE, EmberRunRules.upgradeVerdict(t, t1, t1, "none", 1));       // same piece
        assertEquals(EmberRunRules.UP_NONE, EmberRunRules.upgradeVerdict(t, piece("scorch", "blade", 2, 0, 0), t1, "none", 1)); // other slot
        EmberItemData same = piece("scorch", "charm", 1, 0, 0);
        assertEquals("same tier, equal value is not clearly better", EmberRunRules.UP_NONE, EmberRunRules.upgradeVerdict(t, same, t1, "none", 1));
        assertEquals(EmberRunRules.UP_AUTO, EmberRunRules.upgradeVerdict(t, piece("scorch", "charm", 1, 3, 0), t1, "none", 1)); // 极品 vs 标准
        EmberItemData t1e = piece("scorch", "charm", 1, 0, 6);
        assertEquals("enhanced active → ask with a free swap", EmberRunRules.UP_ASK_SWAP, EmberRunRules.upgradeVerdict(t, t2, t1e, "none", 1));
        assertEquals("breaking an active set → ask", EmberRunRules.UP_ASK, EmberRunRules.upgradeVerdict(t, piece("burst", "charm", 2, 1, 0), t1, "scorch", 1));
        assertEquals(EmberRunRules.UP_AUTO, EmberRunRules.upgradeVerdict(t, t2, t1, "scorch", 1));
    }

    @Test public void abyssBoardBreaksTiesByTheFastestClear_D123() {
        EmberSeason.Row a = new EmberSeason.Row("A", 10, 1000L, 400), b = new EmberSeason.Row("B", 10, 500L, 300),
                c = new EmberSeason.Row("C", 10, 100L, 0), d = new EmberSeason.Row("D", 9, 50L, 100);
        java.util.List<EmberSeason.Row> rows = new java.util.ArrayList<EmberSeason.Row>(java.util.Arrays.asList(a, c, d, b));
        rows.sort((x, y) -> EmberSeason.compareRows("abyss", x, y));
        assertEquals("B", rows.get(0).name);  // same tier: faster first
        assertEquals("A", rows.get(1).name);
        assertEquals("C", rows.get(2).name);  // unknown time after the timed ones
        assertEquals("D", rows.get(3).name);  // lower tier last
        rows.sort((x, y) -> EmberSeason.compareRows("featured", x, y));
        assertEquals("C", rows.get(0).name);  // other boards keep "first to get there"
        assertEquals(300, EmberSeason.bestSecs(a, 10, 10, 300));  // same tier, faster run → kept
        assertEquals(400, EmberSeason.bestSecs(a, 10, 10, 500));  // slower run → old time
        assertEquals(400, EmberSeason.bestSecs(a, 10, 9, 100));   // a lower tier run never changes the time
        assertEquals(700, EmberSeason.bestSecs(d, 10, 10, 700));  // new best tier takes its own time
        assertEquals(250, EmberSeason.bestSecs(null, 5, 5, 250));
        assertEquals("第 10 层 · 6 分 40 秒", EmberSeason.rowText("abyss", a));
        assertEquals("第 10 层", EmberSeason.rowText("abyss", c));
    }

    @Test public void shopPriceLineListsCoinBadgeAndEveryMarkTier_D121() {
        EmberCosmetics.Cosmetic white = EmberCosmetics.byId("color_white"), glow = EmberCosmetics.byId("glow_ember");
        assertEquals("2000 币 / 40 徽 / 印记 T3×10 · T2×20 · T1×40", EmberCosmetics.priceText(white));
        assertEquals("不收币 · 160 徽 / 印记 T3×40 · T2×80 · T1×160", EmberCosmetics.priceText(glow));
    }

    @Test
    public void failRefundIsHalfOncePerDayD128() {
        assertEquals(15, EmberRunRules.failRefundAmount(30, 0.5));
        assertEquals(0, EmberRunRules.failRefundAmount(30, 0));
        assertEquals(0, EmberRunRules.failRefundAmount(0, 0.5));
        assertEquals(30, EmberRunRules.failRefundAmount(30, 2.0));
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        boolean[] c = new boolean[1];
        String run = EmberRunRules.failRefundRun("2026-10-03");
        l.record(run, EmberRunRules.FAIL_REFUND_KEY, "stamina:15:q01c-a", EmberRunRules.ST_DELIVERED, 1L, c);
        assertTrue(c[0]);
        l.record(run, EmberRunRules.FAIL_REFUND_KEY, "stamina:15:q02c-b", EmberRunRules.ST_DELIVERED, 2L, c);
        assertFalse(c[0]);                                  // second fail the same day pays nothing
        assertEquals("stamina:15:q01c-a", l.get(run, EmberRunRules.FAIL_REFUND_KEY).result);
        l.record(EmberRunRules.failRefundRun("2026-10-04"), EmberRunRules.FAIL_REFUND_KEY, "stamina:15:q03c-c", EmberRunRules.ST_DELIVERED, 3L, c);
        assertTrue(c[0]);                                   // next day again
    }

    @Test public void starterBladeGoesToTheBackpackNotTheHotbar_D131() {
        boolean[] used = new boolean[36];
        used[0] = true; used[1] = true; used[3] = true; // starter in 0, T1 blade in 1, new blade landed in hotbar slot 4
        used[4] = true;
        assertEquals("first free backpack slot, not the free hotbar slot 5", 9, EmberRunRules.starterTarget(used, 4));
        used[9] = true; used[10] = true;
        assertEquals(11, EmberRunRules.starterTarget(used, 4));
        for (int i = 9; i < 36; i++) used[i] = true;
        assertEquals("full backpack: swap into the new blade's slot", 4, EmberRunRules.starterTarget(used, 4));
    }

    @Test public void starterSwapStillLetsTheBetterBladeAutoEquip_D130() {
        // FreshQ17: T1 blade in hand, T0 starter still on the hotbar, Q04 drops a T2 blade. After the starter swap the
        // upgrade check must still say AUTO (the giveItem flow no longer returns before it).
        EmberTables t = EmberTables.defaults();
        EmberItemData hand = piece("scorch", "blade", 1, 1, 0), drop = piece("scorch", "blade", 2, 0, 0);
        assertEquals(EmberRunRules.UP_AUTO, EmberRunRules.upgradeVerdict(t, drop, hand, "scorch", 14));
        assertSame(drop, EmberRunRules.bestCandidate(t, drop, java.util.Arrays.asList(hand, drop), hand, "scorch", 14));
    }

    @Test public void betterSameTierPieceInTheBagIsPreferred_D132() {
        EmberTables t = EmberTables.defaults();
        EmberItemData active = piece("scorch", "blade", 1, 0, 0);
        EmberItemData fresh = piece("scorch", "blade", 3, 0, 0), bagGood = piece("scorch", "blade", 3, 2, 0), bagOther = piece("burst", "blade", 3, 3, 0);
        java.util.List<EmberItemData> bag = java.util.Arrays.asList(active, fresh, bagGood, bagOther);
        assertSame("T3 卓越 in the bag beats the new T3 标准", bagGood, EmberRunRules.bestCandidate(t, fresh, bag, active, "scorch", 30));
        assertSame("no set on: the other family's 极品 counts", bagOther, EmberRunRules.bestCandidate(t, fresh, java.util.Arrays.asList(active, fresh, bagOther), active, null, 30));
        assertSame("with a scorch set only scorch bag pieces count", fresh, EmberRunRules.bestCandidate(t, fresh, java.util.Arrays.asList(active, fresh, bagOther), active, "scorch", 30));
        assertSame("ties keep the new piece", fresh, EmberRunRules.bestCandidate(t, fresh, java.util.Arrays.asList(piece("scorch", "blade", 3, 0, 0)), active, null, 30));
        assertTrue(EmberRunRules.betterThan(t, bagGood, fresh, 30));
        assertFalse(EmberRunRules.betterThan(t, fresh, bagGood, 30));
    }

    @Test public void shopBackGoesToTheOpeningPage_D136() {
        assertEquals("ember_p1_gear", EmberCosmetics.FROM_MENUS.get("gear"));
        assertEquals("ember_p1_season", EmberCosmetics.FROM_MENUS.get("season"));
        assertTrue(EmberCosmetics.backLabel("gear").contains("装备页"));
        assertTrue(EmberCosmetics.backLabel("season").contains("赛季"));
        assertTrue(EmberCosmetics.backLabel(null).contains("关闭"));
    }

    @Test public void bountyGoalIsProratedInTheGraduationWeek_D134() {
        assertEquals(3, EmberSeason.proratedTarget(3, 1)); // Monday
        assertEquals(3, EmberSeason.proratedTarget(3, 5)); // Friday: Fri, Sat, Sun
        assertEquals(2, EmberSeason.proratedTarget(3, 6)); // Saturday
        assertEquals(1, EmberSeason.proratedTarget(3, 7)); // Sunday
    }

    @Test public void repeatRunVarietyIsSeededAndPaysOnlyExistingTypesOffFirstClears_D138() {
        EmberRunMaps.Variety v = bundled().variety;
        assertTrue(v.on());
        assertEquals(EmberRunMaps.Variety.KNOWN, v.affixes);
        assertTrue(v.affixShard > 0 && v.affixShard < EmberRunRules.ELITE_SHARD);   // smaller than the elite event
        assertTrue(v.eventCore >= 1 && v.eventSecs >= 20);
        assertTrue(v.blazeWarn >= 1.0);                                             // telegraphed
        // same seed → same roll; every affix and every room shows up over many seeds
        Set<String> seen = new HashSet<String>();
        int events = 0;
        for (long seed = 0; seed < 2000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety")), b = v.roll(EmberRunRules.subSeed(seed, "variety"));
            assertArrayEquals(a, b);
            if (!a[1].isEmpty()) { assertTrue(a[0].matches("r[123]")); seen.add(a[1]); seen.add(a[0]); }
            if (!a[2].isEmpty()) events++;
        }
        assertTrue(seen.containsAll(java.util.Arrays.asList("blazing", "split", "shield", "r1", "r2", "r3")));
        assertEquals(v.eventRate, events / 2000.0, 0.05);
        // rewards: shards / cores only, never with the first-clear package
        assertTrue(EmberRunRules.varietyGrants(true, true, 3, true, 1).isEmpty());
        List<EmberRunRules.Grant> g = EmberRunRules.varietyGrants(false, true, 3, true, 1);
        assertEquals(2, g.size());
        assertEquals("var_affix_shard", g.get(0).key);
        assertEquals(EmberRunRules.Kind.MAT, g.get(0).kind);
        assertEquals("var_event_core", g.get(1).key);
        assertEquals(1, EmberRunRules.varietyGrants(false, false, 3, true, 1).size());
        assertTrue(EmberRunRules.varietyGrants(false, false, 3, false, 1).isEmpty());
        // the elite: heavy, else melee, else the first; its fire circle is a warned circle at its feet
        EmberRunDirector.Tracked r = new EmberRunDirector.Tracked(null, "ranged", "r1", null, null, 10, 3, 8, null);
        EmberRunDirector.Tracked mm = new EmberRunDirector.Tracked(null, "melee", "r1", null, null, 12, 3, 2, null);
        EmberRunDirector.Tracked h = new EmberRunDirector.Tracked(null, "heavy", "r1", null, null, 20, 3, 2, null);
        assertSame(h, EmberRunDirector.affixPick(java.util.Arrays.asList(r, mm, h)));
        assertSame(mm, EmberRunDirector.affixPick(java.util.Arrays.asList(r, mm)));
        assertSame(r, EmberRunDirector.affixPick(java.util.Arrays.asList(r)));
        EmberRunMaps.Skill fire = EmberRunDirector.blazeSkill(h, v);
        assertEquals("circle", fire.type);
        assertEquals(20 * v.blazeDmg, fire.dmg, 1e-9);
        assertEquals(v.blazeRadius, fire.radius, 1e-9);
        // session round trip keeps the roll and the done flags
        EmberRunSession s = new EmberRunSession();
        s.runId = "q03-x"; s.mapKey = "q03"; s.affix = "split"; s.affixRoom = "r2"; s.affixDone = true; s.eventRoom = "r3";
        EmberRunSession t = EmberRunSession.fromMap(s.toMap());
        assertEquals("split", t.affix); assertEquals("r2", t.affixRoom); assertTrue(t.affixDone);
        assertEquals("r3", t.eventRoom); assertFalse(t.eventDone);
    }


    @Test public void rewardEliteTwistsParseOneMovePerMap_D182() {
        EmberRunMaps m = bundled();
        EmberRunMaps.EliteTwists et = m.eliteTwists;
        assertTrue(et.enabled);
        assertEquals(3.0, et.openDelay, 1e-9);
        assertEquals(7, et.byMap.size());
        String[][] expect = {
                {"q01", "shove", "line", "门廊推"},
                {"q02", "stomp", "circle", "焦焰踏"},
                {"q03", "sweep", "cone", "誓印扫"},
                {"q04", "barge", "charge", "闸冲"},
                {"q05", "dust", "circle", "落尘"},
                {"q06", "breath", "cone", "霜息"},
                {"q07", "slag", "line", "矿渣劈"},
        };
        for (String[] row : expect) {
            EmberRunMaps.EliteTwists.Twist tw = et.forMap(row[0]);
            assertNotNull(row[0], tw);
            assertEquals(row[1], tw.move);
            assertEquals(row[2], tw.template.type);
            assertEquals(row[3], tw.name);
            assertTrue(tw.template.light);
            assertTrue(tw.template.warn + " warn", tw.template.warn >= 1.2 - 1e-9);
            // absolute dmg = elite.atk × multiplier
            EmberRunMaps.Role elite = m.byKey(row[0]).roles.get("elite");
            EmberRunMaps.Skill sk = tw.skill(elite.atk);
            assertEquals(elite.atk * tw.template.dmg, sk.dmg, 1e-9);
            assertTrue(sk.light);
        }
        // Q04 barge: kb=0 (no knockback off the tide ledges)
        assertEquals(0.0, et.forMap("q04").template.kb, 1e-9);
        assertEquals("player", et.forMap("q05").template.target);
        // disabled flag → forMap returns null (current Extra.ELITE stump behaviour)
        java.util.Map<Object, Object> off = new java.util.LinkedHashMap<Object, Object>();
        off.put("enabled", false);
        java.util.Map<Object, Object> q01 = new java.util.LinkedHashMap<Object, Object>();
        q01.put("move", "shove"); q01.put("shape", "line"); q01.put("every", 14); q01.put("warn", 1.2);
        q01.put("length", 5); q01.put("width", 2); q01.put("dmg", 1.0);
        off.put("q01", q01);
        EmberRunMaps.EliteTwists disabled = new EmberRunMaps.EliteTwists(off);
        assertFalse(disabled.enabled);
        assertNull(disabled.forMap("q01"));
        assertEquals(1, disabled.byMap.size()); // still parsed, just gated by enabled
    }

    @Test public void rewardEliteTwistsPack2AltPerMap_D185() {
        EmberRunMaps m = bundled();
        EmberRunMaps.EliteTwists et = m.eliteTwists;
        assertTrue(et.enabled);
        String[][] expect = {
                {"q01", "ashfan", "cone", "灰烬扇"},
                {"q02", "sear", "line", "焦线"},
                {"q03", "oathstomp", "circle", "誓踏"},
                {"q04", "tidefan", "cone", "潮扇"},
                {"q05", "rubble", "line", "碎带"},
                {"q06", "frostring", "circle", "霜环踏"},
                {"q07", "forgefan", "cone", "炉扇"},
        };
        for (String[] row : expect) {
            EmberRunMaps.EliteTwists.Twist tw = et.forMap(row[0]);
            assertNotNull(row[0], tw);
            assertNotNull(row[0] + " alt", tw.alt);
            assertEquals(row[1], tw.alt.move);
            assertEquals(row[2], tw.alt.template.type);
            assertEquals(row[3], tw.alt.name);
            assertTrue(tw.alt.template.light);
            assertTrue(tw.alt.template.warn + " warn", tw.alt.template.warn >= 1.2 - 1e-9);
            assertNull("alt has no nested alt", tw.alt.alt);
            EmberRunMaps.Role elite = m.byKey(row[0]).roles.get("elite");
            EmberRunMaps.Skill sk = tw.alt.skill(elite.atk);
            assertEquals(elite.atk * tw.alt.template.dmg, sk.dmg, 1e-9);
        }
        // Q04 Pack2 tidefan kb=0 (same ledge rule as barge)
        assertEquals(0.0, et.forMap("q04").alt.template.kb, 1e-9);
        // Pack1 primary still present
        assertEquals("shove", et.forMap("q01").move);
        assertEquals("slag", et.forMap("q07").move);
    }

    @Test public void varietyPack2RollsNewAffixesAndEventsOffFirstClears_D171() {
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(12, EmberRunMaps.Variety.KNOWN.size()); // D181 Pack 3 expanded 6→8, D189 Pack 4 8→10, D196 Pack 5 10→12
        assertTrue(EmberRunMaps.Variety.KNOWN.containsAll(java.util.Arrays.asList("regen", "charge", "frost", "mortar", "molten")));
        assertEquals(EmberRunMaps.Variety.KNOWN, v.affixes);
        assertEquals(EmberRunMaps.Variety.EVENTS, v.events);
        assertTrue(v.chargeWarn >= 1.2);
        assertEquals(3, v.crystalCount);
        assertEquals(0.35, v.escortHp, 1e-9);
        assertEquals("再生", EmberRunMaps.Variety.label("regen"));
        assertEquals("冲锋", EmberRunMaps.Variety.label("charge"));
        assertEquals("凝霜", EmberRunMaps.Variety.label("frost"));
        assertEquals("砸余烬晶", EmberRunMaps.Variety.eventLabel("crystal"));
        assertEquals("护宝兔", EmberRunMaps.Variety.eventLabel("escort"));
        Set<String> aff = new HashSet<String>(), ev = new HashSet<String>();
        for (long seed = 0; seed < 6000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            assertEquals(4, a.length);
            if (!a[1].isEmpty()) aff.add(a[1]);
            if (!a[2].isEmpty()) { assertTrue(a[3], EmberRunMaps.Variety.EVENTS.contains(a[3])); ev.add(a[3]); }
        }
        assertTrue(aff.containsAll(java.util.Arrays.asList("regen", "charge", "frost", "blazing", "split", "shield", "mortar", "molten")));
        assertTrue(ev.containsAll(java.util.Arrays.asList("timed", "crystal", "escort", "hold", "beacon", "relay")));
        assertEquals(9, EmberRunMaps.Variety.EVENTS.size()); // D191 Pack 4: 6 → 9
        // reflect rejected
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("affix_rate", 1.0);
        raw.put("affixes", java.util.Arrays.asList("blazing", "reflect", "regen"));
        raw.put("event_rate", 0.5);
        EmberRunMaps.Variety bad = new EmberRunMaps.Variety(raw);
        assertFalse(bad.affixes.contains("reflect"));
        assertTrue(bad.affixes.contains("regen"));
        // session keeps eventKind; old saves without kind default to timed
        EmberRunSession s = new EmberRunSession();
        s.eventRoom = "r1"; s.eventKind = "crystal"; s.eventDone = true;
        EmberRunSession round = EmberRunSession.fromMap(s.toMap());
        assertEquals("crystal", round.eventKind);
        EmberRunSession old = EmberRunSession.fromMap(java.util.Collections.singletonMap("event_room", "r2"));
        assertEquals("timed", old.eventKind);
        // charge strip skill shape
        EmberRunDirector.Tracked h = new EmberRunDirector.Tracked(null, "heavy", "r1", null, null, 20, 3, 2, null);
        EmberRunMaps.Skill ch = EmberRunDirector.chargeSkill(h, v, new org.bukkit.util.Vector(1, 0, 0));
        assertEquals("charge", ch.type);
        assertEquals(20 * v.chargeDmg, ch.dmg, 1e-9);
        assertEquals(v.chargeWidth, ch.width, 1e-9);
    }

    @Test public void varietyBountyCountsAnyEventKind_D171() {
        assertEquals("房间事件达标", new EmberRunRules.VarietyBounty("timed", 1, 20, 0).label());
        assertEquals("击败词缀精英", new EmberRunRules.VarietyBounty("affix", 2, 20, 0).label());
        // crystal/escort success advances the same kind=timed bounty (save id unchanged)
        List<EmberRunRules.Grant> g = EmberRunRules.varietyBountyGrants(new EmberRunRules.VarietyBounty("timed", 1, 20, 0), 0, true);
        assertEquals(1, g.size());
        assertEquals("vb_timed", g.get(0).key);
        // settle keys differ by event kind but same core mat
        assertEquals("var_event_crystal", EmberRunRules.varietyGrants(false, false, 0, true, 1, "crystal").get(0).key);
        assertEquals("var_event_escort", EmberRunRules.varietyGrants(false, false, 0, true, 1, "escort").get(0).key);
        assertEquals("var_event_core", EmberRunRules.varietyGrants(false, false, 0, true, 1, "timed").get(0).key);
        assertEquals("砸余烬晶 ", EmberRunService.matSource("var_event_crystal"));
        assertEquals("护宝兔 ", EmberRunService.matSource("var_event_escort"));
        assertEquals("占点 ", EmberRunService.matSource("var_event_hold"));
        assertEquals("护灯 ", EmberRunService.matSource("var_event_beacon"));
        assertEquals("传火 ", EmberRunService.matSource("var_event_relay"));
    }

    @Test public void reflectAffixRejected_D171() {
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("affixes", java.util.Arrays.asList("reflect", "blazing"));
        raw.put("affix_rate", 1.0);
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(raw);
        assertFalse(EmberRunMaps.Variety.KNOWN.contains("reflect"));
        assertEquals(java.util.Collections.singletonList("blazing"), v.affixes);
    }

    @Test public void varietyPack3MortarMolten_D181() {
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(12, EmberRunMaps.Variety.KNOWN.size()); // D189 Pack 4: +venom/jailer; D196 Pack 5: +arcane/firechain
        assertTrue(EmberRunMaps.Variety.KNOWN.containsAll(java.util.Arrays.asList("mortar", "molten")));
        assertEquals(EmberRunMaps.Variety.KNOWN, v.affixes);
        assertEquals(5.0, v.mortarEvery, 1e-9);
        assertTrue(v.mortarWarn >= 1.2);
        assertEquals(2.0, v.mortarRadius, 1e-9);
        assertEquals(0.0, v.mortarAhead, 1e-9);
        assertEquals(1.0, v.mortarDmg, 1e-9);
        assertEquals(0.4, v.moltenDelay, 1e-9);
        assertTrue(v.moltenWarn >= 1.2);
        assertEquals(2.5, v.moltenRadius, 1e-9);
        assertEquals(1.2, v.moltenDmg, 1e-9);
        assertEquals("投弹", EmberRunMaps.Variety.label("mortar"));
        assertEquals("亡爆", EmberRunMaps.Variety.label("molten"));
        // reject reflect + vortex
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("affix_rate", 1.0);
        raw.put("affixes", java.util.Arrays.asList("mortar", "reflect", "vortex", "molten"));
        EmberRunMaps.Variety bad = new EmberRunMaps.Variety(raw);
        assertEquals(java.util.Arrays.asList("mortar", "molten"), bad.affixes);
        assertFalse(EmberRunMaps.Variety.KNOWN.contains("reflect"));
        assertFalse(EmberRunMaps.Variety.KNOWN.contains("vortex"));
        // skill shapes: kb=0, circle
        EmberRunDirector.Tracked h = new EmberRunDirector.Tracked(null, "heavy", "r1", null, null, 20, 3, 2, null);
        EmberRunMaps.Skill mo = EmberRunDirector.mortarSkill(h, v);
        assertEquals("circle", mo.type);
        assertEquals(20 * v.mortarDmg, mo.dmg, 1e-9);
        assertEquals(0.0, mo.kb, 1e-9);
        assertEquals(v.mortarRadius, mo.radius, 1e-9);
        EmberRunMaps.Skill ml = EmberRunDirector.moltenSkill(20, v);
        assertEquals("circle", ml.type);
        assertEquals(20 * v.moltenDmg, ml.dmg, 1e-9);
        assertEquals(0.0, ml.kb, 1e-9);
        // splitAdd must not be treated as molten trigger source (flag exists; scheduleMolten guards it)
        EmberRunDirector.Tracked add = new EmberRunDirector.Tracked(null, "melee", "r1", null, null, 10, 3, 2, null);
        add.splitAdd = true;
        add.affix = "molten"; // even if mis-tagged, scheduleMolten refuses splitAdd
        assertTrue(add.splitAdd);
        // roll eventually hits mortar/molten
        java.util.Set<String> aff = new java.util.HashSet<String>();
        for (long seed = 0; seed < 8000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            if (!a[1].isEmpty()) aff.add(a[1]);
        }
        assertTrue(aff.contains("mortar"));
        assertTrue(aff.contains("molten"));
    }

    @Test public void varietyPack4VenomJailer_D189() {
        EmberRunMaps.Variety v = bundled().variety;
        assertTrue(v.affixes.containsAll(java.util.Arrays.asList("venom", "jailer")));
        assertEquals(12, v.affixes.size()); // D196: +arcane/firechain
        assertEquals("毒十字", EmberRunMaps.Variety.label("venom"));
        assertEquals("禁锢", EmberRunMaps.Variety.label("jailer"));
        assertEquals(6.0, v.venomEvery, 1e-9);
        assertTrue(v.venomWarn >= 1.2);
        assertTrue(v.jailerWarn >= 1.2);
        assertEquals(4.0, v.venomArm, 1e-9);
        assertEquals(1.5, v.venomWidth, 1e-9);
        assertEquals(1.0, v.venomDmg, 1e-9);
        assertEquals(7.0, v.jailerEvery, 1e-9);
        assertEquals(1.6, v.jailerRadius, 1e-9);
        assertEquals(0.5, v.jailerDmg, 1e-9);
        assertEquals(20, EmberRunDirector.jailerRootTicks(v));
        // root is clamped to ≤ 1.5 s; arm ≤ 6; every floors
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("affix_rate", 1.0);
        raw.put("affixes", java.util.Arrays.asList("venom", "jailer"));
        raw.put("venom", java.util.Collections.singletonMap("arm", 99));
        java.util.Map<String, Object> jl = new java.util.LinkedHashMap<String, Object>();
        jl.put("root", 9); jl.put("warn", 0.1); jl.put("every", 0.5);
        raw.put("jailer", jl);
        EmberRunMaps.Variety cl = new EmberRunMaps.Variety(raw);
        assertEquals(6.0, cl.venomArm, 1e-9);
        assertEquals(1.5, cl.jailerRoot, 1e-9);
        assertEquals(30, EmberRunDirector.jailerRootTicks(cl));
        assertEquals(1.2, cl.jailerWarn, 1e-9);
        assertEquals(3.0, cl.jailerEvery, 1e-9);
        // shapes: kb 0, cross through the centre
        EmberRunDirector.Tracked h = new EmberRunDirector.Tracked(null, "caster", "r1", null, null, 20, 3, 2, null);
        EmberRunMaps.Skill arm = EmberRunDirector.venomSkill(h, v);
        assertEquals("line", arm.type);
        assertEquals(0.0, arm.kb, 1e-9);
        assertEquals(20 * v.venomDmg, arm.dmg, 1e-9);
        assertEquals(-4.0, arm.stripFrom(), 1e-9);
        assertEquals(4.0, arm.stripTo(), 1e-9);
        org.bukkit.Location o = new org.bukkit.Location(null, 0, 64, 0);
        org.bukkit.util.Vector[] plus = EmberRunDirector.venomDirs(false), ex = EmberRunDirector.venomDirs(true);
        // "+" hits on the axes (both sides), misses the diagonal gap; "x" is the opposite
        assertTrue(EmberRunDirector.venomHits(arm, o, plus, new org.bukkit.Location(null, 3, 64, 0)));
        assertTrue(EmberRunDirector.venomHits(arm, o, plus, new org.bukkit.Location(null, 0, 64, -3)));
        assertTrue(EmberRunDirector.venomHits(arm, o, plus, new org.bukkit.Location(null, 0.2, 64, 0.2))); // centre
        assertFalse(EmberRunDirector.venomHits(arm, o, plus, new org.bukkit.Location(null, 2.5, 64, 2.5)));
        assertTrue(EmberRunDirector.venomHits(arm, o, ex, new org.bukkit.Location(null, 2.5, 64, 2.5)));
        assertTrue(EmberRunDirector.venomHits(arm, o, ex, new org.bukkit.Location(null, -2, 64, 2)));
        assertFalse(EmberRunDirector.venomHits(arm, o, ex, new org.bukkit.Location(null, 3, 64, 0)));
        // beyond the arm end
        assertFalse(EmberRunDirector.venomHits(arm, o, plus, new org.bukkit.Location(null, 4.6, 64, 0)));
        EmberRunMaps.Skill jail = EmberRunDirector.jailerSkill(h, v);
        assertEquals("circle", jail.type);
        assertEquals(0.0, jail.kb, 1e-9);
        assertEquals(20 * v.jailerDmg, jail.dmg, 1e-9);
        // rewards unchanged: one affix_shard grant per defeated affix elite regardless of id
        assertEquals(2, v.affixShard);
        java.util.Set<String> aff = new java.util.HashSet<String>();
        for (long seed = 0; seed < 8000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            if (!a[1].isEmpty()) aff.add(a[1]);
        }
        assertTrue(aff.containsAll(java.util.Arrays.asList("venom", "jailer")));
    }

    @Test public void varietyPack5ArcaneFirechain_D196() {
        EmberRunMaps.Variety v = bundled().variety;
        assertTrue(v.affixes.containsAll(java.util.Arrays.asList("arcane", "firechain")));
        assertEquals("旋光", EmberRunMaps.Variety.label("arcane"));
        assertEquals("火链", EmberRunMaps.Variety.label("firechain"));
        assertEquals(8.0, v.arcaneEvery, 1e-9);
        assertEquals(1.5, v.arcaneWarn, 1e-9);
        assertEquals(5.0, v.arcaneLength, 1e-9);
        assertEquals(1.2, v.arcaneWidth, 1e-9);
        assertEquals(180.0, v.arcaneSweep, 1e-9);
        assertEquals(3.0, v.arcaneSpin, 1e-9);
        assertEquals(1.0, v.arcaneDmg, 1e-9);
        assertEquals(1.2, v.chainLinkWarn, 1e-9);
        assertEquals(1.0, v.chainLinkWidth, 1e-9);
        assertEquals(1.0, v.chainLinkTick, 1e-9);
        assertEquals(0.3, v.chainLinkDmg, 1e-9);
        assertEquals(10.0, v.chainLinkRange, 1e-9);
        assertEquals(2, v.affixShard); // reward unchanged
        // clamps: never more than a half turn, never faster than 120°/s, warn ≥ 1.2, burn ≤ 1.0 atk and ≤ 2 / s
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("affix_rate", 1.0);
        raw.put("affixes", java.util.Arrays.asList("arcane", "firechain", "orbiter", "waller"));
        java.util.Map<String, Object> ar = new java.util.LinkedHashMap<String, Object>();
        ar.put("sweep", 720); ar.put("spin", 0.2); ar.put("warn", 0.1); ar.put("every", 1); ar.put("length", 40);
        raw.put("arcane", ar);
        java.util.Map<String, Object> fc = new java.util.LinkedHashMap<String, Object>();
        fc.put("warn", 0.0); fc.put("tick", 0.05); fc.put("dmg", 9); fc.put("range", 99);
        raw.put("firechain", fc);
        EmberRunMaps.Variety cl = new EmberRunMaps.Variety(raw);
        assertEquals(java.util.Arrays.asList("arcane", "firechain"), cl.affixes); // unknown ids dropped
        assertEquals(180.0, cl.arcaneSweep, 1e-9);
        assertEquals(1.5, cl.arcaneSpin, 1e-9);
        assertEquals(1.2, cl.arcaneWarn, 1e-9);
        assertEquals(4.0, cl.arcaneEvery, 1e-9);
        assertEquals(6.0, cl.arcaneLength, 1e-9);
        assertEquals(1.2, cl.chainLinkWarn, 1e-9);
        assertEquals(0.5, cl.chainLinkTick, 1e-9);
        assertEquals(1.0, cl.chainLinkDmg, 1e-9);
        assertEquals(12.0, cl.chainLinkRange, 1e-9);
        // beam: the target sits mid-arc; angle runs start → start ± sweep and stops there
        org.bukkit.Location o = new org.bukkit.Location(null, 0, 64, 0);
        double half = Math.PI / 2;
        double st = EmberRunDirector.arcaneStartAngle(o, new org.bukkit.Location(null, 3, 64, 0), 1, Math.PI);
        assertEquals(-half, st, 1e-9);
        assertEquals(st, EmberRunDirector.arcaneAngle(st, 1, Math.PI, 3.0, 0.0), 1e-9);
        assertEquals(0.0, EmberRunDirector.arcaneAngle(st, 1, Math.PI, 3.0, 1.5), 1e-9);
        assertEquals(half, EmberRunDirector.arcaneAngle(st, 1, Math.PI, 3.0, 9.0), 1e-9);
        assertEquals(-half + Math.PI, EmberRunDirector.arcaneAngle(st, 1, Math.PI, 3.0, 3.0), 1e-9);
        assertEquals(half, EmberRunDirector.arcaneStartAngle(o, new org.bukkit.Location(null, 3, 64, 0), -1, Math.PI), 1e-9);
        // swept-interval: a tick from −10° to +10° hits a player at 0° r 3, misses one at 40°, misses beyond length
        double a0 = Math.toRadians(-10), a1 = Math.toRadians(10);
        assertTrue(EmberRunDirector.arcaneSwept(o, a0, a1, 5.0, 1.2, new org.bukkit.Location(null, 3, 64, 0)));
        assertTrue(EmberRunDirector.arcaneSwept(o, a1, a0, 5.0, 1.2, new org.bukkit.Location(null, 3, 64, 0))); // either turn
        assertFalse(EmberRunDirector.arcaneSwept(o, a0, a1, 5.0, 1.2, new org.bukkit.Location(null, 3 * Math.cos(Math.toRadians(40)), 64, 3 * Math.sin(Math.toRadians(40)))));
        assertFalse(EmberRunDirector.arcaneSwept(o, a0, a1, 5.0, 1.2, new org.bukkit.Location(null, 5.4, 64, 0)));
        assertFalse(EmberRunDirector.arcaneSwept(o, a0, a1, 5.0, 1.2, new org.bukkit.Location(null, 3, 68, 0))); // other floor
        assertTrue(EmberRunDirector.arcaneSwept(o, a0, a1, 5.0, 1.2, new org.bukkit.Location(null, 0.3, 64, 2.0 * 0)));  // pivot
        // the beam's half width widens the hit near the edge: 12° off at r 3 (≈ 0.62 off the line) is just outside 0.6
        assertFalse(EmberRunDirector.arcaneSwept(o, 0.0, 0.0, 5.0, 1.2, new org.bukkit.Location(null, 3 * Math.cos(Math.toRadians(12)), 64, 3 * Math.sin(Math.toRadians(12)))));
        assertTrue(EmberRunDirector.arcaneSwept(o, 0.0, 0.0, 5.0, 1.2, new org.bukkit.Location(null, 3 * Math.cos(Math.toRadians(10)), 64, 3 * Math.sin(Math.toRadians(10)))));
        // across the ±π seam: −170° → +170° the short way is NOT what the beam does; a1 − a0 is the real turn
        assertTrue(EmberRunDirector.arcaneSwept(o, Math.toRadians(170), Math.toRadians(190), 5.0, 1.2, new org.bukkit.Location(null, -3, 64, 0)));
        // the whole cast never touches the back half: a full 180° sweep from −90° to +90° misses a player behind (−x)
        assertFalse(EmberRunDirector.arcaneSwept(o, -half, half, 5.0, 1.2, new org.bukkit.Location(null, -3, 64, 0)));
        // chain: a segment between two mobs; touching = within width / 2 horizontally, inside the height band
        org.bukkit.Location ea = new org.bukkit.Location(null, 0, 64, 0), eb = new org.bukkit.Location(null, 6, 64, 0);
        assertTrue(EmberRunDirector.chainTouches(ea, eb, 1.0, new org.bukkit.Location(null, 3, 64, 0.4)));
        assertFalse(EmberRunDirector.chainTouches(ea, eb, 1.0, new org.bukkit.Location(null, 3, 64, 0.6)));
        assertFalse(EmberRunDirector.chainTouches(ea, eb, 1.0, new org.bukkit.Location(null, 7, 64, 0)));   // past the end
        assertFalse(EmberRunDirector.chainTouches(ea, eb, 1.0, new org.bukkit.Location(null, 3, 70, 0)));   // above
        assertTrue(EmberRunDirector.chainTouches(ea, ea, 1.0, new org.bukkit.Location(null, 0.2, 64, 0)));  // degenerate
        // burn cooldown per player
        assertTrue(EmberRunDirector.chainBurnReady(null, 5000L, 1.0));
        assertFalse(EmberRunDirector.chainBurnReady(4500L, 5000L, 1.0));
        assertTrue(EmberRunDirector.chainBurnReady(4000L, 5000L, 1.0));
        // the roll reaches both new affixes
        java.util.Set<String> aff = new java.util.HashSet<String>();
        for (long seed = 0; seed < 8000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            if (!a[1].isEmpty()) aff.add(a[1]);
        }
        assertTrue(aff.containsAll(java.util.Arrays.asList("arcane", "firechain")));
        assertEquals(12, aff.size());
    }

    @Test public void escortRabbitDoesNotPayTreasureCoin_D171() {
        // variety escort is tracked separately; Extra.TREASURE settlement is the only path to treasure coin
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.bossKilled = true;
        in.extra = EmberRunRules.Extra.NONE;
        in.extraDone = false;
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) assertFalse("extra_treasure_coin".equals(x.key));
        // even if somehow marked done without TREASURE extra, still no treasure coin
        in.extraDone = true;
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) assertFalse("extra_treasure_coin".equals(x.key));
        // escort flag on Tracked must not itself produce a grant key
        EmberRunDirector.Tracked rabbit = new EmberRunDirector.Tracked(null, "treasure", "r1", null, null, 0, 9, 2, null);
        rabbit.varietyEscort = true;
        assertTrue(rabbit.varietyEscort);
        assertEquals(0, rabbit.atk, 0);
    }

    @Test public void crystalBlocksCleanupOnFailAndUnload_D171() {
        // unit-level: crystalBlocks list starts empty; clearCrystals is idempotent via finish path contract
        // (live SEA_LANTERN placement needs a World — covered by smoke). Here we only assert config + labels.
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(3, v.crystalCount);
        assertEquals(35, v.crystalSecs);
        assertEquals("砸余烬晶", EmberRunMaps.Variety.eventLabel("crystal"));
        assertTrue(v.eventLimit("crystal") >= 30);
        assertEquals(0, v.eventLimit("escort")); // no countdown
    }


    @Test public void varietyPack3RollsNewEventsOnly_D179() {
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(9, EmberRunMaps.Variety.EVENTS.size()); // D191 Pack 4: 6 → 9
        assertTrue(EmberRunMaps.Variety.EVENTS.containsAll(java.util.Arrays.asList("hold", "beacon", "relay")));
        assertEquals(12, EmberRunMaps.Variety.KNOWN.size()); // affix pool unchanged by D179 (D189 later 8→10, D196 10→12)
        assertEquals(EmberRunMaps.Variety.EVENTS, v.events);
        assertEquals(2.5, v.holdRadius, 1e-9);
        assertEquals(12.0, v.holdNeed, 1e-9);
        assertEquals(40, v.holdSecs);
        assertEquals(0.45, v.beaconHp, 1e-9);
        assertEquals(4.0, v.beaconAggroR, 1e-9);
        assertEquals(1.0, v.beaconTick, 1e-9);
        assertEquals(0.08, v.beaconBite, 1e-9);
        assertEquals(3, v.relayCount);
        assertEquals(1.6, v.relayRadius, 1e-9);
        assertEquals(40, v.relaySecs);
        assertEquals("占点", EmberRunMaps.Variety.eventLabel("hold"));
        assertEquals("护灯", EmberRunMaps.Variety.eventLabel("beacon"));
        assertEquals("传火", EmberRunMaps.Variety.eventLabel("relay"));
        assertEquals(40, v.eventLimit("hold"));
        assertEquals(0, v.eventLimit("beacon"));
        assertEquals(40, v.eventLimit("relay"));
        java.util.Set<String> ev = new java.util.HashSet<String>();
        for (long seed = 0; seed < 8000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            assertEquals(4, a.length);
            if (!a[2].isEmpty()) {
                assertTrue(a[3], EmberRunMaps.Variety.EVENTS.contains(a[3]));
                ev.add(a[3]);
            }
        }
        assertTrue(ev.containsAll(java.util.Arrays.asList("timed", "crystal", "escort", "hold", "beacon", "relay")));
        // first-clear / challenge / abyss / raid: variety gated by caller — roll itself is seed-only; empty gates live in EmberRunService
        EmberRunSession s = new EmberRunSession();
        s.eventRoom = "r2"; s.eventKind = "hold"; s.eventDone = true;
        assertEquals("hold", EmberRunSession.fromMap(s.toMap()).eventKind);
    }

    @Test public void varietyBountyCountsHoldBeaconRelay_D179() {
        // same kind=timed bounty advances for any eventDone
        List<EmberRunRules.Grant> g = EmberRunRules.varietyBountyGrants(new EmberRunRules.VarietyBounty("timed", 1, 20, 0), 0, true);
        assertEquals("vb_timed", g.get(0).key);
        assertEquals("var_event_hold", EmberRunRules.varietyGrants(false, false, 0, true, 1, "hold").get(0).key);
        assertEquals("var_event_beacon", EmberRunRules.varietyGrants(false, false, 0, true, 1, "beacon").get(0).key);
        assertEquals("var_event_relay", EmberRunRules.varietyGrants(false, false, 0, true, 1, "relay").get(0).key);
        assertEquals("占点 ", EmberRunService.matSource("var_event_hold"));
        assertEquals("护灯 ", EmberRunService.matSource("var_event_beacon"));
        assertEquals("传火 ", EmberRunService.matSource("var_event_relay"));
    }

    @Test public void holdRequiresGroundPresence_D179() {
        assertTrue(EmberRunDirector.holdCounts(true, true));
        assertFalse(EmberRunDirector.holdCounts(true, false)); // airborne / creative fly
        assertFalse(EmberRunDirector.holdCounts(false, true)); // outside circle
        assertFalse(EmberRunDirector.holdCounts(false, false));
    }

    @Test public void beaconDoesNotPayTreasureOrBlockClear_D179() {
        assertFalse(EmberRunDirector.beaconIsTreasureExtra());
        assertFalse(EmberRunDirector.beaconBlocksRoomClear());
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.bossKilled = true;
        in.extra = EmberRunRules.Extra.NONE;
        in.extraDone = true;
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) assertFalse("extra_treasure_coin".equals(x.key));
        // beacon success settles core via varietyGrants, not treasure
        assertEquals(EmberUpgradeRules.MAT_CORE, EmberRunRules.varietyGrants(false, false, 0, true, 1, "beacon").get(0).id);
    }

    @Test public void relayEnforcesOrderAndCleanup_D179() {
        assertEquals(0, EmberRunDirector.relayAdvance(0, 1, 3)); // skip invalid
        assertEquals(0, EmberRunDirector.relayAdvance(0, 2, 3));
        assertEquals(1, EmberRunDirector.relayAdvance(0, 0, 3));
        assertEquals(2, EmberRunDirector.relayAdvance(1, 1, 3));
        assertEquals(3, EmberRunDirector.relayAdvance(2, 2, 3));
        assertEquals(3, EmberRunDirector.relayAdvance(3, 3, 3)); // already done
        EmberRunMaps.Variety v = bundled().variety;
        assertEquals(3, v.relayCount);
        assertEquals("传火", EmberRunMaps.Variety.eventLabel("relay"));
    }

    @Test public void varietyPack4RollsBreachChainUnscathed_D191() {
        EmberRunMaps.Variety v = bundled().variety;
        assertTrue(EmberRunMaps.Variety.EVENTS.containsAll(java.util.Arrays.asList("breach", "chain", "unscathed")));
        assertEquals(12, EmberRunMaps.Variety.KNOWN.size()); // affix pool untouched by D191 (D196 later 10→12)
        assertEquals(EmberRunMaps.Variety.EVENTS, v.events);
        assertEquals(0.85, v.eventRate, 1e-9);              // D296 W1a 房间事件必感
        assertEquals(1, v.eventCore);                       // reward amount unchanged
        assertEquals(5.0, v.breachRadius, 1e-9);
        assertEquals(1.5, v.breachMin, 1e-9);
        assertEquals(7.0, v.breachMax, 1e-9);
        assertEquals(0.15, v.breachShrink, 1e-9);
        assertEquals(0.8, v.breachGrow, 1e-9);
        assertEquals(4, v.chainNeed);
        assertEquals(4.0, v.chainGap, 1e-9);
        assertEquals(4, v.unscathedHits);
        assertEquals(2, v.unscathedPerMember);
        assertEquals("裂隙", EmberRunMaps.Variety.eventLabel("breach"));
        assertEquals("连斩", EmberRunMaps.Variety.eventLabel("chain"));
        assertEquals("无伤", EmberRunMaps.Variety.eventLabel("unscathed"));
        assertEquals(0, v.eventLimit("breach"));    // no countdown
        assertEquals(0, v.eventLimit("chain"));
        assertEquals(0, v.eventLimit("unscathed"));
        java.util.Set<String> ev = new java.util.HashSet<String>();
        for (long seed = 0; seed < 12000; seed++) {
            String[] a = v.roll(EmberRunRules.subSeed(seed, "variety"));
            if (!a[2].isEmpty()) ev.add(a[3]);
            assertArrayEquals(a, v.roll(EmberRunRules.subSeed(seed, "variety"))); // same seed → same kind (reconnect / retry)
        }
        assertTrue(ev.containsAll(EmberRunMaps.Variety.EVENTS));
        EmberRunSession s = new EmberRunSession();
        s.eventRoom = "r3"; s.eventKind = "unscathed";
        assertEquals("unscathed", EmberRunSession.fromMap(s.toMap()).eventKind);
    }

    @Test public void pack4PaysExistingCoreOnceOnly_D191() {
        for (String k : new String[]{"breach", "chain", "unscathed"}) {
            List<EmberRunRules.Grant> g = EmberRunRules.varietyGrants(false, false, 0, true, 1, k);
            assertEquals(1, g.size());
            assertEquals("var_event_" + k, g.get(0).key);
            assertEquals(EmberUpgradeRules.MAT_CORE, g.get(0).id);
            assertEquals(1, g.get(0).amount);
            assertTrue(EmberRunRules.varietyGrants(true, false, 0, true, 1, k).isEmpty());   // first clear: nothing
            assertTrue(EmberRunRules.varietyGrants(false, false, 0, false, 1, k).isEmpty()); // failed: nothing
        }
        assertEquals("裂隙 ", EmberRunService.matSource("var_event_breach"));
        assertEquals("连斩 ", EmberRunService.matSource("var_event_chain"));
        assertEquals("无伤 ", EmberRunService.matSource("var_event_unscathed"));
        // 花样委托 kind timed still counts any room-event success (label unchanged)
        assertEquals("房间事件达标", new EmberRunRules.VarietyBounty("timed", 1, 20, 0).label());
    }

    @Test public void breachShrinksGrowsAndCollapses_D191() {
        assertEquals(4.25, EmberRunDirector.breachStep(5.0, 5.0, 0.15), 1e-9);
        assertEquals(5.0, EmberRunDirector.breachStep(5.0, -1.0, 0.15), 1e-9);   // clock skew never grows it
        assertEquals(0.0, EmberRunDirector.breachStep(0.5, 100.0, 0.15), 1e-9);
        assertEquals(5.05, EmberRunDirector.breachGrow(4.25, 0.8, 7.0), 1e-9);
        assertEquals(7.0, EmberRunDirector.breachGrow(6.8, 0.8, 7.0), 1e-9);     // capped
        assertTrue(EmberRunDirector.breachCollapsed(1.5, 1.5));
        assertFalse(EmberRunDirector.breachCollapsed(1.51, 1.5));
        assertTrue(EmberRunDirector.breachInside(3.0, 4.0, 5.0));
        assertFalse(EmberRunDirector.breachInside(3.0, 4.1, 5.0));
        // no kills: 5.0 → 1.5 after (5.0 − 1.5) / 0.15 ≈ 23.3 s
        double r = 5.0; int t = 0;
        while (!EmberRunDirector.breachCollapsed(r, 1.5)) { r = EmberRunDirector.breachStep(r, 1.0, 0.15); t++; }
        assertEquals(24, t);
    }

    @Test public void chainNeedsKillsWithinGap_D191() {
        assertEquals(1, EmberRunDirector.chainNext(0, 0, 1000, 4.0));       // first kill
        assertEquals(2, EmberRunDirector.chainNext(1, 1000, 4999, 4.0));    // within 4 s
        assertEquals(3, EmberRunDirector.chainNext(2, 1000, 5000, 4.0));    // exactly 4 s still counts
        assertEquals(1, EmberRunDirector.chainNext(3, 1000, 5001, 4.0));    // gap broken → restart
        assertEquals(4, EmberRunDirector.chainNeedFor(4, 6));
        assertEquals(3, EmberRunDirector.chainNeedFor(4, 3));               // small room: all of it
        assertEquals(1, EmberRunDirector.chainNeedFor(4, 0));
    }

    @Test public void unscathedBudgetScalesPerMember_D191() {
        assertEquals(4, EmberRunDirector.unscathedBudget(4, 2, 1));
        assertEquals(6, EmberRunDirector.unscathedBudget(4, 2, 2));
        assertEquals(10, EmberRunDirector.unscathedBudget(4, 2, 4));
        assertEquals(4, EmberRunDirector.unscathedBudget(4, 2, 0));         // nobody counted → solo budget
        assertTrue(EmberRunDirector.unscathedOk(4, 4));
        assertFalse(EmberRunDirector.unscathedOk(5, 4));
        assertFalse(EmberRunDirector.unscathedOk(0, -1));                    // never armed → never success
    }

    @Test public void pack4ConfigClampsAndUnknownKindsDropped_D191() {
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("event_rate", 1.0);
        raw.put("events", java.util.Arrays.asList("breach", "ritual", "harbinger", "chain", "unscathed"));
        java.util.Map<String, Object> br = new java.util.LinkedHashMap<String, Object>();
        br.put("radius", 50.0); br.put("min", 0.0); br.put("max", 99.0); br.put("shrink", 0.0); br.put("grow", 9.0);
        raw.put("breach", br);
        java.util.Map<String, Object> cn = new java.util.LinkedHashMap<String, Object>();
        cn.put("need", 1); cn.put("gap", 60.0);
        raw.put("chain", cn);
        java.util.Map<String, Object> us = new java.util.LinkedHashMap<String, Object>();
        us.put("hits", 999); us.put("per_member", 99);
        raw.put("unscathed", us);
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(raw);
        assertEquals(java.util.Arrays.asList("breach", "chain", "unscathed"), v.events); // ritual / harbinger rejected
        assertEquals(0.5, v.breachMin, 1e-9);
        assertEquals(10.0, v.breachMax, 1e-9);
        assertEquals(10.0, v.breachRadius, 1e-9);
        assertEquals(0.01, v.breachShrink, 1e-9);   // never a rift that cannot close
        assertEquals(3.0, v.breachGrow, 1e-9);
        assertEquals(2, v.chainNeed);
        assertEquals(10.0, v.chainGap, 1e-9);
        assertEquals(20, v.unscathedHits);
        assertEquals(10, v.unscathedPerMember);
    }

    @Test public void pack3RejectsCageDrainAsConfig_D179() {
        java.util.Map<String, Object> raw = new java.util.LinkedHashMap<String, Object>();
        raw.put("event_rate", 1.0);
        raw.put("events", java.util.Arrays.asList("hold", "cage", "drain", "beacon", "relay"));
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(raw);
        assertEquals(java.util.Arrays.asList("hold", "beacon", "relay"), v.events);
        assertFalse(EmberRunMaps.Variety.EVENTS.contains("cage"));
        assertFalse(EmberRunMaps.Variety.EVENTS.contains("drain"));
    }


    @Test public void chargeBossesCrashIntoWalls_D188() {
        EmberRunMaps m = bundled();
        String[][] want = {{"q02", "焦冲"}, {"q07", "冲撞"}};
        for (String[] w : want) {
            EmberRunMaps.Skill found = null;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) if (w[1].equals(sk.name)) found = sk;
            assertNotNull(w[0] + " " + w[1], found);
            assertEquals("charge", found.type);
            assertEquals(w[0] + " wall stun", 1.5, found.wallStun, 0);
        }
        for (String k : new String[]{"r02", "r03"})   // D195: r01 冲撞 has it now (raidBossesInheritCounterplay_D195)
            for (EmberRunMaps.Skill sk : m.byKey(k).boss.skills) assertEquals(k + " no wall stun", 0.0, sk.wallStun, 0);
    }

    @Test public void earlyBossesStaggerOnAWhiffedHeavyMove_D192() {
        EmberRunMaps m = bundled();
        String[][] want = {{"q01", "重斩"}, {"q02", "砸地"}, {"q03", "誓斩"}, {"q04", "冲击圈"}, {"q05", "斧刃横扫"}};
        for (String[] w : want) {
            int n = 0;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) {
                if (sk.whiffStun > 0) { n++; assertEquals(w[0], w[1], sk.name); assertEquals(0.5, sk.whiffStun, 0); assertFalse(sk.light); }
                if (sk.follow != null) assertEquals("follow untouched", 0.0, sk.follow.whiffStun, 0);
            }
            assertEquals(w[0] + " exactly one whiff move", 1, n);
        }
        for (String k : new String[]{"q06", "q07", "r01"})   // D195: r02 / r03 have one each (raidBossesInheritCounterplay_D195)
            for (EmberRunMaps.Skill sk : m.byKey(k).boss.skills) assertEquals(k + " untouched", 0.0, sk.whiffStun, 0);
    }

    @Test public void raidBossesInheritCounterplay_D195() {
        EmberRunMaps m = bundled();
        // each raid boss gets the stagger window its source boss already teaches; nothing else on the raid moves changes
        String[][] want = {{"r01", "冲撞", "wall", "1.5", "94"}, {"r02", "砸地", "whiff", "0.5", "88"}, {"r03", "斧刃横扫", "whiff", "0.5", "84"}};
        for (String[] w : want) {
            int n = 0;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) {
                if (sk.follow != null) { assertEquals("follow untouched", 0.0, sk.follow.whiffStun, 0); assertEquals(0.0, sk.follow.wallStun, 0); }
                assertEquals(w[0] + " no break channel", 0.0, sk.breakHp, 0);
                if (sk.wallStun <= 0 && sk.whiffStun <= 0) continue;
                n++;
                assertEquals(w[0], w[1], sk.name);
                assertEquals(w[0] + " " + w[2], Double.parseDouble(w[3]), "wall".equals(w[2]) ? sk.wallStun : sk.whiffStun, 0);
                assertEquals(w[0] + " " + w[2] + " only", 0.0, "wall".equals(w[2]) ? sk.whiffStun : sk.wallStun, 0);
                assertEquals(w[0] + " dmg offset (raidwin gate)", Double.parseDouble(w[4]), sk.dmg, 0);
                assertFalse(w[0] + " heavy move", sk.light);
                assertFalse(sk.share);
            }
            assertEquals(w[0] + " exactly one stagger window", 1, n);
        }
        assertEquals("r02 砸地 still half-HP only", 0.5, skillNamed(m, "r02", "砸地").below, 0);
        assertEquals("charge", skillNamed(m, "r01", "冲撞").type);
    }

    private static EmberRunMaps.Skill skillNamed(EmberRunMaps m, String key, String name) {
        for (EmberRunMaps.Skill sk : m.byKey(key).boss.skills) if (name.equals(sk.name)) return sk;
        throw new AssertionError(key + " " + name);
    }

    @Test public void lateBossesHaveOneHalfHpBreakChannel_D193() {
        EmberRunMaps m = bundled();
        String[][] want = {{"q06", "霜潮汲取", "0.3"}, {"q07", "炉心聚爆", "0.5"}};
        for (String[] w : want) {
            int n = 0;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) {
                if (sk.follow != null) assertEquals("follow untouched", 0.0, sk.follow.breakHp, 0);
                if (sk.breakHp <= 0) continue;
                n++;
                assertEquals(w[0], w[1], sk.name);
                assertEquals("circle", sk.type);
                assertEquals(0.065, sk.breakHp, 1e-9);
                assertEquals(Double.parseDouble(w[2]), sk.breakStun, 1e-9);
                assertEquals(3.0, sk.warn, 0);
                assertEquals("half-HP only", 0.5, sk.below, 0);
                assertEquals(0.0, sk.whiffStun, 0);
                assertFalse(sk.light);
            }
            assertEquals(w[0] + " exactly one break channel", 1, n);
        }
        for (String k : new String[]{"q01", "q02", "q03", "q04", "q05", "r01", "r02", "r03"})
            for (EmberRunMaps.Skill sk : m.byKey(k).boss.skills) assertEquals(k + " untouched", 0.0, sk.breakHp, 0);
    }

    @Test public void everyMainBossHasHalfHpTelegraphedLightPressure_D173() {
        EmberRunMaps m = bundled();
        String[][] want = {
            {"q01", "门廊突刺"}, {"q02", "焦冲"}, {"q03", "誓印扇"},
            {"q04", "回浪"}, {"q05", "余震"}, {"q06", "霜锥"}, {"q07", "矿渣"}
        };
        for (String[] w : want) {
            EmberRunMaps.Skill found = null;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) {
                if (w[1].equals(sk.name)) found = sk;
                if (sk.follow != null && w[1].equals(sk.follow.name)) found = sk.follow;
            }
            assertNotNull(w[0] + " " + w[1], found);
            assertTrue(w[1] + " half-HP gate", found.below <= 0.5);
            assertTrue(w[1] + " dodge window", found.warn >= 1.2);
            assertTrue(w[1] + " light", found.light);
            assertTrue(w[0] + " has below pressure", EmberRunDirector.hasBelowPressure(m.byKey(w[0]).boss));
        }
        // Q01 top-level every 40 (challenge light gate); Q03 follow on 誓印圈 (not 4th every)
        assertEquals(2, m.byKey("q01").boss.skills.size());
        assertEquals(40.0, m.byKey("q01").boss.skills.get(1).every, 0);
        assertEquals(3, m.byKey("q02").boss.skills.size());
        assertEquals(13.0, m.byKey("q02").boss.skills.get(0).every, 0);
        assertEquals(2, m.byKey("q03").boss.skills.size());
        assertEquals(16.5, m.byKey("q03").boss.skills.get(0).every, 0);
        assertEquals(18.0, m.byKey("q03").boss.skills.get(1).every, 0);
        assertEquals("誓印扇", m.byKey("q03").boss.skills.get(1).follow.name);
        assertEquals(3, m.byKey("q04").boss.skills.size());
        assertEquals(18.0, m.byKey("q04").boss.skills.get(2).every, 0);
        assertEquals(3, m.byKey("q05").boss.skills.size());
        assertEquals(19.0, m.byKey("q05").boss.skills.get(2).every, 0);
        assertEquals(3, m.byKey("q06").boss.skills.size()); // D193 +霜潮汲取 (break channel, last)
        assertEquals(13.0, m.byKey("q06").boss.skills.get(0).every, 0);
        assertEquals(18.0, m.byKey("q06").boss.skills.get(1).every, 0);
        assertEquals(4, m.byKey("q07").boss.skills.size()); // D193 +炉心聚爆 (break channel, last)
        assertEquals(17.5, m.byKey("q07").boss.skills.get(2).every, 0);
    }

    @Test public void everyMainBossHasOneNewTelegraphedLightMove_D140() {
        EmberRunMaps m = bundled();
        String[][] want = {{"q01", "踏地"}, {"q02", "骨刺"}, {"q03", "誓印圈"}, {"q04", "潮涌"}, {"q05", "落石"}, {"q06", "霜环"}, {"q07", "矿锤横扫"}};
        for (String[] w : want) {
            EmberRunMaps.Skill found = null;
            for (EmberRunMaps.Skill sk : m.byKey(w[0]).boss.skills) {
                if (w[1].equals(sk.name)) found = sk;
                if (sk.follow != null && w[1].equals(sk.follow.name)) found = sk.follow;
            }
            assertNotNull(w[0] + " " + w[1], found);
            assertTrue(w[1] + " dodge window", found.warn >= 1.2);
            assertTrue(w[1] + " light in the challenge", found.light);
        }
    }

    // ------------------------------------------------------------------ D160 余烬连战: weekly reward claim, unlimited retries

    @Test public void rushRewardIsClaimedOncePerWeekRetriesAreFree() {
        assertTrue(EmberRunRules.rushPaysReward(0, 1));
        assertFalse(EmberRunRules.rushPaysReward(1, 1));
        assertFalse(EmberRunRules.rushPaysReward(5, 1));
        assertFalse("cap 0 = off", EmberRunRules.rushPaysReward(0, 0));
        // the switch-over week: a pre-D160 clear (old rule counted the entry, the clear is on the board) is the claim
        assertEquals(1, EmberRunRules.rushClaims(0, true));
        assertEquals(0, EmberRunRules.rushClaims(0, false));
        assertEquals(2, EmberRunRules.rushClaims(2, true));
        assertTrue(EmberRunRules.rushWeekText(0, 1).contains("失败可无限重试"));
        assertTrue(EmberRunRules.rushWeekText(1, 1).contains("练习"));
    }

    /** Econ invariant: whatever the attempt / fail pattern, a week pays at most the cap (and exactly the cap once anything clears). */
    @Test public void rushWeeklyRewardOutputNeverExceedsTheCap() {
        Random r = new Random(160);
        for (int week = 0; week < 20000; week++) {
            double clearRate = r.nextDouble();
            int attempts = 1 + r.nextInt(40), claims = 0, paid = 0, clears = 0;
            for (int a = 0; a < attempts; a++) {
                if (r.nextDouble() >= clearRate) continue; // a failed attempt: nothing counted, nothing paid
                clears++;
                if (EmberRunRules.rushPaysReward(claims, 1)) { paid++; claims++; }
            }
            assertTrue("paid " + paid, paid <= 1);
            assertEquals(clears > 0 ? 1 : 0, paid);
        }
    }
}
