package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import town.sunshine.corerpg.p1.EmberItems;
import town.sunshine.corerpg.p1.EmberMode;
import town.sunshine.corerpg.p1.EmberVault;
import town.sunshine.corerpg.p1.EmberVaultLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 1.62 vanilla inventory protection. Snapshots each player's inventory (41 slots incl. armor + offhand) and ender
 * chest (27) as Bukkit-serialized ItemStacks (YAML; CraftMetaItem keeps unknown NBT under "internal", so NI / ember
 * signatures survive) on join, quit, death, entering / leaving a P1 or instance world, every 10 min (skipped when
 * unchanged) and at plugin disable. Stored gzip'd in MySQL cr_inv_snapshot, or as gzip yaml under
 * plugins/CoreRpg/snapshots/&lt;uuid&gt;/ when MySQL is down. Retention: last 50 + newest per CST day for 30 days.
 * Admin: /corerpg invsnap list|view|restore|diff|take. Restore always snapshots the current state first (undo =
 * restore that "pre-restore" id); offline targets are queued in invsnap-pending.yml and applied at their next join.
 */
public final class InvSnapService implements Listener {

    public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS cr_inv_snapshot ("
            + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "player_uuid CHAR(36) NOT NULL,"
            + "name VARCHAR(32) NULL,"
            + "reason VARCHAR(24) NOT NULL,"
            + "world VARCHAR(64) NULL,"
            + "created_at BIGINT NOT NULL,"
            + "item_count INT NOT NULL,"
            + "hash CHAR(40) NOT NULL,"
            + "data MEDIUMBLOB NOT NULL,"
            + "KEY idx_inv_snap_player (player_uuid, created_at),"
            + "KEY idx_inv_snap_name (name)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    private static final String P = ChatColor.GOLD + "[背包快照] " + ChatColor.RESET;
    public static final int INV = 41, ENDER = 27;

    private final CoreRpgPlugin plugin;
    private final File dir;
    private final File pendingFile;
    private final Map<UUID, String> lastHash = new ConcurrentHashMap<UUID, String>();
    private final ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "CoreRpg-invsnap");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean schemaOk;

