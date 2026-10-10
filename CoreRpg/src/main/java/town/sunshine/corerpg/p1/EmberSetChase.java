package town.sunshine.corerpg.p1;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D695/D741: set completion chase — progress + next awakening + focus gap + bag equip.
 * Zero set power / AFK / drop change. At most one [换上] button — no celebration twin.
 */
public final class EmberSetChase {

    private EmberSetChase() {}

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
        try {
            if (!"none".equals(lo.activeSet))
                p.sendActionBar("§e成套 · §f" + lo.setLabel());
            else if (gap != null && !gap.isEmpty())
                p.sendActionBar("§e成套追 · §f焦点未齐");
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
        if (formed) { // D872
            ConfirmTokens.sendButtons(p, EmberRunService.P + "§7成套成型 · 下一步：",
                    new String[]{"[工坊]", "/corerpg p1 forge", "强化同族", "GOLD"},
                    new String[]{"[镶嵌]", "/corerpg p1 gem list", "嵌空孔", "GREEN"},
                    new String[]{"[设焦点]", "/corerpg p1 setfocus",
                            d != null && EmberSetFocus.valid(EmberSetFocus.get(d)) ? "已设可改" : "定追击方向", "AQUA"});
        } else if (d != null && !EmberSetFocus.valid(EmberSetFocus.get(d))) {
            p.sendMessage(EmberRunService.P + "§7可设套装焦点：/corerpg p1 setfocus");
        }
    }

    /** After equip/charm — refresh chase with equip button if still gapped. */
    public static void afterEquip(Player p, PlayerData d, EmberLoadoutService loadouts) {
        if (p == null || loadouts == null) return;
        EmberLoadout lo = loadouts.refresh(p);
        String fk = d == null ? null : EmberSetFocus.familyKey(EmberSetFocus.get(d));
        tell(p, d, lo, loadouts);
        if (fk != null && fk.equals(lo.activeSet)) { // D744
            try { p.sendActionBar("§a成套追 · 焦点已齐 · §f" + lo.setLabel()); } catch (Throwable ignored) { }
            // D863 meaty next
            ConfirmTokens.sendButtons(p, EmberRunService.P + "§7成套已齐 · 下一步：",
                    new String[]{"[工坊]", "/corerpg p1 forge", "强化同族", "GOLD"},
                    new String[]{"[镶嵌]", "/corerpg p1 gem list", "嵌空孔", "GREEN"},
                    new String[]{"[图装追]", "/corerpg p1 mapfarm", "刷同族图", "AQUA"});
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
        }
        return true;
    }
}
