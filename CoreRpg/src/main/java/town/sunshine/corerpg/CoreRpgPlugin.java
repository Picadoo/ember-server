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
import org.bukkit.configuration.file.YamlConfiguration;
import town.sunshine.corerpg.storage.MysqlStorage;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.sql.SQLException;
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
    private TicketGrantService ticketGrantService;
    private TicketEntryService ticketEntryService;
    private StaminaService staminaService;
    private EliteService eliteService;
    private ScrapService scrapService;
    private MailService mailService;
    private FriendService friendService;
    private LadderService ladderService;
    private PetService petService;
    private GuildService guildService;
    private ArenaService arenaService;
    private AuctionService auctionService;
    private WarehouseService warehouseService;
    private AbyssSettleService abyssSettleService;
    private SkillService skillService;
    private FlexSkillService flexSkillService;
    private SetService setService;
    private RaidService raidService;
    private ProgressService progressService;
    private QuestService questService;
    private LifeService lifeService;
    private ForgeService forgeService;
    private PartService partService;
    private GearPassiveService gearPassiveService;
    private AfkTierService afkTierService;
    private town.sunshine.corerpg.p1.EmberAfkService emberAfk; // D177 P1 挂机庭
    private HubPlazaService hubPlazaService;
    private HubNpcService hubNpcService;
    private AbyssShaftService abyssShaftService;
    private WeeklyCorridorService weeklyCorridorService;
    private EliteCorridorService eliteCorridorService;
    private DailyCourtyardService dailyCourtyardService;
    private DailyAshCorridorService dailyAshCorridorService;
    private DailyCryptService dailyCryptService;
    private DailyTideService dailyTideService;
    private DailySpireService dailySpireService;
    private DailyFrostService dailyFrostService;
    private DailyRailService dailyRailService;
    private RaidHallService raidHallService;
    private CalamityBasinService calamityBasinService;
    private LootService lootService;
    /** ember-v1.0-P1 (default OFF; per-world scope) */
    private town.sunshine.corerpg.p1.EmberMode emberMode;
    private town.sunshine.corerpg.p1.EmberDamageTrace emberTrace;
    private town.sunshine.corerpg.p1.EmberCommand emberCommand;
    private town.sunshine.corerpg.p1.EmberItemStore emberStore;
    private town.sunshine.corerpg.p1.EmberLoadoutService emberLoadouts;
    private town.sunshine.corerpg.p1.EmberCombatListener emberCombat;
    private town.sunshine.corerpg.p1.EmberSetService emberSets;
    private town.sunshine.corerpg.p1.EmberForgeService emberForge;
    private town.sunshine.corerpg.p1.EmberRunService emberRuns;
    private town.sunshine.corerpg.p1.EmberSignService emberSign; // D180
    private town.sunshine.corerpg.p1.EmberSupplyService emberSupplies;
    private MysqlStorage mysqlStorage;
    private String storageMode = "yaml"; // yaml | mysql (effective)

    static final class BountyDef {
        final String id;
        final String name;
        final int killTarget;
        final int rewardCoin;
        BountyDef(String id, String name, int killTarget, int rewardCoin) {
            this.id = id; this.name = name; this.killTarget = killTarget; this.rewardCoin = rewardCoin;
        }
    }

    /** F-review #9 (D126): no vanilla "has made the advancement" broadcasts (English, off-theme) on any world */
    static void quietAdvancements(org.bukkit.World w) {
        try { if (!"false".equals(w.getGameRuleValue("announceAdvancements"))) w.setGameRuleValue("announceAdvancements", "false"); }
        catch (RuntimeException ignored) { /* rule missing on this server version */ }
    }

    @org.bukkit.event.EventHandler
    public void onWorldLoadQuiet(org.bukkit.event.world.WorldLoadEvent e) { quietAdvancements(e.getWorld()); }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initStorage();
        dataStore = new PlayerDataStore(this);
        niBridge = new NiBridge(this);
        // ember-v1.0-P1: mode flag + read-only damage probes first, so the LOWEST probe sees the raw value
        emberMode = new town.sunshine.corerpg.p1.EmberMode(this); // config read in reloadLocal()
        emberTrace = new town.sunshine.corerpg.p1.EmberDamageTrace(this);
        Bukkit.getPluginManager().registerEvents(emberTrace, this);
        emberCommand = new town.sunshine.corerpg.p1.EmberCommand(emberMode, emberTrace);
        emberStore = new town.sunshine.corerpg.p1.EmberItemStore(this);
        emberLoadouts = new town.sunshine.corerpg.p1.EmberLoadoutService(this,
                new town.sunshine.corerpg.p1.EmberItems(this, niBridge), emberStore);
        Bukkit.getPluginManager().registerEvents(emberLoadouts, this);
        emberCommand.setLoadouts(emberLoadouts);
        enhanceService = new EnhanceService(this, niBridge);
        calamityService = new CalamityService(this);
        covenantService = new CovenantService(this, niBridge, dataStore);
        talentService = new TalentService(this, niBridge, dataStore);
        cashService = new CashService(this, niBridge, dataStore);
        ticketGrantService = new TicketGrantService(this, niBridge, dataStore);
        cashService.setTicketGrantService(ticketGrantService);
        ticketEntryService = new TicketEntryService(this, dataStore, niBridge);
        staminaService = new StaminaService(this, dataStore, niBridge);
        staminaService.setCashService(cashService);
        eliteService = new EliteService(this, dataStore, niBridge);
        scrapService = new ScrapService(this, niBridge);
        mailService = new MailService(this, niBridge, dataStore);
        friendService = new FriendService(this, dataStore);
        ladderService = new LadderService(this, dataStore);
        petService = new PetService(this, dataStore, niBridge);
        guildService = new GuildService(this, dataStore, niBridge);
        arenaService = new ArenaService(this, dataStore, niBridge);
        auctionService = new AuctionService(this, dataStore, niBridge);
        warehouseService = new WarehouseService(this, dataStore, niBridge);
        abyssSettleService = new AbyssSettleService(this, niBridge, dataStore);
        skillService = new SkillService(this, dataStore);
        flexSkillService = new FlexSkillService(this, dataStore);
        setService = new SetService(this, niBridge);
        gearPassiveService = new GearPassiveService(this, niBridge);
        raidService = new RaidService(this, dataStore, niBridge);
        progressService = new ProgressService(this, dataStore);
        questService = new QuestService(this, dataStore, niBridge);
        lifeService = new LifeService(this, dataStore, niBridge);
        forgeService = new ForgeService(this, dataStore, niBridge);
        partService = new PartService(this, niBridge);
        afkTierService = new AfkTierService(this, dataStore);
        emberAfk = new town.sunshine.corerpg.p1.EmberAfkService(this); // D177 (config: ember-v1.yml afk, read in reloadLocal)
        new town.sunshine.corerpg.p1.EmberBindGuard(this); // D276 O9: bind guards outside AFK class
        new town.sunshine.corerpg.p1.EmberParry(this); // D301 守招·余烬招架 (bare Q)
        hubPlazaService = new HubPlazaService(this);
        hubNpcService = new HubNpcService(this);
        abyssShaftService = new AbyssShaftService(this);
        weeklyCorridorService = new WeeklyCorridorService(this);
        eliteCorridorService = new EliteCorridorService(this);
        dailyCourtyardService = new DailyCourtyardService(this);
        dailyAshCorridorService = new DailyAshCorridorService(this);
        dailyCryptService = new DailyCryptService(this);
        dailyTideService = new DailyTideService(this);
        dailySpireService = new DailySpireService(this);
        dailyFrostService = new DailyFrostService(this);
        dailyRailService = new DailyRailService(this);
        raidHallService = new RaidHallService(this);
        calamityBasinService = new CalamityBasinService(this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() { if (questService != null) questService.tickAll(); }
        }, 400L, 200L);
        lootService = new LootService(this, dataStore, niBridge);
        statService = new StatService(this, niBridge, dataStore);
        dataStore.setTalentService(talentService);
        reloadLocal();
        dbGuard = new DbGuard(this); // 1.62: MySQL configured but down at enable → refuse joins instead of silent YAML
        Bukkit.getPluginManager().registerEvents(dbGuard, this);
        if (DbGuard.shouldEngage("mysql".equalsIgnoreCase(getConfig().getString("storage", "yaml")), isMysqlActive(),
                getConfig().getBoolean("storage_guard.enabled", true), town.sunshine.corerpg.p1.EmberMode.active())) {
            dbGuard.engage("connect to MySQL failed at enable (see the [storage] error above)");
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        for (org.bukkit.World w : Bukkit.getWorlds()) quietAdvancements(w); // F-review #9 (D126)
        Bukkit.getPluginManager().registerEvents(questService, this);
        Bukkit.getPluginManager().registerEvents(hubNpcService, this);
        Bukkit.getPluginManager().registerEvents(lifeService, this);
        Bukkit.getPluginManager().registerEvents(staminaService, this);
        Bukkit.getPluginManager().registerEvents(statService, this);
        emberCombat = new town.sunshine.corerpg.p1.EmberCombatListener(this, emberLoadouts);
        Bukkit.getPluginManager().registerEvents(emberCombat, this);
        emberCombat.startHealGuard();
        emberSets = new town.sunshine.corerpg.p1.EmberSetService(this, emberLoadouts, emberCombat);
        Bukkit.getPluginManager().registerEvents(emberSets, this);
        emberSets.start();
        emberCommand.setSets(emberSets);
        emberForge = new town.sunshine.corerpg.p1.EmberForgeService(this, emberLoadouts);
        Bukkit.getPluginManager().registerEvents(emberForge, this);
        emberCommand.setForge(emberForge);
        { // 1.62 P1 material warehouse + gear library (storage) and vanilla inventory snapshots
            town.sunshine.corerpg.p1.EmberVault vault = new town.sunshine.corerpg.p1.EmberVault(this);
            Bukkit.getPluginManager().registerEvents(vault, this);
            Bukkit.getPluginManager().registerEvents(new town.sunshine.corerpg.p1.EmberGearLib(this, emberLoadouts), this);
            invSnap = new InvSnapService(this);
            Bukkit.getPluginManager().registerEvents(invSnap, this);
            invSnap.start();
        }
        emberRuns = new town.sunshine.corerpg.p1.EmberRunService(this, emberLoadouts, emberStore); // G04 Q01–Q03 runs + settlement
        Bukkit.getPluginManager().registerEvents(emberRuns, this);
        // D318 六槽 T1: migration trigger / 待领 / armor page actions (gear.six_slot.* default off → handlers return at once)
        Bukkit.getPluginManager().registerEvents(new town.sunshine.corerpg.p1.EmberSixSlotService(this, emberLoadouts), this);
        emberSign = new town.sunshine.corerpg.p1.EmberSignService(this); // D180 每日签到 + 在线时长 (ember-v1.yml signin / online)
        emberSign.reload();
        { // P2-9 (D83) titles + trails (cosmetic only)
            final town.sunshine.corerpg.p1.EmberCosmetics cos = new town.sunshine.corerpg.p1.EmberCosmetics(emberRuns);
            emberRuns.setCosmetics(cos);
            emberRuns.setLeaderboard(new town.sunshine.corerpg.p1.EmberLeaderboard(getDataFolder(), getLogger())); // P2-10 (D84)
            Bukkit.getPluginManager().registerEvents(cos, this);
            Bukkit.getScheduler().runTaskTimer(this, cos::tick, 40L, 4L);
            final town.sunshine.corerpg.p1.EmberSeason season = new town.sunshine.corerpg.p1.EmberSeason(emberRuns, getDataFolder(), getLogger()); // D116/D117
            emberRuns.setSeason(season);
            Bukkit.getScheduler().runTaskTimer(this, season::tick, 200L, 1200L);
        }
        try { // D141 talents / D142 honors / D143 affix reroll (ember-v1-growth.yml)
            Bukkit.getPluginManager().registerEvents(new town.sunshine.corerpg.p1.EmberGrowthService(this, emberRuns), this);
        } catch (RuntimeException ex) {
            getLogger().warning("[P1] growth service failed to load: " + ex);
        }
        emberRuns.start();
        emberCommand.setRuns(emberRuns);
        hubAmbience = new HubAmbienceService(this); // D97 hub atmosphere (display only)
        Bukkit.getPluginManager().registerEvents(hubAmbience, this);
        Bukkit.getScheduler().runTaskLater(this, hubAmbience::start, 100L);
        emberSupplies = new town.sunshine.corerpg.p1.EmberSupplyService(this); // B2.169 §19.4 shop + §3.1 starter supplies
        Bukkit.getPluginManager().registerEvents(emberSupplies, this);
        emberCommand.setSupplies(emberSupplies);
        Bukkit.getPluginManager().registerEvents(flexSkillService, this);
        statService.start();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override public void run() {
                if (questService != null) { questService.hookAdyeshach(); questService.ensureNpc(false); }
                if (hubNpcService != null) {
                    hubNpcService.reload();
                    hubNpcService.hookAdyeshach();
                    hubNpcService.ensureAll(false);
                    // re-ensure after chunks settle so stale persisted hitboxes get purged
                    Bukkit.getScheduler().runTaskLater(CoreRpgPlugin.this, new Runnable() {
                        @Override public void run() {
                            if (hubNpcService != null) hubNpcService.ensureAll(false);
                        }
                    }, 200L);
                }
            }
        }, 100L);
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
            @Override public void run() {
                dataStore.saveAll();
                if (guildService != null) guildService.saveAll();
                if (auctionService != null) auctionService.saveIfDirty();
            }
        }, 6000L, 6000L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                if (calamityService != null) calamityService.tick();
            }
        }, 20L, 200L);
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
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override public void run() { if (emberMode != null) emberMode.checkConflicts(); } // after all plugins enabled
        });
        getLogger().info("CoreRpg " + getDescription().getVersion() + " enabled (storage=" + storageMode + "; coin/sign/activity/bounty/enhance/socket/scrap/reforge/calamity/abyss-settle/covenant/talent/skill/cash/shop/monthly/stamina/tickets/mail/friend/settings/ladder/pet/guild/arena/auction/warehouse/raid/set/part).");
    }

    @Override
    public void onDisable() {
        if (town.sunshine.corerpg.p1.EmberVaultLog.get() != null) town.sunshine.corerpg.p1.EmberVaultLog.get().shutdown(); // 1.64.1 warehouse log
        if (invSnap != null) invSnap.shutdown(); // 1.62: last inventory snapshot of everyone online (quit events come after disable)
        if (hubAmbience != null) hubAmbience.stop(); // D97: floating lines never outlive the plugin
        if (petService != null) petService.shutdown();
        if (guildService != null) guildService.saveAll();
        if (auctionService != null) auctionService.saveAll();
        if (emberAfk != null) emberAfk.shutdown(); // D177: stamp offline start for everyone online (quit events come after disable)
        if (dataStore != null) dataStore.saveAll();
        if (emberSign != null) emberSign.shutdown(); // D180
        if (emberRuns != null) emberRuns.shutdown(); // G04: run mobs removed; open runs are aborted + refunded on the next start
        if (emberSets != null) emberSets.flushAll(); // G02 remaining cooldowns → states, saved just below
        if (emberLoadouts != null && town.sunshine.corerpg.p1.EmberMode.active()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                town.sunshine.corerpg.p1.EmberPlayerState st = emberLoadouts.state(p.getUniqueId());
                if (town.sunshine.corerpg.p1.EmberMode.isP1(p)) { st.lastHp = p.getHealth(); st.lastWorld = p.getWorld().getName(); }
                emberLoadouts.saveState(p);
            }
        }
        if (emberStore != null) emberStore.shutdown(); // flush P1 writes before the pool closes
        if (mysqlStorage != null) {
            mysqlStorage.close();
            mysqlStorage = null;
        }
    }

    private StatService statService;
    public StatService getStatService() { return statService; }

    private InvSnapService invSnap;
    private DbGuard dbGuard;
    public InvSnapService getInvSnap() { return invSnap; }
    public DbGuard getDbGuard() { return dbGuard; }

    /** test hook: env CORERPG_TEST_MYSQL_PORT overrides mysql.port (used by the DB-down guard smoke) */
    public static Integer testMysqlPort() {
        String v = System.getenv("CORERPG_TEST_MYSQL_PORT");
        if (v == null || v.trim().isEmpty()) return null;
        try { return Integer.valueOf(v.trim()); } catch (NumberFormatException e) { return null; }
    }

    public int effectiveMysqlPort() {
        Integer t = testMysqlPort();
        return t != null ? t : getConfig().getInt("mysql.port", 3306);
    }
    public PlayerDataStore getDataStore() { return dataStore; }
    public CalamityService getCalamityService() { return calamityService; }
    public SetService getSetService() { return setService; }
    public GearPassiveService getGearPassiveService() { return gearPassiveService; }
    public RaidService getRaidService() { return raidService; }
    public NiBridge getNiBridge() { return niBridge; }
    public CovenantService getCovenantService() { return covenantService; }
    public TalentService getTalentService() { return talentService; }
    public CashService getCashService() { return cashService; }
    public ProgressService getProgressService() { return progressService; }
    public QuestService getQuestService() { return questService; }
    private HubAmbienceService hubAmbience;
    public HubNpcService getHubNpcService() { return hubNpcService; }
    public AfkTierService getAfkTierService() { return afkTierService; }
    public town.sunshine.corerpg.p1.EmberAfkService getEmberAfk() { return emberAfk; }
    public TalentService getTalentServicePublic() { return talentService; }
    public WarehouseService getWarehouseServicePublic() { return warehouseService; }
    public TicketGrantService getTicketGrantService() { return ticketGrantService; }
    public TicketEntryService getTicketEntryService() { return ticketEntryService; }
    public StaminaService getStaminaService() { return staminaService; }
    public EliteService getEliteService() { return eliteService; }
    public ScrapService getScrapService() { return scrapService; }
    public MailService getMailService() { return mailService; }
    public FriendService getFriendService() { return friendService; }
    public LadderService getLadderService() { return ladderService; }
    public PetService getPetService() { return petService; }
    public LifeService getLifeService() { return lifeService; }
    public GuildService getGuildService() { return guildService; }
    public ArenaService getArenaService() { return arenaService; }
    public AuctionService getAuctionService() { return auctionService; }
    public WarehouseService getWarehouseService() { return warehouseService; }
    public AbyssSettleService getAbyssSettleService() { return abyssSettleService; }
    public SkillService getSkillService() { return skillService; }
    public FlexSkillService getFlexSkillService() { return flexSkillService; }
    public EnhanceService getEnhanceService() { return enhanceService; }
    public MysqlStorage getMysqlStorage() { return mysqlStorage; }
    public String getStorageMode() { return storageMode; }
    public town.sunshine.corerpg.p1.EmberMode getEmberMode() { return emberMode; }
    public town.sunshine.corerpg.p1.EmberLoadoutService getEmberLoadouts() { return emberLoadouts; }
    public town.sunshine.corerpg.p1.EmberForgeService getEmberForge() { return emberForge; }
    public town.sunshine.corerpg.p1.EmberCombatListener getEmberCombat() { return emberCombat; }
    public town.sunshine.corerpg.p1.EmberSetService getEmberSets() { return emberSets; }
    public town.sunshine.corerpg.p1.EmberRunService getEmberRuns() { return emberRuns; }
    public town.sunshine.corerpg.p1.EmberSupplyService getEmberSupplies() { return emberSupplies; }
    public boolean isMysqlActive() { return mysqlStorage != null && mysqlStorage.isActive(); }


    private void initStorage() {
        String want = getConfig().getString("storage", "yaml");
        if (want == null) want = "yaml";
        want = want.trim().toLowerCase();
        storageMode = "yaml";
        if (mysqlStorage != null) {
            mysqlStorage.close();
            mysqlStorage = null;
        }
        if ("mysql".equals(want)) {
            mysqlStorage = new MysqlStorage(this);
            org.bukkit.configuration.ConfigurationSection sec = getConfig().getConfigurationSection("mysql");
            Integer tp = testMysqlPort();
            if (tp != null && sec != null) {
                org.bukkit.configuration.file.YamlConfiguration c = new org.bukkit.configuration.file.YamlConfiguration();
                for (String k : sec.getKeys(true)) if (!sec.isConfigurationSection(k)) c.set(k, sec.get(k));
                c.set("port", tp);
                sec = c;
                getLogger().warning("[storage] TEST OVERRIDE: CORERPG_TEST_MYSQL_PORT=" + tp + " (mysql.port from config ignored)");
            }
            if (mysqlStorage.tryInit(sec)) {
                storageMode = "mysql";
            } else {
                getLogger().severe("[storage] configured mysql but connect failed — YAML fallback for non-player caches; DbGuard will refuse joins if P1+storage_guard");
                mysqlStorage = null;
                storageMode = "yaml";
            }
        } else {
            getLogger().info("[storage] mode=yaml (default)");
        }
    }

    private boolean cmdStorage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[CoreRpg] storage=" + ChatColor.YELLOW + storageMode
                + ChatColor.GRAY + " (config=" + getConfig().getString("storage", "yaml") + ")");
        if (isMysqlActive()) {
            long ms = mysqlStorage.pingMs();
            sender.sendMessage(ChatColor.GRAY + "  MySQL ping: "
                    + (ms < 0 ? ChatColor.RED + "FAIL" : ChatColor.GREEN + String.valueOf(ms) + "ms"));
        } else if ("mysql".equalsIgnoreCase(getConfig().getString("storage", "yaml"))) {
            sender.sendMessage(ChatColor.RED + "  MySQL configured but inactive (fell back to yaml)");
        }
        return true;
    }

    private boolean cmdAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg admin migrate-yaml-to-mysql");
            return true;
        }
        String sub = args[1].toLowerCase();
        if ("migrate-yaml-to-mysql".equals(sub) || "migrate".equals(sub)) {
            return cmdMigrateYamlToMysql(sender);
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg admin migrate-yaml-to-mysql");
        return true;
    }

    private boolean cmdMigrateYamlToMysql(CommandSender sender) {
        if (!isMysqlActive()) {
            sender.sendMessage(ChatColor.RED + "[CoreRpg] MySQL 未激活。请先设 storage: mysql 并重载/重启。");
            return true;
        }
        int players = 0, guilds = 0, mails = 0;
        boolean auctionOk = false;
        try {
            // players
            File pdir = dataStore.getPlayersDir();
            if (pdir != null && pdir.isDirectory()) {
                File[] files = pdir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String n = f.getName();
                        if (!n.endsWith(".yml") || n.startsWith("_")) continue;
                        String base = n.substring(0, n.length() - 4);
                        UUID uuid;
                        try { uuid = UUID.fromString(base); } catch (IllegalArgumentException e) { continue; }
                        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                        String name = y.getString("lastKnownName", "");
                        mysqlStorage.savePlayerYaml(uuid, name, y.saveToString());
                        players++;
                    }
                }
            }
            // guilds
            if (guildService != null && guildService.getGuildsDir() != null) {
                File gdir = guildService.getGuildsDir();
                File[] files = gdir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String n = f.getName();
                        if (!n.endsWith(".yml")) continue;
                        String id = n.substring(0, n.length() - 4);
                        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                        mysqlStorage.saveGuildYaml(id, y.saveToString());
                        guilds++;
                    }
                }
            }
            // auction listings blob from auction.yml
            File auctionFile = new File(getDataFolder(), "auction.yml");
            if (auctionFile.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(auctionFile);
                YamlConfiguration blob = new YamlConfiguration();
                blob.set("next_id", Integer.valueOf(cfg.getInt("next_id", 1)));
                if (cfg.getConfigurationSection("listings") != null) {
                    for (String key : cfg.getConfigurationSection("listings").getKeys(false)) {
                        blob.set("listings." + key, cfg.get("listings." + key));
                    }
                }
                mysqlStorage.saveAuctionBlob(blob.saveToString());
                auctionOk = true;
            }
            // mail
            if (mailService != null && mailService.getMailDir() != null) {
                File mdir = mailService.getMailDir();
                File[] files = mdir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String n = f.getName();
                        if (!n.endsWith(".yml")) continue;
                        String base = n.substring(0, n.length() - 4);
                        UUID uuid;
                        try { uuid = UUID.fromString(base); } catch (IllegalArgumentException e) { continue; }
                        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                        mysqlStorage.saveMailInboxYaml(uuid, y.saveToString());
                        mails++;
                    }
                }
            }
            sender.sendMessage(ChatColor.GREEN + "[CoreRpg] migrate-yaml-to-mysql done: players="
                    + players + " guilds=" + guilds + " mail=" + mails
                    + " auction=" + (auctionOk ? "ok" : "skip"));
            getLogger().info("migrate-yaml-to-mysql: players=" + players + " guilds=" + guilds
                    + " mail=" + mails + " auction=" + auctionOk);
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "[CoreRpg] migrate failed: " + e.getMessage());
            getLogger().warning("migrate failed: " + e.getMessage());
        }
        return true;
    }


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
        if (emberMode != null) {
            emberMode.reload();
            if (emberTrace != null) emberTrace.setConsole(emberMode.b("debug.console", false));
            if (emberMode.isActive() && emberStore != null) {
                emberStore.ensureSchema();
                if (emberLoadouts != null) emberLoadouts.ensureLoadedAll();
            }
        }
        if (progressService != null) progressService.reload();
        if (questService != null) {
            questService.reload();
            // refresh hitbox after npc coords reload (hooks stay from enable)
            Bukkit.getScheduler().runTask(this, new Runnable() {
                @Override public void run() {
                    if (questService != null) questService.ensureNpc(false);
                }
            });
        }
        if (hubNpcService != null) {
            hubNpcService.reload();
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override public void run() {
                    if (hubNpcService != null) hubNpcService.ensureAll(false);
                }
            }, 40L);
        }
        if (lifeService != null) lifeService.reload();
        if (forgeService != null) forgeService.reload();
        if (partService != null) partService.reload();
        if (lootService != null) lootService.reload();
        if (emberSign != null) emberSign.reload(); // D180 (after emberMode.reload above)
        if (statService != null) statService.reload();
        if (afkTierService != null) afkTierService.reload();
        if (emberAfk != null) emberAfk.reload(); // D177 (after emberMode.reload above)
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
        if (ticketGrantService != null) ticketGrantService.reload();
        if (ticketEntryService != null) ticketEntryService.reload();
        if (staminaService != null) { staminaService.reload(); staminaService.setCashService(cashService); }
        if (eliteService != null) eliteService.reload();
        if (scrapService != null) scrapService.reload();
        if (mailService != null) mailService.reload();
        if (friendService != null) friendService.reload();
        if (ladderService != null) ladderService.reload();
        if (petService != null) petService.reload();
        if (guildService != null) guildService.reload();
        if (arenaService != null) arenaService.reload();
        if (auctionService != null) auctionService.reload();
        if (warehouseService != null) warehouseService.reload();
        if (abyssSettleService != null) abyssSettleService.reload();
        if (skillService != null) skillService.reload();
        if (flexSkillService != null) flexSkillService.reload();
        if (setService != null) setService.reload();
        if (gearPassiveService != null) gearPassiveService.reload();
        if (raidService != null) raidService.reload();
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
        if (staminaService != null) staminaService.onJoin(player);
        if (cashService != null) cashService.onJoin(player);
        if (ladderService != null) ladderService.recomputePower(player);
        List<?> lines = getConfig().getList("join_message");
        // D62: P1 is the default mode → the P1 welcome (ember-v1.yml join_message) replaces the old-route tips
        if (town.sunshine.corerpg.p1.EmberMode.active() && town.sunshine.corerpg.p1.EmberMode.get() != null
                && town.sunshine.corerpg.p1.EmberMode.get().config() != null
                && !town.sunshine.corerpg.p1.EmberMode.get().config().getStringList("join_message").isEmpty()) {
            lines = town.sunshine.corerpg.p1.EmberMode.get().config().getStringList("join_message");
            if (emberRuns != null) { // E-review #10: returning players get a progress greeting instead of "start at Q01"
                List<String> own = emberRuns.joinLines(joinData);
                if (own != null) lines = own;
            }
        }
        if (lines != null) {
            for (Object o : lines) if (o != null) player.sendMessage(color(String.valueOf(o)));
        }
        String[][] own = town.sunshine.corerpg.p1.EmberMode.active() && emberRuns != null ? emberRuns.joinButtons(joinData) : null;
        if (own != null) ConfirmTokens.sendButtons(player, "§7", own); // F-review #3: graduates get the season / goals page
        else if (town.sunshine.corerpg.p1.EmberMode.active()) // D99: open the menus by clicking, never by typing /ember
            ConfirmTokens.sendButtons(player, "§7",
                    new String[]{"[主菜单]", "/ember", "冒险 · 装备 · 工坊 · 帮助都在这里", "GOLD"},
                    new String[]{"[冒险页]", "/ember_p1_adventure", "选图进副本（和右键门吏 · 灰钥一样）", "GREEN"},
                    new String[]{"[帮助]", "/ember_help", "怎么玩 · 操作 · 三套装 · 变强", "AQUA"});
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override public void run() {
                if (player.isOnline()) refreshBoard(player);
            }
        }, 10L);
        if (questService != null) questService.onJoin(player);
        if (abyssSettleService != null) abyssSettleService.onPlayerJoin(player); // B2.140 relog grace / payout
        if (mailService != null) {
            final Player jp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (jp.isOnline()) town.sunshine.corerpg.p1.EmberMailPath.maybeAfterMail(jp, mailService); // D523
            }, 90L);
        }
        if (petService != null) {
            final Player pp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (pp.isOnline()) town.sunshine.corerpg.p1.EmberPetPath.maybeAfterJoin(pp, petService); // D525
            }, 100L);
        }
        if (petService != null) {
            final Player fp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (fp.isOnline()) town.sunshine.corerpg.p1.EmberFeedPath.maybeAfterJoin(fp, petService); // D528
            }, 105L);
        }
        if (getEmberRuns() != null && getEmberRuns().cosmetics() != null) {
            final Player tp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (tp.isOnline() && getEmberRuns() != null && getEmberRuns().cosmetics() != null) {
                    town.sunshine.corerpg.p1.EmberTrailPath.maybeAfterProgress(tp, getEmberRuns().cosmetics()); // D529
                    town.sunshine.corerpg.p1.EmberTitlePath.maybeAfterProgress(tp, getEmberRuns().cosmetics()); // D532
                    town.sunshine.corerpg.p1.EmberGlowPath.maybeAfterProgress(tp, getEmberRuns().cosmetics()); // D534
                }
            }, 115L);
        }
        {
            final Player pingP = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (!pingP.isOnline()) return;
                town.sunshine.corerpg.p1.EmberPingPath.maybeDigestOnJoin(pingP); // D533 GATE
                town.sunshine.corerpg.p1.EmberPingPath.notifyWatchers(pingP); // D533 OPEN
            }, 120L);
        }
        {
            final Player sp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (sp.isOnline()) town.sunshine.corerpg.p1.EmberStashPath.maybeAfterProgress(sp); // D526
                if (sp.isOnline()) town.sunshine.corerpg.p1.EmberJunkPath.maybeAfterProgress(sp); // D531
            }, 110L);
        {
            final Player gp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (gp.isOnline()) town.sunshine.corerpg.p1.EmberGemPath.maybeAfterProgress(gp); // D535
            }, 125L);
        {
            final Player tp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (tp.isOnline()) town.sunshine.corerpg.p1.EmberTicketPath.maybeAfterProgress(tp); // D536
            }, 130L);
        {
            final Player dp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (dp.isOnline()) town.sunshine.corerpg.p1.EmberDealPath.maybeDigestOnJoin(dp); // D537
            }, 135L);
        {
            final Player scp = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (scp.isOnline()) town.sunshine.corerpg.p1.EmberScrapPath.maybeAfterProgress(scp); // D538
            }, 140L);
        {
            final Player dop = player;
            getServer().getScheduler().runTaskLater(this, () -> {
                if (dop.isOnline()) town.sunshine.corerpg.p1.EmberDosePath.maybeAfterProgress(dop); // D539
            }, 145L);
        }
        }
        }
        }
        }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (petService != null) petService.onPlayerQuit(event.getPlayer());
        if (arenaService != null) arenaService.onPlayerQuit(event.getPlayer());
        if (abyssSettleService != null) abyssSettleService.onPlayerQuit(event.getPlayer());
        if (skillService != null) skillService.onQuit(id);
        if (flexSkillService != null) flexSkillService.onQuit(id);
        if (gearPassiveService != null) gearPassiveService.onQuit(id);
        if (emberCombat != null) emberCombat.onQuit(id);
        boards.remove(id);
        dataStore.unload(id);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null && questService != null && !(entity instanceof Player)) killer = questService.anyPlayerDamager(entity.getUniqueId(), entity.getWorld()); // 1.8.1: last player who hit it, same world, any time
        if (calamityService != null && calamityService.isPublicCalamityBoss(entity)) { // 2026-09-27: guild-boss variant no longer settles as calamity
            if (LegacyGate.blocksCalamitySettle(town.sunshine.corerpg.p1.EmberMode.active(), legacyPayoutGuard())) { // D200 S0-4 ④
                getLogger().info("[legacy_gate] payout skip calamity settle (P1 on)");
            } else {
                calamityService.onCalamityKilled(killer);
            }
        }
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(entity)) return; // G04 E02/E11: P1 run kills pay only through the run settlement
        if (town.sunshine.corerpg.p1.EmberAfkService.blocksLegacyPayout(entity.getWorld())) return; // D177: no kill coin / kill XP / legacy drops in the P1 挂机庭
        if (killer != null && setService != null && !(entity instanceof Player)) {
            setService.onKill(killer);
        }
        if (killer != null && questService != null && !(entity instanceof Player)) questService.onKill(killer, entity);
        if (killer == null || !isQualifyingKill(entity)) return;
        if (legacyKillPayoutBlocked(killer.getWorld().getName())) return; // D200 S0-4 ②: no legacy kill coin / activity / XP / bounty outside P1 worlds while P1 is on
        ensureBounty(killer);
        PlayerData data = dataStore.get(killer.getUniqueId());
        data.addKillToday();
        if (coinKillReward > 0) {
            int capCoin = getConfig().getInt("afk_caps.kill_coin", -1);
            boolean inst = !getConfig().getStringList("afk_caps.worlds").contains(killer.getWorld().getName());
            boolean allowCoin = !getConfig().getBoolean("afk_caps.enabled", false) || capCoin < 0 || inst;
            if (!allowCoin) {
                int cc = data.periodCount("afk_coin", DailyService.today());
                allowCoin = cc < capCoin || Math.random() < afkOverChance(cc, capCoin);
            }
            if (allowCoin) {
                data.addCoin(coinKillReward);
                if (!inst) data.addPeriodCount("afk_coin", DailyService.today(), coinKillReward);
            }
        }
        if (data.getActivityFromKills() < actKillCap) {
            int room = actKillCap - data.getActivityFromKills();
            int give = Math.min(actKillPoints, room);
            if (give > 0) {
                data.setActivityFromKills(data.getActivityFromKills() + give);
                data.addActivity(give);
            }
        }
        if (progressService != null) progressService.grantEmberXp(killer, "kill");
        if (!data.isBountyClaimed()) {
            BountyDef b = findBounty(data.getBountyId());
            if (b != null && data.getBountyProgress() < b.killTarget) data.addBountyProgress(1);
        }
        dataStore.flushMutation(killer.getUniqueId());
        refreshBoard(killer);
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
        if (questService != null && questService.isEnabled()) {
            String q = questService.objective(player);
            if (!q.isEmpty()) setLine(obj, ChatColor.GOLD + "主线 " + ChatColor.WHITE + q, 7);
        }
        setLine(obj, ChatColor.GOLD + "币 " + ChatColor.WHITE + data.getCoin(), 6);
        setLine(obj, ChatColor.GREEN + "活跃 " + ChatColor.WHITE + data.getActivity() + "/100", 5);
        setLine(obj, ChatColor.YELLOW + "击杀 " + ChatColor.WHITE + data.getKillsToday(), 4);
        setLine(obj, ChatColor.RED + "碎片 " + ChatColor.WHITE + countNamed(player, shardNeedle), 3);
        setLine(obj, ChatColor.GRAY + "骨尘 " + ChatColor.WHITE + countNamed(player, dustNeedle), 2);
        setLine(obj, ChatColor.AQUA + "附魔晶 " + ChatColor.WHITE + countNamed(player, crystalNeedle), 1);
        setLine(obj, ChatColor.DARK_GRAY + "/ember", 0);
        if (emberRuns != null && emberRuns.cosmetics() != null) { // D107 nameplate flairs
            try { emberRuns.cosmetics().syncFlair(board); } catch (RuntimeException ex) { getLogger().fine("flair sync: " + ex); }
        }
        player.setScoreboard(board);
    }

    private static void setLine(Objective obj, String text, int score) {
        if (text.length() > 40) text = text.substring(0, 40);
        obj.getScore(text).setScore(score);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hub")) return cmdHub(sender);
        if (!command.getName().equalsIgnoreCase("corerpg")
                && !label.equalsIgnoreCase("crpg") && !label.equalsIgnoreCase("rpg")) return false;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) { sendHelp(sender); return true; }
        String sub = args[0].toLowerCase();
        if (legacyRouteRefused(sender, args)) return true;
        if ("reload".equals(sub)) {
            if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            reloadLocal();
            sender.sendMessage(ChatColor.GREEN + "[CoreRpg] 配置已重载 (storage=" + storageMode + ")");
            return true;
        }
        if ("storage".equals(sub)) {
            if (args.length >= 2 && "guard".equalsIgnoreCase(args[1]) && dbGuard != null) return dbGuard.cmd(sender, args);
            return cmdStorage(sender);
        }
        if ("invsnap".equals(sub)) {
            if (invSnap == null) { sender.sendMessage(ChatColor.RED + "invsnap 未加载"); return true; }
            return invSnap.cmd(sender, args);
        }
        if ("admin".equals(sub)) {
            return cmdAdmin(sender, args);
        }
        if ("status".equals(sub)) return cmdStatus(sender);
        if ("spawn".equals(sub)) return cmdSpawn(sender, args);
        if ("coin".equals(sub)) return cmdCoin(sender, args);
        if ("sign".equals(sub)) return cmdSign(sender);
        if ("xpreward".equals(sub)) return progressService.cmdAdminGrant(sender, args, false);
        if ("passxp".equals(sub)) return progressService.cmdAdminGrant(sender, args, true);
        if ("progress".equals(sub)) return progressService.cmdProgress(sender, args);
        if ("loot".equals(sub)) return lootService.cmd(sender, args);
        if ("stats".equals(sub) || "属性".equals(sub)) return statService.cmd(sender, args);
        if ("p1".equals(sub) || "ember".equals(sub)) return emberCommand.cmd(sender, args);
        if ("forge".equals(sub) || "锻造".equals(sub)) return forgeService.cmd(sender, args);
        if ("part".equals(sub) || "部件".equals(sub)) return partService.cmd(sender, args);
        if ("mmgiveall".equals(sub)) return cmdMmGiveAll(sender, args);
        if ("mmgive".equals(sub) || "mmxp".equals(sub)) return cmdMmCredit(sender, args, "mmxp".equals(sub));
        if ("quest".equals(sub) || "mainline".equals(sub) || "主线".equals(sub)) return questService.cmd(sender, args);
        if ("hubbuild".equals(sub) || "hubplaza".equals(sub)) return hubPlazaService.cmd(sender, args);
        if ("hubambience".equals(sub) && hubAmbience != null) return hubAmbience.cmd(sender, args);
        if ("hubnpc".equals(sub) || "workshopnpc".equals(sub)) return hubNpcService.cmd(sender, args);
        if ("abyssbuild".equals(sub) || "abyssshaft".equals(sub)) return abyssShaftService.cmd(sender, args);
        if ("weeklybuild".equals(sub) || "weeklycorridor".equals(sub)) return weeklyCorridorService.cmd(sender, args);
        if ("elitebuild".equals(sub) || "elitecorridor".equals(sub)) return eliteCorridorService.cmd(sender, args);
        if ("dailybuild".equals(sub) || "dailycourtyard".equals(sub) || "courtyard".equals(sub)) return dailyCourtyardService.cmd(sender, args);
        if ("ashbuild".equals(sub) || "dailyash".equals(sub) || "ashcorridor".equals(sub)) return dailyAshCorridorService.cmd(sender, args);
        if ("cryptbuild".equals(sub) || "dailycrypt".equals(sub) || "crypt".equals(sub)) return dailyCryptService.cmd(sender, args);
        if ("tidebuild".equals(sub) || "dailytide".equals(sub) || "tide".equals(sub)) return dailyTideService.cmd(sender, args);
        if ("spirebuild".equals(sub) || "dailyspire".equals(sub) || "spire".equals(sub)) return dailySpireService.cmd(sender, args);
        if ("frostbuild".equals(sub) || "dailyfrost".equals(sub) || "frost".equals(sub)) return dailyFrostService.cmd(sender, args);
        if ("railbuild".equals(sub) || "dailyrail".equals(sub) || "rail".equals(sub)) return dailyRailService.cmd(sender, args);
        if ("raidbuild".equals(sub) || "raidhall".equals(sub)) return raidHallService.cmd(sender, args);
        if ("calamitybuild".equals(sub) || "eventbuild".equals(sub) || "calamitybasin".equals(sub)) return calamityBasinService.cmd(sender, args);
        if ("afk".equals(sub) || "挂机".equals(sub)) return afkTierService.cmd(sender, args);
        if ("life".equals(sub) || "vendor".equals(sub) || "补给".equals(sub) || "生活".equals(sub)) return lifeService.cmd(sender, args);
        if ("level".equals(sub) || "lv".equals(sub) || "等级".equals(sub)) {
            if (!requirePlayer(sender)) return true;
            progressService.cmdLevel((Player) sender);
            return true;
        }
        if ("activity".equals(sub)) return cmdActivity(sender, args);
        if ("bounty".equals(sub)) return cmdBounty(sender, args);
        if ("enhance".equals(sub)) return cmdEnhance(sender, args);
        if ("socket".equals(sub)) return cmdSocket(sender, args);
        if ("calamity".equals(sub)) return cmdCalamity(sender, args);
        if ("abyss".equals(sub)) {
            if (abyssSettleService != null) return abyssSettleService.cmdRoot(sender, args);
            return cmdAbyss(sender);
        }
        if ("elite".equals(sub) || "eliteweekly".equals(sub) || "精英试炼".equals(sub)) {
            if (eliteService != null) return eliteService.cmdRoot(sender, args);
            sender.sendMessage(ChatColor.RED + "[精英试炼] 服务未就绪");
            return true;
        }
        if ("covenant".equals(sub)) return cmdCovenant(sender, args);
        if ("talent".equals(sub)) return cmdTalent(sender, args);
        if ("cash".equals(sub)) return cmdCash(sender, args);
        if ("stamina".equals(sub) || "体力".equals(sub)) {
            if (staminaService != null) return staminaService.cmdRoot(sender, args);
            sender.sendMessage(ChatColor.RED + "[体力] 服务未就绪");
            return true;
        }
        if ("enter".equals(sub) || "进本".equals(sub)) {
            if (ticketEntryService != null) return ticketEntryService.cmdEnter(sender, args);
            sender.sendMessage(ChatColor.RED + "[进本] 服务未就绪");
            return true;
        }
        if ("tickets".equals(sub) || "ticket".equals(sub)) return cmdTickets(sender, args);
        if ("shop".equals(sub)) return cmdShop(sender, args);
        if ("monthly".equals(sub)) return cmdMonthly(sender, args);
        if ("vip".equals(sub)) return cmdVip(sender, args);
        if ("pass".equals(sub) || "战令".equals(sub)) return cmdPass(sender, args);
        if ("enderchest".equals(sub) || "ec".equals(sub) || "末影箱".equals(sub)) return cmdEnderChest(sender);
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
        if ("guild".equals(sub) || "alliance".equals(sub) || "盟约".equals(sub)) {
            if (guildService != null) guildService.cmdRoot(sender, args);
            return true;
        }
        if ("arena".equals(sub) || "竞技".equals(sub) || "pvp".equals(sub)) {
            if (arenaService != null) arenaService.cmdRoot(sender, args);
            return true;
        }
        if ("auction".equals(sub) || "寄售".equals(sub) || "ah".equals(sub)) {
            // D177 exploit review: the legacy coin auction is a command-only path that moves 余烬币 / core fragments between
            // accounts; P1 has no trading (D73, book §19.5) → closed for players while P1 is on (admins and legacy mode keep it)
            if (town.sunshine.corerpg.p1.EmberMode.active() && !sender.hasPermission("corerpg.admin")
                    && !(emberMode != null && emberMode.config() != null && emberMode.config().getBoolean("legacy_auction", false))) {
                sender.sendMessage(ChatColor.GRAY + "[寄售] P1 首版不开放交易（D73），余烬币和材料只能自己用。");
                return true;
            }
            if (auctionService != null) auctionService.cmdRoot(sender, args);
            return true;
        }
        if ("warehouse".equals(sub) || "仓库".equals(sub) || "wh".equals(sub)) {
            if (warehouseService != null) warehouseService.cmdRoot(sender, args);
            return true;
        }
        if ("skill".equals(sub) || "技能".equals(sub)) {
            if (skillService != null) skillService.cmdRoot(sender, args);
            return true;
        }
        if ("flex".equals(sub) || "轻技".equals(sub)) {
            if (flexSkillService != null) flexSkillService.cmdRoot(sender, args);
            return true;
        }
        if ("raid".equals(sub) || "团本".equals(sub)) {
            if (raidService != null) raidService.cmdRoot(sender, args);
            return true;
        }
        if ("set".equals(sub) || "套装".equals(sub)) {
            if (setService != null) setService.cmdRoot(sender, args);
            return true;
        }
        sendHelp(sender);
        return true;
    }

    /** D200 / ARCH S0-4: ember-v1.yml legacy_gate.payout_guard (default true). */
    public boolean legacyPayoutGuard() {
        org.bukkit.configuration.ConfigurationSection gate = (emberMode != null && emberMode.config() != null)
                ? emberMode.config().getConfigurationSection("legacy_gate") : null;
        return gate == null || gate.getBoolean("payout_guard", true);
    }

    private java.util.Set<String> legacyGateList(String key) {
        org.bukkit.configuration.ConfigurationSection gate = (emberMode != null && emberMode.config() != null)
                ? emberMode.config().getConfigurationSection("legacy_gate") : null;
        java.util.Set<String> out = new java.util.HashSet<String>();
        if (gate != null) for (String v : gate.getStringList(key)) if (v != null) out.add(v.trim().toLowerCase(java.util.Locale.ROOT));
        return out;
    }

    /** D200 S0-4 ①: legacy ember / pass XP source closed while P1 is on (legacy_gate.xp_sources_allow re-opens one). */
    public boolean legacyXpBlocked(String source) {
        boolean p1 = town.sunshine.corerpg.p1.EmberMode.active();
        if (!p1) return false;
        return LegacyGate.blocksLegacyXp(true, legacyPayoutGuard(), source, legacyGateList("xp_sources_allow"));
    }

    /** D200 S0-4 ② ③: legacy kill / MM payouts closed in this world while P1 is on (legacy_gate.kill_payout_worlds re-opens one). */
    public boolean legacyKillPayoutBlocked(String world) {
        boolean p1 = town.sunshine.corerpg.p1.EmberMode.active();
        if (!p1) return false;
        return LegacyGate.blocksLegacyKillPayout(true, legacyPayoutGuard(), world, legacyGateList("kill_payout_worlds"));
    }

    /**
     * D199 / ARCH S0-3: deny-by-default /corerpg route gate while P1 is on (non-admin players only; console, OP and
     * corerpg.admin pass). Whitelist = ember-v1.yml legacy_gate.allow (LegacyGate.DEFAULT_ALLOW if missing).
     */
    private boolean legacyRouteRefused(CommandSender sender, String[] args) {
        boolean p1 = town.sunshine.corerpg.p1.EmberMode.active();
        if (!p1 || !(sender instanceof Player)) return false;
        boolean privileged = sender.isOp() || sender.hasPermission("corerpg.admin");
        org.bukkit.configuration.ConfigurationSection gate = (emberMode != null && emberMode.config() != null)
                ? emberMode.config().getConfigurationSection("legacy_gate") : null;
        if (gate != null && !gate.getBoolean("route_whitelist", true)) return false;
        java.util.Map<String, java.util.Set<String>> allow = null;
        org.bukkit.configuration.ConfigurationSection sec = gate != null ? gate.getConfigurationSection("allow") : null;
        if (sec != null) {
            allow = new java.util.HashMap<String, java.util.Set<String>>();
            for (String k : sec.getKeys(false)) {
                java.util.Set<String> acts = new java.util.HashSet<String>();
                if (sec.isList(k)) {
                    for (Object o : sec.getList(k)) acts.add(o == null ? "" : String.valueOf(o).toLowerCase(java.util.Locale.ROOT));
                } else {
                    acts.add(String.valueOf(sec.get(k)).trim().toLowerCase(java.util.Locale.ROOT));
                }
                allow.put(LegacyGate.canonical(k), acts);
            }
        }
        if (!LegacyGate.refuseRoute(true, true, privileged, allow, args)) return false;
        sender.sendMessage(ChatColor.GRAY + "[余烬] " + LegacyGate.ROUTE_CLOSED_MSG);
        getLogger().info("[legacy_gate] deny " + sender.getName() + " /corerpg " + String.join(" ", args));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[CoreRpg] " + ChatColor.YELLOW
                + "/corerpg spawn|status|coin|sign|activity|bounty|enhance|socket|scrap|reforge|calamity|abyss|elite|raid|enter|set|covenant|talent|skill|cash|stamina|tickets|shop|monthly|vip|pass|enderchest|mail|friend|settings|ladder|pet|guild|arena|pvp|auction|warehouse|storage|reload|help");
        sender.sendMessage(ChatColor.GRAY + "  coin [give <玩家> <数量>] · sign · activity [claim] · bounty [claim]");
        sender.sendMessage(ChatColor.GRAY + "  enhance [info] · socket list|insert <gemId>|remove <slot>");
        sender.sendMessage(ChatColor.GRAY + "  scrap [info] · reforge");
        sender.sendMessage(ChatColor.GOLD + "  quest" + ChatColor.GRAY + " · 主线（引路人·灰烛）");
        sender.sendMessage(ChatColor.GOLD + "  hubnpc" + ChatColor.GRAY + " · 工坊 NPC ensure|purge|count|list|reload");
        sender.sendMessage(ChatColor.GRAY + "  calamity [status|forceopen|forceend|trigger] · abyss [progress|settle|evacuate] · elite [start|weekly-first|status]");
        sender.sendMessage(ChatColor.GRAY + "  raid [ring|claim-ring|grant-ring] · set");
        sender.sendMessage(ChatColor.GRAY + "  covenant [set <id>|reset] · talent [info|unlock|reset|grant] · skill [info|kit|shape <fan|line|ring>|dir <forward|back>] · flex [equip|unequip|cast|info]");
        sender.sendMessage(ChatColor.GRAY + "  cash · tickets · shop buy daily_ticket|pass_unlock|weekly_ticket · monthly [buy] · vip [claim]");
        sender.sendMessage(ChatColor.GRAY + "  mail [read|claim|delete|send] · friend [add|accept|deny|remove|invite|mentor]");
        sender.sendMessage(ChatColor.GRAY + "  settings [sound|tip|privacy]");
        sender.sendMessage(ChatColor.GRAY + "  ladder [me|power|abyss|speed]");
        sender.sendMessage(ChatColor.GRAY + "  pet [list|summon|dismiss|unlock|feed]");
        sender.sendMessage(ChatColor.GRAY + "  guild|alliance [create|info|invite|accept|leave|kick|donate|boss]");
        sender.sendMessage(ChatColor.GRAY + "  arena|pvp [queue 1v1|2v2|leave|stats|claim|forfeit] · auction [list|sell|buy|cancel]");
        // D255 / S0-10: player help shows view-only; write ops only on admin line
        sender.sendMessage(ChatColor.GRAY + "  warehouse [list|info] · 存取请用枢纽「仓库」（P1 下不可 deposit/withdraw/unlock）");
        sender.sendMessage(ChatColor.DARK_GRAY + "  admin: warehouse deposit|withdraw|unlock · xpreward · passxp · progress · pass season reset · enhance set · calamity forceopen/forceend · p1 status/debug/world · talent grant · cash give · mail send · ladder set/refresh · migrate-yaml-to-mysql");
        sender.sendMessage(ChatColor.DARK_GRAY + "  storage — 显示 yaml|mysql 与 ping · storage guard [release] — 数据库断线保护");
        sender.sendMessage(ChatColor.DARK_GRAY + "  invsnap list|view|restore|diff|take <玩家> [id] — 背包/末影箱快照（管理员）");
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
        // 1.4.10 audit: was open to every player (console-dispatched mm spawn of any mob/amount)
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
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
        if (town.sunshine.corerpg.p1.EmberMode.active()) { // D180: P1 sign-in only (the legacy 100-coin payout was outside the P1 sim economy, D63)
            if (emberSign != null) return emberSign.legacySign(sender);
            sender.sendMessage(ChatColor.YELLOW + "[余烬] 签到在 主菜单 → 签到 · 在线");
            return true;
        }
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
        if (progressService != null) {
            progressService.grantPassXp(p, "sign");
            progressService.grantEmberXp(p, "sign");
        }
        if (questService != null) questService.onEvent(p, "sign");
        refreshBoard(p);
        return true;
    }

    private boolean cmdActivity(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (town.sunshine.corerpg.p1.EmberMode.active()) { // D180: no legacy activity chests (coin) in P1 → daily online-time rewards
            sender.sendMessage(ChatColor.YELLOW + "[余烬] P1 没有活跃宝箱：在线时长奖励在 主菜单 → 签到 · 在线");
            if (emberSign != null) emberSign.openMenu((Player) sender);
            return true;
        }
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
        if (town.sunshine.corerpg.p1.EmberMode.active()) { // D180: legacy bounty coin closed in P1; the P1 每日委托 is paid by main-story clears
            sender.sendMessage(ChatColor.YELLOW + "[余烬] P1 的每日委托在主线本里结算（当天第 3 局），旧悬赏不开放");
            return true;
        }
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
            // 1.7.0: bounty (1 claim/day) → ember XP + pass XP (progress.yml sources.bounty)
            if (progressService != null) {
                progressService.grantEmberXp(p, "bounty");
                progressService.grantPassXp(p, "bounty");
            }
            if (questService != null) questService.onEvent(p, "bounty");
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
    /**
     * 1.8.1: MythicMobs death rewards credited to the last player who hit the mob (15 s), not MM's &lt;trigger&gt;
     * (which was "Unknown" when a mob, the sun or a skill landed the final blow).
     * /corerpg mmgive <mob uuid> <niId> [n] · /corerpg mmxp <mob uuid> <elite|boss>   (console, MM ~onDeath @Self)
     */

    private boolean cmdMmCredit(CommandSender sender, String[] args, boolean xp) {
        if (!sender.hasPermission("corerpg.admin")) return true;
        if (args.length < 3) return true;
        UUID id;
        try { id = UUID.fromString(args[1]); } catch (IllegalArgumentException e) { return true; }
        org.bukkit.entity.Entity en = Bukkit.getEntity(id);
        Player p = null;
        String via = "hit";
        if (en instanceof LivingEntity) { p = ((LivingEntity) en).getKiller(); via = "killer"; }
        if (p == null && questService != null) { p = questService.anyPlayerDamager(id, en == null ? null : en.getWorld()); via = "hit"; }
        // nobody ever hit it → no reward (mob died to environment / despawn cleanup)
        if (getConfig().getBoolean("debug.mm_credit", false)) {
            String cause = "?";
            if (en != null && en.getLastDamageCause() != null) {
                org.bukkit.event.entity.EntityDamageEvent ld = en.getLastDamageCause();
                cause = ld.getCause().name() + (ld instanceof org.bukkit.event.entity.EntityDamageByEntityEvent
                        ? "/" + ((org.bukkit.event.entity.EntityDamageByEntityEvent) ld).getDamager().getType().name() : "");
            }
            getLogger().info("mmcredit " + args[0] + " " + args[1] + " " + args[2] + " -> " + (p == null ? "none" : p.getName())
                    + " (" + via + ", en=" + (en == null ? "gone" : en.getType().name()) + ", last=" + cause + ")");
        }
        if (p == null || !p.isOnline()) return true;
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(en) || town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(p)) return true; // G04 E02
        if (town.sunshine.corerpg.p1.EmberAfkService.blocksLegacyPayout(p.getWorld())) return true; // D177: P1 挂机庭 pays only by rounds
        if (legacyKillPayoutBlocked(p.getWorld().getName())) { // D200 S0-4 ③: legacy MM drops / XP closed while P1 is on
            getLogger().info("[legacy_gate] payout skip " + args[0] + " " + args[2] + " " + p.getName() + " @" + p.getWorld().getName());
            return true;
        }
        if (xp) {
            progressService.grantKillLevels(p, args[2]);
            progressService.grantEmberXp(p, args[2]);
        } else {
            int n = 1;
            if (args.length >= 4) try { n = Math.max(1, Integer.parseInt(args[3])); } catch (NumberFormatException ignored) { }
            n = afkCapped(p, args[2], n);
            if (n > 0) niBridge.giveNiItem(p, args[2], n);
        }
        return true;
    }

    /**
     * 1.12.0: /corerpg mmgiveall <mob uuid> <niId> [n] — every player in the mob's world who hit it or is within
     * 64 blocks (raid participants in the instance world). Falls back to mmgive credit when the mob is gone.
     */
    private boolean cmdMmGiveAll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin") || args.length < 3) return true;
        UUID id;
        try { id = UUID.fromString(args[1]); } catch (IllegalArgumentException e) { return true; }
        org.bukkit.entity.Entity en = Bukkit.getEntity(id);
        if (en == null) return cmdMmCredit(sender, args, false);
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(en)) return true; // G04 E02
        if (town.sunshine.corerpg.p1.EmberAfkService.blocksLegacyPayout(en.getWorld())) return true; // D177
        int n = 1;
        if (args.length >= 4) try { n = Math.max(1, Integer.parseInt(args[3])); } catch (NumberFormatException ignored) { }
        int given = 0;
        for (Player p : en.getWorld().getPlayers()) {
            if (!p.isOnline()) continue;
            if (p.getLocation().distanceSquared(en.getLocation()) > 64 * 64) continue;
            niBridge.giveNiItem(p, args[2], n);
            given++;
        }
        getLogger().info("mmgiveall " + args[2] + " x" + n + " -> " + given + " players in " + en.getWorld().getName());
        return true;
    }

    /** 1.11.0: open-world drop daily caps; 1.15.22: tier-2 softcap when periodCount >= 2×cap. */
    private final java.util.Set<String> afkCapNotified = new java.util.HashSet<String>();

    /** Softcap roll chance by count: [cap, 2×cap) → over_chance; ≥2×cap → over_chance_2. */
    double afkOverChance(int count, int cap) {
        if (count >= 2 * cap) return getConfig().getDouble("afk_caps.over_chance_2", 0.08);
        return getConfig().getDouble("afk_caps.over_chance", 0.25);
    }

    int afkCapped(Player p, String item, int n) {
        if (!getConfig().getBoolean("afk_caps.enabled", false)) return n;
        if (!getConfig().getStringList("afk_caps.worlds").contains(p.getWorld().getName())) return n;
        int cap = getConfig().getInt("afk_caps.items." + item, -1);
        if (cap < 0) return n;
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        double over1 = getConfig().getDouble("afk_caps.over_chance", 0.25);
        int out = 0;
        for (int i = 0; i < n; i++) {
            int c = d.periodCount("afk_" + item, today);
            double over = afkOverChance(c, cap);
            if (c < cap || Math.random() < over) { out++; d.addPeriodCount("afk_" + item, today, 1); }
            if (c >= cap && afkCapNotified.add(p.getUniqueId() + ":" + today + ":t1")) {
                p.sendMessage(ChatColor.GRAY + "[余烬] 今日野外掉落已达收益上限，之后掉率降为 " + (int) Math.round(over1 * 100)
                        + "%。去打日常/周本/深渊，或钓鱼做饭吧。（每日 0 点重置）");
            }
            if (c >= 2 * cap && afkCapNotified.add(p.getUniqueId() + ":" + today + ":t2")) {
                p.sendMessage(ChatColor.DARK_GRAY + "[余烬] 挂机收益再降，建议去打日常/周本。");
            }
        }
        return out;
    }

    /** 1.8.1: /hub · /spawn — players had no way back from ember_afk (console mvtp to the hub spawn). */
    private final Map<UUID, Long> hubCooldown = new HashMap<UUID, Long>();
    private boolean cmdHub(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        String hub = getConfig().getString("hub_world", "ember_hub");
        if (p.getWorld().getName().equals(hub) && p.getLocation().distance(p.getWorld().getSpawnLocation()) < 3) {
            p.sendMessage(ChatColor.GRAY + "[余烬] 你已经在枢纽出生点了。");
            return true;
        }
        Long last = hubCooldown.get(p.getUniqueId());
        if (last != null && System.currentTimeMillis() - last < 5000) { p.sendMessage(ChatColor.RED + "[余烬] 稍等几秒再回城。"); return true; }
        hubCooldown.put(p.getUniqueId(), System.currentTimeMillis());
        if (questService != null && questService.isInstanceWorld(p.getWorld())) {
            p.sendMessage(ChatColor.RED + "[余烬] 副本中请用 /dp leave 离开。");
            return true;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + p.getName() + " " + hub);
        p.sendMessage(ChatColor.GREEN + "[余烬] 已回到枢纽。引路人·灰烛 在出生点北边。");
        return true;
    }

    private boolean cmdCalamity(CommandSender sender, String[] args) {
        String act = args.length >= 2 ? args[1].toLowerCase() : "status";
        if ("join".equals(act) || "go".equals(act)) {
            // 1.7.0: public calamity world entry with level gate (players have no mvtp permission; console teleports)
            if (!requirePlayer(sender)) return true;
            Player p = (Player) sender;
            PlayerData d = dataStore.get(p.getUniqueId());
            if (progressService != null && !p.isOp() && !progressService.passesGate(d, "calamity")) {
                p.sendMessage(progressService.gateRefusal(d, "calamity", "灾厄公共窗"));
                return true;
            }
            String world = getConfig().getString("calamity_world", "ember_event");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp " + p.getName() + " " + world);
            p.sendMessage(ChatColor.DARK_RED + "[灾厄] 已前往 " + world + " · /corerpg calamity status 查看窗口");
            if (questService != null) questService.onEvent(p, "calamity_join");
            return true;
        }
        if ("forceopen".equals(act) || "trigger".equals(act) || "spawn".equals(act) || "force".equals(act)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            calamityService.forceOpen(sender);
            return true;
        }
        if ("forceend".equals(act) || "end".equals(act)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            calamityService.forceEnd(sender);
            return true;
        }
        calamityService.sendStatus(sender);
        return true;
    }

    /** Fallback when AbyssSettleService is unavailable — S0: abyss entry costs stamina (no tickets). */
    private boolean cmdAbyss(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        Player p = (Player) sender;
        PlayerData data = dataStore.get(p.getUniqueId());
        int cost = staminaService != null ? staminaService.costOf("abyss") : 0;
        p.sendMessage(ChatColor.DARK_PURPLE + "[余烬深渊] " + ChatColor.LIGHT_PURPLE + "无尽波次下潜 · 进本消耗 "
                + cost + " 体力（与日常同池）");
        if (staminaService != null) {
            p.sendMessage(ChatColor.GRAY + "  当前体力 §f" + staminaService.getStamina(data) + "§7/§f"
                    + staminaService.getMax(data) + ChatColor.GRAY + " · 历史最深 §f" + data.getAbyssBest() + ChatColor.GRAY + " 层");
        }
        p.sendMessage(ChatColor.GRAY + "  进本：打开枢纽菜单 → 深渊（/corerpg enter abyss）");
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
            questRecheck(p);
            return true;
        }
        if ("reset".equals(act) || "clear".equals(act)) {
            covenantService.cmdReset(p);
            return true;
        }
        covenantService.cmdShow(p);
        return true;
    }

    /** 1.11.0: re-evaluate the mainline step next tick (enhance / covenant / talent done). */
    public void questRecheck(Player p) {
        if (questService != null) questService.onLevelChanged(p);
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
            questRecheck(p);
            return true;
        }
        if ("reset".equals(act)) {
            talentService.cmdReset(p);
            return true;
        }
        talentService.cmdShow(p);
        return true;
    }

    private boolean cmdTickets(CommandSender sender, String[] args) {
        if (args.length >= 2 && "consume".equalsIgnoreCase(args[1])) {
            if (ticketEntryService != null) return ticketEntryService.cmdConsume(sender, args);
            sender.sendMessage(ChatColor.RED + "[门票] 服务未就绪");
            return true;
        }
        if (!requirePlayer(sender)) return true;
        if (ticketGrantService != null) {
            ticketGrantService.cmdShowTickets((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "[门票] 服务未就绪");
        }
        return true;
    }

    private boolean cmdCash(CommandSender sender, String[] args) {
        if (args.length >= 2 && "give".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            if (args.length < 4) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg cash give <玩家> <数量> [nocount]");
                return true;
            }
            int amount;
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数量无效");
                return true;
            }
            if (cashService != null) {
                cashService.cmdCashGive(sender, args[2], amount);
                // 1.6.0: cash give is the top-up stand-in → counts toward 勋阶 unless "nocount" (compensation etc.)
                boolean count = !(args.length >= 5 && "nocount".equalsIgnoreCase(args[4]));
                Player tp = Bukkit.getPlayerExact(args[2]);
                if (count && amount > 0 && tp != null && progressService != null) progressService.recordTopUp(tp, amount);
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
        if (args.length >= 3 && "buy".equalsIgnoreCase(args[1])
                && "pass_unlock".equalsIgnoreCase(args[2])) {
            if (cashService != null) cashService.cmdShopBuyPassUnlock((Player) sender);
            return true;
        }
        if (args.length >= 3 && "buy".equalsIgnoreCase(args[1])
                && "weekly_ticket".equalsIgnoreCase(args[2])) {
            if (cashService != null) cashService.cmdShopBuyWeeklyTicket((Player) sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "/corerpg shop buy daily_ticket|pass_unlock|weekly_ticket");
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

    private boolean cmdVip(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.use");
            return true;
        }
        if (cashService == null) {
            sender.sendMessage(ChatColor.RED + "[勋阶] 服务未就绪");
            return true;
        }
        if (args.length >= 2 && "claim".equalsIgnoreCase(args[1])) {
            cashService.cmdVipClaim((Player) sender);
            return true;
        }
        cashService.cmdVipShow((Player) sender);
        return true;
    }

    /** 1.4.10: /corerpg pass free — free-track supply, once per day (menu used to mail it on every click). */
    private boolean cmdPass(CommandSender sender, String[] args) {
        if (args.length >= 2 && "season".equalsIgnoreCase(args[1]) && progressService != null) {
            progressService.cmdSeason(sender, args);
            return true;
        }
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.use");
            return true;
        }
        if (cashService == null) {
            sender.sendMessage(ChatColor.RED + "[战令] 服务未就绪");
            return true;
        }
        if (args.length >= 2 && "free".equalsIgnoreCase(args[1])) {
            cashService.cmdPassFree((Player) sender);
            return true;
        }
        if (args.length >= 2 && "claim".equalsIgnoreCase(args[1]) && progressService != null) {
            progressService.cmdPassClaim((Player) sender);
            return true;
        }
        if (args.length >= 2 && "rewards".equalsIgnoreCase(args[1]) && progressService != null) {
            progressService.cmdPassRewards((Player) sender);
            return true;
        }
        cashService.cmdPassShow((Player) sender);
        return true;
    }

    /** 1.4.10: /corerpg enderchest — open own ender chest (storage menu button; no plugin provided /enderchest). */
    private boolean cmdEnderChest(CommandSender sender) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.use");
            return true;
        }
        Player p = (Player) sender;
        p.openInventory(p.getEnderChest());
        return true;
    }

    private boolean cmdScrap(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return true;
        if (!sender.hasPermission("corerpg.scrap") && !sender.hasPermission("corerpg.use")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.scrap");
            return true;
        }
        Player p = (Player) sender;
        String mode = args.length >= 2 && "info".equalsIgnoreCase(args[1]) ? "info"
                : args.length >= 2 && ("confirm".equalsIgnoreCase(args[1]) || "确认".equals(args[1])) ? "confirm" : "preview";
        if (scrapService != null) scrapService.cmdScrap(p, mode, args.length >= 3 ? args[2] : null); // B2.137 click-confirm
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
