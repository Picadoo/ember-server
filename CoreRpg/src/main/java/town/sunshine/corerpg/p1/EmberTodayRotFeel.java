package town.sunshine.corerpg.p1;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * TIP-CO D13320: short 「今日推」本局内可感 — session todayRot + enter/room/settle cues.
 * Zero daycap / S40–S58 / Cast / Aid / stamina / todayKey algorithm.
 */
public final class EmberTodayRotFeel {

    /** Same-room ActionBar throttle (ms). */
    public static final long ROOM_THROTTLE_MS = 15_000L;

    public static final String ENTER_TELL = "§6【今日推】§f入场 · §7本局有今日标记";
    public static final String ENTER_AB = "§6今日推·本";
    public static final String ENTER_TITLE = "§6今日推·本";
    public static final String SETTLE_DONE = "§a今日推完成 §7· 日更本已通";
    public static final String REENTER_LABEL = "[再进今日推]";
    public static final String REENTER_CMD = "/corerpg p1 enter short-today";
    public static final String REENTER_HOVER = "进今日轮换短征";

    private EmberTodayRotFeel() {}

    /** Bukkit-free: short expedition whose key is today's rotation. */
    public static boolean isTodayRot(boolean shortExpedition, String mapKey, String todayKey) {
        if (!shortExpedition || mapKey == null || mapKey.isEmpty()) return false;
        if (todayKey == null || todayKey.isEmpty()) return false;
        return mapKey.equalsIgnoreCase(todayKey);
    }

    public static boolean isTodayRot(EmberRunMaps.MapDef m, String todayKey) {
        return m != null && isTodayRot(m.shortExpedition, m.key, todayKey);
    }

    /** Stamp session flag when enter commits to today's short. */
    public static boolean markIfToday(EmberRunSession s, EmberRunMaps.MapDef m, String todayKey) {
        if (s == null || !isTodayRot(m, todayKey)) return false;
        s.todayRot = true;
        return true;
    }

    public static String roomAb(int roomOrd1Based) {
        int n = Math.max(1, roomOrd1Based);
        return "§6今日推 · 第" + n + "房";
    }

    /**
     * Same-room throttle ≥15s; different room always allowed.
     * @return true when caller should flash the room AB
     */
    public static boolean shouldRoomCue(boolean todayRot, String roomId,
                                        String lastRoomId, long lastAtMs, long nowMs) {
        if (!todayRot || roomId == null || roomId.isEmpty()) return false;
        if (lastRoomId != null && roomId.equals(lastRoomId)
                && lastAtMs > 0L && nowMs - lastAtMs < ROOM_THROTTLE_MS) {
            return false;
        }
        return true;
    }

    /** Enter cue: title + ActionBar + tell（可叠 EmberMapEnterFeel 短征行）. */
    public static void enterCue(Player p) {
        if (p == null) return;
        try { p.sendTitle(ENTER_TITLE, "§7日更本 · 本局有今日标记", 5, 35, 10); } catch (Throwable ignored) { }
        try { p.sendActionBar(ENTER_AB); } catch (Throwable ignored) { }
        try { p.sendMessage(EmberRunService.P + ENTER_TELL); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.45f, 1.35f);
        } catch (Throwable ignored) { }
    }

    /** Room open/enter AB when todayRot; updates session throttle fields. */
    public static void roomCue(EmberRunSession s, EmberRunMaps.MapDef m, EmberRunMaps.Room r, Player p) {
        if (s == null || !s.todayRot || r == null || p == null) return;
        long now = System.currentTimeMillis();
        if (!shouldRoomCue(true, r.id, s.todayRotRoomCueId, s.todayRotRoomCueAt, now)) return;
        int ord = 1;
        if (m != null) {
            int idx = m.roomIndex(r.id);
            if (idx >= 0) ord = idx + 1;
        }
        String ab = roomAb(ord);
        try { p.sendActionBar(ab); } catch (Throwable ignored) { }
        s.todayRotRoomCueId = r.id;
        s.todayRotRoomCueAt = now;
    }

    /** Rewarded settle append: done tell + re-enter button. */
    public static void settleCue(Player p, boolean todayRot, boolean pays) {
        if (p == null || !todayRot || !pays) return;
        try { p.sendMessage(EmberRunService.P + SETTLE_DONE); } catch (Throwable ignored) { }
        try {
            town.sunshine.corerpg.ConfirmTokens.sendButton(p, EmberRunService.P + "§7今日推：",
                    REENTER_LABEL, REENTER_CMD, REENTER_HOVER);
        } catch (Throwable ignored) { }
    }
}
