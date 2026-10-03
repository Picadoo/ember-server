package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Ember ladder boards + power_score (display only).
 * docs/design/DESIGN-ember-guild-ladder.md §2 · docs/ember-holograms.md
 */
public final class LadderService {

    public static final class Entry {
        public final String name;
        public final int value;
        public final UUID uuid;
        public Entry(String name, int value, UUID uuid) {
            this.name = name == null || name.isEmpty() ? "---" : name;
            this.value = value;
            this.uuid = uuid;
        }
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private int refreshSeconds = 60;
    private int perEnhance = 8;
    private int perSocket = 5;
    private int perTalentNode = 3;
    private int perCovenant = 10;
    private boolean usePetBonus = true;
    private int base = 100;
    private int coinDiv = 100;
    private int coinCap = 50;
    private int powerSize = 10;
    private int abyssSize = 5;
    private int speedSize = 5;

    private final List<Entry> powerBoard = new ArrayList<Entry>();
    private final List<Entry> abyssBoard = new ArrayList<Entry>();
    private final List<Entry> speedBoard = new ArrayList<Entry>();

    public LadderService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "ladder.yml");
        if (!file.exists()) {
            plugin.saveResource("ladder.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("ladder.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        refreshSeconds = Math.max(10, cfg.getInt("refresh_seconds", 60));
        ConfigurationSection power = cfg.getConfigurationSection("power");
        if (power != null) {
            perEnhance = power.getInt("per_enhance_level", 8);
            perSocket = power.getInt("per_socket_filled", 5);
            perTalentNode = power.getInt("per_talent_node", 3);
            perCovenant = power.getInt("per_covenant", 10);
            usePetBonus = power.getBoolean("use_pet_bonus", true);
            base = power.getInt("base", 100);
            coinDiv = Math.max(1, power.getInt("coin_div", 100));
            coinCap = Math.max(0, power.getInt("coin_cap", 50));
        }
        ConfigurationSection boards = cfg.getConfigurationSection("boards");
        if (boards != null) {
            powerSize = Math.max(1, boards.getInt("power_size", 10));
            abyssSize = Math.max(1, boards.getInt("abyss_size", 5));
            speedSize = Math.max(1, boards.getInt("speed_size", 5));
        }
        loadSnapshot(cfg);
    }

    public int getRefreshSeconds() { return refreshSeconds; }
    public boolean isEnabled() { return enabled; }

    public List<Entry> getPowerBoard() { return Collections.unmodifiableList(powerBoard); }
    public List<Entry> getAbyssBoard() { return Collections.unmodifiableList(abyssBoard); }
    public List<Entry> getSpeedBoard() { return Collections.unmodifiableList(speedBoard); }

    public Entry getPowerAt(int rank1) { return at(powerBoard, rank1); }
    public Entry getAbyssAt(int rank1) { return at(abyssBoard, rank1); }
    public Entry getSpeedAt(int rank1) { return at(speedBoard, rank1); }

    private static Entry at(List<Entry> list, int rank1) {
        if (rank1 < 1 || rank1 > list.size()) return null;
        return list.get(rank1 - 1);
    }

    public String nameOrEmpty(Entry e) { return e == null ? "---" : e.name; }
    public String valueOrZero(Entry e) { return e == null ? "0" : String.valueOf(e.value); }

    /** Recompute power for online player (inventory scan) and store. */
    public int recomputePower(Player player) {
        if (player == null) return 0;
        PlayerData data = dataStore.get(player.getUniqueId());
        data.setLastKnownName(player.getName());
        int enhanceSum = 0;
        int filled = 0;
        EnhanceService enhance = plugin.getEnhanceService();
        PlayerInventory inv = player.getInventory();
        ItemStack[] all = concat(inv.getContents(), inv.getArmorContents(),
                new ItemStack[]{inv.getItemInOffHand()});
        for (ItemStack stack : all) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (enhance == null || enhance.resolveGearId(stack) == null) {
                // still count if lore markers present (whitelist miss / offline NI)
                if (GearLore.readEnhance(stack) <= 0
                        && !hasAnySocketFill(stack)) continue;
            }
            enhanceSum += GearLore.readEnhance(stack);
            String[] socks = GearLore.readSockets(stack, 4);
            if (socks != null) {
                for (String s : socks) {
                    if (s != null && !s.isEmpty()) filled++;
                }
            }
        }
        int score = computeScore(data, enhanceSum, filled);
        data.setPowerScore(score);
        dataStore.saveIfDirty(player.getUniqueId());
        return score;
    }

