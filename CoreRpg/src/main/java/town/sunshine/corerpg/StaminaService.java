package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * S0 余烬体力 — 日 0:00 Asia/Shanghai 回满；进本扣体力；旧票兑换/逾期折算。
 * 设计：docs/design-ember-stamina-dnf-daily.md §A
 */
public final class StaminaService implements Listener {

    public static final String POTION_NI_ID = "consumable_ember_stamina_30";
    public static final String POTION_NI_ID_45 = "consumable_ember_stamina_45";

    /** NI id → stamina grant; only these ids (no display-name match). */
    private static final Map<String, Integer> POTION_AMOUNTS;
    static {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        m.put(POTION_NI_ID, Integer.valueOf(30));
        m.put(POTION_NI_ID_45, Integer.valueOf(45));
        POTION_AMOUNTS = Collections.unmodifiableMap(m);
    }

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JavaPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;
    private CashService cashService;

    private int baseMax = 90;
    private int monthlyBonus = 30;
    private int bankCap = 30;
    private int potionDailyCap = 90;
    private int potionGrantAmount = 30;
    private String migrationT0 = "2026-09-28";
    private int migrationDays = 7;

    private final Map<String, Integer> costs = new LinkedHashMap<String, Integer>();
    private final Map<String, Integer> ticketConvert = new LinkedHashMap<String, Integer>();

    public StaminaService(JavaPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
        defaults();
    }

    public void setCashService(CashService cashService) {
        this.cashService = cashService;
    }

    private void defaults() {
        costs.clear();
        costs.put("daily", 30);
        costs.put("daily_ash", 30);
        costs.put("daily_crypt", 30);
        costs.put("weekly", 45);
        costs.put("elite", 40);
        costs.put("abyss", 30);
        costs.put("raid", 50);
        costs.put("calamity", 0);
        ticketConvert.clear();
        ticketConvert.put("ticket_ember_daily", 30);
        ticketConvert.put("ticket_ember_weekly", 45);
        ticketConvert.put("ticket_ember_abyss", 30);
        ticketConvert.put("ticket_ember_raid", 50);
        ticketConvert.put("ticket_ember_elite", 40);
    }

    public void reload() {
        defaults();
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
        ConfigurationSection st = cfg.getConfigurationSection("stamina");
        if (st == null) return;
        baseMax = st.getInt("base_max", 90);
        monthlyBonus = st.getInt("monthly_bonus", 30);
        bankCap = st.getInt("bank_cap", 30);
        potionDailyCap = st.getInt("potion_daily_cap", 90);
        potionGrantAmount = st.getInt("potion_grant_amount", 30);
        migrationT0 = st.getString("migration_t0", "2026-09-28");
        migrationDays = st.getInt("migration_days", 7);
        ConfigurationSection c = st.getConfigurationSection("costs");
        if (c != null) {
            for (String k : c.getKeys(false)) {
                costs.put(k.toLowerCase(), Integer.valueOf(c.getInt(k)));
            }
        }
        ConfigurationSection conv = st.getConfigurationSection("ticket_convert");
        if (conv != null) {
            ticketConvert.clear();
            for (String k : conv.getKeys(false)) {
                ticketConvert.put(k, Integer.valueOf(conv.getInt(k)));
            }
        }
    }

    public int getBaseMax() { return baseMax; }
    public int getMonthlyBonus() { return monthlyBonus; }
    public int getBankCap() { return bankCap; }
    public int getPotionDailyCap() { return potionDailyCap; }
    public int getPotionGrantAmount() { return potionGrantAmount; }
    public String getMigrationT0() { return migrationT0; }
    public int getMigrationDays() { return migrationDays; }

    public int costOf(String kind) {
        if (kind == null) return 0;
        String k = kind.toLowerCase();
        Integer v = costs.get(k);
        if (v == null && k.startsWith("daily_")) {
            v = costs.get("daily"); // S2 ash/crypt 与庭院同池 30
        }
        return v == null ? 0 : v.intValue();
    }

    public int costOf(TicketEntryService.Kind kind) {
        return kind == null ? 0 : costOf(kind.key);
    }

