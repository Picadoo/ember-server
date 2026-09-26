package town.sunshine.corerpg;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 1.8.0 (2026-09-26): mainline quest 「旧誓余烬」. Chapters/steps in quest.yml; progress in PlayerData (MySQL blob).
 * Hooks: MythicMobs kills (reflection), DP clears via /corerpg progress, sign, bounty, enchant/anvil/craft (vanilla events),
 * calamity join/boss, ember level-ups, Adyeshach NPC interact (reflection, soft).
 */
public final class QuestService implements Listener {

    static final class Step {
        String type = "talk";
        String event = "";
        String completeOn = "";
        List<String> mobs = new ArrayList<String>();
        int count = 1;
        int level = 0;
        String desc = "";
        String hint = "";
        int xp = 0;
        int levels = 0;
        Map<String, Integer> items = new LinkedHashMap<String, Integer>();
        List<String> done = new ArrayList<String>();
    }

    static final class Chapter {
        int no;
        String name = "";
        List<String> intro = new ArrayList<String>();
        Map<String, Integer> startItems = new LinkedHashMap<String, Integer>();
        List<Step> steps = new ArrayList<Step>();
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;
    private final TreeMap<Integer, Chapter> chapters = new TreeMap<Integer, Chapter>();
    private boolean enabled = true;
    private boolean autoStart = true;
    private String title = "§6[主线]";
    private boolean npcEnabled = true;
    private String npcId = "ember_guide";
    private String npcName = "§6引路人 · 灰烛";
    private String npcWorld = "ember_hub";
    private double npcX, npcY, npcZ;
    private float npcYaw;
    private double talkRadius = 8;
    private final Map<UUID, Long> talkCooldown = new HashMap<UUID, Long>();
    private boolean adyHooked = false;
    // MythicMobs reflection cache
    private Object mmApi;
    private Method mmIsMythic, mmGetInstance, mmGetType, mmInternalName;
    private boolean mmTried = false;
    private boolean mmWarned = false;

    public QuestService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    // ---------------- config ----------------

    public void reload() {
        File file = new File(plugin.getDataFolder(), "quest.yml");
        if (!file.exists()) plugin.saveResource("quest.yml", false);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enabled = cfg.getBoolean("enabled", true);
        autoStart = cfg.getBoolean("auto_start", true);
        title = color(cfg.getString("title", "§6[主线]"));
        npcEnabled = cfg.getBoolean("npc.enabled", true);
        npcId = cfg.getString("npc.id", "ember_guide");
        npcName = color(cfg.getString("npc.name", "§6引路人 · 灰烛"));
        npcWorld = cfg.getString("npc.world", "ember_hub");
        npcX = cfg.getDouble("npc.x", -16.5);
        npcY = cfg.getDouble("npc.y", 58.0);
        npcZ = cfg.getDouble("npc.z", 106.5);
        npcYaw = (float) cfg.getDouble("npc.yaw", 0.0);
        talkRadius = cfg.getDouble("npc.talk_radius", 8.0);
        chapters.clear();
        ConfigurationSection cs = cfg.getConfigurationSection("chapters");
        if (cs != null) {
            for (String k : cs.getKeys(false)) {
                ConfigurationSection c = cs.getConfigurationSection(k);
                if (c == null) continue;
                Chapter ch = new Chapter();
                try { ch.no = Integer.parseInt(k); } catch (NumberFormatException e) { continue; }
                ch.name = c.getString("name", "第" + k + "章");
                for (String l : c.getStringList("intro")) ch.intro.add(color(l));
                readItems(c.getConfigurationSection("start_items"), ch.startItems);
                for (Map<?, ?> m : c.getMapList("steps")) ch.steps.add(readStep(m));
                if (!ch.steps.isEmpty()) chapters.put(ch.no, ch);
            }
        }
        plugin.getLogger().info("Quest: " + chapters.size() + " chapters loaded (enabled=" + enabled + ")");
    }

