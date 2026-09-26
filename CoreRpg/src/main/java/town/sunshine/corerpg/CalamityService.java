package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * World calamity public window (Asia/Shanghai).
 * Primary config: plugins/CoreRpg/calamity.yml
 * State: calamity-state.yml
 */
public final class CalamityService implements Listener {

    private static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int OPEN_GRACE_SECONDS = 120;
    private static final String PROTECT_SCROLL_ID = "mat_ember_protect_scroll";

    private static final class ChestReward {
        final String niId;
        final int amount;
        ChestReward(String niId, int amount) {
            this.niId = niId;
            this.amount = amount;
        }
    }

    private final CoreRpgPlugin plugin;
    private final File stateFile;
    private final File configFile;
    private final Random random = new Random();

    private boolean enabled = true;
    private final List<String> times = new ArrayList<String>();
    private int durationMinutes = 10;
    private String spawnTemplate = "mm m spawn EmberCalamityBoss 1 {world},{x},{y},{z}";
    private String mythicId = "EmberCalamityBoss";
    private String displayName = "余烬灾厄使";
    private String worldName = "ember_event";
    private double x;
    private double y = 64;
    private double z;
    private int amount = 1;

    private final List<Integer> preMinutes = new ArrayList<Integer>();
    private String messagePre = "§4[余烬灾厄] §e{minutes} 分钟后降临！§7/ember → 灾厄 → 奔赴";
    private String messageOpen = "§4§l[余烬灾厄] §e灾厄使降临祭坛！§7/ember → 灾厄 → 奔赴 §8或 §f/mvtp ember_event";
    private String messageClosed = "§c灾厄未苏醒。下一窗：§f{next_window}";
    private String messageEnd = "§4[余烬灾厄] §7本轮窗口结束，灰烬合拢。";

    private int chestLimit = 1;
    private double protectScrollChance = 0.10;
    private final List<ChestReward> chestRewards = new ArrayList<ChestReward>();
    private final List<String> killRewardCommands = new ArrayList<String>();
    private boolean gateMvtpOnMenu = true;

    // persisted + memory
    private String lastFireDate = "";
    private final List<String> firedTimes = new ArrayList<String>();
    private String currentWindowSlot;
    private boolean windowOpen;
    private long windowEndEpochMs;
    private boolean bossSpawnedThisWindow;
    private boolean bossKilledThisWindow;
    private final Set<String> announcedPre = new HashSet<String>();
    /** Players who dealt damage to the calamity boss since the last open/settlement (spec §3: A/B need damage). */
    private final Set<UUID> damagers = new HashSet<UUID>();