    public boolean isMonthlyActive(PlayerData data) {
        if (cashService != null) return cashService.isMonthlyActive(data);
        return data != null && data.isMonthlyCard();
    }

    public int resolveMax(PlayerData data) {
        int max = baseMax;
        if (isMonthlyActive(data)) max += monthlyBonus;
        return max;
    }

    /** Day cut + week credit refresh. Call before any read/consume. */
    public void ensure(PlayerData data) {
        if (data == null) return;
        String today = DailyService.today();
        int max = resolveMax(data);
        if (!today.equals(data.getStaminaResetDate())) {
            // Fill from bank first into current, then snap to max (overflow bank kept separately)
            int bank = data.getStaminaBank();
            int cur = data.isStaminaInitialized() ? data.getStamina() : 0;
            if (bank > 0 && cur < max) {
                int need = max - cur;
                int take = Math.min(need, bank);
                cur += take;
                bank -= take;
                data.setStaminaBank(bank);
            }
            // Daily reset: restore to max (design A.1). Bank overflow beyond max is kept.
            data.setStamina(max);
            data.setStaminaResetDate(today);
            data.setPotionStaminaToday(0);
            data.setStaminaInitialized(true);
        } else if (!data.isStaminaInitialized()) {
            data.setStamina(max);
            data.setStaminaInitialized(true);
        } else {
            // Cap may change when monthly activates mid-day — do not auto-fill, but clamp display max
            if (data.getStamina() > max + data.getStaminaBank()) {
                // nothing; stamina itself can briefly exceed if max dropped — clamp to max
            }
            if (data.getStamina() > max) {
                int overflow = data.getStamina() - max;
                data.setStamina(max);
                data.setStaminaBank(Math.min(bankCap, data.getStaminaBank() + overflow));
            }
        }
        ensureWeekCredits(data);
    }

    public void ensureWeekCredits(PlayerData data) {
        if (data == null) return;
        String week = DailyService.weekId();
        if (!week.equals(data.getWeeklyGrantCreditWeekId())) {
            data.setWeeklyGrantCreditWeekId(week);
            data.setWeeklyGrantCreditWeekly(1);
            data.setWeeklyGrantCreditElite(1);
            data.setWeeklyGrantCreditRaid(1);
        }
    }

    public int getStamina(PlayerData data) {
        ensure(data);
        return data.getStamina();
    }

    public int getMax(PlayerData data) {
        ensure(data);
        return resolveMax(data);
    }

    /**
     * Add stamina. Overflow goes to bank (capped). Returns amount actually applied to current+bank.
     */
    public int addStamina(PlayerData data, int amount, boolean toBankOverflow) {
        if (data == null || amount <= 0) return 0;
        ensure(data);
        int max = resolveMax(data);
        int cur = data.getStamina();
        int room = Math.max(0, max - cur);
        int intoCur = Math.min(room, amount);
        data.setStamina(cur + intoCur);
        int left = amount - intoCur;
        if (left > 0 && toBankOverflow) {
            int bankRoom = Math.max(0, bankCap - data.getStaminaBank());
            int intoBank = Math.min(bankRoom, left);
            data.setStaminaBank(data.getStaminaBank() + intoBank);
            left -= intoBank;
        }
        return amount - left;
    }

