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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
 * Stage 4.5: abyss_floor / elite_weekly_clear / forge state-type events; volume 2 chapters 7–10.
 */
public final class QuestService implements Listener {

    static final class Step {
        String type = "talk";
        String event = "";
        String completeOn = "";
        List<String> mobs = new ArrayList<String>();
        int count = 1;
        int level = 0;
        /** abyss_floor target (historical best ≥ floor). Shared on multi-select steps. */
        int floor = 0;
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
    /** Bukkit hitbox metadata key; value = npc id (ember_guide). */
    static final String META_QUEST_NPC = "corerpg_quest_npc";
    private static final String[] ADY_INTERACT_EVENTS = {
            "ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent",
            "ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent"
    };
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
        s.floor = num(m.get("floor"), 0);
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
        if (d.isQuestDone()) return d.getQuestChapter() >= 7 ? "第二卷已完成" : "第一卷已完成";
        Step s = step(d);
        if (s == null) return "找 灰烛 开始主线";
        return ChatColor.stripColor(s.desc) + progressSuffix(p, d, s);
    }

    private String progressSuffix(Player p, PlayerData d, Step s) {
        if ("kill".equals(s.type) || ("event".equals(s.type) && s.count > 1)) return " " + Math.min(d.getQuestCount(), s.count) + "/" + s.count;
        if ("level".equals(s.type)) return " (Lv." + d.getEmberLevel() + "/" + s.level + ")";
        if ("event".equals(s.type) && matches(s.event, "enhance") && p != null) return " (+" + maxEnhance(p) + "/+" + s.level + ")";
        return "";
    }

