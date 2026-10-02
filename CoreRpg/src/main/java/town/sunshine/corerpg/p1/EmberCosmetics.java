package town.sunshine.corerpg.p1;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import town.sunshine.corerpg.PlayerData;

/**
 * P2-9 (D83) prestige cosmetics: titles (chat prefix + PAPI) and particle trails. No stats, no drop, no shop value —
 * proof of a clear only (book §23.3). Earned flags are period counters ("p2_title_<id>@all" = number of clears;
 * abyss titles read the abyss best tier). The selection is one counter whose period is the chosen id.
 */
public final class EmberCosmetics implements Listener {

    public enum Kind { TITLE, TRAIL, COLOR, FLAIR }

    public static final class Cosmetic {
        public final String id, label, how;
        public final Kind kind;
        public final int abyssTier;   // > 0: earned by abyss best >= this tier
        public final String raid;     // non-null: earned by a clear of this raid key
        public final String particle; // trails only
        public final String firstClear; // non-null: earned by the first clear of this main map (D103 milestones)
        public final int price;         // D107 > 0: bought with 余烬币 in the cosmetic shop (display only)
        public final String style;      // D107 COLOR: colour code for owned titles · FLAIR: nameplate prefix
        Cosmetic(String id, Kind kind, String label, String how, int price, String style, String particle, boolean shop) {
            this.id = id; this.kind = kind; this.label = label; this.how = how; this.abyssTier = 0; this.raid = null;
            this.particle = particle; this.firstClear = null; this.price = price; this.style = style;
        }
        Cosmetic(String id, Kind kind, String label, String how, int abyssTier, String raid, String particle) {
            this(id, kind, label, how, abyssTier, raid, particle, null);
        }
        Cosmetic(String id, Kind kind, String label, String how, int abyssTier, String raid, String particle, String firstClear) {
            this.id = id; this.kind = kind; this.label = label; this.how = how; this.abyssTier = abyssTier; this.raid = raid; this.particle = particle;
            this.firstClear = firstClear;
            this.price = 0; this.style = null;
        }
    }

    public static final List<Cosmetic> ALL = Collections.unmodifiableList(Arrays.asList(
            // D103 (midgame #10/#6): milestones on the way, display only — Q01→Q07 took 8–26 days with nothing to show
            new Cosmetic("q04", Kind.TITLE, "§3潮蚀渡者", "首通 Q04 潮蚀水道", 0, null, null, "q04"),
            new Cosmetic("q07", Kind.TITLE, "§6锈轨归来", "首通 Q07 锈轨矿道", 0, null, null, "q07"),
            new Cosmetic("abyss3", Kind.TITLE, "§9深渊初探", "深渊最高通关第 3 层", 3, null, null),
            new Cosmetic("abyss5", Kind.TITLE, "§5深渊行者", "深渊最高通关第 5 层", 5, null, null),
            new Cosmetic("abyss10", Kind.TITLE, "§d§l余烬深渊之主", "深渊最高通关第 10 层", 10, null, null),
            new Cosmetic("r01", Kind.TITLE, "§6锈轨破袭者", "通关团本 R01 锈轨矿道·团", 0, "r01", null),
            new Cosmetic("r02", Kind.TITLE, "§b霜封守望者", "通关团本 R02 霜封哨所·团", 0, "r02", null),
            new Cosmetic("trail_r01", Kind.TRAIL, "§6余烬火星", "通关团本 R01（团本专属足迹）", 0, "r01", "FLAME"),
            new Cosmetic("trail_r02", Kind.TRAIL, "§b霜花", "通关团本 R02（团本专属足迹）", 0, "r02", "SNOW_SHOVEL")));

