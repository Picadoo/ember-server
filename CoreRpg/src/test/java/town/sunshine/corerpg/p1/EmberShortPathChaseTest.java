package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CV D13327: short_path* — path NONE→ok=0；FC/DAY/LATE+resolve→ok=1；无匹配→ok=0；
 * label/tip/line 边界；resolve 行为复用（零改排序/权重）。
 */
public class EmberShortPathChaseTest {

    private static EmberRunMaps.MapDef map(String key, String name) {
        java.util.Map<String, Object> raw = new java.util.HashMap<String, Object>();
        if (name != null) raw.put("name", name);
        EmberRunMaps.MapDef m = new EmberRunMaps.MapDef(key, raw);
        m.shortExpedition = true;
        return m;
    }

    @Test
    public void nonePathOkZeroAndTokenZero() {
        assertEquals("0", EmberShortPathChase.token(EmberShortPath.NONE));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.NONE, "sx01"));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.NONE, ""));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.NONE, null));
        assertEquals("0", EmberShortPathChase.ok(99, "sx01")); // invalid
    }

    @Test
    public void validPathWithResolveOkOne() {
        assertEquals("1", EmberShortPathChase.token(EmberShortPath.FC));
        assertEquals("2", EmberShortPathChase.token(EmberShortPath.DAY));
        assertEquals("3", EmberShortPathChase.token(EmberShortPath.LATE));
        assertEquals("1", EmberShortPathChase.ok(EmberShortPath.FC, "sx01"));
        assertEquals("1", EmberShortPathChase.ok(EmberShortPath.DAY, "sx07"));
        assertEquals("1", EmberShortPathChase.ok(EmberShortPath.LATE, "sx14"));
    }

    @Test
    public void validPathEmptyResolveOkZero() {
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.FC, ""));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.DAY, null));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.LATE, ""));
    }

    @Test
    public void resolveKeyAndNameFromMapDef() {
        assertEquals("", EmberShortPathChase.resolveKey(null));
        assertEquals("sx07", EmberShortPathChase.resolveKey(map("sx07", "烬塔回升")));
        assertEquals("", EmberShortPathChase.resolveName(null));
        assertEquals("烬塔回升", EmberShortPathChase.resolveName(map("sx07", "烬塔回升")));
        assertEquals("sx09", EmberShortPathChase.resolveName(map("sx09", null)));
        assertEquals("sx09", EmberShortPathChase.resolveName(map("sx09", "")));
    }

    @Test
    public void labelTipReuseEmberShortPath() {
        assertEquals("未选", EmberShortPath.label(EmberShortPath.NONE));
        assertEquals("短征·首通追", EmberShortPath.label(EmberShortPath.FC));
        assertEquals("短征·有奖追", EmberShortPath.label(EmberShortPath.DAY));
        assertEquals("短征·后段追", EmberShortPath.label(EmberShortPath.LATE));
        assertTrue(EmberShortPath.tip(EmberShortPath.FC).contains("首通"));
        assertTrue(EmberShortPath.tip(EmberShortPath.DAY).contains("有奖"));
        assertTrue(EmberShortPath.tip(EmberShortPath.LATE).contains("后段")
                || EmberShortPath.tip(EmberShortPath.LATE).contains("sx14"));
        assertTrue(EmberShortPath.tip(EmberShortPath.NONE).contains("偏好")
                || EmberShortPath.tip(EmberShortPath.NONE).length() > 0);
    }

    @Test
    public void lineBoundaries() {
        assertEquals("路径追 · 未选", EmberShortPathChase.line(EmberShortPath.NONE, "sx01", "X"));
        assertEquals("路径追 · 未选", EmberShortPathChase.line(EmberShortPath.NONE, "", null));
        assertEquals("短征·首通追 · 暂无下一本",
                EmberShortPathChase.line(EmberShortPath.FC, "", null));
        assertEquals("短征·有奖追 · 暂无下一本",
                EmberShortPathChase.line(EmberShortPath.DAY, null, "ignored"));
        assertEquals("短征·有奖追 · 下一本 SX07 烬塔回升",
                EmberShortPathChase.line(EmberShortPath.DAY, "sx07", "烬塔回升"));
        assertEquals("短征·后段追 · 下一本 SX14 sx14",
                EmberShortPathChase.line(EmberShortPath.LATE, "sx14", ""));
        // resolveLine style: key uppercased
        assertEquals("短征·首通追 · 下一本 SX03 名",
                EmberShortPathChase.line(EmberShortPath.FC, "sx03", "名"));
    }

    @Test
    public void resolveReusedNullSafeNoSilentPathChange() {
        // Invalid / null inputs → null (existing EmberShortPath.resolve contract; zero algo change)
        assertNull(EmberShortPath.resolve(null, new PlayerData(), EmberShortPath.FC));
        assertNull(EmberShortPath.resolve(null, null, EmberShortPath.DAY));
        PlayerData d = new PlayerData();
        assertEquals(EmberShortPath.NONE, EmberShortPath.get(d));
        assertNull(EmberShortPath.resolve(null, d, EmberShortPath.NONE)); // NONE not valid → null
        // Setting path does not invent resolve; ok stays 0 without a key
        assertTrue(EmberShortPath.set(d, EmberShortPath.DAY));
        assertEquals(EmberShortPath.DAY, EmberShortPath.get(d));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.get(d), ""));
        // clear back — still no silent rewrite on read path
        assertTrue(EmberShortPath.set(d, EmberShortPath.NONE));
        assertEquals(EmberShortPath.NONE, EmberShortPath.get(d));
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.get(d), "sx01"));
    }

    @Test
    public void okMatchesEnterGatePredicate() {
        // enter short-path rejects when ok==0: NONE or empty resolve
        assertEquals("0", EmberShortPathChase.ok(EmberShortPath.NONE, EmberShortPathChase.resolveKey(null)));
        EmberRunMaps.MapDef hit = map("sx05", "试");
        assertEquals("1", EmberShortPathChase.ok(EmberShortPath.FC, EmberShortPathChase.resolveKey(hit)));
        assertEquals("sx05", EmberShortPathChase.resolveKey(hit));
        assertEquals("试", EmberShortPathChase.resolveName(hit));
    }
}
