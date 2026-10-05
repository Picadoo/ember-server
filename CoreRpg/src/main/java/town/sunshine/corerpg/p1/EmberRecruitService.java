package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * D234 / ARCH S3 step 5: 招募板 (D104 / E-review #5) logic extracted from {@link EmberRunService}
 * with <b>no behaviour change</b>. Owns the in-memory board ({@code recruits} / {@code recruitAt}),
 * {@code /corerpg p1 recruit} (post / list / join), the adventure-icon PAPI label, the Q07 login
 * board flash, and the DP {@code request unaccept} refuse line.
 * <p>Shares no counters / settle / D106 state with {@link EmberRaidService} — only reads
 * {@link EmberRunMaps} / {@code progressFlag} / the DP team bridge. {@link EmberRunService} keeps
 * thin delegates and the Bukkit {@code @EventHandler}s (season apply on join stays there; recruit
 * flash and refuse-line forward here).
 * <p>{@link #cooldownOk}, {@link #cooldownRemainSec}, {@link #stillLive}, {@link #boardText},
 * {@link #labelText}, {@link #callBody}, {@link #postedOk}, {@link #postedEmpty}, {@link #usageHelp},
 * {@link #emptyBoardMsg} are Bukkit-free so unit tests can pin board / cooldown / label shapes
 * (bv58 unchanged).
 */
public final class EmberRecruitService {

    /** board entry TTL: 10 minutes */
    static final long TTL_MS = 10 * 60_000L;
    /** per-leader post cooldown: 60 seconds */
    static final long COOLDOWN_MS = 60_000L;
    /** board drops when the leader's team is full (raid party_max ceiling) */
    static final int TEAM_CAP = 5;
    /** %corerpg_p1_recruits% shows at most this many entries */
    static final int LABEL_SHOW = 2;

    private final EmberRunService runs;
    private final Map<UUID, Long> recruitAt = new java.util.concurrent.ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Recruit> recruits = new java.util.concurrent.ConcurrentHashMap<UUID, Recruit>();

    EmberRecruitService(EmberRunService runs) {
        this.runs = runs;
    }

    private static String P() { return EmberRunService.P; }

    // ------------------------------------------------------------------ Bukkit-free board / cooldown / text

    static boolean cooldownOk(Long lastAt, long now) {
        return lastAt == null || now - lastAt >= COOLDOWN_MS;
    }

    /** whole seconds still left on the post cooldown — same formula as pre-extract {@code 60 - (now-last)/1000}. */
    static long cooldownRemainSec(long lastAt, long now) {
        return Math.max(0L, (COOLDOWN_MS / 1000L) - (now - lastAt) / 1000L);
    }

    /**
     * Live board predicate (same checks as the pre-extract loop): younger than TTL, leader online,
     * not in an instance world, still leading a team that is not full.
     */
    static boolean stillLive(long at, long now, boolean leaderOnline, boolean inInstanceWorld,
                             boolean hasTeam, boolean isLeader, int teamSize) {
        return now - at < TTL_MS && leaderOnline && !inInstanceWorld && hasTeam && isLeader && teamSize < TEAM_CAP;
    }

    /** one board / chat line: {@code R01 · Name · n/min（可开本） · 刚刚|N 分钟前} */
    static String boardText(String raidKey, String leaderName, int teamSize, int partyMin, long ageMs) {
        long mins = ageMs / 60_000L;
        int n = teamSize < 1 ? 1 : teamSize;
        return raidKey.toUpperCase(Locale.ROOT) + " · " + leaderName + " · " + n + "/" + partyMin
                + (n >= partyMin ? "（可开本）" : "") + " · " + (mins == 0 ? "刚刚" : mins + " 分钟前");
    }

    /** %corerpg_p1_recruits%: up to {@link #LABEL_SHOW} entries, else「暂无（右键发一个）」. */
    static String labelText(List<String> entryTexts) {
        if (entryTexts == null || entryTexts.isEmpty()) return "暂无（右键发一个）";
        StringBuilder b = new StringBuilder();
        int show = Math.min(LABEL_SHOW, entryTexts.size());
        for (int i = 0; i < show; i++) b.append(i == 0 ? "" : " ｜ ").append(entryTexts.get(i));
        return b + (entryTexts.size() > LABEL_SHOW ? " 等 " + entryTexts.size() + " 个" : "");
    }

    /** broadcast body after the {@code [余烬]} prefix (party hint already prefixed with space+§e when non-empty). */
    static String callBody(String leaderName, String mapName, int teamSize, int partyMin, String partyHint) {
        int need = Math.max(0, partyMin - teamSize);
        return "§6" + leaderName + " §f招 §e" + mapName + " §f队员（现在 " + teamSize + " 人"
                + (need > 0 ? "，还差 " + need + " 人开本" : "") + "；人越多越稳，最多 5 人）"
                + (partyHint == null || partyHint.isEmpty() ? "" : " §e" + partyHint) + " ";
    }

    static String postedOk(int sent, String requiresUpper) {
        return "§a已向 " + sent + " 位已首通 " + requiresUpper + " 的在线玩家发出招募；有人申请时会出现 [同意] [拒绝]。"
                + "§7招募挂在冒险页团本图标上 10 分钟，之后上线的人也看得到。";
    }

    static String postedEmpty(String requiresUpper) {
        return "§7现在没有其他已首通 " + requiresUpper + " 的玩家在线。"
                + "§7招募挂在冒险页团本图标上 10 分钟，之后上线的人也看得到。";
    }

    static String usageHelp() { return "/corerpg p1 recruit <r01|r02|r03> — 全服招募团本队员"; }

    static String emptyBoardMsg() {
        return "现在没有团本在招人。冒险页团本图标右键可以自己发一个。";
    }

    // ------------------------------------------------------------------ board entry

    static final class Recruit {
        final UUID leader; final String name, raid, raidName; final long at;
        Recruit(UUID leader, String name, String raid, String raidName, long at) {
            this.leader = leader; this.name = name; this.raid = raid; this.raidName = raidName; this.at = at;
        }
    }

    // ------------------------------------------------------------------ live wrappers

    /**
     * D104 (midgame #5): /corerpg p1 recruit &lt;r01|r02|r03&gt; — the leader (a DP team is created if needed) sends every online
     * player with their own Q07 first clear a clickable call; /corerpg p1 recruit join &lt;leader&gt; sends the DP join request
     * and gives the leader a clickable [同意]. 60 s cooldown per leader.
     */
    boolean cmd(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        if (args.length >= 4 && "join".equalsIgnoreCase(args[2])) {
            Player l = Bukkit.getPlayerExact(args[3]);
            if (l == null || l.equals(p)) { p.sendMessage(P() + ChatColor.RED + "队长不在线。"); return true; }
            if (EmberRunBridges.hasTeam(p)) { p.sendMessage(P() + ChatColor.RED + "你已经在一支队伍里了，先退出再申请。"); return true; }
            // E-review #5: DP's own line already carries [同意] [拒绝] — no second [同意] from us
            p.performCommand("dungeon-team request join " + l.getName());
            return true;
        }
        if (args.length >= 3 && "list".equalsIgnoreCase(args[2])) { show(p, true); return true; }
        String key = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "r01";
        EmberRunMaps.MapDef m = runs.maps().byKey(key);
        if (m == null || !m.raid) { p.sendMessage(P() + usageHelp()); return true; }
        if (!runs.progressFlag(runs.dataOf(p.getUniqueId()), m.requires)) {
            p.sendMessage(P() + ChatColor.RED + "先首通 " + m.requires.toUpperCase(Locale.ROOT) + " 才能开团本。");
            return true;
        }
        if (!EmberRunBridges.teamLeader(p)) { p.sendMessage(P() + ChatColor.RED + "只有队长能招募。"); return true; }
        long now = System.currentTimeMillis();
        Long last = recruitAt.get(p.getUniqueId());
        if (!cooldownOk(last, now)) {
            p.sendMessage(P() + ChatColor.RED + "招募 60 秒内只能发一次（还剩 " + cooldownRemainSec(last, now) + " 秒）。");
            return true;
        }
        if (!EmberRunBridges.hasTeam(p)) p.performCommand("dungeon-team create");
        recruitAt.put(p.getUniqueId(), now);
        List<UUID> team = EmberRunBridges.teamMembers(p);
        String line = P() + callBody(p.getName(), m.name, team.size(), runs.maps().partyMin(m), m.partyHint);
        recruits.put(p.getUniqueId(), new Recruit(p.getUniqueId(), p.getName(), m.key, m.name, now));
        int sent = 0;
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.equals(p) || team.contains(o.getUniqueId())) continue;
            if (!runs.progressFlag(runs.dataOf(o.getUniqueId()), m.requires)) continue;
            town.sunshine.corerpg.ConfirmTokens.sendButtons(o, line,
                    new String[]{"[申请入队]", "/corerpg p1 recruit join " + p.getName(), "向队长申请；队长同意后入队", "GREEN"});
            sent++;
        }
        String req = m.requires.toUpperCase(Locale.ROOT);
        p.sendMessage(P() + (sent > 0 ? postedOk(sent, req) : postedEmpty(req)));
        return true;
    }

    /** live board entries: younger than 10 min, leader online, still leading a team that is not full and not in a run */
    List<Recruit> live() {
        long now = System.currentTimeMillis();
        List<Recruit> out = new ArrayList<Recruit>();
        for (Recruit r : new ArrayList<Recruit>(recruits.values())) {
            Player l = Bukkit.getPlayer(r.leader);
            boolean online = l != null && l.isOnline();
            boolean ok = stillLive(r.at, now, online,
                    online && EmberRunService.blocksLegacy(l.getWorld()),
                    online && EmberRunBridges.hasTeam(l),
                    online && EmberRunBridges.teamLeader(l),
                    online ? EmberRunBridges.teamMembers(l).size() : 0);
            if (ok) out.add(r); else recruits.remove(r.leader);
        }
        out.sort((a, b) -> Long.compare(b.at, a.at));
        return out;
    }

    String textOf(Recruit r) {
        Player l = Bukkit.getPlayer(r.leader);
        int n = l == null ? 1 : EmberRunBridges.teamMembers(l).size();
        EmberRunMaps.MapDef m = runs.maps().byKey(r.raid);
        int min = m == null ? 3 : runs.maps().partyMin(m);
        return boardText(r.raid, r.name, n, min, System.currentTimeMillis() - r.at);
    }

    /** %corerpg_p1_recruits%: up to two board entries for the adventure icons */
    String label() {
        List<Recruit> live = live();
        List<String> texts = new ArrayList<String>();
        for (Recruit r : live) texts.add(textOf(r));
        return labelText(texts);
    }

    /** chat list with [申请入队] (players with their own Q07 first clear, not already in a team) */
    void show(Player p, boolean tellEmpty) {
        List<Recruit> live = live();
        if (live.isEmpty()) { if (tellEmpty) p.sendMessage(P() + emptyBoardMsg()); return; }
        p.sendMessage(P() + "§6团本招募板§7（挂 10 分钟）：");
        for (Recruit r : live) {
            if (r.leader.equals(p.getUniqueId())) { p.sendMessage(P() + "  §7" + textOf(r) + "（你的招募）"); continue; }
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P() + "  §f" + textOf(r) + " ",
                    new String[]{"[申请入队]", "/corerpg p1 recruit join " + r.name, "向队长申请；队长同意后入队", "GREEN"});
        }
    }

    /** E-review #5: players with a Q07 first clear see open recruits when they log in (scheduled from EmberRunService). */
    void onJoinShow(Player p) {
        if (!p.isOnline()) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (!runs.progressFlag(d, "q07") || live().isEmpty()) return;
        show(p, false);
    }

    /** E-review #5: DP says nothing to a leader who refused an application ([拒绝] runs request unaccept &lt;name&gt;). */
    void onRequestRefused(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        String[] a = e.getMessage().replaceFirst("^/", "").trim().split("\\s+");
        if (a.length < 4 || !a[0].toLowerCase(Locale.ROOT).endsWith("dungeon-team") || !"request".equalsIgnoreCase(a[1])
                || !"unaccept".equalsIgnoreCase(a[2])) return;
        e.getPlayer().sendMessage(P() + "已拒绝 " + a[3] + " 的入队申请。");
    }
}
