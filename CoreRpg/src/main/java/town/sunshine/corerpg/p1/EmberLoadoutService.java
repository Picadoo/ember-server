package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves the EquippedLoadout of a player: the P1 blade in the main hand plus the explicitly selected charm
 * uid (anywhere in the player inventory, counted once; the off hand is not a second charm). An item only counts
 * when its NBT is valid and signed, the NI id matches, the uid appears exactly once in the inventory, and — with
 * MySQL — the {@code cr_p1_item} row exists, belongs to this player, is active and has the same rev (G07).
 */
public final class EmberLoadoutService implements Listener {

    private final CoreRpgPlugin plugin;
    private final EmberItems items;
    private final EmberItemStore store;

    private final Map<UUID, EmberLoadout> cache = new ConcurrentHashMap<UUID, EmberLoadout>();
    private final Map<UUID, List<String>> notes = new ConcurrentHashMap<UUID, List<String>>();
    private final Map<UUID, EmberPlayerState> states = new ConcurrentHashMap<UUID, EmberPlayerState>();
    /** DB trust cache: uid → row (main thread only) */
    private final Map<String, EmberItemStore.Row> rows = new HashMap<String, EmberItemStore.Row>();
    private final Map<String, Long> lookupAt = new HashMap<String, Long>();

    public EmberLoadoutService(CoreRpgPlugin plugin, EmberItems items, EmberItemStore store) {
        this.plugin = plugin;
        this.items = items;
        this.store = store;
    }

    public EmberItems items() { return items; }
    public EmberItemStore store() { return store; }

    public EmberPlayerState state(UUID id) {
        EmberPlayerState s = states.get(id);
        if (s == null) {
            s = new EmberPlayerState();
            EmberPlayerState prev = states.putIfAbsent(id, s);
            if (prev != null) s = prev;
        }
        return s;
    }

    public void saveState(Player p) {
        if (store.usable()) store.saveState(p.getUniqueId(), state(p.getUniqueId()));
    }

    /** Cached loadout (re-resolved by {@link #refresh} every second from StatService for P1 players). */
    public EmberLoadout get(Player p) {
        EmberLoadout l = cache.get(p.getUniqueId());
        return l != null ? l : refresh(p);
    }

    public List<String> notes(Player p) {
        List<String> n = notes.get(p.getUniqueId());
        return n == null ? Collections.<String>emptyList() : n;
    }

    public void invalidate(UUID id) { cache.remove(id); }

    public EmberLoadout refresh(Player p) {
        List<String> why = new ArrayList<String>();
        EmberPlayerState st = state(p.getUniqueId());
        PlayerInventory inv = p.getInventory();
        ItemStack[] all = inv.getContents();
        Map<String, Integer> count = new HashMap<String, Integer>();
        Map<String, EmberItems.Read> charmCandidates = new HashMap<String, EmberItems.Read>();
        for (ItemStack it : all) {
            if (it == null || !items.hasData(it)) continue;
            EmberItems.Read r = items.read(it);
            if (r == null || r.data == null || r.data.uid == null) continue;
            Integer c = count.get(r.data.uid);
            count.put(r.data.uid, (c == null ? 0 : c) + Math.max(1, it.getAmount()));
            if (r.ok()) codex(p, r.data);
            if (st.charmUid != null && st.charmUid.equals(r.data.uid)) charmCandidates.put(r.data.uid, r);
        }
        EmberItemData blade = null;
        ItemStack main = inv.getItemInMainHand();
        if (main != null && items.hasData(main)) {
            EmberItems.Read r = items.read(main);
            if (r == null) why.add("主手: 无 ember_v1 数据");
            else if (!r.ok()) why.add("主手: " + r.problem);
            else if (!r.data.isBlade()) why.add("主手: 不是刃 (" + r.data.slot + ")");
            else if (count.get(r.data.uid) != null && count.get(r.data.uid) > 1) why.add("主手: uid 重复出现 " + count.get(r.data.uid) + " 次");
            else {
                String db = dbCheck(p, r.data);
                if (db != null) why.add("主手: " + db);
                else blade = r.data;
            }
        }
        EmberItemData charm = null;
        if (st.charmUid != null) {
            EmberItems.Read r = charmCandidates.get(st.charmUid);
            if (r == null) why.add("护符: 已选 " + st.charmUid.substring(0, 8) + " 不在背包");
            else if (!r.ok()) why.add("护符: " + r.problem);
            else if (!r.data.isCharm()) why.add("护符: 选中的不是护符");
            else if (count.get(r.data.uid) > 1) why.add("护符: uid 重复出现 " + count.get(r.data.uid) + " 次");
            else {
                String db = dbCheck(p, r.data);
                if (db != null) why.add("护符: " + db);
                else charm = r.data;
            }
        }
        int level = 10;
        try {
            PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
            if (pd != null) level = pd.getEmberLevel();
        } catch (Throwable ignored) {}
        EmberLoadout l = EmberLoadout.compute(EmberMode.tables(), blade, charm, level);
        cache.put(p.getUniqueId(), l);
        notes.put(p.getUniqueId(), why);
        String mainUid = blade == null ? null : blade.uid;
        boolean changed = !eq(st.mainhandUid, mainUid) || !eq(st.activeSet, l.activeSet) || st.awakening != l.awakening;
        st.mainhandUid = mainUid;
        st.activeSet = l.activeSet;
        st.awakening = l.awakening;
        if (changed && EmberMode.isP1(p)) saveState(p);
        return l;
    }

