package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import town.sunshine.corerpg.storage.MysqlStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Coin auction / 寄售 — whitelist NI listings, 10% tax.
 * DESIGN-ember-arena-auction.md §2 · store plugins/CoreRpg/auction.yml
 */
public final class AuctionService {

    public static final String PREFIX = ChatColor.GOLD + "[寄售] " + ChatColor.RESET;

    public static final class Listing {
        public int id;
        public UUID sellerUuid;
        public String sellerName = "";
        public String niId = "";
        public int amount = 1;
        public int price;
        public String created = "";
        public String displayName = "";
    }

    private final JavaPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge niBridge;

    private boolean enabled = true;
    private double taxRate = 0.10;
    private int pageSize = 8;
    private int maxPerPlayer = 12;
    private int minPrice = 1;
    private int maxPrice = 1000000;
    private int banEnhanceAt = 7;
    private final Set<String> whitelist = new HashSet<String>();

    private final Map<Integer, Listing> listings = new LinkedHashMap<Integer, Listing>();
    private int nextId = 1;
    private File dataFile;
    private boolean dirty;

    public AuctionService(JavaPlugin plugin, PlayerDataStore dataStore, NiBridge niBridge) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.niBridge = niBridge;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "auction.yml");
        if (!file.exists()) {
            plugin.saveResource("auction.yml", false);
        }
        dataFile = file;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("auction.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            // merge missing config keys but keep runtime listings
            for (String key : def.getKeys(true)) {
                if (!cfg.contains(key) && !key.startsWith("listings") && !"next_id".equals(key)) {
                    cfg.set(key, def.get(key));
                }
            }
        }
        enabled = cfg.getBoolean("enabled", true);
        taxRate = cfg.getDouble("tax_rate", 0.10);
        if (taxRate < 0.0) taxRate = 0.0;
        if (taxRate > 0.5) taxRate = 0.5;
        pageSize = Math.max(1, cfg.getInt("page_size", 8));
        maxPerPlayer = Math.max(1, cfg.getInt("max_listings_per_player", 12));
        minPrice = Math.max(1, cfg.getInt("min_price", 1));
        maxPrice = Math.max(minPrice, cfg.getInt("max_price", 1000000));
        banEnhanceAt = Math.max(0, cfg.getInt("ban_enhance_at", 7));

        whitelist.clear();
        List<?> wl = cfg.getList("whitelist_ni_ids");
        if (wl != null) {
            for (Object o : wl) {
                if (o == null) continue;
                String s = String.valueOf(o).trim();
                if (!s.isEmpty()) whitelist.add(s);
            }
        }
        if (whitelist.isEmpty()) {
            whitelist.add("mat_ember_shard");
            whitelist.add("mat_ember_bone_dust");
            whitelist.add("mat_ember_core_fragment");
            whitelist.add("mat_ember_reforge_stone");
            whitelist.add("mat_ember_protect_scroll");
            whitelist.add("gem_ember_sharp");
            whitelist.add("gem_ember_steady");
            whitelist.add("cosmetic_calamity_shard");
        }

        listings.clear();
        nextId = Math.max(1, cfg.getInt("next_id", 1));
        MysqlStorage mysql = mysqlOrNull();
        FileConfiguration listSrc = cfg;
        if (mysql != null) {
            try {
                String blob = mysql.loadAuctionBlob();
                if (blob != null && !blob.isEmpty()) {
                    YamlConfiguration y = new YamlConfiguration();
                    y.loadFromString(blob);
                    listSrc = y;
                    nextId = Math.max(1, y.getInt("next_id", nextId));
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Failed to load auction blob from MySQL", t);
            }
        }
        ConfigurationSection sec = listSrc.getConfigurationSection("listings");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                ConfigurationSection row = sec.getConfigurationSection(key);
                if (row == null) continue;
                Listing L = new Listing();
                try {
                    L.id = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    L.id = row.getInt("id", 0);
                }
                if (L.id <= 0) continue;
                String su = row.getString("sellerUuid", "");
                try {
                    L.sellerUuid = UUID.fromString(su);
                } catch (Exception e) {
                    continue;
                }
                L.sellerName = row.getString("sellerName", "");
                L.niId = row.getString("niId", "");
                L.amount = Math.max(1, row.getInt("amount", 1));
                L.price = Math.max(1, row.getInt("price", 1));
                L.created = row.getString("created", "");
                L.displayName = row.getString("displayName", L.niId);
                if (L.niId == null || L.niId.isEmpty()) continue;
                listings.put(Integer.valueOf(L.id), L);
                if (L.id >= nextId) nextId = L.id + 1;
            }
        }
        dirty = false;
        plugin.getLogger().info("Auction listings loaded: " + listings.size()
                + (mysql != null ? " (mysql)" : " (yaml)"));
    }

    public void saveAll() {
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                YamlConfiguration blob = listingsToYaml();
                mysql.saveAuctionBlob(blob.saveToString());
                dirty = false;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save auction MySQL", e);
            }
            // Keep config keys on disk (no listings) so yaml mode remains usable
            persistAuctionConfigOnly();
            return;
        }
        if (dataFile == null) {
            dataFile = new File(plugin.getDataFolder(), "auction.yml");
        }
        FileConfiguration cfg;
        if (dataFile.exists()) {
            cfg = YamlConfiguration.loadConfiguration(dataFile);
        } else {
            cfg = new YamlConfiguration();
            InputStream in = plugin.getResource("auction.yml");
            if (in != null) {
                cfg = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        }
        cfg.set("enabled", Boolean.valueOf(enabled));
        cfg.set("tax_rate", Double.valueOf(taxRate));
        cfg.set("page_size", Integer.valueOf(pageSize));
        cfg.set("max_listings_per_player", Integer.valueOf(maxPerPlayer));
        cfg.set("min_price", Integer.valueOf(minPrice));
        cfg.set("max_price", Integer.valueOf(maxPrice));
        cfg.set("ban_enhance_at", Integer.valueOf(banEnhanceAt));
        cfg.set("whitelist_ni_ids", new ArrayList<String>(whitelist));
        cfg.set("listings", null);
        for (Listing L : listings.values()) {
            String path = "listings." + L.id;
            cfg.set(path + ".sellerUuid", L.sellerUuid.toString());
            cfg.set(path + ".sellerName", L.sellerName);
            cfg.set(path + ".niId", L.niId);
            cfg.set(path + ".amount", Integer.valueOf(L.amount));
            cfg.set(path + ".price", Integer.valueOf(L.price));
            cfg.set(path + ".created", L.created);
            cfg.set(path + ".displayName", L.displayName);
        }
        cfg.set("next_id", Integer.valueOf(nextId));
        try {
            cfg.save(dataFile);
            dirty = false;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save auction.yml", e);
        }
    }

    YamlConfiguration listingsToYaml() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("next_id", Integer.valueOf(nextId));
        for (Listing L : listings.values()) {
            String path = "listings." + L.id;
            y.set(path + ".sellerUuid", L.sellerUuid.toString());
            y.set(path + ".sellerName", L.sellerName);
            y.set(path + ".niId", L.niId);
            y.set(path + ".amount", Integer.valueOf(L.amount));
            y.set(path + ".price", Integer.valueOf(L.price));
            y.set(path + ".created", L.created);
            y.set(path + ".displayName", L.displayName);
        }
        return y;
    }

    private void persistAuctionConfigOnly() {
        if (dataFile == null) {
            dataFile = new File(plugin.getDataFolder(), "auction.yml");
        }
        FileConfiguration cfg;
        if (dataFile.exists()) {
            cfg = YamlConfiguration.loadConfiguration(dataFile);
        } else {
            cfg = new YamlConfiguration();
        }
        cfg.set("enabled", Boolean.valueOf(enabled));
        cfg.set("tax_rate", Double.valueOf(taxRate));
        cfg.set("page_size", Integer.valueOf(pageSize));
        cfg.set("max_listings_per_player", Integer.valueOf(maxPerPlayer));
        cfg.set("min_price", Integer.valueOf(minPrice));
        cfg.set("max_price", Integer.valueOf(maxPrice));
        cfg.set("ban_enhance_at", Integer.valueOf(banEnhanceAt));
        cfg.set("whitelist_ni_ids", new ArrayList<String>(whitelist));
        // do not wipe listings on disk during mysql mode — leave as fallback snapshot
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save auction.yml config", e);
        }
    }

    private MysqlStorage mysqlOrNull() {
        if (plugin instanceof CoreRpgPlugin) {
            MysqlStorage m = ((CoreRpgPlugin) plugin).getMysqlStorage();
            if (m != null && m.isActive()) return m;
        }
        return null;
    }

    public void saveIfDirty() {
        if (dirty) saveAll();
    }

    public boolean isEnabled() { return enabled; }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "寄售系统未启用。");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.auction") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.auction");
            return;
        }
        if (args.length < 2) {
            cmdMine(p);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if ("list".equals(sub) || "browse".equals(sub)) {
            int page = 1;
            if (args.length >= 3) {
                try { page = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {}
            }
            cmdList(p, page);
            return;
        }
        if ("sell".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg auction sell <价格>");
                p.sendMessage(ChatColor.DARK_GRAY + "  手持白名单物品上架 · 成交税 "
                        + pctLabel() + " · 卖方实收 ×" + String.format(Locale.ROOT, "%.0f%%", (1.0 - taxRate) * 100));
                return;
            }
            int price;
            try {
                price = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(PREFIX + ChatColor.RED + "价格无效");
                return;
            }
            cmdSell(p, price);
            return;
        }
        if ("buy".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg auction buy <id>");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(PREFIX + ChatColor.RED + "无效 id");
                return;
            }
            cmdBuy(p, id);
            return;
        }
        if ("cancel".equals(sub) || "off".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg auction cancel <id>");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(PREFIX + ChatColor.RED + "无效 id");
                return;
            }
            cmdCancel(p, id);
            return;
        }
        if ("help".equals(sub)) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg auction [list|sell|buy|cancel]");
            p.sendMessage(ChatColor.GRAY + "  税率 §e" + pctLabel()
                    + ChatColor.GRAY + " · 白名单材料/宝石/票/使魔蛋 · 禁高强化装备");
            return;
        }
        cmdMine(p);
    }

    private String pctLabel() {
        return String.format(Locale.ROOT, "%.0f%%", taxRate * 100.0);
    }

    private int sellerReceive(int price) {
        int tax = (int) Math.floor(price * taxRate);
        return Math.max(0, price - tax);
    }

    private void cmdMine(Player p) {
        List<Listing> mine = new ArrayList<Listing>();
        for (Listing L : listings.values()) {
            if (p.getUniqueId().equals(L.sellerUuid)) mine.add(L);
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW + "我的在售 §f" + mine.size()
                + ChatColor.GRAY + "/" + maxPerPlayer
                + ChatColor.DARK_GRAY + " · 税 " + pctLabel());
        if (mine.isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "  暂无上架。手持白名单物：/corerpg auction sell <价>");
            return;
        }
        for (Listing L : mine) {
            p.sendMessage(ChatColor.GRAY + "  #" + L.id + " "
                    + ChatColor.WHITE + displayOf(L) + ChatColor.GRAY + " ×" + L.amount
                    + ChatColor.YELLOW + "  " + L.price + " 币"
                    + ChatColor.DARK_GRAY + "（实收 " + sellerReceive(L.price) + "）");
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg auction cancel <id> · list · buy <id>");
    }

    private void cmdList(Player p, int page) {
        List<Listing> all = new ArrayList<Listing>(listings.values());
        if (all.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.GRAY + "寄售架为空。");
            return;
        }
        int pages = Math.max(1, (all.size() + pageSize - 1) / pageSize);
        if (page < 1) page = 1;
        if (page > pages) page = pages;
        int from = (page - 1) * pageSize;
        int to = Math.min(all.size(), from + pageSize);
        p.sendMessage(PREFIX + ChatColor.YELLOW + "在售列表 §f" + all.size()
                + ChatColor.GRAY + " · 第 " + page + "/" + pages + " 页"
                + ChatColor.DARK_GRAY + " · 税 " + pctLabel());
        for (int i = from; i < to; i++) {
            Listing L = all.get(i);
            p.sendMessage(ChatColor.GRAY + "  #" + L.id + " "
                    + ChatColor.WHITE + displayOf(L) + ChatColor.GRAY + " ×" + L.amount
                    + ChatColor.YELLOW + "  " + L.price + " 币"
                    + ChatColor.DARK_GRAY + " · " + L.sellerName);
        }
        if (page < pages) {
            p.sendMessage(ChatColor.DARK_GRAY + "  下一页：/corerpg auction list " + (page + 1));
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  购买：/corerpg auction buy <id>");
    }

    private void cmdSell(Player p, int price) {
        if (price < minPrice || price > maxPrice) {
            p.sendMessage(PREFIX + ChatColor.RED + "价格须在 " + minPrice + "～" + maxPrice + " 之间。");
            return;
        }
        int mine = countMine(p.getUniqueId());
        if (mine >= maxPerPlayer) {
            p.sendMessage(PREFIX + ChatColor.RED + "上架已满（" + maxPerPlayer + "）。先 cancel 部分。");
            return;
        }
        @SuppressWarnings("deprecation")
        ItemStack hand = p.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR || hand.getAmount() <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "请手持要上架的物品。");
            return;
        }
        if (town.sunshine.corerpg.p1.EmberSupplyService.isBound(hand)) {
            p.sendMessage(PREFIX + ChatColor.RED + "绑定物品不能寄售。");
            return;
        }
        String niId = niBridge.getNiId(hand);
        if (niId == null || niId.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "仅可上架白名单 NI 物品（材料/宝石/票/使魔蛋等）。");
            return;
        }
        if (!whitelist.contains(niId)) {
            p.sendMessage(PREFIX + ChatColor.RED + "物品不在寄售白名单：" + niId);
            p.sendMessage(ChatColor.DARK_GRAY + "  禁毕业刃直售；核心碎片可卖但走税 " + pctLabel());
            return;
        }
        int enhance = GearLore.readEnhance(hand);
        if (banEnhanceAt > 0 && enhance >= banEnhanceAt) {
            p.sendMessage(PREFIX + ChatColor.RED + "强化 +" + enhance
                    + " ≥" + banEnhanceAt + " 的装备禁止寄售（防毕业滥用）。");
            return;
        }
        // gear blades/charms are not on whitelist; double-check enhance>0 gear ids
        if ((niId.startsWith("gear_") || niId.contains("blade") || niId.contains("charm"))
                && enhance > 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "已强化装备不可寄售。");
            return;
        }

        int amount = hand.getAmount();
        String display = resolveDisplay(hand, niId);

        // take held stack (1.12 API)
        p.setItemInHand(null);
        p.updateInventory();

        Listing L = new Listing();
        L.id = nextId++;
        L.sellerUuid = p.getUniqueId();
        L.sellerName = p.getName();
        L.niId = niId;
        L.amount = amount;
        L.price = price;
        L.created = DailyService.today() + " " + DailyService.nowHm();
        L.displayName = display;
        listings.put(Integer.valueOf(L.id), L);
        dirty = true;
        saveAll();

        p.sendMessage(PREFIX + ChatColor.GREEN + "已上架 #" + L.id + " "
                + ChatColor.WHITE + display + ChatColor.GRAY + " ×" + amount
                + ChatColor.YELLOW + " · " + price + " 币"
                + ChatColor.DARK_GRAY + "（成交实收 " + sellerReceive(price) + "，税 " + pctLabel() + "）");
    }

    private void cmdBuy(Player p, int id) {
        Listing L = listings.get(Integer.valueOf(id));
        if (L == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到寄售 #" + id);
            return;
        }
        if (p.getUniqueId().equals(L.sellerUuid)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能购买自己的寄售，请用 cancel。");
            return;
        }
        PlayerData buyer = dataStore.get(p.getUniqueId());
        if (!buyer.takeCoin(L.price)) {
            p.sendMessage(PREFIX + ChatColor.RED + "余烬币不足（需要 " + L.price
                    + "，余额 " + buyer.getCoin() + "）。");
            return;
        }
        dataStore.flushMutation(p.getUniqueId());

        int receive = sellerReceive(L.price);
        PlayerData sellerData = dataStore.get(L.sellerUuid);
        if (receive > 0) {
            sellerData.addCoin(receive);
            dataStore.flushMutation(L.sellerUuid);
        }

        boolean given = niBridge.giveNiItem(p, L.niId, L.amount);
        if (!given) {
            // refund buyer; revoke seller credit
            buyer.addCoin(L.price);
            dataStore.flushMutation(p.getUniqueId());
            if (receive > 0) {
                sellerData.takeCoin(receive);
                dataStore.flushMutation(L.sellerUuid);
            }
            p.sendMessage(PREFIX + ChatColor.RED + "发放物品失败，已退款。");
            return;
        }

        listings.remove(Integer.valueOf(id));
        dirty = true;
        saveAll();

        p.sendMessage(PREFIX + ChatColor.GREEN + "已购买 #" + id + " "
                + ChatColor.WHITE + displayOf(L) + ChatColor.GRAY + " ×" + L.amount
                + ChatColor.YELLOW + " · 花费 " + L.price + " 币"
                + ChatColor.GRAY + "（余额 " + buyer.getCoin() + "）");

        Player sellerOnline = Bukkit.getPlayer(L.sellerUuid);
        if (sellerOnline != null && sellerOnline.isOnline()) {
            sellerOnline.sendMessage(PREFIX + ChatColor.GREEN + "寄售 #" + id + " 已成交"
                    + ChatColor.YELLOW + " · 实收 " + receive + " 币"
                    + ChatColor.DARK_GRAY + "（税 " + pctLabel() + "，标价 " + L.price + "）"
                    + ChatColor.GRAY + " · 买家 " + p.getName());
        }
    }

    private void cmdCancel(Player p, int id) {
        Listing L = listings.get(Integer.valueOf(id));
        if (L == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到寄售 #" + id);
            return;
        }
        boolean admin = p.hasPermission("corerpg.admin");
        if (!p.getUniqueId().equals(L.sellerUuid) && !admin) {
            p.sendMessage(PREFIX + ChatColor.RED + "只能下架自己的寄售。");
            return;
        }
        listings.remove(Integer.valueOf(id));
        dirty = true;
        saveAll();

        boolean given = niBridge.giveNiItem(p, L.niId, L.amount);
        if (!given && !p.getUniqueId().equals(L.sellerUuid)) {
            // admin cancel for other: try give to original seller
            OfflinePlayer off = Bukkit.getOfflinePlayer(L.sellerUuid);
            Player sellerOnline = off != null ? off.getPlayer() : null;
            if (sellerOnline != null) {
                given = niBridge.giveNiItem(sellerOnline, L.niId, L.amount);
            }
        }
        p.sendMessage(PREFIX + ChatColor.GREEN + "已下架 #" + id + " "
                + ChatColor.WHITE + displayOf(L) + ChatColor.GRAY + " ×" + L.amount
                + (given ? ChatColor.GREEN + " · 物品已返还" : ChatColor.RED + " · 返还失败请联系管理"));
    }

    private int countMine(UUID uuid) {
        int n = 0;
        for (Listing L : listings.values()) {
            if (uuid.equals(L.sellerUuid)) n++;
        }
        return n;
    }

    private static String displayOf(Listing L) {
        if (L.displayName != null && !L.displayName.isEmpty()) return L.displayName;
        return L.niId;
    }

    private static String resolveDisplay(ItemStack stack, String niId) {
        if (stack != null) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String n = ChatColor.stripColor(meta.getDisplayName());
                if (n != null && !n.isEmpty()) return n;
            }
        }
        return niId;
    }
}