    /** Offline / file-based stub when inventory unavailable. */
    public int recomputePowerOffline(PlayerData data) {
        if (data == null) return 0;
        // stub when no gear scan: emberLevel*5 + talentSpent*3 + crystal/10 + formula extras
        int score = base;
        score += data.getEmberLevel() * 5;
        score += data.getTalentPointsSpent() * 3;
        score += data.getTalentNodes().size() * perTalentNode;
        if (data.hasCovenant()) score += perCovenant;
        int coinPart = data.getCoin() / coinDiv;
        if (coinPart > coinCap) coinPart = coinCap;
        score += coinPart;
        score += Math.max(0, data.getCrystalCash() / 10);
        if (usePetBonus && plugin.getPetService() != null) {
            score += plugin.getPetService().getActivePowerBonus(data);
        }
        data.setPowerScore(Math.max(0, score));
        return data.getPowerScore();
    }

    private int computeScore(PlayerData data, int enhanceSum, int filledSockets) {
        int score = base;
        score += enhanceSum * perEnhance;
        score += filledSockets * perSocket;
        score += data.getTalentNodes().size() * perTalentNode;
        if (data.hasCovenant()) score += perCovenant;
        int coinPart = data.getCoin() / coinDiv;
        if (coinPart > coinCap) coinPart = coinCap;
        score += coinPart;
        if (usePetBonus && plugin.getPetService() != null) {
            score += plugin.getPetService().getActivePowerBonus(data);
        }
        return Math.max(0, score);
    }

    private static boolean hasAnySocketFill(ItemStack stack) {
        String[] socks = GearLore.readSockets(stack, 4);
        if (socks == null) return false;
        for (String s : socks) {
            if (s != null && !s.isEmpty()) return true;
        }
        return false;
    }

    private static ItemStack[] concat(ItemStack[] a, ItemStack[] b, ItemStack[] c) {
        int n = (a == null ? 0 : a.length) + (b == null ? 0 : b.length) + (c == null ? 0 : c.length);
        ItemStack[] out = new ItemStack[n];
        int i = 0;
        if (a != null) for (ItemStack s : a) out[i++] = s;
        if (b != null) for (ItemStack s : b) out[i++] = s;
        if (c != null) for (ItemStack s : c) out[i++] = s;
        return out;
    }

    /** Full rebuild from all player files + online inventory scans. */
    public void refresh() {
        if (!enabled) {
            powerBoard.clear();
            abyssBoard.clear();
            speedBoard.clear();
            return;
        }
        // Update online first
        for (Player p : Bukkit.getOnlinePlayers()) {
            recomputePower(p);
        }

        List<Scored> power = new ArrayList<Scored>();
        List<Scored> abyss = new ArrayList<Scored>();
        List<Scored> speed = new ArrayList<Scored>();

        for (UUID uuid : dataStore.listAllUuids()) {
            Player online = Bukkit.getPlayer(uuid);
            PlayerData data = dataStore.get(uuid);
            if (online == null || !online.isOnline()) {
                // Offline: refresh stub score if unset
                if (data.getPowerScore() <= 0) {
                    recomputePowerOffline(data);
                    dataStore.saveIfDirty(uuid);
                }
            }
            String name = resolveName(uuid, data);
            if (name == null || name.isEmpty()) name = shortUuid(uuid);

            int ps = data.getPowerScore();
            if (ps > 0) power.add(new Scored(name, ps, uuid));

            int ab = data.effectiveAbyssWeek();
            if (ab <= 0) ab = data.getAbyssBest(); // fallback historical for empty week
            if (ab > 0) abyss.add(new Scored(name, ab, uuid));

            int sec = data.getWeeklyBestSec();
            if (sec > 0) speed.add(new Scored(name, sec, uuid));
        }

        Collections.sort(power, new Comparator<Scored>() {
            @Override public int compare(Scored a, Scored b) {
                if (b.value != a.value) return Integer.compare(b.value, a.value);
                return a.name.compareToIgnoreCase(b.name);
            }
        });
        Collections.sort(abyss, new Comparator<Scored>() {
            @Override public int compare(Scored a, Scored b) {
                if (b.value != a.value) return Integer.compare(b.value, a.value);
                return a.name.compareToIgnoreCase(b.name);
            }
        });
        Collections.sort(speed, new Comparator<Scored>() {
            @Override public int compare(Scored a, Scored b) {
                // lower sec better
                if (a.value != b.value) return Integer.compare(a.value, b.value);
                return a.name.compareToIgnoreCase(b.name);
            }
        });

        powerBoard.clear();
        abyssBoard.clear();
        speedBoard.clear();
        for (int i = 0; i < power.size() && i < powerSize; i++) {
            Scored s = power.get(i);
            powerBoard.add(new Entry(s.name, s.value, s.uuid));
        }
        for (int i = 0; i < abyss.size() && i < abyssSize; i++) {
            Scored s = abyss.get(i);
            abyssBoard.add(new Entry(s.name, s.value, s.uuid));
        }
        for (int i = 0; i < speed.size() && i < speedSize; i++) {
            Scored s = speed.get(i);
            speedBoard.add(new Entry(s.name, s.value, s.uuid));
        }
        saveSnapshot();
        syncHolograms();
    }

