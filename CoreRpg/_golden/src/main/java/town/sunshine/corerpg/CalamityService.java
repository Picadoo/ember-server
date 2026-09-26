package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** World calamity boss windows (Asia/Shanghai HH:mm) + persist lastFireDate. */
public final class CalamityService {

    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
    private static final int WINDOW_SECONDS = 120;

    private final CoreRpgPlugin plugin;
    private final File stateFile;

    private boolean enabled = true;
    private final List<String> times = new ArrayList<String>();
    private String spawnTemplate = "mm m spawn EmberCalamityBoss {world},{x},{y},{z}";
    private String worldName = "world";
    private double x;
    private double y = 70;
    private double z;
    private String warningMsg = "&c[余烬] &4灾厄将至——灰烬正在苏醒…";
    private String spawnMsg = "&4[余烬] &c余烬灾厄使降临于 {world} ({x}, {y}, {z})！";
    private int warningSeconds = 10;
    private final List<String> killRewardCommands = new ArrayList<String>();

    private String lastFireDate = "";
    private final List<String> firedTimes = new ArrayList<String>();
    private boolean spawnPending;

    public CalamityService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "calamity-state.yml");
        loadState();
        reload();
    }

    public void reload() {
        FileConfiguration cfg = plugin.getConfig();
        enabled = cfg.getBoolean("calamity.enabled", true);
        times.clear();
        times.addAll(cfg.getStringList("calamity.times"));
        if (times.isEmpty()) {
            times.add("12:00");
            times.add("20:00");
            times.add("22:00");
        }
        spawnTemplate = cfg.getString("calamity.spawn_command",
                "mm m spawn EmberCalamityBoss {world},{x},{y},{z}");
        worldName = cfg.getString("calamity.location.world", "world");
        x = cfg.getDouble("calamity.location.x", 0);
        y = cfg.getDouble("calamity.location.y", 70);
        z = cfg.getDouble("calamity.location.z", 0);
        warningMsg = cfg.getString("calamity.warning_message",
                "&c[余烬] &4灾厄将至——灰烬正在苏醒…");
        spawnMsg = cfg.getString("calamity.spawn_message",
                "&4[余烬] &c余烬灾厄使降临于 {world} ({x}, {y}, {z})！");
        warningSeconds = cfg.getInt("calamity.warning_seconds", 10);
        killRewardCommands.clear();
        killRewardCommands.addAll(cfg.getStringList("calamity.kill_rewards"));
        if (killRewardCommands.isEmpty()) {
            killRewardCommands.add("ni give {player} mat_calamity_ember 1");
            killRewardCommands.add("ni give {player} cosmetic_calamity_shard 1");
        }
    }

    public boolean isEnabled() { return enabled; }

    public String nextWindowLabel() {
        String next = nextSlot(LocalTime.now(DailyService.zone()));
        return next == null ? "-" : next;
    }

    public void tick() {
        if (!enabled || spawnPending) return;
        String today = DailyService.today();
        if (!today.equals(lastFireDate)) {
            lastFireDate = today;
            firedTimes.clear();
            saveState();
        }
        LocalTime now = LocalTime.now(DailyService.zone());
        int nowSecOfDay = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            if (slot == null || slot.isEmpty() || firedTimes.contains(slot)) continue;
            int sched = parseMinutes(slot);
            if (sched < 0) continue;
            int elapsed = nowSecOfDay - (sched * 60);
            if (elapsed >= 0 && elapsed < WINDOW_SECONDS) {
                fireWindow(slot, false);
                return;
            }
        }
    }

    public void trigger(CommandSender sender) {
        fireWindow(null, true);
        if (sender != null) {
            sender.sendMessage(ChatColor.GREEN + "[CoreRpg] 已强制触发灾厄广播与刷新");
        }
    }

    private void fireWindow(String slot, boolean manual) {
        if (!manual && slot != null) {
            if (!firedTimes.contains(slot)) firedTimes.add(slot);
            lastFireDate = DailyService.today();
            saveState();
        }
        spawnPending = true;
        broadcast(warningMsg);
        long delay = Math.max(0, warningSeconds) * 20L;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                try {
                    doSpawn();
                } finally {
                    spawnPending = false;
                }
            }
        }, delay);
    }

    private void doSpawn() {
        World w = Bukkit.getWorld(worldName);
        String wname = w != null ? w.getName() : worldName;
        String sx = String.format("%.1f", x);
        String sy = String.format("%.1f", y);
        String sz = String.format("%.1f", z);
        String cmd = spawnTemplate
                .replace("{world}", wname)
                .replace("{x}", sx)
                .replace("{y}", sy)
                .replace("{z}", sz);
        boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        String msg = spawnMsg
                .replace("{world}", wname)
                .replace("{x}", String.valueOf((int) x))
                .replace("{y}", String.valueOf((int) y))
                .replace("{z}", String.valueOf((int) z));
        broadcast(msg);
        plugin.getLogger().info("Calamity spawn " + (ok ? "OK" : "FAIL") + ": " + cmd);
    }

    public void sendStatus(CommandSender sender) {
        String today = DailyService.today();
        LocalTime now = LocalTime.now(DailyService.zone());
        sender.sendMessage(ChatColor.GOLD + "[余烬灾厄] "
                + (enabled ? ChatColor.GREEN + "开启" : ChatColor.RED + "关闭")
                + ChatColor.GRAY + " · " + today + " " + now.format(HM) + " CST");
        String next = nextSlot(now);
        sender.sendMessage(ChatColor.GRAY + "  窗口: " + joinTimes()
                + (next != null
                ? ChatColor.YELLOW + "  下一窗 " + next
                : ChatColor.GREEN + "  今日窗口已过或已刷新"));
        sender.sendMessage(ChatColor.GRAY + "  今日已刷新: "
                + (firedTimes.isEmpty() ? "无" : firedTimes.toString())
                + "  lastFireDate=" + (lastFireDate.isEmpty() ? "-" : lastFireDate));
        sender.sendMessage(ChatColor.GRAY + "  坐标 " + worldName + " "
                + (int) x + "," + (int) y + "," + (int) z);
        sender.sendMessage(ChatColor.DARK_GRAY + "  " + spawnTemplate);
    }

    public boolean isCalamityEntity(LivingEntity entity) {
        if (entity == null) return false;
        String name = entity.getCustomName();
        if (name == null) return false;
        String plain = ChatColor.stripColor(name);
        return plain != null && (plain.contains("余烬灾厄") || plain.contains("EmberCalamity"));
    }

    public void onCalamityKilled(Player killer) {
        if (killer == null || killRewardCommands.isEmpty()) return;
        for (int i = 0; i < killRewardCommands.size(); i++) {
            String raw = killRewardCommands.get(i);
            if (raw == null || raw.isEmpty()) continue;
            String cmd = raw.replace("{player}", killer.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        killer.sendMessage(ChatColor.GOLD + "[余烬] " + ChatColor.RED + "灾厄倒下。"
                + ChatColor.GRAY + "获得灾厄余烬与外观碎片。");
    }

    private String nextSlot(LocalTime now) {
        int nowMin = now.getHour() * 60 + now.getMinute();
        String today = DailyService.today();
        if (!today.equals(lastFireDate)) {
            // new day, first remaining slot
        }
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            int m = parseMinutes(slot);
            if (m < 0) continue;
            boolean fired = today.equals(lastFireDate) && firedTimes.contains(slot);
            if (!fired && m >= nowMin) return slot;
        }
        return null;
    }

    private String joinTimes() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            if (i > 0) sb.append(" / ");
            sb.append(times.get(i));
        }
        return sb.toString();
    }

    private static int parseMinutes(String hm) {
        try {
            String[] p = hm.trim().split(":");
            return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    private void broadcast(String raw) {
        if (raw == null || raw.isEmpty()) return;
        String msg = ChatColor.translateAlternateColorCodes('&', raw.replace('§', '&'));
        Bukkit.broadcastMessage(msg);
    }

    private void loadState() {
        if (!stateFile.exists()) return;
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(stateFile);
        lastFireDate = yaml.getString("lastFireDate", "");
        firedTimes.clear();
        List<String> raw = yaml.getStringList("firedTimes");
        if (raw != null) firedTimes.addAll(raw);
    }

    private void saveState() {
        FileConfiguration yaml = new YamlConfiguration();
        yaml.set("lastFireDate", lastFireDate);
        yaml.set("firedTimes", new ArrayList<String>(firedTimes));
        try {
            yaml.save(stateFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save calamity-state.yml", e);
        }
    }
}
