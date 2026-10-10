package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

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
        if ("convert".equals(sub) || "转化".equals(sub)) { // D431 每周转化
            if (!(s instanceof Player) || EmberGrowthService.get() == null) { s.sendMessage(P + "转化服务未加载"); return true; }
            return EmberGrowthService.get().convertCommand((Player) s, args);
        }
        if ("modepath".equals(sub) || "进阶路径".equals(sub)) { // D462 admin/smoke: force mode first-path pick
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            String map = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
            Player t = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if (!EmberModePath.isModeUnlockMap(map)) {
                s.sendMessage(P + "用法：/corerpg p1 modepath <q04|q05|q06> [玩家]");
                return true;
            }
            EmberModePath.forceOffer(t, map);
            s.sendMessage(P + "已向 " + t.getName() + " 弹出 " + map.toUpperCase(Locale.ROOT) + " 进阶路径点选");
            return true;
        }
        if ("convertpath".equals(sub) || "转化路径".equals(sub)) { // D469 convert destination-family path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            if (args.length < 3) {
                int sf = EmberSetFocus.get(d);
                p.sendMessage(P + EmberConvertPath.glance(EmberConvertPath.get(d),
                        EmberSetFocus.familyKey(sf), runs.target(d)));
                EmberConvertPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                String week = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Shanghai")));
                boolean offered = EmberConvertPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberConvertPath.get(d);
                    if (EmberConvertPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberConvertPath.label(cur) + " · 改路径再 /corerpg p1 convertpath");
                    else EmberConvertPath.offerPick(p);
                }
                return true;
            }
            if ("sync".equalsIgnoreCase(args[2]) || "对齐焦点".equals(args[2])) {
                int sf = EmberSetFocus.get(d);
                String fk = EmberSetFocus.familyKey(sf);
                if (fk == null) { p.sendMessage(P + "§c请先设套装焦点 /corerpg p1 setfocus"); return true; }
                int id = EmberConvertPath.parse(fk);
                EmberConvertPath.set(d, id);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                p.sendMessage(P + "§a转化路径已对齐套装焦点 → §f" + EmberConvertPath.label(id));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一步：",
                        "[去转化成" + EmberConvertPath.label(id) + "]", "/corerpg p1 convert " + fk,
                        "手持刃/护符后确认（周 1 次）");
                return true;
            }
            int id = EmberConvertPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 convertpath <scorch|burst|sustain|clear|sync>");
                return true;
            }
            boolean ch = EmberConvertPath.set(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberConvertPath.NONE) p.sendMessage(P + "已取消转化路径");
            else {
                String fk = EmberConvertPath.familyKey(id);
                int sf = EmberSetFocus.get(d);
                p.sendMessage(P + "§a转化路径 → §f" + EmberConvertPath.label(id) + "§7（" + EmberConvertPath.tip(id) + "）"
                        + (ch ? "" : " §8· 已是该路径"));
                p.sendMessage(P + EmberConvertPath.glance(id, EmberSetFocus.familyKey(sf), runs.target(d)));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一步：",
                        "[去转化成" + EmberConvertPath.label(id) + "]", "/corerpg p1 convert " + fk,
                        "手持刃/护符后确认（周 1 次）");
            }
            return true;
        }
        if ("echopath".equals(sub) || "残响路径".equals(sub)) { // D468 echo residual weekly path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberEchoPath.UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberEchoPath.glance(EmberEchoPath.get(d), unlocked));
                if (unlocked) EmberEchoPath.forceOffer(p);
                else p.sendMessage(P + "§7首通 Q04 后可选本周残响追哪只首领");
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c需本人首通 Q04（首领残响）"); return true; }
                String week = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Shanghai")));
                boolean offered = EmberEchoPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberEchoPath.get(d);
                    if (EmberEchoPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberEchoPath.label(cur) + " · 改路径再 /corerpg p1 echopath");
                    else EmberEchoPath.forceOffer(p);
                }
                return true;
            }
            int id = EmberEchoPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 echopath <q01..q07|clear>");
                return true;
            }
            if (id != EmberEchoPath.NONE && !unlocked) {
                p.sendMessage(P + "§c需本人首通 Q04 后才能选残响路径");
                return true;
            }
            boolean ch = EmberEchoPath.set(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberEchoPath.NONE) p.sendMessage(P + "已取消残响路径");
            else {
                p.sendMessage(P + "§a残响路径 → §f" + EmberEchoPath.label(id) + "§7（" + EmberEchoPath.tip(id) + "）"
                        + (ch ? "" : " §8· 已是该路径"));
                p.sendMessage(P + EmberEchoPath.glance(id, true));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一步：",
                        "[去残响·Q0" + id + "]", "/corerpg p1 rush " + EmberEchoPath.mapKey(id) + " go",
                        "不耗体力 · 周池有奖徽记");
            }
            return true;
        }
        if ("brandpath".equals(sub) || "烙纹路径".equals(sub)) { // D467 brand playstyle path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            if (args.length < 3) {
                p.sendMessage(P + EmberBrandPath.glance(EmberBrandPath.get(d)));
                EmberBrandPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                String week = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Shanghai")));
                boolean offered = EmberBrandPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBrandPath.get(d);
                    if (EmberBrandPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBrandPath.label(cur) + " · 改路径再 /corerpg p1 brandpath");
                    else EmberBrandPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBrandPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 brandpath <hunt|ember|bind|clear>");
                return true;
            }
            boolean ch = EmberBrandPath.set(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberBrandPath.NONE) p.sendMessage(P + "已取消烙纹路径");
            else {
                p.sendMessage(P + "§a烙纹路径 → §f" + EmberBrandPath.label(id) + "§7（" + EmberBrandPath.tip(id) + "）"
                        + (ch ? "" : " §8· 已是该路径"));
                p.sendMessage(P + EmberBrandPath.glance(id));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一步：",
                        EmberBrandPath.pinLabel(id), EmberBrandPath.pinCmd(id),
                        "预览定向（不扣料直到确认）");
            }
            return true;
        }
        if ("counterpath".equals(sub) || "破绽路径".equals(sub)) { // D480 counterplay combat path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            if (args.length < 3) {
                p.sendMessage(P + EmberCounterPath.glance(EmberCounterPath.get(d)));
                EmberCounterPath.offerPick(p);
                return true;
            }
            int id = EmberCounterPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 counterpath <wall|whiff|break|clear>");
                return true;
            }
            EmberCounterPath.applyAndReply(p, id);
            return true;
        }
        if ("codexseed".equals(sub)) { // D522 admin smoke: register 5 drop entries so stage-5 is claimable
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if (runs == null) { s.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            String[][] kinds = new String[][] {
                {"none","blade","0"}, {"none","charm","0"},
                {"scorch","blade","1"}, {"scorch","charm","1"}, {"burst","blade","1"}
            };
            int n = 0;
            for (String[] k : kinds) {
                if (EmberCodex.register(d, k[0], k[1], Integer.parseInt(k[2]), "drop")) n++;
            }
            runs.plugin().getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + "已为 " + t.getName() + " 登记图录 +" + n + "（现 " + EmberCodex.count(d) + "/20，可领 " + EmberCodex.claimable(d).size() + " 档）");
            EmberCodexPath.maybeAfterProgress(t, runs);
            return true;
        }
        if ("packpath".equals(sub) || "腾包路径".equals(sub)) { // D555 mid-run pack relief path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberPackPath.glance(EmberPackPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c腾包路径需本人首通 Q01。");
                    return true;
                }
                EmberPackPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c腾包路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPackPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPackPath.get(d);
                    if (EmberPackPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPackPath.label(cur) + " · 改路径再 /corerpg p1 packpath");
                    else EmberPackPath.offerPick(p);
                }
                return true;
            }
            int id = EmberPackPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 packpath <auto|ask|mute|clear>");
                return true;
            }
            EmberPackPath.applyAndReply(p, id);
            return true;
        }
        if ("pack".equals(sub) && args.length == 2) { // D555 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!EmberPackPath.pathPackClick(p)) p.sendMessage(P + "无法腾包（需在本内且背包有可存材料）");
            else p.sendMessage(P + "§a腾包·手点 §7已入库腾格");
            return true;
        }
        if ("flepath".equals(sub) || "fleepath".equals(sub) || "撤离路径".equals(sub)) { // D554 post-fall flee path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberFleePath.glance(EmberFleePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c撤离路径需本人首通 Q01。");
                    return true;
                }
                EmberFleePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c撤离路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFleePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFleePath.get(d);
                    if (EmberFleePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFleePath.label(cur) + " · 改路径再 /corerpg p1 flepath");
                    else EmberFleePath.offerPick(p);
                }
                return true;
            }
            int id = EmberFleePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 flepath <auto|ask|mute|clear>");
                return true;
            }
            EmberFleePath.applyAndReply(p, id);
            return true;
        }
        if ("flee".equals(sub) && args.length == 2) { // D554 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            if (!runs.pathFleeLeave(p)) p.sendMessage(P + "无法离本（非倒下/团本需双确认/未进本）");
            else p.sendMessage(P + "§a撤离·手点 §7已离本");
            return true;
        }
        if ("chestpath".equals(sub) || "开箱路径".equals(sub)) { // D553 extra chest open path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberChestPath.glance(EmberChestPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c开箱路径需本人首通 Q01。");
                    return true;
                }
                EmberChestPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c开箱路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberChestPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberChestPath.get(d);
                    if (EmberChestPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberChestPath.label(cur) + " · 改路径再 /corerpg p1 chestpath");
                    else EmberChestPath.offerPick(p);
                }
                return true;
            }
            int id = EmberChestPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 chestpath <auto|ask|mute|clear>");
                return true;
            }
            EmberChestPath.applyAndReply(p, id);
            return true;
        }
        if ("chestopen".equals(sub) && args.length == 2) { // D553 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            if (!runs.pathOpenExtraChest(p)) p.sendMessage(P + "无法开箱（无额外宝箱/已开/未进本）");
            else p.sendMessage(P + "§a开箱·手点 §7已开启");
            return true;
        }
        if ("sightpath".equals(sub) || "夜视路径".equals(sub)) { // D552 dungeon sight path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberSightPath.glance(EmberSightPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c夜视路径需本人首通 Q01。");
                    return true;
                }
                EmberSightPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c夜视路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberSightPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSightPath.get(d);
                    if (EmberSightPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberSightPath.label(cur) + " · 改路径再 /corerpg p1 sightpath");
                    else EmberSightPath.offerPick(p);
                }
                return true;
            }
            int id = EmberSightPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 sightpath <auto|ask|mute|clear>");
                return true;
            }
            EmberSightPath.applyAndReply(p, id);
            return true;
        }
        if ("sight".equals(sub) && args.length == 2) { // D552 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!EmberSightPath.pathApplySight(p)) p.sendMessage(P + "无法开夜视（已有/不在副本）");
            else p.sendMessage(P + "§a夜视·手点 §7已开启");
            return true;
        }
        if ("charmpath".equals(sub) || "护符路径".equals(sub)) { // D551 charm select path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberCharmPath.glance(EmberCharmPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c护符路径需本人首通 Q01。");
                    return true;
                }
                EmberCharmPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c护符路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberCharmPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberCharmPath.get(d);
                    if (EmberCharmPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberCharmPath.label(cur) + " · 改路径再 /corerpg p1 charmpath");
                    else EmberCharmPath.offerPick(p);
                }
                return true;
            }
            int id = EmberCharmPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 charmpath <auto|ask|mute|clear>");
                return true;
            }
            EmberCharmPath.applyAndReply(p, id);
            return true;
        }
        if ("charmpick".equals(sub) && args.length == 2) { // D551 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (loadouts == null) { p.sendMessage(P + "装配服务未加载"); return true; }
            if (!loadouts.pathSelectBestCharm(p)) p.sendMessage(P + "无法选符（已是最好/副本内）");
            else {
                EmberLoadout cur = loadouts.refresh(p);
                p.sendMessage(P + "§a护符·手点 §7已选定 §f" + (cur.charm == null ? "护符" : cur.charm.shortLabel()));
            }
            return true;
        }
        if ("grippath".equals(sub) || "握刃路径".equals(sub)) { // D550 combat grip path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberGripPath.glance(EmberGripPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c握刃路径需本人首通 Q01。");
                    return true;
                }
                EmberGripPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c握刃路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberGripPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberGripPath.get(d);
                    if (EmberGripPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberGripPath.label(cur) + " · 改路径再 /corerpg p1 grippath");
                    else EmberGripPath.offerPick(p);
                }
                return true;
            }
            int id = EmberGripPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 grippath <auto|ask|mute|clear>");
                return true;
            }
            EmberGripPath.applyAndReply(p, id);
            return true;
        }
        if ("grip".equals(sub) && args.length == 2) { // D550 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (loadouts == null) { p.sendMessage(P + "装配服务未加载"); return true; }
            if (!loadouts.pathGripBlade(p)) p.sendMessage(P + "无法握刃（已持刃/无刃/不在副本）");
            else p.sendMessage(P + "§a握刃·手点 §7已收回");
            return true;
        }
        if ("armorpath".equals(sub) || "护甲路径".equals(sub)) { // D549 six-slot armor equip path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberArmorPath.glance(EmberArmorPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c护甲路径需本人首通 Q01。");
                    return true;
                }
                EmberArmorPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c护甲路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberArmorPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberArmorPath.get(d);
                    if (EmberArmorPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberArmorPath.label(cur) + " · 改路径再 /corerpg p1 armorpath");
                    else EmberArmorPath.offerPick(p);
                }
                return true;
            }
            int id = EmberArmorPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 armorpath <auto|ask|mute|clear>");
                return true;
            }
            EmberArmorPath.applyAndReply(p, id);
            return true;
        }
        if ("armorwear".equals(sub) && args.length == 2) { // D549 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            EmberSixSlotService six = EmberSixSlotService.get();
            if (six == null) { p.sendMessage(P + "护甲服务未加载"); return true; }
            int n = six.pathEquipAll(p);
            if (n <= 0) p.sendMessage(P + "无法换甲（已是最好/副本内/六槽关）");
            else p.sendMessage(P + "§a护甲·手点 §7已换上 ×" + n);
            return true;
        }
        if ("sippath".equals(sub) || "喝药路径".equals(sub)) { // D548 combat sip path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberSipPath.glance(EmberSipPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c喝药路径需本人首通 Q01。");
                    return true;
                }
                EmberSipPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c喝药路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberSipPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSipPath.get(d);
                    if (EmberSipPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberSipPath.label(cur) + " · 改路径再 /corerpg p1 sippath");
                    else EmberSipPath.offerPick(p);
                }
                return true;
            }
            int id = EmberSipPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 sippath <auto|ask|mute|clear>");
                return true;
            }
            EmberSipPath.applyAndReply(p, id);
            return true;
        }
        if ("sip".equals(sub) && args.length == 2) { // D548 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            EmberSupplyService sup = runs == null ? null : runs.plugin().getEmberSupplies();
            if (sup == null) { p.sendMessage(P + "补给服务未加载"); return true; }
            if (!sup.pathSipOne(p)) p.sendMessage(P + "无法喝药（生命够/无药/冷却中/不在副本）");
            else p.sendMessage(P + "§a喝药·手点 §7已喝");
            return true;
        }
        if ("breadpath".equals(sub) || "面包路径".equals(sub)) { // D547 hub bread buy path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberBreadPath.glance(EmberBreadPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c面包路径需本人首通 Q01。");
                    return true;
                }
                EmberBreadPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c面包路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBreadPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBreadPath.get(d);
                    if (EmberBreadPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBreadPath.label(cur) + " · 改路径再 /corerpg p1 breadpath");
                    else EmberBreadPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBreadPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 breadpath <auto|ask|mute|clear>");
                return true;
            }
            EmberBreadPath.applyAndReply(p, id);
            return true;
        }
        if ("bread".equals(sub) && args.length == 2) { // D547 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.LifeService life = runs == null ? null : runs.plugin().getLifeService();
            if (life == null) { p.sendMessage(P + "生活服务未加载"); return true; }
            if (!life.pathBuyBread(p)) p.sendMessage(P + "无法买面包（已有干粮/缺币/副本内）");
            else p.sendMessage(P + "§a面包·手点 §7已购买");
            return true;
        }
        if ("brewpath".equals(sub) || "熬药路径".equals(sub)) { // D546 life brew heal path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberBrewPath.glance(EmberBrewPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c熬药路径需本人首通 Q01。");
                    return true;
                }
                EmberBrewPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c熬药路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBrewPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBrewPath.get(d);
                    if (EmberBrewPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBrewPath.label(cur) + " · 改路径再 /corerpg p1 brewpath");
                    else EmberBrewPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBrewPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 brewpath <auto|ask|mute|clear>");
                return true;
            }
            EmberBrewPath.applyAndReply(p, id);
            return true;
        }
        if ("brew".equals(sub) && args.length == 2) { // D546 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.LifeService life = runs == null ? null : runs.plugin().getLifeService();
            if (life == null) { p.sendMessage(P + "生活服务未加载"); return true; }
            if (!life.pathBrewHeal(p)) p.sendMessage(P + "无法熬药（等级/材料/币/已够药/副本内）");
            else p.sendMessage(P + "§a熬药·手点 §7完成");
            return true;
        }
        if ("rodpath".equals(sub) || "钓竿路径".equals(sub)) { // D545 fishing rod buy path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberRodPath.glance(EmberRodPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c钓竿路径需本人首通 Q01。");
                    return true;
                }
                EmberRodPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c钓竿路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRodPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRodPath.get(d);
                    if (EmberRodPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRodPath.label(cur) + " · 改路径再 /corerpg p1 rodpath");
                    else EmberRodPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRodPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 rodpath <auto|ask|mute|clear>");
                return true;
            }
            EmberRodPath.applyAndReply(p, id);
            return true;
        }
        if ("rod".equals(sub) && args.length == 2) { // D545 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.LifeService life = runs == null ? null : runs.plugin().getLifeService();
            if (life == null) { p.sendMessage(P + "生活服务未加载"); return true; }
            if (!life.pathBuyRod(p)) p.sendMessage(P + "无法买钓竿（已有/缺币/副本内）");
            else p.sendMessage(P + "§a钓竿·手点 §7已购买余烬钓竿");
            return true;
        }
        if ("bitepath".equals(sub) || "果腹路径".equals(sub)) { // D544 food bite path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberBitePath.glance(EmberBitePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c果腹路径需本人首通 Q01。");
                    return true;
                }
                EmberBitePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c果腹路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBitePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBitePath.get(d);
                    if (EmberBitePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBitePath.label(cur) + " · 改路径再 /corerpg p1 bitepath");
                    else EmberBitePath.offerPick(p);
                }
                return true;
            }
            int id = EmberBitePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 bitepath <auto|ask|mute|clear>");
                return true;
            }
            EmberBitePath.applyAndReply(p, id);
            return true;
        }
        if ("bite".equals(sub) && args.length == 2) { // D544 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.LifeService life = runs == null ? null : runs.plugin().getLifeService();
            if (life == null) { p.sendMessage(P + "生活服务未加载"); return true; }
            int ok = life.pathBiteOne(p);
            if (ok <= 0) p.sendMessage(P + "无法果腹（无烤鱼或已饱）");
            else p.sendMessage(P + "§a果腹·手点 §7饱食 " + p.getFoodLevel() + "/20");
            return true;
        }
        if ("cookpath".equals(sub) || "代烤路径".equals(sub)) { // D543 life cook path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberCookPath.glance(EmberCookPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c代烤路径需本人首通 Q01。");
                    return true;
                }
                EmberCookPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c代烤路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberCookPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberCookPath.get(d);
                    if (EmberCookPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberCookPath.label(cur) + " · 改路径再 /corerpg p1 cookpath");
                    else EmberCookPath.offerPick(p);
                }
                return true;
            }
            int id = EmberCookPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 cookpath <auto|ask|mute|clear>");
                return true;
            }
            EmberCookPath.applyAndReply(p, id);
            return true;
        }
        if ("cook".equals(sub) && args.length == 2) { // D543 ASK button (not /corerpg life cook)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.LifeService life = runs == null ? null : runs.plugin().getLifeService();
            if (life == null) { p.sendMessage(P + "生活服务未加载"); return true; }
            int ok = life.pathCook(p);
            if (ok <= 0) p.sendMessage(P + "无法代烤（无生鱼、缺币、或在副本内）");
            else p.sendMessage(P + "§a代烤·手点 §7已烤 §f" + ok + " §7条 → 炭烤鱼");
            return true;
        }
        if ("makeuppath".equals(sub) || "补签路径".equals(sub)) { // D542 sign makeup path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberMakeupPath.glance(EmberMakeupPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c补签路径需本人首通 Q01。");
                    return true;
                }
                EmberMakeupPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c补签路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberMakeupPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberMakeupPath.get(d);
                    if (EmberMakeupPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberMakeupPath.label(cur) + " · 改路径再 /corerpg p1 makeuppath");
                    else EmberMakeupPath.offerPick(p);
                }
                return true;
            }
            int id = EmberMakeupPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 makeuppath <auto|ask|mute|clear>");
                return true;
            }
            EmberMakeupPath.applyAndReply(p, id);
            return true;
        }
        if ("makeup".equals(sub) && args.length == 2) { // D542 ASK button
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            EmberSignService sign = runs == null ? null : runs.plugin().getEmberSign();
            if (sign == null) { p.sendMessage(P + "签到未加载"); return true; }
            sign.makeup(p);
            return true;
        }
        if ("creditpath".equals(sub) || "周免路径".equals(sub)) { // D541 weekly free-entry credit path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberCreditPath.glance(EmberCreditPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c周免路径需本人首通 Q01。");
                    return true;
                }
                EmberCreditPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c周免路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberCreditPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberCreditPath.get(d);
                    if (EmberCreditPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberCreditPath.label(cur) + " · 改路径再 /corerpg p1 creditpath");
                    else EmberCreditPath.offerPick(p);
                }
                return true;
            }
            int id = EmberCreditPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 creditpath <spend|hold|ask|clear>");
                return true;
            }
            EmberCreditPath.applyAndReply(p, id);
            return true;
        }
        if ("bankpath".equals(sub) || "取银路径".equals(sub)) { // D540 stamina bank draw path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberBankPath.glance(EmberBankPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c取银路径需本人首通 Q01。");
                    return true;
                }
                EmberBankPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c取银路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBankPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBankPath.get(d);
                    if (EmberBankPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBankPath.label(cur) + " · 改路径再 /corerpg p1 bankpath");
                    else EmberBankPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBankPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 bankpath <auto|ask|mute|clear>");
                return true;
            }
            EmberBankPath.applyAndReply(p, id);
            return true;
        }
        if ("bank".equals(sub) && args.length == 2) { // D540 ASK button: draw stamina bank
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.StaminaService st = runs == null ? null : runs.plugin().getStaminaService();
            if (st == null) { p.sendMessage(P + "体力服务未加载"); return true; }
            int got = st.pathDrawBank(p);
            PlayerData d = runs == null ? null : runs.dataOf(p.getUniqueId());
            if (got <= 0) p.sendMessage(P + "无法取银（银行空、或当前已够日常消耗）");
            else p.sendMessage(P + "§a取银·手点 §7+" + got + " §7（现 " + st.getStamina(d) + "/" + st.resolveMax(d)
                    + " · 银行 " + (d == null ? 0 : d.getStaminaBank()) + "）");
            return true;
        }
        if ("dosepath".equals(sub) || "补体路径".equals(sub)) { // D539 stamina potion dose path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberDosePath.glance(EmberDosePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c补体路径需本人首通 Q01。");
                    return true;
                }
                EmberDosePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c补体路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberDosePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberDosePath.get(d);
                    if (EmberDosePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberDosePath.label(cur) + " · 改路径再 /corerpg p1 dosepath");
                    else EmberDosePath.offerPick(p);
                }
                return true;
            }
            int id = EmberDosePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 dosepath <auto|ask|mute|clear>");
                return true;
            }
            EmberDosePath.applyAndReply(p, id);
            return true;
        }
        if ("dose".equals(sub)) { // D539 ASK button: drink one stamina potion
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.StaminaService st = runs == null ? null : runs.plugin().getStaminaService();
            if (st == null) { p.sendMessage(P + "体力服务未加载"); return true; }
            int got = st.pathDrinkOne(p);
            if (got <= 0) p.sendMessage(P + "无法喝药（无药、已满日上限、或体力服务忙）");
            else {
                PlayerData d = runs.dataOf(p.getUniqueId());
                p.sendMessage(P + "§a补体·手点 §7+" + got + " §7（现 " + st.getStamina(d) + "/" + st.resolveMax(d) + "）");
            }
            return true;
        }
        if ("scrappath".equals(sub) || "分解路径".equals(sub)) { // D538 legacy scrap path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberScrapPath.glance(EmberScrapPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c分解路径需本人首通 Q01。");
                    return true;
                }
                EmberScrapPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c分解路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberScrapPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberScrapPath.get(d);
                    if (EmberScrapPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberScrapPath.label(cur) + " · 改路径再 /corerpg p1 scrappath");
                    else EmberScrapPath.offerPick(p);
                }
                return true;
            }
            int id = EmberScrapPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 scrappath <auto|ask|mute|clear>");
                return true;
            }
            EmberScrapPath.applyAndReply(p, id);
            return true;
        }
        if ("scrap".equals(sub) && args.length == 2) { // D538 ASK button: path-driven backpack scrap (not /corerpg scrap)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            town.sunshine.corerpg.ScrapService scrap = runs == null ? null : runs.plugin().getScrapService();
            if (scrap == null || !scrap.isEnabled()) { p.sendMessage(P + "分解未启用"); return true; }
            int ok = scrap.pathBulkScrap(p);
            if (ok <= 0) p.sendMessage(P + "没有可分解的未强化旧刃/护符（或只剩最后一把武器）");
            else p.sendMessage(P + "§a分解·手点 §7已分解 §f" + ok + " §7件 → 材料");
            return true;
        }
        if ("dealpath".equals(sub) || "成交路径".equals(sub)) { // D537 auction deal notify path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberDealPath.glance(EmberDealPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c成交路径需本人首通 Q01。");
                    return true;
                }
                EmberDealPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c成交路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberDealPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberDealPath.get(d);
                    if (EmberDealPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberDealPath.label(cur) + " · 改路径再 /corerpg p1 dealpath");
                    else EmberDealPath.offerPick(p);
                }
                return true;
            }
            int id = EmberDealPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 dealpath <open|gate|busy|clear>");
                return true;
            }
            EmberDealPath.applyAndReply(p, id);
            return true;
        }
        if ("ticketpath".equals(sub) || "旧票路径".equals(sub)) { // D536 legacy ticket→stamina path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberTicketPath.glance(EmberTicketPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c旧票路径需本人首通 Q01。");
                    return true;
                }
                EmberTicketPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c旧票路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTicketPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTicketPath.get(d);
                    if (EmberTicketPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTicketPath.label(cur) + " · 改路径再 /corerpg p1 ticketpath");
                    else EmberTicketPath.offerPick(p);
                }
                return true;
            }
            int id = EmberTicketPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 ticketpath <auto|ask|mute|clear>");
                return true;
            }
            EmberTicketPath.applyAndReply(p, id);
            return true;
        }
        if ("gempath".equals(sub) || "宝石路径".equals(sub)) { // D535 combat gem preference + auto-socket
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberGemPath.glance(EmberGemPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c宝石路径需本人首通 Q01。");
                    return true;
                }
                EmberGemPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c宝石路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberGemPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberGemPath.get(d);
                    if (EmberGemPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberGemPath.label(cur) + " · 改路径再 /corerpg p1 gempath");
                    else EmberGemPath.offerPick(p);
                }
                return true;
            }
            int id = EmberGemPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 gempath <sharp|steady|drain|gale|clear>");
                return true;
            }
            EmberGemPath.applyAndReply(p, id);
            return true;
        }
        if ("glowpath".equals(sub) || "辉光路径".equals(sub)) { // D534 cosmetic glow wear path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberGlowPath.glance(EmberGlowPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c辉光路径需本人首通 Q01。");
                    return true;
                }
                EmberGlowPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c辉光路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberGlowPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberGlowPath.get(d);
                    if (EmberGlowPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberGlowPath.label(cur) + " · 改路径再 /corerpg p1 glowpath");
                    else EmberGlowPath.offerPick(p);
                }
                return true;
            }
            int id = EmberGlowPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 glowpath <auto|ask|mute|clear>");
                return true;
            }
            EmberGlowPath.applyAndReply(p, id);
            return true;
        }
        if ("pingpath".equals(sub) || "上线路径".equals(sub) || "提醒路径".equals(sub)) { // D533 friend online ping
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberPingPath.glance(EmberPingPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c上线路径需本人首通 Q01。");
                    return true;
                }
                EmberPingPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c上线路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPingPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPingPath.get(d);
                    if (EmberPingPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPingPath.label(cur) + " · 改路径再 /corerpg p1 pingpath");
                    else EmberPingPath.offerPick(p);
                }
                return true;
            }
            int id = EmberPingPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 pingpath <open|gate|busy|clear>");
                return true;
            }
            EmberPingPath.applyAndReply(p, id);
            return true;
        }
        if ("titlepath".equals(sub) || "称号路径".equals(sub)) { // D532 cosmetic title wear path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberTitlePath.glance(EmberTitlePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c称号路径需本人首通 Q01。");
                    return true;
                }
                EmberTitlePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c称号路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTitlePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTitlePath.get(d);
                    if (EmberTitlePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTitlePath.label(cur) + " · 改路径再 /corerpg p1 titlepath");
                    else EmberTitlePath.offerPick(p);
                }
                return true;
            }
            int id = EmberTitlePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 titlepath <auto|ask|mute|clear>");
                return true;
            }
            EmberTitlePath.applyAndReply(p, id);
            return true;
        }
        if ("junkpath".equals(sub) || "清库路径".equals(sub)) { // D531 gearlib junk dismantle path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberJunkPath.glance(EmberJunkPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c清库路径需本人首通 Q01。");
                    return true;
                }
                EmberJunkPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c清库路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberJunkPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberJunkPath.get(d);
                    if (EmberJunkPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberJunkPath.label(cur) + " · 改路径再 /corerpg p1 junkpath");
                    else EmberJunkPath.offerPick(p);
                }
                return true;
            }
            int id = EmberJunkPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 junkpath <auto|ask|mute|clear>");
                return true;
            }
            EmberJunkPath.applyAndReply(p, id);
            return true;
        }
        if ("junk".equals(sub)) { // D531 ASK button: path-driven bulk junk
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            EmberGearLib glib = EmberGearLib.get();
            if (glib == null || !glib.usable()) { p.sendMessage(P + "装备库未就绪"); return true; }
            glib.pathBulkJunk(p);
            return true;
        }
        if ("partypath".equals(sub) || "组队路径".equals(sub)) { // D530 dungeon-team invite accept path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberPartyPath.glance(EmberPartyPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c组队路径需本人首通 Q01。");
                    return true;
                }
                EmberPartyPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c组队路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPartyPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPartyPath.get(d);
                    if (EmberPartyPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPartyPath.label(cur) + " · 改路径再 /corerpg p1 partypath");
                    else EmberPartyPath.offerPick(p);
                }
                return true;
            }
            int id = EmberPartyPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 partypath <open|gate|busy|clear>");
                return true;
            }
            EmberPartyPath.applyAndReply(p, id);
            return true;
        }
        if ("trailpath".equals(sub) || "足迹路径".equals(sub)) { // D529 cosmetic trail wear path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberTrailPath.glance(EmberTrailPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c足迹路径需本人首通 Q01。");
                    return true;
                }
                EmberTrailPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c足迹路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTrailPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTrailPath.get(d);
                    if (EmberTrailPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTrailPath.label(cur) + " · 改路径再 /corerpg p1 trailpath");
                    else EmberTrailPath.offerPick(p);
                }
                return true;
            }
            int id = EmberTrailPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 trailpath <auto|ask|mute|clear>");
                return true;
            }
            EmberTrailPath.applyAndReply(p, id);
            return true;
        }
        if ("packseed".equals(sub)) { // D555 admin smoke: temp P1 + fill bag + shards then probe
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberMode mode = EmberMode.get();
            String wn = t.getWorld().getName();
            boolean added = false;
            if (mode != null && !EmberMode.isP1World(t.getWorld())) {
                mode.addWorld(wn);
                added = true;
            }
            // fill storage until <=1 empty, leave room for shards
            org.bukkit.inventory.PlayerInventory inv = t.getInventory();
            org.bukkit.inventory.ItemStack filler = new org.bukkit.inventory.ItemStack(org.bukkit.Material.COBBLESTONE, 64);
            int empty = EmberPackPath.emptyStorage(t);
            while (empty > EmberPackPath.TIGHT_SLOTS) {
                java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> left = inv.addItem(filler.clone());
                if (!left.isEmpty()) break;
                empty = EmberPackPath.emptyStorage(t);
            }
            if (runs.plugin().getNiBridge() != null)
                runs.plugin().getNiBridge().giveNiItem(t, EmberUpgradeRules.MAT_SHARD, 8);
            // ensure tight after shard give
            empty = EmberPackPath.emptyStorage(t);
            while (empty > EmberPackPath.TIGHT_SLOTS) {
                java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> left = inv.addItem(filler.clone());
                if (!left.isEmpty()) break;
                empty = EmberPackPath.emptyStorage(t);
            }
            EmberPackPath.maybeProbe(t);
            if (added && mode != null) mode.removeWorld(wn);
            s.sendMessage(P + "已塞满并触发腾包路径 → " + t.getName()
                    + " 空格=" + EmberPackPath.emptyStorage(t)
                    + (added ? "（临时标记世界）" : ""));
            return true;
        }
        if ("fleeseed".equals(sub)) { // D554 admin smoke: probe flee path
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberFleePath.maybeProbe(t);
            s.sendMessage(P + "已触发撤离路径探测 → " + t.getName()
                    + (runs != null && runs.canFleeLeave(t) ? "（可离本）" : "（无倒下态）"));
            return true;
        }
        if ("chestseed".equals(sub)) { // D553 admin smoke: probe chest path (live open if pending)
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberChestPath.maybeProbe(t);
            s.sendMessage(P + "已触发开箱路径探测 → " + t.getName()
                    + (runs != null && runs.hasPendingExtraChest(t) ? "（局内有箱）" : "（无局内箱）"));
            return true;
        }
        if ("sightseed".equals(sub)) { // D552 admin smoke: temp P1 + clear NV
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberMode mode = EmberMode.get();
            String wn = t.getWorld().getName();
            boolean added = false;
            if (mode != null && !EmberMode.isP1World(t.getWorld())) {
                mode.addWorld(wn);
                added = true;
            }
            t.removePotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION);
            EmberSightPath.maybeAfterProgress(t);
            if (added && mode != null) mode.removeWorld(wn);
            s.sendMessage(P + "已清夜视并触发夜视路径 → " + t.getName()
                    + (added ? "（临时标记世界）" : ""));
            return true;
        }
        if ("charmseed".equals(sub)) { // D551 admin smoke: better charm in bag, keep weaker selected
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null || loadouts == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberLoadout cur = loadouts.refresh(t);
            String keepUid = cur.charm == null ? null : cur.charm.uid;
            EmberItemData d = EmberItemData.create("sustain", "charm", 2, 1, 0, 0, true, "admin")
                    .withOrigin(EmberProvenance.forAdmin("charmseed", System.currentTimeMillis()));
            String bad = d.validate();
            if (bad != null) { s.sendMessage(P + "§c护符参数无效: " + bad); return true; }
            org.bukkit.inventory.ItemStack item = loadouts.items().create(d);
            if (item == null) { s.sendMessage(P + "§c生成护符失败（NI）"); return true; }
            loadouts.remember(d, t.getUniqueId());
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = t.getInventory().addItem(item);
            for (org.bukkit.inventory.ItemStack drop : left.values()) t.getWorld().dropItemNaturally(t.getLocation(), drop);
            if (keepUid != null) loadouts.selectCharmUid(t, keepUid); // keep weaker selected so upgrade is visible
            else loadouts.clearCharm(t);
            s.sendMessage(P + "已发 T2 护符到背包并保持旧选定 → " + t.getName());
            EmberCharmPath.maybeAfterProgress(t);
            return true;
        }
        if ("gripseed".equals(sub)) { // D550 admin smoke: temp P1 + unarmed with blade in bag
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null || loadouts == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberMode mode = EmberMode.get();
            String wn = t.getWorld().getName();
            boolean added = false;
            if (mode != null && !EmberMode.isP1World(t.getWorld())) {
                mode.addWorld(wn);
                added = true;
            }
            org.bukkit.inventory.ItemStack main = t.getInventory().getItemInMainHand();
            boolean moved = false;
            if (main != null && main.getType() != org.bukkit.Material.AIR) {
                java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = t.getInventory().addItem(main.clone());
                if (left.isEmpty()) {
                    t.getInventory().setItemInMainHand(null);
                    moved = true;
                } else {
                    org.bukkit.inventory.ItemStack[] st = t.getInventory().getStorageContents();
                    for (int i = 0; i < st.length; i++) {
                        if (st[i] == null || st[i].getType() == org.bukkit.Material.AIR) {
                            st[i] = main.clone();
                            t.getInventory().setStorageContents(st);
                            t.getInventory().setItemInMainHand(null);
                            moved = true;
                            break;
                        }
                    }
                }
            }
            loadouts.markDirty(t);
            EmberGripPath.maybeAfterProgress(t);
            if (added && mode != null) mode.removeWorld(wn);
            s.sendMessage(P + "已让主手空出并触发握刃路径 → " + t.getName()
                    + (moved ? "" : "（主本已空）") + (added ? "（临时标记世界）" : ""));
            return true;
        }
        if ("armorseed".equals(sub)) { // D549 admin smoke: better chest in bag
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null || loadouts == null) { s.sendMessage(P + "找不到玩家"); return true; }
            if (!EmberSixSlot.enabled()) { s.sendMessage(P + "§c六槽未开"); return true; }
            // Clear worn chest so backpack T2 is strictly better (migration may already mirror charm kit).
            org.bukkit.inventory.ItemStack[] wornArmor = t.getInventory().getArmorContents();
            int chestBi = EmberSixSlot.toArmorContents(1);
            if (chestBi >= 0 && chestBi < wornArmor.length) {
                org.bukkit.inventory.ItemStack oldChest = wornArmor[chestBi];
                wornArmor[chestBi] = null;
                t.getInventory().setArmorContents(wornArmor);
                if (oldChest != null && oldChest.getType() != org.bukkit.Material.AIR) {
                    java.util.Map<Integer, org.bukkit.inventory.ItemStack> spill = t.getInventory().addItem(oldChest);
                    for (org.bukkit.inventory.ItemStack drop : spill.values()) t.getWorld().dropItemNaturally(t.getLocation(), drop);
                }
                loadouts.markDirty(t);
            }
            EmberItemData d = EmberItemData.create("sustain", "chest", 2, 1, 0, 0, true, "admin")
                    .withOrigin(EmberProvenance.forAdmin("armorseed", System.currentTimeMillis()));
            String bad = d.validate();
            if (bad != null) { s.sendMessage(P + "§c护甲参数无效: " + bad); return true; }
            org.bukkit.inventory.ItemStack item = loadouts.items().create(d);
            if (item == null) { s.sendMessage(P + "§c生成护甲失败（NI）"); return true; }
            loadouts.remember(d, t.getUniqueId());
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = t.getInventory().addItem(item);
            for (org.bukkit.inventory.ItemStack drop : left.values()) t.getWorld().dropItemNaturally(t.getLocation(), drop);
            s.sendMessage(P + "已卸胸甲并发 T2 胸甲到背包 → " + t.getName());
            EmberArmorPath.maybeAfterProgress(t);
            return true;
        }
        if ("sipseed".equals(sub)) { // D548 admin smoke: temp P1 scope + low HP + pot
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberSupplyService sup = runs.plugin().getEmberSupplies();
            EmberMode mode = EmberMode.get();
            String wn = t.getWorld().getName();
            boolean added = false;
            if (mode != null && !EmberMode.isP1World(t.getWorld())) {
                mode.addWorld(wn);
                added = true;
            }
            if (sup != null && sup.countHealPotions(t) < 1) {
                int g = sup.give(t, 1, "sipseed");
                if (g < 1) s.sendMessage(P + "§c发药失败");
            }
            double max = EmberHeal.maxHp(t);
            if (max > 1) t.setHealth(Math.max(1.0, max * 0.30));
            EmberLoadoutService ls = runs.plugin().getEmberLoadouts();
            if (ls != null) {
                EmberPlayerState st = ls.state(t.getUniqueId());
                st.healCdUntil = 0L;
                ls.saveState(t);
            }
            EmberSipPath.maybeAfterLowHp(t);
            if (added && mode != null) mode.removeWorld(wn);
            s.sendMessage(P + "已灌药+压血至约30%并触发喝药路径 → " + t.getName()
                    + (added ? "（临时标记世界）" : ""));
            return true;
        }
        if ("breadseed".equals(sub)) { // D547 admin smoke: no food + coin for bread
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.LifeService life = runs.plugin().getLifeService();
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            if (life != null && plg.getNiBridge() != null) {
                String food = life.cookOutputId();
                int g = plg.getNiBridge().countInInventoryOnly(t, food);
                if (g > 0) plg.getNiBridge().consumeExact(t, food, g);
                int b = plg.getNiBridge().countInInventoryOnly(t, town.sunshine.corerpg.LifeService.BREAD_NI_ID);
                if (b > 0) plg.getNiBridge().consumeExact(t, town.sunshine.corerpg.LifeService.BREAD_NI_ID, b);
            }
            PlayerData pd = plg.getDataStore().get(t.getUniqueId());
            if (pd != null && pd.getCoin() < 40) {
                pd.addCoin(40 - pd.getCoin());
                plg.getDataStore().flushMutation(t.getUniqueId());
            }
            s.sendMessage(P + "已清空烤鱼/面包并确保≥40币 → " + t.getName());
            EmberBreadPath.maybeAfterProgress(t);
            return true;
        }
        if ("brewseed".equals(sub)) { // D546 admin smoke: life Lv2 + mats + low heal pots
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            PlayerData pd = plg.getDataStore().get(t.getUniqueId());
            if (pd == null) { s.sendMessage(P + "数据未就绪"); return true; }
            int xp = pd.periodCount("life_xp", "all");
            if (xp < 20) pd.addPeriodCount("life_xp", "all", 20 - xp);
            if (pd.getCoin() < 10) pd.addCoin(10 - pd.getCoin());
            plg.getDataStore().flushMutation(t.getUniqueId());
            // strip heal potions so needsBrew sees low count
            EmberSupplyService supply = plg.getEmberSupplies();
            if (supply != null && plg.getNiBridge() != null) {
                org.bukkit.inventory.ItemStack[] cont = t.getInventory().getContents();
                for (int i = 0; i < cont.length; i++) {
                    if (cont[i] != null && supply.isHealPotion(cont[i])) t.getInventory().setItem(i, null);
                }
            }
            if (plg.getNiBridge() == null
                    || !plg.getNiBridge().giveNiItem(t, "fish_ember_puffer", 1)
                    || !plg.getNiBridge().giveNiItem(t, "fish_ember_cod", 2)) {
                s.sendMessage(P + "发放熬药材料失败"); return true;
            }
            s.sendMessage(P + "已注入熬药条件（Lv≥2·材料·低药）→ " + t.getName());
            EmberBrewPath.maybeAfterProgress(t);
            return true;
        }
        if ("rodseed".equals(sub)) { // D545 admin smoke: ensure no rod + coin for buy
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.LifeService life = runs.plugin().getLifeService();
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            if (life != null && plg.getNiBridge() != null) {
                int have = plg.getNiBridge().countInInventoryOnly(t, town.sunshine.corerpg.LifeService.ROD_NI_ID);
                if (have > 0) plg.getNiBridge().consumeExact(t, town.sunshine.corerpg.LifeService.ROD_NI_ID, have);
            }
            PlayerData pd = plg.getDataStore().get(t.getUniqueId());
            if (pd != null && pd.getCoin() < 60) {
                pd.addCoin(60 - pd.getCoin());
                plg.getDataStore().flushMutation(t.getUniqueId());
            }
            s.sendMessage(P + "已清空钓竿并确保≥60币 → " + t.getName());
            EmberRodPath.maybeAfterProgress(t);
            return true;
        }
        if ("biteseed".equals(sub)) { // D544 admin smoke: grilled fish + low hunger
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.LifeService life = runs.plugin().getLifeService();
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            String food = life == null ? "food_ember_grilled_fish" : life.cookOutputId();
            if (plg.getNiBridge() == null || !plg.getNiBridge().giveNiItem(t, food, 2)) {
                s.sendMessage(P + "发放烤鱼失败"); return true;
            }
            t.setFoodLevel(8);
            t.setSaturation(0f);
            s.sendMessage(P + "已发放烤鱼×2 · 饱食=8 → " + t.getName());
            EmberBitePath.maybeAfterProgress(t);
            return true;
        }
        if ("cookseed".equals(sub)) { // D543 admin smoke: give raw fish + coin
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            if (plg.getNiBridge() == null || !plg.getNiBridge().giveNiItem(t, "fish_ember_cod", 3)) {
                s.sendMessage(P + "发放生鱼失败"); return true;
            }
            PlayerData pd = plg.getDataStore().get(t.getUniqueId());
            if (pd != null) { pd.addCoin(20); plg.getDataStore().flushMutation(t.getUniqueId()); }
            s.sendMessage(P + "已发放生鱼×3 → " + t.getName());
            EmberCookPath.maybeAfterProgress(t);
            return true;
        }
        if ("makeupseed".equals(sub)) { // D542 admin smoke: force makeup-eligible state
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberSignService sign = runs.plugin().getEmberSign();
            if (sign == null) { s.sendMessage(P + "签到未加载"); return true; }
            boolean ok = sign.adminSeedMakeup(t);
            if (!ok) { s.sendMessage(P + "注入失败（月初无漏签日/签到关）"); return true; }
            s.sendMessage(P + "已注入可补签状态 → " + t.getName());
            EmberMakeupPath.maybeAfterProgress(t);
            return true;
        }
        if ("creditseed".equals(sub)) { // D541 admin smoke: ensure weekly credit + tip/probe
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.StaminaService st = runs.plugin().getStaminaService();
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (st == null || d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            st.ensure(d);
            if (d.getWeeklyGrantCreditWeekly() <= 0) d.setWeeklyGrantCreditWeekly(1);
            // ensure stamina can pay weekly so HOLD probe spends stamina not credit
            st.cmdRoot(s, new String[]{"stamina", "set", t.getName(), "90"});
            runs.plugin().getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + "已确保周本免费×" + d.getWeeklyGrantCreditWeekly() + " · 体力满 → " + t.getName());
            EmberCreditPath.maybeAfterProgress(t);
            return true;
        }
        if ("creditprobe".equals(sub)) { // D541 admin: simulate weekly enter consume + refund
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.StaminaService st = runs.plugin().getStaminaService();
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (st == null || d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            int beforeCredit = d.getWeeklyGrantCreditWeekly();
            int beforeSta = st.getStamina(d);
            town.sunshine.corerpg.StaminaService.ConsumeResult r = st.consumeForEnter(t, town.sunshine.corerpg.TicketEntryService.Kind.WEEKLY);
            if (!r.ok) { s.sendMessage(P + "探测失败: " + r.failMessage); return true; }
            st.refundEnter(t, town.sunshine.corerpg.TicketEntryService.Kind.WEEKLY, r);
            s.sendMessage(P + "探测周本进场 → " + (r.usedCredit ? "用了免费抵扣" : ("扣体力×" + r.cost))
                    + " · 路径 " + EmberCreditPath.label(EmberCreditPath.get(d))
                    + " · 前信用 " + beforeCredit + " 体力 " + beforeSta);
            t.sendMessage(P + "§7周免探测：" + (r.usedCredit ? "§a用了免费抵扣" : ("§e扣体力×" + r.cost)) + " §8（已退回）");
            return true;
        }
        if ("bankseed".equals(sub)) { // D540 admin smoke: low stamina + bank stock
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.StaminaService st = runs.plugin().getStaminaService();
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (st == null || d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            st.cmdRoot(s, new String[]{"stamina", "set", t.getName(), "5"});
            d.setStaminaBank(40);
            runs.plugin().getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + "已设体力=5 · 银行=40 → " + t.getName());
            EmberBankPath.maybeAfterProgress(t);
            return true;
        }
        if ("doseseed".equals(sub)) { // D539 admin smoke: low stamina + potion
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.StaminaService st = runs.plugin().getStaminaService();
            if (st == null) { s.sendMessage(P + "体力服务未加载"); return true; }
            st.cmdRoot(s, new String[]{"stamina", "set", t.getName(), "5"});
            if (!st.grantPotionNi(t, town.sunshine.corerpg.StaminaService.POTION_NI_ID, 1)) {
                s.sendMessage(P + "发放体力药失败"); return true;
            }
            s.sendMessage(P + "已设体力=5 并发药 ×1 → " + t.getName());
            EmberDosePath.maybeAfterProgress(t);
            return true;
        }
        if ("scrapseed".equals(sub)) { // D538 admin smoke: give enhance-0 charm into backpack
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            if (plg.getNiBridge() == null || !plg.getNiBridge().giveNiItem(t, "gear_ember_charm", 1)) {
                s.sendMessage(P + "发放护符失败"); return true;
            }
            s.sendMessage(P + "已发放 gear_ember_charm ×1 → " + t.getName());
            EmberScrapPath.maybeAfterProgress(t);
            return true;
        }
        if ("dealseed".equals(sub)) { // D537 admin smoke: queue pending auction digest
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            EmberDealPath.adminSeedPending(d, 1, 42);
            runs.plugin().getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + "已注入待汇总成交 1 笔 · 实收 42 → " + t.getName());
            EmberDealPath.maybeDigestOnJoin(t);
            return true;
        }
        if ("ticketseed".equals(sub)) { // D536 admin smoke: give convertible daily ticket
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            town.sunshine.corerpg.CoreRpgPlugin plg = runs.plugin();
            if (plg == null || plg.getNiBridge() == null) { s.sendMessage(P + "NI 未就绪"); return true; }
            String tid = "ticket_ember_daily";
            if (!plg.getNiBridge().giveNiItem(t, tid, 1)) { s.sendMessage(P + "发放旧票失败"); return true; }
            s.sendMessage(P + "已发放 " + tid + " ×1 → " + t.getName());
            EmberTicketPath.maybeAfterProgress(t);
            return true;
        }
        if ("gemseed".equals(sub)) { // D535 admin smoke: legacy socketable blade in hand + preferred gem
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            String gem = args.length >= 4 ? args[3] : EmberGemPath.GEM_SHARP;
            if (t == null) { s.sendMessage(P + "找不到玩家"); return true; }
            if (EmberGemPath.parse(gem) > 0) gem = EmberGemPath.gemId(EmberGemPath.parse(gem));
            town.sunshine.corerpg.CoreRpgPlugin plg = runs == null ? null : runs.plugin();
            if (plg == null || plg.getNiBridge() == null) { s.sendMessage(P + "NI 未就绪"); return true; }
            // Enhance/socket whitelist uses legacy gear_ember_blade (P1 ember_v1_* has no GearLore sockets)
            org.bukkit.inventory.ItemStack blade = plg.getNiBridge().createNiItem("gear_ember_blade");
            if (blade == null) { s.sendMessage(P + "无法生成 gear_ember_blade"); return true; }
            t.getInventory().setItemInMainHand(blade);
            t.updateInventory();
            if (!plg.getNiBridge().giveNiItem(t, gem, 1)) { s.sendMessage(P + "发放宝石失败: " + gem); return true; }
            s.sendMessage(P + "已发放 socket 刃 + " + gem + " ×1 → " + t.getName());
            EmberGemPath.maybeAfterProgress(t);
            return true;
        }
        if ("glowseed".equals(sub)) { // D534 admin smoke: grant glow_ember + clear selection
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            EmberCosmetics cos = runs.cosmetics();
            if (cos == null) { s.sendMessage(P + "外观服务未加载"); return true; }
            if (!cos.adminGrantGlow(t, d, "glow_ember")) { s.sendMessage(P + "授予辉光失败"); return true; }
            s.sendMessage(P + "已授予 " + t.getName() + " 余烬辉光（可测辉光路径）");
            EmberGlowPath.maybeAfterProgress(t, cos);
            return true;
        }
        if ("titleseed".equals(sub)) { // D532 admin smoke: clear title selection so path can fill q01
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null || runs == null) { s.sendMessage(P + "找不到玩家"); return true; }
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            EmberCosmetics cos = runs.cosmetics();
            if (cos == null) { s.sendMessage(P + "外观服务未加载"); return true; }
            cos.adminClearTitle(d);
            runs.flushData(t.getUniqueId());
            s.sendMessage(P + "已清空 " + t.getName() + " 称号选择（可测称号路径）");
            EmberTitlePath.maybeAfterProgress(t, cos);
            return true;
        }
        if ("junkseed".equals(sub)) { // D531 admin smoke: drop-source T1 blade into gearlib as junk
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "找不到玩家"); return true; }
            EmberGearLib glib = EmberGearLib.get();
            if (glib == null || !glib.usable() || loadouts == null) { s.sendMessage(P + "装备库未就绪"); return true; }
            if (!NmsNbt.isReady()) { s.sendMessage(P + "NBT 桥不可用"); return true; }
            EmberItemData d = EmberItemData.create("scorch", "blade", 1, 0, 0, 0, true, "drop")
                    .withOrigin(EmberProvenance.forAdmin("junkseed", System.currentTimeMillis()));
            String bad = d.validate();
            if (bad != null) { s.sendMessage(P + "参数无效: " + bad); return true; }
            if (!glib.autoStash(t, d, 0, false, true)) {
                // fallback: hand out then depositAll
                org.bukkit.inventory.ItemStack item = loadouts.items().create(d);
                if (item == null) { s.sendMessage(P + "生成失败"); return true; }
                loadouts.remember(d, t.getUniqueId());
                t.getInventory().addItem(item);
                glib.depositAll(t, () -> {});
            }
            s.sendMessage(P + "已注入清库测试件 T1 余烬刃 → " + t.getName() + " 装备库");
            EmberJunkPath.maybeAfterProgress(t);
            return true;
        }
        if ("trailseed".equals(sub)) { // D529 admin smoke: grant trail_ash + honor path
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if (runs == null || runs.cosmetics() == null) { s.sendMessage(P + "外观服务未加载"); return true; }
            PlayerData d = runs.dataOf(t.getUniqueId());
            if (d == null) { s.sendMessage(P + "数据未就绪"); return true; }
            // clear current trail so path can fill
            String cur = EmberCosmetics.selected(d, EmberCosmetics.Kind.TRAIL);
            if (cur != null) d.addPeriodCount("p2_trailsel", cur, -d.periodCount("p2_trailsel", cur));
            if (!runs.cosmetics().adminGrantTrail(t, d, "trail_ash")) { s.sendMessage(P + "授予足迹失败"); return true; }
            s.sendMessage(P + "已为 " + t.getName() + " 授予足迹·灰烬（未装）");
            EmberTrailPath.maybeAfterProgress(t, runs.cosmetics());
            return true;
        }
        if ("feedpath".equals(sub) || "喂养路径".equals(sub)) { // D528 familiar feed path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberFeedPath.glance(EmberFeedPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c喂养路径需本人首通 Q01。");
                    return true;
                }
                EmberFeedPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c喂养路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFeedPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFeedPath.get(d);
                    if (EmberFeedPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFeedPath.label(cur) + " · 改路径再 /corerpg p1 feedpath");
                    else EmberFeedPath.offerPick(p);
                }
                return true;
            }
            int id = EmberFeedPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 feedpath <auto|ask|mute|clear>");
                return true;
            }
            EmberFeedPath.applyAndReply(p, id);
            return true;
        }
        if ("feedseed".equals(sub)) { // D528 admin smoke: unlock ashling + give dust + honor path
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            town.sunshine.corerpg.PetService pets = runs == null ? null : runs.plugin().getPetService();
            if (pets == null) { s.sendMessage(P + "使魔服务未加载"); return true; }
            pets.adminUnlock(t, "ashling");
            pets.adminGiveFeedDust(t, 8);
            s.sendMessage(P + "已为 " + t.getName() + " 解锁灰灵并塞入魂尘 ×8");
            EmberFeedPath.maybeAfterJoin(t, pets);
            return true;
        }
        if ("guildpath".equals(sub) || "盟约路径".equals(sub)) { // D527 guild-invite accept path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberGuildPath.glance(EmberGuildPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c盟约路径需本人首通 Q01。");
                    return true;
                }
                EmberGuildPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c盟约路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberGuildPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberGuildPath.get(d);
                    if (EmberGuildPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberGuildPath.label(cur) + " · 改路径再 /corerpg p1 guildpath");
                    else EmberGuildPath.offerPick(p);
                }
                return true;
            }
            int id = EmberGuildPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 guildpath <open|gate|busy|clear>");
                return true;
            }
            EmberGuildPath.applyAndReply(p, id);
            return true;
        }
        if ("stashpath".equals(sub) || "存仓路径".equals(sub)) { // D526 backpack stash path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberStashPath.glance(EmberStashPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c存仓路径需本人首通 Q01。");
                    return true;
                }
                EmberStashPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c存仓路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberStashPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberStashPath.get(d);
                    if (EmberStashPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberStashPath.label(cur) + " · 改路径再 /corerpg p1 stashpath");
                    else EmberStashPath.offerPick(p);
                }
                return true;
            }
            int id = EmberStashPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 stashpath <auto|ask|mute|clear>");
                return true;
            }
            EmberStashPath.applyAndReply(p, id);
            return true;
        }
        if ("stashseed".equals(sub)) { // D526 admin smoke: put vault mats in backpack then honor path
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if (runs == null || runs.plugin().getNiBridge() == null) { s.sendMessage(P + "服务未就绪"); return true; }
            runs.plugin().getNiBridge().giveNiItem(t, EmberUpgradeRules.MAT_SHARD, 8);
            s.sendMessage(P + "已向 " + t.getName() + " 背包塞入碎片 ×8");
            EmberStashPath.maybeAfterProgress(t);
            return true;
        }
        if ("petpath".equals(sub) || "使魔路径".equals(sub)) { // D525 familiar summon path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberPetPath.glance(EmberPetPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c使魔路径需本人首通 Q01。");
                    return true;
                }
                EmberPetPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c使魔路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPetPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPetPath.get(d);
                    if (EmberPetPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPetPath.label(cur) + " · 改路径再 /corerpg p1 petpath");
                    else EmberPetPath.offerPick(p);
                }
                return true;
            }
            int id = EmberPetPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 petpath <auto|ask|mute|clear>");
                return true;
            }
            EmberPetPath.applyAndReply(p, id);
            return true;
        }
        if ("petseed".equals(sub)) { // D525 admin smoke: unlock ashling without egg
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = args.length >= 3 ? Bukkit.getPlayerExact(args[2]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            town.sunshine.corerpg.PetService pets = runs == null ? null : runs.plugin().getPetService();
            if (pets == null) { s.sendMessage(P + "使魔服务未加载"); return true; }
            String id = args.length >= 4 ? args[3] : "ashling";
            if (!pets.adminUnlock(t, id)) { s.sendMessage(P + "解锁失败（未知 id？）"); return true; }
            s.sendMessage(P + "已为 " + t.getName() + " 解锁使魔 " + id);
            EmberPetPath.maybeAfterJoin(t, pets);
            return true;
        }
        if ("mentorpath".equals(sub) || "师徒路径".equals(sub)) { // D524 mentor-request accept path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberMentorPath.glance(EmberMentorPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c师徒路径需本人首通 Q01。");
                    return true;
                }
                EmberMentorPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c师徒路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberMentorPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberMentorPath.get(d);
                    if (EmberMentorPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberMentorPath.label(cur) + " · 改路径再 /corerpg p1 mentorpath");
                    else EmberMentorPath.offerPick(p);
                }
                return true;
            }
            int id = EmberMentorPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 mentorpath <open|gate|busy|clear>");
                return true;
            }
            EmberMentorPath.applyAndReply(p, id);
            return true;
        }
        if ("mailpath".equals(sub) || "邮件路径".equals(sub)) { // D523 system-mail attachment claim path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberMailPath.glance(EmberMailPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c邮件路径需本人首通 Q01。");
                    return true;
                }
                EmberMailPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c邮件路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberMailPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberMailPath.get(d);
                    if (EmberMailPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberMailPath.label(cur) + " · 改路径再 /corerpg p1 mailpath");
                    else EmberMailPath.offerPick(p);
                }
                return true;
            }
            int id = EmberMailPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 mailpath <auto|ask|mute|clear>");
                return true;
            }
            EmberMailPath.applyAndReply(p, id);
            return true;
        }
        if ("codexpath".equals(sub) || "图录路径".equals(sub)) { // D522 equipment-codex stage claim path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberCodexPath.glance(EmberCodexPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c图录路径需本人首通 Q01。");
                    return true;
                }
                EmberCodexPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c图录路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberCodexPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberCodexPath.get(d);
                    if (EmberCodexPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberCodexPath.label(cur) + " · 改路径再 /corerpg p1 codexpath");
                    else EmberCodexPath.offerPick(p);
                }
                return true;
            }
            int id = EmberCodexPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 codexpath <auto|ask|mute|clear>");
                return true;
            }
            EmberCodexPath.applyAndReply(p, id);
            return true;
        }
        if ("goalpath".equals(sub) || "周标路径".equals(sub)) { // D521 weekly season-goal focus path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberGoalPath.glance(EmberGoalPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c周标路径需本人首通 Q07。");
                    return true;
                }
                EmberGoalPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c周标路径需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberGoalPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberGoalPath.get(d);
                    if (EmberGoalPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberGoalPath.label(cur) + " · 改路径再 /corerpg p1 goalpath");
                    else EmberGoalPath.offerPick(p);
                }
                return true;
            }
            int id = EmberGoalPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 goalpath <featured|abyss|raid|bounty|flex|clear>");
                return true;
            }
            EmberGoalPath.applyAndReply(p, id);
            return true;
        }
        if ("signpath".equals(sub) || "签到路径".equals(sub)) { // D520 daily sign-in claim path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberSignPath.glance(EmberSignPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c签到路径需本人首通 Q01。");
                    return true;
                }
                EmberSignPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c签到路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberSignPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSignPath.get(d);
                    if (EmberSignPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberSignPath.label(cur) + " · 改路径再 /corerpg p1 signpath");
                    else EmberSignPath.offerPick(p);
                }
                return true;
            }
            int id = EmberSignPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 signpath <auto|ask|mute|clear>");
                return true;
            }
            EmberSignPath.applyAndReply(p, id);
            return true;
        }
        if ("challengepath".equals(sub) || "硬本路径".equals(sub) || "挑战路径".equals(sub)) { // D519 cleared-map challenge preference
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberChallengePath.glance(EmberChallengePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c硬本路径需本人首通 Q07。");
                    return true;
                }
                EmberChallengePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c硬本路径需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberChallengePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberChallengePath.get(d);
                    if (EmberChallengePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberChallengePath.label(cur) + " · 改路径再 /corerpg p1 challengepath");
                    else EmberChallengePath.offerPick(p);
                }
                return true;
            }
            int id = EmberChallengePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 challengepath <prefer|easy|follow|clear>");
                return true;
            }
            EmberChallengePath.applyAndReply(p, id);
            return true;
        }
        if ("onlinepath".equals(sub) || "在线路路径".equals(sub) || "在线路径".equals(sub)) { // D518 online milestone claim path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberOnlinePath.glance(EmberOnlinePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c在线路路径需本人首通 Q01。");
                    return true;
                }
                EmberOnlinePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c在线路路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberOnlinePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberOnlinePath.get(d);
                    if (EmberOnlinePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberOnlinePath.label(cur) + " · 改路径再 /corerpg p1 onlinepath");
                    else EmberOnlinePath.offerPick(p);
                }
                return true;
            }
            int id = EmberOnlinePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 onlinepath <auto|ask|mute|clear>");
                return true;
            }
            EmberOnlinePath.applyAndReply(p, id);
            return true;
        }
        if ("equippath".equals(sub) || "换装路径".equals(sub)) { // D517 better-piece equip path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberEquipPath.glance(EmberEquipPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c换装路径需本人首通 Q01。");
                    return true;
                }
                EmberEquipPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c换装路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberEquipPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberEquipPath.get(d);
                    if (EmberEquipPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberEquipPath.label(cur) + " · 改路径再 /corerpg p1 equippath");
                    else EmberEquipPath.offerPick(p);
                }
                return true;
            }
            int id = EmberEquipPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 equippath <keep|quick|bold|clear>");
                return true;
            }
            EmberEquipPath.applyAndReply(p, id);
            return true;
        }
        if ("friendpath".equals(sub) || "好友路径".equals(sub)) { // D516 friend-request accept path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberFriendPath.glance(EmberFriendPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c好友路径需本人首通 Q01。");
                    return true;
                }
                EmberFriendPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c好友路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFriendPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFriendPath.get(d);
                    if (EmberFriendPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFriendPath.label(cur) + " · 改路径再 /corerpg p1 friendpath");
                    else EmberFriendPath.offerPick(p);
                }
                return true;
            }
            int id = EmberFriendPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 friendpath <open|gate|busy|clear>");
                return true;
            }
            EmberFriendPath.applyAndReply(p, id);
            return true;
        }
        if ("claimpath".equals(sub) || "补领路径".equals(sub)) { // D515 pending claim deliver path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberClaimPath.glance(EmberClaimPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c补领路径需本人首通 Q01。");
                    return true;
                }
                EmberClaimPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c补领路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberClaimPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberClaimPath.get(d);
                    if (EmberClaimPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberClaimPath.label(cur) + " · 改路径再 /corerpg p1 claimpath");
                    else EmberClaimPath.offerPick(p);
                }
                return true;
            }
            int id = EmberClaimPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 claimpath <auto|hold|brief|clear>");
                return true;
            }
            EmberClaimPath.applyAndReply(p, id);
            return true;
        }
        if ("dailypath".equals(sub) || "日委路径".equals(sub)) { // D514 daily clear-bounty chase path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberDailyPath.glance(EmberDailyPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c日委路径需本人首通 Q01。");
                    return true;
                }
                EmberDailyPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c日委路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberDailyPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberDailyPath.get(d);
                    if (EmberDailyPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberDailyPath.label(cur) + " · 改路径再 /corerpg p1 dailypath");
                    else EmberDailyPath.offerPick(p);
                }
                return true;
            }
            int id = EmberDailyPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 dailypath <light|full|flex|clear>");
                return true;
            }
            EmberDailyPath.applyAndReply(p, id);
            return true;
        }
        if ("recruitpath".equals(sub) || "招募路径".equals(sub)) { // D513 raid recruit accept path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberRecruitPath.glance(EmberRecruitPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c招募路径需本人首通 Q07。");
                    return true;
                }
                EmberRecruitPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c招募路径需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRecruitPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRecruitPath.get(d);
                    if (EmberRecruitPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRecruitPath.label(cur) + " · 改路径再 /corerpg p1 recruitpath");
                    else EmberRecruitPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRecruitPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 recruitpath <open|gate|mute|clear>");
                return true;
            }
            EmberRecruitPath.applyAndReply(p, id);
            return true;
        }
        if ("barpath".equals(sub) || "药栏路径".equals(sub)) { // D512 potion hotbar fill path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberBarPath.glance(EmberBarPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c药栏路径需本人首通 Q01。");
                    return true;
                }
                EmberBarPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c药栏路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBarPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBarPath.get(d);
                    if (EmberBarPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBarPath.label(cur) + " · 改路径再 /corerpg p1 barpath");
                    else EmberBarPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBarPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 barpath <right|left|key5|clear>");
                return true;
            }
            EmberBarPath.applyAndReply(p, id);
            return true;
        }
        if ("feepath".equals(sub) || "层费路径".equals(sub)) { // D511 abyss fee pay path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberFeePath.glance(EmberFeePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c层费路径需本人首通 Q07。");
                    return true;
                }
                EmberFeePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c层费路径需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFeePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFeePath.get(d);
                    if (EmberFeePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFeePath.label(cur) + " · 改路径再 /corerpg p1 feepath");
                    else EmberFeePath.offerPick(p);
                }
                return true;
            }
            int id = EmberFeePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 feepath <coin|mark|auto|clear>");
                return true;
            }
            EmberFeePath.applyAndReply(p, id);
            return true;
        }
        if ("breakpath".equals(sub) || "间歇路径".equals(sub)) { // D510 rush break timing path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q04");
            if (args.length < 3) {
                p.sendMessage(P + EmberBreakPath.glance(EmberBreakPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c间歇路径需本人首通 Q04。");
                    return true;
                }
                EmberBreakPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c间歇路径需本人首通 Q04。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBreakPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBreakPath.get(d);
                    if (EmberBreakPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBreakPath.label(cur) + " · 改路径再 /corerpg p1 breakpath");
                    else EmberBreakPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBreakPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 breakpath <full|short|snap|clear>");
                return true;
            }
            EmberBreakPath.applyAndReply(p, id);
            return true;
        }
        if ("restpath".equals(sub) || "休整路径".equals(sub)) { // D509 rush rest heal path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q04");
            if (args.length < 3) {
                p.sendMessage(P + EmberRestPath.glance(EmberRestPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c休整路径需本人首通 Q04。");
                    return true;
                }
                EmberRestPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c休整路径需本人首通 Q04。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRestPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRestPath.get(d);
                    if (EmberRestPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRestPath.label(cur) + " · 改路径再 /corerpg p1 restpath");
                    else EmberRestPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRestPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 restpath <full|light|bare|clear>");
                return true;
            }
            EmberRestPath.applyAndReply(p, id);
            return true;
        }
        if ("bountypath".equals(sub) || "委托路径".equals(sub) || "花样委托路径".equals(sub)) { // D508 variety bounty path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q02");
            if (args.length < 3) {
                p.sendMessage(P + EmberBountyPath.glance(EmberBountyPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c委托路径需本人首通 Q02。");
                    return true;
                }
                String vb = runs.varietyBountyLine(d);
                if (vb != null && !vb.isEmpty()) p.sendMessage(P + "§7今日花样委托：§f" + vb);
                EmberBountyPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c委托路径需本人首通 Q02。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberBountyPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberBountyPath.get(d);
                    if (EmberBountyPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberBountyPath.label(cur) + " · 改路径再 /corerpg p1 bountypath");
                    else EmberBountyPath.offerPick(p);
                }
                return true;
            }
            int id = EmberBountyPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 bountypath <affix|event|both|clear>");
                return true;
            }
            EmberBountyPath.applyAndReply(p, id);
            return true;
        }
        if ("failpath".equals(sub) || "败退路径".equals(sub) || "退体路径".equals(sub)) { // D507 fail stamina refund path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberFailPath.glance(EmberFailPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c败退路径需本人首通 Q07。");
                    return true;
                }
                EmberFailPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c败退路径需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFailPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFailPath.get(d);
                    if (EmberFailPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFailPath.label(cur) + " · 改路径再 /corerpg p1 failpath");
                    else EmberFailPath.offerPick(p);
                }
                return true;
            }
            int id = EmberFailPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 failpath <keep|light|skip|clear>");
                return true;
            }
            EmberFailPath.applyAndReply(p, id);
            return true;
        }
        if ("roompath".equals(sub) || "房序路径".equals(sub)) { // D506 variety room-order path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q02");
            if (args.length < 3) {
                p.sendMessage(P + EmberRoomPath.glance(EmberRoomPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c房序路径需本人首通 Q02。");
                    return true;
                }
                EmberRoomPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c房序路径需本人首通 Q02。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRoomPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRoomPath.get(d);
                    if (EmberRoomPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRoomPath.label(cur) + " · 改路径再 /corerpg p1 roompath");
                    else EmberRoomPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRoomPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 roompath <front|mid|back|clear>");
                return true;
            }
            EmberRoomPath.applyAndReply(p, id);
            return true;
        }
        if ("refundpath".equals(sub) || "倒退路径".equals(sub) || "退药路径".equals(sub)) { // D505 death refund path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberRefundPath.glance(EmberRefundPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c倒退路径需本人首通 Q01。");
                    return true;
                }
                EmberRefundPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c倒退路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRefundPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRefundPath.get(d);
                    if (EmberRefundPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRefundPath.label(cur) + " · 改路径再 /corerpg p1 refundpath");
                    else EmberRefundPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRefundPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 refundpath <full|light|bare|clear>");
                return true;
            }
            EmberRefundPath.applyAndReply(p, id);
            return true;
        }
        if ("twistpath".equals(sub) || "变招路径".equals(sub)) { // D504 elite twist path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberTwistPath.glance(EmberTwistPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c变招路径需本人首通 Q01。");
                    return true;
                }
                EmberTwistPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c变招路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTwistPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTwistPath.get(d);
                    if (EmberTwistPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTwistPath.label(cur) + " · 改路径再 /corerpg p1 twistpath");
                    else EmberTwistPath.offerPick(p);
                }
                return true;
            }
            int id = EmberTwistPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 twistpath <prim|alt|rotate|clear>");
                return true;
            }
            EmberTwistPath.applyAndReply(p, id);
            return true;
        }
        if ("shortpath".equals(sub) || "短征路径".equals(sub)) { // D503 short chase path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberShortPath.glance(EmberShortPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c短征路径需本人首通 Q01。");
                    return true;
                }
                EmberRunMaps.MapDef next = EmberShortPath.resolve(runs, d, EmberShortPath.get(d));
                if (next != null) p.sendMessage(P + EmberShortPath.resolveLine(next));
                EmberShortPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c短征路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberShortPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberShortPath.get(d);
                    if (EmberShortPath.valid(cur)) {
                        p.sendMessage(P + "§7当前：" + EmberShortPath.label(cur));
                        EmberRunMaps.MapDef next = EmberShortPath.resolve(runs, d, cur);
                        p.sendMessage(P + EmberShortPath.resolveLine(next));
                    } else EmberShortPath.offerPick(p);
                }
                return true;
            }
            int id = EmberShortPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 shortpath <fc|day|late|clear>");
                return true;
            }
            EmberShortPath.applyAndReply(p, id);
            return true;
        }
        if ("preppath".equals(sub) || "备药路径".equals(sub)) { // D502 potion prep auto top-up
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberPrepPath.glance(EmberPrepPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c备药路径需本人首通 Q01。");
                    return true;
                }
                EmberPrepPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c备药路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPrepPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPrepPath.get(d);
                    if (EmberPrepPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPrepPath.label(cur) + " · 改路径再 /corerpg p1 preppath");
                    else EmberPrepPath.offerPick(p);
                }
                return true;
            }
            int id = EmberPrepPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 preppath <light|full|bare|clear>");
                return true;
            }
            EmberPrepPath.applyAndReply(p, id);
            return true;
        }
        if ("extrapath".equals(sub) || "加料路径".equals(sub)) { // D501 extra path (treasure/elite/chest bias)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberExtraPath.glance(EmberExtraPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c加料路径需本人首通 Q01。");
                    return true;
                }
                EmberExtraPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c加料路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberExtraPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberExtraPath.get(d);
                    if (EmberExtraPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberExtraPath.label(cur) + " · 改路径再 /corerpg p1 extrapath");
                    else EmberExtraPath.offerPick(p);
                }
                return true;
            }
            int id = EmberExtraPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 extrapath <treasure|elite|chest|clear>");
                return true;
            }
            EmberExtraPath.applyAndReply(p, id);
            return true;
        }
        if ("spicepath".equals(sub) || "花样路径".equals(sub)) { // D500 spice encounter bias (variety roll)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberSignature.IMPRINT_UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberSpicePath.glance(EmberSpicePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c花样路径需本人首通 Q02。");
                    return true;
                }
                EmberSpicePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c花样路径需本人首通 Q02。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberSpicePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSpicePath.get(d);
                    if (EmberSpicePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberSpicePath.label(cur) + " · 改路径再 /corerpg p1 spicepath");
                    else EmberSpicePath.offerPick(p);
                }
                return true;
            }
            int id = EmberSpicePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 spicepath <blaze|control|objective|clear>");
                return true;
            }
            EmberSpicePath.applyAndReply(p, id);
            return true;
        }
        if ("stridepath".equals(sub) || "步态路径".equals(sub)) { // D499 stride path (flex + step together)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01") && EmberSkillKit.stepVariantUnlocked(d, runs);
            if (args.length < 3) {
                p.sendMessage(P + EmberStridePath.glance(EmberStridePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c步态需本人首通 Q01（轻技）与 Q05（身法）。");
                    return true;
                }
                EmberStridePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c步态需本人首通 Q01 与 Q05。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberStridePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberStridePath.get(d);
                    if (EmberStridePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberStridePath.label(cur) + " · 改路径再 /corerpg p1 stridepath");
                    else EmberStridePath.offerPick(p);
                }
                return true;
            }
            int id = EmberStridePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 stridepath <push|bail|bare|clear>");
                return true;
            }
            EmberStridePath.applyAndReply(p, id);
            return true;
        }
        if ("tunepath".equals(sub) || "签律路径".equals(sub)) { // D498 tune path (duallead + attune together)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberSignature.ALT_UNLOCK)
                    && runs.progressFlag(d, EmberSignature.DUAL_UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberTunePath.glance(EmberTunePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c签律需本人首通 Q03（双签）与 Q07（调律）。");
                    return true;
                }
                EmberTunePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c签律需本人首通 Q03 与 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTunePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTunePath.get(d);
                    if (EmberTunePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTunePath.label(cur) + " · 改路径再 /corerpg p1 tunepath");
                    else EmberTunePath.offerPick(p);
                }
                return true;
            }
            int id = EmberTunePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 tunepath <sharp|ward|full|clear>");
                return true;
            }
            EmberTunePath.applyAndReply(p, id);
            return true;
        }
        if ("sealpath".equals(sub) || "纹印路径".equals(sub)) { // D497 seal path (brand sticky + duallead slots)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberSignature.DUAL_UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberSealPath.glance(EmberSealPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c纹印需本人首通 Q03（双签名解锁）。");
                    return true;
                }
                EmberSealPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c纹印需本人首通 Q03。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberSealPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSealPath.get(d);
                    if (EmberSealPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberSealPath.label(cur) + " · 改路径再 /corerpg p1 sealpath");
                    else EmberSealPath.offerPick(p);
                }
                return true;
            }
            int id = EmberSealPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 sealpath <hunt|ember|bind|clear>");
                return true;
            }
            EmberSealPath.applyAndReply(p, id);
            return true;
        }
        if ("trialpath".equals(sub) || "试炼路径".equals(sub)) { // D496 combat trial (pledge+counter together)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberPledgeService.UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberTrialPath.glance(EmberTrialPath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c试炼需本人首通 Q06 霜封哨所。");
                    return true;
                }
                EmberTrialPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c试炼需本人首通 Q06。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberTrialPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberTrialPath.get(d);
                    if (EmberTrialPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberTrialPath.label(cur) + " · 改路径再 /corerpg p1 trialpath");
                    else EmberTrialPath.offerPick(p);
                }
                return true;
            }
            int id = EmberTrialPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 trialpath <cautious|aggressive|pressure|clear>");
                return true;
            }
            EmberTrialPath.applyAndReply(p, id);
            return true;
        }
        if ("posturepath".equals(sub) || "姿态路径".equals(sub)) { // D495 combat posture (shape+step together)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = EmberSkillKit.shapeUnlocked(d, runs) && EmberSkillKit.stepVariantUnlocked(d, runs);
            if (args.length < 3) {
                p.sendMessage(P + EmberPosturePath.glance(EmberPosturePath.get(d), unlocked));
                if (!unlocked) {
                    p.sendMessage(P + "§c姿态需本人首通 Q04（符文）与 Q05（身法）。");
                    return true;
                }
                EmberPosturePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c姿态需本人首通 Q04 与 Q05。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPosturePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPosturePath.get(d);
                    if (EmberPosturePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPosturePath.label(cur) + " · 改路径再 /corerpg p1 posturepath");
                    else EmberPosturePath.offerPick(p);
                }
                return true;
            }
            int id = EmberPosturePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 posturepath <strike|guard|sweep|clear>");
                return true;
            }
            EmberPosturePath.applyAndReply(p, id);
            return true;
        }
        if ("flexpath".equals(sub) || "轻技路径".equals(sub)) { // D494 flex equip combat path (changes sneak+Q)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberFlexPath.glance(EmberFlexPath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c轻技路径需本人首通 Q01。"); return true; }
                EmberFlexPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c轻技路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFlexPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFlexPath.get(d);
                    if (EmberFlexPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFlexPath.label(cur) + " · 改路径再 /corerpg p1 flexpath");
                    else EmberFlexPath.offerPick(p);
                }
                return true;
            }
            int id = EmberFlexPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 flexpath <on|off|clear>");
                return true;
            }
            EmberFlexPath.applyAndReply(p, id);
            return true;
        }
        if ("familypath".equals(sub) || "套装打法".equals(sub)) { // D493 set-family combat path (focus+loot+identity)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            if (args.length < 3) {
                p.sendMessage(P + EmberFamilyPath.glance(EmberFamilyPath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c套装打法需本人首通 Q01。"); return true; }
                EmberFamilyPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c套装打法需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFamilyPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFamilyPath.get(d);
                    if (EmberFamilyPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFamilyPath.label(cur) + " · 改路径再 /corerpg p1 familypath");
                    else EmberFamilyPath.offerPick(p);
                }
                return true;
            }
            int id = EmberFamilyPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 familypath <scorch|burst|sustain|clear>");
                return true;
            }
            EmberFamilyPath.applyAndReply(p, id);
            return true;
        }
        if ("attunepath".equals(sub) || "调律路径".equals(sub)) { // D492 signature attune combat path (changes play)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberSignature.ALT_UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberAttunePath.glance(EmberAttunePath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c签名调律需本人首通 Q07。"); return true; }
                EmberAttunePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c签名调律需本人首通 Q07。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberAttunePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberAttunePath.get(d);
                    if (EmberAttunePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberAttunePath.label(cur) + " · 改路径再 /corerpg p1 attunepath");
                    else EmberAttunePath.offerPick(p);
                }
                return true;
            }
            int id = EmberAttunePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 attunepath <origin|alt|clear>");
                return true;
            }
            EmberAttunePath.applyAndReply(p, id);
            return true;
        }
        if ("steppath".equals(sub) || "身法路径".equals(sub)) { // D491 step-dir combat path (changes play)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = EmberSkillKit.stepVariantUnlocked(d, runs);
            if (args.length < 3) {
                p.sendMessage(P + EmberStepPath.glance(EmberStepPath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c身法方向需本人首通 Q05。"); return true; }
                EmberStepPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c身法方向需本人首通 Q05。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberStepPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberStepPath.get(d);
                    if (EmberStepPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberStepPath.label(cur) + " · 改路径再 /corerpg p1 steppath");
                    else EmberStepPath.offerPick(p);
                }
                return true;
            }
            int id = EmberStepPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 steppath <forward|back|clear>");
                return true;
            }
            EmberStepPath.applyAndReply(p, id);
            return true;
        }
        if ("shapepath".equals(sub) || "符文路径".equals(sub)) { // D490 slash-shape combat path (changes play)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = EmberSkillKit.shapeUnlocked(d, runs);
            if (args.length < 3) {
                p.sendMessage(P + EmberShapePath.glance(EmberShapePath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c烬斩符文需本人首通 Q04。"); return true; }
                EmberShapePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c烬斩符文需本人首通 Q04。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberShapePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberShapePath.get(d);
                    if (EmberShapePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberShapePath.label(cur) + " · 改路径再 /corerpg p1 shapepath");
                    else EmberShapePath.offerPick(p);
                }
                return true;
            }
            int id = EmberShapePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 shapepath <fan|line|ring|clear>");
                return true;
            }
            EmberShapePath.applyAndReply(p, id);
            return true;
        }
        if ("featurepath".equals(sub) || "精选路径".equals(sub)) { // D488 featured map identity path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q01");
            String fl = runs.featuredLabel(d);
            if (args.length < 3) {
                p.sendMessage(P + EmberFeaturedPath.glance(EmberFeaturedPath.get(d), unlocked, fl));
                if (!unlocked) { p.sendMessage(P + "§c精选路径需本人首通 Q01。"); return true; }
                EmberFeaturedPath.offerPick(p, fl);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c精选路径需本人首通 Q01。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberFeaturedPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberFeaturedPath.get(d);
                    if (EmberFeaturedPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberFeaturedPath.label(cur) + " · 改路径再 /corerpg p1 featurepath");
                    else EmberFeaturedPath.offerPick(p, fl);
                }
                return true;
            }
            int id = EmberFeaturedPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 featurepath <challenge|normal|clear>");
                return true;
            }
            EmberFeaturedPath.applyAndReply(p, id);
            return true;
        }
        if ("abysspath".equals(sub) || "深渊路径".equals(sub)) { // D486 abyss push/farm combat path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.abyss() != null && runs.abyss().open(d);
            if (args.length < 3) {
                p.sendMessage(P + EmberAbyssPath.glance(EmberAbyssPath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c深渊需本人首通 Q07 断塔回廊。"); return true; }
                EmberAbyssPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c深渊需本人首通 Q07 断塔回廊。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberAbyssPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberAbyssPath.get(d);
                    if (EmberAbyssPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberAbyssPath.label(cur) + " · 改路径再 /corerpg p1 abysspath");
                    else EmberAbyssPath.offerPick(p);
                }
                return true;
            }
            int id = EmberAbyssPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 abysspath <push|farm|clear>");
                return true;
            }
            EmberAbyssPath.applyAndReply(p, id);
            return true;
        }
        if ("raidpath".equals(sub) || "团本路径".equals(sub)) { // D483 raid focus combat path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, "q07");
            if (args.length < 3) {
                p.sendMessage(P + EmberRaidPath.glance(EmberRaidPath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c团本需本人首通 Q07 断塔回廊。"); return true; }
                EmberRaidPath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c团本需本人首通 Q07 断塔回廊。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberRaidPath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberRaidPath.get(d);
                    if (EmberRaidPath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberRaidPath.label(cur) + " · 改路径再 /corerpg p1 raidpath");
                    else EmberRaidPath.offerPick(p);
                }
                return true;
            }
            int id = EmberRaidPath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 raidpath <r01|r02|r03|clear>");
                return true;
            }
            EmberRaidPath.applyAndReply(p, id);
            return true;
        }
        if ("upgradeloop".equals(sub)) { // D487 admin smoke: upgrade → power spend chase
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            int n = 1;
            if (args.length >= 3) try { n = Math.max(1, Integer.parseInt(args[2])); } catch (NumberFormatException ignored) {}
            for (int i = 0; i < Math.min(3, n); i++) EmberUpgradeLoop.afterUpgrade(p);
            p.sendMessage(P + "§8upgradeloop · " + EmberUpgradeLoop.followHint());
            return true;
        }
        if ("enhanceloop".equals(sub)) { // D485 admin smoke: enhance → forge-branch spend chase
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            boolean won = true;
            if (args.length >= 3) {
                String a = args[2].toLowerCase(Locale.ROOT);
                if ("fail".equals(a) || "miss".equals(a) || "失败".equals(a)) won = false;
            }
            EmberEnhanceLoop.afterEnhance(p, won);
            p.sendMessage(P + "§8enhanceloop · " + EmberEnhanceLoop.followHint(won));
            return true;
        }
        if ("swaploop".equals(sub)) { // D484 admin smoke: free enhance-swap → spend chase buttons
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            int n = 1;
            if (args.length >= 3) try { n = Math.max(1, Integer.parseInt(args[2])); } catch (NumberFormatException ignored) {}
            for (int i = 0; i < Math.min(3, n); i++) EmberSwapLoop.afterSwap(p);
            p.sendMessage(P + "§8swaploop · " + EmberSwapLoop.followHint());
            return true;
        }
        if ("blankloop".equals(sub)) { // D482 admin smoke: dismantle→blank spend chase buttons
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            int n = 2;
            if (args.length >= 3) {
                try { n = Integer.parseInt(args[2]); } catch (NumberFormatException e) { n = 2; }
            }
            EmberBlankLoop.afterDismantle(p, Math.max(1, Math.min(99, n)));
            return true;
        }
        if ("pledgepath".equals(sub) || "誓约路径".equals(sub)) { // D481 self-pledge combat path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean unlocked = runs.progressFlag(d, EmberPledgeService.UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberPledgePath.glance(EmberPledgePath.get(d), unlocked));
                if (!unlocked) { p.sendMessage(P + "§c自选誓约需本人首通 Q06 霜封哨所。"); return true; }
                EmberPledgePath.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                if (!unlocked) { p.sendMessage(P + "§c自选誓约需本人首通 Q06 霜封哨所。"); return true; }
                String week = EmberPlayfeelTelemetry.weekKey();
                boolean offered = EmberPledgePath.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberPledgePath.get(d);
                    if (EmberPledgePath.valid(cur)) p.sendMessage(P + "§7当前：" + EmberPledgePath.label(cur) + " · 改路径再 /corerpg p1 pledgepath");
                    else EmberPledgePath.offerPick(p);
                }
                return true;
            }
            int id = EmberPledgePath.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 pledgepath <lean|reverse|both|clear>");
                return true;
            }
            EmberPledgePath.applyAndReply(p, id);
            return true;
        }
        if ("duallead".equals(sub) || "双签路径".equals(sub)) { // D466 dual-sig first-path
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            boolean dual = runs.progressFlag(d, EmberSignature.DUAL_UNLOCK);
            if (args.length < 3) {
                p.sendMessage(P + EmberDualLead.glance(EmberDualLead.get(d), dual));
                if (dual) EmberDualLead.forceOffer(p);
                else p.sendMessage(P + "§7首通 Q03 后可选刃优先 / 护符优先 / 两条都开");
                return true;
            }
            if ("offer".equalsIgnoreCase(args[2]) || "nudge".equalsIgnoreCase(args[2])) {
                if (!dual) { p.sendMessage(P + "§c需本人首通 Q03（双签名）"); return true; }
                EmberDualLead.forceOffer(p);
                return true;
            }
            int id = EmberDualLead.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 duallead <blade|charm|both|clear|offer>");
                return true;
            }
            if (id != EmberDualLead.NONE && !dual) {
                p.sendMessage(P + "§c需本人首通 Q03 后才能开双签路径");
                return true;
            }
            EmberDualLead.apply(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberDualLead.NONE) p.sendMessage(P + "已清除双签路径偏好（开关槽位未改）");
            else {
                boolean bOff = d.periodCount(EmberGrowthService.C_SIGOFF + "blade", "all") > 0;
                boolean cOff = d.periodCount(EmberGrowthService.C_SIGOFF + "charm", "all") > 0;
                p.sendMessage(P + "§a双签路径 → §f" + EmberDualLead.label(id) + "§7（" + EmberDualLead.tip(id) + "）");
                p.sendMessage(P + EmberDualLead.glance(id, true)
                        + " §8· 刃" + (bOff ? "关" : "开") + " · 护符" + (cOff ? "关" : "开"));
            }
            return true;
        }
        if ("setfocus".equals(sub) || "套装焦点".equals(sub)) { // D465 set-family playstyle focus
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            if (args.length < 3) {
                EmberLoadout lo = runs.loadouts() != null ? runs.loadouts().get(p) : null;
                String active = lo == null ? "none" : lo.activeSet;
                int awk = lo == null ? 0 : lo.awakening;
                p.sendMessage(P + EmberSetFocus.glance(EmberSetFocus.get(d), active, awk, runs.target(d)));
                EmberSetFocus.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                String week = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Shanghai")));
                boolean offered = EmberSetFocus.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberSetFocus.get(d);
                    if (EmberSetFocus.valid(cur)) p.sendMessage(P + "§7当前焦点：" + EmberSetFocus.label(cur) + " · 改焦点再 /corerpg p1 setfocus");
                }
                return true;
            }
            int id = EmberSetFocus.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 setfocus <scorch|burst|sustain|clear>");
                return true;
            }
            boolean ch = EmberSetFocus.set(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberSetFocus.NONE) p.sendMessage(P + "已取消套装焦点");
            else {
                p.sendMessage(P + "§a套装焦点 → §f" + EmberSetFocus.label(id) + "§7（" + EmberSetFocus.tip(id) + "）"
                        + (ch ? "" : " §8· 已是该焦点"));
                EmberLoadout lo = runs.loadouts() != null ? runs.loadouts().get(p) : null;
                p.sendMessage(P + EmberSetFocus.glance(id, lo == null ? "none" : lo.activeSet, lo == null ? 0 : lo.awakening, runs.target(d)));
                // optional: align loot target button
                String loot = runs.target(d);
                String fk = EmberSetFocus.familyKey(id);
                if (fk != null && (loot == null || !fk.equals(loot))) {
                    town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7掉落目标族还可对齐：",
                            "[掉落也设成" + EmberSetFocus.label(id) + "]", "/corerpg p1 target " + fk,
                            "约六成掉该族 · 可另改");
                }
            }
            return true;
        }
        if ("forge".equals(sub) || "工坊".equals(sub) || "workshop".equals(sub)) { // D473 open forge menu
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(),
                    "trmenu open ember_p1_forge " + p.getName());
            return true;
        }
        if ("forgegoal".equals(sub) || "工坊目标".equals(sub)) { // D461 forge craft-goal path pick
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            PlayerData d = runs.dataOf(p.getUniqueId());
            if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
            if (args.length < 3) {
                int cur = EmberForgeGoal.get(d);
                p.sendMessage(P + EmberForgeGoal.glance(cur, ""));
                EmberForgeGoal.offerPick(p);
                return true;
            }
            if ("nudge".equalsIgnoreCase(args[2]) || "offer".equalsIgnoreCase(args[2])) {
                String week = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Shanghai")));
                boolean offered = EmberForgeGoal.maybeOfferWeekly(p, d, week);
                runs.plugin().getDataStore().flushMutation(p.getUniqueId());
                if (!offered) {
                    int cur = EmberForgeGoal.get(d);
                    if (EmberForgeGoal.valid(cur)) p.sendMessage(P + "§7当前：" + EmberForgeGoal.label(cur) + " · 改目标再 /corerpg p1 forgegoal");
                }
                return true;
            }
            int id = EmberForgeGoal.parse(args[2]);
            if (id < 0) {
                p.sendMessage(P + "用法：/corerpg p1 forgegoal <enhance|refine|brand|roll|convert|clear>");
                return true;
            }
            boolean ch = EmberForgeGoal.set(d, id);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            if (id == EmberForgeGoal.NONE) p.sendMessage(P + "已取消工坊目标");
            else p.sendMessage(P + "§a工坊目标 → §f" + EmberForgeGoal.label(id)
                    + "§7（" + EmberForgeGoal.tip(id) + "）" + (ch ? "" : " §8· 已是该目标"));
            return true;
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
        if ("slashready".equals(sub)) { // D479 admin smoke: shared 烬斩 charge CD-ready ActionBar
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            int sec = 1;
            if (args.length >= 3) {
                try { sec = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sec = 1; }
            }
            EmberSlashReady.adminArmSeconds(p, sec);
            p.sendMessage(P + "§8slashready · CD " + Math.max(0, Math.min(60, sec)) + "s 后 ActionBar: " + EmberSlashReady.readyActionBar());
            return true;
        }
        if ("matready".equals(sub)) { // D478 admin smoke: forge mat-ready rising-edge sample / live check
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            if (args.length >= 3 && "live".equalsIgnoreCase(args[2])) {
                boolean hit = EmberForgeMatReady.maybeReadyCue(p);
                p.sendMessage(P + "§8matready live · " + (hit ? "rising-edge fired" : "no cue (held/afford/latch)"));
                return true;
            }
            int from = 2, to = 3;
            if (args.length >= 4) {
                try { from = Integer.parseInt(args[2]); to = Integer.parseInt(args[3]); } catch (NumberFormatException ignored) { }
            }
            EmberForgeMatReady.adminSample(p, from, to);
            return true;
        }
        if ("mapcard".equals(sub)) { // D489 admin smoke: map-enter chat 名片+loot (no ActionBar)
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
            String key = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "q01";
            boolean challenge = args.length >= 4 && "challenge".equalsIgnoreCase(args[3]);
            int abyss = 0;
            if (args.length >= 4) {
                try { abyss = Integer.parseInt(args[3]); } catch (NumberFormatException ignored) { }
            }
            EmberRunMaps.MapDef m = runs.maps().byKey(key);
            if (m == null) {
                // sample without MapDef
                String mode = challenge ? "挑战" : (abyss > 0 ? "深渊" + abyss : "主线");
                String msg = EmberMapCardCue.chatLine(key.toUpperCase(Locale.ROOT) + " 样例", mode, "样例名片", "不偏向");
                p.sendMessage(P + msg);
                p.sendMessage(P + "§8mapcard sample · unknown key " + key);
                return true;
            }
            EmberMapCardCue.cueSample(p, m, challenge, abyss);
            p.sendMessage(P + "§8mapcard · " + EmberMapCardCue.followHint() + " · " + key
                    + (challenge ? " challenge" : "") + (abyss > 0 ? " abyss" + abyss : ""));
            return true;
        }
        if ("mapenter".equals(sub)) { // D477 admin smoke: map-enter ActionBar sample
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            String kind = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "main";
            String name = "主线样例";
            String mode = EmberMapEnterFeel.modeTag(false, false, null, false, false, 0);
            if ("echo".equals(kind)) { name = "残响样例"; mode = EmberMapEnterFeel.modeTag(false, true, "echo", false, false, 0); }
            else if ("outpost".equals(kind)) { name = "前哨样例"; mode = EmberMapEnterFeel.modeTag(false, true, "outpost", false, false, 0); }
            else if ("raid".equals(kind)) { name = "团本样例"; mode = EmberMapEnterFeel.modeTag(false, false, null, true, false, 0); }
            else if ("short".equals(kind)) { name = "短征样例"; mode = EmberMapEnterFeel.modeTag(true, false, null, false, false, 0); }
            else if ("abyss".equals(kind)) { name = "深渊样例"; mode = EmberMapEnterFeel.modeTag(false, false, null, false, true, 3); }
            else if ("challenge".equals(kind)) { name = "挑战样例"; mode = EmberMapEnterFeel.modeTag(false, false, null, false, true, 0); }
            String msg = EmberMapEnterFeel.line(name, mode);
            EmberMapEnterFeel.flashLine(p, msg);
            p.sendMessage(P + "§8mapenter sample · " + msg);
            return true;
        }
        if ("parryready".equals(sub)) { // D476 admin smoke: schedule parry CD-ready ActionBar
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            EmberParry parry = EmberParry.get();
            if (parry == null) { p.sendMessage(P + "招架服务未就绪"); return true; }
            int sec = 1;
            if (args.length >= 3) {
                try { sec = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sec = 1; }
            }
            parry.adminArmCdSeconds(p, sec);
            p.sendMessage(P + "§8parryready · CD " + Math.max(0, Math.min(60, sec)) + "s 后 ActionBar: " + EmberParry.readyActionBar());
            return true;
        }
        if ("forgefeel".equals(sub)) { // D475 admin smoke: ActionBar forge result samples
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            String kind = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "enhance";
            String note = "enhance".equals(kind) ? "强化成功 +2 → +3（本档第 1/3 次）"
                    : "refine".equals(kind) ? "精工 0% → 2%（确定成功，成色不变）"
                    : "quality".equals(kind) ? "成色 标准 → 精良"
                    : "upgrade".equals(kind) ? "升阶 T1 → T2"
                    : "强化失败，保持 +2（本档已失败 1 次，最多再 2 次必成）";
            if ("fail".equals(kind)) { kind = "enhance"; note = "强化失败，保持 +2（本档已失败 1 次，最多再 2 次必成）"; }
            EmberForgeFeel.flash(p, kind, note);
            p.sendMessage(P + "§8forgefeel sample · " + EmberForgeFeel.line(kind, note));
            return true;
        }
        if ("convertloop".equals(sub)) { // D474 admin smoke: replay convert success chase
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            String fam = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "scorch";
            if (!EmberRunRules.validFamily(fam)) fam = "scorch";
            PlayerData d = runs != null ? runs.dataOf(p.getUniqueId()) : null;
            EmberConvertLoop.afterConvert(p, d, fam, true);
            return true;
        }
        if ("shortspend".equals(sub)) { // D473 admin smoke: replay short settle forge-chase cue
            if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
            Player p = (Player) s;
            if (!p.hasPermission("corerpg.admin")) { p.sendMessage(P + "需要管理员"); return true; }
            int after = 3, cap = 3;
            String chase = EmberShortRules.spendChaseTell(after, cap);
            p.sendMessage(P + chase);
            try { p.sendActionBar(EmberShortRules.spendChaseActionBar(after, cap)); } catch (Throwable ignored) { }
            town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一步：",
                    EmberShortRules.spendChaseButtonLabel(),
                    EmberShortRules.spendChaseButtonCmd(),
                    EmberShortRules.spendChaseButtonHover());
            return true;
        }
        if ("skillcue".equals(sub)) { // D459 admin smoke: replay skill-unlock announce
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            String map = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
            Player t = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if (EmberSkillUnlock.unlockLine(map) == null) { s.sendMessage(P + "用法：/corerpg p1 skillcue <q02|q03|q04|q05> [玩家]"); return true; }
            EmberSkillUnlock.announce(t, map);
            s.sendMessage(P + "已向 " + t.getName() + " 宣告 " + EmberSkillUnlock.shortName(map)
                    + (EmberSkillKit.UNLOCK_SHAPE.equals(map) || EmberSkillKit.UNLOCK_STEP.equals(map)
                            ? " §7· 玩法选择约 2 秒后弹出" : ""));
            return true;
        }
        if ("skillpick".equals(sub)) { // D460 admin smoke: force shape/step first-pick buttons
            if (s instanceof Player && !s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            String kind = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
            Player t = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : (s instanceof Player ? (Player) s : null);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            if ("shape".equals(kind) || "符文".equals(kind)) {
                EmberSkillUnlock.forceOfferShape(t);
                s.sendMessage(P + "已向 " + t.getName() + " 强制弹出符文三选一");
                return true;
            }
            if ("step".equals(kind) || "dir".equals(kind) || "身法".equals(kind)) {
                EmberSkillUnlock.forceOfferStep(t);
                s.sendMessage(P + "已向 " + t.getName() + " 强制弹出身法方向二选一");
                return true;
            }
            s.sendMessage(P + "用法：/corerpg p1 skillpick <shape|step> [玩家]");
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
