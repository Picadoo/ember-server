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

    public enum Kind { TITLE, TRAIL }

    public static final class Cosmetic {
        public final String id, label, how;
        public final Kind kind;
        public final int abyssTier;   // > 0: earned by abyss best >= this tier
        public final String raid;     // non-null: earned by a clear of this raid key
        public final String particle; // trails only
        Cosmetic(String id, Kind kind, String label, String how, int abyssTier, String raid, String particle) {
            this.id = id; this.kind = kind; this.label = label; this.how = how; this.abyssTier = abyssTier; this.raid = raid; this.particle = particle;
        }
    }

    public static final List<Cosmetic> ALL = Collections.unmodifiableList(Arrays.asList(
            new Cosmetic("abyss5", Kind.TITLE, "§5深渊行者", "深渊最高通关第 5 层", 5, null, null),
            new Cosmetic("abyss10", Kind.TITLE, "§d§l余烬深渊之主", "深渊最高通关第 10 层", 10, null, null),
            new Cosmetic("r01", Kind.TITLE, "§6锈轨破袭者", "通关团本 R01 锈轨矿道·团", 0, "r01", null),
            new Cosmetic("r02", Kind.TITLE, "§b霜封守望者", "通关团本 R02 霜封哨所·团", 0, "r02", null),
            new Cosmetic("trail_r01", Kind.TRAIL, "§6余烬火星", "通关团本 R01（团本专属足迹）", 0, "r01", "FLAME"),
            new Cosmetic("trail_r02", Kind.TRAIL, "§b霜花", "通关团本 R02（团本专属足迹）", 0, "r02", "SNOW_SHOVEL")));

    static final String C_EARNED = "p2_title_";   // + raid key, value = clears
    static final String C_SEL_TITLE = "p2_titlesel";
    static final String C_SEL_TRAIL = "p2_trailsel";
    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;

    private final EmberRunService runs;
    private final Map<UUID, Location> last = new HashMap<UUID, Location>();

    public EmberCosmetics(EmberRunService runs) { this.runs = runs; }

    public static Cosmetic byId(String id) {
        for (Cosmetic c : ALL) if (c.id.equalsIgnoreCase(id)) return c;
        return null;
    }

    /** raid clears that count for {@code raid} (the earned counter doubles as the clear tally) */
    public static int raidClears(PlayerData d, String raid) { return d == null ? 0 : d.periodCount(C_EARNED + raid, "all"); }

    public static boolean earned(PlayerData d, int abyssBest, Cosmetic c) {
        if (d == null || c == null) return false;
        if (c.abyssTier > 0) return abyssBest >= c.abyssTier;
        return c.raid != null && raidClears(d, c.raid) > 0;
    }

    public static String selected(PlayerData d, Kind k) {
        if (d == null) return null;
        String name = k == Kind.TITLE ? C_SEL_TITLE : C_SEL_TRAIL;
        for (String key : d.getCounters().keySet()) if (key.startsWith(name + "@")) return key.substring(name.length() + 1);
        return null;
    }

    /** called from raid settlement (once per fresh run): counts the clear and says what it unlocked */
    void onRaidClear(Player p, PlayerData d, String raid) {
        int n = d.addPeriodCount(C_EARNED + raid, "all", 1);
        if (n == 1 && p != null && p.isOnline()) {
            for (Cosmetic c : ALL) if (raid.equals(c.raid))
                p.sendMessage(P + "§d获得" + (c.kind == Kind.TITLE ? "称号" : "足迹") + "「" + c.label + "§d」§7（只做展示，/corerpg p1 title）");
        }
    }

    void onAbyssBest(Player p, int oldBest, int newBest) {
        if (p == null || !p.isOnline()) return;
        for (Cosmetic c : ALL) if (c.abyssTier > oldBest && c.abyssTier <= newBest)
            p.sendMessage(P + "§d获得称号「" + c.label + "§d」§7（只做展示，/corerpg p1 title）");
    }

    public String titleText(PlayerData d) {
        Cosmetic c = byId(String.valueOf(selected(d, Kind.TITLE)));
        return c == null || !earned(d, runs.abyssBest(d), c) ? "" : c.label;
    }

    public int earnedCount(PlayerData d) {
        int n = 0, best = runs.abyssBest(d);
        for (Cosmetic c : ALL) if (earned(d, best, c)) n++;
        return n;
    }

    /** /corerpg p1 title [id|off] · /corerpg p1 trail [id|off] */
    public boolean command(Player p, PlayerData d, String sub, String[] args) {
        Kind k = "trail".equals(sub) ? Kind.TRAIL : Kind.TITLE;
        String name = k == Kind.TITLE ? C_SEL_TITLE : C_SEL_TRAIL;
        int best = runs.abyssBest(d);
        if (args.length >= 3) {
            String want = args[2];
            String cur = selected(d, k);
            if ("off".equalsIgnoreCase(want)) {
                if (cur != null) d.addPeriodCount(name, cur, -1);
                p.sendMessage(P + (k == Kind.TITLE ? "已取下称号" : "已关闭足迹"));
                return true;
            }
            Cosmetic c = byId(want);
            if (c == null || c.kind != k) { p.sendMessage(P + "§c没有这个" + (k == Kind.TITLE ? "称号" : "足迹") + "：" + want); return true; }
            if (!earned(d, best, c)) { p.sendMessage(P + "§c还没获得「" + c.label + "§c」：" + c.how); return true; }
            d.addPeriodCount(name, c.id, 1 - d.periodCount(name, c.id)); // also drops the previous selection
            p.sendMessage(P + "已装上" + (k == Kind.TITLE ? "称号" : "足迹") + "「" + c.label + "§7」");
            return true;
        }
        p.sendMessage(P + "§6荣誉（只做展示，不加属性）§7 已获得 " + earnedCount(d) + "/" + ALL.size()
                + " · 团本通关 R01 " + raidClears(d, "r01") + " 次 · R02 " + raidClears(d, "r02") + " 次 · 深渊最高 " + best + " 层");
        String selT = selected(d, Kind.TITLE), selR = selected(d, Kind.TRAIL);
        for (Cosmetic c : ALL) {
            boolean on = c.id.equals(c.kind == Kind.TITLE ? selT : selR);
            p.sendMessage(P + (earned(d, best, c) ? "§a✔ " : "§8✘ ") + (c.kind == Kind.TITLE ? "称号 " : "足迹 ") + c.label
                    + " §7" + c.how + " §8（" + c.id + "）" + (on ? " §e· 使用中" : ""));
        }
        p.sendMessage(P + "§7装上：/corerpg p1 title <id> · /corerpg p1 trail <id> · 取下：title off / trail off");
        return true;
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

    /** every 4 ticks: a small puff behind moving players who wear a trail (spectators and invisible players skipped) */
    public void tick() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) continue;
            PlayerData d = runs.dataOf(p.getUniqueId());
            String sel = selected(d, Kind.TRAIL);
            if (sel == null) continue;
            Cosmetic c = byId(sel);
            if (c == null || c.particle == null || !earned(d, runs.abyssBest(d), c)) continue;
            Location now = p.getLocation(), was = last.put(p.getUniqueId(), now);
            if (was == null || was.getWorld() != now.getWorld() || was.distanceSquared(now) < 0.04) continue;
            try {
                p.getWorld().spawnParticle(Particle.valueOf(c.particle), now.clone().add(0, 0.1, 0), 3, 0.15, 0.02, 0.15, 0.01);
            } catch (IllegalArgumentException ignored) { /* particle missing on this server version */ }
        }
    }
}
