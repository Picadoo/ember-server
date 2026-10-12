package town.sunshine.corerpg.p1;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D695/D741: set completion chase — progress + next awakening + focus gap + bag equip.
 * Zero set power / AFK / drop change. At most one [换上] button — no celebration twin.
 */
public final class EmberSetChase {

    private EmberSetChase() {}

    /** Strip § codes for ActionBar half-lines that already carry color. */
    static String stripSection(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '§' && i + 1 < s.length()) { i++; continue; }
            b.append(c);
        }
        return "§e" + b.toString();
    }

    /**
     * D1377: focus-family codex remain (T1–T3 blade+charm) — display only.
     */
    public static String focusCodexRemain(PlayerData d) {
        if (d == null) return "";
        String fk = EmberSetFocus.familyKey(EmberSetFocus.get(d));
        if (fk == null) return "";
        int miss = 0;
        String first = null;
        for (int t = 1; t <= 3; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                if (!EmberCodex.has(d, EmberCodex.key(fk, slot, t))) {
                    miss++;
                    if (first == null)
                        first = "T" + t + " " + EmberItemData.familyName(fk) + EmberItemData.slotName(slot);
                }
            }
        }
        if (miss == 0) return "焦点图鉴 · " + EmberItemData.familyName(fk) + " T1–T3 齐";
        return "焦点图鉴 · " + EmberItemData.familyName(fk) + " 还差" + miss + "种 · 先 " + first;
    }

    /**
     * D12265: one-line TrMenu short for hub「套装」— focus have/need + gap / awakening remain.
     * Display only; reuses focusGap / awakeningRemain / focusCodexRemain. No new counters.
     */
    public static String menuLine(PlayerData d, EmberLoadout lo) {
        if (d == null || !EmberSetFocus.valid(EmberSetFocus.get(d)))
            return "§8套装焦点：未选 · 点右选焚烬/烬爆/炽愈";
        int focus = EmberSetFocus.get(d);
        String label = EmberSetFocus.label(focus);
        String fk = EmberSetFocus.familyKey(focus);
        int have = 0;
        if (lo != null) {
            if (lo.blade != null && fk.equals(lo.blade.family)) have++;
            if (lo.charm != null && fk.equals(lo.charm.family)) have++;
        }
        boolean wearing = lo != null && fk.equals(lo.activeSet);
        if (!wearing) {
            String gap = gapNeedWord(d, lo);
            return "§e焦点§f" + label + " §7· 同族 " + have + "/2 §8· §e还差" + gap;
        }
        if (lo.awakening < 3) {
            String awk = "觉醒" + EmberSetFocus.toRoman(Math.max(1, lo.awakening));
            String remain = compressAwakeningRemain(lo.awakeningRemain());
            return "§e焦点§f" + label + " §a穿着 §7· " + awk + " §8· §e还差 " + remain;
        }
        String base = "§a焦点§f" + label + " §a觉醒III · 齐";
        String fcr = focusCodexRemain(d);
        if (fcr != null && !fcr.isEmpty()) {
            if (fcr.contains("齐")) return base + " §8图鉴齐";
            int miss = codexMissCount(d, fk);
            if (miss > 0) return base + " §8还差" + miss + "种";
        }
        return base;
    }

    /** Strip focusGap to the short need word (缺同族护符 → 同族护符; 换焚烬刃 stays). */
    static String gapNeedWord(PlayerData d, EmberLoadout lo) {
        String gap = focusGap(d, lo);
        if (gap != null && !gap.isEmpty()) {
            String plain = plainSection(gap);
            int idx = plain.lastIndexOf(" · ");
            String need = idx >= 0 ? plain.substring(idx + 3).trim() : plain.trim();
            if (need.startsWith("缺")) need = need.substring(1);
            if (!need.isEmpty()) return need;
        }
        // lo null / empty focusGap: same rules as focusGap need branch
        if (lo == null || lo.blade == null) return "刃";
        String fk = EmberSetFocus.familyKey(EmberSetFocus.get(d));
        if (fk == null) return "件";
        if (lo.charm == null) return "同族护符";
        if (!fk.equals(lo.blade.family) && !fk.equals(lo.charm.family))
            return "刃/符都不是" + EmberItemData.familyName(fk);
        if (!fk.equals(lo.blade.family)) return "换" + EmberItemData.familyName(fk) + "刃";
        return "换" + EmberItemData.familyName(fk) + "护符";
    }

    static String compressAwakeningRemain(String awk) {
        if (awk == null || awk.isEmpty()) return "";
        if ("觉醒满".equals(awk)) return "满";
        int i = awk.indexOf("缺：");
        if (i >= 0) {
            String rest = awk.substring(i + 2).trim();
            int semi = rest.indexOf('；');
            if (semi > 0) rest = rest.substring(0, semi).trim();
            return rest.isEmpty() ? awk : rest;
        }
        return awk;
    }

    static int codexMissCount(PlayerData d, String fk) {
        if (d == null || fk == null) return 0;
        int miss = 0;
        for (int t = 1; t <= 3; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                if (!EmberCodex.has(d, EmberCodex.key(fk, slot, t))) miss++;
            }
        }
        return miss;
    }

    static String plainSection(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '§' && i + 1 < s.length()) { i++; continue; }
            b.append(c);
        }
        return b.toString();
    }

    public static String focusGap(PlayerData d, EmberLoadout lo) {
        if (d == null || lo == null) return "";
        int focus = EmberSetFocus.get(d);
        String fk = EmberSetFocus.familyKey(focus);
        if (fk == null) return "";
        if (fk.equals(lo.activeSet)) return "";
        String need;
        if (lo.blade == null) need = "缺刃";
        else if (lo.charm == null) need = "缺同族护符";
        else if (!fk.equals(lo.blade.family) && !fk.equals(lo.charm.family))
            need = "刃/符都不是" + EmberItemData.familyName(fk);
        else if (!fk.equals(lo.blade.family)) need = "换" + EmberItemData.familyName(fk) + "刃";
        else need = "换" + EmberItemData.familyName(fk) + "护符";
        return "§e成套追 · 焦点§f" + EmberSetFocus.label(focus) + " §7· " + need;
    }

    /**
     * D741: bag piece that closes focus gap (prefer charm if charm wrong/missing, else blade).
     * Bukkit inventory scan; null if none.
     */
    public static EmberItemData findEquipCandidate(Player p, PlayerData d, EmberLoadout lo, EmberLoadoutService loadouts) {
        if (p == null || d == null || lo == null || loadouts == null) return null;
        String fk = EmberSetFocus.familyKey(EmberSetFocus.get(d));
        if (fk == null) return null;
        if (fk.equals(lo.activeSet)) return null;
        boolean needCharm = lo.charm == null || !fk.equals(lo.charm.family);
        boolean needBlade = lo.blade == null || !fk.equals(lo.blade.family);
        EmberItemData bestCharm = null, bestBlade = null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!fk.equals(x.family)) continue;
            if (x.isCharm() && needCharm) {
                if (lo.charm != null && lo.charm.uid.equals(x.uid)) continue;
                if (bestCharm == null || x.tier > bestCharm.tier
                        || (x.tier == bestCharm.tier && x.enhance > bestCharm.enhance))
                    bestCharm = x;
            } else if (x.isBlade() && needBlade) {
                if (lo.blade != null && lo.blade.uid.equals(x.uid)) continue;
                if (bestBlade == null || x.tier > bestBlade.tier
                        || (x.tier == bestBlade.tier && x.enhance > bestBlade.enhance))
                    bestBlade = x;
            }
        }
        if (needCharm && bestCharm != null) return bestCharm;
        if (needBlade && bestBlade != null) return bestBlade;
        return null;
    }

    public static void offerEquip(Player p, EmberItemData cand) {
        if (p == null || cand == null || cand.uid == null) return;
        String P = EmberRunService.P;
        String slot = cand.isCharm() ? "护符" : "刃";
        ConfirmTokens.sendButtons(p, P + "§7成套追换上：", // D870
                new String[]{"[换上" + slot + "]", "/corerpg p1 equip " + cand.uid,
                        cand.shortLabel() + " · 焦点成套", "GREEN"},
                new String[]{"[图装追]", "/corerpg p1 mapfarm", "刷同族补件", "AQUA"});
        try { p.sendActionBar("§e成套追 · 可换上 §f" + EmberItemData.familyName(cand.family) + slot); }
        catch (Throwable ignored) { }
    }

    public static void tell(Player p, PlayerData d, EmberLoadout lo) {
        tell(p, d, lo, null);
    }

    public static void tell(Player p, PlayerData d, EmberLoadout lo, EmberLoadoutService loadouts) {
        if (p == null || lo == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e" + lo.setProgress());
        String next = lo.nextAwakeningHint();
        if (next != null && !next.isEmpty()) p.sendMessage(P + "§7" + next);
        String gap = focusGap(d, lo);
        if (gap != null && !gap.isEmpty()) p.sendMessage(P + gap);
        String fcr = focusCodexRemain(d);
        if (fcr != null && !fcr.isEmpty()) p.sendMessage(P + "§7" + fcr);
        try { // D1340 / D1396: gap or awakening remain
            if (!"none".equals(lo.activeSet)) {
                String awk = lo.awakeningRemain(); // D1412
                if ("觉醒满".equals(awk))
                    p.sendActionBar("§a成套 · §f" + lo.setLabel() + " §7· 满觉醒");
                else
                    p.sendActionBar("§e成套 · §f" + lo.setLabel() + " §7· " + awk);
            } else if (gap != null && !gap.isEmpty())
                p.sendActionBar(stripSection(gap));
            else
                p.sendActionBar("§e成套 · §7未成套");
        } catch (Throwable ignored) { }
        // D741 equip flow
        if (loadouts != null) {
            EmberItemData cand = findEquipCandidate(p, d, lo, loadouts);
            if (cand != null) offerEquip(p, cand);
        }
    }

    /** Rising-edge when 2pc forms or awakening bumps. */
    public static void maybeAfterSetChange(Player p, PlayerData d, String prevSet, int prevAwk, EmberLoadout after) {
        if (p == null || after == null) return;
        boolean formed = (prevSet == null || "none".equals(prevSet)) && !"none".equals(after.activeSet);
        boolean awkUp = after.awakening > prevAwk;
        if (!formed && !awkUp) return;
        tell(p, d, after);
        if (formed) { // D872 / D2129 piece-aware
            EmberForgeCommitNext.afterSetReady(p, null, EmberRunService.P + "§7成套成型 · 同件下一槽：");
        } else if (d != null && !EmberSetFocus.valid(EmberSetFocus.get(d))) {
            p.sendMessage(EmberRunService.P + "§7可设套装焦点：/corerpg p1 setfocus");
        }
    }

    /** After equip/charm — refresh chase with equip button if still gapped. */
    public static void afterEquip(Player p, PlayerData d, EmberLoadoutService loadouts) {
        if (p == null || loadouts == null) return;
        EmberLoadout lo = loadouts.refresh(p);
        try { // D4705 imprint auto after equip empty-sig
            CoreRpgPlugin pl = (CoreRpgPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            EmberRunService runs = pl == null ? null : pl.getEmberRuns();
            EmberImprintBreak.maybeAuto(p, d, lo, runs);
        } catch (Throwable ignored) { }
        String fk = d == null ? null : EmberSetFocus.familyKey(EmberSetFocus.get(d));
        tell(p, d, lo, loadouts);
        if (fk != null && fk.equals(lo.activeSet)) { // D744
            try { p.sendActionBar("§a成套追 · 焦点已齐 · §f" + lo.setLabel()); } catch (Throwable ignored) { }
            // D863 meaty next
            EmberForgeCommitNext.afterSetReady(p, null, EmberRunService.P + "§7成套已齐 · 同件下一槽：");
        }
    }

    public static boolean cmd(CommandSender s, String[] args, EmberRunService runs, EmberLoadoutService loadouts) {
        String P = EmberRunService.P;
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        if (runs == null || loadouts == null) { p.sendMessage(P + "服务未加载"); return true; }
        PlayerData d = runs.dataOf(p.getUniqueId());
        EmberLoadout lo = loadouts.refresh(p);
        tell(p, d, lo, loadouts); // D741
        if (d != null && !EmberSetFocus.valid(EmberSetFocus.get(d))) { // D867
            ConfirmTokens.sendButton(p, P + "§7未设焦点：",
                    "[选套装焦点]", "/corerpg p1 setfocus", "定成套追方向");
        } else if (d != null && runs != null && runs.maps() != null) { // D1387
            String fk = EmberSetFocus.familyKey(EmberSetFocus.get(d));
            String mk = EmberMapIdentity.suggestMapForFamily(runs.maps(), fk);
            String fcr = focusCodexRemain(d);
            if (mk != null && fcr != null && fcr.contains("还差")) {
                ConfirmTokens.sendButton(p, P + "§7刷焦点图鉴：",
                        "[进·" + mk.toUpperCase() + "]", "/corerpg p1 enter " + mk,
                        "本图偏向 " + EmberItemData.familyName(fk) + " · 不锁通关");
            }
        }
        // D1412: 成套未满觉醒 → 工坊强化路径
        if (!"none".equals(lo.activeSet) && lo.awakening < 3) {
            ConfirmTokens.sendButton(p, P + "§7觉醒下一档：",
                    "[工坊]", "/corerpg p1 forge", lo.awakeningRemain());
        }
        return true;
    }
}