    @SuppressWarnings("unchecked")
    private Step readStep(Map<?, ?> m) {
        Step s = new Step();
        s.type = str(m.get("type"), "talk").toLowerCase();
        s.event = str(m.get("event"), "").toLowerCase();
        s.completeOn = str(m.get("complete_on"), "").toLowerCase();
        Object mobs = m.get("mobs");
        if (mobs instanceof List) for (Object o : (List<Object>) mobs) s.mobs.add(String.valueOf(o));
        s.count = Math.max(1, num(m.get("count"), 1));
        s.level = num(m.get("level"), 0);
        s.desc = color(str(m.get("desc"), ""));
        s.hint = color(str(m.get("hint"), ""));
        s.xp = Math.max(0, num(m.get("xp"), 0));
        s.levels = Math.max(0, num(m.get("levels"), 0));
        Object items = m.get("items");
        if (items instanceof Map) for (Map.Entry<Object, Object> e : ((Map<Object, Object>) items).entrySet())
            s.items.put(String.valueOf(e.getKey()), num(e.getValue(), 1));
        Object done = m.get("done");
        if (done instanceof List) for (Object o : (List<Object>) done) s.done.add(color(String.valueOf(o)));
        return s;
    }

    private static void readItems(ConfigurationSection sec, Map<String, Integer> out) {
        if (sec == null) return;
        for (String k : sec.getKeys(false)) out.put(k, Math.max(1, sec.getInt(k, 1)));
    }

    private static String str(Object o, String def) { return o == null ? def : String.valueOf(o); }
    private static int num(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try { return o == null ? def : Integer.parseInt(String.valueOf(o).trim()); } catch (NumberFormatException e) { return def; }
    }
    private static String color(String s) { return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s); }

    // ---------------- state helpers ----------------

    private Chapter chapter(PlayerData d) { return chapters.get(d.getQuestChapter()); }

    private Step step(PlayerData d) {
        Chapter c = chapter(d);
        if (c == null || d.getQuestStep() < 0 || d.getQuestStep() >= c.steps.size()) return null;
        return c.steps.get(d.getQuestStep());
    }

