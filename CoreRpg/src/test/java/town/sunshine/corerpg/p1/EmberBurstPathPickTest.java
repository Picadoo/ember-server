package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-FA D13384 W1a: BurstPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 burstpath 主 UI / 零改伤害CD掉落体力价/D563 破绽爆发语义.
 */
public class EmberBurstPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBurstpath() {
        assertEquals("/ember_p1_burst_path", EmberBurstPath.PATH_PICK_CMD);
        assertEquals("[破绽爆发路径]", EmberBurstPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBurstPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberBurstPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberBurstPath.PATH_PICK_CMD.contains("burstpath"));
        assertFalse(EmberBurstPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareBurstpath() {
        String locked = EmberBurstPath.glance(EmberBurstPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 burstpath"));
        String none = EmberBurstPath.glance(EmberBurstPath.NONE, true);
        assertTrue(none.contains("点破绽爆发路径页"));
        assertFalse(none.contains("/corerpg p1 burstpath"));
        String auto = EmberBurstPath.glance(EmberBurstPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 burstpath"));
        String ask = EmberBurstPath.glance(EmberBurstPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 burstpath"));
        String mute = EmberBurstPath.glance(EmberBurstPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 burstpath"));
    }

    @Test
    public void menuLine_labelTipReuse_burstSemanticsUnchanged() {
        assertEquals("破绽爆发路径 · 未选", EmberBurstPath.menuLine(EmberBurstPath.NONE));
        assertEquals("破绽爆发路径 · 破绽爆发·自动", EmberBurstPath.menuLine(EmberBurstPath.AUTO));
        assertEquals("破绽爆发路径 · 破绽爆发·提醒", EmberBurstPath.menuLine(EmberBurstPath.ASK));
        assertEquals("破绽爆发路径 · 破绽爆发·静默", EmberBurstPath.menuLine(EmberBurstPath.MUTE));
        assertEquals("破绽爆发·自动", EmberBurstPath.label(EmberBurstPath.AUTO));
        assertEquals("破绽爆发·提醒", EmberBurstPath.label(EmberBurstPath.ASK));
        assertEquals("破绽爆发·静默", EmberBurstPath.label(EmberBurstPath.MUTE));
        assertEquals("未选", EmberBurstPath.label(EmberBurstPath.NONE));
        assertTrue(EmberBurstPath.tip(EmberBurstPath.AUTO).contains("圈") || EmberBurstPath.tip(EmberBurstPath.AUTO).contains("自动"));
        assertTrue(EmberBurstPath.tip(EmberBurstPath.ASK).contains("提醒"));
        assertTrue(EmberBurstPath.tip(EmberBurstPath.MUTE).contains("提示") || EmberBurstPath.tip(EmberBurstPath.MUTE).contains("静默"));
        assertEquals(EmberBurstPath.AUTO, EmberBurstPath.parse("auto"));
        assertEquals(EmberBurstPath.ASK, EmberBurstPath.parse("ask"));
        assertEquals(EmberBurstPath.MUTE, EmberBurstPath.parse("mute"));
        assertEquals(EmberBurstPath.NONE, EmberBurstPath.parse("clear"));
        assertTrue(EmberBurstPath.shouldAuto(EmberBurstPath.AUTO));
        assertTrue(EmberBurstPath.shouldMute(EmberBurstPath.MUTE));
        assertFalse(EmberBurstPath.shouldAuto(EmberBurstPath.ASK));
        assertFalse(EmberBurstPath.shouldMute(EmberBurstPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareBurstpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBurstPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_burst_path"));
        assertTrue(src.contains("TIP-FA"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 burstpath"));
        assertFalse(offerBody.contains("burstpath auto"));
        assertFalse(offerBody.contains("burstpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 burstpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBurstPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ01", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ01");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void combatButtons_burstUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBurstPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[爆发]\", \"/corerpg p1 burst\""));
        assertTrue(src.contains("pathBurst"));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 burstpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterStagger"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inBurstPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBurstPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-FA"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBurstpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean burstPathCmd");
        assertTrue(block > 0);
        // next private method after burstPathCmd
        int end = src.indexOf("private boolean burstGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点破绽爆发路径页"));
        assertFalse(body.contains("用法：/corerpg p1 burstpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点破绽爆发路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 burstpath"));
    }
}
