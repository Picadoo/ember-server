package town.sunshine.coregacha;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * CoreRpg integration without touching CoreRpg source: CoreRpg has no console command that grants a shop cosmetic,
 * so this calls its public Java API by reflection — {@code CoreRpgPlugin#getDataStore().get(uuid)} → PlayerData
 * counters — exactly what its own cosmetic shop writes on a purchase ({@code p2_cosbuy_<id>@all = 1}), then
 * {@code flushMutation}. Coins ({@code getCoin/takeCoin/addCoin}) and 余烬徽 ({@code p3_badge@all}) the same way.
 * MAIN THREAD ONLY (CoreRpg mutates PlayerData on the main thread). Any reflection failure = "not available" → the
 * caller rolls back / refunds.
 */
public final class CoreRpgBridge {
    static final String C_BOUGHT = "p2_cosbuy_";
    static final String C_BADGE = "p3_badge";
    static final String C_BOUNTY = "p2_bounty";

    private final Logger log;
    private Object store;
    private Method get, flush, getCoin, takeCoin, addCoin, periodCount, addPeriodCount, today, byId, shop;

    public CoreRpgBridge(Logger log) { this.log = log; }

    public boolean init() {
        try {
            Plugin p = Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (p == null || !p.isEnabled()) { log.warning("[bridge] CoreRpg not enabled — CoreRpg cosmetics / coin exchange off"); return false; }
            store = p.getClass().getMethod("getDataStore").invoke(p);
            get = store.getClass().getMethod("get", UUID.class);
            flush = store.getClass().getMethod("flushMutation", UUID.class);
            ClassLoader cl = p.getClass().getClassLoader();
            Class<?> pd = Class.forName("town.sunshine.corerpg.PlayerData", true, cl);
            getCoin = pd.getMethod("getCoin");
            takeCoin = pd.getMethod("takeCoin", int.class);
            addCoin = pd.getMethod("addCoin", int.class);
            periodCount = pd.getMethod("periodCount", String.class, String.class);
            addPeriodCount = pd.getMethod("addPeriodCount", String.class, String.class, int.class);
            today = Class.forName("town.sunshine.corerpg.DailyService", true, cl).getMethod("today");
            Class<?> cos = Class.forName("town.sunshine.corerpg.p1.EmberCosmetics", true, cl);
            byId = cos.getMethod("byId", String.class);
            shop = Class.forName("town.sunshine.corerpg.p1.EmberCosmetics$Cosmetic", true, cl).getMethod("shop");
            log.info("[bridge] CoreRpg API ok (data store, coin, 余烬徽, cosmetic shop flags)");
            return true;
        } catch (Throwable t) {
            log.warning("[bridge] CoreRpg API not available: " + t);
            store = null;
            return false;
        }
    }

    public boolean ready() { return store != null; }

    private Object data(UUID u) throws Exception { return get.invoke(store, u); }

    private void flush(UUID u) { try { flush.invoke(store, u); } catch (Throwable t) { log.warning("[bridge] flush " + u + ": " + t); } }

    /** true = this id is a CoreRpg shop cosmetic (coin or mark item) */
    public boolean isShopCosmetic(String id) {
        if (!ready()) return false;
        try { Object c = byId.invoke(null, id); return c != null && Boolean.TRUE.equals(shop.invoke(c)); }
        catch (Throwable t) { return false; }
    }

    public boolean hasCosmetic(UUID u, String id) {
        try { return ready() && (Integer) periodCount.invoke(data(u), C_BOUGHT + id, "all") > 0; }
        catch (Throwable t) { return false; }
    }

    /** grant = the shop's purchase flag; false when not possible or already owned */
    public boolean grantCosmetic(UUID u, String id) {
        if (!ready() || !isShopCosmetic(id)) return false;
        try {
            Object d = data(u);
            if ((Integer) periodCount.invoke(d, C_BOUGHT + id, "all") > 0) return false;
            addPeriodCount.invoke(d, C_BOUGHT + id, "all", 1);
            boolean ok = (Integer) periodCount.invoke(d, C_BOUGHT + id, "all") > 0;
            flush(u);
            return ok;
        } catch (Throwable t) { log.warning("[bridge] grant " + id + " → " + u + ": " + t); return false; }
    }

    /** undo a grant made in a rolled-back transaction */
    public void revokeCosmetic(UUID u, String id) {
        try {
            Object d = data(u);
            int n = (Integer) periodCount.invoke(d, C_BOUGHT + id, "all");
            if (n > 0) addPeriodCount.invoke(d, C_BOUGHT + id, "all", -n);
            flush(u);
        } catch (Throwable t) { log.warning("[bridge] revoke " + id + " → " + u + ": " + t); }
    }

    public int coin(UUID u) {
        try { return ready() ? (Integer) getCoin.invoke(data(u)) : 0; } catch (Throwable t) { return 0; }
    }

    public boolean takeCoin(UUID u, int n) {
        try { boolean ok = ready() && (Boolean) takeCoin.invoke(data(u), n); if (ok) flush(u); return ok; }
        catch (Throwable t) { log.warning("[bridge] takeCoin: " + t); return false; }
    }

    public void addCoin(UUID u, int n) {
        try { addCoin.invoke(data(u), n); flush(u); } catch (Throwable t) { log.severe("[bridge] REFUND addCoin " + n + " → " + u + " failed: " + t); }
    }

    public int badges(UUID u) {
        try { return ready() ? (Integer) periodCount.invoke(data(u), C_BADGE, "all") : 0; } catch (Throwable t) { return 0; }
    }

    public boolean takeBadges(UUID u, int n) {
        try {
            Object d = data(u);
            if ((Integer) periodCount.invoke(d, C_BADGE, "all") < n) return false;
            addPeriodCount.invoke(d, C_BADGE, "all", -n);
            flush(u);
            return true;
        } catch (Throwable t) { log.warning("[bridge] takeBadges: " + t); return false; }
    }

    public void addBadges(UUID u, int n) {
        try { addPeriodCount.invoke(data(u), C_BADGE, "all", n); flush(u); } catch (Throwable t) { log.severe("[bridge] REFUND badges " + n + " → " + u + " failed: " + t); }
    }

    /** today's CoreRpg daily-commission count (settled main-line runs, weighted), read only */
    public int bountyToday(UUID u) {
        try { return ready() ? (Integer) periodCount.invoke(data(u), C_BOUNTY, (String) today.invoke(null)) : 0; } catch (Throwable t) { return 0; }
    }
}