    private static boolean eq(String a, String b) { return a == null ? b == null : a.equals(b); }

    /** @return null when the DB agrees (or there is no DB), else the reason. */
    private String dbCheck(Player p, EmberItemData d) {
        if (!store.usable()) return null; // YAML storage: signed NBT only
        EmberItemStore.Row row = rows.get(d.uid);
        if (row == null) {
            Long at = lookupAt.get(d.uid);
            long now = System.currentTimeMillis();
            if (at == null || now - at > 30000L) {
                lookupAt.put(d.uid, now);
                final String uid = d.uid;
                final UUID owner = p.getUniqueId();
                store.lookupItem(uid, r -> {
                    if (r != null) { rows.put(uid, r); cache.remove(owner); } // D93
                });
            }
            return "DB 记录未找到/查询中";
        }
        if (!"active".equals(row.state)) return "DB 状态 " + row.state;
        if (row.owner == null || !row.owner.equals(p.getUniqueId().toString())) return "DB 所有者不是你";
        if (row.rev != d.rev) return "DB rev " + row.rev + " != NBT rev " + d.rev;
        return null;
    }

    /** Called after an admin give / future item mutations so the trust cache matches immediately. */
    public void remember(EmberItemData d, UUID owner) {
        rows.put(d.uid, new EmberItemStore.Row(owner == null ? null : owner.toString(), d.rev, "active"));
        store.upsertItem(d, owner, "active");
    }

    public EmberItemStore.Row cachedRow(String uid) { return rows.get(uid); }

    /** G03 trust check for a mutation: same rules as combat (DB owner/rev/state; YAML = signed NBT only). */
    public String trust(Player p, EmberItemData d) { return dbCheck(p, d); }

    /** After a committed G03 transaction: update the trust cache without another upsert. */
    public void rememberRow(String uid, UUID owner, int rev, String state) {
        rows.put(uid, new EmberItemStore.Row(owner == null ? null : owner.toString(), rev, state));
    }

    /** Explicit charm selection (book §4.1: only the selected charm counts). */
    /**
     * B2.173: the set the selected charm makes with the player's blade, for the {@code charm select} reply. While the
     * charm is in the main hand the live loadout has no blade, so this takes the main-hand blade if there is one, else
     * the blade equipped before ({@code lastBladeUid}, if still in the inventory), else the first P1 blade on the
     * hotbar. Display only: the live loadout still uses the main hand. {@code [0]} = loadout, {@code [1]} = the blade
     * was not in the main hand (Boolean).
     */
    public Object[] previewWithBlade(Player p, String lastBladeUid) {
        EmberLoadout cur = get(p);
        if (cur.blade != null) return new Object[] { cur, Boolean.FALSE };
        PlayerInventory inv = p.getInventory();
        EmberItemData blade = null;
        for (int pass = 0; pass < 2 && blade == null; pass++) {
            ItemStack[] it = pass == 0 ? inv.getStorageContents() : java.util.Arrays.copyOf(inv.getStorageContents(), 9);
            for (ItemStack s : it) {
                if (s == null || !items.hasData(s)) continue;
                EmberItems.Read r = items.read(s);
                if (r == null || !r.ok() || r.data == null || !r.data.isBlade()) continue;
                if (pass == 0 && (lastBladeUid == null || !lastBladeUid.equals(r.data.uid))) continue;
                if (dbCheck(p, r.data) != null) continue;
                blade = r.data;
                break;
            }
        }
        if (blade == null) return new Object[] { cur, Boolean.FALSE };
        int level = 10;
        try {
            PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
            if (pd != null) level = pd.getEmberLevel();
        } catch (Throwable ignored) {}
        return new Object[] { EmberLoadout.compute(EmberMode.tables(), blade, cur.charm, level), Boolean.TRUE };
    }

