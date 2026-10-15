package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-FB D13385 W1a: RallyPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 rallypath 主 UI / 零改伤害CD掉落体力价/D562 清房突进语义.
 */
public class EmberRallyPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBurstpath() {
        assertEquals("/ember_p1_rally_path", EmberRallyPath.PATH_PICK_CMD);
        assertEquals("[清房突进路径]", EmberRallyPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberRallyPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberRallyPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberRallyPath.PATH_PICK_CMD.contains("rallypath"));
        assertFalse(EmberRallyPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareRallypath() {
        String locked = EmberRallyPath.glance(EmberRallyPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 rallypath"));
        String none = EmberRallyPath.glance(EmberRallyPath.NONE, true);
        assertTrue(none.contains("点清房突进路径页"));
        assertFalse(none.contains("/corerpg p1 rallypath"));
        String auto = EmberRallyPath.glance(EmberRallyPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 rallypath"));
        String ask = EmberRallyPath.glance(EmberRallyPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 rallypath"));
        String mute = EmberRallyPath.glance(EmberRallyPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 rallypath"));
    }

    @Test
    public void menuLine_labelTipReuse_rallySemanticsUnchanged() {
        assertEquals("清房突进路径 · 未选", EmberRallyPath.menuLine(EmberRallyPath.NONE));
        assertEquals("清房突进路径 · 清房突进·自动", EmberRallyPath.menuLine(EmberRallyPath.AUTO));
        assertEquals("清房突进路径 · 清房突进·提醒", EmberRallyPath.menuLine(EmberRallyPath.ASK));
        assertEquals("清房突进路径 · 清房突进·静默", EmberRallyPath.menuLine(EmberRallyPath.MUTE));
        assertEquals("清房突进·自动", EmberRallyPath.label(EmberRallyPath.AUTO));
        assertEquals("清房突进·提醒", EmberRallyPath.label(EmberRallyPath.ASK));
        assertEquals("清房突进·静默", EmberRallyPath.label(EmberRallyPath.MUTE));
        assertEquals("未选", EmberRallyPath.label(EmberRallyPath.NONE));
        assertTrue(EmberRallyPath.tip(EmberRallyPath.AUTO).contains("房") || EmberRallyPath.tip(EmberRallyPath.AUTO).contains("自动"));
        assertTrue(EmberRallyPath.tip(EmberRallyPath.ASK).contains("提醒"));
        assertTrue(EmberRallyPath.tip(EmberRallyPath.MUTE).contains("走") || EmberRallyPath.tip(EmberRallyPath.MUTE).contains("静默"));
        assertEquals(EmberRallyPath.AUTO, EmberRallyPath.parse("auto"));
        assertEquals(EmberRallyPath.ASK, EmberRallyPath.parse("ask"));
        assertEquals(EmberRallyPath.MUTE, EmberRallyPath.parse("mute"));
        assertEquals(EmberRallyPath.NONE, EmberRallyPath.parse("clear"));
        assertTrue(EmberRallyPath.shouldAuto(EmberRallyPath.AUTO));
        assertTrue(EmberRallyPath.shouldMute(EmberRallyPath.MUTE));
        assertFalse(EmberRallyPath.shouldAuto(EmberRallyPath.ASK));
        assertFalse(EmberRallyPath.shouldMute(EmberRallyPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareRallypathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRallyPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_rally_path"));
        assertTrue(src.contains("TIP-FB"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 rallypath"));
        assertFalse(offerBody.contains("rallypath auto"));
        assertFalse(offerBody.contains("rallypath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 rallypath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRallyPath.java")),
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
    public void combatButtons_rallyUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRallyPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[清房突进]\", \"/corerpg p1 rally\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 rallypath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterClear"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inRallyPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRallyPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-FB"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBurstpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean rallyPathCmd");
        assertTrue(block > 0);
        // next private method after burstPathCmd
        int end = src.indexOf("private boolean rallyGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点清房突进路径页"));
        assertFalse(body.contains("用法：/corerpg p1 rallypath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点清房突进路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 rallypath"));
    }
}