    public String chapterLabel(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        Chapter c = chapter(d);
        if (d.isQuestDone()) {
            return d.getQuestChapter() >= 7 ? "烬火未灭 · 完" : "旧誓余烬 · 完";
        }
        if (c == null) return "-";
        if (c.no >= 7) return "第二卷 · " + c.name;
        return "第" + c.no + "章 " + c.name;
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
                if (d.isQuestDone()) {
                    // Stage 4.5: volume 2 chapters added after vol1 was marked done → reopen next chapter
                    Integer nk = chapters.higherKey(d.getQuestChapter());
                    if (nk != null) {
                        d.setQuestDone(false);
                        dataStore.flushMutation(p.getUniqueId());
                        startChapter(p, nk);
                    }
                    return;
                }
                // 1.11.0 migration: ch2 was reordered and the T1 blade moved from the Lv20 step to the daily-clear step
                if (d.getQuestChapter() == 2 && d.getQuestStep() >= 2 && d.periodCount("mig_quest_1110", "all") == 0) {
                    d.addPeriodCount("mig_quest_1110", "all", 1);
                    dataStore.flushMutation(p.getUniqueId());
                    p.sendMessage(title + ChatColor.GRAY + " 第2章流程已调整（立誓 / 天赋 / 附魔 / 强化），补发精炼刃：");
                    giveItems(p, java.util.Collections.singletonMap("gear_ember_t1_blade", 1));
                }
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
        if (no == 2 && d.periodCount("mig_quest_1110", "all") == 0) d.addPeriodCount("mig_quest_1110", "all", 1); // new ch2 flow from the start
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
                if (ch >= 7) {
                    p.sendMessage(title + ChatColor.GOLD + " 主线第二卷「烬火未灭」完成。");
                } else {
                    p.sendMessage(title + ChatColor.GOLD + " 主线第一卷「旧誓余烬」完成。");
                }
                return;
            }
            startChapter(p, nk);
            return;
        }
        announceStep(p, d);
        checkPassive(p);
    }

    /** Steps that can already be satisfied (level; 1.11.0: state-type events done earlier). */
    public void checkPassive(Player p) {
        if (!enabled || p == null || !p.isOnline()) return;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d.isQuestDone()) return;
        Step s = step(d);
        if (s == null) return;
        if ("level".equals(s.type) && d.getEmberLevel() >= s.level) { completeStep(p, d, s); return; }
        if ("event".equals(s.type) && already(p, d, s)) {
            p.sendMessage(title + ChatColor.GREEN + " 已完成过：" + ChatColor.stripColor(s.desc));
            completeStep(p, d, s);
        }
    }

    /** 1.11.0: state checks so a step whose action was done before the step started still counts. */
    private boolean already(Player p, PlayerData d, Step s) {
        for (String ev : s.event.split("\\|")) {
            ev = ev.trim();
            if ("sign".equals(ev) && DailyService.today().equals(d.getLastSignDate())) return true;
            if ("bounty".equals(ev) && d.isBountyClaimed()) return true;
            if ("covenant".equals(ev) && d.hasCovenant()) return true;
            if ("talent".equals(ev) && d.getTalentPointsSpent() >= Math.max(1, s.count)) return true;
            if ("enhance".equals(ev) && maxEnhance(p) >= Math.max(1, s.level)) return true;
            if ("enchant".equals(ev) && (d.periodCount("ever_enchant", "all") > 0 || hasEnchantedGear(p))) return true;
            if ("anvil".equals(ev) && d.periodCount("ever_anvil", "all") > 0) return true;
            // Stage 4.5 state-type events
            if ("forge".equals(ev) && d.periodCount("ever_forge", "all") > 0) return true;
            if ("abyss_floor".equals(ev) && d.getAbyssBest() >= Math.max(1, s.floor)) return true;
            if ("elite_weekly_clear".equals(ev) && hasEliteWeeklyClear(d)) return true;
        }
        return false;
    }

    private static boolean hasEliteWeeklyClear(PlayerData d) {
        String week = DailyService.weekId();
        if (week == null || week.isEmpty()) return false;
        return d.getLootWeekMarks().contains(EliteService.CLEAR_MARK + "=" + week);
    }

    private int maxEnhance(Player p) {
        int best = 0;
        for (ItemStack it : allItems(p)) {
            if (it == null || ni == null) continue;
            String id = ni.getNiId(it);
            if (id != null && id.startsWith("gear_ember")) best = Math.max(best, GearLore.readEnhance(it));
        }
        return best;
    }

    private boolean hasEnchantedGear(Player p) {
        for (ItemStack it : allItems(p)) {
            if (it == null || ni == null) continue;
            String id = ni.getNiId(it);
            if (id == null || !id.startsWith("gear_ember") || id.contains("t3")) continue;
            for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> e : it.getEnchantments().entrySet()) {
                if (!e.getKey().equals(org.bukkit.enchantments.Enchantment.DURABILITY) || e.getValue() > 1) return true;
            }
        }
        return false;
    }

    private static List<ItemStack> allItems(Player p) {
        List<ItemStack> out = new ArrayList<ItemStack>();
        Collections.addAll(out, p.getInventory().getContents());
        Collections.addAll(out, p.getInventory().getArmorContents());
        return out;
    }

    private static boolean matches(String stepEvent, String event) {
        for (String ev : stepEvent.split("\\|")) if (ev.trim().equalsIgnoreCase(event)) return true;
        return false;
    }

    /** Every 10 s: re-evaluate passive steps for online players (covers actions from other plugins/menus). */
    public void tickAll() {
        if (!enabled) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            try { checkPassive(p); } catch (Throwable t) { plugin.getLogger().log(Level.WARNING, "quest tick", t); }
        }
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
        if (!"event".equals(s.type) || !matches(s.event, event)) return;
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
                if (args.length < 4) { sender.sendMessage("/corerpg quest event <player> <event> [extra]"); return true; }
                String evName = args[3].toLowerCase();
                // Stage 4.5: quest event <p> abyss_floor <N> — raise historical best then refresh
                if ("abyss_floor".equals(evName) && args.length >= 5) {
                    int fl = num(args[4], 0);
                    if (fl > 0) {
                        d.recordAbyssFloor(fl);
                        dataStore.flushMutation(t.getUniqueId());
                    }
                    checkPassive(t);
                    onEvent(t, "abyss_floor");
                    sender.sendMessage("[CoreRpg] quest event " + t.getName() + " abyss_floor " + fl
                            + " (best=" + d.getAbyssBest() + ") → " + objective(t));
                    return true;
                }
                onEvent(t, evName);
                // state-type may already be satisfied without a bump (forge mark / elite mark)
                checkPassive(t);
                sender.sendMessage("[CoreRpg] quest event " + t.getName() + " " + evName + " → " + objective(t));
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
        if (d.isQuestDone()) {
            p.sendMessage(ChatColor.GRAY + "  " + (d.getQuestChapter() >= 7 ? "第二卷已完成。" : "第一卷已完成。"));
            return true;
        }
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
    public void onEnchant(EnchantItemEvent e) {
        dataStore.get(e.getEnchanter().getUniqueId()).addPeriodCount("ever_enchant", "all", 1);
        onEvent(e.getEnchanter(), "enchant");
    }

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
        dataStore.get(e.getWhoClicked().getUniqueId()).addPeriodCount("ever_anvil", "all", 1);
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

    // ---------------- Adyeshach NPC (reflection, soft) + Bukkit hitbox ----------------

    public void hookAdyeshach() {
        if (!enabled || !npcEnabled || adyHooked) return;
        final Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        if (ady == null || !ady.isEnabled()) return;
        final ClassLoader cl = ady.getClass().getClassLoader();
        int registered = 0;
        for (String className : ADY_INTERACT_EVENTS) {
            try {
                @SuppressWarnings("unchecked")
                final Class<? extends Event> evt = (Class<? extends Event>) Class.forName(className, true, cl);
                final Method getEntity = evt.getMethod("getEntity");
                final Method getPlayer = evt.getMethod("getPlayer");
                // isMainHand intentionally ignored — off-hand / protocol quirks must still talk
                Bukkit.getPluginManager().registerEvent(evt, this, EventPriority.NORMAL, new EventExecutor() {
                    @Override public void execute(Listener l, Event event) {
                        if (!evt.isInstance(event)) return;
                        try {
                            Object ent = getEntity.invoke(event);
                            if (!matchesAdyNpc(ent)) return;
                            final Player p = (Player) getPlayer.invoke(event);
                            if (p == null) return;
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override public void run() { if (p.isOnline()) talk(p); }
                            });
                        } catch (Throwable t) {
                            plugin.getLogger().log(Level.WARNING, "Adyeshach interact hook (" + className + ")", t);
                        }
                    }
                }, plugin);
                registered++;
                plugin.getLogger().info("Quest: Adyeshach interact hook registered: " + className + " (npc id " + npcId + ")");
            } catch (ClassNotFoundException cnf) {
                plugin.getLogger().info("Quest: Adyeshach event class absent (ok): " + className);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Quest: Adyeshach hook failed for " + className, t);
            }
        }
        if (registered > 0) {
            adyHooked = true;
            plugin.getLogger().info("Quest: Adyeshach interact hooks active=" + registered + " (npc id " + npcId + ")");
        }
    }

    /** Match Ady NPC by id, custom-name containing 灰烛, or distance ≤2 to configured coords. */
    private boolean matchesAdyNpc(Object ent) {
        if (ent == null) return false;
        try {
            String id = String.valueOf(ent.getClass().getMethod("getId").invoke(ent));
            if (npcId.equals(id)) return true;
        } catch (Throwable ignored) {}
        try {
            Object cn = ent.getClass().getMethod("getCustomName").invoke(ent);
            if (cn != null) {
                String plain = ChatColor.stripColor(String.valueOf(cn));
                if (plain != null && plain.contains("灰烛")) return true;
            }
        } catch (Throwable ignored) {}
        try {
            Object locObj = null;
            for (String m : new String[]{"getLocation", "getWorldPosition", "getExactLocation"}) {
                try {
                    Method gm = ent.getClass().getMethod(m);
                    locObj = gm.invoke(ent);
                    if (locObj instanceof Location) break;
                } catch (NoSuchMethodException ignored) {}
            }
            if (locObj instanceof Location) {
                Location loc = (Location) locObj;
                World w = Bukkit.getWorld(npcWorld);
                if (w != null && w.equals(loc.getWorld())
                        && loc.distanceSquared(new Location(w, npcX, npcY, npcZ)) <= 4.0) {
                    return true;
                }
            }
        } catch (Throwable ignored) {}
        return false;
    }

    /** Create the hub quest-giver in Adyeshach's persistent manager if it is missing; always ensure Bukkit hitbox. */
    public void ensureNpc(boolean force) {
        if (!enabled || !npcEnabled) return;
        Plugin ady = Bukkit.getPluginManager().getPlugin("Adyeshach");
        World w = Bukkit.getWorld(npcWorld);
        if (ady == null || !ady.isEnabled() || w == null) {
            ensureHitboxNpc();
            return;
        }
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
        ensureHitboxNpc();
    }

    /**
     * Real Bukkit hitbox at 灰烛 coords so vanilla / mineflayer use_entity always reaches talk().
     * Invisible Silent NoAI Nitwit villager — avoids trade UI, no path block, no second nametag clash with Ady.
     */
    public void ensureHitboxNpc() {
        if (!enabled || !npcEnabled) return;
        World w = Bukkit.getWorld(npcWorld);
        if (w == null) return;
        Location loc = new Location(w, npcX, npcY, npcZ, npcYaw, 0f);
        // purge stale hitboxes in a small radius
        for (Entity e : w.getEntitiesByClass(LivingEntity.class)) {
            if (!e.hasMetadata(META_QUEST_NPC)) continue;
            if (e.getLocation().distanceSquared(loc) > 16.0) continue;
            e.remove();
        }
        Villager v;
        try {
            v = (Villager) w.spawnEntity(loc, EntityType.VILLAGER);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Quest: spawn hitbox villager failed, trying ArmorStand", t);
            ensureHitboxArmorStand(loc);
            return;
        }
        try {
            v.setAI(false);
            v.setSilent(true);
            v.setInvulnerable(true);
            v.setCollidable(false);
            v.setRemoveWhenFarAway(false);
            v.setCanPickupItems(false);
            v.setCustomNameVisible(false);
            // keep no custom name so Ady nametag is the only visible label
            try { v.setProfession(Villager.Profession.NITWIT); } catch (Throwable ignored) {}
            try { v.setRecipes(Collections.<org.bukkit.inventory.MerchantRecipe>emptyList()); } catch (Throwable ignored) {}
            v.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false), true);
            v.setMetadata(META_QUEST_NPC, new FixedMetadataValue(plugin, npcId));
            plugin.getLogger().info("Quest: Bukkit hitbox villager ensured at " + npcWorld
                    + " " + npcX + "," + npcY + "," + npcZ);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Quest: configure hitbox villager failed", t);
            v.remove();
            ensureHitboxArmorStand(loc);
        }
    }

    private void ensureHitboxArmorStand(Location loc) {
        try {
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setGravity(false);
            as.setBasePlate(false);
            as.setArms(false);
            as.setMarker(false); // keep clickable hitbox
            as.setSmall(false);
            as.setCustomNameVisible(false);
            as.setInvulnerable(true);
            as.setCollidable(false);
            as.setRemoveWhenFarAway(false);
            as.setMetadata(META_QUEST_NPC, new FixedMetadataValue(plugin, npcId));
            plugin.getLogger().info("Quest: Bukkit hitbox ArmorStand ensured at " + loc);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Quest: spawn hitbox ArmorStand failed", t);
        }
    }

    private boolean isQuestHitbox(Entity e) {
        return e != null && e.hasMetadata(META_QUEST_NPC);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onHitboxInteract(PlayerInteractEntityEvent e) {
        if (!enabled || !npcEnabled) return;
        Entity clicked = e.getRightClicked();
        if (!isQuestHitbox(clicked)) return;
        // 1.9+ fires once per hand; accept both, but cancel trade/UI
        try {
            if (e.getHand() != null && e.getHand() != EquipmentSlot.HAND && e.getHand() != EquipmentSlot.OFF_HAND) {
                return;
            }
        } catch (Throwable ignored) {}
        e.setCancelled(true);
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() { if (p.isOnline()) talk(p); }
        });
    }

    /** Near-miss fallback: right-click air/block while looking toward 灰烛 within 3 blocks. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLookTalk(PlayerInteractEvent e) {
        if (!enabled || !npcEnabled) return;
        Action a = e.getAction();
        if (a != Action.RIGHT_CLICK_AIR && a != Action.RIGHT_CLICK_BLOCK) return;
        try {
            if (e.getHand() != null && e.getHand() != EquipmentSlot.HAND && e.getHand() != EquipmentSlot.OFF_HAND) return;
        } catch (Throwable ignored) {}
        Player p = e.getPlayer();
        World w = Bukkit.getWorld(npcWorld);
        if (w == null || !p.getWorld().equals(w)) return;
        Location npcLoc = new Location(w, npcX, npcY + 1.0, npcZ);
        if (p.getLocation().distanceSquared(npcLoc) > 9.0) return; // ≤3 blocks
        Vector toNpc = npcLoc.toVector().subtract(p.getEyeLocation().toVector());
        if (toNpc.lengthSquared() < 1.0e-6) {
            talk(p);
            return;
        }
        toNpc.normalize();
        Vector look = p.getEyeLocation().getDirection().normalize();
        if (look.dot(toNpc) < 0.72) return; // roughly facing NPC
        talk(p);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHitboxDamage(EntityDamageEvent e) {
        if (isQuestHitbox(e.getEntity())) e.setCancelled(true);
    }

    /** 1.8.1: lost the starter blade    /** 1.8.1: lost the starter blade (death before keepInventory, dropped…) → 灰烛 hands a new T0 blade, once per day. */
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
        instanceGrace(p, 100);
    }

    /** 1.11.0: heal now and again after StatService re-applies max HP (the world change clamps HP to the vanilla 20-ish). */
    private void instanceGrace(final Player p, final int resistTicks) {
        final World w = p.getWorld();
        Runnable heal = new Runnable() {
            @Override public void run() {
                if (!p.isOnline() || !p.getWorld().equals(w)) return;
                try { p.setHealth(p.getMaxHealth()); } catch (Throwable ignored) { }
                p.setFoodLevel(20);
                p.setSaturation(10f);
                p.setFireTicks(0);
            }
        };
        heal.run();
        p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, resistTicks, 4, true, false), true);
        Bukkit.getScheduler().runTaskLater(plugin, heal, 5L);
        Bukkit.getScheduler().runTaskLater(plugin, heal, 25L);
        Bukkit.getScheduler().runTaskLater(plugin, heal, 45L);
    }

    /** 1.11.0: reconnecting straight into a dungeon instance → grace (heal + resistance) and a hint. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinInstance(org.bukkit.event.player.PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline() || !isInstanceWorld(p.getWorld())) return;
                instanceGrace(p, 140);
                p.sendMessage(ChatColor.GOLD + "[余烬] 你回到了副本中：已回满生命并获得 7 秒保护。" + ChatColor.GRAY + "想离开请 /dp leave");
            }
        }, 10L);
    }

    private static final java.util.Set<String> HUB_CMDS = new java.util.HashSet<String>(java.util.Arrays.asList(
            "hub", "spawn", "lobby", "回城", "ember", "menu", "home", "warp", "back", "tpa", "mvtp", "mv"));
    private final Map<UUID, Long> blockedHint = new HashMap<UUID, Long>();

    /** 1.11.0: DungeonPlus silently cancels non-whitelisted commands in instances → explain.
     * 2026-09-27: in EmberAbyss, /ember|/menu opens ember_abyss (evacuate path) instead of hub block. */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDungeonCmdEarly(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        if (!isInstanceWorld(e.getPlayer().getWorld())) return;
        String root = e.getMessage().replaceFirst("^/", "").trim().split("\\s+")[0].toLowerCase();
        if (root.contains(":")) root = root.substring(root.indexOf(':') + 1);
        if (!HUB_CMDS.contains(root)) return;

        final Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        boolean inAbyss = worldName != null && worldName.toLowerCase().contains("abyss");

        // Abyss only: /ember or /menu → open deep-abyss menu (evacuate), not hub
        if (inAbyss && ("ember".equals(root) || "menu".equals(root))) {
            e.setCancelled(true);
            blockedHint.put(p.getUniqueId(), System.currentTimeMillis()); // suppress MONITOR duplicate
            final String name = p.getName();
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override public void run() {
                    if (!p.isOnline()) return;
                    // Console bypasses DP player command whitelist
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open ember_abyss " + name);
                }
            });
            p.sendMessage(ChatColor.DARK_GRAY + "[深渊] 本内菜单 · 点「上浮撤离」");
            return;
        }

        e.setCancelled(true);
        e.getPlayer().sendMessage(ChatColor.RED + "[余烬] 副本中不能回城或开菜单，离开请用 " + ChatColor.YELLOW + "/dp leave"
                + ChatColor.GRAY + "（可用：/corerpg skill · quest · stats）");
        blockedHint.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDungeonCmdBlocked(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled() || !isInstanceWorld(e.getPlayer().getWorld())) return;
        Long last = blockedHint.get(e.getPlayer().getUniqueId());
        if (last != null && System.currentTimeMillis() - last < 300) return;
        blockedHint.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        String w = e.getPlayer().getWorld().getName();
        boolean inAbyss = w != null && w.toLowerCase().contains("abyss");
        String avail = inAbyss
                ? "可用：菜单撤离（本内 /ember 开深渊菜单）· /dp leave · skill/quest/stats"
                : "可用：/dp leave · /corerpg skill · quest · stats";
        e.getPlayer().sendMessage(ChatColor.RED + "[余烬] 副本中该命令不可用。" + ChatColor.GRAY + avail);
    }

    // ---------------- util ----------------

    private void giveItems(Player p, Map<String, Integer> items) {
        if (items == null || items.isEmpty()) return;
        if (items.containsKey("gear_ember_t1_blade")) dataStore.get(p.getUniqueId()).addPeriodCount("mig_quest_1110", "all", 1);
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
