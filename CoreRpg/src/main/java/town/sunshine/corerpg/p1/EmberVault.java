package town.sunshine.corerpg.p1;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 1.62 P1 material warehouse (材料仓). Reuses the legacy WarehouseService data — PlayerData warehouse slots, mirrored in
 * MySQL {@code cr_warehouse} — but in P1 there are no slot limits and no paid unlocks: one entry per NI id, up to
 * {@link EmberStorageRules#VAULT_CAP} each. Whitelisted materials / currency items (ember-v1.yml
 * {@code storage.vault.whitelist}) go straight in on pickup and on settlement while the player's 自动入库 is on
 * (default on), with an actionbar notice. Through {@link NiBridge.ExtraSource} every P1 cost check and payment sees
 * backpack + warehouse as one pool, so nothing has to be withdrawn to forge. Potions are never accepted.
 */
public final class EmberVault implements Listener, NiBridge.ExtraSource {

    static final String C_AUTO_OFF = "p5_vault_off"; // period all: 1 = auto-pickup off (absent = on)
    public static final List<String> DEFAULT_WHITELIST = Arrays.asList(
            EmberUpgradeRules.MAT_SHARD, EmberUpgradeRules.MAT_CORE, EmberUpgradeRules.MAT_BONE, EmberUpgradeRules.MAT_BLANK,
            "ember_fest_coin_gq26");

    private static volatile EmberVault instance;
    public static EmberVault get() { return instance; }

    private final CoreRpgPlugin plugin;
    private final Set<UUID> flushQueued = new HashSet<UUID>();
    /** actionbar batching: uid → id → amount picked up in the last second */
    private final Map<UUID, Map<String, Long>> recent = new HashMap<UUID, Map<String, Long>>();

    public EmberVault(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        plugin.getNiBridge().setExtraSource(this);
    }

    private NiBridge ni() { return plugin.getNiBridge(); }

    public boolean enabled() {
        EmberMode m = EmberMode.get();
        return EmberMode.active() && (m == null || m.b("storage.vault.enabled", true));
    }

    public Set<String> whitelist() {
        EmberMode m = EmberMode.get();
        List<String> l = m == null ? null : m.config().getStringList("storage.vault.whitelist");
        return new LinkedHashSet<String>(l == null || l.isEmpty() ? DEFAULT_WHITELIST : l);
    }

    public boolean accepts(String niId) { return niId != null && whitelist().contains(niId); }

    private PlayerData data(UUID id) {
        try { return plugin.getDataStore().get(id); } catch (RuntimeException e) { return null; }
    }

    public boolean autoOn(Player p) {
        EmberMode m = EmberMode.get();
        if (m != null && !m.b("storage.vault.auto_pickup", true)) return false;
        PlayerData d = data(p.getUniqueId());
        return d != null && d.periodCount(C_AUTO_OFF, "all") <= 0;
    }

