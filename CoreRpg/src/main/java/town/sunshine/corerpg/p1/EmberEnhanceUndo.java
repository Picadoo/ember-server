package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D614: soft undo for the last <b>failed</b> enhance attempt (60s). Refunds mats and rolls pity back.
 * Success enhances are never undoable. Zero RATE / MAX_TRIES / price change.
 */
public final class EmberEnhanceUndo {

    public static final long WINDOW_MS = 60_000L;

    static final class Snap {
        final UUID player;
        final String uid;
        final int enhance;
        final int pityBefore;
        final int pityAfter;
        final int revAfter;
        final Cost cost;
        final long at;

        Snap(UUID player, String uid, int enhance, int pityBefore, int pityAfter, int revAfter, Cost cost, long at) {
            this.player = player; this.uid = uid; this.enhance = enhance;
            this.pityBefore = pityBefore; this.pityAfter = pityAfter; this.revAfter = revAfter;
            this.cost = cost; this.at = at;
        }

        boolean open(long now) { return now - at <= WINDOW_MS; }
    }

    static final ConcurrentHashMap<UUID, Snap> LAST = new ConcurrentHashMap<UUID, Snap>();

    private EmberEnhanceUndo() {}

    /** After enhance commit OK: remember fail-only; offer undo button. */
    public static void afterCommit(Player p, List<TxnItem> list, Cost cost) {
        if (p == null || list == null || list.isEmpty() || cost == null) return;
        if (EmberEnhanceLoop.leveled(list)) {
            LAST.remove(p.getUniqueId()); // success clears pending fail undo
            return;
        }
        TxnItem t = list.get(0);
        if (t == null || t.before == null || t.after == null) return;
        if (t.after.enhance != t.before.enhance) return; // not a pure fail
        Snap s = new Snap(p.getUniqueId(), t.after.uid, t.after.enhance, t.before.pity, t.after.pity,
                t.after.rev, cost, System.currentTimeMillis());
        LAST.put(p.getUniqueId(), s);
        String P = EmberRunService.P;
        int sec = remainSec(s, System.currentTimeMillis());
        try { // D694
            p.sendActionBar("§e强化失败可撤 · §f" + sec + "s §7· /corerpg p1 enhance undo");
        } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7误点可撤（" + sec + " 秒内 · 仅失败）：",
                new String[]{"[撤销本次失败]", "/corerpg p1 enhance undo",
                        "退回材料 · pity 回滚 · 剩余约 " + sec + "s", "YELLOW"});
        scheduleMidRemind(p, s); // D713
    }

    /** D713: one mid-window ActionBar if the same fail snap is still open. */
    static void scheduleMidRemind(Player p, Snap s) {
        if (p == null || s == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) return;
        final UUID id = p.getUniqueId();
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            Player q = Bukkit.getPlayer(id);
            if (q == null || !q.isOnline()) return;
            Snap cur = peek(id);
            if (cur == null || cur != s && (cur.at != s.at || !cur.uid.equals(s.uid))) return;
            int left = remainSec(cur, System.currentTimeMillis());
            if (left <= 0) return;
            try { q.sendActionBar("§e失败仍可撤 · §f" + left + "s §7· /corerpg p1 enhance undo"); }
            catch (Throwable ignored) { }
        }, 30L * 20L);
    }

    /** D694: open-forge glance if undo window still open. */
    public static void forgeGlance(Player p) {
        if (p == null) return;
        Snap s = peek(p.getUniqueId());
        if (s == null) return;
        int sec = remainSec(s, System.currentTimeMillis());
        if (sec <= 0) { // D726
            LAST.remove(p.getUniqueId(), s);
            try { p.sendActionBar("§8失败撤销窗已过"); } catch (Throwable ignored) { }
            return;
        }
        p.sendMessage(EmberRunService.P + "§e仍可撤销强化失败 · §f约 " + sec + "s §7· /corerpg p1 enhance undo");
        try { p.sendActionBar("§e失败可撤 · §f" + sec + "s"); } catch (Throwable ignored) { }
    }

    public static Snap peek(UUID id) { return id == null ? null : LAST.get(id); }

    public static Snap takeIfOpen(UUID id) {
        Snap s = peek(id);
        if (s == null) return null;
        if (!s.open(System.currentTimeMillis())) { LAST.remove(id, s); return null; }
        return s;
    }

    public static void clear(UUID id) { if (id != null) LAST.remove(id); }

    public static void putBack(Snap s) { if (s != null) LAST.put(s.player, s); }

    public static boolean consume(UUID id, Snap expect) {
        if (id == null || expect == null) return false;
        return LAST.remove(id, expect);
    }

    /** Remaining seconds for UI/tests. */
    public static int remainSec(Snap s, long now) {
        if (s == null) return 0;
        long left = WINDOW_MS - (now - s.at);
        return left <= 0 ? 0 : (int) ((left + 999) / 1000);
    }
}