    public String selectCharm(Player p, ItemStack held) {
        EmberItems.Read r = items.read(held);
        if (r == null) return "手持物品不是 P1 物品";
        if (!r.ok()) return "物品不可信: " + r.problem;
        if (!r.data.isCharm()) return "手持的是 " + EmberItemData.slotName(r.data.slot) + "，不是护符";
        EmberPlayerState st = state(p.getUniqueId());
        st.charmUid = r.data.uid;
        saveState(p);
        refresh(p);
        return null;
    }

    /**
     * Onboarding fix (D85): a charm that arrives while no valid charm is selected is selected automatically. A new
     * player otherwise fought with half the life (H 20 instead of 40) until he found the select command. The T0
     * starter is also replaced by the first T1+ charm. Choosing between real charms stays manual (book §4.1).
     */
    public boolean autoSelectCharm(Player p, String uid, int newTier) {
        EmberPlayerState st = state(p.getUniqueId());
        EmberLoadout cur = refresh(p);
        if (uid == null) return false;
        if (cur.charm != null && !(cur.charm.tier == 0 && newTier >= 1)) return false; // T0 starter → first real charm
        st.charmUid = uid;
        saveState(p);
        refresh(p);
        return true;
    }

    public void clearCharm(Player p) {
        state(p.getUniqueId()).charmUid = null;
        saveState(p);
        refresh(p);
    }

    // ------------------------------------------------------------------ lifecycle

    /**
     * B2.168: P1 gear is a permanent, DB-tracked asset (book §9 / §18.3「不掉永久装备」): vanilla durability must not
     * wear it out. The T0 starter blade is an IRON_SWORD (250 uses) and broke after ~9 Q01 runs; an admin-given T2+6
     * DIAMOND_SWORD broke the same way during long test sessions, leaving its cr_p1_item row active with no item.
     * Runs in every world and regardless of the P1 switch (the item exists either way).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent e) {
        if (e.getItem() != null && items().hasData(e.getItem())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
        backfillCodex(e.getPlayer());
        if (!EmberMode.active()) return;
        ensureLoaded(e.getPlayer());
    }

    /** Loads persisted state + owned item rows once per server session (join, or when P1 is switched on). */
    public void ensureLoaded(Player p) {
        final UUID id = p.getUniqueId();
        EmberPlayerState st = state(id);
        if (!store.usable()) { st.loaded = true; return; }
        if (st.loaded) return;
        store.ensureSchema();
        store.loadState(id, st, () -> { Player q = Bukkit.getPlayer(id); if (q != null) refresh(q); });
        // D93: the cached loadout computed before the rows arrived said "主手不是有效 P1 刃" until something else refreshed it
        store.loadOwnerItems(id, m -> { rows.putAll(m); cache.remove(id); });
    }

    private final java.util.Set<UUID> codexLoaded = new java.util.HashSet<UUID>();

    /** B2.180: register a verified item in the player's 图录 (first time only) and tell them. */
    private void codex(Player p, EmberItemData d) {
        PlayerData pd;
        try { pd = plugin.getDataStore().get(p.getUniqueId()); } catch (Throwable t) { return; }
        if (pd != null && EmberCodex.register(pd, d) && d.tier > 0) // D99: the two starter pieces register silently
            p.sendMessage("§6[图录] §f登记 T" + d.tier + " " + EmberItemData.familyName(d.tier == 0 ? "none" : d.family)
                    + EmberItemData.slotName(d.slot) + " §7（" + EmberCodex.count(pd) + "/" + EmberCodex.ENTRIES.size() + "，主菜单 图录 → 装备图鉴）");
    }

    /** B2.180: once per server session, register every kind the player ever owned (DB rows), quietly. */
    public void backfillCodex(Player p) {
        final UUID id = p.getUniqueId();
        if (!store.usable() || !codexLoaded.add(id)) return;
        store.ensureSchema();
        store.loadOwnerKinds(id, kinds -> {
            PlayerData pd = plugin.getDataStore().get(id);
            if (pd == null) return;
            for (String[] k : kinds) {
                try { EmberCodex.register(pd, k[0], k[1], Integer.parseInt(k[2]), "drop"); } catch (NumberFormatException ignored) {}
            }
        });
    }

    public void ensureLoadedAll() {
        for (Player p : Bukkit.getOnlinePlayers()) ensureLoaded(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        cache.remove(id);
        notes.remove(id);
        if (!EmberMode.active()) return;
        EmberPlayerState st = state(id);
        if (EmberMode.isP1(p)) {
            st.lastHp = p.getHealth();
            st.lastWorld = p.getWorld().getName();
        } else {
            st.lastHp = Double.NaN;
            st.lastWorld = null;
        }
        saveState(p);
        // state (cooldowns) intentionally stays in memory: reconnect must not reset them (D03 / A12)
    }
}