    /**
     * D107 cosmetic shop (coin sink, display only, no stats): title colours for titles you own, plain coin trails
     * (1 small particle every 8 ticks — the raid trails puff 3 every 4 ticks and stay the prestige ones), and a nameplate
     * flair shown outside dungeon instances. Prices 2k–20k 余烬币; bought once, kept forever, never sold back.
     */
    public static final List<Cosmetic> SHOP = Collections.unmodifiableList(Arrays.asList(
            new Cosmetic("color_white", Kind.COLOR, "§f素白", "已有称号换成白色", 2000, "§f", null, true),
            new Cosmetic("color_gold", Kind.COLOR, "§6金辉", "已有称号换成金色", 3000, "§6", null, true),
            new Cosmetic("color_aqua", Kind.COLOR, "§b霜青", "已有称号换成青色", 3000, "§b", null, true),
            new Cosmetic("color_red", Kind.COLOR, "§c绯焰", "已有称号换成红色", 5000, "§c", null, true),
            new Cosmetic("color_purple", Kind.COLOR, "§d紫晶", "已有称号换成紫色", 5000, "§d", null, true),
            new Cosmetic("trail_ash", Kind.TRAIL, "§7灰烬", "脚下一缕灰烟（朴素足迹）", 4000, null, "SMOKE_NORMAL", true),
            new Cosmetic("trail_spark", Kind.TRAIL, "§e星屑", "脚下一点星光（朴素足迹）", 8000, null, "CRIT", true),
            new Cosmetic("trail_leaf", Kind.TRAIL, "§a萤绿", "脚下一点绿光（朴素足迹）", 12000, null, "VILLAGER_HAPPY", true),
            new Cosmetic("flair_star", Kind.FLAIR, "§6✦", "名牌前的金星（主城和野外显示）", 6000, "§6✦ §r", null, true),
            new Cosmetic("flair_snow", Kind.FLAIR, "§b❄", "名牌前的雪花（主城和野外显示）", 10000, "§b❄ §r", null, true),
            new Cosmetic("flair_crown", Kind.FLAIR, "§e♛", "名牌前的王冠（主城和野外显示）", 20000, "§e♛ §r", null, true)));

    static final String C_BOUGHT = "p2_cosbuy_";     // + id, period "all", value 1
    static final String C_SEL_COLOR = "p2_colorsel";
    static final String C_SEL_FLAIR = "p2_flairsel";

    static final String C_EARNED = "p2_title_";   // + raid key, value = clears
    static final String C_SEL_TITLE = "p2_titlesel";
    static final String C_SEL_TRAIL = "p2_trailsel";
    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;

    private final EmberRunService runs;
    private final Map<UUID, Location> last = new HashMap<UUID, Location>();

    public EmberCosmetics(EmberRunService runs) { this.runs = runs; firstClearOf = runs::firstClearedKey; }

    /** D103: called after a first clear of a main map; says which milestone title it unlocked */
    void onFirstClear(Player p, String mapKey) {
        if (p == null || !p.isOnline()) return;
        for (Cosmetic c : ALL) if (mapKey.equals(c.firstClear))
            p.sendMessage(P + "§d获得称号「" + c.label + "§d」§7（只做展示，主菜单「荣誉与排行」右键装上）");
    }

    public static Cosmetic byId(String id) {
        for (Cosmetic c : ALL) if (c.id.equalsIgnoreCase(id)) return c;
        for (Cosmetic c : SHOP) if (c.id.equalsIgnoreCase(id)) return c;
        return null;
    }

    public static boolean bought(PlayerData d, Cosmetic c) { return d != null && c != null && c.price > 0 && d.periodCount(C_BOUGHT + c.id, "all") > 0; }

    private static String selName(Kind k) {
        return k == Kind.TITLE ? C_SEL_TITLE : k == Kind.TRAIL ? C_SEL_TRAIL : k == Kind.COLOR ? C_SEL_COLOR : C_SEL_FLAIR;
    }

    /** D107: the title in its bought colour (bold kept for titles that are bold by default) */
    static String colored(String label, String color) {
        if (color == null) return label;
        String plain = ChatColor.stripColor(label);
        return color + (label.contains("§l") ? "§l" : "") + plain;
    }

    /** raid clears that count for {@code raid} (the earned counter doubles as the clear tally) */
    public static int raidClears(PlayerData d, String raid) { return d == null ? 0 : d.periodCount(C_EARNED + raid, "all"); }

    /** D103: first-clear lookup for milestone titles (set by the constructor; EmberRunService#firstClearedKey) */
    private static volatile java.util.function.BiPredicate<PlayerData, String> firstClearOf = (d, k) -> false;

    public static boolean earned(PlayerData d, int abyssBest, Cosmetic c) {
        if (d == null || c == null) return false;
        if (c.price > 0) return bought(d, c); // D107 shop items
        if (c.firstClear != null) return firstClearOf.test(d, c.firstClear);
        if (c.abyssTier > 0) return abyssBest >= c.abyssTier;
        return c.raid != null && raidClears(d, c.raid) > 0;
    }

    public static String selected(PlayerData d, Kind k) {
        if (d == null) return null;
        String name = selName(k);
        for (Map.Entry<String, Integer> e : d.getCounters().entrySet())
            if (e.getKey().startsWith(name + "@") && e.getValue() != null && e.getValue() > 0) return e.getKey().substring(name.length() + 1);
        return null;
    }

