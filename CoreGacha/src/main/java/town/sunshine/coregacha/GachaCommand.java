package town.sunshine.coregacha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Tier;
import town.sunshine.coregacha.sim.Simulator;

final class GachaCommand implements CommandExecutor, TabCompleter {
    private final CoreGachaPlugin pl;
    GachaCommand(CoreGachaPlugin pl) { this.pl = pl; }

    static final String P = GachaService.P;

    private void open(Player p, String menu) {
        Bukkit.getScheduler().runTask(pl, () -> { if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName()); });
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] a) {
        String sub = a.length == 0 ? "" : a[0].toLowerCase(Locale.ROOT);
        GachaService g = pl.service();
        if ("admin".equals(sub)) return admin(s, a);
        if (!(s instanceof Player)) { s.sendMessage(P + "/gacha admin reload|give-tickets|give-shards|inspect|simulate|failnext"); return true; }
        Player p = (Player) s;
        PCache pc = g.get(p.getUniqueId());
        switch (sub) {
            case "": case "open": case "menu":
                g.welcome(p);
                open(p, "ember_gacha");
                return true;
            case "pull": case "抽":
                if (a.length < 3) { p.sendMessage(P + "/gacha pull <池> <1|10>（池：" + String.join(" / ", g.cfg.banners.keySet()) + "）"); return true; }
                int n;
                try { n = Integer.parseInt(a[2]); } catch (NumberFormatException e) { n = 0; }
                g.pull(p, a[1], n);
                return true;
            case "history": case "记录":
                if (a.length >= 2 && "chat".equalsIgnoreCase(a[1])) { history(p, pc); return true; }
                open(p, "ember_gacha_history");
                return true;
            case "rates": case "概率": {
                Banner b = g.cfg.banner(a.length >= 2 ? a[1] : "standard");
                if (b == null) { p.sendMessage(P + "§c没有这个池"); return true; }
                if (pc != null) pc.view = b.id;
                if (a.length >= 3 && "chat".equalsIgnoreCase(a[2])) { rates(p, b); return true; }
                open(p, "ember_gacha_rates");
                return true;
            }
            case "shop": case "光屑":
                open(p, "ember_gacha_shop");
                return true;
            case "craft": case "兑换": {
                if (a.length < 2) { open(p, "ember_gacha_shop"); return true; }
                Item it = g.cfg.items.get(a[1]);
                if (it == null) { p.sendMessage(P + "§c没有这件：" + a[1]); return true; }
                int price = g.cfg.craftPrice.get(it.tier);
                if (a.length >= 3 && "confirm".equalsIgnoreCase(a[2])) { g.craft(p, it.id); return true; }
                if (g.owns(p.getUniqueId(), it)) { p.sendMessage(P + "你已经有「" + it.display() + "§7」了。"); return true; }
                buttons(p, P + "用 §b" + price + " 光屑§7 兑换 " + it.tier.colored() + " §f" + Item.stripColor(it.name) + " §8(" + it.kindLabel() + ")§7？现有 §b" + (pc == null ? 0 : pc.shards) + " ",
                        new String[]{"§a[确认兑换]", "/gacha craft " + it.id + " confirm", "扣 " + price + " 光屑，只做展示，不退"});
                return true;
            }
            case "spark": case "火花": {
                Banner b = g.cfg.banner(a.length >= 2 ? a[1] : "");
                if (b == null) { p.sendMessage(P + "/gacha spark <池> [外观]"); return true; }
                if (a.length >= 3) { g.spark(p, b.id, a[2]); return true; }
                PCache.BState bs = pc == null ? null : pc.banners.get(b.id);
                int sp = bs == null ? 0 : bs.spark;
                int need = g.cfg.sparkFor(b);
                long now = System.currentTimeMillis();
                p.sendMessage(P + "§f" + Item.stripColor(b.name) + " §7火花 §f" + sp + "/" + need + (sp >= need ? " §a可以兑换：点一件没有的" : " §8（满了才能兑换）")
                        + (b.sparkItems.isEmpty() ? "" : " §7· 只能换本池限定传说"));
                if (sp >= need) for (Item it : g.cfg.pool(b, now).all())
                    if (g.cfg.sparkAllows(b, it.id, now) && !g.owns(p.getUniqueId(), it)) buttons(p, "  " + it.tier.colored() + " §f" + Item.stripColor(it.name) + " §8(" + it.kindLabel() + ") ",
                            new String[]{"§a[选这件]", "/gacha spark " + b.id + " " + it.id, "扣 " + need + " 火花"});
                return true;
            }
            case "exchange": case "换券":
                if (a.length < 2) { p.sendMessage(P + "/gacha exchange <coin|badge> [张数]（" + pl.getConfig().getInt("tickets.exchange.coin") + " 余烬币 或 " + pl.getConfig().getInt("tickets.exchange.badge") + " 余烬徽 = 1 张，每天最多 " + pl.getConfig().getInt("tickets.exchange.daily_cap") + " 张）"); return true; }
                int k = 1;
                if (a.length >= 3) try { k = Integer.parseInt(a[2]); } catch (NumberFormatException ignored) { }
                g.exchange(p, a[1].toLowerCase(Locale.ROOT), k);
                return true;
            case "wear": case "外观":
                if (a.length >= 3 && "off".equalsIgnoreCase(a[1])) { g.wear(p, null, true, a[2].toLowerCase(Locale.ROOT)); p.sendMessage(P + "已取下"); return true; }
                if (a.length >= 2) {
                    Item it = g.cfg.items.get(a[1]);
                    if (it == null || it.external()) { p.sendMessage(P + "§c扭蛋外观里没有：" + a[1] + "（CoreRpg 外观在外观商店换上）"); return true; }
                    g.wear(p, it, false, null);
                    if (pc != null && pc.owned.contains(it.id)) p.sendMessage(P + "已装上" + it.kindLabel() + "「" + it.display() + "§7」" + ("show".equals(it.kind) ? "（/gacha fx 放）" : ""));
                    return true;
                }
                wardrobe(p, pc);
                return true;
            case "fx": case "烟火":
                pl.render().show(p);
                return true;
            case "redeem": case "存券":
                pl.sources().redeem(p);
                return true;
            default:
                p.sendMessage(P + "/gacha · pull <池> <1|10> · history · rates <池> · shop · craft <外观> · spark <池> · exchange <coin|badge> [张数] · wear · fx · redeem");
                return true;
        }
    }

    static void buttons(Player p, String text, String[]... btn) {
        TextComponent t = new TextComponent(text);
        for (String[] b : btn) {
            TextComponent c = new TextComponent(b[0] + " ");
            c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, b[1]));
            if (b.length > 2) c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(b[2])}));
            t.addExtra(c);
        }
        p.spigot().sendMessage(t);
    }

    private void wardrobe(Player p, PCache pc) {
        if (pc == null) return;
        GachaConfig c = pl.service().cfg;
        p.sendMessage(P + "§f我的扭蛋外观§7（只做展示，不加属性；每种装一件）· CoreRpg 外观商店的件在 /corerpg p1 cosmetic 里换上");
        for (String kind : Arrays.asList("badge", "tag", "aura", "pet", "show")) {
            List<Item> own = new ArrayList<Item>();
            for (Item it : c.items.values()) if (it.kind.equals(kind) && pc.owned.contains(it.id)) own.add(it);
            String cur = pc.wear.get(kind);
            String kl = own.isEmpty() ? new Item("x", "", Tier.COMMON, kind, null, null, null).kindLabel() : own.get(0).kindLabel();
            if (own.isEmpty()) { p.sendMessage(P + "§e" + kl + "§8：还没有"); continue; }
            TextComponent t = new TextComponent(P + "§e" + kl + "§7：");
            for (Item it : own) {
                boolean on = it.id.equals(cur);
                TextComponent b = new TextComponent((on ? "§a§n" : "§f") + Item.stripColor(it.name) + "§r ");
                b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, on ? "/gacha wear off " + kind : "/gacha wear " + it.id));
                b.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(it.tier.colored() + " §7" + it.prop("desc", it.kindLabel()) + (on ? "\n§7点击取下" : "\n§a点击装上"))}));
                t.addExtra(b);
            }
            p.spigot().sendMessage(t);
        }
    }

    private void history(Player p, PCache pc) {
        if (pc == null) return;
        p.sendMessage(P + "最近 " + pc.hist.size() + " 抽（新的在上）：");
        int i = 0;
        for (PCache.Hist h : pc.hist) { if (i++ >= 20) break; p.sendMessage("  " + pl.expansion().histLine(h)); }
    }

    private void rates(Player p, Banner b) {
        GachaService g = pl.service();
        for (String l : pl.expansion().rateLines(b)) p.sendMessage(P + l);
        for (Tier t : Tier.values()) p.sendMessage(P + pl.expansion().itemLines(b, t).replace("\n", " "));
    }

    private boolean admin(CommandSender s, String[] a) {
        if (!s.hasPermission("coregacha.admin")) { s.sendMessage(P + "§c需要 coregacha.admin"); return true; }
        String op = a.length >= 2 ? a[1].toLowerCase(Locale.ROOT) : "";
        GachaService g = pl.service();
        switch (op) {
            case "reload":
                if (pl.reloadAll()) s.sendMessage(P + "已重载：池 " + g.cfg.banners.keySet() + " · 物品 " + g.cfg.items.size() + " · 传说综合 " + RateMath.pct(g.rates().effective.get(Tier.LEGEND))
                        + (g.cfg.disabled.isEmpty() ? "" : " · 停用 " + g.cfg.disabled) + (g.cfg.warnings.isEmpty() ? "" : " · 警告 " + g.cfg.warnings));
                else s.sendMessage(P + "§c重载失败（看日志），仍用旧配置");
                return true;
            case "give-tickets": case "give": {
                if (a.length < 4) { s.sendMessage(P + "/gacha admin give-tickets <玩家> <n>"); return true; }
                int n;
                try { n = Integer.parseInt(a[3]); } catch (NumberFormatException e) { s.sendMessage(P + "数量不对"); return true; }
                UUID u = uuid(a[2]);
                if (u == null) { s.sendMessage(P + "§c找不到玩家 " + a[2]); return true; }
                g.giveAdmin(s, u, a[2], n);
                return true;
            }
            case "give-shards": {
                if (a.length < 4) { s.sendMessage(P + "/gacha admin give-shards <玩家> <n>"); return true; }
                int n;
                try { n = Integer.parseInt(a[3]); } catch (NumberFormatException e) { s.sendMessage(P + "数量不对"); return true; }
                UUID u = uuid(a[2]);
                if (u == null) { s.sendMessage(P + "§c找不到玩家 " + a[2]); return true; }
                g.giveShardsAdmin(s, u, a[2], n);
                return true;
            }
            case "inspect": {
                if (a.length < 3) { s.sendMessage(P + "/gacha admin inspect <玩家>"); return true; }
                UUID u = uuid(a[2]);
                if (u == null) { s.sendMessage(P + "§c找不到玩家 " + a[2]); return true; }
                g.inspect(s, u, a[2]);
                return true;
            }
            case "failnext": { // test hook: the next CoreRpg grant for this player fails → whole batch rolls back
                Player t = a.length >= 3 ? Bukkit.getPlayerExact(a[2]) : null;
                if (t == null) { s.sendMessage(P + "/gacha admin failnext <在线玩家>"); return true; }
                g.failNext.add(t.getUniqueId());
                s.sendMessage(P + t.getName() + " 下一次 CoreRpg 外观发放会失败（测试回滚退券）");
                return true;
            }
            case "simulate": case "sim": {
                Banner b = g.cfg.banner(a.length >= 3 ? a[2] : "standard");
                long n = 100000;
                if (a.length >= 4) try { n = Math.min(5_000_000L, Long.parseLong(a[3])); } catch (NumberFormatException ignored) { }
                if (b == null) { s.sendMessage(P + "/gacha admin simulate <池> <n>"); return true; }
                final long nn = n;
                final GachaConfig c = g.cfg;
                final RateMath rm = g.rates();
                long now = System.currentTimeMillis();
                final long at = b.open(now) ? now : (b.limited() ? b.start + 1 : now);
                s.sendMessage(P + "模拟 " + b.id + " " + nn + " 抽（真实引擎，不写库）…");
                Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                    Simulator r = Simulator.run(c, b, at, nn, System.nanoTime());
                    List<String> out = new ArrayList<String>();
                    for (Tier t : Tier.values()) {
                        double pp = rm.effective.get(t), sd = Math.sqrt(pp * (1 - pp) / nn);
                        out.add(String.format(Locale.ROOT, "%s 公示 %s · 观测 %s（%d 次，%+.2fσ）", t.colored() + "§7", RateMath.pct(pp), RateMath.pct(r.rate(t)), r.tierCount.get(t), (r.rate(t) - pp) / sd));
                    }
                    out.add("最长无传说 " + r.maxLegendGap + "/" + c.hardPity + " · 最长无史诗+ " + r.maxEpicGap + "/" + c.tier2Every + " · 连出同一传说 " + r.sameLegendTwice + " 次 · 硬保底 " + r.ruleCount.getOrDefault("hard", 0L) + " 次");
                    for (Map.Entry<String, Long> e : r.legendCount.entrySet()) out.add("  传说 " + e.getKey() + " ×" + e.getValue());
                    Bukkit.getScheduler().runTask(pl, () -> { for (String l : out) s.sendMessage(P + l); });
                });
                return true;
            }
            default:
                s.sendMessage(P + "/gacha admin reload | give-tickets <玩家> <n> | give-shards <玩家> <n> | inspect <玩家> | simulate <池> <n> | failnext <玩家>");
                return true;
        }
    }

    @SuppressWarnings("deprecation")
    private static UUID uuid(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) return p.getUniqueId();
        OfflinePlayer o = Bukkit.getOfflinePlayer(name);
        return o != null && o.hasPlayedBefore() ? o.getUniqueId() : null;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> out = new ArrayList<String>();
        GachaService g = pl.service();
        if (a.length == 1) out.addAll(Arrays.asList("pull", "history", "rates", "shop", "craft", "spark", "exchange", "wear", "fx", "redeem"));
        else if (a.length == 2 && Arrays.asList("pull", "rates", "spark").contains(a[0].toLowerCase(Locale.ROOT))) out.addAll(g.cfg.banners.keySet());
        else if (a.length == 3 && "pull".equalsIgnoreCase(a[0])) out.addAll(Arrays.asList("1", "10"));
        else if (a.length == 2 && "exchange".equalsIgnoreCase(a[0])) out.addAll(Arrays.asList("coin", "badge"));
        else if (a.length == 2 && "craft".equalsIgnoreCase(a[0])) for (Item i : g.craftable()) out.add(i.id);
        String last = a.length == 0 ? "" : a[a.length - 1].toLowerCase(Locale.ROOT);
        out.removeIf(x -> !x.toLowerCase(Locale.ROOT).startsWith(last));
        return out;
    }
}
