package town.sunshine.coregacha;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.Engine;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.PityState;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Result;
import town.sunshine.coregacha.engine.Tier;

/**
 * Every write goes through one single-thread DB executor; each operation is one MySQL transaction with
 * SELECT … FOR UPDATE on the rows it changes. CoreRpg grants happen on the main thread while the transaction is still
 * open; a failed grant (or a failed commit) rolls the whole thing back and undoes the grants — the player is never
 * charged for a reward he did not get (design §6).
 */
public final class GachaService {
    static final String P = "§d[扭蛋] §7";
    static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    final CoreGachaPlugin pl;
    final Db db;
    final CoreRpgBridge rpg;
    volatile GachaConfig cfg;
    volatile Engine engine;
    volatile RateMath rates;
    final ExecutorService exec = Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "CoreGacha-DB"); t.setDaemon(true); return t; });
    final ConcurrentHashMap<UUID, PCache> cache = new ConcurrentHashMap<UUID, PCache>();
    final Set<UUID> busy = ConcurrentHashMap.newKeySet();
    final SecureRandom rng = new SecureRandom();
    /** admin test hook: the next CoreRpg grant for this player fails (exercises the rollback / refund path) */
    final Set<UUID> failNext = ConcurrentHashMap.newKeySet();

    GachaService(CoreGachaPlugin pl, Db db, CoreRpgBridge rpg, GachaConfig cfg) {
        this.pl = pl; this.db = db; this.rpg = rpg;
        setConfig(cfg);
    }

    void setConfig(GachaConfig c) {
        for (Item i : c.items.values())
            if (i.external() && !rpg.isShopCosmetic(i.ref)) { c.disabled.add(i.id); pl.getLogger().warning("item " + i.id + ": CoreRpg shop id '" + i.ref + "' not found — disabled"); }
        this.cfg = c; this.engine = new Engine(c); this.rates = new RateMath(c);
    }

    static String today() { return LocalDate.now(ZONE).toString(); }

    PCache get(UUID u) { return cache.get(u); }

    void shutdown() {
        exec.shutdown();
        try { exec.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private void main(Runnable r) { if (pl.isEnabled()) Bukkit.getScheduler().runTask(pl, r); }

    private static void msg(UUID u, String m) { Player p = Bukkit.getPlayer(u); if (p != null) p.sendMessage(P + m); }

    // ------------------------------------------------------------------ helpers

    interface Tx<T> { T run(Connection c) throws Exception; }

    /** run in the DB thread inside one transaction; result (or null on error) delivered on the main thread */
    <T> void tx(String what, Tx<T> body, Consumer<T> done) {
        exec.submit(() -> {
            T out = null;
            try (Connection c = db.get()) {
                c.setAutoCommit(false);
                try { out = body.run(c); }
                catch (Exception e) { try { c.rollback(); } catch (SQLException ignored) { } throw e; }
                finally { try { c.setAutoCommit(true); } catch (SQLException ignored) { } }
            } catch (Exception e) {
                pl.getLogger().log(Level.WARNING, "[tx] " + what + " failed: " + e, e);
            }
            final T r = out;
            if (done != null) main(() -> done.accept(r));
        });
    }

    static void ensureWallet(Connection c, UUID u, String name) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_wallet (uuid, name) VALUES (?, ?)")) { ps.setString(1, u.toString()); ps.setString(2, name); ps.executeUpdate(); }
    }

    static int[] lockWallet(Connection c, UUID u) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT tickets, shards, welcomed FROM gacha_wallet WHERE uuid=? FOR UPDATE")) {
            ps.setString(1, u.toString());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)} : new int[]{0, 0, 0}; }
        }
    }

    static void setWallet(Connection c, UUID u, int tickets, int shards) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_wallet SET tickets=?, shards=? WHERE uuid=?")) { ps.setInt(1, tickets); ps.setInt(2, shards); ps.setString(3, u.toString()); ps.executeUpdate(); }
    }

    static void ledger(Connection c, UUID u, String name, int dt, int ds, int ta, int sa, String reason, String ref) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO gacha_ledger (uuid, name, d_tickets, d_shards, tickets_after, shards_after, reason, ref) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, u.toString()); ps.setString(2, name); ps.setInt(3, dt); ps.setInt(4, ds); ps.setInt(5, ta); ps.setInt(6, sa); ps.setString(7, reason); ps.setString(8, ref);
            ps.executeUpdate();
        }
    }

    /** an audit row outside the (rolled back) transaction */
    void ledgerAlone(UUID u, String name, int ta, int sa, String reason, String ref) {
        try (Connection c = db.get()) { ledger(c, u, name, 0, 0, ta, sa, reason, ref); }
        catch (SQLException e) { pl.getLogger().warning("[ledger] " + reason + ": " + e); }
    }

    static int dailyLock(Connection c, UUID u, String day, String k) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_daily (uuid, day, k, v) VALUES (?,?,?,0)")) { ps.setString(1, u.toString()); ps.setString(2, day); ps.setString(3, k); ps.executeUpdate(); }
        try (PreparedStatement ps = c.prepareStatement("SELECT v FROM gacha_daily WHERE uuid=? AND day=? AND k=? FOR UPDATE")) {
            ps.setString(1, u.toString()); ps.setString(2, day); ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    static void dailySet(Connection c, UUID u, String day, String k, int v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_daily SET v=? WHERE uuid=? AND day=? AND k=?")) { ps.setInt(1, v); ps.setString(2, u.toString()); ps.setString(3, day); ps.setString(4, k); ps.executeUpdate(); }
    }

    static Set<String> ownedRows(Connection c, UUID u) throws SQLException {
        Set<String> s = new HashSet<String>();
        try (PreparedStatement ps = c.prepareStatement("SELECT item FROM gacha_owned WHERE uuid=?")) {
            ps.setString(1, u.toString());
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) s.add(rs.getString(1)); }
        }
        return s;
    }

    static void addOwned(Connection c, UUID u, String item, String source, String batch) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_owned (uuid, item, source, batch_id) VALUES (?,?,?,?)")) {
            ps.setString(1, u.toString()); ps.setString(2, item); ps.setString(3, source); ps.setString(4, batch); ps.executeUpdate();
        }
    }

    /**
     * Grant CoreRpg cosmetics on the main thread while the caller's transaction is open, then commit. Any failure →
     * rollback + revoke what was granted. Returns null when committed, else the player-facing reason.
     */
    String grantAndCommit(Connection c, UUID u, List<String> refs) throws SQLException {
        final List<String> granted = new ArrayList<String>();
        if (!refs.isEmpty()) {
            final AtomicInteger state = new AtomicInteger(); // 0 pending · 1 started · 2 cancelled
            Future<Boolean> f = Bukkit.getScheduler().callSyncMethod(pl, () -> {
                if (!state.compareAndSet(0, 1)) return false;
                if (failNext.remove(u)) { pl.getLogger().warning("[test] failnext: CoreRpg grant forced to fail for " + u); return false; }
                for (String id : refs) {
                    if (!rpg.grantCosmetic(u, id)) { for (String g : granted) rpg.revokeCosmetic(u, g); granted.clear(); return false; }
                    granted.add(id);
                }
                return true;
            });
            Boolean ok;
            try { ok = f.get(10, TimeUnit.SECONDS); }
            catch (Exception e) {
                if (state.compareAndSet(0, 2)) ok = false;
                else { try { ok = f.get(); } catch (Exception e2) { ok = false; } }
            }
            if (!Boolean.TRUE.equals(ok)) { c.rollback(); return "CoreRpg 外观发放失败"; }
        }
        try { c.commit(); return null; }
        catch (SQLException e) {
            if (!granted.isEmpty()) main(() -> { for (String g : granted) rpg.revokeCosmetic(u, g); });
            throw e;
        }
    }

    // ------------------------------------------------------------------ load / settle

    void load(Player p) {
        final UUID u = p.getUniqueId();
        final String name = p.getName();
        final String day = today();
        final GachaConfig c0 = cfg;
        tx("load " + name, c -> {
            ensureWallet(c, u, name);
            try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_wallet SET name=? WHERE uuid=?")) { ps.setString(1, name); ps.setString(2, u.toString()); ps.executeUpdate(); }
            int[] w = lockWallet(c, u);
            // limited banners that ended: leftover spark → 光屑 (design §2.2), or (1.0.1, spark_leftover: carry) 1:1 into
            // the retire_to banner's spark — the limited items retire into that banner, so the points keep their value
            int gain = 0;
            List<String> settled = new ArrayList<String>();
            Map<String, Integer> carry = new java.util.LinkedHashMap<String, Integer>();
            try (PreparedStatement ps = c.prepareStatement("SELECT banner, spark FROM gacha_banner_state WHERE uuid=? AND settled=0 FOR UPDATE")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Banner b = c0.banner(rs.getString(1));
                        if (b == null || !b.limited() || !b.ended(System.currentTimeMillis())) continue;
                        settled.add(b.id);
                        Banner to = "carry".equals(b.sparkLeftover) ? c0.banner(b.retireTo) : null;
                        if (to != null) { if (rs.getInt(2) > 0) carry.merge(to.id + "<" + b.id, rs.getInt(2), Integer::sum); }
                        else gain += rs.getInt(2) * c0.sparkLeftoverShard;
                    }
                }
            }
            for (String b : settled) try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_banner_state SET spark=0, settled=1 WHERE uuid=? AND banner=?")) {
                ps.setString(1, u.toString()); ps.setString(2, b); ps.executeUpdate();
            }
            for (Map.Entry<String, Integer> e : carry.entrySet()) {
                String to = e.getKey().substring(0, e.getKey().indexOf('<'));
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO gacha_banner_state (uuid, banner, spark) VALUES (?,?,?) ON DUPLICATE KEY UPDATE spark=spark+VALUES(spark)")) {
                    ps.setString(1, u.toString()); ps.setString(2, to); ps.setInt(3, e.getValue()); ps.executeUpdate();
                }
                ledger(c, u, name, 0, 0, w[0], w[1], "spark_carry", e.getKey() + " +" + e.getValue());
            }
            if (gain > 0) { setWallet(c, u, w[0], w[1] + gain); ledger(c, u, name, 0, gain, w[0], w[1] + gain, "spark_leftover", String.join(",", settled)); w[1] += gain; }
            c.commit();
            PCache pc = new PCache(name);
            pc.tickets = w[0]; pc.shards = w[1]; pc.welcomed = w[2] > 0; pc.day = day;
            try (PreparedStatement ps = c.prepareStatement("SELECT pity_group, pity5, pity4 FROM gacha_pity WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) pc.pity.put(rs.getString(1), new int[]{rs.getInt(2), rs.getInt(3)}); }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT banner, spark, pulls, last_legend FROM gacha_banner_state WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) pc.banners.put(rs.getString(1), new PCache.BState(rs.getInt(2), rs.getInt(3), rs.getString(4))); }
            }
            pc.owned.addAll(ownedRows(c, u));
            try (PreparedStatement ps = c.prepareStatement("SELECT kind, item FROM gacha_wear WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) pc.wear.put(rs.getString(1), rs.getString(2)); }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT k, v FROM gacha_daily WHERE uuid=? AND day=?")) {
                ps.setString(1, u.toString()); ps.setString(2, day);
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) pc.daily.put(rs.getString(1), rs.getInt(2)); }
            }
            pc.onlineMin = pc.dailyGet("online_min", day);
            List<PCache.Hist> h = new ArrayList<PCache.Hist>();
            try (PreparedStatement ps = c.prepareStatement("SELECT pull_id, created_at, banner, tier, item, dup, shards, rule FROM gacha_pull WHERE uuid=? AND status='granted' ORDER BY pull_id DESC LIMIT 100")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) h.add(new PCache.Hist(rs.getLong(1), rs.getTimestamp(2).getTime(), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6) > 0, rs.getInt(7), rs.getString(8)));
                }
            }
            pc.hist = java.util.Collections.unmodifiableList(h);
            final int g = gain; final List<String> st = settled;
            if (g > 0) main(() -> msg(u, "限定池 " + st + " 已结束：剩下的火花折成 §b" + g + " 光屑§7。"));
            return pc;
        }, pc -> { if (pc != null && Bukkit.getPlayer(u) != null) { cache.put(u, pc); pl.render().onLoaded(Bukkit.getPlayer(u)); } });
    }

    // ------------------------------------------------------------------ pull

    /** the CoreRpg cosmetics of this pool the player already owns (main thread snapshot; re-checked at grant time) */
    Set<String> rpgOwned(UUID u, Banner.Pool pool) {
        Set<String> s = new HashSet<String>();
        for (Item i : pool.all()) if (i.external() && rpg.hasCosmetic(u, i.ref)) s.add(i.id);
        return s;
    }

    boolean owns(UUID u, Item i) {
        PCache pc = cache.get(u);
        if (i.external()) return rpg.hasCosmetic(u, i.ref);
        return pc != null && pc.owned.contains(i.id);
    }

    static final class Outcome {
        String error; List<Result> results; List<PCache.Hist> hist; int tickets, shards, pity5, pity4, today; PCache.BState bs; String batch;
    }

    void pull(Player p, String bannerId, int n) {
        final UUID u = p.getUniqueId();
        final GachaConfig c0 = cfg;
        final Engine e0 = engine;
        final Banner b = c0.banner(bannerId);
        if (n != 1 && n != 10) { p.sendMessage(P + "§c只能单抽或十连：/gacha pull <池> <1|10>"); return; }
        if (b == null) { p.sendMessage(P + "§c没有这个池：" + bannerId + "（" + String.join(" / ", c0.banners.keySet()) + "）"); return; }
        final long now = System.currentTimeMillis();
        if (!b.open(now)) { p.sendMessage(P + "§c「" + b.name + "§c」现在没开放" + (b.ended(now) ? "（已结束，外观已转入常驻池）" : "")); return; }
        PCache pc = cache.get(u);
        if (pc == null) { p.sendMessage(P + "§c数据还在加载，稍等一秒再试。"); return; }
        final int cost = n * c0.costPerPull;
        if (pc.tickets < cost) {
            int phys = pl.sources().countPhysical(p);
            p.sendMessage(P + "§c扭蛋券不够：需要 " + cost + "，现有 " + pc.tickets + (phys > 0 ? "§7（背包里还有 " + phys + " 张实物券：右键或 /gacha redeem 存入）" : "§7（来源：在线、主线结算、余烬币 / 余烬徽兑换 → /gacha）"));
            return;
        }
        final String day = today();
        if (pc.dailyGet("pulls", day) + n > c0.dailyPullCap) { p.sendMessage(P + "§c今天已抽 " + pc.dailyGet("pulls", day) + "/" + c0.dailyPullCap + " 次，明天再来（每日上限，防止无上限抽奖）"); return; }
        if (!busy.add(u)) { p.sendMessage(P + "§c上一次抽卡还没结束。"); return; }
        final Banner.Pool pool = c0.pool(b, now);
        final Set<String> rpgHave = rpgOwned(u, pool);
        final String name = p.getName();
        tx("pull " + name + " " + b.id + " x" + n, c -> {
            Outcome o = new Outcome();
            ensureWallet(c, u, name);
            int[] w = lockWallet(c, u);
            if (w[0] < cost) { c.rollback(); o.error = "扭蛋券不够：需要 " + cost + "，现有 " + w[0]; return o; }
            int today = dailyLock(c, u, day, "pulls");
            if (today + n > c0.dailyPullCap) { c.rollback(); o.error = "今天已抽 " + today + "/" + c0.dailyPullCap + " 次"; return o; }
            int p5 = 0, p4 = 0, spark = 0, pulls = 0;
            String last = null;
            try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_pity (uuid, pity_group) VALUES (?,?)")) { ps.setString(1, u.toString()); ps.setString(2, b.pityGroup); ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("SELECT pity5, pity4 FROM gacha_pity WHERE uuid=? AND pity_group=? FOR UPDATE")) {
                ps.setString(1, u.toString()); ps.setString(2, b.pityGroup);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { p5 = rs.getInt(1); p4 = rs.getInt(2); } }
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_banner_state (uuid, banner) VALUES (?,?)")) { ps.setString(1, u.toString()); ps.setString(2, b.id); ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("SELECT spark, pulls, last_legend FROM gacha_banner_state WHERE uuid=? AND banner=? FOR UPDATE")) {
                ps.setString(1, u.toString()); ps.setString(2, b.id);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { spark = rs.getInt(1); pulls = rs.getInt(2); last = rs.getString(3); } }
            }
            Set<String> owned = ownedRows(c, u);
            owned.addAll(rpgHave);
            PityState st = new PityState(p5, p4, last, spark);
            List<Result> rs = e0.pullN(pool, st, owned, n, rng::nextDouble);
            int gain = 0;
            for (Result r : rs) gain += r.shards;
            String batch = UUID.randomUUID().toString();
            setWallet(c, u, w[0] - cost, w[1] + gain);
            dailySet(c, u, day, "pulls", today + n);
            try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_pity SET pity5=?, pity4=? WHERE uuid=? AND pity_group=?")) {
                ps.setInt(1, st.pity5); ps.setInt(2, st.pity4); ps.setString(3, u.toString()); ps.setString(4, b.pityGroup); ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_banner_state SET spark=?, pulls=?, last_legend=? WHERE uuid=? AND banner=?")) {
                ps.setInt(1, st.spark); ps.setInt(2, pulls + n); ps.setString(3, st.lastLegend); ps.setString(4, u.toString()); ps.setString(5, b.id); ps.executeUpdate();
            }
            List<String> ext = new ArrayList<String>();
            for (Result r : rs) if (!r.dup) { addOwned(c, u, r.item.id, "pull", batch); if (r.item.external()) ext.add(r.item.ref); }
            List<PCache.Hist> hist = new ArrayList<PCache.Hist>();
            long at = System.currentTimeMillis();
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO gacha_pull (batch_id, uuid, name, banner, pity_group, seq, tier, item, dup, shards, pity5_before, pity5_after,"
                    + " pity4_before, pity4_after, spark_after, rule, cost, status, created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, 'granted', ?)", Statement.RETURN_GENERATED_KEYS)) {
                int seq = 0;
                for (Result r : rs) {
                    seq++;
                    ps.setString(1, batch); ps.setString(2, u.toString()); ps.setString(3, name); ps.setString(4, b.id); ps.setString(5, b.pityGroup);
                    ps.setInt(6, seq); ps.setString(7, r.tier.key()); ps.setString(8, r.item.id); ps.setInt(9, r.dup ? 1 : 0); ps.setInt(10, r.shards);
                    ps.setInt(11, r.pity5Before); ps.setInt(12, r.pity5After); ps.setInt(13, r.pity4Before); ps.setInt(14, r.pity4After); ps.setInt(15, r.sparkAfter);
                    ps.setString(16, r.rule); ps.setInt(17, c0.costPerPull); ps.setTimestamp(18, new java.sql.Timestamp(at));
                    ps.executeUpdate();
                    long id = 0;
                    try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) id = k.getLong(1); }
                    hist.add(new PCache.Hist(id, at, b.id, r.tier.key(), r.item.id, r.dup, r.shards, r.rule));
                }
            }
            ledger(c, u, name, -cost, gain, w[0] - cost, w[1] + gain, "pull", batch);
            String err = grantAndCommit(c, u, ext);
            if (err != null) { ledgerAlone(u, name, w[0], w[1], "pull_rolled_back", batch); o.error = err + "，这次没有扣券（已退还）。请再试一次。"; return o; }
            o.results = rs; o.hist = hist; o.tickets = w[0] - cost; o.shards = w[1] + gain; o.pity5 = st.pity5; o.pity4 = st.pity4; o.today = today + n;
            o.bs = new PCache.BState(st.spark, pulls + n, st.lastLegend); o.batch = batch;
            return o;
        }, o -> {
            busy.remove(u);
            Player pp = Bukkit.getPlayer(u);
            if (o == null) { if (pp != null) pp.sendMessage(P + "§c数据库出错，这次没有扣券。"); return; }
            if (o.error != null) { if (pp != null) pp.sendMessage(P + "§c" + o.error); return; }
            PCache c1 = cache.get(u);
            if (c1 != null) {
                c1.tickets = o.tickets; c1.shards = o.shards;
                c1.pity.put(b.pityGroup, new int[]{o.pity5, o.pity4});
                c1.banners.put(b.id, o.bs);
                if (!day.equals(c1.day)) { c1.daily.clear(); c1.day = day; }
                c1.daily.put("pulls", o.today);
                for (Result r : o.results) if (!r.dup) c1.owned.add(r.item.id);
                c1.pushHist(o.hist);
                c1.reveal = java.util.Collections.unmodifiableList(o.hist);
                c1.revealBanner = b.id;
            }
            pl.getLogger().info("[pull] " + name + " " + b.id + " x" + n + " batch " + o.batch + " → " + summary(o.results));
            if (pp != null) pl.anim().play(pp, b, o.results, o.tickets, o.shards);
        });
    }

    static String summary(List<Result> rs) {
        StringBuilder s = new StringBuilder();
        for (Result r : rs) s.append(r.tier.key().charAt(0)).append(':').append(r.item.id).append(r.dup ? "(dup)" : "").append(' ');
        return s.toString().trim();
    }

    // ------------------------------------------------------------------ craft / spark

    /** every item a player can craft right now: the pools of all open banners (standard includes retired limited items) */
    List<Item> craftable() {
        long now = System.currentTimeMillis();
        java.util.LinkedHashSet<Item> s = new java.util.LinkedHashSet<Item>();
        for (Banner b : cfg.banners.values()) if (b.open(now)) s.addAll(cfg.pool(b, now).all());
        List<Item> l = new ArrayList<Item>(s);
        l.sort((a, b) -> a.tier != b.tier ? a.tier.compareTo(b.tier) : 0);
        return l;
    }

    void craft(Player p, String itemId) {
        final GachaConfig c0 = cfg;
        final Item it = c0.items.get(itemId);
        if (it == null || !craftable().contains(it)) { p.sendMessage(P + "§c光屑兑换里没有这件：" + itemId); return; }
        acquire(p, it, "craft", null, c0.craftPrice.get(it.tier));
    }

    void spark(Player p, String bannerId, String itemId) {
        final GachaConfig c0 = cfg;
        Banner b = c0.banner(bannerId);
        long now = System.currentTimeMillis();
        if (b == null || !b.open(now)) { p.sendMessage(P + "§c这个池现在没开放：" + bannerId); return; }
        Item it = c0.items.get(itemId);
        if (it == null || !c0.pool(b, now).contains(it.id)) { p.sendMessage(P + "§c「" + b.name + "§c」里没有这件：" + itemId); return; }
        if (!c0.sparkAllows(b, it.id, now)) { p.sendMessage(P + "§c「" + b.name + "§c」的火花只能换本池限定传说"); return; }
        acquire(p, it, "spark", b, 0);
    }

    /** craft (shards) or spark (banner spark points): one transaction, CoreRpg grant inside it */
    private void acquire(Player p, Item it, String how, Banner b, int price) {
        final UUID u = p.getUniqueId();
        final String name = p.getName();
        if (owns(u, it)) { p.sendMessage(P + "你已经有「" + it.display() + "§7」了。"); return; }
        if (!busy.add(u)) { p.sendMessage(P + "§c上一个操作还没结束。"); return; }
        final int need = cfg.sparkFor(b);
        tx(how + " " + name + " " + it.id, c -> {
            ensureWallet(c, u, name);
            int[] w = lockWallet(c, u);
            if (ownedRows(c, u).contains(it.id)) { c.rollback(); return "你已经有这件了"; }
            int spark = 0;
            if (b != null) {
                try (PreparedStatement ps = c.prepareStatement("SELECT spark FROM gacha_banner_state WHERE uuid=? AND banner=? FOR UPDATE")) {
                    ps.setString(1, u.toString()); ps.setString(2, b.id);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) spark = rs.getInt(1); }
                }
                if (spark < need) { c.rollback(); return "火花不够：" + spark + "/" + need; }
                try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_banner_state SET spark=spark-? WHERE uuid=? AND banner=?")) { ps.setInt(1, need); ps.setString(2, u.toString()); ps.setString(3, b.id); ps.executeUpdate(); }
            } else {
                if (w[1] < price) { c.rollback(); return "光屑不够：" + w[1] + "/" + price; }
                setWallet(c, u, w[0], w[1] - price);
            }
            String ref = UUID.randomUUID().toString();
            addOwned(c, u, it.id, how, ref);
            ledger(c, u, name, 0, -price, w[0], w[1] - price, how, it.id + (b == null ? "" : "@" + b.id));
            List<String> ext = new ArrayList<String>();
            if (it.external()) ext.add(it.ref);
            String err = grantAndCommit(c, u, ext);
            if (err != null) { ledgerAlone(u, name, w[0], w[1], how + "_rolled_back", it.id); return err + "，没有扣" + (b == null ? "光屑" : "火花") + "（已退还）"; }
            return "OK:" + (w[1] - price) + ":" + (spark - need);
        }, r -> {
            busy.remove(u);
            Player pp = Bukkit.getPlayer(u);
            if (r == null) { if (pp != null) pp.sendMessage(P + "§c数据库出错，没有扣除。"); return; }
            if (!r.startsWith("OK:")) { if (pp != null) pp.sendMessage(P + "§c" + r); return; }
            String[] a = r.split(":");
            PCache pc = cache.get(u);
            if (pc != null) {
                pc.owned.add(it.id);
                if (b == null) pc.shards = Integer.parseInt(a[1]);
                else { PCache.BState bs = pc.banners.get(b.id); if (bs != null) bs.spark = Integer.parseInt(a[2]); }
            }
            pl.getLogger().info("[" + how + "] " + name + " " + it.id + (b == null ? " for " + price + " shards" : " spark@" + b.id));
            if (pp != null) {
                pp.sendMessage(P + "§a" + ("spark".equals(how) ? "火花兑换" : "光屑兑换") + "：获得 " + it.tier.colored() + " §f" + Item.stripColor(it.name) + " §7（" + it.kindLabel() + "）"
                        + (b == null ? " · 剩余光屑 " + a[1] : ""));
                pl.render().afterGain(pp, it);
            }
        });
    }

    // ------------------------------------------------------------------ tickets

    /** add tickets; {@code onceKey} != null = at most once per day for that key (gameplay sources) */
    void addTickets(UUID u, String name, int n, String reason, String onceKey, String note) {
        final String day = today();
        tx("tickets " + name + " " + reason, c -> {
            ensureWallet(c, u, name);
            if (onceKey != null) {
                try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO gacha_daily (uuid, day, k, v) VALUES (?,?,?,1)")) {
                    ps.setString(1, u.toString()); ps.setString(2, day); ps.setString(3, onceKey);
                    if (ps.executeUpdate() == 0) { c.rollback(); return -1; }
                }
            }
            int[] w = lockWallet(c, u);
            setWallet(c, u, w[0] + n, w[1]);
            ledger(c, u, name, n, 0, w[0] + n, w[1], reason, onceKey);
            c.commit();
            return w[0] + n;
        }, t -> {
            if (t == null || t < 0) return;
            PCache pc = cache.get(u);
            if (pc != null) { pc.tickets = t; if (onceKey != null) { if (!day.equals(pc.day)) { pc.daily.clear(); pc.day = day; } pc.daily.put(onceKey, 1); } }
            if (note != null) msg(u, "§a+" + n + " 扭蛋券§7（" + note + "）· 现有 §f" + t + "§7 张 · /gacha");
        });
    }

    /** first /gacha: the one-time welcome tickets */
    void welcome(Player p) {
        PCache pc = cache.get(p.getUniqueId());
        int n = pl.getConfig().getInt("tickets.welcome", 5);
        if (pc == null || pc.welcomed || n <= 0) return;
        pc.welcomed = true;
        final UUID u = p.getUniqueId(); final String name = p.getName();
        tx("welcome " + name, c -> {
            int[] w = lockWallet(c, u);
            if (w[2] > 0) { c.rollback(); return -1; }
            try (PreparedStatement ps = c.prepareStatement("UPDATE gacha_wallet SET welcomed=1, tickets=tickets+? WHERE uuid=?")) { ps.setInt(1, n); ps.setString(2, u.toString()); ps.executeUpdate(); }
            ledger(c, u, name, n, 0, w[0] + n, w[1], "welcome", null);
            c.commit();
            return w[0] + n;
        }, t -> {
            if (t == null || t < 0) return;
            PCache c1 = cache.get(u);
            if (c1 != null) c1.tickets = t;
            msg(u, "§a见面礼：+" + n + " 扭蛋券§7（只出外观，零属性；之后每天在线、主线结算都会给券）");
        });
    }

    /** 余烬币 / 余烬徽 → 扭蛋券: take on the main thread, add in a transaction, give back if that fails */
    void exchange(Player p, String what, int n) {
        final UUID u = p.getUniqueId(); final String name = p.getName();
        boolean coin = "coin".equals(what);
        if (!coin && !"badge".equals(what)) { p.sendMessage(P + "/gacha exchange <coin|badge> [张数]"); return; }
        if (!rpg.ready()) { p.sendMessage(P + "§cCoreRpg 没加载，暂时不能兑换。"); return; }
        int cap = pl.getConfig().getInt("tickets.exchange.daily_cap", 5);
        PCache pc = cache.get(u);
        if (pc == null) return;
        final String day = today();
        int left = cap - pc.dailyGet("exch", day);
        if (left <= 0) { p.sendMessage(P + "§c今天已经换了 " + cap + " 张（每日上限）。"); return; }
        final int k = Math.max(1, Math.min(n, left));
        final int unit = pl.getConfig().getInt(coin ? "tickets.exchange.coin" : "tickets.exchange.badge", coin ? 1200 : 20);
        final int price = unit * k;
        if (!busy.add(u)) { p.sendMessage(P + "§c上一个操作还没结束。"); return; }
        boolean took = coin ? rpg.takeCoin(u, price) : rpg.takeBadges(u, price);
        if (!took) {
            busy.remove(u);
            p.sendMessage(P + "§c" + (coin ? "余烬币" : "余烬徽") + "不够：" + k + " 张要 " + price + "，现有 " + (coin ? rpg.coin(u) : rpg.badges(u)));
            return;
        }
        tx("exchange " + name + " " + what + " x" + k, c -> {
            ensureWallet(c, u, name);
            int done = dailyLock(c, u, day, "exch");
            if (done + k > cap) { c.rollback(); return -1; }
            int[] w = lockWallet(c, u);
            setWallet(c, u, w[0] + k, w[1]);
            dailySet(c, u, day, "exch", done + k);
            ledger(c, u, name, k, 0, w[0] + k, w[1], coin ? "exchange_coin" : "exchange_badge", String.valueOf(price));
            c.commit();
            return w[0] + k;
        }, t -> {
            busy.remove(u);
            if (t == null || t < 0) {
                if (coin) rpg.addCoin(u, price); else rpg.addBadges(u, price);
                pl.getLogger().warning("[exchange] " + name + " failed, refunded " + price + " " + what);
                msg(u, "§c兑换失败，已退还 " + price + (coin ? " 余烬币" : " 余烬徽") + "。");
                return;
            }
            PCache c1 = cache.get(u);
            if (c1 != null) { c1.tickets = t; if (!day.equals(c1.day)) { c1.daily.clear(); c1.day = day; } c1.daily.merge("exch", k, Integer::sum); }
            pl.getLogger().info("[exchange] " + name + " " + price + " " + what + " → " + k + " tickets");
            msg(u, "§a用 " + price + (coin ? " 余烬币" : " 余烬徽") + " 换了 " + k + " 张扭蛋券§7 · 现有 §f" + t + "§7 张（今天还能换 " + (left - k) + " 张）");
        });
    }

    void wear(Player p, Item it, boolean off, String kind) {
        final UUID u = p.getUniqueId();
        PCache pc = cache.get(u);
        if (pc == null) return;
        final String k = off ? kind : it.kind;
        if (!off && !pc.owned.contains(it.id)) { p.sendMessage(P + "§c还没有「" + it.display() + "§c」"); return; }
        if (off) pc.wear.remove(k); else pc.wear.put(k, it.id);
        tx("wear " + p.getName(), c -> {
            if (off) try (PreparedStatement ps = c.prepareStatement("DELETE FROM gacha_wear WHERE uuid=? AND kind=?")) { ps.setString(1, u.toString()); ps.setString(2, k); ps.executeUpdate(); }
            else try (PreparedStatement ps = c.prepareStatement("REPLACE INTO gacha_wear (uuid, kind, item) VALUES (?,?,?)")) { ps.setString(1, u.toString()); ps.setString(2, k); ps.setString(3, it.id); ps.executeUpdate(); }
            c.commit();
            return true;
        }, null);
        pl.render().onWearChanged(p, k);
    }

    void saveOnline(UUID u, int minutes) {
        final String day = today();
        tx("online", c -> {
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO gacha_daily (uuid, day, k, v) VALUES (?,?, 'online_min', ?) ON DUPLICATE KEY UPDATE v=GREATEST(v, VALUES(v))")) {
                ps.setString(1, u.toString()); ps.setString(2, day); ps.setInt(3, minutes); ps.executeUpdate();
            }
            c.commit();
            return true;
        }, null);
    }

    void giveAdmin(org.bukkit.command.CommandSender s, UUID u, String name, int n) {
        tx("admin give " + name, c -> {
            ensureWallet(c, u, name);
            int[] w = lockWallet(c, u);
            int t = Math.max(0, w[0] + n);
            setWallet(c, u, t, w[1]);
            ledger(c, u, name, t - w[0], 0, t, w[1], "admin", s.getName());
            c.commit();
            return t;
        }, t -> {
            if (t == null) { s.sendMessage(P + "§c失败（看日志）"); return; }
            PCache pc = cache.get(u);
            if (pc != null) pc.tickets = t;
            s.sendMessage(P + name + " 扭蛋券 → " + t);
            pl.getLogger().info("[admin] " + s.getName() + " give-tickets " + name + " " + n + " → " + t);
            if (n > 0) msg(u, "§a+" + n + " 扭蛋券§7（管理员发放）· 现有 §f" + t);
        });
    }

    /** admin shards (compensation / testing); ledger reason admin_shards */
    void giveShardsAdmin(org.bukkit.command.CommandSender s, UUID u, String name, int n) {
        tx("admin shards " + name, c -> {
            ensureWallet(c, u, name);
            int[] w = lockWallet(c, u);
            int sh = Math.max(0, w[1] + n);
            setWallet(c, u, w[0], sh);
            ledger(c, u, name, 0, sh - w[1], w[0], sh, "admin_shards", s.getName());
            c.commit();
            return sh;
        }, sh -> {
            if (sh == null) { s.sendMessage(P + "§c失败（看日志）"); return; }
            PCache pc = cache.get(u);
            if (pc != null) pc.shards = sh;
            s.sendMessage(P + name + " 光屑 → " + sh);
            pl.getLogger().info("[admin] " + s.getName() + " give-shards " + name + " " + n + " → " + sh);
            if (n > 0) msg(u, "§b+" + n + " 光屑§7（管理员发放）· 现有 §f" + sh);
        });
    }

    void inspect(org.bukkit.command.CommandSender s, UUID u, String name) {
        tx("inspect " + name, c -> {
            List<String> out = new ArrayList<String>();
            try (PreparedStatement ps = c.prepareStatement("SELECT tickets, shards, welcomed FROM gacha_wallet WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { out.add(rs.next() ? "钱包：券 " + rs.getInt(1) + " · 光屑 " + rs.getInt(2) + " · 见面礼 " + (rs.getInt(3) > 0 ? "已领" : "未领") : "没有钱包（没进过扭蛋）"); }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT pity_group, pity5, pity4 FROM gacha_pity WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add("保底 " + rs.getString(1) + "：距上次传说 " + rs.getInt(2) + " 抽 · 距上次史诗+ " + rs.getInt(3) + " 抽"); }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT banner, spark, pulls, last_legend, settled FROM gacha_banner_state WHERE uuid=?")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add("池 " + rs.getString(1) + "：火花 " + rs.getInt(2) + " · 累计 " + rs.getInt(3) + " 抽 · 上次传说 " + rs.getString(4) + (rs.getInt(5) > 0 ? " · 已结算" : "")); }
            }
            out.add("拥有（扭蛋记录）：" + ownedRows(c, u).size() + " 件");
            try (PreparedStatement ps = c.prepareStatement("SELECT pull_id, banner, tier, item, dup, pity5_before, pity5_after, rule, created_at FROM gacha_pull WHERE uuid=? ORDER BY pull_id DESC LIMIT 5")) {
                ps.setString(1, u.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add("#" + rs.getLong(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + (rs.getInt(5) > 0 ? " (重复)" : "")
                            + " 保底 " + rs.getInt(6) + "→" + rs.getInt(7) + " " + rs.getString(8) + " " + rs.getTimestamp(9));
                }
            }
            c.rollback();
            return out;
        }, out -> {
            if (out == null) { s.sendMessage(P + "§c失败（看日志）"); return; }
            s.sendMessage(P + "§e" + name + " " + u);
            for (String l : out) s.sendMessage(P + l);
        });
    }

    RateMath rates() { return rates; }
}
