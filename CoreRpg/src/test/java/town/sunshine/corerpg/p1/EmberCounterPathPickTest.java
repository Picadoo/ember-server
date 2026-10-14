package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DL D13343 W1a: CounterPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 counterpath/enter 主 UI / 零改伤害CD掉落体力价/破绽眩晕加秒.
 */
public class EmberCounterPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareCounterpathOrEnter() {
        assertEquals("/ember_p1_counter_path", EmberCounterPath.PATH_PICK_CMD);
        assertEquals("[破绽路径]", EmberCounterPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCounterPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberCounterPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberCounterPath.PATH_PICK_CMD.contains("counterpath"));
        assertFalse(EmberCounterPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareCounterpath() {
        String locked = EmberCounterPath.glance(EmberCounterPath.NONE, false);
        assertTrue(locked.contains("Q02"));
        assertFalse(locked.contains("/corerpg p1 counterpath"));
        String none = EmberCounterPath.glance(EmberCounterPath.NONE, true);
        assertTrue(none.contains("点破绽路径页"));
        assertFalse(none.contains("/corerpg p1 counterpath"));
        String wall = EmberCounterPath.glance(EmberCounterPath.WALL, true);
        assertTrue(wall.contains("撞墙"));
        assertFalse(wall.contains("/corerpg p1 counterpath"));
        String whiff = EmberCounterPath.glance(EmberCounterPath.WHIFF, true);
        assertTrue(whiff.contains("落空"));
        assertFalse(whiff.contains("/corerpg p1 counterpath"));
        String br = EmberCounterPath.glance(EmberCounterPath.BREAK, true);
        assertTrue(br.contains("破招"));
        assertFalse(br.contains("/corerpg p1 counterpath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("破绽路径 · 未选", EmberCounterPath.menuLine(EmberCounterPath.NONE));
        assertEquals("破绽路径 · 撞墙破绽", EmberCounterPath.menuLine(EmberCounterPath.WALL));
        assertEquals("破绽路径 · 落空破绽", EmberCounterPath.menuLine(EmberCounterPath.WHIFF));
        assertEquals("破绽路径 · 破招通道", EmberCounterPath.menuLine(EmberCounterPath.BREAK));
        assertEquals("撞墙破绽", EmberCounterPath.label(EmberCounterPath.WALL));
        assertEquals("落空破绽", EmberCounterPath.label(EmberCounterPath.WHIFF));
        assertEquals("破招通道", EmberCounterPath.label(EmberCounterPath.BREAK));
        assertEquals("未选", EmberCounterPath.label(EmberCounterPath.NONE));
        assertTrue(EmberCounterPath.tip(EmberCounterPath.WALL).contains("Q02"));
        assertTrue(EmberCounterPath.tip(EmberCounterPath.WHIFF).contains("落空"));
        assertTrue(EmberCounterPath.tip(EmberCounterPath.BREAK).contains("破招"));
    }

    @Test
    public void preferredStunBonus_unchanged_zeroPoint12() {
        // D480 preferredStunBonusSec +0.12s semantics must stay; method still present.
        assertEquals(0.0, EmberCounterPath.preferredStunBonusSec(null, null, EmberCounterPath.KIND_WALL), 0.0);
        assertEquals(0.0, EmberCounterPath.preferredStunBonusSec(null, null, EmberCounterPath.KIND_WHIFF), 0.0);
        assertEquals(0.0, EmberCounterPath.preferredStunBonusSec(null, null, EmberCounterPath.KIND_BREAK), 0.0);
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareCounterEnterMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCounterPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_counter_path"));
        assertTrue(src.contains("TIP-DL"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 counterpath"));
        assertFalse(offerBody.contains("counterpath wall"));
        assertFalse(offerBody.contains("counterpath whiff"));
        assertFalse(offerBody.contains("counterpath break"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 counterpath"));
        assertFalse(applyBody.contains("enterCmd(id)"));
        assertFalse(applyBody.contains("\"/corerpg p1 enter"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCounterPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ02", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ02");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inCounterPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCounterPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DL"));
        assertTrue(src.contains("preferredStunBonusSec"));
        assertTrue(src.contains("0.12"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareCounterpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("if (\"counterpath\".equals(sub)");
        assertTrue(block > 0);
        int end = src.indexOf("if (\"codexseed\".equals(sub)", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点破绽路径页"));
        assertFalse(body.contains("用法：/corerpg p1 counterpath <wall|whiff|break|clear>"));
        assertTrue(body.contains("用法：点破绽路径页选撞墙/落空/破招/清空"));
    }
}
