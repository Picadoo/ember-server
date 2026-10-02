package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

/**
 * {@code /corerpg p1 ...} — ember-v1.0-P1 admin/test commands.
 * Everything except {@code status} needs corerpg.admin.
 */
public final class EmberCommand {

    private final EmberMode mode;
    private final EmberDamageTrace trace;
    private EmberLoadoutService loadouts;

    private EmberSetService sets;

    public void setLoadouts(EmberLoadoutService l) { this.loadouts = l; }
    public void setSets(EmberSetService v) { this.sets = v; }

    private EmberRunService runs;
    public void setRuns(EmberRunService v) { this.runs = v; }

    private EmberSupplyService supplies;
    public void setSupplies(EmberSupplyService v) { this.supplies = v; }

    private EmberForgeService forge;
    public void setForge(EmberForgeService v) { this.forge = v; }
    private static final java.util.Set<String> FORGE_OPS = new java.util.HashSet<String>(java.util.Arrays.asList(
            "enhance", "swap", "upgrade", "refine", "quality", "dismantle", "sync", "flag"));

    public EmberCommand(EmberMode mode, EmberDamageTrace trace) {
        this.mode = mode;
        this.trace = trace;
    }

    private static final String P = ChatColor.GOLD + "[P1] " + ChatColor.GRAY;

