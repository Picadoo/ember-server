package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * D318 六槽 T1 · Bukkit side: migration trigger (hub join / back from an instance, switch {@code gear.six_slot.migrate},
 * default off), the per-player record {@code plugins/CoreRpg/p1-six/<uuid>.yml} (flag {@code p1_six_mig}, journal,
 * 待领), the 待领 claim, and the armor-page actions (equip / equip all) driven by {@code ember_p1_armor}.
 * All logic that decides anything lives in {@link EmberSixMigration} / {@link EmberSixRank}.
 */
public final class EmberSixSlotService implements Listener {

    public static final String DIR = "p1-six";
    public static final String CLAIM_TAG = "ember_six_c";
    /** player scoreboard tags (saved with the inventory in the player file): swap mark / 待领 hand-out mark */
    static final String MARK_SWAP = "ember_six_m_", MARK_CLAIM = "ember_six_c_";
    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    private static volatile EmberSixSlotService instance;

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final Set<UUID> busy = Collections.synchronizedSet(new HashSet<UUID>());
    private final Map<UUID, Object[]> allPending = new HashMap<UUID, Object[]>();
    private final Map<UUID, Integer> stashN = new java.util.concurrent.ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Object[]> viewCache = new HashMap<UUID, Object[]>();
    /** D320 ①: uids whose active row the DB confirmed in this process (remember / heal callback) */
    private final Set<String> dbConfirmed = Collections.synchronizedSet(new HashSet<String>());
    /** D320 ①: per player, the issued uids a DB_PENDING commit waits for (main thread only) */
    private final Map<UUID, Set<String>> awaiting = new HashMap<UUID, Set<String>>();

    public EmberSixSlotService(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
        instance = this;
    }

    public static EmberSixSlotService get() { return instance; }

    // ------------------------------------------------------------------ record file

    File file(UUID id) { return new File(new File(plugin.getDataFolder(), DIR), id.toString() + ".yml"); }

    EmberSixMigration.Record<ItemStack> load(UUID id) {
        EmberSixMigration.Record<ItemStack> r = new EmberSixMigration.Record<ItemStack>();
        File f = file(id);
        if (!f.exists()) return r;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
        r.flag = y.getInt(EmberSixMigration.FLAG, 0) == 1;
        r.doneAt = y.getLong("done_at", 0L);
        r.seq = y.getInt("seq", 0);
        ConfigurationSection j = y.getConfigurationSection("journal");
        if (j != null) {
            List<ItemStack> orig = new ArrayList<ItemStack>();
            List<EmberItemData> issued = new ArrayList<EmberItemData>();
            for (int i = 0; i < 4; i++) {
                orig.add(j.getItemStack("originals." + i));
                ConfigurationSection d = j.getConfigurationSection("issued." + i);
                if (d != null) issued.add(EmberItemData.fromMap(d.getValues(false)));
            }
            r.journal = new EmberSixMigration.Journal<ItemStack>(orig, issued, j.getLong("at"), j.getBoolean("reverting", false), j.getString("mark", ""));
        }
        ConfigurationSection s = y.getConfigurationSection("stash");
        if (s != null) for (String k : s.getKeys(false)) {
            ItemStack it = s.getItemStack(k + ".item");
            if (it != null) r.stash.put(k, new EmberSixMigration.StashEntry<ItemStack>(k, s.getInt(k + ".slot"), it));
        }
        ConfigurationSection v = y.getConfigurationSection("voided");
        if (v != null) for (String k : v.getKeys(false)) {
            ConfigurationSection d = v.getConfigurationSection(k);
            if (d != null) r.voided.add(EmberItemData.fromMap(d.getValues(false)));
        }
        ConfigurationSection pc = y.getConfigurationSection("pieces");
        if (pc != null) for (String k : pc.getKeys(false)) {
            ConfigurationSection d = pc.getConfigurationSection(k);
            if (d != null) r.pieces.add(EmberItemData.fromMap(d.getValues(false)));
        }
        return r;
    }

