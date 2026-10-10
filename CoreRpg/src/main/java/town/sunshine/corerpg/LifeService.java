package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 1.11.0 (2026-09-27): 生活玩法 — hub supply vendor (food / rod, coin sink), cooking and brewing at the vendor,
 * relic / pearl exchanges with weekly caps (reforge stone / stable charm free sources), life level from fishing,
 * cooking and brewing, and consumable effects for NI food/potions. docs/ember-master-plan.md §8.
 */
public final class LifeService implements Listener {

    static final class Offer {
        String id, name, group = "shop";
        int coin, weekly, daily, lifeLevel, xp;
        Map<String, Integer> inputs = new LinkedHashMap<String, Integer>();
        Map<String, Integer> give = new LinkedHashMap<String, Integer>();
    }

    static final class Consumable {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        int cooldownSec;
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;
    private boolean enabled = true;
    private final Map<String, Offer> offers = new LinkedHashMap<String, Offer>();
    private final Map<String, Consumable> consumables = new HashMap<String, Consumable>();
    private List<String> cookInputs = new ArrayList<String>();
    private String cookOutput = "food_ember_grilled_fish";
    private int cookCoinEach = 1, cookMax = 32, cookXp = 1, fishXp = 2;
    private int[] levelXp = {0, 20, 60, 150, 300, 600, 1000, 1600};
    private final Map<String, Long> cooldowns = new HashMap<String, Long>();
    private int repairFee = 0;
    private boolean debug = false;

    public LifeService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    public void reload() {
        File f = new File(plugin.getDataFolder(), "life.yml");
        if (!f.exists()) plugin.saveResource("life.yml", false);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        enabled = c.getBoolean("enabled", true);
        cookInputs = c.getStringList("cook.inputs");
        cookOutput = c.getString("cook.output", cookOutput);
        cookCoinEach = c.getInt("cook.coin_each", 1);
        cookMax = c.getInt("cook.max_per_use", 32);
        cookXp = c.getInt("cook.xp_each", 1);
        fishXp = c.getInt("fish_xp", 2);
        repairFee = c.getInt("fees.repair", 0);
        debug = c.getBoolean("debug", false);
        List<Integer> lx = c.getIntegerList("level_xp");
        if (lx != null && !lx.isEmpty()) { levelXp = new int[lx.size()]; for (int i = 0; i < lx.size(); i++) levelXp[i] = lx.get(i); }
        offers.clear();
        ConfigurationSection os = c.getConfigurationSection("offers");
        if (os != null) for (String k : os.getKeys(false)) {
            ConfigurationSection o = os.getConfigurationSection(k);
            if (o == null) continue;
            Offer of = new Offer();
            of.id = k;
            of.name = ChatColor.translateAlternateColorCodes('&', o.getString("name", k));
            of.group = o.getString("group", "shop");
            of.coin = o.getInt("coin", 0);
            of.weekly = o.getInt("weekly", 0);
            of.daily = o.getInt("daily", 0);
            of.lifeLevel = o.getInt("life_level", 0);
            of.xp = o.getInt("xp", 0);
            readMap(o.getConfigurationSection("inputs"), of.inputs);
            readMap(o.getConfigurationSection("give"), of.give);
            offers.put(k, of);
        }
        consumables.clear();
        ConfigurationSection cs = c.getConfigurationSection("consumables");
        if (cs != null) for (String k : cs.getKeys(false)) {
            ConfigurationSection o = cs.getConfigurationSection(k);
            if (o == null) continue;
            Consumable cn = new Consumable();
            cn.cooldownSec = o.getInt("cooldown", 0);
            for (String e : o.getStringList("effects")) {
                String[] p = e.split(":");
                PotionEffectType t = PotionEffectType.getByName(p[0].trim().toUpperCase());
                if (t == null) { plugin.getLogger().warning("life.yml: unknown effect " + e); continue; }
                int amp = p.length > 1 ? Integer.parseInt(p[1].trim()) : 0;
                int ticks = p.length > 2 ? Integer.parseInt(p[2].trim()) : 1;
                cn.effects.add(new PotionEffect(t, ticks, amp, false, true));
            }
            consumables.put(k, cn);
        }
        plugin.getLogger().info("Life: " + offers.size() + " offers, " + consumables.size() + " consumables");
    }

