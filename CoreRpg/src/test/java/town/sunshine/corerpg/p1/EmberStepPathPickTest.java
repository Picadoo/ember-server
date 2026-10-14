package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DO D13346 W1a: StepPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 steppath/enter 主 UI / 零改伤害CD掉落体力价/身法规则语义(D491).
 */
public class EmberStepPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSteppathOrEnter() {
        assertEquals("/ember_p1_step_path", EmberStepPath.PATH_PICK_CMD);
        assertEquals("[身法路径]", EmberStepPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberStepPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberStepPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberStepPath.PATH_PICK_CMD.contains("steppath"));
        assertFalse(EmberStepPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSteppath() {
        String locked = EmberStepPath.glance(EmberStepPath.NONE, false);
        assertTrue(locked.contains("Q05"));
        assertFalse(locked.contains("/corerpg p1 steppath"));
        String none = EmberStepPath.glance(EmberStepPath.NONE, true);
        assertTrue(none.contains("点身法路径页"));
        assertFalse(none.contains("/corerpg p1 steppath"));
        String fwd = EmberStepPath.glance(EmberStepPath.FORWARD, true);
        assertTrue(fwd.contains("前冲"));
        assertFalse(fwd.contains("/corerpg p1 steppath"));
        String back = EmberStepPath.glance(EmberStepPath.BACK, true);
        assertTrue(back.contains("后撤"));
        assertFalse(back.contains("/corerpg p1 steppath"));
    }

    @Test
    public void menuLine_labelTipReuse_stepSemanticsUnchanged() {
        assertEquals("身法路径 · 未选", EmberStepPath.menuLine(EmberStepPath.NONE));
        assertEquals("身法路径 · 前冲", EmberStepPath.menuLine(EmberStepPath.FORWARD));
        assertEquals("身法路径 · 后撤", EmberStepPath.menuLine(EmberStepPath.BACK));
        assertEquals("前冲", EmberStepPath.label(EmberStepPath.FORWARD));
        assertEquals("后撤", EmberStepPath.label(EmberStepPath.BACK));
        assertEquals("未选", EmberStepPath.label(EmberStepPath.NONE));
        assertTrue(EmberStepPath.tip(EmberStepPath.FORWARD).contains("前"));
        assertTrue(EmberStepPath.tip(EmberStepPath.BACK).contains("后"));
        assertEquals(EmberStepPath.FORWARD, EmberStepPath.parse("forward"));
        assertEquals(EmberStepPath.BACK, EmberStepPath.parse("back"));
        assertEquals(EmberStepPath.NONE, EmberStepPath.parse("clear"));
        assertEquals(EmberSkillKit.DIR_FORWARD, EmberStepPath.toDirId(EmberStepPath.FORWARD));
        assertEquals(EmberSkillKit.DIR_BACK, EmberStepPath.toDirId(EmberStepPath.BACK));
        assertTrue(EmberStepPath.enterCmd(EmberStepPath.FORWARD).contains("q05"));
        assertTrue(EmberStepPath.enterCmd(EmberStepPath.BACK).contains("q02"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSteppathEnterMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStepPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_step_path"));
        assertTrue(src.contains("TIP-DO"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 steppath"));
        assertFalse(offerBody.contains("steppath forward"));
        assertFalse(offerBody.contains("steppath back"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static void applyDir", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 steppath"));
        assertFalse(applyBody.contains("enterCmd(id)"));
        assertFalse(applyBody.contains("enterLabel(id)"));
        // D491 dir write still present
        assertTrue(applyBody.contains("applyDir") || applyBody.contains("toDirId"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStepPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ05", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ05");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inStepPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStepPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DO"));
        assertTrue(src.contains("applyDir") || src.contains("cmdStepDir"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSteppath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("if (\"steppath\".equals(sub)");
        assertTrue(block > 0);
        int end = src.indexOf("if (\"shapepath\".equals(sub)", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点身法路径页"));
        assertFalse(body.contains("用法：/corerpg p1 steppath <forward|back|clear>"));
        assertTrue(body.contains("用法：点身法路径页选前冲/后撤/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 steppath"));
    }
}
