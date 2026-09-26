package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/** Ember RPG: coin / sign / activity / bounty + scoreboard / spawn. */
public final class CoreRpgPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Scoreboard> boards = new HashMap<UUID, Scoreboard>();
    private final Random random = new Random();
    private final List<BountyDef> bountyDefs = new ArrayList<BountyDef>();
    private final Map<Integer, Integer> chestRewards = new HashMap<Integer, Integer>();

    private PlayerDataStore dataStore;
    private String shardNeedle = "余烬碎片";
    private String dustNeedle = "余烬骨尘";
    private String crystalNeedle = "余烬附魔晶";
    private String boardTitle = ChatColor.GOLD + "余烬进度";
    private boolean boardEnabled = true;
    private String spawnMob = "EmberCryptZombie";
    private int spawnAmount = 1;
    private String spawnTemplate = "mm m spawn {mob} {amount} {world},{x},{y},{z}";
    private String coinName = "余烬币";
    private boolean papiRegistered;

    private int signReward = 100;
    private int signActivity = 10;
    private int coinKillReward = 1;
    private int actKillPoints = 1;
    private int actKillCap = 15;
    private int actOnlineInterval = 4;
    private int actOnlinePoints = 5;
    private int actOnlineCap = 15;
    private int actBountyComplete = 15;

    private NiBridge niBridge;
    private EnhanceService enhanceService;
    private CalamityService calamityService;
    private CovenantService covenantService;
    private TalentService talentService;
    private CashService cashService;
    private ScrapService scrapService;
    private MailService mailService;
    private FriendService friendService;
    private LadderService ladderService;
    private PetService petService;

    static final class BountyDef {
        final String id;
        final String name;
        final int killTarget;
        final int rewardCoin;
        BountyDef(String id, String name, int killTarget, int rewardCoin) {
            this.id = id; this.name = name; this.killTarget = killTarget; this.rewardCoin = rewardCoin;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        dataStore = new PlayerDataStore(this);
        niBridge = new NiBridge(this);
        enhanceService = new EnhanceService(this, niBridge);
        calamityService = new CalamityService(this);
        covenantService = new CovenantService(this, niBridge, dataStore);
        talentService = new TalentService(this, niBridge, dataStore);
        cashService = new CashService(this, niBridge, dataStore);
        scrapService = new ScrapService(this, niBridge);
        mailService = new MailService(this, niBridge, dataStore);
        friendService = new FriendService(this, dataStore);
        ladderService = new LadderService(this, dataStore);
        petService = new PetService(this, dataStore, niBridge);
        dataStore.setTalentService(talentService);
        reloadLocal();
        Bukkit.getPluginManager().registerEvents(this, this);
        if (petService != null) {
            petService.reload();
            petService.start();
        }
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    ensureBounty(p);
                    refreshBoard(p);
                }
            }
        }, 40L, 40L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) tickOnlineMinute(p);
            }
        }, 1200L, 1200L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() { dataStore.saveAll(); }
        }, 6000L, 6000L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                if (calamityService != null) calamityService.tick();
            }
        }, 20L, 1200L);
        if (ladderService != null) {
            ladderService.reload();
            ladderService.refresh();
            long period = Math.max(200L, ladderService.getRefreshSeconds() * 20L);
            Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                @Override public void run() {
                    if (ladderService != null) ladderService.refresh();
                }
            }, period, period);
        }
        registerPapi();
        getLogger().info("CoreRpg 1.3.5 enabled (coin/sign/activity/bounty/enhance/socket/scrap/reforge/calamity/covenant/talent/cash/shop/monthly/mail/friend/settings/ladder/pet).");
    }

    @Override
    public void onDisable() {
        if (petService != null) petService.shutdown();
        if (dataStore != null) dataStore.saveAll();
    }

    public PlayerDataStore getDataStore() { return dataStore; }
    public CalamityService getCalamityService() { return calamityService; }
    public NiBridge getNiBridge() { return niBridge; }
    public CovenantService getCovenantService() { return covenantService; }
    public TalentService getTalentService() { return talentService; }
    public CashService getCashService() { return cashService; }
    public ScrapService getScrapService() { return scrapService; }
    public MailService getMailService() { return mailService; }
    public FriendService getFriendService() { return friendService; }
    public LadderService getLadderService() { return ladderService; }
    public PetService getPetService() { return petService; }
    public EnhanceService getEnhanceService() { return enhanceService; }

    private void registerPapi() {
        papiRegistered = false;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;
        try {
            CoreRpgExpansion expansion = new CoreRpgExpansion(this);
            if (expansion.register()) papiRegistered = true;
        } catch (Throwable t) {
            getLogger().warning("PAPI expansion failed: " + t.getMessage());
        }
        try {
            EmberLadderExpansion ember = new EmberLadderExpansion(this);
            if (ember.register()) {
                getLogger().info("PAPI expansion registered: ember (ladder)");
            }
        } catch (Throwable t) {
            getLogger().warning("PAPI ember expansion failed: " + t.getMessage());
        }
    }

    private void reloadLocal() {
        reloadConfig();
        shardNeedle = getConfig().getString("shard_name_contains", "余烬碎片");
        dustNeedle = getConfig().getString("dust_name_contains", "余烬骨尘");
        crystalNeedle = getConfig().getString("crystal_name_contains", "余烬附魔晶");
        boardTitle = color(getConfig().getString("scoreboard.title", "§6余烬进度"));
        boardEnabled = getConfig().getBoolean("scoreboard.enabled", true);
        spawnMob = getConfig().getString("spawn.mob", "EmberCryptZombie");
        spawnAmount = getConfig().getInt("spawn.amount", 1);
        spawnTemplate = getConfig().getString("spawn.command_template",
                "mm m spawn {mob} {amount} {world},{x},{y},{z}");
        coinName = getConfig().getString("coin.name", "余烬币");
        coinKillReward = getConfig().getInt("coin.kill_reward", 1);
        signReward = getConfig().getInt("sign.reward", getConfig().getInt("sign.coin_reward", 100));
        signActivity = getConfig().getInt("sign.activity_points", getConfig().getInt("activity.sign_points", 10));
        actKillPoints = getConfig().getInt("activity.kill_points", getConfig().getInt("activity.per_kill", 1));
        actKillCap = getConfig().getInt("activity.kill_cap", getConfig().getInt("activity.kill_daily_cap", 15));
        actOnlineInterval = getConfig().getInt("activity.online_interval_minutes", 4);
        actOnlinePoints = getConfig().getInt("activity.online_points", 5);
        actOnlineCap = getConfig().getInt("activity.online_cap", 15);
        actBountyComplete = getConfig().getInt("activity.bounty_complete_points", 15);

        chestRewards.clear();
        ConfigurationSection mapSec = getConfig().getConfigurationSection("activity.chests");
        if (mapSec != null) {
            boolean looksLikeMap = true;
            for (String key : mapSec.getKeys(false)) {
                try { Integer.parseInt(key); } catch (NumberFormatException e) { looksLikeMap = false; break; }
            }
            if (looksLikeMap) {
                for (String key : mapSec.getKeys(false)) {
                    try { chestRewards.put(Integer.parseInt(key), mapSec.getInt(key)); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        if (chestRewards.isEmpty()) {
            List<?> chestList = getConfig().getList("activity.chests");
            if (chestList != null) {
                for (Object o : chestList) {
                    if (o instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) o;
                        int th = toInt(m.get("threshold"), 0);
                        int coin = toInt(m.get("coin"), 0);
                        if (th > 0) chestRewards.put(th, coin);
                    }
                }
            }
        }
        if (chestRewards.isEmpty()) {
            chestRewards.put(25, 50); chestRewards.put(50, 100);
            chestRewards.put(75, 150); chestRewards.put(100, 250);
        }

        bountyDefs.clear();
        List<?> rawB = getConfig().getList("bounties");
        if (rawB != null) {
            for (Object o : rawB) {
                if (!(o instanceof Map)) continue;
                Map<?, ?> m = (Map<?, ?>) o;
                String id = String.valueOf(m.get("id"));
                String name = m.containsKey("name") ? String.valueOf(m.get("name")) : id;
                bountyDefs.add(new BountyDef(id, name, toInt(m.get("killTarget"), 30), toInt(m.get("rewardCoin"), 80)));
            }
        }
        if (bountyDefs.isEmpty()) {
            bountyDefs.add(new BountyDef("clear_undead", "清剿亡灵", 30, 80));
            bountyDefs.add(new BountyDef("ember_hunt", "余烬猎杀", 50, 120));
            bountyDefs.add(new BountyDef("crypt_sweep", "地窟扫荡", 20, 60));
        }
        if (dataStore != null) dataStore.setStartingCoin(getConfig().getInt("coin.starting", 0));
        if (enhanceService != null) enhanceService.reload();
        if (calamityService != null) calamityService.reload();
        if (covenantService != null) covenantService.reload();
        if (talentService != null) {
            talentService.reload();
            if (dataStore != null) dataStore.setTalentService(talentService);
        }
        if (cashService != null) cashService.reload();
        if (scrapService != null) scrapService.reload();
        if (mailService != null) mailService.reload();
        if (friendService != null) friendService.reload();
        if (ladderService != null) ladderService.reload();
        if (petService != null) petService.reload();
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o != null) {
            try { return Integer.parseInt(String.valueOf(o)); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }

    private BountyDef findBounty(String id) {
        if (id == null || id.isEmpty()) return null;
        for (BountyDef b : bountyDefs) if (b.id.equals(id)) return b;
        return null;
    }

    private void ensureBounty(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        if ((data.getBountyId() == null || data.getBountyId().isEmpty())
                && !data.isBountyClaimed() && !bountyDefs.isEmpty()) {
            BountyDef pick = bountyDefs.get(random.nextInt(bountyDefs.size()));
            data.setBountyId(pick.id);
            data.setBountyProgress(0);
            dataStore.saveIfDirty(player.getUniqueId());
        }
    }

    private void tickOnlineMinute(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        data.addPlayMinute();
        if (actOnlineInterval > 0
                && data.getPlayMinutesToday() % actOnlineInterval == 0
                && data.getActivityFromOnline() < actOnlineCap) {
            int room = actOnlineCap - data.getActivityFromOnline();
            int give = Math.min(actOnlinePoints, room);
            if (give > 0) {
                data.setActivityFromOnline(data.getActivityFromOnline() + give);
                data.addActivity(give);
            }
        }
        dataStore.saveIfDirty(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerData joinData = dataStore.get(player.getUniqueId()); // ensureDaily + talent seed
        joinData.setLastKnownName(player.getName());
        ensureBounty(player);
        if (cashService != null) cashService.onJoin(player);
        if (ladderService != null) ladderService.recomputePower(player);
        List<?> lines = getConfig().getList("join_message");
        if (lines != null) {
            for (Object o : lines) if (o != null) player.sendMessage(color(String.valueOf(o)));
        }
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override public void run() {
                if (player.isOnline()) refreshBoard(player);
            }
        }, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (petService != null) petService.onPlayerQuit(event.getPlayer());
        boards.remove(id);
        dataStore.unload(id);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null || !isQualifyingKill(entity)) return;
        ensureBounty(killer);
        PlayerData data = dataStore.get(killer.getUniqueId());
        data.addKillToday();
        if (coinKillReward > 0) data.addCoin(coinKillReward);
        if (data.getActivityFromKills() < actKillCap) {
            int room = actKillCap - data.getActivityFromKills();
            int give = Math.min(actKillPoints, room);
            if (give > 0) {
                data.setActivityFromKills(data.getActivityFromKills() + give);
                data.addActivity(give);
            }
        }
        if (!data.isBountyClaimed()) {
            BountyDef b = findBounty(data.getBountyId());
            if (b != null && data.getBountyProgress() < b.killTarget) data.addBountyProgress(1);
        }
        dataStore.flushMutation(killer.getUniqueId());
        refreshBoard(killer);
        if (calamityService != null && calamityService.isCalamityEntity(entity)) {
            calamityService.onCalamityKilled(killer);
        }
    }

    private boolean isQualifyingKill(LivingEntity entity) {
        String name = entity.getCustomName();
        String type = entity.getType().name();
        if (name != null && (name.contains("余烬") || name.contains("Ember") || name.contains("Crypt"))) return true;
        return "ZOMBIE".equals(type) || "SKELETON".equals(type) || "ZOMBIE_VILLAGER".equals(type);
    }

    private int countNamed(Player player, String needle) {
        if (needle == null || needle.isEmpty()) return 0;
        int total = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            ItemMeta meta = stack.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) continue;
            String dn = ChatColor.stripColor(meta.getDisplayName());
            if (dn != null && dn.contains(needle)) total += stack.getAmount();
        }
        return total;
    }

    private void refreshBoard(Player player) {
        if (!boardEnabled || player == null || !player.isOnline()) return;
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        if (mgr == null) return;
        Scoreboard board = boards.get(player.getUniqueId());
        if (board == null) {
            board = mgr.getNewScoreboard();
            boards.put(player.getUniqueId(), board);
        }
        Objective obj = board.getObjective("corerpg");
        if (obj != null) obj.unregister();
        obj = board.registerNewObjective("corerpg", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        String title = boardTitle;
        if (title.length() > 32) title = title.substring(0, 32);
        obj.setDisplayName(title);
        PlayerData data = dataStore.get(player.getUniqueId());
        setLine(obj, ChatColor.GOLD + "币 " + ChatColor.WHITE + data.getCoin(), 6);
        setLine(obj, ChatColor.GREEN + "活跃 " + ChatColor.WHITE + data.getActivity() + "/100", 5);
        setLine(obj, ChatColor.YELLOW + "击杀 " + ChatColor.WHITE + data.getKillsToday(), 4);
        setLine(obj, ChatColor.RED + "碎片 " + ChatColor.WHITE + countNamed(player, shardNeedle), 3);
        setLine(obj, ChatColor.GRAY + "骨尘 " + ChatColor.WHITE + countNamed(player, dustNeedle), 2);
        setLine(obj, ChatColor.AQUA + "附魔晶 " + ChatColor.WHITE + countNamed(player, crystalNeedle), 1);
        setLine(obj, ChatColor.DARK_GRAY + "/ember", 0);
        player.setScoreboard(board);
    }

    private static void setLine(Objective obj, String text, int score) {
        if (text.length() > 40) text = text.substring(0, 40);
        obj.getScore(text).setScore(score);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("corerpg")
                && !label.equalsIgnoreCase("crpg") && !label.equalsIgnoreCase("rpg")) return false;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) { sendHelp(sender); return true; }
        String sub = args[0].toLowerCase();
        if ("reload".equals(sub)) {
            if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            reloadLocal();
            sender.sendMessage(ChatColor.GREEN + "[CoreRpg] 配置已重载");
            return true;
        }
        if ("status".equals(sub)) return cmdStatus(sender);
        if ("spawn".equals(sub)) return cmdSpawn(sender, args);
        if ("coin".equals(sub)) return cmdCoin(sender, args);
        if ("sign".equals(sub)) return cmdSign(sender);
        if ("activity".equals(sub)) return cmdActivity(sender, args);
        if ("bounty".equals(sub)) return cmdBounty(sender, args);
        if ("enhance".equals(sub)) return cmdEnhance(sender, args);
        if ("socket".equals(sub)) return cmdSocket(sender, args);
        if ("calamity".equals(sub)) return cmdCalamity(sender, args);
        if ("abyss".equals(sub)) return cmdAbyss(sender);
        if ("covenant".equals(sub)) return cmdCovenant(sender, args);
        if ("talent".equals(sub)) return cmdTalent(sender, args);
        if ("cash".equals(sub)) return cmdCash(sender, args);
        if ("shop".equals(sub)) return cmdShop(sender, args);
        if ("monthly".equals(sub)) return cmdMonthly(sender, args);
        if ("scrap".equals(sub)) return cmdScrap(sender, args);
        if ("reforge".equals(sub)) return cmdReforge(sender, args);
        if ("mail".equals(sub)) {
            if (mailService != null) mailService.cmdRoot(sender, args);
            return true;
        }
        if ("friend".equals(sub) || "friends".equals(sub)) {
            if (friendService != null) friendService.cmdRoot(sender, args);
            return true;
        }
        if ("settings".equals(sub) || "setting".equals(sub)) return cmdSettings(sender, args);
        if ("ladder".equals(sub)) {
            if (ladderService != null) ladderService.cmdRoot(sender, args);
            return true;
        }
        if ("pet".equals(sub) || "pets".equals(sub)) {
            if (petService != null) petService.cmdRoot(sender, args);
            return true;
        }
        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[CoreRpg] " + ChatColor.YELLOW
                + "/corerpg spawn|status|coin|sign|activity|bounty|enhance|socket|scrap|reforge|calamity|abyss|covenant|talent|cash|shop|monthly|mail|friend|settings|ladder|pet|reload|help");
        sender.sendMessage(ChatColor.GRAY + "  coin [give <玩家> <数量>] · sign · activity [claim] · bounty [claim]");
        sender.sendMessage(ChatColor.GRAY + "  enhance [info] · socket list|insert <gemId>|remove <slot>");
        sender.sendMessage(ChatColor.GRAY + "  scrap [info] · reforge");
        sender.sendMessage(ChatColor.GRAY + "  calamity [status|trigger] · abyss");
        sender.sendMessage(ChatColor.GRAY + "  covenant [set <id>|reset] · talent [info|unlock|reset|grant]");
        sender.sendMessage(ChatColor.GRAY + "  cash · shop buy daily_ticket · monthly [buy]");
        sender.sendMessage(ChatColor.GRAY + "  mail [read|claim|delete|send] · friend [add|accept|deny|remove|invite|mentor]");
        sender.sendMessage(ChatColor.GRAY + "  settings [sound|tip|privacy]");
        sender.sendMessage(ChatColor.GRAY + "  ladder [me|power|abyss|speed]");
        sender.sendMessage(ChatColor.GRAY + "  pet [list|summon|dismiss|unlock|feed]");
        sender.sendMessage(ChatColor.DARK_GRAY + "  admin: enhance set · calamity trigger · talent grant · cash give · mail send · ladder set/refresh");
    }

    private boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only"); return false; }
        return true;
    }

    private boolean cmdStatus(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        ensureBounty(p);
        PlayerData data = dataStore.get(p.getUniqueId());
        BountyDef b = findBounty(data.getBountyId());
        sender.sendMessage(ChatColor.GOLD + "[CoreRpg] 币=" + data.getCoin()
                + " 签到=" + (DailyService.isToday(data.getLastSignDate()) ? "是" : "否")
                + " 活跃=" + data.getActivity() + "/100"
                + " 击杀=" + data.getKillsToday()
                + " 碎片=" + countNamed(p, shardNeedle)
                + " 骨尘=" + countNamed(p, dustNeedle)
                + " 附魔晶=" + countNamed(p, crystalNeedle));
        if (b != null) {
            sender.sendMessage(ChatColor.YELLOW + "  悬赏: " + b.name + " "
                    + data.getBountyProgress() + "/" + b.killTarget
                    + (data.isBountyClaimed() ? " 已领" : ""));
        }
        refreshBoard(p);
        return true;
    }

    private boolean cmdSpawn(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        Location loc = p.getLocation();
        String mob = spawnMob;
        int amount = spawnAmount;
        if (args.length >= 2) mob = args[1];
        if (args.length >= 3) {
            try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {}
        }
        String cmd = spawnTemplate.replace("{mob}", mob).replace("{amount}", String.valueOf(amount))
                .replace("{world}", loc.getWorld().getName())
                .replace("{x}", String.format("%.1f", loc.getX()))
                .replace("{y}", String.format("%.1f", loc.getY()))
                .replace("{z}", String.format("%.1f", loc.getZ()));
        boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        sender.sendMessage(ChatColor.GREEN + "[CoreRpg] spawn " + mob + " x" + amount
                + (ok ? " OK" : " FAIL") + " @ "
                + (int) loc.getX() + "," + (int) loc.getY() + "," + (int) loc.getZ());
        return true;
    }

    private boolean cmdCoin(CommandSender sender, String[] args) {
        if (args.length >= 2 && "give".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true;
            }
            if (args.length < 4) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg coin give <玩家> <数量>"); return true;
            }
            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) { sender.sendMessage(ChatColor.RED + "玩家不在线: " + args[2]); return true; }
            int amount;
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数量无效"); return true;
            }
            if (amount <= 0) { sender.sendMessage(ChatColor.RED + "数量须为正整数"); return true; }
            PlayerData d = dataStore.get(target.getUniqueId());
            d.addCoin(amount);
            dataStore.flushMutation(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " " + coinName
                    + " ×" + amount + "（余额 " + d.getCoin() + "）");
            target.sendMessage(ChatColor.GOLD + "[余烬] §e获得" + coinName + " §f×" + amount
                    + ChatColor.GRAY + "（余额 " + d.getCoin() + "）");
            refreshBoard(target);
            return true;
        }
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        PlayerData d = dataStore.get(p.getUniqueId());
        p.sendMessage(ChatColor.GOLD + "[余烬] §e" + coinName + "：§f" + d.getCoin()
                + ChatColor.GRAY + "（软通货 · 挂机/任务产出）");
        return true;
    }

    private boolean cmdSign(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        PlayerData data = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (today.equals(data.getLastSignDate())) {
            p.sendMessage(ChatColor.RED + "[余烬] 今日已签到，明日再来吧。");
            return true;
        }
        data.setLastSignDate(today);
        data.addCoin(signReward);
        data.addActivity(signActivity);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(ChatColor.GREEN + "[余烬] 签到成功！获得" + coinName + " §f×" + signReward
                + ChatColor.GREEN + "，活跃 +" + signActivity
                + ChatColor.GRAY + "（余额 " + data.getCoin() + " · 活跃 " + data.getActivity() + "）");
        refreshBoard(p);
        return true;
    }

    private boolean cmdActivity(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        PlayerData data = dataStore.get(p.getUniqueId());
        boolean forceClaim = args.length >= 2 && "claim".equalsIgnoreCase(args[1]);
        p.sendMessage(ChatColor.GOLD + "[余烬] 活跃度 §f" + data.getActivity() + "/100");
        p.sendMessage(ChatColor.GRAY + "  击杀贡献 " + data.getActivityFromKills() + "/" + actKillCap
                + " · 在线贡献 " + data.getActivityFromOnline() + "/" + actOnlineCap
                + " · 在线分钟 " + data.getPlayMinutesToday());
        int[] order = new int[]{25, 50, 75, 100};
        for (int t : order) {
            Integer reward = chestRewards.get(Integer.valueOf(t));
            if (reward == null) continue;
            String state;
            if (data.hasClaimedThreshold(t)) state = ChatColor.DARK_GRAY + "已领";
            else if (data.getActivity() >= t) state = ChatColor.GREEN + "可领";
            else state = ChatColor.YELLOW + "未达";
            p.sendMessage(ChatColor.GRAY + "  · " + t + " 箱 → " + reward + " " + coinName
                    + " [" + state + ChatColor.GRAY + "]");
        }
        int claimed = claimChests(p, data);
        if (claimed == 0 && forceClaim) p.sendMessage(ChatColor.YELLOW + "暂无可领取的活跃宝箱。");
        if (claimed > 0) {
            dataStore.flushMutation(p.getUniqueId());
            refreshBoard(p);
        }
        return true;
    }

    private int claimChests(Player p, PlayerData data) {
        int count = 0;
        int[] order = new int[]{25, 50, 75, 100};
        for (int t : order) {
            if (data.getActivity() < t || data.hasClaimedThreshold(t)) continue;
            Integer reward = chestRewards.get(Integer.valueOf(t));
            if (reward == null) continue;
            data.addClaimedThreshold(t);
            data.addCoin(reward.intValue());
            count++;
            p.sendMessage(ChatColor.GREEN + "[余烬] 领取活跃宝箱 §e" + t
                    + ChatColor.GREEN + "！→ " + coinName + " §f×" + reward);
        }
        return count;
    }

    private boolean cmdBounty(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        ensureBounty(p);
        PlayerData data = dataStore.get(p.getUniqueId());
        BountyDef b = findBounty(data.getBountyId());
        boolean doClaim = args.length >= 2 && "claim".equalsIgnoreCase(args[1]);
        if (doClaim) {
            if (b == null) { p.sendMessage(ChatColor.RED + "[余烬] 暂无可用悬赏。"); return true; }
            if (data.isBountyClaimed()) {
                p.sendMessage(ChatColor.YELLOW + "[余烬] 今日悬赏奖励已领取。"); return true;
            }
            if (data.getBountyProgress() < b.killTarget) {
                p.sendMessage(ChatColor.RED + "[余烬] 悬赏未完成：" + b.name + " "
                        + data.getBountyProgress() + "/" + b.killTarget);
                return true;
            }
            data.setBountyClaimed(true);
            data.addCoin(b.rewardCoin);
            data.addActivity(actBountyComplete);
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "[余烬] 悬赏完成！§e" + b.name
                    + ChatColor.GREEN + " → " + coinName + " §f×" + b.rewardCoin
                    + ChatColor.GREEN + "，活跃 +" + actBountyComplete);
            refreshBoard(p);
            return true;
        }
        if (b == null) { p.sendMessage(ChatColor.RED + "[余烬] 暂无可用悬赏。"); return true; }
        String status;
        if (data.isBountyClaimed()) status = ChatColor.GREEN + "已领取";
        else if (data.getBountyProgress() >= b.killTarget) status = ChatColor.YELLOW + "可领取 /corerpg bounty claim";
        else status = ChatColor.WHITE + String.valueOf(data.getBountyProgress()) + "/" + b.killTarget;
        p.sendMessage(ChatColor.GOLD + "[余烬] 今日悬赏：§e" + b.name);
        p.sendMessage(ChatColor.GRAY + "  目标击杀 " + b.killTarget + " · 奖励 "
                + b.rewardCoin + " " + coinName + " · 进度 " + status);
        return true;
    }

    private boolean cmdEnhance(CommandSender sender, String[] args) {
        if (args.length >= 2 && "set".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            if (!requirePlayer(sender)) return true;
            if (args.length < 3) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg enhance set <0-10>  §8(管理测试)");
                return true;
            }
            int level;
            try { level = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "等级无效"); return true;
            }
            enhanceService.cmdSet((Player) sender, level);
            return true;
        }
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.enhance") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.enhance");
            return true;
        }
        Player p = (Player) sender;
        if (args.length >= 2 && "info".equalsIgnoreCase(args[1])) {
            enhanceService.cmdInfo(p);
            return true;
        }
        enhanceService.cmdAttempt(p);
        return true;
    }

    private boolean cmdSocket(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.socket") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.socket");
            return true;
        }
        Player p = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg socket list|insert <gemId>|remove <slot>");
            return true;
        }
        String sub = args[1].toLowerCase();
        if ("list".equals(sub)) {
            enhanceService.cmdSocketList(p);
            return true;
        }
        if ("insert".equals(sub)) {
            enhanceService.cmdSocketInsert(p, args.length >= 3 ? args[2] : null);
            return true;
        }
        if ("remove".equals(sub)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg socket remove <slot>");
                return true;
            }
            enhanceService.cmdSocketRemove(p, args[2]);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg socket list|insert <gemId>|remove <slot>");
        return true;
    }
    private boolean cmdCalamity(CommandSender sender, String[] args) {
        String act = args.length >= 2 ? args[1].toLowerCase() : "status";
        if ("trigger".equals(act) || "spawn".equals(act) || "force".equals(act)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            calamityService.trigger(sender);
            return true;
        }
        calamityService.sendStatus(sender);
        return true;
    }

    private boolean cmdAbyss(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        PlayerData data = dataStore.get(p.getUniqueId());
        int tickets = 0;
        if (niBridge != null) tickets = niBridge.countInInventory(p, "ticket_ember_abyss");
        p.sendMessage(ChatColor.DARK_PURPLE + "[余烬深渊] " + ChatColor.LIGHT_PURPLE + "日限 1 次 · 进本扣余烬深渊票");
        p.sendMessage(ChatColor.GRAY + "  背包深渊票 §f" + tickets
                + ChatColor.GRAY + " · 软计数 " + data.getAbyssUsedToday() + "/1（以票为准）");
        p.sendMessage(ChatColor.GRAY + "  进本：§f/dp start EmberAbyss"
                + ChatColor.DARK_GRAY + "  · 通关箱：碎片8 骨尘4 核心2 + 锋利/稳固石");
        p.sendMessage(ChatColor.DARK_GRAY + "  无票时：/ni give " + p.getName() + " ticket_ember_abyss 1（管理）");
        return true;
    }

    private boolean cmdCovenant(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.covenant") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.covenant");
            return true;
        }
        Player p = (Player) sender;
        if (args.length < 2) {
            covenantService.cmdShow(p);
            return true;
        }
        String act = args[1].toLowerCase();
        if ("set".equals(act)) {
            covenantService.cmdSet(p, args.length >= 3 ? args[2] : null);
            return true;
        }
        if ("reset".equals(act) || "clear".equals(act)) {
            covenantService.cmdReset(p);
            return true;
        }
        covenantService.cmdShow(p);
        return true;
    }

    private boolean cmdTalent(CommandSender sender, String[] args) {
        if (args.length >= 2 && "grant".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            talentService.cmdGrant(sender,
                    args.length >= 3 ? args[2] : null,
                    args.length >= 4 ? args[3] : null);
            return true;
        }
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.talent") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.talent");
            return true;
        }
        Player p = (Player) sender;
        if (args.length < 2) {
            talentService.cmdShow(p);
            return true;
        }
        String act = args[1].toLowerCase();
        if ("info".equals(act)) {
            talentService.cmdInfo(p, args.length >= 3 ? args[2] : null);
            return true;
        }
        if ("unlock".equals(act) || "learn".equals(act)) {
            talentService.cmdUnlock(p, args.length >= 3 ? args[2] : null);
            return true;
        }
        if ("reset".equals(act)) {
            talentService.cmdReset(p);
            return true;
        }
        talentService.cmdShow(p);
        return true;
    }

    private boolean cmdCash(CommandSender sender, String[] args) {
        if (args.length >= 2 && "give".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            if (args.length < 4) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg cash give <玩家> <数量>");
                return true;
            }
            int amount;
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数量无效");
                return true;
            }
            if (cashService != null) {
                cashService.cmdCashGive(sender, args[2], amount);
            }
            return true;
        }
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.cash") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.cash");
            return true;
        }
        if (cashService != null) cashService.cmdShowCash((Player) sender);
        return true;
    }

    private boolean cmdShop(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.shop") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.shop");
            return true;
        }
        if (args.length >= 3 && "buy".equalsIgnoreCase(args[1])
                && "daily_ticket".equalsIgnoreCase(args[2])) {
            if (cashService != null) cashService.cmdShopBuyDailyTicket((Player) sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg shop buy daily_ticket");
        return true;
    }

    private boolean cmdMonthly(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.monthly") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.monthly");
            return true;
        }
        if (args.length >= 2 && "buy".equalsIgnoreCase(args[1])) {
            if (cashService != null) cashService.cmdMonthlyBuy((Player) sender);
            return true;
        }
        if (cashService != null) cashService.cmdMonthlyShow((Player) sender);
        return true;
    }

    private boolean cmdScrap(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.scrap") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.scrap");
            return true;
        }
        Player p = (Player) sender;
        boolean info = args.length >= 2 && "info".equalsIgnoreCase(args[1]);
        if (scrapService != null) scrapService.cmdScrap(p, info);
        return true;
    }

    private boolean cmdReforge(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.reforge") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.reforge");
            return true;
        }
        if (scrapService != null) scrapService.cmdReforge((Player) sender);
        return true;
    }

    private boolean cmdSettings(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.settings") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.settings");
            return true;
        }
        Player p = (Player) sender;
        PlayerData data = dataStore.get(p.getUniqueId());
        String PREFIX = ChatColor.AQUA + "[设置] " + ChatColor.RESET;
        if (args.length < 2) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "音效=" + onOff(data.isSettingsSound())
                    + ChatColor.GRAY + " · 提示=" + onOff(data.isSettingsTip())
                    + ChatColor.GRAY + " · 隐私=" + onOff(data.isSettingsPrivacy()));
            p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg settings sound|tip|privacy");
            return true;
        }
        String sub = args[1].toLowerCase();
        if ("sound".equals(sub)) {
            data.setSettingsSound(!data.isSettingsSound());
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + "音效 → " + onOff(data.isSettingsSound()));
            return true;
        }
        if ("tip".equals(sub)) {
            data.setSettingsTip(!data.isSettingsTip());
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + "聊天提示 → " + onOff(data.isSettingsTip())
                    + ChatColor.GRAY + "（关提示不关奖励）");
            return true;
        }
        if ("privacy".equals(sub)) {
            data.setSettingsPrivacy(!data.isSettingsPrivacy());
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + "隐私 → " + onOff(data.isSettingsPrivacy())
                    + ChatColor.GRAY + "（ON=拒陌生人好友申请）");
            return true;
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg settings [sound|tip|privacy]");
        return true;
    }

    private static String onOff(boolean v) {
        return v ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF";
    }

}
