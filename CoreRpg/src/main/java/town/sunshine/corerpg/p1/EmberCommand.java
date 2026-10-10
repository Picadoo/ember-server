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

    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;

    public boolean cmd(CommandSender s, String[] args) {
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "status";
        if ("status".equals(sub)) return status(s);
        if ("charm".equals(sub)) return charm(s, args);
        if ("inspect".equals(sub)) return inspect(s);
        if ("codex".equals(sub) || "图录".equals(sub)) {
            if (!(s instanceof Player) || runs == null) { s.sendMessage(P + "仅玩家可用"); return true; }
            return runs.codexCommand((Player) s, args);
        }
        if ("top".equals(sub) || "排行".equals(sub)) { // P2-10 (D84)
            if (runs == null) { s.sendMessage(P + "主线本服务未加载"); return true; }
            return runs.topCommand(s);
        }
        if ("title".equals(sub) || "trail".equals(sub) || "称号".equals(sub)) { // P2-9 (D83)
            if (!(s instanceof Player) || runs == null || runs.cosmetics() == null) { s.sendMessage(P + "仅玩家可用"); return true; }
            return runs.cosmetics().command((Player) s, runs.dataOf(((Player) s).getUniqueId()), "trail".equals(sub) ? "trail" : "title", args);
        }
        if ("reroll".equals(sub) || "洗练".equals(sub)) { // D143 词条洗练
            if (!(s instanceof Player) || EmberGrowthService.get() == null) { s.sendMessage(P + "洗练服务未加载"); return true; }
            return EmberGrowthService.get().rerollCommand((Player) s, args);
        }
        if ("brand".equals(sub) || "烙纹".equals(sub)) { // D429 烙纹定向
            if (!(s instanceof Player) || EmberGrowthService.get() == null) { s.sendMessage(P + "烙纹服务未加载"); return true; }
            return EmberGrowthService.get().brandCommand((Player) s, args);
        }
        if ("honor".equals(sub) || "勋记".equals(sub)) { // D142 余烬勋记
            if (args.length >= 3 && "test".equalsIgnoreCase(args[2])) { // admin test hook (10-04): grant / clear honor conditions
                if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
                if (EmberGrowthService.get() == null) { s.sendMessage(P + "勋记服务未加载"); return true; }
                return EmberGrowthService.get().honorTest(s, args);
            }
            if (!(s instanceof Player) || EmberGrowthService.get() == null) { s.sendMessage(P + "勋记服务未加载"); return true; }
            return EmberGrowthService.get().honorCommand((Player) s, args);
        }
        if ("sign".equals(sub) || "签到".equals(sub) || "online".equals(sub) || "在线".equals(sub)) { // D180 每日签到 + 在线时长
            if (EmberSignService.get() == null) { s.sendMessage(P + "签到服务未加载"); return true; }
            return EmberSignService.get().cmd(s, args);
        }
        if ("sig".equals(sub) || "签名".equals(sub)) { // D174 签名传奇
            if (EmberGrowthService.get() == null) { s.sendMessage(P + "签名服务未加载"); return true; }
            return EmberGrowthService.get().sigCommand(s, args);
        }
        if ("spec".equals(sub) || "天赋".equals(sub)) { // D141 talent specialization
            if (!(s instanceof Player) || EmberGrowthService.get() == null) { s.sendMessage(P + "天赋服务未加载"); return true; }
            return EmberGrowthService.get().command((Player) s, args);
        }
        if ("cosmetic".equals(sub) || "外观".equals(sub)) { // D107 cosmetic shop
            if (!(s instanceof Player) || runs == null || runs.cosmetics() == null) { s.sendMessage(P + "外观商店未加载"); return true; }
            return runs.cosmetics().shop((Player) s, runs.dataOf(((Player) s).getUniqueId()), args);
        }
        if ("shop".equals(sub) || "补给".equals(sub)) {
            if (supplies == null) { s.sendMessage(P + "补给服务未加载"); return true; }
            return supplies.cmd(s, args);
        }
        if ("vault".equals(sub) || "仓库".equals(sub) || "gearlib".equals(sub) || "装备库".equals(sub) || "stash".equals(sub)
                || "undo".equals(sub) || "itemlog".equals(sub) || "deliver".equals(sub) || "fault".equals(sub)) { // 1.62 storage + data protection; D162 deliveries
            if (EmberGearLib.get() == null) { s.sendMessage(P + "仓库服务未加载"); return true; }
            return EmberGearLib.get().cmd(s, sub, args);
        }
        if ("armor".equals(sub) || "护甲".equals(sub)) { // D318 六槽护甲页（菜单代发；开关 gear.six_slot.enabled 默认关）
            if (EmberSixSlotService.get() == null) { s.sendMessage(P + "护甲服务未加载"); return true; }
            return EmberSixSlotService.get().cmd(s, args);
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
        if ("telemetry".equals(sub) || "遥测".equals(sub)) { // D298 playfeel telemetry OP read-only
            org.bukkit.plugin.Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) { s.sendMessage(P + "CoreRpg 未加载"); return true; }
            return EmberPlayfeelTelemetry.cmd(s, args, (town.sunshine.corerpg.CoreRpgPlugin) pl);
        }
        if ("debug".equals(sub)) return debug(s, args);
        if ("calc".equals(sub)) return calc(s, args);
        if ("give".equals(sub)) return give(s, args);
        if ("givedup".equals(sub)) return giveDup(s, args);
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
        if (s instanceof Player && !s.hasPermission("corerpg.admin")) { // D100: players get buttons, not a command list
            town.sunshine.corerpg.ConfirmTokens.sendButtons((Player) s, P + "常用入口：",
                    new String[]{"[主菜单]", "/ember", "打开主菜单", "GREEN"},
                    new String[]{"[冒险页]", "/ember_p1_adventure", "选本、选目标族、领首通", "AQUA"},
                    new String[]{"[装备页]", "/ember_p1_gear", "装备、属性、补给", "YELLOW"},
                    new String[]{"[帮助]", "/ember_help", "玩法说明", "GRAY"});
            return true;
        }
        s.sendMessage(P + "/corerpg p1 status | enable | disable | follow");
        s.sendMessage(P + "/corerpg p1 world add|remove [世界] | world list");
        s.sendMessage(P + "/corerpg p1 debug [all|console|off]  — 每击伤害来源日志");
        s.sendMessage(P + "/corerpg p1 telemetry [玩家|server]  — D298 玩法可感周摘要（只读）");
        s.sendMessage(P + "/corerpg p1 give <scorch|burst|sustain|t0> <blade|charm> <阶0-3> [成色0-3] [精工0-3] [强化0-10] [玩家]");
        s.sendMessage(P + "/corerpg p1 givedup <blade|charm> [玩家] [成色0-3]  — 洗练测试：发一件和正在用的那件同族同部位同阶的重复件（src=admin：可当洗练重复件，不可分解、不计图录）");
        s.sendMessage(P + "/corerpg p1 honor test <勋记id|条件|all|clear|show> [玩家]  — 勋记测试：直接满足条件（会发解锁提示）/ 清掉");
        s.sendMessage(P + "/corerpg p1 heal [玩家]  — 经账本回满（P1 世界内其它直接改血会被回退）");
        s.sendMessage(P + "/corerpg p1 charm select|clear  ·  /corerpg p1 inspect  — 手持物品身份/校验");
        EmberForgeService.helpLines(s);
        EmberRunService.helpLines(s);
        s.sendMessage(P + "/corerpg p1 flag <玩家> <q04|q07> [clear]  — 首通标记桩（升阶条件）");
        s.sendMessage(P + "/corerpg p1 calc <武阶> <武成色> <武精工> <武强化> <符阶> <符成色> <符精工> <符强化> <等级> [sustain]");
        return true;
    }

    private boolean status(CommandSender s) {
        boolean admin = !(s instanceof Player) || s.hasPermission("corerpg.admin");
        if (admin) s.sendMessage(P + EmberMode.MODE_ID + " active=" + mode.isActive() + " (config=" + mode.isConfigEnabled()
                + ", runtime=" + (mode.getRuntimeOverride() == null ? "follow" : mode.getRuntimeOverride()) + ")"
                + (mode.isBlocked() ? ChatColor.RED + " BLOCKED: " + mode.getBlockedReason() : ""));
        if (admin) for (String line : mode.describeScope()) s.sendMessage(P + line);
        if (s instanceof Player) {
            Player p = (Player) s;
            if (admin) {
                s.sendMessage(P + "当前世界 " + p.getWorld().getName() + " → " + (EmberMode.isP1(p) ? ChatColor.GOLD + "P1 新模式" : "旧模式"));
                if (trace != null) s.sendMessage(P + "debug " + trace.describe(p.getUniqueId()));
            }
            if (loadouts != null) {
                EmberLoadout l = loadouts.refresh(p);
                if (admin) s.sendMessage(P + String.format(Locale.ROOT, "B=%.2f H=%.2f (H0 %.2f) D=%.0f M=%.4f EHP=%.1f  Lv%d  套装: %s",
                        l.b, l.h, l.h0, l.d, l.m, l.ehp(), l.level, l.setLabel()));
                else s.sendMessage(P + String.format(Locale.ROOT, "攻击 %.0f · 生命 %.0f · 防御 %.0f · 余烬等级 %d · 套装：%s",
                        l.b, l.h, l.d, l.level, l.setLabel()));
                s.sendMessage(P + l.nextAwakeningHint());
                if (runs != null) runs.routeCommand(p); // D120: cheapest real route + buttons
                s.sendMessage(P + l.setProgress());
                if (admin) s.sendMessage(P + String.format(Locale.ROOT, "生命 %.2f / %.2f", p.getHealth(), EmberHeal.maxHp(p)));
                s.sendMessage(P + "主手: " + (l.blade == null ? "没有有效的余烬刃" : l.blade.shortLabel())
                        + "  护符: " + (l.charm == null ? "未选定/无效" : l.charm.shortLabel()));
                EmberFestival fest = EmberFestival.get(); // D139 festival charm slot
                if (fest != null && runs != null && fest.worn(runs.dataOf(p.getUniqueId())))
                    s.sendMessage(P + "活动护符: §c" + fest.charmName + "§7（佩戴中" + (l.festHp > 0 || l.festDef > 0
                            ? String.format(Locale.ROOT, " · 生命 +%.0f 防御 +%.0f", l.festHp, l.festDef) : "") + "）· 烟火迸发");
                EmberItemData best = null; // P2 draft §2 low-priority item: show the player's best 成色 (display only)
                for (org.bukkit.inventory.ItemStack it : p.getInventory().getContents()) {
                    if (it == null || !loadouts.items().hasData(it)) continue;
                    EmberItems.Read r = loadouts.items().read(it);
                    if (r == null || !r.ok() || r.data == null || r.data.tier == 0) continue;
                    if (best == null || r.data.quality > best.quality || (r.data.quality == best.quality && r.data.tier > best.tier)) best = r.data;
                }
                if (best != null) s.sendMessage(P + "§7背包里最好成色：§f" + EmberItemData.qualityName(best.quality) + "§7（T" + best.tier + " "
                        + EmberItemData.familyName(best.family) + EmberItemData.slotName(best.slot) + "）§8· 极品只从掉落获得，挑战版和深渊高层更容易");
                for (String n : loadouts.notes(p)) s.sendMessage(P + ChatColor.YELLOW + n);
                // D99: the formulas moved here from the gear page lore (only shown when the player asks for details)
                s.sendMessage(P + "§8攻击 = 刃基础攻击 ×（1 + 强化 + 成色 + 精工）+ 0.2 ×（余烬等级 − 10）");
                s.sendMessage(P + "§8生命 = 20 + 护符生命 ×（1 + 强化 + 成色 + 精工）+（余烬等级 − 10）");
                s.sendMessage(P + "§8承伤倍率 = max(0.5, 40 ÷ (40 + 防御)) · 普攻 10% 暴击 ×1.5 · 烬斩 1.5 倍攻击、不暴击");
            }
            if (sets != null && admin) s.sendMessage(P + sets.describe(p));
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
        if (s.hasPermission("corerpg.admin")) // D245: provenance for OPs only (never in lore)
            s.sendMessage(P + "§8来源记录：" + (d.origin.present() ? d.origin.map + " · " + d.origin.src + " · " + d.origin.run + " · " + d.origin.at : "无（1.65.70 之前的件）"));
        if (!r.ok()) {
            s.sendMessage(P + ChatColor.RED + "这件物品校验失败，P1 中不生效：" + r.problem);
        } else if (d.isCharm()) {
            Object[] pv = loadouts.previewWithBlade(p, loadouts.state(p.getUniqueId()).mainhandUid);
            EmberLoadout before = (EmberLoadout) pv[0];
            if (before.charm != null && before.charm.uid.equals(d.uid)) {
                s.sendMessage(P + "§a这是你已选定的护符（生效中）");
            } else {
                EmberLoadout after = EmberLoadout.compute(EmberMode.tables(), before.blade, d, before.level, before.festHp, before.festDef);
                if (pending) s.sendMessage(P + "§e（正在向数据库校验，2 秒后再点一次数字更准）");
                s.sendMessage(P + "§e选定这件护符后" + (before.blade == null ? "（没有可配的刃）" : "（配 " + before.blade.shortLabel() + "）") + "：");
                for (String line : EmberCompare.diff(before, after)) s.sendMessage(P + "  " + line);
                s.sendMessage(P + "§8确定要换：手持它点装备页「已选护符」");
            }
        } else if (d.isBlade()) {
            boolean live = cur.blade != null && cur.blade.uid.equals(d.uid);
            boolean admin = s.hasPermission("corerpg.admin");
            s.sendMessage(P + (live ? "§a主手生效中" : pending ? "§e正在核对这件装备，2 秒后再点一次"
                    : "§c这把没有生效" + (admin ? "：" + String.join("; ", loadouts.notes(p)) : "（要拿在主手；同一件只能有一份）")));
            s.sendMessage(P + "§7当前：攻击 " + EmberCompare.n(cur.b) + " · 生命 " + EmberCompare.n(cur.h) + " · " + cur.setLabel()
                    + " §8· " + cur.nextAwakeningHint());
            s.sendMessage(P + "§8比较两把刃：切换快捷栏后再看一次（只有主手那把生效）");
        }
        if (!s.hasPermission("corerpg.admin")) return true; // D101: ids are for admins only
        s.sendMessage(P + "§8uid=" + d.uid + " ni=" + d.ni + " ver=" + d.version + " rev=" + d.rev);
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
        EmberItemData d = EmberItemData.create(fam, slot, tier, q, craft, enh, true, "admin")
                .withOrigin(EmberProvenance.forAdmin("give", System.currentTimeMillis())); // D245
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

    /**
     * Admin (10-04): a duplicate of the target's equipped blade / selected charm for D143 reroll tests — same family,
     * slot and tier, no enhance / craft. D243 (G4): source=admin (was drop): not dismantlable, no codex entry; still a
     * valid reroll duplicate (EmberAffix.duplicateOk accepts drop or admin).
     * The optional quality is for making a test target, not a duplicate (a duplicate must stay 标准).
     */
    private boolean giveDup(CommandSender s, String[] args) {
        if (loadouts == null) return true;
        String slot = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (!"blade".equals(slot) && !"charm".equals(slot)) { s.sendMessage(P + "/corerpg p1 givedup <blade|charm> [玩家] [成色0-3]"); return true; }
        Player target = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : (s instanceof Player ? (Player) s : null);
        if (target == null) { s.sendMessage(P + "找不到目标玩家"); return true; }
        int q = 0;
        if (args.length >= 5) { try { q = Math.max(0, Math.min(3, Integer.parseInt(args[4]))); } catch (NumberFormatException e) { q = 0; } }
        if (!NmsNbt.isReady()) { s.sendMessage(P + ChatColor.RED + "NBT 桥不可用: " + NmsNbt.error()); return true; }
        EmberLoadout lo = loadouts.refresh(target);
        EmberItemData cur = "charm".equals(slot) ? lo.charm : lo.blade;
        if (cur == null || cur.tier < 1) { s.sendMessage(P + target.getName() + " 没有正在用的 T1+ " + ("charm".equals(slot) ? "护符" : "刃")); return true; }
        // D243 (G4): an OP test duplicate is source "admin" — never dismantlable, never a codex entry. EmberAffix.duplicateOk
        // still accepts it as the reroll duplicate (that is what this test command is for).
        EmberItemData d = EmberItemData.create(cur.family, cur.slot, cur.tier, q, 0, 0, true, "admin")
                .withOrigin(EmberProvenance.forAdmin("givedup", System.currentTimeMillis())); // D245
        String bad = d.validate();
        if (bad != null) { s.sendMessage(P + ChatColor.RED + "参数无效: " + bad); return true; }
        org.bukkit.inventory.ItemStack item = loadouts.items().create(d);
        if (item == null) { s.sendMessage(P + ChatColor.RED + "生成失败（NI 模板或签名密钥）"); return true; }
        loadouts.remember(d, target.getUniqueId());
        java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = target.getInventory().addItem(item);
        for (org.bukkit.inventory.ItemStack drop : left.values()) target.getWorld().dropItemNaturally(target.getLocation(), drop);
        s.sendMessage(P + "已发放重复件（测试，src=admin，不可分解）" + d.shortLabel() + " uid=" + d.uid + " → " + target.getName());
        Bukkit.getLogger().info("[P1] admin " + s.getName() + " givedup " + d.shortLabel() + " uid=" + d.uid + " q=" + q + " → " + target.getName());
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
