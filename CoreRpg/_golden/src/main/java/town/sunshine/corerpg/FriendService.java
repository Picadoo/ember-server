package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Friends + mentor + party invite (chat tip only). */
public final class FriendService {

    private static final String PREFIX = ChatColor.GREEN + "[好友] " + ChatColor.RESET;

    private final JavaPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private int maxFriends = 40;

    public FriendService(JavaPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "friend.yml");
        if (!file.exists()) {
            plugin.saveResource("friend.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("friend.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        maxFriends = Math.max(1, cfg.getInt("max_friends", 40));
    }

    public boolean isEnabled() { return enabled; }
    public int getMaxFriends() { return maxFriends; }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "好友系统未启用。");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.friend") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.friend");
            return;
        }
        if (args.length < 2) {
            cmdList(p);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if ("list".equals(sub)) {
            cmdList(p);
            return;
        }
        if ("add".equals(sub)) {
            cmdAdd(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("accept".equals(sub)) {
            cmdAccept(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("deny".equals(sub) || "reject".equals(sub)) {
            cmdDeny(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("remove".equals(sub) || "del".equals(sub)) {
            cmdRemove(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("invite".equals(sub)) {
            cmdInvite(p, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("mentor".equals(sub)) {
            cmdMentor(p, args);
            return;
        }
        cmdList(p);
    }

    private void cmdList(Player p) {
        PlayerData data = dataStore.get(p.getUniqueId());
        List<String> friends = new ArrayList<String>(data.getFriends());
        Collections.sort(friends, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                boolean ao = Bukkit.getPlayerExact(a) != null;
                boolean bo = Bukkit.getPlayerExact(b) != null;
                if (ao != bo) return ao ? -1 : 1;
                return a.compareToIgnoreCase(b);
            }
        });
        p.sendMessage(PREFIX + ChatColor.YELLOW + "好友 §f" + friends.size() + "/" + maxFriends);
        if (friends.isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "  （空）· /corerpg friend add <名>");
        } else {
            int shown = 0;
            for (String name : friends) {
                if (shown >= 30) {
                    p.sendMessage(ChatColor.DARK_GRAY + "  … 还有 " + (friends.size() - shown) + " 人");
                    break;
                }
                Player online = Bukkit.getPlayerExact(name);
                String flag = online != null && online.isOnline()
                        ? ChatColor.GREEN + "[在线]" : ChatColor.DARK_GRAY + "[离线]";
                p.sendMessage(ChatColor.GRAY + "  " + flag + ChatColor.WHITE + " " + name);
                shown++;
            }
        }
        if (!data.getPendingIn().isEmpty()) {
            p.sendMessage(ChatColor.AQUA + "  待处理申请: " + ChatColor.WHITE + join(data.getPendingIn()));
            p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg friend accept|deny <名>");
        }
        if (!data.getPendingOut().isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "  已发出: " + join(data.getPendingOut()));
        }
        String mentor = data.getMentorName();
        if (mentor != null && !mentor.isEmpty()) {
            p.sendMessage(ChatColor.GOLD + "  师徒: §f" + mentor);
        } else if (data.getMentorPending() != null && !data.getMentorPending().isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "  师徒待确认: §f" + data.getMentorPending());
        }
        p.sendMessage(ChatColor.DARK_GRAY + "  add|accept|deny|remove|invite|mentor …");
    }

    private void cmdAdd(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend add <名>");
            return;
        }
        if (p.getName().equalsIgnoreCase(targetName)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能添加自己。");
            return;
        }
        OfflinePlayer targetOff = resolveOffline(targetName);
        if (targetOff == null || targetOff.getUniqueId() == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到玩家: " + targetName);
            return;
        }
        String canon = resolveName(targetOff, targetName);
        PlayerData self = dataStore.get(p.getUniqueId());
        PlayerData other = dataStore.get(targetOff.getUniqueId());
        if (containsIgnoreCase(self.getFriends(), canon)) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "已是好友: " + canon);
            return;
        }
        if (self.getFriends().size() >= maxFriends) {
            p.sendMessage(PREFIX + ChatColor.RED + "好友已满（" + maxFriends + "）。");
            return;
        }
        if (other.getFriends().size() >= maxFriends) {
            p.sendMessage(PREFIX + ChatColor.RED + "对方好友已满。");
            return;
        }
        // privacy ON = refuse stranger friend requests
        if (other.isSettingsPrivacy() && !containsIgnoreCase(other.getFriends(), p.getName())) {
            p.sendMessage(PREFIX + ChatColor.RED + "对方开启了隐私，拒收陌生人好友申请。");
            return;
        }
        if (containsIgnoreCase(self.getPendingOut(), canon)) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "已向对方发出申请，等待确认。");
            return;
        }
        // if they already sent us a request, auto-accept
        if (containsIgnoreCase(self.getPendingIn(), canon)) {
            acceptPair(p, self, targetOff.getUniqueId(), other, canon);
            return;
        }
        addUnique(self.getPendingOut(), canon);
        addUnique(other.getPendingIn(), p.getName());
        self.markDirty();
        other.markDirty();
        dataStore.flushMutation(p.getUniqueId());
        dataStore.flushMutation(targetOff.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已向 §f" + canon + ChatColor.GREEN + " 发出好友申请。");
        Player online = Bukkit.getPlayer(targetOff.getUniqueId());
        if (online != null && online.isOnline()) {
            online.sendMessage(PREFIX + ChatColor.YELLOW + p.getName()
                    + ChatColor.GRAY + " 申请加好友 · /corerpg friend accept " + p.getName());
        }
    }

    private void cmdAccept(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend accept <名>");
            return;
        }
        PlayerData self = dataStore.get(p.getUniqueId());
        String match = findIgnoreCase(self.getPendingIn(), targetName);
        if (match == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "没有来自 " + targetName + " 的申请。");
            return;
        }
        OfflinePlayer targetOff = resolveOffline(match);
        if (targetOff == null || targetOff.getUniqueId() == null) {
            self.getPendingIn().remove(match);
            self.markDirty();
            dataStore.flushMutation(p.getUniqueId());
            p.sendMessage(PREFIX + ChatColor.RED + "找不到玩家数据: " + match);
            return;
        }
        PlayerData other = dataStore.get(targetOff.getUniqueId());
        acceptPair(p, self, targetOff.getUniqueId(), other, resolveName(targetOff, match));
    }

    private void acceptPair(Player p, PlayerData self, UUID otherId, PlayerData other, String otherName) {
        if (self.getFriends().size() >= maxFriends || other.getFriends().size() >= maxFriends) {
            p.sendMessage(PREFIX + ChatColor.RED + "好友名额不足，无法接受。");
            return;
        }
        removeIgnoreCase(self.getPendingIn(), otherName);
        removeIgnoreCase(self.getPendingOut(), otherName);
        removeIgnoreCase(other.getPendingIn(), p.getName());
        removeIgnoreCase(other.getPendingOut(), p.getName());
        addUnique(self.getFriends(), otherName);
        addUnique(other.getFriends(), p.getName());
        self.markDirty();
        other.markDirty();
        dataStore.flushMutation(p.getUniqueId());
        dataStore.flushMutation(otherId);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已与 §f" + otherName + ChatColor.GREEN + " 成为好友。");
        Player online = Bukkit.getPlayer(otherId);
        if (online != null && online.isOnline()) {
            online.sendMessage(PREFIX + ChatColor.GREEN + p.getName() + " 已接受你的好友申请。");
        }
    }

    private void cmdDeny(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend deny <名>");
            return;
        }
        PlayerData self = dataStore.get(p.getUniqueId());
        String match = findIgnoreCase(self.getPendingIn(), targetName);
        if (match == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "没有来自 " + targetName + " 的申请。");
            return;
        }
        removeIgnoreCase(self.getPendingIn(), match);
        self.markDirty();
        dataStore.flushMutation(p.getUniqueId());
        OfflinePlayer targetOff = resolveOffline(match);
        if (targetOff != null && targetOff.getUniqueId() != null) {
            PlayerData other = dataStore.get(targetOff.getUniqueId());
            removeIgnoreCase(other.getPendingOut(), p.getName());
            other.markDirty();
            dataStore.flushMutation(targetOff.getUniqueId());
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW + "已拒绝 " + match + " 的好友申请。");
    }

    private void cmdRemove(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend remove <名>");
            return;
        }
        PlayerData self = dataStore.get(p.getUniqueId());
        String match = findIgnoreCase(self.getFriends(), targetName);
        if (match == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "不在好友列表: " + targetName);
            return;
        }
        removeIgnoreCase(self.getFriends(), match);
        self.markDirty();
        dataStore.flushMutation(p.getUniqueId());
        OfflinePlayer targetOff = resolveOffline(match);
        if (targetOff != null && targetOff.getUniqueId() != null) {
            PlayerData other = dataStore.get(targetOff.getUniqueId());
            removeIgnoreCase(other.getFriends(), p.getName());
            other.markDirty();
            dataStore.flushMutation(targetOff.getUniqueId());
            Player online = Bukkit.getPlayer(targetOff.getUniqueId());
            if (online != null && online.isOnline()) {
                online.sendMessage(PREFIX + ChatColor.GRAY + p.getName() + " 已与你解除好友。");
            }
        }
        p.sendMessage(PREFIX + ChatColor.GREEN + "已解除与 §f" + match + ChatColor.GREEN + " 的好友关系。");
    }

    private void cmdInvite(Player p, String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend invite <名>");
            return;
        }
        PlayerData self = dataStore.get(p.getUniqueId());
        String match = findIgnoreCase(self.getFriends(), targetName);
        if (match == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "只能邀请好友组队。先 /corerpg friend add");
            return;
        }
        Player online = Bukkit.getPlayerExact(match);
        if (online == null || !online.isOnline()) {
            p.sendMessage(PREFIX + ChatColor.RED + match + " 不在线。");
            return;
        }
        p.sendMessage(PREFIX + ChatColor.AQUA + "已向 §f" + match + ChatColor.AQUA + " 发出组队邀请（不强制进队）。");
        online.sendMessage(PREFIX + ChatColor.AQUA + p.getName()
                + ChatColor.GRAY + " 邀请你组队（团本/周本）· 自行决定是否跟随。");
    }

    private void cmdMentor(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg friend mentor <名|accept|break>");
            return;
        }
        String act = args[2].toLowerCase(Locale.ROOT);
        PlayerData self = dataStore.get(p.getUniqueId());
        if ("break".equals(act) || "clear".equals(act) || "cancel".equals(act)) {
            String old = self.getMentorName();
            if (old == null || old.isEmpty()) {
                if (self.getMentorPending() != null && !self.getMentorPending().isEmpty()) {
                    String pend = self.getMentorPending();
                    self.setMentorPending("");
                    dataStore.flushMutation(p.getUniqueId());
                    OfflinePlayer off = resolveOffline(pend);
                    if (off != null && off.getUniqueId() != null) {
                        PlayerData other = dataStore.get(off.getUniqueId());
                        if (p.getName().equalsIgnoreCase(other.getMentorPending())) {
                            other.setMentorPending("");
                            dataStore.flushMutation(off.getUniqueId());
                        }
                    }
                    p.sendMessage(PREFIX + ChatColor.YELLOW + "已取消师徒申请。");
                    return;
                }
                p.sendMessage(PREFIX + ChatColor.YELLOW + "当前没有师徒关系。");
                return;
            }
            self.setMentorName("");
            dataStore.flushMutation(p.getUniqueId());
            OfflinePlayer off = resolveOffline(old);
            if (off != null && off.getUniqueId() != null) {
                PlayerData other = dataStore.get(off.getUniqueId());
                if (p.getName().equalsIgnoreCase(other.getMentorName())) {
                    other.setMentorName("");
                    dataStore.flushMutation(off.getUniqueId());
                }
                Player online = Bukkit.getPlayer(off.getUniqueId());
                if (online != null && online.isOnline()) {
                    online.sendMessage(PREFIX + ChatColor.GRAY + p.getName() + " 已解除师徒关系。");
                }
            }
            p.sendMessage(PREFIX + ChatColor.GREEN + "已解除与 §f" + old + ChatColor.GREEN + " 的师徒关系。");
            return;
        }
        if ("accept".equals(act)) {
            String pend = self.getMentorPending();
            if (pend == null || pend.isEmpty()) {
                p.sendMessage(PREFIX + ChatColor.RED + "没有待确认的师徒申请。");
                return;
            }
            if (self.getMentorName() != null && !self.getMentorName().isEmpty()) {
                p.sendMessage(PREFIX + ChatColor.RED + "你已有师徒: " + self.getMentorName());
                return;
            }
            OfflinePlayer off = resolveOffline(pend);
            if (off == null || off.getUniqueId() == null) {
                self.setMentorPending("");
                dataStore.flushMutation(p.getUniqueId());
                p.sendMessage(PREFIX + ChatColor.RED + "找不到申请人: " + pend);
                return;
            }
            PlayerData other = dataStore.get(off.getUniqueId());
            String otherName = resolveName(off, pend);
            self.setMentorName(otherName);
            self.setMentorPending("");
            other.setMentorName(p.getName());
            other.setMentorPending("");
            dataStore.flushMutation(p.getUniqueId());
            dataStore.flushMutation(off.getUniqueId());
            p.sendMessage(PREFIX + ChatColor.GOLD + "已与 §f" + otherName + ChatColor.GOLD + " 结成师徒。");
            Player online = Bukkit.getPlayer(off.getUniqueId());
            if (online != null && online.isOnline()) {
                online.sendMessage(PREFIX + ChatColor.GOLD + p.getName() + " 已接受师徒绑定。");
            }
            return;
        }
        // request mentor with name
        String targetName = args[2];
        if (p.getName().equalsIgnoreCase(targetName)) {
            p.sendMessage(PREFIX + ChatColor.RED + "不能拜自己为师。");
            return;
        }
        if (self.getMentorName() != null && !self.getMentorName().isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "你已有师徒: " + self.getMentorName() + " · mentor break 解除");
            return;
        }
        OfflinePlayer off = resolveOffline(targetName);
        if (off == null || off.getUniqueId() == null) {
            p.sendMessage(PREFIX + ChatColor.RED + "找不到玩家: " + targetName);
            return;
        }
        String canon = resolveName(off, targetName);
        PlayerData other = dataStore.get(off.getUniqueId());
        if (other.getMentorName() != null && !other.getMentorName().isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.RED + "对方已有师徒关系。");
            return;
        }
        self.setMentorPending(canon);
        other.setMentorPending(p.getName());
        dataStore.flushMutation(p.getUniqueId());
        dataStore.flushMutation(off.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GOLD + "已向 §f" + canon + ChatColor.GOLD + " 发出师徒申请。");
        Player online = Bukkit.getPlayer(off.getUniqueId());
        if (online != null && online.isOnline()) {
            online.sendMessage(PREFIX + ChatColor.GOLD + p.getName()
                    + ChatColor.GRAY + " 申请结成师徒 · /corerpg friend mentor accept");
        }
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveOffline(String name) {
        if (name == null || name.isEmpty()) return null;
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;
        OfflinePlayer off = Bukkit.getOfflinePlayer(name);
        if (off != null && (off.hasPlayedBefore() || off.isOnline())) return off;
        if (off != null && off.getUniqueId() != null) return off;
        return null;
    }

    private static String resolveName(OfflinePlayer off, String fallback) {
        if (off.getName() != null && !off.getName().isEmpty()) return off.getName();
        return fallback;
    }

    private static boolean containsIgnoreCase(List<String> list, String name) {
        return findIgnoreCase(list, name) != null;
    }

    private static String findIgnoreCase(List<String> list, String name) {
        if (list == null || name == null) return null;
        for (String s : list) {
            if (s != null && s.equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    private static void removeIgnoreCase(List<String> list, String name) {
        if (list == null || name == null) return;
        for (int i = list.size() - 1; i >= 0; i--) {
            String s = list.get(i);
            if (s != null && s.equalsIgnoreCase(name)) list.remove(i);
        }
    }

    private static void addUnique(List<String> list, String name) {
        if (name == null || name.isEmpty()) return;
        if (!containsIgnoreCase(list, name)) list.add(name);
    }

    private static String join(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s);
        }
        return sb.toString();
    }
}