    void save(UUID id, EmberSixMigration.Record<ItemStack> r) {
        YamlConfiguration y = new YamlConfiguration();
        y.set(EmberSixMigration.FLAG, r.flag ? 1 : 0);
        if (r.doneAt > 0) y.set("done_at", r.doneAt);
        y.set("seq", r.seq);
        if (r.journal != null) {
            y.set("journal.at", r.journal.at);
            y.set("journal.reverting", r.journal.reverting);
            y.set("journal.mark", r.journal.mark);
            for (int i = 0; i < 4; i++) {
                if (i < r.journal.originals.size() && r.journal.originals.get(i) != null) y.set("journal.originals." + i, r.journal.originals.get(i));
                if (i < r.journal.issued.size()) y.createSection("journal.issued." + i, r.journal.issued.get(i).toMap());
            }
        }
        for (EmberSixMigration.StashEntry<ItemStack> e : r.stash.values()) {
            y.set("stash." + e.id + ".slot", e.slot);
            y.set("stash." + e.id + ".item", e.item);
        }
        for (int i = 0; i < r.voided.size(); i++) y.createSection("voided." + i, r.voided.get(i).toMap());
        for (int i = 0; i < r.pieces.size(); i++) y.createSection("pieces." + i, r.pieces.get(i).toMap());
        stashN.put(id, r.stash.size());
        File f = file(id);
        try {
            f.getParentFile().mkdirs();
            File tmp = new File(f.getParentFile(), f.getName() + ".tmp");
            y.save(tmp);
            Files.move(tmp.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception ex) {
            throw new IllegalStateException("p1-six record save " + id + ": " + ex, ex);
        }
    }

    /** number of items waiting in 待领 (PAPI) */
    public int stashCount(UUID id) {
        Integer n = stashN.get(id);
        if (n == null) stashN.put(id, n = load(id).stash.size());
        return n;
    }

    /**
     * D321 H7: every P1 uid sitting in {@code p1-six/<uuid>.yml} 待领 — any stash key (migration originals
     * {@code m…}, re-issued {@code r}+uid, …). Reads the ItemStack's signed uid; non-P1 originals contribute nothing.
     */
    public java.util.Set<String> stashP1Uids(UUID id) {
        java.util.Set<String> out = new java.util.LinkedHashSet<String>();
        for (EmberSixMigration.StashEntry<ItemStack> e : load(id).stash.values()) {
            if (e.item == null || !loadouts.items().hasData(e.item)) continue;
            EmberItems.Read r = loadouts.items().read(e.item);
            if (r != null && r.data != null && r.data.uid != null) out.add(r.data.uid);
        }
        return out;
    }
    public boolean migrated(UUID id) { return load(id).flag; }

    /**
     * D319 invsnap restore guard ({@link EmberSixRestoreGuard}) for online {@code p} and a snapshot taken at
     * {@code snapAtMs}; null = allow. Reads the record only (journal / flag / done_at), never the scoreboard tags (D319 (a)).
     * Never migrated (no record file) → null after one {@code exists()}.
     */
    public static EmberSixRestoreGuard.Verdict restoreGuard(Player p, long snapAtMs) {
        EmberSixSlotService svc = instance;
        if (svc == null || p == null) return null;
        if (!svc.file(p.getUniqueId()).exists()) return null;
        EmberSixMigration.Record<ItemStack> r = svc.load(p.getUniqueId());
        return EmberSixRestoreGuard.check(true, r.flag, r.journal != null, r.doneAt, snapAtMs);
    }

    // ------------------------------------------------------------------ ports

    final class LivePort implements EmberSixMigration.Port<ItemStack>, EmberSixMigration.ClaimPort<ItemStack> {
        final Player p;
        LivePort(Player p) { this.p = p; }
        public EmberSixMigration.Record<ItemStack> load() { return EmberSixSlotService.this.load(p.getUniqueId()); }
        public void save(EmberSixMigration.Record<ItemStack> r) { EmberSixSlotService.this.save(p.getUniqueId(), r); }
        public EmberSixMigration.Snapshot snapshot() {
            EmberLoadout cur = loadouts.refresh(p);
            return new EmberSixMigration.Snapshot(EmberMode.tables(), cur.blade, cur.charm, cur.level, cur.festHp, cur.festDef);
        }
        public ItemStack armor(int i) {
            ItemStack it = p.getInventory().getArmorContents()[EmberSixSlot.toArmorContents(i)];
            return it == null || it.getType() == Material.AIR ? null : it.clone();
        }
        public void setArmor(int i, ItemStack s) {
            PlayerInventory inv = p.getInventory();
            ItemStack[] a = inv.getArmorContents();
            a[EmberSixSlot.toArmorContents(i)] = s;
            inv.setArmorContents(a);
        }
        public void apply(List<ItemStack> armor, String mark, boolean marked) {
            if (armor != null) {
                PlayerInventory inv = p.getInventory();
                ItemStack[] a = inv.getArmorContents();
                for (int i = 0; i < 4; i++) a[EmberSixSlot.toArmorContents(i)] = armor.get(i);
                inv.setArmorContents(a);
            }
            if (marked) p.addScoreboardTag(MARK_SWAP + mark); else p.removeScoreboardTag(MARK_SWAP + mark);
        }
        public boolean marked(String mark) { return p.getScoreboardTags().contains(MARK_SWAP + mark); }
        public int dropSwapMarks() {
            int n = 0;
            for (String t : new ArrayList<String>(p.getScoreboardTags())) if (t.startsWith(MARK_SWAP) && p.removeScoreboardTag(t)) n++;
            return n;
        }
        public ItemStack create(EmberItemData d) { return loadouts.items().create(d); }
        public EmberItemData readWorn(int i) {
            ItemStack it = armor(i);
            if (it == null || !loadouts.items().hasData(it)) return null;
            EmberItems.Read r = loadouts.items().read(it);
            return r != null && r.ok() ? r.data : null;
        }
        public boolean holds(String uid) {
            for (ItemStack it : p.getInventory().getContents()) {
                if (it == null || !loadouts.items().hasData(it)) continue;
                EmberItems.Read r = loadouts.items().read(it);
                if (r != null && r.data != null && uid.equals(r.data.uid)) return true;
            }
            return false;
        }
        public boolean take(String uid) {
            PlayerInventory inv = p.getInventory();
            ItemStack[] c = inv.getContents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] == null || !loadouts.items().hasData(c[i])) continue;
                EmberItems.Read r = loadouts.items().read(c[i]);
                if (r == null || r.data == null || !uid.equals(r.data.uid)) continue;
                inv.setItem(i, null);
                return true;
            }
            return false;
        }
        public boolean persistInventory() { return EmberVault.savePlayerFileChecked(p); }
        public void remember(EmberItemData d) {
            final UUID owner = p.getUniqueId();
            final String uid = d.uid;
            loadouts.rememberRow(uid, owner, d.rev, "active"); // trust cache now (same as EmberLoadoutService.remember)
            loadouts.store().logCreate(d, owner, "active", null);
            Set<String> w = awaiting.get(owner);
            if (w == null) awaiting.put(owner, w = new HashSet<String>());
            w.add(uid);
            loadouts.store().upsertItem(d, owner, "active", ok -> dbAnswer(owner, uid, ok));
        }
        public boolean confirmed(String uid) { return !loadouts.store().usable() || dbConfirmed.contains(uid); }
        public void heal(EmberItemData d) {
            final UUID owner = p.getUniqueId();
            final String uid = d.uid;
            loadouts.store().insertItemIfAbsent(d, owner, row -> {
                if (row == null) { plugin.getLogger().warning("[P1 six] " + p.getName() + " self-heal " + uid + ": DB query failed (retried next join / hub visit)"); return; }
                UUID o = null;
                try { if (row.owner != null) o = UUID.fromString(row.owner); } catch (IllegalArgumentException ignored) {}
                loadouts.rememberRow(uid, o, row.rev, row.state); // whatever the DB really holds (a retired row stays retired)
                if ("active".equals(row.state) && owner.toString().equals(row.owner)) dbConfirmed.add(uid);
                else plugin.getLogger().warning("[P1 six] " + p.getName() + " self-heal " + uid + ": DB row is " + row.state + " / owner " + row.owner + " — left as it is");
                Player on = Bukkit.getPlayer(owner);
                if (on != null) loadouts.markDirty(on);
            });
        }
        public void retire(EmberItemData d) {
            loadouts.rememberRow(d.uid, p.getUniqueId(), d.rev, "retired"); // trust cache: void now, whatever the DB write does
            loadouts.store().upsertItem(d, p.getUniqueId(), "retired");     // async, idempotent; re-sent on join / hub
        }
        public void alert(String msg) { EmberSixSlotService.this.alert(p, msg); }
        public String newUid() { return EmberItemData.newUid(); }
        public long nowSec() { return System.currentTimeMillis() / 1000L; }

        // claim
        public boolean give(ItemStack stack, String entryId) {
            PlayerInventory inv = p.getInventory();
            int slot = inv.firstEmpty();
            if (slot < 0 || slot >= 36) return false;
            Map<String, Object> tag = new LinkedHashMap<String, Object>();
            tag.put("id", entryId);
            ItemStack t = NmsNbt.write(stack, CLAIM_TAG, tag);
            if (t == null) return false;
            inv.setItem(slot, t);
            p.addScoreboardTag(MARK_CLAIM + entryId);
            return true;
        }
        /** the hand-out mark (on the player, saved with the inventory) — not the stack, which the player may have moved */
        public boolean tagged(String entryId) { return p.getScoreboardTags().contains(MARK_CLAIM + entryId); }
        int findTag(String entryId) {
            ItemStack[] c = p.getInventory().getContents();
            for (int i = 0; i < c.length; i++) {
                Map<String, Object> m = c[i] == null ? null : NmsNbt.read(c[i], CLAIM_TAG);
                if (m != null && entryId.equals(String.valueOf(m.get("id")))) return i;
            }
            return -1;
        }
        public void untag(String entryId) {
            p.removeScoreboardTag(MARK_CLAIM + entryId);
            int i = findTag(entryId);
            if (i < 0) return;
            ItemStack clean = NmsNbt.write(p.getInventory().getContents()[i], CLAIM_TAG, null);
            if (clean != null) p.getInventory().setItem(i, clean);
        }
        public boolean revoke(String entryId) {
            p.removeScoreboardTag(MARK_CLAIM + entryId);
            int i = findTag(entryId);
            if (i < 0) return false;
            p.getInventory().setItem(i, null);
            return true;
        }
        public void untagExcept(Set<String> owed) {
            for (String t : new ArrayList<String>(p.getScoreboardTags()))
                if (t.startsWith(MARK_CLAIM) && !owed.contains(t.substring(MARK_CLAIM.length()))) p.removeScoreboardTag(t);
            ItemStack[] c = p.getInventory().getContents();
            for (int i = 0; i < c.length; i++) {
                Map<String, Object> m = c[i] == null ? null : NmsNbt.read(c[i], CLAIM_TAG);
                if (m == null || owed.contains(String.valueOf(m.get("id")))) continue;
                ItemStack clean = NmsNbt.write(c[i], CLAIM_TAG, null);
                if (clean != null) p.getInventory().setItem(i, clean);
            }
        }
    }

    private void alert(Player p, String msg) {
        String line = "[P1 six] " + (p == null ? "?" : p.getName()) + " " + msg;
        plugin.getLogger().warning(line);
        for (Player op : Bukkit.getOnlinePlayers()) if (op.hasPermission("corerpg.admin")) op.sendMessage(ChatColor.RED + line);
    }

    // ------------------------------------------------------------------ migration trigger

    /** §3.2 时机: hub only — not in a run instance, not in the 挂机庭, alive, not spectating */
    boolean atHub(Player p) {
        if (p == null || !p.isOnline() || p.isDead() || p.getGameMode() == GameMode.SPECTATOR) return false;
        if (!EmberMode.active()) return false;
        if (EmberRunService.blocksLegacy(p)) return false;
        return !EmberAfkService.blocksLegacyPayout(p.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!EmberSixSlot.enabled()) return;
        final Player p = e.getPlayer();
        if (file(p.getUniqueId()).exists()) {
            try { EmberSixMigration.onJoin(new LivePort(p)); } // voided uids: trust cache now, DB write again; D320 ② self-heal
            catch (RuntimeException ex) { alert(p, "六槽作废 uid 重发 / 自愈失败：" + ex); }
        }
        if (!EmberSixSlot.migrateEnabled()) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> tryMigrate(p), 120L); // after invsnap (40) and deliveries (60)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorld(PlayerChangedWorldEvent e) {
        if (!EmberSixSlot.migrateEnabled()) return;
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> tryMigrate(p), 40L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        allPending.remove(id);
        awaiting.remove(id); // a late DB answer still lands in dbConfirmed; the next visit commits
        stashN.remove(id);
        viewCache.remove(id);
    }

    /** @return the outcome, or null when deferred / switched off */
    public EmberSixMigration.Outcome tryMigrate(Player p) {
        if (!EmberSixSlot.migrateEnabled() || !atHub(p)) return null;
        if (!busy.add(p.getUniqueId())) return null;
        try {
            EmberSixMigration<ItemStack> m = new EmberSixMigration<ItemStack>(new LivePort(p));
            EmberSixMigration.Outcome o;
            try {
                o = m.run();
            } catch (RuntimeException ex) {
                alert(p, "六槽迁移中断（下次回到枢纽时自动续上或作废）：" + ex);
                return EmberSixMigration.Outcome.ERROR;
            }
            if (o != EmberSixMigration.Outcome.ALREADY || !m.lastDetail.isEmpty()) plugin.getLogger().info("[P1 six] " + p.getName() + " migration " + o + " " + m.lastDetail);
            if (o == EmberSixMigration.Outcome.MIGRATED || o == EmberSixMigration.Outcome.ROLLED_FORWARD) {
                loadouts.markDirty(p);
                p.sendMessage(P + "护甲栏已换上和你护符对应的四件护甲，生命和防御不变。");
                int n = stashCount(p.getUniqueId());
                if (n > 0) p.sendMessage(P + EmberSixPapi.stashLine(n) + "§7（装备 → 护甲）");
            } else if (o == EmberSixMigration.Outcome.DB_PENDING) {
                // D320 ①: the DB answer re-runs the migration (dbAnswer); nothing to tell the player yet
            } else if (o == EmberSixMigration.Outcome.SAVE_FAILED) {
                alert(p, "六槽迁移未完成：" + m.lastDetail); // nothing marked; the next hub visit resumes
            } else if (o == EmberSixMigration.Outcome.NO_TEMPLATE) {
                alert(p, "六槽迁移未执行：护甲物品模板缺失 " + m.lastDetail);
            }
            return o;
        } finally {
            busy.remove(p.getUniqueId());
        }
    }

    /**
     * D320 ①: a confirmed / failed DB write of an issued piece (main thread). All of the player's awaited uids confirmed →
     * run the migration again (it commits); a failure → OP alert, the next hub visit sends again.
     */
    void dbAnswer(UUID owner, String uid, boolean ok) {
        Set<String> w = awaiting.get(owner);
        if (ok) dbConfirmed.add(uid);
        Player p = Bukkit.getPlayer(owner);
        if (!ok) {
            if (w != null) w.remove(uid);
            alert(p, "六槽迁移等待 DB 确认失败（" + uid + "），未置完成标记；下次回到枢纽重试（DB 状态见运维手册）");
            return;
        }
        if (w == null) return;
        w.remove(uid);
        if (!w.isEmpty()) return;
        awaiting.remove(owner);
        if (p != null && p.isOnline()) Bukkit.getScheduler().runTask(plugin, () -> tryMigrate(p));
    }

    // ------------------------------------------------------------------ commands (menu only)

    /**
     * /corerpg p1 armor claim | equip &lt;slot&gt; | all | worn &lt;slot&gt; | set — sent by ember_p1_armor, never typed by
     * players. Every click gets a reply ({@link EmberSixPapi#route}). D320 ④: 待领 claim does not depend on the switch
     * (a rollback with the switch off must still hand the originals back); everything else answers 「尚未开放」 when off.
     */
    public boolean cmd(CommandSender s, String[] args) {
        String a = args.length >= 3 ? args[2].toLowerCase(java.util.Locale.ROOT) : "";
        if ("mig".equals(a) || "status".equals(a)) return admin(s, a, args);
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        boolean on = EmberSixSlot.enabled();
        int slot = args.length >= 4 ? EmberSixSlot.parseSlot(args[3]) : -1;
        switch (EmberSixPapi.route(on, on || !"claim".equals(a) ? 0 : stashCount(p.getUniqueId()), a)) {
            case CLAIM: claim(p); return true;
            case EQUIP: equip(p, slot); return true;
            case ALL: equipAll(p, args.length >= 4 && "confirm".equalsIgnoreCase(args[3])); return true;
            case WORN: p.sendMessage(P + EmberSixPapi.wornReply(slot < 0 ? null : view(p), slot)); return true;
            case SET: p.sendMessage(P + EmberSixPapi.setReply(view(p))); return true;
            case OFF: p.sendMessage(P + EmberSixPapi.OFF_TEXT); return true;
            default: p.sendMessage(P + "请从 装备 → 护甲 页操作。"); return true;
        }
    }

    private boolean admin(CommandSender s, String a, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        Player t = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : (s instanceof Player ? (Player) s : null);
        if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
        if ("status".equals(a)) {
            EmberSixMigration.Record<ItemStack> r = load(t.getUniqueId());
            s.sendMessage(P + t.getName() + " p1_six_mig=" + (r.flag ? 1 : 0) + " journal=" + (r.journal != null) + " 待领=" + r.stash.size()
                    + " enabled=" + EmberSixSlot.enabled() + " migrate=" + EmberSixSlot.migrateEnabled());
            return true;
        }
        if (!EmberSixSlot.migrateEnabled()) { s.sendMessage(P + "gear.six_slot.migrate 未开（默认关）"); return true; }
        EmberSixMigration.Outcome o = tryMigrate(t);
        s.sendMessage(P + t.getName() + " → " + (o == null ? "延后（不在枢纽 / 忙）" : o.name()));
        return true;
    }

    private final Set<Object> claimBusy = Collections.synchronizedSet(new HashSet<Object>());

    public void claim(Player p) {
        viewCache.remove(p.getUniqueId());
        LivePort port = new LivePort(p);
        int[] r;
        try {
            r = EmberSixMigration.claimGuarded(claimBusy, p.getUniqueId(), port, port);
            if (r == null) return; // a claim for this player is still running (repeated click): it finishes the job
        } catch (RuntimeException ex) {
            alert(p, "待领领取中断：" + ex);
            p.sendMessage(P + ChatColor.RED + "领取没有完成，物品仍在待领里，稍后再试。");
            return;
        }
        if (r[0] == 0 && r[1] == 0) p.sendMessage(P + "没有待领物品。");
        else if (r[1] == 0) p.sendMessage(P + "已领取 " + r[0] + " 件，放进了背包。");
        else p.sendMessage(P + "已领取 " + r[0] + " 件；背包满了，还有 " + r[1] + " 件留在待领里，腾出空位再来。");
        loadouts.markDirty(p);
    }

    // ------------------------------------------------------------------ armor page actions

    /** verified own-bound v2 armor in the backpack (storage slots), with their slot numbers */
    List<int[]> candidateSlots(Player p, List<EmberItemData> out) {
        List<int[]> at = new ArrayList<int[]>();
        ItemStack[] st = p.getInventory().getStorageContents();
        EmberLoadout cur = loadouts.get(p);
        for (int i = 0; i < st.length; i++) {
            ItemStack it = st[i];
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || !r.data.isArmor() || !r.data.bound || !r.data.itemKeys()) continue;
            if (loadouts.trust(p, r.data) != null) continue;
            if (cur.blade != null && cur.blade.uid.equals(r.data.uid) || cur.charm != null && cur.charm.uid.equals(r.data.uid)) continue;
            out.add(r.data);
            at.add(new int[]{i});
        }
        return at;
    }

    /** the page view for PAPI: current loadout + best candidate per slot */
    public EmberSixRank.View view(Player p) {
        EmberLoadout cur = loadouts.get(p);
        List<EmberItemData> cands = new ArrayList<EmberItemData>();
        candidateSlots(p, cands);
        EmberItemData[] worn = cur.sixSlot() ? cur.armorCopy() : new EmberItemData[4];
        return EmberSixRank.view(EmberMode.tables(), cur.blade, cur.charm, cur.level, cur.festHp, cur.festDef, worn, cands);
    }

    /** {@link #view} cached for one second (a menu page asks ~25 placeholders in one refresh) */
    public EmberSixRank.View cachedView(Player p) {
        Object[] c = viewCache.get(p.getUniqueId());
        long now = System.currentTimeMillis();
        if (c != null && now - (Long) c[0] < 1000L) return (EmberSixRank.View) c[1];
        EmberSixRank.View v = view(p);
        viewCache.put(p.getUniqueId(), new Object[]{now, v});
        return v;
    }

    void equip(Player p, int slot) {
        if (slot < 0 || slot > 3) { p.sendMessage(P + "请从护甲页点选部位。"); return; }
        EmberSixRank.View v = view(p);
        EmberSixRank.Pick pick = v.best[slot];
        if (pick == null) { p.sendMessage(P + "背包里没有可换的" + EmberSixSlot.slotLabel(slot) + "。"); return; }
        viewCache.remove(p.getUniqueId());
        if (swapIn(p, slot, pick.piece.uid)) {
            p.sendMessage(P + "已换上" + EmberSixSlot.slotLabel(slot) + "：" + pick.piece.shortLabel() + "（" + EmberSixRank.deltaText(pick.delta) + "）。");
            loadouts.markDirty(p);
        }
    }

    /** D549: backpack has at least one strictly better armor piece to wear (town / hub). */
    public boolean needsArmor(Player p) {
        if (p == null || !EmberSixSlot.enabled()) return false;
        if (EmberMode.isP1World(p.getWorld())) return false;
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) return false;
        EmberSixRank.View v = view(p);
        return !EmberSixRank.allPlan(v).isEmpty();
    }

    /** D549: equip every better piece once (no double-confirm). Returns how many slots swapped. */
    public int pathEquipAll(Player p) {
        if (p == null || !needsArmor(p)) return 0;
        EmberSixRank.View v = view(p);
        List<Integer> todo = EmberSixRank.allPlan(v);
        if (todo.isEmpty()) return 0;
        allPending.remove(p.getUniqueId());
        viewCache.remove(p.getUniqueId());
        int n = 0;
        for (int i : todo) {
            if (v.best[i] == null) continue;
            if (swapIn(p, i, v.best[i].piece.uid)) n++;
        }
        if (n > 0) loadouts.markDirty(p);
        return n;
    }

    void equipAll(Player p, boolean confirm) {
        EmberSixRank.View v = view(p);
        List<Integer> todo = EmberSixRank.allPlan(v);
        if (todo.isEmpty()) {
            if (allPending.remove(p.getUniqueId()) != null) p.sendMessage(P + EmberSixRank.REFRESHED_TEXT); // the plan changed to "nothing"
            p.sendMessage(P + "四个部位都已是最好的一件，不用换。");
            return;
        }
        String key = EmberSixRank.planKey(v, todo);
        Object[] at = allPending.get(p.getUniqueId());
        long now = System.currentTimeMillis();
        EmberSixRank.Confirm c = EmberSixRank.confirm(at == null ? null : (Long) at[0], at == null ? null : (String) at[1], now, key);
        if (c != EmberSixRank.Confirm.EXECUTE) {
            allPending.put(p.getUniqueId(), new Object[]{now, key});
            StringBuilder sb = new StringBuilder();
            for (int i : todo) sb.append(sb.length() == 0 ? "" : "、").append(EmberSixSlot.slotLabel(i));
            String why = EmberSixRank.confirmText(c); // REFRESHED: 背包有变化 / EXPIRED: 确认已超时 (D320 ⑥)
            if (why != null) p.sendMessage(P + why);
            p.sendMessage(P + "将换上：" + sb + "（合计" + EmberSixRank.deltaText(EmberSixRank.allDelta(v, todo)) + "）。30 秒内再点一次「全部换上」确认。");
            return;
        }
        allPending.remove(p.getUniqueId());
        viewCache.remove(p.getUniqueId());
        int n = 0;
        for (int i : todo) if (swapIn(p, i, v.best[i].piece.uid)) n++;
        loadouts.markDirty(p);
        p.sendMessage(P + "已换上 " + n + " 件护甲。");
    }

    /** backpack piece {@code uid} ↔ armor slot: one swap, the old piece takes the freed backpack slot (no loss, no copy) */
    boolean swapIn(Player p, int slot, String uid) {
        PlayerInventory inv = p.getInventory();
        ItemStack[] st = inv.getStorageContents();
        int from = -1;
        for (int i = 0; i < st.length && from < 0; i++) {
            if (st[i] == null || !loadouts.items().hasData(st[i])) continue;
            EmberItems.Read r = loadouts.items().read(st[i]);
            if (r != null && r.data != null && uid.equals(r.data.uid)) from = i;
        }
        if (from < 0) { p.sendMessage(P + "那件护甲已不在背包里，请重新打开护甲页。"); return false; }
        ItemStack[] armor = inv.getArmorContents();
        int bi = EmberSixSlot.toArmorContents(slot);
        ItemStack old = armor[bi];
        armor[bi] = st[from];
        inv.setItem(from, old == null || old.getType() == Material.AIR ? null : old);
        inv.setArmorContents(armor);
        EmberVault.savePlayerFile(p);
        return true;
    }
}
