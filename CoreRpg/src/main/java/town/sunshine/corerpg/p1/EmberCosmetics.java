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

    public enum Kind { TITLE, TRAIL, COLOR, FLAIR, GLOW, ANIM }

    public static final class Cosmetic {
        public final String id, label, how;
        public final Kind kind;
        public final int abyssTier;   // > 0: earned by abyss best >= this tier
        public final String raid;     // non-null: earned by a clear of this raid key
        public final String particle; // trails only
        public final String firstClear; // non-null: earned by the first clear of this main map (D103 milestones)
        public final int price;         // D107 > 0: bought with 余烬币 in the cosmetic shop (display only)
        public final String style;      // D107 COLOR: colour code for owned titles · FLAIR: nameplate prefix · ANIM: colour cycle
        public final int markPts;       // E-review #2: > 0 = mark-only item, priced in mark points (T1 1 · T2 2 · T3 4)
        Cosmetic(String id, Kind kind, String label, String how, int price, String style, String particle, boolean shop) {
            this(id, kind, label, how, price, 0, style, particle);
        }
        Cosmetic(String id, Kind kind, String label, String how, int price, int markPts, String style, String particle) {
            this.id = id; this.kind = kind; this.label = label; this.how = how; this.abyssTier = 0; this.raid = null;
            this.particle = particle; this.firstClear = null; this.price = price; this.style = style; this.markPts = markPts;
        }
        Cosmetic(String id, Kind kind, String label, String how, int abyssTier, String raid, String particle) {
            this(id, kind, label, how, abyssTier, raid, particle, null);
        }
        Cosmetic(String id, Kind kind, String label, String how, int abyssTier, String raid, String particle, String firstClear) {
            this.id = id; this.kind = kind; this.label = label; this.how = how; this.abyssTier = abyssTier; this.raid = raid; this.particle = particle;
            this.firstClear = firstClear;
            this.price = 0; this.style = null; this.markPts = 0;
        }
        /** shop item (coin or mark-only) */
        public boolean shop() { return price > 0 || markPts > 0; }
        /** price in mark points: mark-only items carry their own, coin items convert at POINT_COIN 余烬币 per point */
        public int points() { return markPts > 0 ? markPts : (price + POINT_COIN - 1) / POINT_COIN; }
    }

    public static final List<Cosmetic> ALL = Collections.unmodifiableList(Arrays.asList(
            // D103 (midgame #10/#6): milestones on the way, display only — Q01→Q07 took 8–26 days with nothing to show
            // E-review #9: every main map has a small first-clear title now (the 0.3 players took 26 days with one title)
            new Cosmetic("q01", Kind.TITLE, "§7庭院余火", "首通 Q01 灰烬庭院", 0, null, null, "q01"),
            new Cosmetic("q02", Kind.TITLE, "§8焦骨行者", "首通 Q02 焦骨甬道", 0, null, null, "q02"),
            new Cosmetic("q03", Kind.TITLE, "§2残誓守灯", "首通 Q03 残誓地窖", 0, null, null, "q03"),
            new Cosmetic("q04", Kind.TITLE, "§3潮蚀渡者", "首通 Q04 潮蚀水道", 0, null, null, "q04"),
            new Cosmetic("q05", Kind.TITLE, "§e断塔攀者", "首通 Q05 断塔回廊", 0, null, null, "q05"),
            new Cosmetic("q06", Kind.TITLE, "§b霜哨旧客", "首通 Q06 霜封哨所", 0, null, null, "q06"),
            new Cosmetic("q07", Kind.TITLE, "§6锈轨归来", "首通 Q07 锈轨矿道", 0, null, null, "q07"),
            // E-review #9: 深渊初探 at tier 1 (tier 1 = the challenge strength since the re-curve)
            new Cosmetic("abyss1", Kind.TITLE, "§9深渊初探", "深渊最高通关第 1 层", 1, null, null),
            new Cosmetic("abyss5", Kind.TITLE, "§5深渊行者", "深渊最高通关第 5 层", 5, null, null),
            new Cosmetic("abyss10", Kind.TITLE, "§d§l余烬深渊之主", "深渊最高通关第 10 层", 10, null, null),
            new Cosmetic("r01", Kind.TITLE, "§6锈轨破袭者", "通关团本 R01 锈轨矿道·团", 0, "r01", null),
            new Cosmetic("r02", Kind.TITLE, "§b霜封守望者", "通关团本 R02 霜封哨所·团", 0, "r02", null),
            new Cosmetic("r03", Kind.TITLE, "§6断塔同心", "通关团本 R03 断塔回廊·团", 0, "r03", null), // D137
            new Cosmetic("rush", Kind.TITLE, "§c连战不息", "通关「余烬连战」（每周首领连战，Q05→Q06→Q07 连打）", 0, "rush", null), // D144
            new Cosmetic("raids10", Kind.TITLE, "§c十战老兵", "团本累计通关 10 次（R01 + R02 + R03）", 0, null, null),
            new Cosmetic("trail_r01", Kind.TRAIL, "§6余烬火星", "通关团本 R01（团本专属足迹）", 0, "r01", "FLAME"),
            new Cosmetic("trail_r02", Kind.TRAIL, "§b霜花", "通关团本 R02（团本专属足迹）", 0, "r02", "SNOW_SHOVEL"),
            new Cosmetic("trail_r03", Kind.TRAIL, "§6烬核余辉", "通关团本 R03（团本专属足迹）", 0, "r03", "CRIT_MAGIC"), // D137
            // D139 国庆 2026 (limited): the title = an event clear inside the window (ember-v1-festival.yml), the trail =
            // bought in the event shop with 国庆币; neither can be obtained after the event ends, owners keep them
            new Cosmetic("gq26", Kind.TITLE, "§c盛世§6烟火", "国庆 2026 活动期间通关「烟火庙会」（限时）", 0, "gq26", null),
            new Cosmetic("trail_gq26", Kind.TRAIL, "§c红§6金烟火", "国庆 2026 活动商店（国庆币；活动结束后也能用剩下的国庆币换）", 0, null, "FIREWORKS_SPARK"),
            new Cosmetic("gq26_memo", Kind.TITLE, "§6烟火§c纪念", "国庆 2026 剩下的国庆币兑换（120 国庆币）", 0, null, null), // D146
            // D116 season honors (4-week seasons; display only, kept forever once earned)
            new Cosmetic("season_abyss", Kind.TITLE, "§d赛季深渊三甲", "某赛季「最高深渊层」榜前 3", 0, null, null),
            new Cosmetic("season_featured", Kind.TITLE, "§b赛季精选三甲", "某赛季「精选挑战通关」榜前 3", 0, null, null),
            new Cosmetic("season_raids", Kind.TITLE, "§6赛季团本三甲", "某赛季「团本通关」榜前 3", 0, null, null),
            new Cosmetic("season_fast", Kind.TITLE, "§c赛季疾行者", "某赛季 R01、R02、R03 或余烬连战「最快通关」榜前 3", 0, null, null),
            new Cosmetic("season_deep", Kind.TITLE, "§5赛季深潜者", "某赛季深渊最高达到第 5 层（赛季内通关）", 0, null, null),
            new Cosmetic("season_crown", Kind.FLAIR, "§d❖", "某赛季任一榜第 1 名（名牌前标记，主城和野外显示）", 0, 0, "§d❖ §r", null)));

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
            new Cosmetic("flair_crown", Kind.FLAIR, "§e♛", "名牌前的王冠（主城和野外显示）", 20000, "§e♛ §r", null, true),
            // E-review #2: mark-only items (late-game mark sink, display only). Weapon glows show in the hub only, around
            // a P1 blade in the main hand; title effects make the worn title flow (chat colours shift per message, the
            // hub / field nameplate suffix cycles every board refresh).
            new Cosmetic("glow_ember", Kind.GLOW, "§6余烬辉光", "主城里手持的刃冒火星（只在主城）", 0, 160, null, "FLAME"),
            new Cosmetic("glow_star", Kind.GLOW, "§f星辉", "主城里手持的刃绕着白光（只在主城）", 0, 240, null, "END_ROD"),
            new Cosmetic("glow_witch", Kind.GLOW, "§5紫焰辉光", "主城里手持的刃冒紫焰（只在主城）", 0, 320, null, "SPELL_WITCH"),
            new Cosmetic("anim_ember", Kind.ANIM, "§6流§c火", "火色流光（要先装上称号）", 0, 240, "6ce", null),
            new Cosmetic("anim_frost", Kind.ANIM, "§b霜§f光", "霜色流光（要先装上称号）", 0, 240, "b3f", null)));

    /** E-review #2: mark points per forge mark of tier 1/2/3, and 余烬币 per point when a coin item is paid in marks */
    public static final int[] MARK_POINTS = {0, 1, 2, 4};
    public static final int POINT_COIN = 50;
    /** marks of each tier always kept back for an exchange (only the surplus pays for cosmetics) */
    public static final int MARK_RESERVE = 8;
    static final String RAIDS10 = "raids10";

    static final String C_BOUGHT = "p2_cosbuy_";     // + id, period "all", value 1
    static final String C_SEL_COLOR = "p2_colorsel";
    static final String C_SEL_FLAIR = "p2_flairsel";
    static final String C_SEL_GLOW = "p2_glowsel";
    static final String C_SEL_ANIM = "p2_animsel";

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
            p.sendMessage(P + "§d获得称号「" + c.label + "§d」§7（只做展示，主菜单「赛季 · 排行 · 周目标」右键装上）");
    }

    public static Cosmetic byId(String id) {
        for (Cosmetic c : ALL) if (c.id.equalsIgnoreCase(id)) return c;
        for (Cosmetic c : SHOP) if (c.id.equalsIgnoreCase(id)) return c;
        return null;
    }

    public static boolean bought(PlayerData d, Cosmetic c) { return d != null && c != null && c.shop() && d.periodCount(C_BOUGHT + c.id, "all") > 0; }

    private static String selName(Kind k) {
        switch (k) {
            case TITLE: return C_SEL_TITLE;
            case TRAIL: return C_SEL_TRAIL;
            case COLOR: return C_SEL_COLOR;
            case GLOW: return C_SEL_GLOW;
            case ANIM: return C_SEL_ANIM;
            default: return C_SEL_FLAIR;
        }
    }

    // ------------------------------------------------------------------ E-review #8: 10-second try-on

    private static final class Trial { final Cosmetic c; final long until; Trial(Cosmetic c, long until) { this.c = c; this.until = until; } }
    private static final Map<UUID, Trial> TRIALS = new java.util.concurrent.ConcurrentHashMap<UUID, Trial>();

    /** the cosmetic of kind k this player shows right now: an active try-on wins, else the owned selection */
    static Cosmetic shown(UUID u, PlayerData d, Kind k, int abyssBest) {
        Trial t = u == null ? null : TRIALS.get(u);
        if (t != null) {
            if (t.until < System.currentTimeMillis()) TRIALS.remove(u);
            else if (t.c.kind == k) return t.c;
        }
        Cosmetic c = byId(String.valueOf(selected(d, k)));
        return c != null && c.kind == k && earned(d, abyssBest, c) ? c : null;
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
        if (c.shop()) return bought(d, c); // D107 shop items
        if (c.id.startsWith("season_")) return d.periodCount(EmberSeason.C_AWARD + c.id, "all") > 0; // D116
        if (RAIDS10.equals(c.id)) return raidClears(d, "r01") + raidClears(d, "r02") + raidClears(d, "r03") >= 10; // E-review #9
        if (c.firstClear != null) return firstClearOf.test(d, c.firstClear);
        EmberFestival fest = EmberFestival.get(); // D139 event trail: bought with the event currency
        if (fest != null && fest.isFestTrail(c.id)) return fest.trailOwned(d, c.id);
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
                p.sendMessage(P + "§d获得" + (c.kind == Kind.TITLE ? "称号" : "足迹") + "「" + c.label + "§d」§7（只做展示，主菜单「赛季 · 排行 · 周目标」右键装上）");
        }
        if (raidClears(d, "r01") + raidClears(d, "r02") + raidClears(d, "r03") == 10 && p != null && p.isOnline())
            p.sendMessage(P + "§d获得称号「" + byId(RAIDS10).label + "§d」§7（团本累计 10 次，只做展示）");
    }

    void onAbyssBest(Player p, int oldBest, int newBest) {
        if (p == null || !p.isOnline()) return;
        for (Cosmetic c : ALL) if (c.abyssTier > oldBest && c.abyssTier <= newBest)
            p.sendMessage(P + "§d获得称号「" + c.label + "§d」§7（只做展示，主菜单「赛季 · 排行 · 周目标」右键装上）");
    }

    public String titleText(PlayerData d) { return titleText(null, d, -1); }

    /**
     * The worn title as shown: bought colour (D107), try-on colour, or a title effect (E-review #2: each character
     * takes the next colour of the cycle; {@code step} shifts the cycle — chat passes the message clock, the nameplate
     * the board refresh — so the title flows). step < 0 = no effect (menus, plain text).
     */
    String titleText(UUID u, PlayerData d, long step) {
        int best = runs.abyssBest(d);
        Cosmetic c = shown(u, d, Kind.TITLE, best);
        if (c == null) return "";
        Cosmetic anim = step < 0 ? null : shown(u, d, Kind.ANIM, best);
        if (anim != null) return animated(c.label, anim.style, step);
        Cosmetic col = shown(u, d, Kind.COLOR, best); // D107
        return col != null ? colored(c.label, col.style) : c.label;
    }

    static String animated(String label, String cycle, long step) {
        String plain = ChatColor.stripColor(label);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < plain.length(); i++)
            b.append('§').append(cycle.charAt((int) ((i + step) % cycle.length()))).append(plain.charAt(i));
        return b.toString();
    }

    /** D107: nameplate prefix ("" = none) */
    public String flairOf(PlayerData d) { return flairOf(null, d); }

    String flairOf(UUID u, PlayerData d) {
        Cosmetic c = shown(u, d, Kind.FLAIR, runs.abyssBest(d));
        return c == null ? "" : c.style;
    }

    /** D103: menu text — never an empty 「当前称号：」 */
    public String titleMenuText(PlayerData d) {
        String t = titleText(d);
        return t.isEmpty() ? "§7无（右键「赛季 · 排行 · 周目标」装上）" : t;
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
                + " · 团本通关 R01 " + raidClears(d, "r01") + " 次 · R02 " + raidClears(d, "r02") + " 次 · R03 " + raidClears(d, "r03") + " 次 · 深渊最高 " + best + " 层");
        String selT = selected(d, Kind.TITLE), selR = selected(d, Kind.TRAIL), selF = selected(d, Kind.FLAIR);
        for (Cosmetic c : ALL) { // D95: earned honors are a click away (装上 / 取下)
            boolean on = c.id.equals(c.kind == Kind.TITLE ? selT : c.kind == Kind.FLAIR ? selF : selR);
            boolean got = earned(d, best, c);
            net.md_5.bungee.api.chat.TextComponent line = new net.md_5.bungee.api.chat.TextComponent(P + (got ? "§a✔ " : "§8✘ ")
                    + (c.kind == Kind.TITLE ? "称号 " : c.kind == Kind.FLAIR ? "名牌 " : "足迹 ") + c.label + " §7" + c.how + (on ? " §e· 使用中" : "") + " ");
            if (got) {
                String cmd = c.kind == Kind.FLAIR ? "/corerpg p1 cosmetic flair " + (on ? "off" : c.id) // D116 season flair
                        : "/corerpg p1 " + (c.kind == Kind.TITLE ? "title " : "trail ") + (on ? "off" : c.id);
                net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(on ? "§7[取下]" : "§a[装上]");
                b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, cmd));
                line.addExtra(b);
            }
            p.spigot().sendMessage(line);
        }
        p.sendMessage(P + "§7点 [装上] / [取下]；只做展示，不加属性。");
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[外观商店]", "/corerpg p1 cosmetic", "打开外观商店页：称号颜色、足迹、名牌标记、辉光、动效（币 / 余烬徽 / 印记，只做展示）", "GOLD"});
        return true;
    }

    /** one selection per kind: zero every other selected id of that kind, then set this one */
    private static void select(PlayerData d, Kind k, String id) {
        String name = selName(k);
        d.addPeriodCount(name, id, 1 - d.periodCount(name, id)); // a period counter keeps one period: drops the old pick
    }

    // ------------------------------------------------------------------ D107 cosmetic shop

    static String kindName(Kind k) {
        switch (k) {
            case COLOR: return "称号颜色";
            case TRAIL: return "足迹";
            case FLAIR: return "名牌标记";
            case GLOW: return "武器辉光";
            case ANIM: return "称号动效";
            default: return "称号";
        }
    }

    private static String kindNote(Kind k) {
        switch (k) {
            case COLOR: return "§7（用在你已有的称号上）";
            case TRAIL: return "§7（比团本足迹朴素）";
            case FLAIR: return "§7（主城和野外显示，副本里不显示）";
            case GLOW: return "§7（不收币：余烬徽或印记 · 只在主城显示，手持余烬刃时）";
            case ANIM: return "§7（不收币：余烬徽或印记 · 让装上的称号流动变色）";
            default: return "";
        }
    }

    private int ownedTitles(PlayerData d) {
        int n = 0, best = runs.abyssBest(d);
        for (Cosmetic c : ALL) if (c.kind == Kind.TITLE && earned(d, best, c)) n++;
        return n;
    }

    /** marks of tier t that may pay for cosmetics (everything above the exchange reserve) */
    int surplus(PlayerData d, int t) { return Math.max(0, runs.marks(d, t) - MARK_RESERVE); }

    /** marks of tier t needed for this item */
    static int markCost(Cosmetic c, int t) { return (c.points() + MARK_POINTS[t] - 1) / MARK_POINTS[t]; }

    /**
     * F-review #2 (D121): the one price line, shared by the shop page (PAPI shopprice_<id>), the confirm page and the
     * chat list — coin (or 不收币) / 余烬徽 / every mark tier.
     */
    static String priceText(Cosmetic c) {
        return (c.price > 0 ? c.price + " 币" : "不收币 ·") + (c.price > 0 ? " / " : " ") + c.points() + " 徽 / 印记 T3×" + markCost(c, 3)
                + " · T2×" + markCost(c, 2) + " · T1×" + markCost(c, 1);
    }

    /** F-review #2: the one description line (kind + what it does) */
    static String howText(Cosmetic c) { return kindName(c.kind) + "：" + c.how; }

    /** F-review #2: the canonical shop page (every link opens this; the chat list stays as /corerpg p1 cosmetic list) */
    public static final String SHOP_MENU = "ember_p1_shop";
    static final String BUY_MENU = "ember_p1_shop_buy";

    /** open a TrMenu page for this player one tick later (menus close on click; console opens it for them) */
    void openMenu(Player p, String menu) {
        Bukkit.getScheduler().runTask(runs.plugin(), () -> {
            if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName());
        });
    }

    /** Back-button fix (D136): the page the shop was opened from (gear / season); none = opened from chat or a command → Back closes */
    private static final Map<UUID, String> FROM = new java.util.concurrent.ConcurrentHashMap<UUID, String>();
    static final Map<String, String> FROM_MENUS = new java.util.LinkedHashMap<String, String>();
    static {
        FROM_MENUS.put("gear", "ember_p1_gear");
        FROM_MENUS.put("season", "ember_p1_season");
    }

    /** D136: label of the shop's Back button for this origin */
    static String backLabel(String from) {
        return "gear".equals(from) ? "§7返回装备页" : "season".equals(from) ? "§7返回赛季 · 排行 · 周目标" : "§7关闭商店";
    }

    /** F-review #2: the item a player picked on the shop page, shown on the in-menu confirm page */
    private static final Map<UUID, String> PICKED = new java.util.concurrent.ConcurrentHashMap<UUID, String>();

    /** PAPI lines of the confirm page: one per way to pay, saying what it takes or what is short */
    String payLine(PlayerData d, Cosmetic c, String pay) {
        if (d == null || c == null) return "";
        if ("coin".equals(pay)) {
            if (c.price <= 0) return "§8这件不收币";
            return d.getCoin() >= c.price ? "§a付得起 §7· 扣 " + c.price + " 余烬币（现有 " + d.getCoin() + "）"
                    : "§8还差 " + (c.price - d.getCoin()) + " 币（现有 " + d.getCoin() + "）";
        }
        if ("badge".equals(pay)) {
            int bh = EmberSeason.badges(d);
            return bh >= c.points() ? "§a付得起 §7· 扣 " + c.points() + " 余烬徽（现有 " + bh + "）"
                    : "§8还差 " + (c.points() - bh) + " 徽（现有 " + bh + "，周目标奖励）";
        }
        int t = "t3".equals(pay) ? 3 : "t2".equals(pay) ? 2 : 1;
        int need = markCost(c, t), have = surplus(d, t);
        return have >= need ? "§a付得起 §7· 扣 " + need + " 枚 T" + t + "（可用 " + have + "，另留 " + MARK_RESERVE + " 枚备用兑换）"
                : "§8还差 " + (need - have) + " 枚 T" + t + "（可用 " + have + "，留 " + MARK_RESERVE + " 枚不动）";
    }

    /** F-review #2: a title must be worn to see a colour / effect — wear the first owned one and say so */
    private void ensureTitle(Player p, PlayerData d, Cosmetic c) {
        if (!needsTitle(c.kind) || !titleText(d).isEmpty()) return;
        int best = runs.abyssBest(d);
        for (Cosmetic t : ALL) if (t.kind == Kind.TITLE && earned(d, best, t)) {
            select(d, Kind.TITLE, t.id);
            p.sendMessage(P + "已先帮你装上称号「" + t.label + "§7」（" + kindName(c.kind) + "要装着称号才看得到；赛季页可换）");
            return;
        }
    }

    private static boolean needsTitle(Kind k) { return k == Kind.COLOR || k == Kind.ANIM; }

    /**
     * /corerpg p1 cosmetic [buy <id> [coin|t1|t2|t3] | try <id> | color|flair|glow|anim <id|off>]
     * E-review #2/#3/#8: coin or surplus forge marks (T1 1 · T2 2 · T3 4 points, 1 point = 50 币, 8 marks per tier kept
     * back), mark-only glows / title effects, a 10 s try-on, and a buy button only when the player can pay.
     */
    public boolean shop(Player p, PlayerData d, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(java.util.Locale.ROOT) : "";
        Kind selKind = "color".equals(op) ? Kind.COLOR : "flair".equals(op) ? Kind.FLAIR : "glow".equals(op) ? Kind.GLOW : "anim".equals(op) ? Kind.ANIM : null;
        if (selKind != null && args.length >= 4) {
            Kind k = selKind;
            if ("off".equalsIgnoreCase(args[3])) {
                String cur = selected(d, k);
                if (cur != null) d.addPeriodCount(selName(k), cur, -d.periodCount(selName(k), cur));
                p.sendMessage(P + (k == Kind.COLOR ? "称号恢复原色" : "已取下" + kindName(k)));
                runs.flushData(p.getUniqueId());
                return true;
            }
            Cosmetic c = byId(args[3]);
            if (c == null || c.kind != k) { p.sendMessage(P + "§c没有这个" + kindName(k) + "：" + args[3]); return true; }
            if (!earned(d, runs.abyssBest(d), c)) { p.sendMessage(P + "§c还没有「" + c.label + "§c」" + (c.shop() ? "" : "：" + c.how)); return true; }
            select(d, k, c.id);
            runs.flushData(p.getUniqueId());
            p.sendMessage(P + "已换上" + kindName(k) + "「" + c.label + "§7」" + (needsTitle(k) && titleText(d).isEmpty() ? "（先装上一个称号才看得到）" : ""));
            return true;
        }
        if ("from".equals(op)) { // D136: a menu button opens the shop and remembers where Back goes
            String from = args.length >= 4 ? args[3].toLowerCase(java.util.Locale.ROOT) : "";
            if (FROM_MENUS.containsKey(from)) FROM.put(p.getUniqueId(), from); else FROM.remove(p.getUniqueId());
            openMenu(p, SHOP_MENU);
            return true;
        }
        if ("back".equals(op)) { // D136: the shop's Back button → the page it was opened from (chat / command → just close)
            String menu = FROM_MENUS.get(FROM.get(p.getUniqueId()));
            if (menu != null) openMenu(p, menu);
            return true;
        }
        if ("pick".equals(op) && args.length >= 4) { // D119 shop page click: owned → put on / take off, else the buy page
            Cosmetic c = byId(args[3]);
            if (c == null || !c.shop()) { p.sendMessage(P + "§c商店里没有这件：" + args[3]); return true; }
            if (!bought(d, c)) { // F-review #2: confirm inside the menu (one page per item), no detour through chat
                PICKED.put(p.getUniqueId(), c.id);
                openMenu(p, BUY_MENU);
                return true;
            }
            boolean on = c.id.equals(selected(d, c.kind));
            if (c.kind == Kind.TRAIL) command(p, d, "trail", new String[]{args[0], "trail", on ? "off" : c.id});
            else shop(p, d, new String[]{args[0], args[1], c.kind.name().toLowerCase(java.util.Locale.ROOT), on ? "off" : c.id});
            if (!on) ensureTitle(p, d, c);
            openMenu(p, SHOP_MENU);
            return true;
        }
        if ("buysel".equals(op) && args.length >= 4) { // F-review #2: the confirm page's pay buttons
            String id = PICKED.get(p.getUniqueId());
            Cosmetic c = id == null ? null : byId(id);
            if (c == null) { p.sendMessage(P + "§c先在外观商店页点一件商品。"); openMenu(p, SHOP_MENU); return true; }
            String pay = args[3].toLowerCase(java.util.Locale.ROOT);
            if ("try".equals(pay)) { shop(p, d, new String[]{args[0], args[1], "try", c.id}); return true; }
            shop(p, d, new String[]{args[0], args[1], "buy", c.id, pay});
            if (bought(d, c)) { PICKED.remove(p.getUniqueId()); openMenu(p, SHOP_MENU); }
            else openMenu(p, BUY_MENU);
            return true;
        }
        if ("try".equals(op) && args.length >= 4) {
            Cosmetic c = byId(args[3]);
            if (c == null || !c.shop()) { p.sendMessage(P + "§c商店里没有这件：" + args[3]); return true; }
            if (needsTitle(c.kind) && titleText(d).isEmpty()) {
                ensureTitle(p, d, c);
                if (titleText(d).isEmpty()) { p.sendMessage(P + "§c" + kindName(c.kind) + "要装着称号才看得到：先拿到一个称号（首通 Q01 就有）。"); return true; }
            }
            TRIALS.put(p.getUniqueId(), new Trial(c, System.currentTimeMillis() + 10_000L));
            p.sendMessage(P + "§a试穿「" + c.label + "§a」10 秒§7" + (c.kind == Kind.GLOW ? "（只在主城、手持余烬刃时显示）" : c.kind == Kind.TRAIL ? "（走动时看脚下）"
                    : c.kind == Kind.FLAIR ? "（名牌约 2 秒后刷新）" : "（发一句话看效果）"));
            return true;
        }
        if ("buy".equals(op) && args.length >= 4) {
            Cosmetic c = byId(args[3]);
            if (c == null || !c.shop()) { p.sendMessage(P + "§c商店里没有这件：" + args[3]); return true; }
            if (bought(d, c)) { p.sendMessage(P + "你已经有「" + c.label + "§7」了。"); return true; }
            if (needsTitle(c.kind) && ownedTitles(d) == 0) { p.sendMessage(P + "§c" + kindName(c.kind) + "只能用在已有的称号上：先拿到一个称号（首通 Q01 就有）。"); return true; }
            String pay = args.length >= 5 ? args[4].toLowerCase(java.util.Locale.ROOT) : "";
            int tier = "t1".equals(pay) ? 1 : "t2".equals(pay) ? 2 : "t3".equals(pay) ? 3 : 0;
            boolean coin = "coin".equals(pay) || "confirm".equals(pay);
            boolean badge = "badge".equals(pay); // D117: weekly-goal 余烬徽, 1 徽 = 1 point
            if (!coin && !badge && tier == 0 && !(p.isOnline() && "chat".equals(pay))) { // F-review #2: the buy page
                PICKED.put(p.getUniqueId(), c.id);
                openMenu(p, BUY_MENU);
                return true;
            }
            if (!coin && !badge && tier == 0) { // chat preview (from the chat list): one button per way the player can pay right now
                p.sendMessage(P + "购买" + kindName(c.kind) + "「" + c.label + "§7」：" + c.how + " · §e" + priceText(c) + "§7 · 只做展示，不加属性，买了不退");
                List<String[]> btn = new java.util.ArrayList<String[]>();
                if (c.price > 0) {
                    if (d.getCoin() >= c.price) btn.add(new String[]{"[用 " + c.price + " 币]", "/corerpg p1 cosmetic buy " + c.id + " coin", "扣 " + c.price + " 余烬币（现有 " + d.getCoin() + "）", "GREEN"});
                    else p.sendMessage(P + "§8余烬币还差 " + (c.price - d.getCoin()) + "（现有 " + d.getCoin() + "）");
                }
                int bh = EmberSeason.badges(d);
                if (bh >= c.points()) btn.add(new String[]{"[用 " + c.points() + " 余烬徽]", "/corerpg p1 cosmetic buy " + c.id + " badge", "扣 " + c.points() + " 余烬徽（现有 " + bh + "，周目标奖励）", "LIGHT_PURPLE"});
                for (int t = 3; t >= 1; t--) {
                    int need = markCost(c, t), have = surplus(d, t);
                    if (have >= need) btn.add(new String[]{"[用 " + need + " 枚 T" + t + " 印记]", "/corerpg p1 cosmetic buy " + c.id + " t" + t,
                            "扣 " + need + " 枚 T" + t + " 印记（现有 " + runs.marks(d, t) + "，留 " + MARK_RESERVE + " 枚备用兑换）", t == 3 ? "AQUA" : "DARK_AQUA"});
                }
                if (btn.isEmpty()) p.sendMessage(P + "§8现在付不起：余烬徽 " + bh + "/" + c.points() + " · 印记只用超过 " + MARK_RESERVE + " 枚的部分（T3 可用 " + surplus(d, 3) + " · T2 " + surplus(d, 2) + " · T1 " + surplus(d, 1) + "）");
                btn.add(new String[]{"[试穿 10 秒]", "/corerpg p1 cosmetic try " + c.id, "先看效果", "YELLOW"});
                town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, btn.toArray(new String[0][]));
                return true;
            }
            String paid;
            if (badge) {
                int bh = EmberSeason.badges(d);
                if (bh < c.points()) { p.sendMessage(P + "§c余烬徽不足（需要 " + c.points() + "，现有 " + bh + "）"); return true; }
                d.addPeriodCount(EmberSeason.C_BADGE, "all", -c.points()); // econ-ok: C15 spend (cosmetics paused, OUT of the model)
                paid = c.points() + " 余烬徽（剩余 " + EmberSeason.badges(d) + "）";
            } else if (coin) {
                if (c.price <= 0) { p.sendMessage(P + "§c这件只能用印记换。"); return true; }
                if (d.getCoin() < c.price) { p.sendMessage(P + "§c余烬币不足（需要 " + c.price + "，现有 " + d.getCoin() + "）"); return true; }
                if (!d.takeCoin(c.price)) { p.sendMessage(P + "§c扣除余烬币失败"); return true; }
                paid = c.price + " 余烬币（剩余 " + d.getCoin() + "）";
            } else {
                int need = markCost(c, tier);
                if (surplus(d, tier) < need) { p.sendMessage(P + "§cT" + tier + " 印记不够：需要 " + need + " 枚，可用 " + surplus(d, tier) + "（留 " + MARK_RESERVE + " 枚备用兑换）"); return true; }
                d.addPeriodCount(EmberRunService.C_MARK + tier, "all", -need); // econ-ok: C15 spend (cosmetics paused, OUT of the model)
                paid = need + " 枚 T" + tier + " 印记（剩余 " + runs.marks(d, tier) + "）";
            }
            d.addPeriodCount(C_BOUGHT + c.id, "all", 1);
            if (c.kind != Kind.TITLE) select(d, c.kind, c.id); // put it on right away
            TRIALS.remove(p.getUniqueId());
            runs.flushData(p.getUniqueId());
            Bukkit.getLogger().info("[P1 cosmetic] " + p.getName() + " bought " + c.id + " for " + ChatColor.stripColor(paid));
            p.sendMessage(P + "§a已购买并换上" + kindName(c.kind) + "「" + c.label + "§a」§7 · 花费 " + paid);
            ensureTitle(p, d, c);
            return true;
        }
        if (!"list".equals(op)) { // F-review #2: one canonical shop (the page)
            if (op.isEmpty()) FROM.remove(p.getUniqueId()); // D136: opened from chat / a command, Back just closes
            openMenu(p, SHOP_MENU);
            return true;
        }
        p.sendMessage(P + "§6外观商店§7（只做展示，不加属性；买一次永久有，不回收）· 余烬币 §f" + d.getCoin()
                + " §7· 余烬徽 §f" + EmberSeason.badges(d)
                + " §7· 可用印记 T3 §f" + surplus(d, 3) + " §7T2 §f" + surplus(d, 2) + " §7T1 §f" + surplus(d, 1));
        p.sendMessage(P + "§7币、余烬徽（周目标奖励）或多出来的印记都能付：1 徽 = 1 点 · T1 1 点 · T2 2 点 · T3 4 点，1 点 = " + POINT_COIN + " 币；每阶留 " + MARK_RESERVE + " 枚备用兑换不动");
        if (ownedTitles(d) == 0) p.sendMessage(P + "§8称号颜色 / 动效要先有称号：首通 Q01 就送一个（买的时候会自动装上）");
        Map<Kind, String> sel = new HashMap<Kind, String>();
        for (Kind k : Kind.values()) sel.put(k, selected(d, k));
        Kind last = null;
        for (Cosmetic c : SHOP) {
            if (c.kind != last) { last = c.kind; p.sendMessage(P + "§e" + kindName(c.kind) + kindNote(c.kind)); }
            boolean got = bought(d, c);
            boolean on = c.id.equals(sel.get(c.kind));
            String cmd, label;
            if (!got) { cmd = "/corerpg p1 cosmetic buy " + c.id + " chat"; label = "§a[购买]"; }
            else if (c.kind == Kind.TRAIL) { cmd = "/corerpg p1 trail " + (on ? "off" : c.id); label = on ? "§7[取下]" : "§a[换上]"; }
            else { cmd = "/corerpg p1 cosmetic " + c.kind.name().toLowerCase(java.util.Locale.ROOT) + " " + (on ? "off" : c.id); label = on ? "§7[取下]" : "§a[换上]"; }
            net.md_5.bungee.api.chat.TextComponent line = new net.md_5.bungee.api.chat.TextComponent(P + "  " + c.label + " §7" + c.how
                    + (got ? (on ? " §e· 使用中 " : " §a· 已拥有 ") : " §6" + priceText(c) + " ")); // same texts as the page
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(label);
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, cmd));
            line.addExtra(b);
            if (!got) {
                net.md_5.bungee.api.chat.TextComponent t = new net.md_5.bungee.api.chat.TextComponent(" §e[试穿]");
                t.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/corerpg p1 cosmetic try " + c.id));
                line.addExtra(t);
            }
            p.spigot().sendMessage(line);
        }
        return true;
    }

    /** D119 PAPI %corerpg_p1_shop_<id>%: one status line for the shop page */
    public String shopLabel(UUID u, PlayerData d, String id) {
        Cosmetic c = byId(id);
        if (c == null || !c.shop()) return "";
        if (bought(d, c)) return c.id.equals(selected(d, c.kind)) ? "§e使用中 §7· 左键取下" : "§a已拥有 §7· 左键换上";
        int bh = EmberSeason.badges(d);
        boolean can = (c.price > 0 && d != null && d.getCoin() >= c.price) || bh >= c.points();
        for (int t = 1; t <= 3 && !can; t++) can = d != null && surplus(d, t) >= markCost(c, t);
        return can ? "§a付得起 §7· 左键看付款方式" : "§8还付不起 §7· 左键看差多少";
    }

    /** F-review #2 PAPI: shopprice_/shopname_/shophow_<id>, shopsel_name|how|price|state|coin|badge|t3|t2|t1 */
    public String papi(UUID u, PlayerData d, String key) {
        if (key.startsWith("shopprice_")) { Cosmetic c = byId(key.substring(10)); return c == null || !c.shop() ? "" : priceText(c); }
        if (key.startsWith("shopname_")) { Cosmetic c = byId(key.substring(9)); return c == null ? "" : c.label; }
        if (key.startsWith("shophow_")) { Cosmetic c = byId(key.substring(8)); return c == null ? "" : howText(c); }
        if ("shopback".equals(key)) return backLabel(u == null ? null : FROM.get(u)); // D136
        if ("shopmarks".equals(key)) return d == null ? "" : "T3 " + surplus(d, 3) + " · T2 " + surplus(d, 2) + " · T1 " + surplus(d, 1);
        if (key.startsWith("shopsel_")) {
            String id = u == null ? null : PICKED.get(u);
            Cosmetic c = id == null ? null : byId(id);
            String f = key.substring(8);
            if (c == null) return "name".equals(f) ? "§7（没有选中商品）" : "";
            if ("name".equals(f)) return c.label;
            if ("how".equals(f)) return howText(c);
            if ("price".equals(f)) return priceText(c);
            if ("state".equals(f)) return bought(d, c) ? "§a已拥有" : shopLabel(u, d, c.id);
            if ("note".equals(f)) return needsTitle(c.kind) && titleText(d).isEmpty() ? (ownedTitles(d) == 0 ? "§c要先有一个称号（首通 Q01 就有）" : "§7买下时会先帮你装上一个称号") : "§7只做展示，不加属性，买了不退";
            return payLine(d, c, f);
        }
        return null;
    }

    private long boardStep;

    /**
     * D107 / E-review #2: nameplate decorations on one viewer's scoreboard (CoreRpg gives every player an own sidebar
     * board, refreshed every 2 s): one team per decorated player, prefix = flair, suffix = the worn title when it has
     * a title effect (cycling colours). Instances stay clean.
     */
    public void syncFlair(org.bukkit.scoreboard.Scoreboard board) {
        long step = System.currentTimeMillis() / 2000L;
        Map<String, String[]> want = new HashMap<String, String[]>(); // team → {entry, prefix, suffix}
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.getWorld().getName().startsWith("dungeon_")) continue;
            PlayerData d = runs.dataOf(o.getUniqueId());
            String f = flairOf(o.getUniqueId(), d);
            String suffix = "";
            if (shown(o.getUniqueId(), d, Kind.ANIM, runs.abyssBest(d)) != null) {
                String t = titleText(o.getUniqueId(), d, step);
                if (!t.isEmpty()) suffix = cut(" " + t, 16);
            }
            if (f.isEmpty() && suffix.isEmpty()) continue;
            String team = "efl_" + (o.getName().length() > 12 ? o.getName().substring(0, 12) : o.getName());
            want.put(team, new String[]{o.getName(), cut(f, 16), suffix});
        }
        for (org.bukkit.scoreboard.Team t : new java.util.ArrayList<org.bukkit.scoreboard.Team>(board.getTeams())) {
            if (!t.getName().startsWith("efl_")) continue;
            String[] w = want.get(t.getName());
            if (w == null) { t.unregister(); continue; }
            for (String e : new java.util.ArrayList<String>(t.getEntries())) if (!e.equals(w[0])) t.removeEntry(e);
        }
        for (Map.Entry<String, String[]> e : want.entrySet()) {
            org.bukkit.scoreboard.Team t = board.getTeam(e.getKey());
            if (t == null) t = board.registerNewTeam(e.getKey());
            String[] w = e.getValue();
            if (!w[1].equals(t.getPrefix())) t.setPrefix(w[1]);
            if (!w[2].equals(t.getSuffix())) t.setSuffix(w[2]);
            if (!t.hasEntry(w[0])) t.addEntry(w[0]);
        }
    }

    /** cut to n chars without leaving a dangling § */
    static String cut(String s, int n) {
        if (s.length() <= n) return s;
        String c = s.substring(0, n);
        return c.endsWith("§") ? c.substring(0, n - 1) : c;
    }

    // ------------------------------------------------------------------ display

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        try { // async thread: a racing counter write must never break chat
            String t = titleText(e.getPlayer().getUniqueId(), runs.dataOf(e.getPlayer().getUniqueId()), System.currentTimeMillis() / 700L);
            if (!t.isEmpty()) e.setFormat("§8[" + t + "§8]§r " + e.getFormat());
        } catch (RuntimeException ignored) { /* plain chat this once */ }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { last.remove(e.getPlayer().getUniqueId()); TRIALS.remove(e.getPlayer().getUniqueId()); FROM.remove(e.getPlayer().getUniqueId()); }

    private int tickN;

    /** every 4 ticks: a small puff behind moving players who wear a trail (spectators and invisible players skipped) */
    public void tick() {
        tickN++;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) continue;
            PlayerData d = runs.dataOf(p.getUniqueId());
            int best = runs.abyssBest(d);
            if ((tickN & 1) == 0 && HUB.equals(p.getWorld().getName())) glow(p, shown(p.getUniqueId(), d, Kind.GLOW, best)); // E-review #2
            Cosmetic c = shown(p.getUniqueId(), d, Kind.TRAIL, best);
            if (c == null || c.particle == null) continue;
            Location now = p.getLocation(), was = last.put(p.getUniqueId(), now);
            if (was == null || was.getWorld() != now.getWorld() || was.distanceSquared(now) < 0.04) continue;
            boolean plain = c.price > 0; // D107 coin trails: 1 particle every 8 ticks (raid trails: 3 every 4)
            if (plain && (tickN & 1) == 1) continue;
            try {
                p.getWorld().spawnParticle(Particle.valueOf(c.particle), now.clone().add(0, 0.1, 0), plain ? 1 : 3, plain ? 0.05 : 0.15, 0.02, plain ? 0.05 : 0.15, plain ? 0.0 : 0.01);
            } catch (IllegalArgumentException ignored) { /* particle missing on this server version */ }
        }
    }

    static final String HUB = "ember_hub";

    /** E-review #2: weapon glow — a couple of particles at the right hand while a P1 blade is in the main hand (hub only) */
    private void glow(Player p, Cosmetic c) {
        if (c == null || c.particle == null) return;
        EmberLoadoutService ls = runs.loadouts();
        EmberLoadout l = ls == null ? null : ls.get(p);
        if (l == null || l.blade == null) return;
        Location eye = p.getEyeLocation();
        double yaw = Math.toRadians(eye.getYaw());
        // right hand ≈ 0.35 to the right, 0.6 below the eyes, 0.3 forward
        Location hand = eye.clone().add(-Math.cos(yaw) * 0.35 - Math.sin(yaw) * 0.3, -0.6, -Math.sin(yaw) * 0.35 + Math.cos(yaw) * 0.3);
        try {
            p.getWorld().spawnParticle(Particle.valueOf(c.particle), hand, 2, 0.08, 0.15, 0.08, 0.0);
        } catch (IllegalArgumentException ignored) { /* particle missing on this server version */ }
    }
}
