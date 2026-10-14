package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EI D13366 W1a: BoltPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 boltpath 主 UI / 零改伤害CD掉落体力价/D589 闪线语义.
 */
public class EmberBoltPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBoltpath() {
        assertEquals("/ember_p1_bolt_path", EmberBoltPath.PATH_PICK_CMD);
        assertEquals("[闪线路径]", EmberBoltPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBoltPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberBoltPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberBoltPath.PATH_PICK_CMD.contains("boltpath"));
        assertFalse(EmberBoltPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareBoltpath() {
        String locked = EmberBoltPath.glance(EmberBoltPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 boltpath"));
        String none = EmberBoltPath.glance(EmberBoltPath.NONE, true);
        assertTrue(none.contains("点闪线路径页"));
        assertFalse(none.contains("/corerpg p1 boltpath"));
        String auto = EmberBoltPath.glance(EmberBoltPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 boltpath"));
        String ask = EmberBoltPath.glance(EmberBoltPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 boltpath"));
        String mute = EmberBoltPath.glance(EmberBoltPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 boltpath"));
    }

    @Test
    public void menuLine_labelTipReuse_boltSemanticsUnchanged() {
        assertEquals("闪线路径 · 未选", EmberBoltPath.menuLine(EmberBoltPath.NONE));
        assertEquals("闪线路径 · 闪线·自动", EmberBoltPath.menuLine(EmberBoltPath.AUTO));
        assertEquals("闪线路径 · 闪线·提醒", EmberBoltPath.menuLine(EmberBoltPath.ASK));
        assertEquals("闪线路径 · 闪线·静默", EmberBoltPath.menuLine(EmberBoltPath.MUTE));
        assertEquals("闪线·自动", EmberBoltPath.label(EmberBoltPath.AUTO));
        assertEquals("闪线·提醒", EmberBoltPath.label(EmberBoltPath.ASK));
        assertEquals("闪线·静默", EmberBoltPath.label(EmberBoltPath.MUTE));
        assertEquals("未选", EmberBoltPath.label(EmberBoltPath.NONE));
        assertTrue(EmberBoltPath.tip(EmberBoltPath.AUTO).contains("自动") || EmberBoltPath.tip(EmberBoltPath.AUTO).contains("侧移"));
        assertTrue(EmberBoltPath.tip(EmberBoltPath.ASK).contains("提醒"));
        assertTrue(EmberBoltPath.tip(EmberBoltPath.MUTE).contains("静默") || EmberBoltPath.tip(EmberBoltPath.MUTE).contains("直线"));
        assertEquals(EmberBoltPath.AUTO, EmberBoltPath.parse("auto"));
        assertEquals(EmberBoltPath.ASK, EmberBoltPath.parse("ask"));
        assertEquals(EmberBoltPath.MUTE, EmberBoltPath.parse("mute"));
        assertEquals(EmberBoltPath.NONE, EmberBoltPath.parse("clear"));
        assertTrue(EmberBoltPath.shouldAuto(EmberBoltPath.AUTO));
        assertTrue(EmberBoltPath.shouldMute(EmberBoltPath.MUTE));
        assertFalse(EmberBoltPath.shouldAuto(EmberBoltPath.ASK));
        assertFalse(EmberBoltPath.shouldMute(EmberBoltPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareBoltpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBoltPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_bolt_path"));
        assertTrue(src.contains("TIP-EI"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 boltpath"));
        assertFalse(offerBody.contains("boltpath auto"));
        assertFalse(offerBody.contains("boltpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 boltpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBoltPath.java")),
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
    public void combatButtons_boltUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBoltPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[闪线]\", \"/corerpg p1 bolt\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 boltpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathBoltOut"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inBoltPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBoltPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EI"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBoltpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean boltPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean boltGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点闪线路径页"));
        assertFalse(body.contains("用法：/corerpg p1 boltpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点闪线路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 boltpath"));
    }
}