    /** 1.4.10: HolographicDisplays 2.4.9 has no PAPI → push resolved TOP lines via `hd setline` (only changed lines). */
    private final java.util.Map<String, String> holoCache = new java.util.HashMap<String, String>();
    private void syncHolograms() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) return;
        syncBoard("ember_ladder_power", powerBoard, powerSize, "&b", "");
        syncBoard("ember_ladder_abyss", abyssBoard, abyssSize, "&d", "层");
        syncBoard("ember_ladder_speed", speedBoard, speedSize, "&a", "s");
    }
    /** HD 2.4.9 internal registry (reflection; HD loads its holograms after CoreRpg enables). */
    private static boolean hdHologramExists(String name) {
        try {
            Class<?> c = Class.forName("com.gmail.filoghost.holographicdisplays.object.NamedHologramManager");
            Object o = c.getMethod("isExistingHologram", String.class).invoke(null, name);
            return Boolean.TRUE.equals(o);
        } catch (Throwable t) {
            return false;
        }
    }
    private void syncBoard(String holo, List<Entry> board, int size, String topColor, String unit) {
        if (!hdHologramExists(holo)) return;
        for (int r = 1; r <= size; r++) {
            Entry e = r <= board.size() ? board.get(r - 1) : null;
            boolean top = r <= 3;
            String line = (top ? "&e" : "&7") + r + ". &f" + nameOrEmpty(e) + " &7- " + (top ? topColor : "&7")
                    + (e == null ? "-" : valueOrZero(e) + unit);
            String key = holo + "#" + r;
            if (line.equals(holoCache.get(key))) continue;
            try {
                if (Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hd setline " + holo + " " + (r + 2) + " " + line)) {
                    holoCache.put(key, line);
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.FINE, "hd setline failed", t);
            }
        }
    }

    private static final class Scored {
        final String name;
        final int value;
        final UUID uuid;
        Scored(String name, int value, UUID uuid) {
            this.name = name; this.value = value; this.uuid = uuid;
        }
    }

    private String resolveName(UUID uuid, PlayerData data) {
        if (data != null && data.getLastKnownName() != null && !data.getLastKnownName().isEmpty()) {
            return data.getLastKnownName();
        }
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) return online.getName();
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        if (off != null && off.getName() != null) return off.getName();
        return "";
    }

    private static String shortUuid(UUID uuid) {
        String s = uuid.toString();
        return s.length() > 8 ? s.substring(0, 8) : s;
    }

    private void loadSnapshot(FileConfiguration cfg) {
        powerBoard.clear();
        abyssBoard.clear();
        speedBoard.clear();
        readBoard(cfg.getConfigurationSection("snapshot.power"), powerBoard);
        readBoard(cfg.getConfigurationSection("snapshot.abyss"), abyssBoard);
        readBoard(cfg.getConfigurationSection("snapshot.speed"), speedBoard);
    }

    private void readBoard(ConfigurationSection sec, List<Entry> out) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            ConfigurationSection e = sec.getConfigurationSection(key);
            if (e == null) continue;
            String name = e.getString("name", "---");
            int value = e.getInt("value", 0);
            UUID uuid = null;
            String us = e.getString("uuid", "");
            if (us != null && !us.isEmpty()) {
                try { uuid = UUID.fromString(us); } catch (IllegalArgumentException ignored) {}
            }
            out.add(new Entry(name, value, uuid));
        }
    }

    private void saveSnapshot() {
        File file = new File(plugin.getDataFolder(), "ladder.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("snapshot", null);
        writeBoard(cfg, "snapshot.power", powerBoard);
        writeBoard(cfg, "snapshot.abyss", abyssBoard);
        writeBoard(cfg, "snapshot.speed", speedBoard);
        cfg.set("snapshot.updated", DailyService.today() + " " + DailyService.nowHm());
        cfg.set("snapshot.weekId", DailyService.weekId());
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save ladder.yml snapshot", e);
        }
    }

    private void writeBoard(FileConfiguration cfg, String path, List<Entry> board) {
        int i = 1;
        for (Entry e : board) {
            String p = path + "." + i;
            cfg.set(p + ".name", e.name);
            cfg.set(p + ".value", Integer.valueOf(e.value));
            cfg.set(p + ".uuid", e.uuid == null ? "" : e.uuid.toString());
            i++;
        }
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        // args[0] == "ladder"
        if (args.length == 1 || (args.length == 2 && "me".equalsIgnoreCase(args[1]))) {
            cmdMe(sender);
            return;
        }
        String sub = args[1].toLowerCase();
        if ("power".equals(sub) || "abyss".equals(sub) || "speed".equals(sub)) {
            cmdList(sender, sub);
            return;
        }
        if ("refresh".equals(sub)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return;
            }
            refresh();
            sender.sendMessage(ChatColor.GREEN + "[天梯] 已重建榜单"
                    + " power=" + powerBoard.size()
                    + " abyss=" + abyssBoard.size()
                    + " speed=" + speedBoard.size());
            return;
        }
        if ("set".equals(sub)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return;
            }
            // /corerpg ladder set abyss <player> <floor>
            // /corerpg ladder set speed <player> <sec>
            if (args.length < 5) {
                sender.sendMessage(ChatColor.YELLOW + "用法: /corerpg ladder set abyss|speed <玩家> <值>");
                return;
            }
            cmdSet(sender, args[2], args[3], args[4]);
            return;
        }
        sender.sendMessage(ChatColor.YELLOW
                + "/corerpg ladder [me|power|abyss|speed|refresh|set abyss|set speed]");
    }

    private void cmdMe(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        int score = recomputePower(p);
        PlayerData data = dataStore.get(p.getUniqueId());
        sender.sendMessage(ChatColor.GOLD + "[天梯] " + ChatColor.YELLOW + p.getName());
        sender.sendMessage(ChatColor.GRAY + "  战力展示分: " + ChatColor.AQUA + score
                + ChatColor.DARK_GRAY + " (不乘伤害)");
        sender.sendMessage(ChatColor.GRAY + "  深渊最高: " + ChatColor.LIGHT_PURPLE + data.getAbyssBest()
                + ChatColor.GRAY + " · 本周: " + ChatColor.LIGHT_PURPLE + data.effectiveAbyssWeek());
        int sec = data.getWeeklyBestSec();
        sender.sendMessage(ChatColor.GRAY + "  周本最快: "
                + (sec <= 0 ? ChatColor.DARK_GRAY + "无" : ChatColor.GREEN + String.valueOf(sec) + "s"));
    }

    private void cmdList(CommandSender sender, String board) {
        List<Entry> list;
        String title;
        String unit;
        if ("power".equals(board)) {
            list = powerBoard; title = "战力天梯"; unit = "";
        } else if ("abyss".equals(board)) {
            list = abyssBoard; title = "深渊天梯"; unit = "层";
        } else {
            list = speedBoard; title = "竞速天梯"; unit = "s";
        }
        if (list.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "[天梯] " + title + ChatColor.GRAY + " — 暂无数据（可 /corerpg ladder refresh）");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "[天梯] " + ChatColor.YELLOW + title);
        int i = 1;
        for (Entry e : list) {
            sender.sendMessage(ChatColor.YELLOW + "  " + i + ". " + ChatColor.WHITE + e.name
                    + ChatColor.GRAY + " - " + ChatColor.AQUA + e.value + unit);
            i++;
        }
    }

    private void cmdSet(CommandSender sender, String kind, String playerName, String valueStr) {
        int value;
        try {
            value = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "数值无效: " + valueStr);
            return;
        }
        OfflinePlayer off = Bukkit.getPlayerExact(playerName);
        if (off == null) off = Bukkit.getOfflinePlayer(playerName);
        if (off == null || (off.getUniqueId() == null)) {
            sender.sendMessage(ChatColor.RED + "找不到玩家: " + playerName);
            return;
        }
        UUID uuid = off.getUniqueId();
        PlayerData data = dataStore.get(uuid);
        String name = off.getName() != null ? off.getName() : playerName;
        data.setLastKnownName(name);
        if ("abyss".equalsIgnoreCase(kind)) {
            data.recordAbyssFloor(value);
            // also force historical if admin sets high
            if (value > data.getAbyssBest()) data.setAbyssBest(value);
            dataStore.flushMutation(uuid);
            refresh();
            sender.sendMessage(ChatColor.GREEN + "[天梯] 已设 " + name + " 深渊层=" + value
                    + " (best=" + data.getAbyssBest() + " week=" + data.effectiveAbyssWeek() + ")");
            return;
        }
        if ("speed".equalsIgnoreCase(kind)) {
            if (value <= 0) {
                data.setWeeklyBestSec(0);
            } else {
                data.setWeeklyBestSec(value);
            }
            dataStore.flushMutation(uuid);
            refresh();
            sender.sendMessage(ChatColor.GREEN + "[天梯] 已设 " + name + " 竞速秒=" + data.getWeeklyBestSec());
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "用法: /corerpg ladder set abyss|speed <玩家> <值>");
    }
}
