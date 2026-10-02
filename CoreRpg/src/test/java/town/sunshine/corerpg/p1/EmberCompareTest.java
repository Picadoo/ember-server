package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class EmberCompareTest {

    private final EmberTables t = EmberTables.defaults();

    private static EmberItemData it(String fam, String slot, int tier, int q, int craft, int enh, String src) {
        return EmberItemData.create(fam, slot, tier, q, craft, enh, true, src);
    }

    @Test public void bookExampleCard() {
        // §19.2: "T2 烬爆刃｜卓越｜锋刃4%｜+6；当前基准攻击组成 42×(1+0.30+0.08+0.04)，角色等级部分另列"
        List<String> c = EmberCompare.card(t, it("burst", "blade", 2, 2, 2, 6, "drop"), 10);
        assertEquals("§6T2 烬爆刃｜卓越｜锋刃4%｜+6", c.get(0));
        assertTrue(c.get(1), c.get(1).contains("42×(1+0.30+0.08+0.04) = 59.6"));
        assertTrue(c.get(1), c.get(1).contains("角色等级另加 §f0"));
        assertTrue(c.get(2), c.get(2).contains("烬爆") && c.get(2).contains("≥ T1"));
        assertTrue(c.get(3), c.get(3).contains("绑定") && c.get(3).contains("掉落"));
    }

    @Test public void charmCardShowsHpAndDefense() {
        List<String> c = EmberCompare.card(t, it("sustain", "charm", 1, 0, 0, 0, "quest"), 10);
        assertTrue(c.get(1), c.get(1).contains("20 + 50×(1+0.00+0.00+0.00) = 70"));
        assertTrue(c.get(2), c.get(2).contains("防御：§f6") && c.get(2).contains("×0.87"));
        assertTrue(c.get(4), c.get(4).contains("不可分解"));
    }

    @Test public void higherOffFamilyCharmWarnsSetLoss() {
        // §19.2: a higher-tier off-family charm raises HP but cancels 烬爆 — must be shown before selecting
        EmberItemData blade = it("burst", "blade", 2, 0, 0, 6, "drop");
        EmberLoadout before = EmberLoadout.compute(t, blade, it("burst", "charm", 2, 0, 0, 6, "drop"), 20);
        EmberLoadout after = EmberLoadout.compute(t, blade, it("sustain", "charm", 3, 0, 0, 0, "drop"), 20);
        List<String> d = EmberCompare.diff(before, after);
        assertTrue(d.get(0), d.get(0).contains("不变"));
        assertTrue(d.get(1), d.get(1).contains("§a"));
        assertTrue(d.get(3), d.get(3).contains("会取消烬爆") && d.get(3).contains("未成套"));
    }

    @Test public void awakeningDownIsRed() {
        EmberItemData blade = it("scorch", "blade", 2, 0, 0, 6, "drop");
        EmberLoadout before = EmberLoadout.compute(t, blade, it("scorch", "charm", 2, 0, 0, 6, "drop"), 20);
        EmberLoadout after = EmberLoadout.compute(t, blade, it("scorch", "charm", 2, 0, 0, 5, "drop"), 20);
        String s = EmberCompare.diff(before, after).get(3);
        assertTrue(s, s.contains("觉醒II → §c焚烬 觉醒I") && s.contains("觉醒降档"));
    }
}
