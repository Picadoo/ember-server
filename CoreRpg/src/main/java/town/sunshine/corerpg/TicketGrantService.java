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
import java.util.ArrayList;
import java.util.List;

/**
 * S0: 停发 NI 票。周本/精英/团「本周首次免费」改走 StaminaService weekly_grant_credit。
 * 保留配置读取与 cmdShow（改为体力摘要）以兼容旧命令路径。
 */
public final class TicketGrantService {

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;

    private int weeklyFreeTickets = 0;
    private String weeklyTicketNiId = "ticket_ember_weekly";
    private int abyssFreeTickets = 0;
    private String abyssTicketNiId = "ticket_ember_abyss";
    private int raidFreeTickets = 0;
    private String raidTicketNiId = "ticket_ember_raid";
    private int eliteFreeTickets = 0;
    private String eliteTicketNiId = "ticket_ember_elite";
    private int eliteHardCap = 1;

    public TicketGrantService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
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
        ConfigurationSection weekly = cfg.getConfigurationSection("weekly");
        weeklyFreeTickets = weekly != null ? weekly.getInt("free_tickets", 0) : 0;
        weeklyTicketNiId = weekly != null ? weekly.getString("ticket_ni_id", "ticket_ember_weekly") : "ticket_ember_weekly";
        ConfigurationSection raid = cfg.getConfigurationSection("raid");
        raidFreeTickets = raid != null ? raid.getInt("free_tickets", 0) : 0;
        raidTicketNiId = raid != null ? raid.getString("ticket_ni_id", "ticket_ember_raid") : "ticket_ember_raid";
        ConfigurationSection abyss = cfg.getConfigurationSection("abyss");
        abyssFreeTickets = abyss != null ? abyss.getInt("free_tickets", 0) : 0;
        abyssTicketNiId = abyss != null ? abyss.getString("ticket_ni_id", "ticket_ember_abyss") : "ticket_ember_abyss";
        ConfigurationSection elite = cfg.getConfigurationSection("elite");
        if (elite != null) {
            eliteFreeTickets = elite.getInt("free_tickets", 0);
            eliteTicketNiId = elite.getString("ticket_ni_id", "ticket_ember_elite");
            eliteHardCap = Math.max(1, elite.getInt("hard_cap", 1));
        } else {
            eliteFreeTickets = 0;
            eliteTicketNiId = "ticket_ember_elite";
            eliteHardCap = 1;
        }
    }

    public int getWeeklyFreeTickets() { return weeklyFreeTickets; }
    public String getWeeklyTicketNiId() { return weeklyTicketNiId; }
    public int getAbyssFreeTickets() { return abyssFreeTickets; }
    public String getAbyssTicketNiId() { return abyssTicketNiId; }
    public int getEliteFreeTickets() { return eliteFreeTickets; }
    public String getEliteTicketNiId() { return eliteTicketNiId; }

    /** S0: no NI ticket grants. Weekly credits refreshed by StaminaService.ensure. */
    public List<String> grantOnJoin(Player player, PlayerData data) {
        List<String> parts = new ArrayList<String>();
        if (player == null || data == null) return parts;
        if (plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) st.ensureWeekCredits(data);
        }
        // Mark legacy grant week ids so old needs*Grant stay false
        String week = DailyService.weekId();
        String today = DailyService.today();
        if (data.getWeeklyTicketGrantWeekId().isEmpty() || !week.equals(data.getWeeklyTicketGrantWeekId())) {
            data.setWeeklyTicketGrantWeekId(week);
        }
        if (data.getRaidTicketGrantWeekId().isEmpty() || !week.equals(data.getRaidTicketGrantWeekId())) {
            data.setRaidTicketGrantWeekId(week);
        }
        if (data.getEliteTicketGrantWeekId().isEmpty() || !week.equals(data.getEliteTicketGrantWeekId())) {
            data.setEliteTicketGrantWeekId(week);
        }
        if (data.getAbyssTicketGrantDate().isEmpty() || !today.equals(data.getAbyssTicketGrantDate())) {
            data.setAbyssTicketGrantDate(today);
        }
        return parts;
    }

    public int grantWeeklyIfNeeded(Player player, PlayerData data) { return 0; }
    public int grantAbyssIfNeeded(Player player, PlayerData data) { return 0; }
    public int grantRaidIfNeeded(Player player, PlayerData data) { return 0; }
    public int grantEliteIfNeeded(Player player, PlayerData data) { return 0; }

    public boolean needsWeeklyGrant(PlayerData data) { return false; }
    public boolean needsRaidGrant(PlayerData data) { return false; }
    public boolean needsAbyssGrant(PlayerData data) { return false; }
    public boolean needsEliteGrant(PlayerData data) { return false; }

    public void cmdShowTickets(Player player) {
        if (player == null) return;
        if (plugin instanceof CoreRpgPlugin) {
            StaminaService st = ((CoreRpgPlugin) plugin).getStaminaService();
            if (st != null) {
                st.show(player);
                return;
            }
        }
        player.sendMessage(ChatColor.GRAY + "[体力] 服务未就绪");
    }
}
