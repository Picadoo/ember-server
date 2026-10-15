package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-ER D13375 W1a: HurryPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 hurrypath 主 UI / 零改伤害CD掉落体力价/D598 急清语义.
 */
public class EmberHurryPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareHurrypath() {
        assertEquals("/ember_p1_hurry_path", EmberHurryPath.PATH_PICK_CMD);
        assertEquals("[急清路径]", EmberHurryPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberHurryPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberHurryPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberHurryPath.PATH_PICK_CMD.contains("hurrypath"));
        assertFalse(EmberHurryPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareHurrypath() {
        String locked = EmberHurryPath.glance(EmberHurryPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 hurrypath"));
        String none = EmberHurryPath.glance(EmberHurryPath.NONE, true);
        assertTrue(none.contains("点急清路径页"));
        assertFalse(none.contains("/corerpg p1 hurrypath"));
        String auto = EmberHurryPath.glance(EmberHurryPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 hurrypath"));
        String ask = EmberHurryPath.glance(EmberHurryPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 hurrypath"));
        String mute = EmberHurryPath.glance(EmberHurryPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 hurrypath"));
    }

    @Test
    public void menuLine_labelTipReuse_hurrySemanticsUnchanged() {
        assertEquals("急清路径 · 未选", EmberHurryPath.menuLine(EmberHurryPath.NONE));
        assertEquals("急清路径 · 急清·自动", EmberHurryPath.menuLine(EmberHurryPath.AUTO));
        assertEquals("急清路径 · 急清·提醒", EmberHurryPath.menuLine(EmberHurryPath.ASK));
        assertEquals("急清路径 · 急清·静默", EmberHurryPath.menuLine(EmberHurryPath.MUTE));
        assertEquals("急清·自动", EmberHurryPath.label(EmberHurryPath.AUTO));
        assertEquals("急清·提醒", EmberHurryPath.label(EmberHurryPath.ASK));
        assertEquals("急清·静默", EmberHurryPath.label(EmberHurryPath.MUTE));
        assertEquals("未选", EmberHurryPath.label(EmberHurryPath.NONE));
        assertTrue(EmberHurryPath.tip(EmberHurryPath.AUTO).contains("10") || EmberHurryPath.tip(EmberHurryPath.AUTO).contains("急清") || EmberHurryPath.tip(EmberHurryPath.AUTO).contains("自动"));
        assertTrue(EmberHurryPath.tip(EmberHurryPath.ASK).contains("提醒"));
        assertTrue(EmberHurryPath.tip(EmberHurryPath.MUTE).contains("提醒") || EmberHurryPath.tip(EmberHurryPath.MUTE).contains("倒计时"));
        assertEquals(EmberHurryPath.AUTO, EmberHurryPath.parse("auto"));
        assertEquals(EmberHurryPath.ASK, EmberHurryPath.parse("ask"));
        assertEquals(EmberHurryPath.MUTE, EmberHurryPath.parse("mute"));
        assertEquals(EmberHurryPath.NONE, EmberHurryPath.parse("clear"));
        assertTrue(EmberHurryPath.shouldAuto(EmberHurryPath.AUTO));
        assertTrue(EmberHurryPath.shouldMute(EmberHurryPath.MUTE));
        assertFalse(EmberHurryPath.shouldAuto(EmberHurryPath.ASK));
        assertFalse(EmberHurryPath.shouldMute(EmberHurryPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareHurrypathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHurryPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_hurry_path"));
        assertTrue(src.contains("TIP-ER"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 hurrypath"));
        assertFalse(offerBody.contains("hurrypath auto"));
        assertFalse(offerBody.contains("hurrypath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 hurrypath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHurryPath.java")),
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
    public void combatButtons_hurryUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHurryPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[冲刺]\", \"/corerpg p1 hurry\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 hurrypath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterHurry"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inHurryPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHurryPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-ER"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareHurrypath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean hurryPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean hurryGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点急清路径页"));
        assertFalse(body.contains("用法：/corerpg p1 hurrypath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点急清路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 hurrypath"));
    }
}
