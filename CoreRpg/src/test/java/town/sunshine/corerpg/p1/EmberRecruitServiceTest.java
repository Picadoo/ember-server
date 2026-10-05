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
import static org.junit.Assert.assertTrue;

/**
 * D234 / ARCH S3-5: fixed proofs that the recruit board TTL / cooldown / board text / PAPI label
 * and post messages match the pre-extract behaviour (bv58 unchanged).
 */
public final class EmberRecruitServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberRecruitServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void constants_andBundledRaids_unchanged() {
        assertEquals(10 * 60_000L, EmberRecruitService.TTL_MS);
        assertEquals(60_000L, EmberRecruitService.COOLDOWN_MS);
        assertEquals(5, EmberRecruitService.TEAM_CAP);
        assertEquals(2, EmberRecruitService.LABEL_SHOW);
        EmberRunMaps maps = bundled();
        assertEquals(Arrays.asList("r01", "r02", "r03"), new java.util.ArrayList<String>(maps.raids.keySet()));
        for (EmberRunMaps.MapDef r : maps.raids.values()) {
            assertTrue(r.raid);
            assertEquals("q07", r.requires);
            assertEquals(3, maps.partyMin(r));
            assertEquals(5, maps.partyMax(r));
        }
        assertEquals(EmberRecruitService.TTL_MS, EmberRunService.RECRUIT_TTL);
    }

    @Test public void cooldown_okAndRemain_matchesPreExtract() {
        long t0 = 1_000_000L;
        assertTrue(EmberRecruitService.cooldownOk(null, t0));
        assertTrue(EmberRecruitService.cooldownOk(t0 - 60_000L, t0));
        assertFalse(EmberRecruitService.cooldownOk(t0 - 59_999L, t0));
        assertFalse(EmberRecruitService.cooldownOk(t0, t0));
        // 60 - (now-last)/1000
        assertEquals(60L, EmberRecruitService.cooldownRemainSec(t0, t0));
        assertEquals(59L, EmberRecruitService.cooldownRemainSec(t0, t0 + 1_500L));
        assertEquals(1L, EmberRecruitService.cooldownRemainSec(t0, t0 + 59_999L));
        assertEquals(0L, EmberRecruitService.cooldownRemainSec(t0, t0 + 60_000L));
    }

    @Test public void stillLive_predicate_unchanged() {
        long now = 5_000_000L;
        long at = now - 60_000L; // 1 min old
        assertTrue(EmberRecruitService.stillLive(at, now, true, false, true, true, 3));
        assertFalse(EmberRecruitService.stillLive(at, now, false, false, true, true, 3)); // offline
        assertFalse(EmberRecruitService.stillLive(at, now, true, true, true, true, 3));  // in instance
        assertFalse(EmberRecruitService.stillLive(at, now, true, false, false, true, 3)); // no team
        assertFalse(EmberRecruitService.stillLive(at, now, true, false, true, false, 3)); // not leader
        assertFalse(EmberRecruitService.stillLive(at, now, true, false, true, true, 5));  // full
        assertTrue(EmberRecruitService.stillLive(at, now, true, false, true, true, 4));
        assertFalse(EmberRecruitService.stillLive(now - EmberRecruitService.TTL_MS, now, true, false, true, true, 3)); // expired
        assertTrue(EmberRecruitService.stillLive(now - EmberRecruitService.TTL_MS + 1, now, true, false, true, true, 3));
    }

    @Test public void boardText_ageAndReady_unchanged() {
        assertEquals("R01 · Alice · 2/3 · 刚刚",
                EmberRecruitService.boardText("r01", "Alice", 2, 3, 0L));
        assertEquals("R01 · Alice · 2/3 · 刚刚",
                EmberRecruitService.boardText("r01", "Alice", 2, 3, 59_999L));
        assertEquals("R01 · Alice · 2/3 · 1 分钟前",
                EmberRecruitService.boardText("r01", "Alice", 2, 3, 60_000L));
        assertEquals("R02 · Bob · 3/3（可开本） · 刚刚",
                EmberRecruitService.boardText("r02", "Bob", 3, 3, 0L));
        assertEquals("R03 · Carol · 5/3（可开本） · 9 分钟前",
                EmberRecruitService.boardText("r03", "Carol", 5, 3, 9 * 60_000L));
        // null leader path used n=1 when team size unknown
        assertEquals("R01 · Solo · 1/3 · 刚刚",
                EmberRecruitService.boardText("r01", "Solo", 0, 3, 0L));
    }

    @Test public void labelText_emptyOneTwoOverflow_unchanged() {
        assertEquals("暂无（右键发一个）", EmberRecruitService.labelText(null));
        assertEquals("暂无（右键发一个）", EmberRecruitService.labelText(Collections.<String>emptyList()));
        assertEquals("R01 · A · 2/3 · 刚刚",
                EmberRecruitService.labelText(Collections.singletonList("R01 · A · 2/3 · 刚刚")));
        assertEquals("R01 · A · 2/3 · 刚刚 ｜ R02 · B · 3/3（可开本） · 1 分钟前",
                EmberRecruitService.labelText(Arrays.asList(
                        "R01 · A · 2/3 · 刚刚",
                        "R02 · B · 3/3（可开本） · 1 分钟前")));
        assertEquals("R01 · A · 2/3 · 刚刚 ｜ R02 · B · 3/3（可开本） · 1 分钟前 等 3 个",
                EmberRecruitService.labelText(Arrays.asList(
                        "R01 · A · 2/3 · 刚刚",
                        "R02 · B · 3/3（可开本） · 1 分钟前",
                        "R03 · C · 1/3 · 刚刚")));
    }

    @Test public void callAndPostMessages_unchanged() {
        assertEquals("§6Lead §f招 §e锈轨矿道·团 §f队员（现在 1 人，还差 2 人开本；人越多越稳，最多 5 人） §e建议队伍里有炽愈 ",
                EmberRecruitService.callBody("Lead", "锈轨矿道·团", 1, 3, "建议队伍里有炽愈"));
        assertEquals("§6Lead §f招 §e霜封哨所·团 §f队员（现在 3 人；人越多越稳，最多 5 人） ",
                EmberRecruitService.callBody("Lead", "霜封哨所·团", 3, 3, ""));
        assertEquals("§6Lead §f招 §e断塔回廊·团 §f队员（现在 4 人；人越多越稳，最多 5 人） ",
                EmberRecruitService.callBody("Lead", "断塔回廊·团", 4, 3, null));
        assertEquals(
                "§a已向 3 位已首通 Q07 的在线玩家发出招募；有人申请时会出现 [同意] [拒绝]。"
                        + "§7招募挂在冒险页团本图标上 10 分钟，之后上线的人也看得到。",
                EmberRecruitService.postedOk(3, "Q07"));
        assertEquals(
                "§7现在没有其他已首通 Q07 的玩家在线。"
                        + "§7招募挂在冒险页团本图标上 10 分钟，之后上线的人也看得到。",
                EmberRecruitService.postedEmpty("Q07"));
        assertEquals("/corerpg p1 recruit <r01|r02|r03> — 全服招募团本队员", EmberRecruitService.usageHelp());
        assertEquals("现在没有团本在招人。冒险页团本图标右键可以自己发一个。", EmberRecruitService.emptyBoardMsg());
        // bundled party hint for r01 (D166)
        EmberRunMaps.MapDef r01 = bundled().raids.get("r01");
        assertNotNull(r01);
        assertFalse(r01.partyHint.isEmpty());
        String body = EmberRecruitService.callBody("X", r01.name, 1, bundled().partyMin(r01), r01.partyHint);
        assertTrue(body.contains(r01.partyHint));
        assertTrue(body.contains("还差 2 人开本"));
    }
}
