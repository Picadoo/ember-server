package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-ES D13376 W1a: StockPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 stockpath 主 UI / 零改伤害CD掉落体力价/D599 备药语义.
 */
public class EmberStockPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareStockpath() {
        assertEquals("/ember_p1_stock_path", EmberStockPath.PATH_PICK_CMD);
        assertEquals("[备药路径]", EmberStockPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberStockPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberStockPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberStockPath.PATH_PICK_CMD.contains("stockpath"));
        assertFalse(EmberStockPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareStockpath() {
        String locked = EmberStockPath.glance(EmberStockPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 stockpath"));
        String none = EmberStockPath.glance(EmberStockPath.NONE, true);
        assertTrue(none.contains("点备药路径页"));
        assertFalse(none.contains("/corerpg p1 stockpath"));
        String auto = EmberStockPath.glance(EmberStockPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 stockpath"));
        String ask = EmberStockPath.glance(EmberStockPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 stockpath"));
        String mute = EmberStockPath.glance(EmberStockPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 stockpath"));
    }

    @Test
    public void menuLine_labelTipReuse_stockSemanticsUnchanged() {
        assertEquals("备药路径 · 未选", EmberStockPath.menuLine(EmberStockPath.NONE));
        assertEquals("备药路径 · 备药·自动", EmberStockPath.menuLine(EmberStockPath.AUTO));
        assertEquals("备药路径 · 备药·提醒", EmberStockPath.menuLine(EmberStockPath.ASK));
        assertEquals("备药路径 · 备药·静默", EmberStockPath.menuLine(EmberStockPath.MUTE));
        assertEquals("备药·自动", EmberStockPath.label(EmberStockPath.AUTO));
        assertEquals("备药·提醒", EmberStockPath.label(EmberStockPath.ASK));
        assertEquals("备药·静默", EmberStockPath.label(EmberStockPath.MUTE));
        assertEquals("未选", EmberStockPath.label(EmberStockPath.NONE));
        assertTrue(EmberStockPath.tip(EmberStockPath.AUTO).contains("核对") || EmberStockPath.tip(EmberStockPath.AUTO).contains("回复药") || EmberStockPath.tip(EmberStockPath.AUTO).contains("自动"));
        assertTrue(EmberStockPath.tip(EmberStockPath.ASK).contains("提醒"));
        assertTrue(EmberStockPath.tip(EmberStockPath.MUTE).contains("提醒") || EmberStockPath.tip(EmberStockPath.MUTE).contains("备药"));
        assertEquals(EmberStockPath.AUTO, EmberStockPath.parse("auto"));
        assertEquals(EmberStockPath.ASK, EmberStockPath.parse("ask"));
        assertEquals(EmberStockPath.MUTE, EmberStockPath.parse("mute"));
        assertEquals(EmberStockPath.NONE, EmberStockPath.parse("clear"));
        assertTrue(EmberStockPath.shouldAuto(EmberStockPath.AUTO));
        assertTrue(EmberStockPath.shouldMute(EmberStockPath.MUTE));
        assertFalse(EmberStockPath.shouldAuto(EmberStockPath.ASK));
        assertFalse(EmberStockPath.shouldMute(EmberStockPath.ASK));
        assertEquals(2, EmberStockPath.NEED);
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareStockpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStockPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_stock_path"));
        assertTrue(src.contains("TIP-ES"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 stockpath"));
        assertFalse(offerBody.contains("stockpath auto"));
        assertFalse(offerBody.contains("stockpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 stockpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStockPath.java")),
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
    public void combatButtons_stockUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStockPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[核对]\", \"/corerpg p1 stock\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 stockpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterStock"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inStockPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStockPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-ES"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareStockpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean stockPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean stockGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点备药路径页"));
        assertFalse(body.contains("用法：/corerpg p1 stockpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点备药路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 stockpath"));
    }
}
