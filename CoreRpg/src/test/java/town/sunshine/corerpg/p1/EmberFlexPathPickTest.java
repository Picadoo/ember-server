package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DN D13345 W1a: FlexPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 flexpath/steppath 主 UI / 零改伤害CD掉落体力价/轻技规则语义(D494).
 */
public class EmberFlexPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareFlexpathOrSteppath() {
        assertEquals("/ember_p1_flex_path", EmberFlexPath.PATH_PICK_CMD);
        assertEquals("[轻技路径]", EmberFlexPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberFlexPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberFlexPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberFlexPath.PATH_PICK_CMD.contains("flexpath"));
        assertFalse(EmberFlexPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareFlexpath() {
        String locked = EmberFlexPath.glance(EmberFlexPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 flexpath"));
        String none = EmberFlexPath.glance(EmberFlexPath.NONE, true);
        assertTrue(none.contains("点轻技路径页"));
        assertFalse(none.contains("/corerpg p1 flexpath"));
        String on = EmberFlexPath.glance(EmberFlexPath.ON, true);
        assertTrue(on.contains("装配踏步"));
        assertFalse(on.contains("/corerpg p1 flexpath"));
        String off = EmberFlexPath.glance(EmberFlexPath.OFF, true);
        assertTrue(off.contains("卸下轻技"));
        assertFalse(off.contains("/corerpg p1 flexpath"));
    }

    @Test
    public void menuLine_labelTipReuse_flexSemanticsUnchanged() {
        assertEquals("轻技路径 · 未选", EmberFlexPath.menuLine(EmberFlexPath.NONE));
        assertEquals("轻技路径 · 装配踏步", EmberFlexPath.menuLine(EmberFlexPath.ON));
        assertEquals("轻技路径 · 卸下轻技", EmberFlexPath.menuLine(EmberFlexPath.OFF));
        assertEquals("装配踏步", EmberFlexPath.label(EmberFlexPath.ON));
        assertEquals("卸下轻技", EmberFlexPath.label(EmberFlexPath.OFF));
        assertEquals("未选", EmberFlexPath.label(EmberFlexPath.NONE));
        assertTrue(EmberFlexPath.tip(EmberFlexPath.ON).contains("身法"));
        assertTrue(EmberFlexPath.tip(EmberFlexPath.OFF).contains("不放"));
        assertEquals(EmberFlexPath.ON, EmberFlexPath.parse("on"));
        assertEquals(EmberFlexPath.OFF, EmberFlexPath.parse("off"));
        assertEquals(EmberFlexPath.NONE, EmberFlexPath.parse("clear"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareFlexSteppathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFlexPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_flex_path"));
        assertTrue(src.contains("TIP-DN"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 flexpath"));
        assertFalse(offerBody.contains("flexpath on"));
        assertFalse(offerBody.contains("flexpath off"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 flexpath"));
        assertFalse(applyBody.contains("\"/corerpg p1 steppath"));
        // D494 equip/unequip write still present
        assertTrue(applyBody.contains("cmdEquip") || applyBody.contains("flex.cmdEquip"));
        assertTrue(applyBody.contains("cmdUnequip") || applyBody.contains("flex.cmdUnequip"));
        assertTrue(applyBody.contains("FlexSkillService.PILOT_ID"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFlexPath.java")),
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
    public void dmgCdDropStaminaUntouched_inFlexPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFlexPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DN"));
        assertTrue(src.contains("cmdEquip") || src.contains("flex.cmdEquip"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareFlexpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("if (\"flexpath\".equals(sub)");
        assertTrue(block > 0);
        int end = src.indexOf("if (\"familypath\".equals(sub)", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点轻技路径页"));
        assertFalse(body.contains("用法：/corerpg p1 flexpath <on|off|clear>"));
        assertTrue(body.contains("用法：点轻技路径页选装配踏步/卸下轻技/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 flexpath"));
    }
}
