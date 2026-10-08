package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * B2.170: admin audit of online players' P1 items (inventory/armor/offhand/ender chest/cursor) against cr_p1_item,
 * and a guarded restore of an item whose DB row is active but no copy is held anywhere online (B2.168 loss).
 * D321 H7: {@code p1-six/<uuid>.yml} 待领 is held too (any stash key: migration originals, {@code r}+uid re-entries, …);
 * an active uid only in 待领 is reported as {@code ACTIVE_IN_STASH} (never {@code ACTIVE_NOT_HELD}), and
 * {@code audit restore} refuses to mint a second copy.
 * {@code /corerpg p1 audit [player]} and {@code /corerpg p1 audit restore <player> <uid-prefix>}.
 */
public final class EmberAudit {
    private static final String P = ChatColor.GOLD + "[P1 audit] " + ChatColor.GRAY;
    public static final String REPLACEMENT_LORE = ChatColor.DARK_GRAY + "bug 补发（B2.168 耐久丢失）";
    /** OP-facing: the active row is owed in 六槽待领 — do not audit restore */
    public static final String IN_STASH_TEXT = "在六槽待领，勿 audit restore（让玩家去 装备→护甲 领取）";
    public static final String RESTORE_IN_STASH = "该 uid 在六槽待领里，勿补发；让玩家去 装备→护甲 领取";

    private final EmberLoadoutService loadouts;

    public EmberAudit(EmberLoadoutService loadouts) { this.loadouts = loadouts; }