    /** Potion/shop path: respects potion_daily_cap. Returns false if capped (no mutation). */
    public boolean addPotionStamina(Player player, PlayerData data, int amount) {
        if (player == null || data == null || amount <= 0) return false;
        ensure(data);
        if (data.getPotionStaminaToday() + amount > potionDailyCap) {
            player.sendMessage(ChatColor.RED + "[体力] 今日药剂回体已达上限 " + potionDailyCap);
            return false;
        }
        int applied = addStamina(data, amount, true);
        data.setPotionStaminaToday(data.getPotionStaminaToday() + amount);
        dataStore.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "[体力] 回复 +" + applied
                + ChatColor.GRAY + "（当前 " + data.getStamina() + "/" + resolveMax(data)
                + (data.getStaminaBank() > 0 ? " · 银行 " + data.getStaminaBank() : "")
                + " · 药剂今日 " + data.getPotionStaminaToday() + "/" + potionDailyCap + "）");
        // Under-cap use always succeeds even if current+bank already full (amount still counted)
        return true;
    }

    /** Monthly login / direct grant: +30, overflow to bank. */
    public int grantMonthlyStamina(Player player, PlayerData data, int amount) {
        if (data == null || amount <= 0) return 0;
        ensure(data);
        int applied = addStamina(data, amount, true);
        if (player != null && applied > 0) {
            dataStore.flushMutation(player.getUniqueId());
        }
        return applied;
    }

    public static final class ConsumeResult {
        public final boolean ok;
        public final boolean usedCredit;
        public final int cost;
        public final String failMessage;
        public ConsumeResult(boolean ok, boolean usedCredit, int cost, String failMessage) {
            this.ok = ok;
            this.usedCredit = usedCredit;
            this.cost = cost;
            this.failMessage = failMessage;
        }
    }

    /**
     * Validate + consume for dungeon enter. Weekly/elite/raid first free uses weekly_grant_credit.
     */
    public ConsumeResult consumeForEnter(Player player, TicketEntryService.Kind kind) {
        if (player == null || kind == null) {
            return new ConsumeResult(false, false, 0, "无效");
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        int cost = costOf(kind);
        // weekly grant credit (account, not NI)
        if (kind == TicketEntryService.Kind.WEEKLY && data.getWeeklyGrantCreditWeekly() > 0) {
            data.setWeeklyGrantCreditWeekly(data.getWeeklyGrantCreditWeekly() - 1);
            dataStore.flushMutation(player.getUniqueId());
            return new ConsumeResult(true, true, 0, null);
        }
        if (kind == TicketEntryService.Kind.ELITE && data.getWeeklyGrantCreditElite() > 0) {
            data.setWeeklyGrantCreditElite(data.getWeeklyGrantCreditElite() - 1);
            dataStore.flushMutation(player.getUniqueId());
            return new ConsumeResult(true, true, 0, null);
        }
        if (kind == TicketEntryService.Kind.RAID && data.getWeeklyGrantCreditRaid() > 0) {
            data.setWeeklyGrantCreditRaid(data.getWeeklyGrantCreditRaid() - 1);
            dataStore.flushMutation(player.getUniqueId());
            return new ConsumeResult(true, true, 0, null);
        }
        if (cost <= 0) {
            return new ConsumeResult(true, false, 0, null);
        }
        if (data.getStamina() < cost) {
            return new ConsumeResult(false, false, cost,
                    ChatColor.RED + "体力不足（需 " + cost + "，当前 " + data.getStamina()
                            + "/" + resolveMax(data) + "）· 明日 0 点恢复");
        }
        data.setStamina(data.getStamina() - cost);
        dataStore.flushMutation(player.getUniqueId());
        return new ConsumeResult(true, false, cost, null);
    }

    public void refundEnter(Player player, TicketEntryService.Kind kind, ConsumeResult prior) {
        if (player == null || kind == null || prior == null || !prior.ok) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        if (prior.usedCredit) {
            if (kind == TicketEntryService.Kind.WEEKLY) {
                data.setWeeklyGrantCreditWeekly(data.getWeeklyGrantCreditWeekly() + 1);
            } else if (kind == TicketEntryService.Kind.ELITE) {
                data.setWeeklyGrantCreditElite(data.getWeeklyGrantCreditElite() + 1);
            } else if (kind == TicketEntryService.Kind.RAID) {
                data.setWeeklyGrantCreditRaid(data.getWeeklyGrantCreditRaid() + 1);
            }
        } else if (prior.cost > 0) {
            data.setStamina(data.getStamina() + prior.cost);
        }
        dataStore.flushMutation(player.getUniqueId());
    }

    /** Convert all old tickets in inventory. Returns stamina gained. */
    public int convertInventoryTickets(Player player, boolean announce) {
        if (player == null || ni == null) return 0;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        int total = 0;
        int tickets = 0;
        for (Map.Entry<String, Integer> e : ticketConvert.entrySet()) {
            String id = e.getKey();
            int per = e.getValue().intValue();
            int have = ni.countInInventory(player, id);
            if (have <= 0) continue;
            if (!ni.consumeExact(player, id, have)) continue;
            total += per * have;
            tickets += have;
        }
        if (total > 0) {
            addStamina(data, total, true);
            dataStore.flushMutation(player.getUniqueId());
            if (announce) {
                player.sendMessage(ChatColor.GREEN + "[体力] 旧日票已折算为体力 +" + total
                        + ChatColor.GRAY + "（" + tickets + " 张）· 当前 "
                        + data.getStamina() + "/" + resolveMax(data));
            }
        }
        return total;
    }

    public boolean isAutoConvertDue() {
        try {
            LocalDate t0 = LocalDate.parse(migrationT0, DAY);
            LocalDate deadline = t0.plusDays(migrationDays);
            return !LocalDate.now(DailyService.zone()).isBefore(deadline);
        } catch (Exception e) {
            return false;
        }
    }

    /** Join: ensure stamina + optional overdue auto-convert. */
    public void onJoin(Player player) {
        if (player == null) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        dataStore.flushMutation(player.getUniqueId());
        if (isAutoConvertDue()) {
            int gained = convertInventoryTickets(player, false);
            if (gained > 0) {
                player.sendMessage(ChatColor.YELLOW + "旧日票已折算为体力");
            }
        }
    }

    /** Grant potion NI or fallback direct stamina (shop). NI path does NOT pre-count potionStaminaToday. */
    public boolean grantPotionOrStamina(Player player, int packs) {
        if (player == null || packs <= 0) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        int totalAmt = packs * potionGrantAmount;
        // Prefer NI item if present — day cap counted on actual use
        if (ni != null && ni.giveNiItem(player, POTION_NI_ID, packs)) {
            player.sendMessage(ChatColor.GREEN + "[体力] 获得体力药 ×" + packs
                    + ChatColor.GRAY + "（使用后 +" + potionGrantAmount + "/瓶）");
            return true;
        }
        // Fallback: direct +stamina (物品岗未上架时) — still respects potion_daily_cap
        return addPotionStamina(player, data, totalAmt);
    }

    /** Give stamina potion NI by id (no pre-count). Unknown id → false. */
    public boolean grantPotionNi(Player player, String niId, int packs) {
        if (player == null || niId == null || packs <= 0) return false;
        if (!POTION_AMOUNTS.containsKey(niId)) return false;
        if (ni == null || !ni.giveNiItem(player, niId, packs)) return false;
        int per = POTION_AMOUNTS.get(niId).intValue();
        player.sendMessage(ChatColor.GREEN + "[体力] 获得体力药 ×" + packs
                + ChatColor.GRAY + "（使用后 +" + per + "/瓶）");
        return true;
    }

    /**
     * Apply potion stamina from a consumed NI bottle. Returns false if over potion_daily_cap
     * (caller must not consume the item). Counts full {@code amount} toward the day cap.
     */
    public boolean tryUsePotion(Player player, int amount) {
        if (player == null || amount <= 0) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        return addPotionStamina(player, data, amount);
    }

    /** Resolve stamina amount for a potion NI id, or -1 if not a stamina potion. */
    public int potionAmountOf(String niId) {
        if (niId == null) return -1;
        Integer v = POTION_AMOUNTS.get(niId);
        return v == null ? -1 : v.intValue();
    }

    /** Drink / consume stamina potion NI — id only, no display-name match. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPotionConsume(PlayerItemConsumeEvent e) {
        if (ni == null) return;
        ItemStack stack = e.getItem();
        if (stack == null || stack.getType() == Material.AIR) return;
        String id = ni.getNiId(stack);
        int amount = potionAmountOf(id);
        if (amount <= 0) return;
        final Player p = e.getPlayer();
        PlayerData data = dataStore.get(p.getUniqueId());
        ensure(data);
        if (data.getPotionStaminaToday() + amount > potionDailyCap) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "[体力] 今日药剂回体已达上限 " + potionDailyCap);
            return;
        }
        // Allow vanilla consume (1 bottle), then apply stamina + strip leftover glass bottle
        final boolean bottle = stack.getType() == Material.POTION;
        if (!addPotionStamina(p, data, amount)) {
            // Cap raced or apply failed — keep bottle
            e.setCancelled(true);
            return;
        }
        if (!bottle) return;
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) return;
                ItemStack[] inv = p.getInventory().getContents();
                for (int i = 0; i < inv.length; i++) {
                    ItemStack it = inv[i];
                    if (it == null || it.getType() != Material.GLASS_BOTTLE || ni.getNiId(it) != null) continue;
                    if (it.getAmount() > 1) it.setAmount(it.getAmount() - 1);
                    else p.getInventory().setItem(i, null);
                    break;
                }
            }
        });
    }

    /** Special reward key handling for mail/quest: stamina / consumable_ember_stamina_30|_45 */
    public boolean tryGrantRewardKey(Player player, String key, int amount) {
        if (player == null || key == null || amount <= 0) return false;
        PlayerData data = dataStore.get(player.getUniqueId());
        if ("stamina".equalsIgnoreCase(key) || "余烬体力".equals(key)) {
            int applied = addStamina(data, amount, true);
            dataStore.flushMutation(player.getUniqueId());
            return applied > 0;
        }
        if (POTION_NI_ID.equalsIgnoreCase(key)) {
            return grantPotionOrStamina(player, amount);
        }
        if (POTION_NI_ID_45.equalsIgnoreCase(key)) {
            return grantPotionNi(player, POTION_NI_ID_45, amount);
        }
        return false;
    }

    public boolean cmdRoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && (args.length < 2 || !"set".equalsIgnoreCase(args[1]) && !"give".equalsIgnoreCase(args[1]))) {
            sender.sendMessage("玩家专用，或 stamina set|give <player> <n>");
            return true;
        }
        if (args.length < 2) {
            if (sender instanceof Player) {
                show((Player) sender);
            } else {
                sender.sendMessage("/corerpg stamina <convert|show|set|give>");
            }
            return true;
        }
        String sub = args[1].toLowerCase();
        if ("convert".equals(sub) || "兑换".equals(sub)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("玩家专用");
                return true;
            }
            Player p = (Player) sender;
            int gained = convertInventoryTickets(p, true);
            if (gained <= 0) {
                p.sendMessage(ChatColor.GRAY + "[体力] 背包中没有可兑换的旧票");
            }
            return true;
        }
        if ("show".equals(sub) || "info".equals(sub)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("玩家专用");
                return true;
            }
            show((Player) sender);
            return true;
        }
        if ("set".equals(sub) || "give".equals(sub)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            if (args.length < 4) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg stamina " + sub + " <player> <amount>");
                return true;
            }
            Player target = org.bukkit.Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "玩家不在线");
                return true;
            }
            int amt;
            try { amt = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数量无效");
                return true;
            }
            PlayerData data = dataStore.get(target.getUniqueId());
            ensure(data);
            if ("set".equals(sub)) {
                data.setStamina(Math.max(0, amt));
            } else {
                addStamina(data, amt, true);
            }
            dataStore.flushMutation(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "[体力] " + target.getName() + " → "
                    + data.getStamina() + "/" + resolveMax(data));
            return true;
        }
        if (sender instanceof Player) show((Player) sender);
        else sender.sendMessage("/corerpg stamina convert|show|set|give");
        return true;
    }

    public void show(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        ensure(data);
        player.sendMessage(ChatColor.GOLD + "[体力] "
                + ChatColor.WHITE + data.getStamina() + ChatColor.GRAY + "/"
                + ChatColor.WHITE + resolveMax(data)
                + ChatColor.DARK_GRAY + " · 银行 " + data.getStaminaBank()
                + " · 药剂今日 " + data.getPotionStaminaToday() + "/" + potionDailyCap);
        player.sendMessage(ChatColor.GRAY + "  消耗 日常" + costOf("daily")
                + " · 周" + costOf("weekly")
                + " · 精英" + costOf("elite")
                + " · 深渊" + costOf("abyss")
                + " · 团" + costOf("raid")
                + " · 灾厄奔赴" + costOf("calamity"));
        player.sendMessage(ChatColor.GRAY + "  本周免费抵扣 周本×" + data.getWeeklyGrantCreditWeekly()
                + " · 精英×" + data.getWeeklyGrantCreditElite()
                + " · 团×" + data.getWeeklyGrantCreditRaid());
    }
}
