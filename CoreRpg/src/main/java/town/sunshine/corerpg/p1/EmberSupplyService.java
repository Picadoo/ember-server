package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B2.169 — book §19.4 supply shop and §3.1 starter supplies.
 * <ul>
 *   <li>{@code /corerpg p1 shop [buy [n]]}: the P1 standard healing potion for {@code shop.heal_potion.price} 余烬币
 *       (book: 10), bound, never sold back (there is no sell-back path at all). Only while P1 is on and outside P1
 *       run worlds (book「城内可正常补满」; buying mid-run would bypass the supply budget).</li>
 *   <li>Starter supplies: {@code starter.heal_potions} bound potions (D28 default 5) with the T0 kit, once.</li>
 * </ul>
 * Bound = NBT {@value #BOUND_KEY} + lore line; a bound item cannot be dropped or listed on the auction house.
 */
public final class EmberSupplyService implements Listener {
    public static final String BOUND_KEY = "ember_bound";
    static final int MAX_PER_BUY = 16;
    private static final String P = ChatColor.GOLD + "[余烬补给] " + ChatColor.GRAY;

    private final CoreRpgPlugin plugin;

    public EmberSupplyService(CoreRpgPlugin plugin) { this.plugin = plugin; }

    private static EmberMode mode() { return EmberMode.get(); }
    public static int price() { return mode() == null ? 10 : Math.max(1, mode().i("shop.heal_potion.price", 10)); }
    public static int starterPotions() { return mode() == null ? 5 : Math.max(0, mode().i("starter.heal_potions", 5)); }
    static String potionId() {
        List<String> l = mode() == null ? null : mode().list("heal_potion.items");
        return l == null || l.isEmpty() ? "potion_ember_heal" : l.get(0);
    }

    /** How many potions a purchase really delivers: capped by the request, MAX_PER_BUY, coins and free slots (potions do not stack). */
    static int affordable(int want, int coins, int price, int freeSlots) {
        if (want <= 0 || price <= 0) return 0;
        int n = Math.min(want, MAX_PER_BUY);
        n = Math.min(n, coins / price);
        n = Math.min(n, Math.max(0, freeSlots));
        return Math.max(0, n);
    }

    public static boolean isBound(ItemStack s) { return s != null && NmsNbt.has(s, BOUND_KEY); }

    ItemStack boundPotion(String src) {
        NiBridge ni = plugin.getNiBridge();
        ItemStack s = ni == null ? null : ni.createNiItem(potionId());
        if (s == null) return null;
        Map<String, Object> v = new HashMap<String, Object>();
        v.put("src", src);
        s = NmsNbt.write(s, BOUND_KEY, v);
        ItemMeta m = s.getItemMeta();
        if (m != null) {
            List<String> lore = m.hasLore() ? new ArrayList<String>(m.getLore()) : new ArrayList<String>();
            // D99: the template's legacy lines (id, 立即回复 II, 河豚 recipe) contradicted the P1 rules; bound potions are
            // recognised by their NBT (isBound / NI id), never by lore, so the lore is rewritten in full here
            lore.clear();
            lore.add(ChatColor.GRAY + "回复最大生命 20%");
            lore.add(ChatColor.GRAY + "所有回复药共用 15 秒冷却");
            lore.add(ChatColor.GRAY + "副本里切到它，按住右键喝");
            lore.add(ChatColor.DARK_GRAY + "补药：补给官 · 灰粮 / 装备页，" + price() + " 余烬币一瓶");
            lore.add(ChatColor.DARK_GRAY + "绑定 · 不可交易 · 不回售" + ("starter".equals(src) ? " · 起步补给" : ""));
            m.setLore(lore);
            s.setItemMeta(m);
        }
        return s;
    }

    static int freeSlots(Player p) {
        int n = 0;
        ItemStack[] c = p.getInventory().getStorageContents();
        for (ItemStack s : c) if (s == null || s.getType() == org.bukkit.Material.AIR) n++;
        return n;
    }

    /** Puts up to n bound potions into free slots, empty hotbar slots first (9 → 2, slot 1 stays for the blade); returns how many went in. */
    public int give(Player p, int n, String src) {
        int given = 0;
        for (int i = 0; i < n; i++) {
            if (freeSlots(p) <= 0) break;
            ItemStack s = boundPotion(src);
            if (s == null) break;
            int hb = emptyHotbar(p);
            if (hb >= 0) p.getInventory().setItem(hb, s);
            else if (!p.getInventory().addItem(s).isEmpty()) break;
            given++;
        }
        return given;
    }

    /** First empty hotbar index from the right (8 → 1); -1 when none. */
    static int emptyHotbar(Player p) {
        for (int i = 8; i >= 1; i--) {
            ItemStack s = p.getInventory().getItem(i);
            if (s == null || s.getType() == org.bukkit.Material.AIR) return i;
        }
        return -1;
    }

    boolean isHealPotion(ItemStack s) {
        if (s == null || s.getType() == org.bukkit.Material.AIR) return false;
        if (isBound(s)) return true;
        NiBridge ni = plugin.getNiBridge();
        String id = ni == null ? null : ni.getNiId(s);
        if (id == null) return false;
        List<String> l = mode() == null ? null : mode().list("heal_potion.items");
        return l == null || l.isEmpty() ? "potion_ember_heal".equals(id) : l.contains(id);
    }

    /** Hotbar index (0–8) holding a heal potion, 9 when only the backpack has one, -1 when there is none. */
    public int potionSlot(Player p) {
        boolean bag = false;
        ItemStack[] c = p.getInventory().getStorageContents();
        for (int i = 0; i < c.length; i++) {
            if (!isHealPotion(c[i])) continue;
            if (i < 9) return i;
            bag = true;
        }
        return bag ? 9 : -1;
    }

    /** Pure: the low-HP potion hint (null = nothing to say). slot as potionSlot(); cdLeftMs > 0 = shared cooldown running. */
    static String lowHpHint(double hp, double max, int slot, long cdLeftMs) {
        if (max <= 0 || hp <= 0 || hp / max >= LOW_HP || cdLeftMs > 0) return null;
        if (slot >= 0 && slot < 9) return "§c生命低！§f按数字键 " + (slot + 1) + " 切到回复药，§e按住右键喝§f（回复 20%，冷却 15 秒）";
        if (slot == 9) return "§c生命低！§f回复药在背包里：按 E 把它拖到快捷栏，再按住右键喝";
        return "§c生命低且没带回复药！§f后撤拉开距离 · 出本后找补给官 · 灰粮补货";
    }
    static final double LOW_HP = 0.40;

    /** The hint for a P1 player right now (the set HUD shows it instead of its own line). */
    public String lowHpHint(Player p) {
        if (p == null || p.isDead() || !EmberMode.isP1World(p.getWorld())) return null;
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        long cd = ls == null ? 0 : ls.state(p.getUniqueId()).healCdUntil - System.currentTimeMillis();
        return lowHpHint(p.getHealth(), EmberHeal.maxHp(p), potionSlot(p), cd);
    }

    /** Starter supplies (called once by the starter kit). */
    public void giveStarter(Player p) {
        int want = starterPotions();
        if (want <= 0) return;
        int got = give(p, want, "starter");
        plugin.getLogger().info("[P1 supply] starter " + p.getName() + " heal potions " + got + "/" + want);
        if (got > 0) {
            p.sendMessage(P + ChatColor.YELLOW + "回复药 ×" + got + "（绑定）放在快捷栏右侧：副本里按数字键切过去，按住右键喝（回复 20% 生命，15 秒冷却）。"
                    + "补药：右键补给官 · 灰粮或装备页「回复药」，每瓶 " + price() + " 余烬币。");
        }
    }

    public boolean cmd(CommandSender s, String[] args) {
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        int price = price();
        PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
        int coins = pd == null ? 0 : pd.getCoin();
        boolean buy = args.length >= 3 && ("buy".equalsIgnoreCase(args[2]) || "买".equals(args[2]));
        if (!buy) {
            p.sendMessage(P + "余烬回复药：" + price + " 余烬币 / 瓶 · 回复最大生命 20% · 所有回复药共用 15 秒冷却 · 绑定、不回售");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P + "余烬币 " + coins + " · ",
                    new String[]{"[买 1 瓶]", "/corerpg p1 shop buy 1", price + " 余烬币", "GREEN"},
                    new String[]{"[买 5 瓶]", "/corerpg p1 shop buy 5", (5 * price) + " 余烬币", "GREEN"}); // D99
            return true;
        }
        if (!EmberMode.active()) { p.sendMessage(P + ChatColor.RED + "新模式未开启"); return true; }
        if (EmberMode.isP1World(p.getWorld())) { p.sendMessage(P + ChatColor.RED + "副本内不能购买，出本后再买。"); return true; }
        int want = 1;
        if (args.length >= 4) {
            try { want = Integer.parseInt(args[3]); } catch (NumberFormatException e) { p.sendMessage(P + ChatColor.RED + "数量须为 1–" + MAX_PER_BUY); return true; }
        }
        if (want < 1 || want > MAX_PER_BUY) { p.sendMessage(P + ChatColor.RED + "数量须为 1–" + MAX_PER_BUY); return true; }
        int n = affordable(want, coins, price, freeSlots(p));
        if (n <= 0) {
            p.sendMessage(P + ChatColor.RED + (coins < price ? "余烬币不足（需要 " + price + "，现有 " + coins + "）" : "背包没有空格"));
            return true;
        }
        if (pd == null || !pd.takeCoin(n * price)) { p.sendMessage(P + ChatColor.RED + "扣除余烬币失败"); return true; }
        int got = give(p, n, "shop");
        if (got < n) pd.addCoin((n - got) * price);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        plugin.getLogger().info("[P1 supply] shop " + p.getName() + " bought " + got + " heal potion(s) for " + (got * price) + " coin");
        p.sendMessage(P + ChatColor.GREEN + "购买余烬回复药 ×" + got + "，花费 " + (got * price) + " 余烬币（剩余 " + pd.getCoin() + "）"
                + (got < want ? ChatColor.YELLOW + " · 只买了 " + got + " 瓶（余烬币或背包空格不足）" : ""));
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (isBound(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(P + ChatColor.RED + "绑定物品不能丢出。");
        }
    }
}
