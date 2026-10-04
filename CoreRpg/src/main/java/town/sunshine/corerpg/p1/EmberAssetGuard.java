package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D162 (review A03 / A04): one place that says "no asset mutations for this player right now".
 * <ul>
 *   <li>frozen: an admin inventory restore (invsnap) is running for the player — vault / gear library / forge / delivery wait</li>
 *   <li>paused: P1 store transactions or player saves keep failing (3 in a row) — nothing moves until a write succeeds again
 *   (store side clears itself after 60 s without a new error; player saves clear on the next good autosave)</li>
 * </ul>
 */
public final class EmberAssetGuard {
    private static final Set<UUID> frozen = ConcurrentHashMap.newKeySet();
    private static volatile int saveStreakProbe = -1;

    private EmberAssetGuard() {}

    public static void freeze(UUID id) { if (id != null) frozen.add(id); }
    public static void thaw(UUID id) { if (id != null) frozen.remove(id); }
    public static boolean frozen(UUID id) { return id != null && frozen.contains(id); }

    /** player saves failing streak, set by the plugin (PlayerDataStore); -1 = unknown */
    public static void saveStreak(int n) { saveStreakProbe = n; }

    public static boolean paused() { return EmberItemStore.writesPaused() || saveStreakProbe >= 3; }

    /** null = go ahead; else the message to show */
    public static String hold(Player p) {
        if (p != null && frozen(p.getUniqueId())) return "管理员正在恢复你的背包，仓库 / 装备库 / 锻造暂停几秒，稍后再试";
        if (paused()) return "数据库写入暂时异常，仓库 / 装备库 / 锻造先暂停（不会丢东西，恢复后自动解除）";
        return null;
    }
}
