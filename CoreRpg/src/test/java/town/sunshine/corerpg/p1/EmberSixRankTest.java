package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/** D318 六槽 T1-8 · 护甲页占位符：候选按整装生命差排序、平手同族优先；生命差 = 插件真实公式（与 T1-2 同源）. */
public class EmberSixRankTest {

    static final EmberTables T = EmberTables.defaults();

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    static final String[] FAM = {"scorch", "burst", "sustain"};

    @Test public void bestIsMaxWholeLoadoutDeltaByTheRealFormula() {
        Random r = new Random(318);
        for (int n = 0; n < 3000; n++) {
            EmberItemData charm = piece(FAM[r.nextInt(3)], "charm", 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), r.nextInt(11));
            EmberItemData blade = r.nextBoolean() ? piece(FAM[r.nextInt(3)], "blade", 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), r.nextInt(11)) : null;
            EmberItemData[] worn = new EmberItemData[4];
            for (int i = 0; i < 4; i++) if (r.nextInt(3) > 0) worn[i] = piece(FAM[r.nextInt(3)], EmberItemData.ARMOR_SLOTS.get(i), 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), 0);
            List<EmberItemData> cands = new ArrayList<EmberItemData>();
            int k = r.nextInt(9);
            for (int j = 0; j < k; j++) cands.add(piece(FAM[r.nextInt(3)], EmberItemData.ARMOR_SLOTS.get(r.nextInt(4)), 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), 0));
            int lv = 1 + r.nextInt(30);
            EmberSixRank.View v = EmberSixRank.view(T, blade, charm, lv, 0, 0, worn, cands);
            double h = EmberLoadout.compute(T, blade, charm, lv, 0, 0, worn).h;
            assertEquals(Double.doubleToLongBits(h), Double.doubleToLongBits(v.h));
            for (int i = 0; i < 4; i++) {
                double best = Double.NEGATIVE_INFINITY;
                int count = 0;
                for (EmberItemData c : cands) {
                    if (EmberItemData.armorIndex(c.slot) != i) continue;
                    count++;
                    EmberItemData[] a = worn.clone();
                    a[i] = c;
                    best = Math.max(best, EmberLoadout.compute(T, blade, charm, lv, 0, 0, a).h - h);
                }
                assertEquals(count, v.count[i]);
                if (count == 0) { assertNull(v.best[i]); continue; }
                EmberItemData[] a = worn.clone();
                a[i] = v.best[i].piece;
                double d = EmberLoadout.compute(T, blade, charm, lv, 0, 0, a).h - h;
                assertEquals("delta is the real formula", Double.doubleToLongBits(d), Double.doubleToLongBits(v.best[i].delta));
                assertEquals(Double.doubleToLongBits(best), Double.doubleToLongBits(v.best[i].delta));
            }
        }
    }

    @Test public void tiesPreferTheWornFamilyThenTheCharmFamily() {
        EmberItemData charm = piece("burst", "charm", 2, 1, 1, 3);
        EmberItemData worn = piece("scorch", "chest", 2, 0, 0, 0);
        EmberItemData a = piece("sustain", "chest", 2, 2, 1, 0), b = piece("scorch", "chest", 1, 2, 1, 0), c = piece("burst", "chest", 3, 2, 1, 0);
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(a, c, b));
        assertSame("same q / f → same delta; worn family (scorch) wins over higher tier", b, v.best[1].piece);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[4], Arrays.asList(a, b, c));
        assertSame("empty slot: the charm family (burst) wins", c, v.best[1].piece);
        EmberItemData better = piece("sustain", "chest", 1, 3, 1, 0);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(a, b, c, better));
        assertSame("a larger delta beats any family preference", better, v.best[1].piece);
        assertTrue(v.best[1].delta > 0);
    }

    @Test public void allPlanAndTexts() {
        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        EmberItemData[] worn = {piece("scorch", "head", 2, 2, 2, 0), piece("burst", "chest", 2, 3, 3, 0), null, piece("scorch", "boots", 2, 1, 1, 0)};
        List<EmberItemData> cands = Arrays.asList(piece("scorch", "head", 2, 2, 2, 0), piece("scorch", "chest", 2, 0, 0, 0),
                piece("sustain", "legs", 1, 0, 0, 0), piece("scorch", "boots", 3, 3, 3, 0));
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 25, 0, 0, worn, cands);
        assertEquals(0.0, v.best[0].delta, 0);
        assertTrue(v.best[1].delta < 0);
        assertEquals(0.0, v.best[2].delta, 0); // q0 f0 = the empty slot
        assertTrue(v.best[3].delta > 0);
        assertEquals(Arrays.asList(2, 3), EmberSixRank.allPlan(v)); // ties / worse keep the current piece; empty slots fill
        assertEquals("§7生命 不变", EmberSixPapi.text(true, v, 0, "armor_head_delta"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_chest_delta").startsWith("§c生命 -"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_boots_delta").startsWith("§a生命 +"));
        assertEquals("§e族不同：四件套 焚烬族 2/4 → 焚烬族 3/4", EmberSixPapi.text(true, v, 0, "armor_chest_fam"));
        assertEquals("", EmberSixPapi.text(true, v, 0, "armor_head_fam"));
        assertEquals("§f焚烬族 2/4 · 需掉落阶 T2+", EmberSixPapi.text(true, v, 0, "armor_set"));
        assertEquals("§8护腿：空（按成色标准、精工 0% 计算）", EmberSixPapi.text(true, v, 0, "armor_legs"));
        assertEquals("§f胸甲 · 烬爆族", EmberSixPapi.text(true, v, 0, "armor_chest"));
        assertEquals("§7成色 极品 · 精工 6% · §8掉落阶 T2", EmberSixPapi.text(true, v, 0, "armor_chest_info"));
        assertEquals("§7成色 极品 → 标准 · 精工 6% → 0%", EmberSixPapi.text(true, v, 0, "armor_chest_cmp"));
        assertEquals("§f背包里：护腿 · 炽愈族", EmberSixPapi.text(true, v, 0, "armor_legs_cand"));
        assertEquals("1", EmberSixPapi.text(true, v, 0, "armor_all_has"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_all").startsWith("§7将换上 2 件 · 生命 +"));
        assertEquals("§e待领物品 3 件 · 点击领取", EmberSixPapi.text(true, v, 3, "armor_stash_line"));
        assertEquals("3", EmberSixPapi.text(true, v, 3, "armor_stash"));
        assertEquals("", EmberSixPapi.text(true, v, 0, "armor_nosuch_x"));
        // switch off: everything blank, armor_on 0
        for (String k : new String[]{"armor_head", "armor_set", "armor_all", "armor_stash_line", "armor_chest_delta"}) assertEquals("", EmberSixPapi.text(false, v, 3, k));
        assertEquals("0", EmberSixPapi.text(false, null, 0, "armor_on"));
        assertEquals("1", EmberSixPapi.text(true, v, 0, "armor_on"));
    }

    @Test public void noCharmMeansArmorDoesNothing() {
        EmberSixRank.View v = EmberSixRank.view(T, null, null, 10, 0, 0, new EmberItemData[4],
                Arrays.asList(piece("burst", "head", 3, 3, 3, 0)));
        assertEquals(0.0, v.best[0].delta, 0);
        assertEquals("生命 不变", EmberSixRank.deltaText(0.0));
        assertEquals("生命 +0.5", EmberSixRank.deltaText(0.5));
        assertEquals("生命 -0.3", EmberSixRank.deltaText(-0.3));
        assertEquals("生命 +<0.1", EmberSixRank.deltaText(0.01));
    }

    @Test public void routingAndHeldArmorLines() {
        for (String k : new String[]{"armor_on", "armor_head", "armor_boots_delta", "armor_set", "armor_all", "armor_stash_line"})
            assertEquals(k, EmberRunPapi.Section.ARMOR, EmberRunPapi.route(k));
        EmberItemData a = piece("burst", "legs", 2, 1, 1, 0);
        assertEquals("§8" + EmberUpgradeRules.ARMOR_REFUSE, EmberSixPapi.heldArmorLine(a, "enhance"));
        assertEquals("§8" + EmberUpgradeRules.ARMOR_REFUSE, EmberSixPapi.heldArmorLine(a, "next"));
        assertEquals("§7分解得白板胚 0.2 个（零头攒满 1 个自动到账）", EmberSixPapi.heldArmorLine(a, "dismantle"));
        EmberItemData m = new EmberItemData(EmberItemData.newUid(), a.ni, "burst", "legs", 2, 1, 1, 0, 0, true, "migrate", 2, 0);
        assertEquals("§8这件护甲不能分解", EmberSixPapi.heldArmorLine(m, "dismantle"));
    }
}
