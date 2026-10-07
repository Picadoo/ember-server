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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Crystal cash shop + monthly card (docs/design/DESIGN-ember-cash-monthly.md). */
public final class CashService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;
    private TicketGrantService ticketGrantService;

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
    private int passUnlockPrice = 48;
    private String passUnlockMailTemplate = "pass_track_paid_welcome";

    // weekly ticket shop
    private int weeklyShopPrice = 180;
    private int weeklyShopSkuLimit = 1;
    private int weeklyHardCap = 2;
    private boolean weeklyShopGrantNi = true;
    private String weeklyTicketNiId = "ticket_ember_weekly";

    // light VIP stub
    private boolean vipEnabled = true;
    private int vipDailyClaimCoin = 120;
    /** 1.5.0: tier 0 (no 勋阶) gets only a token amount. */
    private int vipTier0ClaimCoin = 20;

    public CashService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void setTicketGrantService(TicketGrantService ticketGrantService) {
        this.ticketGrantService = ticketGrantService;
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
        if (ni != null) {
            ni.warnMissingOnceIfAbsent(ticketNiId);
            ni.warnMissingOnceIfAbsent(weeklyTicketNiId);
        }
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
        ConfigurationSection passUnlock = cfg.getConfigurationSection("shop.pass_unlock");
        if (passUnlock != null) {
            passUnlockPrice = passUnlock.getInt("price", 48);
            String tpl = passUnlock.getString("mail_template", "pass_track_paid_welcome");
            passUnlockMailTemplate = (tpl == null || tpl.isEmpty()) ? "pass_track_paid_welcome" : tpl;
        } else {
            passUnlockPrice = 48;
            passUnlockMailTemplate = "pass_track_paid_welcome";
        }
        ConfigurationSection weeklyShop = cfg.getConfigurationSection("shop.weekly_ticket");
        if (weeklyShop != null) {
            weeklyShopPrice = weeklyShop.getInt("price", 180);
            if (weeklyShop.contains("sku_limit")) {
                weeklyShopSkuLimit = weeklyShop.getInt("sku_limit", 1);
            } else {
                weeklyShopSkuLimit = weeklyShop.getInt("sku_limit_per_week", 1);
            }
            weeklyHardCap = weeklyShop.getInt("hard_cap", 2);
            weeklyShopGrantNi = weeklyShop.getBoolean("grant_ni", true);
            String wNi = weeklyShop.getString("ticket_ni_id", null);
            if (wNi == null || wNi.isEmpty()) {
                wNi = cfg.getString("weekly.ticket_ni_id", "ticket_ember_weekly");
            }
            weeklyTicketNiId = (wNi == null || wNi.isEmpty()) ? "ticket_ember_weekly" : wNi;
        } else {
            weeklyShopPrice = 180;
            weeklyShopSkuLimit = 1;
            weeklyHardCap = 2;
            weeklyShopGrantNi = true;
            weeklyTicketNiId = cfg.getString("weekly.ticket_ni_id", "ticket_ember_weekly");
            if (weeklyTicketNiId == null || weeklyTicketNiId.isEmpty()) {
                weeklyTicketNiId = "ticket_ember_weekly";
            }
        }

        ConfigurationSection vip = cfg.getConfigurationSection("vip");
        if (vip != null) {
            vipEnabled = vip.getBoolean("enabled", true);
            vipDailyClaimCoin = vip.getInt("daily_claim_coin", 120);
            vipTier0ClaimCoin = vip.getInt("tier0_daily_claim_coin", 20);
        } else {
            vipEnabled = true;
            vipDailyClaimCoin = 120;
            vipTier0ClaimCoin = 20;
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
    public int getWeeklyShopPrice() { return weeklyShopPrice; }
    public int getWeeklyShopSkuLimit() { return weeklyShopSkuLimit; }
    public int getWeeklyHardCap() { return weeklyHardCap; }
    public String getWeeklyTicketNiId() { return weeklyTicketNiId; }

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

    /**
     * Weekly shop counter reset when weekId changes.
     * If free weekly already granted this week (weeklyTicketGrantWeekId), count free toward granted.
     */
    public void ensureWeeklyTicketWeek(PlayerData data) {
        if (data == null) return;
        String week = DailyService.weekId();
        if (!week.equals(data.getWeeklyTicketShopWeekId())) {
            data.setWeeklyTicketsBought(0);
            data.setWeeklyTicketsGranted(0);
            data.setWeeklyTicketShopWeekId(week);
        }
        if (week.equals(data.getWeeklyTicketGrantWeekId())) {
            int free = 1;
            if (ticketGrantService != null) {
                free = Math.max(0, ticketGrantService.getWeeklyFreeTickets());
            }
            if (free > 0 && data.getWeeklyTicketsGranted() < free) {
                data.setWeeklyTicketsGranted(free);
            }
        }
    }

    /** Join path: free daily → monthly → weekly → abyss. Retries at +20t/+60t if NI not ready. */
    public void onJoin(Player player) {
        if (!enabled || player == null) return;
        runJoinGrants(player, true);
        scheduleJoinRetries(player);
    }

    /**
     * Idempotent grant pass. When {@code announce} is true, send one [门票] line for newly given free tickets.
     * @return true if any free ticket grant still pending (NI likely not ready)
     */
    public boolean runJoinGrants(Player player, boolean announce) {
        if (!enabled || player == null || !player.isOnline()) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureCashDay(data);

        grantFreeTicketsIfNeeded(player, data);
        processMonthlyLogin(player, data);
        if (ticketGrantService != null) {
            ticketGrantService.grantOnJoin(player, data);
        }
        if (data.isDirty()) dataStore.flushMutation(player.getUniqueId());
        return false; // S0: no NI ticket retries
    }

    private boolean needsRetry(PlayerData data) {
        if (!data.isDailyFreeGranted() && freeTickets > 0) return true;
        if (ticketGrantService != null) {
            if (ticketGrantService.needsWeeklyGrant(data)) return true;
            if (ticketGrantService.needsAbyssGrant(data)) return true;
            if (ticketGrantService.needsRaidGrant(data)) return true;
            if (ticketGrantService.needsEliteGrant(data)) return true;
        }
        return false;
    }

    /** Always schedule +20t/+60t so late NI load can complete free daily/weekly/abyss grants. */
    private void scheduleJoinRetries(final Player player) {
        if (player == null) return;
        final UUID uuid = player.getUniqueId();
        Runnable retry = new Runnable() {
            @Override public void run() {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null || !p.isOnline()) return;
                runJoinGrants(p, true);
            }
        };
        Bukkit.getScheduler().runTaskLater(plugin, retry, 20L);
        Bukkit.getScheduler().runTaskLater(plugin, retry, 60L);
    }

    /**
     * @return amount newly given this call (0 if already granted, capped, or NI failed)
     */
    /** S0: 停发日票；日体力由 StaminaService 0:00 回满。 */
    public int grantFreeTicketsIfNeeded(Player player, PlayerData data) {
        if (!data.isDailyFreeGranted()) {
            data.setDailyFreeGranted(true);
        }
        return 0;
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
        // D282: monthly login gifts stay on under P1 (owner: 月卡照常发). Vip/pass claim still gated below.

        data.addCoin(monthlyLoginCoin);
        int staminaAmt = 0;
        if (monthlyLoginTicket > 0 && plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) {
                // design A.5: 每日登录 币200 + 体力+30（溢出进 bank）
                staminaAmt = st.grantMonthlyStamina(player, data, monthlyLoginTicket * 30);
            }
        }
        data.setMonthlyLastGrantDate(today);
        if (staminaAmt > 0) {
            player.sendMessage(ChatColor.GREEN + "[月卡] 登录礼：余烬币 +" + monthlyLoginCoin + " · 体力 +" + staminaAmt);
        } else {
            player.sendMessage(ChatColor.GREEN + "[月卡] 登录礼：余烬币 +" + monthlyLoginCoin);
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
        if (plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) {
                st.ensure(data);
                player.sendMessage(ChatColor.AQUA + "[体力] §f" + data.getStamina() + "§7/" + st.resolveMax(data)
                        + " §8（药剂今日 " + data.getPotionStaminaToday() + "/" + st.getPotionDailyCap()
                        + " · 购 " + data.getDailyTicketsBought() + "/" + shopSkuLimit + "）");
                return;
            }
        }
        player.sendMessage(ChatColor.AQUA + "[晶钻] 今日体力药购 §f" + data.getDailyTicketsBought()
                + "§7/" + shopSkuLimit);
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
            player.sendMessage(ChatColor.RED + "[商城] 体力药限购已满（日 " + shopSkuLimit + "）");
            return;
        }
        // potion_daily_cap is enforced on drink/use — do not gate purchase or pre-count
        StaminaService st = plugin instanceof CoreRpgPlugin ? ((CoreRpgPlugin) plugin).getStaminaService() : null;
        if (!data.takeCrystalCash(shopPrice)) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + shopPrice + "）");
            return;
        }
        boolean ok = st != null && st.grantPotionOrStamina(player, 1);
        if (!ok) {
            data.addCrystalCash(shopPrice);
            dataStore.flushMutation(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "[商城] 发放体力药失败，已退还晶钻");
            return;
        }
        data.setDailyTicketsBought(data.getDailyTicketsBought() + 1);
        data.setDailyTicketsGranted(data.getDailyTicketsGranted() + 1);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[商城] 已购买体力药 ×1（-" + shopPrice + " 晶钻）");
    }

    public void cmdShopBuyPassUnlock(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[商城] 未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (plugin instanceof CoreRpgPlugin && ((CoreRpgPlugin) plugin).getProgressService() != null) {
            ((CoreRpgPlugin) plugin).getProgressService().ensureSeason(data);
        }
        if (data.isSeasonPassPaid()) {
            player.sendMessage(ChatColor.YELLOW + "[战令] 付费轨已开通，无需重复购买");
            return;
        }
        if (data.getCrystalCash() < passUnlockPrice) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + passUnlockPrice + "）");
            return;
        }
        if (!data.takeCrystalCash(passUnlockPrice)) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + passUnlockPrice + "）");
            return;
        }
        data.setSeasonPassPaid(true);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[商城] 已开通战令付费轨（-" + passUnlockPrice + " 晶钻）");

        boolean delivered = false;
        if (plugin instanceof CoreRpgPlugin) {
            MailService mail = ((CoreRpgPlugin) plugin).getMailService();
            if (mail != null) {
                delivered = mail.deliverByTemplateId(player.getUniqueId(), passUnlockMailTemplate);
            }
        }
        if (delivered) {
            player.sendMessage(ChatColor.GREEN + "[战令] 欢迎礼已寄出，请到邮箱领取");
            return;
        }
        // Template missing/fail: grant the same attachments, keep paid flag.
        data.addCoin(300);
        boolean staminaOk = false;
        if (plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) staminaOk = st.grantPotionOrStamina(player, 1);
        }
        dataStore.flushMutation(player.getUniqueId());
        if (staminaOk) {
            player.sendMessage(ChatColor.GREEN + "[战令] 欢迎礼已发放：余烬币 +300 · 体力药 ×1");
        } else {
            player.sendMessage(ChatColor.GREEN + "[战令] 欢迎礼已发放：余烬币 +300");
        }
    }

    public void cmdShopBuyWeeklyTicket(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[商城] 未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        ensureWeeklyTicketWeek(data);

        if (data.getWeeklyTicketsBought() >= weeklyShopSkuLimit) {
            player.sendMessage(ChatColor.RED + "[商城] 周体力包限购已满");
            return;
        }
        if (data.getWeeklyTicketsGranted() >= weeklyHardCap) {
            player.sendMessage(ChatColor.RED + "[商城] 已达周体力包硬顶 " + weeklyHardCap);
            return;
        }
        if (data.getCrystalCash() < weeklyShopPrice) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + weeklyShopPrice + "）");
            return;
        }
        if (!data.takeCrystalCash(weeklyShopPrice)) {
            player.sendMessage(ChatColor.RED + "[商城] 晶钻不足（需 " + weeklyShopPrice + "）");
            return;
        }
        StaminaService st = plugin instanceof CoreRpgPlugin ? ((CoreRpgPlugin) plugin).getStaminaService() : null;
        int applied = 0;
        if (st != null) {
            applied = st.addStamina(data, 45, true);
        }
        if (applied <= 0) {
            data.addCrystalCash(weeklyShopPrice);
            dataStore.flushMutation(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "[商城] 发放周体力失败，已退还晶钻");
            return;
        }
        data.setWeeklyTicketsBought(data.getWeeklyTicketsBought() + 1);
        data.setWeeklyTicketsGranted(data.getWeeklyTicketsGranted() + 1);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[商城] 已购买周体力 +" + applied + "（-" + weeklyShopPrice + " 晶钻）");
    }

    public void cmdVipShow(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        int tier = data.getVipTier();
        ProgressService ps = plugin instanceof CoreRpgPlugin ? ((CoreRpgPlugin) plugin).getProgressService() : null;
        String tierLabel = tier <= 0 ? "无（0）" : (ps != null ? ps.tierName(tier) : "勋阶 " + tier);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[勋阶] 当前：" + ChatColor.WHITE + tierLabel);
        if (ps != null) {
            int next = ps.nextThreshold(tier);
            player.sendMessage(ChatColor.GRAY + "  累计充值晶钻 " + data.getVipToppedUp()
                    + (next > 0 ? " · 下一阶需 " + next : " · 已满阶")
                    + " · 特权：日礼币、余烬经验 +" + (tier * ps.getVipXpBonusPerTier()) + "%、仓库加格、专属称号");
        }
        if (!vipEnabled) {
            player.sendMessage(ChatColor.DARK_GRAY + "  日礼未启用");
            return;
        }
        String today = DailyService.today();
        boolean claimed = today.equals(data.getVipDailyClaimDate());
        player.sendMessage(ChatColor.GRAY + "  日礼币 " + (tier <= 0 ? vipTier0ClaimCoin : vipDailyClaimCoin)
                + " · 今日：" + (claimed ? ChatColor.GREEN + "已领" : ChatColor.YELLOW + "未领 /corerpg vip claim"));
    }

    public void cmdVipClaim(Player player) {
        if (!vipEnabled) {
            player.sendMessage(ChatColor.RED + "[勋阶] 日礼未启用");
            return;
        }
        if (CashCoinRules.p1BlocksCoin(town.sunshine.corerpg.p1.EmberMode.active())) {
            player.sendMessage(ChatColor.GRAY + "[勋阶] P1 模式下不发余烬币日礼（请用主线 / 挂机庭）。");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        String today = DailyService.today();
        if (today.equals(data.getVipDailyClaimDate())) {
            player.sendMessage(ChatColor.YELLOW + "[勋阶] 今日日礼已领取");
            return;
        }
        int coin = Math.max(0, data.getVipTier() <= 0 ? vipTier0ClaimCoin : vipDailyClaimCoin);
        if (coin > 0) data.addCoin(coin);
        data.setVipDailyClaimDate(today);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[勋阶] 日礼已领取：余烬币 +" + coin
                + ChatColor.GRAY + "（勋阶 " + data.getVipTier() + "）");
    }

    /** 1.4.10: free pass-track supply (mail template pass_track_free), once per day. */
    public void cmdPassFree(Player player) {
        if (CashCoinRules.p1BlocksCoin(town.sunshine.corerpg.p1.EmberMode.active())) {
            player.sendMessage(ChatColor.GRAY + "[战令·免费轨] P1 模式下不发旧战令补给（请用主线 / 签到）。");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        String today = DailyService.today();
        if (today.equals(data.getPassFreeClaimDate())) {
            player.sendMessage(ChatColor.YELLOW + "[战令·免费轨] 今日补给已领取，明日再来");
            return;
        }
        boolean delivered = false;
        if (plugin instanceof CoreRpgPlugin) {
            MailService mail = ((CoreRpgPlugin) plugin).getMailService();
            if (mail != null) delivered = mail.deliverByTemplateId(player.getUniqueId(), "pass_track_free");
        }
        if (!delivered) {
            player.sendMessage(ChatColor.RED + "[战令·免费轨] 发放失败（邮件模板缺失或收件箱已满），请稍后再试");
            return;
        }
        data.setPassFreeClaimDate(today);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[战令·免费轨] 今日补给已寄出（每日 1 次）");
    }

    public void cmdPassShow(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        if (plugin instanceof CoreRpgPlugin && ((CoreRpgPlugin) plugin).getProgressService() != null) {
            ((CoreRpgPlugin) plugin).getProgressService().ensureSeason(data);
        }
        if (town.sunshine.corerpg.p1.EmberMode.active() && !player.hasPermission("corerpg.admin")) {
            // D201 (AUDIT S0-3/S0-4 follow-up): while P1 is on, pass free/claim are refused and legacy pass XP pays 0 —
            // don't advertise /corerpg pass free or the legacy XP sources; point at the P1 menu instead.
            player.sendMessage(ChatColor.AQUA + "[战令] " + ChatColor.GRAY + "P1 模式下旧战令已停用（不再累积、不再领取），"
                    + "每日奖励请用 /ember 主菜单的签到与在线奖励。");
            return;
        }
        boolean claimed = DailyService.today().equals(data.getPassFreeClaimDate());
        player.sendMessage(ChatColor.AQUA + "[战令] 付费轨：" + (data.isSeasonPassPaid() ? ChatColor.GREEN + "已开通" : ChatColor.GRAY + "未开通")
                + ChatColor.GRAY + " · 免费轨今日补给：" + (claimed ? ChatColor.GREEN + "已领" : ChatColor.YELLOW + "未领 /corerpg pass free"));
        if (plugin instanceof CoreRpgPlugin && ((CoreRpgPlugin) plugin).getProgressService() != null) {
            player.sendMessage(ChatColor.AQUA + "[战令] " + ChatColor.WHITE + ((CoreRpgPlugin) plugin).getProgressService().passLine(data)
                    + ChatColor.GRAY + "（签到 / 日本 / 周本 / 深渊 / 团本 / 盟Boss 通关 / 悬赏获得）");
        }
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
                    + " · 每日 币" + monthlyLoginCoin + " + 体力+" + (monthlyLoginTicket * 30));
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

        int staminaOk = 0;
        if (monthlyImmediateTicket > 0 && plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) {
                staminaOk = st.grantMonthlyStamina(player, data, monthlyImmediateTicket * 30);
            }
        }

        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[月卡] 已开通/续期至 " + newExpire
                + (staminaOk > 0 ? ChatColor.GRAY + " · 体力 +" + staminaOk : ""));
    }

    private boolean giveTickets(Player player, int amount) {
        if (amount <= 0) return true;
        if (ni == null) return false;
        return ni.giveNiItem(player, ticketNiId, amount);
    }

    private boolean giveWeeklyTickets(Player player, int amount) {
        if (amount <= 0) return true;
        if (ni == null) return false;
        return ni.giveNiItem(player, weeklyTicketNiId, amount);
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