    public boolean toggleAuto(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) return false;
        if (d.periodCount(C_AUTO_OFF, "all") > 0) d.addPeriodCount(C_AUTO_OFF, "all", -d.periodCount(C_AUTO_OFF, "all"));
        else d.addPeriodCount(C_AUTO_OFF, "all", 1);
        flushSoon(p.getUniqueId());
        return autoOn(p);
    }

    // ------------------------------------------------------------------ amounts

    public long amount(UUID id, String niId) {
        PlayerData d = data(id);
        if (d == null || niId == null) return 0;
        try {
            for (PlayerData.WarehouseSlot s : new ArrayList<PlayerData.WarehouseSlot>(d.getWarehouseSlots()))
                if (s != null && niId.equals(s.niId)) return Math.max(0, s.amount);
        } catch (RuntimeException ignored) { /* placeholder thread read during a main-thread write */ }
        return 0;
    }

    /** id → amount, whitelisted ids first in whitelist order, then anything else left from the legacy warehouse */
    public Map<String, Long> all(UUID id) {
        Map<String, Long> out = new java.util.LinkedHashMap<String, Long>();
        PlayerData d = data(id);
        if (d == null) return out;
        List<PlayerData.WarehouseSlot> sl = new ArrayList<PlayerData.WarehouseSlot>(d.getWarehouseSlots());
        for (String w : whitelist()) for (PlayerData.WarehouseSlot s : sl) if (w.equals(s.niId) && s.amount > 0) out.put(w, s.amount);
        for (PlayerData.WarehouseSlot s : sl) if (s.amount > 0 && !out.containsKey(s.niId)) out.put(s.niId, s.amount);
        return out;
    }

    /** main thread: adds up to the cap; returns added */
    public long add(Player p, String niId, long n) {
        PlayerData d = data(p.getUniqueId());
        if (d == null || n <= 0) return 0;
        List<PlayerData.WarehouseSlot> sl = new ArrayList<PlayerData.WarehouseSlot>(d.getWarehouseSlots());
        PlayerData.WarehouseSlot hit = null;
        for (PlayerData.WarehouseSlot s : sl) if (niId.equals(s.niId)) { hit = s; break; }
        long put = EmberStorageRules.fits(hit == null ? 0 : hit.amount, n);
        if (put <= 0) return 0;
        if (hit == null) sl.add(new PlayerData.WarehouseSlot(niId, put)); else hit.amount += put;
        d.setWarehouseSlots(sl);
        flushSoon(p.getUniqueId());
        return put;
    }

    /** main thread: removes up to n; returns removed */
    public long takeFrom(Player p, String niId, long n) {
        PlayerData d = data(p.getUniqueId());
        if (d == null || n <= 0) return 0;
        List<PlayerData.WarehouseSlot> sl = new ArrayList<PlayerData.WarehouseSlot>(d.getWarehouseSlots());
        for (PlayerData.WarehouseSlot s : sl) {
            if (!niId.equals(s.niId)) continue;
            long t = Math.min(n, s.amount);
            s.amount -= t;
            d.setWarehouseSlots(sl); // drops empty entries
            flushSoon(p.getUniqueId());
            return t;
        }
        return 0;
    }

    /** the warehouse write reaches MySQL within ~2 s (one save per burst of pickups) */
    private void flushSoon(final UUID id) {
        if (!flushQueued.add(id)) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            flushQueued.remove(id);
            plugin.getDataStore().flushMutation(id);
        }, 40L);
    }

    // ------------------------------------------------------------------ NiBridge.ExtraSource (P1 only)

    @Override public long count(Player player, String niId) {
        if (player == null || !enabled() || !accepts(niId)) return 0;
        return amount(player.getUniqueId(), niId);
    }

    @Override public long take(Player player, String niId, long amount) {
        if (player == null || !enabled() || !accepts(niId) || !Bukkit.isPrimaryThread()) return 0;
        return takeFrom(player, niId, amount);
    }

    // ------------------------------------------------------------------ deposits

    /** settlement / dismantle path: true when the materials went into the warehouse (auto on + whitelisted) */
    public boolean autoDeposit(Player p, String niId, int n) {
        if (p == null || n <= 0 || !enabled() || !accepts(niId) || !autoOn(p)) return false;
        long put = add(p, niId, n);
        if (put < n) { if (put > 0) takeFrom(p, niId, put); return false; } // at the cap: leave it to the caller
        notice(p, niId, put);
        return true;
    }

    /** warehouse when auto is on, else the backpack (overflow at the feet, as before) */
    public void give(Player p, String niId, int n) {
        if (!autoDeposit(p, niId, n)) ni().giveNiItem(p, niId, n);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!enabled() || !autoOn(p)) return;
        Item ent = e.getItem();
        ItemStack st = ent.getItemStack();
        String id = ni().getNiId(st);
        if (id == null || !accepts(id)) return;
        long put = add(p, id, st.getAmount());
        if (put <= 0) return; // warehouse full for this id: normal pickup
        e.setCancelled(true);
        if (put >= st.getAmount()) ent.remove();
        else { st.setAmount((int) (st.getAmount() - put)); ent.setItemStack(st); }
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.4f, 1.6f);
        notice(p, id, put);
    }

    /** batched actionbar line: 「+24 碎片 → 仓库（共 312）」 */
    private void notice(final Player p, String niId, long n) {
        final UUID id = p.getUniqueId();
        Map<String, Long> m = recent.get(id);
        boolean first = m == null;
        if (m == null) { m = new java.util.LinkedHashMap<String, Long>(); recent.put(id, m); }
        m.merge(niId, n, Long::sum);
        if (!first) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Map<String, Long> got = recent.remove(id);
            Player q = Bukkit.getPlayer(id);
            if (got == null || q == null) return;
            List<String> parts = new ArrayList<String>();
            for (Map.Entry<String, Long> x : got.entrySet())
                parts.add("§a+" + x.getValue() + " §f" + ni().displayName(x.getKey()) + " §7（共 " + amount(id, x.getKey()) + "）");
            String msg = "§3[仓库] " + String.join(" §8· ", parts) + " §8自动入库";
            try { q.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg)); } catch (Throwable ignored) { }
        }, 10L);
    }

    /**
     * Every whitelisted stack in the backpack + hotbar (not armor / off hand) → warehouse. Returns id → amount moved.
     * Works anywhere (putting materials away never helps a fight).
     */
    public Map<String, Long> depositAll(Player p) {
        Map<String, Long> moved = new java.util.LinkedHashMap<String, Long>();
        if (!enabled()) return moved;
        ItemStack[] st = p.getInventory().getStorageContents();
        for (int i = 0; i < st.length; i++) {
            ItemStack x = st[i];
            if (x == null || x.getType() == Material.AIR) continue;
            String id = ni().getNiId(x);
            if (id == null || !accepts(id)) continue;
            long put = add(p, id, x.getAmount());
            if (put <= 0) continue;
            if (put >= x.getAmount()) p.getInventory().setItem(i, null);
            else { x.setAmount((int) (x.getAmount() - put)); p.getInventory().setItem(i, x); }
            moved.merge(id, put, Long::sum);
        }
        if (!moved.isEmpty()) p.updateInventory();
        return moved;
    }

    /**
     * Withdraw up to {@code want} (capped by the free room of the backpack so nothing spills on the ground).
     * Returns the amount handed out, or −1 when the backpack has no room.
     */
    public long withdraw(Player p, String niId, long want) {
        long have = amount(p.getUniqueId(), niId);
        if (have <= 0 || want <= 0) return 0;
        ItemStack proto = ni().createNiItem(niId);
        int max = proto == null ? 64 : Math.max(1, proto.getMaxStackSize());
        long room = 0;
        for (ItemStack x : p.getInventory().getStorageContents()) {
            if (x == null || x.getType() == Material.AIR) room += max;
            else if (niId.equals(ni().getNiId(x))) room += Math.max(0, max - x.getAmount());
        }
        if (room <= 0) return -1;
        long n = Math.min(Math.min(want, have), Math.min(room, Integer.MAX_VALUE));
        long took = takeFrom(p, niId, n);
        if (took <= 0) return 0;
        if (!ni().giveNiItem(p, niId, (int) took)) { add(p, niId, took); return 0; }
        return took;
    }
}
