package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.QuestService;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberItemStore.TxnResult;
import town.sunshine.corerpg.p1.EmberItemStore.TxnStatus;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * G03 P1 item investment on Bukkit (策划书 §5.3, §6.1–6.4). Old EnhanceService/ForgeService/ScrapService stay as
 * they are for legacy items (they already refuse ember_v1 stacks).
 *
 * <p>Flow of every mutation (main thread unless noted): mode on + outside P1/instance worlds → item trusted (signed
 * NBT, DB owner/rev/state) → per-uid lock → materials and coins checked, then paid together → RNG (enhance only) →
 * new signed stack pre-built → <b>one DB transaction</b> (async: replay check by request id, SELECT … FOR UPDATE, rev
 * check, UPDATE, ledger INSERT into cr_p1_txn) → OK: rewrite the stack found by uid; otherwise refund everything.
 * With YAML storage there is no DB: signed NBT only, replay map in memory.</p>
 */
public final class EmberForgeService implements Listener {

    private static final String P = ChatColor.GOLD + "[余烬锻造] " + ChatColor.GRAY;
    private static final Pattern RID = Pattern.compile("[A-Za-z0-9_\\-:]{6,64}");
    private static final Pattern HEX = Pattern.compile("[0-9a-f]{4,32}");
    public static final String FLAG_PREFIX = "p1_first_clear_";

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final Set<String> busy = new HashSet<String>();
    /** request id → note of the completed request (YAML mode / quick replay); bounded */
    private final Map<String, String> done = new LinkedHashMap<String, String>(64, 0.75f, false) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, String> e) { return size() > 1024; }
    };
    /** refunds that could not be handed out because the player had left (in memory; logged) */
    private final Map<UUID, List<Cost>> pendingRefunds = new HashMap<UUID, List<Cost>>();

    public EmberForgeService(CoreRpgPlugin plugin, EmberLoadoutService loadouts) {
        this.plugin = plugin;
        this.loadouts = loadouts;
    }

    private NiBridge ni() { return plugin.getNiBridge(); }
    private EmberItems items() { return loadouts.items(); }
    private EmberItemStore store() { return loadouts.store(); }

    // ================================================================== commands

    /** /corerpg p1 enhance|swap|upgrade|refine|quality|dismantle|sync|flag ... */
    public boolean cmd(CommandSender s, String op, String[] args) {
        if ("flag".equals(op)) return flag(s, args);
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        boolean go = false;
        String rid = null, target = null, sub = null, tok = null;
        for (int i = 2; i < args.length; i++) {
            String a = args[i];
            String l = a.toLowerCase(Locale.ROOT);
            if (l.equals("confirm") || l.equals("go") || l.equals("确认")) go = true;
            else if (l.startsWith("rid:")) rid = a.substring(4);
            else if (l.startsWith("tok:")) tok = a.substring(4);
            else if (l.equals("craft") || l.equals("精工")) sub = "craft";
            else if (l.equals("quality") || l.equals("成色")) sub = "quality";
            else if (HEX.matcher(l).matches()) target = l;
        }
        if (rid != null && !RID.matcher(rid).matches()) { p.sendMessage(P + ChatColor.RED + "request id 只能是 6–64 位字母数字/-/_/:"); return true; }
        if ("sync".equals(op)) { resync(p, true); return true; }
        if ("refine".equals(op) && "quality".equals(sub)) op = "quality";
        String gate = gate(p);
        if (gate != null) { p.sendMessage(P + ChatColor.RED + gate); return true; }
        if ("swap".equals(op)) return swap(p, target, go, rid);
        Slot it = held(p);
        if (it.error != null) { p.sendMessage(P + ChatColor.RED + it.error); return true; }
        if ("enhance".equals(op)) return enhance(p, it, go, rid);
        if ("upgrade".equals(op)) return simple(p, it, "upgrade", EmberUpgradeRules.upgrade(it.data, firstClear(p, EmberUpgradeRules.upgradeFlag(it.data.tier))), go, rid);
        if ("refine".equals(op)) return simple(p, it, "refine", EmberUpgradeRules.refine(it.data), go, rid);
        if ("quality".equals(op)) return simple(p, it, "quality", EmberUpgradeRules.quality(it.data), go, rid);
        if ("dismantle".equals(op)) return dismantle(p, it, go, rid, tok);
        return help(p);
    }

    public static void helpLines(CommandSender s) {
        s.sendMessage(P + "手持 P1 装备：/corerpg p1 enhance | upgrade | refine [craft|quality] | dismantle  — 先预览，加 confirm 执行");
        s.sendMessage(P + "/corerpg p1 swap [对方uid前缀] [confirm] — 主手与副手（或背包里该 uid）同部位互换强化轨道，二者绑定");
        s.sendMessage(P + "可选 rid:<请求ID> 用于重放保护；/corerpg p1 sync 按 DB 同步背包内 P1 装备");
    }

    private boolean help(Player p) { helpLines(p); return true; }

    /** @return null when forging is allowed here */
    private String gate(Player p) {
        if (!EmberMode.active()) return "P1 新模式未开启";
        if (EmberMode.isP1World(p.getWorld())) return "请回城后操作（P1 战斗世界内不可锻造）";
        QuestService q = plugin.getQuestService();
        if (q != null && q.isInstanceWorld(p.getWorld())) return "请回城后操作（副本内不可锻造）";
        return null;
    }

    private static final class Slot {
        final int index; final ItemStack stack; final EmberItemData data; final String error;
        Slot(int index, ItemStack stack, EmberItemData data, String error) { this.index = index; this.stack = stack; this.data = data; this.error = error; }
    }

    private Slot held(Player p) {
        PlayerInventory inv = p.getInventory();
        return check(p, inv.getHeldItemSlot(), inv.getItemInMainHand());
    }

    private Slot check(Player p, int index, ItemStack st) {
        if (st == null || !items().hasData(st)) return new Slot(index, st, null, "请手持 P1 装备（ember_v1）");
        EmberItems.Read r = items().read(st);
        if (r == null || !r.ok()) return new Slot(index, st, null, "物品不可信: " + (r == null ? "无数据" : r.problem) + "（可试 /corerpg p1 sync）");
        String t = loadouts.trust(p, r.data);
        if (t != null) return new Slot(index, st, null, "物品校验未通过: " + t + "（稍后重试或 /corerpg p1 sync）");
        if (busy.contains(r.data.uid)) return new Slot(index, st, null, "该物品有操作正在处理中");
        return new Slot(index, st, r.data, null);
    }

    // ================================================================== payment

    private List<String> lacking(Player p, Cost c) {
        List<String> out = new ArrayList<String>();
        for (Map.Entry<String, Integer> m : c.materials().entrySet()) {
            int have = ni().countInInventory(p, m.getKey());
            if (have < m.getValue()) out.add(ni().displayName(m.getKey()) + " " + have + "/" + m.getValue());
        }
        PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
        int coins = pd == null ? 0 : pd.getCoin();
        if (c.coins > 0 && coins < c.coins) out.add("余烬币 " + coins + "/" + c.coins);
        return out;
    }

    /** Checks everything first, then takes all of it; on any partial failure gives back what was taken. */
    private String pay(Player p, Cost c) {
        List<String> lack = lacking(p, c);
        if (!lack.isEmpty()) return "材料不足（未扣除、未抽取、保底不变）: " + String.join("，", lack);
        Map<String, Integer> taken = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> m : c.materials().entrySet()) {
            int got = ni().consume(p, m.getKey(), m.getValue());
            taken.put(m.getKey(), got);
            if (got < m.getValue()) { giveBack(p, taken, 0); return "扣除材料失败，已退回"; }
        }
        PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
        if (c.coins > 0 && (pd == null || !pd.takeCoin(c.coins))) { giveBack(p, taken, 0); return "扣除余烬币失败，已退回"; }
        return null;
    }

    private void giveBack(Player p, Map<String, Integer> mats, int coins) {
        for (Map.Entry<String, Integer> m : mats.entrySet()) if (m.getValue() > 0) ni().giveNiItem(p, m.getKey(), m.getValue());
        if (coins > 0) { PlayerData pd = plugin.getDataStore().get(p.getUniqueId()); if (pd != null) pd.addCoin(coins); }
    }

    private void refund(UUID id, Cost c) {
        Player p = Bukkit.getPlayer(id);
        if (p == null) {
            List<Cost> l = pendingRefunds.get(id);
            if (l == null) { l = new ArrayList<Cost>(); pendingRefunds.put(id, l); }
            l.add(c);
            plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] forge refund queued for offline " + id + ": " + c.json());
            return;
        }
        giveBack(p, c.materials(), c.coins);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        List<Cost> l = pendingRefunds.remove(p.getUniqueId());
        if (l != null) {
            for (Cost c : l) giveBack(p, c.materials(), c.coins);
            p.sendMessage(P + "上次掉线时未完成的锻造已退回材料。");
        }
        if (EmberMode.active() && store().usable()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) resync(p, false); }, 60L);
        }
    }

    private boolean firstClear(Player p, String flag) {
        if (flag == null) return false;
        PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
        if (pd == null) return false;
        // B2.143: the run service writes p1_first_clear_<map>@<content version>; the admin stub writes @all
        EmberRunService runs = plugin.getEmberRuns();
        if (runs != null && runs.firstClearedKey(pd, flag)) return true;
        return pd.periodCount(FLAG_PREFIX + flag, "all") > 0; // admin stub (/corerpg p1 flag) until Q04/Q07 exist
    }

    // ================================================================== operations

    private String preview(EmberItemData d) {
        EmberTables t = EmberMode.tables();
        return String.format(Locale.ROOT, "%s · e=%.0f%% · 成长 ×%.2f", d.shortLabel(), t.enhance(d.enhance) * 100,
                EmberFormula.growth(t, d.quality, d.craft, d.enhance));
    }

    private boolean enhance(Player p, Slot it, boolean go, String rid) {
        EmberItemData d = it.data;
        Plan c = EmberUpgradeRules.enhanceCheck(d);
        if (!c.ok()) { p.sendMessage(P + ChatColor.RED + c.error); return true; }
        int target = d.enhance + 1;
        int max = EmberUpgradeRules.maxTries(target);
        if (!go) {
            p.sendMessage(P + "强化预览: +" + d.enhance + " → +" + target + "  成功率 " + Math.round(EmberUpgradeRules.rate(target) * 100)
                    + "%  本档第 " + EmberUpgradeRules.attemptNo(d) + "/" + max + " 次" + (EmberUpgradeRules.guaranteed(d) ? ChatColor.GREEN + "（本次必成）" + ChatColor.GRAY : ""));
            p.sendMessage(P + "每次消耗: " + c.cost.label() + "  · 失败不降级、不爆装，失败计数保存在本物品");
            List<String> lack = lacking(p, c.cost);
            if (!lack.isEmpty()) p.sendMessage(P + ChatColor.RED + "缺少: " + String.join("，", lack));
            p.sendMessage(P + "执行: /corerpg p1 enhance confirm");
            return true;
        }
        String r = rid != null ? rid : "enh:" + d.uid + ":" + d.rev;
        if (replayed(p, r)) return true;
        String err = pay(p, c.cost);
        if (err != null) { p.sendMessage(P + ChatColor.RED + err); return true; }
        Plan plan = EmberUpgradeRules.enhance(d, ThreadLocalRandom.current().nextDouble());
        commit(p, "enhance", r, c.cost, Arrays.asList(new TxnItem(d, plan.after, null)), plan.note);
        return true;
    }

    private boolean simple(Player p, Slot it, String kind, Plan plan, boolean go, String rid) {
        if (!plan.ok()) { p.sendMessage(P + ChatColor.RED + plan.error); return true; }
        if (!go) {
            p.sendMessage(P + plan.note);
            p.sendMessage(P + "当前: " + preview(it.data));
            p.sendMessage(P + "之后: " + preview(plan.after) + "（确定成功，预览即结果）");
            p.sendMessage(P + "消耗: " + plan.cost.label());
            List<String> lack = lacking(p, plan.cost);
            if (!lack.isEmpty()) p.sendMessage(P + ChatColor.RED + "缺少: " + String.join("，", lack));
            p.sendMessage(P + "执行: /corerpg p1 " + ("quality".equals(kind) ? "refine quality" : kind) + " confirm");
            return true;
        }
        String r = rid != null ? rid : kind + ":" + it.data.uid + ":" + it.data.rev;
        if (replayed(p, r)) return true;
        String err = pay(p, plan.cost);
        if (err != null) { p.sendMessage(P + ChatColor.RED + err); return true; }
        commit(p, kind, r, plan.cost, Arrays.asList(new TxnItem(it.data, plan.after, null)), plan.note);
        return true;
    }

    private boolean dismantle(Player p, Slot it, boolean go, String rid, String tok) {
        String why = EmberUpgradeRules.dismantleCheck(it.data);
        if (why != null) { p.sendMessage(P + ChatColor.RED + why); return true; }
        int blanks = EmberUpgradeRules.dismantleYield(it.data);
        // B2.137: destroying needs the one-shot token from a preview (clickable [确认分解]), bound to uid+rev+slot
        String fp = it.data.uid + "|" + it.data.rev + "|" + it.index;
        if (go) {
            String bad = town.sunshine.corerpg.ConfirmTokens.consume(p, "p1dismantle", tok, fp);
            if (bad != null) { p.sendMessage(P + ChatColor.RED + bad); go = false; }
        }
        if (!go) {
            p.sendMessage(P + "分解 " + it.data.shortLabel() + " → 胚料 ×" + blanks + "（不退强化材料与金币，物品永久销毁）");
            if (it.data.isBlade() && onlyBlade(p, it.index))
                p.sendMessage(P + ChatColor.RED + "⚠ 这是你背包里唯一的 P1 刃，分解后新模式副本内将没有可用武器");
            String t = town.sunshine.corerpg.ConfirmTokens.issue(p, "p1dismantle", fp);
            town.sunshine.corerpg.ConfirmTokens.sendClick(p, P + "物品将被永久销毁：", "[确认分解]",
                    "/corerpg p1 dismantle confirm tok:" + t, "分解 " + it.data.shortLabel() + "\n物品永久销毁，不可撤销");
            return true;
        }
        if (ni().createNiItem(EmberUpgradeRules.MAT_BLANK) == null) { p.sendMessage(P + ChatColor.RED + "胚料模板 " + EmberUpgradeRules.MAT_BLANK + " 未加载（需 ni reload/重启）"); return true; }
        String r = rid != null ? rid : "dis:" + it.data.uid + ":" + it.data.rev;
        if (replayed(p, r)) return true;
        // take the item out first so it cannot be used or moved while the transaction runs
        final ItemStack original = it.stack.clone();
        p.getInventory().setItem(it.index, null);
        pendingDismantle.put(it.data.uid, new Object[]{original, blanks}); // before commit: YAML mode finishes inline
        commit(p, "dismantle", r, Cost.NONE, Arrays.asList(new TxnItem(it.data, null, "dismantled")),
                "分解 " + it.data.shortLabel() + " → 胚料×" + blanks);
        return true;
    }

    /** B2.137: true when no other inventory slot holds a valid P1 blade. */
    private boolean onlyBlade(Player p, int except) {
        ItemStack[] all = p.getInventory().getContents();
        for (int i = 0; i < all.length; i++) {
            if (i == except || all[i] == null || !items().hasData(all[i])) continue;
            EmberItems.Read r = items().read(all[i]);
            if (r != null && r.data != null && r.data.isBlade()) return false;
        }
        return true;
    }

    /** uid → {original stack, blanks}: returned on failure, blanks given on success */
    private final Map<String, Object[]> pendingDismantle = new HashMap<String, Object[]>();

    private boolean swap(Player p, String target, boolean go, String rid) {
        PlayerInventory inv = p.getInventory();
        Slot a = held(p);
        if (a.error != null) { p.sendMessage(P + ChatColor.RED + "主手: " + a.error); return true; }
        Slot b = null;
        if (target != null) {
            ItemStack[] all = inv.getContents();
            for (int i = 0; i < all.length; i++) {
                if (i == inv.getHeldItemSlot() || all[i] == null || !items().hasData(all[i])) continue;
                EmberItems.Read r = items().read(all[i]);
                if (r != null && r.data != null && r.data.uid != null && r.data.uid.startsWith(target)) {
                    if (b != null) { p.sendMessage(P + ChatColor.RED + "uid 前缀 " + target + " 匹配多件，请写长一些"); return true; }
                    b = check(p, i, all[i]);
                }
            }
            if (b == null) { p.sendMessage(P + ChatColor.RED + "背包里没有 uid 以 " + target + " 开头的 P1 装备"); return true; }
        } else {
            int off = offhandIndex(inv);
            b = check(p, off, inv.getItemInOffHand());
        }
        if (b.error != null) { p.sendMessage(P + ChatColor.RED + "对方: " + b.error); return true; }
        EmberUpgradeRules.SwapPlan plan = EmberUpgradeRules.swap(a.data, b.data);
        if (!plan.ok()) { p.sendMessage(P + ChatColor.RED + plan.error); return true; }
        if (!go) {
            p.sendMessage(P + "强化轨道互换（免费，确定）:");
            p.sendMessage(P + "  " + a.data.shortLabel() + "  →  +" + plan.a.enhance + "（失败计数 " + plan.a.pity + "）");
            p.sendMessage(P + "  " + b.data.shortLabel() + "  →  +" + plan.b.enhance + "（失败计数 " + plan.b.pity + "）");
            p.sendMessage(P + ChatColor.YELLOW + "两件都会绑定；成色/精工/家族/阶级不变。执行: /corerpg p1 swap " + (target == null ? "" : target + " ") + "confirm");
            return true;
        }
        String first = a.data.uid.compareTo(b.data.uid) < 0 ? a.data.uid + ":" + a.data.rev + ":" + b.data.uid + ":" + b.data.rev
                : b.data.uid + ":" + b.data.rev + ":" + a.data.uid + ":" + a.data.rev;
        String r = rid != null ? rid : "swap:" + UUID.nameUUIDFromBytes(first.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (replayed(p, r)) return true;
        commit(p, "swap", r, Cost.NONE, Arrays.asList(new TxnItem(a.data, plan.a, null), new TxnItem(b.data, plan.b, null)),
                "互换强化轨道: " + a.data.uid.substring(0, 8) + " +" + a.data.enhance + "→+" + plan.a.enhance + " · "
                        + b.data.uid.substring(0, 8) + " +" + b.data.enhance + "→+" + plan.b.enhance + "（均已绑定）");
        return true;
    }

    private static int offhandIndex(PlayerInventory inv) { return 40; } // 1.12 contents: 0–35 main, 36–39 armor, 40 offhand

    private boolean replayed(Player p, String rid) {
        String note = done.get(rid);
        if (note == null) return false;
        p.sendMessage(P + ChatColor.YELLOW + "请求 " + rid + " 已处理过，不再执行: " + note);
        return true;
    }

    // ================================================================== commit

    private void commit(final Player p, final String kind, final String rid, final Cost cost, final List<TxnItem> list,
                        final String note) {
        final UUID id = p.getUniqueId();
        // pre-build the new stacks: if a template is missing nothing has been written yet
        final Map<String, ItemStack> built = new HashMap<String, ItemStack>();
        for (TxnItem t : list) {
            if (t.after == null) continue;
            ItemStack st = items().create(t.after);
            if (st == null) {
                refund(id, cost);
                restoreDismantle(p, list);
                p.sendMessage(P + ChatColor.RED + "无法生成新物品（NI 模板 " + t.after.ni + " 缺失或签名密钥不可用），已退回");
                return;
            }
            built.put(t.after.uid, st);
        }
        for (TxnItem t : list) busy.add(t.before.uid);
        java.util.function.Consumer<TxnResult> finish = res -> {
            for (TxnItem t : list) busy.remove(t.before.uid);
            Player q = Bukkit.getPlayer(id);
            if (res.status == TxnStatus.OK) {
                done.put(rid, note);
                for (TxnItem t : list) {
                    loadouts.rememberRow(t.before.uid, id, t.before.rev + 1, t.after == null ? t.retireState : "active");
                    if (t.after != null && q != null && !replace(q, t.before.uid, built.get(t.after.uid))) {
                        q.sendMessage(P + ChatColor.YELLOW + "物品已不在背包，DB 已更新；放回背包后执行 /corerpg p1 sync");
                    }
                }
                if ("dismantle".equals(kind)) {
                    Object[] pd = pendingDismantle.remove(list.get(0).before.uid);
                    int blanks = pd == null ? 0 : (Integer) pd[1];
                    if (q != null) ni().giveNiItem(q, EmberUpgradeRules.MAT_BLANK, blanks);
                    else plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] dismantle blanks for offline " + id + " not given: " + blanks);
                }
                plugin.getLogger().info("[" + EmberMode.MODE_ID + "] forge " + kind + " " + rid + " " + id + " ok: " + note + " cost " + cost.json());
                if (q != null) {
                    q.sendMessage(P + ChatColor.GREEN + note);
                    for (TxnItem t : list) if (t.after != null) q.sendMessage(P + "现在: " + preview(t.after));
                    q.playSound(q.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.2f);
                    loadouts.refresh(q);
                }
            } else {
                refund(id, cost);
                if (q != null) restoreDismantle(q, list); else pendingDismantle.remove(list.get(0).before.uid);
                if (res.status == TxnStatus.REPLAY) {
                    done.put(rid, res.detail == null ? "(原结果)" : res.detail);
                    if (q != null) q.sendMessage(P + ChatColor.YELLOW + "请求 " + rid + " 已处理过（重放），未再次执行，材料已退回: " + res.detail);
                } else if (q != null) {
                    q.sendMessage(P + ChatColor.RED + "未完成（" + res.status + ": " + res.detail + "），材料与金币已退回、保底未变");
                }
            }
        };
        if (store().usable()) store().commitTxn(rid, kind, id, list, cost.json(), note, finish);
        else finish.accept(new TxnResult(TxnStatus.OK, null)); // YAML storage: signed NBT is the only record
    }

    private void restoreDismantle(Player p, List<TxnItem> list) {
        Object[] pd = pendingDismantle.remove(list.get(0).before.uid);
        if (pd == null || p == null) return;
        Map<Integer, ItemStack> left = p.getInventory().addItem((ItemStack) pd[0]);
        for (ItemStack s : left.values()) p.getWorld().dropItem(p.getLocation(), s);
    }

    /** Replaces the stack carrying {@code uid} (wherever it moved in the inventory). */
    private boolean replace(Player p, String uid, ItemStack fresh) {
        if (fresh == null) return false;
        PlayerInventory inv = p.getInventory();
        ItemStack[] all = inv.getContents();
        for (int i = 0; i < all.length; i++) {
            if (all[i] == null || !items().hasData(all[i])) continue;
            Map<String, Object> m = NmsNbt.read(all[i], EmberItemData.NBT_KEY);
            if (m != null && uid.equals(String.valueOf(m.get("uid")))) {
                inv.setItem(i, fresh.clone());
                p.updateInventory();
                return true;
            }
        }
        return false;
    }

    // ================================================================== DB → NBT resync

    /** Rewrites inventory P1 stacks whose NBT rev is behind the DB row (DB is authoritative). */
    public void resync(final Player p, final boolean verbose) {
        if (!store().usable()) { if (verbose) p.sendMessage(P + "非 MySQL 存储：NBT 即唯一记录，无需同步"); return; }
        ItemStack[] all = p.getInventory().getContents();
        int n = 0;
        for (ItemStack st : all) {
            if (st == null || !items().hasData(st)) continue;
            EmberItems.Read r = items().read(st);
            if (r == null || r.data == null || r.data.uid == null || r.data.validate() != null) continue;
            final EmberItemData nbt = r.data;
            final boolean sigOk = r.ok() || (r.problem != null && !r.problem.contains("signature"));
            n++;
            store().lookupFull(nbt.uid, row -> {
                if (!p.isOnline() || row == null) return;
                if (!p.getUniqueId().toString().equals(row.owner)) return;
                if (!"active".equals(row.state)) {
                    if (verbose) p.sendMessage(P + ChatColor.RED + nbt.uid.substring(0, 8) + " 在 DB 中状态为 " + row.state + "，不计入战斗");
                    return;
                }
                if (row.data.rev > nbt.rev && sigOk && row.data.validate() == null) {
                    ItemStack fresh = items().create(row.data);
                    if (replace(p, nbt.uid, fresh)) {
                        loadouts.rememberRow(nbt.uid, p.getUniqueId(), row.data.rev, "active");
                        p.sendMessage(P + "已按 DB 同步 " + row.data.shortLabel() + "（rev " + nbt.rev + " → " + row.data.rev + "）");
                        loadouts.refresh(p);
                    }
                } else {
                    loadouts.rememberRow(nbt.uid, p.getUniqueId(), row.data.rev, row.state);
                }
            });
        }
        if (verbose) p.sendMessage(P + "正在核对 " + n + " 件 P1 装备…");
    }

    // ================================================================== admin flag stub

    private boolean flag(CommandSender s, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length < 4) { s.sendMessage(P + "/corerpg p1 flag <玩家> <q04|q07> [clear] — 首通标记（Q04/Q07 接线前的桩）"); return true; }
        Player t = Bukkit.getPlayerExact(args[2]);
        String f = args[3].toLowerCase(Locale.ROOT);
        if (t == null || !("q04".equals(f) || "q07".equals(f))) { s.sendMessage(P + "玩家需在线，标记只能是 q04/q07"); return true; }
        PlayerData pd = plugin.getDataStore().get(t.getUniqueId());
        if (pd == null) { s.sendMessage(P + "玩家数据未加载"); return true; }
        int cur = pd.periodCount(FLAG_PREFIX + f, "all");
        boolean clear = args.length >= 5 && "clear".equalsIgnoreCase(args[4]);
        if (clear && cur > 0) pd.addPeriodCount(FLAG_PREFIX + f, "all", -cur);
        if (!clear && cur == 0) pd.addPeriodCount(FLAG_PREFIX + f, "all", 1);
        s.sendMessage(P + t.getName() + " 首通 " + f.toUpperCase(Locale.ROOT) + " = " + (pd.periodCount(FLAG_PREFIX + f, "all") > 0));
        return true;
    }
}
