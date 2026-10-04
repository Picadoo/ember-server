package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.Delivery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * D162 (review A01): applies the player's pending cr_p1_delivery rows — on the transaction callback when the player is
 * online, else at the next join — exactly once, never re-rolled (the owed amount / piece was fixed when the row was
 * written in the same DB transaction as the state change).
 * <p>
 * Exactly once:
 * <ul>
 *   <li>{@code mat}: the warehouse change and the marker counter {@code p1dlv_<id>} go into the same PlayerData save (player
 *   row + warehouse row are ONE MySQL transaction since D162). Row acked only after that save returned true; a marker
 *   already present means "applied, ack missing" → ack only. Marker dropped after the ack.</li>
 *   <li>{@code gear}: the stack carries the uid; a stack with that uid already in the backpack / ender chest means
 *   "delivered, ack missing" → ack only. Else the DB row must be active and ours (otherwise void with the reason), the
 *   stack is created from the DB row and the .dat saved before the ack.</li>
 * </ul>
 * Rows that cannot be applied now (backpack full, warehouse at the cap, save failed, assets frozen) stay pending with
 * attempts + reason and are retried on the next kick (join, next transaction, /corerpg p1 deliver, 30 s retry while online).
 */
public final class EmberDelivery {
    private static final String P = ChatColor.GOLD + "[到账] " + ChatColor.GRAY;
    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final Set<UUID> inflight = new HashSet<UUID>(), again = new HashSet<UUID>(), retryQueued = new HashSet<UUID>();

    public EmberDelivery(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
    }

    private EmberItemStore store() { return loadouts.store(); }

    static String marker(long id) { return "p1dlv_" + id; }

    /** D162: set in the same save as a forge cost deduction; a reconciled refund without it was never really paid */
    public static String paidMarker(String rid) { return "p1paid_" + Integer.toHexString(rid.hashCode()); }

    private static final long START = System.currentTimeMillis();

    /** join: settle refund holds left by a crash (made before this server start, or older than 5 min), then deliver */
    public void onJoin(final Player p) {
        if (p == null || !store().usable()) return;
        long olderThan = Math.max(START, System.currentTimeMillis() - 300000L);
        store().reconcileHolds(p.getUniqueId(), olderThan, () -> kick(p));
    }

    private final java.util.Map<UUID, List<Runnable>> afters = new java.util.HashMap<UUID, List<Runnable>>();

    /** same as {@link #kick(Player)}; {@code after} runs on the main thread once this round is through */
    public void kick(final Player p, Runnable after) {
        if (p != null && after != null) afters.computeIfAbsent(p.getUniqueId(), k -> new java.util.ArrayList<Runnable>()).add(after);
        kick(p);
    }

    /** main thread: deliver whatever is pending for {@code p} (no-op while frozen / paused → retried later) */
    public void kick(final Player p) {
        if (p == null || !p.isOnline() || !store().usable()) { if (p != null) runAfters(p.getUniqueId()); return; }
        final UUID id = p.getUniqueId();
        if (EmberAssetGuard.frozen(id) || EmberAssetGuard.paused()) { retryLater(p); runAfters(id); return; }
        if (!inflight.add(id)) { again.add(id); return; }
        store().pendingDeliveries(id, rows -> {
            if (rows == null) { inflight.remove(id); retryLater(p); runAfters(id); return; }
            step(p, rows, 0, new int[1]);
        });
    }

