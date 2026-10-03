package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        assertEquals(24, m.balanceVersion);                // D141–D143
        assertEquals("g04-1/b24", m.ruleVersion);
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
        assertEquals(4, m.goalTargets.size());
        assertEquals(15, m.goalReward);
        assertEquals(20, m.goalBonus);
        assertEquals(0.2, m.raidLastReviveHp, 1e-9);      // D118
        assertEquals(20.0, m.raidReviveDelay, 1e-9);
        // P2-8 weekly rules: 3 rules × 7 maps, all 21 pairs over 21 weeks; no multiplier keys at all
        assertEquals(3, m.modifiers.size());
        assertEquals(3, m.modifier("lean").potionCap);
        assertEquals("caster", m.modifier("casters").role("ranged", q3));
        assertEquals("ranged", m.modifier("casters").role("ranged", q1)); // Q01 has no caster → unchanged
        assertEquals("melee", m.modifier("casters").role("melee", q3));
        assertTrue(m.modifier("lean").normal);              // D94: repeat normal runs get lean / reverse only
        assertTrue(m.modifier("reverse").normal);
        assertFalse(m.modifier("casters").normal);
        assertTrue(m.modifier("reverse").swapRooms);
        assertNull(m.modifier(""));
        java.util.Set<String> pairs = new java.util.HashSet<String>();
        java.time.LocalDate d0 = java.time.LocalDate.of(2026, 10, 5);
        for (int wk = 0; wk < 21; wk++) {
            java.time.LocalDate d = d0.plusWeeks(wk);
            pairs.add(EmberRunRules.featuredChallenge(new java.util.ArrayList<String>(m.maps.keySet()), d) + "/" + m.modifierFor(d).id);
            assertSame(m.modifierFor(d), m.modifierFor(d.plusDays(6)));
        }
        assertEquals(21, pairs.size());
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
        assertEquals("r2", q4.eventAfter); // D15: E is entered from R2 on every book map
        assertEquals("r2", q5.eventAfter);
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
        assertEquals(12.5, blade.every, 0);             // D140: 10 → 12.5 (+ 霜环)
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
        assertEquals("r2", q6.eventAfter);
        assertEquals("r2", q7.eventAfter);
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
        assertEquals(25, EmberCosmetics.ALL.size()); // D103 + E-review #9 + D116 six season honors + D137 R03 title / trail + D139 国庆 title / trail
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
}