    /**
     * Pure diff. {@code held}: uid → copies in the player's live containers (inventory / ender / …);
     * {@code stash}: P1 uids sitting in {@code p1-six} 待领 (presence only — never counted toward DUPLICATE);
     * {@code rows}: uid → DB state. Output lines in a stable order.
     */
    public static List<String> diff(Map<String, Integer> held, Set<String> stash, Map<String, String> rows) {
        if (stash == null) stash = Collections.emptySet();
        List<String> out = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : new TreeMap<String, Integer>(held).entrySet()) {
            String st = rows.get(e.getKey());
            if (st == null) out.add("NO_ROW " + e.getKey());
            else if (!"active".equals(st)) out.add("HELD_NOT_ACTIVE " + e.getKey() + " state=" + st);
            if (e.getValue() > 1) out.add("DUPLICATE " + e.getKey() + " x" + e.getValue());
        }
        for (Map.Entry<String, String> e : new TreeMap<String, String>(rows).entrySet()) {
            if (!"active".equals(e.getValue()) || held.containsKey(e.getKey())) continue;
            if (stash.contains(e.getKey())) out.add("ACTIVE_IN_STASH " + e.getKey() + " · " + IN_STASH_TEXT);
            else out.add("ACTIVE_NOT_HELD " + e.getKey());
        }
        return out;
    }

    /** backward-compatible: no 待领 set → same as an empty stash */
    public static List<String> diff(Map<String, Integer> held, Map<String, String> rows) {
        return diff(held, Collections.<String>emptySet(), rows);
    }

    /**
     * whether {@code audit restore} may mint a copy. Stash wins over the generic online-holder check so the OP sees
     * the 待领 wording (D321 H7).
     * @return null = may restore; otherwise the OP-facing refusal line (no color codes)
     */
    public static String restoreBlockReason(boolean inTargetStash, boolean heldAnywhereOnline) {
        if (inTargetStash) return RESTORE_IN_STASH;
        if (heldAnywhereOnline) return "该 uid 仍有在线副本，不补发（防复制）";
        return null;
    }

    /** uid → copies held by one player, across every live container the player carries (not 待领). */
    public Map<String, Integer> heldLive(Player p) {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        List<ItemStack> all = new ArrayList<ItemStack>();
        for (ItemStack it : p.getInventory().getContents()) all.add(it); // 1.12 includes armor + offhand
        for (ItemStack it : p.getEnderChest().getContents()) all.add(it);
        all.add(p.getItemOnCursor());
        if (p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null
                && p.getOpenInventory().getTopInventory().getType() == org.bukkit.event.inventory.InventoryType.CRAFTING) {
            for (ItemStack it : p.getOpenInventory().getTopInventory().getContents()) all.add(it);
        }
        for (ItemStack it : all) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || r.data == null || r.data.uid == null) continue;
            Integer c = m.get(r.data.uid);
            m.put(r.data.uid, c == null ? 1 : c + 1);
        }
        return m;
    }

    /** live containers + 待领 presence (D321 H7). Prefer {@link #heldLive} + {@link #stashUids} when the two must stay apart. */
    public Map<String, Integer> held(Player p) {
        Map<String, Integer> m = heldLive(p);
        for (String uid : stashUids(p.getUniqueId())) if (!m.containsKey(uid)) m.put(uid, 1);
        return m;
    }

    /** P1 uids in {@code plugins/CoreRpg/p1-six/<uuid>.yml} 待领 (any stash key). Empty when the six-slot service is down. */
    public Set<String> stashUids(java.util.UUID id) {
        EmberSixSlotService six = EmberSixSlotService.get();
        return six == null ? Collections.<String>emptySet() : six.stashP1Uids(id);
    }


    public boolean cmd(final CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length >= 3 && "restore".equalsIgnoreCase(args[2])) return restore(s, args);
        List<Player> targets = new ArrayList<Player>();
        if (args.length >= 3) {
            Player p = Bukkit.getPlayerExact(args[2]);
            if (p == null) { s.sendMessage(P + "玩家不在线: " + args[2]); return true; }
            targets.add(p);
        } else targets.addAll(Bukkit.getOnlinePlayers());
        if (targets.isEmpty()) { s.sendMessage(P + "无在线玩家"); return true; }
        for (final Player p : targets) {
            final Map<String, Integer> live = heldLive(p);
            final Set<String> stash = stashUids(p.getUniqueId());
            loadouts.store().loadOwnerItems(p.getUniqueId(), rows -> {
                Map<String, String> st = new LinkedHashMap<String, String>();
                for (Map.Entry<String, EmberItemStore.Row> e : rows.entrySet()) st.put(e.getKey(), e.getValue().state);
                List<String> d = diff(live, stash, st);
                int active = 0;
                for (String v : st.values()) if ("active".equals(v)) active++;
                int heldN = live.size();
                for (String u : stash) if (!live.containsKey(u)) heldN++;
                String head = p.getName() + ": held " + heldN + " (live " + live.size() + " · 待领 " + stash.size() + ") · rows "
                        + rows.size() + " (active " + active + ") · findings " + d.size();
                s.sendMessage(P + (d.isEmpty() ? ChatColor.GREEN : ChatColor.YELLOW) + head);
                Bukkit.getLogger().info("[P1 audit] " + head);
                for (final String line : d) {
                    final String uid = line.split(" ")[1];
                    loadouts.store().lookupFull(uid, fr -> {
                        String label = fr == null ? "(无 DB 行)" : fr.data.shortLabel();
                        s.sendMessage(P + "  " + line + " · " + label);
                        Bukkit.getLogger().info("[P1 audit]   " + p.getName() + " " + line + " " + label);
                    });
                }
            });
        }
        return true;
    }

    private boolean restore(final CommandSender s, String[] args) {
        if (args.length < 5) { s.sendMessage(P + "/corerpg p1 audit restore <player> <uid-prefix>"); return true; }
        final Player p = Bukkit.getPlayerExact(args[3]);
        if (p == null) { s.sendMessage(P + "玩家不在线: " + args[3]); return true; }
        final String prefix = args[4].toLowerCase(Locale.ROOT);
        if (prefix.length() < 8) { s.sendMessage(P + "uid 前缀至少 8 位"); return true; }
        loadouts.store().loadOwnerItems(p.getUniqueId(), rows -> {
            List<String> hit = new ArrayList<String>();
            for (String uid : rows.keySet()) if (uid.startsWith(prefix)) hit.add(uid);
            if (hit.size() != 1) { s.sendMessage(P + ChatColor.RED + "前缀匹配 " + hit.size() + " 行（需恰好 1 行，且属于该玩家）"); return; }
            final String uid = hit.get(0);
            loadouts.store().lookupFull(uid, fr -> {
                if (fr == null || !p.getUniqueId().toString().equals(fr.owner)) { s.sendMessage(P + ChatColor.RED + "DB 行不存在或不属于该玩家"); return; }
                if (!"active".equals(fr.state)) { s.sendMessage(P + ChatColor.RED + "DB 状态 " + fr.state + "，不补发"); return; }
                if (!p.isOnline()) { s.sendMessage(P + ChatColor.RED + "玩家已离线"); return; }
                boolean inStash = stashUids(p.getUniqueId()).contains(uid);
                boolean liveAnywhere = false;
                for (Player o : Bukkit.getOnlinePlayers()) if (heldLive(o).containsKey(uid)) { liveAnywhere = true; break; }
                String block = restoreBlockReason(inStash, liveAnywhere);
                if (block != null) { s.sendMessage(P + ChatColor.RED + block); return; }
                if (p.getInventory().firstEmpty() < 0) { s.sendMessage(P + ChatColor.RED + "背包已满"); return; }
                ItemStack it = loadouts.items().create(fr.data);
                if (it == null) { s.sendMessage(P + ChatColor.RED + "物品生成失败（NI 模板/签名）"); return; }
                ItemMeta meta = it.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
                    lore.add(REPLACEMENT_LORE);
                    meta.setLore(lore);
                    it.setItemMeta(meta);
                }
                p.getInventory().addItem(it);
                p.sendMessage(P + ChatColor.GREEN + "补发 " + fr.data.shortLabel() + "（B2.168 耐久丢失 bug 补偿，同 uid，DB 行未改）");
                s.sendMessage(P + ChatColor.GREEN + "已补发 " + p.getName() + " " + fr.data.shortLabel() + " uid " + uid);
                Bukkit.getLogger().info("[P1 audit] restore " + p.getName() + " " + uid + " " + fr.data.shortLabel() + " rev " + fr.data.rev
                        + " by " + s.getName() + " (bug replacement B2.168)");
            });
        });
        return true;
    }
}