    /** called from raid settlement (once per fresh run): counts the clear and says what it unlocked */
    void onRaidClear(Player p, PlayerData d, String raid) {
        int n = d.addPeriodCount(C_EARNED + raid, "all", 1);
        if (n == 1 && p != null && p.isOnline()) {
            for (Cosmetic c : ALL) if (raid.equals(c.raid))
                p.sendMessage(P + "§d获得" + (c.kind == Kind.TITLE ? "称号" : "足迹") + "「" + c.label + "§d」§7（只做展示，主菜单「荣誉与排行」右键装上）");
        }
    }

    void onAbyssBest(Player p, int oldBest, int newBest) {
        if (p == null || !p.isOnline()) return;
        for (Cosmetic c : ALL) if (c.abyssTier > oldBest && c.abyssTier <= newBest)
            p.sendMessage(P + "§d获得称号「" + c.label + "§d」§7（只做展示，主菜单「荣誉与排行」右键装上）");
    }

    public String titleText(PlayerData d) {
        Cosmetic c = byId(String.valueOf(selected(d, Kind.TITLE)));
        if (c == null || !earned(d, runs.abyssBest(d), c)) return "";
        Cosmetic col = byId(String.valueOf(selected(d, Kind.COLOR))); // D107
        return col != null && col.kind == Kind.COLOR && bought(d, col) ? colored(c.label, col.style) : c.label;
    }

    /** D107: nameplate prefix ("" = none) */
    public String flairOf(PlayerData d) {
        Cosmetic c = byId(String.valueOf(selected(d, Kind.FLAIR)));
        return c != null && c.kind == Kind.FLAIR && bought(d, c) ? c.style : "";
    }

    /** D103: menu text — never an empty 「当前称号：」 */
    public String titleMenuText(PlayerData d) {
        String t = titleText(d);
        return t.isEmpty() ? "§7无（右键「荣誉与排行」装上）" : t;
    }

    public int earnedCount(PlayerData d) {
        int n = 0, best = runs.abyssBest(d);
        for (Cosmetic c : ALL) if (earned(d, best, c)) n++; // prestige honors only (shop items are not honors)
        return n;
    }

