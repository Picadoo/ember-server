package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Material warehouse — virtual slots of NI id × amount (DESIGN-ember-material-warehouse).
 * Commands under /corerpg warehouse (not /corerpg storage).
 * D252 / ARCH S0-10: while P1 is on, non-OP players may only view (list/info); deposit / withdraw / unlock
 * are refused (hub menu uses {@code EmberVault} instead). OP / {@code corerpg.admin} still full access.
 */
public final class WarehouseService {

    public static final String PREFIX = ChatColor.GOLD + "[仓库] " + ChatColor.RESET;

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge niBridge;

    private boolean enabled = true;
    private int defaultSlots = 8;
    private int maxSlots = 48;
    private long perSlotCap = 2000000000L;
    private int coinBase = 5000;
    private int coinStep = 1500;
    private int cashPrice = 28;
    private boolean allowCash = true;
    private final Set<String> whitelist = new HashSet<String>();

    public WarehouseService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge niBridge) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.niBridge = niBridge;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "warehouse.yml");
        if (!file.exists()) {
            plugin.saveResource("warehouse.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("warehouse.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        defaultSlots = Math.max(1, cfg.getInt("default_slots", 8));
        maxSlots = Math.max(defaultSlots, cfg.getInt("max_slots", 48));
        perSlotCap = Math.max(1L, cfg.getLong("per_slot_cap", 2000000000L));
        coinBase = Math.max(0, cfg.getInt("unlock.coin_base", 5000));
        coinStep = Math.max(0, cfg.getInt("unlock.coin_step", 1500));
        cashPrice = Math.max(0, cfg.getInt("unlock.cash_price", 28));
        allowCash = cfg.getBoolean("unlock.allow_cash", true);
        whitelist.clear();
        List<String> wl = cfg.getStringList("whitelist");
        if (wl != null) {
            for (String id : wl) {
                if (id != null && !id.trim().isEmpty()) {
                    whitelist.add(id.trim());
                }
            }
        }
    }

    public boolean isEnabled() { return enabled; }
    public int getDefaultSlots() { return defaultSlots; }
    public int getMaxSlots() { return maxSlots; }
    public long getPerSlotCap() { return perSlotCap; }

    public void ensureDefaults(PlayerData data) {
        if (data == null) return;
        if (data.getWarehouseSlotsUnlocked() <= 0) {
            data.setWarehouseSlotsUnlocked(defaultSlots);
        }
        if (data.getWarehouseSlotsUnlocked() > maxSlots) {
            data.setWarehouseSlotsUnlocked(maxSlots);
        }
    }

    public int unlockCoinPrice(PlayerData data) {
        ensureDefaults(data);
        int unlockedExtra = Math.max(0, data.getWarehouseSlotsUnlocked() - defaultSlots);
        return coinBase + unlockedExtra * coinStep;
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "仅玩家可用。");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.warehouse") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.warehouse");
            return;
        }
        if (!enabled) {
            p.sendMessage(PREFIX + ChatColor.RED + "材料仓未启用。");
            return;
        }
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "";
        if (sub.isEmpty() || "list".equals(sub) || "overview".equals(sub)) {
            cmdOverview(p);
            return;
        }
        if ("info".equals(sub)) {
            cmdInfo(p, args);
            return;
        }
        // D252 S0-10: P1 view-only for non-OP (belt-and-suspenders vs legacy_gate.allow)
        if (refuseWriteWhileP1(p, sub)) return;
        if ("deposit".equals(sub) || "in".equals(sub) || "存".equals(sub)) {
            cmdDeposit(p);
            return;
        }
        if ("withdraw".equals(sub) || "out".equals(sub) || "取".equals(sub)) {
            cmdWithdraw(p, args);
            return;
        }
        if ("unlock".equals(sub) || "expand".equals(sub)) {
            cmdUnlock(p, args);
            return;
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW
                + "/corerpg warehouse | deposit | withdraw <slot|id> [n] | unlock [coin|cash] | info <slot]");
    }

    /** D252 / S0-10: true → write sub refused (player told to use hub vault). */
    private boolean refuseWriteWhileP1(Player p, String sub) {
        if (!isWriteSub(sub)) return false;
        if (!town.sunshine.corerpg.p1.EmberMode.active()) return false;
        if (p.isOp() || p.hasPermission("corerpg.admin")) return false;
        p.sendMessage(PREFIX + ChatColor.YELLOW
                + "P1 模式下请打开枢纽「仓库」存取材料；旧仓库命令只可查看。");
        return true;
    }

    private static boolean isWriteSub(String sub) {
        return "deposit".equals(sub) || "in".equals(sub) || "存".equals(sub)
                || "withdraw".equals(sub) || "out".equals(sub) || "取".equals(sub)
                || "unlock".equals(sub) || "expand".equals(sub);
    }

    private void cmdOverview(Player p) {
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDefaults(data);
        List<PlayerData.WarehouseSlot> slots = data.getWarehouseSlots();
        int used = 0;
        for (PlayerData.WarehouseSlot s : slots) {
            if (s != null && s.niId != null && !s.niId.isEmpty() && s.amount > 0L) used++;
        }
        int total = data.getWarehouseSlotsUnlocked();
        p.sendMessage(PREFIX + ChatColor.YELLOW + "材料仓 "
                + ChatColor.WHITE + used + "/" + total
                + ChatColor.GRAY + " 格（上限 " + maxSlots + "）");
        if (slots.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.GRAY + "空仓。请用枢纽菜单「仓库」存入材料。");
            return;
        }
        for (int i = 0; i < slots.size(); i++) {
            PlayerData.WarehouseSlot s = slots.get(i);
            if (s == null || s.niId == null || s.niId.isEmpty() || s.amount <= 0L) continue;
            p.sendMessage(ChatColor.DARK_GRAY + "  #" + (i + 1) + " "
                    + ChatColor.AQUA + s.niId + ChatColor.GRAY + " × "
                    + ChatColor.WHITE + s.amount);
        }
        int nextPrice = unlockCoinPrice(data);
        if (total < maxSlots) {
            p.sendMessage(PREFIX + ChatColor.GRAY + "解锁下一格: "
                    + ChatColor.GOLD + nextPrice + " 余烬币"
                    + (allowCash ? ChatColor.GRAY + " 或 " + ChatColor.LIGHT_PURPLE + cashPrice + " 晶钻" : ""));
        }
    }

    private void cmdDeposit(Player p) {
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDefaults(data);
        ItemStack hand = mainHand(p);
        if (hand == null || hand.getType() == Material.AIR) {
            p.sendMessage(PREFIX + ChatColor.RED + "请手持要存入的材料。");
            return;
        }
        String niId = niBridge.getNiId(hand);
        if (niId == null || niId.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "主手不是 NeigeItems 物品，无法入仓。");
            return;
        }
        if (!whitelist.contains(niId)) {
            p.sendMessage(PREFIX + ChatColor.RED + "「" + niId + "」不在材料仓白名单，拒存。");
            return;
        }
        int amount = hand.getAmount();
        if (amount <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "数量无效。");
            return;
        }
        List<PlayerData.WarehouseSlot> slots = new ArrayList<PlayerData.WarehouseSlot>(data.getWarehouseSlots());
        int matchIdx = -1;
        for (int i = 0; i < slots.size(); i++) {
            PlayerData.WarehouseSlot s = slots.get(i);
            if (s != null && niId.equals(s.niId)) {
                matchIdx = i;
                break;
            }
        }
        int unlocked = data.getWarehouseSlotsUnlocked();
        if (matchIdx < 0) {
            // count occupied
            int occupied = 0;
            for (PlayerData.WarehouseSlot s : slots) {
                if (s != null && s.niId != null && !s.niId.isEmpty() && s.amount > 0L) occupied++;
            }
            if (occupied >= unlocked) {
                p.sendMessage(PREFIX + ChatColor.RED + "仓库已满（" + occupied + "/" + unlocked
                        + "）。请先 /corerpg warehouse unlock 或取出空格。");
                return;
            }
            long put = Math.min((long) amount, perSlotCap);
            slots.add(new PlayerData.WarehouseSlot(niId, put));
            consumeMainHand(p, (int) put);
            data.setWarehouseSlots(slots);
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + ChatColor.GREEN + "已存入 "
                    + ChatColor.AQUA + niId + ChatColor.GREEN + " × " + put
                    + ChatColor.GRAY + "（新格）");
            return;
        }
        PlayerData.WarehouseSlot existing = slots.get(matchIdx);
        long room = perSlotCap - existing.amount;
        if (room <= 0L) {
            p.sendMessage(PREFIX + ChatColor.RED + "该材料格已达上限 " + perSlotCap + "。");
            return;
        }
        long put = Math.min((long) amount, room);
        existing.amount += put;
        slots.set(matchIdx, existing);
        consumeMainHand(p, (int) put);
        data.setWarehouseSlots(slots);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已存入 "
                + ChatColor.AQUA + niId + ChatColor.GREEN + " × " + put
                + ChatColor.GRAY + "（合计 " + existing.amount + "）");
    }

    private void cmdWithdraw(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg warehouse withdraw <slot|id> [n]");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDefaults(data);
        List<PlayerData.WarehouseSlot> slots = new ArrayList<PlayerData.WarehouseSlot>(data.getWarehouseSlots());
        String key = args[2];
        int idx = resolveSlotIndex(slots, key);
        if (idx < 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到格/材料: " + key);
            return;
        }
        PlayerData.WarehouseSlot slot = slots.get(idx);
        // D177 rev 2: the account-bound part (挂机庭 loot, EmberVault C_BOUND) never leaves the warehouse — P1 or not
        long bound = Math.max(0, Math.min(slot.amount, data.periodCount("p1_vbound_" + slot.niId, "all")));
        long available = slot.amount - bound;
        if (available <= 0) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "这些是挂机庭的账号绑定战利品（" + bound + "）：不能取出，锻造 / 强化时直接从仓库扣。");
            return;
        }
        long want = 64L;
        if (args.length >= 4) {
            try {
                want = Long.parseLong(args[3]);
            } catch (NumberFormatException e) {
                p.sendMessage(PREFIX + ChatColor.RED + "数量无效: " + args[3]);
                return;
            }
        }
        if (want <= 0L) {
            p.sendMessage(PREFIX + ChatColor.RED + "数量须 > 0。");
            return;
        }
        long take = Math.min(want, available);
        // give at most Integer.MAX_VALUE per call (NI stacks)
        int giveAmt = take > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) take;
        if (giveAmt <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "取出数量无效。");
            return;
        }
        boolean ok = niBridge.giveNiItem(p, slot.niId, giveAmt);
        if (!ok) {
            p.sendMessage(PREFIX + ChatColor.RED + "发放物品失败（NI 未就绪或 id 缺失）: " + slot.niId);
            return;
        }
        long left = slot.amount - giveAmt; // D177 rev 2: the bound part stays in the entry
        if (left <= 0L) {
            slots.remove(idx);
        } else {
            slot.amount = left;
            slots.set(idx, slot);
        }
        data.setWarehouseSlots(slots);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已取出 "
                + ChatColor.AQUA + slot.niId + ChatColor.GREEN + " × " + giveAmt
                + (left > 0L ? ChatColor.GRAY + "（余 " + left + "）" : ChatColor.GRAY + "（格已空）"));
    }

    private void cmdUnlock(Player p, String[] args) {
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDefaults(data);
        int cur = data.getWarehouseSlotsUnlocked();
        if (cur >= maxSlots) {
            p.sendMessage(PREFIX + ChatColor.RED + "已达最大格数 " + maxSlots + "。");
            return;
        }
        String pay = "coin";
        if (args.length >= 3 && args[2] != null && !args[2].isEmpty()) {
            pay = args[2].toLowerCase(Locale.ROOT);
        }
        if ("cash".equals(pay) || "crystal".equals(pay) || "晶钻".equals(pay)) {
            if (!allowCash) {
                p.sendMessage(PREFIX + ChatColor.RED + "未开放晶钻解锁。");
                return;
            }
            if (!data.takeCrystalCash(cashPrice)) {
                p.sendMessage(PREFIX + ChatColor.RED + "晶钻不足（需要 " + cashPrice
                        + "，当前 " + data.getCrystalCash() + "）。");
                return;
            }
            data.setWarehouseSlotsUnlocked(cur + 1);
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + ChatColor.GREEN + "已用 "
                    + ChatColor.LIGHT_PURPLE + cashPrice + " 晶钻"
                    + ChatColor.GREEN + " 解锁至 "
                    + ChatColor.WHITE + data.getWarehouseSlotsUnlocked() + ChatColor.GREEN + " 格。");
            return;
        }
        // default coin
        int price = unlockCoinPrice(data);
        if (!data.takeCoin(price)) {
            p.sendMessage(PREFIX + ChatColor.RED + "余烬币不足（需要 " + price
                    + "，当前 " + data.getCoin() + "）。");
            return;
        }
        data.setWarehouseSlotsUnlocked(cur + 1);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已用 "
                + ChatColor.GOLD + price + " 余烬币"
                + ChatColor.GREEN + " 解锁至 "
                + ChatColor.WHITE + data.getWarehouseSlotsUnlocked() + ChatColor.GREEN + " 格。");
    }

    private void cmdInfo(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg warehouse info <slot>");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDefaults(data);
        List<PlayerData.WarehouseSlot> slots = data.getWarehouseSlots();
        int idx = resolveSlotIndex(slots, args[2]);
        if (idx < 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "无效格位: " + args[2]);
            return;
        }
        PlayerData.WarehouseSlot s = slots.get(idx);
        p.sendMessage(PREFIX + ChatColor.YELLOW + "格 #" + (idx + 1));
        p.sendMessage(ChatColor.GRAY + "  id: " + ChatColor.AQUA + s.niId);
        p.sendMessage(ChatColor.GRAY + "  数量: " + ChatColor.WHITE + s.amount
                + ChatColor.DARK_GRAY + " / " + perSlotCap);
        p.sendMessage(ChatColor.GRAY + "  白名单: "
                + (whitelist.contains(s.niId) ? ChatColor.GREEN + "是" : ChatColor.RED + "否"));
    }

    /** 1-based slot number or niId. */
    private int resolveSlotIndex(List<PlayerData.WarehouseSlot> slots, String key) {
        if (key == null || key.isEmpty() || slots == null) return -1;
        try {
            int n = Integer.parseInt(key);
            if (n >= 1 && n <= slots.size()) return n - 1;
            return -1;
        } catch (NumberFormatException ignored) {
        }
        for (int i = 0; i < slots.size(); i++) {
            PlayerData.WarehouseSlot s = slots.get(i);
            if (s != null && key.equalsIgnoreCase(s.niId)) return i;
        }
        return -1;
    }

    private static ItemStack mainHand(Player p) {
        try {
            return p.getInventory().getItemInMainHand();
        } catch (Throwable t) {
            try {
                return p.getItemInHand();
            } catch (Throwable t2) {
                return null;
            }
        }
    }

    private static void consumeMainHand(Player p, int amount) {
        ItemStack hand = mainHand(p);
        if (hand == null || hand.getType() == Material.AIR || amount <= 0) return;
        int left = hand.getAmount() - amount;
        if (left <= 0) {
            try {
                p.getInventory().setItemInMainHand(null);
            } catch (Throwable t) {
                p.setItemInHand(null);
            }
        } else {
            hand.setAmount(left);
            try {
                p.getInventory().setItemInMainHand(hand);
            } catch (Throwable t) {
                p.setItemInHand(hand);
            }
        }
        p.updateInventory();
    }
}
