package town.sunshine.corerpg;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Reads covenant.yml and handles set / reset (wash fee via ticket or crystalCash). */
public final class CovenantService {

    private static final DateTimeFormatter CHOSEN_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private boolean firstPickFree = true;
    private boolean resetClearsTalent = true;
    private int resetCrystalCash = 80;
    private String ticketNiId = "mat_ember_covenant_reset";
    private int cooldownHours = 0;
    private boolean lastPayTicket;
    private final Map<String, CovenantDef> covenants = new LinkedHashMap<String, CovenantDef>();

    public static final class CovenantDef {
        public final String id;
        public final String display;
        public final String description;
        public final String weaponBias;
        public final Map<String, Double> stats;
        CovenantDef(String id, String display, String description, String weaponBias, Map<String, Double> stats) {
            this.id = id;
            this.display = display;
            this.description = description;
            this.weaponBias = weaponBias;
            this.stats = stats;
        }
    }

    public CovenantService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "covenant.yml");
        if (!file.exists()) {
            plugin.saveResource("covenant.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("covenant.yml");
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
        covenants.clear();
        enabled = cfg.getBoolean("enabled", true);
        firstPickFree = cfg.getBoolean("first_pick_free", true);
        resetClearsTalent = cfg.getBoolean("reset_clears_talent", true);
        ConfigurationSection reset = cfg.getConfigurationSection("reset");
        if (reset != null) {
            resetCrystalCash = reset.getInt("crystal_cash", 80);
            ticketNiId = reset.getString("ticket_ni_id", "mat_ember_covenant_reset");
            cooldownHours = reset.getInt("cooldown_hours", 0);
        }
        ConfigurationSection root = cfg.getConfigurationSection("covenants");
        if (root != null) {
            for (String id : root.getKeys(false)) {
                ConfigurationSection sec = root.getConfigurationSection(id);
                if (sec == null) continue;
                Map<String, Double> stats = new LinkedHashMap<String, Double>();
                ConfigurationSection st = sec.getConfigurationSection("stats");
                if (st != null) {
                    for (String k : st.getKeys(false)) {
                        stats.put(k, Double.valueOf(st.getDouble(k)));
                    }
                }
                covenants.put(id.toLowerCase(), new CovenantDef(
                        id.toLowerCase(),
                        color(sec.getString("display", id)),
                        sec.getString("description", ""),
                        sec.getString("weapon_bias", ""),
                        Collections.unmodifiableMap(stats)));
            }
        }
    }

    public boolean isEnabled() { return enabled; }
    public boolean isResetClearsTalent() { return resetClearsTalent; }
    public int getResetCrystalCash() { return resetCrystalCash; }
    public String getTicketNiId() { return ticketNiId; }
    public Set<String> getIds() { return covenants.keySet(); }

    public CovenantDef get(String id) {
        if (id == null) return null;
        return covenants.get(id.toLowerCase());
    }

    public boolean isValidId(String id) {
        return get(id) != null;
    }

    public String displayName(String id) {
        CovenantDef d = get(id);
        if (d == null) return id == null ? "无" : id;
        return ChatColor.stripColor(d.display) + "（" + d.id + "）";
    }

    public String displayColored(String id) {
        CovenantDef d = get(id);
        if (d == null) return ChatColor.GRAY + "无";
        return d.display + ChatColor.GRAY + "（" + d.id + "）";
    }

    public String formatStats(CovenantDef def) {
        if (def == null || def.stats.isEmpty()) return ChatColor.DARK_GRAY + "（无伪属性）";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : def.stats.entrySet()) {
            if (sb.length() > 0) sb.append(ChatColor.GRAY).append(", ");
            sb.append(ChatColor.WHITE).append(e.getKey())
                    .append(ChatColor.GRAY).append("=")
                    .append(ChatColor.AQUA).append(formatNum(e.getValue().doubleValue()));
        }
        return sb.toString();
    }

    private static String formatNum(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9) return String.valueOf((long) Math.rint(v));
        return String.format("%.3f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }

    private String nowChosenAt() {
        return LocalDateTime.now(DailyService.zone()).format(CHOSEN_FMT);
    }

    /** Prefer NI ticket; else crystalCash. Returns null on success, else failure reason. */
    public String tryPayWash(Player player, PlayerData data) {
        lastPayTicket = false;
        if (ni != null && ticketNiId != null && !ticketNiId.isEmpty()) {
            if (ni.countInInventory(player, ticketNiId) >= 1) {
                if (ni.consumeExact(player, ticketNiId, 1)) {
                    lastPayTicket = true;
                    return null;
                }
            }
            ni.warnMissingOnceIfAbsent(ticketNiId);
        }
        if (data.takeCrystalCash(resetCrystalCash)) {
            lastPayTicket = false;
            return null;
        }
        return "费用不足（需晶钻 " + resetCrystalCash + " 或誓约重置券）";
    }

    public boolean lastPayWasTicket() { return lastPayTicket; }

    public void cmdShow(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[誓约] 功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(ChatColor.YELLOW + "[誓约] 尚未选定。首次选定免费。之后洗约需晶钻或誓约重置券。");
            player.sendMessage(ChatColor.GRAY + "  /corerpg covenant set <blaze|ash|warden>");
            player.sendMessage(ChatColor.GRAY + "  洗约费用：晶钻 " + resetCrystalCash
                    + " 或券 " + ticketNiId);
            return;
        }
        CovenantDef def = get(data.getCovenant());
        player.sendMessage(ChatColor.GREEN + "[誓约] 当前：" + displayColored(data.getCovenant()));
        if (def != null) {
            player.sendMessage(ChatColor.GRAY + "  " + def.description);
            player.sendMessage(ChatColor.GRAY + "  伪属性：" + formatStats(def));
        }
        player.sendMessage(ChatColor.GRAY + "  选定于 " + data.getCovenantChosenAt()
                + " · 洗约：晶钻 " + resetCrystalCash + " / 券 " + ticketNiId
                + (resetClearsTalent ? "（清空天赋）" : ""));
        player.sendMessage(ChatColor.DARK_GRAY + "  /corerpg covenant set <id> · /corerpg covenant reset");
    }

    public void cmdSet(Player player, String idRaw) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[誓约] 功能未启用");
            return;
        }
        if (idRaw == null || idRaw.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "[誓约] /corerpg covenant set <blaze|ash|warden>");
            return;
        }
        String id = idRaw.toLowerCase();
        CovenantDef def = get(id);
        if (def == null) {
            player.sendMessage(ChatColor.RED + "[誓约] 无效 ID（blaze|ash|warden）");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean first = !data.hasCovenant();
        if (first && firstPickFree) {
            applySet(player, data, def, true, false);
            return;
        }
        if (data.getCovenant().equalsIgnoreCase(id)) {
            player.sendMessage(ChatColor.YELLOW + "[誓约] 已是当前誓约：" + displayColored(id));
            return;
        }
        String fail = tryPayWash(player, data);
        if (fail != null) {
            player.sendMessage(ChatColor.RED + "[誓约] " + fail);
            return;
        }
        applySet(player, data, def, false, true);
        String how = lastPayWasTicket() ? "已扣重置券" : ("已扣晶钻 ×" + resetCrystalCash);
        player.sendMessage(ChatColor.GRAY + "  费用：" + how);
    }

    public void cmdReset(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[誓约] 功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(ChatColor.YELLOW + "[誓约] 尚未选定，无需重置。首次选定免费：/corerpg covenant set <id>");
            return;
        }
        String fail = tryPayWash(player, data);
        if (fail != null) {
            player.sendMessage(ChatColor.RED + "[誓约] " + fail);
            return;
        }
        String prev = data.getCovenant();
        data.setCovenant("none");
        data.setCovenantChosenAt("");
        if (resetClearsTalent) {
            data.clearTalentAllocation();
        }
        dataStore.flushMutation(player.getUniqueId());
        String how = lastPayWasTicket() ? "已扣重置券" : ("已扣晶钻 ×" + resetCrystalCash);
        player.sendMessage(ChatColor.GREEN + "[誓约] 已重置为无（原 " + displayName(prev)
                + "），天赋点数已退回可用池。");
        player.sendMessage(ChatColor.GRAY + "  费用：" + how
                + " · 可重新 /corerpg covenant set <blaze|ash|warden>");
    }

    private void applySet(Player player, PlayerData data, CovenantDef def, boolean free, boolean wash) {
        if (wash && resetClearsTalent) {
            data.clearTalentAllocation();
        }
        data.setCovenant(def.id);
        data.setCovenantChosenAt(nowChosenAt());
        dataStore.flushMutation(player.getUniqueId());
        if (free) {
            player.sendMessage(ChatColor.GREEN + "[誓约] 已选定：" + displayColored(def.id));
            player.sendMessage(ChatColor.YELLOW + "[誓约] 首次选定免费。之后洗约需晶钻或誓约重置券。");
        } else {
            player.sendMessage(ChatColor.GREEN + "[誓约] 已选定：" + displayColored(def.id)
                    + ChatColor.GRAY + "（已洗约扣费"
                    + (resetClearsTalent ? " · 天赋已清空" : "") + "）");
        }
        player.sendMessage(ChatColor.GRAY + "  伪属性：" + formatStats(def));
    }
}