    private void retryLater(final Player p) {
        final UUID id = p.getUniqueId();
        if (!retryQueued.add(id)) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> { retryQueued.remove(id); Player q = Bukkit.getPlayer(id); if (q != null) kick(q); }, 600L);
    }

    private void finish(Player p, int[] left) {
        UUID id = p.getUniqueId();
        inflight.remove(id);
        if (again.remove(id)) { kick(p); return; }
        if (left[0] > 0) retryLater(p);
        runAfters(id);
    }

    private void runAfters(UUID id) {
        List<Runnable> l = afters.remove(id);
        if (l != null) for (Runnable r : l) try { r.run(); } catch (RuntimeException e) { plugin.getLogger().warning("[P1 delivery] after: " + e); }
    }

    private void step(final Player p, final List<Delivery> rows, final int i, final int[] left) {
        if (i >= rows.size() || !p.isOnline()) { finish(p, left); return; }
        final Delivery d = rows.get(i);
        final Runnable next = () -> step(p, rows, i + 1, left);
        if (EmberAssetGuard.frozen(p.getUniqueId()) || EmberAssetGuard.paused()) { left[0]++; next.run(); return; }
        if ("mat".equals(d.kind) || "coin".equals(d.kind)) { mat(p, d, left, next); return; }
        if ("gear".equals(d.kind)) { gear(p, d, left, next); return; }
        store().ackDelivery(d.id, "void", "unknown kind " + d.kind, ok -> next.run());
    }

    // ------------------------------------------------------------------ materials

    private void mat(final Player p, final Delivery d, final int[] left, final Runnable next) {
        final UUID id = p.getUniqueId();
        PlayerData pd;
        try { pd = plugin.getDataStore().get(id); } catch (RuntimeException e) { pd = null; }
        if (pd == null || pd.isLoadFailed()) { keep(d, "player data not loaded", left, next); return; }
        String mk = marker(d.id);
        boolean applied = pd.periodCount(mk, "1") > 0;
        final String paid = d.request.startsWith("refund:") ? paidMarker(d.request.substring(7)) : null;
        if (!applied && paid != null && pd.periodCount(paid, "1") <= 0) { // crash before the deduction reached the save
            store().ackDelivery(d.id, "void", "cost never saved", ok -> next.run());
            plugin.getLogger().warning("[P1 delivery] " + p.getName() + " refund " + d.request + " void: the cost was never saved");
            return;
        }
        String name = "coin".equals(d.kind) ? "余烬币" : plugin.getNiBridge().displayName(d.item);
        if (!applied && "coin".equals(d.kind)) {
            if (d.amount > 0) pd.addCoin((int) d.amount);
            else if (d.amount < 0 && !pd.takeCoin((int) -d.amount)) plugin.getLogger().warning("[P1 delivery] " + p.getName() + " coin debit " + d.request + " short");
            tell(p, (d.amount > 0 ? ChatColor.GREEN + "退回 " : "扣回 ") + name + " ×" + Math.abs(d.amount) + ChatColor.GRAY + "（" + why(d) + "）");
            pd.addPeriodCount(mk, "1", 1);
        } else if (!applied) {
            EmberVault v = EmberVault.get();
            if (d.amount > 0) {
                if (v == null || !v.credit(p, d.item, d.amount)) { keep(d, "warehouse full / disabled", left, next); tell(p, "仓库放不下 " + name + " ×" + d.amount + "，腾出空间后 /corerpg p1 deliver 领取"); return; }
                tell(p, ChatColor.GREEN + "到账 " + name + " ×" + d.amount + " → 仓库" + ChatColor.GRAY + "（" + why(d) + "）");
            } else if (d.amount < 0) {
                long want = -d.amount, got = v != null ? v.debit(p, d.item, want) : plugin.getNiBridge().consume(p, d.item, (int) want);
                if (got < want) plugin.getLogger().warning("[P1 delivery] " + p.getName() + " debit " + d.request + " short: " + got + "/" + want + " " + d.item);
                tell(p, "扣回 " + name + " ×" + got + "（" + why(d) + "）");
            }
            pd.addPeriodCount(mk, "1", 1);
        }
        // the warehouse change and the marker reach MySQL together (one save, one DB transaction) — or neither does
        boolean saved = plugin.getDataStore().save(id, pd);
        EmberVault.savePlayerFile(p);
        if (!saved) { keep(d, "player save failed", left, next); return; }
        ack(p, d, () -> {
            PlayerData x = pd2(id);
            if (x == null) return;
            x.addPeriodCount(mk, "1", -1);
            if (paid != null && x.periodCount(paid, "1") > 0) x.addPeriodCount(paid, "1", -1); // one count per refunded line
        }, next);
    }

    private PlayerData pd2(UUID id) { return plugin.getDataStore().get(id); }

    // ------------------------------------------------------------------ gear

    private void gear(final Player p, final Delivery d, final int[] left, final Runnable next) {
        final UUID id = p.getUniqueId();
        final String mk = marker(d.id);
        final Runnable unmark = () -> { PlayerData x = pd2(id); if (x != null) x.addPeriodCount(mk, "1", -1); };
        PlayerData pd0 = pd2(id);
        // delivered before, ack missing: the marker (saved right after the .dat) or the stack itself says so
        if ((pd0 != null && pd0.periodCount(mk, "1") > 0) || holds(p, d.item)) { ack(p, d, unmark, next); return; }
        store().lookupFull(d.item, row -> {
            if (!p.isOnline()) { left[0]++; next.run(); return; }
            if (row == null) { keep(d, "item row not readable", left, next); return; }
            if (!p.getUniqueId().toString().equals(row.owner) || !"active".equals(row.state)) {
                store().ackDelivery(d.id, "void", "row " + row.state + " owner " + row.owner, ok -> next.run());
                plugin.getLogger().warning("[P1 delivery] " + p.getName() + " gear " + d.item + " void: row " + row.state);
                return;
            }
            if (holds(p, d.item)) { ack(p, d, unmark, next); return; }
            if (p.getInventory().firstEmpty() < 0) { keep(d, "backpack full", left, next); tell(p, ChatColor.RED + "背包满了，" + row.data.shortLabel() + " 还没到账：腾出 1 格后 /corerpg p1 deliver（或下次进服自动到账）"); return; }
            ItemStack st = loadouts.items().create(row.data);
            if (st == null) { keep(d, "stack create failed", left, next); return; }
            loadouts.remember(row.data, p.getUniqueId());
            p.getInventory().addItem(st);
            EmberVault.savePlayerFile(p);
            PlayerData pd = pd2(id);
            if (pd != null) { pd.addPeriodCount(mk, "1", 1); plugin.getDataStore().save(id, pd); }
            tell(p, ChatColor.GREEN + "到账 " + row.data.shortLabel() + " → 背包" + ChatColor.GRAY + "（" + why(d) + "）");
            ack(p, d, unmark, next);
        });
    }

    private boolean holds(Player p, String uid) {
        EmberItems items = loadouts.items();
        for (ItemStack[] arr : new ItemStack[][]{p.getInventory().getContents(), p.getEnderChest().getContents()})
            for (ItemStack s : arr) {
                if (s == null || !items.hasData(s)) continue;
                EmberItems.Read r = items.read(s);
                if (r != null && r.data != null && uid.equals(r.data.uid)) return true;
            }
        return false;
    }

    // ------------------------------------------------------------------ ack / keep

    private void ack(final Player p, final Delivery d, final Runnable onAck, final Runnable next) {
        if (EmberFaults.fire(p.getUniqueId(), EmberFaults.AFTER_DELIVER)) {
            plugin.getLogger().warning("[P1 delivery] TEST FAULT after_deliver: " + d.id + " " + d.request + " applied, ack skipped");
            next.run();
            return;
        }
        store().ackDelivery(d.id, "delivered", d.attempts > 0 ? "after " + d.attempts + " retries" : null, ok -> {
            if (ok && onAck != null) onAck.run();
            if (ok) plugin.getLogger().info("[P1 delivery] " + p.getName() + " delivered #" + d.id + " " + d.request + "/" + d.idx + " " + d.kind + " " + d.item + " " + d.amount);
            next.run();
        });
    }

    private void keep(Delivery d, String reason, int[] left, Runnable next) {
        left[0]++;
        store().ackDelivery(d.id, null, reason, ok -> next.run());
    }

    private static String why(Delivery d) { return d.note == null ? d.request : d.note; }

    private static void tell(Player p, String m) { if (p != null && p.isOnline()) p.sendMessage(P + m); }
}
