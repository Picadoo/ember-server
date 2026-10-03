package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import town.sunshine.corerpg.storage.MysqlStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Lightweight guild / 盟约 — create, invite, donate; weekly guild boss.
 * docs/design/DESIGN-ember-guild-ladder.md §1
 */
public final class GuildService {

    public static final String PREFIX = ChatColor.GOLD + "[盟约] " + ChatColor.RESET;

    public static final class Guild {
        public String id;
        public String name;
        public UUID leaderUuid;
        public final List<UUID> members = new ArrayList<UUID>();
        public final List<UUID> officers = new ArrayList<UUID>();
        public final Map<String, Integer> contribution = new LinkedHashMap<String, Integer>();
        public String created = "";
        public boolean dirty;
    }

    private final JavaPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge niBridge;

    private boolean enabled = true;
    private int createCoinCost = 5000;
    private int createCrystalCost = 40;
    private boolean preferCoin = true;
    private int nameMin = 2;
    private int nameMax = 8;
    private final List<String> bannedNames = new ArrayList<String>();
    private int maxMembers = 20;
    private int dailyDonateCap = 50;
    private final Map<String, Integer> donateRates = new LinkedHashMap<String, Integer>();
    private int donateActivityPoints = 0;
    private boolean bossEnabled = false;
    private String bossStub = "暂未开放";
    private String bossDungeonId = "EmberGuildBoss";
    private int bossContributionCost = 20;
    private int bossWeeklyLimit = 1;
    private String bossStartCommand = "dp start-console {player} EmberGuildBoss";
    /** 1.4.9: one-time start pass (uuid → expiry ms) read by DP condition %corerpg_guildboss_pass%. */
    private final Map<UUID, Long> bossPass = new ConcurrentHashMap<UUID, Long>();
    private static final long BOSS_PASS_MS = 5000L;

    private final Map<String, Guild> byId = new ConcurrentHashMap<String, Guild>();
    private final Map<String, String> nameIndex = new ConcurrentHashMap<String, String>();
    /** invitee UUID -> guildId */
    private final Map<UUID, String> pendingInvites = new ConcurrentHashMap<UUID, String>();

    private File guildsDir;

