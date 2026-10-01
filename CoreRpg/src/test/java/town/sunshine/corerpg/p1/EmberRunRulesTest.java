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
        assertEquals(3, m.maps.size());
        EmberRunMaps.MapDef q1 = m.byKey("q01"), q2 = m.byKey("q02"), q3 = m.byKey("q03");
        assertEquals("ember_daily", q1.template);
        assertEquals("ember_daily_ash", q2.template);
        assertEquals("ember_daily_crypt", q3.template);
        assertEquals("", q1.requires);
        assertEquals("q01", q2.requires);
        assertEquals("q02", q3.requires);
        assertEquals("blade", q1.firstClear.choiceSlot);
        assertEquals("charm", q2.firstClear.choiceSlot);
        assertEquals(600, q3.firstClear.coin);
        assertSame(q2, m.byWorld("dungeon_EmberQ02_252696C4"));
        assertNull(m.byWorld("dungeon_EmberDaily_252696C4"));
        // book numbers
        assertEquals(36, q1.roles.get("melee").hp, 0);
        assertEquals(240, q1.boss.hp, 0);
        assertEquals(900, q2.boss.hp, 0);
        assertEquals(1300, q3.boss.hp, 0);
        assertEquals(4, (int) q1.room("r1").a.get("melee"));
        assertEquals(2, (int) q2.room("r2").b.get("ranged"));
        assertEquals(1, (int) q3.room("r3").a.get("caster"));
        assertNotNull(q3.boss.adds);
        assertNotNull(q2.boss.skills.get(0).follow);
        assertEquals(0.5, q2.boss.skills.get(0).follow.below, 0);
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
}
