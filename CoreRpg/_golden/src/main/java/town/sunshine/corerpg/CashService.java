package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Crystal cash shop + monthly card (DESIGN-ember-cash-monthly.md). */
public final class CashService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private String currencyDisplay = "余烬晶钻";
    private int freeTickets = 3;
    private int hardCap = 6;
    private String ticketNiId = "ticket_ember_daily";
    private int shopPrice = 60;
    private int shopSkuLimit = 3;
    private boolean shopGrantNi = true;
    private boolean monthlyEnabled = true;
    private int monthlyDurationDays = 30;
    private int monthlyPrice = 68;
    private int monthlyImmediateTicket = 1;
    private int monthlyLoginCoin = 200;
    private int monthlyLoginTicket = 1;

    public CashService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "cash.yml");
        if (!file.exists()) {
            plugin.saveResource("cash.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("cash.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        loadFrom(cfg);
        if (ni != null) ni.warnMissingOnceIfAbsent(ticketNiId);
    }

    private void loadFrom(FileConfiguration cfg) {
        enabled = cfg.getBoolean("enabled", true);
        currencyDisplay = cfg.getString("currency.display", "余烬晶钻");
        freeTickets = cfg.getInt("daily.free_tickets", 3);
        hardCap = cfg.getInt("daily.hard_cap", 6);
        ticketNiId = cfg.getString("daily.ticket_ni_id", "ticket_ember_daily");
        ConfigurationSection shop = cfg.getConfigurationSection("shop.daily_ticket");
        if (shop != null) {
            shopPrice = shop.getInt("price", 60);
            shopSkuLimit = shop.getInt("sku_limit", 3);
            shopGrantNi = shop.getBoolean("grant_ni", true);
        } else {
            shopPrice = 60;
            shopSkuLimit = 3;
            shopGrantNi = true;
        }
        ConfigurationSection monthly = cfg.getConfigurationSection("monthly");
        if (monthly != null) {
            monthlyEnabled = monthly.getBoolean("enabled", true);
            monthlyDurationDays = monthly.getInt("duration_days", 30);
            monthlyPrice = monthly.getInt("price", 68);
            monthlyImmediateTicket = monthly.getInt("immediate_daily_ticket", 1);
            ConfigurationSection login = monthly.getConfigurationSection("daily_login");
            if (login != null) {
                monthlyLoginCoin = login.getInt("coin", 200);
                monthlyLoginTicket = login.getInt("daily_ticket", 1);
            } else {
                monthlyLoginCoin = 200;
                monthlyLoginTicket = 1;
            }
        }
    }

    public boolean isEnabled() { return enabled; }
    public int getHardCap() { return hardCap; }
    public int getShopPrice() { return shopPrice; }
    public int getShopSkuLimit() { return shopSkuLimit; }
    public int getFreeTickets() { return freeTickets; }
    public String getTicketNiId() { return ticketNiId; }
    public String getCurrencyDisplay() { return currencyDisplay; }

    /** Cross-day reset for cash/ticket counters (Asia/Shanghai via DailyService.today()). */
    public void ensureCashDay(PlayerData data) {
        String today = DailyService.today();
        if (today.equals(data.getDailyCashResetDate())) return;
        data.setDailyTicketsGranted(0);
        data.setDailyTicketsBought(0);
        data.setDailyEntriesUsed(0);
        data.setDailyFreeGranted(false);
        data.setDailyCashResetDate(today);
    }

    /** Join path: free daily tickets then monthly login gift. Call after dataStore.get (ensureDaily). */
    public void onJoin(Player player) {
        if (!enabled || player == null) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);
        grantFreeTicketsIfNeeded(player, data);
        processMonthlyLogin(player, data);
        if (data.isDirty()) dataStore.flushMutation(player.getUniqueId());
    }

    public void grantFreeTicketsIfNeeded(Player player, PlayerData data) {
        if (data.isDailyFreeGranted()) return;
        int want = Math.max(0, freeTickets);
        if (want <= 0) {
            data.setDailyFreeGranted(true);
            return;
        }
        int room = hardCap - data.getDailyTicketsGranted();
        int give = Math.min(want, Math.max(0, room));
        if (give <= 0) {
            data.setDailyFreeGranted(true);
            return;
        }
        if (!giveTickets(player, give)) {
            plugin.getLogger().warning("Failed to give free daily tickets to " + player.getName()
                    + " — will retry next join");
            return; // leave dailyFreeGranted false for retry
        }
        data.setDailyTicketsGranted(data.getDailyTicketsGranted() + give);
        data.setDailyFreeGranted(true);
    }

    public void processMonthlyLogin(Player player, PlayerData data) {
        String today = DailyService.today();
        if (!isMonthlyActive(data, today)) {
            if (data.isMonthlyCard()) {
                data.setMonthlyCard(false);
            }
            return;
        }
        data.setMonthlyCard(true);
        if (today.equals(data.getMonthlyLastGrantDate())) return;

        data.addCoin(monthlyLoginCoin);
        boolean ticketGiven = false;
        if (monthlyLoginTicket > 0 && data.getDailyTicketsGranted() < hardCap) {
            int give = Math.min(monthlyLoginTicket, hardCap - data.getDailyTicketsGranted());
            if (give > 0 && giveTickets(player, give)) {
                data.setDailyTicketsGranted(data.getDailyTicketsGranted() + give);
                ticketGiven = true;
            } else if (give > 0) {
                // NI failed — still count? Design: grant via NI. If fail, don't increment granted.
                plugin.getLogger().warning("Monthly login ticket give failed for " + player.getName());
            }
        }
        data.setMonthlyLastGrantDate(today);
        if (ticketGiven) {
            player.sendMessage(ChatColor.GREEN + "[月卡] 登录礼：余烬币 +" + monthlyLoginCoin + " · 日票 +1");
        } else {
            player.sendMessage(ChatColor.GREEN + "[月卡] 登录礼：余烬币 +" + monthlyLoginCoin + " · 日票已达硬顶未发放");
        }
    }

    public boolean isMonthlyActive(PlayerData data) {
        return isMonthlyActive(data, DailyService.today());
    }

    public boolean isMonthlyActive(PlayerData data, String today) {
        String exp = data.getMonthlyExpireDate();
        if (exp == null || exp.isEmpty()) return false;
        return compareDates(exp, today) >= 0;
    }

    public void cmdShowCash(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);
        grantFreeTicketsIfNeeded(player, data);
        if (data.isDirty()) dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.AQUA + "[晶钻] 余额：" + data.getCrystalCash());
        player.sendMessage(ChatColor.AQUA + "[晶钻] 今日日票 §f" + data.getDailyTicketsGranted()
                + "§7/" + hardCap + " §8（晶钻购 " + data.getDailyTicketsBought() + "/" + shopSkuLimit + "）");
    }

    public void cmdCashGive(org.bukkit.command.CommandSender sender, String playerName, int amount) {
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线: " + playerName);
            return;
        }
        if (amount == 0) {
            sender.sendMessage(ChatColor.RED + "数量不能为 0");
            return;
        }
        PlayerData d = dataStore.get(target.getUniqueId());
        d.addCrystalCash(amount);
        dataStore.flushMutation(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " 晶钻 ×" + amount
                + "（余额 " + d.getCrystalCash() + "）");
        target.sendMessage(ChatColor.AQUA + "[晶钻] 余额：" + d.getCrystalCash()
                + ChatColor.GRAY + "（变动 " + (amount > 0 ? "+" : "") + amount + "）");
    }

    public void cmdShopBuyDailyTicket(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[商城] 未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);
        grantFreeTicketsIfNeeded(player, data);

        if (data.getCrystalCash() < shopPrice) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + shopPrice + "）");
            return;
        }
        if (data.getDailyTicketsBought() >= shopSkuLimit) {
            player.sendMessage(ChatColor.RED + "[商城] 日票限购已满");
            return;
        }
        if (data.getDailyTicketsGranted() >= hardCap) {
            player.sendMessage(ChatColor.RED + "[商城] 已达日票硬顶 " + hardCap);
            return;
        }
        if (!data.takeCrystalCash(shopPrice)) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + shopPrice + "）");
            return;
        }
        if (shopGrantNi && !giveTickets(player, 1)) {
            data.addCrystalCash(shopPrice); // refund
            dataStore.flushMutation(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "[商城] 发放日票失败（NI），已退还晶钻");
            return;
        }
        data.setDailyTicketsBought(data.getDailyTicketsBought() + 1);
        data.setDailyTicketsGranted(data.getDailyTicketsGranted() + 1);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[商城] 已购买日票 ×1（-" + shopPrice + " 晶钻）");
    }

    public void cmdMonthlyShow(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);
        String today = DailyService.today();
        if (isMonthlyActive(data, today)) {
            data.setMonthlyCard(true);
            String granted = today.equals(data.getMonthlyLastGrantDate()) ? "已领" : "未领";
            player.sendMessage(ChatColor.YELLOW + "[月卡] 生效中 · 至 " + data.getMonthlyExpireDate());
            player.sendMessage(ChatColor.GRAY + "  今日登录礼：" + granted
                    + " · 每日 币" + monthlyLoginCoin + " + 日票×" + monthlyLoginTicket);
        } else {
            if (data.isMonthlyCard()) data.setMonthlyCard(false);
            player.sendMessage(ChatColor.GRAY + "[月卡] 未开通 · /corerpg monthly buy");
            player.sendMessage(ChatColor.DARK_GRAY + "  价 " + monthlyPrice + " 晶钻 · " + monthlyDurationDays + " 天");
        }
        if (data.isDirty()) dataStore.flushMutation(player.getUniqueId());
    }

    public void cmdMonthlyBuy(Player player) {
        if (!enabled || !monthlyEnabled) {
            player.sendMessage(ChatColor.RED + "[月卡] 未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);
        grantFreeTicketsIfNeeded(player, data);

        if (data.getCrystalCash() < monthlyPrice) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + monthlyPrice + "）");
            return;
        }
        if (!data.takeCrystalCash(monthlyPrice)) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + monthlyPrice + "）");
            return;
        }

        String today = DailyService.today();
        String base = today;
        String cur = data.getMonthlyExpireDate();
        if (cur != null && !cur.isEmpty() && compareDates(cur, today) >= 0) {
            base = cur;
        }
        String newExpire = plusDays(base, monthlyDurationDays);
        data.setMonthlyExpireDate(newExpire);
        data.setMonthlyCard(true);

        boolean ticketOk = false;
        if (monthlyImmediateTicket > 0 && data.getDailyTicketsGranted() < hardCap) {
            int give = Math.min(monthlyImmediateTicket, hardCap - data.getDailyTicketsGranted());
            if (give > 0 && giveTickets(player, give)) {
                data.setDailyTicketsGranted(data.getDailyTicketsGranted() + give);
                ticketOk = true;
            } else if (give > 0) {
                player.sendMessage(ChatColor.YELLOW + "[月卡] 日票发放失败（NI），卡已开通");
            }
        } else if (monthlyImmediateTicket > 0) {
            player.sendMessage(ChatColor.YELLOW + "[月卡] 日票已达硬顶未发放");
        }

        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[月卡] 已开通/续期至 " + newExpire
                + (ticketOk ? ChatColor.GRAY + " · 日票 +1" : ""));
    }

    private boolean giveTickets(Player player, int amount) {
        if (amount <= 0) return true;
        if (ni == null) return false;
        return ni.giveNiItem(player, ticketNiId, amount);
    }

    /** Compare yyyy-MM-dd strings; positive if a > b. Invalid dates sort as empty. */
    static int compareDates(String a, String b) {
        LocalDate da = parseDate(a);
        LocalDate db = parseDate(b);
        if (da == null && db == null) return 0;
        if (da == null) return -1;
        if (db == null) return 1;
        return da.compareTo(db);
    }

    static LocalDate parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    static String plusDays(String date, int days) {
        LocalDate d = parseDate(date);
        if (d == null) d = LocalDate.now(DailyService.zone());
        return d.plusDays(days).format(FMT);
    }
}