    /** /corerpg p1 title [id|off] · /corerpg p1 trail [id|off] */
    public boolean command(Player p, PlayerData d, String sub, String[] args) {
        Kind k = "trail".equals(sub) ? Kind.TRAIL : Kind.TITLE;
        String name = selName(k);
        int best = runs.abyssBest(d);
        if (args.length >= 3) {
            String want = args[2];
            String cur = selected(d, k);
            if ("off".equalsIgnoreCase(want)) {
                if (cur != null) d.addPeriodCount(name, cur, -d.periodCount(name, cur));
                p.sendMessage(P + (k == Kind.TITLE ? "已取下称号" : "已关闭足迹"));
                return true;
            }
            Cosmetic c = byId(want);
            if (c == null || c.kind != k) { p.sendMessage(P + "§c没有这个" + (k == Kind.TITLE ? "称号" : "足迹") + "：" + want); return true; }
            if (!earned(d, best, c)) { p.sendMessage(P + "§c还没获得「" + c.label + "§c」：" + c.how); return true; }
            select(d, k, c.id);
            p.sendMessage(P + "已装上" + (k == Kind.TITLE ? "称号" : "足迹") + "「" + c.label + "§7」");
            return true;
        }
        p.sendMessage(P + "§6荣誉（只做展示，不加属性）§7 已获得 " + earnedCount(d) + "/" + ALL.size()
                + " · 团本通关 R01 " + raidClears(d, "r01") + " 次 · R02 " + raidClears(d, "r02") + " 次 · 深渊最高 " + best + " 层");
        String selT = selected(d, Kind.TITLE), selR = selected(d, Kind.TRAIL);
        for (Cosmetic c : ALL) { // D95: earned honors are a click away (装上 / 取下)
            boolean on = c.id.equals(c.kind == Kind.TITLE ? selT : selR);
            boolean got = earned(d, best, c);
            net.md_5.bungee.api.chat.TextComponent line = new net.md_5.bungee.api.chat.TextComponent(P + (got ? "§a✔ " : "§8✘ ")
                    + (c.kind == Kind.TITLE ? "称号 " : "足迹 ") + c.label + " §7" + c.how + (on ? " §e· 使用中" : "") + " ");
            if (got) {
                String cmd = "/corerpg p1 " + (c.kind == Kind.TITLE ? "title " : "trail ") + (on ? "off" : c.id);
                net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(on ? "§7[取下]" : "§a[装上]");
                b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, cmd));
                line.addExtra(b);
            }
            p.spigot().sendMessage(line);
        }
        p.sendMessage(P + "§7点 [装上] / [取下]；只做展示，不加属性。");
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[外观商店]", "/corerpg p1 cosmetic", "称号颜色、朴素足迹、名牌标记（花余烬币，只做展示）", "GOLD"});
        return true;
    }

    /** one selection per kind: zero every other selected id of that kind, then set this one */
    private static void select(PlayerData d, Kind k, String id) {
        String name = selName(k);
        d.addPeriodCount(name, id, 1 - d.periodCount(name, id)); // a period counter keeps one period: drops the old pick
    }

    // ------------------------------------------------------------------ D107 cosmetic shop

    private static String kindName(Kind k) {
        return k == Kind.COLOR ? "称号颜色" : k == Kind.TRAIL ? "足迹" : k == Kind.FLAIR ? "名牌标记" : "称号";
    }

    private int ownedTitles(PlayerData d) {
        int n = 0, best = runs.abyssBest(d);
        for (Cosmetic c : ALL) if (c.kind == Kind.TITLE && earned(d, best, c)) n++;
        return n;
    }

    /** /corerpg p1 cosmetic [buy <id> [confirm] | color <id|off> | flair <id|off>] */
    public boolean shop(Player p, PlayerData d, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(java.util.Locale.ROOT) : "";
        if (("color".equals(op) || "flair".equals(op)) && args.length >= 4) {
            Kind k = "color".equals(op) ? Kind.COLOR : Kind.FLAIR;
            if ("off".equalsIgnoreCase(args[3])) {
                String cur = selected(d, k);
                if (cur != null) d.addPeriodCount(selName(k), cur, -d.periodCount(selName(k), cur));
                p.sendMessage(P + (k == Kind.COLOR ? "称号恢复原色" : "已取下名牌标记"));
                runs.flushData(p.getUniqueId());
                return true;
            }
            Cosmetic c = byId(args[3]);
            if (c == null || c.kind != k) { p.sendMessage(P + "§c没有这个" + kindName(k) + "：" + args[3]); return true; }
            if (!bought(d, c)) { p.sendMessage(P + "§c还没买「" + c.label + "§c」"); return true; }
            select(d, k, c.id);
            runs.flushData(p.getUniqueId());
            p.sendMessage(P + "已换上" + kindName(k) + "「" + c.label + "§7」" + (k == Kind.COLOR && titleText(d).isEmpty() ? "（先装上一个称号才看得到）" : ""));
            return true;
        }
        if ("buy".equals(op) && args.length >= 4) {
            Cosmetic c = byId(args[3]);
            if (c == null || c.price <= 0) { p.sendMessage(P + "§c商店里没有这件：" + args[3]); return true; }
            if (bought(d, c)) { p.sendMessage(P + "你已经有「" + c.label + "§7」了。"); return true; }
            if (c.kind == Kind.COLOR && ownedTitles(d) == 0) { p.sendMessage(P + "§c称号颜色只能用在已有的称号上：先拿到一个称号（首通 Q04 就有）。"); return true; }
            boolean go = args.length >= 5 && "confirm".equalsIgnoreCase(args[4]);
            if (!go) {
                p.sendMessage(P + "购买" + kindName(c.kind) + "「" + c.label + "§7」：" + c.how + " · §e" + c.price + " 余烬币§7（现有 " + d.getCoin() + "）· 只做展示，不加属性，买了不退");
                town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[确认购买]", "/corerpg p1 cosmetic buy " + c.id + " confirm", "扣 " + c.price + " 余烬币", "GREEN"});
                return true;
            }
            if (d.getCoin() < c.price) { p.sendMessage(P + "§c余烬币不足（需要 " + c.price + "，现有 " + d.getCoin() + "）"); return true; }
            if (!d.takeCoin(c.price)) { p.sendMessage(P + "§c扣除余烬币失败"); return true; }
            d.addPeriodCount(C_BOUGHT + c.id, "all", 1);
            if (c.kind != Kind.TITLE) select(d, c.kind, c.id); // put it on right away
            runs.flushData(p.getUniqueId());
            Bukkit.getLogger().info("[P1 cosmetic] " + p.getName() + " bought " + c.id + " for " + c.price + " coin");
            p.sendMessage(P + "§a已购买并换上" + kindName(c.kind) + "「" + c.label + "§a」§7（剩余 " + d.getCoin() + " 余烬币）");
            return true;
        }
        p.sendMessage(P + "§6外观商店§7（只做展示，不加属性；买一次永久有，不回收）· 余烬币 §f" + d.getCoin());
        String selC = selected(d, Kind.COLOR), selT = selected(d, Kind.TRAIL), selF = selected(d, Kind.FLAIR);
        Kind last = null;
        for (Cosmetic c : SHOP) {
            if (c.kind != last) {
                last = c.kind;
                p.sendMessage(P + "§e" + kindName(c.kind) + (c.kind == Kind.COLOR ? "§7（用在你已有的称号上）" : c.kind == Kind.TRAIL ? "§7（比团本足迹朴素）" : "§7（主城和野外显示，副本里不显示）"));
            }
            boolean got = bought(d, c);
            String sel = c.kind == Kind.COLOR ? selC : c.kind == Kind.TRAIL ? selT : selF;
            boolean on = c.id.equals(sel);
            String cmd, label;
            if (!got) { cmd = "/corerpg p1 cosmetic buy " + c.id; label = "§a[购买]"; }
            else if (c.kind == Kind.TRAIL) { cmd = "/corerpg p1 trail " + (on ? "off" : c.id); label = on ? "§7[取下]" : "§a[换上]"; }
            else { cmd = "/corerpg p1 cosmetic " + (c.kind == Kind.COLOR ? "color " : "flair ") + (on ? "off" : c.id); label = on ? "§7[取下]" : "§a[换上]"; }
            net.md_5.bungee.api.chat.TextComponent line = new net.md_5.bungee.api.chat.TextComponent(P + "  " + c.label + " §7" + c.how
                    + (got ? (on ? " §e· 使用中 " : " §a· 已拥有 ") : " §6" + c.price + " 币 "));
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(label);
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, cmd));
            line.addExtra(b);
            p.spigot().sendMessage(line);
        }
        return true;
    }

    /** D107: nameplate flairs on one viewer's scoreboard (CoreRpg gives every player an own sidebar board) */
    public void syncFlair(org.bukkit.scoreboard.Scoreboard board) {
        Map<String, String> want = new HashMap<String, String>(); // player name → team
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.getWorld().getName().startsWith("dungeon_")) continue; // instances stay clean
            String f = flairOf(runs.dataOf(o.getUniqueId()));
            if (f.isEmpty()) continue;
            String id = selected(runs.dataOf(o.getUniqueId()), Kind.FLAIR);
            want.put(o.getName(), ("efl_" + id).length() > 16 ? ("efl_" + id).substring(0, 16) : "efl_" + id);
        }
        for (org.bukkit.scoreboard.Team t : new java.util.ArrayList<org.bukkit.scoreboard.Team>(board.getTeams())) {
            if (!t.getName().startsWith("efl_")) continue;
            for (String e : new java.util.ArrayList<String>(t.getEntries())) if (!t.getName().equals(want.get(e))) t.removeEntry(e);
        }
        for (Map.Entry<String, String> e : want.entrySet()) {
            org.bukkit.scoreboard.Team t = board.getTeam(e.getValue());
            if (t == null) {
                t = board.registerNewTeam(e.getValue());
                Cosmetic c = byId(e.getValue().substring(4));
                if (c != null) t.setPrefix(c.style);
            }
            if (!t.hasEntry(e.getKey())) t.addEntry(e.getKey());
        }
    }

    // ------------------------------------------------------------------ display

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        try { // async thread: a racing counter write must never break chat
            String t = titleText(runs.dataOf(e.getPlayer().getUniqueId()));
            if (!t.isEmpty()) e.setFormat("§8[" + t + "§8]§r " + e.getFormat());
        } catch (RuntimeException ignored) { /* plain chat this once */ }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { last.remove(e.getPlayer().getUniqueId()); }

    private int tickN;

    /** every 4 ticks: a small puff behind moving players who wear a trail (spectators and invisible players skipped) */
    public void tick() {
        tickN++;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) continue;
            PlayerData d = runs.dataOf(p.getUniqueId());
            String sel = selected(d, Kind.TRAIL);
            if (sel == null) continue;
            Cosmetic c = byId(sel);
            if (c == null || c.particle == null || !earned(d, runs.abyssBest(d), c)) continue;
            Location now = p.getLocation(), was = last.put(p.getUniqueId(), now);
            if (was == null || was.getWorld() != now.getWorld() || was.distanceSquared(now) < 0.04) continue;
            boolean plain = c.price > 0; // D107 coin trails: 1 particle every 8 ticks (raid trails: 3 every 4)
            if (plain && (tickN & 1) == 1) continue;
            try {
                p.getWorld().spawnParticle(Particle.valueOf(c.particle), now.clone().add(0, 0.1, 0), plain ? 1 : 3, plain ? 0.05 : 0.15, 0.02, plain ? 0.05 : 0.15, plain ? 0.0 : 0.01);
            } catch (IllegalArgumentException ignored) { /* particle missing on this server version */ }
        }
    }
}