    public boolean cmd(CommandSender s, String[] args) {
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "status";
        if ("status".equals(sub)) return status(s);
        if ("charm".equals(sub)) return charm(s, args);
        if ("inspect".equals(sub)) return inspect(s);
        if ("codex".equals(sub) || "图录".equals(sub)) {
            if (!(s instanceof Player) || runs == null) { s.sendMessage(P + "仅玩家可用"); return true; }
            return runs.codexCommand((Player) s, args);
        }
        if ("shop".equals(sub) || "补给".equals(sub)) {
            if (supplies == null) { s.sendMessage(P + "补给服务未加载"); return true; }
            return supplies.cmd(s, args);
        }
        if ("audit".equals(sub)) {
            if (loadouts == null) { s.sendMessage(P + "装备服务未加载"); return true; }
            return new EmberAudit(loadouts).cmd(s, args); // B2.170, checks corerpg.admin itself
        }
        if (EmberRunService.OPS.contains(sub)) {
            if (runs == null) { s.sendMessage(P + "主线本服务未加载"); return true; }
            return runs.cmd(s, sub, args); // admin sub-ops check corerpg.admin themselves
        }
        if (FORGE_OPS.contains(sub)) {
            if (forge == null) { s.sendMessage(P + "锻造服务未加载"); return true; }
            return forge.cmd(s, sub, args); // flag checks corerpg.admin itself
        }
        if (!s.hasPermission("corerpg.admin")) {
            s.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if ("mapbuild".equals(sub)) { // D15 / B2.151: build a book white-box template (q05 = hand-checked spire v1)
            String k = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
            org.bukkit.plugin.Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
            Map<String, Object> m = town.sunshine.corerpg.p1.map.P1MapLayout.bookMap(k);
            if (m == null) { s.sendMessage(P + "/corerpg p1 mapbuild <q01..q07>"); return true; }
            town.sunshine.corerpg.p1.map.P1MapLayout l = "q05".equals(k) ? town.sunshine.corerpg.p1.map.P1MapLayout.spireV1()
                    : town.sunshine.corerpg.p1.map.P1MapLayout.fromBook(m);
            new town.sunshine.corerpg.p1.map.P1MapBuilder(pl).build(s, String.valueOf(m.get("template")), String.valueOf(m.get("base")), l);
            return true;
        }
        if ("enable".equals(sub) || "on".equals(sub)) {
            mode.setRuntimeEnabled(Boolean.TRUE);
            if (loadouts != null && mode.isActive()) { loadouts.store().ensureSchema(); loadouts.ensureLoadedAll(); }
            s.sendMessage(P + "运行期开启 " + EmberMode.MODE_ID + "（仅 scope 内世界生效，重启后恢复配置值）"
                    + (mode.isBlocked() ? ChatColor.RED + " 但已被阻止: " + mode.getBlockedReason() : ""));
            return true;
        }
        if ("disable".equals(sub) || "off".equals(sub)) {
            mode.setRuntimeEnabled(Boolean.FALSE);
            s.sendMessage(P + "运行期关闭 " + EmberMode.MODE_ID + "：所有世界回到旧模式（下一秒刷新属性）");
            return true;
        }
        if ("follow".equals(sub)) {
            mode.setRuntimeEnabled(null);
            s.sendMessage(P + "取消运行期覆盖，跟随 ember-v1.yml enabled=" + mode.isConfigEnabled());
            return true;
        }
        if ("world".equals(sub)) return world(s, args);
        if ("debug".equals(sub)) return debug(s, args);
        if ("calc".equals(sub)) return calc(s, args);
        if ("give".equals(sub)) return give(s, args);
        if ("heal".equals(sub)) { // admin: fill HP through the ledger (other heals are reverted in P1 worlds)
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            double got = EmberHeal.full(t, "管理员 /corerpg p1 heal");
            s.sendMessage(P + String.format(Locale.ROOT, "%s +%.2f → %.2f / %.2f", t.getName(), got, t.getHealth(), EmberHeal.maxHp(t)));
            return true;
        }
        return help(s);
    }

    public boolean help(CommandSender s) {
        s.sendMessage(P + "/corerpg p1 status | enable | disable | follow");
        s.sendMessage(P + "/corerpg p1 world add|remove [世界] | world list");
        s.sendMessage(P + "/corerpg p1 debug [all|console|off]  — 每击伤害来源日志");
        s.sendMessage(P + "/corerpg p1 give <scorch|burst|sustain|t0> <blade|charm> <阶0-3> [成色0-3] [精工0-3] [强化0-10] [玩家]");
        s.sendMessage(P + "/corerpg p1 heal [玩家]  — 经账本回满（P1 世界内其它直接改血会被回退）");
        s.sendMessage(P + "/corerpg p1 charm select|clear  ·  /corerpg p1 inspect  — 手持物品身份/校验");
        EmberForgeService.helpLines(s);
        EmberRunService.helpLines(s);
        s.sendMessage(P + "/corerpg p1 flag <玩家> <q04|q07> [clear]  — 首通标记桩（升阶条件）");
        s.sendMessage(P + "/corerpg p1 calc <武阶> <武成色> <武精工> <武强化> <符阶> <符成色> <符精工> <符强化> <等级> [sustain]");
        return true;
    }

    private boolean status(CommandSender s) {
        s.sendMessage(P + EmberMode.MODE_ID + " active=" + mode.isActive() + " (config=" + mode.isConfigEnabled()
                + ", runtime=" + (mode.getRuntimeOverride() == null ? "follow" : mode.getRuntimeOverride()) + ")"
                + (mode.isBlocked() ? ChatColor.RED + " BLOCKED: " + mode.getBlockedReason() : ""));
        for (String line : mode.describeScope()) s.sendMessage(P + line);
        if (s instanceof Player) {
            Player p = (Player) s;
            s.sendMessage(P + "当前世界 " + p.getWorld().getName() + " → " + (EmberMode.isP1(p) ? ChatColor.GOLD + "P1 新模式" : "旧模式"));
            if (trace != null) s.sendMessage(P + "debug " + trace.describe(p.getUniqueId()));
            if (loadouts != null) {
                EmberLoadout l = loadouts.refresh(p);
                s.sendMessage(P + String.format(Locale.ROOT, "B=%.2f H=%.2f (H0 %.2f) D=%.0f M=%.4f EHP=%.1f  Lv%d  套装: %s",
                        l.b, l.h, l.h0, l.d, l.m, l.ehp(), l.level, l.setLabel()));
                s.sendMessage(P + l.nextAwakeningHint());
                s.sendMessage(P + l.setProgress());
                s.sendMessage(P + String.format(Locale.ROOT, "生命 %.2f / %.2f", p.getHealth(), EmberHeal.maxHp(p)));
                s.sendMessage(P + "主手: " + (l.blade == null ? "无有效 P1 刃" : l.blade.shortLabel())
                        + "  护符: " + (l.charm == null ? "未选定/无效" : l.charm.shortLabel()));
                for (String n : loadouts.notes(p)) s.sendMessage(P + ChatColor.YELLOW + n);
            }
            if (sets != null) s.sendMessage(P + sets.describe(p));
        }
        if (s.hasPermission("corerpg.admin")) s.sendMessage(P + "tables " + EmberMode.tables());
        return true;
    }

    private boolean world(CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "list";
        if ("list".equals(op)) {
            for (String line : mode.describeScope()) s.sendMessage(P + line);
            for (World w : Bukkit.getWorlds()) {
                if (mode.worldInScope(w.getName())) s.sendMessage(P + "  ✔ " + w.getName() + " (" + w.getPlayers().size() + " 人)");
            }
            return true;
        }
        String name = args.length >= 4 ? args[3] : (s instanceof Player ? ((Player) s).getWorld().getName() : null);
        if (name == null) { s.sendMessage(P + "控制台需指定世界名"); return true; }
        if ("add".equals(op)) {
            mode.addWorld(name);
            s.sendMessage(P + "运行期加入 P1 世界: " + name + (mode.isActive() ? "" : ChatColor.YELLOW + "（总开关未开启：/corerpg p1 enable）"));
            return true;
        }
        if ("remove".equals(op) || "del".equals(op)) {
            mode.removeWorld(name);
            s.sendMessage(P + "运行期移出 P1 世界: " + name);
            return true;
        }
        return help(s);
    }

    private boolean debug(CommandSender s, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "self";
        if ("console".equals(op)) {
            trace.setConsole(!trace.isConsole());
            s.sendMessage(P + "控制台伤害日志: " + trace.isConsole() + "（仅 P1 世界）");
            return true;
        }
        if (!(s instanceof Player)) { s.sendMessage(P + "控制台只能用 debug console"); return true; }
        Player p = (Player) s;
        if ("off".equals(op)) { trace.off(p.getUniqueId()); s.sendMessage(P + "伤害日志: 关"); return true; }
        if ("all".equals(op)) {
            boolean on = trace.toggleAll(p.getUniqueId());
            s.sendMessage(P + "P1 世界全部命中日志: " + (on ? "开" : "关"));
            return true;
        }
        boolean on = trace.toggleSelf(p.getUniqueId());
        s.sendMessage(P + "与你相关的每击伤害日志: " + (on ? "开" : "关") + "（raw → 各修正 → final）");
        return true;
    }

    private boolean charm(CommandSender s, String[] args) {
        if (!(s instanceof Player) || loadouts == null) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "select";
        if ("clear".equals(op)) {
            loadouts.clearCharm(p);
            s.sendMessage(P + "已取消选定护符");
            return true;
        }
        String lastBlade = loadouts.state(p.getUniqueId()).mainhandUid; // B2.173: blade equipped before the charm was taken in hand
        String err = loadouts.selectCharm(p, p.getInventory().getItemInMainHand());
        if (err != null) { s.sendMessage(P + ChatColor.RED + err); return true; }
        EmberLoadout l = loadouts.get(p);
        Object[] pv = loadouts.previewWithBlade(p, lastBlade);
        EmberLoadout v = (EmberLoadout) pv[0];
        String set = v.blade == null ? "未成套（没有可配的刃）"
                : Boolean.TRUE.equals(pv[1]) ? v.setLabel() + "（配 " + v.blade.shortLabel() + "，手持该刃时生效）" : v.setLabel();
        s.sendMessage(P + "已选定护符 " + (l.charm == null ? "(暂未生效: " + String.join("; ", loadouts.notes(p)) + ")" : l.charm.shortLabel())
                + " · 套装: " + set);
        return true;
    }

    private boolean inspect(CommandSender s) {
        if (!(s instanceof Player) || loadouts == null) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        org.bukkit.inventory.ItemStack it = p.getInventory().getItemInMainHand();
        if (!NmsNbt.isReady()) { s.sendMessage(P + ChatColor.RED + "NBT 桥不可用: " + NmsNbt.error()); return true; }
        EmberItems.Read r = loadouts.items().read(it);
        if (r == null) { s.sendMessage(P + "手持物品没有 ember_v1 数据（旧物品在 P1 中不提供任何属性）"); return true; }
        EmberItemData d = r.data;
        // B2.179 §19.2: item card in book order, then the whole-loadout comparison (no single power score)
        EmberLoadout cur = loadouts.refresh(p); // fresh: the held slot may have just changed / a DB lookup may have landed
        boolean pending = String.join(";", loadouts.notes(p)).contains("查询中");
        for (String line : EmberCompare.card(EmberMode.tables(), d, cur.level)) s.sendMessage(P + line);
        if (!r.ok()) {
            s.sendMessage(P + ChatColor.RED + "这件物品校验失败，P1 中不生效：" + r.problem);
        } else if (d.isCharm()) {
            Object[] pv = loadouts.previewWithBlade(p, loadouts.state(p.getUniqueId()).mainhandUid);
            EmberLoadout before = (EmberLoadout) pv[0];
            if (before.charm != null && before.charm.uid.equals(d.uid)) {
                s.sendMessage(P + "§a这是你已选定的护符（生效中）");
            } else {
                EmberLoadout after = EmberLoadout.compute(EmberMode.tables(), before.blade, d, before.level);
                if (pending) s.sendMessage(P + "§e（正在向数据库校验，2 秒后再点一次数字更准）");
                s.sendMessage(P + "§e选定这件护符后" + (before.blade == null ? "（没有可配的刃）" : "（配 " + before.blade.shortLabel() + "）") + "：");
                for (String line : EmberCompare.diff(before, after)) s.sendMessage(P + "  " + line);
                s.sendMessage(P + "§8确定要换：手持它点装备页「已选护符」，或 /corerpg p1 charm select");
            }
        } else if (d.isBlade()) {
            boolean live = cur.blade != null && cur.blade.uid.equals(d.uid);
            s.sendMessage(P + (live ? "§a主手生效中" : pending ? "§e正在向数据库校验这件装备，2 秒后再点一次"
                    : "§c主手没有生效：" + String.join("; ", loadouts.notes(p))));
            s.sendMessage(P + "§7当前：B " + EmberCompare.n(cur.b) + " · 生命 " + EmberCompare.n(cur.h) + " · " + cur.setLabel()
                    + " §8· " + cur.nextAwakeningHint());
            s.sendMessage(P + "§8比较两把刃：切换快捷栏后再看一次（只有主手那把生效）");
        }
        s.sendMessage(P + "§8uid=" + d.uid + " ni=" + d.ni + " ver=" + d.version + " rev=" + d.rev);
        if (!s.hasPermission("corerpg.admin")) return true;
        s.sendMessage(P + "fam=" + d.family + " slot=" + d.slot + " T" + d.tier + " q=" + d.quality + " craft=" + d.craft
                + " +" + d.enhance + " pity=" + d.pity + " bound=" + d.bound + " src=" + d.source);
        s.sendMessage(P + "NBT 校验: " + (r.ok() ? ChatColor.GREEN + "OK" : ChatColor.RED + r.problem));
        EmberItemStore.Row row = loadouts.cachedRow(d.uid);
        s.sendMessage(P + "DB: " + (!loadouts.store().usable() ? "未用 MySQL（只校验签名 NBT）"
                : row == null ? "未缓存（加入/选定时会查询）" : "owner=" + row.owner + " rev=" + row.rev + " state=" + row.state));
        return true;
    }

    private boolean give(CommandSender s, String[] args) {
        if (loadouts == null) return true;
        if (args.length < 5) return help(s);
        String fam = args[2].toLowerCase(Locale.ROOT);
        String slot = args[3].toLowerCase(Locale.ROOT);
        int tier, q = 0, craft = 0, enh = 0;
        try {
            tier = Integer.parseInt(args[4]);
            if (args.length >= 6) q = Integer.parseInt(args[5]);
            if (args.length >= 7) craft = Integer.parseInt(args[6]);
            if (args.length >= 8) enh = Integer.parseInt(args[7]);
        } catch (NumberFormatException ex) { s.sendMessage(P + "阶/成色/精工/强化需为整数"); return true; }
        if ("t0".equals(fam)) { fam = "none"; tier = 0; }
        Player target = args.length >= 9 ? Bukkit.getPlayerExact(args[8]) : (s instanceof Player ? (Player) s : null);
        if (target == null) { s.sendMessage(P + "找不到目标玩家"); return true; }
        if (!NmsNbt.isReady()) { s.sendMessage(P + ChatColor.RED + "NBT 桥不可用: " + NmsNbt.error()); return true; }
        EmberItemData d = EmberItemData.create(fam, slot, tier, q, craft, enh, true, "admin");
        String bad = d.validate();
        if (bad != null) { s.sendMessage(P + ChatColor.RED + "参数无效: " + bad); return true; }
        org.bukkit.inventory.ItemStack item = loadouts.items().create(d);
        if (item == null) {
            s.sendMessage(P + ChatColor.RED + "生成失败：NI 模板 " + d.ni + " 未加载（plugins/NeigeItems/Items/ember-v1-gear.yml 需重启/ni reload 后生效）或签名密钥不可用");
            return true;
        }
        loadouts.remember(d, target.getUniqueId());
        java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = target.getInventory().addItem(item);
        for (org.bukkit.inventory.ItemStack drop : left.values()) target.getWorld().dropItemNaturally(target.getLocation(), drop);
        s.sendMessage(P + "已发放 " + d.shortLabel() + " uid=" + d.uid + " → " + target.getName()
                + (loadouts.store().usable() ? "（已写入 cr_p1_item）" : "（未用 MySQL：仅签名 NBT）"));
        return true;
    }

    private boolean calc(CommandSender s, String[] args) {
        if (args.length < 11) { return help(s); }
        try {
            int wt = Integer.parseInt(args[2]), wq = Integer.parseInt(args[3]), wf = Integer.parseInt(args[4]), we = Integer.parseInt(args[5]);
            int ct = Integer.parseInt(args[6]), cq = Integer.parseInt(args[7]), cf = Integer.parseInt(args[8]), ce = Integer.parseInt(args[9]);
            int lv = Integer.parseInt(args[10]);
            boolean sustain = args.length >= 12 && ("sustain".equalsIgnoreCase(args[11]) || "true".equalsIgnoreCase(args[11]));
            EmberTables t = EmberMode.tables();
            double b = EmberFormula.baseAttack(t, wt, wq, wf, we, lv);
            double h0 = EmberFormula.baseHp(t, ct, cq, cf, ce, lv);
            double h = EmberFormula.maxHp(t, h0, sustain);
            double d = EmberFormula.defense(t, ct);
            double m = EmberFormula.mitigation(t, d);
            s.sendMessage(P + String.format(Locale.ROOT, "B=%.4f  H0=%.4f  H=%.4f  D=%.1f  M=%.4f  EHP=%.4f",
                    b, h0, h, d, m, EmberFormula.ehp(h, m)));
            s.sendMessage(P + String.format(Locale.ROOT, "满蓄力 %.2f / 暴击 %.2f / 期望 %.2f / 烬斩 %.2f",
                    EmberFormula.melee(t, b, 1, false), EmberFormula.melee(t, b, 1, true),
                    EmberFormula.expectedFullSwing(t, b), EmberFormula.skill(t, b)));
        } catch (NumberFormatException ex) {
            s.sendMessage(P + "参数需为整数");
        }
        return true;
    }
}