    public CalamityService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "calamity-state.yml");
        this.configFile = new File(plugin.getDataFolder(), "calamity.yml");
        loadState();
        reload();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Calamity window service 1.4.8 loaded (world=" + worldName
                + " " + (int) x + "," + (int) y + "," + (int) z
                + " duration=" + durationMinutes + "m windows=" + joinTimes() + ")");
    }

    public void reload() {
        if (!configFile.exists()) {
            plugin.saveResource("calamity.yml", false);
        }
        FileConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
        InputStream in = plugin.getResource("calamity.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            yml.setDefaults(def);
            yml.options().copyDefaults(false);
        }
        FileConfiguration cfg = plugin.getConfig();

        enabled = yml.getBoolean("enabled", cfg.getBoolean("calamity.enabled", true));

        times.clear();
        times.addAll(yml.getStringList("schedule.windows"));
        if (times.isEmpty()) {
            times.addAll(cfg.getStringList("calamity.times"));
        }
        if (times.isEmpty()) {
            times.add("12:00");
            times.add("20:00");
            times.add("22:00");
        }
        durationMinutes = Math.max(1, yml.getInt("schedule.duration_minutes", 10));

        mythicId = yml.getString("boss.mythic_id", "EmberCalamityBoss");
        displayName = yml.getString("boss.display", "余烬灾厄使");
        worldName = yml.getString("boss.world", cfg.getString("calamity.location.world", "ember_event"));
        x = firstDouble(yml, "boss.x", cfg, "calamity.location.x", 0);
        y = firstDouble(yml, "boss.y", cfg, "calamity.location.y", 64);
        z = firstDouble(yml, "boss.z", cfg, "calamity.location.z", 0);
        amount = Math.max(1, yml.getInt("boss.amount", 1));
        spawnTemplate = yml.getString("boss.spawn_command",
                cfg.getString("calamity.spawn_command",
                        "mm m spawn EmberCalamityBoss 1 {world},{x},{y},{z}"));

        preMinutes.clear();
        List<Integer> pre = yml.getIntegerList("announce.pre_minutes");
        if (pre != null) {
            for (Integer m : pre) {
                if (m != null && m.intValue() > 0) preMinutes.add(m);
            }
        }
        if (preMinutes.isEmpty()) {
            preMinutes.add(Integer.valueOf(5));
            preMinutes.add(Integer.valueOf(1));
        }
        messagePre = yml.getString("announce.message_pre", messagePre);
        messageOpen = yml.getString("announce.message_open", messageOpen);
        messageClosed = yml.getString("announce.message_closed", messageClosed);
        messageEnd = yml.getString("announce.message_end", messageEnd);

        chestLimit = Math.max(1, yml.getInt("daily_chest.limit_per_day", 1));
        protectScrollChance = yml.getDouble("daily_chest.protect_scroll_chance", 0.10);
        chestRewards.clear();
        loadChestRewards(yml);
        if (chestRewards.isEmpty()) {
            chestRewards.add(new ChestReward("mat_ember_core_fragment", 2));
            chestRewards.add(new ChestReward("crystal_ember_enchant", 1));
            chestRewards.add(new ChestReward("mat_calamity_ember", 2));
        }
        gateMvtpOnMenu = yml.getBoolean("gate_mvtp_on_menu", true);

        killRewardCommands.clear();
        killRewardCommands.addAll(cfg.getStringList("calamity.kill_rewards"));
        if (killRewardCommands.isEmpty()) {
            killRewardCommands.add("ni give {player} mat_calamity_ember 1");
            killRewardCommands.add("ni give {player} cosmetic_calamity_shard 1");
        }
    }

    public boolean isEnabled() { return enabled; }

    /** Public for menus: current public window is OPEN. */
    public boolean isWindowOpen() { return windowOpen; }

    public boolean isGateMvtpOnMenu() { return gateMvtpOnMenu; }

    public String nextWindowLabel() {
        String next = nextSlot(LocalTime.now(DailyService.zone()));
        return next == null ? "-" : next;
    }

    public String formatClosedMessage() {
        return colorize(messageClosed.replace("{next_window}", nextWindowLabel()));
    }

    public void tick() {
        if (!enabled) return;
        String today = DailyService.today();
        if (!today.equals(lastFireDate)) {
            if (windowOpen) {
                endWindow(false);
            }
            lastFireDate = today;
            firedTimes.clear();
            announcedPre.clear();
            currentWindowSlot = null;
            bossSpawnedThisWindow = false;
            bossKilledThisWindow = false;
            saveState();
        }

        if (windowOpen) {
            if (System.currentTimeMillis() >= windowEndEpochMs) {
                endWindow(false);
            }
            return;
        }

        LocalTime now = LocalTime.now(DailyService.zone());
        int nowSec = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();

        // PRE: T-5 / T-1 once each
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            if (slot == null || slot.isEmpty() || firedTimes.contains(slot)) continue;
            int schedSec = parseMinutes(slot) * 60;
            if (schedSec < 0) continue;
            int remaining = schedSec - nowSec;
            if (remaining <= 0) continue;
            for (int p = 0; p < preMinutes.size(); p++) {
                int pre = preMinutes.get(p).intValue();
                int preSec = pre * 60;
                if (remaining <= preSec) {
                    String key = slot + ":" + pre;
                    if (announcedPre.add(key)) {
                        broadcast(messagePre.replace("{minutes}", String.valueOf(pre)));
                        saveState();
                    }
                }
            }
        }

        // OPEN at window time (grace so a 10s/60s tick still catches it)
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            if (slot == null || slot.isEmpty() || firedTimes.contains(slot)) continue;
            int schedSec = parseMinutes(slot) * 60;
            if (schedSec < 0) continue;
            int elapsed = nowSec - schedSec;
            if (elapsed >= 0 && elapsed < OPEN_GRACE_SECONDS) {
                openWindow(slot, false);
                return;
            }
        }
    }

    public void forceOpen(CommandSender sender) {
        if (windowOpen) {
            if (!bossSpawnedThisWindow) {
                doSpawn();
                bossSpawnedThisWindow = true;
                saveState();
                if (sender != null) {
                    sender.sendMessage(ChatColor.GREEN + "[CoreRpg] 窗口已开，补刷 Boss");
                }
            } else if (sender != null) {
                sender.sendMessage(ChatColor.YELLOW + "[CoreRpg] 本窗已刷过 Boss，同窗不二刷");
            }
            return;
        }
        String due = findDueSlot(LocalTime.now(DailyService.zone()));
        String slot = due != null ? due : "force";
        openWindow(slot, true);
        if (sender != null) {
            sender.sendMessage(ChatColor.GREEN + "[CoreRpg] 已强制开启灾厄窗口");
        }
    }

    /** @deprecated use {@link #forceOpen(CommandSender)} */
    public void trigger(CommandSender sender) {
        forceOpen(sender);
    }

    public void forceEnd(CommandSender sender) {
        if (!windowOpen) {
            if (sender != null) {
                sender.sendMessage(colorize(messageClosed.replace("{next_window}", nextWindowLabel())));
            }
            return;
        }
        String due = findDueSlot(LocalTime.now(DailyService.zone()));
        if (due != null && !firedTimes.contains(due)) {
            firedTimes.add(due);
        }
        endWindow(true);
        if (sender != null) {
            sender.sendMessage(ChatColor.YELLOW + "[CoreRpg] 已强制结束灾厄窗口");
        }
    }

    private void openWindow(String slot, boolean manual) {
        String today = DailyService.today();
        if (!today.equals(lastFireDate)) {
            lastFireDate = today;
            firedTimes.clear();
            announcedPre.clear();
        }
        if (slot != null && !slot.isEmpty() && !firedTimes.contains(slot)) {
            firedTimes.add(slot);
        }
        String due = findDueSlot(LocalTime.now(DailyService.zone()));
        if (due != null && !firedTimes.contains(due)) {
            firedTimes.add(due);
        }
        currentWindowSlot = slot;
        windowOpen = true;
        windowEndEpochMs = System.currentTimeMillis() + durationMinutes * 60L * 1000L;
        bossSpawnedThisWindow = false;
        bossKilledThisWindow = false;
        damagers.clear();
        broadcast(messageOpen);
        if (!bossSpawnedThisWindow) {
            doSpawn();
            bossSpawnedThisWindow = true;
        }
        saveState();
        plugin.getLogger().info("Calamity OPEN slot=" + slot + " manual=" + manual
                + " endMs=" + windowEndEpochMs + " spawned=" + bossSpawnedThisWindow);
    }

    private void endWindow(boolean manual) {
        if (!windowOpen && !manual) return;
        windowOpen = false;
        despawnNearbyBoss();
        broadcast(messageEnd);
        saveState();
        plugin.getLogger().info("Calamity END slot=" + currentWindowSlot + " manual=" + manual
                + " killed=" + bossKilledThisWindow);
        currentWindowSlot = null;
        saveState();
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
                .replace("{z}", sz)
                .replace("{amount}", String.valueOf(amount))
                .replace("{mob}", mythicId);
        boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        plugin.getLogger().info("Calamity spawn " + (ok ? "OK" : "FAIL") + ": " + cmd);
    }

    private void despawnNearbyBoss() {
        World w = Bukkit.getWorld(worldName);
        if (w == null) return;
        Location center = new Location(w, x, y, z);
        int removed = 0;
        for (LivingEntity e : w.getLivingEntities()) {
            if (e instanceof Player) continue;
            if (!isCalamityEntity(e)) continue;
            if (e.getLocation().distanceSquared(center) > 80.0 * 80.0) continue;
            e.remove();
            removed++;
        }
        if (removed > 0) {
            plugin.getLogger().info("Calamity END despawned " + removed + " nearby boss entit(y/ies)");
        }
    }

    public void sendStatus(CommandSender sender) {
        if (!windowOpen) {
            sender.sendMessage(formatClosedMessage());
        } else {
            String endLabel = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(windowEndEpochMs), DailyService.zone()).format(HMS);
            sender.sendMessage(ChatColor.GREEN + "灾厄进行中"
                    + ChatColor.GRAY + " · 结束 " + ChatColor.WHITE + endLabel
                    + ChatColor.DARK_GRAY + " CST");
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PlayerDataStore store = plugin.getDataStore();
            if (store != null) {
                PlayerData data = store.get(p.getUniqueId());
                boolean claimed = DailyService.today().equals(data.getCalamityChestDate());
                sender.sendMessage(claimed
                        ? ChatColor.YELLOW + "日箱已领"
                        : ChatColor.GREEN + "日箱未领");
            }
        }
    }

    public boolean isCalamityEntity(LivingEntity entity) {
        if (entity == null) return false;
        String name = entity.getCustomName();
        if (name == null) return false;
        String plain = ChatColor.stripColor(name);
        if (plain == null) return false;
        if (plain.contains("余烬灾厄") || plain.contains("EmberCalamity")) return true;
        if (displayName != null && !displayName.isEmpty() && plain.contains(ChatColor.stripColor(displayName))) {
            return true;
        }
        return false;
    }

    public void onCalamityKilled(Player killer) {
        if (windowOpen) {
            if (bossKilledThisWindow) {
                return;
            }
            bossKilledThisWindow = true;
            saveState();
            plugin.getLogger().info("Calamity boss killed this window — no second spawn until END");
        }

        // Spec §3: only players who damaged the boss get A (参战) and are eligible for daily chest B.
        List<Player> recipients = new ArrayList<Player>();
        if (killer != null) recipients.add(killer);
        for (UUID id : damagers) {
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline() || recipients.contains(p)) continue;
            recipients.add(p);
        }
        damagers.clear();
        plugin.getLogger().info("Calamity kill settled: killer=" + (killer != null ? killer.getName() : "-")
                + " recipients=" + recipients.size());
        if (recipients.isEmpty()) return;

        for (int i = 0; i < recipients.size(); i++) {
            grantKillRewardsA(recipients.get(i));
            grantDailyChestB(recipients.get(i));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBossDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (!isCalamityEntity((LivingEntity) event.getEntity())) return;
        if (event.getFinalDamage() <= 0) return;
        Entity src = event.getDamager();
        Player p = null;
        if (src instanceof Player) {
            p = (Player) src;
        } else if (src instanceof Projectile && ((Projectile) src).getShooter() instanceof Player) {
            p = (Player) ((Projectile) src).getShooter();
        }
        if (p != null) damagers.add(p.getUniqueId());
    }

    private void grantKillRewardsA(Player player) {
        if (player == null) return;
        if (!killRewardCommands.isEmpty()) {
            for (int i = 0; i < killRewardCommands.size(); i++) {
                String raw = killRewardCommands.get(i);
                if (raw == null || raw.isEmpty()) continue;
                String cmd = raw.replace("{player}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        } else {
            NiBridge ni = plugin.getNiBridge();
            if (ni != null) {
                ni.giveNiItem(player, "mat_calamity_ember", 1);
                ni.giveNiItem(player, "mat_ember_core_fragment", 1);
                ni.giveNiItem(player, "cosmetic_calamity_shard", 1);
            }
        }
        player.sendMessage(ChatColor.GOLD + "[余烬] " + ChatColor.RED + "灾厄倒下。"
                + ChatColor.GRAY + "获得参战掉落。");
    }

    private void grantDailyChestB(Player player) {
        if (player == null || chestLimit <= 0) return;
        PlayerDataStore store = plugin.getDataStore();
        if (store == null) return;
        PlayerData data = store.get(player.getUniqueId());
        String today = DailyService.today();
        if (today.equals(data.getCalamityChestDate())) {
            player.sendMessage(ChatColor.YELLOW + "今日日箱已领，仅参战掉落");
            return;
        }
        NiBridge ni = plugin.getNiBridge();
        for (int i = 0; i < chestRewards.size(); i++) {
            ChestReward r = chestRewards.get(i);
            if (r.niId == null || r.niId.isEmpty() || r.amount <= 0) continue;
            if (ni != null) {
                ni.giveNiItem(player, r.niId, r.amount);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "ni give " + player.getName() + " " + r.niId + " " + r.amount);
            }
        }
        if (protectScrollChance > 0 && random.nextDouble() < protectScrollChance) {
            if (ni != null) {
                ni.giveNiItem(player, PROTECT_SCROLL_ID, 1);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "ni give " + player.getName() + " " + PROTECT_SCROLL_ID + " 1");
            }
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[余烬] 日箱额外掉落保护券。");
        }
        data.setCalamityChestDate(today);
        store.flushMutation(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "[余烬] " + ChatColor.YELLOW + "领取本日灾厄日箱。");
    }

    private String findDueSlot(LocalTime now) {
        int nowSec = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
        int windowSec = durationMinutes * 60;
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            int sched = parseMinutes(slot) * 60;
            if (sched < 0) continue;
            int elapsed = nowSec - sched;
            if (elapsed >= 0 && elapsed < windowSec) return slot;
        }
        return null;
    }

    private String nextSlot(LocalTime now) {
        int nowMin = now.getHour() * 60 + now.getMinute();
        String today = DailyService.today();
        for (int i = 0; i < times.size(); i++) {
            String slot = times.get(i);
            int m = parseMinutes(slot);
            if (m < 0) continue;
            boolean fired = today.equals(lastFireDate) && firedTimes.contains(slot);
            if (!fired && m >= nowMin) return slot;
        }
        if (!times.isEmpty()) {
            return times.get(0) + "（明日）";
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

    private static double firstDouble(FileConfiguration a, String ka, FileConfiguration b, String kb, double def) {
        if (a.contains(ka)) return a.getDouble(ka, def);
        if (b != null && b.contains(kb)) return b.getDouble(kb, def);
        return def;
    }

    private void loadChestRewards(FileConfiguration yml) {
        List<?> raw = yml.getList("daily_chest.rewards");
        if (raw == null) return;
        for (int i = 0; i < raw.size(); i++) {
            Object o = raw.get(i);
            String niId = null;
            int amt = 1;
            if (o instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) o;
                Object idObj = m.containsKey("ni") ? m.get("ni") : m.get("id");
                niId = idObj == null ? null : String.valueOf(idObj);
                Object a = m.get("amount");
                if (a instanceof Number) amt = ((Number) a).intValue();
                else if (a != null) {
                    try { amt = Integer.parseInt(String.valueOf(a)); } catch (NumberFormatException ignored) {}
                }
            } else if (o instanceof ConfigurationSection) {
                ConfigurationSection sec = (ConfigurationSection) o;
                niId = sec.getString("ni", sec.getString("id", null));
                amt = sec.getInt("amount", 1);
            }
            if (niId != null && !niId.isEmpty() && amt > 0) {
                chestRewards.add(new ChestReward(niId, amt));
            }
        }
    }

    private void broadcast(String raw) {
        if (raw == null || raw.isEmpty()) return;
        Bukkit.broadcastMessage(colorize(raw));
    }

    private static String colorize(String raw) {
        if (raw == null) return "";
        return ChatColor.translateAlternateColorCodes('&', raw.replace('§', '&'));
    }

    private void loadState() {
        if (!stateFile.exists()) return;
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(stateFile);
        lastFireDate = yaml.getString("lastFireDate", "");
        firedTimes.clear();
        List<String> raw = yaml.getStringList("firedTimes");
        if (raw != null) firedTimes.addAll(raw);
        String slot = yaml.getString("currentWindowSlot", "");
        currentWindowSlot = (slot == null || slot.isEmpty()) ? null : slot;
        windowOpen = yaml.getBoolean("windowOpen", false);
        windowEndEpochMs = yaml.getLong("windowEndEpochMs", 0L);
        bossSpawnedThisWindow = yaml.getBoolean("bossSpawnedThisWindow", false);
        bossKilledThisWindow = yaml.getBoolean("bossKilledThisWindow", false);
        announcedPre.clear();
        List<String> pre = yaml.getStringList("announcedPre");
        if (pre != null) announcedPre.addAll(pre);
        if (windowOpen && windowEndEpochMs > 0L && System.currentTimeMillis() >= windowEndEpochMs) {
            windowOpen = false;
            currentWindowSlot = null;
        }
    }

    private void saveState() {
        FileConfiguration yaml = new YamlConfiguration();
        yaml.set("lastFireDate", lastFireDate);
        yaml.set("firedTimes", new ArrayList<String>(firedTimes));
        yaml.set("currentWindowSlot", currentWindowSlot == null ? "" : currentWindowSlot);
        yaml.set("windowOpen", Boolean.valueOf(windowOpen));
        yaml.set("windowEndEpochMs", Long.valueOf(windowEndEpochMs));
        yaml.set("bossSpawnedThisWindow", Boolean.valueOf(bossSpawnedThisWindow));
        yaml.set("bossKilledThisWindow", Boolean.valueOf(bossKilledThisWindow));
        yaml.set("announcedPre", new ArrayList<String>(announcedPre));
        try {
            yaml.save(stateFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save calamity-state.yml", e);
        }
    }
}
