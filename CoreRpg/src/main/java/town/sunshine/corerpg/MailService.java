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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/** System mail inbox — templates + claim via NI / coin / crystal cash. */
public final class MailService {

    private static final String PREFIX = ChatColor.GOLD + "[邮寄] " + ChatColor.RESET;

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;
    private final File mailDir;

    private boolean enabled = true;
    private int maxInbox = 50;
    private final Map<String, MailTemplate> templates = new LinkedHashMap<String, MailTemplate>();

    static final class MailTemplate {
        final String id;
        final String title;
        final String body;
        final Map<String, Integer> attachments;
        MailTemplate(String id, String title, String body, Map<String, Integer> attachments) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.attachments = attachments;
        }
    }

    static final class MailMessage {
        int id;
        String template = "";
        String title = "";
        String body = "";
        Map<String, Integer> attachments = new LinkedHashMap<String, Integer>();
        boolean read;
        boolean claimed;
        long created;

        boolean hasAttachment() {
            return attachments != null && !attachments.isEmpty();
        }

        boolean canDelete() {
            return claimed || !hasAttachment();
        }
    }

    public MailService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
        this.mailDir = new File(plugin.getDataFolder(), "mail");
        if (!mailDir.exists()) mailDir.mkdirs();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "mail.yml");
        if (!file.exists()) {
            plugin.saveResource("mail.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("mail.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        maxInbox = Math.max(1, cfg.getInt("max_inbox", 50));
        templates.clear();
        ConfigurationSection sec = cfg.getConfigurationSection("templates");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                ConfigurationSection t = sec.getConfigurationSection(key);
                if (t == null) continue;
                String title = t.getString("title", key);
                String body = t.getString("body", "");
                Map<String, Integer> atts = new LinkedHashMap<String, Integer>();
                ConfigurationSection aSec = t.getConfigurationSection("attachments");
                if (aSec != null) {
                    for (String aKey : aSec.getKeys(false)) {
                        int amt = aSec.getInt(aKey, 0);
                        if (amt > 0) atts.put(aKey, Integer.valueOf(amt));
                    }
                }
                templates.put(key, new MailTemplate(key, title, body, atts));
            }
        }
        if (!mailDir.exists()) mailDir.mkdirs();
    }

    public boolean isEnabled() { return enabled; }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "邮寄系统未启用。");
            return;
        }
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg mail send <玩家> <模板id>");
                return;
            }
            cmdList((Player) sender);
            return;
        }
        String sub = args[1].toLowerCase();
        if ("send".equals(sub)) {
            cmdSend(sender, args);
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only (except mail send)");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.mail") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.mail");
            return;
        }
        if ("read".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg mail read <id>");
                return;
            }
            cmdRead(p, parseId(args[2]));
            return;
        }
        if ("claim".equals(sub)) {
            if (args.length >= 3 && "all".equalsIgnoreCase(args[2])) {
                cmdClaimAll(p);
                return;
            }
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg mail claim <id|all>");
                return;
            }
            cmdClaim(p, parseId(args[2]));
            return;
        }
        if ("delete".equals(sub) || "del".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg mail delete <id>");
                return;
            }
            cmdDelete(p, parseId(args[2]));
            return;
        }
        if ("list".equals(sub)) {
            cmdList(p);
            return;
        }
        cmdList(p);
    }

    private int parseId(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    private void cmdList(Player p) {
        List<MailMessage> list = loadInbox(p.getUniqueId());
        if (list.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.GRAY + "收件箱为空。");
            return;
        }
        List<MailMessage> sorted = new ArrayList<MailMessage>(list);
        Collections.sort(sorted, new Comparator<MailMessage>() {
            @Override
            public int compare(MailMessage a, MailMessage b) {
                if (a.read != b.read) return a.read ? 1 : -1;
                return Long.compare(b.created, a.created);
            }
        });
        int unread = 0;
        for (MailMessage m : list) if (!m.read) unread++;
        p.sendMessage(PREFIX + ChatColor.YELLOW + "收件箱 §f" + list.size() + "/" + maxInbox
                + ChatColor.GRAY + " · 未读 " + unread);
        int shown = 0;
        for (MailMessage m : sorted) {
            if (shown >= 20) {
                p.sendMessage(ChatColor.DARK_GRAY + "  … 还有 " + (sorted.size() - shown) + " 封");
                break;
            }
            String flag = m.read ? ChatColor.DARK_GRAY + "[已读]" : ChatColor.GREEN + "[未读]";
            String att;
            if (!m.hasAttachment()) att = ChatColor.DARK_GRAY + "无附件";
            else if (m.claimed) att = ChatColor.DARK_GRAY + "已领";
            else att = ChatColor.AQUA + "有附件";
            p.sendMessage(ChatColor.GRAY + "  #" + m.id + " " + flag + ChatColor.WHITE + " "
                    + m.title + " " + att);
            shown++;
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg mail read|claim|delete <id> · claim all");
    }

    private void cmdRead(Player p, int id) {
        if (id < 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "无效邮件 id");
            return;
        }
        List<MailMessage> list = loadInbox(p.getUniqueId());
        MailMessage m = find(list, id);
        if (m == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到邮件 #" + id);
            return;
        }
        m.read = true;
        saveInbox(p.getUniqueId(), list);
        p.sendMessage(PREFIX + ChatColor.YELLOW + m.title);
        if (m.body != null && !m.body.isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "  " + m.body);
        }
        if (m.hasAttachment()) {
            p.sendMessage(ChatColor.AQUA + "  附件: " + formatAttachments(m.attachments)
                    + (m.claimed ? ChatColor.DARK_GRAY + "（已领）" : ChatColor.GREEN + "（未领 · /corerpg mail claim " + id + "）"));
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "  无附件");
        }
    }

    private void cmdClaim(Player p, int id) {
        if (id < 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "无效邮件 id");
            return;
        }
        List<MailMessage> list = loadInbox(p.getUniqueId());
        MailMessage m = find(list, id);
        if (m == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到邮件 #" + id);
            return;
        }
        if (!m.hasAttachment()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "该信无附件。");
            return;
        }
        if (m.claimed) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "附件已领取。");
            return;
        }
        if (!grantAttachments(p, m.attachments)) {
            p.sendMessage(PREFIX + ChatColor.RED + "发放失败，请稍后重试。");
            return;
        }
        m.claimed = true;
        m.read = true;
        saveInbox(p.getUniqueId(), list);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已领取 #" + id + " 附件："
                + ChatColor.WHITE + formatAttachments(m.attachments));
    }

    private void cmdClaimAll(Player p) {
        List<MailMessage> list = loadInbox(p.getUniqueId());
        int n = 0;
        for (MailMessage m : list) {
            if (!m.hasAttachment() || m.claimed) continue;
            if (!grantAttachments(p, m.attachments)) {
                p.sendMessage(PREFIX + ChatColor.RED + "领取 #" + m.id + " 失败，已停止。");
                break;
            }
            m.claimed = true;
            m.read = true;
            n++;
        }
        if (n > 0) saveInbox(p.getUniqueId(), list);
        if (n == 0) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "没有可领取的附件。");
        } else {
            p.sendMessage(PREFIX + ChatColor.GREEN + "一键领取完成：§f" + n + ChatColor.GREEN + " 封。");
        }
    }

    private void cmdDelete(Player p, int id) {
        if (id < 0) {
            p.sendMessage(PREFIX + ChatColor.RED + "无效邮件 id");
            return;
        }
        List<MailMessage> list = loadInbox(p.getUniqueId());
        MailMessage m = find(list, id);
        if (m == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到邮件 #" + id);
            return;
        }
        if (!m.canDelete()) {
            p.sendMessage(PREFIX + ChatColor.RED + "未领取附件的邮件不可删除。请先 claim。");
            return;
        }
        list.remove(m);
        saveInbox(p.getUniqueId(), list);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已删除邮件 #" + id);
    }

    private void cmdSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.mail.send") && !sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.mail.send 或 corerpg.admin");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg mail send <玩家> <模板id>");
            sender.sendMessage(ChatColor.DARK_GRAY + "  模板: " + joinTemplateIds());
            return;
        }
        String playerName = args[2];
        String templateId = args[3];
        MailTemplate tpl = templates.get(templateId);
        if (tpl == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "未知模板: " + templateId
                    + ChatColor.GRAY + " · 可用: " + joinTemplateIds());
            return;
        }
        UUID uuid = resolveUuid(playerName);
        if (uuid == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "找不到玩家: " + playerName);
            return;
        }
        boolean ok = deliver(uuid, tpl);
        if (!ok) {
            sender.sendMessage(PREFIX + ChatColor.RED + "投递失败（收件箱已满且无可清理旧信）。");
            return;
        }
        sender.sendMessage(PREFIX + ChatColor.GREEN + "已投递模板 §e" + templateId
                + ChatColor.GREEN + " → §f" + playerName);
        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) {
            online.sendMessage(PREFIX + ChatColor.YELLOW + "收到新邮件：§f" + tpl.title
                    + ChatColor.GRAY + " · /corerpg mail");
        }
    }


    /** Unread mail count for PAPI / UI. */
    public int countUnread(java.util.UUID uuid) {
        if (uuid == null) return 0;
        int n = 0;
        for (MailMessage m : loadInbox(uuid)) {
            if (!m.read) n++;
        }
        return n;
    }

    /** Deliver a template mail to uuid. Returns false if inbox full and cannot prune. */
    public boolean deliver(UUID uuid, MailTemplate tpl) {
        if (tpl == null || uuid == null) return false;
        List<MailMessage> list = loadInbox(uuid);
        while (list.size() >= maxInbox) {
            MailMessage prune = findPrunable(list);
            if (prune == null) return false;
            list.remove(prune);
        }
        MailMessage m = new MailMessage();
        m.id = nextId(list);
        m.template = tpl.id;
        m.title = tpl.title;
        m.body = tpl.body;
        m.attachments = new LinkedHashMap<String, Integer>(tpl.attachments);
        m.read = false;
        m.claimed = !m.hasAttachment();
        m.created = System.currentTimeMillis();
        list.add(m);
        saveInbox(uuid, list);
        return true;
    }

    /** 1.6.0: ad-hoc system mail (pass level rewards). */
    public boolean deliverCustom(UUID uuid, String id, String title, String body, Map<String, Integer> attachments) {
        return deliver(uuid, new MailTemplate(id, title, body, new LinkedHashMap<String, Integer>(attachments)));
    }

    public boolean deliverByTemplateId(UUID uuid, String templateId) {
        MailTemplate tpl = templates.get(templateId);
        if (tpl == null) return false;
        return deliver(uuid, tpl);
    }

    private MailMessage findPrunable(List<MailMessage> list) {
        MailMessage oldest = null;
        for (MailMessage m : list) {
            if (!m.canDelete()) continue;
            if (oldest == null || m.created < oldest.created) oldest = m;
        }
        return oldest;
    }

    private int nextId(List<MailMessage> list) {
        int max = 0;
        for (MailMessage m : list) if (m.id > max) max = m.id;
        return max + 1;
    }

    private MailMessage find(List<MailMessage> list, int id) {
        for (MailMessage m : list) if (m.id == id) return m;
        return null;
    }

    private boolean grantAttachments(Player p, Map<String, Integer> atts) {
        if (atts == null || atts.isEmpty()) return true;
        PlayerData data = dataStore.get(p.getUniqueId());
        for (Map.Entry<String, Integer> e : atts.entrySet()) {
            String key = e.getKey();
            int amt = e.getValue().intValue();
            if (amt <= 0) continue;
            if ("coin".equalsIgnoreCase(key) || "余烬币".equals(key)) {
                data.addCoin(amt);
                continue;
            }
            if ("ember_crystal_cash".equalsIgnoreCase(key)
                    || "crystal".equalsIgnoreCase(key)
                    || "crystal_cash".equalsIgnoreCase(key)
                    || "cash".equalsIgnoreCase(key)) {
                data.addCrystalCash(amt);
                continue;
            }
            if (ni == null || !ni.giveNiItem(p, key, amt)) {
                plugin.getLogger().warning("Mail NI grant failed: " + key + " x" + amt + " -> " + p.getName());
                // still continue other grants; mark overall success if coin/cash already applied
            }
        }
        dataStore.flushMutation(p.getUniqueId());
        return true;
    }

    private String formatAttachments(Map<String, Integer> atts) {
        if (atts == null || atts.isEmpty()) return "无";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : atts.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getKey()).append("×").append(e.getValue());
        }
        return sb.toString();
    }

    private String joinTemplateIds() {
        if (templates.isEmpty()) return "(无)";
        StringBuilder sb = new StringBuilder();
        for (String id : templates.keySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(id);
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    private UUID resolveUuid(String name) {
        if (name == null || name.isEmpty()) return null;
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online.getUniqueId();
        OfflinePlayer off = Bukkit.getOfflinePlayer(name);
        if (off != null && (off.hasPlayedBefore() || off.isOnline())) {
            return off.getUniqueId();
        }
        // still accept if OfflinePlayer returns a uuid (may be new)
        if (off != null && off.getUniqueId() != null && off.getName() != null) {
            return off.getUniqueId();
        }
        return null;
    }

    List<MailMessage> loadInbox(UUID uuid) {
        List<MailMessage> list = new ArrayList<MailMessage>();
        FileConfiguration yaml = null;
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                String raw = mysql.loadMailInboxYaml(uuid);
                if (raw != null && !raw.isEmpty()) {
                    YamlConfiguration y = new YamlConfiguration();
                    y.loadFromString(raw);
                    yaml = y;
                } else {
                    return list;
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Failed to load mail MySQL " + uuid, t);
            }
        }
        if (yaml == null) {
            File file = new File(mailDir, uuid.toString() + ".yml");
            if (!file.exists()) return list;
            yaml = YamlConfiguration.loadConfiguration(file);
        }
        List<?> raw = yaml.getList("messages");
        if (raw == null) return list;
        for (Object o : raw) {
            if (!(o instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>) o;
            MailMessage m = new MailMessage();
            m.id = toInt(map.get("id"), 0);
            m.template = str(map.get("template"));
            m.title = str(map.get("title"));
            m.body = str(map.get("body"));
            m.read = toBool(map.get("read"), false);
            m.claimed = toBool(map.get("claimed"), false);
            m.created = toLong(map.get("created"), System.currentTimeMillis());
            Object attObj = map.get("attachments");
            if (attObj instanceof Map) {
                Map<?, ?> am = (Map<?, ?>) attObj;
                for (Map.Entry<?, ?> en : am.entrySet()) {
                    if (en.getKey() == null) continue;
                    int amt = toInt(en.getValue(), 0);
                    if (amt > 0) m.attachments.put(String.valueOf(en.getKey()), Integer.valueOf(amt));
                }
            }
            if (m.id > 0) list.add(m);
        }
        return list;
    }

    void saveInbox(UUID uuid, List<MailMessage> list) {
        YamlConfiguration yaml = inboxToYaml(list);
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                mysql.saveMailInboxYaml(uuid, yaml.saveToString());
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save mail MySQL " + uuid, e);
            }
            return;
        }
        if (!mailDir.exists()) mailDir.mkdirs();
        File file = new File(mailDir, uuid.toString() + ".yml");
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save mail " + uuid, e);
        }
    }

    YamlConfiguration inboxToYaml(List<MailMessage> list) {
        YamlConfiguration yaml = new YamlConfiguration();
        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        for (MailMessage m : list) {
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("id", Integer.valueOf(m.id));
            row.put("template", m.template);
            row.put("title", m.title);
            row.put("body", m.body);
            row.put("attachments", new LinkedHashMap<String, Integer>(m.attachments));
            row.put("read", Boolean.valueOf(m.read));
            row.put("claimed", Boolean.valueOf(m.claimed));
            row.put("created", Long.valueOf(m.created));
            out.add(row);
        }
        yaml.set("messages", out);
        return yaml;
    }

    private MysqlStorage mysqlOrNull() {
        if (plugin instanceof CoreRpgPlugin) {
            MysqlStorage m = ((CoreRpgPlugin) plugin).getMysqlStorage();
            if (m != null && m.isActive()) return m;
        }
        return null;
    }

    public File getMailDir() { return mailDir; }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o != null) {
            try { return Integer.parseInt(String.valueOf(o)); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

    private static long toLong(Object o, long def) {
        if (o instanceof Number) return ((Number) o).longValue();
        if (o != null) {
            try { return Long.parseLong(String.valueOf(o)); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

    private static boolean toBool(Object o, boolean def) {
        if (o instanceof Boolean) return ((Boolean) o).booleanValue();
        if (o != null) {
            String s = String.valueOf(o).toLowerCase();
            if ("true".equals(s) || "yes".equals(s) || "1".equals(s)) return true;
            if ("false".equals(s) || "no".equals(s) || "0".equals(s)) return false;
        }
        return def;
    }
}
