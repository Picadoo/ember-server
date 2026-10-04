package town.sunshine.coregacha;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import town.sunshine.coregacha.engine.Banner;
import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.Item;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Tier;

/** %coregacha_…% for the TrMenu pages (everything shown comes from the same config + RateMath as /gacha rates). */
public final class GachaExpansion extends PlaceholderExpansion {
    private final CoreGachaPlugin pl;
    GachaExpansion(CoreGachaPlugin pl) { this.pl = pl; }

    @Override public String getIdentifier() { return "coregacha"; }
    @Override public String getAuthor() { return "sunshine-town"; }
    @Override public String getVersion() { return pl.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }

    private static String tierName(String key) { Tier t = Tier.parse(key); return t == null ? key : t.colored(); }

    String histLine(PCache.Hist h) {
        GachaConfig c = pl.service().cfg;
        Item it = c.items.get(h.item);
        Banner b = c.banner(h.banner);
        SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm", Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone(GachaService.ZONE));
        return "§7" + f.format(new Date(h.at)) + " " + tierName(h.tier) + " §f" + (it == null ? h.item : Item.stripColor(it.name))
                + " §8" + (b == null ? h.banner : Item.stripColor(b.name).replaceAll(" · .*", "")) + (h.dup ? " §7重复 +" + h.shards : " §a新")
                + ("hard".equals(h.rule) ? " §c硬保底" : "soft".equals(h.rule) ? " §6软保底" : "tier2".equals(h.rule) ? " §d十抽保底" : "");
    }

    static String left(Banner b) {
        long now = System.currentTimeMillis();
        if (b.end <= 0) return "§a常驻";
        if (now >= b.end) return "§8已结束（外观已转入常驻池）";
        if (now < b.start) return "§7未开始";
        long m = (b.end - now) / 60000, d = m / 1440, h = (m % 1440) / 60;
        SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm", Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone(GachaService.ZONE));
        return "§e还剩 " + (d > 0 ? d + " 天 " : "") + h + " 小时 " + (m % 60) + " 分 §8（" + f.format(new Date(b.end)) + " 结束）";
    }

    List<String> rateLines(Banner b) {
        GachaConfig c = pl.service().cfg;
        RateMath rm = pl.service().rates();
        List<String> l = new ArrayList<String>();
        l.add("§f" + Item.stripColor(b.name) + " §7概率公示（只出外观，零属性）");
        double[] base = {c.baseLegend, c.baseEpic, c.baseRare, 1 - c.baseLegend - c.baseEpic - c.baseRare};
        for (Tier t : Tier.values())
            l.add(t.colored() + " §7基础 §f" + RateMath.pct(base[t.ordinal()]) + " §7· 算上保底的综合 §f" + RateMath.pct(rm.effective.get(t)));
        l.add(String.format(Locale.ROOT, "§7传说：第 1–%d 抽 %s，之后每抽 +%s，第 %d 抽必出；平均 %.1f 抽一件", c.softStart, RateMath.pct(c.baseLegend), RateMath.pct(c.softStep), c.hardPity, rm.expectedPullsPerLegend));
        l.add("§7每 " + c.tier2Every + " 抽至少一件史诗或以上 · 同一池不连出同一件传说 · 火花 " + c.sparkFor(b) + " 抽"
                + (b.sparkItems.isEmpty() ? "任选一件" : "任选一件本池限定传说"));
        if (b.limited() && "carry".equals(b.sparkLeftover) && b.retireTo != null)
            l.add("§7本池结束后剩下的火花 1:1 转进「" + Item.stripColor(c.banner(b.retireTo) == null ? b.retireTo : c.banner(b.retireTo).name) + "§7」的火花（限定件结束后也进那个池）");
        l.add("§7保底组：" + (b.limited() ? "限定池共用（换季延续）" : "常驻池单独"));
        return l;
    }