    public GuildService(JavaPlugin plugin, PlayerDataStore dataStore, NiBridge niBridge) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.niBridge = niBridge;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "guild.yml");
        if (!file.exists()) {
            plugin.saveResource("guild.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("guild.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        createCoinCost = Math.max(0, cfg.getInt("create.coin_cost", 5000));
        createCrystalCost = Math.max(0, cfg.getInt("create.crystal_cost", 40));
        preferCoin = cfg.getBoolean("create.prefer_coin", true);
        nameMin = Math.max(1, cfg.getInt("name.min_chars", 2));
        nameMax = Math.max(nameMin, cfg.getInt("name.max_chars", 8));
        bannedNames.clear();
        List<?> banned = cfg.getList("name.banned");
        if (banned != null) {
            for (Object o : banned) {
                if (o != null) {
                    String s = String.valueOf(o).trim();
                    if (!s.isEmpty()) bannedNames.add(s.toLowerCase(Locale.ROOT));
                }
            }
        }
        maxMembers = Math.max(1, cfg.getInt("members.max", 20));
        dailyDonateCap = Math.max(1, cfg.getInt("donate.daily_cap", 50));
        donateRates.clear();
        ConfigurationSection rates = cfg.getConfigurationSection("donate.rates");
        if (rates != null) {
            for (String key : rates.getKeys(false)) {
                int v = rates.getInt(key, 0);
                if (v > 0) donateRates.put(key, Integer.valueOf(v));
            }
        }
        if (donateRates.isEmpty()) {
            donateRates.put("mat_ember_shard", Integer.valueOf(10));
            donateRates.put("mat_ember_bone_dust", Integer.valueOf(5));
        }
        donateActivityPoints = Math.max(0, cfg.getInt("donate.activity_points", 0));
        bossEnabled = cfg.getBoolean("boss.enabled", false);
        bossStub = cfg.getString("boss.stub_message", "暂未开放");
        if (bossStub == null || bossStub.isEmpty()) bossStub = "暂未开放";
        bossDungeonId = cfg.getString("boss.dungeon_id", "EmberGuildBoss");
        if (bossDungeonId == null || bossDungeonId.isEmpty()) bossDungeonId = "EmberGuildBoss";
        bossContributionCost = Math.max(0, cfg.getInt("boss.contribution_cost", 20));
        bossWeeklyLimit = Math.max(1, cfg.getInt("boss.weekly_limit", 1));
        bossStartCommand = cfg.getString("boss.start_command", "dp start-console {player} " + bossDungeonId);
        if (bossStartCommand == null || bossStartCommand.trim().isEmpty()) {
            bossStartCommand = "dp start-console {player} " + bossDungeonId;
        }

        guildsDir = new File(plugin.getDataFolder(), "guilds");
        if (!guildsDir.exists()) guildsDir.mkdirs();
        loadAllGuilds();
    }

    public boolean isEnabled() { return enabled; }

    private MysqlStorage mysqlOrNull() {
        if (plugin instanceof CoreRpgPlugin) {
            MysqlStorage m = ((CoreRpgPlugin) plugin).getMysqlStorage();
            if (m != null && m.isActive()) return m;
        }
        return null;
    }

    public File getGuildsDir() { return guildsDir; }

    /** In-memory guilds for migrate. */
    public java.util.Collection<Guild> allGuilds() { return byId.values(); }

    private void loadAllGuilds() {
        byId.clear();
        nameIndex.clear();
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                Map<String, String> all = mysql.loadAllGuildYaml();
                for (Map.Entry<String, String> e : all.entrySet()) {
                    try {
                        YamlConfiguration yaml = new YamlConfiguration();
                        yaml.loadFromString(e.getValue());
                        Guild g = loadGuildFromYaml(e.getKey(), yaml);
                        if (g != null) {
                            byId.put(g.id, g);
                            nameIndex.put(g.name.toLowerCase(Locale.ROOT), g.id);
                        }
                    } catch (Throwable t) {
                        plugin.getLogger().log(Level.WARNING, "Failed to load guild MySQL " + e.getKey(), t);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to list guilds from MySQL", e);
            }
            plugin.getLogger().info("Guilds loaded (mysql): " + byId.size());
            return;
        }
        File[] files = guildsDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            String n = f.getName();
            if (!n.endsWith(".yml")) continue;
            String id = n.substring(0, n.length() - 4);
            try {
                Guild g = loadGuild(id, f);
                if (g != null) {
                    byId.put(g.id, g);
                    nameIndex.put(g.name.toLowerCase(Locale.ROOT), g.id);
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Failed to load guild " + id, t);
            }
        }
        plugin.getLogger().info("Guilds loaded: " + byId.size());
    }

    private Guild loadGuild(String id, File file) {
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return loadGuildFromYaml(id, yaml);
    }

    private Guild loadGuildFromYaml(String id, FileConfiguration yaml) {
        Guild g = new Guild();
        g.id = id;
        g.name = yaml.getString("name", id);
        String leader = yaml.getString("leaderUuid", "");
        try {
            g.leaderUuid = UUID.fromString(leader);
        } catch (Exception e) {
            plugin.getLogger().warning("Guild " + id + " missing leaderUuid");
            return null;
        }
        g.created = yaml.getString("created", "");
        g.members.clear();
        for (String s : yaml.getStringList("members")) {
            try { g.members.add(UUID.fromString(s)); } catch (Exception ignored) {}
        }
        if (!g.members.contains(g.leaderUuid)) g.members.add(g.leaderUuid);
        g.officers.clear();
        for (String s : yaml.getStringList("officers")) {
            try { g.officers.add(UUID.fromString(s)); } catch (Exception ignored) {}
        }
        g.contribution.clear();
        ConfigurationSection sec = yaml.getConfigurationSection("contribution");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                g.contribution.put(key, Integer.valueOf(sec.getInt(key, 0)));
            }
        }
        g.dirty = false;
        return g;
    }

    private void saveGuild(Guild g) {
        if (g == null) return;
        FileConfiguration yaml = guildToYaml(g);
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                mysql.saveGuildYaml(g.id, ((YamlConfiguration) yaml).saveToString());
                g.dirty = false;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save guild MySQL " + g.id, e);
            }
            return;
        }
        if (guildsDir == null) return;
        try {
            yaml.save(new File(guildsDir, g.id + ".yml"));
            g.dirty = false;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save guild " + g.id, e);
        }
    }

    YamlConfiguration guildToYaml(Guild g) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("name", g.name);
        yaml.set("leaderUuid", g.leaderUuid.toString());
        List<String> mem = new ArrayList<String>();
        for (UUID u : g.members) mem.add(u.toString());
        yaml.set("members", mem);
        List<String> off = new ArrayList<String>();
        for (UUID u : g.officers) off.add(u.toString());
        yaml.set("officers", off);
        for (Map.Entry<String, Integer> e : g.contribution.entrySet()) {
            yaml.set("contribution." + e.getKey(), e.getValue());
        }
        yaml.set("created", g.created);
        return yaml;
    }

    public void saveAll() {
        for (Guild g : byId.values()) {
            if (g.dirty) saveGuild(g);
        }
    }

    public Guild getById(String id) {
        return id == null ? null : byId.get(id);
    }

    public Guild getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        String id = nameIndex.get(name.toLowerCase(Locale.ROOT));
        return id == null ? null : byId.get(id);
    }

    public Guild getPlayerGuild(UUID uuid) {
        if (uuid == null) return null;
        PlayerData data = dataStore.get(uuid);
        String gid = data.getGuildId();
        if (gid == null || gid.isEmpty()) return null;
        Guild g = byId.get(gid);
        if (g == null) {
            data.setGuildId("");
            dataStore.flushMutation(uuid);
            return null;
        }
        return g;
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "盟约系统未启用。");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.guild") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.guild");
            return;
        }
        if (args.length < 2) {
            cmdSelf(p);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if ("create".equals(sub)) {
            cmdCreate(p, args.length >= 3 ? joinFrom(args, 2) : null);
            return;
        }
        if ("info".equals(sub)) {
            cmdInfo(p, args.length >= 3 ? joinFrom(args, 2) : null);
            return;
        }
        if ("invite".equals(sub)) {
            cmdInvite(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("accept".equals(sub)) {
            cmdAccept(p);
            return;
        }
        if ("leave".equals(sub)) {
            cmdLeave(p);
            return;
        }
        if ("kick".equals(sub)) {
            cmdKick(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("donate".equals(sub)) {
            cmdDonate(p, args.length >= 3 ? args[2] : null, args.length >= 4 ? args[3] : null);
            return;
        }
        if ("boss".equals(sub)) {
            cmdBoss(p);
            return;
        }
        if ("help".equals(sub)) {
            sendHelp(p);
            return;
        }
        cmdSelf(p);
    }

    private void sendHelp(Player p) {
        p.sendMessage(PREFIX + ChatColor.YELLOW
                + "/corerpg guild create|info|invite|accept|leave|kick|donate|boss");
        p.sendMessage(ChatColor.GRAY + "  create <名> · donate <niId> <amt> · boss（周限·贡献）");
    }

    private void cmdSelf(Player p) {
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "你还没有盟约。");
            p.sendMessage(ChatColor.GRAY + "  /corerpg guild create <名>  （费 "
                    + createCoinCost + " 币 或 " + createCrystalCost + " 晶钻）");
            p.sendMessage(ChatColor.DARK_GRAY + "  invite|accept|leave|kick|donate|info|boss");
            String pend = pendingInvites.get(p.getUniqueId());
            if (pend != null) {
                Guild pg = byId.get(pend);
                if (pg != null) {
                    p.sendMessage(ChatColor.AQUA + "  待接受邀请: §f" + pg.name
                            + ChatColor.GRAY + " · /corerpg guild accept");
                }
            }
            return;
        }
        showGuild(p, g, true);
    }

    private void cmdInfo(Player p, String name) {
        Guild g;
        if (name == null || name.isEmpty()) {
            g = getPlayerGuild(p.getUniqueId());
            if (g == null) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg guild info [名]");
                return;
            }
        } else {
            g = getByName(name);
            if (g == null) {
                p.sendMessage(PREFIX + ChatColor.RED + "找不到盟约: " + name);
                return;
            }
        }
        showGuild(p, g, g.members.contains(p.getUniqueId()));
    }

    private void showGuild(Player p, Guild g, boolean memberView) {
        String leaderName = nameOf(g.leaderUuid);
        int totalContrib = 0;
        for (Integer v : g.contribution.values()) {
            if (v != null) totalContrib += v.intValue();
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW + g.name
                + ChatColor.GRAY + " · 成员 " + g.members.size() + "/" + maxMembers);
        p.sendMessage(ChatColor.GRAY + "  盟主 §f" + leaderName
                + ChatColor.GRAY + " · 总贡献 §f" + totalContrib
                + ChatColor.DARK_GRAY + " · 创建 " + g.created);
        if (memberView) {
            PlayerData data = dataStore.get(p.getUniqueId());
            ensureDonateDaily(data);
            int mine = contribOf(g, p.getUniqueId());
            p.sendMessage(ChatColor.GRAY + "  我的贡献 §f" + mine
                    + ChatColor.GRAY + " · 今日捐献 §f" + data.getGuildDonateToday()
                    + "/" + dailyDonateCap);
        }
        if (!g.officers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (UUID u : g.officers) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(nameOf(u));
            }
            p.sendMessage(ChatColor.GRAY + "  干部: §f" + sb);
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  invite|leave|kick|donate <niId> <amt>|boss");
    }

    private void cmdCreate(Player p, String rawName) {
        if (rawName == null || rawName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg guild create <名>");
            p.sendMessage(ChatColor.GRAY + "  名 " + nameMin + "～" + nameMax
                    + " 字 · 费 " + createCoinCost + " 币 或 " + createCrystalCost + " 晶钻");
            return;
        }
        String name = rawName.trim();
        int chars = name.codePointCount(0, name.length());
        if (chars < nameMin || chars > nameMax) {
            p.sendMessage(PREFIX + ChatColor.RED + "盟约名须 " + nameMin + "～" + nameMax + " 字。");
            return;
        }
        if (containsBanned(name)) {
            p.sendMessage(PREFIX + ChatColor.RED + "盟约名包含禁用词。");
            return;
        }
        if (getByName(name) != null) {
            p.sendMessage(PREFIX + ChatColor.RED + "盟约名已被占用: " + name);
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        if (data.getGuildId() != null && !data.getGuildId().isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "你已在盟约中，请先 leave。");
            return;
        }

        boolean payCoin = false;
        if (preferCoin && data.getCoin() >= createCoinCost) {
            payCoin = true;
        } else if (data.getCrystalCash() >= createCrystalCost) {
            payCoin = false;
        } else if (data.getCoin() >= createCoinCost) {
            payCoin = true;
        } else {
            p.sendMessage(PREFIX + ChatColor.RED + "创建费不足：需要 "
                    + createCoinCost + " 币 或 " + createCrystalCost + " 晶钻"
                    + ChatColor.GRAY + "（币 " + data.getCoin()
                    + " · 晶钻 " + data.getCrystalCash() + "）");
            return;
        }
        if (payCoin) {
            if (!data.takeCoin(createCoinCost)) {
                p.sendMessage(PREFIX + ChatColor.RED + "扣币失败。");
                return;
            }
        } else {
            if (!data.takeCrystalCash(createCrystalCost)) {
                p.sendMessage(PREFIX + ChatColor.RED + "扣晶钻失败。");
                return;
            }
        }

        String id = UUID.randomUUID().toString();
        Guild g = new Guild();
        g.id = id;
        g.name = name;
        g.leaderUuid = p.getUniqueId();
        g.members.add(p.getUniqueId());
        g.created = DailyService.today() + " " + DailyService.nowHm();
        g.contribution.put(p.getUniqueId().toString(), Integer.valueOf(0));
        g.dirty = true;
        byId.put(id, g);
        nameIndex.put(name.toLowerCase(Locale.ROOT), id);
        saveGuild(g);

        data.setGuildId(id);
        dataStore.flushMutation(p.getUniqueId());
        pendingInvites.remove(p.getUniqueId());

        String paid = payCoin
                ? ("余烬币 ×" + createCoinCost)
                : ("晶钻 ×" + createCrystalCost);
        p.sendMessage(PREFIX + ChatColor.GREEN + "盟约 §f" + name
                + ChatColor.GREEN + " 已创建！"
                + ChatColor.GRAY + "（消耗 " + paid + "）");
        p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg guild invite <玩家> · donate …");
    }

    private void cmdInvite(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg guild invite <玩家>");
            return;
        }
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "你不在任何盟约中。");
            return;
        }
        if (!canInvite(g, p.getUniqueId())) {
            p.sendMessage(PREFIX + ChatColor.RED + "只有盟主/干部可邀请。");
            return;
        }
        if (g.members.size() >= maxMembers) {
            p.sendMessage(PREFIX + ChatColor.RED + "盟约人数已满（" + maxMembers + "）。");
            return;
        }
        if (p.getName().equalsIgnoreCase(targetName)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能邀请自己。");
            return;
        }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            p.sendMessage(PREFIX + ChatColor.RED + "玩家不在线: " + targetName);
            return;
        }
        PlayerData td = dataStore.get(target.getUniqueId());
        if (td.getGuildId() != null && !td.getGuildId().isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + target.getName() + " 已有盟约。");
            return;
        }
        pendingInvites.put(target.getUniqueId(), g.id);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已邀请 §f" + target.getName()
                + ChatColor.GREEN + " 加入 §f" + g.name);
        target.sendMessage(PREFIX + ChatColor.YELLOW + p.getName()
                + ChatColor.GRAY + " 邀请你加入盟约 §f" + g.name
                + ChatColor.GRAY + " · /corerpg guild accept");
    }

    private void cmdAccept(Player p) {
        String gid = pendingInvites.remove(p.getUniqueId());
        if (gid == null || gid.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "没有待处理的盟约邀请。");
            return;
        }
        Guild g = byId.get(gid);
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "邀请的盟约已不存在。");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        if (data.getGuildId() != null && !data.getGuildId().isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "你已在盟约中。");
            return;
        }
        if (g.members.size() >= maxMembers) {
            p.sendMessage(PREFIX + ChatColor.RED + "盟约人数已满。");
            return;
        }
        if (!g.members.contains(p.getUniqueId())) {
            g.members.add(p.getUniqueId());
        }
        if (!g.contribution.containsKey(p.getUniqueId().toString())) {
            g.contribution.put(p.getUniqueId().toString(), Integer.valueOf(0));
        }
        g.dirty = true;
        saveGuild(g);
        data.setGuildId(g.id);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已加入盟约 §f" + g.name);
        notifyOnlineMembers(g, ChatColor.GRAY + p.getName() + " 加入了盟约。", p.getUniqueId());
    }

    private void cmdLeave(Player p) {
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "你不在任何盟约中。");
            return;
        }
        if (g.leaderUuid.equals(p.getUniqueId())) {
            if (g.members.size() > 1) {
                p.sendMessage(PREFIX + ChatColor.RED + "盟主须先 kick 其他成员，或转让（暂未开放）。");
                return;
            }
            // dissolve
            byId.remove(g.id);
            nameIndex.remove(g.name.toLowerCase(Locale.ROOT));
            MysqlStorage mysqlDel = mysqlOrNull();
            if (mysqlDel != null) {
                try { mysqlDel.deleteGuild(g.id); } catch (SQLException e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to delete guild MySQL " + g.id, e);
                }
            } else if (guildsDir != null) {
                File f = new File(guildsDir, g.id + ".yml");
                if (f.exists()) f.delete();
            }
            PlayerData data = dataStore.get(p.getUniqueId());
            data.setGuildId("");
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + ChatColor.YELLOW + "盟约 §f" + g.name
                    + ChatColor.YELLOW + " 已解散（你是最后一人）。");
            return;
        }
        g.members.remove(p.getUniqueId());
        g.officers.remove(p.getUniqueId());
        g.dirty = true;
        saveGuild(g);
        PlayerData data = dataStore.get(p.getUniqueId());
        data.setGuildId("");
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.YELLOW + "已离开盟约 §f" + g.name);
        notifyOnlineMembers(g, ChatColor.GRAY + p.getName() + " 离开了盟约。", null);
    }

    private void cmdKick(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg guild kick <玩家>");
            return;
        }
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "你不在任何盟约中。");
            return;
        }
        if (!g.leaderUuid.equals(p.getUniqueId())) {
            p.sendMessage(PREFIX + ChatColor.RED + "只有盟主可踢人。");
            return;
        }
        if (p.getName().equalsIgnoreCase(targetName)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能踢自己，用 leave。");
            return;
        }
        UUID targetId = null;
        String canon = null;
        for (UUID u : g.members) {
            String n = nameOf(u);
            if (n.equalsIgnoreCase(targetName)) {
                targetId = u;
                canon = n;
                break;
            }
        }
        if (targetId == null) {
            OfflinePlayer off = resolveOffline(targetName);
            if (off != null && g.members.contains(off.getUniqueId())) {
                targetId = off.getUniqueId();
                canon = resolveName(off, targetName);
            }
        }
        if (targetId == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "成员中找不到: " + targetName);
            return;
        }
        if (targetId.equals(g.leaderUuid)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能踢盟主。");
            return;
        }
        g.members.remove(targetId);
        g.officers.remove(targetId);
        g.dirty = true;
        saveGuild(g);
        PlayerData td = dataStore.get(targetId);
        td.setGuildId("");
        dataStore.flushMutation(targetId);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已将 §f" + canon + ChatColor.GREEN + " 移出盟约。");
        Player online = Bukkit.getPlayer(targetId);
        if (online != null && online.isOnline()) {
            online.sendMessage(PREFIX + ChatColor.RED + "你被移出盟约 §f" + g.name);
        }
    }

    private void cmdDonate(Player p, String niId, String amtStr) {
        if (niId == null || niId.isEmpty() || amtStr == null || amtStr.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg guild donate <niId> <amt>");
            p.sendMessage(ChatColor.GRAY + "  可捐: " + joinRates());
            p.sendMessage(ChatColor.DARK_GRAY + "  日贡献上限 " + dailyDonateCap);
            return;
        }
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "你不在任何盟约中。");
            return;
        }
        Integer rate = donateRates.get(niId);
        if (rate == null) {
            // try short aliases
            if ("shard".equalsIgnoreCase(niId) || "碎片".equals(niId)) {
                niId = "mat_ember_shard";
                rate = donateRates.get(niId);
            } else if ("bone".equalsIgnoreCase(niId) || "dust".equalsIgnoreCase(niId)
                    || "骨尘".equals(niId)) {
                niId = "mat_ember_bone_dust";
                rate = donateRates.get(niId);
            }
        }
        if (rate == null || rate.intValue() <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "不可捐献该物品: " + niId);
            p.sendMessage(ChatColor.GRAY + "  可捐: " + joinRates());
            return;
        }
        int amt;
        try {
            amt = Integer.parseInt(amtStr);
        } catch (NumberFormatException e) {
            p.sendMessage(PREFIX + ChatColor.RED + "数量无效。");
            return;
        }
        if (amt <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "数量须为正整数。");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        ensureDonateDaily(data);
        int room = dailyDonateCap - data.getGuildDonateToday();
        if (room <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "今日贡献已达上限（" + dailyDonateCap + "）。");
            return;
        }
        // contrib = floor(amt / rate); need at least 1 contrib
        int maxByItems = amt / rate.intValue();
        if (maxByItems <= 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "数量不足换算 1 贡献（每 "
                    + rate + " 个 → 1 贡献）。");
            return;
        }
        int gain = Math.min(maxByItems, room);
        int take = gain * rate.intValue();
        if (niBridge == null || niBridge.countInInventory(p, niId) < take) {
            int have = niBridge == null ? 0 : niBridge.countInInventory(p, niId);
            p.sendMessage(PREFIX + ChatColor.RED + "背包材料不足: " + niId
                    + " 需要 ×" + take + ChatColor.GRAY + "（有 " + have + "）");
            return;
        }
        if (!niBridge.consumeExact(p, niId, take)) {
            p.sendMessage(PREFIX + ChatColor.RED + "扣除材料失败。");
            return;
        }
        data.setGuildDonateToday(data.getGuildDonateToday() + gain);
        if (donateActivityPoints > 0) {
            data.addActivity(donateActivityPoints);
        }
        dataStore.flushMutation(p.getUniqueId());

        String key = p.getUniqueId().toString();
        int prev = contribOf(g, p.getUniqueId());
        g.contribution.put(key, Integer.valueOf(prev + gain));
        g.dirty = true;
        saveGuild(g);

        p.sendMessage(PREFIX + ChatColor.GREEN + "捐献成功！"
                + ChatColor.GRAY + " 消耗 " + niId + " ×" + take
                + ChatColor.GREEN + " → 贡献 +" + gain
                + ChatColor.GRAY + "（今日 " + data.getGuildDonateToday()
                + "/" + dailyDonateCap + " · 个人累计 "
                + (prev + gain) + "）");
    }

    private void cmdBoss(Player p) {
        if (!bossEnabled) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + bossStub);
            return;
        }
        Guild g = getPlayerGuild(p.getUniqueId());
        if (g == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "你不在任何盟约中。");
            return;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        String week = DailyService.weekId();
        if (week == null) week = "";
        if (!week.equals(data.getGuildBossWeekId())) {
            data.setGuildBossWeekId(week);
            data.setGuildBossUsed(0);
        }
        if (data.getGuildBossUsed() >= bossWeeklyLimit) {
            p.sendMessage(PREFIX + ChatColor.RED + "本周盟 Boss 次数已用尽（"
                    + data.getGuildBossUsed() + "/" + bossWeeklyLimit
                    + ChatColor.GRAY + " · 周 " + week + "）");
            return;
        }
        int mine = contribOf(g, p.getUniqueId());
        if (mine < bossContributionCost) {
            p.sendMessage(PREFIX + ChatColor.RED + "贡献不足：需要 §f" + bossContributionCost
                    + ChatColor.RED + "，当前 §f" + mine
                    + ChatColor.GRAY + "（donate 材料换贡献）");
            return;
        }
        // 1.4.9: DP team — only the leader may start; collect members for the one-time pass
        final List<UUID> party = new ArrayList<UUID>();
        party.add(p.getUniqueId());
        Object team = dpTeam(p);
        if (team != null) {
            UUID leader = dpTeamLeader(team);
            if (leader != null && !leader.equals(p.getUniqueId())) {
                p.sendMessage(PREFIX + ChatColor.RED + "请由 DP 队长执行 /corerpg guild boss。");
                return;
            }
            if (dpTeamInDungeon(team)) {
                p.sendMessage(PREFIX + ChatColor.RED + "你的队伍已在地牢中。");
                return;
            }
            for (UUID u : dpTeamMembers(team)) if (!party.contains(u)) party.add(u);
        }
        // spend personal contribution
        final String key = p.getUniqueId().toString();
        g.contribution.put(key, Integer.valueOf(mine - bossContributionCost));
        g.dirty = true;
        saveGuild(g);

        data.setGuildBossUsed(data.getGuildBossUsed() + 1);
        data.setGuildBossWeekId(week);
        dataStore.flushMutation(p.getUniqueId());

        String tpl = bossStartCommand == null ? ("dp start-console {player} " + bossDungeonId) : bossStartCommand.trim();
        // {player} template → run from console (DP start-console); legacy template → as the player
        final boolean asConsole = tpl.contains("{player}");
        final String cmd = tpl.replace("{player}", p.getName()).replace("{dungeon}", bossDungeonId);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已消耗贡献 §f" + bossContributionCost
                + ChatColor.GREEN + " · 启动 §f" + bossDungeonId
                + ChatColor.GRAY + "（本周 " + data.getGuildBossUsed() + "/" + bossWeeklyLimit + "）");
        long exp = System.currentTimeMillis() + BOSS_PASS_MS;
        for (UUID u : party) bossPass.put(u, Long.valueOf(exp));
        boolean ok;
        try {
            ok = Bukkit.dispatchCommand(asConsole ? Bukkit.getConsoleSender() : p, cmd);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "guild boss dispatch failed for " + p.getName(), t);
            ok = false;
        }
        if (!ok) {
            clearBossPass(party);
            refundBoss(g, p, key, data);
            return;
        }
        if (dpInDungeon(p)) {
            clearBossPass(party);
            plugin.getLogger().info("guild boss started by " + p.getName() + " party=" + party.size());
            return;
        }
        // DP may start on a later tick — verify after 2s, refund if the team never entered
        final Guild fg = g;
        final PlayerData fdata = data;
        final UUID pid = p.getUniqueId();
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                clearBossPass(party);
                Player pp = Bukkit.getPlayer(pid);
                if (pp != null && dpInDungeon(pp)) {
                    plugin.getLogger().info("guild boss started (delayed) by " + pp.getName());
                    return;
                }
                if (pp != null) refundBoss(fg, pp, key, fdata);
            }
        }, 40L);
    }

    private void refundBoss(Guild g, Player p, String key, PlayerData data) {
        g.contribution.put(key, Integer.valueOf(contribOf(g, p.getUniqueId()) + bossContributionCost));
        g.dirty = true;
        saveGuild(g);
        data.setGuildBossUsed(Math.max(0, data.getGuildBossUsed() - 1));
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.RED + "启动失败（已退还贡献与次数）。"
                + ChatColor.GRAY + " 请确认已组队（1～5 人、全员在线、由队长执行）后重试。");
    }

    /** PAPI %corerpg_guildboss_pass% — true only during a paid /corerpg guild boss start. */
    public boolean hasBossPass(UUID id) {
        if (id == null) return false;
        Long exp = bossPass.get(id);
        if (exp == null) return false;
        if (System.currentTimeMillis() > exp.longValue()) { bossPass.remove(id); return false; }
        return true;
    }

    private void clearBossPass(List<UUID> ids) {
        for (UUID u : ids) bossPass.remove(u);
    }

    // ---- DungeonPlus (soft, via reflection; no compile dependency) ----
    private Object dpTeam(Player p) {
        try {
            Class<?> c = Class.forName("org.serverct.ersha.dungeon.DungeonPlus");
            Object tm = c.getField("teamManager").get(null);
            if (tm == null) return null;
            return tm.getClass().getMethod("getTeam", Player.class).invoke(tm, p);
        } catch (Throwable t) {
            return null;
        }
    }

    private UUID dpTeamLeader(Object team) {
        try {
            Object o = team.getClass().getField("leader").get(team);
            return o instanceof UUID ? (UUID) o : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private List<UUID> dpTeamMembers(Object team) {
        List<UUID> out = new ArrayList<UUID>();
        try {
            UUID l = dpTeamLeader(team);
            if (l != null) out.add(l);
            Object o = team.getClass().getField("players").get(team);
            if (o instanceof List) {
                for (Object x : (List<?>) o) if (x instanceof UUID && !out.contains(x)) out.add((UUID) x);
            }
        } catch (Throwable ignored) {
        }
        return out;
    }

    private boolean dpTeamInDungeon(Object team) {
        try {
            return team.getClass().getMethod("getTeamDungeon").invoke(team) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    private boolean dpInDungeon(Player p) {
        Object team = dpTeam(p);
        return team != null && dpTeamInDungeon(team);
    }

    private void ensureDonateDaily(PlayerData data) {
        String today = DailyService.today();
        if (!today.equals(data.getGuildDonateDate())) {
            data.setGuildDonateDate(today);
            data.setGuildDonateToday(0);
        }
    }

    private boolean canInvite(Guild g, UUID uuid) {
        if (g.leaderUuid.equals(uuid)) return true;
        return g.officers.contains(uuid);
    }

    private int contribOf(Guild g, UUID uuid) {
        Integer v = g.contribution.get(uuid.toString());
        return v == null ? 0 : v.intValue();
    }

    private boolean containsBanned(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        for (String b : bannedNames) {
            if (b != null && !b.isEmpty() && lower.contains(b)) return true;
        }
        return false;
    }

    private void notifyOnlineMembers(Guild g, String msg, UUID exclude) {
        for (UUID u : g.members) {
            if (exclude != null && exclude.equals(u)) continue;
            Player pl = Bukkit.getPlayer(u);
            if (pl != null && pl.isOnline()) {
                pl.sendMessage(PREFIX + msg);
            }
        }
    }

    private String joinRates() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : donateRates.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getKey()).append("×").append(e.getValue()).append("→1");
        }
        return sb.toString();
    }

    private static String joinFrom(String[] args, int start) {
        if (args == null || start >= args.length) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private String nameOf(UUID uuid) {
        if (uuid == null) return "?";
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) return online.getName();
        PlayerData data = dataStore.peek(uuid);
        if (data == null) data = dataStore.get(uuid);
        if (data != null && data.getLastKnownName() != null && !data.getLastKnownName().isEmpty()) {
            return data.getLastKnownName();
        }
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        if (off != null && off.getName() != null) return off.getName();
        return uuid.toString().substring(0, 8);
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveOffline(String name) {
        if (name == null || name.isEmpty()) return null;
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;
        OfflinePlayer off = Bukkit.getOfflinePlayer(name);
        if (off != null && (off.hasPlayedBefore() || off.isOnline())) return off;
        return off;
    }

    private static String resolveName(OfflinePlayer off, String fallback) {
        if (off.getName() != null && !off.getName().isEmpty()) return off.getName();
        return fallback;
    }
}
