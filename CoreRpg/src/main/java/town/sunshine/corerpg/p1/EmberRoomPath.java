package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D506: variety room-order path — front / mid / back.
 * NEW play: remaps affix/event room slots on repeat-run variety (D138).
 * Party-leader sticky after spice bias. Chat only. Zero rates/tables / AFK / sx.
 * Not spice kind-bias twin (rooms, not affix/event ids), not combo sticky,
 * not prep/short/twist/refund twin, not forge/enter/ActionBar cue.
 */
public final class EmberRoomPath {

    public static final String C_PATH = "p1_room_path";
    public static final String C_OFFER = "p1_room_path_offer";

    public static final int NONE = 0;
    public static final int FRONT = 1; // r1
    public static final int MID = 2;   // r2
    public static final int BACK = 3;  // r3

    private EmberRoomPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FRONT || id == MID || id == BACK;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_PATH, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "取消".equals(s)) return NONE;
        if ("front".equals(s) || "r1".equals(s) || "前房".equals(s) || "1".equals(s)) return FRONT;
        if ("mid".equals(s) || "r2".equals(s) || "中房".equals(s) || "2".equals(s)) return MID;
        if ("back".equals(s) || "r3".equals(s) || "后房".equals(s) || "3".equals(s)) return BACK;
        return -1;
    }

    public static String key(int id) {
        if (id == FRONT) return "front";
        if (id == MID) return "mid";
        if (id == BACK) return "back";
        return "none";
    }

    public static String roomId(int id) {
        if (id == FRONT) return "r1";
        if (id == MID) return "r2";
        if (id == BACK) return "r3";
        return null;
    }

    public static String label(int id) {
        if (id == FRONT) return "房序·前房";
        if (id == MID) return "房序·中房";
        if (id == BACK) return "房序·后房";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FRONT) return "重打本花样词缀/事件尽量落在第 1 间";
        if (id == MID) return "重打本花样词缀/事件尽量落在第 2 间";
        if (id == BACK) return "重打本花样词缀/事件尽量落在第 3 间";
        return "点选花样落房偏好（改节奏 · 不改种类表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8房序路径：首进/首通 Q02 后可选（重打本花样）";
        if (!valid(id)) return "§8房序路径：未选 · /corerpg p1 roompath";
        return "§e房序路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Remap affix/event rooms to preferred slot when that outcome exists.
     * D2514: also pins event room when eventKind present without room; never invents kinds.
     * Bukkit-free.
     */
    public static String[] applyBias(String[] rolled, int path) {
        String[] v = new String[4];
        v[0] = rolled != null && rolled.length > 0 && rolled[0] != null ? rolled[0] : "";
        v[1] = rolled != null && rolled.length > 1 && rolled[1] != null ? rolled[1] : "";
        v[2] = rolled != null && rolled.length > 2 && rolled[2] != null ? rolled[2] : "";
        v[3] = rolled != null && rolled.length > 3 && rolled[3] != null ? rolled[3] : (!v[2].isEmpty() ? "timed" : "");
        String room = roomId(path);
        if (room == null) return v;
        if (!v[1].isEmpty()) v[0] = room; // affix → preferred room
        if (!v[3].isEmpty() || !v[2].isEmpty()) {
            v[2] = room; // event → preferred room
            if (v[3].isEmpty()) v[3] = "timed";
        }
        return v;
    }

    public static boolean isPreferredRoom(int path, String roomKey) {
        String want = roomId(path);
        return want != null && roomKey != null && want.equalsIgnoreCase(roomKey);
    }

    /** D2546: FRONT early burst / BACK late push. */
    public static int openSpeedTicks(int path) {
        if (path == FRONT) return 80;
        if (path == MID) return 60;
        if (path == BACK) return 40;
        return 60;
    }

    public static int clearSpeedTicks(int path) {
        if (path == FRONT) return 30;
        if (path == MID) return 40;
        if (path == BACK) return 60;
        return 40;
    }


    /**
     * D2515: preferred-room open → party Speed I 3s (footwork, zero skill_mult).
     * Call from run room-start when variety pinned this room.
     */
    /** D2530: preferred variety room clear → party Speed I 2s (push to next). */
    public static void pulsePreferredClear(EmberRunSession s, EmberRunMaps.Room r, EmberRunService runs) {
        if (s == null || r == null || runs == null || r.id == null) return;
        try {
            org.bukkit.entity.Player leader = null;
            for (java.util.UUID u : s.committed) {
                org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(u);
                if (p != null && p.isOnline()) { leader = p; break; }
            }
            if (leader == null) return;
            PlayerData ld = runs.dataOf(leader.getUniqueId());
            int path = get(ld);
            if (!valid(path) || !isPreferredRoom(path, r.id)) return;
            boolean varietyHere = (r.id.equals(s.affixRoom) && s.affix != null && !s.affix.isEmpty())
                    || (r.id.equals(s.eventRoom) && s.eventRoom != null && !s.eventRoom.isEmpty());
            if (!varietyHere) return;
            for (java.util.UUID u : s.committed) {
                org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(u);
                if (p == null || !p.isOnline() || p.isDead()) continue;
                int ticks = clearSpeedTicks(path);
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.SPEED, ticks, 0, false, true), true);
                try { p.sendActionBar("§a优房清 · §fSpeed I " + (ticks / 20) + "s"); } catch (Throwable ignored) { }
            }
        } catch (Throwable ignored) { }
    }

    public static void pulsePreferredRoom(EmberRunSession s, EmberRunMaps.Room r, EmberRunService runs) {
        if (s == null || r == null || runs == null || r.id == null) return;
        try {
            org.bukkit.entity.Player leader = null;
            for (java.util.UUID u : s.committed) {
                org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(u);
                if (p != null && p.isOnline()) { leader = p; break; }
            }
            if (leader == null) return;
            PlayerData ld = runs.dataOf(leader.getUniqueId());
            int path = get(ld);
            if (!valid(path) || !isPreferredRoom(path, r.id)) return;
            boolean varietyHere = (r.id.equals(s.affixRoom) && s.affix != null && !s.affix.isEmpty())
                    || (r.id.equals(s.eventRoom) && s.eventRoom != null && !s.eventRoom.isEmpty());
            if (!varietyHere) return;
            for (java.util.UUID u : s.committed) {
                org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(u);
                if (p == null || !p.isOnline() || p.isDead()) continue;
                int ticks = openSpeedTicks(path);
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.SPEED, ticks, 0, false, true), true);
                try {
                    p.sendMessage(EmberRunService.P + "§a房序优房 · §f" + r.label + " §7Speed I " + (ticks / 20) + "s");
                    p.sendActionBar("§a优房 · §f" + label(path));
                } catch (Throwable ignored) { }
            }
        } catch (Throwable ignored) { }
    }

    /** D2503: map-enter glance when path set (no ConfirmTokens). */
    public static void enterCue(Player p, PlayerData d) {
        if (p == null || d == null) return;
        int id = get(d);
        if (!valid(id)) return;
        String line = "§e房序 · §f" + label(id) + " §8· " + tip(id);
        try { p.sendMessage(EmberRunService.P + line); } catch (Throwable ignored) { }
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选花样落房（改词缀/事件出在哪间 · 可随时改 · 不改种类与数值）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[前房]", "/corerpg p1 roompath front", tip(FRONT), "GREEN"},
                new String[]{"[中房]", "/corerpg p1 roompath mid", tip(MID), "GOLD"},
                new String[]{"[后房]", "/corerpg p1 roompath back", tip(BACK), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 roompath clear", "清空偏好", "DARK_GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        offerPick(p);
        return true;
    }

    public static void scheduleOfferAfterQ02(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 110L); // after spice ~85L
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        EmberRunService runs = pl.getEmberRuns();
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!EmberSkillKit.roomPathUnlocked(d, runs)) {
            p.sendMessage(EmberRunService.P + "§c房序路径需本人首进或首通 Q02（重打本才有花样）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空房序路径（花样落房回随机）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a房序 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "重打已通主线本时生效（队长）", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 roompath", "重选", "GRAY"});
    }

    private static PlayerData dataOf(Player p) {
        CoreRpgPlugin pl = plugin();
        return pl == null || p == null ? null : pl.getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