    /** one tier's items with their long-run single-item probability, "\n"-joined (TrMenu splits lore lines) */
    String itemLines(Banner b, Tier t) {
        GachaConfig c = pl.service().cfg;
        RateMath rm = pl.service().rates();
        Banner.Pool pool = c.pool(b, System.currentTimeMillis());
        Map<Item, Double> sh = RateMath.shares(pool, c.noRepeatLegend);
        StringBuilder s = new StringBuilder();
        for (Item it : pool.tier(t).keySet()) {
            if (s.length() > 0) s.append('\n');
            s.append("§7· §f").append(Item.stripColor(it.name)).append(" §8").append(it.kindLabel()).append(" §7").append(RateMath.pct(rm.itemRate(it, sh)))
             .append(" §8(品质内 ").append(String.format(Locale.ROOT, "%.1f%%", sh.get(it) * 100)).append(")");
        }
        return s.length() == 0 ? "§8（无）" : s.toString();
    }

    @Override
    public String onRequest(OfflinePlayer op, String key) {
        try { return req(op, key); } catch (RuntimeException e) { return ""; }
    }

    private String req(OfflinePlayer op, String key) {
        GachaService g = pl.service();
        GachaConfig c = g.cfg;
        UUID u = op == null ? null : op.getUniqueId();
        PCache pc = u == null ? null : g.get(u);
        String day = GachaService.today();
        switch (key) {
            case "tickets": return pc == null ? "…" : String.valueOf(pc.tickets);
            case "shards": return pc == null ? "…" : String.valueOf(pc.shards);
            case "coin": return u == null ? "0" : String.valueOf(pl.rpg().coin(u));
            case "badges": return u == null ? "0" : String.valueOf(pl.rpg().badges(u));
            case "today": return pc == null ? "…" : pc.dailyGet("pulls", day) + "/" + c.dailyPullCap;
            case "exch_left": return pc == null ? "…" : String.valueOf(Math.max(0, pl.getConfig().getInt("tickets.exchange.daily_cap", 5) - pc.dailyGet("exch", day)));
            case "online": return pc == null ? "…" : String.valueOf(pc.onlineMin);
            case "hist_count": return pc == null ? "0" : String.valueOf(pc.hist.size());
            case "rv_count": return pc == null ? "0" : String.valueOf(pc.reveal.size());
            case "rv_banner": { Banner b = pc == null ? null : c.banner(pc.revealBanner); return b == null ? "" : b.name; }
            case "rv_bid": return pc == null || pc.revealBanner == null ? "standard" : pc.revealBanner;
            case "rv_pity": return req(op, "b_pity_" + (pc == null || pc.revealBanner == null ? "standard" : pc.revealBanner));
            case "view": { Banner b = pc == null ? c.banner("standard") : c.banner(pc.view); return b == null ? "standard" : b.id; }
            case "view_name": { Banner b = pc == null ? c.banner("standard") : c.banner(pc.view); return b == null ? "" : b.name; }
            case "view_rules": { Banner b = pc == null ? c.banner("standard") : c.banner(pc.view); return b == null ? "" : String.join("\n", rateLines(b)); }
            case "owned_count": {
                if (u == null) return "0";
                int n = 0, all = 0;
                for (Item it : g.craftable()) { all++; if (g.owns(u, it)) n++; }
                return n + "/" + all;
            }
            default: break;
        }
        if (key.startsWith("hist_")) { // hist_<1..100>, or hist_page_<p> = 25 lines joined
            if (pc == null) return "";
            if (key.startsWith("hist_page_")) {
                int p = Integer.parseInt(key.substring(10));
                StringBuilder s = new StringBuilder();
                for (int i = (p - 1) * 25; i < Math.min(pc.hist.size(), p * 25); i++) { if (s.length() > 0) s.append('\n'); s.append(histLine(pc.hist.get(i))); }
                return s.length() == 0 ? "§8（空）" : s.toString();
            }
            int i = Integer.parseInt(key.substring(5)) - 1;
            return i < 0 || i >= pc.hist.size() ? "" : histLine(pc.hist.get(i));
        }
        if (key.startsWith("rv_")) { // rv_<i>_tier|name|note|kind
            String[] a = key.split("_", 3);
            int i = Integer.parseInt(a[1]) - 1;
            PCache.Hist h = pc == null || i < 0 || i >= pc.reveal.size() ? null : pc.reveal.get(i);
            if (h == null) return "tier".equals(a[2]) || a[2].startsWith("is") ? "0" : "";
            Item it = c.items.get(h.item);
            Tier t = Tier.parse(h.tier);
            switch (a[2]) {
                case "tier": return String.valueOf(t == null ? 0 : t.code);
                case "is1": case "is2": case "is3": case "is4": // one flag per tier, so menu conditions never overlap
                    return t != null && t.code == a[2].charAt(2) - '0' ? "1" : "0";
                case "name": return t.color + "§l" + (it == null ? h.item : Item.stripColor(it.name));
                case "kind": return t.colored() + " §8· §7" + (it == null ? "" : it.kindLabel());
                case "note": return h.dup ? "§7已拥有 → §b+" + h.shards + " 光屑" : "§a§l新！" + (it != null && it.external() ? " §7到 CoreRpg 外观商店换上" : " §7/gacha wear 装上");
                case "desc": return it == null ? "" : "§8" + it.prop("desc", "");
                default: return "";
            }
        }
        if (key.startsWith("b_")) { // b_<field>_<banner>
            String[] a = key.split("_", 3);
            if (a.length < 3) return "";
            Banner b = c.banner(a[2]);
            if (b == null) return "";
            switch (a[1]) {
                case "name": return b.name;
                case "open": return b.open(System.currentTimeMillis()) ? "1" : "0";
                case "left": return left(b);
                case "desc": return "§7" + b.desc;
                case "spark": { PCache.BState bs = pc == null ? null : pc.banners.get(b.id); return (bs == null ? 0 : bs.spark) + "/" + c.sparkFor(b); }
                case "pulls": { PCache.BState bs = pc == null ? null : pc.banners.get(b.id); return String.valueOf(bs == null ? 0 : bs.pulls); }
                case "pity": {
                    int[] p = pc == null ? null : pc.pity.get(b.pityGroup);
                    int p5 = p == null ? 0 : p[0], p4 = p == null ? 0 : p[1];
                    double next = c.legendRate(p5 + 1);
                    return "§7距上次传说 §f" + p5 + "§7 抽（下一抽传说 " + RateMath.pct(next) + "，最多再 " + (c.hardPity - p5) + " 抽）· 距上次史诗+ §f" + p4 + "§7/" + c.tier2Every;
                }
                case "legend": { // the banner's 传说 names
                    StringBuilder s = new StringBuilder();
                    for (Item it : c.pool(b, System.currentTimeMillis()).tier(Tier.LEGEND).keySet()) s.append(s.length() == 0 ? "" : "、").append(Item.stripColor(it.name));
                    return s.toString();
                }
                default: return "";
            }
        }
        if (key.startsWith("view_tier_") || key.startsWith("view_items_")) {
            Banner b = pc == null ? c.banner("standard") : c.banner(pc.view);
            if (b == null) return "";
            Tier t = Tier.parse(key.substring(key.lastIndexOf('_') + 1));
            if (t == null) return "";
            if (key.startsWith("view_items_")) return itemLines(b, t);
            double[] base = {c.baseLegend, c.baseEpic, c.baseRare, 1 - c.baseLegend - c.baseEpic - c.baseRare};
            return "§7基础 §f" + RateMath.pct(base[t.ordinal()]) + " §7· 综合 §f" + RateMath.pct(g.rates().effective.get(t)) + " §8（" + c.pool(b, System.currentTimeMillis()).tier(t).size() + " 件）";
        }
        if (key.startsWith("shop_")) { // shop_<field>_<item>
            String[] a = key.split("_", 3);
            if (a.length < 3) return "";
            Item it = c.items.get(a[2]);
            if (it == null) return "";
            int price = c.craftPrice.get(it.tier);
            boolean own = u != null && g.owns(u, it);
            switch (a[1]) {
                case "name": return it.tier.color + Item.stripColor(it.name);
                case "kind": return it.tier.colored() + " §8· §7" + it.kindLabel();
                case "desc": return "§8" + it.prop("desc", it.external() ? "和外观商店里买的完全一样" : "");
                case "price": return "§b" + price + " 光屑";
                case "state": return own ? "§a已拥有" : pc != null && pc.shards >= price ? "§e左键兑换（现有 " + pc.shards + "）" : "§8光屑不够（" + (pc == null ? 0 : pc.shards) + "/" + price + "）";
                case "owned": return own ? "1" : "0";
                default: return "";
            }
        }
        if (key.startsWith("wear_")) {
            String id = pc == null ? null : pc.wear.get(key.substring(5));
            Item it = id == null ? null : c.items.get(id);
            return it == null ? "§8无" : it.display();
        }
        return null;
    }
}
