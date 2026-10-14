package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EM D13370 W1a: SequelPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 sequelpath 主 UI / 零改伤害CD掉落体力价/D593 续招预站语义.
 */
public class EmberSequelPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSequelpath() {
        assertEquals("/ember_p1_sequel_path", EmberSequelPath.PATH_PICK_CMD);
        assertEquals("[续招路径]", EmberSequelPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSequelPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSequelPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSequelPath.PATH_PICK_CMD.contains("sequelpath"));
        assertFalse(EmberSequelPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSequelpath() {
        String locked = EmberSequelPath.glance(EmberSequelPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 sequelpath"));
        String none = EmberSequelPath.glance(EmberSequelPath.NONE, true);
        assertTrue(none.contains("点续招路径页"));
        assertFalse(none.contains("/corerpg p1 sequelpath"));
        String auto = EmberSequelPath.glance(EmberSequelPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 sequelpath"));
        String ask = EmberSequelPath.glance(EmberSequelPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 sequelpath"));
        String mute = EmberSequelPath.glance(EmberSequelPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 sequelpath"));
    }

    @Test
    public void menuLine_labelTipReuse_sequelSemanticsUnchanged() {
        assertEquals("续招路径 · 未选", EmberSequelPath.menuLine(EmberSequelPath.NONE));
        assertEquals("续招路径 · 续招·自动", EmberSequelPath.menuLine(EmberSequelPath.AUTO));
        assertEquals("续招路径 · 续招·提醒", EmberSequelPath.menuLine(EmberSequelPath.ASK));
        assertEquals("续招路径 · 续招·静默", EmberSequelPath.menuLine(EmberSequelPath.MUTE));
        assertEquals("续招·自动", EmberSequelPath.label(EmberSequelPath.AUTO));
        assertEquals("续招·提醒", EmberSequelPath.label(EmberSequelPath.ASK));
        assertEquals("续招·静默", EmberSequelPath.label(EmberSequelPath.MUTE));
        assertEquals("未选", EmberSequelPath.label(EmberSequelPath.NONE));
        assertTrue(EmberSequelPath.tip(EmberSequelPath.AUTO).contains("自动") || EmberSequelPath.tip(EmberSequelPath.AUTO).contains("预站"));
        assertTrue(EmberSequelPath.tip(EmberSequelPath.ASK).contains("提醒"));
        assertTrue(EmberSequelPath.tip(EmberSequelPath.MUTE).contains("静默") || EmberSequelPath.tip(EmberSequelPath.MUTE).contains("预站"));
        assertEquals(EmberSequelPath.AUTO, EmberSequelPath.parse("auto"));
        assertEquals(EmberSequelPath.ASK, EmberSequelPath.parse("ask"));
        assertEquals(EmberSequelPath.MUTE, EmberSequelPath.parse("mute"));
        assertEquals(EmberSequelPath.NONE, EmberSequelPath.parse("clear"));
        assertTrue(EmberSequelPath.shouldAuto(EmberSequelPath.AUTO));
        assertTrue(EmberSequelPath.shouldMute(EmberSequelPath.MUTE));
        assertFalse(EmberSequelPath.shouldAuto(EmberSequelPath.ASK));
        assertFalse(EmberSequelPath.shouldMute(EmberSequelPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSequelpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSequelPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_sequel_path"));
        assertTrue(src.contains("TIP-EM"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 sequelpath"));
        assertFalse(offerBody.contains("sequelpath auto"));
        assertFalse(offerBody.contains("sequelpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 sequelpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSequelPath.java")),
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
    public void combatButtons_preStandUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSequelPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[预站]\", \"/corerpg p1 sequel\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 sequelpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSequelReady"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSequelPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSequelPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EM"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSequelpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean sequelPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean sequelGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点续招路径页"));
        assertFalse(body.contains("用法：/corerpg p1 sequelpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点续招路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 sequelpath"));
    }
}