    public InvSnapService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "snapshots");
        this.pendingFile = new File(plugin.getDataFolder(), "invsnap-pending.yml");
    }

    /** ember-v1.yml storage.invsnap.* wins when P1 mode config is loaded; else config.yml invsnap.* */
    private boolean enabled() {
        town.sunshine.corerpg.p1.EmberMode m = town.sunshine.corerpg.p1.EmberMode.get();
        if (m != null) return m.b("storage.invsnap.enabled", plugin.getConfig().getBoolean("invsnap.enabled", true));
        return plugin.getConfig().getBoolean("invsnap.enabled", true);
    }
    private int keepLast() { return Math.max(5, plugin.getConfig().getInt("invsnap.keep_last", InvSnapRules.KEEP_LAST)); }
    private int keepDays() { return Math.max(1, plugin.getConfig().getInt("invsnap.keep_days", InvSnapRules.KEEP_DAYS)); }
    private int intervalMinutes() {
        town.sunshine.corerpg.p1.EmberMode m = town.sunshine.corerpg.p1.EmberMode.get();
        int def = plugin.getConfig().getInt("invsnap.interval_minutes", 10);
        return m == null ? def : Math.max(1, m.config().getInt("storage.invsnap.interval_minutes", def));
    }

    public void start() {
        exec.submit(this::ensureSchema);
        long period = Math.max(1, intervalMinutes()) * 1200L;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!enabled()) return;
            for (Player p : Bukkit.getOnlinePlayers()) snapshot(p, "periodic", null);
        }, period, period);
    }

    private boolean db() { return plugin.isMysqlActive(); }

    private void ensureSchema() {
        if (!db()) return;
        try (Connection c = plugin.getMysqlStorage().getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(SCHEMA);
            schemaOk = true;
            plugin.getLogger().info("[invsnap] cr_inv_snapshot ready");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "[invsnap] schema failed — snapshots go to files", e);
        }
    }

    // ================================================================== capture / store

    static final class Cap {
        UUID uuid; String name; String reason; String world; long at; int count; String hash; byte[] gz;
    }

    /** the serialized body: inv.&lt;slot&gt; / ender.&lt;slot&gt; (main thread) */
    private static String body(ItemStack[] inv, ItemStack[] ender, int[] count) {
        YamlConfiguration y = new YamlConfiguration();
        y.set("v", 1);
        int n = 0;
        for (int i = 0; i < inv.length; i++) if (real(inv[i])) { y.set("inv." + i, inv[i].clone()); n++; }
        for (int i = 0; i < ender.length; i++) if (real(ender[i])) { y.set("ender." + i, ender[i].clone()); n++; }
        count[0] = n;
        return y.saveToString();
    }

    private static boolean real(ItemStack s) { return s != null && s.getType() != Material.AIR && s.getAmount() > 0; }

    private Cap capture(Player p, String reason) {
        int[] n = new int[1];
        String body = body(p.getInventory().getContents(), p.getEnderChest().getContents(), n);
        Cap c = new Cap();
        c.uuid = p.getUniqueId(); c.name = p.getName(); c.reason = reason; c.world = p.getWorld().getName();
        c.at = System.currentTimeMillis(); c.count = n[0]; c.hash = sha1(body);
        YamlConfiguration meta = new YamlConfiguration();
        meta.set("meta.name", c.name); meta.set("meta.uuid", c.uuid.toString()); meta.set("meta.reason", reason);
        meta.set("meta.world", c.world); meta.set("meta.at", c.at); meta.set("meta.hash", c.hash);
        // 1.64.1: warehouse baseline at the same instant, so a restore can net out what was stashed after it
        EmberVault v = EmberVault.get();
        if (v != null && v.enabled()) {
            for (String id : v.whitelist()) meta.set("meta.vault." + id, v.amount(p.getUniqueId(), id));
            meta.set("meta.vault_ok", true);
        }
        c.gz = gzip(body + meta.saveToString());
        return c;
    }

    /** Main thread. {@code done} (main thread, may be null) gets the new snapshot id, or null when skipped / failed. */
    public void snapshot(Player p, String reason, Consumer<String> done) {
        if (p == null || !p.isOnline() || (!enabled() && !"manual".equals(reason) && !"pre-restore".equals(reason))) {
            if (done != null) done.accept(null);
            return;
        }
        final Cap c;
        try {
            c = capture(p, reason);
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, "[invsnap] capture failed for " + p.getName(), ex);
            if (done != null) done.accept(null);
            return;
        }
        if (!InvSnapRules.store(reason, c.hash.equals(lastHash.get(c.uuid)))) {
            if (done != null) done.accept(null);
            return;
        }
        lastHash.put(c.uuid, c.hash);
        exec.submit(() -> {
            String id = write(c);
            if (done != null) Bukkit.getScheduler().runTask(plugin, () -> done.accept(id));
        });
    }

    /** off-thread: DB insert (or file fallback) + prune */
    private String write(Cap c) {
        String id = null;
        if (db()) {
            if (!schemaOk) ensureSchema();
            if (schemaOk) {
                try (Connection con = plugin.getMysqlStorage().getConnection();
                     PreparedStatement ps = con.prepareStatement("INSERT INTO cr_inv_snapshot (player_uuid,name,reason,world,created_at,item_count,hash,data) VALUES (?,?,?,?,?,?,?,?)",
                             Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, c.uuid.toString()); ps.setString(2, c.name); ps.setString(3, c.reason);
                    ps.setString(4, c.world); ps.setLong(5, c.at); ps.setInt(6, c.count); ps.setString(7, c.hash);
                    ps.setBytes(8, c.gz);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) id = String.valueOf(rs.getLong(1)); }
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.WARNING, "[invsnap] DB write failed for " + c.name + " — file fallback", e);
                }
            }
        }
        if (id == null) {
            File d = new File(dir, c.uuid.toString());
            d.mkdirs();
            File f = new File(d, c.at + "-" + c.reason + ".yml.gz");
            try (OutputStream o = new FileOutputStream(f)) {
                o.write(c.gz);
                id = "f" + c.at;
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "[invsnap] could not write " + f, e);
                return null;
            }
        }
        try { prune(c.uuid); } catch (RuntimeException e) { plugin.getLogger().log(Level.WARNING, "[invsnap] prune", e); }
        return id;
    }

    private void prune(UUID uuid) {
        long now = System.currentTimeMillis();
        if (db() && schemaOk) {
            List<InvSnapRules.Snap> rows = new ArrayList<InvSnapRules.Snap>();
            try (Connection con = plugin.getMysqlStorage().getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT id,created_at FROM cr_inv_snapshot WHERE player_uuid=?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) { while (rs.next()) rows.add(new InvSnapRules.Snap(String.valueOf(rs.getLong(1)), rs.getLong(2))); }
                }
                List<String> del = InvSnapRules.prune(rows, now, keepLast(), keepDays());
                if (!del.isEmpty()) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM cr_inv_snapshot WHERE id=?")) {
                        for (String id : del) { ps.setLong(1, Long.parseLong(id)); ps.addBatch(); }
                        ps.executeBatch();
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "[invsnap] prune db", e);
            }
        }
        File d = new File(dir, uuid.toString());
        File[] fs = d.listFiles((x, n) -> n.endsWith(".yml.gz"));
        if (fs == null || fs.length == 0) return;
        List<InvSnapRules.Snap> rows = new ArrayList<InvSnapRules.Snap>();
        for (File f : fs) { long at = fileAt(f.getName()); if (at > 0) rows.add(new InvSnapRules.Snap(f.getName(), at)); }
        for (String n : InvSnapRules.prune(rows, now, keepLast(), keepDays())) new File(d, n).delete();
    }

    private static long fileAt(String n) {
        int i = n.indexOf('-');
        try { return Long.parseLong(i > 0 ? n.substring(0, i) : n); } catch (NumberFormatException e) { return -1; }
    }

    /** plugin disable: capture + write synchronously (quit events do not fire before plugins are disabled at stop) */
    public void shutdown() {
        if (enabled()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    Cap c = capture(p, "shutdown");
                    if (!c.hash.equals(lastHash.get(c.uuid))) { write(c); lastHash.put(c.uuid, c.hash); }
                } catch (RuntimeException e) {
                    plugin.getLogger().log(Level.WARNING, "[invsnap] shutdown snapshot " + p.getName(), e);
                }
            }
        }
        exec.shutdown();
        try { exec.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    // ================================================================== triggers

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            String pend = pending(p.getUniqueId());
            if (pend != null) applyPending(p, pend);
            else snapshot(p, "join", null);
        }, 40L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        snapshot(e.getPlayer(), "quit", null);
        lastHash.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) { snapshot(e.getEntity(), "death", null); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getTo() == null || e.getFrom().getWorld() == null || e.getTo().getWorld() == null) return;
        World a = e.getFrom().getWorld(), b = e.getTo().getWorld();
        if (a.equals(b)) return;
        boolean da = dungeon(a), dbw = dungeon(b);
        if (!da && !dbw) return;
        snapshot(e.getPlayer(), dbw && !da ? "enter" : (da && !dbw ? "exit" : "hop"), null);
    }

    private boolean dungeon(World w) {
        if (EmberMode.isP1World(w)) return true;
        QuestService q = plugin.getQuestService();
        return q != null && q.isInstanceWorld(w);
    }

    // ================================================================== list / load

    public static final class Entry {
        public final String id, reason, world, name;
        public final long at;
        public final int count;
        Entry(String id, String name, String reason, String world, long at, int count) {
            this.id = id; this.name = name; this.reason = reason; this.world = world; this.at = at; this.count = count;
        }
    }

    public static final class Snap {
        public final ItemStack[] inv = new ItemStack[INV];
        public final ItemStack[] ender = new ItemStack[ENDER];
        public String id, reason, world, name;
        public long at;
        /** 1.64.1 warehouse baseline (null = older snapshot) */
        public Map<String, Long> vault;
        public int count() { int n = 0; for (ItemStack s : inv) if (real(s)) n++; for (ItemStack s : ender) if (real(s)) n++; return n; }
    }

    private void list(UUID uuid, int limit, Consumer<List<Entry>> cb) {
        exec.submit(() -> {
            List<Entry> out = new ArrayList<Entry>();
            if (db() && schemaOk) {
                try (Connection con = plugin.getMysqlStorage().getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT id,name,reason,world,created_at,item_count FROM cr_inv_snapshot WHERE player_uuid=? ORDER BY created_at DESC, id DESC LIMIT ?")) {
                    ps.setString(1, uuid.toString()); ps.setInt(2, limit);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) out.add(new Entry(String.valueOf(rs.getLong(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getLong(5), rs.getInt(6)));
                    }
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.WARNING, "[invsnap] list", e);
                }
            }
            File[] fs = new File(dir, uuid.toString()).listFiles((x, n) -> n.endsWith(".yml.gz"));
            if (fs != null) for (File f : fs) {
                String n = f.getName();
                long at = fileAt(n);
                String reason = n.substring(n.indexOf('-') + 1, n.length() - ".yml.gz".length());
                out.add(new Entry("f" + at, null, reason, "(文件)", at, -1));
            }
            out.sort((x, y) -> Long.compare(y.at, x.at));
            final List<Entry> r = out.size() > limit ? new ArrayList<Entry>(out.subList(0, limit)) : out;
            Bukkit.getScheduler().runTask(plugin, () -> cb.accept(r));
        });
    }

    /** loads one snapshot (async read, main-thread decode); cb gets null when missing / unreadable */
    public void load(UUID uuid, String id, Consumer<Snap> cb) {
        exec.submit(() -> {
            byte[] gz = null;
            try {
                if (id.startsWith("f")) {
                    File[] fs = new File(dir, uuid.toString()).listFiles((x, n) -> n.startsWith(id.substring(1) + "-") && n.endsWith(".yml.gz"));
                    if (fs != null && fs.length > 0) gz = Files.readAllBytes(fs[0].toPath());
                } else if (db() && schemaOk) {
                    try (Connection con = plugin.getMysqlStorage().getConnection();
                         PreparedStatement ps = con.prepareStatement("SELECT data FROM cr_inv_snapshot WHERE id=? AND player_uuid=?")) {
                        ps.setLong(1, Long.parseLong(id)); ps.setString(2, uuid.toString());
                        try (ResultSet rs = ps.executeQuery()) { if (rs.next()) gz = rs.getBytes(1); }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "[invsnap] load " + id, e);
            }
            final String text = gz == null ? null : gunzip(gz);
            Bukkit.getScheduler().runTask(plugin, () -> cb.accept(text == null ? null : decode(id, text)));
        });
    }

    private Snap decode(String id, String text) {
        YamlConfiguration y = new YamlConfiguration();
        try { y.loadFromString(text); } catch (InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "[invsnap] corrupt snapshot " + id, e);
            return null;
        }
        Snap s = new Snap();
        s.id = id;
        for (int i = 0; i < INV; i++) s.inv[i] = y.getItemStack("inv." + i);
        for (int i = 0; i < ENDER; i++) s.ender[i] = y.getItemStack("ender." + i);
        s.reason = y.getString("meta.reason", "?"); s.world = y.getString("meta.world", "?");
        s.name = y.getString("meta.name", "?"); s.at = y.getLong("meta.at", 0L);
        if (y.getBoolean("meta.vault_ok", false) && y.isConfigurationSection("meta.vault")) {
            s.vault = new HashMap<String, Long>();
            for (String k : y.getConfigurationSection("meta.vault").getKeys(false)) s.vault.put(k, y.getLong("meta.vault." + k, 0L));
        }
        return s;
    }

    // ================================================================== restore

    /** restores {@code s} onto online {@code p}: pre-restore snapshot first, P1 trust check, ledger net-out, then apply */
    private void restore(final CommandSender admin, final Player p, final Snap s) { restore(admin, p, s, null); }

    /**
     * D162 (review A04): the player's asset operations (vault / gear library / forge / deliveries / auto-pickup into the
     * warehouse) are frozen from the pre-restore snapshot to the end of apply, so netOut's ledger reading and the restored
     * backpack describe the same moment. {@code done} gets null on success, else the reason (nothing applied).
     */
    private void restore(final CommandSender admin, final Player p, final Snap s, final java.util.function.Consumer<String> done) {
        final UUID u = p.getUniqueId();
        final town.sunshine.corerpg.p1.EmberSixRestoreGuard.Verdict six = town.sunshine.corerpg.p1.EmberSixSlotService.restoreGuard(p, s.at);
        if (six != null) { // D319: never restore across the six-slot migration (checked again here for queued restores)
            refuseSix(admin, p, s, six);
            if (done != null) done.accept("six-slot guard (" + six + ")");
            return;
        }
        if (town.sunshine.corerpg.p1.EmberAssetGuard.frozen(u)) { msg(admin, ChatColor.RED + "这个玩家正在恢复中，稍后再试"); if (done != null) done.accept("restore already running"); return; }
        town.sunshine.corerpg.p1.EmberAssetGuard.freeze(u);
        final java.util.function.Consumer<String> end = why -> {
            town.sunshine.corerpg.p1.EmberAssetGuard.thaw(u);
            if (done != null) done.accept(why);
            Player q = Bukkit.getPlayer(u);
            if (q != null && town.sunshine.corerpg.p1.EmberGearLib.get() != null) town.sunshine.corerpg.p1.EmberGearLib.get().delivery().kick(q);
        };
        snapshot(p, "pre-restore", pre -> {
            if (!p.isOnline()) { msg(admin, ChatColor.RED + "玩家已下线，未恢复"); end.accept("offline before pre-restore"); return; }
            if (pre == null) { msg(admin, ChatColor.RED + "恢复前快照写入失败，为安全起见未恢复"); end.accept("pre-restore snapshot failed"); return; }
            trustCheck(p, s, skipped -> netOut(p, s, net -> {
                if (!p.isOnline()) { msg(admin, ChatColor.RED + "玩家已下线，未恢复"); end.accept("offline before apply"); return; }
                try { apply(p, s); } catch (RuntimeException ex) { plugin.getLogger().log(Level.SEVERE, "[invsnap] apply " + s.id + " for " + p.getName(), ex); end.accept("apply failed: " + ex); return; }
                String undo = "/corerpg invsnap restore " + p.getName() + " " + pre;
                msg(admin, ChatColor.GREEN + "已把 " + p.getName() + " 的背包/末影箱恢复到快照 #" + s.id + "（" + fmt(s.at) + " " + s.reason + "）");
                report(admin, skipped, net);
                msg(admin, ChatColor.GRAY + "恢复前状态已存为 #" + pre + "，撤销：" + ChatColor.YELLOW + undo);
                p.sendMessage(P + ChatColor.YELLOW + "管理员已恢复你的背包与末影箱（快照 " + fmt(s.at) + "；材料仓与扭蛋钱包未回滚）");
                plugin.getLogger().warning("[invsnap] " + admin.getName() + " restored " + p.getName() + " to snapshot " + s.id
                        + " (pre-restore " + pre + ", skipped P1 " + skipped.size() + ", net-out " + net.summary() + ")");
                end.accept(null);
                snapshot(p, "restored", null);
            }));
        });
    }

    /** D319: the six-slot guard refused a restore — reason + where the runbook explains it, to the admin and the log */
    private void refuseSix(CommandSender admin, Player p, Snap s, town.sunshine.corerpg.p1.EmberSixRestoreGuard.Verdict v) {
        msg(admin, ChatColor.RED + "已拒绝把 " + p.getName() + " 恢复到快照 #" + s.id + "（" + fmt(s.at) + "）：" + v.reason);
        msg(admin, ChatColor.GRAY + (v.permanent ? "这张快照以后也不能恢复；请选迁移完成之后的快照。" : "稍后（问题解决后）可以再试。")
                + "规则见运维手册 " + town.sunshine.corerpg.p1.EmberSixRestoreGuard.MANUAL);
        plugin.getLogger().warning("[invsnap] six-slot guard refused restore " + s.id + " for " + p.getName() + " by "
                + (admin == null ? "?" : admin.getName()) + ": " + v);
    }

    /** /corerpg invsnap preview: same checks as restore, nothing applied */
    private void preview(final CommandSender admin, final Player p, final Snap s) {
        final town.sunshine.corerpg.p1.EmberSixRestoreGuard.Verdict six = town.sunshine.corerpg.p1.EmberSixSlotService.restoreGuard(p, s.at);
        if (six != null) msg(admin, ChatColor.RED + "注意：六槽迁移守卫会拒绝这次恢复：" + six.reason + "（" + town.sunshine.corerpg.p1.EmberSixRestoreGuard.MANUAL + "）");
        final int before = s.count();
        trustCheck(p, s, skipped -> netOut(p, s, net -> {
            msg(admin, ChatColor.AQUA + "预览：恢复 " + p.getName() + " 到快照 #" + s.id + "（" + fmt(s.at) + " " + s.reason + "）· 快照 " + before + " 格 → 实际恢复 " + s.count() + " 格");
            report(admin, skipped, net);
            int over = 0;
            ItemStack[] cur = p.getInventory().getContents(), en = p.getEnderChest().getContents();
            for (int i = 0; i < INV && i < cur.length; i++) if (real(cur[i]) && !same(cur[i], s.inv[i])) over++;
            for (int i = 0; i < ENDER && i < en.length; i++) if (real(en[i]) && !same(en[i], s.ender[i])) over++;
            msg(admin, ChatColor.GRAY + "当前背包/末影箱有 " + over + " 格和快照不同，恢复时会被覆盖（会先存 pre-restore 快照可撤销）");
            msg(admin, ChatColor.YELLOW + "确认无误：/corerpg invsnap restore " + p.getName() + " " + s.id);
        }));
    }

    private void report(CommandSender admin, List<String> skipped, Net net) {
        msg(admin, ChatColor.GRAY + "快照只管背包+末影箱；材料仓 / 扭蛋钱包 / 装备库不会回滚。");
        if (!skipped.isEmpty()) {
            msg(admin, ChatColor.RED + "跳过 " + skipped.size() + " 件 P1 装备（防复制）：");
            int k = 0;
            for (String x : skipped) { if (k++ >= 10) { msg(admin, ChatColor.GRAY + "  …"); break; } msg(admin, ChatColor.GRAY + "  " + x); }
        } else msg(admin, ChatColor.GRAY + "P1 装备：没有需要跳过的（已分解 / 在装备库 / 他人持有的都会跳过）");
        if (net.lines.isEmpty()) msg(admin, ChatColor.GRAY + "材料 / 国庆币 / 实物扭蛋券：快照里没有");
        else {
            msg(admin, (net.held > 0 ? ChatColor.YELLOW : ChatColor.GRAY) + "材料 / 国庆币 / 实物扭蛋券（按仓库流水 + 扭蛋账本对账，防复制）：");
            for (String x : net.lines) msg(admin, ChatColor.GRAY + "  " + x);
        }
    }

    private void apply(Player p, Snap s) {
        // P1 gear slots were nulled in trustCheck, material / ticket stacks reduced in netOut
        p.closeInventory();
        ItemStack[] inv = new ItemStack[INV];
        for (int i = 0; i < INV; i++) inv[i] = s.inv[i] == null ? null : s.inv[i].clone();
        ItemStack[] en = new ItemStack[ENDER];
        for (int i = 0; i < ENDER; i++) en[i] = s.ender[i] == null ? null : s.ender[i].clone();
        p.getInventory().setContents(inv);
        p.getEnderChest().setContents(en);
        p.updateInventory();
        lastHash.remove(p.getUniqueId());
        try { p.saveData(); } catch (RuntimeException e) { plugin.getLogger().warning("[invsnap] saveData after restore: " + e); } // D161/D162: saved before the queue entry is cleared
        try {
            if (plugin.getEmberForge() != null) plugin.getEmberForge().resync(p, false);
            if (plugin.getEmberLoadouts() != null) plugin.getEmberLoadouts().refresh(p);
        } catch (RuntimeException e) {
            plugin.getLogger().log(Level.WARNING, "[invsnap] post-restore resync", e);
        }
    }

    // ------------------------------------------------------------------ 1.64.1 ledger net-out

    /** what netOut held back: one admin line per NI id */
    static final class Net {
        final List<String> lines = new ArrayList<String>();
        long held;
        final StringBuilder sum = new StringBuilder();
        String summary() { return sum.length() == 0 ? "none" : sum.toString(); }
    }

    private String ticketNi() {
        File f = new File(plugin.getDataFolder().getParentFile(), "CoreGacha/config.yml");
        if (!f.exists()) return InvSnapRules.GACHA_TICKET_NI;
        return YamlConfiguration.loadConfiguration(f).getString("tickets.ni_item", InvSnapRules.GACHA_TICKET_NI);
    }

    /**
     * Anti-dupe for ledger-backed stacks (review round 2 #1). For every warehouse-whitelisted material (incl. 国庆币) and
     * physical gacha ticket in the snapshot, hold back what provably left the backpack after the snapshot: warehouse
     * growth vs. the snapshot's baseline, 一键存入 and direct backpack spends from cr_vault_log, ticket redeems from
     * gacha_ledger. Old snapshots (no baseline) and unreadable ledgers hold the whole amount back (= 1.64.0 strip).
     * Mutates {@code s}; {@code cb} on the main thread.
     */
    private void netOut(final Player p, final Snap s, final Consumer<Net> cb) {
        final NiBridge ni = plugin.getNiBridge();
        final EmberVault v = EmberVault.get();
        final Set<String> wl = v != null ? v.whitelist() : new HashSet<String>(EmberVault.DEFAULT_WHITELIST);
        final String ticket = ticketNi();
        // id -> list of {part, slot}
        final Map<String, List<int[]>> at = new java.util.LinkedHashMap<String, List<int[]>>();
        for (int part = 0; part < 2; part++) {
            ItemStack[] arr = part == 0 ? s.inv : s.ender;
            for (int i = 0; i < arr.length; i++) {
                if (!real(arr[i]) || ni == null) continue;
                String id = ni.getNiId(arr[i]);
                if (id == null || !(wl.contains(id) || ticket.equals(id))) continue;
                at.computeIfAbsent(id, k -> new ArrayList<int[]>()).add(new int[]{part, i});
            }
        }
        final Net net = new Net();
        if (at.isEmpty()) { cb.accept(net); return; }
        final UUID u = p.getUniqueId();
        final boolean wantTickets = at.containsKey(ticket);
        final EmberVaultLog log = EmberVaultLog.get();
        Consumer<EmberVaultLog.Sums> withLog = sums -> exec.submit(() -> {
            long redeemed = 0;
            boolean ledgerOk = false;
            if (wantTickets && db()) {
                try (Connection con = plugin.getMysqlStorage().getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT COALESCE(SUM(d_tickets),0) FROM gacha_ledger WHERE uuid=? AND reason='redeem' AND UNIX_TIMESTAMP(created_at)*1000>=?")) {
                    ps.setString(1, u.toString()); ps.setLong(2, s.at);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) redeemed = rs.getLong(1); }
                    ledgerOk = true;
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.WARNING, "[invsnap] gacha_ledger read (tickets held back)", e);
                }
            }
            final long red = redeemed;
            final boolean lok = ledgerOk;
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Map.Entry<String, List<int[]>> e : at.entrySet()) {
                    String id = e.getKey();
                    List<int[]> slots = e.getValue();
                    int[] amts = new int[slots.size()];
                    long snapAmt = 0;
                    for (int k = 0; k < slots.size(); k++) { int[] x = slots.get(k); amts[k] = (x[0] == 0 ? s.inv : s.ender)[x[1]].getAmount(); snapAmt += amts[k]; }
                    long deduct;
                    String why;
                    if (ticket.equals(id) && !wl.contains(id)) {
                        deduct = InvSnapRules.ticketDeduct(snapAmt, red, lok);
                        why = !lok ? "扭蛋账本读取失败" : "快照后存进扭蛋钱包 " + red + " 张（gacha_ledger redeem）";
                    } else {
                        long base = s.vault == null ? -1 : (s.vault.containsKey(id) ? s.vault.get(id) : 0L);
                        long now = v == null ? 0 : v.amount(u, id);
                        boolean ok = sums != null && sums.ok;
                        long stash = ok ? sums.get(EmberVaultLog.STASH, id) : 0, spend = ok ? -sums.get(EmberVaultLog.SPEND_INV, id) : 0;
                        deduct = InvSnapRules.vaultDeduct(snapAmt, base, now, stash, spend, ok);
                        why = InvSnapRules.vaultWhy(base, now, stash, spend, ok);
                    }
                    InvSnapRules.takeFromStacks(amts, deduct);
                    for (int k = 0; k < slots.size(); k++) {
                        int[] x = slots.get(k);
                        ItemStack[] arr = x[0] == 0 ? s.inv : s.ender;
                        if (amts[k] <= 0) arr[x[1]] = null; else { ItemStack c = arr[x[1]].clone(); c.setAmount(amts[k]); arr[x[1]] = c; }
                    }
                    String name = ni == null ? id : ni.displayName(id);
                    net.lines.add(InvSnapRules.netLine(name, snapAmt, deduct, why));
                    net.held += deduct;
                    net.sum.append(net.sum.length() == 0 ? "" : ",").append(id).append(' ').append(snapAmt - deduct).append('/').append(snapAmt);
                }
                cb.accept(net);
            });
        });
        if (log != null) log.since(u, s.at, withLog);
        else withLog.accept(null);
    }

    /**
     * Anti-dupe for P1 gear: a signed piece is restored only when cr_p1_item says it is active and owned by this
     * player and no other online player holds it; otherwise its slot is emptied (it stays in the pre-restore
     * snapshot / gear library / wherever it is now). Without MySQL only the online-holder check applies.
     */
    private void trustCheck(Player p, Snap s, Consumer<List<String>> cb) {
        final EmberItems items = plugin.getEmberLoadouts() == null ? null : plugin.getEmberLoadouts().items();
        final List<String> skipped = new ArrayList<String>();
        if (items == null) { cb.accept(skipped); return; }
        final Map<String, List<int[]>> where = new HashMap<String, List<int[]>>(); // uid -> {0 inv / 1 ender, slot}
        final Map<String, String> label = new HashMap<String, String>();
        Set<String> seen = new HashSet<String>();
        for (int part = 0; part < 2; part++) {
            ItemStack[] arr = part == 0 ? s.inv : s.ender;
            for (int i = 0; i < arr.length; i++) {
                if (!real(arr[i]) || !items.hasData(arr[i])) continue;
                EmberItems.Read r = items.read(arr[i]);
                String uid = r == null || r.data == null ? null : r.data.uid;
                String lb = (r == null || r.data == null ? "?" : r.data.shortLabel()) + " @" + (part == 0 ? InvSnapRules.slotName(i) : "末影箱" + (i + 1));
                if (uid == null || !seen.add(uid)) { arr[i] = null; skipped.add(lb + "（无效或重复）"); continue; }
                where.computeIfAbsent(uid, k -> new ArrayList<int[]>()).add(new int[]{part, i});
                label.put(uid, lb);
            }
        }
        if (where.isEmpty()) { cb.accept(skipped); return; }
        // held by somebody else online right now?
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.getUniqueId().equals(p.getUniqueId())) continue;
            for (ItemStack[] arr : Arrays.asList(o.getInventory().getContents(), o.getEnderChest().getContents())) {
                for (ItemStack st : arr) {
                    if (!real(st) || !items.hasData(st)) continue;
                    EmberItems.Read r = items.read(st);
                    if (r != null && r.data != null && r.data.uid != null && where.containsKey(r.data.uid)) {
                        drop(s, where.remove(r.data.uid));
                        skipped.add(label.get(r.data.uid) + "（在线玩家 " + o.getName() + " 持有）");
                    }
                }
            }
        }
        if (where.isEmpty() || !db()) { cb.accept(skipped); return; }
        final String owner = p.getUniqueId().toString();
        final List<String> uids = new ArrayList<String>(where.keySet());
        exec.submit(() -> {
            final Map<String, String[]> rows = new HashMap<String, String[]>();
            boolean ok = true;
            try (Connection con = plugin.getMysqlStorage().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT owner_uuid,state FROM cr_p1_item WHERE item_uid=?")) {
                for (String u : uids) {
                    ps.setString(1, u);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) rows.put(u, new String[]{rs.getString(1), rs.getString(2)}); }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "[invsnap] trust check", e);
                ok = false;
            }
            final boolean fine = ok;
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (String u : uids) {
                    String[] row = rows.get(u);
                    String why = !fine ? "DB 查询失败" : row == null ? "DB 无记录" : !owner.equals(row[0]) ? "属于其他玩家"
                            : !"active".equals(row[1]) ? ("stored".equals(row[1]) ? "已在装备库" : "DB 状态 " + row[1]) : null;
                    if (why != null) { drop(s, where.get(u)); skipped.add(label.get(u) + "（" + why + "）"); }
                }
                cb.accept(skipped);
            });
        });
    }

    private static void drop(Snap s, List<int[]> slots) {
        if (slots == null) return;
        for (int[] x : slots) { if (x[0] == 0) s.inv[x[1]] = null; else s.ender[x[1]] = null; }
    }

    // ================================================================== offline queue

    private synchronized String pending(UUID uuid) {
        if (!pendingFile.exists()) return null;
        return YamlConfiguration.loadConfiguration(pendingFile).getString(uuid.toString() + ".id");
    }

    private synchronized void setPending(UUID uuid, String id, String by) {
        YamlConfiguration y = pendingFile.exists() ? YamlConfiguration.loadConfiguration(pendingFile) : new YamlConfiguration();
        if (id == null) y.set(uuid.toString(), null);
        else { y.set(uuid.toString() + ".id", id); y.set(uuid.toString() + ".by", by); y.set(uuid.toString() + ".at", System.currentTimeMillis()); }
        try { y.save(pendingFile); } catch (IOException e) { plugin.getLogger().log(Level.SEVERE, "[invsnap] cannot save " + pendingFile, e); }
    }

    /**
     * D162 (review A04): the queue entry is cleared only after the restore really applied (the .dat was saved inside
     * apply); a failed attempt keeps it with attempts + last reason and is retried at the next join.
     */
    private void applyPending(final Player p, final String id) {
        final UUID u = p.getUniqueId();
        load(u, id, s -> {
            if (s == null) {
                pendingFailed(u, "snapshot " + id + " not found");
                plugin.getLogger().warning("[invsnap] queued restore " + id + " for " + p.getName() + " not found (kept queued with the reason)");
                snapshot(p, "join", null);
                return;
            }
            if (!p.isOnline()) { pendingFailed(u, "offline before restore"); return; }
            // D319: checked when the queued restore runs (join + 40 ticks, before the six-slot migration at + 120)
            final town.sunshine.corerpg.p1.EmberSixRestoreGuard.Verdict six = town.sunshine.corerpg.p1.EmberSixSlotService.restoreGuard(p, s.at);
            if (six != null) {
                refuseSix(Bukkit.getConsoleSender(), p, s, six);
                for (Player op : Bukkit.getOnlinePlayers()) if (op.hasPermission("corerpg.admin")) msg(op, ChatColor.RED + "排队恢复 #" + id + "（" + p.getName() + "）被六槽迁移守卫拒绝：" + six.reason);
                if (six.permanent) { setPending(u, null, null); plugin.getLogger().warning("[invsnap] queued restore " + id + " for " + p.getName() + " dropped (six-slot guard, permanent)"); }
                else pendingFailed(u, "six-slot guard (" + six + ")");
                snapshot(p, "join", null);
                return;
            }
            plugin.getLogger().warning("[invsnap] applying queued restore " + id + " for " + p.getName());
            restore(Bukkit.getConsoleSender(), p, s, why -> {
                if (why == null) { setPending(u, null, null); plugin.getLogger().warning("[invsnap] queued restore " + id + " for " + p.getName() + " applied, dequeued"); }
                else { pendingFailed(u, why); plugin.getLogger().warning("[invsnap] queued restore " + id + " for " + p.getName() + " NOT applied (" + why + "), kept for the next join"); }
            });
        });
    }

    /** keep the queued restore, count the attempt, remember why */
    private synchronized void pendingFailed(UUID uuid, String why) {
        if (!pendingFile.exists()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(pendingFile);
        if (y.getString(uuid.toString() + ".id") == null) return;
        y.set(uuid.toString() + ".attempts", y.getInt(uuid.toString() + ".attempts", 0) + 1);
        y.set(uuid.toString() + ".last_error", why);
        y.set(uuid.toString() + ".last_attempt_at", System.currentTimeMillis());
        try { y.save(pendingFile); } catch (IOException e) { plugin.getLogger().log(Level.SEVERE, "[invsnap] cannot save " + pendingFile, e); }
    }

    // ================================================================== commands

    public boolean cmd(final CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        String op = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "help";
        if (args.length < 3 || "help".equals(op)) {
            s.sendMessage(P + "/corerpg invsnap list <玩家> [条数]  — 列出快照（id/时间/原因/世界/件数）");
            s.sendMessage(P + "/corerpg invsnap view <玩家> <id>  — 只读预览（第1页背包+护甲+副手，第2页末影箱）");
            s.sendMessage(P + "/corerpg invsnap restore <玩家> <id>  — 恢复背包+末影箱（先存 pre-restore 可撤销；离线则下次进服时恢复）");
            s.sendMessage(P + "/corerpg invsnap preview <玩家> <id>  — 先看会恢复什么、跳过什么、为什么（不改动）");
            s.sendMessage(P + ChatColor.GRAY + "  快照不管材料仓 / 扭蛋钱包 / 装备库；快照里的材料、国庆币、实物扭蛋券按仓库流水 + 扭蛋账本对账：快照后存进仓库 / 钱包或直接花掉的部分不恢复（防复制）");
            s.sendMessage(P + ChatColor.GRAY + "  交易 / 丢给别人的普通物品无法追踪，恢复前先 preview 并问清楚");
            s.sendMessage(P + "/corerpg invsnap diff <玩家> <id>  — 当前背包与快照逐格比对（附材料仓摘要）");
            s.sendMessage(P + "/corerpg invsnap take <玩家>  — 立即拍一张 manual 快照");
            return true;
        }
        final String who = args[2];
        resolve(who, uuid -> {
            if (uuid == null) { s.sendMessage(P + ChatColor.RED + "找不到玩家 " + who); return; }
            final Player online = Bukkit.getPlayer(uuid);
            switch (op) {
                case "list": {
                    int n = 20;
                    if (args.length >= 4) try { n = Math.max(1, Math.min(200, Integer.parseInt(args[3]))); } catch (NumberFormatException ignored) {}
                    list(uuid, n, es -> {
                        s.sendMessage(P + who + " 的快照（最新在前，共显示 " + es.size() + "）：");
                        for (Entry e : es) s.sendMessage(ChatColor.YELLOW + " #" + e.id + ChatColor.GRAY + "  " + fmt(e.at) + "  "
                                + ChatColor.WHITE + e.reason + ChatColor.GRAY + "  " + e.world + (e.count >= 0 ? "  " + e.count + " 格" : ""));
                        if (es.isEmpty()) s.sendMessage(ChatColor.GRAY + "  （无）");
                    });
                    return;
                }
                case "take": {
                    if (online == null) { s.sendMessage(P + "玩家需在线"); return; }
                    snapshot(online, "manual", id -> s.sendMessage(P + (id == null ? ChatColor.RED + "快照失败" : "已保存快照 #" + id)));
                    return;
                }
                case "view": case "restore": case "diff": case "preview": {
                    if (args.length < 4) { s.sendMessage(P + "缺少快照 id（先 list）"); return; }
                    final String id = args[3].startsWith("#") ? args[3].substring(1) : args[3];
                    if ("restore".equals(op) && online == null) {
                        load(uuid, id, snap -> {
                            if (snap == null) { s.sendMessage(P + ChatColor.RED + "快照 #" + id + " 不存在"); return; }
                            setPending(uuid, id, s.getName());
                            plugin.getLogger().warning("[invsnap] " + s.getName() + " queued restore " + id + " for offline " + who);
                            s.sendMessage(P + who + " 不在线：已排队，下次进服时恢复到 #" + id + "（会先自动存 pre-restore 快照）");
                        });
                        return;
                    }
                    if (("diff".equals(op) || "preview".equals(op)) && online == null) { s.sendMessage(P + "玩家需在线"); return; }
                    if ("view".equals(op) && !(s instanceof Player)) { s.sendMessage(P + "view 需在游戏内（控制台用 diff）"); return; }
                    load(uuid, id, snap -> {
                        if (snap == null) { s.sendMessage(P + ChatColor.RED + "快照 #" + id + " 不存在或已损坏"); return; }
                        if ("view".equals(op)) openView((Player) s, snap, 0);
                        else if ("diff".equals(op)) diff(s, online, snap);
                        else if ("preview".equals(op)) preview(s, online, snap);
                        else restore(s, online, snap);
                    });
                    return;
                }
                default:
                    s.sendMessage(P + "未知子命令，/corerpg invsnap help");
            }
        });
        return true;
    }

    private void resolve(String name, Consumer<UUID> cb) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) { cb.accept(p.getUniqueId()); return; }
        try { cb.accept(UUID.fromString(name)); return; } catch (IllegalArgumentException ignored) {}
        exec.submit(() -> {
            UUID u = null;
            if (db() && schemaOk) {
                try (Connection con = plugin.getMysqlStorage().getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT player_uuid FROM cr_inv_snapshot WHERE name=? ORDER BY id DESC LIMIT 1")) {
                    ps.setString(1, name);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) u = UUID.fromString(rs.getString(1)); }
                } catch (Exception ignored) {}
            }
            final UUID fu = u;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (fu != null) { cb.accept(fu); return; }
                @SuppressWarnings("deprecation") OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                cb.accept(op != null && (op.hasPlayedBefore() || op.isOnline()) ? op.getUniqueId() : null);
            });
        });
    }

    private void diff(CommandSender s, Player p, Snap snap) {
        List<String> bad = new ArrayList<String>();
        ItemStack[] cur = p.getInventory().getContents();
        for (int i = 0; i < INV; i++) if (!same(cur.length > i ? cur[i] : null, snap.inv[i])) bad.add(InvSnapRules.slotName(i) + ": " + desc(cur.length > i ? cur[i] : null) + " ≠ " + desc(snap.inv[i]));
        ItemStack[] en = p.getEnderChest().getContents();
        for (int i = 0; i < ENDER; i++) if (!same(en.length > i ? en[i] : null, snap.ender[i])) bad.add("末影箱" + (i + 1) + ": " + desc(en.length > i ? en[i] : null) + " ≠ " + desc(snap.ender[i]));
        s.sendMessage(P + p.getName() + " 对比快照 #" + snap.id + "（" + snap.count() + " 格）：" + (bad.isEmpty() ? ChatColor.GREEN + "完全一致 MATCH" : ChatColor.RED + "不一致 " + bad.size() + " 格"));
        for (int k = 0; k < bad.size() && k < 12; k++) s.sendMessage(ChatColor.GRAY + "  " + bad.get(k));
        s.sendMessage(ChatColor.GRAY + "（快照只管背包+末影箱；材料仓 / 扭蛋钱包不在 diff 范围，恢复时也不会回滚）");
        EmberVault v = EmberVault.get();
        if (v != null && v.enabled()) {
            Map<String, Long> all = v.all(p.getUniqueId());
            if (all.isEmpty()) s.sendMessage(ChatColor.GRAY + "材料仓：空");
            else {
                StringBuilder sb = new StringBuilder();
                int n = 0;
                for (Map.Entry<String, Long> e : all.entrySet()) {
                    if (n++ >= 6) { sb.append(" …"); break; }
                    if (sb.length() > 0) sb.append(" · ");
                    sb.append(e.getKey()).append("=").append(e.getValue());
                }
                s.sendMessage(ChatColor.GRAY + "材料仓现有：" + sb);
            }
        }
    }

    private static boolean same(ItemStack a, ItemStack b) {
        boolean ra = real(a), rb = real(b);
        if (!ra || !rb) return ra == rb;
        return a.equals(b);
    }

    private static String desc(ItemStack s) {
        if (!real(s)) return "空";
        String n = s.hasItemMeta() && s.getItemMeta().hasDisplayName() ? ChatColor.stripColor(s.getItemMeta().getDisplayName()) : s.getType().name();
        return n + "×" + s.getAmount();
    }

    // ================================================================== read-only GUI

    public static final class View implements InventoryHolder {
        final Snap snap; final int page;
        View(Snap snap, int page) { this.snap = snap; this.page = page; }
        @Override public Inventory getInventory() { return null; }
    }

    private void openView(Player viewer, Snap s, int page) {
        Inventory inv = Bukkit.createInventory(new View(s, page), 54,
                cut("§8快照#" + s.id + " " + (page == 0 ? "背包" : "末影箱") + " " + s.name));
        if (page == 0) {
            for (int i = 9; i <= 35; i++) if (real(s.inv[i])) inv.setItem(i - 9, s.inv[i].clone());
            for (int i = 0; i <= 8; i++) if (real(s.inv[i])) inv.setItem(27 + i, s.inv[i].clone());
            for (int i = 36; i <= 40; i++) if (real(s.inv[i])) inv.setItem(i, s.inv[i].clone()); // boots, legs, chest, helmet, offhand
            for (int i = 41; i <= 44; i++) inv.setItem(i, icon(Material.STAINED_GLASS_PANE, "§7← 36-39 护甲(靴/腿/胸/头) · 40 副手"));
        } else {
            for (int i = 0; i < ENDER; i++) if (real(s.ender[i])) inv.setItem(i, s.ender[i].clone());
        }
        inv.setItem(45, icon(Material.ENDER_CHEST, page == 0 ? "§d查看末影箱 →" : "§a← 查看背包"));
        inv.setItem(49, icon(Material.PAPER, "§e#" + s.id + " · " + fmt(s.at), "§7原因 " + s.reason + " · 世界 " + s.world,
                "§7共 " + s.count() + " 格物品", "§c只读预览，不能拿取", "§7恢复：/corerpg invsnap restore " + s.name + " " + s.id));
        inv.setItem(53, icon(Material.BARRIER, "§c关闭"));
        viewer.openInventory(inv);
    }

    private static String cut(String t) { return t.length() > 32 ? t.substring(0, 32) : t; }

    private static ItemStack icon(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(name);
        if (lore.length > 0) im.setLore(Arrays.asList(lore));
        it.setItemMeta(im);
        return it;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof View)) return;
        e.setCancelled(true); // read-only: every click, both inventories, shift / number keys / creative clone
        if (!(e.getWhoClicked() instanceof Player) || e.getRawSlot() < 0 || e.getRawSlot() >= 54) return;
        View v = (View) e.getInventory().getHolder();
        final Player p = (Player) e.getWhoClicked();
        if (e.getRawSlot() == 45) Bukkit.getScheduler().runTask(plugin, () -> openView(p, v.snap, 1 - v.page));
        else if (e.getRawSlot() == 53) Bukkit.getScheduler().runTask(plugin, p::closeInventory);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof View) e.setCancelled(true);
    }

    // ================================================================== util

    private static void msg(CommandSender s, String m) { if (s != null) s.sendMessage(P + m); }

    static String fmt(long t) {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return f.format(new Date(t)) + " CST";
    }

    static String sha1(String s) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-1").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder b = new StringBuilder();
            for (byte x : h) b.append(String.format("%02x", x & 0xff));
            return b.toString();
        } catch (Exception e) { return Integer.toHexString(s.hashCode()); }
    }

    static byte[] gzip(String s) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try (GZIPOutputStream g = new GZIPOutputStream(bo)) { g.write(s.getBytes(StandardCharsets.UTF_8)); } catch (IOException e) { throw new IllegalStateException(e); }
        return bo.toByteArray();
    }

    static String gunzip(byte[] b) {
        try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(b))) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) bo.write(buf, 0, n);
            return new String(bo.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) { return null; }
    }
}
