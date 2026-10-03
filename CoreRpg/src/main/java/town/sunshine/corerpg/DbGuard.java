package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 1.62 startup guard. With {@code storage: mysql} configured, P1 on and {@code storage_guard.enabled} (default true),
 * a failed MySQL connect at CoreRpg enable no longer fails open to YAML silently: the server logs a loud banner and
 * refuses every join (whitelist-style message) until the play server is restarted with the database reachable. A
 * TCP probe every 30 s logs when MySQL answers again. It stays closed until a restart on purpose: in YAML mode the
 * guild / auction / player caches were filled from stale files, and a hot switch back would write them into MySQL.
 * {@code /corerpg storage guard release} (admin) opens joins anyway (YAML mode, the old behaviour).
 * Also: a player whose own MySQL read fails later (PlayerData.loadFailed) is kicked and that data is never saved.
 */
public final class DbGuard implements Listener {

    public static final String KICK = "§c余烬服数据库暂时连不上，为保护你的存档，服务器暂停进入。\n§7请稍后再试（通常几分钟）。";

    private final CoreRpgPlugin plugin;
    private volatile boolean engaged;
    private volatile String reason = "";
    private volatile long since;
    private volatile boolean reachable;
    private long lastNote;

    public DbGuard(CoreRpgPlugin plugin) { this.plugin = plugin; }

    /** pure decision (unit-tested) */
    public static boolean shouldEngage(boolean configuredMysql, boolean connected, boolean guardEnabled, boolean p1Active) {
        return configuredMysql && !connected && guardEnabled && p1Active;
    }

    public boolean engaged() { return engaged; }

    public void engage(String why) {
        engaged = true;
        reason = why == null ? "" : why;
        since = System.currentTimeMillis();
        String bar = "################################################################";
        plugin.getLogger().severe(bar);
        plugin.getLogger().severe("# [DB GUARD] MySQL NOT CONNECTED at CoreRpg enable — P1 player data lives in MySQL.");
        plugin.getLogger().severe("# [DB GUARD] NOT falling back to YAML for players: ALL JOINS ARE REFUSED until restart.");
        plugin.getLogger().severe("# [DB GUARD] cause: " + reason);
        plugin.getLogger().severe("# [DB GUARD] fix the database, then restart the play server (server-runtime/stop.sh; start.sh)");
        plugin.getLogger().severe("# [DB GUARD] override (YAML mode, unsafe): /corerpg storage guard release");
        plugin.getLogger().severe(bar);
        for (Player p : Bukkit.getOnlinePlayers()) p.kickPlayer(KICK);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::probe, 200L, 600L);
    }

    private void probe() {
        if (!engaged) return;
        ConfigurationSection s = plugin.getConfig().getConfigurationSection("mysql");
        String host = s == null ? "127.0.0.1" : s.getString("host", "127.0.0.1");
        int port = plugin.effectiveMysqlPort();
        boolean ok;
        try (Socket so = new Socket()) { so.connect(new InetSocketAddress(host, port), 3000); ok = true; } catch (Exception e) { ok = false; }
        long now = System.currentTimeMillis();
        if (ok != reachable || now - lastNote > 300000L) {
            lastNote = now;
            if (ok) plugin.getLogger().warning("[DB GUARD] MySQL answers again on " + host + ":" + port + " — joins stay refused until the play server is restarted");
            else plugin.getLogger().severe("[DB GUARD] MySQL still unreachable on " + host + ":" + port + "; joins refused since " + fmt(since));
        }
        reachable = ok;
    }

    private static String fmt(long t) {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return f.format(new Date(t)) + " CST";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (engaged) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, KICK);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        if (engaged) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, KICK);
            plugin.getLogger().warning("[DB GUARD] refused join of " + e.getPlayer().getName() + " (MySQL down at startup)");
        }
    }

    /** a later MySQL read failure for this one player: kick, the cached data is never saved (PlayerDataStore) */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = e.getPlayer();
            if (!p.isOnline() || !plugin.isMysqlActive()) return;
            PlayerData d = plugin.getDataStore().peek(p.getUniqueId());
            if (d != null && d.isLoadFailed()) {
                plugin.getLogger().severe("[DB GUARD] kicked " + p.getName() + ": MySQL read of the player failed (data not saved this session)");
                plugin.getDataStore().forget(p.getUniqueId());
                p.kickPlayer(KICK);
            }
        }, 5L);
    }

    /** /corerpg storage guard [status|release] */
    public boolean cmd(CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "status";
        if ("release".equals(op)) {
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            if (!engaged) { s.sendMessage(ChatColor.GRAY + "[DB GUARD] 未拦截"); return true; }
            engaged = false;
            plugin.getLogger().severe("[DB GUARD] RELEASED by " + s.getName() + " — players join in YAML mode (no MySQL)");
            s.sendMessage(ChatColor.YELLOW + "[DB GUARD] 已放行：玩家将以 YAML 模式进入（不推荐）");
            return true;
        }
        s.sendMessage(ChatColor.GOLD + "[DB GUARD] " + (engaged ? ChatColor.RED + "拦截中（" + fmt(since) + " 起）：" + reason
                + (reachable ? ChatColor.YELLOW + " · MySQL 已能连上，重启游玩服即可放行" : "") : ChatColor.GREEN + "未拦截")
                + ChatColor.GRAY + " · storage_guard.enabled=" + plugin.getConfig().getBoolean("storage_guard.enabled", true));
        return true;
    }
}