    /** Short objective for sidebar/PAPI. */
    public String objective(Player p) {
        if (!enabled || p == null) return "";
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d.isQuestDone()) return "第一卷已完成";
        Step s = step(d);
        if (s == null) return "找 灰烛 开始主线";
        return ChatColor.stripColor(s.desc) + progressSuffix(p, d, s);
    }

    private String progressSuffix(Player p, PlayerData d, Step s) {
        if ("kill".equals(s.type) || ("event".equals(s.type) && s.count > 1)) return " " + Math.min(d.getQuestCount(), s.count) + "/" + s.count;
        if ("level".equals(s.type)) return " (Lv." + d.getEmberLevel() + "/" + s.level + ")";
        return "";
    }

    public String chapterLabel(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        Chapter c = chapter(d);
        if (d.isQuestDone()) return "旧誓余烬 · 完";
        return c == null ? "-" : ("第" + c.no + "章 " + c.name);
    }

    // ---------------- flow ----------------

    public void onJoin(final Player p) {
        if (plugin.getTalentServicePublic() != null) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run() { if (p.isOnline()) plugin.getTalentServicePublic().migrateOnJoin(p); }
            }, 60L);
        }
        if (!enabled) return;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) return;
                PlayerData d = dataStore.get(p.getUniqueId());
                if (d.getQuestChapter() <= 0 && !d.isQuestDone()) {
                    if (autoStart && !chapters.isEmpty()) startChapter(p, chapters.firstKey());
                    return;
                }
                if (d.isQuestDone()) return;
                Step s = step(d);
                if (s != null) {
                    p.sendMessage(title + ChatColor.YELLOW + " " + chapterLabel(p) + ChatColor.GRAY + " · 当前：" + ChatColor.WHITE + objective(p));
                    checkPassive(p);
                }
            }
        }, 60L);
    }

    private void startChapter(Player p, int no) {
        Chapter c = chapters.get(no);
        if (c == null) return;
        PlayerData d = dataStore.get(p.getUniqueId());
        d.setQuestChapter(no);
        d.setQuestStep(0);
        d.setQuestCount(0);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage("");
        p.sendMessage(title + ChatColor.GOLD + " 第" + no + "章 · " + c.name);
        for (String l : c.intro) p.sendMessage(l);
        giveItems(p, c.startItems);
        announceStep(p, d);
        checkPassive(p);
    }

    private void announceStep(Player p, PlayerData d) {
        Step s = step(d);
        if (s == null) return;
        p.sendMessage(title + ChatColor.WHITE + " 目标：" + ChatColor.YELLOW + ChatColor.stripColor(s.desc) + progressSuffix(p, d, s)
                + (s.hint.isEmpty() ? "" : ChatColor.GRAY + "（" + ChatColor.stripColor(s.hint) + "）"));
        actionBar(p, ChatColor.GOLD + "主线 " + ChatColor.WHITE + objective(p));
    }

    private void completeStep(Player p, PlayerData d, Step s) {
        for (String l : s.done) p.sendMessage(l);
        ProgressService ps = plugin.getProgressService();
        int ch = d.getQuestChapter();
        Chapter c = chapter(d);
        int next = d.getQuestStep() + 1;
        // advance first so level-ups triggered by the reward XP evaluate the NEXT step
        d.setQuestStep(next);
        d.setQuestCount(0);
        dataStore.flushMutation(p.getUniqueId());
        giveItems(p, s.items);
        if (s.xp > 0 && ps != null) ps.grantFlatEmberXp(p, s.xp, title);
        if (s.levels > 0) { p.giveExpLevels(s.levels); p.sendMessage(title + ChatColor.GREEN + " 原版经验 +" + s.levels + " 级（附魔/修理用）"); }
        if (c != null && next >= c.steps.size()) {
            p.sendMessage(title + ChatColor.GREEN + " 第" + ch + "章「" + c.name + "」完成！");
            Integer nk = chapters.higherKey(ch);
            if (nk == null) {
                d.setQuestDone(true);
                dataStore.flushMutation(p.getUniqueId());
                p.sendMessage(title + ChatColor.GOLD + " 主线第一卷「旧誓余烬」完成。");
                return;
            }
            startChapter(p, nk);
            return;
        }
        announceStep(p, d);
        checkPassive(p);
    }

    /** Steps that can already be satisfied (level). */
    private void checkPassive(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        Step s = step(d);
        if (s != null && "level".equals(s.type) && d.getEmberLevel() >= s.level) completeStep(p, d, s);
    }

    public void onLevelChanged(final Player p) {
        if (!enabled || p == null) return;
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() { if (p.isOnline()) checkPassive(p); }
        });
    }

    /** Generic event hook (sign, bounty, *_clear, enchant, anvil, craft, calamity_join, calamity_boss). */
    public void onEvent(Player p, String event) {
        if (!enabled || p == null || event == null) return;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d.isQuestDone()) return;
        Step s = step(d);
        if (s == null) return;
        if ("kill".equals(s.type) && !s.completeOn.isEmpty() && s.completeOn.equalsIgnoreCase(event)) {
            // the clear this kill step lives in happened → finish it, then let the same clear count for the next step
            completeStep(p, d, s);
            onEvent(p, event);
            return;
        }
        if (!"event".equals(s.type) || !s.event.equalsIgnoreCase(event)) return;
        bump(p, d, s);
    }

    private void bump(Player p, PlayerData d, Step s) {
        d.setQuestCount(d.getQuestCount() + 1);
        dataStore.flushMutation(p.getUniqueId());
        if (d.getQuestCount() >= s.count) completeStep(p, d, s);
        else actionBar(p, ChatColor.GOLD + "主线 " + ChatColor.WHITE + objective(p));
    }

    public void onKill(Player killer, Entity victim) {
        if (!enabled || killer == null || victim == null) return;
        PlayerData d = dataStore.get(killer.getUniqueId());
        if (d.isQuestDone()) return;
        Step s = step(d);
        if (s == null || !"kill".equals(s.type)) return;
        String id = mmSeen.remove(victim.getUniqueId());
        if (id == null) id = mythicId(victim);
        if (id == null) return;
        for (String m : s.mobs) {
            boolean hit = m.endsWith("*") ? id.startsWith(m.substring(0, m.length() - 1)) : id.equals(m);
            if (hit) { bump(killer, d, s); return; }
        }
    }

    /** NPC talk (Adyeshach interact or /corerpg quest talk near the NPC). */
    public void talk(Player p) {
        if (!enabled || p == null) return;
        long now = System.currentTimeMillis();
        Long last = talkCooldown.get(p.getUniqueId());
        if (last != null && now - last < 1000) return;
        talkCooldown.put(p.getUniqueId(), now);
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d.getQuestChapter() <= 0 && !d.isQuestDone() && !chapters.isEmpty()) { startChapter(p, chapters.firstKey()); return; }
        reissueStarter(p, d);
        Step s = step(d);
        if (s != null && "talk".equals(s.type)) { completeStep(p, d, s); return; }
        if (d.isQuestDone()) { p.sendMessage(npcName + ChatColor.WHITE + "：火还亮着。去吧，同袍们在等你。"); return; }
        p.sendMessage(npcName + ChatColor.WHITE + "：" + (s == null ? "……" : "先去完成：" + ChatColor.YELLOW + objective(p)));
        if (s != null && !s.hint.isEmpty()) p.sendMessage(ChatColor.GRAY + "  " + ChatColor.stripColor(s.hint));
    }

    private boolean nearNpc(Player p) {
        World w = Bukkit.getWorld(npcWorld);
        if (w == null || !p.getWorld().equals(w)) return false;
        return p.getLocation().distance(new Location(w, npcX, npcY, npcZ)) <= talkRadius;
    }

    // ---------------- commands ----------------

    /** /corerpg quest [talk|reset <player>|set <player> <chapter> <step>|event <player> <event>] */
    public boolean cmd(CommandSender sender, String[] args) {
        String act = args.length > 1 ? args[1].toLowerCase() : "";
        if ("talk".equals(act)) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            if (!nearNpc(p) && !p.isOp()) { p.sendMessage(title + ChatColor.RED + " 需要站在 " + npcName + ChatColor.RED + " 身边（枢纽出生点北边（工坊旁））"); return true; }
            talk(p);
            return true;
        }
        if ("set".equals(act) || "reset".equals(act) || "event".equals(act) || "npc".equals(act)) {
            if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            if ("npc".equals(act)) { ensureNpc(true); sender.sendMessage("[CoreRpg] quest npc ensured (hooked=" + adyHooked + ")"); return true; }
            Player t = args.length > 2 ? Bukkit.getPlayerExact(args[2]) : null;
            if (t == null) { sender.sendMessage(ChatColor.RED + "玩家不在线"); return true; }
            PlayerData d = dataStore.get(t.getUniqueId());
            if ("reset".equals(act)) {
                d.setQuestChapter(0); d.setQuestStep(0); d.setQuestCount(0); d.setQuestDone(false);
                dataStore.flushMutation(t.getUniqueId());
                sender.sendMessage("[CoreRpg] quest reset " + t.getName());
                return true;
            }
            if ("event".equals(act)) {
                if (args.length < 4) { sender.sendMessage("/corerpg quest event <player> <event>"); return true; }
                onEvent(t, args[3]);
                sender.sendMessage("[CoreRpg] quest event " + t.getName() + " " + args[3] + " → " + objective(t));
                return true;
            }
            if (args.length < 5) { sender.sendMessage("/corerpg quest set <player> <chapter> <step>"); return true; }
            int ch = num(args[3], 1), st = num(args[4], 0);
            d.setQuestDone(false); d.setQuestChapter(ch); d.setQuestStep(st); d.setQuestCount(0);
            dataStore.flushMutation(t.getUniqueId());
            sender.sendMessage("[CoreRpg] quest set " + t.getName() + " → " + chapterLabel(t) + " · " + objective(t));
            announceStep(t, d);
            checkPassive(t);
            return true;
        }
        if (!(sender instanceof Player)) { sender.sendMessage("/corerpg quest set|reset|event|npc"); return true; }
        Player p = (Player) sender;
        PlayerData d = dataStore.get(p.getUniqueId());
        p.sendMessage(title + ChatColor.GOLD + " " + chapterLabel(p));
        if (d.isQuestDone()) { p.sendMessage(ChatColor.GRAY + "  第一卷已完成。"); return true; }
        Chapter c = chapter(d);
        if (c == null) { p.sendMessage(ChatColor.GRAY + "  找枢纽的 " + npcName + ChatColor.GRAY + " 开始主线"); return true; }
        for (int i = 0; i < c.steps.size(); i++) {
            Step s = c.steps.get(i);
            String mark = i < d.getQuestStep() ? ChatColor.GREEN + "✓ " : (i == d.getQuestStep() ? ChatColor.YELLOW + "▶ " : ChatColor.DARK_GRAY + "· ");
            String line = ChatColor.stripColor(s.desc) + (i == d.getQuestStep() ? progressSuffix(p, d, s) : "");
            p.sendMessage("  " + mark + (i == d.getQuestStep() ? ChatColor.WHITE : ChatColor.GRAY) + line);
        }
        Step s = step(d);
        if (s != null && !s.hint.isEmpty()) p.sendMessage(ChatColor.GRAY + "  提示：" + ChatColor.stripColor(s.hint));
        return true;
    }

    // MM may unregister the ActiveMob before our MONITOR death handler runs → remember ids while the mob is alive.
    private final Map<UUID, String> mmSeen = new LinkedHashMap<UUID, String>(64, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<UUID, String> e) { return size() > 2000; }
    };

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobHit(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if (!enabled || e.getEntity() instanceof Player) return;
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof org.bukkit.entity.Projectile)) return;
        if (mmSeen.containsKey(e.getEntity().getUniqueId())) return;
        String id = mythicId(e.getEntity());
        if (id != null) mmSeen.put(e.getEntity().getUniqueId(), id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobDeathEarly(org.bukkit.event.entity.EntityDeathEvent e) {
        if (!enabled || e.getEntity() instanceof Player) return;
        String id = mythicId(e.getEntity());
        if (id != null) mmSeen.put(e.getEntity().getUniqueId(), id);
    }

    // ---------------- vanilla station hooks ----------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e) { onEvent(e.getEnchanter(), "enchant"); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (e.getWhoClicked() instanceof Player) onEvent((Player) e.getWhoClicked(), "craft");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvilTake(InventoryClickEvent e) {
        if (e.getInventory() == null || e.getInventory().getType() != InventoryType.ANVIL) return;
        if (e.getRawSlot() != 2 || !(e.getWhoClicked() instanceof Player)) return;
        ItemStack cur = e.getCurrentItem();
        if (cur == null || cur.getType() == org.bukkit.Material.AIR) return;
        onEvent((Player) e.getWhoClicked(), "anvil");
    }

    // ---------------- MythicMobs (reflection) ----------------

    private String mythicId(Entity e) {
        try {
            if (!mmTried) {
                mmTried = true;
                Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
                if (mm != null) {
                    Class<?> c = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs", true, mm.getClass().getClassLoader());
                    Object inst = c.getMethod("inst").invoke(null);
                    mmApi = inst.getClass().getMethod("getAPIHelper").invoke(inst);
                    mmIsMythic = mmApi.getClass().getMethod("isMythicMob", Entity.class);
                    mmGetInstance = mmApi.getClass().getMethod("getMythicMobInstance", Entity.class);
                }
            }
            if (mmApi == null) return null;
            if (!(Boolean) mmIsMythic.invoke(mmApi, e)) return null;
            Object am = mmGetInstance.invoke(mmApi, e);
            if (am == null) return null;
            if (mmGetType == null) mmGetType = am.getClass().getMethod("getType");
            Object type = mmGetType.invoke(am);
            if (mmInternalName == null) mmInternalName = type.getClass().getMethod("getInternalName");
            return String.valueOf(mmInternalName.invoke(type));
        } catch (Throwable t) {
            if (!mmWarned) { mmWarned = true; plugin.getLogger().log(Level.WARNING, "Quest: MythicMobs lookup failed", t); }
            return null;
        }
    }

    // ---------------- Adyeshach NPC (reflection, soft) ----------------

    public void hookAdyeshach() {
        if (!enabled || !npcEnabled || adyHooked) return;
        final Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        if (ady == null || !ady.isEnabled()) return;
        try {
            final ClassLoader cl = ady.getClass().getClassLoader();
            @SuppressWarnings("unchecked")
            final Class<? extends Event> evt = (Class<? extends Event>) Class.forName("ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent", true, cl);
            final Method getEntity = evt.getMethod("getEntity");
            final Method getPlayer = evt.getMethod("getPlayer");
            final Method isMain = evt.getMethod("isMainHand");
            Bukkit.getPluginManager().registerEvent(evt, this, EventPriority.NORMAL, new EventExecutor() {
                @Override public void execute(Listener l, Event event) {
                    if (!evt.isInstance(event)) return;
                    try {
                        if (!(Boolean) isMain.invoke(event)) return;
                        Object ent = getEntity.invoke(event);
                        String id = String.valueOf(ent.getClass().getMethod("getId").invoke(ent));
                        if (!npcId.equals(id)) return;
                        final Player p = (Player) getPlayer.invoke(event);
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override public void run() { if (p.isOnline()) talk(p); }
                        });
                    } catch (Throwable t) {
                        plugin.getLogger().log(Level.WARNING, "Adyeshach interact hook", t);
                    }
                }
            }, plugin);
            adyHooked = true;
            plugin.getLogger().info("Quest: Adyeshach interact hook registered (npc id " + npcId + ")");
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Quest: Adyeshach hook failed", t);
        }
    }

    /** Create the hub quest-giver in Adyeshach's persistent manager if it is missing. */
    public void ensureNpc(boolean force) {
        if (!enabled || !npcEnabled) return;
        Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        World w = Bukkit.getWorld(npcWorld);
        if (ady == null || !ady.isEnabled() || w == null) return;
        try {
            ClassLoader cl = ady.getClass().getClassLoader();
            Class<?> adyC = Class.forName("ink.ptms.adyeshach.core.Adyeshach", true, cl);
            Object api = adyC.getMethod("api").invoke(adyC.getField("INSTANCE").get(null));
            Class<?> mtC = Class.forName("ink.ptms.adyeshach.core.entity.manager.ManagerType", true, cl);
            Object persistent = mtC.getField("PERSISTENT").get(null);
            Class<?> apiC = Class.forName("ink.ptms.adyeshach.core.AdyeshachAPI", true, cl);
            Object mgr = apiC.getMethod("getPublicEntityManager", mtC).invoke(api, persistent);
            Class<?> mgrC = Class.forName("ink.ptms.adyeshach.core.entity.manager.Manager", true, cl);
            List<?> found = (List<?>) mgrC.getMethod("getEntityById", String.class).invoke(mgr, npcId);
            Class<?> eiC = Class.forName("ink.ptms.adyeshach.core.entity.EntityInstance", true, cl);
            Class<?> geC = Class.forName("ink.ptms.adyeshach.core.entity.GenericEntity", true, cl);
            Location loc = new Location(w, npcX, npcY, npcZ, npcYaw, 0f);
            Object ent;
            if (found != null && !found.isEmpty()) {
                ent = found.get(0);
                if (force) eiC.getMethod("teleport", Location.class).invoke(ent, loc);
            } else {
                Class<?> etC = Class.forName("ink.ptms.adyeshach.core.entity.EntityTypes", true, cl);
                Object villager = etC.getField("VILLAGER").get(null);
                ent = mgrC.getMethod("create", etC, Location.class).invoke(mgr, villager, loc);
                eiC.getMethod("setId", String.class).invoke(ent, npcId);
                plugin.getLogger().info("Quest: created Adyeshach NPC " + npcId + " at " + npcWorld + " " + npcX + "," + npcY + "," + npcZ);
            }
            geC.getMethod("setCustomName", String.class).invoke(ent, npcName);
            geC.getMethod("setCustomNameVisible", boolean.class).invoke(ent, true);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Quest: ensure Adyeshach NPC failed", t);
        }
    }

    /** 1.8.1: lost the starter blade (death before keepInventory, dropped…) → 灰烛 hands a new T0 blade, once per day. */
    private void reissueStarter(Player p, PlayerData d) {
        if (ni == null || d.getQuestChapter() <= 0) return;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !it.hasItemMeta() || !it.getItemMeta().hasDisplayName()) continue;
            String n = ChatColor.stripColor(it.getItemMeta().getDisplayName());
            if (n.contains("余烬") && n.contains("刃")) return;
        }
        String today = DailyService.today();
        if (today.equals(d.getStarterReissueDate())) return;
        d.setStarterReissueDate(today);
        dataStore.flushMutation(p.getUniqueId());
        ni.giveNiItem(p, "gear_ember_blade", 1);
        p.sendMessage(npcName + ChatColor.WHITE + "：刃丢了？拿这把旧的去，别再弄丢了。" + ChatColor.GRAY + "（每日可补领一次）");
    }

    // ---------------- 1.8.1 newbie safety ----------------

    private final java.util.Set<String> staticWorlds = new java.util.HashSet<String>(java.util.Arrays.asList(
            "world", "world_nether", "world_the_end", "ember_hub", "ember_afk", "ember_event"));
    private String rogueName = "余烬地窟骷髅";
    private final Map<UUID, Object[]> lastHit = new LinkedHashMap<UUID, Object[]>(64, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<UUID, Object[]> e) { return size() > 4000; }
    };

    public boolean isInstanceWorld(World w) { return w != null && !staticWorlds.contains(w.getName()); }

    /** Last player who damaged this entity within 15 s (kills credited to Unknown otherwise). */
    public Player lastPlayerDamager(Entity e) { return lastPlayerDamager(e.getUniqueId()); }

    public Player lastPlayerDamager(UUID id) {
        Object[] v = lastHit.get(id);
        if (v == null || System.currentTimeMillis() - (Long) v[1] > 15000) return null;
        Player p = Bukkit.getPlayer((UUID) v[0]);
        return p != null && p.isOnline() ? p : null;
    }

    /** Any player who ever damaged this entity (last one wins), no time limit; must be online and in world w (null = any). */
    public Player anyPlayerDamager(UUID id, World w) {
        Object[] v = lastHit.get(id);
        if (v == null) return null;
        Player p = Bukkit.getPlayer((UUID) v[0]);
        if (p == null || !p.isOnline()) return null;
        if (w != null && !w.equals(p.getWorld())) return null;
        return p;
    }

    private static Player playerSource(Entity damager) {
        if (damager instanceof Player) return (Player) damager;
        if (damager instanceof org.bukkit.entity.Projectile) {
            Object sh = ((org.bukkit.entity.Projectile) damager).getShooter();
            if (sh instanceof Player) return (Player) sh;
        }
        return null;
    }

    /** Mobs must not kill MythicMobs mobs (steals the last hit → drops/XP go to "Unknown"); remember player hits. */
    /** Record every player hit attempt (LOWEST, even if a damage plugin later cancels/replaces the vanilla event). */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHit(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player || !(e.getEntity() instanceof org.bukkit.entity.LivingEntity)) return;
        Player src = playerSource(e.getDamager());
        if (src != null) lastHit.put(e.getEntity().getUniqueId(), new Object[]{src.getUniqueId(), System.currentTimeMillis()});
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInfight(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player || !(e.getEntity() instanceof org.bukkit.entity.LivingEntity)) return;
        Player src = playerSource(e.getDamager());
        if (src != null) return;
        String id = mmSeen.get(e.getEntity().getUniqueId());
        if (id == null) id = mythicId(e.getEntity());
        if (id != null) { mmSeen.put(e.getEntity().getUniqueId(), id); e.setCancelled(true); }
    }

    /** The DP map templates carry a baked-in 余烬地窟骷髅 1.7 blocks from the entry point → remove it on chunk load. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent e) {
        if (!isInstanceWorld(e.getWorld())) return;
        for (Entity en : e.getChunk().getEntities()) removeIfRogue(en);
    }

    private boolean removeIfRogue(Entity en) {
        if (en instanceof Player || en.getCustomName() == null) return false;
        if (!ChatColor.stripColor(en.getCustomName()).contains(rogueName)) return false;
        if (mythicId(en) != null) return false; // a live MM mob (not the baked template entity)
        en.remove();
        plugin.getLogger().info("Removed baked template mob '" + rogueName + "' in " + en.getWorld().getName());
        return true;
    }

    /** Entering a dungeon instance: full heal + 5 s resistance (players arrived with 2 HP from the last run). */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(org.bukkit.event.player.PlayerChangedWorldEvent e) {
        final Player p = e.getPlayer();
        if (!isInstanceWorld(p.getWorld())) return;
        for (Entity en : p.getWorld().getEntities()) removeIfRogue(en);
        try { p.setHealth(p.getMaxHealth()); } catch (Throwable ignored) { }
        p.setFoodLevel(20);
        p.setFireTicks(0);
        p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 100, 4, true, false), true);
    }

    // ---------------- util ----------------

    private void giveItems(Player p, Map<String, Integer> items) {
        if (items == null || items.isEmpty()) return;
        List<String> got = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            if (ni != null && ni.giveNiItem(p, e.getKey(), e.getValue())) got.add(ni.displayName(e.getKey()) + "×" + e.getValue());
        }
        if (!got.isEmpty()) p.sendMessage(title + ChatColor.AQUA + " 获得：" + ChatColor.WHITE + String.join("，", got));
    }

    private static void actionBar(Player p, String msg) {
        try { p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg)); } catch (Throwable ignored) { }
    }

    public boolean isEnabled() { return enabled; }
    public List<Integer> chapterNumbers() { return Collections.unmodifiableList(new ArrayList<Integer>(chapters.keySet())); }
}
