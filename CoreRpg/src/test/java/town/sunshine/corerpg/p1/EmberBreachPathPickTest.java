package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EU D13378 W1a: BreachPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 breachpath 主 UI / 零改伤害CD掉落体力价/D569 裂隙语义.
 */
public class EmberBreachPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBreachpath() {
        assertEquals("/ember_p1_breach_path", EmberBreachPath.PATH_PICK_CMD);
        assertEquals("[裂隙路径]", EmberBreachPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBreachPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberBreachPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberBreachPath.PATH_PICK_CMD.contains("breachpath"));
        assertFalse(EmberBreachPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareBreachpath() {
        String locked = EmberBreachPath.glance(EmberBreachPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 breachpath"));
        String none = EmberBreachPath.glance(EmberBreachPath.NONE, true);
        assertTrue(none.contains("点裂隙路径页"));
        assertFalse(none.contains("/corerpg p1 breachpath"));
        String auto = EmberBreachPath.glance(EmberBreachPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 breachpath"));
        String ask = EmberBreachPath.glance(EmberBreachPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 breachpath"));
        String mute = EmberBreachPath.glance(EmberBreachPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 breachpath"));
    }

    @Test
    public void menuLine_labelTipReuse_breachSemanticsUnchanged() {
        assertEquals("裂隙路径 · 未选", EmberBreachPath.menuLine(EmberBreachPath.NONE));
        assertEquals("裂隙路径 · 裂隙·自动", EmberBreachPath.menuLine(EmberBreachPath.AUTO));
        assertEquals("裂隙路径 · 裂隙·提醒", EmberBreachPath.menuLine(EmberBreachPath.ASK));
        assertEquals("裂隙路径 · 裂隙·静默", EmberBreachPath.menuLine(EmberBreachPath.MUTE));
        assertEquals("裂隙·自动", EmberBreachPath.label(EmberBreachPath.AUTO));
        assertEquals("裂隙·提醒", EmberBreachPath.label(EmberBreachPath.ASK));
        assertEquals("裂隙·静默", EmberBreachPath.label(EmberBreachPath.MUTE));
        assertEquals("未选", EmberBreachPath.label(EmberBreachPath.NONE));
        assertTrue(EmberBreachPath.tip(EmberBreachPath.AUTO).contains("紫圈") || EmberBreachPath.tip(EmberBreachPath.AUTO).contains("自动"));
        assertTrue(EmberBreachPath.tip(EmberBreachPath.ASK).contains("提醒"));
        assertTrue(EmberBreachPath.tip(EmberBreachPath.MUTE).contains("紫圈") || EmberBreachPath.tip(EmberBreachPath.MUTE).contains("提醒"));
        assertEquals(EmberBreachPath.AUTO, EmberBreachPath.parse("auto"));
        assertEquals(EmberBreachPath.ASK, EmberBreachPath.parse("ask"));
        assertEquals(EmberBreachPath.MUTE, EmberBreachPath.parse("mute"));
        assertEquals(EmberBreachPath.NONE, EmberBreachPath.parse("clear"));
        assertTrue(EmberBreachPath.shouldAuto(EmberBreachPath.AUTO));
        assertTrue(EmberBreachPath.shouldMute(EmberBreachPath.MUTE));
        assertFalse(EmberBreachPath.shouldAuto(EmberBreachPath.ASK));
        assertFalse(EmberBreachPath.shouldMute(EmberBreachPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareBreachpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBreachPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_breach_path"));
        assertTrue(src.contains("TIP-EU"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 breachpath"));
        assertFalse(offerBody.contains("breachpath auto"));
        assertFalse(offerBody.contains("breachpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 breachpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBreachPath.java")),
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
    public void combatButtons_breachUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBreachPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[裂隙]\", \"/corerpg p1 breach\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 breachpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterBreach"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inBreachPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBreachPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EU"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBreachpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean breachPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean breachGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点裂隙路径页"));
        assertFalse(body.contains("用法：/corerpg p1 breachpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点裂隙路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 breachpath"));
    }
}