    private static void readMap(ConfigurationSection s, Map<String, Integer> out) {
        if (s == null) return;
        for (String k : s.getKeys(false)) out.put(k, Math.max(1, s.getInt(k, 1)));
    }

    // ---------------- life level ----------------

    int lifeXp(PlayerData d) { return d.periodCount("life_xp", "all"); }

    int lifeLevel(PlayerData d) {
        int xp = lifeXp(d), lv = 1;
        for (int i = 1; i < levelXp.length; i++) if (xp >= levelXp[i]) lv = i + 1;
        return lv;
    }

    /** D388: cumulative xp needed for next life level; 0 when already max (show() used -1). */
    int lifeXpNext(PlayerData d) {
        int lv = lifeLevel(d);
        return lv < levelXp.length ? levelXp[lv] : 0;
    }

    private void addXp(Player p, int n) {
        if (n <= 0) return;
        PlayerData d = dataStore.get(p.getUniqueId());
        int before = lifeLevel(d);
        d.addPeriodCount("life_xp", "all", n);
        int after = lifeLevel(d);
        if (after > before) p.sendMessage(ChatColor.GREEN + "[生活] 生活等级提升 → Lv." + after + ChatColor.GRAY + "（解锁更多补给配方，/corerpg life）");
    }

    // ---------------- events ----------------

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent e) {
        if (debug) plugin.getLogger().info("life fish " + e.getPlayer().getName() + " state=" + e.getState() + " cancelled=" + e.isCancelled()
                + " caught=" + (e.getCaught() == null ? "-" : e.getCaught().getType().name())
                + (e.getHook() == null ? "" : " " + hookInfo(e.getHook())));
        if (!enabled || e.isCancelled() || e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        addXp(e.getPlayer(), fishXp);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent e) {
        if (!enabled) return;
        String id = ni.getNiId(e.getItem());
        if (id == null) return;
        Consumable c = consumables.get(id);
        if (c == null) return;
        final Player p = e.getPlayer();
        if (town.sunshine.corerpg.p1.EmberMode.isP1(p) && consumeP1(e, p, id)) return; // ember-v1.0-P1 A05/D02/D03/D04
        if (c.cooldownSec > 0) {
            String key = p.getUniqueId() + ":" + id;
            Long until = cooldowns.get(key);
            long now = System.currentTimeMillis();
            if (until != null && until > now) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "[生活] 冷却中，还需 " + ((until - now) / 1000 + 1) + " 秒。");
                return;
            }
            cooldowns.put(key, now + c.cooldownSec * 1000L);
        }
        final List<PotionEffect> eff = c.effects;
        final boolean bottle = e.getItem().getType() == org.bukkit.Material.POTION;
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) return;
                for (PotionEffect pe : eff) p.addPotionEffect(pe, true);
                // plain POTION items leave an empty bottle behind — take it back
                if (bottle) takeBottle(p);
            }
        });
    }

    private void takeBottle(Player p) {
        org.bukkit.inventory.ItemStack[] inv = p.getInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
            org.bukkit.inventory.ItemStack it = inv[i];
            if (it == null || it.getType() != org.bukkit.Material.GLASS_BOTTLE || ni.getNiId(it) != null) continue;
            if (it.getAmount() > 1) it.setAmount(it.getAmount() - 1); else p.getInventory().setItem(i, null);
            break;
        }
    }

    /**
     * ember-v1.0-P1 consumables inside P1 worlds (策划书 §19.4): the heal potion becomes the unified heal
     * (percent of max HP, one shared cooldown that survives reconnect); ids in consumables.allowed keep their
     * legacy effect; other potions are refused with a message (no silent swallow); food only restores hunger.
     * @return true when handled here (legacy effects must not run)
     */
    private boolean consumeP1(PlayerItemConsumeEvent e, final Player p, String id) {
        town.sunshine.corerpg.p1.EmberMode mode = town.sunshine.corerpg.p1.EmberMode.get();
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (mode == null || ls == null) return false;
        if (mode.list("consumables.allowed").contains(id)) return false;
        final boolean bottle = e.getItem().getType() == org.bukkit.Material.POTION;
        if (mode.list("heal_potion.items").contains(id)) {
            town.sunshine.corerpg.p1.EmberPlayerState st = ls.state(p.getUniqueId());
            long now = System.currentTimeMillis();
            town.sunshine.corerpg.p1.EmberRunService rc = plugin.getEmberRuns();
            if (rc != null && rc.potionCapped(p.getUniqueId())) { // P2-8 weekly rule 限药
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "[余烬] 本周规则：本局最多喝 " + rc.potionCap(p.getUniqueId()) + " 瓶回复药，已用完。");
                return true;
            }
            if (st.healCdUntil > now) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "[余烬] 回复药冷却中，还需 " + ((st.healCdUntil - now) / 1000 + 1) + " 秒（所有回复药共用）。");
                return true;
            }
            st.healCdUntil = now + Math.max(0, mode.i("heal_potion.cooldown_seconds", 15)) * 1000L;
            ls.saveState(p);
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns(); // D32: count for the death refund
            if (runs != null) runs.notePotion(p);
            town.sunshine.corerpg.p1.EmberGrowthService growth = town.sunshine.corerpg.p1.EmberGrowthService.get(); // D141 talents
            final double pct = mode.d("heal_potion.percent", 0.20) * (growth == null ? 1.0 : growth.potionMult(p));
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override public void run() {
                    if (!p.isOnline()) return;
                    town.sunshine.corerpg.p1.EmberHeal.heal(p, pct * town.sunshine.corerpg.p1.EmberHeal.maxHp(p), "D03 回复药 " + Math.round(pct * 100) + "%");
                    if (bottle) takeBottle(p);
                }
            });
            return true;
        }
        if (bottle) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "[余烬] 新模式副本内此药剂不可用（首版不开放增伤/加速类药）。");
            return true;
        }
        p.sendMessage(ChatColor.GRAY + "[余烬] 新模式副本内食物只补饱食度，附带效果不生效。");
        return true;
    }

    /** Anvil repair coin fee (coin sink); charged when the result is actually taken. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAnvilTake(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (!enabled || repairFee <= 0) return;
        if (!(e.getInventory() instanceof org.bukkit.inventory.AnvilInventory) || e.getRawSlot() != 2) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        org.bukkit.inventory.ItemStack cur = e.getCurrentItem();
        if (cur == null || cur.getType() == org.bukkit.Material.AIR) return;
        Player p = (Player) e.getWhoClicked();
        if (p.getGameMode() == org.bukkit.GameMode.CREATIVE) return;
        org.bukkit.inventory.AnvilInventory inv = (org.bukkit.inventory.AnvilInventory) e.getInventory();
        int lv = 0;
        try { lv = inv.getRepairCost(); } catch (Throwable ignored) { }
        if (p.getLevel() < lv) return; // vanilla refuses the take anyway
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d.getCoin() < repairFee) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "[铁砧] 修理手续费 " + repairFee + " 余烬币，你只有 " + d.getCoin() + "。");
            return;
        }
        d.takeCoin(repairFee);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(ChatColor.GRAY + "[铁砧] 修理手续费 -" + repairFee + " 余烬币");
    }

    // ---------------- command ----------------

    /** /corerpg life [buy <offer> [times] | cook] */
    public boolean cmd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("players only"); return true; }
        Player p = (Player) sender;
        if (!enabled) { p.sendMessage(ChatColor.RED + "[生活] 未启用"); return true; }
        PlayerData d = dataStore.get(p.getUniqueId());
        String act = args.length > 1 ? args[1].toLowerCase() : "";
        if (!act.isEmpty() && plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) {
            p.sendMessage(ChatColor.RED + "[生活] 副本内不能交易，出本后再来。");
            return true;
        }
        if ("fishdebug".equals(act) && p.isOp()) { fishDebug(p); return true; }
        if ("xp".equals(act) && p.isOp() && args.length > 3) {
            Player t = plugin.getServer().getPlayerExact(args[2]);
            if (t == null) { p.sendMessage(ChatColor.RED + "offline"); return true; }
            addXp(t, Integer.parseInt(args[3]));
            dataStore.flushMutation(t.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "[生活] " + t.getName() + " life Lv." + levelOf(t));
            return true;
        }
        if ("cook".equals(act) || "烹饪".equals(act)) { cook(p, d); return true; }
        if ("buy".equals(act) || "make".equals(act)) {
            Offer o = args.length > 2 ? offers.get(args[2]) : null;
            if (o == null) { p.sendMessage(ChatColor.RED + "[生活] 未知项目。/corerpg life 查看列表"); return true; }
            int times = 1;
            if (args.length > 3) try { times = Math.max(1, Math.min(16, Integer.parseInt(args[3]))); } catch (NumberFormatException ignored) { }
            int done = 0;
            for (int i = 0; i < times; i++) { if (!buy(p, d, o, i == 0 || done > 0)) break; done++; }
            if (done > 1) p.sendMessage(ChatColor.GREEN + "[生活] 共完成 " + done + " 次。");
            if (done > 0) dataStore.flushMutation(p.getUniqueId());
            return true;
        }
        show(p, d);
        return true;
    }

    private void show(Player p, PlayerData d) {
        int lv = lifeLevel(d);
        int next = lv < levelXp.length ? levelXp[lv] : -1;
        p.sendMessage(ChatColor.GOLD + "—— 枢纽补给 · 生活 ——" + ChatColor.GRAY + "  生活 Lv." + lv + "（经验 " + lifeXp(d)
                + (next > 0 ? "/" + next : "") + "）  余烬币 " + ChatColor.GOLD + d.getCoin());
        p.sendMessage(ChatColor.YELLOW + " cook" + ChatColor.GRAY + " — 代烤鱼：鳕鱼/鲑鱼 → 炭烤余烬鱼，每条 " + cookCoinEach + " 币");
        for (Offer o : offers.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.YELLOW).append(" ").append(o.id).append(ChatColor.GRAY).append(" — ").append(ChatColor.WHITE).append(o.name).append(ChatColor.GRAY).append("：");
            List<String> cost = new ArrayList<String>();
            for (Map.Entry<String, Integer> e : o.inputs.entrySet()) cost.add(ni.displayName(e.getKey()) + "×" + e.getValue());
            if (o.coin > 0) cost.add(o.coin + " 币");
            sb.append(cost.isEmpty() ? "免费" : String.join(" + ", cost));
            if (o.weekly > 0) sb.append(ChatColor.DARK_GRAY).append(" · 本周 ").append(d.periodCount("life_" + o.id, DailyService.weekId())).append("/").append(o.weekly);
            if (o.daily > 0) sb.append(ChatColor.DARK_GRAY).append(" · 今日 ").append(d.periodCount("life_" + o.id, DailyService.today())).append("/").append(o.daily);
            if (o.lifeLevel > lv) sb.append(ChatColor.RED).append(" · 需生活 Lv.").append(o.lifeLevel);
            p.sendMessage(sb.toString());
        }
        p.sendMessage(ChatColor.GRAY + " /corerpg life buy <项目> [次数] · /corerpg life cook · 钓竿在补给处购买，水边抛竿钓鱼升生活等级");
    }

    private boolean buy(Player p, PlayerData d, Offer o, boolean verbose) {
        if (o.lifeLevel > lifeLevel(d)) { if (verbose) p.sendMessage(ChatColor.RED + "[生活] 需要生活 Lv." + o.lifeLevel + "（钓鱼/烹饪/熬药升级）"); return false; }
        String wk = DailyService.weekId(), td = DailyService.today();
        if (o.weekly > 0 && d.periodCount("life_" + o.id, wk) >= o.weekly) { p.sendMessage(ChatColor.RED + "[生活] 本周次数已用完（" + o.weekly + "）。"); return false; }
        if (o.daily > 0 && d.periodCount("life_" + o.id, td) >= o.daily) { p.sendMessage(ChatColor.RED + "[生活] 今日次数已用完（" + o.daily + "）。"); return false; }
        for (Map.Entry<String, Integer> e : o.inputs.entrySet()) {
            int have = ni.countInInventory(p, e.getKey());
            if (have < e.getValue()) { p.sendMessage(ChatColor.RED + "[生活] 材料不足：" + ni.displayName(e.getKey()) + " " + have + "/" + e.getValue()); return false; }
        }
        if (d.getCoin() < o.coin) { p.sendMessage(ChatColor.RED + "[生活] 余烬币不足：需要 " + o.coin + "，拥有 " + d.getCoin()); return false; }
        for (Map.Entry<String, Integer> e : o.inputs.entrySet()) {
            if (!ni.consumeExact(p, e.getKey(), e.getValue())) { p.sendMessage(ChatColor.RED + "[生活] 扣除失败"); return false; }
        }
        if (o.coin > 0) d.takeCoin(o.coin);
        if (o.weekly > 0) d.addPeriodCount("life_" + o.id, wk, 1);
        if (o.daily > 0) d.addPeriodCount("life_" + o.id, td, 1);
        List<String> got = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : o.give.entrySet()) {
            ni.giveNiItem(p, e.getKey(), e.getValue());
            got.add(ni.displayName(e.getKey()) + "×" + e.getValue());
        }
        p.sendMessage(ChatColor.GREEN + "[生活] " + ChatColor.stripColor(o.name) + " → " + ChatColor.WHITE + String.join("，", got)
                + (o.coin > 0 ? ChatColor.GRAY + "（-" + o.coin + " 币）" : ""));
        addXp(p, o.xp);
        return true;
    }

    public static final String BREAD_OFFER_ID = "bread";
    public static final String BREAD_NI_ID = "food_ember_bread";

    /** D547: no grilled fish and no bread in bag (need hub bread pack). */
    public boolean needsBread(Player p) {
        if (p == null || !enabled || ni == null) return false;
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) return false;
        if (town.sunshine.corerpg.p1.EmberMode.active() && town.sunshine.corerpg.p1.EmberMode.isP1World(p.getWorld())) return false;
        Offer o = offers.get(BREAD_OFFER_ID);
        if (o == null) return false;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d == null || d.getCoin() < o.coin) return false;
        if (countPathFood(p) > 0) return false;
        if (ni.countInInventoryOnly(p, BREAD_NI_ID) > 0) return false;
        return true;
    }

    /** D547: buy life offer bread once. */
    public boolean pathBuyBread(Player p) {
        if (p == null || !needsBread(p)) return false;
        Offer o = offers.get(BREAD_OFFER_ID);
        PlayerData d = dataStore.get(p.getUniqueId());
        boolean ok = buy(p, d, o, true);
        if (ok) dataStore.flushMutation(p.getUniqueId());
        return ok;
    }

    public static final String BREW_OFFER_ID = "heal_potion";
    public static final int BREW_POTION_NEED = 2; // brew when below this many heal pots

    /** D546: can brew heal potion now (town, level, mats, coin, under potion count). */
    public boolean needsBrew(Player p) {
        if (p == null || !enabled || ni == null) return false;
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) return false;
        if (town.sunshine.corerpg.p1.EmberMode.active() && town.sunshine.corerpg.p1.EmberMode.isP1World(p.getWorld())) return false;
        Offer o = offers.get(BREW_OFFER_ID);
        if (o == null) return false;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d == null) return false;
        if (lifeLevel(d) < o.lifeLevel) return false;
        if (d.getCoin() < o.coin) return false;
        town.sunshine.corerpg.p1.EmberSupplyService supply = plugin.getEmberSupplies();
        int pots = supply == null ? 0 : supply.countHealPotions(p);
        if (pots >= BREW_POTION_NEED) return false;
        for (Map.Entry<String, Integer> e : o.inputs.entrySet()) {
            if (ni.countInInventory(p, e.getKey()) < e.getValue()) return false;
        }
        String wk = DailyService.weekId(), td = DailyService.today();
        if (o.weekly > 0 && d.periodCount("life_" + o.id, wk) >= o.weekly) return false;
        if (o.daily > 0 && d.periodCount("life_" + o.id, td) >= o.daily) return false;
        return true;
    }

    /** D546: brew one heal potion via life offer. */
    public boolean pathBrewHeal(Player p) {
        if (p == null || !needsBrew(p)) return false;
        Offer o = offers.get(BREW_OFFER_ID);
        PlayerData d = dataStore.get(p.getUniqueId());
        boolean ok = buy(p, d, o, true);
        if (ok) dataStore.flushMutation(p.getUniqueId());
        return ok;
    }

    public static final String ROD_NI_ID = "tool_ember_rod";
    public static final String ROD_OFFER_ID = "rod";

    /** D545: true when backpack has no fishing rod. */
    public boolean needsRod(Player p) {
        if (p == null || ni == null) return false;
        return ni.countInInventoryOnly(p, ROD_NI_ID) <= 0;
    }

    /** D545: buy life offer "rod" once (town only). Returns true on success. */
    public boolean pathBuyRod(Player p) {
        if (p == null || !enabled) return false;
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) return false;
        if (town.sunshine.corerpg.p1.EmberMode.active() && town.sunshine.corerpg.p1.EmberMode.isP1World(p.getWorld())) return false;
        Offer o = offers.get(ROD_OFFER_ID);
        if (o == null) return false;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d == null) return false;
        if (!needsRod(p)) return false;
        boolean ok = buy(p, d, o, true);
        if (ok) dataStore.flushMutation(p.getUniqueId());
        return ok;
    }

    /** D544: grilled food NI id used by bite path. */
    public String cookOutputId() { return cookOutput; }

    /** D544: count grilled food in backpack. */
    public int countPathFood(Player p) {
        if (p == null || ni == null || cookOutput == null) return 0;
        return ni.countInInventoryOnly(p, cookOutput);
    }

    public static final int BITE_HUNGER_NEED = 14;
    public static final int BITE_RESTORE = 5;

    public boolean needsBite(Player p) {
        if (p == null) return false;
        return p.getFoodLevel() < BITE_HUNGER_NEED && countPathFood(p) > 0;
    }

    /**
     * D544: eat one grilled fish from backpack. Restores hunger (combat sustain). Returns 1 if eaten.
     */
    public int pathBiteOne(Player p) {
        if (p == null || ni == null) return 0;
        if (p.getFoodLevel() >= 20) return 0;
        if (countPathFood(p) <= 0) return 0;
        if (!ni.consumeExact(p, cookOutput, 1)) return 0;
        int fl = Math.min(20, p.getFoodLevel() + BITE_RESTORE);
        p.setFoodLevel(fl);
        float sat = Math.min(20f, p.getSaturation() + BITE_RESTORE);
        p.setSaturation(sat);
        return 1;
    }

    /** D543: count cookable fish units in backpack (capped by cookMax). */
    public int countPathCook(Player p) {
        if (p == null || ni == null) return 0;
        int total = 0;
        for (String in : cookInputs) {
            int have = ni.countInInventory(p, in);
            if (have > 0) total += have;
            if (total >= cookMax) return cookMax;
        }
        return total;
    }

    /** D543: path AUTO/ASK — cook fish → food (town-safe; skips P1 run worlds). Returns cooked count. */
    public int pathCook(Player p) {
        if (p == null) return 0;
        if (town.sunshine.corerpg.p1.EmberMode.active() && town.sunshine.corerpg.p1.EmberMode.isP1World(p.getWorld())) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        if (d == null) return 0;
        int before = countPathCook(p);
        if (before <= 0) return 0;
        cook(p, d);
        int after = countPathCook(p);
        return Math.max(0, before - after);
    }

    private void cook(Player p, PlayerData d) {
        int total = 0;
        Map<String, Integer> take = new LinkedHashMap<String, Integer>();
        for (String in : cookInputs) {
            int have = ni.countInInventory(p, in);
            int use = Math.min(have, cookMax - total);
            if (use > 0) { take.put(in, use); total += use; }
        }
        if (total == 0) { p.sendMessage(ChatColor.RED + "[生活] 背包里没有可烤的鱼（鳕鱼/鲑鱼）。拿钓竿去水边钓吧。"); return; }
        int fee = total * cookCoinEach;
        if (d.getCoin() < fee) {
            total = cookCoinEach > 0 ? d.getCoin() / cookCoinEach : total;
            if (total <= 0) { p.sendMessage(ChatColor.RED + "[生活] 余烬币不足（每条 " + cookCoinEach + " 币）"); return; }
            fee = total * cookCoinEach;
            int left = total;
            for (Map.Entry<String, Integer> e : take.entrySet()) { int u = Math.min(left, e.getValue()); e.setValue(u); left -= u; }
        }
        int cooked = 0;
        for (Map.Entry<String, Integer> e : take.entrySet()) if (e.getValue() > 0 && ni.consumeExact(p, e.getKey(), e.getValue())) cooked += e.getValue();
        if (cooked <= 0) return;
        d.takeCoin(cooked * cookCoinEach);
        ni.giveNiItem(p, cookOutput, cooked);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(ChatColor.GREEN + "[生活] 烤好了 " + ni.displayName(cookOutput) + "×" + cooked + ChatColor.GRAY + "（-" + cooked * cookCoinEach + " 币）");
        addXp(p, cooked * cookXp);
    }

    // ---------------- fishing diagnostics (op) ----------------

    private final Map<UUID, Integer> fishDebugTasks = new HashMap<UUID, Integer>();

    static String hookInfo(org.bukkit.entity.Entity h) {
        org.bukkit.Location l = h.getLocation();
        org.bukkit.block.Block b = l.getBlock();
        org.bukkit.block.Block top = l.getWorld().getHighestBlockAt(l);
        return String.format("hook@%.2f,%.2f,%.2f block=%s below=%s ground=%s sky=%d highestY=%d biome=%s rain=%s ticks=%d",
                l.getX(), l.getY(), l.getZ(), b.getType(), b.getRelative(org.bukkit.block.BlockFace.DOWN).getType(),
                h.isOnGround(), b.getLightFromSky(), top.getY(), b.getBiome(), l.getWorld().hasStorm(), h.getTicksLived());
    }

    /** Toggle: every 2 s report the player's fishing hook block / sky / biome (chat + log). */
    private void fishDebug(final Player p) {
        Integer old = fishDebugTasks.remove(p.getUniqueId());
        if (old != null) { plugin.getServer().getScheduler().cancelTask(old); p.sendMessage(ChatColor.GRAY + "[生活] fishdebug off"); return; }
        int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override public void run() {
                if (!p.isOnline()) { Integer t = fishDebugTasks.remove(p.getUniqueId()); if (t != null) plugin.getServer().getScheduler().cancelTask(t); return; }
                org.bukkit.entity.FishHook hook = null;
                for (org.bukkit.entity.FishHook h : p.getWorld().getEntitiesByClass(org.bukkit.entity.FishHook.class))
                    if (h.getShooter() == p) hook = h;
                String msg = hook == null ? "no hook" : hookInfo(hook);
                p.sendMessage(ChatColor.DARK_GRAY + "[fishdebug] " + msg);
                plugin.getLogger().info("fishdebug " + p.getName() + " " + msg);
            }
        }, 40L, 40L);
        fishDebugTasks.put(p.getUniqueId(), id);
        p.sendMessage(ChatColor.GRAY + "[生活] fishdebug on");
    }

    public boolean isEnabled() { return enabled; }
    public int levelOf(Player p) { return lifeLevel(dataStore.get(p.getUniqueId())); }
}
